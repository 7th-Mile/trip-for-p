package team.seventhmile.tripforp.domain.reviewComment.controller;

import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import team.seventhmile.tripforp.domain.reviewComment.dto.GetReviewCommentDto;
import team.seventhmile.tripforp.domain.reviewComment.dto.ReviewCommentDto;
import team.seventhmile.tripforp.domain.reviewComment.service.ReviewCommentService;
import team.seventhmile.tripforp.domain.reviewPost.entity.ReviewPost;
import team.seventhmile.tripforp.domain.reviewPost.service.ReviewPostService;

@RestController
@RequestMapping("/api/review-posts/{postId}/comments")
public class ReviewCommentController {

	private final ReviewCommentService reviewCommentService;
	private final ReviewPostService reviewPostService;

	@Autowired
	public ReviewCommentController(ReviewCommentService reviewCommentService,
		ReviewPostService reviewPostService) {
		this.reviewCommentService = reviewCommentService;
		this.reviewPostService = reviewPostService;
	}

	@GetMapping
	public ResponseEntity<Page<GetReviewCommentDto>> getReviewComments(@PathVariable("postId") Long postId, Pageable pageable) {
		ReviewPost reviewPost = reviewPostService.getReviewPostEntity(postId);
		Page<GetReviewCommentDto> reviewComments = reviewCommentService.getCommentsByPost(reviewPost, pageable);
		return ResponseEntity.ok(reviewComments);
	}

	@PreAuthorize("hasRole('USER')")
	@PostMapping
	public ResponseEntity<ReviewCommentDto> createReviewComment(
		@PathVariable("postId") Long postId,
		@Valid @RequestBody ReviewCommentDto commentDto,
		@AuthenticationPrincipal UserDetails user) {
		ReviewPost reviewPost = reviewPostService.getReviewPostEntity(postId);
		ReviewCommentDto createdComment = reviewCommentService.createComment(reviewPost, commentDto,
			user);
		return ResponseEntity.status(HttpStatus.CREATED).body(createdComment);
	}

	@PreAuthorize("hasRole('USER')")
	@PutMapping("/{commentId}")
	public ResponseEntity<ReviewCommentDto> updateReviewComment(
		@PathVariable Long postId,
		@PathVariable Long commentId,
		@Valid @RequestBody ReviewCommentDto commentDto,
		@AuthenticationPrincipal UserDetails user) {
		ReviewCommentDto updatedComment = reviewCommentService.updateComment(commentId, commentDto,
			user);
		return ResponseEntity.ok(updatedComment);
	}

	@PreAuthorize("hasAnyRole('USER', 'ADMIN')")
	@DeleteMapping("/{commentId}")
	public ResponseEntity<Void> deleteReviewComment(
		@PathVariable Long postId,
		@PathVariable Long commentId,
		@AuthenticationPrincipal UserDetails user) {
		reviewCommentService.deleteComment(commentId, user);
		return ResponseEntity.noContent().build();
	}

	@PreAuthorize("hasRole('USER')")
	@GetMapping("/me")
	public ResponseEntity<Page<ReviewCommentDto>> getMyReviewComments(
			@AuthenticationPrincipal UserDetails user,
			Pageable pageable,
			@PathVariable("postId") Long postId){
		return ResponseEntity.ok(reviewCommentService.getMyReviewCommentList(user, pageable));
	}

}
