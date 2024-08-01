package fplhn.udpm.examdistribution.core.headdepartment.headsubjects.service.impl;

import fplhn.udpm.examdistribution.core.common.base.PageableObject;
import fplhn.udpm.examdistribution.core.common.base.ResponseObject;
import fplhn.udpm.examdistribution.core.headdepartment.headsubjects.model.request.AssignSubjectForHeadSubjectRequest;
import fplhn.udpm.examdistribution.core.headdepartment.headsubjects.model.request.HeadSubjectRequest;
import fplhn.udpm.examdistribution.core.headdepartment.headsubjects.model.request.SubjectByHeadSubjectRequest;
import fplhn.udpm.examdistribution.core.headdepartment.headsubjects.repository.HDHSHeadSubjectBySemesterRepository;
import fplhn.udpm.examdistribution.core.headdepartment.headsubjects.repository.HDHSSFacilityExtendRepository;
import fplhn.udpm.examdistribution.core.headdepartment.headsubjects.repository.HDHSSSemesterExtendRepository;
import fplhn.udpm.examdistribution.core.headdepartment.headsubjects.repository.HDHSStaffExtendRepository;
import fplhn.udpm.examdistribution.core.headdepartment.headsubjects.repository.HDHSSubjectRepository;
import fplhn.udpm.examdistribution.core.headdepartment.headsubjects.service.HeadSubjectsService;
import fplhn.udpm.examdistribution.entity.HeadSubjectBySemester;
import fplhn.udpm.examdistribution.entity.Staff;
import fplhn.udpm.examdistribution.entity.Subject;
import fplhn.udpm.examdistribution.infrastructure.constant.Role;
import fplhn.udpm.examdistribution.utils.SessionHelper;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.validation.annotation.Validated;

import java.util.Optional;

import static fplhn.udpm.examdistribution.utils.Helper.createPageable;

@Service
@RequiredArgsConstructor
@Validated
public class HeadSubjectsServiceImpl implements HeadSubjectsService {

    private final HDHSHeadSubjectBySemesterRepository hdhsHeadSubjectBySemesterRepository;

    private final HDHSSubjectRepository hdhsSubjectRepository;

    private final SessionHelper sessionHelper;

    private final HDHSStaffExtendRepository hdhsStaffExtendRepository;

    private final HDHSSFacilityExtendRepository hdhsFacilityExtendRepository;

    private final HDHSSSemesterExtendRepository hdhsSemesterExtendRepository;

    @Override
    public ResponseObject<?> getAllHeadSubjects(HeadSubjectRequest request) {
        if (request.getCurrentSemesterId() == null) request.setCurrentSemesterId(sessionHelper.getCurrentSemesterId());
        request.setCurrentUserId(sessionHelper.getCurrentUserId());
        request.setCurrentFacilityId(sessionHelper.getCurrentUserFacilityId());
        request.setHeadSubjectRoleCode(Role.TRUONG_MON.name());
        request.setCurrentDepartmentFacilityId(sessionHelper.getCurrentUserDepartmentFacilityId());
        return new ResponseObject<>(
                PageableObject.of(
                        hdhsHeadSubjectBySemesterRepository.getAllHeadSubjectsBySemester(
                                createPageable(request),
                                request
                        )
                ),
                HttpStatus.OK,
                "Lấy danh sách trưởng bộ môn thành công"
        );
    }

    @Override
    public ResponseObject<?> getSubjectsByHeadSubject(SubjectByHeadSubjectRequest request) {
        if (request.getCurrentSemesterId() == null) request.setCurrentSemesterId(sessionHelper.getCurrentSemesterId());
        return new ResponseObject<>(
                PageableObject.of(
                        hdhsSubjectRepository.getSubjectByHeadSubject(
                                createPageable(request),
                                request
                        )
                ),
                HttpStatus.OK,
                "Lấy danh sách môn học theo trưởng bộ môn thành công"
        );
    }

    @Override
    public ResponseObject<?> getSubjectsWithAssign(SubjectByHeadSubjectRequest request) {
        if (request.getCurrentSemesterId() == null) request.setCurrentSemesterId(sessionHelper.getCurrentSemesterId());
        return new ResponseObject<>(
                PageableObject.of(
                        hdhsSubjectRepository.getSubjectAssign(
                                createPageable(request),
                                request
                        )
                ),
                HttpStatus.OK,
                "Lấy danh sách môn học theo trưởng bộ môn thành công"
        );
    }

    @Override
    public ResponseObject<?> assignSubjectForHeadSubject(String headSubjectId, @Valid AssignSubjectForHeadSubjectRequest request) {

        Optional<Staff> staff = hdhsStaffExtendRepository.findById(headSubjectId);

        if (staff.isEmpty()) {
            return ResponseObject.errorForward(
                    "Nhân viên không phải tồn tại",
                    HttpStatus.NOT_FOUND
            );
        }

        Optional<Subject> subject = hdhsSubjectRepository.findById(request.getSubjectId());

        if (subject.isEmpty()) {
            return ResponseObject.errorForward(
                    "Môn học không tồn tại",
                    HttpStatus.NOT_FOUND
            );
        }

        Optional<HeadSubjectBySemester> headSubjectBySemester = hdhsHeadSubjectBySemesterRepository
                .findBySemester_IdAndSubject_IdAndFacility_Id(
                        sessionHelper.getCurrentSemesterId(),
                        request.getSubjectId(),
                        sessionHelper.getCurrentUserFacilityId()
                );
        if (headSubjectBySemester.isPresent()) {
            headSubjectBySemester.get().setStaff(staff.get());
            hdhsHeadSubjectBySemesterRepository.save(headSubjectBySemester.get());
        } else {
            HeadSubjectBySemester headSubject = new HeadSubjectBySemester();
            headSubject.setSemester(hdhsSemesterExtendRepository.getReferenceById(sessionHelper.getCurrentSemesterId()));
            headSubject.setSubject(subject.get());
            headSubject.setStaff(staff.get());
            headSubject.setFacility(hdhsFacilityExtendRepository.getReferenceById(sessionHelper.getCurrentUserFacilityId()));
            hdhsHeadSubjectBySemesterRepository.save(headSubject);
        }
        return ResponseObject.successForward(
                HttpStatus.OK,
                "Gán môn học cho trưởng bộ môn thành công"
        );
    }

}
