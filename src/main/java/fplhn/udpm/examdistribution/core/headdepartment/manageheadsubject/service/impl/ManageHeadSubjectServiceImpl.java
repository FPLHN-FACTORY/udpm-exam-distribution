package fplhn.udpm.examdistribution.core.headdepartment.manageheadsubject.service.impl;

import fplhn.udpm.examdistribution.core.common.base.PageableObject;
import fplhn.udpm.examdistribution.core.common.base.ResponseObject;
import fplhn.udpm.examdistribution.core.headdepartment.joinroom.repository.HDSStaffExtendRepository;
import fplhn.udpm.examdistribution.core.headdepartment.manageheadsubject.model.request.AssignSubjectGroupToStaffRequest;
import fplhn.udpm.examdistribution.core.headdepartment.manageheadsubject.model.request.HeadSubjectRequest;
import fplhn.udpm.examdistribution.core.headdepartment.manageheadsubject.model.request.SubjectGroupAssignedRequest;
import fplhn.udpm.examdistribution.core.headdepartment.manageheadsubject.repository.HDHeadSubjectBySemesterExtendRepository;
import fplhn.udpm.examdistribution.core.headdepartment.manageheadsubject.repository.HDSemesterExtendRepository;
import fplhn.udpm.examdistribution.core.headdepartment.manageheadsubject.repository.HDSubjectGroupRepository;
import fplhn.udpm.examdistribution.core.headdepartment.manageheadsubject.service.ManageHeadSubjectService;
import fplhn.udpm.examdistribution.entity.HeadSubjectBySemester;
import fplhn.udpm.examdistribution.entity.Staff;
import fplhn.udpm.examdistribution.entity.SubjectGroup;
import fplhn.udpm.examdistribution.infrastructure.constant.Role;
import fplhn.udpm.examdistribution.infrastructure.log.LoggerObject;
import fplhn.udpm.examdistribution.utils.CSVManipulationUtils;
import fplhn.udpm.examdistribution.utils.SessionHelper;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.validation.annotation.Validated;

import java.util.List;
import java.util.Objects;
import java.util.Optional;

import static fplhn.udpm.examdistribution.utils.Helper.createPageable;

@Service
@Validated
@RequiredArgsConstructor
public class ManageHeadSubjectServiceImpl implements ManageHeadSubjectService {

    private final HDHeadSubjectBySemesterExtendRepository hdHeadSubjectBySemesterExtendRepository;

    private final HDSubjectGroupRepository hdSubjectGroupRepository;

    private final HDSStaffExtendRepository hdStaffExtendRepository;

    private final HDSemesterExtendRepository hdSemesterExtendRepository;

    private final CSVManipulationUtils csvManipulationUtils;

    private final SessionHelper sessionHelper;

    @Override
    public ResponseObject<?> getStaffAndHeadSubjects(@Valid HeadSubjectRequest request) {
        request.setRoleName(Role.TRUONG_MON.name());
        return new ResponseObject<>(
                PageableObject.of(
                        hdHeadSubjectBySemesterExtendRepository.getHeadSubjects(
                                createPageable(request, "id"),
                                request
                        )
                ),
                HttpStatus.OK,
                "Lấy danh sách nhân viên thành công"
        );
    }

    @Override
    public ResponseObject<?> getSubjectGroupAssigned(@Valid SubjectGroupAssignedRequest request) {
        if (request.getCurrentUserId() == null) {
            request.setCurrentUserId(sessionHelper.getCurrentUserId());
        }
        if (request.getDepartmentFacilityId() == null) {
            request.setDepartmentFacilityId(sessionHelper.getCurrentUserDepartmentFacilityId());
        }
        if (request.getSemesterId() == null) {
            request.setSemesterId(sessionHelper.getCurrentSemesterId());
        }
        return new ResponseObject<>(
                PageableObject.of(
                        hdSubjectGroupRepository.getSubjectGroups(
                                createPageable(request, "id"),
                                request
                        )
                ),
                HttpStatus.OK,
                "Lấy danh sách nhóm môn học thành công"
        );
    }

