package it.unical.asde.pr7.controller;

import it.unical.asde.pr7.entity.User;
import it.unical.asde.pr7.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;

public abstract class BaseController {

    @Autowired
    protected UserService userService;

    protected final User loggedInUser() {
        String username = this.getAuthentication().getName();

        return userService.findByUsername(username);
    }

    private Authentication getAuthentication() {
        return SecurityContextHolder.getContext().getAuthentication();
    }
}
