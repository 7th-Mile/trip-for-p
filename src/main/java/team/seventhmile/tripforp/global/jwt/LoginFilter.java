package team.seventhmile.tripforp.global.jwt;

import jakarta.servlet.FilterChain;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.Collection;
import java.util.Iterator;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import team.seventhmile.tripforp.domain.refresh.service.RefreshService;
import team.seventhmile.tripforp.domain.user.service.CustomUserDetails;

@RequiredArgsConstructor
public class LoginFilter extends UsernamePasswordAuthenticationFilter {

	private final AuthenticationManager authenticationManager;
	private final JwtUtil jwtUtil;
	private final RefreshService refreshService;

	@Override
	public Authentication attemptAuthentication(HttpServletRequest request,
		HttpServletResponse response) throws AuthenticationException {

		String email = obtainUsername(request);
		String password = obtainPassword(request);

		UsernamePasswordAuthenticationToken authToken = new UsernamePasswordAuthenticationToken(
			email, password, null);

		return authenticationManager.authenticate(authToken);
	}

	@Override
	protected void successfulAuthentication(HttpServletRequest request,
		HttpServletResponse response, FilterChain chain, Authentication authentication)
		throws IOException {

		String username = authentication.getName();

		Collection<? extends GrantedAuthority> authorities = authentication.getAuthorities();
		Iterator<? extends GrantedAuthority> iterator = authorities.iterator();
		GrantedAuthority auth = iterator.next();

		String role = auth.getAuthority().split("_")[1];

		String nickname = ((CustomUserDetails) authentication.getPrincipal()).getNickname();

		String access = jwtUtil.createJwt("access", username, nickname, role, 600000L);
		String refresh = jwtUtil.createJwt("refresh", username, nickname, role, 86400000L);

		refreshService.saveRefreshToken(username, refresh, 86400000L);

		response.setHeader("access", "Bearer " + access);

		Cookie cookie = new Cookie("refresh", null);
		cookie.setHttpOnly(true);
		cookie.setMaxAge(0);
		cookie.setPath("/");
		response.addCookie(cookie);
		response.addCookie(createCookie("refresh", refresh));
		response.setStatus(HttpStatus.OK.value());

	}

	@Override
	protected void unsuccessfulAuthentication(HttpServletRequest request,
		HttpServletResponse response, AuthenticationException failed) {
		response.setStatus(401);
	}

	private Cookie createCookie(String key, String value) {

		Cookie cookie = new Cookie(key, value);
		cookie.setMaxAge(24 * 60 * 60);
		cookie.setHttpOnly(true);
		cookie.setPath("/");

		return cookie;
	}
}
