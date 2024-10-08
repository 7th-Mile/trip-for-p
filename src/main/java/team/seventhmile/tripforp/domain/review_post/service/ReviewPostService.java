package team.seventhmile.tripforp.domain.review_post.service;

import java.io.IOException;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;
import team.seventhmile.tripforp.domain.file.entity.ReviewFile;
import team.seventhmile.tripforp.domain.file.service.ReviewFileService;
import team.seventhmile.tripforp.domain.plan.entity.Plan;
import team.seventhmile.tripforp.domain.plan.repository.PlanRepository;
import team.seventhmile.tripforp.domain.review_post.dto.ReviewPostDto;
import team.seventhmile.tripforp.domain.review_post.entity.ReviewPost;
import team.seventhmile.tripforp.domain.review_post.repository.ReviewPostRepository;
import team.seventhmile.tripforp.domain.user.entity.Role;
import team.seventhmile.tripforp.domain.user.entity.User;
import team.seventhmile.tripforp.domain.user.repository.UserRepository;
import team.seventhmile.tripforp.global.exception.ResourceNotFoundException;
import team.seventhmile.tripforp.global.exception.UnauthorizedAccessException;

@Service
@Transactional(readOnly = true)
@RequiredArgsConstructor
public class ReviewPostService {

	private final ReviewPostRepository reviewPostRepository;
	private final PlanRepository planRepository;
	private final UserRepository userRepository;
	private final ReviewFileService reviewFileService;

	// 리뷰 게시글 작성
	@Transactional
	public ReviewPostDto createReviewPost(ReviewPostDto reviewPostDto, String userEmail,
		List<MultipartFile> files) throws IOException {

		// 현재 로그인된 사용자 가져오기
		User user = userRepository.findByEmail(userEmail)
			.orElseThrow(() -> new ResourceNotFoundException(User.class));

		// Plan 가져오기
		Plan plan = planRepository.findByIdAndUser(reviewPostDto.getPlanId(), user)
			.orElseThrow(() -> new ResourceNotFoundException(Plan.class));

		// 리뷰 게시글 생성
		ReviewPost reviewPost = reviewPostDto.convertToEntity(user, plan);

		// 첨부 파일 처리
		if (files != null && !files.isEmpty()) {
			for (MultipartFile file : files) {
				ReviewFile reviewFile = reviewFileService.saveFile(file);
				reviewPost.addFile(reviewFile);
			}
		}

		reviewPostRepository.save(reviewPost);

		return ReviewPostDto.convertToDto(reviewPost);
	}

	// 리뷰 게시글 수정
	@Transactional
	public ReviewPostDto updateReviewPost(Long id,
		ReviewPostDto reviewPostDto, String userEmail, List<MultipartFile> files) throws IOException {

		ReviewPost reviewPost = reviewPostRepository.findById(id)
			.orElseThrow(() -> new ResourceNotFoundException(ReviewPost.class));

		// 현재 로그인된 사용자 가져오기
		User user = userRepository.findByEmail(userEmail)
			.orElseThrow(() -> new ResourceNotFoundException(User.class));

		// 작성자 확인
		if (!reviewPost.getUser().getEmail().equals(userEmail)) {
			throw new UnauthorizedAccessException(ReviewPost.class);
		}

		// 업데이트
		reviewPost.update(reviewPostDto.getTitle(), reviewPostDto.getContent());
		if (reviewPost.getFiles() != null) {
			for (ReviewFile file : reviewPost.getFiles()) {
				reviewFileService.deleteFile(file.getFileName());
			}
		}
		reviewPost.clearFile();

		if (files != null && !files.isEmpty()) {
			for (MultipartFile file : files) {
				ReviewFile reviewFile = reviewFileService.saveFile(file);
				reviewPost.addFile(reviewFile);
			}
		}

		return reviewPostDto.convertToDto(reviewPost);
	}

	// 리뷰 게시글 삭제
	@Transactional
	public void deleteReviewPost(Long id, String userEmail) {

		ReviewPost reviewPost = reviewPostRepository.findById(id)
			.orElseThrow(() -> new ResourceNotFoundException(ReviewPost.class));

		// 현재 로그인된 사용자 가져오기
		User user = userRepository.findByEmail(userEmail)
			.orElseThrow(() -> new ResourceNotFoundException(User.class));

		// 권한 확인 (작성자 또는 ADMIN)
		if (!reviewPost.getUser().getEmail().equals(userEmail) && user.getRole() != Role.ADMIN) {
			throw new UnauthorizedAccessException(ReviewPost.class);
		}

		if (reviewPost.getFiles() != null) {
			for (ReviewFile file : reviewPost.getFiles()) {
				reviewFileService.deleteFile(file.getFileName());
			}
		}

		reviewPostRepository.delete(reviewPost);
	}

	// 리뷰 게시글 목록 조회
	@Transactional(readOnly = true)
	public Page<ReviewPostDto> getAllReviewPost(Pageable pageable) {

		return reviewPostRepository.getReviewPosts(pageable)
			.map(ReviewPostDto::convertToDto);

	}

	// 리뷰 게시글 상세 조회
	@Transactional
	public ReviewPostDto getReviewPostDetail(Long id) {
		ReviewPost reviewPost = reviewPostRepository.findById(id)
			.orElseThrow(() -> new ResourceNotFoundException(ReviewPost.class));

		// 조회 수 증가
		reviewPost.incrementViews();

		return ReviewPostDto.convertToDto(reviewPost);
	}

	// 리뷰 게시글 검색(제목, 내용) 조회
	@Transactional(readOnly = true)
	public Page<ReviewPostDto> getReviewPostSearch(String keyword, Pageable pageable) {

		if (keyword == null || keyword.trim().isEmpty()) {
			return Page.empty(pageable);
		}

		Page<ReviewPost> reviewPosts = reviewPostRepository.getReviewPostKeywordContaining(
			keyword.trim(), pageable);
		return reviewPosts.map(ReviewPostDto::convertToDto);

	}

	// ReviewPost 엔티티 조회
	@Transactional(readOnly = true)
	public ReviewPost getReviewPostEntity(Long id) {
		return reviewPostRepository.findById(id)
			.orElseThrow(() -> new ResourceNotFoundException(ReviewPost.class));
	}

	//[마이페이지] 내가 작성한 리뷰게시글 목록 조회
	@Transactional(readOnly = true)
	public Page<ReviewPostDto> getMyReviewList(UserDetails user, Pageable pageable) {
		return reviewPostRepository.getMyReviews(user.getUsername(), pageable).map(ReviewPostDto::convertToDto);
	}
}
