package team.seventhmile.tripforp.domain.user.controller;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import team.seventhmile.tripforp.domain.user.dto.ApiResponse;
import team.seventhmile.tripforp.domain.user.dto.FindPasswordRequest;
import team.seventhmile.tripforp.domain.user.dto.ModifyPasswordRequest;
import team.seventhmile.tripforp.domain.user.dto.UserDto;
import team.seventhmile.tripforp.domain.user.dto.UserInfoRequest;
import team.seventhmile.tripforp.domain.user.dto.UserInfoResponse;
import team.seventhmile.tripforp.domain.user.service.CustomUserDetails;
import team.seventhmile.tripforp.domain.user.service.UserService;
import team.seventhmile.tripforp.global.exception.AuthCustomException;
import team.seventhmile.tripforp.global.exception.ErrorCode;
import team.seventhmile.tripforp.global.jwt.JwtUtil;

@RestController
@RequestMapping("/api/users")
public class UserController {

	private final UserService userService;
	private final JwtUtil jwtUtil;

	public UserController(@Qualifier("memoryUserService") UserService userService, JwtUtil jwtUtil) {
		this.userService = userService;
		this.jwtUtil = jwtUtil;
	}

	@PostMapping("/registration")
	public ResponseEntity<?> signup(@Valid @RequestBody UserDto userDto) {

		userService.register(userDto);

		return ResponseEntity.ok(new ApiResponse("success", "회원 가입 성공했습니다."));
	}

	@GetMapping("/nickname-verification")
	public ResponseEntity<?> checkDuplicatedNickname(@RequestParam("nickname") String nickname) {
		boolean isDuplicated = userService.isDuplicatedNickname(nickname);
		if (isDuplicated) {
			throw new AuthCustomException(ErrorCode.NICKNAME_ALREADY_IN_USE);
		} else {
			return ResponseEntity.ok(new ApiResponse("success", "사용 가능한 닉네임입니다."));
		}
	}

	@PreAuthorize("hasAnyRole('USER', 'ADMIN')")
	@PostMapping("/reissue")
	public ResponseEntity<?> reissue(HttpServletRequest request, HttpServletResponse response) {

		return jwtUtil.reissueToken(request, response);
	}

	@PreAuthorize("hasRole('USER')")
	@GetMapping("/me")
	public ResponseEntity<UserInfoResponse> getUserInfo(
		@AuthenticationPrincipal UserDetails userDetails) {
		return ResponseEntity.ok(userService.getUserInfo(userDetails));
	}

	@PreAuthorize("hasRole('USER')")
	@PatchMapping("/me")
	public ResponseEntity<UserInfoResponse> updateUser(
		@AuthenticationPrincipal CustomUserDetails userDetails,
		@Valid @RequestBody UserInfoRequest userInfoReq) {
		return ResponseEntity.ok(userService.updateInfo(userDetails, userInfoReq));
	}

	@PreAuthorize("hasRole('USER')")
	@PatchMapping("/me/password")
	public ResponseEntity<?> modifyPassword(HttpServletRequest request,
		@Valid @RequestBody ModifyPasswordRequest modifyPasswordRequest) {
		return userService.modifyPassword(request, modifyPasswordRequest.getNewPassword());
	}

	@PostMapping("/password/renewal")
	public ResponseEntity<?> resetPassword(@Valid @RequestBody FindPasswordRequest findPasswordRequest) {
		return userService.findPassword(findPasswordRequest.getEmail(),
			findPasswordRequest.getNewPassword());
	}

	@PreAuthorize("hasRole('USER')")
	@PatchMapping("/deletion")
	public ResponseEntity<?> deleteUser(
			@AuthenticationPrincipal UserDetails userDetails,
			HttpServletResponse httpServletResponse) {
        userService.deleteUser(userDetails, httpServletResponse);
        return ResponseEntity.status(HttpStatus.NO_CONTENT).build();
    }
}
