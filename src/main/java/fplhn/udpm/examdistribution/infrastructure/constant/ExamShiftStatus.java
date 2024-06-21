package fplhn.udpm.examdistribution.infrastructure.constant;

import lombok.Getter;

@Getter
public enum ExamShiftStatus {

    NOT_STARTED("Chưa bắt đầu"),
    IN_PROGRESS("Đang diễn ra"),
    FINISHED("Đã kết thúc");

    private final String value;

    ExamShiftStatus(String value) {
        this.value = value;
    }

}
