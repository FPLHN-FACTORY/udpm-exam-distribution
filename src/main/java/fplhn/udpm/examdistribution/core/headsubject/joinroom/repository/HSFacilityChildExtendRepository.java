package fplhn.udpm.examdistribution.core.headsubject.joinroom.repository;

import fplhn.udpm.examdistribution.core.headsubject.joinroom.model.response.HSFacilityChildResponse;
import fplhn.udpm.examdistribution.repository.FacilityChildRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface HSFacilityChildExtendRepository extends FacilityChildRepository {

    @Query(value = """
            SELECT
            	fc.id as id,
            	fc.name as facilityChildName
            FROM
            	facility_child fc
            JOIN class_subject cs ON
            	fc.id = cs.id_facility_child
            JOIN subject s ON
            	cs.id_subject = s.id
            WHERE
            	cs.class_subject_code = :classSubjectCode
            	AND s.id = :subjectId
            """, nativeQuery = true)
    List<HSFacilityChildResponse> findAllByClassSubjectCodeAndSubjectId(String classSubjectCode, String subjectId);

}
