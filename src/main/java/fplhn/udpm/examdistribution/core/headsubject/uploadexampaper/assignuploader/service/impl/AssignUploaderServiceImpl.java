package fplhn.udpm.examdistribution.core.headsubject.uploadexampaper.assignuploader.service.impl;

import fplhn.udpm.examdistribution.core.common.base.PageableObject;
import fplhn.udpm.examdistribution.core.common.base.ResponseObject;
import fplhn.udpm.examdistribution.core.headsubject.uploadexampaper.assignuploader.repository.AUAssignUploaderExtendRepository;
import fplhn.udpm.examdistribution.core.headsubject.uploadexampaper.assignuploader.model.request.AssignUploaderRequest;
import fplhn.udpm.examdistribution.core.headsubject.uploadexampaper.assignuploader.model.request.FindStaffRequest;
import fplhn.udpm.examdistribution.core.headsubject.uploadexampaper.assignuploader.model.request.FindSubjectRequest;
import fplhn.udpm.examdistribution.core.headsubject.uploadexampaper.assignuploader.repository.AUStaffExtendRepository;
import fplhn.udpm.examdistribution.core.headsubject.uploadexampaper.assignuploader.repository.AUSubjectExtendRepository;
import fplhn.udpm.examdistribution.core.headsubject.uploadexampaper.assignuploader.service.AssignUploaderService;
import fplhn.udpm.examdistribution.entity.AssignUploader;
import fplhn.udpm.examdistribution.entity.Staff;
import fplhn.udpm.examdistribution.entity.Subject;
import fplhn.udpm.examdistribution.infrastructure.constant.EntityStatus;
import fplhn.udpm.examdistribution.infrastructure.constant.SessionConstant;
import fplhn.udpm.examdistribution.utils.Helper;
import jakarta.servlet.http.HttpSession;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
@RequiredArgsConstructor
public class AssignUploaderServiceImpl implements AssignUploaderService {

    private final AUSubjectExtendRepository subjectRepository;

    private final AUStaffExtendRepository staffExtendRepository;

    private final AUAssignUploaderExtendRepository assignUploaderRepository;

    private final HttpSession httpSession;

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
        String userId = httpSession.getAttribute(SessionConstant.CURRENT_USER_ID).toString();
        return new ResponseObject<>(
                PageableObject.of(staffExtendRepository.getAllStaff(pageable, departmentFacilityId, request, userId)),
                HttpStatus.OK,
                "Lấy thành công danh sách nhân viên"
        );
    }

    @Override
    public ResponseObject<?> addOrDelAssignUploader(AssignUploaderRequest request) {
        Optional<AssignUploader> isAssignUploaderExist = assignUploaderRepository.isAssignUploaderExist(request);
        if (isAssignUploaderExist.isPresent()) {
            assignUploaderRepository.delete(isAssignUploaderExist.get());
            return new ResponseObject<>(
                    null,
                    HttpStatus.OK,
                    "Xóa thành công giảng viên " + isAssignUploaderExist.get().getStaff().getName() + " khỏi danh sách người tải đề"
            );
        }

        Optional<Staff> isStaffExist = staffExtendRepository.findById(request.getStaffId());
        if (isStaffExist.isEmpty()) {
            return new ResponseObject<>(
                    null,
                    HttpStatus.NOT_ACCEPTABLE,
                    "Không tìm thấy nhân viên này"
            );
        }

        Optional<Subject> isSubjectExist = subjectRepository.findById(request.getSubjectId());
        if (isSubjectExist.isEmpty()) {
            return new ResponseObject<>(
                    null,
                    HttpStatus.NOT_ACCEPTABLE,
                    "Không tìm thấy môn học này"
            );
        }


        AssignUploader postAssignUploader = new AssignUploader();
        postAssignUploader.setStaff(isStaffExist.get());
        postAssignUploader.setSubject(isSubjectExist.get());
        postAssignUploader.setStatus(EntityStatus.ACTIVE);

        AssignUploader postedAssignUploader = assignUploaderRepository.save(postAssignUploader);

        return new ResponseObject<>(
                null,
                HttpStatus.OK,
                "Phân công giảng viên " + postedAssignUploader.getStaff().getName() + " làm người tải đề cho môn học "
                + postAssignUploader.getSubject().getName() + " thành công"
        );
    }

}
