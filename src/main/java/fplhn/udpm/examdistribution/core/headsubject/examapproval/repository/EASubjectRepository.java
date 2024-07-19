package fplhn.udpm.examdistribution.core.headsubject.examapproval.repository;

import fplhn.udpm.examdistribution.core.headsubject.examapproval.model.response.EASubjectResponse;
import fplhn.udpm.examdistribution.repository.SubjectRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface EASubjectRepository extends SubjectRepository {

//    @Query(value = """
//            SELECT CONCAT(subj.subject_code,' - ',subj.name)as subjectName,
//            		subj.id as subjectId
//            FROM subject subj
//            JOIN department d on d.id = subj.id_department
//            JOIN department_facility df on df.id_department = d.id
//            JOIN head_subject_by_semester hsbs ON hsbs.id_subject = subj.id
//            JOIN staff st on st.id = hsbs.id_staff
//            WHERE subj.status = 0 AND
//                  df.id LIKE :departmentFacilityId AND
//                  st.id LIKE :staffId AND
//                  hsbs.id_semester = :semesterId
//            """, nativeQuery = true)
//    List<EASubjectResponse> getAllSubjects(String departmentFacilityId, String staffId, String semesterId);

    @Query(value = """
            SELECT
            	CONCAT(s.subject_code,' - ',s.name)as subjectName,
            	s.id as subjectId
            FROM
            	head_subject_by_semester hsbs
            JOIN staff st ON
                st.id = hsbs.id_staff
            JOIN subject_group sg ON
            	hsbs.id_subject_group = sg.id
            JOIN subject s ON
                s.id = sg.id_subject
            WHERE
                sg.id_staff = :staffId AND
                st.id = :staffId AND
                hsbs.id_semester = :semesterId AND
                sg.id_department_facility = :departmentFacilityId AND
                sg.status = 0
            """, countQuery = """
            SELECT
            	COUNT(hsbs.id)
            FROM
            	head_subject_by_semester hsbs
            JOIN staff st ON
                st.id = hsbs.id_staff
            JOIN subject_group sg ON
            	hsbs.id_subject_group = sg.id
            JOIN subject s ON
                s.id = sg.id_subject
            WHERE
                sg.id_staff = :staffId AND
                st.id = :staffId AND
                hsbs.id_semester = :semesterId AND
                sg.id_department_facility = :departmentFacilityId AND
                sg.status = 0
            """, nativeQuery = true)
    List<EASubjectResponse> getAllSubjects(String departmentFacilityId, String staffId, String semesterId);

}
