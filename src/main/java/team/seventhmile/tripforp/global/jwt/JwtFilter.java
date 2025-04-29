package team.seventhmile.tripforp.global.jwt;

import io.jsonwebtoken.ExpiredJwtException;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.PrintWriter;
import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.filter.OncePerRequestFilter;
import team.seventhmile.tripforp.domain.user.entity.Role;
import team.seventhmile.tripforp.domain.user.entity.User;
import team.seventhmile.tripforp.domain.user.service.CustomUserDetails;

@RequiredArgsConstructor
public class JwtFilter extends OncePerRequestFilter {

	private final JwtUtil jwtUtil;

	@Override
	protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response,
		FilterChain filterChain) throws ServletException, IOException {

		String accessToken = request.getHeader("Authorization");

		if (accessToken == null || !accessToken.startsWith("Bearer ")) {
			filterChain.doFilter(request, response);

			return;
		}
		String token = accessToken.split(" ")[1];
		try {
			jwtUtil.isExpired(token);
		} catch (ExpiredJwtException e) {

			PrintWriter writer = response.getWriter();
			writer.print("access token expired");

			response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
			return;
		}

		String category = jwtUtil.getCategory(token);

		if (!category.equals("access")) {

			PrintWriter writer = response.getWriter();
			writer.print("invalid access token");

			response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
			return;
		}
		String username = jwtUtil.getUsername(token);
		String role = jwtUtil.getRole(token);

		User user = User.builder()
			.email(username)
			.role(Role.valueOf(role))
			.build();
		CustomUserDetails customUserDetails = new CustomUserDetails(user);

		Authentication authToken = new UsernamePasswordAuthenticationToken(customUserDetails, null,
			customUserDetails.getAuthorities());
		SecurityContextHolder.getContext().setAuthentication(authToken);

		filterChain.doFilter(request, response);
	}
}
