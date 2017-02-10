package com.maply.response;

import java.util.Date;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonInclude.Include;
import com.maply.Constant;
import com.maply.entity.User;
import com.maply.util.StringUtility;

@JsonInclude(value = Include.NON_EMPTY)
public class JourneyResponse {

	private Long id;
	private User user;
	private String name;
	private String image;
	private Double latitude;
	private Double longitude;
	private Integer moments;
	private Boolean isNew;
	@JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd'T'HH:mm:ss'Z'")
	private Date createdDate;

	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}

	public User getUser() {
		return user;
	}

	public void setUser(User user) {
		this.user = user;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getImage() {
		if (!StringUtility.isNullOrEmpty(image)) {
			return Constant.BUCKET_URL + image;
		}

		return null;
	}

	public void setImage(String image) {
		this.image = image;
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

	public Integer getMoments() {
		return moments;
	}

	public void setMoments(Integer moments) {
		this.moments = moments;
	}

	public Boolean getIsNew() {
		return isNew;
	}

	public void setIsNew(Boolean isNew) {
		this.isNew = isNew;
	}

	public Date getCreatedDate() {
		return createdDate;
	}

	public void setCreatedDate(Date createdDate) {
		this.createdDate = createdDate;
	}
}
