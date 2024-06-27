package fplhn.udpm.examdistribution.core.headoffice.classsubject.service.impl;

import fplhn.udpm.examdistribution.core.headoffice.classsubject.model.request.ClassSubjectByStaffRequest;
import fplhn.udpm.examdistribution.core.headoffice.classsubject.repository.StaffSubjectExtendRepository;
import fplhn.udpm.examdistribution.entity.StaffSubject;
import fplhn.udpm.examdistribution.infrastructure.constant.EntityStatus;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.Optional;

@Component
@RequiredArgsConstructor
public class StaffSubjectService {

    private final StaffSubjectExtendRepository staffSubjectExtendRepository;

    public void createOrUpdateStaffSubject(StaffSubject staffSubject) {
        Optional<StaffSubject> staffSubjectOptional = staffSubjectExtendRepository
                .findByStaffAndSubjectAndRecentlySemester(staffSubject.getStaff(),
                        staffSubject.getSubject(), staffSubject.getRecentlySemester());

        StaffSubject staffSubjectAddOrUpdate = staffSubjectOptional.orElse(staffSubject);
        staffSubjectAddOrUpdate.setStatus(EntityStatus.ACTIVE);

        staffSubjectExtendRepository.save(staffSubjectAddOrUpdate);
    }

    public void removeStaffSubject(ClassSubjectByStaffRequest request) {
        Integer count = staffSubjectExtendRepository.countClassSubjectByStaff(request);
        String idStaffSubject = staffSubjectExtendRepository.getClassSubjectId(request);
        if (count <= 1 && idStaffSubject != null) {
            Optional<StaffSubject> staffSubject = staffSubjectExtendRepository.findById(idStaffSubject);
            if (staffSubject.isPresent()) {
                staffSubject.get().setStatus(EntityStatus.INACTIVE);
                staffSubjectExtendRepository.save(staffSubject.get());
            }
        }
    }

}
