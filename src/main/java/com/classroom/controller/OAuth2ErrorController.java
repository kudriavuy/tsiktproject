package com.classroom.controller;

import jakarta.servlet.http.HttpServletRequest;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

@Controller
@RequestMapping("/api/auth/oauth2")
public class OAuth2ErrorController {

    @GetMapping("/failure")
    public String handleOAuth2Failure(@RequestParam(required = false) String error,
                                      @RequestParam(required = false) String error_description,
                                      HttpServletRequest request) {
        String errorMessage = "Google authorization error";

        // Try to get error from request parameters
        if (error == null) {
            error = request.getParameter("error");
        }
        if (error_description == null) {
            error_description = request.getParameter("error_description");
        }

        if (error != null && !error.isEmpty()) {
            switch (error) {
                case "invalid_client":
                    errorMessage = "OAuth client not found. Check Client ID and Client Secret in application.properties";
                    break;
                case "access_denied":
                    errorMessage = "Access denied. You cancelled authorization.";
                    break;
                case "invalid_request":
                    errorMessage = "Invalid request to Google OAuth. Most common reasons:<br>" +
                            "• Redirect URI doesn't match settings in Google Cloud Console<br>" +
                            "• Client ID or Client Secret are incorrect<br>" +
                            "• OAuth Consent Screen is not configured<br>" +
                            "<br><strong>Check in Google Cloud Console:</strong><br>" +
                            "1. Credentials > OAuth 2.0 Client ID<br>" +
                            "2. Authorized redirect URIs should contain:<br>" +
                            "&nbsp;&nbsp;&nbsp;<code>http://localhost:8080/login/oauth2/code/google</code><br>" +
                            "3. OAuth consent screen should be configured";
                    break;
                default:
                    errorMessage = "Error: " + error;
            }
        }

        if (error_description != null && !error_description.isEmpty()) {
            errorMessage += "<br><br>Details: " + error_description;
        } else if (error == null || error.isEmpty()) {
            errorMessage += "<br><br>Possible reasons:<br>" +
                    "• Client ID or Client Secret are incorrect<br>" +
                    "• OAuth Consent Screen is not configured<br>" +
                    "• Redirect URI doesn't match settings in Google Cloud Console";
        }

        String html = String.format(
                "<html><head><title>OAuth2 Error</title>" +
                        "<style>body { font-family: Arial; padding: 50px; background: #f5f5f5; } " +
                        ".error-box { background: white; padding: 30px; border-radius: 5px; max-width: 600px; margin: 0 auto; } " +
                        "h1 { color: #d32f2f; } " +
                        "p { line-height: 1.6; } " +
                        ".code { background: #f5f5f5; padding: 10px; border-radius: 3px; font-family: monospace; }</style></head>" +
                        "<body><div class='error-box'>" +
                        "<h1>Google Authorization Error</h1>" +
                        "<p>%s</p>" +
                        "<h2>What to do:</h2>" +
                        "<ol>" +
                        "<li>Check that Client ID and Client Secret are correct in application.properties</li>" +
                        "<li>Make sure OAuth Consent Screen is configured in Google Cloud Console</li>" +
                        "<li>Check that Redirect URI in Google Cloud Console: <span class='code'>http://localhost:8080/login/oauth2/code/google</span></li>" +
                        "<li>Restart the server after changing settings</li>" +
                        "</ol>" +
                        "<p><a href='http://localhost:8080/oauth2/authorization/google'>Try again</a></p>" +
                        "</div></body></html>",
                errorMessage
        );

        return html;
    }
}

