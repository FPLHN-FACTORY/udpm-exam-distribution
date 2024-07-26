package fplhn.udpm.examdistribution.core.headoffice.classsubject.repository;

import fplhn.udpm.examdistribution.core.headoffice.classsubject.model.request.ClassSubjectByStaffRequest;
import fplhn.udpm.examdistribution.entity.ClassSubject;
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

    Optional<StaffSubject> findByClassSubject(ClassSubject classSubject);

    @Query(value = """
            SELECT
            	IFNULL(COUNT(cs.id), 0)
            FROM
            	class_subject cs
            WHERE
            	cs.id_staff = :#{#request.staffId}
            	AND cs.id_subject = :#{#request.subjectId}
            	AND cs.id_block = :#{#request.blockId}
            """, nativeQuery = true)
    Integer countClassSubjectByStaff(ClassSubjectByStaffRequest request);

    @Query(value = """
            SELECT
            	ss.id
            FROM
            	staff_subject ss
            WHERE
            	ss.id_staff = :#{#request.staffId}
            	AND ss.id_subject = :#{#request.subjectId}
            """, nativeQuery = true)
    String getClassSubjectId(ClassSubjectByStaffRequest request);

}
