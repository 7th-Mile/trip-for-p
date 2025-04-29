package team.seventhmile.tripforp.domain.reviewPost.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import java.time.ZonedDateTime;
import java.util.ArrayList;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import team.seventhmile.tripforp.domain.file.entity.ReviewFile;
import team.seventhmile.tripforp.domain.course.entity.Course;
import team.seventhmile.tripforp.domain.reviewComment.dto.GetReviewCommentDto;
import team.seventhmile.tripforp.domain.reviewPost.entity.ReviewPost;

import java.util.List;
import team.seventhmile.tripforp.domain.user.entity.User;

@NoArgsConstructor
@AllArgsConstructor
@Data
@Builder
public class ReviewPostDto {

	private Long id;

	private Long courseId;

	private String author;

	@NotBlank
	@Size(max = 255, message = "제목은 255자를 초과할 수 없습니다.")
	private String title;

	@NotBlank
	@Size(max = 999999, message = "내용은 999999자를 초과할 수 없습니다.")
	private String content;

	private Integer views;

	private ZonedDateTime createdAt;
	private ZonedDateTime updatedAt;

	private List<String> fileUrls;

	private List<GetReviewCommentDto> comments;

	public ReviewPost convertToEntity(User user, Course course) {
		return ReviewPost.builder()
			.id(this.id)
			.course(course)
			.user(user)
			.title(this.title)
			.content(this.content)
			.views(0)
			.build();
	}

	public static ReviewPostDto convertToDto(ReviewPost reviewPost) {
		return ReviewPostDto.builder()
			.id(reviewPost.getId())
			.courseId(reviewPost.getCourse().getId())
			.author(reviewPost.getUser().getNickname())
			.title(reviewPost.getTitle())
			.content(reviewPost.getContent())
			.views(reviewPost.getViews())
			.createdAt(reviewPost.getCreatedAt())
			.updatedAt(reviewPost.getUpdatedAt())
			.fileUrls(reviewPost.getFiles().stream().map(ReviewFile::getUrl).toList())
			.comments(reviewPost.getComments() != null ? reviewPost.getComments().stream().map(
				GetReviewCommentDto::new).toList() : new ArrayList<>())
			.build();
	}
}
