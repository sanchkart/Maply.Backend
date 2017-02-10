package com.maply.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.maply.entity.User;

@Repository
public interface UserRepository extends JpaRepository<User, Long> {

	User findByFacebookId(String facebookId);

	User findByIdAndIsPublished(Long id, Boolean isPublished);

	User findByFacebookIdAndIsPublished(String facebookId, Boolean isPublished);

	User findByUsernameIgnoreCase(String username);
	
	User findByEmailIgnoreCase(String email);

}
