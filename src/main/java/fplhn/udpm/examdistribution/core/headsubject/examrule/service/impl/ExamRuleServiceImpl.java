package fplhn.udpm.examdistribution.core.headsubject.examrule.service.impl;

import fplhn.udpm.examdistribution.core.common.base.PageableObject;
import fplhn.udpm.examdistribution.core.common.base.ResponseObject;
import fplhn.udpm.examdistribution.core.headsubject.examrule.model.request.FindSubjectRequest;
import fplhn.udpm.examdistribution.core.headsubject.examrule.model.request.GetFileRequest;
import fplhn.udpm.examdistribution.core.headsubject.examrule.model.request.UploadExamRuleRequest;
import fplhn.udpm.examdistribution.core.headsubject.examrule.repository.ERSubjectExtendRepository;
import fplhn.udpm.examdistribution.core.headsubject.examrule.service.ExamRuleService;
import fplhn.udpm.examdistribution.entity.Subject;
import fplhn.udpm.examdistribution.infrastructure.conflig.googledrive.service.GoogleDriveFileService;
import fplhn.udpm.examdistribution.utils.Helper;
import lombok.RequiredArgsConstructor;
import org.springframework.core.io.Resource;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class ExamRuleServiceImpl implements ExamRuleService {

    private final ERSubjectExtendRepository subjectRepository;

    private final GoogleDriveFileService googleDriveFileService;

    @Override
    public ResponseObject<?> getAllSubject(String departmentFacilityId, FindSubjectRequest request) {
        Pageable pageable = Helper.createPageable(request, "createdDate");
        return new ResponseObject<>(
                PageableObject.of(subjectRepository.getAllSubject(pageable, departmentFacilityId, request)),
                HttpStatus.OK,
                "Lấy thành công danh sách môn học"
        );
    }

    @Override
    public ResponseObject<?> uploadExamRule(String subjectId, UploadExamRuleRequest request) {
        try {
            if (request.getFile().isEmpty()) {
                return new ResponseObject<>(
                        null,
                        HttpStatus.NOT_ACCEPTABLE,
                        "Nội quy thi chưa được tải"
                );
            }

            Subject putSubject = subjectRepository.getReferenceById(subjectId);

            if(!putSubject.getPathExamRule().isEmpty()){
                googleDriveFileService.deleteById(putSubject.getPathExamRule());
            }

            String fileId = googleDriveFileService.upload(request.getFile(), request.getFolderName(), true);

            putSubject.setPathExamRule(fileId);
            subjectRepository.save(putSubject);

            return new ResponseObject<>(
                    null,
                    HttpStatus.OK,
                    "Tải nội quy thi thành công"
            );
        } catch (Exception e) {
            return new ResponseObject<>(
                    null,
                    HttpStatus.BAD_REQUEST,
                    "Có lỗi trong quá trình xử lý"
            );
        }
    }

    @Override
    public ResponseObject<?> getFile(GetFileRequest request) throws IOException {
        Optional<Subject> isSubjectExist = subjectRepository.findById(request.getSubjectId());
        if (isSubjectExist.isEmpty()) {
            return new ResponseObject<>(
                    null,
                    HttpStatus.NOT_FOUND,
                    "Không tìm thấy môn học này!"
            );
        }

        Subject subject = isSubjectExist.get();
        if (subject.getPathExamRule().isEmpty()) {
            return new ResponseObject<>(
                    null,
                    HttpStatus.NOT_FOUND,
                    "Môn học này chưa được tải quy định đề thi"
            );
        }

        return new ResponseObject<>(
                googleDriveFileService.loadFile(request.getFileId()),
                HttpStatus.OK,
                "Tìm thấy file thành công"
        );
    }

};
