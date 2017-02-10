package com.maply.resource;

import java.util.List;

import javax.validation.Valid;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import com.maply.Constant;
import com.maply.entity.Journey;
import com.maply.entity.Message;
import com.maply.response.JourneyResponse;
import com.maply.response.ListResponse;
import com.maply.service.MaplyService;

@RestController
@RequestMapping("")
public class MaplyResource {

	@Autowired
	private MaplyService maplyService;

	@RequestMapping(value = "/journey/{userId}/{offset}", method = RequestMethod.GET, produces = MediaType.APPLICATION_JSON_VALUE)
	public ResponseEntity<ListResponse<JourneyResponse>> getJourney(@PathVariable @Valid Long userId,
			@PathVariable @Valid Integer offset) {
		HttpHeaders headers = new HttpHeaders();
		ListResponse<JourneyResponse> response = new ListResponse<JourneyResponse>();
		List<JourneyResponse> journeyList = maplyService.getJourney(userId, offset);
		if (offset == 0) {
			response.setMyJourney(maplyService.getMyJourney(userId));
		}

		response.setList(journeyList);
		if (response.getList() != null && response.getList().size() == Constant.LIMIT) {
			response.setNextOffset(offset + Constant.LIMIT);
		} else {
			response.setNextOffset(-1);
		}

		return new ResponseEntity<ListResponse<JourneyResponse>>(response, headers, HttpStatus.OK);
	}

	@RequestMapping(value = "/journey/{journeyUserId}/view/{userId}/{offset}", method = RequestMethod.GET, produces = MediaType.APPLICATION_JSON_VALUE)
	public ResponseEntity<ListResponse<Message>> getJourneyMessage(@PathVariable @Valid Long journeyUserId,
			@PathVariable @Valid Long userId, @PathVariable @Valid Integer offset) {
		HttpHeaders headers = new HttpHeaders();
		ListResponse<Message> response = new ListResponse<Message>();
		List<Message> messages = maplyService.getJourneyMessage(journeyUserId, userId, offset);
		response.setList(messages);
		if (response.getList() != null && response.getList().size() == Constant.LIMIT) {
			response.setNextOffset(offset + Constant.LIMIT);
		} else {
			response.setNextOffset(-1);
		}

		return new ResponseEntity<ListResponse<Message>>(response, headers, HttpStatus.OK);
	}

//	@RequestMapping(value = "/journey", method = RequestMethod.GET, produces = MediaType.APPLICATION_JSON_VALUE)
//	public ResponseEntity<Journey> autoDeleteJourney() {
//		HttpHeaders headers = new HttpHeaders();
//		maplyService.autoDeleteJourney();
//		return new ResponseEntity<Journey>(headers, HttpStatus.OK);
//	}

//	@RequestMapping(value = "/chat", method = RequestMethod.GET, produces = MediaType.APPLICATION_JSON_VALUE)
//	public ResponseEntity<Journey> autoDeleteMessage() {
//		HttpHeaders headers = new HttpHeaders();
//		maplyService.autoDeleteMessage();
//		return new ResponseEntity<Journey>(headers, HttpStatus.OK);
//	}

	@RequestMapping(value = "/moment/{messageId}/user/{userId}", method = RequestMethod.DELETE, produces = MediaType.APPLICATION_JSON_VALUE)
	public ResponseEntity<Message> readMessage(@PathVariable @Valid Long messageId, @PathVariable @Valid Long userId) {
		HttpHeaders headers = new HttpHeaders();
		maplyService.deleteMessage(messageId, userId);
		Message response = new Message();
		response.setMessages("Success");
		return new ResponseEntity<Message>(response, headers, HttpStatus.OK);
	}
}
