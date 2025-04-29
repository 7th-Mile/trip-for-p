package team.seventhmile.tripforp.domain.freePost.entity;

import jakarta.persistence.*;
import java.util.ArrayList;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.hibernate.annotations.ColumnDefault;
import team.seventhmile.tripforp.domain.freeComment.entity.FreeComment;
import team.seventhmile.tripforp.domain.user.entity.User;
import team.seventhmile.tripforp.global.common.BaseEntity;

import java.util.List;

@Entity
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Table(name = "free_posts")
@Builder
public class FreePost extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "free_post_id")
    private Long id;

    @ManyToOne
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    @Column(nullable = false)
    private String content;

    @Column(nullable = false)
    @ColumnDefault("0")
    private Integer views;

    @OneToMany(mappedBy = "freePost", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<FreeComment> comments = new ArrayList<>();

    // 수정 로직
    public void update(String content) {
        validateField(content, "Content");

        this.content = content;
    }

    // 조회 수 증가 로직
    public void incrementViews() {
        this.views += 1;
    }

    // Not Null 예외 처리 로직
    public static void validateField(String field, String fieldName) {
        if (field == null || field.isEmpty()) {
            throw new IllegalArgumentException(fieldName + "은(는) 필수 입력 항목입니다.");
        }
    }
}

