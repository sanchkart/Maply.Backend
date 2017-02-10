package com.maply.service;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

import com.maply.repository.UserChatRepository;
import com.maply.util.Enumeration;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import com.amazonaws.services.s3.AmazonS3;
import com.amazonaws.services.s3.transfer.TransferManager;
import com.amazonaws.services.s3.transfer.Upload;
import com.maply.AppException;
import com.maply.Constant;
import com.maply.entity.Friend;
import com.maply.entity.Message;
import com.maply.entity.Notification;
import com.maply.entity.User;
import com.maply.repository.FriendRepository;
import com.maply.repository.NotificationRepository;
import com.maply.repository.UserRepository;
import com.maply.request.UserActionRequest;
import com.maply.response.FriendList;
import com.maply.util.Enumeration.MediaType;
import com.maply.util.Enumeration.MessageType;
import com.maply.util.Enumeration.UserRequest;
import com.maply.util.Enumeration.LiveRequestType;
import com.maply.util.Enumeration.UserRole;
import com.maply.util.Enumeration.UserStatus;
import com.maply.util.StringUtility;

@Service
public class UserService {

	@Autowired
	private UserRepository userRepository;

	@Autowired
	private NotificationRepository notificationRepository;

	@Autowired
	private NotificationService notificationService;

	@Autowired
	private FriendRepository friendRepository;

	@Autowired
	private UserChatRepository userChatRepository;

	@Autowired
	private JdbcTemplate jdbcTemplate;

	@Autowired
	private AmazonS3 amazonS3;

	public User loginUser(User request) {
		
		User user = userRepository.findByFacebookId(request.getFacebookId());
		

		if (user != null) {
			if (!StringUtility.isNullOrEmpty(request.getDeviceToken())) {
				if (StringUtility.isNullOrEmpty(user.getAwsArn())) {
					String token = notificationService.subscribe(request.getDeviceToken(), request.getDeviceType());
					user.setDeviceToken(request.getDeviceToken());
					user.setAwsArn(token);

				} else if (!request.getDeviceToken().equals(user.getDeviceToken())) {
					String token = notificationService.subscribe(request.getDeviceToken(), request.getDeviceType());
					notificationService.delete(user.getAwsArn());
					user.setDeviceToken(request.getDeviceToken());
					user.setAwsArn(token);

				}
				
			} else if (StringUtility.isNullOrEmpty(user.getDeviceToken())) {
				user.setDeviceType(request.getDeviceType());
			}

			if (user.getName() == null) {
				user.setName(request.getName());
			}

			if (user.getEmail() == null) {
				user.setEmail(request.getEmail());
			}

			if (user.getGender() == null) {
				user.setGender(request.getGender());
			}

			if (user.getBirthday() == null) {
				user.setBirthday(request.getBirthday());
			}

			if (user.getImage() == null) {
				user.setImage(request.getImage());
			}

			if (request.getLatitude() != null) {
				user.setLatitude(request.getLatitude());
			}

			if (request.getLongitude() != null) {
				user.setLongitude(request.getLongitude());
			}

			if (request.getLang() == null) {
				user.setLang("da");
			}
			else {
				user.setLang(request.getLang());
			} 

			userRepository.save(user);

		} else {
			user = new User();
			user.setFacebookId(request.getFacebookId());
			user.setEmail(request.getEmail());
			user.setName(request.getName());
			user.setImage(request.getImage());
			user.setIsPublished(true);
			user.setStatus(UserStatus.Live);
			user.setDeviceType(request.getDeviceType());
			if (!StringUtility.isNullOrEmpty(request.getDeviceToken())) {
				String token = notificationService.subscribe(request.getDeviceToken(), request.getDeviceType());
				user.setDeviceToken(request.getDeviceToken());
				user.setAwsArn(token);
			}

			if (request.getLatitude() != null) {
				user.setLatitude(request.getLatitude());
			}

			if (request.getLongitude() != null) {
				user.setLongitude(request.getLongitude());
			}
			user.setRole(UserRole.USER);

			if (request.getLang() == null) {
				user.setLang("da");
			}
			else {
				user.setLang(request.getLang());
			} 

			userRepository.save(user);
			Notification notification = new Notification();
			notification.setFriendRequest(true);
			notification.setLiveFriendsNearby(true);
			notification.setSound(true);
			notification.setLiveRequest(true);
			notification.setLiveUpdate(true);
			notification.setMaixmumDistance(2);
			notification.setMaplyNews(true);
			notification.setUserId(user.getId());
			notification.setMessage(true);

			this.notificationRepository.save(notification);
		}

		return user;
	}

	public Notification getNotification(Long userId) {
		return notificationRepository.findByUserId(userId);
	}

