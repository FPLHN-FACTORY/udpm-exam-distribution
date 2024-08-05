package fplhn.udpm.examdistribution.core.headdepartment.classsubject.repository;

import fplhn.udpm.examdistribution.core.headdepartment.classsubject.model.request.BlockRequest;
import fplhn.udpm.examdistribution.core.headdepartment.classsubject.model.response.BlockListResponse;
import fplhn.udpm.examdistribution.repository.BlockRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface HDBlockClassSubjectRepository extends BlockRepository {


    @Query(value = """
            SELECT
            	b.id AS 'blockId',
            	b.name AS 'blockName',
            	s.`year` AS 'year'
            FROM
            	block b
            LEFT JOIN semester s ON
            	b.id_semester = s.id
            WHERE
            	(:#{#request.year} IS NULL OR :#{#request.year} LIKE '' OR s.`year` = :#{#request.year}) AND 
            	(:#{#request.semesterId} IS NULL OR :#{#request.semesterId} LIKE '' OR s.id = :#{#request.semesterId})
            """, nativeQuery = true)
    List<BlockListResponse> getAllBlockByYear(BlockRequest request);
}
