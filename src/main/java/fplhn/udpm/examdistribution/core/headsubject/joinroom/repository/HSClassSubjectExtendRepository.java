package fplhn.udpm.examdistribution.core.headsubject.joinroom.repository;

import fplhn.udpm.examdistribution.core.headsubject.joinroom.model.request.HSClassSubjectRequest;
import fplhn.udpm.examdistribution.repository.ClassSubjectRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

@Repository
public interface HSClassSubjectExtendRepository extends ClassSubjectRepository {

    @Query(value = """
            SELECT
            	cs.id
            FROM
            	class_subject cs
            WHERE
            	cs.class_subject_code = :#{#hsClassSubjectRequest.classSubjectCode}
            	AND cs.id_subject = :#{#hsClassSubjectRequest.subjectId}
            	AND cs.id_block = :#{#hsClassSubjectRequest.blockId}
            	AND cs.id_facility_child = :#{#hsClassSubjectRequest.facilityChildId}
            """, nativeQuery = true)
    String getClassSubjectIdByRequest(HSClassSubjectRequest hsClassSubjectRequest);

}
