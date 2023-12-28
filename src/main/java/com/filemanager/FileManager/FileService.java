package com.filemanager.FileManager;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.UnsupportedEncodingException;
import java.io.InputStream;
import java.io.IOException;

import java.net.URLEncoder;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.nio.charset.StandardCharsets;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.http.HttpHeaders;
import org.springframework.core.io.InputStreamResource;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.http.HttpStatus;

@Service
public class FileService {

	@Autowired
	private UserRepository userRepository;

	@Autowired
	private FileRepository fileRepository;

	@Autowired
	private FileLinkRepository fileLinkRepository;

	@Autowired
	private FileLinkService fileLinkService;

	// Should be "/tmp/files" if running under Google Application Engine.
	private static final String PATH_TO_DESTINATION_DIR = "./files";

	public String uploadFile(MultipartFile file, String owner){
		try {
			if(!hasDestinationDirectory()) Files.createDirectories(Paths.get(PATH_TO_DESTINATION_DIR).toAbsolutePath());

			System.out.println("in 34: " + file.getOriginalFilename());

			long currentTime = System.currentTimeMillis();
			String storingFileName = owner + "_" + currentTime;
			
			String ext = composeExt(file.getOriginalFilename());
			if(ext.length() > 0) {
				storingFileName = storingFileName + "." + ext;
			}

			generateAndSaveDbFileObject(file, storingFileName, owner);

			Path destinationFile = Paths.get(PATH_TO_DESTINATION_DIR + "/" + storingFileName).normalize().toAbsolutePath();
			
			InputStream inputStream = file.getInputStream();
			Files.copy(inputStream, destinationFile);


			String linkString = fileLinkService.generateLink(storingFileName);

			System.out.println("Save as: " + destinationFile +"; and will be available at " + linkString);

		} catch (IOException e) {
			e.printStackTrace();
			return "redirect:/error";
		}

		return "redirect:/hello";
	}

	public ResponseEntity<InputStreamResource> downloadFile(String linkValue){
		FileLink fileLink = fileLinkRepository.findById(linkValue).get();
		System.out.println("in 80 fileLink's fileId = " + fileLink.getFileId());

		com.filemanager.FileManager.File dbFile = fileRepository.findById(fileLink.getFileId()).get();


		try{
			String fileRoute = PATH_TO_DESTINATION_DIR + "/" + dbFile.getId();
			System.out.println("in 87 the fileRoute = " + fileRoute);
			InputStream in = new FileInputStream(fileRoute);
			System.out.println("Download stream created!!!");

			HttpHeaders headers = new HttpHeaders();
        	headers.add(HttpHeaders.CONTENT_DISPOSITION, String.format("attachment; filename=\"%s\"", URLEncoder.encode(dbFile.getFilename(), StandardCharsets.UTF_8.name())));

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

	private boolean hasDestinationDirectory(){
		return new File(PATH_TO_DESTINATION_DIR).exists();
	}

	private void generateAndSaveDbFileObject(MultipartFile file, String storingFileName, String owner){

		int userId = userRepository.findByUsername(owner).get(0).getId();

		com.filemanager.FileManager.File dbFile = new com.filemanager.FileManager.File();
		dbFile.setId(storingFileName);
		dbFile.setFilename(file.getOriginalFilename());
		dbFile.setUserId(userId);
		fileRepository.save(dbFile);
	}

	private String composeExt(String originalFilename){

		if(originalFilename.split("\\.").length > 1){
			String[] temp = originalFilename.split("\\.");
			if(temp[temp.length - 1].length() <= 4) return temp[temp.length - 1];
 		}

 		return "";
	}

}