package com.maply.entity;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Transient;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonInclude.Include;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.maply.Constant;
import com.maply.util.Enumeration.MediaType;
import com.maply.util.StringUtility;

@JsonInclude(value = Include.NON_EMPTY)
@Entity
public class Message extends AbstractEntity {

	private String image;
	@JsonProperty("location_id")
	private String locationId;
	@JsonProperty("location_name")
	private String locationName;
	@JsonProperty("location_address")
	private String locationAddress;
	private Double latitude;
	private Double longitude;
	@Column(length = 1500)
	private String text;
	@JsonIgnore
	private Long journeyId;
	@JsonProperty("user")
	@ManyToOne(optional = false, cascade = { CascadeType.PERSIST, CascadeType.REFRESH })
	@JoinColumn(name = "user_id")
	private User user;
	@Transient
	private Long count;
	private String media;
	private String bannerImage;
	@Transient
	private MediaType type;
	private Long viewCount;
	@Transient
	private Boolean isSaved;
	@Transient
	private boolean expired;
	@JsonIgnore
	private Boolean isPublished;

	public String getImage() {
		if (!StringUtility.isNullOrEmpty(image)) {
			return Constant.BUCKET_URL + image;
		}

		return image;
	}

	public void setImage(String image) {
		this.image = image;
	}

	public String getLocationName() {
		return locationName;
	}

	public void setLocationName(String locationName) {
		this.locationName = locationName;
	}

	public String getLocationAddress() {
		return locationAddress;
	}

	public void setLocationAddress(String locationAddress) {
		this.locationAddress = locationAddress;
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

	public String getText() {
		return text;
	}

	public void setText(String text) {
		this.text = text;
	}

	public Long getJourneyId() {
		return journeyId;
	}

	public void setJourneyId(Long journeyId) {
		this.journeyId = journeyId;
	}

	public String getLocationId() {
		return locationId;
	}

	public void setLocationId(String locationId) {
		this.locationId = locationId;
	}

	public User getUser() {
		return user;
	}

	public void setUser(User user) {
		this.user = user;
	}

	public Long getCount() {
		return count;
	}

	public void setCount(Long count) {
		this.count = count;
	}

	public String getMedia() {
		if (!StringUtility.isNullOrEmpty(media)) {
			return Constant.BUCKET_URL + media;
		}

		return null;
	}

	public void setMedia(String media) {
		this.media = media;
	}

	public String getBannerImage() {
		if (!StringUtility.isNullOrEmpty(bannerImage)) {
			return Constant.BUCKET_URL + bannerImage;
		}

		return null;
	}

	public void setBannerImage(String bannerImage) {
		this.bannerImage = bannerImage;
	}

	public MediaType getType() {
		return type;
	}

	public void setType(MediaType type) {
		this.type = type;
	}

	public Long getViewCount() {
		return viewCount;
	}

	public void setViewCount(Long viewCount) {
		this.viewCount = viewCount;
	}

	public Boolean getIsSaved() {
		return isSaved;
	}

	public void setIsSaved(Boolean isSaved) {
		this.isSaved = isSaved;
	}

	public boolean getExpired() {
		return expired;
	}

	public void setExpired(boolean expired) {
		this.expired = expired;
	}

	public Boolean getIsPublished() {
		return isPublished;
	}

	public void setIsPublished(Boolean isPublished) {
		this.isPublished = isPublished;
	}
	
	

}
