package fplhn.udpm.examdistribution.utils;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;
import java.util.Base64;
import java.util.UUID;

import org.apache.commons.lang3.RandomStringUtils;

public class PasswordUtils {

    private static final String ALGORITHM = "SHA-512";

    public static String getSecurePassword(String password, String salt) {
        String generatedPassword = null;
        try {
            MessageDigest md = MessageDigest.getInstance(ALGORITHM);
            md.update(salt.getBytes());
            byte[] bytes = md.digest(password.getBytes());
            generatedPassword = Base64.getEncoder().encodeToString(bytes);
        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
        }
        return generatedPassword;
    }

    public static String generateSalt() {
        SecureRandom sr;
        byte[] salt = new byte[16];
        try {
            sr = SecureRandom.getInstance("SHA1PRNG");
            sr.nextBytes(salt);
        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
        }
        return Base64.getEncoder().encodeToString(salt);
    }

    public static boolean verifyUserPassword(String providedPassword, String securedPassword, String salt) {
        // Generate New secure password with the same salt
        String newSecurePassword = getSecurePassword(providedPassword, salt);
        // Check if two passwords are equal
        return newSecurePassword.equals(securedPassword);
    }

    public static String generatePassword() {
        return UUID.randomUUID().toString().substring(0, 6).toUpperCase();
    }

//    public static void main(String[] args) {
//        String password = "123456";
//        String salt = generateSalt();
//        String securePassword = getSecurePassword(password, salt);
//
//        System.out.println("Secure Password: " + securePassword);
//        System.out.println("Salt: " + salt);
//
//        boolean passwordMatch = verifyUserPassword(password, securePassword, salt);
//        System.out.println("Password Match: " + passwordMatch);
//
//    }

}
