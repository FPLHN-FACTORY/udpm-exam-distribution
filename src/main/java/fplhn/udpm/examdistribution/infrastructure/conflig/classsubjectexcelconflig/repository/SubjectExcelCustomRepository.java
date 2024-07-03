package fplhn.udpm.examdistribution.infrastructure.conflig.classsubjectexcelconflig.repository;

import fplhn.udpm.examdistribution.entity.Department;
import fplhn.udpm.examdistribution.entity.Subject;
import fplhn.udpm.examdistribution.infrastructure.constant.EntityStatus;
import fplhn.udpm.examdistribution.repository.SubjectRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface SubjectExcelCustomRepository extends SubjectRepository {

    List<Subject> findAllBySubjectCode(String subjectCode);
    @Query(value = """
            SELECT CONCAT(s.subject_code,' - ',s.name) 
            FROM subject s WHERE s.status = 0
            """, nativeQuery = true)
    List<String> findAllSubject();

}
