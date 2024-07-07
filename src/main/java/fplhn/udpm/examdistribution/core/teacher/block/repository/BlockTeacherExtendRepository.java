package fplhn.udpm.examdistribution.core.teacher.block.repository;

import fplhn.udpm.examdistribution.core.teacher.block.model.response.BlockTeacherResponse;
import fplhn.udpm.examdistribution.repository.BlockRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface BlockTeacherExtendRepository extends BlockRepository {

    @Query(value = """
            SELECT
            	b.id as id,
            	b.name as blockName
            FROM
            	block b
            JOIN class_subject cs on
            	b.id = cs.id_block
            JOIN subject s on
            	cs.id_subject = s.id
            WHERE
            	cs.class_subject_code = :classSubjectCode
            	AND s.id = :subjectId
            """, nativeQuery = true)
    List<BlockTeacherResponse> findAllByClassSubjectCodeAndSubjectId(String classSubjectCode, String subjectId);

    @Query(value = """
            SELECT
            	b.id
            FROM
            	block b
            JOIN class_subject cs on
            	b.id = cs.id_block
            JOIN subject s on
            	cs.id_subject = s.id
            WHERE
                :examShiftDate BETWEEN b.start_time AND b.end_time
            	AND cs.class_subject_code = :classSubjectCode
            	AND s.id = :subjectId
            """, nativeQuery = true)
    String findBlockId(Long examShiftDate, String classSubjectCode, String subjectId);

}
