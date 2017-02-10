package com.maply.resource;

import java.util.List;

import javax.validation.Valid;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import com.maply.AppException;
import com.maply.entity.Message;
import com.maply.response.ListResponse;
import com.maply.response.MessageResponse;
import com.maply.service.MessageService;

@RestController
@RequestMapping("/messages")
public class MessageResource {

	@Autowired
	private MessageService messageService;

	@RequestMapping(value = "", method = RequestMethod.POST, produces = MediaType.APPLICATION_JSON_VALUE)
	public ResponseEntity<Message> addMessage(@RequestParam(value = "image", required = false) MultipartFile image,
			@RequestParam(value = "banner_image", required = false) MultipartFile bannerImage,
			@RequestParam(value = "video", required = false) MultipartFile video,
			@RequestParam(value = "location_id", required = false) String locationId,
			@RequestParam(value = "latitude", required = false) Double latitude,
			@RequestParam(value = "longitude", required = false) Double longitude,
			@RequestParam(value = "text", required = false) String text,
			@RequestParam(value = "location_name", required = false) String locationName,
			@RequestParam(value = "address", required = false) String address,
			@RequestParam(value = "user_id") Long userId, @RequestParam(value = "is_jounery") Boolean isJounery) {
		HttpHeaders headers = new HttpHeaders();
		Message response = new Message();
		try {
			response = messageService.addMessage(image, video, bannerImage, locationId, latitude, longitude,
					locationName, address, userId, isJounery, text);
			return new ResponseEntity<Message>(response, headers, HttpStatus.OK);
		} catch (AppException e) {
			response = new Message();
			response.setMessages(e.getMessage());
			return new ResponseEntity<Message>(response, headers, e.getStatus());
		}
	}

	@RequestMapping(value = "/{id}", method = RequestMethod.POST, produces = MediaType.APPLICATION_JSON_VALUE)
	public ResponseEntity<Message> replyMessage(@RequestParam(value = "image", required = false) MultipartFile image,
			@RequestParam(value = "location_id", required = false) String locationId,
			@RequestParam(value = "latitude", required = false) Double latitude,
			@RequestParam(value = "longitude", required = false) Double longitude,
			@RequestParam(value = "location_name", required = false) String locationName,
			@RequestParam(value = "address", required = false) String address,
			@RequestParam(value = "user_id") Long userId, @RequestParam(value = "text", required = false) String text,
			@PathVariable @Valid Long id) {
		HttpHeaders headers = new HttpHeaders();
		Message response = new Message();
		try {
			response = messageService.replyMessage(id, image, locationId, latitude, longitude, locationName, address,
					userId, text);
			return new ResponseEntity<Message>(response, headers, HttpStatus.OK);
		} catch (AppException e) {
			response = new Message();
			response.setMessages(e.getMessage());
			return new ResponseEntity<Message>(response, headers, e.getStatus());
		}
	}

	@RequestMapping(value = "/send/{messageId}", method = RequestMethod.POST, produces = MediaType.APPLICATION_JSON_VALUE)
	public ResponseEntity<Message> sendMessage(@PathVariable @Valid Long messageId,
			@RequestBody @Valid List<Long> request) {
		HttpHeaders headers = new HttpHeaders();
		Message response = new Message();
		try {
			response = messageService.sendMessage(messageId, request);
			return new ResponseEntity<Message>(response, headers, HttpStatus.OK);
		} catch (AppException e) {
			response = new Message();
			response.setMessages(e.getMessage());
			return new ResponseEntity<Message>(response, headers, e.getStatus());
		}
	}

	@RequestMapping(value = "/unread/{userId}", method = RequestMethod.GET, produces = MediaType.APPLICATION_JSON_VALUE)
	public ResponseEntity<MessageResponse> getUnreadMessageCount(@PathVariable @Valid Long userId) {
		HttpHeaders headers = new HttpHeaders();
		MessageResponse response = new MessageResponse();
		try {
			response = messageService.getUnreadMessageCount(userId);
			return new ResponseEntity<MessageResponse>(response, headers, HttpStatus.OK);
		} catch (AppException e) {
			response = new MessageResponse();
			response.setMessage(e.getMessage());
			return new ResponseEntity<MessageResponse>(response, headers, e.getStatus());
		}
	}

	@RequestMapping(value = "/sent/{userId}", method = RequestMethod.GET, produces = MediaType.APPLICATION_JSON_VALUE)
	public ResponseEntity<ListResponse<Message>> getSentMessage(@PathVariable @Valid Long userId) {
		HttpHeaders headers = new HttpHeaders();
		ListResponse<Message> response = new ListResponse<Message>();
		List<Message> list = messageService.getSentMessage(userId);
		response.setList(list);
		return new ResponseEntity<ListResponse<Message>>(response, headers, HttpStatus.OK);
	}

	@RequestMapping(value = "/direct/{userId}", method = RequestMethod.GET, produces = MediaType.APPLICATION_JSON_VALUE)
	public ResponseEntity<ListResponse<Message>> getDirectMessage(@PathVariable @Valid Long userId) {
		HttpHeaders headers = new HttpHeaders();
		ListResponse<Message> response = new ListResponse<Message>();
		List<Message> list = messageService.getDirectMessage(userId);
		response.setList(list);
		return new ResponseEntity<ListResponse<Message>>(response, headers, HttpStatus.OK);
	}

	@RequestMapping(value = "/{messageId}/save/{userId}", method = RequestMethod.POST, produces = MediaType.APPLICATION_JSON_VALUE)
	public ResponseEntity<Message> saveMessage(@PathVariable @Valid Long messageId, @PathVariable @Valid Long userId) {
		HttpHeaders headers = new HttpHeaders();
		Message response = new Message();
		messageService.saveMessage(messageId, userId);
		response.setMessages("Successfully Saved");
		return new ResponseEntity<Message>(response, headers, HttpStatus.OK);
	}

	@RequestMapping(value = "/view/{userId}", method = RequestMethod.DELETE, produces = MediaType.APPLICATION_JSON_VALUE)
	public ResponseEntity<Message> readMessage(@PathVariable @Valid Long userId,
			@RequestParam(value = "ids") String ids) {
		HttpHeaders headers = new HttpHeaders();
		messageService.readMessage(ids, userId);
		Message response = new Message();
		response.setMessages("Success");
		return new ResponseEntity<Message>(response, headers, HttpStatus.OK);
	}
}
