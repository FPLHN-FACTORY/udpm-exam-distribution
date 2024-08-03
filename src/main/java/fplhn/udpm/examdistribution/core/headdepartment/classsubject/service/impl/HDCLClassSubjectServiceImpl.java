package fplhn.udpm.examdistribution.core.headdepartment.classsubject.service.impl;

import fplhn.udpm.examdistribution.core.common.base.PageableObject;
import fplhn.udpm.examdistribution.core.common.base.ResponseObject;
import fplhn.udpm.examdistribution.core.headdepartment.classsubject.model.request.ClassSubjectKeywordRequest;
import fplhn.udpm.examdistribution.core.headdepartment.classsubject.model.request.ClassSubjectRequest;
import fplhn.udpm.examdistribution.core.headdepartment.classsubject.model.request.CreateUpdateClassSubjectRequest;
import fplhn.udpm.examdistribution.core.headdepartment.classsubject.repository.HDBlockClassSubjectRepository;
import fplhn.udpm.examdistribution.core.headdepartment.classsubject.repository.HDCLClassSubjectExtendRepository;
import fplhn.udpm.examdistribution.core.headdepartment.classsubject.repository.HDFacilityChildClassSubjectRepository;
import fplhn.udpm.examdistribution.core.headdepartment.classsubject.repository.HDStaffClassSubjectRepository;
import fplhn.udpm.examdistribution.core.headdepartment.classsubject.repository.HDSubjectClassSubjectRepository;
import fplhn.udpm.examdistribution.core.headdepartment.classsubject.service.HDCLClassSubjectService;
import fplhn.udpm.examdistribution.entity.Block;
import fplhn.udpm.examdistribution.entity.ClassSubject;
import fplhn.udpm.examdistribution.entity.FacilityChild;
import fplhn.udpm.examdistribution.entity.Semester;
import fplhn.udpm.examdistribution.entity.Staff;
import fplhn.udpm.examdistribution.entity.Subject;
import fplhn.udpm.examdistribution.infrastructure.constant.EntityStatus;
import fplhn.udpm.examdistribution.infrastructure.constant.Shift;
import fplhn.udpm.examdistribution.utils.Helper;
import fplhn.udpm.examdistribution.utils.SessionHelper;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.validation.annotation.Validated;

import java.util.Optional;

@Service
@RequiredArgsConstructor
@Validated
public class HDCLClassSubjectServiceImpl implements HDCLClassSubjectService {

    private final HDCLClassSubjectExtendRepository HDCLClassSubjectExtendRepository;

    private final HDBlockClassSubjectRepository HDBlockClassSubjectRepository;

    private final HDFacilityChildClassSubjectRepository HDFacilityChildClassSubjectRepository;

    private final HDStaffClassSubjectRepository HDStaffClassSubjectRepository;

    private final HDSubjectClassSubjectRepository HDSubjectClassSubjectRepository;

    private final SessionHelper sessionHelper;


    @Override
    public ResponseObject<?> getAllClassSubject(ClassSubjectRequest request) {
        Pageable pageable = Helper.createPageable(request, "createdDate");
        return new ResponseObject<>(
                PageableObject.of(HDCLClassSubjectExtendRepository.getSearchClassSubject(
                        pageable,
                        request,
                        sessionHelper.getCurrentUserFacilityId()
                )),
                HttpStatus.OK, "Get all class subject successfully"
        );
    }

    @Override
    public ResponseObject<?> getAllClassSubjectByKeyword(ClassSubjectKeywordRequest request) {
        Pageable pageable = Helper.createPageable(request, "createdDate");
        return new ResponseObject<>(
                PageableObject.of(HDCLClassSubjectExtendRepository.getSearchClassSubjectByKeyword(
                        pageable,
                        request,
                        sessionHelper.getCurrentUserFacilityId()
                )),
                HttpStatus.OK, "Get all class subject successfully"
        );
    }

    @Override
    public ResponseObject<?> createClassSubject(CreateUpdateClassSubjectRequest request) {

        Optional<Block> blockOptional = HDBlockClassSubjectRepository.findById(request.getBlockId());
        if (blockOptional.isEmpty()) {
            return new ResponseObject<>(null, HttpStatus.NOT_FOUND, "Block not found");
        }

        // Kiểm tra xem ngày có nằm trong khoảng thời gian không
        Block block = blockOptional.get();
        if (request.getDay() < block.getStartTime() || request.getDay() > block.getEndTime()) {
            return new ResponseObject<>(null, HttpStatus.NOT_FOUND, "Day is out of block range");
        }

        Optional<FacilityChild> facilityChildOptional = HDFacilityChildClassSubjectRepository.findById(request.getFacilityChildId());
        if (facilityChildOptional.isEmpty()) {
            return new ResponseObject<>(null, HttpStatus.NOT_FOUND, "Facility child not found");
        }

        Optional<Staff> staffOptional = HDStaffClassSubjectRepository.findByStaffCodeAndStatus(request.getStaffCode(), EntityStatus.ACTIVE);
        if (staffOptional.isEmpty()) {
            return new ResponseObject<>(null, HttpStatus.NOT_FOUND, "Staff not found");
        }

        Optional<Subject> subjectOptional = HDSubjectClassSubjectRepository.findBySubjectCode(request.getSubjectCode());
        if (subjectOptional.isEmpty()) {
            return new ResponseObject<>(null, HttpStatus.NOT_FOUND, "Subject not found");
        }

        // check hoc ky => them giao vien day mon
        Semester semester = blockOptional.get().getSemester();
        if (semester == null) {
            return new ResponseObject<>(null, HttpStatus.NOT_FOUND, "Semester not found");
        }

        Optional<ClassSubject> classSubjectOptional = HDCLClassSubjectExtendRepository
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
        ClassSubject classSubjectSave = HDCLClassSubjectExtendRepository.save(classSubject);

        // them giao vien day mon
//        StaffSubject staffSubject = new StaffSubject();
//        staffSubject.setSubject(subjectOptional.get());
//        staffSubject.setStaff(staffOptional.get());
//        staffSubject.setRecentlySemester(semester);
//        staffSubject.setClassSubject(classSubjectSave);
//
//        HDStaffSubjectService.createStaffSubject(staffSubject);

        return new ResponseObject<>(null, HttpStatus.CREATED, "Create class subject successfully");
    }

