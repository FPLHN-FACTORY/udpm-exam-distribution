package fplhn.udpm.examdistribution.core.headoffice.semester.service.impl;

import fplhn.udpm.examdistribution.core.common.base.PageableObject;
import fplhn.udpm.examdistribution.core.common.base.ResponseObject;
import fplhn.udpm.examdistribution.core.headoffice.block.repository.BlockExtendRepository;
import fplhn.udpm.examdistribution.core.headoffice.semester.model.request.CreateUpdateSemesterRequest;
import fplhn.udpm.examdistribution.core.headoffice.semester.model.request.SemesterRequest;
import fplhn.udpm.examdistribution.core.headoffice.semester.repository.SemesterExtendRepository;
import fplhn.udpm.examdistribution.core.headoffice.semester.service.SemesterService;
import fplhn.udpm.examdistribution.entity.Block;
import fplhn.udpm.examdistribution.entity.Semester;
import fplhn.udpm.examdistribution.infrastructure.constant.EntityStatus;
import fplhn.udpm.examdistribution.infrastructure.constant.SemesterName;
import fplhn.udpm.examdistribution.utils.Helper;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.validation.annotation.Validated;

import java.time.Instant;
import java.time.LocalDate;
import java.time.ZoneId;
import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
@Validated
public class SemesterServiceImpl implements SemesterService {

    private final SemesterExtendRepository semesterExtendRepository;

    private final BlockExtendRepository blockExtendRepository;

    @Override
    public ResponseObject<?> getAllSemester(SemesterRequest request) {
        Pageable pageable = Helper.createPageable(request, "createdDate");
        return new ResponseObject<>(
                PageableObject.of(semesterExtendRepository.getAllSemester(pageable, request)),
                HttpStatus.OK,
                "Lấy danh sách học kỳ thành công!"
        );
    }

    @Override
    public ResponseObject<?> createSemester(@Valid CreateUpdateSemesterRequest createUpdateSemesterRequest) {
        String name = SemesterName.valueOf(createUpdateSemesterRequest.getSemesterName()).toString();
        Integer year = createUpdateSemesterRequest.getSemesterYear();
        Long startDate = createUpdateSemesterRequest.getStartTime();

        Optional<Semester> existingHocKy = semesterExtendRepository.existingBySemesterNameAndSemesterYear(name, year);
        if (existingHocKy.isPresent()) {
            return new ResponseObject<>(null, HttpStatus.CONFLICT, "Học kỳ đã tồn tại!");
        }

        LocalDate localDate = LocalDate.ofInstant(Instant.ofEpochMilli(startDate), ZoneId.systemDefault());
        Integer yearStartDate = localDate.getYear();
        if (yearStartDate.compareTo(year) != 0) {
            return new ResponseObject<>(null, HttpStatus.CONFLICT,
                    "Thời gian bắt đầu phải trùng với năm học!");
        }

        Semester semester = new Semester();
        semester.setSemesterName(SemesterName.valueOf(name));
        semester.setYear(year);
        semester.setStartTime(startDate);
        semester.setStatus(EntityStatus.ACTIVE);
        semesterExtendRepository.save(semester);

        return new ResponseObject<>(null, HttpStatus.CREATED, "Thêm học kỳ thành công!");
    }

    @Override
    public ResponseObject<?> updateSemester(String semesterId, CreateUpdateSemesterRequest createUpdateSemesterRequest) {
        Optional<Semester> existingSemester = semesterExtendRepository.findById(semesterId);
        if (existingSemester.isPresent()) {
            Semester semester = existingSemester.get();
            String name = SemesterName.valueOf(createUpdateSemesterRequest.getSemesterName()).toString();
            Integer year = createUpdateSemesterRequest.getSemesterYear();
            Long startTime = createUpdateSemesterRequest.getStartTime();

            Optional<Semester> existingByHocKyAndYear = semesterExtendRepository.existingBySemesterNameAndSemesterYear(name, year);
            if (existingByHocKyAndYear.isPresent() && !existingByHocKyAndYear.get().equals(semester)) {
                return new ResponseObject<>(null, HttpStatus.CONFLICT,
                        "Học kỳ đã tồn tại!");
            }

            LocalDate localDate = LocalDate.ofInstant(Instant.ofEpochMilli(startTime), ZoneId.systemDefault());
            Integer yearStartTime = localDate.getYear();
            if (yearStartTime.compareTo(year) != 0) {
                return new ResponseObject<>(null, HttpStatus.CONFLICT,
                        "Thời gian bắt đầu phải trùng với năm học!");
            }

            semester.setSemesterName(SemesterName.valueOf(createUpdateSemesterRequest.getSemesterName()));
            semester.setYear(year);
            semester.setStartTime(startTime);
            semesterExtendRepository.save(semester);
            return new ResponseObject<>(null, HttpStatus.OK, "Cập nhật học kỳ thành công!");
        }
        return new ResponseObject<>(null, HttpStatus.NOT_FOUND, "Học kỳ không tồn tại!");
    }

    @Override
    public ResponseObject<?> getSemesterById(String semesterId) {
        return semesterExtendRepository.getDetailSemesterById(semesterId)
                .map(semester -> new ResponseObject<>(semester, HttpStatus.OK, "Lấy thông tin học kỳ thành công!"))
                .orElseGet(() -> new ResponseObject<>(null, HttpStatus.NOT_FOUND, "Học kỳ không tồn tại!"));
    }

    @Override
    public ResponseObject<?> statusChangeSemester(String semesterId) {
        Optional<Semester> existingSemester = semesterExtendRepository.findById(semesterId);
        if (existingSemester.isPresent()) {
            Semester semester = existingSemester.get();
            List<Block> blockList = blockExtendRepository.findAllBySemesterId(semesterId);
            if (semester.getStatus().equals(EntityStatus.ACTIVE)) {
                semester.setStatus(EntityStatus.INACTIVE);
                for (Block block : blockList) {
                    block.setStatus(EntityStatus.INACTIVE);
                    blockExtendRepository.save(block);
                }
            } else {
                semester.setStatus(EntityStatus.ACTIVE);
            }
            semesterExtendRepository.save(semester);
            return new ResponseObject<>(null, HttpStatus.OK, "Thay đổi trạng thái học kỳ thành công!");

        }
        return new ResponseObject<>(null, HttpStatus.NOT_FOUND, "Học kỳ không tồn tại!");
    }
}
