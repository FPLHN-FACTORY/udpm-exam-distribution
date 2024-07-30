package fplhn.udpm.examdistribution.infrastructure.config.job.sharepermissionexampaper.repository;

import fplhn.udpm.examdistribution.entity.SharePermissionExamPaper;
import fplhn.udpm.examdistribution.repository.SharePermissionExamPaperRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface SPEPSharePermissionExamPaperExtendRepository extends SharePermissionExamPaperRepository {

    @Query("""
            SELECT spep
            FROM SharePermissionExamPaper spep
            WHERE spep.block.id = :blockId AND
                  spep.facility.id = :facilityId
            """)
    List<SharePermissionExamPaper> listSharePermissionExamPaper(String blockId,String facilityId);

}
