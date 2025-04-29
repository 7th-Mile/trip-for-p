package team.seventhmile.tripforp.domain.reviewPost.entity;

import jakarta.persistence.*;
import java.util.ArrayList;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.hibernate.annotations.ColumnDefault;
import team.seventhmile.tripforp.domain.course.entity.Course;
import team.seventhmile.tripforp.domain.file.entity.ReviewFile;
import team.seventhmile.tripforp.domain.reviewComment.entity.ReviewComment;
import team.seventhmile.tripforp.domain.user.entity.User;
import team.seventhmile.tripforp.global.common.BaseEntity;
import java.util.List;

@Entity
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Table(name = "review_posts")
public class ReviewPost extends BaseEntity {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Column(name = "review_post_id")
	private Long id;

	@ManyToOne
	@JoinColumn(name = "course_id", nullable = false)
	private Course course;

	@ManyToOne
	@JoinColumn(name = "user_id", nullable = false)
	private User user;

	@Column(nullable = false)
	private String title;

	@Column(nullable = false)
	private String content;

	@Column(nullable = false)
	@ColumnDefault("0")
	private Integer views;

	@OneToMany(mappedBy = "reviewPost", cascade = CascadeType.ALL, orphanRemoval = true)
	private List<ReviewComment> comments;

	@OneToMany(mappedBy = "reviewPost", cascade = CascadeType.ALL, orphanRemoval = true)
	@Builder.Default
	private List<ReviewFile> files = new ArrayList<>();

	public void update(String title, String content) {
		validateField(title, "Title");
		validateField(content, "Content");

		this.title = title;
		this.content = content;
	}

	public void addFile(ReviewFile file) {
		this.files.add(file);
		file.setReviewPost(this);
	}

	public void clearFile() {
		this.files.clear();
	}

	public void incrementViews() {
		this.views += 1;
	}

	public static void validateField(String field, String fieldName) {
		if (field == null || field.isEmpty()) {
			throw new IllegalArgumentException(fieldName + "은(는) 필수 입력 항목입니다.");
		}
	}
}
