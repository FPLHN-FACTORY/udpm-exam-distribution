package fplhn.udpm.examdistribution.entity.listener;

import fplhn.udpm.examdistribution.entity.Subject;
import fplhn.udpm.examdistribution.infrastructure.config.redis.service.RedisService;
import fplhn.udpm.examdistribution.infrastructure.constant.RedisPrefixConstant;
import jakarta.persistence.PreUpdate;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
public class SubjectListener {

    private final RedisService redisService;

    @PreUpdate
    public void preUpdate(Subject subject) {
        String key = RedisPrefixConstant.REDIS_PREFIX_DETAIL_EXAM_RULE + subject.getId();
        if (redisService.get(key) != null) {
            redisService.delete(key);
        }
    }

}
