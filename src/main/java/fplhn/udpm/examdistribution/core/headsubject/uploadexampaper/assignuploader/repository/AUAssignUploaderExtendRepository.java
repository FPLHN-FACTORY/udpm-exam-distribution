package fplhn.udpm.examdistribution.core.headsubject.uploadexampaper.assignuploader.repository;

import fplhn.udpm.examdistribution.core.headsubject.uploadexampaper.assignuploader.model.request.AssignUploaderRequest;
import fplhn.udpm.examdistribution.entity.AssignUploader;
import fplhn.udpm.examdistribution.repository.AssignUploaderRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface AUAssignUploaderExtendRepository extends AssignUploaderRepository {

    @Query("""
            SELECT au
            FROM AssignUploader au
            WHERE au.staff.id = :#{#request.staffId} AND au.subject.id = :#{#request.subjectId}
            """)
    Optional<AssignUploader> isAssignUploaderExist(AssignUploaderRequest request);

}
