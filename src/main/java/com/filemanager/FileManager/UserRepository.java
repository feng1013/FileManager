package com.filemanager.FileManager;

import org.springframework.data.repository.CrudRepository;
import com.filemanager.FileManager.User;
import java.util.List;

// This will be AUTO IMPLEMENTED by Spring into a Bean called userRepository
// CRUD refers Create, Read, Update, Delete

public interface UserRepository extends CrudRepository<User, Integer> {

	public List<User> findByUsername(String name);

}