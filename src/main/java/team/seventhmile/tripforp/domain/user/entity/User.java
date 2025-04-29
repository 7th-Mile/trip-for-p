package team.seventhmile.tripforp.domain.user.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.*;
import team.seventhmile.tripforp.domain.user.dto.UserGetDto;
import team.seventhmile.tripforp.global.common.BaseEntity;

@Entity
@AllArgsConstructor
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Builder
@Table(name = "users")
public class User extends BaseEntity {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Column(name = "user_id")
	private Long id;

	@Column(nullable = false, unique = true)
	private String email;

	@Column(nullable = false)
	private String password;

	@Column(nullable = false, unique = true)
	private String nickname;

	@Column(nullable = false)
	private Boolean isDeleted;

	@Column(nullable = false)
	@Enumerated(EnumType.STRING)
	private Role role;

	protected User(String nickname, String email) {
		this.nickname = nickname;
		this.email = email;
	}

	public static User fromDto(UserGetDto dto) {
		return User.builder()
				.nickname(dto.getNickname())
				.email(dto.getEmail())
				.build();
	}

	public UserGetDto toDto() {
		return new UserGetDto(this.nickname, this.email);
	}

	public void updateNickname(String nickname){
		this.nickname = nickname;
	}
	public void updatePassword(String password){
		this.password = password;
	}

	public void withdrawUser(){
		this.isDeleted = true;
	}
}
