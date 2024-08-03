package fplhn.udpm.examdistribution.core.headdepartment.classsubject.repository;

import fplhn.udpm.examdistribution.core.headdepartment.classsubject.model.request.SemesterRequest;
import fplhn.udpm.examdistribution.core.headdepartment.classsubject.model.response.SemesterResponse;
import fplhn.udpm.examdistribution.entity.Semester;
import fplhn.udpm.examdistribution.infrastructure.constant.SemesterName;
import fplhn.udpm.examdistribution.repository.SemesterRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface HDSemesterClassSubjectRepository extends SemesterRepository {

    Optional<Semester> findBySemesterNameAndYear(SemesterName semesterName, Integer year);

    @Query(value = """
            SELECT
            	s.id AS 'semesterId',
             	s.name AS 'semesterName'
            FROM
            	semester s
            WHERE
            	s.name LIKE :#{#request.semesterName}
            	AND s.`year` = :#{#request.year}
            """, nativeQuery = true)
    Optional<SemesterResponse> findByNameAndYear(SemesterRequest request);

}
