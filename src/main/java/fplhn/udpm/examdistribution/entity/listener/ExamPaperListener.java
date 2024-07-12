package fplhn.udpm.examdistribution.entity.listener;

import fplhn.udpm.examdistribution.entity.ExamPaper;
import fplhn.udpm.examdistribution.infrastructure.config.redis.service.RedisService;
import fplhn.udpm.examdistribution.infrastructure.constant.ExamPaperType;
import fplhn.udpm.examdistribution.infrastructure.constant.RedisPrefixConstant;
import jakarta.persistence.PreRemove;
import jakarta.persistence.PreUpdate;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
public class ExamPaperListener {

    private final RedisService redisService;

    @PreUpdate
    public void preUpdate(ExamPaper examPaper) {
        String key = "";
        if (examPaper.getExamPaperType().equals(ExamPaperType.SAMPLE_EXAM_PAPER)) {
            key = RedisPrefixConstant.REDIS_PREFIX_SAMPLE_EXAM_PAPER + examPaper.getId();
        } else {
            key = RedisPrefixConstant.REDIS_PREFIX_EXAM_PAPER + examPaper.getId();
        }
        if (redisService.get(key) != null) {
            redisService.delete(key);
        }
    }

    @PreRemove
    public void preRemove(ExamPaper examPaper) {
        String key = RedisPrefixConstant.REDIS_PREFIX_EXAM_PAPER_APPROVAL + examPaper.getId();
        if (redisService.get(key) != null) {
            redisService.delete(key);
        }
    }

}
