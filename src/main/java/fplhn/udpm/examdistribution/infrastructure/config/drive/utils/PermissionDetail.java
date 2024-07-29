package fplhn.udpm.examdistribution.infrastructure.config.drive.utils;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class PermissionDetail {

    private String type;

    private String role;

    private String emailAddress;

}
