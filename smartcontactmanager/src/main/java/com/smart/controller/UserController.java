package com.smart.controller;

import java.security.Principal;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;

import com.smart.dao.UserRepository;
import com.smart.entities.User;

@Controller
@RequestMapping("/user")
public class UserController {
	@Autowired
	private UserRepository userRepository;
	
	@RequestMapping(value = "/index")
	public String dashboard(Model model , Principal principal) {
		model.addAttribute("title", "dashboard");
		String userName = principal.getName();
		System.out.println("USERNAME: "+userName);
				
//		get data using user name
		User user = userRepository.getUserByUserName(userName);
		System.out.println(user);
		model.addAttribute("user", user);
		
		
		
		return "normal/user_dashboard";
	}
	

}
