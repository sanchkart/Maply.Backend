package com.maply.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.maply.entity.Journey;

@Repository
public interface JourneyRepository extends JpaRepository<Journey, Long> {

	Journey findByLocationIdAndUserIdAndIsPublished(String locationId, Long id, Boolean isPublished);

}
