package team.seventhmile.tripforp.global.jwt;

import io.jsonwebtoken.ExpiredJwtException;
import io.jsonwebtoken.Jwts;
import jakarta.annotation.PostConstruct;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.nio.charset.StandardCharsets;
import java.util.Date;
import javax.crypto.SecretKey;
import javax.crypto.spec.SecretKeySpec;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;
import team.seventhmile.tripforp.domain.refresh.service.RefreshService;

@Component
@RequiredArgsConstructor
public class JwtUtil {

	private SecretKey secretKey;
	private final RefreshService refreshService;

	@Value("${jwt.secret}")
	private String secret;

	@PostConstruct
	public void init() {
		this.secretKey = new SecretKeySpec(secret.getBytes(StandardCharsets.UTF_8),
			Jwts.SIG.HS256.key().build().getAlgorithm());
	}

	public String getUsername(String token) {

		return Jwts.parser().verifyWith(secretKey).build().parseSignedClaims(token).getPayload()
			.get("username", String.class);
	}

	public String getRole(String token) {

		return Jwts.parser().verifyWith(secretKey).build().parseSignedClaims(token).getPayload()
			.get("role", String.class);
	}

	public String getNickname(String token) {

		return Jwts.parser().verifyWith(secretKey).build().parseSignedClaims(token).getPayload()
			.get("nickname", String.class);
	}

	public String getCategory(String token) {

		return Jwts.parser().verifyWith(secretKey).build().parseSignedClaims(token).getPayload()
			.get("category", String.class);
	}

	public Boolean isExpired(String token) {
		try {
			return Jwts.parser()
				.verifyWith(secretKey)
				.build()
				.parseSignedClaims(token)
				.getPayload()
				.getExpiration()
				.before(new Date());
		} catch (ExpiredJwtException e) {
			return true;
		}
	}

	public String createJwt(String category, String username, String nickname, String role, Long expiredMs) {

		return Jwts.builder()
			.claim("category", category)
			.claim("username", username)
			.claim("nickname", nickname)
			.claim("role", role)
			.issuedAt(new Date(System.currentTimeMillis()))
			.expiration(new Date(System.currentTimeMillis() + expiredMs))
			.signWith(secretKey)
			.compact();
	}

	public ResponseEntity<?> reissueToken(HttpServletRequest request,
		HttpServletResponse response) {
		String refresh = getRefreshTokenFromCookie(request);
		if (refresh == null) {
			return new ResponseEntity<>("refresh token null", HttpStatus.BAD_REQUEST);
		}

		try {
			isExpired(refresh);
		} catch (ExpiredJwtException e) {
			return new ResponseEntity<>("refresh token expired", HttpStatus.BAD_REQUEST);
		}

		String username = getUsername(refresh);
		String role = getRole(refresh);
		String nickname = getNickname(refresh);

		String refreshToken = refreshService.getRefreshToken(username);

		if (!refresh.equals(refreshToken)) {
			return new ResponseEntity<>("invalid refresh token", HttpStatus.BAD_REQUEST);
		}

		String newAccess = createJwt("access", username, nickname, role, 600000L);
		String newRefresh = createJwt("refresh", username, nickname, role, 86400000L);

		refreshService.saveRefreshToken(username, newRefresh, 86400000L);

		response.setHeader("access", "Bearer " + newAccess);
		response.addCookie(createCookie("refresh", newRefresh));

		return new ResponseEntity<>(HttpStatus.OK);
	}

	private String getRefreshTokenFromCookie(HttpServletRequest request) {
		Cookie[] cookies = request.getCookies();
		if (cookies != null) {
			for (Cookie cookie : cookies) {
				if (cookie.getName().equals("refresh")) {
					return cookie.getValue();
				}
			}
		}
		return null;
	}

	private Cookie createCookie(String key, String value) {
		Cookie cookie = new Cookie(key, value);
		cookie.setMaxAge(24 * 60 * 60);
		cookie.setHttpOnly(true);
		cookie.setPath("/");

		return cookie;
	}

}
