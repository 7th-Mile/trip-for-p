package team.seventhmile.tripforp.domain.magazine.controller;

import java.io.IOException;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.PageRequest;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RequestPart;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;
import team.seventhmile.tripforp.domain.magazine.dto.GetMagazinesResponse;
import team.seventhmile.tripforp.domain.magazine.dto.MagazineDto;
import team.seventhmile.tripforp.domain.magazine.service.MagazineService;
import team.seventhmile.tripforp.global.common.PageResponse;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/magazines")
public class MagazineController {

    private final MagazineService magazineService;

    @PostMapping
    @PreAuthorize("hasRole('ADMIN')")
    public MagazineDto createMagazinePost(
        @AuthenticationPrincipal UserDetails user,
        @RequestPart(value = "request") MagazineDto magazineDto,
        @RequestPart(value = "files") List<MultipartFile> files
    ) throws IOException {
        return magazineService.createMagazinePost(magazineDto, user.getUsername(), files);
    }

    @PatchMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public MagazineDto updateMagazinePost(
        @PathVariable("id") Long id,
        @AuthenticationPrincipal UserDetails user,
        @RequestPart(value = "request") MagazineDto magazineDto,
        @RequestPart(value = "files", required = false) List<MultipartFile> files
    ) throws IOException {
        return magazineService.updateMagazinePost(id, magazineDto, user.getUsername(), files);
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public void deleteMagazinePost(@PathVariable("id") Long id,
        @AuthenticationPrincipal UserDetails user) {
        magazineService.deleteMagazinePost(id, user.getUsername());
    }

    @GetMapping
    public ResponseEntity<PageResponse<GetMagazinesResponse>> getAllMagazineList(
        @RequestParam(value = "page", defaultValue = "0") int page,
        @RequestParam(value = "size", defaultValue = "10") int size,
        @RequestParam(value = "keyword", required = false) String keyword) {
        PageRequest pageRequest = PageRequest.of(page, size);
        return ResponseEntity.ok(magazineService.getMagazineSearch(keyword, pageRequest));
    }

    @GetMapping("/{id}")
    public MagazineDto getMagazineDetail(@PathVariable("id") Long id) {
        return magazineService.getMagazineDetail(id);
    }
}
