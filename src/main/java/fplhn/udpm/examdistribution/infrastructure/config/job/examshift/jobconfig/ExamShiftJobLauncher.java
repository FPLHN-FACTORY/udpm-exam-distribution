package fplhn.udpm.examdistribution.infrastructure.config.job.examshift.jobconfig;

import fplhn.udpm.examdistribution.infrastructure.config.upload.FileUploadService;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;
import org.springframework.batch.core.ExitStatus;
import org.springframework.batch.core.Job;
import org.springframework.batch.core.JobExecution;
import org.springframework.batch.core.JobParameter;
import org.springframework.batch.core.JobParameters;
import org.springframework.batch.core.launch.JobLauncher;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.atomic.AtomicBoolean;

@Component
@Slf4j
@NoArgsConstructor
public class ExamShiftJobLauncher {

    private final AtomicBoolean enabled = new AtomicBoolean(false);

    @Setter(onMethod_ = {@Autowired}, onParam_ = {@Qualifier("saveExamShiftToDatabaseJob")})
    private Job job;

    @Setter(onMethod_ = {@Autowired})
    private JobLauncher jobLauncher;

    @Setter(onMethod_ = {@Autowired}, onParam_ = {@Qualifier("fileUploadShiftServiceImpl")})
    private FileUploadService storageService;

    @Setter
    private String filePathExamShift;


    @Scheduled(cron = "${excel.file.to.database.job.cron}")
    void launchExcelToDatabaseJob() {
        if (enabled.get() && filePathExamShift != null) {
            try {
                log.info("Launching excel to database job");
                JobExecution jobExecution = jobLauncher.run(job, newExecution());
                ExitStatus exitStatus = jobExecution.getExitStatus();
                log.info("Exit status: {}", exitStatus);
                if (exitStatus.getExitCode().equals(ExitStatus.COMPLETED.getExitCode())) {
                    storageService.deleteAll();
                }
                if (exitStatus.getExitCode().equals(ExitStatus.FAILED.getExitCode())) {
                    log.error("Error launching excel to database job");
                }
            } catch (Exception e) {
                log.error("Error launching excel to database job", e);
            } finally {
                filePathExamShift = null;
                disable();
            }
        }
    }

    private JobParameters newExecution() {
        Map<String, JobParameter<?>> parameters = new HashMap<>();
        parameters.put("time", new JobParameter<>(new Date(), Date.class));
        parameters.put("filePathExamShift", new JobParameter<>(filePathExamShift, String.class));
        return new JobParameters(parameters);
    }

    public void enable() {
        enabled.set(true);
    }

    public void disable() {
        enabled.set(false);
    }


}
