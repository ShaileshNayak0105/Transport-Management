package com.school.busmanagement.exception;

import jakarta.servlet.http.HttpServletRequest;
import java.net.URI;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.servlet.FlashMap;
import org.springframework.web.servlet.support.RequestContextUtils;

@ControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(ResourceNotFoundException.class)
    public String handleResourceNotFound(ResourceNotFoundException exception, HttpServletRequest request, Model model) {
        String refererRedirect = buildRefererRedirect(request, exception.getMessage());
        if (refererRedirect != null) {
            return refererRedirect;
        }

        model.addAttribute("errorTitle", "Record not found");
        model.addAttribute("errorMessage", exception.getMessage());
        model.addAttribute("path", request.getRequestURI());
        return "error";
    }

    @ExceptionHandler(BusFullException.class)
    public String handleBusFull(BusFullException exception, HttpServletRequest request) {
        return redirectBackWithFlashMessage(request, exception.getMessage(), "/admin/students");
    }

    @ExceptionHandler(DuplicateEmailException.class)
    public String handleDuplicateEmail(DuplicateEmailException exception, HttpServletRequest request) {
        return redirectBackWithFlashMessage(request, exception.getMessage(), "/admin/parents/new");
    }

    @ExceptionHandler(IllegalArgumentException.class)
    public String handleBusinessExceptions(RuntimeException exception, HttpServletRequest request) {
        return redirectBackWithFlashMessage(request, exception.getMessage(), "/dashboard");
    }

    @ExceptionHandler(Exception.class)
    public String handleGenericException(Exception exception, HttpServletRequest request, Model model) {
        model.addAttribute("errorTitle", "Something went wrong");
        model.addAttribute("errorMessage", exception.getMessage());
        model.addAttribute("path", request.getRequestURI());
        return "error";
    }

    private String redirectBackWithFlashMessage(HttpServletRequest request, String errorMessage, String fallbackPath) {
        String referer = request.getHeader("Referer");
        FlashMap flashMap = RequestContextUtils.getOutputFlashMap(request);
        flashMap.put("errorMessage", errorMessage);

        if (referer == null || referer.isBlank()) {
            return "redirect:" + fallbackPath;
        }

        try {
            URI uri = URI.create(referer);
            String path = uri.getPath();
            if (uri.getQuery() != null && !uri.getQuery().isBlank()) {
                path = path + "?" + uri.getQuery();
            }
            return "redirect:" + path;
        } catch (IllegalArgumentException ignored) {
            return "redirect:" + fallbackPath;
        }
    }

    private String buildRefererRedirect(HttpServletRequest request, String errorMessage) {
        String referer = request.getHeader("Referer");
        if (referer == null || referer.isBlank()) {
            return null;
        }
        return redirectBackWithFlashMessage(request, errorMessage, "/dashboard");
    }
}
