package fplhn.udpm.examdistribution.infrastructure.conflig.googledrive.dto;

import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

import java.io.Serializable;

@Getter
@Setter
@EqualsAndHashCode(callSuper = false)
@ToString
public class GoogleDriveFileDTO implements Serializable {

    private String id;
    private String name;
    private String link;
    private String size;
    private String thumbnailLink;
    private boolean shared;

}