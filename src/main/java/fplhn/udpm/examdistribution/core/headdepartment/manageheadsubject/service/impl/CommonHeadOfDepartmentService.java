package fplhn.udpm.examdistribution.core.headdepartment.manageheadsubject.service.impl;

import fplhn.udpm.examdistribution.core.common.base.ResponseObject;
import fplhn.udpm.examdistribution.core.headdepartment.manageheadsubject.model.response.SemesterInfoResponse;
import fplhn.udpm.examdistribution.core.headdepartment.manageheadsubject.repository.HDSemesterExtendRepository;
import fplhn.udpm.examdistribution.utils.SessionHelper;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
@RequiredArgsConstructor
public class CommonHeadOfDepartmentService {

    private final HDSemesterExtendRepository hdSemesterExtendRepository;

    private final SessionHelper sessionHelper;

    public ResponseObject<?> getSemesterInfo() {

        List<SemesterInfoResponse> semesterInfos = hdSemesterExtendRepository.getSemesterInfos();

        String currentSemester = sessionHelper.getCurrentSemesterId();

        semesterInfos.sort((o1, o2) -> {
            if (o1.getId().equals(currentSemester)) {
                return -1;
            } else if (o2.getId().equals(currentSemester)) {
                return 1;
            } else {
                return o2.getId().compareTo(o1.getId());
            }
        });

        return new ResponseObject<>(
                semesterInfos,
                HttpStatus.OK,
                "Lấy thông tin học kỳ thành công"
        );
    }

}
