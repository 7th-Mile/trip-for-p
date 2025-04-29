package team.seventhmile.tripforp.domain.freeComment.dto;

import java.time.ZonedDateTime;
import lombok.Getter;
import lombok.NoArgsConstructor;
import team.seventhmile.tripforp.domain.freeComment.entity.FreeComment;

@NoArgsConstructor
@Getter
public class GetFreeCommentDto {

    private Long id;
    private String author;
    private String content;
    private ZonedDateTime createdAt;

    public GetFreeCommentDto(FreeComment freeComment) {
        this.id = freeComment.getId();
        this.author = freeComment.getAuthor().getNickname();
        this.content = freeComment.getContent();
        this.createdAt = freeComment.getCreatedAt();
    }
}
