package fplhn.udpm.examdistribution.core.headoffice.department.department.service.impl;

import fplhn.udpm.examdistribution.core.common.base.PageableObject;
import fplhn.udpm.examdistribution.core.common.base.ResponseObject;
import fplhn.udpm.examdistribution.core.headoffice.department.department.model.request.CreateOrUpdateMajorRequest;
import fplhn.udpm.examdistribution.core.headoffice.department.department.model.request.FindMajorRequest;
import fplhn.udpm.examdistribution.core.headoffice.department.department.repository.DPDepartmentExtendRepository;
import fplhn.udpm.examdistribution.core.headoffice.department.department.repository.DPMajorExtendRepository;
import fplhn.udpm.examdistribution.core.headoffice.department.department.service.MajorService;
import fplhn.udpm.examdistribution.entity.Department;
import fplhn.udpm.examdistribution.entity.Major;
import fplhn.udpm.examdistribution.infrastructure.constant.EntityStatus;
import fplhn.udpm.examdistribution.utils.Helper;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.validation.annotation.Validated;

import java.util.Optional;

@Service
@Validated
@RequiredArgsConstructor
public class MajorServiceImpl implements MajorService {

    private final DPMajorExtendRepository majorRepository;

    private final DPDepartmentExtendRepository departmentRepository;

    @Override
    public ResponseObject<?> getAllMajor(String id, FindMajorRequest request) {
        Pageable pageable = Helper.createPageable(request, "createdDate");
        return new ResponseObject<>(
                PageableObject.of(majorRepository.getAllMajorByDepartmentIdFilter(id, pageable, request)),
                HttpStatus.OK,
                "Lấy thành công danh sách chuyên ngành"
        );
    }

    @Override
    public ResponseObject<?> addMajor(@Valid CreateOrUpdateMajorRequest request) {
        request.setMajorName(request.getMajorName().replaceAll("\\s+", " "));
        Optional<Major> existsMajor = majorRepository.findMajorByNameAndDepartmentId(request.getMajorName().trim(), request.getDepartmentId());
        Optional<Department> departmentOptional = departmentRepository.findById(request.getDepartmentId());

        if (departmentOptional.isPresent()) {
            Department currentDepartment = departmentOptional.get();
            if (existsMajor.isEmpty()) {
                Major addMajor = new Major();
                addMajor.setName(request.getMajorName().trim());
                addMajor.setDepartment(currentDepartment);
                addMajor.setStatus(EntityStatus.ACTIVE);
                majorRepository.save(addMajor);

                return new ResponseObject<>(null, HttpStatus.CREATED, "Thêm chuyên ngành vào bộ môn " +
                                                                      currentDepartment.getName() + " thành công");
            } else {
                return new ResponseObject<>(null, HttpStatus.CONFLICT, "Chuyên ngành đã tồn tại trong bộ môn " +
                                                                       currentDepartment.getName());
            }
        } else {
            return new ResponseObject<>(null, HttpStatus.NOT_ACCEPTABLE, "Bộ môn mà bạn đang thêm chuyên " +
                                                                         "ngành không tồn tại [ " + departmentOptional.get().getName() + " ]");
        }
    }

    @Override
    public ResponseObject<?> updateMajor(@Valid CreateOrUpdateMajorRequest request, String id) {
        request.setMajorName(request.getMajorName().replaceAll("\\s+", " "));
        Optional<Major> majorOptional = majorRepository.findById(id);
        Optional<Department> departmentOptional = departmentRepository.findById(request.getDepartmentId());

        if (departmentOptional.isPresent()) {
            Department currentDepartment = departmentOptional.get();
            if (majorOptional.isPresent()) {
                Major majorUpdate = majorOptional.get();

                if (!majorUpdate.getName().trim().equalsIgnoreCase(request.getMajorName().trim())) {
                    Optional<Major> existsMajor = majorRepository.findMajorByNameAndDepartmentId(request.getMajorName().trim(), request.getDepartmentId());
                    if(existsMajor.isPresent()){
                        return new ResponseObject<>(null, HttpStatus.NOT_ACCEPTABLE, "Tên chuyên ngành đã tồn tại: " +
                                                                                     request.getMajorName().trim());
                    }
                }

                majorUpdate.setName(request.getMajorName().trim());
                majorUpdate.setDepartment(currentDepartment);
                majorRepository.save(majorUpdate);

                return new ResponseObject<>(null, HttpStatus.OK, "Cập nhât chuyên ngành vào bộ môn " +
                                                                 currentDepartment.getName() + " thành công");
            } else {
                return new ResponseObject<>(null, HttpStatus.NOT_ACCEPTABLE, "Chuyên ngành không tồn tại trong bộ môn " +
                                                                             currentDepartment.getName());
            }
        } else {
            return new ResponseObject<>(null, HttpStatus.NOT_ACCEPTABLE, "Bộ môn mà bạn đang cập nhật chuyên ngành " +
                                                                         "không tồn tại [ " + departmentOptional.get().getName() + " ]");
        }
    }

    @Override
    public ResponseObject<?> deleteMajor(String id) {
        Optional<Major> majorOptional = majorRepository.findById(id);

        if (majorOptional.isPresent()) {
            Major majorDelete = majorOptional.get();

            majorDelete.setStatus(
                    majorDelete.getStatus().name().equalsIgnoreCase(EntityStatus.ACTIVE.name())
                            ? EntityStatus.INACTIVE : EntityStatus.ACTIVE
            );
            majorRepository.save(majorDelete);
            return new ResponseObject<>(null, HttpStatus.OK, "Chuyển đổi thành công chuyên ngành " +
                                                             majorDelete.getName());
        } else {
            return new ResponseObject<>(null, HttpStatus.OK, "chuyên ngành không tồn tại trong bộ môn");
        }
    }

}
