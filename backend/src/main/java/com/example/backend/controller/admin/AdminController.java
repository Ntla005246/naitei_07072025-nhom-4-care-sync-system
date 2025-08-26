package com.example.backend.controller.admin;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
@RequestMapping("/admin")
public class AdminController {

    @GetMapping
    public String index() {
        return "admin/dashboard";
    }

    @GetMapping("/dashboard")
    public String dashboard() {
        return "admin/dashboard";
    }

    @GetMapping("/users")
    public String users() {
        return "admin/users";
    }

    @GetMapping("/appointments")
    public String appointments() {
        return "admin/appointments";
    }

    @GetMapping("/doctors")
    public String doctors() {
        return "admin/doctors";
    }

    @GetMapping("/patients")
    public String patients() {
        return "admin/patients";
    }

    @GetMapping("/payments")
    public String payments() {
        return "admin/payments";
    }

    @GetMapping("/template-example")
    public String templateExample() {
        return "admin/template-example";
    }
}
