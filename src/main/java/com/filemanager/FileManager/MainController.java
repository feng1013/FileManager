package com.filemanager.FileManager;

import java.io.InputStream;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.UnsupportedEncodingException;
import java.io.IOException;
import java.util.stream.Collectors;

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
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.mvc.method.annotation.MvcUriComponentsBuilder;
import org.springframework.validation.BindingResult;

import java.util.List;
import java.util.ArrayList;

/**
 * The main controller of the MVC. 
 */ 
@Controller
public class MainController {

	@Autowired
	private UserRepository userRepository;

	@Autowired
	private FileRepository fileRepository;

	@Autowired
	private FileService fileService;

	@Autowired
	private FileLinkService fileLinkService;

	@Autowired
	private FileLinkRepository fileLinkRepository;


	/**
	 * Verify whether the link is still valid. Only initiate the download if so.
	 */ 
	@RequestMapping("/download/{id}")
	public ResponseEntity<InputStreamResource> downloadFile(@PathVariable String id) {

		// Will be anonymousUser if is not logged in.
		System.out.println("[download] The current user is: " + getCurrentUserAuth().getName());

		if(!fileLinkService.verifyLink(id)){
			System.out.println("Link expired!");
			HttpHeaders invalidPageHeader = new HttpHeaders();
			invalidPageHeader.add("Location", "/invalid");
			return new ResponseEntity<>(invalidPageHeader, HttpStatus.FOUND);
		} else {
			return fileService.downloadFile(id);
		}

	}

	@RequestMapping(value = "uploadFile", method = RequestMethod.POST)
	public String uploadFile(@RequestParam(value = "fileName") MultipartFile file){

		String ownerName = getCurrentUserAuth().getName();
		if(ownerName == null || ownerName.equals("anonymousUser")){
			return "redirect:/error";
		}

		return fileService.uploadFile(file, getCurrentUserAuth().getName());
	}

	/**
	 * Handles the generateLink request.
	 * 
	 * This method will generate the download link for the specified file.
	 */ 
	@RequestMapping(value = "generateLink", method = RequestMethod.POST)
	public String generateLink(@RequestParam(value = "id") String id, Model model){
		fileLinkService.generateLink(id);

		model.addAttribute("wrappers", composeFilesWrapperForUser());

		return "main";
	}

	/**
	 * Lists all the files for the given user.
	 * 
	 * Please be noted that the user ID is not passed in. Since in our model this method could only be called after user logged in, this method calls getCurrentUserAuth() to retrieve the user.
	 */ 
	@RequestMapping(value = "/main", method = RequestMethod.GET)
	public String listAllFiles(Model model) throws IOException {
		model.addAttribute("wrappers", composeFilesWrapperForUser());

		return "main";
	}

	private List<FileWrapper> composeFilesWrapperForUser(){
		String ownerName = getCurrentUserAuth().getName();
		User user = userRepository.findByUsername(ownerName).get(0);

		List<File> files = fileRepository.findByUserId(user.getId());
		ArrayList<FileWrapper> wrappers = new ArrayList<>();

		for(File f : files) {
			FileWrapper wrapper = new FileWrapper();
			wrapper.setFile(f);
			wrapper.setFullLink(adjustWithFullLink(f));
			wrappers.add(wrapper);
		}

		return wrappers;
	}

	private String adjustWithFullLink(File file){
		String prefix = "http://localhost:8080/download/";
		System.out.println("the file id is: " + file.getId());
		FileLink fileLink = fileLinkRepository.findByFileId(file.getId()).get(0);

		if(fileLinkService.verifyLink(fileLink.getLinkValue())){
			return prefix + fileLink.getLinkValue();
		} else {
			return "N/A";
		}
	}


	private Authentication getCurrentUserAuth(){
		return SecurityContextHolder.getContext().getAuthentication();
	}

}