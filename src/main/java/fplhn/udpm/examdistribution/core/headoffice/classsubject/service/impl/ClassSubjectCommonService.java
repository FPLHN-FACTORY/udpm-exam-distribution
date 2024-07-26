package fplhn.udpm.examdistribution.core.headoffice.classsubject.service.impl;

import fplhn.udpm.examdistribution.core.common.base.ResponseObject;
import fplhn.udpm.examdistribution.core.headoffice.classsubject.model.request.BlockRequest;
import fplhn.udpm.examdistribution.core.headoffice.classsubject.model.request.SemesterRequest;
import fplhn.udpm.examdistribution.core.headoffice.classsubject.repository.BlockClassSubjectRepository;
import fplhn.udpm.examdistribution.core.headoffice.classsubject.repository.FacilityChildClassSubjectRepository;
import fplhn.udpm.examdistribution.core.headoffice.classsubject.repository.FacilityClassSubjectRepository;
import fplhn.udpm.examdistribution.core.headoffice.classsubject.repository.SemesterClassSubjectRepository;
import fplhn.udpm.examdistribution.entity.Facility;
import fplhn.udpm.examdistribution.infrastructure.constant.EntityStatus;
import fplhn.udpm.examdistribution.utils.SessionHelper;
import jakarta.servlet.http.HttpSession;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class ClassSubjectCommonService {

    private final BlockClassSubjectRepository blockClassSubjectRepository;

    private final FacilityChildClassSubjectRepository facilityChildClassSubjectRepository;

    private final FacilityClassSubjectRepository facilityClassSubjectRepository;

    private final SemesterClassSubjectRepository semesterClassSubjectRepository;

    private final SessionHelper sessionHelper;

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
        Facility facility = facilityClassSubjectRepository.findById(sessionHelper.getCurrentUserFacilityId()).orElseThrow(() -> new RuntimeException("Facility not found"));
        return new ResponseObject<>(
                facilityChildClassSubjectRepository.findAllByFacilityAndStatus(facility, EntityStatus.ACTIVE),
                HttpStatus.OK,
                "Get all facility child successfully"
        );
    }

}
