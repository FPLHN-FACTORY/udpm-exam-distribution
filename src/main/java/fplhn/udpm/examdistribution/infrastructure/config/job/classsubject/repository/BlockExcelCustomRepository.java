package fplhn.udpm.examdistribution.infrastructure.config.job.classsubject.repository;

import fplhn.udpm.examdistribution.entity.Block;
import fplhn.udpm.examdistribution.entity.Semester;
import fplhn.udpm.examdistribution.infrastructure.constant.BlockName;
import fplhn.udpm.examdistribution.repository.BlockRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface BlockExcelCustomRepository extends BlockRepository {

    @Query(value = """
            SELECT CONCAT(b.name, ' - ' , s.name , ' - ', s.year ) AS blockName
            FROM block b
            JOIN semester s ON b.id_semester = s.id
            WHERE s.id = :semesterId
            AND b.status = 0
            """, nativeQuery = true)
    List<String> findAllBySemester(String semesterId);

    List<Block> findAllByNameAndSemester(BlockName blockName, Semester semester);

}
