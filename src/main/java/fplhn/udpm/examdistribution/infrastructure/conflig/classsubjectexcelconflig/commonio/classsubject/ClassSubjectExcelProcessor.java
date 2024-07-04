package fplhn.udpm.examdistribution.infrastructure.conflig.classsubjectexcelconflig.commonio.classsubject;

import fplhn.udpm.examdistribution.entity.*;
import fplhn.udpm.examdistribution.infrastructure.conflig.classsubjectexcelconflig.model.request.ClassSubjectExcelRequest;
import fplhn.udpm.examdistribution.infrastructure.conflig.classsubjectexcelconflig.model.request.ClassSubjectSearchParams;
import fplhn.udpm.examdistribution.infrastructure.conflig.classsubjectexcelconflig.repository.*;
import fplhn.udpm.examdistribution.infrastructure.constant.BlockName;
import fplhn.udpm.examdistribution.infrastructure.constant.EntityStatus;
import fplhn.udpm.examdistribution.infrastructure.constant.SemesterName;
import fplhn.udpm.examdistribution.infrastructure.constant.Shift;
import fplhn.udpm.examdistribution.utils.CodeGenerator;
import lombok.extern.slf4j.Slf4j;
import org.springframework.batch.item.ItemProcessor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
@Slf4j
public class ClassSubjectExcelProcessor implements ItemProcessor<ClassSubjectExcelRequest, ClassSubject> {

    @Autowired
    private SubjectExcelCustomRepository subjectExcelCustomRepository;

    @Autowired
    private FacilityChildExcelCustomRepository facilityChildExcelCustomRepository;

    @Autowired
    private StaffExcelCustomRepository staffCustomRepository;

    @Autowired
    private ClassSubjectExcelCustomRepository classSubjectExcelCustomRepository;

    @Autowired
    private BlockExcelCustomRepository blockExcelCustomRepository;

    @Autowired
    private SemesterExcelCustomRepository semesterExcelCustomRepository;

    @Override
    public ClassSubject process(ClassSubjectExcelRequest item) throws Exception {
        try {
            log.info("Processing item: " + item.getSubjectCode());
            List<Subject> subject = subjectExcelCustomRepository.findAllBySubjectCode(item.getSubjectCode().split(" - ")[0]);
            if (subject.isEmpty()) {
                log.error("Mon hoc khong ton tai: " + item.getSubjectCode());
                return null;
            }

            List<FacilityChild> facilityChild = facilityChildExcelCustomRepository.findAllByName(item.getFacilityChildName());
            if (facilityChild.isEmpty()) {
                log.error("Co so con khong ton tai: " + item.getFacilityChildName());
                return null;
            }

            List<Staff> staff = staffCustomRepository.findAllByStaffCodeAndStatus(item.getStaffCode(),EntityStatus.ACTIVE);
            if (staff.isEmpty()) {
                    log.error("Giang vien khong ton tai: " + item.getStaffCode());
                return null;
            }

            String blockName = item.getBlockName().split(" - ")[0];
            String semesterName = item.getBlockName().split(" - ")[1];
            String year = item.getBlockName().split(" - ")[2];

            List<Semester> semesters = semesterExcelCustomRepository.findAllBySemesterNameAndYearAndStatus(SemesterName.valueOf(semesterName),Integer.parseInt(year),EntityStatus.ACTIVE);
            if (semesters.isEmpty()) {
                log.error("Block khong ton tai: " + item.getBlockName());
                return null;
            }
            List<Block> block = blockExcelCustomRepository.findAllByNameAndSemester(BlockName.valueOf(blockName),semesters.get(0));
            if (block.isEmpty()) {
                log.error("Block khong ton tai: " + item.getBlockName());
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
            log.error("Error processing item: " + item, e);
            return null;
        }
    }

}
