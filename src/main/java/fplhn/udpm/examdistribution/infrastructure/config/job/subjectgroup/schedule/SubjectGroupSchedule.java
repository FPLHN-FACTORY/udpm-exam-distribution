package fplhn.udpm.examdistribution.infrastructure.config.job.subjectgroup.schedule;

import fplhn.udpm.examdistribution.entity.HeadSubjectBySemester;
import fplhn.udpm.examdistribution.entity.Semester;
import fplhn.udpm.examdistribution.entity.SubjectGroup;
import fplhn.udpm.examdistribution.infrastructure.config.job.subjectgroup.repository.SCHeadSubjectBySemesterExtendRepository;
import fplhn.udpm.examdistribution.infrastructure.config.job.subjectgroup.repository.SCSemesterExtendRepository;
import fplhn.udpm.examdistribution.infrastructure.config.job.subjectgroup.repository.SCSubjectGroupExtendRepository;
import fplhn.udpm.examdistribution.utils.DateTimeUtil;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Objects;

@Component
@Slf4j
public class SubjectGroupSchedule {

    @Setter(onMethod_ = {@Autowired})
    private SCSubjectGroupExtendRepository subjectGroupRepository;

    @Setter(onMethod_ = {@Autowired})
    private SCHeadSubjectBySemesterExtendRepository headSubjectBySemesterRepository;

    @Setter(onMethod_ = {@Autowired})
    private SCSemesterExtendRepository semesterRepository;

    @Scheduled(cron = "${schedule.subject.group.cron}")
    public void assignSubjectGroup() {
        log.info("----------------- Start assign subject group new semester - block -----------------");
        List<SubjectGroup> listSubjectGroupAlready = subjectGroupRepository.findAllBySemester_Id(getCurrentSemesterId());
        if (!listSubjectGroupAlready.isEmpty()) {
            log.info("Subject group is already assigned for this semester");
            return;
        }
        List<SubjectGroup> listSubjectGroupPrevious = subjectGroupRepository.findAllBySemester_Id(getPreviousSemesterId());
        for (SubjectGroup subjectGroup : listSubjectGroupPrevious) {
            SubjectGroup newSubjectGroup = new SubjectGroup();
            newSubjectGroup.setSemester(semesterRepository.getReferenceById(Objects.requireNonNull(getCurrentSemesterId())));
            newSubjectGroup.setAttachRoleName(subjectGroup.getAttachRoleName());
            newSubjectGroup.setDepartmentFacility(subjectGroup.getDepartmentFacility());
            subjectGroupRepository.save(newSubjectGroup);
        }

        List<HeadSubjectBySemester> listHeadSubjectBySemesterPrevious = headSubjectBySemesterRepository.findAllBySemester_Id(getPreviousSemesterId());
        List<SubjectGroup> listSubjectGroupCurrent = subjectGroupRepository.findAllBySemester_Id(getCurrentSemesterId());
        for (HeadSubjectBySemester headSubjectBySemester : listHeadSubjectBySemesterPrevious) {
            for (SubjectGroup subjectGroup : listSubjectGroupCurrent) {
                HeadSubjectBySemester newHeadSubjectBySemester = new HeadSubjectBySemester();
                newHeadSubjectBySemester.setSemester(semesterRepository.getReferenceById(Objects.requireNonNull(getCurrentSemesterId())));
                newHeadSubjectBySemester.setSubjectGroup(subjectGroup);
                newHeadSubjectBySemester.setStaff(headSubjectBySemester.getStaff());
                headSubjectBySemesterRepository.save(newHeadSubjectBySemester);
            }
        }
        log.info("----------------- End assign subject group new semester - block -------------------");
    }

    private String getCurrentSemesterId() {
        List<Semester> semesters = semesterRepository.findAll();
        for (Semester semester : semesters) {
            if (semester.getStartTime() <= DateTimeUtil.getCurrentTime()) {
                return semester.getId();
            }
        }
        return null;
    }

    private String getPreviousSemesterId() {
        Semester currentSemester = semesterRepository.getReferenceById(Objects.requireNonNull(getCurrentSemesterId()));
        List<Semester> semesters = semesterRepository.findAll();
        for (Semester semester : semesters) {
            if (semester.getStartTime() <= currentSemester.getStartTime()) {
                return semester.getId();
            }
        }
        return null;
    }

}
