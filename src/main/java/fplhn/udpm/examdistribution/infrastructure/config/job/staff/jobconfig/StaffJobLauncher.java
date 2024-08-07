package fplhn.udpm.examdistribution.infrastructure.config.job.staff.jobconfig;

import fplhn.udpm.examdistribution.infrastructure.config.job.staff.service.UploadStaffService;
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
public class StaffJobLauncher {

    private final AtomicBoolean enabled = new AtomicBoolean(false);

    @Setter(onMethod_ = @Autowired, onParam_ = @Qualifier("excelFileToDatabaseJob"))
    private Job job;

    @Setter(onMethod_ = @Autowired)
    private JobLauncher jobLauncher;

    @Setter(onMethod_ = @Autowired)
    private UploadStaffService storageService;

    @Setter
    private String fullPathFileName;


    @Scheduled(cron = "${excel.file.to.database.job.cron}")
    void launchExcelToDatabaseJob() {
        if (enabled.get() && fullPathFileName != null) {
            try {
                JobExecution jobExecution = jobLauncher.run(job, newExecution());
                ExitStatus exitStatus = jobExecution.getExitStatus();
                if (exitStatus.getExitCode().equals(ExitStatus.COMPLETED.getExitCode())) {
                    storageService.deleteAll();
                    disable();
                }
            } catch (Exception e) {
                e.printStackTrace(System.err);
            }
        }
    }

    private JobParameters newExecution() {
        Map<String, JobParameter<?>> parameters = new HashMap<>();
        parameters.put("time", new JobParameter<>(new Date(), Date.class));
        parameters.put("fullPathFileName", new JobParameter<>(fullPathFileName, String.class));
        return new JobParameters(parameters);
    }

    public void enable() {
        enabled.set(true);
    }

    public void disable() {
        enabled.set(false);
    }


}
