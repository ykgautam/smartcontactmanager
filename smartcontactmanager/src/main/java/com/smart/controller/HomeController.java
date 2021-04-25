package com.smart.controller;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
public class HomeController {

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
		return "signup";
	}

	/*
	 * @Autowired private UserRepository UserRepository;
	 * 
	 * @GetMapping("/test")
	 * 
	 * @ResponseBody public String test() { User user=new User();
	 * user.setName("Yash"); user.setEmail("yash@gmail.com"); Contact contact=new
	 * Contact();
	 * 
	 * user.getContacts().add(contact); UserRepository.save(user); return "working";
	 * }
	 */

}
