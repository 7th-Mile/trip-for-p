package team.seventhmile.tripforp.global.jwt;

import io.jsonwebtoken.ExpiredJwtException;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.ServletRequest;
import jakarta.servlet.ServletResponse;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;
import lombok.RequiredArgsConstructor;
import org.springframework.web.filter.GenericFilterBean;
import team.seventhmile.tripforp.domain.refresh.service.RefreshService;

@RequiredArgsConstructor
public class CustomLogoutFilter extends GenericFilterBean {

	private final JwtUtil jwtUtil;
	private final RefreshService refreshService;

	@Override
	public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain)
		throws IOException, ServletException {

		doFilter((HttpServletRequest) request, (HttpServletResponse) response, chain);
	}

	private void doFilter(HttpServletRequest request, HttpServletResponse response,
		FilterChain filterChain) throws IOException, ServletException {

		String requestUri = request.getRequestURI();
		if (!requestUri.matches("^\\/api\\/users\\/signout$")) {

			filterChain.doFilter(request, response);
			return;
		}
		String requestMethod = request.getMethod();
		if (!requestMethod.equals("POST")) {

			filterChain.doFilter(request, response);
			return;
		}

		String refresh = null;
		Cookie[] cookies = request.getCookies();
		if (cookies != null) {
			for (Cookie cookie : cookies) {
				if (cookie.getName().equals("refresh")) {
					refresh = cookie.getValue();
					break;
				}
			}
		}

		if (refresh == null) {

			response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
			return;
		}

		try {
			jwtUtil.isExpired(refresh);
		} catch (ExpiredJwtException e) {

			response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
			return;
		}

		String category = jwtUtil.getCategory(refresh);
		if (!category.equals("refresh")) {

			response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
			return;
		}

		String username = jwtUtil.getUsername(refresh);
		Boolean isKeyExist = refreshService.isKeyExists(username);
		if (!isKeyExist) {

			response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
			return;
		}

		refreshService.deleteRefreshToken(username);

		Cookie cookie = new Cookie("refresh", null);
		cookie.setHttpOnly(true);
		cookie.setMaxAge(0);
		cookie.setPath("/");
		response.addCookie(cookie);
		response.setStatus(HttpServletResponse.SC_OK);
	}
}
