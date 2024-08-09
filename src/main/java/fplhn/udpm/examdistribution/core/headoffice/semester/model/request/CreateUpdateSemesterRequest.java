package fplhn.udpm.examdistribution.core.headoffice.semester.model.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.Setter;

import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneId;

@Getter
@Setter
public class CreateUpdateSemesterRequest {

    private String semesterId;

    @NotBlank(message = "Tên học kỳ không được để trống")
    private String semesterName;

    @NotNull(message = "Thời gian không được để trống")
    private Long startTime;

    @NotNull(message = "Thời gian không được để trống")
    private Long endTime;

    @NotNull(message = "Thời gian kết thúc block 1 không được để trống")
    private Long endTimeBlock1;

    public Long getStartTimeCustom() {
        LocalDateTime startDateSemester = Instant.ofEpochMilli(this.startTime)
                .atZone(ZoneId.systemDefault())
                .toLocalDateTime();
        //set giờ phút giây
        startDateSemester = startDateSemester.withHour(0)
                .withMinute(0)
                .withSecond(0)
                .withNano(0);
        return startDateSemester.atZone(ZoneId.systemDefault())
                .toInstant()
                .toEpochMilli();
    }

    public Long getEndTimeCustom() {
        LocalDateTime endDateSemester = Instant.ofEpochMilli(this.endTime)
                .atZone(ZoneId.systemDefault())
                .toLocalDateTime();
        //set giờ phút giây
        endDateSemester = endDateSemester.withHour(23)
                .withMinute(59)
                .withSecond(59)
                .withNano(0);
        return endDateSemester.atZone(ZoneId.systemDefault())
                .toInstant()
                .toEpochMilli();
    }

    public Long getEndTimeBlock1Custom() {
        LocalDateTime endDateBlock1 = Instant.ofEpochMilli(endTimeBlock1)
                .atZone(ZoneId.systemDefault())
                .toLocalDateTime();
        endDateBlock1 = endDateBlock1.withHour(23)
                .withMinute(59)
                .withSecond(59)
                .withNano(0);
        return endDateBlock1.atZone(ZoneId.systemDefault())
                .toInstant()
                .toEpochMilli();
    }


}
