package team.seventhmile.tripforp.domain.user.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import team.seventhmile.tripforp.domain.user.entity.Role;

@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
public class UserDto {

	@NotBlank(message = "이메일은 필수 입력 값입니다.")
	@Pattern(regexp = "^[A-Za-z0-9._%+-]+@[A-Za-z0-9.-]+.[A-Za-z]{2,6}$", message = "이메일 형식이 올바르지 않습니다.")
	private String email;

	@NotBlank(message = "비밀번호는 필수 입력 값입니다.")
	@Pattern(regexp = "^(?=.*[a-z])(?=.*\\d)[A-Za-z\\d@$!%*?&]{8,16}$", message = "비밀번호는 8~16자이며 영문 소문자, 숫자를 포함해야 합니다.")
	private String password;

	@NotBlank(message = "닉네임은 필수 입력 값입니다.")
	@Pattern(regexp = "^[ㄱ-ㅎ가-힣a-z0-9-_]{2,10}$", message = "닉네임은 특수문자를 제외한 2~10자리여야 합니다.")
	private String nickname;
	private Role role;

}
