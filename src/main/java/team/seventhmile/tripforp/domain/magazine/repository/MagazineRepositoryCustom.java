package team.seventhmile.tripforp.domain.magazine.repository;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import team.seventhmile.tripforp.domain.magazine.dto.GetMagazinesResponse;

public interface MagazineRepositoryCustom {

	Page<GetMagazinesResponse> getMagazineKeywordContaining(String keyword, Pageable pageable);

}
