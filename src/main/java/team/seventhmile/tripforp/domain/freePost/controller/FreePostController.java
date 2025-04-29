package team.seventhmile.tripforp.domain.freePost.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;
import team.seventhmile.tripforp.domain.freePost.dto.FreePostDto;
import team.seventhmile.tripforp.domain.freePost.service.FreePostService;


@RestController
@RequiredArgsConstructor
@RequestMapping("/api/free-posts")
public class FreePostController {

	private final FreePostService freePostService;

	@PreAuthorize("hasRole('USER')")
	@PostMapping
	public FreePostDto createFreePost(
		@AuthenticationPrincipal UserDetails user,
		@RequestBody FreePostDto freePostDto) {
		return freePostService.createFreePost(freePostDto, user.getUsername());
	}

	@PreAuthorize("hasRole('USER')")
	@PutMapping("/{id}")
	public FreePostDto updateFreePost(@PathVariable("id") Long id,
		@RequestBody FreePostDto freePostDto,
		@AuthenticationPrincipal UserDetails user) {
		return freePostService.updateFreePost(id, freePostDto, user.getUsername());
	}

	@PreAuthorize("hasAnyRole('USER', 'ADMIN')")
	@DeleteMapping("/{id}")
	public void deleteFreePost(@PathVariable("id") Long id,
		@AuthenticationPrincipal UserDetails user) {
		freePostService.deleteFreePost(id, user.getUsername());
	}

	@GetMapping
	public Page<FreePostDto> getFreePosts(
		@RequestParam(value = "page", defaultValue = "0") int page,
		@RequestParam(value = "size", defaultValue = "10") int size,
		@RequestParam(value = "keyword", required = false) String keyword) {
		PageRequest pageRequest = PageRequest.of(page, size);
		if (keyword != null && !keyword.trim().isEmpty()) {
			return freePostService.getFreePostSearch(keyword, pageRequest);
		} else {
			return freePostService.getAllFreePost(pageRequest);
		}
	}

	@GetMapping("/{id}")
	public FreePostDto getFreePostDetail(@PathVariable("id") Long id) {
		return freePostService.getFreePostDetail(id);
	}

	@PreAuthorize("hasRole('USER')")
	@GetMapping("/me")
	public ResponseEntity<Page<FreePostDto>> getMyFreePostList(
			@AuthenticationPrincipal UserDetails user,
			Pageable pageable) {
		return ResponseEntity.ok(freePostService.getMyFreePostList(user, pageable));
	}
}

