package fplhn.udpm.examdistribution.core.headdepartment.managehos.service.impl;

import fplhn.udpm.examdistribution.core.common.base.PageableObject;
import fplhn.udpm.examdistribution.core.common.base.ResponseObject;
import fplhn.udpm.examdistribution.core.headdepartment.managehos.model.request.ModifySubjectGroupRequest;
import fplhn.udpm.examdistribution.core.headdepartment.managehos.model.request.RoleSubjectGroupRequest;
import fplhn.udpm.examdistribution.core.headdepartment.managehos.model.request.SubjectBySubjectGroupRequest;
import fplhn.udpm.examdistribution.core.headdepartment.managehos.repository.HDDepartmentFacilityExtendRepository;
import fplhn.udpm.examdistribution.core.headdepartment.managehos.repository.HDSemesterExtendRepository;
import fplhn.udpm.examdistribution.core.headdepartment.managehos.repository.HDSubjectExtendRepository;
import fplhn.udpm.examdistribution.core.headdepartment.managehos.repository.HDSubjectGroupRepository;
import fplhn.udpm.examdistribution.core.headdepartment.managehos.service.ManageRoleSubjectGroup;
import fplhn.udpm.examdistribution.entity.Subject;
import fplhn.udpm.examdistribution.entity.SubjectGroup;
import fplhn.udpm.examdistribution.infrastructure.constant.SessionConstant;
import fplhn.udpm.examdistribution.utils.Helper;
import jakarta.servlet.http.HttpSession;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.validation.annotation.Validated;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Service
@Validated
@RequiredArgsConstructor
public class ManageRoleSubjectGroupImpl implements ManageRoleSubjectGroup {

    private final HDSubjectGroupRepository hdSubjectGroupRepository;

    private final HDSubjectExtendRepository hdSubjectExtendRepository;

    private final HDSemesterExtendRepository hdSemesterExtendRepository;

    private final HDDepartmentFacilityExtendRepository hdDepartmentFacilityExtendRepository;

    private final HttpSession httpSession;

    @Override
    public ResponseObject<?> getRoleSubjectGroup(RoleSubjectGroupRequest request) {
        return new ResponseObject<>(
                PageableObject.of(
                        hdSubjectGroupRepository.getRoleSubjectGroup(
                                Helper.createPageable(request, "id"), request
                        )
                ),
                HttpStatus.OK,
                "Lấy danh sách chức vụ môn học thành công"
        );
    }

    @Override
    public ResponseObject<?> createRoleSubjectGroup(@Valid ModifySubjectGroupRequest request) {
        List<Subject> attachSubjects = new ArrayList<>();
        List<Subject> detachSubjects = new ArrayList<>();
        for (String attachSubjectId : request.getAttachSubjectIds()) {
            Optional<Subject> subject = hdSubjectExtendRepository.findById(attachSubjectId);
            subject.ifPresent(attachSubjects::add);
        }
        for (String detachSubjectId : request.getDetachSubjectIds()) {
            Optional<Subject> subject = hdSubjectExtendRepository.findById(detachSubjectId);
            subject.ifPresent(detachSubjects::add);
        }

        Optional<SubjectGroup> subjectGroup = hdSubjectGroupRepository.findByAttachRoleName(request.getAttachRoleName());
        if (subjectGroup.isPresent()) {
            return new ResponseObject<>(
                    null,
                    HttpStatus.BAD_REQUEST,
                    "Chức vụ môn học đã tồn tại"
            );
        }

        SubjectGroup newSubjectGroup = new SubjectGroup();
        newSubjectGroup.setAttachRoleName(request.getAttachRoleName());
        newSubjectGroup.setSemester(
                hdSemesterExtendRepository.findById(
                        (String) httpSession.getAttribute(SessionConstant.CURRENT_SEMESTER_ID)
                ).orElse(null)
        );
        newSubjectGroup.setDepartmentFacility(
                hdDepartmentFacilityExtendRepository.findById(
                        (String) httpSession.getAttribute(SessionConstant.CURRENT_USER_DEPARTMENT_FACILITY_ID)
                ).orElse(null)
        );

        SubjectGroup savedSubjectGroup = hdSubjectGroupRepository.save(newSubjectGroup);

        for (Subject attachSubject : attachSubjects) {
            attachSubject.setSubjectGroup(savedSubjectGroup);
            hdSubjectExtendRepository.save(attachSubject);
        }

        for (Subject detachSubject : detachSubjects) {
            detachSubject.setSubjectGroup(null);
            hdSubjectExtendRepository.save(detachSubject);
        }

        return new ResponseObject<>(
                null,
                HttpStatus.OK,
                "Tạo chức vụ môn học thành công"
        );
    }

    @Override
    public ResponseObject<?> updateRoleSubjectGroup(String subjectGroupId, ModifySubjectGroupRequest request) {
        List<Subject> attachSubjects = new ArrayList<>();
        List<Subject> detachSubjects = new ArrayList<>();
        for (String attachSubjectId : request.getAttachSubjectIds()) {
            Optional<Subject> subject = hdSubjectExtendRepository.findById(attachSubjectId);
            subject.ifPresent(attachSubjects::add);
        }
        for (String detachSubjectId : request.getDetachSubjectIds()) {
            Optional<Subject> subject = hdSubjectExtendRepository.findById(detachSubjectId);
            subject.ifPresent(detachSubjects::add);
        }

        Optional<SubjectGroup> subjectGroup = hdSubjectGroupRepository.findById(subjectGroupId);
        if (subjectGroup.isEmpty()) {
            return new ResponseObject<>(
                    null,
                    HttpStatus.NOT_FOUND,
                    "Chức vụ môn học không tồn tại"
            );
        }

        subjectGroup.get().setAttachRoleName(request.getAttachRoleName());
        SubjectGroup savedSubjectGroup = hdSubjectGroupRepository.save(subjectGroup.get());

        for (Subject attachSubject : attachSubjects) {
            attachSubject.setSubjectGroup(savedSubjectGroup);
            hdSubjectExtendRepository.save(attachSubject);
        }

        for (Subject detachSubject : detachSubjects) {
            detachSubject.setSubjectGroup(null);
            hdSubjectExtendRepository.save(detachSubject);
        }

        return new ResponseObject<>(
                null,
                HttpStatus.OK,
                "Cập nhật chức vụ môn học thành công"
        );
    }

    @Override
    public ResponseObject<?> getListSubjectBySubjectGroupId(SubjectBySubjectGroupRequest request) {
        return new ResponseObject<>(
                PageableObject.of(
                        hdSubjectGroupRepository.getSubjectBySubjectGroup(
                                Helper.createPageable(request, "id"), request
                        )
                ),
                HttpStatus.OK,
                "Lấy danh sách môn học theo chức vụ môn học thành công"
        );
    }

}
