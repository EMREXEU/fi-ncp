# Running and debugging fi-ncp locally

How to run fi-ncp in IntelliJ IDEA and hit breakpoints in both the Spring Boot backend and the Angular frontend, with the Angular app served from inside the Java service (http://localhost:9001), as it is in production.

## 1. Prerequisites (per `.sdkmanrc` / `.idea/misc.xml`)

- JDK 17, specifically **Temurin 17** (project SDK is set to `temurin-17`). `.sdkmanrc` pins `java=17.0.19-tem`.
- Maven 3.9+ (`.sdkmanrc` pins `3.9.16`).
- For the Angular frontend: Node 16.14.2 / npm 8.5.0 / Angular CLI 16.1.5, with `ng` installed globally (the Maven build calls it directly).

If sdkman is installed, `sdk env install` in the project root picks up the Java and Maven versions from `.sdkmanrc`.

## 2. Generate dev certs (required, `certs/` is gitignored)

The app reads `certs/ncp_dev_cert.cer` and `certs/ncp.dev.key` (set in `application-dev.properties`), but the whole `certs/` directory is excluded from git (see `.gitignore`). On a fresh clone, generate them first with the openssl commands from the README, or the app won't start:

```bash
openssl genrsa -out private-key.pem 3072
openssl pkcs8 -topk8 -nocrypt -in private-key.pem -out ncp.dev.key
openssl rsa -in private-key.pem -pubout -out public-key.pem
openssl req -new -x509 -key private-key.pem -out ncp_dev_emrex_cert.cer -days 360
```

## 3. Initialize the git submodule

`.gitmodules` declares `fi-common` at `src/main/resources/static/fi-common`. If that directory doesn't exist, the submodule hasn't been initialized. Run:

```bash
git submodule update --init --recursive
```

It contains shared static assets served by the backend. The app boots without it, but anything that hits those paths needs it.

## 4. Open in IntelliJ and do a first full build

Open `pom.xml` as a project. The `dev` Maven profile is active by default. The CXF `wsdl2java`/`xjc` plugins generate required Java sources into `target/generated-sources/cxf` (`fi.csc.emrex.ncp` depends on the generated VIRTA/ELMO classes). On a clean checkout, run once from the terminal or the Maven tool window:

```bash
mvn clean install -Pdev
```

This also builds the Angular frontend into `src/main/webapp` and the war.

Then configure the Maven runner under **Settings → Build, Execution, Deployment → Build Tools → Maven → Runner**:

- Enable **"Delegate IDE build/run actions to Maven"**, so IntelliJ always runs code generation and resource filtering. The `spring.profiles.active=@activatedProperties@` placeholder in `application.properties` only resolves to `dev` through Maven filtering.
- Under **Properties**, add `exec.skip` = `true`. See [section 6](#6-debug-the-angular-typescript) for why.

## 5. Create a run/debug configuration

Create a **Spring Boot** run configuration (named `fi-ncp`):

- Main class: `fi.csc.emrex.ncp.FiNcpApplication`
- Module: `fi-ncp`
- Working directory: project root, so that both `certs/...` and `src/main/webapp` resolve
- Active profiles: `dev`

Run it in **Debug** mode. The app comes up on http://localhost:9001 and uses VIRTA-TEST per the README. Java breakpoints work as normal.

## 6. Debug the Angular TypeScript

### Why TypeScript breakpoints don't work by default

- With "Delegate IDE build/run actions to Maven" on, Maven runs every time the `fi-ncp` run configuration starts.
- That Maven build includes the `exec-npm-ng-build` execution in `pom.xml`, which always runs `ng build --configuration production`.
- The `production` configuration in `src/main/fi-ncp-front/angular.json` has `"sourceMap": false`, and its output goes to `src/main/webapp` (`"outputPath": "../webapp"`).
- When the app runs from IntelliJ, Spring Boot's embedded Tomcat serves the Angular files directly from `src/main/webapp` on disk. A production build there is a minified bundle (e.g. `main.<hash>.js`) without `.map` files, so the debugger can't map it back to the `.ts` sources.

The fix: stop Maven building the frontend during IDE runs (`exec.skip=true` from [section 4](#4-open-in-intellij-and-do-a-first-full-build)), and put a development build with source maps into `src/main/webapp` yourself.

`exec.skip=true` skips both `exec-maven-plugin` executions (`npm install` and `ng build`), but only for builds IntelliJ starts. A `mvn clean install` from the terminal still builds the frontend as before.

### Run a development build in watch mode

```bash
cd src/main/fi-ncp-front
ng build -c development --watch
```

- Writes unminified bundles plus `.map` files to `src/main/webapp`.
- Rebuilds on every save of a `.ts` / `.html` file. No Java restart needed, just reload the browser.
- Development builds don't hash file names (`main.js` instead of `main.<hash>.js`), so keep DevTools' **"Disable cache"** on (Network tab) or hard-reload to avoid a stale bundle.
- `index.html` already has `<base href="/">`, matching the dev Maven profile, so no `--base-href` is needed.

Then start the `fi-ncp` run configuration in **Debug** mode and open http://localhost:9001.

### Option A: Chrome DevTools

Sources tab → `webpack://` → `src/app/...` → set breakpoints in the `.ts` files and reload the page.

### Option B: IntelliJ (Ultimate)

1. Run → Edit Configurations → **+ → JavaScript Debug**
   - URL: `http://localhost:9001/`
   - Browser: Chrome
2. Start it in **Debug** mode alongside the Spring Boot configuration.

Both Java and TypeScript breakpoints then work in the same IDE. For example, you can step from `courses.service.ts` into `NcpUiController.getCourses`.

If TypeScript breakpoints stay greyed out ("not bound"), IntelliJ hasn't matched the source-map paths to the local files. In the JavaScript Debug configuration's **URL mappings**, map the local folder `src/main/fi-ncp-front` to `webpack:///.`

## Things to keep in mind

- Any Maven build without `exec.skip` (e.g. `mvn clean install` in a terminal) overwrites `src/main/webapp` with a production build again. Restart `ng build --watch` afterwards, or just save a file while it's still running to trigger a rebuild.
- There is no Shibboleth locally. A local debug patch in `NcpUiController.getCourses` hard-codes `personId` to a test person (don't commit it). The `learnerId` lookup still reads the Shibboleth attribute, so that branch isn't exercised locally.
- `src/main/webapp/*` is gitignored, so the dev build there won't show up in `git status`.

## Alternative: `ng serve` (README Option 3)

```bash
cd src/main/fi-ncp-front
ng serve
```

Serves the frontend on http://localhost:4200 with live reload and forwards `/api` to `localhost:9001` (`src/proxy.conf.json`). Debug with a JavaScript Debug configuration pointing at `http://localhost:4200/`.

Reloads are faster, but the app isn't served from the Java service as in production. `POST /ncp_entry` isn't forwarded, so the EMREX entry flow can't be tested this way.
