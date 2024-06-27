package fplhn.udpm.examdistribution.core.headoffice.classsubject.service.impl;

import fplhn.udpm.examdistribution.core.headoffice.classsubject.model.request.CountClassSubjectByStaffRequest;
import fplhn.udpm.examdistribution.core.headoffice.classsubject.model.response.CountClassSubjectByStaffResponse;
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
        Optional<StaffSubject> subjectOptional = staffSubjectExtendRepository
                .findByStaffAndSubjectAndRecentlySemester(staffSubject.getStaff(),
                        staffSubject.getSubject(), staffSubject.getRecentlySemester());

        StaffSubject staffSubjectAddOrUpdate = subjectOptional.orElse(staffSubject);
        staffSubjectAddOrUpdate.setStatus(EntityStatus.ACTIVE);

        staffSubjectExtendRepository.save(staffSubjectAddOrUpdate);
    }

    public void removeStaffSubject(CountClassSubjectByStaffRequest request) {
        Optional<CountClassSubjectByStaffResponse> countClassSubjectByStaffResponseOptional =
                staffSubjectExtendRepository.countClassSubjectByStaff(request);

        if (countClassSubjectByStaffResponseOptional.isPresent() && countClassSubjectByStaffResponseOptional.get().getCountClassSubject() <= 1) {

            String idStaffSubject = countClassSubjectByStaffResponseOptional.get().getStaffSubjectId();
            StaffSubject staffSubject = staffSubjectExtendRepository.findById(idStaffSubject).get();
            staffSubject.setStatus(EntityStatus.INACTIVE);
            staffSubjectExtendRepository.save(staffSubject);
        }
    }

}
