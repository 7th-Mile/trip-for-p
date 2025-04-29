package team.seventhmile.tripforp.domain.refresh.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Table(name = "refreshes")
public class Refresh {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "refresh_id")
    private Long id;

    private String username;
    @Column(length = 500)
    private String token;
    private String expiration;

    @Builder
    public Refresh(String username, String token, String expiration) {
        this.username = username;
        this.token = token;
        this.expiration = expiration;
    }
}
