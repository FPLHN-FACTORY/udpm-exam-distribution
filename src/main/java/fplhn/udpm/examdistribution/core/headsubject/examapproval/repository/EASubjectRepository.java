package fplhn.udpm.examdistribution.core.headsubject.examapproval.repository;

import fplhn.udpm.examdistribution.core.headsubject.examapproval.model.response.EASubjectResponse;
import fplhn.udpm.examdistribution.repository.SubjectRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface EASubjectRepository extends SubjectRepository {

    @Query(value = """
            SELECT CONCAT(subj.subject_code,' - ',subj.name)as subjectName,
            		subj.id as subjectId
            FROM subject subj
            JOIN department d on d.id = subj.id_department
            JOIN department_facility df on df.id_department = d.id
            JOIN head_subject_by_semester hsbs ON hsbs.id_subject = subj.id
            JOIN staff st on st.id = hsbs.id_staff
            WHERE subj.status = 0 AND
                  df.id LIKE :departmentFacilityId AND
                  st.id LIKE :staffId AND
                  hsbs.id_semester = :semesterId
            """, nativeQuery = true)
    List<EASubjectResponse> getAllSubjects(String departmentFacilityId, String staffId, String semesterId);

}
