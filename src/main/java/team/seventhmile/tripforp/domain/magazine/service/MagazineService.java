package team.seventhmile.tripforp.domain.magazine.service;

import java.io.IOException;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;
import team.seventhmile.tripforp.domain.file.entity.MagazineFile;
import team.seventhmile.tripforp.domain.file.service.MagazineFileService;
import team.seventhmile.tripforp.domain.magazine.dto.GetMagazinesResponse;
import team.seventhmile.tripforp.domain.magazine.dto.MagazineDto;
import team.seventhmile.tripforp.domain.magazine.entity.Magazine;
import team.seventhmile.tripforp.domain.magazine.repository.MagazineRepository;
import team.seventhmile.tripforp.domain.user.entity.Role;
import team.seventhmile.tripforp.domain.user.entity.User;
import team.seventhmile.tripforp.domain.user.repository.UserRepository;
import team.seventhmile.tripforp.global.common.PageResponse;
import team.seventhmile.tripforp.global.exception.ResourceNotFoundException;
import team.seventhmile.tripforp.global.exception.UnauthorizedAccessException;

@Service
@Transactional(readOnly = true)
@RequiredArgsConstructor
public class MagazineService {

    private final MagazineRepository magazineRepository;
    private final UserRepository userRepository;
    private final MagazineFileService magazineFileService;

    @Transactional
    @CacheEvict(value = "magazines", allEntries = true)
    public MagazineDto createMagazinePost(MagazineDto magazineDto, String userEmail,
        List<MultipartFile> files) throws IOException {

        User user = userRepository.findByEmail(userEmail)
            .orElseThrow(() -> new ResourceNotFoundException(User.class));

        if (user.getRole() != Role.ADMIN) {
            throw new UnauthorizedAccessException(Magazine.class);
        }

        Magazine magazine = magazineDto.convertToEntity(user);

        if (files != null && !files.isEmpty()) {
            for (MultipartFile file : files) {
                MagazineFile magazineFile = magazineFileService.saveFile(file);
                magazine.addFile(magazineFile);
            }
        }
        magazineRepository.save(magazine);

        return MagazineDto.convertToDto(magazine);
    }

    @Transactional
    @CacheEvict(value = "magazines", allEntries = true)
    public MagazineDto updateMagazinePost(Long id, MagazineDto magazineDto, String userEmail,
        List<MultipartFile> files) throws IOException {

        User user = userRepository.findByEmail(userEmail)
            .orElseThrow(() -> new ResourceNotFoundException(User.class));

        if (user.getRole() != Role.ADMIN) {
            throw new UnauthorizedAccessException(Magazine.class);
        }

        Magazine magazine = magazineRepository.findById(id)
            .orElseThrow(() -> new ResourceNotFoundException(Magazine.class));

        magazine.update(magazineDto.getTitle(), magazineDto.getContent());

        if (files != null && !files.isEmpty()) {
            if (magazine.getFiles() != null) {
                for (MagazineFile file : magazine.getFiles()) {
                    magazineFileService.deleteFile(file.getFileName());
                }
            }
            magazine.clearFile();
            for (MultipartFile file : files) {
                MagazineFile magazineFile = magazineFileService.saveFile(file);
                magazine.addFile(magazineFile);
            }
        }

        return MagazineDto.convertToDto(magazine);
    }

    @Transactional
    @CacheEvict(value = "magazines", allEntries = true)
    public void deleteMagazinePost(Long id, String userEmail) {

        Magazine magazine = magazineRepository.findById(id)
            .orElseThrow(() -> new ResourceNotFoundException(Magazine.class));

        User user = userRepository.findByEmail(userEmail)
            .orElseThrow(() -> new ResourceNotFoundException(User.class));

        if (user.getRole() != Role.ADMIN) {
            throw new UnauthorizedAccessException(Magazine.class);
        }

        if (magazine.getFiles() != null) {
            for (MagazineFile file : magazine.getFiles()) {
                magazineFileService.deleteFile(file.getFileName());
            }
        }

        magazineRepository.delete(magazine);
    }

    @Transactional
    public MagazineDto getMagazineDetail(Long id) {
        Magazine magazine = magazineRepository.findById(id)
            .orElseThrow(() -> new ResourceNotFoundException(Magazine.class));

        magazine.incrementViews();

        return MagazineDto.convertToDto(magazine);
    }

    @Transactional(readOnly = true)
    @Cacheable(value = "magazines")
    public PageResponse<GetMagazinesResponse> getMagazineSearch(String keyword, Pageable pageable) {

        return new PageResponse<>(magazineRepository.getMagazineKeywordContaining(
            keyword, pageable));
    }
}
