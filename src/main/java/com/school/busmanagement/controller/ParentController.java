package com.school.busmanagement.controller;

import com.school.busmanagement.security.CustomUserDetails;
import com.school.busmanagement.service.ParentService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
@RequestMapping("/parent")
@PreAuthorize("hasRole('PARENT')")
@RequiredArgsConstructor
public class ParentController {

    private final ParentService parentService;

    // Render the parent dashboard using the logged-in user's email address
    // from the Spring Security context.
    @GetMapping("/dashboard")
    public String dashboard(@AuthenticationPrincipal CustomUserDetails userDetails, Model model) {
        model.addAttribute("dashboardData", parentService.getDashboardForParent(userDetails.getUsername()));
        return "parent/dashboard";
    }
}
