package fplhn.udpm.examdistribution.infrastructure.conflig.classsubjectexcelconflig.repository;

import fplhn.udpm.examdistribution.entity.ClassSubject;
import fplhn.udpm.examdistribution.infrastructure.conflig.classsubjectexcelconflig.model.request.ClassSubjectSearchParams;
import fplhn.udpm.examdistribution.repository.ClassSubjectRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface ClassSubjectExcelCustomRepository extends ClassSubjectRepository {

    @Query(value = """
            SELECT cs.id,
                   cs.id_block,
                   cs.id_facility_child,
                   cs.id_subject,
                   cs.id_staff,
                   cs.day,
                   cs.shift,
                   cs.class_subject_code,
                   cs.status,
                   cs.created_date,
                   cs.last_modified_date
             FROM class_subject cs
             WHERE cs.id_block = :#{#params.idBlock}
             AND cs.id_facility_child = :#{#params.idFacilityChild}
             AND cs.id_subject = :#{#params.idSubject}
             AND cs.id_staff = :#{#params.idStaff}
             AND cs.shift = :#{#params.shift}
             AND cs.day = :#{#params.date}
             AND cs.class_subject_code = :#{#params.classCode}
             AND cs.status = 0
              """, nativeQuery = true)
    List<ClassSubject> findByParams(@Param("params") ClassSubjectSearchParams params);

}
