package fplhn.udpm.examdistribution.core.teacher.mockexampaper.service.impl;

import fplhn.udpm.examdistribution.core.common.base.ResponseObject;
import fplhn.udpm.examdistribution.core.teacher.mockexampaper.model.request.TMockExamPaperRequest;
import fplhn.udpm.examdistribution.core.teacher.mockexampaper.model.request.TSubjectMockExamRequest;
import fplhn.udpm.examdistribution.core.teacher.mockexampaper.repository.TMockExamPaperRepository;
import fplhn.udpm.examdistribution.core.teacher.mockexampaper.repository.TMockExamSubjectRepository;
import fplhn.udpm.examdistribution.core.teacher.mockexampaper.repository.TSemesterRepository;
import fplhn.udpm.examdistribution.core.teacher.mockexampaper.service.TMockExamPaperService;
import fplhn.udpm.examdistribution.entity.ExamPaper;
import fplhn.udpm.examdistribution.infrastructure.config.drive.service.GoogleDriveFileService;
import fplhn.udpm.examdistribution.infrastructure.constant.SessionConstant;
import fplhn.udpm.examdistribution.utils.Helper;
import jakarta.servlet.http.HttpSession;
import lombok.RequiredArgsConstructor;
import org.springframework.core.io.Resource;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class TMockExamPaperServiceImpl implements TMockExamPaperService {

    private final TMockExamSubjectRepository mockExamPaperRepository;

    private final TSemesterRepository semesterRepository;

    private final TMockExamPaperRepository examPaperRepository;

    private final GoogleDriveFileService googleDriveFileService;

    private final HttpSession httpSession;

    @Override
    public ResponseObject<?> getAllSubject(TSubjectMockExamRequest request) {
        Pageable pageable = Helper.createPageable(request, "createdDate");
        return new ResponseObject<>(mockExamPaperRepository.getAllSubject(pageable, (String) httpSession.getAttribute(SessionConstant.CURRENT_USER_ID), request), HttpStatus.OK, "Lấy danh sách môn học thành công");
    }

    @Override
    public ResponseObject<?> getMockExams(TMockExamPaperRequest request) {
        return new ResponseObject<>(examPaperRepository.getMockExamPapers(request), HttpStatus.OK, "Lấy danh sách đề thì thử thành công");
    }

    @Override
    public ResponseObject<?> getSemesters() {
        return new ResponseObject<>(semesterRepository.getSemester(), HttpStatus.OK, "Lấy danh sách học kì thành công");
    }

    @Override
    public ResponseObject<?> getFile(String idMockExamPaper) throws IOException {
        try {
            Optional<ExamPaper> mockExamPaper = examPaperRepository.findById(idMockExamPaper);
            Resource resource = null;
            if (mockExamPaper.isPresent()) {
                resource = googleDriveFileService.loadFile(mockExamPaper.get().getPath());
            }
            return new ResponseObject<>(
                    resource,
                    HttpStatus.OK,
                    "Tìm thấy file thành công"
            );
        } catch (Exception e) {
            return new ResponseObject<>(
                    null,
                    HttpStatus.OK,
                    "Không tìm thấy đề"
            );
        }
    }

    @Override
    public ResponseObject<?> downLoad(String fileId) {
        try {
            if (fileId.trim().isEmpty()) {
                return new ResponseObject<>(
                        null,
                        HttpStatus.BAD_REQUEST,
                        "Không tìm thấy file"
                );
            }

            return new ResponseObject<>(
                    googleDriveFileService.loadFile(fileId),
                    HttpStatus.OK,
                    "Tìm thấy file thành công"
            );
        } catch (IOException e) {
            return new ResponseObject<>(
                    null,
                    HttpStatus.BAD_REQUEST,
                    "Đã có 1 vài lỗi xảy ra"
            );
        }
    }
}
