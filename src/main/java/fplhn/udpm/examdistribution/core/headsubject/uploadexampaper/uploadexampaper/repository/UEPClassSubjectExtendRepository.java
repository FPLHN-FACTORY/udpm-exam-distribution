package fplhn.udpm.examdistribution.core.headsubject.uploadexampaper.uploadexampaper.repository;

import fplhn.udpm.examdistribution.repository.ClassSubjectRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

@Repository
public interface UEPClassSubjectExtendRepository extends ClassSubjectRepository {

    @Query("""
            SELECT st.accountFpt
            FROM ClassSubject cs
            JOIN Staff st ON st.id = cs.staff.id
            JOIN StaffDepartmentFacility sdf ON sdf.staff.id = st.id
            WHERE cs.block.id = :blockId AND
                  cs.subject.id = :subjectId AND
                  sdf.departmentFacility.id = :departmentFacilityId
            """)
    String[] getEmailStaffByBlockId(String blockId, String subjectId, String departmentFacilityId);

}
