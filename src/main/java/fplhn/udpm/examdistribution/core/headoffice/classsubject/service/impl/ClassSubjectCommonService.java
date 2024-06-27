package fplhn.udpm.examdistribution.core.headoffice.classsubject.service.impl;

import fplhn.udpm.examdistribution.core.common.base.ResponseObject;
import fplhn.udpm.examdistribution.core.headoffice.classsubject.model.request.BlockRequest;
import fplhn.udpm.examdistribution.core.headoffice.classsubject.model.request.SemesterRequest;
import fplhn.udpm.examdistribution.core.headoffice.classsubject.repository.BlockClassSubjectRepository;
import fplhn.udpm.examdistribution.core.headoffice.classsubject.repository.FacilityChildClassSubjectRepository;
import fplhn.udpm.examdistribution.core.headoffice.classsubject.repository.SemesterClassSubjectRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class ClassSubjectCommonService {

    private final BlockClassSubjectRepository blockClassSubjectRepository;

    private final FacilityChildClassSubjectRepository facilityChildClassSubjectRepository;

    private final SemesterClassSubjectRepository semesterClassSubjectRepository;

    public ResponseObject<?> findBySemesterByNameAndYear(SemesterRequest request) {
        return new ResponseObject<>(
                semesterClassSubjectRepository.findByNameAndYear(request),
                HttpStatus.OK,
                "Find successfully"
        );
    }

    public ResponseObject<?> getAllBlockByYear(BlockRequest request) {
        return new ResponseObject<>(
                blockClassSubjectRepository.getAllBlockByYear(request),
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

}
