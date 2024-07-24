package fplhn.udpm.examdistribution.core.teacher.examshift.repository;

import fplhn.udpm.examdistribution.core.teacher.examshift.model.response.TFacilityChildResponse;
import fplhn.udpm.examdistribution.repository.FacilityChildRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface TFacilityChildExtendRepository extends FacilityChildRepository {

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
    List<TFacilityChildResponse> findAllByClassSubjectCodeAndSubjectId(String classSubjectCode, String subjectId);

}
