package fplhn.udpm.examdistribution.infrastructure.security.oauth2.response;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
public class BlockAndSemesterIdResponse {

    private String blockId;

    private String semesterId;

}
