package team.seventhmile.tripforp.domain.user.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
public class UserGetDto extends UserDto {
    private String email;
    private String nickname;

}
