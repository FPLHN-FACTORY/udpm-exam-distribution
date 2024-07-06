package fplhn.udpm.examdistribution.core.headsubject.uploadexampaper.uploadexampaper.service.impl;

import fplhn.udpm.examdistribution.core.common.base.ResponseObject;
import fplhn.udpm.examdistribution.core.headsubject.uploadexampaper.uploadexampaper.model.response.ListExamPaperResponse;
import fplhn.udpm.examdistribution.core.headsubject.uploadexampaper.uploadexampaper.repository.UEPUploadExamPaperExtendRepository;
import fplhn.udpm.examdistribution.core.headsubject.uploadexampaper.uploadexampaper.service.UploadExamPaperService;
import fplhn.udpm.examdistribution.entity.ExamPaper;
import fplhn.udpm.examdistribution.infrastructure.conflig.googledrive.dto.GoogleDriveFileDTO;
import fplhn.udpm.examdistribution.infrastructure.conflig.googledrive.service.GoogleDriveFileService;
import fplhn.udpm.examdistribution.infrastructure.constant.SessionConstant;
import jakarta.servlet.http.HttpSession;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Service
@RequiredArgsConstructor
public class UploadExamPaperServiceImpl implements UploadExamPaperService {

    private final UEPUploadExamPaperExtendRepository examPaperRepository;

    private final GoogleDriveFileService googleDriveFileService;

    private final HttpSession httpSession;

    @Override
    public ResponseObject<?> getListSubject() {
        String userId = httpSession.getAttribute(SessionConstant.CURRENT_USER_ID).toString();
        return new ResponseObject<>(
                examPaperRepository.getListSubject(userId),
                HttpStatus.OK,
                "Lấy danh sách môn học thành công"
        );
    }

    @Override
    public ResponseObject<?> getAllExamPaper() {
        try {
            List<GoogleDriveFileDTO> listFile = googleDriveFileService.findAllInFolder("1xN3sEW6pXm9ijgAlKlSLkTRUVbTexpBx");

            List<ExamPaper> listExamPaper = examPaperRepository.getListExamPaper();

            List<ListExamPaperResponse> listExamPaperResponses = new ArrayList<>();
            for (GoogleDriveFileDTO file : listFile) {
                for (ExamPaper examPaper : listExamPaper) {
                    if (file.getId().equalsIgnoreCase(examPaper.getPath())) {
                        ListExamPaperResponse examPaperResponse = new ListExamPaperResponse();
                        examPaperResponse.setId(examPaper.getId());
                        examPaperResponse.setExamPaperCode(examPaper.getExamPaperCode());
                        examPaperResponse.setStatus(examPaper.getExamPaperStatus().toString());
                        examPaperResponse.setSubjectId(examPaper.getSubject().getId());
                        examPaperResponse.setSubjectName(examPaper.getSubject().getName());
                        examPaperResponse.setFacilityName(examPaper.getMajorFacility().getDepartmentFacility().getFacility().getName());
                        examPaperResponse.setMajorName(examPaper.getMajorFacility().getMajor().getName());
                        examPaperResponse.setStaffName(examPaper.getStaffUpload().getName() + " - " + examPaper.getStaffUpload().getStaffCode());
                        examPaperResponse.setCreatedDate(examPaper.getCreatedDate());
                        examPaperResponse.setThumnailLink(file.getThumbnailLink());
                        listExamPaperResponses.add(examPaperResponse);
                    }
                }
            }

            return new ResponseObject<>(
                    listExamPaperResponses,
                    HttpStatus.OK,
                    "Lấy thành công danh sách đề thi"
            );
        } catch (Exception e) {
            return new ResponseObject<>(
                    null,
                    HttpStatus.BAD_REQUEST,
                    "Đã có 1 vài lỗi sảy ra"
            );
        }
    }

}
