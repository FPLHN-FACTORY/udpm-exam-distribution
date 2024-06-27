package fplhn.udpm.examdistribution.core.headoffice.classsubject.service.impl;

import fplhn.udpm.examdistribution.core.common.base.ResponseObject;
import fplhn.udpm.examdistribution.core.headoffice.classsubject.repository.BlockClassSubjectRepository;
import fplhn.udpm.examdistribution.core.headoffice.classsubject.repository.FacilityChildClassSubjectRepository;
import fplhn.udpm.examdistribution.core.headoffice.classsubject.repository.StaffClassSubjectRepository;
import fplhn.udpm.examdistribution.core.headoffice.classsubject.repository.SubjectClassSubjectRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class ClassSubjectCommonService {

    private final BlockClassSubjectRepository blockClassSubjectRepository;

    private final FacilityChildClassSubjectRepository facilityChildClassSubjectRepository;

    private final StaffClassSubjectRepository staffClassSubjectRepository;

    private final SubjectClassSubjectRepository subjectClassSubjectRepository;

    public ResponseObject<?> getAllBlock() {
        return new ResponseObject<>(
                blockClassSubjectRepository.findAll(),
                HttpStatus.OK,
                "Get all block successfully"
        );
    }

    public ResponseObject<?> getAllBlockByYear(Integer year) {
        return new ResponseObject<>(
                blockClassSubjectRepository.getAllBlockByYear(year),
                HttpStatus.OK,
                "Get all block by year successfully"
        );
    }

    public ResponseObject<?> getAllFacilityChild() {
        return new ResponseObject<>(
                facilityChildClassSubjectRepository.findAll(),
                HttpStatus.OK,
                "Get all facility child successfully"
        );
    }

    public ResponseObject<?> getAllStaff() {
        return new ResponseObject<>(
                staffClassSubjectRepository.findAll(),
                HttpStatus.OK,
                "Get all staff successfully"
        );
    }

    public ResponseObject<?> findStaffByCode(String staffCode) {
        return staffClassSubjectRepository.findByStaffCode(staffCode)
                .map(staff -> new ResponseObject<>(staff, HttpStatus.OK, "Find staff by code"))
                .orElseGet(() -> new ResponseObject<>(null, HttpStatus.NOT_FOUND, "Staff not found"));
    }

    public ResponseObject<?> getAllSubject() {
        return new ResponseObject<>(
                subjectClassSubjectRepository.findAll(),
                HttpStatus.OK,
                "Get all subject successfully"
        );
    }

    public ResponseObject<?> findSubjectByCode(String subjectCode) {
        return subjectClassSubjectRepository.findBySubjectCode(subjectCode)
                .map(subject -> new ResponseObject<>(subject, HttpStatus.OK, "Find subject by code"))
                .orElseGet(() -> new ResponseObject<>(null, HttpStatus.NOT_FOUND, "Subject not found"));
    }

}
