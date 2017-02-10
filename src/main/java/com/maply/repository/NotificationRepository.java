package com.maply.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.maply.entity.Notification;

@Repository
public interface NotificationRepository extends
		JpaRepository<Notification, Long> {

	Notification findByUserId(Long userId);

}
