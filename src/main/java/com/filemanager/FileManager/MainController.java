package com.filemanager.FileManager;

import java.io.InputStream;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.UnsupportedEncodingException;
import java.io.IOException;

import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.InputStreamResource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.multipart.MultipartFile;

import java.util.ArrayList;

@Controller
public class MainController {

	@Autowired
	private UserRepository userRepository;

	@RequestMapping("/download/{id}")
	public ResponseEntity<InputStreamResource> downloadFile(@PathVariable String id) {

		for(User u : userRepository.findAll()){
			System.out.println("id=" + u.getId());
		}


		if(!id.equals("111")){
			HttpHeaders invalidPageHeader = new HttpHeaders();
			invalidPageHeader.add("Location", "/invalid");
			return new ResponseEntity<>(invalidPageHeader, HttpStatus.FOUND);
		}

		try{
			String filename = "4700720.jpg";
			InputStream in = new FileInputStream(filename);
			System.out.println("stream created!!! 12/27 05:08");

			HttpHeaders headers = new HttpHeaders();
        	headers.add(HttpHeaders.CONTENT_DISPOSITION, String.format("attachment; filename=\"%s\"", URLEncoder.encode(filename, StandardCharsets.UTF_8.name())));

			return ResponseEntity.ok()
			.headers(headers)
			.contentType(MediaType.APPLICATION_OCTET_STREAM)
    		.body(new InputStreamResource(in));
		} catch (FileNotFoundException e){
			System.out.println("File not found");

			return new ResponseEntity<>(HttpStatus.NOT_FOUND);
		} catch (UnsupportedEncodingException e){
			System.out.println("Error when transforming file names ");

			return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
		}
	}

	@RequestMapping(value = "uploadFile", method = RequestMethod.POST)
	public String uploadFile(@RequestParam(value = "fileName") MultipartFile file ){
		System.out.println("Post method Called! the file is: " + file);

		Path destinationFile = Paths.get(file.getOriginalFilename()).normalize().toAbsolutePath();
		System.out.println("The path is " + destinationFile);

		try (InputStream inputStream = file.getInputStream()) {
				Files.copy(inputStream, destinationFile,
					StandardCopyOption.REPLACE_EXISTING);
		} catch (IOException e) {
			e.printStackTrace();
			return "redirect:/error";
		}

		return "redirect:/hello";
	}

}