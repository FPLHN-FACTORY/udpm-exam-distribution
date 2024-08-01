package fplhn.udpm.examdistribution.core.common.service;

import fplhn.udpm.examdistribution.core.common.base.ResponseObject;
import fplhn.udpm.examdistribution.core.common.model.SemesterInfoResponse;
import fplhn.udpm.examdistribution.core.common.repository.CMSemesterExtendRepository;
import fplhn.udpm.examdistribution.utils.SessionHelper;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
@RequiredArgsConstructor
public class CommonServiceHelper {

    private final CMSemesterExtendRepository cMSemesterExtendRepository;

    private final SessionHelper sessionHelper;

    public ResponseObject<?> getSemesterInfo() {

        List<SemesterInfoResponse> semesterInfos = cMSemesterExtendRepository.getSemesterInfos();

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
