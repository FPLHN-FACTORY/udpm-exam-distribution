package fplhn.udpm.examdistribution.core.headdepartment.classsubject.service.impl;

import fplhn.udpm.examdistribution.core.headdepartment.classsubject.model.request.ClassSubjectByStaffRequest;
import fplhn.udpm.examdistribution.core.headdepartment.classsubject.repository.HDStaffSubjectExtendRepository;
import fplhn.udpm.examdistribution.entity.StaffSubject;
import fplhn.udpm.examdistribution.infrastructure.constant.EntityStatus;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.Optional;

@Component
@RequiredArgsConstructor
public class HDStaffSubjectService {

    private final HDStaffSubjectExtendRepository HDStaffSubjectExtendRepository;

    public void updateStaffSubject(StaffSubject staffSubject) {
        Optional<StaffSubject> staffSubjectOptional = HDStaffSubjectExtendRepository
                .findByClassSubject(staffSubject.getClassSubject());

        StaffSubject staffSubjectAddOrUpdate = staffSubjectOptional.orElse(staffSubject);
        staffSubjectAddOrUpdate.setStatus(EntityStatus.ACTIVE);

        HDStaffSubjectExtendRepository.save(staffSubjectAddOrUpdate);
    }

    public void createStaffSubject(StaffSubject staffSubject) {
        staffSubject.setStatus(EntityStatus.ACTIVE);
        HDStaffSubjectExtendRepository.save(staffSubject);
    }

    public void removeStaffSubject(ClassSubjectByStaffRequest request) {
        Integer count = HDStaffSubjectExtendRepository.countClassSubjectByStaff(request);
        String idStaffSubject = HDStaffSubjectExtendRepository.getClassSubjectId(request);
        if (count <= 1 && idStaffSubject != null) {
            Optional<StaffSubject> staffSubject = HDStaffSubjectExtendRepository.findById(idStaffSubject);
            if (staffSubject.isPresent()) {
                staffSubject.get().setStatus(EntityStatus.INACTIVE);
                HDStaffSubjectExtendRepository.save(staffSubject.get());
            }
        }
    }

}
