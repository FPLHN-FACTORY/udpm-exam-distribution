package fplhn.udpm.examdistribution.core.headsubject.uploadexampaper.uploadexampaper.repository;

import fplhn.udpm.examdistribution.core.headsubject.uploadexampaper.uploadexampaper.model.response.ListSubjectResponse;
import fplhn.udpm.examdistribution.repository.SubjectRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface UEPSubjectExtendRepository extends SubjectRepository {

    @Query(value = """
            SELECT
            	subj.id AS id,
            	subj.name AS name
            FROM
            	head_subject_by_semester hsbs
            JOIN subject subj ON
            	hsbs.id_subject = subj.id
            JOIN staff st ON
            	st.id = hsbs.id_staff
            WHERE
            	st.id = :userId AND
            	hsbs.id_semester = :semesterId
            """, countQuery = """
            SELECT
            	COUNT(hsbs.id)
            FROM
            	head_subject_by_semester hsbs
            JOIN subject subj ON
            	hsbs.id_subject = subj.id
            JOIN staff st ON
            	st.id = hsbs.id_staff
            WHERE
            	st.id = :userId AND
            	hsbs.id_semester = :semesterId
            """, nativeQuery = true)
    List<ListSubjectResponse> getListSubject(String userId, String semesterId);

}
