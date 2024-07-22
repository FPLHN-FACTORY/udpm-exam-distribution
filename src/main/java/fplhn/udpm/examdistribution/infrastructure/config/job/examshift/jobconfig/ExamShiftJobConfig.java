package fplhn.udpm.examdistribution.infrastructure.config.job.examshift.jobconfig;

import fplhn.udpm.examdistribution.entity.ExamShift;
import fplhn.udpm.examdistribution.infrastructure.config.job.examshift.commonio.ExamShiftWriter;
import fplhn.udpm.examdistribution.infrastructure.config.job.examshift.commonio.ShiftProcessor;
import fplhn.udpm.examdistribution.infrastructure.config.job.examshift.commonio.ShiftRowMapper;
import fplhn.udpm.examdistribution.infrastructure.config.job.examshift.model.ImportShiftRequest;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;
import org.springframework.batch.core.Job;
import org.springframework.batch.core.Step;
import org.springframework.batch.core.configuration.annotation.StepScope;
import org.springframework.batch.core.job.builder.JobBuilder;
import org.springframework.batch.core.launch.support.RunIdIncrementer;
import org.springframework.batch.core.repository.JobRepository;
import org.springframework.batch.core.step.builder.StepBuilder;
import org.springframework.batch.item.ExecutionContext;
import org.springframework.batch.item.ItemProcessor;
import org.springframework.batch.item.ItemReader;
import org.springframework.batch.item.ItemWriter;
import org.springframework.batch.item.excel.RowMapper;
import org.springframework.batch.item.excel.poi.PoiItemReader;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.io.FileSystemResource;
import org.springframework.core.io.Resource;
import org.springframework.transaction.PlatformTransactionManager;
import org.springframework.transaction.annotation.EnableTransactionManagement;

import java.io.File;

@Configuration
@Slf4j
@EnableTransactionManagement
public class ExamShiftJobConfig {

    @Setter(onMethod_ = {@Autowired}, onParam_ = {@Qualifier("transactionManager")})
    private PlatformTransactionManager transactionManager;

    @Value("${file.upload.exam.shift.path}")
    private String fullPath;

    @Bean
    @StepScope
    ItemReader<ImportShiftRequest> examShiftReader(@Value("#{jobParameters['filePathExamShift']}") String fileName) {
        try {
            PoiItemReader<ImportShiftRequest> reader = new PoiItemReader<>();
            Resource resource = new FileSystemResource(
                    new File(fullPath + "/" + fileName)
            );
            if (!resource.exists()) {
                throw new RuntimeException("Could not read the file!");
            }
            reader.setResource(resource);
            reader.setLinesToSkip(1);
            reader.open(new ExecutionContext());
            reader.setRowMapper(rowMapper());
            reader.setLinesToSkip(1);
            return reader;
        } catch (Exception e) {
            e.printStackTrace(System.err);
            throw new RuntimeException(e);
        }
    }

    @StepScope
    private RowMapper<ImportShiftRequest> rowMapper() {
        return new ShiftRowMapper();
    }

    @StepScope
    @Bean
    @Qualifier("shiftProcessor")
    ItemProcessor<ImportShiftRequest, ExamShift> examShiftProcessor() {
        return new ShiftProcessor();
    }

    @StepScope
    @Bean
    ItemWriter<ExamShift> examShiftWriter() {
        return new ExamShiftWriter();
    }

    @Bean
    Step firstStep(
            @Qualifier("examShiftReader") ItemReader<ImportShiftRequest> excelRequestItemReader
            , JobRepository jobRepository
    ) {
        return new StepBuilder("Import Exam Shift To Database - Step", jobRepository)
                .<ImportShiftRequest, ExamShift>chunk(100, transactionManager)
                .reader(excelRequestItemReader)
                .processor(item -> examShiftProcessor().process(item))
                .writer(chunk -> examShiftWriter().write(chunk))
                .build();
    }

    @Bean
    Job saveExamShiftToDatabaseJob(
            @Qualifier("firstStep") Step firstStepInJob,
            JobRepository jobRepository
    ) {
        return new JobBuilder("Import Exam Shift To Database - Job", jobRepository)
                .incrementer(new RunIdIncrementer())
                .start(firstStepInJob)
                .build();
    }

}
