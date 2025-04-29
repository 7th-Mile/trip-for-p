package team.seventhmile.tripforp.domain.freePost.service;

import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import team.seventhmile.tripforp.domain.freePost.dto.FreePostDto;
import team.seventhmile.tripforp.domain.freePost.entity.FreePost;
import team.seventhmile.tripforp.domain.freePost.repository.FreePostRepository;
import team.seventhmile.tripforp.domain.user.entity.Role;
import team.seventhmile.tripforp.domain.user.entity.User;
import team.seventhmile.tripforp.domain.user.repository.UserRepository;
import team.seventhmile.tripforp.global.exception.ResourceNotFoundException;
import team.seventhmile.tripforp.global.exception.UnauthorizedAccessException;


@Service
@Transactional(readOnly = true)
@RequiredArgsConstructor
public class FreePostService {

	private final FreePostRepository freePostRepository;
	private final UserRepository userRepository;

	@Transactional
	public FreePostDto createFreePost(FreePostDto freePostDto, String userEmail) {

		User user = userRepository.findByEmail(userEmail)
			.orElseThrow(() -> new ResourceNotFoundException(User.class));

		FreePost freePost = freePostDto.convertToEntity(user);
		freePostRepository.save(freePost);

		return FreePostDto.convertToDto(freePost);
	}

	@Transactional
	public FreePostDto updateFreePost(Long id, FreePostDto freePostDto, String userEmail) {

		FreePost freePost = freePostRepository.findById(id)
			.orElseThrow(() -> new ResourceNotFoundException(FreePost.class));

		User user = userRepository.findByEmail(userEmail)
			.orElseThrow(() -> new ResourceNotFoundException(User.class));

		if (!freePost.getUser().getEmail().equals(userEmail)) {
			throw new UnauthorizedAccessException(FreePost.class);
		}

		// 업데이트
		freePost.update(freePostDto.getContent());

		freePostRepository.save(freePost);

		return FreePostDto.convertToDto(freePost);
	}

	@Transactional
	public void deleteFreePost(Long id, String userEmail) {

		FreePost freePost = freePostRepository.findById(id)
			.orElseThrow(() -> new ResourceNotFoundException(FreePost.class));

		User user = userRepository.findByEmail(userEmail)
			.orElseThrow(() -> new ResourceNotFoundException(User.class));

		if (!freePost.getUser().getEmail().equals(userEmail)
			&& user.getRole() != Role.ADMIN) {
			throw new UnauthorizedAccessException(FreePost.class);
		}

		freePostRepository.delete(freePost);
	}

	@Transactional(readOnly = true)
	public Page<FreePostDto> getAllFreePost(Pageable pageable) {

		return freePostRepository.getFreePosts(pageable)
			.map(FreePostDto::convertToDto);

	}

	@Transactional
	public FreePostDto getFreePostDetail(Long id) {
		FreePost freePost = freePostRepository.findById(id)
			.orElseThrow(() -> new ResourceNotFoundException(FreePost.class));

		freePost.incrementViews();

		return FreePostDto.convertToDto(freePost);
	}

	@Transactional(readOnly = true)
	public Page<FreePostDto> getFreePostSearch(String keyword, Pageable pageable) {

		if (keyword == null || keyword.trim().isEmpty()) {
			return Page.empty(pageable);
		}

		Page<FreePost> freePosts = freePostRepository.getFreePostKeywordContaining(keyword.trim(),
			pageable);
		return freePosts.map(FreePostDto::convertToDto);

	}

	@Transactional(readOnly = true)
	public FreePost getFreePostEntity(Long id) {
		return freePostRepository.findById(id)
			.orElseThrow(() -> new ResourceNotFoundException(FreePost.class, id));
	}

	@Transactional(readOnly = true)
	public Page<FreePostDto> getMyFreePostList(UserDetails user, Pageable pageable) {
		return freePostRepository.findByUserEmailOrderByCreatedAtDesc(user.getUsername(), pageable)
				.map(FreePostDto::convertToDto);
	}

}
