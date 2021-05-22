package com.smart.controller;

import java.io.File;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.security.Principal;
import java.util.List;
import java.util.Optional;

import javax.servlet.http.HttpSession;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.ClassPathResource;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.multipart.MultipartFile;

import com.smart.dao.ContactRepository;
import com.smart.dao.UserRepository;
import com.smart.entities.Contact;
import com.smart.entities.User;
import com.smart.helper.Message;

@Controller
@RequestMapping("/user")
public class UserController {
	@Autowired
	private UserRepository userRepository;

	@Autowired
	private ContactRepository contactRepository;

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
//				setting a default pic if pic is not provided
				contact.setImage("default_pic.png");
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

//	show contact handler
//	per page =5[n]
//	current page=0[page]
	@GetMapping("/show-contacts/{page}")
	public String showContacts(@PathVariable("page") Integer page, Model model, Principal principal) {
		model.addAttribute("title", "show user contacts");
		String userName = principal.getName();
		User user = this.userRepository.getUserByUserName(userName);

//		currentpage- page
//		contact per page -5
		Pageable pageable = PageRequest.of(page, 5);
		Page<Contact> contactsByUser = this.contactRepository.findContactsByUser(user.getId(), pageable);

		model.addAttribute("contacts", contactsByUser);
		model.addAttribute("currentPage", page);
		model.addAttribute("totalPages", contactsByUser.getTotalPages());

		return "normal/show_contacts";
	}

	@GetMapping(path = "/contact/{cid}")
	public String showContactDetail(@PathVariable("cid") Integer cid, Model model, Principal principal) {
		System.out.println("cid " + cid);

		Optional<Contact> result = this.contactRepository.findById(cid);
		Contact contact = result.get();

//		getting current user 
		String userName = principal.getName();
		User user = this.userRepository.getUserByUserName(userName);

//		verify the authorized user 
		if (user.getId() == contact.getUsers().getId()) {
			model.addAttribute("contact", contact);
			model.addAttribute("title", contact.getName());
		}
		return "normal/contact_detail";
	}

}
