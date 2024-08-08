package fplhn.udpm.examdistribution.infrastructure.config.job.examshift.commonio;

import fplhn.udpm.examdistribution.entity.Block;
import fplhn.udpm.examdistribution.entity.ClassSubject;
import fplhn.udpm.examdistribution.entity.ExamShift;
import fplhn.udpm.examdistribution.entity.FacilityChild;
import fplhn.udpm.examdistribution.entity.Staff;
import fplhn.udpm.examdistribution.entity.Subject;
import fplhn.udpm.examdistribution.infrastructure.config.global.GlobalVariables;
import fplhn.udpm.examdistribution.infrastructure.config.job.examshift.model.ImportShiftRequest;
import fplhn.udpm.examdistribution.infrastructure.config.job.examshift.repository.IPBlockExtendRepository;
import fplhn.udpm.examdistribution.infrastructure.config.job.examshift.repository.IPClassSubjectExtendRepository;
import fplhn.udpm.examdistribution.infrastructure.config.job.examshift.repository.IPExamShiftExtendRepository;
import fplhn.udpm.examdistribution.infrastructure.config.job.examshift.repository.IPFacilityChildRepository;
import fplhn.udpm.examdistribution.infrastructure.config.job.examshift.repository.IPStaffExtendRepository;
import fplhn.udpm.examdistribution.infrastructure.config.job.examshift.repository.IPSubjectExtendRepository;
import fplhn.udpm.examdistribution.infrastructure.constant.BlockName;
import fplhn.udpm.examdistribution.infrastructure.constant.ExamShiftStatus;
import fplhn.udpm.examdistribution.infrastructure.constant.LogFileType;
import fplhn.udpm.examdistribution.infrastructure.constant.SessionConstant;
import fplhn.udpm.examdistribution.infrastructure.constant.Shift;
import fplhn.udpm.examdistribution.utils.CodeGenerator;
import fplhn.udpm.examdistribution.utils.DateTimeUtil;
import fplhn.udpm.examdistribution.utils.Helper;
import fplhn.udpm.examdistribution.utils.HistoryLogUtils;
import lombok.Setter;
import org.springframework.batch.item.ItemProcessor;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.List;
import java.util.Optional;

public class ShiftProcessor implements ItemProcessor<ImportShiftRequest, ExamShift> {

    @Setter(onMethod_ = {@Autowired})
    private IPExamShiftExtendRepository examShiftRepository;

    @Setter(onMethod_ = {@Autowired})
    private IPSubjectExtendRepository subjectRepository;

    @Setter(onMethod_ = {@Autowired})
    private IPClassSubjectExtendRepository classSubjectRepository;

    @Setter(onMethod_ = {@Autowired})
    private IPBlockExtendRepository blockRepository;

    @Setter(onMethod_ = {@Autowired})
    private IPFacilityChildRepository facilityChildRepository;

    @Setter(onMethod_ = {@Autowired})
    private IPStaffExtendRepository staffRepository;

    @Setter(onMethod_ = {@Autowired})
    private HistoryLogUtils historyLogUtils;

    @Setter(onMethod_ = {@Autowired})
    private GlobalVariables globalVariables;

    @Override
    public ExamShift process(ImportShiftRequest item) throws Exception {
        try {
            Shift shift = Shift.fromString("CA" + (int) Double.parseDouble(item.getShift()));
            Long startDate = DateTimeUtil.parseStringToLong(item.getStartDate());
            String room = Helper.extractPrefix(item.getRoomInfo());

            Optional<Subject> subject = subjectRepository.findByNameAndSubjectCode(item.getSubjectName(), item.getSubjectCode());
            if (subject.isEmpty()) {
                logErrorRecordToCSV(
                        "Lỗi bản ghi số " + item.getOrderNumber() + ": Môn học không tồn tại trong hệ thống"
                );
                return null;
            }

            Optional<FacilityChild> facilityChild = facilityChildRepository.findByName(item.getCampusCode());
            if (facilityChild.isEmpty()) {
                logErrorRecordToCSV(
                        "Lỗi bản ghi số " + item.getOrderNumber() + ": Cơ sở không tồn tại trong hệ thống"
                );
                return null;
            }

            String blockNameConvention = item.getBlock().toUpperCase().replace(" ", "_");
            List<Block> listBlockCurrent = blockRepository.findByName(BlockName.valueOf(blockNameConvention));
            if (listBlockCurrent.isEmpty()) {
                logErrorRecordToCSV(
                        "Lỗi bản ghi số " + item.getOrderNumber() + ": Khung giờ không tồn tại trong hệ thống"
                );
                return null;
            }

            Block block = listBlockCurrent.stream()
                    .filter(b -> b.getStartTime() < DateTimeUtil.getCurrentTime() && b.getEndTime() > DateTimeUtil.getCurrentTime())
                    .findFirst().orElse(null);
            if (block == null) {
                logErrorRecordToCSV(
                        "Lỗi bản ghi số " + item.getOrderNumber() + ": Khung giờ không phù hợp với thời gian hiện tại"
                );
                return null;
            }

            Optional<ClassSubject> classSubject = classSubjectRepository
                    .findBySubjectAndBlockAndClassSubjectCodeAndFacilityChild(
                            subject.get(),
                            block,
                            item.getClassSubjectCode(),
                            facilityChild.get()
                    );
            if (classSubject.isEmpty()) {
                logErrorRecordToCSV(
                        "Lỗi bản ghi số " + item.getOrderNumber() + ": Lớp học không tồn tại trong hệ thống"
                );
                return null;
            }

            Optional<Staff> firstSupervisor = staffRepository.findByStaffCode(item.getFirstSupervisorCode());
            Optional<Staff> secondSupervisor = staffRepository.findByStaffCode(item.getSecondSupervisorCode());

            try {
                Integer.parseInt(item.getTotalStudent());
            } catch (NumberFormatException e) {
                logErrorRecordToCSV(
                        "Lỗi bản ghi số " + item.getOrderNumber() + ": Số lượng sinh viên không hợp lệ"
                );
                return null;
            }

            ExamShift examShift = new ExamShift();
            examShift.setShift(shift);
            examShift.setClassSubject(classSubject.get());
            examShift.setExamDate(startDate);
            examShift.setExamShiftCode(CodeGenerator.generateRandomCode());
            examShift.setFirstSupervisor(firstSupervisor.orElse(null));
            examShift.setSecondSupervisor(secondSupervisor.orElse(null));
            examShift.setRoom(room);
            examShift.setExamShiftStatus(ExamShiftStatus.NOT_STARTED);
            return examShift;
        } catch (Exception e) {
            logErrorRecordToCSV(
                    "Lỗi không xác định: " + e.getMessage()
            );
            return null;
        }
    }

    private void logErrorRecordToCSV(String content) {
        String fileName = "shift";
        historyLogUtils.logErrorRecord(
                content,
                fileName,
                (String) globalVariables.getGlobalVariable(SessionConstant.CURRENT_USER_ID),
                (String) globalVariables.getGlobalVariable(SessionConstant.CURRENT_USER_FACILITY_ID),
                LogFileType.EXAM_SHIFT
        );
    }

}