package com.maply.service;

import java.io.IOException;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import com.maply.AppException;
import com.maply.Constant;
import com.maply.entity.Journey;
import com.maply.entity.Message;
import com.maply.entity.User;
import com.maply.entity.UserChat;
import com.maply.repository.JourneyRepository;
import com.maply.repository.MessageRepository;
import com.maply.repository.UserChatRepository;
import com.maply.response.MessageResponse;
import com.maply.util.Enumeration.NotificationType;
import com.maply.util.StringUtility;

@Service
public class MessageService {

	@Autowired
	private MessageRepository messageRepository;

	@Autowired
	private MediaService mediaService;

	@Autowired
	private UserService userService;

	@Autowired
	private NotificationService notificationService;

	@Autowired
	private UserChatRepository userChatRepository;

	@Autowired
	private JourneyRepository journeyRepository;

	@Autowired
	private JdbcTemplate jdbcTemplate;

	public Message addMessage(MultipartFile image, MultipartFile video, MultipartFile bannerImage, String locationId,
			Double latitude, Double longitude, String locationName, String address, Long userId, Boolean isJounery,
			String text) throws AppException {
		if (image == null && video == null) {
			throw new AppException("Media can't be empty");
		}

		if (isJounery && StringUtility.isNullOrEmpty(locationId)) {
			throw new AppException("Location Id can't be empty");
		}

		Message message = new Message();
		message.setIsPublished(true);
		message.setLatitude(latitude);
		message.setViewCount(0L);
		message.setLongitude(longitude);
		message.setLocationAddress(address);
		message.setText(text);
		message.setLocationId(locationId);
		message.setLocationName(locationName);
		User user = userService.findById(userId);

		if (user == null) {
			throw new AppException("Invalid UserId");
		}

		message.setUser(user);

		if (image != null) {
			String fileName = System.currentTimeMillis() + image.getOriginalFilename();
			try {
				mediaService.uploadImage(image.getBytes(), fileName, null, Constant.BUCKET_NAME);
				message.setImage(fileName);
			} catch (IOException e) {
				e.printStackTrace();
			}
		}

		String bannerImageFileName = null;
		if (bannerImage != null) {
			bannerImageFileName = System.currentTimeMillis() + bannerImage.getOriginalFilename();
			try {
				mediaService.uploadImage(bannerImage.getBytes(), bannerImageFileName, null, Constant.BUCKET_NAME);
				message.setBannerImage(bannerImageFileName);
			} catch (IOException e) {
				e.printStackTrace();
			}
		}

		if (video != null) {
			String fileName = System.currentTimeMillis() + video.getOriginalFilename();
			try {
				mediaService.uploadImage(video.getBytes(), fileName, null, Constant.BUCKET_NAME);
				message.setMedia(fileName);
			} catch (IOException e) {
				e.printStackTrace();
			}
		}

		if (isJounery) {
			Journey journey = new Journey();
			journey.setUserId(user.getId());
			journey.setLocationId(locationId);
			journey.setName(locationName);
			journey.setLatitude(latitude);
			journey.setIsPublished(true);
			journey.setLongitude(longitude);
			journey.setImage(bannerImageFileName);
			journey.setMoments(1);
			journeyRepository.save(journey);
			message.setJourneyId(journey.getId());
		}

		messageRepository.save(message);
		Message response = new Message();
		response.setId(message.getId());
		response.setMessages("Successfully Added ");
		return response;
	}

	public Message sendMessage(Long messageId, List<Long> request) throws AppException {
		Message message = this.findById(messageId);
		if (message == null) {
			throw new AppException("Invalid Message Id");
		}

		List<User> userList = new ArrayList<User>();
		for (Long id : request) {
			User user = userService.findById(id);
			if (user == null || user.getId() == message.getUser().getId()) {
				throw new AppException("Invalid User Id ");
			}

			userList.add(user);
		}

		for (User user : userList) {
			UserChat userChat = new UserChat();
			userChat.setIsUnread(true);
			userChat.setIsPublished(true);
			userChat.setMessage(message);
			userChat.setUser(user);
			userChat.setUserBy(message.getUser());
			
			User userBy = message.getUser();
			userChatRepository.save(userChat);
			if (!StringUtility.isNullOrEmpty(userBy.getAwsArn())) {
				String name = "";
				if(userBy.getName() != null && userBy.getName().contains(" ")){
					name = userBy.getName().substring(0, userBy.getName().indexOf(" "));
				}
				
				String txtMsg = " har sendt dig et Moment!";
				
				if (user.getLang().equals("en"))
				txtMsg = " sent you a Moment";
				
				notificationService.sendIosNotification(user.getAwsArn(), name + txtMsg,
						NotificationType.UMC, "user_id", message.getUser().getId(),
						userChatRepository.countByUserAndIsUnread(user, true), user.getId());
			}
		}

		Message response = new Message();
		response.setMessages("Successfully Sent");
		return response;
	}

