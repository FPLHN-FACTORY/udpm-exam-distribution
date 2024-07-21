package fplhn.udpm.examdistribution.core.headdepartment.manageheadsubject.service.impl;

import fplhn.udpm.examdistribution.core.common.base.ResponseObject;
import fplhn.udpm.examdistribution.core.headdepartment.manageheadsubject.repository.HDSemesterExtendRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class CommonHeadOfDepartmentService {

    private final HDSemesterExtendRepository hdSemesterExtendRepository;

    public ResponseObject<?> getSemesterInfo() {
        return new ResponseObject<>(
                hdSemesterExtendRepository.getSemesterInfos(),
                HttpStatus.OK,
                "Lấy thông tin học kỳ thành công"
        );
    }

}
