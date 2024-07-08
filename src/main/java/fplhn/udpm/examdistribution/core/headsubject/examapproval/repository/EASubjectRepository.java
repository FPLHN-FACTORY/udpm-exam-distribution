package fplhn.udpm.examdistribution.core.headsubject.examapproval.repository;

import fplhn.udpm.examdistribution.core.headsubject.examapproval.model.response.EASubjectResponse;
import fplhn.udpm.examdistribution.repository.SubjectRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface EASubjectRepository extends SubjectRepository {

    @Query(value = """
            SELECT CONCAT(s.subject_code,' - ',s.name)as subjectName,
            		s.id as subjectId
            FROM subject s
            JOIN department d on d.id = s.id_department
            JOIN department_facility df on df.id_department = d.id
            JOIN staff s2 on s2.id = s.id_head_subject
            WHERE s.status = 0
            AND df.id LIKE :departmentFacilityId
            AND s2.id LIKE :staffId
            """, nativeQuery = true)
    List<EASubjectResponse> getAllSubjects(String departmentFacilityId,String staffId);

}
