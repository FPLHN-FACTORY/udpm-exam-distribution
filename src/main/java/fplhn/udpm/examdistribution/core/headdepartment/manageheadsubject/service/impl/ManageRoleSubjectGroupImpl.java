package fplhn.udpm.examdistribution.core.headdepartment.manageheadsubject.service.impl;

import fplhn.udpm.examdistribution.core.common.base.PageableObject;
import fplhn.udpm.examdistribution.core.common.base.ResponseObject;
import fplhn.udpm.examdistribution.core.headdepartment.manageheadsubject.model.request.CreateSubjectGroupRequest;
import fplhn.udpm.examdistribution.core.headdepartment.manageheadsubject.model.request.ModifySubjectGroupRequest;
import fplhn.udpm.examdistribution.core.headdepartment.manageheadsubject.model.request.RoleSubjectGroupRequest;
import fplhn.udpm.examdistribution.core.headdepartment.manageheadsubject.model.request.SubjectBySubjectGroupRequest;
import fplhn.udpm.examdistribution.core.headdepartment.manageheadsubject.model.response.CreateSubjectGroupResponse;
import fplhn.udpm.examdistribution.core.headdepartment.manageheadsubject.repository.HDDepartmentFacilityExtendRepository;
import fplhn.udpm.examdistribution.core.headdepartment.manageheadsubject.repository.HDSemesterExtendRepository;
import fplhn.udpm.examdistribution.core.headdepartment.manageheadsubject.repository.HDSubjectBySubjectGroupExtendRepository;
import fplhn.udpm.examdistribution.core.headdepartment.manageheadsubject.repository.HDSubjectExtendRepository;
import fplhn.udpm.examdistribution.core.headdepartment.manageheadsubject.repository.HDSubjectGroupRepository;
import fplhn.udpm.examdistribution.core.headdepartment.manageheadsubject.service.ManageRoleSubjectGroup;
import fplhn.udpm.examdistribution.entity.Subject;
import fplhn.udpm.examdistribution.entity.SubjectBySubjectGroup;
import fplhn.udpm.examdistribution.entity.SubjectGroup;
import fplhn.udpm.examdistribution.utils.Helper;
import fplhn.udpm.examdistribution.utils.SessionHelper;
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

    private final HDSubjectBySubjectGroupExtendRepository hdSubjectBySubjectGroupExtendRepository;

    private final SessionHelper sessionHelper;

    @Override
    public ResponseObject<?> getRoleSubjectGroup(@Valid RoleSubjectGroupRequest request) {
        if (request.getSemesterId() == null) request.setSemesterId(sessionHelper.getCurrentSemesterId());
        if (request.getDepartmentFacilityId() == null) request.setDepartmentFacilityId(
                sessionHelper.getCurrentUserDepartmentFacilityId()
        );
        return new ResponseObject<>(
                PageableObject.of(
                        hdSubjectGroupRepository.getRoleSubjectGroup(
                                Helper.createPageable(request, "createdDate"), request
                        )
                ),
                HttpStatus.OK,
                "Lấy danh sách chức vụ môn học thành công"
        );
    }

    @Override
    public ResponseObject<?> createRoleSubjectGroup(@Valid CreateSubjectGroupRequest request) {
        if (hdSubjectGroupRepository.existsByAttachRoleNameAndSemesterIdAndDepartmentFacilityId(
                request.getAttachRoleName(),
                sessionHelper.getCurrentSemesterId(),
                sessionHelper.getCurrentUserDepartmentFacilityId()
        )) {
            return new ResponseObject<>(
                    null,
                    HttpStatus.BAD_REQUEST,
                    "Chức vụ cho nhóm môn học đã tồn tại"
            );
        }
        SubjectGroup subjectGroup = new SubjectGroup();
        subjectGroup.setAttachRoleName(request.getAttachRoleName());
        subjectGroup.setSemester(hdSemesterExtendRepository.getReferenceById(sessionHelper.getCurrentSemesterId()));
        subjectGroup.setDepartmentFacility(
                hdDepartmentFacilityExtendRepository.getReferenceById(sessionHelper.getCurrentUserDepartmentFacilityId())
        );

        hdSubjectGroupRepository.save(subjectGroup);
        return new ResponseObject<>(
                new CreateSubjectGroupResponse(
                        subjectGroup.getAttachRoleName(),
                        subjectGroup.getId()
                ),
                HttpStatus.CREATED,
                "Tạo chức vụ cho nhóm môn học thành công"
        );
    }

    @Override
    public ResponseObject<?> updateRoleSubjectGroup(String subjectGroupId, ModifySubjectGroupRequest request) {
        try {
            List<Subject> attachedSubjects = new ArrayList<>();
            if (request.getAttachSubjectIds() != null) {
                for (String subjectId : request.getAttachSubjectIds()) {
                    Subject subject = hdSubjectExtendRepository
                            .findById(subjectId)
                            .orElseThrow(() -> new IllegalArgumentException("Môn học không tồn tại"));
                    attachedSubjects.add(subject);
                }
            }

            List<Subject> detachedSubjects = new ArrayList<>();
            if (request.getDetachSubjectIds() != null) {
                for (String subjectId : request.getDetachSubjectIds()) {
                    Subject subject = hdSubjectExtendRepository
                            .findById(subjectId)
                            .orElseThrow(() -> new IllegalArgumentException("Môn học không tồn tại"));
                    detachedSubjects.add(subject);
                }
            }

            SubjectGroup subjectGroup = hdSubjectGroupRepository
                    .findById(subjectGroupId)
                    .orElseThrow(() -> new IllegalArgumentException("Chức vụ môn học không tồn tại"));
            subjectGroup.setAttachRoleName(request.getAttachRoleName());
            hdSubjectGroupRepository.save(subjectGroup);

            for (Subject subject : attachedSubjects) {
                if (hdSubjectBySubjectGroupExtendRepository
                        .findBySubjectIdAndSubjectGroupId(subject.getId(), subjectGroupId)
                        .isPresent()) {
                    continue;
                }
                Optional<SubjectGroup> subjectGroupCurrentSubjectIn = hdSubjectBySubjectGroupExtendRepository
                        .findBySubjectIdAndSemesterId(subject.getId(), sessionHelper.getCurrentSemesterId());
                subjectGroupCurrentSubjectIn.ifPresent(
                        group -> hdSubjectBySubjectGroupExtendRepository
                                .deleteBySubjectIdAndSubjectGroupId(
                                        subject.getId(),
                                        group.getId()
                                )
                );
                hdSubjectBySubjectGroupExtendRepository.save(
                        new SubjectBySubjectGroup(
                                subject,
                                subjectGroup
                        )
                );
            }

            for (Subject subject : detachedSubjects) {
                hdSubjectBySubjectGroupExtendRepository
                        .findBySubjectIdAndSubjectGroupId(subject.getId(), subjectGroupId)
                        .ifPresent(hdSubjectBySubjectGroupExtendRepository::delete);
            }

            return new ResponseObject<>(
                    null,
                    HttpStatus.OK,
                    "Cập nhật chức vụ môn học thành công"
            );

        } catch (IllegalArgumentException e) {
            return new ResponseObject<>(
                    null,
                    HttpStatus.BAD_REQUEST,
                    e.getMessage()
            );
        }
    }

    @Override
    public ResponseObject<?> getListSubjectBySubjectGroupId(SubjectBySubjectGroupRequest request) {
        if (request.getSemesterId() == null) {
            request.setSemesterId(sessionHelper.getCurrentSemesterId());
        }
        if (request.getDepartmentFacilityId() == null) {
            request.setDepartmentFacilityId(sessionHelper.getCurrentUserDepartmentFacilityId());
        }
        return new ResponseObject<>(
                PageableObject.of(
                        hdSubjectGroupRepository.findAllSubjectsInSubjectGroup(
                                Helper.createPageable(request, "id"), request
                        )
                ),
                HttpStatus.OK,
                "Lấy danh sách môn học theo chức vụ môn học thành công"
        );
    }

}
