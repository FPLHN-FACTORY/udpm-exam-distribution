package fplhn.udpm.examdistribution.infrastructure.config.job.classsubject.commonio;

import fplhn.udpm.examdistribution.entity.ClassSubject;
import fplhn.udpm.examdistribution.infrastructure.config.job.classsubject.repository.ClassSubjectExcelCustomRepository;
import jakarta.transaction.Transactional;
import lombok.extern.slf4j.Slf4j;
import org.springframework.batch.item.Chunk;
import org.springframework.batch.item.ItemWriter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Slf4j
@Component
@Transactional
public class ClassSubjectExcelWriter implements ItemWriter<ClassSubject> {

    @Autowired
    private ClassSubjectExcelCustomRepository classSubjectExcelCustomRepository;

    @Override
    public void write(Chunk<? extends ClassSubject> chunk) {
        try {
            if (chunk != null) {
                log.info("Number of items in chunk: " + chunk.getItems().size());
                for (ClassSubject classSubject : chunk.getItems()) {
                   ClassSubject classSubjectSave =  classSubjectExcelCustomRepository.save(classSubject);
                   log.info("Class subject save: " + classSubjectSave.getClassSubjectCode());
                }
            } else {
                log.info("Chunk or items in chunk is null");
            }
        } catch (Exception e) {
            log.error("Error writing chunk : " + chunk, e);
        }
    }
}
