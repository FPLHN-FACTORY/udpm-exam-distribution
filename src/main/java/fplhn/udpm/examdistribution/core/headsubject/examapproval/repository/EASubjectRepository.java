package fplhn.udpm.examdistribution.core.headsubject.examapproval.repository;

import fplhn.udpm.examdistribution.core.headsubject.examapproval.model.response.EASubjectResponse;
import fplhn.udpm.examdistribution.repository.SubjectRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface EASubjectRepository extends SubjectRepository {

    @Query(value = """
            SELECT
            	CONCAT(s.subject_code,' - ',s.name)as subjectName,
            	s.id as subjectId
            FROM
                head_subject_by_semester hsbs
            JOIN subject s ON
                s.id = hsbs.id_subject
            JOIN department d ON
                d.id = s.id_department
            JOIN department_facility df ON
                df.id_department = d.id
            WHERE
                hsbs.id_staff = :userId AND
                hsbs.id_semester = :semesterId AND
                df.id = :departmentFacilityId AND
                hsbs.status = 0
            """, countQuery = """
            SELECT
            	COUNT(hsbs.id)
            FROM
                head_subject_by_semester hsbs
            JOIN subject s ON
                s.id = hsbs.id_subject
            JOIN department d ON
                d.id = s.id_department
            JOIN department_facility df ON
                df.id_department = d.id
            WHERE
                hsbs.id_staff = :userId AND
                hsbs.id_semester = :semesterId AND
                df.id = :departmentFacilityId AND
                hsbs.status = 0
            """, nativeQuery = true)
    List<EASubjectResponse> getAllSubjects(String departmentFacilityId, String userId, String semesterId);

}
