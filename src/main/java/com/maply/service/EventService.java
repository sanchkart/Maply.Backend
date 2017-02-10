package com.maply.service;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;

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
import com.maply.entity.Event;
import com.maply.repository.EventRepository;
import com.maply.util.StringUtility;

@Service
public class EventService {

	@Autowired
	private EventRepository eventRepository;

	@Autowired
	private AmazonS3 amazonS3;

	@Autowired
	private JdbcTemplate jdbcTemplate;

	public Event addEvent(Event request) throws AppException {
		Event response = new Event();
		//request.setId(null);
		eventRepository.save(request);
		response.setId(request.getId());
		response.setMessages("Successfully Saved");
		return response;
	}

	public Event editEvent(Long id, Event request) throws AppException {
		Event response = new Event();
		Event event = eventRepository.findOne(id);

		if (event == null) {
			throw new AppException("Event Not Available");
		}

		if (!StringUtility.isNullOrEmpty(request.getName())) {
			event.setName(request.getName());
		}

		if (!StringUtility.isNullOrEmpty(request.getLocation())) {
			event.setLocation(request.getLocation());
		}

		if (request.getLatitude() != null) {
			event.setLatitude(request.getLatitude());
		}

		if (request.getTitle() != null) {
			event.setTitle(request.getTitle());
		}

		if (request.getDescription() != null) {
			event.setDescription(request.getDescription());
		}

		if (request.getLongitude() != null) {
			event.setLongitude(request.getLongitude());
		}
		eventRepository.save(event);
		response.setId(event.getId());
		response.setMessages("Successfully Updated");
		return response;
	}

	public Event uploadEventImage(MultipartFile image, Long id) {
		Event response = new Event();
		Event event = eventRepository.findOne(id);
		if (event != null) {
			try {
				String filename = System.currentTimeMillis() + image.getOriginalFilename();
				this.uploadImage(image, filename, event.getImage(), Constant.BUCKET_NAME);
				event.setImage(filename);
				eventRepository.save(event);
				response.setImage(event.getImage());
			} catch (IOException e) {
				response.setMessages("Error to Upload Image " + e.getLocalizedMessage());
			}
		} else {
			response.setMessages("Event Id Incorrect !");

		}

		return response;
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

	public List<Event> getEventList(Integer page, String name) {
		int offset = 0;
		if (page > 0) {
			offset = (page - 1) * Constant.LIMIT;
		}

		String sql = " SELECT e.id, e.name, e.location, e.image, e.title FROM event e ";
		if (!StringUtility.isNullOrEmpty(name)) {
			sql += " WHERE e.name like '%" + name + "%' ";
		}

		sql += " ORDER BY e.id DESC LIMIT ?, ? ";
		List<Event> userList = this.jdbcTemplate.query(sql, new Object[] { offset, Constant.LIMIT },
				new RowMapper<Event>() {

					@Override
					public Event mapRow(ResultSet rs, int arg1) throws SQLException {

						Event event = new Event();
						event.setId(rs.getLong("id"));
						event.setName(rs.getString("name"));
						event.setLocation(rs.getString("location"));
						event.setTitle(rs.getString("title"));
						event.setImage(rs.getString("image"));
						return event;
					}

				});

		return userList;

	}

	public Long getEventCount(String name) {

		String sql = " SELECT COUNT(*) AS count FROM event e ";

		if (!StringUtility.isNullOrEmpty(name)) {
			sql += " WHERE e.name like '%" + name + "%' ";
		}

		List<Long> count = jdbcTemplate.queryForList(sql, new Object[] {}, Long.class);
		return count != null && count.size() > 0 ? count.get(0) : 0L;
	}

	public Event getEventDetails(Long id) throws AppException {

		Event response = eventRepository.findOne(id);
		System.out.println("in get event Details");
		if (response == null) {
			throw new AppException("Event Not Available");
		}
		return response;
	}

	public Event deleteEvent(Long id) throws AppException {

		Event response = new Event();
		Event event = eventRepository.findOne(id);
		if (event == null) {
			throw new AppException("Event Not Available");
		}
		eventRepository.delete(event);
		response.setMessages("Successfully Deleted");
		return response;
	}
}
