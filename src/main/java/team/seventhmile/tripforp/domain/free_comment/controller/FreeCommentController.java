package team.seventhmile.tripforp.domain.free_comment.controller;

import jakarta.validation.Valid;
import java.util.List;
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
import team.seventhmile.tripforp.domain.free_comment.dto.FreeCommentDto;
import team.seventhmile.tripforp.domain.free_comment.dto.GetFreeCommentDto;
import team.seventhmile.tripforp.domain.free_comment.service.FreeCommentService;
import team.seventhmile.tripforp.domain.free_post.entity.FreePost;
import team.seventhmile.tripforp.domain.free_post.service.FreePostService;
import team.seventhmile.tripforp.domain.user.entity.User;

@RestController
@RequestMapping("/api/free-posts/{postId}/comments")
public class FreeCommentController {

	private final FreeCommentService freeCommentService;
	private final FreePostService freePostService;

	@Autowired
	public FreeCommentController(FreeCommentService freeCommentService,
		FreePostService freePostService) {
		this.freeCommentService = freeCommentService;
		this.freePostService = freePostService;
	}

	@GetMapping
	public ResponseEntity<Page<GetFreeCommentDto>> getFreeComments(
		@PathVariable Long postId,
		Pageable pageable
	) {
		FreePost freePost = freePostService.getFreePostEntity(postId);
		Page<GetFreeCommentDto> freeComments = freeCommentService.getCommentsByPost(freePost, pageable);
		return ResponseEntity.ok(freeComments);
	}

	@PreAuthorize("hasRole('USER')")
	@PostMapping
	public ResponseEntity<FreeCommentDto> createFreeComment(
		@PathVariable Long postId,
		@Valid @RequestBody FreeCommentDto commentDto,
		@AuthenticationPrincipal UserDetails user) {
		FreePost freePost = freePostService.getFreePostEntity(postId);
		FreeCommentDto createdComment = freeCommentService.createComment(freePost, commentDto,
			user);
		return ResponseEntity.status(HttpStatus.CREATED).body(createdComment);
	}

	@PreAuthorize("hasRole('USER')")
	@PutMapping("/{commentId}")
	public ResponseEntity<FreeCommentDto> updateFreeComment(
		@PathVariable Long postId,
		@PathVariable Long commentId,
		@Valid @RequestBody FreeCommentDto commentDto,
		@AuthenticationPrincipal UserDetails user) {
		FreeCommentDto updatedComment = freeCommentService.updateComment(commentId, commentDto,
			user);
		return ResponseEntity.ok(updatedComment);
	}

	@PreAuthorize("hasAnyRole('USER', 'ADMIN')")
	@DeleteMapping("/{commentId}")
	public ResponseEntity<Void> deleteFreeComment(
		@PathVariable Long postId,
		@PathVariable Long commentId,
		@AuthenticationPrincipal UserDetails user) {
		freeCommentService.deleteComment(commentId, user);
		return ResponseEntity.noContent().build();
	}

}
