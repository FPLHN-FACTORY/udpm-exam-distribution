package fplhn.udpm.examdistribution.core.headsubject.assignuploader.service.impl;

import fplhn.udpm.examdistribution.core.common.base.PageableObject;
import fplhn.udpm.examdistribution.core.common.base.ResponseObject;
import fplhn.udpm.examdistribution.core.headsubject.assignuploader.model.request.AssignUploaderRequest;
import fplhn.udpm.examdistribution.core.headsubject.assignuploader.model.request.FindStaffRequest;
import fplhn.udpm.examdistribution.core.headsubject.assignuploader.model.request.FindSubjectRequest;
import fplhn.udpm.examdistribution.core.headsubject.assignuploader.repository.AUAssignUploaderExtendRepository;
import fplhn.udpm.examdistribution.core.headsubject.assignuploader.repository.AUStaffExtendRepository;
import fplhn.udpm.examdistribution.core.headsubject.assignuploader.repository.AUSubjectExtendRepository;
import fplhn.udpm.examdistribution.core.headsubject.assignuploader.service.AssignUploaderService;
import fplhn.udpm.examdistribution.entity.AssignUploader;
import fplhn.udpm.examdistribution.entity.Staff;
import fplhn.udpm.examdistribution.entity.Subject;
import fplhn.udpm.examdistribution.infrastructure.constant.EntityStatus;
import fplhn.udpm.examdistribution.utils.Helper;
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

    @Override
    public ResponseObject<?> addOrDelAssignUploader(AssignUploaderRequest request) {
        Optional<AssignUploader> isAssignUploaderExist = assignUploaderRepository.isAssignUploaderExist(request);
        if (isAssignUploaderExist.isPresent()) {
            assignUploaderRepository.delete(isAssignUploaderExist.get());
            return new ResponseObject<>(
                    null,
                    HttpStatus.OK,
                    "Xóa thành công " + isAssignUploaderExist.get().getStaff().getName() + " khỏi danh sách người tải đề"
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
                "Phân công nhân viên " + postedAssignUploader.getStaff().getName() + " làm người tải đề cho môn học "
                + postAssignUploader.getSubject().getName() + " thành công"
        );
    }

}
