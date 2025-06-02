package com.carizo.controller;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/user")
public class UserController {
	 @GetMapping("/user-dashboard")
	    public String userDashboard() {
	        return "Welcome to User Dashboard";
	    }

}
