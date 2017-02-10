package com.maply.entity;

import java.util.Date;

import javax.persistence.CascadeType;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonInclude.Include;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.maply.util.Enumeration.UserRequest;
import com.maply.util.Enumeration.LiveRequestType;

@JsonInclude(value = Include.NON_EMPTY)
@Entity
public class Friend extends AbstractEntity {

	@JsonProperty("user")
	@ManyToOne(optional = false, cascade = { CascadeType.PERSIST, CascadeType.REFRESH })
	@JoinColumn(name = "user_to")
	private User userTo;

	@JsonIgnore
	@ManyToOne(optional = false, cascade = { CascadeType.PERSIST, CascadeType.REFRESH })
	@JoinColumn(name = "user_by")
	private User userBy;

	@JsonProperty("friend_request")
	@Enumerated(EnumType.STRING)
	private UserRequest friendRequest;

	@JsonProperty("live_request")
	@Enumerated(EnumType.STRING)
	private UserRequest liveRequest;

	@JsonProperty("live_request_type")
	@Enumerated(EnumType.STRING)
	private LiveRequestType liveRequestType;

	@JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd'T'HH:mm:ss'Z'")
	private Date expiryDate;

	private Boolean isFacebookFriend;

	private Boolean isInternalFriend;

	public User getUserTo() {
		return userTo;
	}

	public void setUserTo(User userTo) {
		this.userTo = userTo;
	}

	public User getUserBy() {
		return userBy;
	}

	public void setUserBy(User userBy) {
		this.userBy = userBy;
	}

	public UserRequest getFriendRequest() {
		return friendRequest;
	}

	public void setFriendRequest(UserRequest friendRequest) {
		this.friendRequest = friendRequest;
	}

	public UserRequest getLiveRequest() {
		return liveRequest;
	}

	public LiveRequestType getLiveRequestType() {
		return liveRequestType;
	}

	public void setLiveRequestType(LiveRequestType liveRequestType) {
		this.liveRequestType = liveRequestType;
	}

	public void setLiveRequest(UserRequest liveRequest) {
		this.liveRequest = liveRequest;
	}

	public Boolean getIsFacebookFriend() {
		return isFacebookFriend;
	}

	public void setIsFacebookFriend(Boolean isFacebookFriend) {
		this.isFacebookFriend = isFacebookFriend;
	}

	public Boolean getInternalFriend() {
		return isInternalFriend;
	}

	public void setInternalFriend(Boolean internalFriend) {
		isInternalFriend = internalFriend;
	}

	public Date getExpiryDate() {
		return expiryDate;
	}

	public void setExpiryDate(Date expiryDate) {
		this.expiryDate = expiryDate;
	}

}
