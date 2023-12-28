package com.filemanager.FileManager;

import org.springframework.data.repository.CrudRepository;
import java.util.List;
import com.filemanager.FileManager.File;

// This will be AUTO IMPLEMENTED by Spring into a Bean called userRepository
// CRUD refers Create, Read, Update, Delete

public interface FileRepository extends CrudRepository<File, String> {

	public List<File> findByUserId(Integer userId);

}