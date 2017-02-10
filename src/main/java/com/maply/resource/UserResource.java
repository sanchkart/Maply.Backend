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
import com.maply.Constant;
import com.maply.entity.Message;
import com.maply.entity.Notification;
import com.maply.entity.User;
import com.maply.request.UserActionRequest;
import com.maply.response.FriendList;
import com.maply.response.ListResponse;
import com.maply.response.PageableResponse;
import com.maply.service.UserService;
import com.maply.util.Enumeration.MessageType;

@RestController
@RequestMapping("/users")
public class UserResource {

	@Autowired
	private UserService userService;

	@RequestMapping(value = "", method = RequestMethod.PUT, produces = MediaType.APPLICATION_JSON_VALUE)
	public ResponseEntity<User> updateUser(@RequestBody @Valid User request) {
		HttpHeaders headers = new HttpHeaders();
		User user = new User();
		try {
			user = userService.updateUser(request);
			return new ResponseEntity<User>(user, headers, HttpStatus.OK);
		} catch (AppException e) {
			user.setMessages(e.getMessage());
			return new ResponseEntity<User>(user, headers, HttpStatus.BAD_REQUEST);
		}
	}

	@RequestMapping(value = "/check/{username}", method = RequestMethod.GET, produces = MediaType.APPLICATION_JSON_VALUE)
	public ResponseEntity<User> checkUserName(@PathVariable String username) {
		HttpHeaders headers = new HttpHeaders();
		Boolean isAvailable = userService.checkUserName(username);
		if (isAvailable) {
			return new ResponseEntity<User>(headers, HttpStatus.OK);
		} else {
			return new ResponseEntity<User>(headers, HttpStatus.CONFLICT);
		}
	}

	@RequestMapping(value = "/login", method = RequestMethod.POST, produces = MediaType.APPLICATION_JSON_VALUE)
	public ResponseEntity<User> loginUser(@RequestBody @Valid User request) {
		HttpHeaders headers = new HttpHeaders();
		User user = userService.loginUser(request);
		return new ResponseEntity<User>(user, headers, HttpStatus.OK);
	}

	@RequestMapping(value = "/notification/{userId}", method = RequestMethod.GET, produces = MediaType.APPLICATION_JSON_VALUE)
	public ResponseEntity<Notification> getNotification(@PathVariable @Valid Long userId) {
		HttpHeaders headers = new HttpHeaders();
		Notification notification = userService.getNotification(userId);
		return new ResponseEntity<Notification>(notification, headers, HttpStatus.OK);
	}

	@RequestMapping(value = "/notification", method = RequestMethod.PUT, produces = MediaType.APPLICATION_JSON_VALUE)
	public ResponseEntity<Notification> updateNotification(@RequestBody @Valid Notification request) {
		HttpHeaders headers = new HttpHeaders();
		Notification notification = userService.updateNotification(request);
		return new ResponseEntity<Notification>(notification, headers, HttpStatus.OK);
	}

	@RequestMapping(value = "/facebook/{userId}", method = RequestMethod.POST, produces = MediaType.APPLICATION_JSON_VALUE)
	public ResponseEntity<List<User>> uploadFacebookFriends(@PathVariable Long userId,
			@RequestBody @Valid List<String> request) {
		HttpHeaders headers = new HttpHeaders();
		List<User> users = userService.uploadFacebookFriends(userId, request);
		return new ResponseEntity<List<User>>(users, headers, HttpStatus.OK);
	}

	@RequestMapping(value = "/image", method = RequestMethod.POST, produces = MediaType.APPLICATION_JSON_VALUE)
	public ResponseEntity<User> uploadUserImage(@RequestParam("image") MultipartFile image,
			@RequestParam(value = "id") Long id) {
		HttpHeaders headers = new HttpHeaders();
		User user = userService.uploadUserImage(image, id);
		return new ResponseEntity<User>(user, headers, HttpStatus.OK);
	}

	@RequestMapping(value = "/search/{userId}/{offset}", method = RequestMethod.GET, produces = MediaType.APPLICATION_JSON_VALUE)
	public ResponseEntity<ListResponse<User>> searchUser(@PathVariable @Valid Long userId,
			@PathVariable @Valid Integer offset, @RequestParam(value = "keyword", required = false) String keyword) {
		HttpHeaders headers = new HttpHeaders();
		if (offset < 0) {
			offset = 0;
		}

		ListResponse<User> response = new ListResponse<User>();
		List<User> userList = userService.searchUser(userId, keyword, offset);
		response.setList(userList);
		if (offset == 0) {
			response.setCount(userService.searchUserCount(userId, keyword));
		}

		if (response.getList() != null && response.getList().size() == Constant.LIMIT) {
			response.setNextOffset(offset + Constant.LIMIT);
		} else {
			response.setNextOffset(-1);
		}

		return new ResponseEntity<ListResponse<User>>(response, headers, HttpStatus.OK);
	}