	public Notification updateNotification(Notification request) {
		Notification notification = notificationRepository.findByUserId(request.getUserId());
		{
			notification.setFriendRequest(request.getFriendRequest());
			notification.setLiveFriendsNearby(request.getLiveFriendsNearby());
			notification.setLiveRequest(request.getLiveRequest());
			notification.setLiveUpdate(request.getLiveUpdate());
			notification.setSound(request.getSound());
			notification.setMaixmumDistance(request.getMaixmumDistance());
			notification.setMaplyNews(request.getMaplyNews());
			notification.setMessages(request.getMessages());
			notificationRepository.save(notification);
		}

		return notification;
	}

	public User updateUser(User request) throws AppException {
		User user = userRepository.findOne(request.getId());
		if (user != null) {
			if (request.getUsername() != null) {
				user.setUsername(request.getUsername());
			}

			if (request.getName() != null) {
				user.setName(request.getName());
			}

			if (request.getEmail() != null) {
				user.setEmail(request.getEmail());
			}

			if (request.getStatus() != null) {
			    if(request.getStatus() == UserStatus.Away) {
                    Integer hours = request.getOfflineHours();
                    Calendar calendar = Calendar.getInstance();
                    calendar.add(Calendar.HOUR, hours);
					user.setOfflineHours(hours);
                    user.setStatusActiveTill(calendar.getTime());
                } else if (request.getStatus() == UserStatus.Live) {
                    user.setStatusActiveTill(null);
                }
				user.setStatus(request.getStatus());
			}

			userRepository.save(user);
		}

		return user;
	}

	public List<User> uploadFacebookFriends(Long userId, List<String> request) {
		User user = findOne(userId);
		List<User> userList = new ArrayList<User>();
		if (user != null) {
			for (String id : request) {
				User friend = userRepository.findByFacebookIdAndIsPublished(id, true);
				if (friend != null) {
					Friend userFriend = friendRepository.findByUserByAndUserTo(user, friend);

					if (userFriend == null) {
						userFriend = friendRepository.findByUserByAndUserTo(friend, user);
					}

					if (userFriend == null) {
						userFriend = new Friend();
						userFriend.setIsFacebookFriend(true);
						userFriend.setInternalFriend(false);
						userFriend.setUserBy(user);
						userFriend.setUserTo(friend);
						userFriend.setFriendRequest(UserRequest.None);
						userFriend.setLiveRequest(UserRequest.None);
					} else {
						userFriend.setIsFacebookFriend(true);
					}

					this.friendRepository.save(userFriend);
				}
			}

		}

		return userList;
	}

	private User findOne(Long userId) {
		return userRepository.findByIdAndIsPublished(userId, true);
	}

	public Boolean checkUserName(String username) {
		User user = userRepository.findByUsernameIgnoreCase(username);
		return user == null ? true : false;
	}

	public User uploadUserImage(MultipartFile image, Long id) {
		User userResponse = new User();
		User user = userRepository.findOne(id);
		if (user != null) {
			try {
				String filename = System.currentTimeMillis() + image.getOriginalFilename();
				this.uploadImage(image, filename, user.getImage(), Constant.BUCKET_NAME);
				user.setImage(filename);
				userRepository.save(user);
				userResponse.setImage(user.getImage());
			} catch (IOException e) {
				userResponse.setMessages("Error to Upload Image " + e.getLocalizedMessage());
			}
		} else {
			userResponse.setMessages("User Id Incorrect !");

		}

		return userResponse;
	}

	@Async
	public String uploadImage(MultipartFile image, String filename, String oldFile, String bucket) throws IOException {
		byte[] bytes = image.getBytes();
		File file = new File(filename);
		FileOutputStream fos = new FileOutputStream(file);
		fos.write(bytes);
		fos.close();

		TransferManager transferManager = new TransferManager(amazonS3);
		Upload upload = transferManager.upload(bucket, filename, file);
		if (!StringUtility.isNullOrEmpty(oldFile)) {
			amazonS3.deleteObject(bucket, oldFile);
		}

		while (!upload.isDone()) {
			try {
				Thread.sleep(3000);
			} catch (InterruptedException e) {
				break;
			}
		}

		file.delete();
		return filename;
	}

