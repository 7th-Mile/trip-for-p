package team.seventhmile.tripforp.external.ai.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import team.seventhmile.tripforp.external.ai.service.AiService;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/ai")
public class AiController {

    private final AiService aiService;

    @PostMapping
    @PreAuthorize("hasAnyRole('USER', 'ADMIN')")
    public ResponseEntity<String> generateContent(
        @RequestBody String prompt
    ) {
        return ResponseEntity.ok(aiService.generateContent(prompt));
    }

    @GetMapping("chance")
    public ResponseEntity<Long> getChance(
        @AuthenticationPrincipal UserDetails userDetails
    ) {
        return ResponseEntity.ok(aiService.getChance(userDetails));
    }
}
