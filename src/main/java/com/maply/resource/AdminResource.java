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

import com.maply.AppException;
import com.maply.entity.User;
import com.maply.response.PageableResponse;
import com.maply.service.AdminService;

@RestController
@RequestMapping("/admins")
public class AdminResource {

	@Autowired
	private AdminService adminService;
	

	@RequestMapping(value = "/login", method = RequestMethod.POST, produces = MediaType.APPLICATION_JSON_VALUE)
	public ResponseEntity<User> login(@RequestBody @Valid User request) {
		HttpHeaders headers = new HttpHeaders();
		User user=new User();
		try
		{
			user = adminService.login(request);
			return new ResponseEntity<User>(user, headers, HttpStatus.OK);
		}
		catch(AppException e)
		{
			user.setMessages(e.getMessage());
			return new ResponseEntity<User>(user, headers, HttpStatus.FORBIDDEN);

		}
	}
	
	@RequestMapping(value = "", method = RequestMethod.POST, produces = MediaType.APPLICATION_JSON_VALUE)
	public ResponseEntity<User> addAdmin(@RequestBody @Valid User request) {
		HttpHeaders headers = new HttpHeaders();
		User user=new User();
		try
		{
			user = adminService.addAdmin(request);
			return new ResponseEntity<User>(user, headers, HttpStatus.OK);
		}
		catch(AppException e)
		{
			user.setMessages(e.getMessage());
			return new ResponseEntity<User>(user, headers, HttpStatus.FORBIDDEN);

		}
	}
	
	@RequestMapping(value = "/{id}", method = RequestMethod.PUT, produces = MediaType.APPLICATION_JSON_VALUE)
	public ResponseEntity<User> updateAdmin(@RequestBody @Valid User request,
			@PathVariable @Valid Long id
			) {
		HttpHeaders headers = new HttpHeaders();
		User user=new User();
		try
		{
			user = adminService.updateAdmin(id,request);
			return new ResponseEntity<User>(user, headers, HttpStatus.OK);
		}
		catch(AppException e)
		{
			user.setMessages(e.getMessage());
			return new ResponseEntity<User>(user, headers, HttpStatus.FORBIDDEN);

		}
	}
	
	@RequestMapping(value = "/{id}", method = RequestMethod.GET, produces = MediaType.APPLICATION_JSON_VALUE)
	public ResponseEntity<User> getAdmin(@PathVariable @Valid Long id) {
		
		HttpHeaders headers = new HttpHeaders();
		User user=new User();
		try
		{
			user = adminService.getAdmin(id);
			return new ResponseEntity<User>(user, headers, HttpStatus.OK);
		}
		catch(AppException e)
		{
			user.setMessages(e.getMessage());
			return new ResponseEntity<User>(user, headers, HttpStatus.FORBIDDEN);

		}
	}
	
	@RequestMapping(value = "/list/{page}", method = RequestMethod.GET, produces = MediaType.APPLICATION_JSON_VALUE)
	public ResponseEntity<PageableResponse<User>> getList(
			 @PathVariable @Valid Integer page,
			 @RequestParam(value="admin_name", required=false) String name
			)
	{

		HttpHeaders headers = new HttpHeaders();
		PageableResponse<User> response = new PageableResponse<User>();
		List<User> result = adminService.getAdminList( page,name);

		response.setList(result);

		if (page != null && page == 1) {
			response.setCount(adminService.getAdminCount(name));
		}
		return new ResponseEntity<PageableResponse<User>>(response, headers, HttpStatus.OK);

	}
	
	
	@RequestMapping(value = "/change-password", method = RequestMethod.PUT, produces = MediaType.APPLICATION_JSON_VALUE)
	public ResponseEntity<User> changePassword(@RequestBody @Valid User request) {
		
		HttpHeaders headers = new HttpHeaders();
		User user=new User();
		try
		{
			user = adminService.changePassword(request);
			return new ResponseEntity<User>(user, headers, HttpStatus.OK);
		}
		catch(AppException e)
		{
			user.setMessages(e.getMessage());
			return new ResponseEntity<User>(user, headers, HttpStatus.FORBIDDEN);

		}
	}
	
}
