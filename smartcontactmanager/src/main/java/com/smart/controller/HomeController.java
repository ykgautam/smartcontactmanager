package com.smart.controller;

import javax.servlet.http.HttpSession;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;

import com.smart.dao.UserRepository;
import com.smart.entities.User;
import com.smart.helper.Message;

@Controller
public class HomeController {

	@Autowired
	private UserRepository userRepository;

	@RequestMapping(value = "/")
	public String home(Model model) {
		model.addAttribute("title", "Home- smart contact manager");
		return "home";
	}

	@RequestMapping(value = "/about")
	public String about(Model model) {
		model.addAttribute("title", "About - smart contact manager");
		return "about";
	}

	@RequestMapping(value = "/signup")
	public String signup(Model model) {
		model.addAttribute("title", "Register - smart contact manager");
		model.addAttribute("user", new User());
		return "signup";
	}

//	handler for register

	@RequestMapping(value = "/do_register", method = RequestMethod.POST)
	public String registerUrl(@ModelAttribute("user") User user,
			@RequestParam(value = "agreement", defaultValue = "false") boolean agreement, Model model,
			HttpSession session) {

		try {

			if (!agreement) {
				System.out.println("you have not agreed the terms and conditions");
				throw new Exception("you have not agreed the terms and conditions");
			}

			user.setRole("ROLE_USER");
			user.setEnabled(true);

			System.out.println("Agreement " + agreement);
			System.out.println("user " + user);

			User result = this.userRepository.save(user);
			model.addAttribute("user", result);
			session.setAttribute("message", new Message("succefully registered ", "alert-success"));
			return "signup";

		} catch (Exception e) {
			e.printStackTrace();
			model.addAttribute("user", user);
			session.setAttribute("message", new Message("something went wrong ", "alert-danger"));
			return "signup";
		}

	}

}
