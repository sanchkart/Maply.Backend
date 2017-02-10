package com.maply.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.maply.entity.Message;

@Repository
public interface MessageRepository extends JpaRepository<Message, Long> {

	Message findById(Long messageId);


}
