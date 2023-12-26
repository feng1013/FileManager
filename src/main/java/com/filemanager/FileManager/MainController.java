package com.filemanager.FileManager;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;


@RestController
public class MainController {

	@GetMapping("/")
	public String home() {
		return "Greetings from Spring Boot!!!!!!!!!!!";
	}

	@GetMapping("/allFiles")
	public String fetchAllFiles() {
		return "You are viewing all your files";
	}



}