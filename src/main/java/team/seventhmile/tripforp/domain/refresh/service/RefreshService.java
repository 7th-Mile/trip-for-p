package team.seventhmile.tripforp.domain.refresh.service;

public interface RefreshService {

    void saveRefreshToken(String username, String refreshToken, long expiration);
    String getRefreshToken(String username);
    void deleteRefreshToken(String username);
    boolean isKeyExists(String username);

}
