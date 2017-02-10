package com.maply.entity;

import java.util.Date;

import javax.persistence.*;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonInclude.Include;

@JsonInclude(value = Include.NON_EMPTY)
@Entity
public class UserChat extends AbstractEntity {

	@ManyToOne(optional = true, cascade = { CascadeType.PERSIST, CascadeType.REFRESH })
	@JoinColumn(name = "user_id")
	private User user;

	@JsonIgnore
	@ManyToOne(optional = false, cascade = { CascadeType.PERSIST, CascadeType.REFRESH })
	@JoinColumn(name = "user_by")
	private User userBy;

	@ManyToOne(optional = false, cascade = { CascadeType.PERSIST, CascadeType.REFRESH })
	@JoinColumn(name = "message_id")
	private Message message;
	private Boolean isUnread;
	@JsonIgnore
	private Date savedTill;
	@JsonIgnore
	private boolean isSaved;
	@JsonIgnore
	private Boolean isPublished;

	public User getUser() {
		return user;
	}

	public void setUser(User user) {
		this.user = user;
	}

	public Message getMessage() {
		return message;
	}

	public void setMessage(Message message) {
		this.message = message;
	}

	public Boolean getIsUnread() {
		return isUnread;
	}

	public void setIsUnread(Boolean isUnread) {
		this.isUnread = isUnread;
	}

	public User getUserBy() {
		return userBy;
	}

	public void setUserBy(User userBy) {
		this.userBy = userBy;
	}

	public Boolean getIsPublished() {
		return isPublished;
	}

	public void setIsPublished(Boolean isPublished) {
		this.isPublished = isPublished;
	}

}
