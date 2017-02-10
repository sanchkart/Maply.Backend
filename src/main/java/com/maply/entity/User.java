package com.maply.entity;

import java.util.Date;

import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.Transient;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonInclude.Include;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.maply.Constant;
import com.maply.util.Enumeration.DeviceType;
import com.maply.util.Enumeration.UserRequest;
import com.maply.util.Enumeration.LiveRequestType;
import com.maply.util.Enumeration.UserRole;
import com.maply.util.Enumeration.UserStatus;
import com.maply.util.StringUtility;

@JsonInclude(value = Include.NON_EMPTY)
@Entity
public class User extends AbstractEntity {

	private String username;
	@JsonProperty("facebook_id")
	private String facebookId;
	private String image;
	@JsonProperty("device_token")
	private String deviceToken;

	@Enumerated(EnumType.STRING)
	@JsonProperty("device_type")
	private DeviceType deviceType;
	private String location;
	@JsonProperty("full_address")
	private String fullAddress;
	private String name;
	private String email;

	@JsonProperty("battery_status" )
	private float batteryStatus = 0;

	@JsonProperty("lang" )
	private String lang;

	
	@JsonIgnore
	private String awsArn;
	@JsonIgnore
	private Boolean isPublished;
	@Enumerated(EnumType.STRING)
	private UserStatus status;
	private Integer offlineHours;
	private Date statusActiveTill;
	private Double latitude;
	private Double longitude;
	private Integer badgeCount;
	@Transient
	private UserRequest statusType;
	@Transient
	private LiveRequestType liveType;
	@JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd'T'HH:mm:ss'Z'")
	@Transient
	private Date time;
	@Transient
	private String description;
	@Transient
	private String title;
	@Transient
	private String type;

	private String password;

	@Enumerated(EnumType.STRING)
	private UserRole role;
	private String gender;
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "MM/dd/yyyy")
	private Date birthday;

	public String getUsername() {
		return username;
	}

	public void setUsername(String username) {
		this.username = username;
	}

	public String getFacebookId() {
		return facebookId;
	}

	public void setFacebookId(String facebookId) {
		this.facebookId = facebookId;
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

	public String getDeviceToken() {
		return deviceToken;
	}

	public void setDeviceToken(String deviceToken) {
		this.deviceToken = deviceToken;
	}

	public DeviceType getDeviceType() {
		return deviceType;
	}

	public void setDeviceType(DeviceType deviceType) {
		this.deviceType = deviceType;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getEmail() {
		return email;
	}

	public void setEmail(String email) {
		this.email = email;
	}

	public float getBatteryStatus() {
		return batteryStatus;
	}

	public void setBatteryStatus(float batteryStatus) {
		this.batteryStatus = batteryStatus;
	}

	public String getAwsArn() {
		return awsArn;
	}

	public void setAwsArn(String awsArn) {
		this.awsArn = awsArn;
	}

	public Boolean getIsPublished() {
		return isPublished;
	}

	public void setIsPublished(Boolean isPublished) {
		this.isPublished = isPublished;
	}

	public UserStatus getStatus() {
		return status;
	}

	public void setStatus(UserStatus status) {
		this.status = status;
	}

	public Integer getOfflineHours() {
		return offlineHours;
	}

	public void setOfflineHours(Integer offlineHours) {
		this.offlineHours = offlineHours;
	}

	public Date getStatusActiveTill() {
		return statusActiveTill;
	}

	public void setStatusActiveTill(Date statusActiveTill) {
		this.statusActiveTill = statusActiveTill;
	}

	public UserRequest getStatusType() {
		return statusType;
	}

	public void setStatusType(UserRequest statusType) {
		this.statusType = statusType;
	}

	public LiveRequestType getLiveType() {
		return liveType;
	}

	public void setLiveType(LiveRequestType liveType) {
		this.liveType = liveType;
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

	public Integer getBadgeCount() {
		return badgeCount;
	}

	public void setBadgeCount(Integer badgeCount) {
		this.badgeCount = badgeCount;
	}

	public Date getTime() {
		return time;
	}

	public void setTime(Date time) {
		this.time = time;
	}

	@JsonIgnore
	public String getPassword() {
		return password;
	}

	@JsonProperty
	public void setPassword(String password) {
		this.password = password;
	}

	public UserRole getRole() {
		return role;
	}

	public void setRole(UserRole role) {
		this.role = role;
	}

	public String getType() {
		return type;
	}

	public void setType(String type) {
		this.type = type;
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

	public String getFullAddress() {
		return fullAddress;
	}

	public void setFullAddress(String fullAddress) {
		this.fullAddress = fullAddress;
	}

	public String getGender() {
		return gender;
	}

	public void setGender(String gender) {
		this.gender = gender;
	}

	public Date getBirthday() {
		return birthday;
	}

	public void setBirthday(Date birthday) {
		this.birthday = birthday;
	}
	
	public String getLang() {
		return lang;
	}

	public void setLang(String lang) {
		this.lang = lang;
	}

}
