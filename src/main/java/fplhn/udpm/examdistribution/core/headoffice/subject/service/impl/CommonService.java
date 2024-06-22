package fplhn.udpm.examdistribution.core.headoffice.subject.service.impl;

import fplhn.udpm.examdistribution.core.common.base.ResponseObject;
import fplhn.udpm.examdistribution.core.headoffice.subject.repository.DepartmentSubjectRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class CommonService {

    private final DepartmentSubjectRepository departmentSubjectRepository;

    public ResponseObject<?> getAllDepartmentSubject() {
        return new ResponseObject<>(
                departmentSubjectRepository.getAllDepartment(),
                HttpStatus.OK,
                "Get all department successfully"
        );
    }

}
