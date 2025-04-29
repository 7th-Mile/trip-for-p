package team.seventhmile.tripforp.global.common;

import java.util.List;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.springframework.data.domain.Page;

@Getter
@NoArgsConstructor
public class PageResponse<T> {

    private int totalPages;
    private long totalElements;
    private List<T> content;

    public PageResponse(Page<T> page) {
        this.totalPages = page.getTotalPages();
        this.totalElements = page.getTotalElements();
        this.content = page.getContent();
    }
}
