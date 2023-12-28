package com.filemanager.FileManager;

import org.springframework.data.repository.CrudRepository;

import com.filemanager.FileManager.FileLink;

// This will be AUTO IMPLEMENTED by Spring into a Bean called userRepository
// CRUD refers Create, Read, Update, Delete

public interface FileLinkRepository extends CrudRepository<FileLink, String> {

}