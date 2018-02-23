package util;

import java.math.BigInteger;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;
import java.util.Date;

public class EncryptUtil {

    public static String generateSaltedHash(String password, String passwordSalt) {
        MessageDigest messageDigest = null;
        try {
            messageDigest = MessageDigest.getInstance("SHA-256");
            messageDigest.update((password + passwordSalt).getBytes());
        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
        }
        String passwordHash = (new BigInteger(messageDigest.digest())).toString(32);
        return passwordHash;
    }

    public static String nextSalt() {
        SecureRandom random = new SecureRandom();
        return new BigInteger(130, random).toString(32);
    }

    public static String generateSecret(long accountId) {
        MessageDigest messageDigest = null;
        try {
            Date date = new Date();
            long dateLong = date.getTime();
            String dateString = Long.toString(dateLong);
            String idString = Long.toString(accountId);
            messageDigest = MessageDigest.getInstance("SHA-256");
            messageDigest.update((dateString + idString).getBytes());
        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
        }
        String authSecret = (new BigInteger(messageDigest.digest())).toString(32);
        return authSecret;
    }

    public static String generatePasswordSecret(String email) {
        MessageDigest messageDigest = null;
        try {
            Date date = new Date();
            long dateLong = date.getTime();
            String dateString = Long.toString(dateLong);
            messageDigest = MessageDigest.getInstance("SHA-256");
            messageDigest.update((dateString + email).getBytes());
        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
        }
        String passwordSecret = (new BigInteger(messageDigest.digest())).toString(32);
        return passwordSecret;
    }

}
