package fplhn.udpm.examdistribution.core.headsubject.createexampaper.repository;

import fplhn.udpm.examdistribution.core.headsubject.createexampaper.model.response.CREPListMajorFacilityResponse;
import fplhn.udpm.examdistribution.repository.MajorFacilityRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface CREPMajorFacilityExtendRepository extends MajorFacilityRepository {

    @Query(value = """ 
                 SELECT
                    DISTINCT
                 	CONCAT(m.name, ' - ', f.name) as majorFacilityName,
                 	mf.id as majorFacilityId
                 FROM
                 	staff_major_facility smf
                 JOIN major_facility mf ON
                    mf.id = smf.id_major_facility
                 JOIN major m ON
                    m.id = mf.id_major
                 JOIN department_facility df ON
                    df.id = mf.id_department_facility
                 JOIN facility f ON
                    f.id = df.id_facility
                 JOIN head_subject_by_semester hsbs ON
                    hsbs.id_staff = smf.id_staff
                 WHERE smf.id_major_facility = :majorFacilityId AND
                       smf.id_staff = :staffId AND
                       hsbs.id_semester = :semesterId AND
                       smf.status = 0
            """, nativeQuery = true)
    List<CREPListMajorFacilityResponse> getMajorFacilityByDepartmentFacilityId(String majorFacilityId, String staffId, String semesterId);

}
