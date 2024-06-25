package fplhn.udpm.examdistribution.core.headoffice.department.department.model.response;

import fplhn.udpm.examdistribution.core.common.base.HasOrderNumber;
import fplhn.udpm.examdistribution.core.common.base.IsIdentify;
import org.springframework.beans.factory.annotation.Value;

public interface DepartmentResponse extends IsIdentify, HasOrderNumber {

    @Value("#{target.departmentName}")
    String getDepartmentName();

    @Value("#{target.departmentCode}")
    String getDepartmentCode();

    @Value("#{target.departmentStatus}")
    Long getDepartmentStatus();

    @Value("#{target.createdDate}")
    Long getCreatedDate();

}
