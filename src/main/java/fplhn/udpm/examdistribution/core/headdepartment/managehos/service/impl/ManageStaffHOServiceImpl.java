package fplhn.udpm.examdistribution.core.headdepartment.managehos.service.impl;

import fplhn.udpm.examdistribution.core.common.base.PageableObject;
import fplhn.udpm.examdistribution.core.common.base.ResponseObject;
import fplhn.udpm.examdistribution.core.headdepartment.managehos.model.request.StaffRequest;
import fplhn.udpm.examdistribution.core.headdepartment.managehos.repository.HDStaffExtendRepository;
import fplhn.udpm.examdistribution.core.headdepartment.managehos.service.ManageStaffHOSService;
import fplhn.udpm.examdistribution.utils.Helper;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.validation.annotation.Validated;

@Service
@Validated
@RequiredArgsConstructor
public class ManageStaffHOServiceImpl implements ManageStaffHOSService {

    private final HDStaffExtendRepository hdStaffExtendRepository;

    @Override
    public ResponseObject<?> getAllStaffs(@Valid StaffRequest request) {
        return new ResponseObject<>(
                PageableObject.of(hdStaffExtendRepository.getAllStaffs(Helper.createPageable(request, "id"), request)),
                HttpStatus.OK,
                "Lấy danh sách nhân viên thành công"
        );
    }


}
