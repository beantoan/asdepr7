package it.unical.asde.pr7.controller;

import it.unical.asde.pr7.entity.User;
import it.unical.asde.pr7.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.validation.BindingResult;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.servlet.ModelAndView;

@Controller
public class UserController {

    @Autowired
    private UserService userService;

    @GetMapping("/login")
    public ModelAndView login() {
        return new ModelAndView("login", "user", new User());
    }

    @GetMapping("/register")
    public ModelAndView register() {
        return new ModelAndView("register", "user", new User());
    }

    @PostMapping("/register")
    public ModelAndView create(@Validated @ModelAttribute("user") User user, BindingResult bindingResult) {
        if (!bindingResult.hasErrors() && this.userService.createUser(user) != null) {
            return new ModelAndView("redirect:/login");
        } else {
            user.setPassword(null);
            return new ModelAndView("register", "user", user);
        }
    }


}
