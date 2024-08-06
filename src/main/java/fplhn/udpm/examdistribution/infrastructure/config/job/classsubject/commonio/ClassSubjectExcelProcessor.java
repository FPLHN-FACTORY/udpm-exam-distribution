package fplhn.udpm.examdistribution.infrastructure.config.job.classsubject.commonio;

import fplhn.udpm.examdistribution.entity.Block;
import fplhn.udpm.examdistribution.entity.ClassSubject;
import fplhn.udpm.examdistribution.entity.FacilityChild;
import fplhn.udpm.examdistribution.entity.Semester;
import fplhn.udpm.examdistribution.entity.Staff;
import fplhn.udpm.examdistribution.entity.Subject;
import fplhn.udpm.examdistribution.infrastructure.config.global.GlobalVariables;
import fplhn.udpm.examdistribution.infrastructure.config.job.classsubject.model.ClassSubjectExcelRequest;
import fplhn.udpm.examdistribution.infrastructure.config.job.classsubject.model.ClassSubjectSearchParams;
import fplhn.udpm.examdistribution.infrastructure.config.job.classsubject.repository.BlockExcelCustomRepository;
import fplhn.udpm.examdistribution.infrastructure.config.job.classsubject.repository.ClassSubjectExcelCustomRepository;
import fplhn.udpm.examdistribution.infrastructure.config.job.classsubject.repository.FacilityChildExcelCustomRepository;
import fplhn.udpm.examdistribution.infrastructure.config.job.classsubject.repository.SemesterExcelCustomRepository;
import fplhn.udpm.examdistribution.infrastructure.config.job.classsubject.repository.StaffExcelCustomRepository;
import fplhn.udpm.examdistribution.infrastructure.config.job.classsubject.repository.SubjectExcelCustomRepository;
import fplhn.udpm.examdistribution.infrastructure.constant.BlockName;
import fplhn.udpm.examdistribution.infrastructure.constant.EntityStatus;
import fplhn.udpm.examdistribution.infrastructure.constant.LogFileType;
import fplhn.udpm.examdistribution.infrastructure.constant.SemesterName;
import fplhn.udpm.examdistribution.infrastructure.constant.SessionConstant;
import fplhn.udpm.examdistribution.infrastructure.constant.Shift;
import fplhn.udpm.examdistribution.utils.CodeGenerator;
import fplhn.udpm.examdistribution.utils.HistoryLogUtils;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;
import org.springframework.batch.item.ItemProcessor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
@Slf4j
public class ClassSubjectExcelProcessor implements ItemProcessor<ClassSubjectExcelRequest, ClassSubject> {

    @Setter(onMethod_ = {@Autowired})
    private SubjectExcelCustomRepository subjectExcelCustomRepository;

    @Setter(onMethod_ = {@Autowired})
    private FacilityChildExcelCustomRepository facilityChildExcelCustomRepository;

    @Setter(onMethod_ = {@Autowired})
    private StaffExcelCustomRepository staffCustomRepository;

    @Setter(onMethod_ = {@Autowired})
    private ClassSubjectExcelCustomRepository classSubjectExcelCustomRepository;

    @Setter(onMethod_ = {@Autowired})
    private BlockExcelCustomRepository blockExcelCustomRepository;

    @Setter(onMethod_ = {@Autowired})
    private SemesterExcelCustomRepository semesterExcelCustomRepository;

    @Setter(onMethod_ = {@Autowired})
    private GlobalVariables globalVariables;

    @Setter(onMethod_ = {@Autowired})
    private HistoryLogUtils historyLogUtils;

    @Setter
    private String fileName;

