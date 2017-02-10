package com.maply.entity;

import javax.persistence.Entity;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonInclude.Include;
import com.fasterxml.jackson.annotation.JsonProperty;

@JsonInclude(value = Include.NON_EMPTY)
@Entity
public class Notification extends AbstractEntity {

	@JsonProperty("maply_news")
	private Boolean maplyNews;
	private Boolean message;
	@JsonProperty("friend_request")
	private Boolean friendRequest;
	@JsonProperty("live_request")
	private Boolean liveRequest;
	@JsonProperty("live_update")
	private Boolean liveUpdate;
	@JsonProperty("llive_friends_nearby")
	private Boolean liveFriendsNearby;
	@JsonProperty("maximum_distance")
	private Integer maixmumDistance;
	@JsonProperty("user_id")
	private Long userId;
	private Boolean sound;

	public Boolean getMaplyNews() {
		return maplyNews;
	}

	public void setMaplyNews(Boolean maplyNews) {
		this.maplyNews = maplyNews;
	}

	public Boolean getMessage() {
		return message;
	}

	public void setMessage(Boolean message) {
		this.message = message;
	}

	public Boolean getFriendRequest() {
		return friendRequest;
	}

	public void setFriendRequest(Boolean friendRequest) {
		this.friendRequest = friendRequest;
	}

	public Boolean getLiveRequest() {
		return liveRequest;
	}

	public void setLiveRequest(Boolean liveRequest) {
		this.liveRequest = liveRequest;
	}

	public Boolean getLiveUpdate() {
		return liveUpdate;
	}

	public void setLiveUpdate(Boolean liveUpdate) {
		this.liveUpdate = liveUpdate;
	}

	public Boolean getLiveFriendsNearby() {
		return liveFriendsNearby;
	}

	public void setLiveFriendsNearby(Boolean liveFriendsNearby) {
		this.liveFriendsNearby = liveFriendsNearby;
	}

	public Integer getMaixmumDistance() {
		return maixmumDistance;
	}

	public void setMaixmumDistance(Integer maixmumDistance) {
		this.maixmumDistance = maixmumDistance;
	}

	public Long getUserId() {
		return userId;
	}

	public void setUserId(Long userId) {
		this.userId = userId;
	}

	public Boolean getSound() {
		return sound;
	}

	public void setSound(Boolean sound) {
		this.sound = sound;
	}

}
