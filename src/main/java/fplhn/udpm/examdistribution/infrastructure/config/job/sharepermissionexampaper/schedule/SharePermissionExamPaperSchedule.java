package fplhn.udpm.examdistribution.infrastructure.config.job.sharepermissionexampaper.schedule;

import fplhn.udpm.examdistribution.entity.Block;
import fplhn.udpm.examdistribution.entity.SharePermissionExamPaper;
import fplhn.udpm.examdistribution.infrastructure.config.drive.service.GoogleDriveFileService;
import fplhn.udpm.examdistribution.infrastructure.config.job.sharepermissionexampaper.repository.SPEPBlockExtendRepository;
import fplhn.udpm.examdistribution.infrastructure.config.job.sharepermissionexampaper.repository.SPEPSharePermissionExamPaperExtendRepository;
import fplhn.udpm.examdistribution.utils.DateTimeUtil;
import lombok.RequiredArgsConstructor;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.time.LocalDate;
import java.util.Date;
import java.util.Optional;

@Component
@RequiredArgsConstructor
public class SharePermissionExamPaperSchedule {

    private final SPEPSharePermissionExamPaperExtendRepository sharePermissionExamPaperRepository;

    private final SPEPBlockExtendRepository blockRepository;

    private final GoogleDriveFileService googleDriveFileService;

    @Scheduled(cron = "${share.permission.exam.paper.job.cron}")
    void cronRemovePermissionExamPaper() {
        this.removePermissionExamPaper();
    }

    private void removePermissionExamPaper() {
        Optional<Block> blockOptional = blockRepository.findById(this.findBlockId());
        if (blockOptional.isPresent()) {
            Date blockEndDate = new Date(blockOptional.get().getEndTime());

            LocalDate blockEndLocalDate = DateTimeUtil.convertToLocalDate(blockEndDate);
            LocalDate currentLocalDate = LocalDate.now();

            if (blockEndLocalDate.equals(currentLocalDate)) {
                for (SharePermissionExamPaper sharePermissionExamPaper : sharePermissionExamPaperRepository.listSharePermissionExamPaper(
                        this.findBlockId()
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

    private String findBlockId() {
        String blockId = "";
        Long now = new Date().getTime();
        for (Block block : blockRepository.findAll()) {
            if (block.getStartTime() < now && now < block.getEndTime()) {
                blockId = block.getId();
                break;
            }
        }
        return blockId;
    }

}
