package team.seventhmile.tripforp.domain.refresh.service;

import java.util.Date;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Primary;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import team.seventhmile.tripforp.domain.refresh.entity.Refresh;
import team.seventhmile.tripforp.domain.refresh.repository.RefreshRepository;
import team.seventhmile.tripforp.global.exception.ResourceNotFoundException;

@Service
@Transactional(readOnly = true)
@RequiredArgsConstructor
@Primary
public class DBRefreshService implements RefreshService {

    private final RefreshRepository refreshRepository;

    @Override
    @Transactional
    public void saveRefreshToken(String username, String refreshToken, long expiration) {
        deleteRefreshToken(username);
        Refresh refresh = Refresh.builder()
            .username(username)
            .token(refreshToken)
            .expiration(new Date(System.currentTimeMillis() + expiration).toString())
            .build();
        refreshRepository.save(refresh);
    }

    @Override
    public String getRefreshToken(String username) {
        Refresh refresh = refreshRepository.findByUsername(username)
            .orElseThrow(() -> new ResourceNotFoundException(Refresh.class));
        return refresh.getToken();
    }

    @Override
    @Transactional
    public void deleteRefreshToken(String username) {
        refreshRepository.deleteByUsername(username);
    }

    @Override
    public boolean isKeyExists(String username) {
        return refreshRepository.existsByUsername(username);
    }
}
