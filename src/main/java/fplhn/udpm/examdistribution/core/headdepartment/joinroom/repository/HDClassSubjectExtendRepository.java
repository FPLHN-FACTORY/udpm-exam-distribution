package fplhn.udpm.examdistribution.core.headdepartment.joinroom.repository;

import fplhn.udpm.examdistribution.core.headdepartment.joinroom.model.request.HDClassSubjectRequest;
import fplhn.udpm.examdistribution.repository.ClassSubjectRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

@Repository
public interface HDClassSubjectExtendRepository extends ClassSubjectRepository {

    @Query(value = """
            SELECT
            	cs.id
            FROM
            	class_subject cs
            WHERE
            	cs.class_subject_code = :#{#hdClassSubjectRequest.classSubjectCode}
            	AND cs.id_subject = :#{#hdClassSubjectRequest.subjectId}
            	AND cs.id_block = :#{#hdClassSubjectRequest.blockId}
            	AND cs.id_facility_child = :#{#hdClassSubjectRequest.facilityChildId}
            """, nativeQuery = true)
    String getClassSubjectIdByRequest(HDClassSubjectRequest hdClassSubjectRequest);

}
