package fplhn.udpm.examdistribution.core.headsubject.uploadexampaper.uploadexampaper.repository;

import fplhn.udpm.examdistribution.entity.SharePermissionExamPaper;
import fplhn.udpm.examdistribution.repository.SharePermissionExamPaperRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface UEPSharePermissionExamPaperExtendRepository extends SharePermissionExamPaperRepository {

    @Query("""
            SELECT spep
            FROM SharePermissionExamPaper spep
            WHERE spep.block.id = :blockId AND
                  spep.facility.id = :facilityId AND
                  spep.staff.id = :staffId AND
                  spep.examPaper.id = :examPaperId
            """)
    Optional<SharePermissionExamPaper> findByExamPaperIdAndStaffId(String examPaperId,String staffId, String blockId, String facilityId);

    @Query("""
            SELECT spep
            FROM SharePermissionExamPaper spep
            WHERE spep.examPaper.id = :examPaperId AND
                  spep.block.id = :blockId AND
                  spep.facility.id = :facilityId
            """)
    List<SharePermissionExamPaper> getListSharePermissionExamPaperByExamPaperId(String examPaperId, String blockId, String facilityId);

}
