package fplhn.udpm.examdistribution.core.headsubject.uploadexampaper.uploadexampaper.repository;

import fplhn.udpm.examdistribution.core.headsubject.uploadexampaper.uploadexampaper.model.response.ListSemesterResponse;
import fplhn.udpm.examdistribution.repository.SemesterRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface UEPSemesterExtendRepository extends SemesterRepository {

    @Query(value = """
            SELECT s.id AS id,
                   CONCAT(s.name,"-",s.year) AS name,
                   s.start_time AS startTime,
                   (
                    SELECT b.end_time
                    FROM block b
                    WHERE b.id_semester = s.id AND
                          b.name = "BLOCK_2"
                   ) AS endTime
            FROM semester s
            WHERE s.status = 0
            """,countQuery = """
            SELECT COUNT(s.id)
            FROM semester s
            WHERE s.status = 0
            """,nativeQuery = true)
    List<ListSemesterResponse> getListSemester();

}
