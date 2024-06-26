package fplhn.udpm.examdistribution.core.headoffice.staff.service.impl;

import fplhn.udpm.examdistribution.core.common.base.ResponseObject;
import fplhn.udpm.examdistribution.core.headoffice.staff.model.request.HOStaffRequest;
import fplhn.udpm.examdistribution.core.headoffice.staff.repository.HORoleStaffRepository;
import fplhn.udpm.examdistribution.core.headoffice.staff.service.HORoleStaffService;
import org.springframework.stereotype.Service;

@Service
public class HORoleStaffServiceImpl implements HORoleStaffService {

    private HORoleStaffRepository staffRepo;

    public HORoleStaffServiceImpl(HORoleStaffRepository staffRepo) {
        this.staffRepo = staffRepo;
    }

    public ResponseObject<?> getStaffByRole(HOStaffRequest hoRoleStaffRequest){
        return null;
    }

}
