package com.maply.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.maply.entity.Event;

public interface EventRepository extends JpaRepository<Event, Long> {

}
