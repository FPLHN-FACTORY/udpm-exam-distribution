package fplhn.udpm.examdistribution.core.teacher.facilitychild.repository;

import fplhn.udpm.examdistribution.core.teacher.block.model.response.BlockTeacherResponse;
import fplhn.udpm.examdistribution.core.teacher.facilitychild.model.response.FacilityChildTeacherResponse;
import fplhn.udpm.examdistribution.repository.FacilityChildRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface FacilityChildTeacherExtendRepository extends FacilityChildRepository {

    @Query(value = """
            SELECT
            	fc.id as id,
            	fc.name as facilityChildName
            FROM
            	facility_child fc
            JOIN class_subject cs on
            	fc.id = cs.id_facility_child
            JOIN subject s on
            	cs.id_subject = s.id
            WHERE
            	cs.class_subject_code = :classSubjectCode
            	AND s.id = :subjectId
            """, nativeQuery = true)
    List<FacilityChildTeacherResponse> findAllByClassSubjectCodeAndSubjectId(String classSubjectCode, String subjectId);

}
