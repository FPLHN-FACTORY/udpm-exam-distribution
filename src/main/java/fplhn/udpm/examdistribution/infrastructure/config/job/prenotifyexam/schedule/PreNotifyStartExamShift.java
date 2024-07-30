package fplhn.udpm.examdistribution.infrastructure.config.job.prenotifyexam.schedule;

import fplhn.udpm.examdistribution.core.teacher.examshift.repository.TExamShiftExtendRepository;
import fplhn.udpm.examdistribution.entity.ExamShift;
import fplhn.udpm.examdistribution.entity.Semester;
import fplhn.udpm.examdistribution.entity.Staff;
import fplhn.udpm.examdistribution.infrastructure.config.email.service.EmailService;
import fplhn.udpm.examdistribution.infrastructure.config.job.prenotifyexam.repository.SCHDDepartmentFacilityRepository;
import fplhn.udpm.examdistribution.infrastructure.config.job.prenotifyexam.repository.SCHDExamShiftExtendRepository;
import fplhn.udpm.examdistribution.infrastructure.config.job.prenotifyexam.repository.SCHDSemesterExtendRepository;
import fplhn.udpm.examdistribution.infrastructure.config.job.prenotifyexam.response.ExamShiftCommingInfoResponse;
import fplhn.udpm.examdistribution.infrastructure.constant.Shift;
import fplhn.udpm.examdistribution.utils.DateTimeUtil;
import fplhn.udpm.examdistribution.utils.PasswordUtils;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.time.LocalTime;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Component
@Slf4j
public class PreNotifyStartExamShift {

    @Setter(onMethod_ = {@Autowired})
    private EmailService emailService;

    @Setter(onMethod_ = {@Autowired})
    private SCHDExamShiftExtendRepository schdExamShiftExtendRepository;

    @Setter(onMethod_ = {@Autowired})
    private SCHDDepartmentFacilityRepository schdDepartmentFacilityRepository;

    @Setter(onMethod_ = {@Autowired})
    private SCHDSemesterExtendRepository semesterRepository;

    @Setter(onMethod_ = {@Autowired})
    private TExamShiftExtendRepository tExamShiftExtendRepository;

    @Scheduled(cron = "${schedule.prepare.exam.cron}")
    public void preNotifyStartExamShift() {
        log.info("----------------- Start pre notify start exam shift -----------------");
        Shift shift = getCommingShift();
        if (shift == null) {
            log.info("No shift is comming");
            log.info("----------------- End pre notify start exam shift -----------------");
            return;
        }

        List<ExamShift> examShiftsComming = schdExamShiftExtendRepository
                .findByExamDateAndShift(DateTimeUtil.getCurrentTime(), shift);

        if (examShiftsComming.isEmpty()) {
            log.info("No exam shift is comming");
            log.info("----------------- End pre notify start exam shift -----------------");
            return;
        }

        List<ExamShiftInfoAttachWithPassword> examShiftInfoAttachWithPasswords = generateExamShiftInfo(examShiftsComming);

        List<ExamShiftCommingInfoResponse> examShiftCommingInfoResponses = schdExamShiftExtendRepository
                .getContentSendMail(
                        shift.name(),
                        DateTimeUtil.getCurrentTime(),
                        semesterRepository.getReferenceById(getCurrentSemesterId()).getId()
                );

        if (examShiftCommingInfoResponses.isEmpty()) {
            log.info("No exam shift is comming");
            log.info("----------------- End pre notify start exam shift -----------------");
            return;
        }

        processExamShiftCommingInfoResponses(examShiftCommingInfoResponses, examShiftInfoAttachWithPasswords);

        notifyHeadsAndSupervisors(examShiftCommingInfoResponses, examShiftInfoAttachWithPasswords);

        log.info("----------------- End pre notify start exam shift -----------------");
    }

    private Shift getCommingShift() {
        LocalTime now = LocalTime.now().plusMinutes(15);
        for (Shift shift : Shift.values()) {
            if ((now.isAfter(shift.getStartTime()) || now.equals(shift.getStartTime())) && now.isBefore(shift.getEndTime())) {
                return shift;
            }
        }
        return null;
    }

    private List<ExamShiftInfoAttachWithPassword> generateExamShiftInfo(List<ExamShift> examShifts) {
        return examShifts.stream().map(examShift -> {
            String password = PasswordUtils.generatePassword();
            String salt = PasswordUtils.generateSalt();
            return new ExamShiftInfoAttachWithPassword(
                    examShift.getExamShiftCode(),
                    password,
                    PasswordUtils.getSecurePassword(password, salt),
                    salt
            );
        }).collect(Collectors.toList());
    }

