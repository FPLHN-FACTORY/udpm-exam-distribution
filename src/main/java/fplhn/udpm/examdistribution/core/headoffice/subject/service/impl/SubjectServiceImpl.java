package fplhn.udpm.examdistribution.core.headoffice.subject.service.impl;

import fplhn.udpm.examdistribution.core.common.base.PageableObject;
import fplhn.udpm.examdistribution.core.common.base.ResponseObject;
import fplhn.udpm.examdistribution.core.headoffice.subject.model.request.CreateUpdateSubjectRequest;
import fplhn.udpm.examdistribution.core.headoffice.subject.model.request.SubjectRequest;
import fplhn.udpm.examdistribution.core.headoffice.subject.repository.SDepartmentSubjectRepository;
import fplhn.udpm.examdistribution.core.headoffice.subject.repository.SExamTimeBySubjectExtendRepository;
import fplhn.udpm.examdistribution.core.headoffice.subject.repository.SFacilityExtendRepository;
import fplhn.udpm.examdistribution.core.headoffice.subject.repository.SSubjectExtendRepository;
import fplhn.udpm.examdistribution.core.headoffice.subject.service.SubjectService;
import fplhn.udpm.examdistribution.entity.Department;
import fplhn.udpm.examdistribution.entity.ExamTimeBySubject;
import fplhn.udpm.examdistribution.entity.Facility;
import fplhn.udpm.examdistribution.entity.Subject;
import fplhn.udpm.examdistribution.infrastructure.constant.EntityStatus;
import fplhn.udpm.examdistribution.infrastructure.constant.SubjectType;
import fplhn.udpm.examdistribution.utils.Helper;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.validation.annotation.Validated;

import java.util.Optional;

@Service
@RequiredArgsConstructor
@Validated
public class SubjectServiceImpl implements SubjectService {

    private final SSubjectExtendRepository SSubjectExtendRepository;

    private final SDepartmentSubjectRepository SDepartmentSubjectRepository;

    private final SExamTimeBySubjectExtendRepository examTimeBySubjectRepository;

    private final SFacilityExtendRepository facilityRepository;

    @Override
    public ResponseObject<?> getAllSubject(SubjectRequest request) {
        Pageable pageable = Helper.createPageable(request, "id");
        return new ResponseObject<>(
                PageableObject.of(SSubjectExtendRepository.getAllSubject(pageable, request)),
                HttpStatus.OK,
                "Lấy danh sách môn học thành công"
        );
    }

    @Override
    public ResponseObject<?> createSubject(@Valid CreateUpdateSubjectRequest request) {

        Optional<Subject> subjectOptional = SSubjectExtendRepository.findBySubjectCode(request.getSubjectCode());
        if (subjectOptional.isPresent()) {
            return new ResponseObject<>(null, HttpStatus.BAD_REQUEST, "Mã môn học đã tồn tại");
        }

        Optional<Department> department = SDepartmentSubjectRepository.findById(request.getDepartmentId());
        if (department.isEmpty()) {
            return new ResponseObject<>(null, HttpStatus.NOT_FOUND, "Bộ môn không tồn tại");
        }

        Subject subject = new Subject();
        subject.setName(request.getSubjectName());
        subject.setSubjectCode(request.getSubjectCode());
        subject.setDepartment(department.get());
        subject.setSubjectType(SubjectType.valueOf(request.getSubjectType()));
        subject.setSubjectCode(request.getSubjectCode());
        subject.setCreatedTime(System.currentTimeMillis());
//        subject.setSubjectStatus(SubjectStatus.MO);
        subject.setStatus(EntityStatus.ACTIVE);
        Subject subjectSaved = SSubjectExtendRepository.save(subject);

        for (Facility facility : facilityRepository.getListFacility()) {
            ExamTimeBySubject examTimeBySubject = new ExamTimeBySubject();
            examTimeBySubject.setExamTime(60L);
            examTimeBySubject.setSubject(subjectSaved);
            examTimeBySubject.setFacility(facility);
            examTimeBySubject.setAllowOnline(false);
            examTimeBySubject.setPercentRandom(20L);
            examTimeBySubjectRepository.save(examTimeBySubject);
        }

        return new ResponseObject<>(null, HttpStatus.CREATED, "Tạo môn học thành công");
    }

    @Override
    public ResponseObject<?> updateSubject(String subjectId, @Valid CreateUpdateSubjectRequest request) {

        Department department = SDepartmentSubjectRepository.findById(request.getDepartmentId()).orElse(null);
        if (department == null) {
            return new ResponseObject<>(null, HttpStatus.NOT_FOUND, "Bộ môn không tồn tại");
        }


        Optional<Subject> subjectOptional = SSubjectExtendRepository.findById(subjectId)
                .map(subject -> {
                    subject.setName(request.getSubjectName());
                    subject.setSubjectCode(request.getSubjectCode());
//                    subject.setSubjectStatus(subject.getSubjectStatus());
                    subject.setCreatedTime(subject.getCreatedTime());
                    subject.setDepartment(department);
                    subject.setSubjectType(SubjectType.valueOf(request.getSubjectType()));
                    subject.setStatus(EntityStatus.ACTIVE);
                    return SSubjectExtendRepository.save(subject);
                });

        return subjectOptional
                .map(subject -> new ResponseObject<>(null, HttpStatus.OK, "Cập nhật môn học thành công"))
                .orElseGet(() -> new ResponseObject<>(null, HttpStatus.NOT_FOUND, "Môn học không tồn tại"));
    }

    @Override
    public ResponseObject<?> changeSubjectStatus(String subjectId) {
        return null;
    }

    @Override
    public ResponseObject<?> getSubjectById(String subjectId) {
        return SSubjectExtendRepository.getDetailSubjectById(subjectId)
                .map(subject -> new ResponseObject<>(subject, HttpStatus.OK, "Lấy thông tin môn học thành công"))
                .orElseGet(() -> new ResponseObject<>(null, HttpStatus.NOT_FOUND, "Môn học không tồn tại"));
    }

}
