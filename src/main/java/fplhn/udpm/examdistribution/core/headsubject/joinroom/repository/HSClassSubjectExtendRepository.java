package fplhn.udpm.examdistribution.core.headsubject.joinroom.repository;

import fplhn.udpm.examdistribution.core.headsubject.joinroom.model.request.HSClassSubjectRequest;
import fplhn.udpm.examdistribution.core.headsubject.joinroom.model.response.HSClassSubjectResponse;
import fplhn.udpm.examdistribution.repository.ClassSubjectRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface HSClassSubjectExtendRepository extends ClassSubjectRepository {

    @Query(value = """
            SELECT
            	cs.id as id,
            	cs.class_subject_code as classSubjectCode
            FROM
            	class_subject cs
            JOIN subject s ON
            	cs.id_subject = s.id
            JOIN subject_by_subject_group sbsg ON
            	sbsg.id_subject = s.id
            JOIN subject_group sg ON
            	sbsg.id_subject_group = sg.id
            WHERE
            	cs.class_subject_code = :classSubjectCode
            	AND sg.id_department_facility = :departmentFacilityId
            	AND sg.id_semester = :semesterId
            """, nativeQuery = true)
    Optional<HSClassSubjectResponse> getClassSubject(String classSubjectCode, String departmentFacilityId, String semesterId);

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
