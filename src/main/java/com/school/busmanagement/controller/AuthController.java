package com.school.busmanagement.controller;

import com.school.busmanagement.security.CustomUserDetails;
import com.school.busmanagement.service.AuthService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.AnonymousAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
@RequiredArgsConstructor
public class AuthController {

    private final AuthService authService;

    // Redirect the root URL to the dashboard entry point.
    @GetMapping("/")
    public String home() {
        return "redirect:/dashboard";
    }

    // Render the login page. If the user is already authenticated,
    // send them to the dashboard redirect endpoint instead.
    @GetMapping("/login")
    public String login(Authentication authentication) {
        if (authentication != null
                && authentication.isAuthenticated()
                && !(authentication instanceof AnonymousAuthenticationToken)) {
            return "redirect:/dashboard";
        }
        return "login";
    }

    // After login, inspect the authenticated user's role and forward them
    // to the correct dashboard page.
    @GetMapping("/dashboard")
    public String redirectAfterLogin(@AuthenticationPrincipal CustomUserDetails userDetails) {
        return "redirect:" + authService.getDashboardRedirect(userDetails);
    }
}
