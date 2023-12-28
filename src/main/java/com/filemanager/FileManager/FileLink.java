package com.filemanager.FileManager;

import java.util.Set;

import jakarta.persistence.Entity;
import jakarta.persistence.Table;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.UniqueConstraint;
import jakarta.persistence.OneToMany;
import jakarta.persistence.FetchType;
import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.JoinColumn;;

@Entity
public class FileLink {

    @Id
    private String linkValue;

    @Column(name="FileId")
    private String fileId;

    private String expireTime;

    public String getLinkValue(){
      return linkValue;
    }

    public void setLinkValue(String linkValue){
      this.linkValue = linkValue;
    }

    public String getFileId(){
      return fileId;
    }

    public void setFileId(String fileId){
      this.fileId = fileId;
    }

    public String getExpireTime(){
      return expireTime;
    }

    public void setExpireTime(String expireTime){
      this.expireTime = expireTime;
    }

}