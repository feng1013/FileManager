package com.filemanager.FileManager;

import java.io.File;
import java.io.FileInputStream;
import java.io.InputStream;
import java.io.IOException;

import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.nio.charset.StandardCharsets;

import java.security.MessageDigest;
import java.math.BigInteger;
import java.security.NoSuchAlgorithmException;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;


@Service
public class FileLinkService {

	@Autowired
	private FileRepository fileRepository;

	@Autowired
	private FileLinkRepository fileLinkRepository;

	private static final long TIME_TO_EXPIRE_IN_SEC = 20;


	public String generateLink(String fileId){

		long currentTime = System.currentTimeMillis();
		long expireTime = currentTime + TIME_TO_EXPIRE_IN_SEC * 1000;
		String originString = fileId + "_" + currentTime;

		FileLink fileLink = new FileLink();
		fileLink.setLinkValue(conductHash(originString));
		fileLink.setFileId(fileId);
		fileLink.setCreatedTime(String.valueOf(currentTime));
		fileLink.setExpireTime(String.valueOf(expireTime));

		if(fileLinkRepository.findByFileId(fileId).size() > 0){
			fileLinkRepository.deleteByFileId(fileId);
		}


		fileLinkRepository.save(fileLink);

		System.out.println("the hash value = " + fileLink.getLinkValue());


		return fileLink.getLinkValue();
	}

	public boolean verifyLink(String linkValue){

		FileLink fileLink = fileLinkRepository.findById(linkValue).get();
		long currentTime = System.currentTimeMillis();
		long createdTime = Long.valueOf(fileLink.getCreatedTime());

		System.out.println(linkValue + " valid? " + (currentTime - createdTime < TIME_TO_EXPIRE_IN_SEC * 1000));

		return currentTime - createdTime < TIME_TO_EXPIRE_IN_SEC * 1000;
	}


	private String conductHash(String origin){
		try{
			MessageDigest digest = MessageDigest.getInstance("SHA-256");
			byte[] shaByte = digest.digest(origin.getBytes(StandardCharsets.UTF_8));
			BigInteger number = new BigInteger(1, shaByte);
			return new StringBuilder(number.toString(16)).toString();
		} catch (NoSuchAlgorithmException e){
			e.printStackTrace();
			return "";
		}
	}
}