	private Message findById(Long messageId) {
		return messageRepository.findById(messageId);
	}

	public Message replyMessage(Long id, MultipartFile image, String locationId, Double latitude, Double longitude,
			String locationName, String address, Long userId, String text) throws AppException {
		if (image == null && StringUtility.isNullOrEmpty(text)) {
			throw new AppException("Message can't be empty");
		}

		User friend = userService.findById(userId);
		User user = userService.findById(id);
		if (friend == null || user == null) {
			throw new AppException("Incorrect UserId");
		}

		if (image != null) {
			Message message = new Message();
			message.setIsPublished(true);
			message.setViewCount(0L);
			message.setLatitude(latitude);
			message.setLongitude(longitude);
			message.setLocationAddress(address);
			message.setLocationId(locationId);
			message.setLocationName(locationName);
			message.setText(text);
			message.setUser(user);

			if (image != null) {
				String fileName = System.currentTimeMillis() + image.getOriginalFilename();
				try {
					mediaService.uploadImage(image.getBytes(), fileName, null, Constant.BUCKET_NAME);
					message.setImage(fileName);
				} catch (IOException e) {
					e.printStackTrace();
				}
			}

			messageRepository.save(message);

			UserChat userChat = new UserChat();
			userChat.setMessage(message);
			userChat.setUser(friend);
			userChat.setIsPublished(true);
			userChat.setUserBy(user);
			userChat.setIsUnread(true);
			userChatRepository.save(userChat);
		}

		if (!StringUtility.isNullOrEmpty(friend.getAwsArn())) {
			String name = "";
			if(user.getName() != null && user.getName().contains(" ")){
				name = user.getName().substring(0, user.getName().indexOf(" "));
			}
			notificationService.sendIosNotification(friend.getAwsArn(), name + " " + text,
					NotificationType.UMC, "user_id", user.getId(),
					userChatRepository.countByUserAndIsUnread(friend, true), friend.getId());
		}

		Message response = new Message();
		response.setMessages("Successfully Sent ");
		return response;
	}

	public MessageResponse getUnreadMessageCount(Long userId) throws AppException {
		MessageResponse response = new MessageResponse();
		User user = userService.findById(userId);
		if (user == null) {
			throw new AppException("Incorrect UserId");
		}

		response.setCount(userChatRepository.countByUserAndIsUnread(user, true));
		return response;
	}

	public List<Message> getSentMessage(Long userId) {
		Calendar calendar = Calendar.getInstance();
		List<Message> list = jdbcTemplate.query(
				"select distinct u.id as uid, u.username, u.name, uc.is_saved, uc.saved_till, u.image as uimage, m.id as mid, m.text, m.created_date, u.location, m.image as mimage, uc.modified_date from message m inner join user_chat uc On uc.message_id = m.id inner join user u on u.id = uc.user_id where m.id in (SELECT a.message_id From (SELECT message_id, user_id FROM user_chat where user_by = ? and is_published = true order by id desc) as a group by user_id) order by m.id desc",
				new Object[] { userId }, new RowMapper<Message>() {

					@Override
					public Message mapRow(ResultSet rs, int rowNum) throws SQLException {
						Message message = new Message();
						message.setId(rs.getLong("mid"));
						message.setImage(rs.getString("mimage"));
                        Timestamp modifyDate = rs.getTimestamp("modified_date");
						if (modifyDate != null) {
							message.setModifiedDate(modifyDate);
						}
                        Timestamp creationDate = rs.getTimestamp("created_date");
						calendar.setTime(creationDate);
						calendar.add(Calendar.HOUR, 24);
						if(calendar.getTime().before(new Date())) {
							message.setExpired(true);
						} else {
							message.setExpired(false);
						}
						message.setCreatedDate(creationDate);
						User user = new User();
						user.setLocation(rs.getString("location"));
						user.setId(rs.getLong("uid"));
						user.setUsername(rs.getString("username"));
						user.setImage(rs.getString("uimage"));
						user.setName(rs.getString("name"));
						message.setIsSaved(rs.getBoolean("is_saved"));
						message.setUser(user);
						return message;
					}
				});

		return list;

	}

