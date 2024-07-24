package fplhn.udpm.examdistribution.core.headsubject.updateexampaper.model.request;

import lombok.Getter;
import lombok.Setter;
import org.springframework.web.multipart.MultipartFile;

@Getter
@Setter
public class UEPEditFileRequest {

    private MultipartFile file;

    private String examPaperId;

}
