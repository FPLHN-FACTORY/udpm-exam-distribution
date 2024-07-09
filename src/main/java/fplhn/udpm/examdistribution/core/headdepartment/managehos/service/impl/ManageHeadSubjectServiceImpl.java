package fplhn.udpm.examdistribution.core.headdepartment.managehos.service.impl;

import fplhn.udpm.examdistribution.core.common.base.PageableObject;
import fplhn.udpm.examdistribution.core.common.base.ResponseObject;
import fplhn.udpm.examdistribution.core.headdepartment.joinroom.staff.repository.HDSStaffExtendRepository;
import fplhn.udpm.examdistribution.core.headdepartment.managehos.model.request.AssignSubjectStaffRequest;
import fplhn.udpm.examdistribution.core.headdepartment.managehos.model.request.HeadSubjectRequest;
import fplhn.udpm.examdistribution.core.headdepartment.managehos.model.request.SubjectAssignedRequest;
import fplhn.udpm.examdistribution.core.headdepartment.managehos.repository.HDHeadSubjectBySemesterExtendRepository;
import fplhn.udpm.examdistribution.core.headdepartment.managehos.repository.HDSemesterExtendRepository;
import fplhn.udpm.examdistribution.core.headdepartment.managehos.repository.HDSubjectExtendRepository;
import fplhn.udpm.examdistribution.core.headdepartment.managehos.service.ManageHeadSubjectService;
import fplhn.udpm.examdistribution.entity.HeadSubjectBySemester;
import fplhn.udpm.examdistribution.entity.Semester;
import fplhn.udpm.examdistribution.entity.Staff;
import fplhn.udpm.examdistribution.entity.Subject;
import fplhn.udpm.examdistribution.infrastructure.constant.EntityStatus;
import fplhn.udpm.examdistribution.utils.Helper;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.validation.annotation.Validated;

import java.util.Arrays;
import java.util.List;
import java.util.Optional;

@Service
@Validated
@RequiredArgsConstructor
public class ManageHeadSubjectServiceImpl implements ManageHeadSubjectService {

    private final HDHeadSubjectBySemesterExtendRepository hdHeadSubjectBySemesterExtendRepository;

    private final HDSStaffExtendRepository hdStaffExtendRepository;

    private final HDSubjectExtendRepository hdSubjectExtendRepository;

    private final HDSemesterExtendRepository hdSemesterExtendRepository;

    @Override
    public ResponseObject<?> getStaffAndHeadSubjects(@Valid HeadSubjectRequest request) {
        return new ResponseObject<>(
                PageableObject.of(
                        hdHeadSubjectBySemesterExtendRepository.getHeadSubjects(
                                Helper.createPageable(request, "id"),
                                request
                        )
                ),
                HttpStatus.OK,
                "Lấy danh sách nhân viên thành công"
        );
    }

    @Override
    public ResponseObject<?> getSubjectAssigned(@Valid SubjectAssignedRequest request) {
        return new ResponseObject<>(
                PageableObject.of(hdHeadSubjectBySemesterExtendRepository.getSubjectAssigned(
                                Helper.createPageable(request, "id"),
                                request
                        )
                ),
                HttpStatus.OK,
                "Lấy danh sách môn học thành công"
        );
    }

    @Override
    public ResponseObject<?> assignSubjectToStaff(@Valid AssignSubjectStaffRequest request) {
        Optional<Staff> staffOptional = hdStaffExtendRepository.findById(request.getStaffId());
        if (staffOptional.isEmpty()) {
            return new ResponseObject<>(null, HttpStatus.NOT_FOUND, "Giảng viên không tồn tại.");
        }

        Optional<Semester> semesterOptional = hdSemesterExtendRepository.findById(request.getSemesterId());
        if (semesterOptional.isEmpty()) {
            return new ResponseObject<>(null, HttpStatus.NOT_FOUND, "Học kỳ không tồn tại.");
        }

        Staff staff = staffOptional.get();
        Semester semester = semesterOptional.get();

        List<Subject> allSubjects = hdSubjectExtendRepository.findAllById(List.of(request.getAssignedSubjectIds()));
        allSubjects.addAll(hdSubjectExtendRepository.findAllById(List.of(request.getUnassignedSubjectIds())));

        List<Subject> assignedSubjects = allSubjects.stream()
                .filter(subject -> Arrays.stream(request.getAssignedSubjectIds()).anyMatch(id -> id.equals(subject.getId())))
                .toList();

        List<Subject> unassignedSubjects = allSubjects.stream()
                .filter(subject -> request.getUnassignedSubjectIds().length > 0
                                   && Arrays.stream(request.getUnassignedSubjectIds()).anyMatch(id -> id.equals(subject.getId())))
                .toList();

        assignedSubjects.forEach(subject -> assignSubject(subject, staff, semester));
        unassignedSubjects.forEach(subject -> unassignSubject(subject, staff, semester));

        return ResponseObject.successForward(
                null,
                "Phân công môn học cho giảng viên thành công"
        );
    }

    private void assignSubject(Subject subject, Staff staff, Semester semester) {
        hdHeadSubjectBySemesterExtendRepository.findBySubject_IdAndSemester_Id(subject.getId(), semester.getId())
                .ifPresent(existingAssignment -> {
                    if (!existingAssignment.getStaff().getId().equals(staff.getId())) {
                        existingAssignment.setStaff(staff);
                        hdHeadSubjectBySemesterExtendRepository.save(existingAssignment);
                    }
                });

        Optional<HeadSubjectBySemester> existingAssignmentForSameStaff =
                hdHeadSubjectBySemesterExtendRepository.findBySubject_IdAndSemester_IdAndStaff_Id(
                        subject.getId(),
                        semester.getId(),
                        staff.getId()
                );

        if (existingAssignmentForSameStaff.isPresent()) {
            HeadSubjectBySemester headSubjectBySemester = existingAssignmentForSameStaff.get();
            headSubjectBySemester.setStatus(EntityStatus.ACTIVE);
            hdHeadSubjectBySemesterExtendRepository.save(headSubjectBySemester);
        } else {
            HeadSubjectBySemester newHeadSubjectBySemester = new HeadSubjectBySemester();
            newHeadSubjectBySemester.setSubject(subject);
            newHeadSubjectBySemester.setSemester(semester);
            newHeadSubjectBySemester.setStaff(staff);
            newHeadSubjectBySemester.setStatus(EntityStatus.ACTIVE);
            hdHeadSubjectBySemesterExtendRepository.save(newHeadSubjectBySemester);
        }
    }

    private void unassignSubject(Subject subject, Staff staff, Semester semester) {
        hdHeadSubjectBySemesterExtendRepository.findBySubject_IdAndSemester_IdAndStaff_Id(subject.getId(), semester.getId(), staff.getId())
                .ifPresent(hdHeadSubjectBySemesterExtendRepository::delete);
    }

}
