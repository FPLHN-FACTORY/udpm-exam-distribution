package fplhn.udpm.examdistribution.core.headdepartment.managehos.service.impl;

import fplhn.udpm.examdistribution.core.common.base.ResponseObject;
import fplhn.udpm.examdistribution.core.headdepartment.managehos.model.request.ReassignHeadOfSubjectRequest;
import fplhn.udpm.examdistribution.core.headdepartment.managehos.model.request.StaffsBySubjectRequest;
import fplhn.udpm.examdistribution.core.headdepartment.managehos.model.request.SubjectsStaffRequest;
import fplhn.udpm.examdistribution.core.headdepartment.managehos.repository.HDHeadSubjectBySemesterExtendRepository;
import fplhn.udpm.examdistribution.core.headdepartment.managehos.repository.HDStaffExtendRepository;
import fplhn.udpm.examdistribution.core.headdepartment.managehos.repository.HDSubjectExtendRepository;
import fplhn.udpm.examdistribution.core.headdepartment.managehos.service.ManageSubjectService;
import fplhn.udpm.examdistribution.entity.HeadSubjectBySemester;
import fplhn.udpm.examdistribution.entity.Staff;
import fplhn.udpm.examdistribution.utils.Helper;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.validation.annotation.Validated;

import java.util.Optional;

@Service
@Validated
@RequiredArgsConstructor
public class ManageSubjectServiceImpl implements ManageSubjectService {

    private final HDSubjectExtendRepository hdSubjectExtendRepository;

    private final HDHeadSubjectBySemesterExtendRepository hdHeadSubjectBySemesterExtendRepository;

    private final HDStaffExtendRepository hdStaffExtendRepository;

    @Override
    public ResponseObject<?> getSubjectsStaff(SubjectsStaffRequest request) {
        return new ResponseObject<>(
                hdSubjectExtendRepository.getSubjectsStaff(
                        Helper.createPageable(request, "id"),
                        request
                ),
                HttpStatus.OK,
                "Lấy danh sách môn học thành công"
        );
    }

    @Override
    public ResponseObject<?> reassignSubjectToStaff(ReassignHeadOfSubjectRequest request) {
        Optional<HeadSubjectBySemester> headSubjectBySemester = hdHeadSubjectBySemesterExtendRepository.findBySubject_IdAndSemester_Id(
                request.getSubjectId(),
                request.getCurrentSemesterId()
        );

        Optional<Staff> staffOptional = hdStaffExtendRepository.findById(request.getStaffId());
        if (staffOptional.isEmpty()) {
            return new ResponseObject<>(
                    null,
                    HttpStatus.NOT_FOUND,
                    "Không tìm thấy nhân viên"
            );
        }


        if (headSubjectBySemester.isPresent()) {
            headSubjectBySemester.get().setStaff(staffOptional.get());
            hdHeadSubjectBySemesterExtendRepository.save(headSubjectBySemester.get());
        }

        return new ResponseObject<>(
                null,
                HttpStatus.OK,
                "Phân công giáo viên cho môn học thành công"
        );

    }

    @Override
    public ResponseObject<?> getStaffsBySubject(StaffsBySubjectRequest request) {
        return new ResponseObject<>(
                hdSubjectExtendRepository.getStaffsBySubject(
                        request,
                        Helper.createPageable(request, "id")
                ),
                HttpStatus.OK,
                "Lấy danh sách nhân viên theo môn học thành công"
        );
    }

}
