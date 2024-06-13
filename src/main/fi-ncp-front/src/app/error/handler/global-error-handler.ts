import {HttpClient} from '@angular/common/http';
import { ErrorHandler, Injectable } from '@angular/core';

@Injectable()
export class GlobalErrorHandler implements ErrorHandler {
  http: HttpClient;
  constructor(http: HttpClient) {
    this.http = http;
    this.initJSHandler();  // If for some reason angular handler is not fired, we have backup.
  }

  /**
   * Init JS error handler
   */
  initJSHandler() {
    // Catch JS errors
    window.addEventListener("error", (error) => {
      console.log("Window handler fired!");
      let errorContent: string = "";

      errorContent =
        `Source: JS\n
        Message: ${error.message}\n`;
      this.postError(errorContent);
    });
  }

  /**
   * Catch Angular errors
   * @param error
   */
  handleError(error: any) {
    console.log("Error handler fired!");
    let errorContent: string = "";

    errorContent +=
    `Source: Angular\n
    Name: ${error.name}\n
    Message: ${error.message}\n
    Stack:${error.stack}`;
    this.postError(errorContent);
  }

  /**
   * Post error to server.
   * Never log personal data and make sure your server will encrypt the data.
   * @param errorContent
   */
  postError(errorContent: any) {
    this.http.post("api/error", errorContent).subscribe({
      error: errorRes => {
        console.error("Logging failure", errorRes);
      },
    });
  }
}
