package com.maply.response;

import java.util.List;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonInclude.Include;
import com.maply.entity.Message;

@JsonInclude(value = Include.NON_EMPTY)
public class ListResponse<T> {

	private List<Message> myJourney;
	private List<T> list;
	private String message;
	private Integer nextOffset;
	private Long count;

	public List<T> getList() {
		return list;
	}

	public void setList(List<T> list) {
		this.list = list;
	}

	public String getMessage() {
		return message;
	}

	public void setMessage(String message) {
		this.message = message;
	}

	public Integer getNextOffset() {
		return nextOffset;
	}

	public void setNextOffset(Integer nextOffset) {
		this.nextOffset = nextOffset;
	}

	public Long getCount() {
		return count;
	}

	public void setCount(Long count) {
		this.count = count;
	}

	public List<Message> getMyJourney() {
		return myJourney;
	}

	public void setMyJourney(List<Message> myJourney) {
		this.myJourney = myJourney;
	}

}
