package team.seventhmile.tripforp.domain.reviewComment.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import team.seventhmile.tripforp.domain.reviewComment.entity.ReviewComment;

import java.time.ZonedDateTime;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ReviewCommentDto {

	private Long id;

	@NotBlank(message = "내용을 입력해주세요.")
	@Size(max = 500, message = "댓글은 500자 이하로 입력해주세요.")
	private String content;

	private Long postId;
	private Long authorId;
	private ZonedDateTime createdAt;

	public static ReviewCommentDto convertToDto(ReviewComment reviewComment) {
		return ReviewCommentDto.builder()
				.id(reviewComment.getId())
				.content(reviewComment.getContent())
				.postId(reviewComment.getReviewPost().getId())
				.authorId(reviewComment.getAuthor().getId())
				.createdAt(reviewComment.getCreatedAt())
				.build();
	}
}
