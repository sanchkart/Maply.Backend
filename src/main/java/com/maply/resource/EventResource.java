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
import com.maply.entity.Event;
import com.maply.response.PageableResponse;
import com.maply.service.EventService;

@RestController
@RequestMapping("/events")
public class EventResource {
	
	@Autowired
	private EventService eventService;
	
	@RequestMapping(value = "", method = RequestMethod.POST, produces = MediaType.APPLICATION_JSON_VALUE)
	public ResponseEntity<Event> addEvent(@RequestBody @Valid Event request) {
		HttpHeaders headers = new HttpHeaders();
		Event event=new Event();
		try
		{
			event = eventService.addEvent(request);
			return new ResponseEntity<Event>(event, headers, HttpStatus.OK);
		}
		catch(AppException e)
		{
			event.setMessages(e.getMessage());
			return new ResponseEntity<Event>(event, headers, HttpStatus.FORBIDDEN);

		}
	}
	
	@RequestMapping(value = "/{id}", method = RequestMethod.PUT, produces = MediaType.APPLICATION_JSON_VALUE)
	public ResponseEntity<Event> addEvent(@PathVariable @Valid Long id,@RequestBody @Valid Event request) {
		HttpHeaders headers = new HttpHeaders();
		Event event=new Event();
		try
		{
			event = eventService.editEvent(id,request);
			return new ResponseEntity<Event>(event, headers, HttpStatus.OK);
		}
		catch(AppException e)
		{
			event.setMessages(e.getMessage());
			return new ResponseEntity<Event>(event, headers, HttpStatus.FORBIDDEN);

		}
	}
	
	@RequestMapping(value = "/image", method = RequestMethod.POST, produces = MediaType.APPLICATION_JSON_VALUE)
	public ResponseEntity<Event> uploadEventImage(@RequestParam("image") MultipartFile image,
			@RequestParam(value = "id") Long id) {
		HttpHeaders headers = new HttpHeaders();
		Event event = eventService.uploadEventImage(image, id);
		return new ResponseEntity<Event>(event, headers, HttpStatus.OK);
	}
	

	@RequestMapping(value = "/{id}", method = RequestMethod.GET, produces = MediaType.APPLICATION_JSON_VALUE)
	public ResponseEntity<Event> getEventDetails(@PathVariable @Valid Long id) {
		HttpHeaders headers = new HttpHeaders();
		Event event=new Event();
		try
		{
			event = eventService.getEventDetails(id);
			return new ResponseEntity<Event>(event, headers, HttpStatus.OK);
		}
		catch(AppException e)
		{
			event.setMessages(e.getMessage());
			return new ResponseEntity<Event>(event, headers, HttpStatus.FORBIDDEN);

		}
	}
	@RequestMapping(value = "/{id}", method = RequestMethod.DELETE, produces = MediaType.APPLICATION_JSON_VALUE)
	public ResponseEntity<Event> deleteEvent(@PathVariable @Valid Long id) {
		HttpHeaders headers = new HttpHeaders();
		Event event=new Event();
		try
		{
			event = eventService.deleteEvent(id);
			return new ResponseEntity<Event>(event, headers, HttpStatus.OK);
		}
		catch(AppException e)
		{
			event.setMessages(e.getMessage());
			return new ResponseEntity<Event>(event, headers, HttpStatus.FORBIDDEN);

		}
	}
	
	@RequestMapping(value = "/list/{page}", method = RequestMethod.GET, produces = MediaType.APPLICATION_JSON_VALUE)
	public ResponseEntity<PageableResponse<Event>> getList(
			 @PathVariable @Valid Integer page,
			 @RequestParam(value="event_name", required=false) String name
			)
	{

		HttpHeaders headers = new HttpHeaders();
		PageableResponse<Event> response = new PageableResponse<Event>();
		List<Event> result = eventService.getEventList( page,name);

		response.setList(result);

		if (page != null && page == 1) {
			response.setCount(eventService.getEventCount(name));
		}
		return new ResponseEntity<PageableResponse<Event>>(response, headers, HttpStatus.OK);

	}

}
