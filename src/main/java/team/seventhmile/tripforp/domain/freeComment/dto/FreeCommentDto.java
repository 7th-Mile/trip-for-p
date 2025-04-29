package team.seventhmile.tripforp.domain.freeComment.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import team.seventhmile.tripforp.domain.freeComment.entity.FreeComment;

import java.time.ZonedDateTime;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class FreeCommentDto {

	private Long id;

	@NotBlank(message = "내용을 입력해주세요.")
	@Size(max = 500, message = "댓글은 500자 이하로 입력해주세요.")
	private String content;

	private Long postId;
	private Long authorId;
	private ZonedDateTime createdAt;

	public FreeCommentDto(FreeComment freeComment) {
		this.id = freeComment.getId();
		this.content = freeComment.getContent();
		this.postId = freeComment.getFreePost().getId();
		this.authorId = freeComment.getAuthor().getId();
		this.createdAt = freeComment.getCreatedAt();
	}

	public static FreeCommentDto convertToDto(FreeComment freeComment) {
		return FreeCommentDto.builder()
				.id(freeComment.getId())
				.content(freeComment.getContent())
				.postId(freeComment.getFreePost().getId())
				.authorId(freeComment.getAuthor().getId())
				.createdAt(freeComment.getCreatedAt())
				.build();
	}

}
