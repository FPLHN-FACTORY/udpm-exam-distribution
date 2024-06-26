package fplhn.udpm.examdistribution.core.headoffice.block.repository;

import fplhn.udpm.examdistribution.core.headoffice.block.model.response.BlockResponse;
import fplhn.udpm.examdistribution.core.headoffice.semester.model.response.SemesterResponse;
import fplhn.udpm.examdistribution.entity.Block;
import fplhn.udpm.examdistribution.infrastructure.constant.BlockName;
import fplhn.udpm.examdistribution.repository.BlockRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface BlockExtendRepository extends BlockRepository {

    @Query(
            value = """
                    SELECT
                    	b.id as id,
                    	b.id_semester as semesterId,
                    	b.name as blockName,
                    	b.start_time as startTime,
                    	b.end_time as endTime,
                    	b.status as blockStatus
                    FROM
                    	block b
                    WHERE
                    	b.id_semester = :semesterId
                    """, nativeQuery = true
    )
    List<BlockResponse> getAllBlockBySemesterId(String semesterId);

    Optional<Block> findBlockByNameAndSemesterId(BlockName name, String semester_id);

    List<Block> findAllBySemesterId(String semesterId);

    @Query(value = """
                SELECT
                    b.id,
                    b.id_semester,
                    b.name,
                    b.start_time,
                    b.end_time,
                    b.created_date,
                    b.last_modified_date,
                    b.status
                FROM block b
                WHERE b.name = :blockName
                AND b.id_semester = :semesterId
            """, nativeQuery = true)
    Optional<Block> existingByBlockAndSemester(String blockName, String semesterId);

    @Query(
            value = """
                    SELECT
                    	b.id as id,
                    	b.id_semester as semesterId,
                    	b.name as blockName,
                    	b.start_time as startTime,
                    	b.end_time as endTime,
                    	b.status as blockStatus
                    FROM
                    	block b
                    WHERE
                    	b.id = :blockId
                    """, nativeQuery = true
    )
    Optional<BlockResponse> getDetailBlockById(String blockId);
}
