package fplhn.udpm.examdistribution.infrastructure.conflig.googledrive.dto;

import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@EqualsAndHashCode(callSuper = false)
public class GoogleDriveFolderDTO {

    private String id;
    private String name;
    private String link;

}
