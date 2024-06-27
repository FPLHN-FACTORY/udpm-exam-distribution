package fplhn.udpm.examdistribution.core.headoffice.classsubject.repository;

import fplhn.udpm.examdistribution.core.headoffice.classsubject.model.request.CountClassSubjectByStaffRequest;
import fplhn.udpm.examdistribution.core.headoffice.classsubject.model.response.CountClassSubjectByStaffResponse;
import fplhn.udpm.examdistribution.entity.Semester;
import fplhn.udpm.examdistribution.entity.Staff;
import fplhn.udpm.examdistribution.entity.StaffSubject;
import fplhn.udpm.examdistribution.entity.Subject;
import fplhn.udpm.examdistribution.repository.StaffSubjectRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface StaffSubjectExtendRepository extends StaffSubjectRepository {

    Optional<StaffSubject> findByStaffAndSubjectAndRecentlySemester(Staff staff, Subject subject, Semester semester);

    @Query(value = """
            SELECT
            	COUNT(DISTINCT cs.class_subject_code) AS 'countClassSubject',
            	ss.id AS 'staffSubjectId'
            FROM
            	staff_subject ss
            LEFT JOIN staff s ON
             	ss.id_staff = s.id
            LEFT JOIN class_subject cs ON
             	cs.id_staff = s.id
            WHERE
            	ss.id_staff = :#{#request.staffId}
            	AND ss.id_subject = :#{#request.subjectId}
            	AND cs.id_block = :#{#request.blockId}
            GROUP BY ss.id
            """, nativeQuery = true)
    Optional<CountClassSubjectByStaffResponse> countClassSubjectByStaff(CountClassSubjectByStaffRequest request);

}
