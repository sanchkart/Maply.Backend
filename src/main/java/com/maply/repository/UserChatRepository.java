package com.maply.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.maply.entity.User;
import com.maply.entity.UserChat;

@Repository
public interface UserChatRepository extends JpaRepository<UserChat, Long> {

	Long countByUserAndIsUnread(User user, Boolean isUnread);
}
