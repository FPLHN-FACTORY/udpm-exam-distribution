package fplhn.udpm.examdistribution.core.common.repository;

import fplhn.udpm.examdistribution.core.common.model.SemesterInfoResponse;
import fplhn.udpm.examdistribution.repository.SemesterRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface CMSemesterExtendRepository extends SemesterRepository {

    @Query(
            value = """
                    SELECT
                        s.id AS id,
                        CONCAT(s.name, ' - ', s.year) AS semesterInfo
                    FROM
                        semester s
                    ORDER BY s.start_time DESC
                    """,
            nativeQuery = true
    )
    List<SemesterInfoResponse> getSemesterInfos();

}
