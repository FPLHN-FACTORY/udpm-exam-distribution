//package fplhn.udpm.examdistribution.infrastructure.config.job.prenotifyexam.repository;
//
//import fplhn.udpm.examdistribution.entity.ExamShift;
//import fplhn.udpm.examdistribution.infrastructure.config.job.prenotifyexam.response.ExamShiftCommingInfoResponse;
//import fplhn.udpm.examdistribution.infrastructure.constant.Shift;
//import fplhn.udpm.examdistribution.repository.ExamShiftRepository;
//import org.springframework.data.jpa.repository.Query;
//import org.springframework.stereotype.Repository;
//
//import java.util.List;
//import java.util.Optional;
//
//@Repository
//public interface SCHDExamShiftExtendRepository extends ExamShiftRepository {
//
//    @Query(value = """
//            SELECT
//                new fplhn.udpm.examdistribution.infrastructure.config.job.prenotifyexam.response.ExamShiftCommingInfoResponse(
//                    es.examShiftCode,
//                    es.room,
//                    es.examDate,
//                    es.shift,
//                    cs.classSubjectCode,
//                    s3.name,
//                    s.staffCode,
//                    s.name,
//                    s.accountFe,
//                    s.accountFpt,
//                    s2.staffCode,
//                    s2.name,
//                    s2.accountFe,
//                    s2.accountFpt,
//                    df.id,
//                    df.staff.accountFpt,
//                    hsbs.staff.accountFpt
//                )
//            FROM
//            	ExamShift es
//            JOIN Staff s ON
//            	es.firstSupervisor.id = s.id
//            JOIN Staff s2 ON
//            	es.secondSupervisor.id = s2.id
//            JOIN ClassSubject cs ON
//            	es.classSubject.id = cs.id
//            JOIN Subject s3 ON
//            	cs.subject.id = s3.id
//            JOIN SubjectBySubjectGroup sbsg ON
//            	sbsg.subject.id = s3.id
//            JOIN SubjectGroup sg ON
//            	sbsg.subjectGroup.id = sg.id
//            JOIN DepartmentFacility df ON
//            	sg.departmentFacility.id = df.id
//            JOIN HeadSubjectBySemester hsbs ON
//            	hsbs.subjectGroup.id = sg.id
//            JOIN Semester s4 ON
//            	hsbs.semester.id = s4.id
//            WHERE
//                es.examDate = :examDate
//                AND s4.id = :currentSemesterId
//            """)
//    List<ExamShiftCommingInfoResponse>
//    getContentSendMail(Long examDate, String currentSemesterId);
//
//    List<ExamShift> findByExamDate(Long examDate);
//
//    Optional<ExamShift> findByExamShiftCode(String examShiftCode);
//
//}