	public List<User> searchUser(Long userId, String keyword, Integer offset) {
		String sql = "";
		if (!StringUtility.isNullOrEmpty(keyword)) {
			sql =
				"select user.id, user.name, user.username, user.image from user where user.id <> ? and user.username like '%" + keyword + "%' and user.name like '%" + keyword + "%' and " +
				"user.id not in " +
				"(" +
				"	select user_to from friend where user_by = ? " +
				"									and is_internal_friend = true " +
				") and " +
				"user.id not in " +
				"(" +
				"	select user_by from friend where user_to = ? " +
				"									and is_internal_friend = true " +
				") order by user.name limit ?, ?";

		} else {

			sql = 
				"select user.id,user.name,user.username, user.image from user where user.id <> ? and " +
				"user.id in " +
				"(" +
				"	select user_to from friend where user_by = ? " +
				"									and is_facebook_friend = true " +
				"									and is_internal_friend = false " +
				"									and (friend_request <> 'Friend' and friend_request <> 'Block') " +
				") or " +
				"user.id in " +
				"(" +
				"	select user_by from friend where user_to = ? " +
				"									and is_facebook_friend = true " +
				"									and is_internal_friend = false " +
				"									and (friend_request <> 'Friend' and friend_request <> 'Block') " +
				") order by user.name limit ?, ?";
		}

		List<User> userList = jdbcTemplate.query(sql, new Object[] { userId, userId, userId, offset, Constant.LIMIT },
				new RowMapper<User>() {

					@Override
					public User mapRow(ResultSet rs, int rowNum) throws SQLException {
						User user = new User();
						user.setName(rs.getString("name"));
						user.setUsername(rs.getString("username"));
						user.setId(rs.getLong("id"));
						user.setImage(rs.getString("image"));

						return user;
					}
				});

		if (userList != null && userList.size() > 0) {
			String sqlForFriendRequest = 
				"select u.id from user u join friend f on (u.id = f.user_to and f.user_by = ? or u.id = f.user_by and f.user_to = ? ) " + 
				"WHERE f.is_internal_friend = false and f.friend_request = 'Request'";

			List<Long> usersWithFriendRequest = jdbcTemplate.query(sqlForFriendRequest, new Object[] { userId, userId },
				new RowMapper<Long>() {

					@Override
					public Long mapRow(ResultSet rs, int rowNum) throws SQLException {
						return rs.getLong("id");
					}
				});

			if (usersWithFriendRequest != null && usersWithFriendRequest.size() > 0) {
				for (User u : userList) {
					Long uid = u.getId();
					if (usersWithFriendRequest.contains(uid)) {
						u.setStatusType(UserRequest.Request);
					}
				}
			}
		}

		return userList;
	}

	// If you found the people who write this: shoot on the spot
	public User userAction(Long userId, List<UserActionRequest> request) throws AppException {
		User user = userRepository.findByIdAndIsPublished(userId, true);
		if (user == null) {
			throw new AppException("Invalid Login user");
		}

		for (UserActionRequest userRequest : request) {
			User friend = userRepository.findByIdAndIsPublished(userRequest.getUserId(), true);
			if (friend == null) {
				throw new AppException("Inavlid UserId");
			}

			Friend userFriend = friendRepository.findByUserByAndUserTo(user, friend);
			if (userFriend == null) {
				userFriend = friendRepository.findByUserByAndUserTo(friend, user);
			}

			if (userFriend == null) {
				userFriend = new Friend();
			} else if ((userRequest.getAction() == UserRequest.Request
					&& userFriend.getFriendRequest() == UserRequest.None)
					|| (userRequest.getAction() == UserRequest.LiveRequest
							&& userFriend.getLiveRequest() == UserRequest.None)) {
				userFriend.setUserBy(user);
				userFriend.setUserTo(friend);
			}
            if(userRequest.getAction() == UserRequest.Disconnect) {
                userFriend.setLiveRequest(UserRequest.None);
            }
			else if (userRequest.getAction() == UserRequest.Live || userRequest.getAction() == UserRequest.LiveRequest) {
				if (userRequest.getTime() != null) {
					Calendar calendar = Calendar.getInstance();
					calendar.setTime(new Date());
					calendar.add(Calendar.HOUR, userRequest.getTime());
                    if(userRequest.getTime() == 0) {
                        calendar.add(Calendar.YEAR, 10);
                    }
					userFriend.setExpiryDate(calendar.getTime());
                    if(userRequest.getTime() == 24) {
                        userFriend.setLiveRequestType(Enumeration.LiveRequestType.H24);
                    } else if (userRequest.getTime() == 72) {
                        userFriend.setLiveRequestType(Enumeration.LiveRequestType.H72);
                    } else {
                        userFriend.setLiveRequestType(Enumeration.LiveRequestType.UntilDisconnect);
                    }
				}

				userFriend.setLiveRequest(userRequest.getAction());
			} else {
				if (userRequest.getAction() == UserRequest.Request && userFriend.getFriendRequest() != null
						&& userFriend.getFriendRequest() == UserRequest.Request && userFriend.getUserBy() != null
						&& userFriend.getUserBy().getId() != user.getId()) {
					userFriend.setFriendRequest(UserRequest.Friend);
                    userFriend.setInternalFriend(true);
				} else if (userRequest.getAction() == UserRequest.Decline) {
					userFriend.setLiveRequest(userRequest.getAction());
				} else {
					userFriend.setInternalFriend(false);
                    userFriend.setLiveRequest(UserRequest.None);
					userFriend.setFriendRequest(userRequest.getAction());
				}
			}

			if (userFriend.getUserBy() == null) {
				userFriend.setUserBy(user);
				userFriend.setUserTo(friend);
			}

			friendRepository.save(userFriend);
			String name = "";
			if(user.getName() != null && user.getName().contains(" ")){
				name = user.getName().substring(0, user.getName().indexOf(" "));
			}
            if (userRequest.getAction() == UserRequest.Request) {
            	String txtMsg = " har tilføjet dig som ven!";
            	
            	if (user.getLang().equals("en"))
            	txtMsg = " added you as a friend";
            	
                notificationService.sendIosNotification(friend.getAwsArn(), name + txtMsg,
						Enumeration.NotificationType.UMC, "user_id", user.getId(),
						userChatRepository.countByUserAndIsUnread(friend, true), user.getId());
            } else if (userRequest.getAction() == UserRequest.LiveRequest) {
				
            	String txtMsg = " har sendt dig en LIVE anmodning";
            	
            	if (user.getLang().equals("en"))
            		txtMsg = " sent you a LIVE request";
            	
            	notificationService.sendIosNotification(friend.getAwsArn(), name + txtMsg,
						Enumeration.NotificationType.UMC, "user_id", user.getId(),
						userChatRepository.countByUserAndIsUnread(friend, true), user.getId());
			}
		}

		User response = new User();
		response.setMessages("Successfully Updated");
		return response;
	}

