package fplhn.udpm.examdistribution.core.headdepartment.managehos.service.impl;

import fplhn.udpm.examdistribution.core.common.base.PageableObject;
import fplhn.udpm.examdistribution.core.common.base.ResponseObject;
import fplhn.udpm.examdistribution.core.headdepartment.managehos.model.request.AssignSubjectStaffRequest;
import fplhn.udpm.examdistribution.core.headdepartment.managehos.model.request.ReassignHeadOfSubjectRequest;
import fplhn.udpm.examdistribution.core.headdepartment.managehos.model.request.StaffRequest;
import fplhn.udpm.examdistribution.core.headdepartment.managehos.model.request.StaffsBySubjectRequest;
import fplhn.udpm.examdistribution.core.headdepartment.managehos.model.request.SubjectAssignedRequest;
import fplhn.udpm.examdistribution.core.headdepartment.managehos.model.request.SubjectsStaffRequest;
import fplhn.udpm.examdistribution.core.headdepartment.managehos.repository.HDStaffExtendRepository;
import fplhn.udpm.examdistribution.core.headdepartment.managehos.repository.HDSubjectExtendRepository;
import fplhn.udpm.examdistribution.core.headdepartment.managehos.service.ManageStaffHOSService;
import fplhn.udpm.examdistribution.entity.Staff;
import fplhn.udpm.examdistribution.entity.Subject;
import fplhn.udpm.examdistribution.utils.Helper;
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
public class ManageStaffHOServiceImpl implements ManageStaffHOSService {

    private final HDStaffExtendRepository hdStaffExtendRepository;

    private final HDSubjectExtendRepository hdSubjectExtendRepository;

    @Override
    public ResponseObject<?> getAllStaffs(@Valid StaffRequest request) {
//        return new ResponseObject<>(
//                PageableObject.of(hdStaffExtendRepository.getAllStaffs(Helper.createPageable(request, "id"), request)),
//                HttpStatus.OK,
//                "Lấy danh sách nhân viên thành công"
//        );
        return null;
    }

    @Override
    public ResponseObject<?> getSubjectAssigned(@Valid SubjectAssignedRequest request) {
//        return new ResponseObject<>(
//                PageableObject.of(hdSubjectExtendRepository.getSubjectAssigned(request, Helper.createPageable(request, "id"))),
//                HttpStatus.OK,
//                "Lấy danh sách môn học thành công"
//        );
        return null;
    }

    @Override
    public ResponseObject<?> assignSubjectToStaff(@Valid AssignSubjectStaffRequest request) {
//        Optional<Staff> staffOptional = hdStaffExtendRepository.findById(request.getStaffId());
//        if (staffOptional.isEmpty()) {
//            return new ResponseObject<>(null, HttpStatus.NOT_FOUND, "Không tìm thấy nhân viên");
//        }
//        Staff staff = staffOptional.get();
//
//        List<String> assignedSubjectIds = List.of(request.getAssignedSubjectIds());
//        List<String> unassignedSubjectIds = List.of(request.getUnassignedSubjectIds());
//        List<Object> subjectsNotFound = new ArrayList<>();
//        List<Subject> subjectsToSave = new ArrayList<>();
//
//        List<Subject> currentlyAssignedSubjects = hdSubjectExtendRepository.findByHeadSubject(staff);
//
//        currentlyAssignedSubjects.stream()
//                .filter(subject -> !unassignedSubjectIds.contains(subject.getId()))
//                .forEach(subjectsToSave::add);
//
//        for (String subjectId : assignedSubjectIds) {
//            Optional<Subject> subjectOptional = hdSubjectExtendRepository.findById(subjectId);
//            if (subjectOptional.isPresent()) {
//                Subject subject = subjectOptional.get();
//                if (!subjectsToSave.contains(subject)) {
//                    subject.setHeadSubject(staff);
//                    subjectsToSave.add(subject);
//                }
//            } else {
//                subjectsNotFound.add(subjectId);
//            }
//        }
//
//        for (String subjectId : unassignedSubjectIds) {
//            Optional<Subject> subjectOptional = hdSubjectExtendRepository.findById(subjectId);
//            if (subjectOptional.isPresent()) {
//                Subject subject = subjectOptional.get();
//                if (subject.getHeadSubject() != null && subject.getHeadSubject().equals(staff)) {
//                    subject.setHeadSubject(null);
//                    subjectsToSave.add(subject);
//                }
//            } else {
//                subjectsNotFound.add(subjectId);
//            }
//        }
//
//        hdSubjectExtendRepository.saveAll(subjectsToSave);

//        return new ResponseObject<>(
//                subjectsNotFound.isEmpty() ? "All operations successful" : subjectsNotFound,
//                HttpStatus.OK,
//                subjectsNotFound.isEmpty() ? "Cập nhật phân công môn học thành công" : "Some subjects not found"
//        );
        return null;
    }

    @Override
    public ResponseObject<?> getSubjectsStaff(@Valid SubjectsStaffRequest request) {
//        return new ResponseObject<>(
//                PageableObject.of(hdSubjectExtendRepository.getSubjectsStaff(request, Helper.createPageable(request, "id"))),
//                HttpStatus.OK,
//                "Lấy danh sách môn học và nhân viên phụ trách thành công"
//        );
        return null;
    }

    @Override
    public ResponseObject<?> reassignSubjectToStaff(@Valid ReassignHeadOfSubjectRequest request) {
//        Optional<Staff> staffOptional = hdStaffExtendRepository.findById(request.getStaffId());
//        if (staffOptional.isEmpty()) {
//            return new ResponseObject<>(null, HttpStatus.NOT_FOUND, "Không tìm thấy nhân viên");
//        }
//        Staff staff = staffOptional.get();
//
//        Optional<Subject> subjectOptional = hdSubjectExtendRepository.findById(request.getSubjectId());
//        if (subjectOptional.isEmpty()) {
//            return new ResponseObject<>(null, HttpStatus.NOT_FOUND, "Không tìm thấy môn học");
//        }
//
//        Subject subject = subjectOptional.get();
//        subject.setHeadSubject(staff);
//        hdSubjectExtendRepository.save(subject);

        return new ResponseObject<>(null, HttpStatus.OK, "Cập nhật phân công môn học thành công");
    }

    @Override
    public ResponseObject<?> getStaffsBySubject(@Valid StaffsBySubjectRequest request) {
//        return new ResponseObject<>(
//                PageableObject.of(hdSubjectExtendRepository.getStaffsBySubject(request, Helper.createPageable(request, "id"))),
//                HttpStatus.OK,
//                "Lấy danh sách nhân viên theo môn học thành công"
//        );
        return null;
    }

}
