package fplhn.udpm.examdistribution.core.headdepartment.manageheadsubject.repository;

import fplhn.udpm.examdistribution.entity.SubjectBySubjectGroup;
import fplhn.udpm.examdistribution.entity.SubjectGroup;
import fplhn.udpm.examdistribution.repository.SubjectBySubjectGroupRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;

@Repository
public interface HDSubjectBySubjectGroupExtendRepository extends SubjectBySubjectGroupRepository {

    Optional<SubjectBySubjectGroup> findBySubjectIdAndSubjectGroupId(String subjectId, String subjectGroupId);

    @Modifying
    @Transactional
    void deleteBySubjectIdAndSubjectGroupId(String subjectId, String subjectGroupId);

    @Query("""
            SELECT sg
            FROM SubjectGroup sg
            LEFT JOIN SubjectBySubjectGroup sbsg ON sg.id = sbsg.subjectGroup.id
            WHERE sbsg.subject.id = :subjectId AND sg.semester.id = :semesterId
            """)
    Optional<SubjectGroup> findBySubjectIdAndSemesterId(String subjectId, String semesterId);

}
