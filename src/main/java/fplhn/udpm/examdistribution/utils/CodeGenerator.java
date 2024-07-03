package fplhn.udpm.examdistribution.utils;


import java.util.UUID;

public class CodeGenerator {

    public static String generateRandomCode() {
        return UUID.randomUUID().toString().substring(0, 6).toUpperCase();
    }

}
