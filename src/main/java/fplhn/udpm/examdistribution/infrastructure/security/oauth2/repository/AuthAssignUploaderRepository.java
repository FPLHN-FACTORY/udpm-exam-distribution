package fplhn.udpm.examdistribution.infrastructure.security.oauth2.repository;

import fplhn.udpm.examdistribution.entity.AssignUploader;
import fplhn.udpm.examdistribution.repository.AssignUploaderRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface AuthAssignUploaderRepository extends AssignUploaderRepository {

    @Query("""
            SELECT au
            FROM AssignUploader au
            WHERE
                au.staff.id = :userId AND
                au.semester.id = :semesterId
            ORDER BY au.createdDate LIMIT 1
            """)
    Optional<AssignUploader> findByStaffId(String userId, String semesterId);

}