    private void processExamShiftCommingInfoResponses(List<ExamShiftCommingInfoResponse> responses,
                                                      List<ExamShiftInfoAttachWithPassword> passwords) {
        responses.forEach(response -> {
            Optional<ExamShiftInfoAttachWithPassword> infoOptional = passwords.stream()
                    .filter(e -> e.getExamShiftCode().equals(response.getExamShiftCode()))
                    .findFirst();
            if (infoOptional.isPresent()) {
                response.setPassword(infoOptional.get().getPassword());
                schdExamShiftExtendRepository.findByExamShiftCode(response.getExamShiftCode())
                        .ifPresent(examShift -> {
                            examShift.setHash(infoOptional.get().getHash());
                            examShift.setSalt(infoOptional.get().getSalt());
                            schdExamShiftExtendRepository.save(examShift);
                        });
            } else {
                log.info("No password found for exam shift code: {}", response.getExamShiftCode());
            }
        });
    }

    private void notifyHeadsAndSupervisors(List<ExamShiftCommingInfoResponse> responses,
                                           List<ExamShiftInfoAttachWithPassword> passwords) {
        List<String> accountFptHeadDepartment = responses.stream()
                .map(ExamShiftCommingInfoResponse::getDepartmentFacilityId)
                .map(schdDepartmentFacilityRepository::findHeadDepartmentByDepartmentFacilityId)
                .filter(Optional::isPresent)
                .map(Optional::get)
                .map(Staff::getAccountFpt)
                .distinct()
                .toList();

        List<ListExamShiftAndEmailToHeadDepartment> listExamShiftAndEmailToHeadDepartments = accountFptHeadDepartment.stream()
                .map(accountFpt -> new ListExamShiftAndEmailToHeadDepartment(
                        responses.stream()
                                .filter(e -> e.getHeadDepartmentEmail().equals(accountFpt))
                                .collect(Collectors.toList()),
                        accountFpt))
                .toList();

        listExamShiftAndEmailToHeadDepartments.forEach(listExamShiftAndEmailToHeadDepartment ->
                emailService.sendEmailToHeadDepartmentAndSubjectWhenExamShiftComming(
                        listExamShiftAndEmailToHeadDepartment.getExamShiftCommingInfoResponses(),
                        listExamShiftAndEmailToHeadDepartment.getEmailHeadDepartment()
                )
        );

        List<String> accountFptHeadSubject = responses.stream()
                .map(ExamShiftCommingInfoResponse::getHeadSubjectEmail)
                .distinct()
                .toList();

        List<ListExamShiftAndEmailToHeadSubject> listExamShiftAndEmailToHeadSubjects = accountFptHeadSubject.stream()
                .map(accountFpt -> new ListExamShiftAndEmailToHeadSubject(
                        responses.stream()
                                .filter(e -> e.getHeadSubjectEmail().equals(accountFpt))
                                .collect(Collectors.toList()),
                        accountFpt))
                .toList();

        listExamShiftAndEmailToHeadSubjects.forEach(listExamShiftAndEmailToHeadSubject ->
                emailService.sendEmailToHeadDepartmentAndSubjectWhenExamShiftComming(
                        listExamShiftAndEmailToHeadSubject.getExamShiftCommingInfoResponses(),
                        listExamShiftAndEmailToHeadSubject.getEmailHeadSubject()
                )
        );

        List<String> examShiftCodes = responses.stream()
                .map(ExamShiftCommingInfoResponse::getExamShiftCode)
                .distinct()
                .toList();

        examShiftCodes.forEach(examShiftCode -> {
            Optional<ExamShiftInfoAttachWithPassword> infoOptional = passwords.stream()
                    .filter(e -> e.getExamShiftCode().equals(examShiftCode))
                    .findFirst();
            infoOptional.ifPresent(examShiftInfoAttachWithPassword -> emailService.sendEmailToSupervisorWhenOpenExamPaper(
                    tExamShiftExtendRepository.sendMailToSupervisorWhenOpenExamPaper(examShiftCode),
                    examShiftInfoAttachWithPassword.getPassword()
            ));
        });
    }

    @Setter
    @Getter
    @AllArgsConstructor
    private static class ExamShiftInfoAttachWithPassword {

        private String examShiftCode;

        private String password;

        private String hash;

        private String salt;

    }

    @Setter
    @Getter
    @AllArgsConstructor
    private static class ListExamShiftAndEmailToHeadDepartment {

        private List<ExamShiftCommingInfoResponse> examShiftCommingInfoResponses;

        private String emailHeadDepartment;

    }

    @Setter
    @Getter
    @AllArgsConstructor
    private static class ListExamShiftAndEmailToHeadSubject {

        private List<ExamShiftCommingInfoResponse> examShiftCommingInfoResponses;

        private String emailHeadSubject;

    }

    private String getCurrentSemesterId() {
        return semesterRepository.findAll().stream()
                .filter(semester -> semester.getStartTime() <= DateTimeUtil.getCurrentTime())
                .map(Semester::getId)
                .findFirst()
                .orElse(null);
    }
}