	public List<User> getBlockUser(Long userId, Integer offset) {
		String sql = "select u.id, u.name, u.username, u.image, f.friend_request from friend f inner join user u on (u.id = f.user_to and f.user_by = ? and f.friend_request = 'Block') limit ?, ?";

		List<User> userList = jdbcTemplate.query(sql, new Object[] { userId, offset, Constant.LIMIT },
				new RowMapper<User>() {

					@Override
					public User mapRow(ResultSet rs, int rowNum) throws SQLException {
						User user = new User();
						user.setName(rs.getString("name"));
						user.setUsername(rs.getString("username"));
						user.setId(rs.getLong("id"));
						user.setImage(rs.getString("image"));
						user.setStatusType(UserRequest.Block);
						return user;
					}
				});

		return userList;
	}

	public List<User> getFriendList(Long userId, Integer offset, String keyword) {
		String sql;
		List<Long> updateList = new ArrayList<Long>();
		List<User> userList = new ArrayList<User>();

		if (!StringUtility.isNullOrEmpty(keyword) && keyword.equals("TYnbR2g35qH567fK34")) {
			sql = 
				"select u.id, u.name, u.username, u.image, u.status, u.latitude, u.longitude, u.full_address,u.battery_status, f.id as fid, f.live_request, f.live_request_type, f.friend_request ,f.expiry_date " + 
				"from friend f inner join user u on ((u.id = f.user_to and f.user_by = ? or u.id = f.user_by and f.user_to = ? ) and f.is_internal_friend = true) " +
				"WHERE f.live_request = 'Live'";

			jdbcTemplate.query(sql, new Object[] { userId, userId }, new RowMapper<User>() {

				@Override
				public User mapRow(ResultSet rs, int rowNum) throws SQLException {
					User user = new User();
					user.setName(rs.getString("name"));
					user.setUsername(rs.getString("username"));
					user.setId(rs.getLong("id"));
					user.setImage(rs.getString("image"));
					if (rs.getString("live_request") == null) {
						user.setStatusType(UserRequest.None);
					} else {
						user.setStatusType(UserRequest.valueOf(rs.getString("live_request")));
						String liveRequestType = rs.getString("live_request_type");
						if(liveRequestType != null) {
							user.setLiveType(LiveRequestType.valueOf(liveRequestType));
						}
					}

					if (rs.getString("expiry_date") != null) {
						Timestamp timeStamp = rs.getTimestamp("expiry_date");
						user.setTime(timeStamp);
						if (user.getTime().before(new Date())) {
							updateList.add(rs.getLong("fid"));
							user.setStatusType(UserRequest.None);
						}
					}
					user.setLatitude(rs.getDouble("latitude"));
					user.setLongitude(rs.getDouble("longitude"));
					user.setFullAddress(rs.getString("full_address"));
					user.setBatteryStatus(rs.getFloat("battery_status"));
					user.setStatus(UserStatus.valueOf(rs.getString("status")));
					userList.add(user);
					return user;
				}
			});

			for (Long id : updateList) {
				Friend friend = friendRepository.findOne(id);
				friend.setLiveRequest(UserRequest.None);
				friendRepository.save(friend);
			}
		} else {
			sql = "select u.id, u.name, u.username, u.image, u.status, u.latitude, u.longitude, u.full_address,u.battery_status, f.id as fid, f.live_request, f.live_request_type, f.friend_request ,f.expiry_date from friend f inner join user u on ((u.id = f.user_to and f.user_by = ? or u.id = f.user_by and f.user_to = ? ) and f.is_internal_friend = true ) ";
			if (!StringUtility.isNullOrEmpty(keyword)) {
				sql += " WHERE ( username like '" + keyword + "%' or name like '" + keyword + "%')";
			}

			sql += " limit ?, ?";

			jdbcTemplate.query(sql, new Object[] { userId, userId, offset, 100000 }, new RowMapper<User>() {

				@Override
				public User mapRow(ResultSet rs, int rowNum) throws SQLException {
					User user = new User();
					user.setName(rs.getString("name"));
					user.setUsername(rs.getString("username"));
					user.setId(rs.getLong("id"));
					user.setImage(rs.getString("image"));
					if (rs.getString("live_request") == null) {
						user.setStatusType(UserRequest.None);
					} else {
						user.setStatusType(UserRequest.valueOf(rs.getString("live_request")));
						String liveRequestType = rs.getString("live_request_type");
						if(liveRequestType != null) {
							user.setLiveType(LiveRequestType.valueOf(liveRequestType));
						}
					}

					if (rs.getString("expiry_date") != null) {
						Timestamp timeStamp = rs.getTimestamp("expiry_date");
						user.setTime(timeStamp);
						if (user.getTime().before(new Date())) {
							updateList.add(rs.getLong("fid"));
							user.setStatusType(UserRequest.None);
						}
					}
					user.setLatitude(rs.getDouble("latitude"));
					user.setLongitude(rs.getDouble("longitude"));
					user.setFullAddress(rs.getString("full_address"));
					user.setBatteryStatus(rs.getFloat("battery_status"));
					user.setStatus(UserStatus.valueOf(rs.getString("status")));
					userList.add(user);
					return user;
				}
			});

			for (Long id : updateList) {
				Friend friend = friendRepository.findOne(id);
				friend.setLiveRequest(UserRequest.None);
				friendRepository.save(friend);
			}
		}

		return userList;
	}

