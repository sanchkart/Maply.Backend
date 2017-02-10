package com.maply.request;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.maply.util.Enumeration.UserRequest;

public class UserActionRequest {

	@JsonProperty("user_id")
	private Long userId;
	private UserRequest action;
	private Integer time;

	public Long getUserId() {
		return userId;
	}

	public void setUserId(Long userId) {
		this.userId = userId;
	}

	public UserRequest getAction() {
		return action;
	}

	public void setAction(UserRequest action) {
		this.action = action;
	}

	public Integer getTime() {
		return time;
	}

	public void setTime(Integer time) {
		this.time = time;
	}

}