	@RequestMapping(value = "/{userId}/action", method = RequestMethod.PUT, produces = MediaType.APPLICATION_JSON_VALUE)
	public ResponseEntity<User> userAction(@PathVariable @Valid Long userId,
			@RequestBody @Valid List<UserActionRequest> request) {
		HttpHeaders headers = new HttpHeaders();
		User response = null;
		try {
			response = userService.userAction(userId, request);
			return new ResponseEntity<User>(response, headers, HttpStatus.OK);
		} catch (AppException e) {
			response = new User();
			response.setMessages(e.getMessage());
			return new ResponseEntity<User>(response, headers, e.getStatus());
		}
	}

	@RequestMapping(value = "/{userId}/block/{offset}", method = RequestMethod.GET, produces = MediaType.APPLICATION_JSON_VALUE)
	public ResponseEntity<ListResponse<User>> getBlockUser(@PathVariable @Valid Long userId,
			@PathVariable @Valid Integer offset) {
		HttpHeaders headers = new HttpHeaders();
		if (offset < 0) {
			offset = 0;
		}

		ListResponse<User> response = new ListResponse<User>();
		List<User> userList = userService.getBlockUser(userId, offset);
		response.setList(userList);
		response.setCount(3L);
		if (response.getList() != null && response.getList().size() == Constant.LIMIT) {
			response.setNextOffset(offset + Constant.LIMIT);
		} else {
			response.setNextOffset(-1);
		}

		return new ResponseEntity<ListResponse<User>>(response, headers, HttpStatus.OK);
	}

	@RequestMapping(value = "/{userId}/friends", method = RequestMethod.GET, produces = MediaType.APPLICATION_JSON_VALUE)
	public ResponseEntity<FriendList> getTopFriendList(@PathVariable @Valid Long userId) {
		HttpHeaders headers = new HttpHeaders();
		FriendList friendList = userService.getTopFriendList(userId);
		return new ResponseEntity<FriendList>(friendList, headers, HttpStatus.OK);
	}

	@RequestMapping(value = "/{userId}/requests", method = RequestMethod.GET, produces = MediaType.APPLICATION_JSON_VALUE)
	public ResponseEntity<FriendList> getFriendRequest(@PathVariable @Valid Long userId) {
		HttpHeaders headers = new HttpHeaders();
		FriendList friendReqeust = userService.getFriendRequest(userId);
		return new ResponseEntity<FriendList>(friendReqeust, headers, HttpStatus.OK);
	}

	@RequestMapping(value = "/{userId}/friends/{offset}", method = RequestMethod.GET, produces = MediaType.APPLICATION_JSON_VALUE)
	public ResponseEntity<ListResponse<User>> getFriendList(@PathVariable @Valid Long userId,
			@PathVariable @Valid Integer offset, @RequestParam(value = "keyword", required = false) String keyword) {
		HttpHeaders headers = new HttpHeaders();
		if (offset < 0) {
			offset = 0;
		}

		ListResponse<User> response = new ListResponse<User>();
		List<User> userList = userService.getFriendList(userId, offset, keyword);
		response.setList(userList);
		//? magic number?
		response.setCount(3L);
		if (response.getList() != null && response.getList().size() == Constant.LIMIT) {
			response.setNextOffset(offset + Constant.LIMIT);
		} else {
			response.setNextOffset(-1);
		}

		return new ResponseEntity<ListResponse<User>>(response, headers, HttpStatus.OK);
	}

	@RequestMapping(value = "/nearby/{userId}", method = RequestMethod.GET, produces = MediaType.APPLICATION_JSON_VALUE)
	public ResponseEntity<ListResponse<User>> geNearby(@PathVariable @Valid Long userId,
			@RequestParam(value = "latitude") Double latitude, @RequestParam(value = "longitude") Double longitude) {
		HttpHeaders headers = new HttpHeaders();
		ListResponse<User> response = new ListResponse<User>();
		List<User> userList = userService.geNearby(userId, latitude, longitude);
		response.setList(userList);
		return new ResponseEntity<ListResponse<User>>(response, headers, HttpStatus.OK);
	}

	@RequestMapping(value = "/nearby-event/{userId}", method = RequestMethod.GET, produces = MediaType.APPLICATION_JSON_VALUE)
	public ResponseEntity<ListResponse<User>> getNearByEvent(@PathVariable @Valid Long userId,
			@RequestParam(value = "latitude") Double latitude, @RequestParam(value = "longitude") Double longitude) {
		HttpHeaders headers = new HttpHeaders();
		ListResponse<User> response = new ListResponse<User>();
		List<User> userList = userService.getNearByEvent(userId, latitude, longitude);
		response.setList(userList);
		return new ResponseEntity<ListResponse<User>>(response, headers, HttpStatus.OK);
	}

