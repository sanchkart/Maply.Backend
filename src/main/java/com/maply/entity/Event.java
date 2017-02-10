package com.maply.entity;

import javax.persistence.Entity;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonInclude.Include;
import com.maply.Constant;
import com.maply.util.StringUtility;

@JsonInclude(value = Include.NON_EMPTY)
@Entity
public class Event extends AbstractEntity {

	private String name;
	private String title;
	private Double latitude;
	private Double longitude;
	private String location;
	private String image;
	private String description;

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public Double getLatitude() {
		return latitude;
	}

	public void setLatitude(Double latitude) {
		this.latitude = latitude;
	}

	public Double getLongitude() {
		return longitude;
	}

	public void setLongitude(Double longitude) {
		this.longitude = longitude;
	}

	public String getLocation() {
		return location;
	}

	public void setLocation(String location) {
		this.location = location;
	}

	public String getImage() {
		if (!StringUtility.isNullOrEmpty(image) && !image.toLowerCase().startsWith("http")) {
			return Constant.BUCKET_URL + image;
		}

		return image;
	}

	public void setImage(String image) {
		this.image = image;
	}

	public String getTitle() {
		return title;
	}

	public void setTitle(String title) {
		this.title = title;
	}

	public String getDescription() {
		return description;
	}

	public void setDescription(String description) {
		this.description = description;
	}

}
