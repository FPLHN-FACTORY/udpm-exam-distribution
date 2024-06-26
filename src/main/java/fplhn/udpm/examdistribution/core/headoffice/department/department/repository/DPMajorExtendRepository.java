package fplhn.udpm.examdistribution.core.headoffice.department.department.repository;

import fplhn.udpm.examdistribution.core.headoffice.department.department.model.request.FindMajorRequest;
import fplhn.udpm.examdistribution.core.headoffice.department.department.model.response.MajorResponse;
import fplhn.udpm.examdistribution.entity.Major;
import fplhn.udpm.examdistribution.repository.MajorRepository;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface DPMajorExtendRepository extends MajorRepository {

    @Query(
            value = """
                    SELECT
                    	ROW_NUMBER() OVER(
                    	ORDER BY mj.id DESC) AS orderNumber,
                    	mj.id AS majorId,
                    	mj.name AS majorName,
                    	mj.status AS majorStatus,
                    	mj.created_date AS createdDate
                    FROM
                    	major mj
                    WHERE
                        mj.id_department = :id
                        AND ( :#{#req.majorName} IS NULL
                     	OR mj.name LIKE :#{"%" + #req.majorName + "%"})
                    """, countQuery = """
            SELECT
            	COUNT(mj.id)
            FROM
            	major mj
            WHERE
                mj.id_department = :id
                AND ( :#{#req.majorName} IS NULL
             	OR mj.name LIKE :#{"%" + #req.majorName + "%"})
            """, nativeQuery = true)
    Page<MajorResponse> getAllMajorByDepartmentIdFilter(String id, Pageable pageable, @Param("req") FindMajorRequest req);

    Optional<Major> findMajorByNameAndDepartmentId(String name, String departmentId);

}
