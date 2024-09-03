package team.seventhmile.tripforp.domain.file.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import team.seventhmile.tripforp.domain.file.entity.File;

@Repository
public interface FileRepository extends JpaRepository<File, Long> {

}