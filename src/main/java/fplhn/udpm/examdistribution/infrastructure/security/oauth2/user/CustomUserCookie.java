package fplhn.udpm.examdistribution.infrastructure.security.oauth2.user;

import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

import java.io.Serializable;

@Getter
@Setter
@Builder
public class CustomUserCookie implements Serializable {

    private String userRole;

    private String majorFacilityId;

    private String departmentFacilityId;

    private String departmentId;

    private String departmentName;

    private String facilityId;

    private String facilityName;

    private String userEmailFPT;

    private String userEmailFe;

    private String userFullName;

    private String userId;

    private String userPicture;

    private String isAssignUploader;

}
