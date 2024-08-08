package fplhn.udpm.examdistribution.core.headoffice.classsubject.service.impl;

import fplhn.udpm.examdistribution.core.common.base.PageableObject;
import fplhn.udpm.examdistribution.core.common.base.ResponseObject;
import fplhn.udpm.examdistribution.core.headoffice.classsubject.model.request.ClassSubjectKeywordRequest;
import fplhn.udpm.examdistribution.core.headoffice.classsubject.model.request.ClassSubjectRequest;
import fplhn.udpm.examdistribution.core.headoffice.classsubject.model.request.CreateUpdateClassSubjectRequest;
import fplhn.udpm.examdistribution.core.headoffice.classsubject.repository.BlockClassSubjectRepository;
import fplhn.udpm.examdistribution.core.headoffice.classsubject.repository.ClassSubjectExtendRepository;
import fplhn.udpm.examdistribution.core.headoffice.classsubject.repository.FacilityChildClassSubjectRepository;
import fplhn.udpm.examdistribution.core.headoffice.classsubject.repository.StaffClassSubjectRepository;
import fplhn.udpm.examdistribution.core.headoffice.classsubject.repository.SubjectClassSubjectRepository;
import fplhn.udpm.examdistribution.core.headoffice.classsubject.service.ClassSubjectService;
import fplhn.udpm.examdistribution.entity.Block;
import fplhn.udpm.examdistribution.entity.ClassSubject;
import fplhn.udpm.examdistribution.entity.FacilityChild;
import fplhn.udpm.examdistribution.entity.HistoryImport;
import fplhn.udpm.examdistribution.entity.Semester;
import fplhn.udpm.examdistribution.entity.Staff;
import fplhn.udpm.examdistribution.entity.Subject;
import fplhn.udpm.examdistribution.infrastructure.constant.EntityStatus;
import fplhn.udpm.examdistribution.infrastructure.constant.LogFileType;
import fplhn.udpm.examdistribution.infrastructure.constant.Shift;
import fplhn.udpm.examdistribution.utils.Helper;
import fplhn.udpm.examdistribution.utils.HistoryLogUtils;
import fplhn.udpm.examdistribution.utils.SessionHelper;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.validation.annotation.Validated;

import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
@Validated
public class ClassSubjectServiceImpl implements ClassSubjectService {

    private final ClassSubjectExtendRepository classSubjectExtendRepository;

    private final BlockClassSubjectRepository blockClassSubjectRepository;

    private final FacilityChildClassSubjectRepository facilityChildClassSubjectRepository;

    private final StaffClassSubjectRepository staffClassSubjectRepository;

    private final SubjectClassSubjectRepository subjectClassSubjectRepository;

    private final HistoryLogUtils historyLogUtils;

    private final SessionHelper sessionHelper;


    @Override
    public ResponseObject<?> getAllClassSubject(ClassSubjectRequest request) {
        Pageable pageable = Helper.createPageable(request, "createdDate");
        return new ResponseObject<>(
                PageableObject.of(classSubjectExtendRepository.getSearchClassSubject(pageable, request)),
                HttpStatus.OK, "Get all class subject successfully"
        );
    }

    @Override
    public ResponseObject<?> getAllClassSubjectByKeyword(ClassSubjectKeywordRequest request) {
        Pageable pageable = Helper.createPageable(request, "createdDate");
        return new ResponseObject<>(
                PageableObject.of(classSubjectExtendRepository.getSearchClassSubjectByKeyword(pageable, request)),
                HttpStatus.OK, "Get all class subject successfully"
        );
    }

    @Override
    public ResponseObject<?> createClassSubject(CreateUpdateClassSubjectRequest request) {

        Optional<Block> blockOptional = blockClassSubjectRepository.findById(request.getBlockId());
        if (blockOptional.isEmpty()) {
            return new ResponseObject<>(null, HttpStatus.NOT_FOUND, "Block not found");
        }

        // Kiểm tra xem ngày có nằm trong khoảng thời gian không
        Block block = blockOptional.get();
        if (request.getDay() < block.getStartTime() || request.getDay() > block.getEndTime()) {
            return new ResponseObject<>(null, HttpStatus.NOT_FOUND, "Ngày phải nằm trong khoảng thời gian của block");
        }

        Optional<FacilityChild> facilityChildOptional = facilityChildClassSubjectRepository.findById(request.getFacilityChildId());
        if (facilityChildOptional.isEmpty()) {
            return new ResponseObject<>(null, HttpStatus.NOT_FOUND, "Facility child not found");
        }

        Optional<Staff> staffOptional = staffClassSubjectRepository.findByStaffCodeAndStatus(request.getStaffCode(), EntityStatus.ACTIVE);
        if (staffOptional.isEmpty()) {
            return new ResponseObject<>(null, HttpStatus.NOT_FOUND, "Staff not found");
        }

        Optional<Subject> subjectOptional = subjectClassSubjectRepository.findBySubjectCode(request.getSubjectCode());
        if (subjectOptional.isEmpty()) {
            return new ResponseObject<>(null, HttpStatus.NOT_FOUND, "Subject not found");
        }

        // check hoc ky => them giao vien day mon
        Semester semester = blockOptional.get().getSemester();
        if (semester == null) {
            return new ResponseObject<>(null, HttpStatus.NOT_FOUND, "Semester not found");
        }

        Optional<ClassSubject> classSubjectOptional = classSubjectExtendRepository
                .findByClassSubjectCodeAndBlockAndFacilityChildAndSubject(request.getClassSubjectCode(),
                        blockOptional.get(),
                        facilityChildOptional.get(),
                        subjectOptional.get());
        if (classSubjectOptional.isPresent()) {
            return new ResponseObject<>(null, HttpStatus.CONFLICT, "Class subject code already exists");
        }

        // them lop mon
        ClassSubject classSubject = new ClassSubject();
        classSubject.setClassSubjectCode(request.getClassSubjectCode());
        classSubject.setDay(request.getDay());
        classSubject.setShift(Shift.valueOf(request.getShift()));
        classSubject.setBlock(blockOptional.get());
        classSubject.setFacilityChild(facilityChildOptional.get());
        classSubject.setStaff(staffOptional.get());
        classSubject.setSubject(subjectOptional.get());
        classSubject.setStatus(EntityStatus.ACTIVE);
        ClassSubject classSubjectSave = classSubjectExtendRepository.save(classSubject);

        return new ResponseObject<>(null, HttpStatus.CREATED, "Create class subject successfully");
    }

