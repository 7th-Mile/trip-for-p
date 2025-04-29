package team.seventhmile.tripforp.domain.freeComment.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import team.seventhmile.tripforp.domain.freeComment.dto.FreeCommentDto;
import team.seventhmile.tripforp.domain.freeComment.dto.GetFreeCommentDto;
import team.seventhmile.tripforp.domain.freeComment.entity.FreeComment;
import team.seventhmile.tripforp.domain.freeComment.repository.FreeCommentRepository;
import team.seventhmile.tripforp.domain.freePost.entity.FreePost;
import team.seventhmile.tripforp.domain.user.entity.Role;
import team.seventhmile.tripforp.domain.user.entity.User;
import team.seventhmile.tripforp.domain.user.repository.UserRepository;
import team.seventhmile.tripforp.global.exception.ResourceNotFoundException;
import team.seventhmile.tripforp.global.exception.UnauthorizedAccessException;

@Service
@Transactional(readOnly = true)
public class FreeCommentService {

    private final FreeCommentRepository freeCommentRepository;
    private final UserRepository userRepository;

    @Autowired
    public FreeCommentService(FreeCommentRepository freeCommentRepository,
        UserRepository userRepository) {
        this.freeCommentRepository = freeCommentRepository;
        this.userRepository = userRepository;
    }

    public Page<GetFreeCommentDto> getCommentsByPost(FreePost freePost, Pageable pageable) {
        Page<FreeComment> comments = freeCommentRepository.findByFreePost(freePost, pageable);
        return comments.map(GetFreeCommentDto::new);
    }

    @Transactional
    public FreeCommentDto createComment(FreePost freePost, FreeCommentDto freeCommentDto,
        UserDetails user) {
        User findUser = getUser(user);
        FreeComment freeComment = mapToEntity(freeCommentDto);
        freeComment.setFreePost(freePost);
        freeComment.setAuthor(findUser);
        FreeComment savedComment = freeCommentRepository.save(freeComment);
        return mapToDto(savedComment);
    }

    @Transactional
    public FreeCommentDto updateComment(Long id, FreeCommentDto updatedCommentDto,
        UserDetails user) {
        User findUser = getUser(user);
        FreeComment freeComment = freeCommentRepository.findById(id)
            .orElseThrow(() -> new ResourceNotFoundException(FreeComment.class, id));
        if (!freeComment.getAuthor().getId().equals(findUser.getId())) {
            throw new UnauthorizedAccessException(FreeComment.class);
        }

        freeComment.setContent(updatedCommentDto.getContent());
        FreeComment updatedComment = freeCommentRepository.save(freeComment);
        return mapToDto(updatedComment);
    }

    @Transactional
    public void deleteComment(Long id, UserDetails user) {
        User findUser = getUser(user);
        FreeComment freeComment = freeCommentRepository.findById(id)
            .orElseThrow(() -> new ResourceNotFoundException(FreeComment.class, id));
        if (!freeComment.getAuthor().getId().equals(findUser.getId()) && !freeComment.getFreePost()
            .getUser().getId().equals(findUser.getId()) && findUser.getRole() != Role.ADMIN) {
            throw new UnauthorizedAccessException(FreeComment.class);
        }
        freeCommentRepository.delete(freeComment);
    }

    private FreeCommentDto mapToDto(FreeComment freeComment) {
        return FreeCommentDto.builder()
            .id(freeComment.getId())
            .content(freeComment.getContent())
            .postId(freeComment.getFreePost().getId())
            .authorId(freeComment.getAuthor().getId())
            .build();
    }

    private FreeComment mapToEntity(FreeCommentDto freeCommentDto) {
        FreeComment freeComment = new FreeComment();
        freeComment.setContent(freeCommentDto.getContent());
        return freeComment;
    }

    private User getUser(UserDetails user) {
        return userRepository.findByEmail(user.getUsername())
            .orElseThrow(() -> new ResourceNotFoundException(User.class));
    }

    @Transactional(readOnly = true)
    public Page<FreeCommentDto> getMyFreeCommentsList(UserDetails user, Pageable pageable) {
        return freeCommentRepository.findByAuthor_Email(user.getUsername(), pageable)
            .map(FreeCommentDto::convertToDto);
    }
}