    @Override
    public ClassSubject process(ClassSubjectExcelRequest item) throws Exception {
        try {
            List<Subject> subject = subjectExcelCustomRepository.findAllBySubjectCode(item.getSubjectCode().split(" - ")[0]);
            if (subject.isEmpty()) {
                logErrorRecordToCSV(
                        "Mã môn học không tồn tại: " + item.getSubjectCode()
                );
                return null;
            }

            List<FacilityChild> facilityChild = facilityChildExcelCustomRepository.findAllByName(item.getFacilityChildName());
            if (facilityChild.isEmpty()) {
                logErrorRecordToCSV(
                        "Cơ sở không tồn tại: " + item.getFacilityChildName()
                );
                return null;
            }

            List<Staff> staff = staffCustomRepository.findAllByStaffCodeAndStatus(item.getStaffCode(), EntityStatus.ACTIVE);
            if (staff.isEmpty()) {
                logErrorRecordToCSV(
                        "Mã giáo viên không tồn tại: " + item.getStaffCode()
                );
                return null;
            }

            String blockName = item.getBlockName().split(" - ")[0];
            String semesterName = item.getBlockName().split(" - ")[1];
            String year = item.getBlockName().split(" - ")[2];

            List<Semester> semesters = semesterExcelCustomRepository
                    .findAllBySemesterNameAndYearAndStatus(
                            SemesterName.valueOf(semesterName),
                            Integer.parseInt(year),
                            EntityStatus.ACTIVE
                    );
            if (semesters.isEmpty()) {
                logErrorRecordToCSV(
                        "Học kỳ không tồn tại: " + item.getBlockName()
                );
                return null;
            }
            List<Block> block = blockExcelCustomRepository.findAllByNameAndSemester(BlockName.valueOf(blockName), semesters.get(0));
            if (block.isEmpty()) {
                logErrorRecordToCSV(
                        "Block không tồn tại: " + item.getBlockName()
                );
                return null;
            }

            ClassSubjectSearchParams classSubjectSearchParams = new ClassSubjectSearchParams();
            classSubjectSearchParams.setClassCode(item.getClassCode());
            classSubjectSearchParams.setShift(String.valueOf(Shift.valueOf(item.getShift())));
            classSubjectSearchParams.setIdStaff(staff.get(0).getId());
            classSubjectSearchParams.setIdFacilityChild(facilityChild.get(0).getId());
            classSubjectSearchParams.setIdSubject(subject.get(0).getId());
            classSubjectSearchParams.setDate(Long.parseLong(item.getDate()));
            classSubjectSearchParams.setIdBlock(block.get(0).getId());

            List<ClassSubject> lopMonOptional = classSubjectExcelCustomRepository.findByParams(classSubjectSearchParams);
            if (!lopMonOptional.isEmpty()) {
                lopMonOptional.get(0).setSubject(subject.get(0));
                lopMonOptional.get(0).setFacilityChild(facilityChild.get(0));
                lopMonOptional.get(0).setStaff(staff.get(0));
                lopMonOptional.get(0).setBlock(block.get(0));
                lopMonOptional.get(0).setStatus(EntityStatus.ACTIVE);
                lopMonOptional.get(0).setClassSubjectCode(item.getSubjectCode());
                lopMonOptional.get(0).setDay(Long.valueOf(item.getDate()));
                lopMonOptional.get(0).setShift(Shift.valueOf(item.getShift()));
                return lopMonOptional.get(0);
            }

            ClassSubject classSubject = new ClassSubject();
            classSubject.setId(CodeGenerator.generateRandomCode());
            classSubject.setClassSubjectCode(item.getClassCode());
            classSubject.setBlock(block.get(0));
            classSubject.setDay(Long.parseLong(item.getDate()));
            classSubject.setShift(Shift.valueOf(item.getShift()));
            classSubject.setStatus(EntityStatus.ACTIVE);
            classSubject.setSubject(subject.get(0));
            classSubject.setFacilityChild(facilityChild.get(0));
            classSubject.setStaff(staff.get(0));
            return classSubject;

        } catch (Exception e) {
            logErrorRecordToCSV(
                    "Lỗi tại bản ghi số " + item.getOrderNumber() + ": " + e.getMessage()
            );
            return null;
        }
    }

    private void logErrorRecordToCSV(String content) {
        historyLogUtils.logErrorRecord(
                content,
                fileName,
                (String) globalVariables.getGlobalVariable(SessionConstant.CURRENT_USER_ID),
                (String) globalVariables.getGlobalVariable(SessionConstant.CURRENT_USER_FACILITY_ID),
                LogFileType.CLASS_SUBJECT
        );
    }

}
