package com.property_broker.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

import com.property_broker.security.JwtAccessDeniedHandler;
import com.property_broker.security.JwtAuthenticationEntryPoint;
import com.property_broker.security.JwtAuthenticationFilter;

@Configuration
@EnableMethodSecurity
public class SecurityConfig {

	private final JwtAuthenticationFilter jwtFilter;
	private final JwtAuthenticationEntryPoint entryPoint;
	private final JwtAccessDeniedHandler jwtAccessDeniedHandler;

	public SecurityConfig(JwtAuthenticationFilter jwtFilter, JwtAuthenticationEntryPoint entryPoint,
			JwtAccessDeniedHandler jwtAccessDeniedHandler) {
		this.jwtFilter = jwtFilter;
		this.entryPoint = entryPoint;
		this.jwtAccessDeniedHandler = jwtAccessDeniedHandler;
	}

	@Bean
	public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
		http.cors().and().csrf(csrf -> csrf.disable())
				.exceptionHandling(
						e -> e.authenticationEntryPoint(entryPoint).accessDeniedHandler(jwtAccessDeniedHandler))
				.authorizeHttpRequests(auth -> auth

						.requestMatchers(HttpMethod.POST, "/api/auth/**").permitAll()
						.requestMatchers("/v3/api-docs/**", "/swagger-ui/**", "/swagger-ui.html").permitAll()
						.requestMatchers("/api/roles/**").permitAll()
						.requestMatchers("/api/users/**", "/api/properties/**", "/api/bookings/**", "/api/roles/**")
						.hasRole("ADMIN")

						// User management - ADMIN only
						.requestMatchers(HttpMethod.GET, "/api/users").hasRole("ADMIN")
						.requestMatchers(HttpMethod.GET, "/api/users/*").hasRole("ADMIN")
						.requestMatchers(HttpMethod.PUT, "/api/users/*").hasRole("ADMIN")
						.requestMatchers(HttpMethod.DELETE, "/api/users/*").hasRole("ADMIN")
						.requestMatchers(HttpMethod.POST, "/api/users/*/roles").hasRole("ADMIN")
						.requestMatchers(HttpMethod.DELETE, "/api/users/*/roles").hasRole("ADMIN")

						// Property management
						.requestMatchers(HttpMethod.POST, "/api/properties").hasAnyRole("OWNER", "BROKER")
						.requestMatchers(HttpMethod.PUT, "/api/properties/*").hasAnyRole("OWNER", "BROKER")
						.requestMatchers(HttpMethod.DELETE, "/api/properties/*").hasAnyRole("OWNER", "BROKER", "ADMIN")
						.requestMatchers(HttpMethod.POST, "/api/properties/*/images").hasAnyRole("OWNER", "BROKER")
						.requestMatchers(HttpMethod.GET, "/api/properties").permitAll()
						.requestMatchers(HttpMethod.GET, "/api/properties/*").permitAll()
						.requestMatchers(HttpMethod.GET, "/api/properties/search").permitAll()

						//Booking Management
						.requestMatchers(HttpMethod.GET, "/api/bookings").authenticated()
						.requestMatchers(HttpMethod.GET, "/api/bookings/*").authenticated()
						.requestMatchers(HttpMethod.POST, "/api/bookings").hasRole("CUSTOMER")
						.requestMatchers(HttpMethod.PUT, "/api/bookings/*/status").hasAnyRole("OWNER", "BROKER")

						.anyRequest().authenticated())
				.addFilterBefore(jwtFilter, UsernamePasswordAuthenticationFilter.class);

		return http.build();
	}

	@Bean
	public AuthenticationManager authenticationManager(AuthenticationConfiguration authConfig) throws Exception {
		return authConfig.getAuthenticationManager();
	}

	@Bean
	public PasswordEncoder passwordEncoder() {
		return new BCryptPasswordEncoder();
	}
}