    @Override
    @Transactional
    public ResponseObject<?> updateClassSubject(String classSubjectId, CreateUpdateClassSubjectRequest request) {

        Optional<ClassSubject> classSubjectOptional = HDCLClassSubjectExtendRepository.findById(classSubjectId);
        if (classSubjectOptional.isEmpty()) {
            return new ResponseObject<>(null, HttpStatus.NOT_FOUND, "Class subject not found");
        }

        Optional<Block> blockOptional = HDBlockClassSubjectRepository.findById(request.getBlockId());
        if (blockOptional.isEmpty()) {
            return new ResponseObject<>(null, HttpStatus.NOT_FOUND, "Block not found");
        }

        // Kiểm tra xem ngày có nằm trong khoảng thời gian không
        Block block = blockOptional.get();
        if (request.getDay() < block.getStartTime() || request.getDay() > block.getEndTime()) {
            return new ResponseObject<>(null, HttpStatus.NOT_FOUND, "Day is out of block range");
        }

        Optional<FacilityChild> facilityChildOptional = HDFacilityChildClassSubjectRepository.findById(request.getFacilityChildId());
        if (facilityChildOptional.isEmpty()) {
            return new ResponseObject<>(null, HttpStatus.NOT_FOUND, "Facility child not found");
        }

        Optional<Staff> staffOptional = HDStaffClassSubjectRepository.findByStaffCodeAndStatus(request.getStaffCode(), EntityStatus.ACTIVE);
        if (staffOptional.isEmpty()) {
            return new ResponseObject<>(null, HttpStatus.NOT_FOUND, "Staff not found");
        }

        Optional<Subject> subjectOptional = HDSubjectClassSubjectRepository.findBySubjectCode(request.getSubjectCode());
        if (subjectOptional.isEmpty()) {
            return new ResponseObject<>(null, HttpStatus.NOT_FOUND, "Subject not found");
        }

        // check hoc ky => them giao vien day mon
        Semester semester = blockOptional.get().getSemester();
        if (semester == null) {
            return new ResponseObject<>(null, HttpStatus.NOT_FOUND, "Semester not found");
        }

        Optional<ClassSubject> classSubjectDuplicateOptional = HDCLClassSubjectExtendRepository
                .findByClassSubjectCodeAndBlockAndFacilityChildAndSubject(request.getClassSubjectCode(),
                        blockOptional.get(),
                        facilityChildOptional.get(),
                        subjectOptional.get());
        if (classSubjectDuplicateOptional.isPresent()) {
            if (!classSubjectDuplicateOptional.get().getId().equalsIgnoreCase(classSubjectId)) {
                return new ResponseObject<>(null, HttpStatus.CONFLICT, "Class subject code already exists");
            }
        }

//        // them giao vien day mon
//        StaffSubject staffSubject = new StaffSubject();
//        staffSubject.setSubject(subjectOptional.get());
//        staffSubject.setStaff(staffOptional.get());
//        staffSubject.setRecentlySemester(semester);
//        staffSubject.setClassSubject(classSubjectOptional.get());
//
//        HDStaffSubjectService.updateStaffSubject(staffSubject);
//        String staffCodeOld = classSubjectOptional.get().getStaff().getStaffCode();
//        if (!request.getStaffCode().equalsIgnoreCase(staffCodeOld)) {
//            ClassSubjectByStaffRequest countClassSubjectByStaffRequest = new ClassSubjectByStaffRequest();
//            countClassSubjectByStaffRequest.setSubjectId(subjectOptional.get().getId());
//            countClassSubjectByStaffRequest.setBlockId(blockOptional.get().getId());
//            countClassSubjectByStaffRequest.setStaffId(classSubjectOptional.get().getStaff().getId());
//
//            HDStaffSubjectService.removeStaffSubject(countClassSubjectByStaffRequest);
//        }

        ClassSubject classSubject = classSubjectOptional.get();
        classSubject.setClassSubjectCode(request.getClassSubjectCode());
        classSubject.setDay(request.getDay());
        classSubject.setShift(Shift.valueOf(request.getShift()));
        classSubject.setBlock(blockOptional.get());
        classSubject.setFacilityChild(facilityChildOptional.get());
        classSubject.setStaff(staffOptional.get());
        classSubject.setSubject(subjectOptional.get());
        classSubject.setStatus(EntityStatus.ACTIVE);
        HDCLClassSubjectExtendRepository.save(classSubject);

        return new ResponseObject<>(null, HttpStatus.OK, "Update class subject successfully");
    }


    @Override
    public ResponseObject<?> changeClassSubjectStatus(String classSubjectId) {
        return null;
    }

    @Override
    public ResponseObject<?> getClassSubjectById(String classSubjectId) {
        return HDCLClassSubjectExtendRepository.getDetailClassSubject(classSubjectId)
                .map(classSubject -> new ResponseObject<>(classSubject, HttpStatus.OK, "Get class subject successfully"))
                .orElseGet(() -> new ResponseObject<>(null, HttpStatus.NOT_FOUND, "Class subject not found"));
    }
}
