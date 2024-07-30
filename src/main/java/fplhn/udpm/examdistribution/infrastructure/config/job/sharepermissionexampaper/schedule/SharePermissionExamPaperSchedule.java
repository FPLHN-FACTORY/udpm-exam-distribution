package fplhn.udpm.examdistribution.infrastructure.config.job.sharepermissionexampaper.schedule;

import fplhn.udpm.examdistribution.entity.Block;
import fplhn.udpm.examdistribution.entity.SharePermissionExamPaper;
import fplhn.udpm.examdistribution.infrastructure.config.drive.service.GoogleDriveFileService;
import fplhn.udpm.examdistribution.infrastructure.config.job.sharepermissionexampaper.repository.SPEPBlockExtendRepository;
import fplhn.udpm.examdistribution.infrastructure.config.job.sharepermissionexampaper.repository.SPEPSharePermissionExamPaperExtendRepository;
import fplhn.udpm.examdistribution.utils.DateTimeUtil;
import fplhn.udpm.examdistribution.utils.SessionHelper;
import jakarta.annotation.PostConstruct;
import lombok.RequiredArgsConstructor;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.time.LocalDate;
import java.util.Date;
import java.util.Optional;

@Component
@RequiredArgsConstructor
public class SharePermissionExamPaperSchedule {

    private final SessionHelper sessionHelper;

    private final SPEPSharePermissionExamPaperExtendRepository sharePermissionExamPaperRepository;

    private final SPEPBlockExtendRepository blockRepository;

    private final GoogleDriveFileService googleDriveFileService;

    @PostConstruct
    void postConstruct() {
        removePermissionExamPaper();
    }

    @Scheduled(cron = "0 50 23 * * *")
    void cronRemovePermissionExamPaper() {
        removePermissionExamPaper();
    }

    private void removePermissionExamPaper() {
        Optional<Block> blockOptional = blockRepository.findById(sessionHelper.getCurrentBlockId());
        if (blockOptional.isPresent()) {
            Date blockEndDate = new Date(blockOptional.get().getEndTime());

            LocalDate blockEndLocalDate = DateTimeUtil.convertToLocalDate(blockEndDate);
            LocalDate currentLocalDate = LocalDate.now();

            if (blockEndLocalDate.equals(currentLocalDate)) {
                for (SharePermissionExamPaper sharePermissionExamPaper : sharePermissionExamPaperRepository.listSharePermissionExamPaper(
                        sessionHelper.getCurrentBlockId(),
                        sessionHelper.getCurrentUserFacilityId()
                )) {
                    googleDriveFileService.deleteShareFile(
                            sharePermissionExamPaper.getExamPaper().getPath(),
                            sharePermissionExamPaper.getPermissionId()
                    );

                    sharePermissionExamPaperRepository.delete(sharePermissionExamPaper);
                }
            }
        }
    }

}
