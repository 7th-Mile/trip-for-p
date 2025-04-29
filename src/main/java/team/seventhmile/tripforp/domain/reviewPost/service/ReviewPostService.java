package team.seventhmile.tripforp.domain.reviewPost.service;

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
import team.seventhmile.tripforp.domain.course.entity.Course;
import team.seventhmile.tripforp.domain.course.repository.CourseRepository;
import team.seventhmile.tripforp.domain.reviewPost.dto.ReviewPostDto;
import team.seventhmile.tripforp.domain.reviewPost.entity.ReviewPost;
import team.seventhmile.tripforp.domain.reviewPost.repository.ReviewPostRepository;
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
	private final CourseRepository courseRepository;
	private final UserRepository userRepository;
	private final ReviewFileService reviewFileService;

	@Transactional
	public ReviewPostDto createReviewPost(ReviewPostDto reviewPostDto, String userEmail,
		List<MultipartFile> files) throws IOException {

		User user = userRepository.findByEmail(userEmail)
			.orElseThrow(() -> new ResourceNotFoundException(User.class));

		Course course = courseRepository.findByIdAndCreator(reviewPostDto.getCourseId(), user)
			.orElseThrow(() -> new ResourceNotFoundException(Course.class));

		ReviewPost reviewPost = reviewPostDto.convertToEntity(user, course);

		reviewPostRepository.save(reviewPost);
		if (files != null && !files.isEmpty()) {
			for (MultipartFile file : files) {
				ReviewFile reviewFile = reviewFileService.saveFile(file);
				reviewPost.addFile(reviewFile);
			}
		}

		return ReviewPostDto.convertToDto(reviewPost);
	}

	@Transactional
	public ReviewPostDto updateReviewPost(Long id,
		ReviewPostDto reviewPostDto, String userEmail, List<MultipartFile> files) throws IOException {

		ReviewPost reviewPost = reviewPostRepository.findById(id)
			.orElseThrow(() -> new ResourceNotFoundException(ReviewPost.class));

		User user = userRepository.findByEmail(userEmail)
			.orElseThrow(() -> new ResourceNotFoundException(User.class));

		if (!reviewPost.getUser().getEmail().equals(userEmail)) {
			throw new UnauthorizedAccessException(ReviewPost.class);
		}

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

	@Transactional
	public void deleteReviewPost(Long id, String userEmail) {

		ReviewPost reviewPost = reviewPostRepository.findById(id)
			.orElseThrow(() -> new ResourceNotFoundException(ReviewPost.class));

		User user = userRepository.findByEmail(userEmail)
			.orElseThrow(() -> new ResourceNotFoundException(User.class));

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

	@Transactional(readOnly = true)
	public Page<ReviewPostDto> getAllReviewPost(Pageable pageable) {

		return reviewPostRepository.getReviewPosts(pageable)
			.map(ReviewPostDto::convertToDto);

	}

	@Transactional
	public ReviewPostDto getReviewPostDetail(Long id) {
		ReviewPost reviewPost = reviewPostRepository.findById(id)
			.orElseThrow(() -> new ResourceNotFoundException(ReviewPost.class));

		reviewPost.incrementViews();

		return ReviewPostDto.convertToDto(reviewPost);
	}

	@Transactional(readOnly = true)
	public Page<ReviewPostDto> getReviewPostSearch(String keyword, Pageable pageable) {

		if (keyword == null || keyword.trim().isEmpty()) {
			return Page.empty(pageable);
		}

		Page<ReviewPost> reviewPosts = reviewPostRepository.getReviewPostKeywordContaining(
			keyword.trim(), pageable);
		return reviewPosts.map(ReviewPostDto::convertToDto);

	}

	@Transactional(readOnly = true)
	public ReviewPost getReviewPostEntity(Long id) {
		return reviewPostRepository.findById(id)
			.orElseThrow(() -> new ResourceNotFoundException(ReviewPost.class));
	}

	@Transactional(readOnly = true)
	public Page<ReviewPostDto> getMyReviewList(UserDetails user, Pageable pageable) {
		return reviewPostRepository.getMyReviews(user.getUsername(), pageable).map(ReviewPostDto::convertToDto);
	}
}
