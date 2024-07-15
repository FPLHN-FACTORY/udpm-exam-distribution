package fplhn.udpm.examdistribution.core.headsubject.uploadexampaper.uploadexampaper.repository;

import fplhn.udpm.examdistribution.core.headsubject.uploadexampaper.uploadexampaper.model.response.ListBlockResponse;
import fplhn.udpm.examdistribution.repository.BlockRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface UEPBlockExtendRepository extends BlockRepository {

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
    List<ListBlockResponse> getListBlock(String semesterId);

}
