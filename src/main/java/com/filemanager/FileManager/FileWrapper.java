package com.filemanager.FileManager;

public class FileWrapper {

	private File file;

	private String fullLink;


	public File getFile(){
		return file;
	}

	public void setFile(File file){
		this.file = file;
	}

	public String getFullLink(){
		return fullLink;
	}

	public void setFullLink(String fullLink){
		this.fullLink = fullLink;
	}

}