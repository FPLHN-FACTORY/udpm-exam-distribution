package fplhn.udpm.examdistribution.core.headdepartment.joinroom.repository;

import fplhn.udpm.examdistribution.core.headdepartment.joinroom.model.response.HDSubjectResponse;
import fplhn.udpm.examdistribution.repository.SubjectRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface HDSSubjectExtendRepository extends SubjectRepository {

    @Query(value = """
            SELECT
                s.id as id,
            	s.subject_code as subjectCode,
            	s.name as subjectName
            FROM
            	subject s
            JOIN class_subject cs ON
            	s.id = cs.id_subject
            WHERE
            	cs.class_subject_code = :classSubjectCode
            """, nativeQuery = true)
    List<HDSubjectResponse> findAllByClassSubjectCode(String classSubjectCode);

}
