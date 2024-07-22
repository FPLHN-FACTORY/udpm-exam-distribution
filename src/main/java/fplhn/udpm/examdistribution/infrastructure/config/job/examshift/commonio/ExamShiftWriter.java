package fplhn.udpm.examdistribution.infrastructure.config.job.examshift.commonio;

import fplhn.udpm.examdistribution.entity.ExamShift;
import fplhn.udpm.examdistribution.infrastructure.config.job.examshift.repository.IPExamShiftRepository;
import lombok.Setter;
import org.springframework.batch.item.Chunk;
import org.springframework.batch.item.ItemWriter;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.Optional;

public class ExamShiftWriter implements ItemWriter<ExamShift> {

    @Setter(onMethod_ = {@Autowired})
    private IPExamShiftRepository examShiftRepository;

    @Override
    public void write(Chunk<? extends ExamShift> chunk) throws Exception {
        if (chunk != null) {
            for (ExamShift examShift : chunk) {
                try {
                    Optional<ExamShift> examShiftOptional = examShiftRepository
                            .findByClassSubjectAndExamDateAndShift(examShift.getClassSubject(), examShift.getExamDate(), examShift.getShift());
                    if (examShiftOptional.isPresent()) {
                        ExamShift examShiftExist = examShiftOptional.get();
                        examShiftExist.setRoom(examShift.getRoom());
                        examShiftExist.setFirstSupervisor(examShift.getFirstSupervisor());
                        examShiftExist.setSecondSupervisor(examShift.getSecondSupervisor());
                        examShiftExist.setStatus(examShift.getStatus());
                        examShiftRepository.save(examShiftExist);
                    } else {
                        examShiftRepository.save(examShift);
                    }
                } catch (Exception e) {
                    e.printStackTrace(System.err);
                }
            }
        }
    }

}
