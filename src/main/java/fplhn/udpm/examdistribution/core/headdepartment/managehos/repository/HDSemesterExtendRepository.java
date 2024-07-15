package fplhn.udpm.examdistribution.core.headdepartment.managehos.repository;

import fplhn.udpm.examdistribution.core.headdepartment.managehos.model.response.SemesterInfoResponse;
import fplhn.udpm.examdistribution.repository.SemesterRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface HDSemesterExtendRepository extends SemesterRepository {

    @Query(
            value = """
                    SELECT
                        s.id AS id,
                        CONCAT(s.name, ' - ', s.year) AS semesterInfo
                    FROM
                        semester s
                    """,
            nativeQuery = true
    )
    List<SemesterInfoResponse> getSemesterInfos();

}
