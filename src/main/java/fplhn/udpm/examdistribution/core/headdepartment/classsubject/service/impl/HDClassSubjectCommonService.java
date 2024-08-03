package fplhn.udpm.examdistribution.core.headdepartment.classsubject.service.impl;

import fplhn.udpm.examdistribution.core.common.base.ResponseObject;
import fplhn.udpm.examdistribution.core.headdepartment.classsubject.model.request.BlockRequest;
import fplhn.udpm.examdistribution.core.headdepartment.classsubject.model.request.SemesterRequest;
import fplhn.udpm.examdistribution.core.headdepartment.classsubject.repository.HDBlockClassSubjectRepository;
import fplhn.udpm.examdistribution.core.headdepartment.classsubject.repository.HDFacilityChildClassSubjectRepository;
import fplhn.udpm.examdistribution.core.headdepartment.classsubject.repository.HDFacilityClassSubjectRepository;
import fplhn.udpm.examdistribution.core.headdepartment.classsubject.repository.HDSemesterClassSubjectRepository;
import fplhn.udpm.examdistribution.entity.Facility;
import fplhn.udpm.examdistribution.infrastructure.constant.EntityStatus;
import fplhn.udpm.examdistribution.utils.SessionHelper;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class HDClassSubjectCommonService {

    private final HDBlockClassSubjectRepository HDBlockClassSubjectRepository;

    private final HDFacilityChildClassSubjectRepository HDFacilityChildClassSubjectRepository;

    private final HDFacilityClassSubjectRepository HDFacilityClassSubjectRepository;

    private final HDSemesterClassSubjectRepository HDSemesterClassSubjectRepository;

    private final SessionHelper sessionHelper;

    public ResponseObject<?> findBySemesterByNameAndYear(SemesterRequest request) {
        return new ResponseObject<>(
                HDSemesterClassSubjectRepository.findByNameAndYear(request),
                HttpStatus.OK,
                "Find successfully"
        );
    }

    public ResponseObject<?> getAllBlockByYear(BlockRequest request) {
        return new ResponseObject<>(
                HDBlockClassSubjectRepository.getAllBlockByYear(request),
                HttpStatus.OK,
                "Get all block by year successfully"
        );
    }

    public ResponseObject<?> getAllFacilityChild() {
        Facility facility = HDFacilityClassSubjectRepository.findById(sessionHelper.getCurrentUserFacilityId()).orElseThrow(() -> new RuntimeException("Facility not found"));
        return new ResponseObject<>(
                HDFacilityChildClassSubjectRepository.findAllByFacilityAndStatus(facility, EntityStatus.ACTIVE),
                HttpStatus.OK,
                "Get all facility child successfully"
        );
    }

}
