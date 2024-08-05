package fplhn.udpm.examdistribution.core.headdepartment.joinroom.repository;

import fplhn.udpm.examdistribution.core.headdepartment.joinroom.model.response.HDFacilityChildResponse;
import fplhn.udpm.examdistribution.repository.FacilityChildRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface HDFacilityChildExtendRepository extends FacilityChildRepository {

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
            	AND cs.id_block = :blockId
            """, nativeQuery = true)
    List<HDFacilityChildResponse> findAllByClassSubjectCodeAndSubjectId(String classSubjectCode, String subjectId, String blockId);

}
