package fplhn.udpm.examdistribution.infrastructure.config.job.classsubject.jobconfig;

import fplhn.udpm.examdistribution.entity.ClassSubject;
import fplhn.udpm.examdistribution.infrastructure.config.job.classsubject.commonio.ClassSubjectExcelProcessor;
import fplhn.udpm.examdistribution.infrastructure.config.job.classsubject.commonio.ClassSubjectExcelRowMapper;
import fplhn.udpm.examdistribution.infrastructure.config.job.classsubject.commonio.ClassSubjectExcelWriter;
import fplhn.udpm.examdistribution.infrastructure.config.job.classsubject.model.ClassSubjectExcelRequest;
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
import org.springframework.transaction.PlatformTransactionManager;

import java.io.File;

@Configuration
@Slf4j
public class ExcelFileClassSubjectToDatabaseJobConfig {


    @Autowired
    @Qualifier("transactionManager")
    private PlatformTransactionManager transactionManager;

    @Value("${file.upload.class.subject.path}")
    private String fullPath;

    @Bean
    @StepScope
    ItemReader<ClassSubjectExcelRequest> excelReader(@Value("#{jobParameters['filePath']}") String path) {
        PoiItemReader<ClassSubjectExcelRequest> reader = new PoiItemReader<>();
        reader.setResource(new FileSystemResource(new File(fullPath + "/" + path)));
        reader.setLinesToSkip(1);
        reader.open(new ExecutionContext());
        reader.setRowMapper(excelRowMapper());
        reader.setLinesToSkip(1);
        return reader;
    }

    @StepScope
    private RowMapper<ClassSubjectExcelRequest> excelRowMapper() {
        return new ClassSubjectExcelRowMapper();
    }

    @Bean
    @StepScope
    ItemProcessor<ClassSubjectExcelRequest, ClassSubject> excelProcessor() {
        ClassSubjectExcelProcessor processor = new ClassSubjectExcelProcessor();
        processor.setFileName(fullPath);
        return processor;
    }

    @Bean
    @StepScope
    ItemWriter<ClassSubject> excelWriter() {
        return new ClassSubjectExcelWriter();
    }

    @Bean
    Step step2(
            @Qualifier("excelReader") ItemReader<ClassSubjectExcelRequest> reader,
            JobRepository jobRepository
    ) {
        return new StepBuilder("Lop-mon-excel-step", jobRepository)
                .<ClassSubjectExcelRequest, ClassSubject>chunk(100, transactionManager)
                .reader(reader)
                .processor(item -> excelProcessor().process(item))
                .writer(chunk -> excelWriter().write(chunk))
                .build();
    }

    @Bean
    Job excelFileToDatabaseJobClassSubject(@Qualifier("step2") Step step2, JobRepository jobRepository) {
        return new JobBuilder("Excel-File-To-Database-Job", jobRepository)
                .incrementer(new RunIdIncrementer())
                .start(step2)
                .build();
    }

}
