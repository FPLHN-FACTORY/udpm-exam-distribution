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
            JOIN subject s on
            	cs.id_subject = s.id
            JOIN department d on
            	s.id_department = d.id
            JOIN department_facility df on
            	d.id = df.id_department
            JOIN major_facility mf on
            	df.id = mf.id_department_facility
            WHERE
            	cs.class_subject_code = :classSubjectCode
            	AND df.id = :departmentFacilityId
            	AND cs.id_block = :blockId
            	AND mf.id = :majorFacilityId
            """, nativeQuery = true)
    Optional<HSClassSubjectResponse> getClassSubject(
            String classSubjectCode, String departmentFacilityId, String blockId, String majorFacilityId);

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
