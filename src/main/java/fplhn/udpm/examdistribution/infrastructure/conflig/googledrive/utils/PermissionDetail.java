package fplhn.udpm.examdistribution.infrastructure.conflig.googledrive.utils;

import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Builder
public class PermissionDetail {

    private String type;
    private String role;
    private String emailAddress;

}
