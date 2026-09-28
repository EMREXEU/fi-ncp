// Karma configuration file, see link for more information
// https://karma-runner.github.io/1.0/config/configuration-file.html

// KNOWN ISSUE (local dev, at least as of Chrome 150 / karma@6.4.2 /
// karma-chrome-launcher@~3.2.0): every run of `ng test --browsers=ChromeHeadless`
// (or any other launcher) ends with Karma's own
// "Some of your tests did a full page reload!" error, even though the line
// right above it always reads "Executed X of X SUCCESS" - the actual specs
// pass. This reproduces on every spec file individually, including trivial
// ones with no HTTP/DOM/navigation, which rules out an app-code cause; it's
// a version-skew false positive in Karma's own reload-detection heuristic
// against modern Chrome. Neither `--headless=new` nor
// `--disable-features=BackForwardCache` resolved it. Treat the
// "Executed X of X SUCCESS" line as the real signal until this is
// revisited (e.g. by upgrading karma/karma-chrome-launcher or migrating
// off Karma).

module.exports = function (config) {
  config.set({
    basePath: '',
    frameworks: ['jasmine', '@angular-devkit/build-angular'],
    plugins: [
      require('karma-jasmine'),
      require('karma-chrome-launcher'),
      require('karma-jasmine-html-reporter'),
      require('karma-coverage'),
      require('@angular-devkit/build-angular/plugins/karma')
    ],
    client: {
      jasmine: {
        // you can add configuration options for Jasmine here
        // the possible options are listed at https://jasmine.github.io/api/edge/Configuration.html
        // for example, you can disable the random execution with `random: false`
        // or set a specific seed with `seed: 4321`
      },
      clearContext: false // leave Jasmine Spec Runner output visible in browser
    },
    jasmineHtmlReporter: {
      suppressAll: true // removes the duplicated traces
    },
    coverageReporter: {
      dir: require('path').join(__dirname, './coverage/fi-ncp-front'),
      subdir: '.',
      reporters: [
        { type: 'html' },
        { type: 'text-summary' }
      ]
    },
    reporters: ['progress', 'kjhtml'],
    port: 9876,
    colors: true,
    logLevel: config.LOG_INFO,
    autoWatch: true,
    browsers: ['Chrome'],
    singleRun: false,
    restartOnFileChange: true
  });
};
