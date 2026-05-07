package com.example.drone.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class PageController {

    @GetMapping("/login")
    public String login() { return "login"; }

    @GetMapping("/register")
    public String register() { return "register"; }

    @GetMapping("/mfa")
    public String mfa() { return "mfa"; }

    @GetMapping("/dashboard")
    public String dashboard() { return "dashboard"; }

    @GetMapping("/missions")
    public String missions() { return "missions"; }

    @GetMapping("/flight-logs")
    public String flightLogs() { return "flight-logs"; }

    @GetMapping("/maintenance")
    public String maintenance() { return "maintenance"; }
}
