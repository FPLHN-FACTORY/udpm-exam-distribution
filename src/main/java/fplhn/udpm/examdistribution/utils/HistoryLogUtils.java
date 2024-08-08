package fplhn.udpm.examdistribution.utils;

import fplhn.udpm.examdistribution.entity.HistoryImport;
import fplhn.udpm.examdistribution.infrastructure.constant.LogFileType;
import fplhn.udpm.examdistribution.infrastructure.constant.LogType;
import fplhn.udpm.examdistribution.repository.FacilityRepository;
import fplhn.udpm.examdistribution.repository.HistoryImportRepository;
import fplhn.udpm.examdistribution.repository.StaffRepository;
import lombok.Setter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
public class HistoryLogUtils {

    @Setter(onMethod_ = {@Autowired})
    private HistoryImportRepository historyImportRepository;

    @Setter(onMethod_ = {@Autowired})
    private FacilityRepository facilityRepository;

    @Setter(onMethod_ = {@Autowired})
    private StaffRepository staffRepository;

    @Setter(onMethod_ = {@Autowired})
    private SessionHelper sessionHelper;

    public void logErrorRecord(String message, String fileName) {
        HistoryImport historyImport = new HistoryImport();
        historyImport.setFileName(fileName);
        historyImport.setFacility(facilityRepository.getReferenceById(sessionHelper.getCurrentUserFacilityId()));
        historyImport.setMessage(message);
        historyImport.setStaff(staffRepository.getReferenceById(sessionHelper.getCurrentUserId()));
        historyImport.setType(LogType.ERROR);
    }

    public void logErrorRecord(
            String message,
            String fileName,
            String staffId,
            String facilityId,
            LogFileType logFileType
    ) {
        HistoryImport historyImport = new HistoryImport();
        historyImport.setFileName(fileName);
        historyImport.setFacility(facilityRepository.getReferenceById(facilityId));
        historyImport.setMessage(message);
        historyImport.setStaff(staffRepository.getReferenceById(staffId));
        historyImport.setType(LogType.ERROR);
        historyImport.setLogFileType(logFileType);
        historyImportRepository.save(historyImport);
    }

    public void logSuccessRecord(String message, String fileName) {
        HistoryImport historyImport = new HistoryImport();
        historyImport.setFileName(fileName);
        historyImport.setFacility(facilityRepository.getReferenceById(sessionHelper.getCurrentUserFacilityId()));
        historyImport.setMessage(message);
        historyImport.setStaff(staffRepository.getReferenceById(sessionHelper.getCurrentUserId()));
        historyImport.setType(LogType.SUCCESS);
        historyImportRepository.save(historyImport);
    }

    public List<HistoryImport> getHistoryImport() {
        return historyImportRepository.findAll();
    }

    public List<HistoryImport> getHistoryImportByFacilityIdAndStaffIdAndFileType(
            String facilityId,
            String staffId,
            LogFileType logFileType
    ) {
        return historyImportRepository.findAllByFacility_IdAndStaff_IdAndLogFileType(
                facilityId,
                staffId,
                logFileType
        );
    }


}