    @Override
    public ResponseObject<?> assignSubjectGroupToStaff(@Valid AssignSubjectGroupToStaffRequest request) {
        if (request.getSemesterId() != null) {
            if (
                    hdSemesterExtendRepository.existsById(request.getSemesterId())
                    && !Objects.equals(request.getSemesterId(), sessionHelper.getCurrentSemesterId())
            ) {
                return ResponseObject.errorForward(
                        "Chỉ được phân công môn học cho giảng viên trong học kỳ hiện tại",
                        HttpStatus.NOT_FOUND
                );
            }
        }
        if (request.getSemesterId() == null) {
            request.setSemesterId(sessionHelper.getCurrentSemesterId());
        }
        Optional<SubjectGroup> subjectGroup = hdSubjectGroupRepository.findById(request.getSubjectGroupId());
        if (subjectGroup.isEmpty()) {
            return ResponseObject.errorForward(
                    "Nhóm môn học không tồn tại",
                    HttpStatus.NOT_FOUND
            );
        }

        Optional<Staff> staff = hdStaffExtendRepository.findById(request.getStaffId());
        if (staff.isEmpty()) {
            return ResponseObject.errorForward(
                    "Nhân viên không tồn tại",
                    HttpStatus.NOT_FOUND
            );
        }

        if (hdHeadSubjectBySemesterExtendRepository.existsBySubjectGroupIdAndStaffIdAndSemesterId(
                request.getSubjectGroupId(),
                request.getStaffId(),
                request.getSemesterId()
        )) {
            return ResponseObject.errorForward(
                    "Nhân viên đã được phân công môn học này",
                    HttpStatus.BAD_REQUEST
            );
        }

        Optional<HeadSubjectBySemester> headSubjectBySemester = hdHeadSubjectBySemesterExtendRepository
                .findBySubjectGroup_IdAndSemester_Id(
                        request.getSubjectGroupId(),
                        request.getSemesterId()
                );

        if (headSubjectBySemester.isPresent()) {
            headSubjectBySemester.get().setStaff(staff.get());
            hdHeadSubjectBySemesterExtendRepository.save(headSubjectBySemester.get());
        } else if (hdHeadSubjectBySemesterExtendRepository.existsByStaffIdAndSemesterId(
                request.getStaffId(),
                request.getSemesterId()
        )) {
            HeadSubjectBySemester headSubjectBySemesterExist = hdHeadSubjectBySemesterExtendRepository
                    .findByStaff_IdAndSemester_Id(
                            request.getStaffId(),
                            request.getSemesterId()
                    ).get();
            headSubjectBySemesterExist.setSubjectGroup(subjectGroup.get());
            hdHeadSubjectBySemesterExtendRepository.save(headSubjectBySemesterExist);
        } else {
            HeadSubjectBySemester newHeadSubjectBySemester = new HeadSubjectBySemester();
            newHeadSubjectBySemester.setStaff(staff.get());
            newHeadSubjectBySemester.setSubjectGroup(subjectGroup.get());
            newHeadSubjectBySemester.setSemester(hdSemesterExtendRepository.getReferenceById(
                    request.getSemesterId()
            ));
            hdHeadSubjectBySemesterExtendRepository.save(newHeadSubjectBySemester);
        }

        return ResponseObject.successForward(
                null,
                "Phân công môn học cho giảng viên thành công"
        );
    }

    public ResponseObject<?> getChangeHistory(int page, int size) {
        String filePath = csvManipulationUtils.getSwitchFacility() + "head_department_logs.csv";
        LoggerObject loggerObject = new LoggerObject();
        loggerObject.setPathFile(filePath);
        List<LoggerObject> listLogRaw = csvManipulationUtils.readFileCSV(loggerObject);
        List<LoggerObject> pagedSanPhamList = listLogRaw.stream()
                .skip((long) page * size)
                .limit(size)
                .toList();
        return ResponseObject.successForward(
                PageableObject.of(new PageImpl<>(pagedSanPhamList, PageRequest.of(page, size), pagedSanPhamList.size())),
                "Lấy lịch sử thay đổi thành công"
        );
    }

    private void logAction(String content, String methodName) {
        String filePath = csvManipulationUtils.getSwitchFacility() + "head_department_logs.csv";
        csvManipulationUtils.createFile(filePath);
        LoggerObject log = csvManipulationUtils.createLoggerObject(content, filePath, "INFO");
        log.setMethod(methodName);
        csvManipulationUtils.writerFileCSV(log);
    }

}