    @Override
    @Transactional
    public ResponseObject<?> updateClassSubject(String classSubjectId, CreateUpdateClassSubjectRequest request) {

        Optional<ClassSubject> classSubjectOptional = classSubjectExtendRepository.findById(classSubjectId);
        if (classSubjectOptional.isEmpty()) {
            return new ResponseObject<>(null, HttpStatus.NOT_FOUND, "Class subject not found");
        }

        Optional<Block> blockOptional = blockClassSubjectRepository.findById(request.getBlockId());
        if (blockOptional.isEmpty()) {
            return new ResponseObject<>(null, HttpStatus.NOT_FOUND, "Block not found");
        }

        // Kiểm tra xem ngày có nằm trong khoảng thời gian không
        Block block = blockOptional.get();
        if (request.getDay() < block.getStartTime() || request.getDay() > block.getEndTime()) {
            return new ResponseObject<>(null, HttpStatus.NOT_FOUND, "Ngày phải nằm trong khoảng thời gian của block");
        }

        Optional<FacilityChild> facilityChildOptional = facilityChildClassSubjectRepository.findById(request.getFacilityChildId());
        if (facilityChildOptional.isEmpty()) {
            return new ResponseObject<>(null, HttpStatus.NOT_FOUND, "Facility child not found");
        }

        Optional<Staff> staffOptional = staffClassSubjectRepository.findByStaffCodeAndStatus(request.getStaffCode(), EntityStatus.ACTIVE);
        if (staffOptional.isEmpty()) {
            return new ResponseObject<>(null, HttpStatus.NOT_FOUND, "Staff not found");
        }

        Optional<Subject> subjectOptional = subjectClassSubjectRepository.findBySubjectCode(request.getSubjectCode());
        if (subjectOptional.isEmpty()) {
            return new ResponseObject<>(null, HttpStatus.NOT_FOUND, "Subject not found");
        }

        // check hoc ky => them giao vien day mon
        Semester semester = blockOptional.get().getSemester();
        if (semester == null) {
            return new ResponseObject<>(null, HttpStatus.NOT_FOUND, "Semester not found");
        }

        Optional<ClassSubject> classSubjectDuplicateOptional = classSubjectExtendRepository
                .findByClassSubjectCodeAndBlockAndFacilityChildAndSubject(request.getClassSubjectCode(),
                        blockOptional.get(),
                        facilityChildOptional.get(),
                        subjectOptional.get());
        if (classSubjectDuplicateOptional.isPresent()) {
            if (!classSubjectDuplicateOptional.get().getId().equalsIgnoreCase(classSubjectId)) {
                return new ResponseObject<>(null, HttpStatus.CONFLICT, "Class subject code already exists");
            }
        }

        ClassSubject classSubject = classSubjectOptional.get();
        classSubject.setClassSubjectCode(request.getClassSubjectCode());
        classSubject.setDay(request.getDay());
        classSubject.setShift(Shift.valueOf(request.getShift()));
        classSubject.setBlock(blockOptional.get());
        classSubject.setFacilityChild(facilityChildOptional.get());
        classSubject.setStaff(staffOptional.get());
        classSubject.setSubject(subjectOptional.get());
        classSubject.setStatus(EntityStatus.ACTIVE);
        classSubjectExtendRepository.save(classSubject);

        return new ResponseObject<>(null, HttpStatus.OK, "Update class subject successfully");
    }


    @Override
    public ResponseObject<?> changeClassSubjectStatus(String classSubjectId) {
        return null;
    }

    @Override
    public ResponseObject<?> getClassSubjectById(String classSubjectId) {
        return classSubjectExtendRepository.getDetailClassSubject(classSubjectId)
                .map(classSubject -> new ResponseObject<>(classSubject, HttpStatus.OK, "Get class subject successfully"))
                .orElseGet(() -> new ResponseObject<>(null, HttpStatus.NOT_FOUND, "Class subject not found"));
    }

    @Override
    public ResponseObject<?> getLogsImportClassSubject(int page, int size) {
        List<HistoryImport> listLogRaw = historyLogUtils.getHistoryImportByFacilityIdAndStaffIdAndFileType(
                sessionHelper.getCurrentUserFacilityId(),
                sessionHelper.getCurrentUserId(),
                LogFileType.CLASS_SUBJECT
        );
        List<HistoryImport> loggerObjects = listLogRaw.stream()
                .skip((long) page * size)
                .limit(size)
                .toList();
        return ResponseObject.successForward(
                PageableObject.of(new PageImpl<>(
                        loggerObjects, PageRequest.of(page, size), loggerObjects.size()
                )),
                "Lấy lịch sử thay đổi thành công"
        );
    }

}
