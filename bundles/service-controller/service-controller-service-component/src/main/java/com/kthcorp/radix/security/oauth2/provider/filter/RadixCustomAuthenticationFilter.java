package com.kthcorp.radix.security.oauth2.provider.filter;

import java.io.IOException;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.filter.OncePerRequestFilter;

@Deprecated
public class RadixCustomAuthenticationFilter extends OncePerRequestFilter {
	
	@Override
	protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain) throws ServletException, IOException {
		
		final Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
		
		request.setAttribute("businessPlatformKey", authentication.getName());
		request.setAttribute("clientKey", authentication.getName());
		
		filterChain.doFilter(request, response);
		
	}
}