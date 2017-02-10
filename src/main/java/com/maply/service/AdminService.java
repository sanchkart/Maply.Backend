package com.maply.service;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Service;

import com.maply.AppException;
import com.maply.Constant;
import com.maply.entity.User;
import com.maply.util.Enumeration.UserRole;
import com.maply.util.StringUtility;

@Service
public class AdminService {
	
	@Autowired
	private UserService userService;
	
	@Autowired
	private JdbcTemplate jdbcTemplate;

	public User login(User request) throws AppException {
		
		User user=userService.findByEmailIgnoreCase(request.getEmail());
		if(user==null)
		{
			throw new AppException("Invalid user");
		}
		if( !user.getPassword().equals(request.getPassword()))
		{
			throw new AppException("Bad Credentials");
		}
		
		return user;
	}

	public User addAdmin(User request)  throws AppException{
		User response=new User();
	
		User user=new User();	
		user.setName(request.getName());
		User found=userService.findByEmailIgnoreCase(request.getEmail());
		if(found!=null)
		{
			throw new AppException("Email Id Already Exists");
		}
		user.setEmail(request.getEmail());
		user.setPassword(request.getPassword());
		user.setRole(UserRole.ADMIN);
		user.setIsPublished(true);
		userService.save(user);
		response.setMessages("Successfully Added");
		response.setId(user.getId());
		return response;
	}
	
	public User updateAdmin(Long id,User request)  throws AppException{
		User response=new User();
	
		User user=userService.findById(id);
		if(user==null)
		{
			throw new AppException("User Not Exists");
		}
		if(!StringUtility.isNullOrEmpty(request.getName()))
		{
			user.setName(request.getName());
		}
		if(!StringUtility.isNullOrEmpty(request.getEmail()))
		{
			User found=userService.findByEmailIgnoreCase(request.getEmail());
			if(found!=null )
			{
				throw new AppException("Email Id Already Exists");
			}
			user.setEmail(request.getEmail());
		}
		userService.save(user);
		response.setMessages("Successfully Updated");
		response.setId(user.getId());
		return response;
	}

	public List<User> getAdminList(Integer page, String name) {
		
		int offset = 0;
		if (page > 0) {
			offset = (page - 1) * Constant.LIMIT;
		}

		
		String sql =" SELECT u.id, u.name, u.email FROM user u WHERE role='ADMIN' AND u.is_published=true ";
		if(!StringUtility.isNullOrEmpty(name))
		{
			sql+=" AND  u.name like '%"+name+"%' ";
		}

		sql+=" ORDER BY u.id DESC LIMIT ?, ? ";
		List<User> userList = this.jdbcTemplate.query(sql, new Object[] { offset, Constant.LIMIT },
				new RowMapper<User>() {

					@Override
					public User mapRow(ResultSet rs, int arg1) throws SQLException {

						User user=new  User();
						user.setId(rs.getLong("id"));
						user.setName(rs.getString("name"));
						user.setEmail(rs.getString("email"));
						return user;
					}

				});

		return userList;
	}

	public Long getAdminCount(String name)  {
		
		String sql =" SELECT COUNT(*) AS count FROM user u WHERE role='ADMIN'  AND u.is_published=true ";
		if(!StringUtility.isNullOrEmpty(name))
		{
			sql+=" AND u.name like '%"+name+"%' ";
		}
		List<Long> count = jdbcTemplate.queryForList(sql, new Object[] {}, Long.class);
		return count != null && count.size() > 0 ? count.get(0) : 0L;
	}

	public User getAdmin(Long id) throws AppException {
		
		User user=userService.findById(id);
		if( user.getRole()!=UserRole.ADMIN ||user==null )
		{
			throw new AppException("Admin Not Exists");
		}
		return user;
	}

	public User changePassword(User request) throws AppException {
		User user=userService.findById(request.getId());
		if(user==null)
		{
			throw new AppException("User Not Exists");
		}
		user.setPassword(request.getPassword());
		userService.save(user);
		user.setMessages("Password Updated Successfully");
		return user;
	}


}
