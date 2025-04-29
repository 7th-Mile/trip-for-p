package team.seventhmile.tripforp.domain.reviewPost.controller;

import java.io.IOException;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import team.seventhmile.tripforp.domain.reviewPost.dto.ReviewPostDto;
import team.seventhmile.tripforp.domain.reviewPost.service.ReviewPostService;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/review-posts")
public class ReviewPostController {

	private final ReviewPostService reviewPostService;

	@PreAuthorize("hasRole('USER')")
	@PostMapping
	public ReviewPostDto createReviewPost(
		@AuthenticationPrincipal UserDetails user,
		@RequestPart(value = "request") ReviewPostDto reviewPostDto,
		@RequestPart(value = "files", required = false) List<MultipartFile> files
	) throws IOException {
		return reviewPostService.createReviewPost(reviewPostDto, user.getUsername(), files);
	}

	@PreAuthorize("hasRole('USER')")
	@PutMapping("/{id}")
	public ReviewPostDto updateReviewPost(@PathVariable("id") Long id,
		@AuthenticationPrincipal UserDetails user,
		@RequestPart(value = "request") ReviewPostDto reviewPostDto,
		@RequestPart(value = "files", required = false) List<MultipartFile> files
	) throws IOException {
		return reviewPostService.updateReviewPost(id, reviewPostDto, user.getUsername(), files);
	}

	@PreAuthorize("hasAnyRole('USER', 'ADMIN')")
	@DeleteMapping("/{id}")
	public void deleteReviewPost(@PathVariable("id") Long id,
		@AuthenticationPrincipal UserDetails user) {
		reviewPostService.deleteReviewPost(id, user.getUsername());
	}

	@GetMapping
	public Page<ReviewPostDto> getReviewPosts(
		@RequestParam(value = "page", defaultValue = "0") int page,
		@RequestParam(value = "size", defaultValue = "10") int size,
		@RequestParam(value = "keyword", required = false) String keyword) {
		PageRequest pageRequest = PageRequest.of(page, size);
		if (keyword != null && !keyword.trim().isEmpty()) {
			return reviewPostService.getReviewPostSearch(keyword, pageRequest);
		} else {
			return reviewPostService.getAllReviewPost(pageRequest);
		}
	}

	@GetMapping("/{id}")
	public ReviewPostDto getReviewPostDetail(@PathVariable("id") Long id) {
		return reviewPostService.getReviewPostDetail(id);
	}

	@PreAuthorize("hasRole('USER')")
	@GetMapping("/me")
	public ResponseEntity<Page<ReviewPostDto>> getMyReviewPostList(@AuthenticationPrincipal UserDetails user,
																   Pageable pageable){
		return ResponseEntity.ok(reviewPostService.getMyReviewList(user, pageable));
	}
}