	public FriendList getFriendRequest(Long userId) {
		FriendList friendRequest = new FriendList();
		String sql = "select u.id, u.name, u.username, u.image, u.status, f.live_request, f.live_request_type, f.friend_request from friend f inner join user u on (u.id = f.user_by and f.user_to = ? and (f.friend_request = 'Request' or f.live_request = 'LiveRequest'))";

		List<User> liveReqeust = new ArrayList<User>();
		List<User> friendReqeuset = new ArrayList<User>();
		jdbcTemplate.query(sql, new Object[] { userId }, new RowMapper<User>() {

			@Override
			public User mapRow(ResultSet rs, int rowNum) throws SQLException {
				User user = new User();
				user.setName(rs.getString("name"));
				user.setUsername(rs.getString("username"));
				user.setId(rs.getLong("id"));
				user.setImage(rs.getString("image"));
				if (rs.getString("live_request") != null
						&& rs.getString("live_request").equals(UserRequest.LiveRequest.toString())) {
					user.setStatusType(UserRequest.valueOf(rs.getString("live_request")));
					user.setLiveType(LiveRequestType.valueOf(rs.getString("live_request_type")));
					liveReqeust.add(user);
				} else if (rs.getString("friend_request") != null) {
					user.setStatusType(UserRequest.valueOf(rs.getString("friend_request")));
					friendReqeuset.add(user);
				}

				return user;
			}
		});

		friendRequest.setFriendReqeust(friendReqeuset);
		friendRequest.setLiveRequest(liveReqeust);
		return friendRequest;
	}

	public FriendList getTopFriendList(Long userId) {
		FriendList friendRequest = new FriendList();
		String sql = "select u.id, u.name, u.username, u.image, u.status, f.live_request from friend f inner join user u on ((u.id = f.user_to and f.user_by = ? or u.id = f.user_by and f.user_to = ? ) and f.is_internal_friend = true)";

		List<User> userList = jdbcTemplate.query(sql, new Object[] { userId, userId }, new RowMapper<User>() {

			@Override
			public User mapRow(ResultSet rs, int rowNum) throws SQLException {
				User user = new User();
				user.setName(rs.getString("name"));
				user.setUsername(rs.getString("username"));
				user.setId(rs.getLong("id"));
				user.setImage(rs.getString("image"));
				return user;
			}
		});

		List<User> topFriends = new ArrayList<User>();
		List<User> friends = new ArrayList<User>();
		int i = 0;
		for (User user : userList) {
			if (i++ > 3) {
				topFriends.add(user);
			} else {
				friends.add(user);
			}
		}

		friendRequest.setTopFriends(topFriends);
		friendRequest.setFriends(friends);
		return friendRequest;
	}

