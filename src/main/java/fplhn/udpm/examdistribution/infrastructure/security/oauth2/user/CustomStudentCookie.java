package fplhn.udpm.examdistribution.infrastructure.security.oauth2.user;

import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

import java.io.Serializable;

@Getter
@Setter
@Builder
public class CustomStudentCookie implements Serializable {

    private String userRole;

    private String userEmail;

    private String userFullName;

    private String userId;

    private String userPicture;

}
