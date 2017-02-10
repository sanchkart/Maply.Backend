package com.maply.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.maply.entity.Friend;
import com.maply.entity.User;

@Repository
public interface FriendRepository extends JpaRepository<Friend, Long> {

	Friend findByUserByAndUserTo(User user, User friend);

}
