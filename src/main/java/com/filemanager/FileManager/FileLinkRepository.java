package com.filemanager.FileManager;

import org.springframework.data.repository.CrudRepository;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.data.jpa.repository.Modifying;
import java.util.List;
import com.filemanager.FileManager.FileLink;

// This will be AUTO IMPLEMENTED by Spring into a Bean called userRepository
// CRUD refers Create, Read, Update, Delete

public interface FileLinkRepository extends CrudRepository<FileLink, String> {

	public List<FileLink> findByFileId(String fileId);

	@Modifying
	@Transactional
	public void deleteByFileId(String fileId);

}