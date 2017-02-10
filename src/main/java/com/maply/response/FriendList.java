package com.maply.response;

import java.util.List;

import com.maply.entity.User;

public class FriendList {

	private List<User> liveRequest;
	private List<User> friendReqeust;
	private List<User> topFriends;
	private List<User> friends;

	public List<User> getLiveRequest() {
		return liveRequest;
	}

	public void setLiveRequest(List<User> liveRequest) {
		this.liveRequest = liveRequest;
	}

	public List<User> getFriendReqeust() {
		return friendReqeust;
	}

	public void setFriendReqeust(List<User> friendReqeust) {
		this.friendReqeust = friendReqeust;
	}

	public List<User> getTopFriends() {
		return topFriends;
	}

	public void setTopFriends(List<User> topFriends) {
		this.topFriends = topFriends;
	}

	public List<User> getFriends() {
		return friends;
	}

	public void setFriends(List<User> friends) {
		this.friends = friends;
	}

}
