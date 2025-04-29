package team.seventhmile.tripforp.domain.user.service;

import io.jsonwebtoken.ExpiredJwtException;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.util.Optional;
import lombok.RequiredArgsConstructor;
import org.springframework.cache.Cache;
import org.springframework.cache.Cache.ValueWrapper;
import org.springframework.cache.CacheManager;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;
import team.seventhmile.tripforp.domain.refresh.service.RefreshService;
import team.seventhmile.tripforp.domain.user.dto.UserDto;
import team.seventhmile.tripforp.domain.user.dto.UserInfoRequest;
import team.seventhmile.tripforp.domain.user.dto.UserInfoResponse;
import team.seventhmile.tripforp.domain.user.entity.Role;
import team.seventhmile.tripforp.domain.user.entity.User;
import team.seventhmile.tripforp.domain.user.repository.UserRepository;
import team.seventhmile.tripforp.global.exception.AuthCustomException;
import team.seventhmile.tripforp.global.exception.ErrorCode;
import team.seventhmile.tripforp.global.jwt.JwtUtil;

@Service
@RequiredArgsConstructor
public class MemoryUserService implements UserService {

	private final UserRepository userRepository;
	private final BCryptPasswordEncoder bCryptPasswordEncoder;
	private final JwtUtil jwtUtil;
    private final RefreshService refreshService;
	private final CacheManager cacheManager;

	@Transactional
	public void register(UserDto userDto) {

		if (!StringUtils.hasText(userDto.getEmail())) {
			throw new AuthCustomException(ErrorCode.REQUIRED_FIELD_MISSING);
		}
		Optional<User> existingUser = userRepository.findByEmail(userDto.getEmail());
		if (existingUser.isPresent()) {
			User user = existingUser.get();
			if (user.getIsDeleted()) {
				throw new AuthCustomException(ErrorCode.WITHDRAWN_USER);
			}
			throw new AuthCustomException(ErrorCode.EMAIL_ALREADY_IN_USE);
		}
		if (isDuplicatedNickname(userDto.getNickname())) {
			throw new AuthCustomException(ErrorCode.NICKNAME_ALREADY_IN_USE);
		}

		Cache cache = cacheManager.getCache("emailCodes");
		ValueWrapper wrapper = cache.get(userDto.getEmail());
		if (wrapper == null) {
			throw new AuthCustomException(ErrorCode.VERIFICATION_CODE_NOT_FOUND);
		}
		String verifyStatus = (String) wrapper.get();
		if (!verifyStatus.equals("verified")) {
			throw new AuthCustomException(ErrorCode.EMAIL_NOT_VERIFIED);
		}
		String encodedPassword = bCryptPasswordEncoder.encode(userDto.getPassword());

		User newUser =
			User.builder()
				.email(userDto.getEmail())
				.nickname(userDto.getNickname())
				.password(encodedPassword)
				.isDeleted(false)
				.role(Role.USER)
				.build();
		userRepository.save(newUser);

		cache.evict(userDto.getEmail());
	}

	public boolean isDuplicatedNickname(String nickname) {
		if (nickname.length() < 2) {
			throw new AuthCustomException(ErrorCode.NICKNAME_TOO_SHORT);
		}
		if (nickname.length() > 10) {
			throw new AuthCustomException(ErrorCode.NICKNAME_TOO_LONG);
		}
		if (userRepository.existsByNickname(nickname)) {
			return true;
		}
		return false;
	}

	public ResponseEntity<?> resetPassword(String username, String newPassword) {

		try {
			User currentUser = userRepository.findByEmail(username)
				.orElseThrow(() -> new AuthCustomException(ErrorCode.USER_NOT_FOUND_IN_DATABASE));

			User updatedUser = User.builder()
				.id(currentUser.getId())
				.email(currentUser.getEmail())
				.password(bCryptPasswordEncoder.encode(newPassword))
				.isDeleted(false)
				.nickname(currentUser.getNickname())
				.role(currentUser.getRole())
				.build();

			userRepository.save(updatedUser);

			return new ResponseEntity<>("비밀번호 변경이 성공적으로 완료되었습니다.", HttpStatus.OK);
		} catch (ExpiredJwtException e) {
			throw new AuthCustomException(ErrorCode.TOKEN_EXPIRED);
		} catch (Exception e) {
			throw new AuthCustomException(ErrorCode.PASSWORD_CHANGE_ERROR);
		}
	}

