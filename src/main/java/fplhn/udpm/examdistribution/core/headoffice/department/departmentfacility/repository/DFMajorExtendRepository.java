package fplhn.udpm.examdistribution.core.headoffice.department.departmentfacility.repository;

import fplhn.udpm.examdistribution.core.headoffice.department.departmentfacility.model.response.ListMajorResponse;
import fplhn.udpm.examdistribution.repository.MajorRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface DFMajorExtendRepository extends MajorRepository {

    @Query(
        value = """
                SELECT
                    m.id AS majorId,
                    m.name AS majorName
                FROM
                    major m
                WHERE
                    m.id_department = :departmentId
                """,
            nativeQuery = true
    )
    List<ListMajorResponse> findAllByDepartmentId(String departmentId);

}
