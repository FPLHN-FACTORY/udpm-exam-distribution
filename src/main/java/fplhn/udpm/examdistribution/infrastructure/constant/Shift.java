package fplhn.udpm.examdistribution.infrastructure.constant;

import lombok.Getter;

import java.time.LocalTime;

@Getter
public enum Shift {
    CA1(LocalTime.of(0,0), LocalTime.of(2, 0)),
//    CA2(LocalTime.of(9, 30), LocalTime.of(11, 30)),
    CA3(LocalTime.of(12, 30), LocalTime.of(17, 0)),
//    CA4(LocalTime.of(14, 30), LocalTime.of(16, 30)),
//    CA5(LocalTime.of(16, 38), LocalTime.of(19, 0)),
    CA6(LocalTime.of(17, 30), LocalTime.of(23, 0));
//    CA7(LocalTime.of(22, 0), LocalTime.of(0, 0)),
//    CA8(LocalTime.of(0, 15), LocalTime.of(2, 30)),
//    CA9(LocalTime.of(3, 0), LocalTime.of(5, 0)),
//    CA10(LocalTime.of(5, 30), LocalTime.of(7, 30));

    private final LocalTime startTime;
    private final LocalTime endTime;

    Shift(LocalTime startTime, LocalTime endTime) {
        this.startTime = startTime;
        this.endTime = endTime;
    }

    public static Shift getCurrentShift() {
        LocalTime now = LocalTime.now();
        for (Shift shift : Shift.values()) {
            if ((now.isAfter(shift.getStartTime()) || now.equals(shift.getStartTime())) && now.isBefore(shift.getEndTime())) {
                return shift;
            }
        }
        return null;
    }
}