	@RequestMapping(value = "/{id}/chat/{userId}/{offset}", method = RequestMethod.GET, produces = MediaType.APPLICATION_JSON_VALUE)
	public ResponseEntity<ListResponse<Message>> getUserChat(@PathVariable @Valid Long userId,
			@PathVariable @Valid Long id, @PathVariable @Valid Integer offset) {
		HttpHeaders headers = new HttpHeaders();
		if (offset < 0) {
			offset = 0;
		}

		ListResponse<Message> response = new ListResponse<Message>();
		List<Message> messageList = userService.getUserChat(id, userId, offset);
		response.setList(messageList);
		response.setCount(3L);
		if (response.getList() != null && response.getList().size() == Constant.LIMIT) {
			response.setNextOffset(offset + Constant.LIMIT);
		} else {
			response.setNextOffset(-1);
		}

		return new ResponseEntity<ListResponse<Message>>(response, headers, HttpStatus.OK);
	}

	@RequestMapping(value = "/push-token", method = RequestMethod.PUT, produces = MediaType.APPLICATION_JSON_VALUE)
	public ResponseEntity<User> updateUserPushToken(@RequestBody @Valid User request) {
		HttpHeaders headers = new HttpHeaders();
		User user = userService.updateUserPushToken(request);
		return new ResponseEntity<User>(user, headers, HttpStatus.OK);
	}

	@RequestMapping(value = "/location", method = RequestMethod.PUT, produces = MediaType.APPLICATION_JSON_VALUE)
	public ResponseEntity<User> updateLocation(@RequestBody @Valid User request) {
		HttpHeaders headers = new HttpHeaders();
		User user = userService.updateLocation(request);
		return new ResponseEntity<User>(user, headers, HttpStatus.OK);
	}

	@RequestMapping(value = "/batteryStatus", method = RequestMethod.PUT, produces = MediaType.APPLICATION_JSON_VALUE)
	public ResponseEntity<User> updateBatteryStatus(@RequestBody @Valid User request) {
		HttpHeaders headers = new HttpHeaders();
		User user = userService.updateBatteryStatus(request);
		return new ResponseEntity<User>(user, headers, HttpStatus.OK);
	}

	@RequestMapping(value = "/{id}/{messageType}/{userId}/{offset}", method = RequestMethod.GET, produces = MediaType.APPLICATION_JSON_VALUE)
	public ResponseEntity<ListResponse<Message>> getUserChat(@PathVariable @Valid Long id,
			@PathVariable @Valid MessageType messageType, @PathVariable @Valid Long userId,
			@PathVariable @Valid Integer offset) {
		HttpHeaders headers = new HttpHeaders();
		if (offset < 0) {
			offset = 0;
		}

		ListResponse<Message> response = new ListResponse<Message>();
		List<Message> messageList = userService.getUserChat(id, messageType, userId, offset);
		response.setList(messageList);
		if (offset == 0) {
			response.setCount(userService.getUserChatCount(id, messageType, userId));
		}

		if (response.getList() != null && response.getList().size() == Constant.LIMIT) {
			response.setNextOffset(offset + Constant.LIMIT);
		} else {
			response.setNextOffset(-1);
		}

		return new ResponseEntity<ListResponse<Message>>(response, headers, HttpStatus.OK);
	}

	@RequestMapping(value = "/list/{page}", method = RequestMethod.GET, produces = MediaType.APPLICATION_JSON_VALUE)
	public ResponseEntity<PageableResponse<User>> getList(@PathVariable @Valid Integer page,
			@RequestParam(value = "name", required = false) String name) {

		HttpHeaders headers = new HttpHeaders();
		PageableResponse<User> response = new PageableResponse<User>();
		List<User> result = userService.getUserList(page, name);

		response.setList(result);

		if (page != null && page == 1) {
			response.setCount(userService.getUserCount(name));
		}
		return new ResponseEntity<PageableResponse<User>>(response, headers, HttpStatus.OK);

	}

	@RequestMapping(value = "/{id}", method = RequestMethod.GET, produces = MediaType.APPLICATION_JSON_VALUE)
	public ResponseEntity<User> getUser(@PathVariable @Valid Long id) {

		HttpHeaders headers = new HttpHeaders();
		User user = new User();
		try {
			user = userService.getUser(id);
			return new ResponseEntity<User>(user, headers, HttpStatus.OK);
		} catch (AppException e) {
			user.setMessages(e.getMessage());
			return new ResponseEntity<User>(user, headers, HttpStatus.FORBIDDEN);

		}
	}

}
