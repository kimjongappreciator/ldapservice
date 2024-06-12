package com.unmsm.ldapservice.helper;

import java.io.UnsupportedEncodingException;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import jakarta.xml.bind.DatatypeConverter;
import java.util.Base64;

public class Claves {
    public static String ldapShaPassword(String sPassword) throws NoSuchAlgorithmException {
        MessageDigest md = MessageDigest.getInstance("SHA");
        md.update(sPassword.getBytes());
        byte[] raw = md.digest();
        Base64.Encoder base64 = Base64.getEncoder();
        String result = "{SHA}" + base64.encode(raw);
        return result;
    }

    public static String ldapMd5Password(String sPassword) throws NoSuchAlgorithmException, UnsupportedEncodingException {
        MessageDigest md = MessageDigest.getInstance("MD5");
        md.update(sPassword.getBytes("UTF-8"));
        byte[] raw = md.digest();
        Base64.Encoder base64 = Base64.getEncoder();
        String result = "{MD5}" + base64.encode(raw);
        return result;
    }

    public static String googleShaPassword(String sPassword) throws NoSuchAlgorithmException, UnsupportedEncodingException {
        MessageDigest md = MessageDigest.getInstance("SHA-1");
        md.update(sPassword.getBytes());
        byte[] raw = md.digest();
        Base64.Encoder base64 = Base64.getEncoder();
        String result = "{SHA}" + base64.encode(raw);
        return result;
    }

    public static String googleMd5Password(String sPassword) throws NoSuchAlgorithmException, UnsupportedEncodingException {
        MessageDigest md = MessageDigest.getInstance("MD5");
        byte[] raw = md.digest(sPassword.getBytes("UTF-8"));
        String result = DatatypeConverter.printHexBinary(raw);
        return result;
    }
}
