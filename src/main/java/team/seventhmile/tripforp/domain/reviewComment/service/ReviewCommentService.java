package team.seventhmile.tripforp.domain.reviewComment.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import team.seventhmile.tripforp.domain.freeComment.entity.FreeComment;
import team.seventhmile.tripforp.domain.reviewComment.dto.GetReviewCommentDto;
import team.seventhmile.tripforp.domain.reviewComment.dto.ReviewCommentDto;
import team.seventhmile.tripforp.domain.reviewComment.entity.ReviewComment;
import team.seventhmile.tripforp.domain.reviewComment.repository.ReviewCommentRepository;
import team.seventhmile.tripforp.domain.reviewPost.entity.ReviewPost;
import team.seventhmile.tripforp.domain.user.entity.Role;
import team.seventhmile.tripforp.domain.user.entity.User;
import team.seventhmile.tripforp.domain.user.repository.UserRepository;
import team.seventhmile.tripforp.global.exception.ResourceNotFoundException;
import team.seventhmile.tripforp.global.exception.UnauthorizedAccessException;

@Service
@Transactional(readOnly = true)
public class ReviewCommentService {

	private final ReviewCommentRepository reviewCommentRepository;
	private final UserRepository userRepository;

	@Autowired
	public ReviewCommentService(ReviewCommentRepository reviewCommentRepository,
		UserRepository userRepository) {
		this.reviewCommentRepository = reviewCommentRepository;
		this.userRepository = userRepository;
	}

	public Page<GetReviewCommentDto> getCommentsByPost(ReviewPost reviewPost, Pageable pageable) {
		Page<ReviewComment> comments = reviewCommentRepository.findByReviewPost(reviewPost, pageable);
		return comments.map(GetReviewCommentDto::new);
	}

	@Transactional
	public ReviewCommentDto createComment(ReviewPost reviewPost, ReviewCommentDto reviewCommentDto,
		UserDetails user) {
		User findUser = getUser(user);
		ReviewComment reviewComment = mapToEntity(reviewCommentDto);
		reviewComment.setReviewPost(reviewPost);
		reviewComment.setAuthor(findUser);
		ReviewComment savedComment = reviewCommentRepository.save(reviewComment);
		return mapToDto(savedComment);
	}

	@Transactional
	public ReviewCommentDto updateComment(Long id, ReviewCommentDto updatedCommentDto, UserDetails user) {
		User findUser = getUser(user);
		ReviewComment reviewComment = reviewCommentRepository.findById(id)
			.orElseThrow(() -> new ResourceNotFoundException(ReviewComment.class, id));

		if (!reviewComment.getAuthor().getId().equals(findUser.getId())) {
			throw new UnauthorizedAccessException(FreeComment.class);
		}
		reviewComment.setContent(updatedCommentDto.getContent());
		ReviewComment updatedComment = reviewCommentRepository.save(reviewComment);
		return mapToDto(updatedComment);
	}

	@Transactional
	public void deleteComment(Long id, UserDetails user) {
		User findUser = getUser(user);
		ReviewComment reviewComment = reviewCommentRepository.findById(id)
			.orElseThrow(() -> new ResourceNotFoundException(ReviewComment.class, id));

		if (!reviewComment.getAuthor().getId().equals(findUser.getId()) && !reviewComment.getReviewPost()
			.getUser().getId().equals(findUser.getId()) && findUser.getRole() != Role.ADMIN) {
			throw new UnauthorizedAccessException(FreeComment.class);
		}
		reviewCommentRepository.delete(reviewComment);
	}

	private ReviewCommentDto mapToDto(ReviewComment reviewComment) {
		return ReviewCommentDto.builder()
			.id(reviewComment.getId())
			.content(reviewComment.getContent())
			.postId(reviewComment.getReviewPost().getId())
			.authorId(reviewComment.getAuthor().getId())
			.build();
	}

	private ReviewComment mapToEntity(ReviewCommentDto reviewCommentDto) {
		ReviewComment reviewComment = new ReviewComment();
		reviewComment.setContent(reviewCommentDto.getContent());
		return reviewComment;
	}

	private User getUser(UserDetails user) {
		return userRepository.findByEmail(user.getUsername())
			.orElseThrow(() -> new ResourceNotFoundException(User.class));
	}

	@Transactional(readOnly = true)
    public Page<ReviewCommentDto> getMyReviewCommentList(UserDetails user, Pageable pageable) {
		return reviewCommentRepository.findByAuthor_Email(user.getUsername(), pageable)
				.map(ReviewCommentDto::convertToDto);
    }
}
