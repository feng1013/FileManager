package com.filemanager.FileManager;

import java.util.Set;

import jakarta.persistence.Entity;
import jakarta.persistence.Table;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.UniqueConstraint;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.FetchType;
import jakarta.persistence.Column;
import jakarta.persistence.OneToMany;


@Entity
public class File {

	@Id
  	private String id; // Example: Mary_1703783552840.jpg

  	private String filename; // Example: Harry Potter and the Philosopher's Stone.pdf

  	@Column(name="UserId")
  	private Integer userId;

  	// @Id
  	// @GeneratedValue(strategy=GenerationType.IDENTITY)
  	// private Integer extId;

  	@OneToMany
  	@JoinColumn(name="FileId")
  	private Set<FileLink> fileLinks;


  	public String getId(){
  		return id;
  	}

  	public void setId(String id){
  		this.id = id;
  	}

  	public String getFilename(){
  		return filename;
  	}

  	public void setFilename(String filename){
  		this.filename = filename;
  	}

  	public Integer getUserId(){
  		return userId;
  	}

  	public void setUserId(Integer userId){
  		this.userId = userId;
  	}

  	// public Integer getExtId(){
  	// 	return extId;
  	// }

  	// public void setExtId(Integer extId){
  	// 	this.extId = extId;
  	// }
}