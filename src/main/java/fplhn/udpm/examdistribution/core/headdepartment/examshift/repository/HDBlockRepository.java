package fplhn.udpm.examdistribution.core.headdepartment.examshift.repository;

import fplhn.udpm.examdistribution.core.headdepartment.examshift.model.response.BlockInfoResponse;
import fplhn.udpm.examdistribution.repository.BlockRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface HDBlockRepository extends BlockRepository {

    @Query(
            value = """
                    SELECT
                        bl.id AS id,
                        CONCAT(bl.name, ' - ', s.name,' - ', s.year) AS blockInfo
                    FROM block bl
                    LEFT JOIN semester s ON bl.id_semester = s.id
                    WHERE
                        s.id = :semesterId
                    LIMIT 10
                    """,
            nativeQuery = true
    )
    List<BlockInfoResponse> getListBlocks(String semesterId);

}
