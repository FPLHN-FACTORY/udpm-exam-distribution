package fplhn.udpm.examdistribution.core.teacher.classsubject.repository;

import fplhn.udpm.examdistribution.core.teacher.classsubject.model.request.ClassSubjectTeacherRequest;
import fplhn.udpm.examdistribution.repository.ClassSubjectRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

@Repository
public interface ClassSubjectTeacherExtendRepository extends ClassSubjectRepository {

    @Query(value = """
            SELECT
            	cs.id
            FROM
            	class_subject cs
            WHERE
            	cs.class_subject_code = :#{#classSubjectTeacherRequest.classSubjectCode}
            	AND cs.id_subject = :#{#classSubjectTeacherRequest.subjectId}
            	AND cs.id_block = :#{#classSubjectTeacherRequest.blockId}
            	AND cs.id_facility_child = :#{#classSubjectTeacherRequest.facilityChildId}
            """, nativeQuery = true)
    String getClassSubjectIdByRequest(ClassSubjectTeacherRequest classSubjectTeacherRequest);

}
