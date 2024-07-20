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
            JOIN subject_group sg ON
                hsbs.id_subject_group = sg.id
            JOIN subject s ON
                s.id_subject_group = sg.id
            WHERE
                hsbs.id_staff = :staffId AND
                hsbs.id_semester = :semesterId AND
                sg.id_department_facility = :departmentFacilityId AND
                sg.status = 0
            """, countQuery = """
            SELECT
            	COUNT(hsbs.id)
            FROM
                head_subject_by_semester hsbs
            JOIN subject_group sg ON
                hsbs.id_subject_group = sg.id
            JOIN subject s ON
                s.id_subject_group = sg.id
            WHERE
                hsbs.id_staff = :staffId AND
                hsbs.id_semester = :semesterId AND
                sg.id_department_facility = :departmentFacilityId AND
                sg.status = 0
            """, nativeQuery = true)
    List<EASubjectResponse> getAllSubjects(String departmentFacilityId, String staffId, String semesterId);

}
