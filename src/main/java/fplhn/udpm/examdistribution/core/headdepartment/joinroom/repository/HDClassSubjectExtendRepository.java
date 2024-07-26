package fplhn.udpm.examdistribution.core.headdepartment.joinroom.repository;

import fplhn.udpm.examdistribution.core.headdepartment.joinroom.model.request.HDClassSubjectRequest;
import fplhn.udpm.examdistribution.core.headdepartment.joinroom.model.response.HDClassSubjectResponse;
import fplhn.udpm.examdistribution.repository.ClassSubjectRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface HDClassSubjectExtendRepository extends ClassSubjectRepository {

    @Query(value = """
            SELECT
            	cs.id as id,
            	cs.class_subject_code as classSubjectCode
            FROM
            	class_subject cs
            JOIN subject s ON
            	cs.id_subject = s.id
            JOIN department d ON
            	s.id_department = d.id
            JOIN department_facility df ON
            	d.id = df.id_department
            WHERE
            	cs.class_subject_code = :classSubjectCode
            	AND df.id = :departmentFacilityId
            """, nativeQuery = true)
    Optional<HDClassSubjectResponse> getClassSubject(String classSubjectCode, String departmentFacilityId);

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
