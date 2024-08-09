package fplhn.udpm.examdistribution.core.headoffice.subject.service.impl;

import fplhn.udpm.examdistribution.core.common.base.ResponseObject;
import fplhn.udpm.examdistribution.core.headoffice.subject.repository.SDepartmentSubjectRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class CommonService {

    private final SDepartmentSubjectRepository SDepartmentSubjectRepository;

    public ResponseObject<?> getAllDepartmentSubject() {
        return new ResponseObject<>(
                SDepartmentSubjectRepository.getAllDepartment(),
                HttpStatus.OK,
                "Lấy danh sách bộ môn thành công"
        );
    }

}
