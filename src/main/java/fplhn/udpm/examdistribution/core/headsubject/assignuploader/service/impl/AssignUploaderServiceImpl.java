package fplhn.udpm.examdistribution.core.headsubject.assignuploader.service.impl;

import fplhn.udpm.examdistribution.core.common.base.PageableObject;
import fplhn.udpm.examdistribution.core.common.base.ResponseObject;
import fplhn.udpm.examdistribution.core.headsubject.assignuploader.model.request.FindStaffRequest;
import fplhn.udpm.examdistribution.core.headsubject.assignuploader.model.request.FindSubjectRequest;
import fplhn.udpm.examdistribution.core.headsubject.assignuploader.repository.AUSubjectExtendRepository;
import fplhn.udpm.examdistribution.core.headsubject.assignuploader.service.AssignUploaderService;
import fplhn.udpm.examdistribution.utils.Helper;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class AssignUploaderServiceImpl implements AssignUploaderService {

    private final AUSubjectExtendRepository subjectRepository;

    @Override
    public ResponseObject<?> getAllSubject(String departmentFacilityId, FindSubjectRequest request) {
        Pageable pageable = Helper.createPageable(request, "createdDate");
        return new ResponseObject<>(
                PageableObject.of(subjectRepository.getAllSubject(pageable, departmentFacilityId, request)),
                HttpStatus.OK,
                "Lấy thành công danh sách môn học"
        );
    }

    @Override
    public ResponseObject<?> getAllStaff(String departmentFacilityId, FindStaffRequest request) {
        Pageable pageable = Helper.createPageable(request, "createdDate");
        return new ResponseObject<>(
                PageableObject.of(subjectRepository.getAllStaff(pageable, departmentFacilityId, request)),
                HttpStatus.OK,
                "Lấy thành công danh sách nhân viên"
        );
    }

}
