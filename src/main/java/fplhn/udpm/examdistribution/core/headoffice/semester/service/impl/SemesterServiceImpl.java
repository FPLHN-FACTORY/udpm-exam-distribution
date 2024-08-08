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
import fplhn.udpm.examdistribution.infrastructure.constant.BlockName;
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
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.Date;
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
                semesterExtendRepository.getAllSemester(pageable, request),
                HttpStatus.OK,
                "Lấy danh sách học kỳ thành công!"
        );
    }

    @Override
    public ResponseObject<?> createSemester(@Valid CreateUpdateSemesterRequest request) {
        try {
            LocalDateTime startDateSemester = Instant.ofEpochMilli(request.getStartTime())
                    .atZone(ZoneId.systemDefault())
                    .toLocalDateTime();
            LocalDateTime endDateSemester = Instant.ofEpochMilli(request.getEndTime())
                    .atZone(ZoneId.systemDefault())
                    .toLocalDateTime();

            Integer yearStartTime = startDateSemester.getYear();
            Integer yearEndTime = endDateSemester.getYear();
            String name = SemesterName.valueOf(request.getSemesterName()).toString();
            Long startTimeSemester = request.getStartTimeCustom();
            Long endTimeSemester = request.getEndTimeCustom();

            if (!yearStartTime.equals(yearEndTime)) {
                return new ResponseObject<>(null, HttpStatus.BAD_REQUEST, "Thời gian bắt đầu và kết thúc học kỳ phải cùng một năm!");
            }

            if (request.getEndTimeBlock1() > endTimeSemester || request.getEndTimeBlock1() < startTimeSemester) {
                return new ResponseObject<>(null, HttpStatus.BAD_REQUEST, "Thời gian kết thúc block 1 phải nằm trong khoảng thời gian của học kỳ!");
            }

            if (!semesterExtendRepository.checkConflictTime(startTimeSemester, endTimeSemester).isEmpty()) {
                return new ResponseObject<>(null, HttpStatus.CONFLICT, "Đã có học kỳ trong khoảng thời gian này!");
            }

            Optional<Semester> existingHocKy = semesterExtendRepository.existingBySemesterNameAndSemesterYear(name, yearStartTime);
            if (existingHocKy.isPresent()) {
                return new ResponseObject<>(null, HttpStatus.CONFLICT, "Học kỳ đã tồn tại!");
            }

            Semester semester = new Semester();
            semester.setSemesterName(SemesterName.valueOf(name));
            semester.setYear(yearStartTime);
            semester.setStartTime(startTimeSemester);
            semester.setEndTime(endTimeSemester);
            semester.setStatus(EntityStatus.ACTIVE);
            Semester semesterSave = semesterExtendRepository.save(semester);

            createBlock(semesterSave, request.getEndTimeBlock1Custom());

            return new ResponseObject<>(null, HttpStatus.CREATED, "Thêm học kỳ, block thành công!");
        } catch (Exception e) {
            e.printStackTrace();
            return new ResponseObject<>(null, HttpStatus.BAD_REQUEST, "Thêm học kỳ thất bại!");
        }
    }

    @Override
    public ResponseObject<?> updateSemester(CreateUpdateSemesterRequest request) {
        Optional<Semester> existingSemester = semesterExtendRepository.findById(request.getSemesterId());
        if (existingSemester.isPresent()) {
            Semester semester = existingSemester.get();
            LocalDateTime startDateSemester = Instant.ofEpochMilli(request.getStartTime())
                    .atZone(ZoneId.systemDefault())
                    .toLocalDateTime();
            LocalDateTime endDateSemester = Instant.ofEpochMilli(request.getEndTime())
                    .atZone(ZoneId.systemDefault())
                    .toLocalDateTime();
            Integer yearStartTime = startDateSemester.getYear();
            Integer yearEndTime = endDateSemester.getYear();
            Long startTimeSemester = request.getStartTimeCustom();
            Long endTimeSemester = request.getEndTimeCustom();

            if (!yearStartTime.equals(yearEndTime)) {
                return new ResponseObject<>(null, HttpStatus.BAD_REQUEST, "Thời gian bắt đầu và kết thúc học kỳ phải cùng một năm!");
            }

            if (request.getEndTimeBlock1() > endTimeSemester || request.getEndTimeBlock1() < startTimeSemester) {
                return new ResponseObject<>(null, HttpStatus.BAD_REQUEST, "Thời gian kết thúc block 1 phải nằm trong khoảng thời gian của học kỳ!");
            }

            List<Semester> semesters = semesterExtendRepository.checkConflictTime(startTimeSemester, endTimeSemester);
            if (!semesters.isEmpty()) {
                for (Semester s : semesters) {
                    if (!s.getId().equals(semester.getId())) {
                        return new ResponseObject<>(null, HttpStatus.CONFLICT, "Đã có học kỳ trong khoảng thời gian này!");
                    }
                }
            }

            String name = SemesterName.valueOf(request.getSemesterName()).toString();

            Optional<Semester> existingByHocKyAndYear = semesterExtendRepository.existingBySemesterNameAndSemesterYear(name, yearStartTime);
            if (existingByHocKyAndYear.isPresent() && !existingByHocKyAndYear.get().equals(semester)) {
                return new ResponseObject<>(null, HttpStatus.CONFLICT,
                        "Học kỳ đã tồn tại!");
            }

            semester.setSemesterName(SemesterName.valueOf(request.getSemesterName()));
            semester.setYear(yearStartTime);
            semester.setStartTime(startTimeSemester);
            semester.setEndTime(endTimeSemester);
            Semester semesterSave = semesterExtendRepository.save(semester);

            createBlock(semesterSave, request.getEndTimeBlock1Custom());

            return new ResponseObject<>(null, HttpStatus.OK, "Cập nhật học kỳ thành công!");
        }
        return new ResponseObject<>(null, HttpStatus.NOT_FOUND, "Học kỳ không tồn tại!");
    }

    public void createBlock(Semester semester, Long endTimeBlock1) {
        Block block1 = new Block();
        Optional<Block> block1Find = blockExtendRepository.findByNameAndSemester_Id(BlockName.BLOCK_1, semester.getId());
        if (block1Find.isPresent()) {
            block1 = block1Find.get();
        }
        block1.setSemester(semester);
        block1.setStatus(EntityStatus.ACTIVE);
        block1.setName(BlockName.BLOCK_1);
        block1.setStartTime(semester.getStartTime());
        LocalDateTime endDateBlock1 = Instant.ofEpochMilli(endTimeBlock1)
                .atZone(ZoneId.systemDefault())
                .toLocalDateTime();
        block1.setEndTime(endTimeBlock1);
        blockExtendRepository.save(block1);

        Block block2 = new Block();
        Optional<Block> block2Find = blockExtendRepository.findByNameAndSemester_Id(BlockName.BLOCK_2, semester.getId());
        if (block2Find.isPresent()) {
            block2 = block2Find.get();
        }
        block2.setSemester(semester);
        block2.setName(BlockName.BLOCK_2);
        block2.setStatus(EntityStatus.ACTIVE);
        block2.setEndTime(semester.getEndTime());
        LocalDateTime nextDayStartTime = endDateBlock1.plusDays(1)
                .withHour(0)
                .withMinute(0)
                .withSecond(0)
                .withNano(0);
        block2.setStartTime(nextDayStartTime.atZone(ZoneId.systemDefault())
                .toInstant().toEpochMilli());
        blockExtendRepository.save(block2);
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
