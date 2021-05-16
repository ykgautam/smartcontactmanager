package com.smart.controller;

import java.io.File;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.security.Principal;

import javax.servlet.http.HttpSession;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.ClassPathResource;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.multipart.MultipartFile;

import com.smart.dao.UserRepository;
import com.smart.entities.Contact;
import com.smart.entities.User;
import com.smart.helper.Message;

@Controller
@RequestMapping("/user")
public class UserController {
	@Autowired
	private UserRepository userRepository;

//	method for adding common data to response
	@ModelAttribute
	public void addCommonData(Model model, Principal principal) {
		model.addAttribute("title", "dashboard");
		String userName = principal.getName();
		System.out.println("USERNAME: " + userName);

//		get data using user name
		User user = userRepository.getUserByUserName(userName);
		System.out.println(user);
		model.addAttribute("user", user);

	}

//	dashboard home
	@RequestMapping(value = "/index")
	public String dashboard(Model model, Principal principal) {

		return "normal/user_dashboard";
	}

//	open add form handler
	@GetMapping("/add-contact")
	public String openAddContactForm(Model model) {
		model.addAttribute("title", "Add Contact");
		model.addAttribute("contact", new Contact());
		return "normal/add_contact_form";
	}

//	processing add_contact_form 
	@PostMapping("/process-contact")
	public String processContact(@ModelAttribute Contact contact, @RequestParam("profileImage") MultipartFile file,
			Principal principal, HttpSession session) {
		try {
			String name = principal.getName();
			User user = this.userRepository.getUserByUserName(name);

//		processing and uploading file
			if (file.isEmpty()) {
				System.out.println("file is empty");
			} else {
//			file the file to the folder and upload the name to the contact
				contact.setImage(file.getOriginalFilename());
				File saveFile = new ClassPathResource("static/img").getFile();

				Path path = Paths.get(saveFile.getAbsolutePath() + File.separator + file.getOriginalFilename());

				Files.copy(file.getInputStream(), path, StandardCopyOption.REPLACE_EXISTING);

				System.out.println("image is uploaded");

			}

			contact.setUsers(user);

			user.getContacts().add(contact);

			this.userRepository.save(user);

			System.out.println("data " + contact);

			System.out.println("contact added");

//		message success
			session.setAttribute("message", new Message("your contact is added...  add new contact", "success"));

		} catch (Exception e) {
			System.out.println("ERROR " + e.getMessage());

//			message error
			session.setAttribute("message", new Message("something went wrong !! Try agian", "danger"));

		}
		return "normal/add_contact_form";
	}
}
