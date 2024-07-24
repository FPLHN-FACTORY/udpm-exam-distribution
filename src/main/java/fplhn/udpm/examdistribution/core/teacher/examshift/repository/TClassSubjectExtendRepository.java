package fplhn.udpm.examdistribution.core.teacher.examshift.repository;

import fplhn.udpm.examdistribution.core.teacher.examshift.model.request.TClassSubjectRequest;
import fplhn.udpm.examdistribution.repository.ClassSubjectRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

@Repository
public interface TClassSubjectExtendRepository extends ClassSubjectRepository {

    @Query(value = """
            SELECT
            	cs.id
            FROM
            	class_subject cs
            WHERE
            	cs.class_subject_code = :#{#tClassSubjectRequest.classSubjectCode}
            	AND cs.id_subject = :#{#tClassSubjectRequest.subjectId}
            	AND cs.id_block = :#{#tClassSubjectRequest.blockId}
            	AND cs.id_facility_child = :#{#tClassSubjectRequest.facilityChildId}
            """, nativeQuery = true)
    String getClassSubjectIdByRequest(TClassSubjectRequest tClassSubjectRequest);

}
