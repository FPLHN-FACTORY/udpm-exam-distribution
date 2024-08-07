package fplhn.udpm.examdistribution.infrastructure.config.job.staff.jobconfig;

import fplhn.udpm.examdistribution.infrastructure.config.job.staff.commonio.StaffProcessor;
import fplhn.udpm.examdistribution.infrastructure.config.job.staff.commonio.StaffRowMapper;
import fplhn.udpm.examdistribution.infrastructure.config.job.staff.commonio.StaffWriter;
import fplhn.udpm.examdistribution.infrastructure.config.job.staff.model.dto.TransferStaffRole;
import fplhn.udpm.examdistribution.infrastructure.config.job.staff.model.request.StaffExcelRequest;
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
public class StaffJobConfig {

    @Setter(onMethod_ = {@Autowired}, onParam_ = {@Qualifier("transactionManager")})
    private PlatformTransactionManager transactionManager;

    @Value("${file.upload.staff.path}")
    private String fullPath;

    @Bean
    @StepScope
    ItemReader<StaffExcelRequest> excelStaffReader(@Value("#{jobParameters['fullPathFileName']}") String path) {
        try {
            PoiItemReader<StaffExcelRequest> reader = new PoiItemReader<>();
            Resource resource = new FileSystemResource(new File(fullPath + "/" + path));
            if (!resource.exists()) throw new RuntimeException("Could not read the file!");

            reader.setResource(resource);
            reader.setLinesToSkip(1);
            reader.open(new ExecutionContext());
            reader.setRowMapper(rowMapper());
            reader.setLinesToSkip(1);
            return reader;
        } catch (Exception e) {
            e.printStackTrace(System.out);
            throw new RuntimeException(e);
        }
    }

    @StepScope
    private RowMapper<StaffExcelRequest> rowMapper() {
        return new StaffRowMapper();
    }

    @StepScope
    @Bean
    @Qualifier("staffProcessor")
    ItemProcessor<StaffExcelRequest, TransferStaffRole> staffProcessor() {
        StaffProcessor staffProcessor = new StaffProcessor();
        staffProcessor.setFileName(fullPath);
        return staffProcessor;
    }

    @StepScope
    @Bean
    ItemWriter<TransferStaffRole> excelStaffWriter() {
        return new StaffWriter();
    }

    @Bean
    Step step1(@Qualifier("excelStaffReader") ItemReader<StaffExcelRequest> excelRequestItemReader
            , JobRepository jobRepository) {
        return new StepBuilder("Excel-File-To-Database-Step", jobRepository)
                .<StaffExcelRequest, TransferStaffRole>chunk(100, transactionManager)
                .reader(excelRequestItemReader)
                .processor(item -> staffProcessor().process(item))
                .writer(chunk -> excelStaffWriter().write(chunk))
                .build();
    }

    @Bean
    Job excelFileToDatabaseJob(@Qualifier("step1") Step step1, JobRepository jobRepository) {
        return new JobBuilder("Excel-File-To-Database-Job", jobRepository)
                .incrementer(new RunIdIncrementer())
                .start(step1)
                .build();
    }


}
