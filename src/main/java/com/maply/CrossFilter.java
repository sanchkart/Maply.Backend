package com.maply;

import java.io.IOException;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

@Component
public class CrossFilter extends OncePerRequestFilter {

	private static final Logger LOGGER = LoggerFactory.getLogger(CrossFilter.class);
	private static final String ORIGIN = "Origin";

	@Override
	protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain)
			throws ServletException, IOException {
		String origin = request.getHeader(ORIGIN);
		System.out.println(request.getRequestURI());
		if (origin != null) {
			response.setHeader("Access-Control-Allow-Origin", origin);
			response.setHeader("Access-Control-Allow-Credentials", "true");
			response.setHeader("Access-Control-Allow-Headers", request.getHeader("Access-Control-Request-Headers"));
			response.setHeader("Access-Control-Allow-Methods", "PATCH, GET, POST, PUT, DELETE, OPTIONS");
		}

		if (request.getMethod().equals("OPTIONS")) {
			// stop here if OPTIONS used
			try {
				response.setStatus(HttpServletResponse.SC_OK);
				response.setContentType("text/plain");
				response.getWriter().print("OK");
				response.getWriter().flush();
			} catch (IOException e) {
				LOGGER.error("Error sending CORS OPTIONS response.", e);
			}
		} else {
			// continue filter chain
			filterChain.doFilter(request, response);
		}
	}
}