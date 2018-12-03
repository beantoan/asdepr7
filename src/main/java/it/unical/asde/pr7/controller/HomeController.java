package it.unical.asde.pr7.controller;

import it.unical.asde.pr7.entity.User;
import it.unical.asde.pr7.service.RoomService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;

@Controller
public class HomeController extends BaseController {

    @Autowired
    RoomService roomService;

    @GetMapping("/")
    public String welcome() {
        return "welcome";
    }

    @GetMapping("/lobby")
    public String lobby() {
        return "lobby";
    }

    @GetMapping("/achievement")
    public String achievement(@RequestParam(value = "page", required = false) Integer page, Model model) {
        User user = this.loggedInUser();

        model.addAttribute("user", user);
        model.addAttribute("rooms", this.roomService.findAchievements(user, page));

        return "achievement";
    }
}
