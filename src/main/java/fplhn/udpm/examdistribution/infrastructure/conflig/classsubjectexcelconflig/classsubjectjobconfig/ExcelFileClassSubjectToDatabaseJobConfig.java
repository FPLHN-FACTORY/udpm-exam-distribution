package fplhn.udpm.examdistribution.infrastructure.conflig.classsubjectexcelconflig.classsubjectjobconfig;

import fplhn.udpm.examdistribution.entity.ClassSubject;
import fplhn.udpm.examdistribution.infrastructure.conflig.classsubjectexcelconflig.commonio.classsubject.ClassSubjectExcelProcessor;
import fplhn.udpm.examdistribution.infrastructure.conflig.classsubjectexcelconflig.commonio.classsubject.ClassSubjectExcelRowMapper;
import fplhn.udpm.examdistribution.infrastructure.conflig.classsubjectexcelconflig.commonio.classsubject.ClassSubjectExcelWriter;
import fplhn.udpm.examdistribution.infrastructure.conflig.classsubjectexcelconflig.model.request.ClassSubjectExcelRequest;
import lombok.extern.slf4j.Slf4j;
import org.springframework.batch.core.*;
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

    @Bean
    @StepScope
    ItemReader<ClassSubjectExcelRequest> excelReader(@Value("#{jobParameters['filePath']}") String path) {
        PoiItemReader<ClassSubjectExcelRequest> reader = new PoiItemReader<>();
        reader.setResource(new FileSystemResource(new File("src/main/resources/excel/" + path)));
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
        return new ClassSubjectExcelProcessor();
    }

    @Bean
    @StepScope
    ItemWriter<ClassSubject> excelWriter() {
        return new ClassSubjectExcelWriter();
    }

    @Bean
    Step step2(@Qualifier("excelReader") ItemReader<ClassSubjectExcelRequest> reader,
               JobRepository jobRepository
    ) {
        return new StepBuilder("Lop-mon-excel-step", jobRepository)
                .<ClassSubjectExcelRequest, ClassSubject>chunk(100, transactionManager)
                .reader(reader)
                .processor(item -> excelProcessor().process(item))
                .writer(chunk -> excelWriter().write(chunk))
                .listener(new StepExecutionListener() {
                    @Override
                    public ExitStatus afterStep(StepExecution stepExecution) {
                        if (stepExecution.getExitStatus().equals(ExitStatus.COMPLETED)) {
                            try {
                                if (reader.read() == null) {
                                    log.info("No items found in excel file");
                                    return ExitStatus.FAILED;
                                }
                                return ExitStatus.COMPLETED;
                            } catch (Exception e) {
                                throw new RuntimeException(e);
                            }
                        }
                        return stepExecution.getExitStatus();
                    }
                })
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
