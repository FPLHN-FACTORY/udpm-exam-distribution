package fplhn.udpm.examdistribution.core.headoffice.classsubject.service.impl;

import fplhn.udpm.examdistribution.core.common.base.PageableObject;
import fplhn.udpm.examdistribution.core.common.base.ResponseObject;
import fplhn.udpm.examdistribution.core.headoffice.classsubject.model.request.ClassSubjectKeywordRequest;
import fplhn.udpm.examdistribution.core.headoffice.classsubject.model.request.ClassSubjectRequest;
import fplhn.udpm.examdistribution.core.headoffice.classsubject.model.request.CountClassSubjectByStaffRequest;
import fplhn.udpm.examdistribution.core.headoffice.classsubject.model.request.CreateUpdateClassSubjectRequest;
import fplhn.udpm.examdistribution.core.headoffice.classsubject.repository.BlockClassSubjectRepository;
import fplhn.udpm.examdistribution.core.headoffice.classsubject.repository.ClassSubjectExtendRepository;
import fplhn.udpm.examdistribution.core.headoffice.classsubject.repository.FacilityChildClassSubjectRepository;
import fplhn.udpm.examdistribution.core.headoffice.classsubject.repository.StaffClassSubjectRepository;
import fplhn.udpm.examdistribution.core.headoffice.classsubject.repository.StaffSubjectExtendRepository;
import fplhn.udpm.examdistribution.core.headoffice.classsubject.repository.SubjectClassSubjectRepository;
import fplhn.udpm.examdistribution.core.headoffice.classsubject.service.ClassSubjectService;
import fplhn.udpm.examdistribution.entity.Block;
import fplhn.udpm.examdistribution.entity.ClassSubject;
import fplhn.udpm.examdistribution.entity.FacilityChild;
import fplhn.udpm.examdistribution.entity.Semester;
import fplhn.udpm.examdistribution.entity.Staff;
import fplhn.udpm.examdistribution.entity.StaffSubject;
import fplhn.udpm.examdistribution.entity.Subject;
import fplhn.udpm.examdistribution.infrastructure.constant.EntityStatus;
import fplhn.udpm.examdistribution.infrastructure.constant.Shift;
import fplhn.udpm.examdistribution.utils.Helper;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.validation.annotation.Validated;

import java.util.Optional;

@Service
@RequiredArgsConstructor
@Validated
public class ClassSubjectServiceImpl implements ClassSubjectService {

    private final ClassSubjectExtendRepository classSubjectExtendRepository;

    private final StaffSubjectExtendRepository staffSubjectExtendRepository;

    private final StaffSubjectService staffSubjectService;

    private final BlockClassSubjectRepository blockClassSubjectRepository;

    private final FacilityChildClassSubjectRepository facilityChildClassSubjectRepository;

    private final StaffClassSubjectRepository staffClassSubjectRepository;

    private final SubjectClassSubjectRepository subjectClassSubjectRepository;


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

        Optional<FacilityChild> facilityChildOptional = facilityChildClassSubjectRepository.findById(request.getFacilityChildId());
        if (facilityChildOptional.isEmpty()) {
            return new ResponseObject<>(null, HttpStatus.NOT_FOUND, "Facility child not found");
        }

        Optional<Staff> staffOptional = staffClassSubjectRepository.findByStaffCode(request.getStaffCode());
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
        classSubjectExtendRepository.save(classSubject);

        // them giao vien day mon
        StaffSubject staffSubject = new StaffSubject();
        staffSubject.setSubject(subjectOptional.get());
        staffSubject.setStaff(staffOptional.get());
        staffSubject.setRecentlySemester(semester);

        staffSubjectService.createOrUpdateStaffSubject(staffSubject);

        return new ResponseObject<>(null, HttpStatus.CREATED, "Create class subject successfully");
    }

    @Override
    public ResponseObject<?> updateClassSubject(String classSubjectId, CreateUpdateClassSubjectRequest request) {

        Optional<ClassSubject> classSubjectOptional = classSubjectExtendRepository.findById(classSubjectId);
        if (classSubjectOptional.isEmpty()) {
            return new ResponseObject<>(null, HttpStatus.NOT_FOUND, "Class subject not found");
        }

        Optional<Block> blockOptional = blockClassSubjectRepository.findById(request.getBlockId());
        if (blockOptional.isEmpty()) {
            return new ResponseObject<>(null, HttpStatus.NOT_FOUND, "Block not found");
        }

        Optional<FacilityChild> facilityChildOptional = facilityChildClassSubjectRepository.findById(request.getFacilityChildId());
        if (facilityChildOptional.isEmpty()) {
            return new ResponseObject<>(null, HttpStatus.NOT_FOUND, "Facility child not found");
        }

        Optional<Staff> staffOptional = staffClassSubjectRepository.findByStaffCode(request.getStaffCode());
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

        // them giao vien day mon
        StaffSubject staffSubject = new StaffSubject();
        staffSubject.setSubject(subjectOptional.get());
        staffSubject.setStaff(staffOptional.get());
        staffSubject.setRecentlySemester(semester);
        System.out.println("+++++++++++++++++++++++++++++++++++++++");
        System.out.println(request.getStaffCode());
        System.out.println(classSubjectOptional.get().getStaff().getStaffCode());

        staffSubjectService.createOrUpdateStaffSubject(staffSubject);
        if (!request.getStaffCode().equalsIgnoreCase(classSubjectOptional.get().getStaff().getStaffCode())) {
            System.out.println("+++++++++++++++++++++++++++++++++++++++");
            System.out.println("Có thay đổi mã nhân viên");
            CountClassSubjectByStaffRequest countClassSubjectByStaffRequest = new CountClassSubjectByStaffRequest();
            countClassSubjectByStaffRequest.setSubjectId(subjectOptional.get().getId());
            countClassSubjectByStaffRequest.setBlockId(blockOptional.get().getId());
            countClassSubjectByStaffRequest.setStaffId(staffOptional.get().getId());

            staffSubjectService.removeStaffSubject(countClassSubjectByStaffRequest);
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
}
