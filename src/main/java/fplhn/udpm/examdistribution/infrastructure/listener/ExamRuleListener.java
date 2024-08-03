package fplhn.udpm.examdistribution.infrastructure.listener;

import fplhn.udpm.examdistribution.entity.ExamRule;
import fplhn.udpm.examdistribution.infrastructure.config.redis.service.RedisService;
import fplhn.udpm.examdistribution.infrastructure.constant.RedisPrefixConstant;
import jakarta.persistence.PreUpdate;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
public class ExamRuleListener {

    private final RedisService redisService;

    @PreUpdate
    public void preUpdate(ExamRule examRule) {
        String key = RedisPrefixConstant.REDIS_PREFIX_EXAM_RULE + examRule.getId();
        if (redisService.get(key) != null) {
            redisService.delete(key);
        }
    }

}
