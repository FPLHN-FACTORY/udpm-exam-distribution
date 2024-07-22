package fplhn.udpm.examdistribution.core.headsubject.createexampaper.repository;

import fplhn.udpm.examdistribution.core.headsubject.createexampaper.model.response.CREPListSubjectResponse;
import fplhn.udpm.examdistribution.repository.SubjectRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface CREPSubjectExtendRepository extends SubjectRepository {

    @Query(value = """
            SELECT
            	s.id AS id,
            	s.name AS name
            FROM
                head_subject_by_semester hsbs
            JOIN subject_by_subject_group sbsg ON
                sbsg.id_subject_group = hsbs.id_subject_group
            JOIN subject_group sg ON
                sg.id = sbsg.id_subject_group
            JOIN subject s ON
                s.id = sbsg.id_subject
            WHERE
                hsbs.id_staff = :userId AND
                hsbs.id_semester = :semesterId AND
                sg.id_department_facility = :departmentFacilityId AND
                sg.status = 0
            """, countQuery = """
            SELECT
            	COUNT(hsbs.id)
            FROM
                head_subject_by_semester hsbs
            JOIN subject_by_subject_group sbsg ON
                sbsg.id_subject_group = hsbs.id_subject_group
            JOIN subject_group sg ON
                sg.id = sbsg.id_subject_group
            JOIN subject s ON
                s.id = sbsg.id_subject
            WHERE
                hsbs.id_staff = :userId AND
                hsbs.id_semester = :semesterId AND
                sg.id_department_facility = :departmentFacilityId AND
                sg.status = 0
            """, nativeQuery = true)
    List<CREPListSubjectResponse> getListSubject(String userId, String departmentFacilityId, String semesterId);

}
