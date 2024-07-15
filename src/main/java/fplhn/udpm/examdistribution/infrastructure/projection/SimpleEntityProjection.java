package fplhn.udpm.examdistribution.infrastructure.projection;

import fplhn.udpm.examdistribution.entity.base.IsIdentified;
import org.springframework.data.rest.core.config.Projection;

@Projection(types = {})
public interface SimpleEntityProjection extends IsIdentified {
    String getName();
}
