package fplhn.udpm.examdistribution.infrastructure.constant;

import lombok.Getter;

@Getter
public enum CookieConstant {

    EXAM_DISTRIBUTION_INFORMATION("e_d_i");

    private final String name;

    CookieConstant(String name) {
        this.name = name;
    }

}