	public User findById(Long userId) {
		return userRepository.findByIdAndIsPublished(userId, true);
	}

	public List<User> geNearby(Long userId, Double latitude, Double longitude) {
		String sql = "select u.id, u.name, u.full_address, u.username, u.image, u.location, u.latitude, u.longitude, ROUND(( 3959 * ACOS( COS( RADIANS( ? ) ) * COS( RADIANS( u.latitude ) ) * COS(RADIANS(u.longitude) - RADIANS( ? )) + SIN(RADIANS( ?)) * SIN( RADIANS(u.latitude)))),2) as distance  from user  u left join friend f on (((f.user_to = u.id and f.user_by = ?) or (f.user_by = u.id and f.user_to = ?)) and status = 'Live') where  f.live_request = 'Live' HAVING distance <  ? ";
		List<User> userList = jdbcTemplate.query(sql,
				new Object[] { latitude, longitude, latitude, userId, userId, Constant.NEAR_BY },
				new RowMapper<User>() {
					@Override
					public User mapRow(ResultSet rs, int rowNum) throws SQLException {
						User user = new User();
						user.setName(rs.getString("name"));
						user.setUsername(rs.getString("username"));
						user.setId(rs.getLong("id"));
						user.setType("USER");
						user.setImage(rs.getString("image"));
						user.setFullAddress(rs.getString("full_address"));
						user.setLocation(rs.getString("location"));
						user.setLatitude(rs.getDouble("latitude"));
						user.setLongitude(rs.getDouble("longitude"));
						return user;
					}
				});

		return userList;
	}

	public List<Message> getUserChat(Long id, Long userId, Integer offset) {
		List<Message> list = jdbcTemplate.query(
				"select uc.id, m.image, m.media,  m.text from user_chat uc inner join message m on (m.id = uc.message_id and ((uc.user_id = ? and uc.user_by = ?) or (uc.user_by = ? and uc.user_id = ?))) AND uc.is_unread = ture order by m.created_date desc limit ? , ? ",
				new Object[] { id, userId, id, userId, offset, Constant.LIMIT }, new RowMapper<Message>() {

					@Override
					public Message mapRow(ResultSet rs, int rowNum) throws SQLException {
						Message message = new Message();
						message.setId(rs.getLong("id"));
						if (rs.getString("media") != null) {
							message.setMedia(rs.getString("media"));
							if (message.getMedia().endsWith("gif")) {
								message.setType(MediaType.GIF);
							} else {
								message.setType(MediaType.VIDEO);
							}
						} else {
							message.setType(MediaType.IMAGE);
						}

						message.setImage(rs.getString("image"));
						message.setText(rs.getString("text"));
						return message;
					}
				});


		jdbcTemplate.update("update inbox set is_unread = false, modified_date = ? where user_id = ? and user_by = ? ",
                new Object[] { new Date(), id, userId });
		jdbcTemplate.update("update user_chat set is_unread = false, modified_date = ? where user_id = ? and user_by = ? ",
				new Object[] { new Date(), id, userId });
		return list;
	}

	public User updateUserPushToken(User request) {
		User user = userRepository.findByIdAndIsPublished(request.getId(), true);
		if (user != null) {
			if (!StringUtility.isNullOrEmpty(request.getDeviceToken())) {
				if (user.getDeviceToken() == null) {
					String token = notificationService.subscribe(request.getDeviceToken(), request.getDeviceType());
					user.setDeviceType(request.getDeviceType());
					user.setDeviceToken(request.getDeviceToken());
					user.setAwsArn(token);

				} else if (!request.getDeviceToken().equals(user.getDeviceToken())) {
					String token = notificationService.subscribe(request.getDeviceToken(), request.getDeviceType());
					notificationService.delete(user.getAwsArn());
					user.setDeviceType(request.getDeviceType());
					user.setDeviceToken(request.getDeviceToken());
					user.setAwsArn(token);

				}
			}
		}

		User response = new User();
		response.setMessages("Successfully Updated");
		return response;
	}

	public User updateLocation(User request) {
		User user = userRepository.findByIdAndIsPublished(request.getId(), true);
		if (user != null) {
			user.setFullAddress(request.getFullAddress());
			user.setLocation(request.getLocation());
			user.setLatitude(request.getLatitude());
			user.setLongitude(request.getLongitude());
			userRepository.save(user);
		}

		User response = new User();
		response.setMessages("Successfully Updated");
		return response;
	}

