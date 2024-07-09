package fplhn.udpm.examdistribution.core.teacher.mockexampaper.repository;

import fplhn.udpm.examdistribution.core.teacher.mockexampaper.model.response.TSemesterResponse;
import fplhn.udpm.examdistribution.repository.SemesterRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface TSemesterRepository extends SemesterRepository {

    @Query(value = """
                SELECT s.id AS id,
                       CONCAT(s.name,' - ',s.year) AS name,
                       s.start_time AS startTime
                FROM semester s
                ORDER BY s.start_time DESC
            """,nativeQuery = true)
    List<TSemesterResponse> getSemester();

}
