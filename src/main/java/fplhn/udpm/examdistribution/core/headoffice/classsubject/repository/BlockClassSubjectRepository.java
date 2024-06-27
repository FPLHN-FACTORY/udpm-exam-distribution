package fplhn.udpm.examdistribution.core.headoffice.classsubject.repository;

import fplhn.udpm.examdistribution.core.headoffice.classsubject.model.response.BlockListResponse;
import fplhn.udpm.examdistribution.repository.BlockRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface BlockClassSubjectRepository extends BlockRepository {


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
            	:#{#year} IS NULL OR :#{#year} LIKE '' OR s.`year` = :#{#year}
            """, nativeQuery = true)
    List<BlockListResponse> getAllBlockByYear(Integer year);
}