	public List<Message> getDirectMessage(Long userId) {
		// do not touch this ultralogic sql without good understanding :)
		List<Message> list = jdbcTemplate.query(
				"select distinct u.id as uid, u.username, u.name, uc.is_saved, uc.saved_till, u.image as uimage, m.id as mid, m.text, m.created_date, u.location, m.image as mimage, m.modified_date from message m inner join user_chat uc On (uc.message_id = m.id) inner join user u on (u.id = uc.user_by) where m.id in " +
						" (select v.id from (select m.id, uc.user_by, (CASE" +
						" WHEN uc.is_unread = 1 THEN concat('4-', m.id)" +
						" WHEN uc.is_unread = 0 and uc.is_saved = 0 and uc.saved_till > ? THEN concat('3-', UNIX_TIMESTAMP(uc.saved_till))" +
						" WHEN uc.is_unread = 0 and is_saved = 1 THEN concat('2-', UNIX_TIMESTAMP(uc.saved_till))" +
						" ELSE concat('1-', m.id)" +
						" END) val from message m inner join user_chat uc on (uc.user_id = ? and uc.message_id = m.id)" +
						" order by SUBSTRING(val FROM 1 FOR 1) desc, CAST(SUBSTR(val FROM 3) AS UNSIGNED) desc)  as v group by v.user_by) order by m.id desc",
				new Object[] { new Date(), userId }, new RowMapper<Message>() {

					@Override
					public Message mapRow(ResultSet rs, int rowNum) throws SQLException {
						Message message = new Message();
						message.setId(rs.getLong("mid"));
						message.setImage(rs.getString("mimage"));
						message.setCount(getUnreadCount(userId, rs.getLong("uid")));
						Timestamp timestamp = rs.getTimestamp("created_date");
						message.setCreatedDate(timestamp);
						User user = new User();
						user.setLocation(rs.getString("location"));
						user.setId(rs.getLong("uid"));
						user.setUsername(rs.getString("username"));
						user.setImage(rs.getString("uimage"));
						user.setName(rs.getString("name"));
						message.setUser(user);
						message.setIsSaved(false);
						message.setExpired(true);
						boolean isSaved = rs.getBoolean("is_saved");
						Date savedTill = rs.getTimestamp("saved_till");
						if(savedTill != null) {
							if(savedTill.after(new Date())) {
								if (isSaved) {
									message.setIsSaved(true);
								} else {
									message.setExpired(false);
								}
							}
						}
						return message;
					}

					private Long getUnreadCount(Long id, Long userId) {
						String sql = "select count(*) from user_chat Where user_id = ? and user_by = ?  and is_unread = true and is_published = true";
						List<Long> count = jdbcTemplate.queryForList(sql, new Object[] { id, userId }, Long.class);
						return count != null && count.size() > 0 ? count.get(0) : 0L;
					}
				});

		return list;
	}

	public void readMessage(String ids, Long userId) {
		if (ids != null) {
			for (String id : ids.split(",")) {
				if (!StringUtility.isNullOrEmpty(id)) {
					jdbcTemplate.update(
							"UPDATE user_chat set is_unread = false, is_published = false WHERE user_id = ? AND is_published = true AND id = ?",
							new Object[] { userId, id.trim() });

					if (userId != null) {
						jdbcTemplate.update(
								"UPDATE message m  INNER JOIN  user_chat uc ON(uc.message_id = m.id) set m.view_count = m.view_count + 1 WHERE uc.id = ?",
								new Object[] { id.trim() });
					}
				}
			}
		}
	}

	public void saveMessage(Long messageId, Long userId) {
		Calendar calender = Calendar.getInstance();
		calender.setTime(new Date());
		calender.add(Calendar.DATE, 1);
		jdbcTemplate.update("UPDATE user_chat set saved_till = ?, is_saved = TRUE WHERE user_id = ? AND id = ?",
				new Object[] { calender.getTime(), userId, messageId });

	}
}
