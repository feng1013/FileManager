package com.filemanager.FileManager;

import java.io.File;
import java.io.FileInputStream;
import java.io.InputStream;
import java.io.IOException;

import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;


@Service
public class FileService {

	@Autowired
	private UserRepository userRepository;

	@Autowired
	private FileRepository fileRepository;

	@Autowired
	private FileLinkService fileLinkService;

	// Should be "/tmp/files" if running under Google Application Engine.
	private static final String PATH_TO_DESTINATION_DIR = "./files";

	protected String uploadFile(MultipartFile file, String owner){
		try {
			if(!hasDestinationDirectory()) Files.createDirectories(Paths.get(PATH_TO_DESTINATION_DIR).toAbsolutePath());

			System.out.println("in 34: " + file.getOriginalFilename());

			long currentTime = System.currentTimeMillis();
			String storingFileName = owner + "_" + currentTime;
			
			String ext = composeExt(file.getOriginalFilename());
			if(ext.length() > 0) {
				storingFileName = storingFileName + "." + ext;
			}

						System.out.println("in 44: " + file.getOriginalFilename());

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

	private boolean hasDestinationDirectory(){
		return new File(PATH_TO_DESTINATION_DIR).exists();
	}

	private void generateAndSaveDbFileObject(MultipartFile file, String storingFileName, String owner){

					System.out.println("in 68: " + file.getOriginalFilename());

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