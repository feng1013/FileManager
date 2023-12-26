package com.filemanager.FileManager;


import java.io.InputStream;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.UnsupportedEncodingException;

import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;

import org.springframework.core.io.InputStreamResource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@Controller
public class MainController {

	@RequestMapping("/test")
	public String home() {
		return "home";
	}

	@RequestMapping("/download")
	public ResponseEntity<InputStreamResource> downloadFile() {
		try{
			String filename = "4700720.jpg";
			InputStream in = new FileInputStream(filename);
			System.out.println("stream created!!!");

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



}