	//개인정보 조회
	@Transactional(readOnly = true)
	public UserInfoResponse getUserInfo(UserDetails userDetails) {
		User user = userRepository.findByEmail(userDetails.getUsername())
			.orElseThrow(() -> new AuthCustomException(ErrorCode.USER_NOT_FOUND));
		return UserInfoResponse.from(user);
	}

	//비밀번호 변경 (비밀번호 로직 중복 정리 예정)
	@Transactional
	public ResponseEntity<?> modifyPassword(HttpServletRequest request, String newPassword) {
		String accessToken = extractAccessToken(request);
		if (accessToken == null) {
			throw new AuthCustomException(ErrorCode.ACCESS_TOKEN_NOT_FOUND);
		}

		String username = jwtUtil.getUsername(accessToken);
		if (username == null) {
			throw new AuthCustomException(ErrorCode.EMAIL_NOT_FOUND_IN_TOKEN);
		}

		return resetPassword(username, newPassword);
	}

	public String extractAccessToken(HttpServletRequest request) {
		return request.getHeader("access");
	}

	//개인정보 수정
	@Transactional
	public UserInfoResponse updateInfo(@AuthenticationPrincipal CustomUserDetails userDetails, UserInfoRequest userInfoReq) {
		User updatedUser = userRepository.findByEmail(userDetails.getUsername())
			.orElseThrow(() -> new AuthCustomException(ErrorCode.USER_NOT_FOUND));
		if (userInfoReq.getNickname() != null && !userInfoReq.getNickname().isEmpty()) {
			if (!updatedUser.getNickname().equals(userInfoReq.getNickname())) {
				if (userRepository.existsByNickname(userInfoReq.getNickname())) {
					throw new AuthCustomException(ErrorCode.NICKNAME_ALREADY_IN_USE);
				}
				updatedUser.updateNickname(userInfoReq.getNickname());
			}
		}
		if (userInfoReq.getPassword() != null && !userInfoReq.getPassword().isEmpty()) {
			String newPassword = bCryptPasswordEncoder.encode(userInfoReq.getPassword());
			updatedUser.updatePassword(newPassword);
		}
		return UserInfoResponse.from(updatedUser);
	}

	@Transactional
	public ResponseEntity<?> findPassword(String email, String newPassword) {
		try {
			Cache cache = cacheManager.getCache("emailCodes");
			ValueWrapper wrapper = cache.get(email);
			if (wrapper == null) {
				throw new AuthCustomException(ErrorCode.VERIFICATION_CODE_NOT_FOUND);
			}
			String verifyStatus = (String) wrapper.get();
			if (!verifyStatus.equals("verified")) {
				throw new AuthCustomException(ErrorCode.EMAIL_NOT_VERIFIED);
			}

			User user = userRepository.findByEmail(email)
				.orElseThrow(() -> {
					return new AuthCustomException(ErrorCode.USER_NOT_FOUND_IN_DATABASE);
				});

			cache.evict(email);
			return resetPassword(email, newPassword);
		} catch (AuthCustomException e) {
			throw e;
		} catch (Exception e) {
			throw new AuthCustomException(ErrorCode.PASSWORD_CHANGE_ERROR);
		}
	}
	@Transactional
	public void deleteUser(UserDetails userDetails, HttpServletResponse response) {
		User user = userRepository.findByEmail(userDetails.getUsername())
				.orElseThrow(() -> new AuthCustomException(ErrorCode.USER_NOT_FOUND));
		user.withdrawUser();
		refreshService.deleteRefreshToken(userDetails.getUsername());
		Cookie cookie = new Cookie("refresh", null);
		cookie.setMaxAge(0);
		cookie.setPath("/api/users");
		response.addCookie(cookie);
	}
}