	public User updateBatteryStatus(User request) {
		User user = userRepository.findByIdAndIsPublished(request.getId(), true);

		if (user != null) {
			user.setBatteryStatus(request.getBatteryStatus());
			userRepository.save(user);
		}

		User response = new User();
		response.setMessages("Successfully Updated");
		return response;
	}

	public List<Message> getUserChat(Long id, MessageType messageType, Long userId, Integer offset) {
		String sql = "";
		if (messageType == MessageType.sent) {
			sql = "select uc.is_saved, uc.saved_till, m.latitude, m.longitude, m.location_address, m.location_name,  m.id, m.image, m.media, m.text from user_chat uc inner join message m on (m.id = uc.message_id and uc.user_by = ? and uc.user_id = ?) order by m.created_date desc limit ? , ? ";
		} else {
			sql = "select uc.is_saved, uc.saved_till,m.latitude, m.longitude, m.location_address, m.location_name, uc.id, m.image, m.media, m.text from user_chat uc inner join message m on (m.id = uc.message_id and uc.user_id = ? and uc.user_by = ? and uc.is_published = true) order by m.created_date desc limit ? , ? ";
		}

		List<Message> list = jdbcTemplate.query(sql, new Object[] { id, userId, offset, Constant.LIMIT },
				new RowMapper<Message>() {

					@Override
					public Message mapRow(ResultSet rs, int rowNum) throws SQLException {
						Message message = new Message();
						message.setId(rs.getLong("id"));
						message.setIsSaved(rs.getBoolean("is_saved"));
						if (rs.getString("media") != null) {
							message.setMedia(rs.getString("media"));
							if (message.getMedia().endsWith("gif")) {
								message.setType(MediaType.GIF);
							} else {
								message.setType(MediaType.VIDEO);
							}
						} else {
							message.setType(MediaType.IMAGE);
						}

						message.setLatitude(rs.getDouble("latitude"));
						message.setLongitude(rs.getDouble("longitude"));
						message.setLocationAddress(rs.getString("location_address"));
						message.setLocationName(rs.getString("location_name"));
						message.setImage(rs.getString("image"));
						message.setText(rs.getString("text"));
						return message;
					}
				});

		Calendar calender = Calendar.getInstance();
		calender.setTime(new Date());
		calender.add(Calendar.MINUTE, 1);
		jdbcTemplate.update(
				"update user_chat set is_unread = false, modified_date = ? where user_id = ? and user_by = ?",
				new Object[] { new Date(), id, userId });
		jdbcTemplate.update(
				"update user_chat set saved_till = ? where user_id = ? and user_by = ? and saved_till is null",
				new Object[] { calender.getTime(), id, userId });
		return list;
	}

	public User findByEmailIgnoreCase(String email) {
		return userRepository.findByEmailIgnoreCase(email);
	}

	public void save(User user) {
		userRepository.save(user);
	}

	public List<User> getUserList(Integer page, String name) {

		int offset = 0;
		if (page > 0) {
			offset = (page - 1) * Constant.LIMIT;
		}

		String sql = " SELECT u.id, u.username, u.name, u.email, u.location, u.status,u.battery_status  FROM user u WHERE role='USER' AND u.is_published=true ";
		if (!StringUtility.isNullOrEmpty(name)) {
			sql += " AND  ( u.name like '%" + name + "%' or  username like '" + name + "%' )";
		}

		sql += " ORDER BY u.id DESC LIMIT ?, ? ";
		List<User> userList = this.jdbcTemplate.query(sql, new Object[] { offset, Constant.LIMIT },
				new RowMapper<User>() {

					@Override
					public User mapRow(ResultSet rs, int arg1) throws SQLException {

						User user = new User();
						user.setId(rs.getLong("id"));
						user.setUsername(rs.getString("username"));
						user.setLocation(rs.getString("location"));
						user.setName(rs.getString("name"));
						user.setEmail(rs.getString("email"));
						user.setBatteryStatus(rs.getFloat("battery_status"));
						
						if (rs.getString("status") != null) {
							user.setStatus(UserStatus.valueOf(rs.getString("status")));
						}
						return user;
					}

				});

		return userList;
	}

	public Long getUserCount(String name) {

		String sql = " SELECT COUNT(*) AS count FROM user u WHERE role='USER'  AND u.is_published=true ";
		if (!StringUtility.isNullOrEmpty(name)) {
			sql += " AND  ( u.name like '%" + name + "%' or  username like '" + name + "%' )";
		}
		List<Long> count = jdbcTemplate.queryForList(sql, new Object[] {}, Long.class);
		return count != null && count.size() > 0 ? count.get(0) : 0L;
	}

