package fplhn.udpm.examdistribution.core.headsubject.chooseexampaper.repository;

import fplhn.udpm.examdistribution.core.headsubject.chooseexampaper.model.response.CEPListBlockResponse;
import fplhn.udpm.examdistribution.repository.BlockRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface CEPBlockExtendRepository extends BlockRepository {

    @Query(value = """
            SELECT b.id AS id,
                   CONCAT(b.name,"-",s.year) AS name,
                   b.start_time AS startTime,
                   b.end_time AS endTime
            FROM block b
            JOIN semester s ON s.id = b.id_semester
            WHERE b.id_semester = :semesterId AND
                  b.status = 0
            """, nativeQuery = true)
    List<CEPListBlockResponse> getListBlock(String semesterId);

}
