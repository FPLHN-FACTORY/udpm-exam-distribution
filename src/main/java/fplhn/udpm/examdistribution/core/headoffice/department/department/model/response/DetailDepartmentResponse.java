package fplhn.udpm.examdistribution.core.headoffice.department.department.model.response;

import fplhn.udpm.examdistribution.core.common.base.IsIdentify;
import org.springframework.beans.factory.annotation.Value;

public interface DetailDepartmentResponse extends IsIdentify {

    @Value("#{target.departmentCode}")
    String getDepartmentCode();

    @Value("#{target.departmentName}")
    String getDepartmentName();

}
