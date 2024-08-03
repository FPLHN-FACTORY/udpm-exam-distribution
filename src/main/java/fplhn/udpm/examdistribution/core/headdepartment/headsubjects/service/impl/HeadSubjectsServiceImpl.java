package fplhn.udpm.examdistribution.core.headdepartment.headsubjects.service.impl;

import fplhn.udpm.examdistribution.core.common.base.PageableObject;
import fplhn.udpm.examdistribution.core.common.base.ResponseObject;
import fplhn.udpm.examdistribution.core.headdepartment.headsubjects.model.request.AssignOrUnassignSubjectForHeadSubjectRequest;
import fplhn.udpm.examdistribution.core.headdepartment.headsubjects.model.request.HeadSubjectRequest;
import fplhn.udpm.examdistribution.core.headdepartment.headsubjects.model.request.HeadSubjectSearchRequest;
import fplhn.udpm.examdistribution.core.headdepartment.headsubjects.model.request.ReassignHeadSubjectRequest;
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
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.validation.annotation.Validated;

import java.util.List;
import java.util.Optional;

import static fplhn.udpm.examdistribution.utils.Helper.createPageable;

@Service
@RequiredArgsConstructor
@Validated
@Slf4j
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
    public ResponseObject<?> getSubjectsByHeadSubject(String headSubjectId, SubjectByHeadSubjectRequest request) {
        request.setHeadSubjectId(headSubjectId);
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
    public ResponseObject<?> getSubjectsWithAssign(String headSubjectId, SubjectByHeadSubjectRequest request) {
        request.setHeadSubjectId(headSubjectId);
        if (request.getCurrentSemesterId() == null) request.setCurrentSemesterId(sessionHelper.getCurrentSemesterId());
        request.setDepartmentFacilityId(sessionHelper.getCurrentUserDepartmentFacilityId());
        request.setFacilityId(sessionHelper.getCurrentUserFacilityId());
        return new ResponseObject<>(
                PageableObject.of(
                        hdhsSubjectRepository.getSubjectAssign(
                                createPageable(request, "orderNumber"),
                                request
                        )
                ),
                HttpStatus.OK,
                "Lấy danh sách môn học theo trưởng bộ môn thành công"
        );
    }

    @Override
    public ResponseObject<?> assignSubjectForHeadSubject(String headSubjectId, @Valid AssignOrUnassignSubjectForHeadSubjectRequest request) {

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

    @Override
    @Transactional
    public ResponseObject<?> unassignSubjectForHeadSubject(String headSubjectId, AssignOrUnassignSubjectForHeadSubjectRequest request) {

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
            hdhsHeadSubjectBySemesterRepository.delete(headSubjectBySemester.get());
            return ResponseObject.successForward(
                    HttpStatus.OK,
                    "Hủy gán môn học cho trưởng bộ môn thành công"
            );
        } else {
            return ResponseObject.errorForward(
                    "Môn học không được gán cho trưởng bộ môn",
                    HttpStatus.BAD_REQUEST
            );
        }
    }

    @Override
    public ResponseObject<?> reassignSubjectForAnotherHeadSubject(@Valid ReassignHeadSubjectRequest request) {

        String currentHeadSubjectId = request.getCurrentHeadSubjectId();
        String newHeadSubjectId = request.getNewHeadSubjectId();
        Optional<Staff> newHeadSubject = hdhsStaffExtendRepository.findById(newHeadSubjectId);
        if (newHeadSubject.isEmpty()) {
            return ResponseObject.errorForward(
                    "Nhân viên không phải tồn tại",
                    HttpStatus.NOT_FOUND
            );
        }

        List<HeadSubjectBySemester> headSubjectBySemesterOlds = hdhsHeadSubjectBySemesterRepository
                .findBySemester_IdAndFacility_IdAndStaff_Id(
                        sessionHelper.getCurrentSemesterId(),
                        sessionHelper.getCurrentUserFacilityId(),
                        currentHeadSubjectId
                );

        for (HeadSubjectBySemester headSubjectBySemester : headSubjectBySemesterOlds) {
            if (headSubjectBySemester.getStaff().getId().equals(newHeadSubjectId)) {
                return ResponseObject.errorForward(
                        "Nhân viên đã là trưởng bộ môn của môn học này",
                        HttpStatus.BAD_REQUEST
                );
            }
        }

        headSubjectBySemesterOlds.forEach(
                headSubjectBySemester ->
                        headSubjectBySemester.setStaff(newHeadSubject.get())
        );
        hdhsHeadSubjectBySemesterRepository.saveAll(headSubjectBySemesterOlds);

        return ResponseObject.successForward(
                HttpStatus.OK,
                "Chuyển môn học cho trưởng bộ môn khác thành công"
        );
    }

    @Override
    public ResponseObject<?> searchStaff(HeadSubjectSearchRequest request) {
        request.setCurrentDepartmentFacilityId(sessionHelper.getCurrentUserDepartmentFacilityId());
        request.setCurrentSemesterId(sessionHelper.getCurrentSemesterId());
        request.setCurrentUserId(sessionHelper.getCurrentUserId());
        request.setCurrentFacilityId(sessionHelper.getCurrentUserFacilityId());
        return new ResponseObject<>(
                hdhsHeadSubjectBySemesterRepository.getHeadSubjects(request, "TRUONG_MON"),
                HttpStatus.OK,
                "Tìm kiếm nhân viên thành công"
        );
    }

}
