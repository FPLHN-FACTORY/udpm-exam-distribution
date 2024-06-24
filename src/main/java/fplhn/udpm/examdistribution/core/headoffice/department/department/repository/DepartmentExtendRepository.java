package fplhn.udpm.examdistribution.core.headoffice.department.department.repository;

import fplhn.udpm.examdistribution.core.headoffice.department.department.model.request.FindDepartmentsRequest;
import fplhn.udpm.examdistribution.core.headoffice.department.department.model.response.DepartmentResponse;
import fplhn.udpm.examdistribution.core.headoffice.department.department.model.response.ListDepartmentResponse;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface DepartmentRepository {

    @Query(value = """
            SELECT
            	ROW_NUMBER() OVER(
            	ORDER BY bm.id DESC) AS stt,
            	bm.id as idBoMon,
            	bm.ten as tenBoMon,
            	bm.xoa_mem as xoaMemBoMon
            FROM
            	bo_mon bm
            WHERE
            	:#{#req.arrayField} IS NULL
            	OR :#{#req.arrayField} LIKE ''
            	OR bm.id IN :#{#req.arrayField}
            """, countQuery = """
            SELECT 
                COUNT(bm.id) FROM bo_mon bm 
            WHERE 
                :#{#req.arrayField} IS NULL
            	OR :#{#req.arrayField} LIKE ''
            	OR bm.id IN :#{#req.arrayField}
            """, nativeQuery = true)
    Page<DepartmentResponse> getAllBoMonByFilter(Pageable pageable, @Param("req") FindDepartmentsRequest req);

    @Query(value = """
            SELECT
            	ROW_NUMBER() OVER(
            	ORDER BY bm.id DESC) AS stt,
            	bm.id as idBoMon,
            	bm.ten as tenBoMon,
            	bm.xoa_mem as xoaMemBoMon
            FROM
            	bo_mon bm
            """, countQuery = """
            SELECT 
                COUNT(bm.id) FROM bo_mon bm 
            """, nativeQuery = true)
    Page<DepartmentResponse> getAllBoMon(Pageable pageable);

    Boolean existsByTen(String ten);

    @Query(value = """
            SELECT 
                id as idBoMon,
                ten as tenBoMon
            FROM
                bo_mon bm
            """, nativeQuery = true)
    List<ListDepartmentResponse> getListBoMon();

}
