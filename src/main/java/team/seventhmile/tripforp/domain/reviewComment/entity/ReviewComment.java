package team.seventhmile.tripforp.domain.reviewComment.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.experimental.SuperBuilder;
import team.seventhmile.tripforp.domain.reviewPost.entity.ReviewPost;
import team.seventhmile.tripforp.domain.user.entity.User;
import team.seventhmile.tripforp.global.common.BaseEntity;

@Entity
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@SuperBuilder
@Table(name = "review_post_comments")
public class ReviewComment extends BaseEntity {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Column
	private Long id;

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "review_post_id", nullable = false)
	private ReviewPost reviewPost;

	@ManyToOne
	@JoinColumn(name = "user_id", nullable = false)
	private User author;

	@Column(nullable = false, columnDefinition = "TEXT")
	private String content;

}
