package fplhn.udpm.examdistribution.core.teacher.examfile.repository;

import fplhn.udpm.examdistribution.entity.AssignUploader;
import fplhn.udpm.examdistribution.repository.AssignUploaderRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface TAssignUploaderRepository extends AssignUploaderRepository {

    List<AssignUploader> findAllBySubject_IdAndStaff_Id (String subjectId, String staffId);

}
