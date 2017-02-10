package com.maply.service;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.stereotype.Service;

import com.maply.Constant;
import com.maply.entity.Message;
import com.maply.entity.User;
import com.maply.response.JourneyResponse;
import com.maply.util.Enumeration.MediaType;

@Service
public class MaplyService {

	@Autowired
	private JdbcTemplate jdbcTemplate;

	@Autowired
	private NamedParameterJdbcTemplate namedJdbcTemplate;

	public List<JourneyResponse> getJourney(Long userId, Integer offset) {
		Calendar calendar = Calendar.getInstance();
		calendar.add(Calendar.HOUR, -24);
		Map namedParameters = new HashMap();
		namedParameters.put("userId", userId);
		namedParameters.put("expireDate", calendar.getTime());
		namedParameters.put("offset", offset);
		namedParameters.put("limit", Constant.LIMIT);
		List<JourneyResponse> journeyResponses = namedJdbcTemplate.query(
				"SELECT j.id, j.image, j.latitude, j.longitude, j.name, j.created_date, u.image as uimage, u.name as uname, u.username, u.location, u.id as uid, (SELECT count(jj.id) id FROM maply.journey jj where jj.user_id = u.id and jj.created_date > :expireDate and jj.is_published = true) moments FROM maply.journey j inner join user u on (u.id = j.user_id) WHERE j.is_published = true and j.id " +
						"IN (SELECT max(j.id) id FROM maply.journey j inner join friend f on ((f.user_by = :userId and f.user_to = j.user_id and is_internal_friend = true) or " +
						"(f.user_by = j.user_id and f.user_to = :userId and is_internal_friend = true)) " +
						"where j.created_date > :expireDate group by j.user_id) order by j.created_date desc limit :offset, :limit ",
				namedParameters, new RowMapper<JourneyResponse>() {

					@Override
					public JourneyResponse mapRow(ResultSet rs, int rowNum) throws SQLException {
						JourneyResponse journeyResponse = new JourneyResponse();
						journeyResponse.setId(rs.getLong("id"));
						journeyResponse.setName(rs.getString("name"));
						journeyResponse.setImage(rs.getString("image"));

						journeyResponse.setMoments(rs.getInt("moments"));
						journeyResponse.setLatitude(rs.getDouble("latitude"));
						journeyResponse.setLongitude(rs.getDouble("longitude"));

						Timestamp timestamps = rs.getTimestamp("created_date");
						journeyResponse.setCreatedDate(timestamps);

						User user = new User();
						user.setId(rs.getLong("uid"));
						user.setName(rs.getString("uname"));
						user.setImage(rs.getString("uimage"));
						user.setUsername(rs.getString("username"));
						user.setLocation(rs.getString("location"));
						journeyResponse.setUser(user);
						journeyResponse.setIsNew(true);

						return journeyResponse;
					}
				});

		return journeyResponses;
	}

	public List<Message> getJourneyMessage(Long journeyUserId, Long userId, Integer offset) {
		Calendar calendar = Calendar.getInstance();
		calendar.add(Calendar.HOUR, -24);
		List<Message> messageResponses = jdbcTemplate.query(
				"SELECT m.id, m.image, m.media, m.latitude, m.longitude, m.location_name, m.location_address from message m inner join journey j on m.journey_id = j.id where j.user_id = ? and j.created_date > ? and j.is_published = true order by j.created_date desc limit ?, ?",
				new Object[] { journeyUserId, calendar.getTime(), offset, Constant.LIMIT }, new RowMapper<Message>() {

					@Override
					public Message mapRow(ResultSet rs, int rowNum) throws SQLException {
						Message message = new Message();
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

						message.setId(rs.getLong("id"));
						message.setImage(rs.getString("image"));
						message.setLatitude(rs.getDouble("latitude"));
						message.setLongitude(rs.getDouble("longitude"));
						message.setLocationAddress(rs.getString("location_address"));
						message.setLocationName(rs.getString("location_name"));
						return message;
					}
				});

		return messageResponses;
	}

	public void autoDeleteJourney() {
		Calendar calendar = Calendar.getInstance();
		calendar.add(Calendar.HOUR, 24);
		SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd hh:mm:ss");
		jdbcTemplate.update("Update journey set is_published = false where created_date < ? AND is_published = true ",
				new Object[] { formatter.format(calendar.getTime()) });
	}

	public List<Message> getMyJourney(Long userId) {
		Calendar calendar = Calendar.getInstance();
		calendar.add(Calendar.HOUR, -24);
		List<Message> list = jdbcTemplate.query(
				"SELECT m.id, m.text, m.image, m.banner_image, m.media, m.latitude, m.longitude, m.location_address, m.location_name, m.created_date, m.location_name, m.view_count from message m WHERE journey_id IN (SELECT j.id FROM journey j WHERE j.user_id = ? AND j.is_published = true and j.created_date > ?) ORDER BY m.created_date DESC",
				new Object[] { userId, calendar.getTime() }, new RowMapper<Message>() {

					@Override
					public Message mapRow(ResultSet rs, int rowNum) throws SQLException {
						Message message = new Message();
						message.setId(rs.getLong("id"));
						message.setLatitude(rs.getDouble("latitude"));
						message.setLongitude(rs.getDouble("longitude"));
						message.setLocationAddress(rs.getString("location_address"));
						message.setLocationName(rs.getString("location_name"));
						message.setText(rs.getString("text"));
						message.setBannerImage(rs.getString("banner_image"));
						Timestamp timestamps = rs.getTimestamp("created_date");
						message.setCreatedDate(timestamps);

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
						message.setViewCount(rs.getLong("view_count"));
						message.setLocationName(rs.getString("location_name"));
						return message;
					}
				});

		return list;
	}

	public void deleteMessage(Long messageId, Long userId) {
		jdbcTemplate.update(
				"UPDATE message m  INNER JOIN user_chat uc  ON (uc.message_id = m.id)  set uc.is_published = false WHERE uc.user_id = ? AND m.id = ? ",
				new Object[] { userId, messageId });
		List<Long> count = jdbcTemplate.queryForList(
				"Select count(*) from message WHERE is_published = true AND journey_id IN (SELECT journey_id FROM  message where id = ? )",
				new Object[] { messageId }, Long.class);
		if (count == null || count.size() > 0 || count.get(0) < 0) {
			System.out.println(count.get(0) + messageId);
			jdbcTemplate.update(
					"UPDATE journey set is_published = false WHERE id In (SELECT journey_id FROM  message where id = ?) ",
					new Object[] { messageId });
		}
	}

	public void autoDeleteMessage() {
		jdbcTemplate.update(
				"UPDATE message m  INNER JOIN user_chat uc  ON (uc.message_id = m.id)  set uc.is_published = false  where uc.saved_till < now() ",
				new Object[] {});

	}
}
