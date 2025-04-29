package team.seventhmile.tripforp.domain.magazine.dto;

import com.querydsl.core.annotations.QueryProjection;
import java.time.ZonedDateTime;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
public class GetMagazinesResponse {

    private Long id;
    private String title;
    private String content;
    private Integer views;
    private ZonedDateTime createdAt;
    private String fileUrl;

    @QueryProjection
    public GetMagazinesResponse(Long id, String title, String content, Integer views,
        ZonedDateTime createdAt, String fileUrl) {
        this.id = id;
        this.title = title;
        this.content = content;
        this.views = views;
        this.createdAt = createdAt;
        this.fileUrl = fileUrl;
    }
}
