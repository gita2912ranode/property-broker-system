package com.property_broker.security;

//import jakarta.servlet.FilterChain;
//import jakarta.servlet.ServletException;
//import jakarta.servlet.http.HttpServletRequest;
//import jakarta.servlet.http.HttpServletResponse;
//import lombok.RequiredArgsConstructor;
//import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
//import org.springframework.security.core.context.SecurityContextHolder;
//import org.springframework.security.core.userdetails.UserDetails;
//import org.springframework.stereotype.Component;
//import org.springframework.web.filter.OncePerRequestFilter;
//
//import com.property_broker.service.impl.CustomUserDetailsService;
//
//import java.io.IOException;
// 
//@Component
//@RequiredArgsConstructor
//public class JwtAuthenticationFilter extends OncePerRequestFilter {
// 
//    private final JwtTokenUtil jwtTokenUtil;
//    private final CustomUserDetailsService userDetailsService;
// 
//    @Override
//    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain chain)
//            throws ServletException, IOException {
// 
//        String authHeader = request.getHeader("Authorization");
//        if (authHeader != null && authHeader.startsWith("Bearer ")) {
//            String jwt = authHeader.substring(7);
//            String username = jwtTokenUtil.extractUsername(jwt);
// 
//            if (username != null && SecurityContextHolder.getContext().getAuthentication() == null) {
//                UserDetails userDetails = userDetailsService.loadUserByUserName(username);
//                if (jwtTokenUtil.validateToken(jwt)) {
//                    UsernamePasswordAuthenticationToken authentication =
//                            new UsernamePasswordAuthenticationToken(
//                                    userDetails, null, userDetails.getAuthorities());
//                    SecurityContextHolder.getContext().setAuthentication(authentication);
//                }
//            }
//        }
//        chain.doFilter(request, response);
//    }
//}

 
import java.io.IOException;
import java.util.Arrays;
import java.util.stream.Collectors;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpHeaders;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

@Component
public class JwtAuthenticationFilter extends OncePerRequestFilter {
 
    private static final Logger log = LoggerFactory.getLogger(JwtAuthenticationFilter.class);
 
    private final JwtUtil jwtUtil;
    private final UserDetailsService userDetailsService;
 
    public JwtAuthenticationFilter(JwtUtil jwtUtil, UserDetailsService userDetailsService) {
        this.jwtUtil = jwtUtil;
        this.userDetailsService = userDetailsService;
    }
    @Override
    protected void doFilterInternal(HttpServletRequest request,
                                    HttpServletResponse response,
                                    FilterChain filterChain) throws ServletException, IOException {
 
        final String header = request.getHeader(HttpHeaders.AUTHORIZATION);
 
        if (header == null || !header.startsWith("Bearer ")) {
            log.debug("Missing or invalid Authorization header");
            filterChain.doFilter(request, response);
            return; 
        }
 
        final String token = header.substring(7);
        String username;
 
        try {
            username = jwtUtil.extractUsername(token);
            log.debug("Extracted username: {}", username);
        } catch (Exception ex) {
            log.warn("Invalid JWT token structure");
            sendForbidden(response, "Invalid JWT token");
            return;
        }
 
        if (username != null && SecurityContextHolder.getContext().getAuthentication() == null) {
            UserDetails ud;
            try {
                ud = userDetailsService.loadUserByUsername(username);
            } catch (Exception e) {
                log.error("User not found for username: {}", username);
                sendForbidden(response, "User not found");
                return;
            }
 
            if (!jwtUtil.validateToken(token, ud)) {
                log.warn("JWT token expired or invalid for user: {}", username);
                sendForbidden(response, "Token expired or invalid");
                return;
            }
 
            String rolesClaim = jwtUtil.extractRoles(token);
            var authorities = Arrays.stream(rolesClaim.split(","))
                    .map(role -> role.startsWith("ROLE") ? role : "ROLE" + role)
                    .map(SimpleGrantedAuthority::new)
                    .collect(Collectors.toList());
 
            UsernamePasswordAuthenticationToken auth =
                    new UsernamePasswordAuthenticationToken(ud, null, authorities);
 
            SecurityContextHolder.getContext().setAuthentication(auth);
            log.debug("SecurityContext authentication set for user: {}", username);
        }
 
        filterChain.doFilter(request, response);
    }
 
    private void sendForbidden(HttpServletResponse response, String message) throws IOException {
        response.setStatus(HttpServletResponse.SC_FORBIDDEN);
        response.setContentType("application/json");
        response.getWriter().write("{\"error\": \"" + message + "\"}");
    }
}
 
