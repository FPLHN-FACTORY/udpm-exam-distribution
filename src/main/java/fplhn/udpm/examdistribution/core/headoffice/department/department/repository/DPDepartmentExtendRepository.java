package fplhn.udpm.examdistribution.core.headoffice.department.department.repository;

import fplhn.udpm.examdistribution.core.headoffice.department.department.model.request.FindDepartmentsRequest;
import fplhn.udpm.examdistribution.core.headoffice.department.department.model.response.DepartmentResponse;
import fplhn.udpm.examdistribution.core.headoffice.department.department.model.response.DetailDepartmentResponse;
import fplhn.udpm.examdistribution.core.headoffice.department.department.model.response.ListDepartmentResponse;
import fplhn.udpm.examdistribution.repository.DepartmentRepository;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface DPDepartmentExtendRepository extends DepartmentRepository {

    @Query(value = """
            SELECT
            	ROW_NUMBER() OVER(
            	ORDER BY d.id DESC) AS orderNumber,
            	d.id AS id,
            	d.name AS departmentName,
            	d.code AS departmentCode,
            	d.status AS departmentStatus,
            	d.created_date AS createdDate
            FROM
            	department d
            WHERE
                :#{#req.departmentName} IS NULL OR
                d.name LIKE :#{"%" + #req.departmentName + "%"}
            """, countQuery = """
            SELECT
                COUNT(d.id) FROM department d
            WHERE
                :#{#req.departmentName} IS NULL OR
                d.name LIKE :#{"%" + #req.departmentName + "%"}
            """, nativeQuery = true)
    Page<DepartmentResponse> getAllDepartmentByFilter(Pageable pageable, @Param("req") FindDepartmentsRequest req);

    @Query(value = """
            SELECT
            	d.id AS id,
            	d.code AS departmentCode,
            	d.name AS departmentName
            FROM
            	department d
            WHERE
                d.id = :id
            """, countQuery = """
            SELECT
            	COUNT(d.id)
            FROM
            	department d
            WHERE
                d.id = :id
            """, nativeQuery = true)
    DetailDepartmentResponse getDetailDepartment(String id);

    Boolean existsByName(String name);

    Boolean existsByCode(String code);

    @Query(value = """
            SELECT  d.id AS id,
                    d.name AS departmentName
            FROM department d
            """, nativeQuery = true)
    List<ListDepartmentResponse> getListDepartment();

}
