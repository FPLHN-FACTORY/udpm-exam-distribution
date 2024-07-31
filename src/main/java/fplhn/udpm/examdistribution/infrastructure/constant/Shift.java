package fplhn.udpm.examdistribution.infrastructure.constant;

import lombok.Getter;

import java.time.LocalTime;

@Getter
public enum Shift {
    CA1(LocalTime.of(7, 0), LocalTime.of(9, 0)),
    CA2(LocalTime.of(9, 1), LocalTime.of(11, 0)),
    CA3(LocalTime.of(11, 1), LocalTime.of(13, 0)),
    CA4(LocalTime.of(13, 1), LocalTime.of(15, 0)),
    CA5(LocalTime.of(15, 1), LocalTime.of(17, 0)),
    CA6(LocalTime.of(17, 1), LocalTime.of(19, 0)),
    CA7(LocalTime.of(19, 1), LocalTime.of(21, 0)),
    CA8(LocalTime.of(21, 1), LocalTime.of(23, 0)),
    CA9(LocalTime.of(23, 1), LocalTime.of(23, 59)),
    CA10(LocalTime.of(0, 1), LocalTime.of(6, 59));

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

    public static Shift fromString(String shift) {
        for (Shift s : Shift.values()) {
            if (s.name().equals(shift)) {
                return s;
            }
        }
        return null;
    }

    public static String getShift(Shift shift) {
        return shift.name();
    }

}
