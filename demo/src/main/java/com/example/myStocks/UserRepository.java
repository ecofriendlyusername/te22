package com.example.myStocks;

import org.springframework.data.repository.CrudRepository;


// This will be AUTO IMPLEMENTED by Spring into a Bean called userRepository
// CRUD refers Create, Read, Update, Delete

public interface UserRepository extends CrudRepository<User, Integer> {
	User findByName(String name);
	
	boolean existsByName(String name);

	boolean existsByEmail(String email);

	User findByEmail(String name);
}