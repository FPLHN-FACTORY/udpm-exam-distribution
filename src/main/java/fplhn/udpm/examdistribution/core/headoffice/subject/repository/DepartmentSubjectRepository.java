package fplhn.udpm.examdistribution.core.headoffice.subject.repository;

import fplhn.udpm.examdistribution.core.headoffice.subject.model.response.DepartmentListResponse;
import fplhn.udpm.examdistribution.repository.DepartmentRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface DepartmentSubjectRepository extends DepartmentRepository {

    @Query(
            value = """
                    SELECT
                        d.id as departmentId,
                        d.name as departmentName
                    FROM
                        department d
                    """,
            nativeQuery = true
    )
    List<DepartmentListResponse> getAllDepartment();

}
