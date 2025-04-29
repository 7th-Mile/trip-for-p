package team.seventhmile.tripforp.domain.user.service;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import team.seventhmile.tripforp.domain.user.dto.UserDto;
import team.seventhmile.tripforp.domain.user.dto.UserInfoRequest;
import team.seventhmile.tripforp.domain.user.dto.UserInfoResponse;


public interface UserService {

	void register(UserDto userDto);

	boolean isDuplicatedNickname(String nickname);

	ResponseEntity<?> resetPassword(String username, String newPassword);

	UserInfoResponse getUserInfo(UserDetails userDetails);

	ResponseEntity<?> modifyPassword(HttpServletRequest request, String newPassword);

	String extractAccessToken(HttpServletRequest request);

	UserInfoResponse updateInfo(@AuthenticationPrincipal CustomUserDetails userDetails, UserInfoRequest userInfoReq);

	ResponseEntity<?> findPassword(String email, String newPassword);

	void deleteUser(UserDetails userDetails, HttpServletResponse response);
}
