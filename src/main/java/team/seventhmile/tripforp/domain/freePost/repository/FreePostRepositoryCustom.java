package team.seventhmile.tripforp.domain.freePost.repository;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import team.seventhmile.tripforp.domain.freePost.entity.FreePost;

public interface FreePostRepositoryCustom {

    Page<FreePost> getFreePosts(Pageable pageable);

    Page<FreePost> getFreePostKeywordContaining(String keyword, Pageable pageable);

}