	public User getUser(Long id) throws AppException {

		User user = this.findById(id);
		if (user.getRole() != UserRole.USER || user == null) {
			throw new AppException("User Not Exists");
		}
		return user;
	}

	public Long getUserChatCount(Long id, MessageType messageType, Long userId) {
		String sql = "";
		if (messageType == MessageType.sent) {
			sql = "select count(*) from user_chat uc inner join message m on (m.id = uc.message_id and uc.user_by = ? and uc.user_id = ?)  ";
		} else {
			sql = "select count(*) from user_chat uc inner join message m on (m.id = uc.message_id and uc.user_id = ? and uc.user_by = ? and uc.is_unread = true) ";
		}

		List<Long> count = jdbcTemplate.queryForList(sql, new Object[] { id, userId }, Long.class);
		return count != null && count.size() > 0 ? count.get(0) : 0L;
	}

	public Long searchUserCount(Long userId, String keyword) {
		String sql = "";
		if (!StringUtility.isNullOrEmpty(keyword)) {
			sql = "select count(*) from user u left join friend f on ((f.user_to = u.id and f.user_by = ?) or (f.user_by = u.id and f.user_to = ?)) where  friend_request <> 'Block' and friend_request <> 'Friend' and (username like '"
					+ keyword + "%' or  name like '" + keyword + "%' )";
		} else {
			sql = "select count(*) from friend f inner join user u on ((f.user_to = u.id and f.user_by = ?) or (f.user_by = u.id and f.user_to = ?)) where f.is_facebook_friend = true and friend_request <> 'Block' and friend_request <> 'Friend'";
		}

		List<Long> count = jdbcTemplate.queryForList(sql, new Object[] { userId, userId }, Long.class);
		return count != null && count.size() > 0 ? count.get(0) : 0L;
	}

	public List<User> getNearByEvent(Long userId, Double latitude, Double longitude) {
		String sql = "SELECT *, ROUND(( 3959 * ACOS( COS( RADIANS( ? ) ) * COS( RADIANS( latitude ) ) * COS(RADIANS(longitude) - RADIANS( ? )) + SIN(RADIANS( ?)) * SIN( RADIANS(latitude)))),2) as distance  From journey WHERE is_published = true AND user_id = ? HAVING distance <  ? ";
		List<User> userList = new ArrayList<User>();
		jdbcTemplate.query(sql, new Object[] { latitude, longitude, latitude, userId, Constant.NEAR_BY },
				new RowMapper<User>() {
					@Override
					public User mapRow(ResultSet rs, int rowNum) throws SQLException {
						User user = new User();
						user.setName(rs.getString("name"));
						user.setId(rs.getLong("id"));
						user.setType("JOURNEY");
						user.setLatitude(rs.getDouble("latitude"));
						user.setLongitude(rs.getDouble("longitude"));
						userList.add(user);
						return user;
					}
				});

		String eventSql = "SELECT *, ROUND(( 3959 * ACOS( COS( RADIANS( ? ) ) * COS( RADIANS( latitude ) ) * COS(RADIANS(longitude) - RADIANS( ? )) + SIN(RADIANS( ?)) * SIN( RADIANS(latitude)))),2) as distance  FROM event HAVING distance <  ? ";
		jdbcTemplate.query(eventSql, new Object[] { latitude, longitude, latitude, Constant.NEAR_BY },
				new RowMapper<User>() {

					@Override
					public User mapRow(ResultSet rs, int rowNum) throws SQLException {
						User user = new User();
						user.setName(rs.getString("name"));
						user.setId(rs.getLong("id"));
						user.setType("EVENT");
						user.setDescription(rs.getString("description"));
						user.setTitle(rs.getString("title"));
						user.setImage(rs.getString("image"));
						user.setLocation(rs.getString("location"));
						user.setLatitude(rs.getDouble("latitude"));
						user.setLongitude(rs.getDouble("longitude"));
						userList.add(user);
						return user;
					}
				});

		return userList;
	}

    public List<User> geUsersToBeOnline() {
        String sql = "select id, offline_hours, aws_arn from `user` where status = ? and status_active_till < ?";
        List<User> userList = jdbcTemplate.query(sql,
                new Object[] { UserStatus.Away.name(), new Date() },
                new RowMapper<User>() {
                    @Override
                    public User mapRow(ResultSet rs, int rowNum) throws SQLException {
                        User user = new User();
                        user.setId(rs.getLong("id"));
                        user.setAwsArn(rs.getString("aws_arn"));
                        user.setOfflineHours(rs.getInt("offline_hours"));
                        return user;
                    }
                });

        return userList;
    }
}
