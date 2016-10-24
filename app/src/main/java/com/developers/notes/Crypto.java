package com.developers.notes;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.security.AlgorithmParameters;
import java.security.spec.KeySpec;

import javax.crypto.Cipher;
import javax.crypto.CipherInputStream;
import javax.crypto.CipherOutputStream;
import javax.crypto.SecretKey;
import javax.crypto.SecretKeyFactory;
import javax.crypto.spec.IvParameterSpec;
import javax.crypto.spec.PBEKeySpec;
import javax.crypto.spec.SecretKeySpec;

/**
 * Created by avispa on 21.10.2016.
 */
public class Crypto {

    public static void encryptFile(File input, String password) throws Exception {
        byte[] salt = "strong salt".getBytes();
        KeySpec spec = new PBEKeySpec(password.toCharArray(), salt, 65536, 128); // AES-128
        SecretKeyFactory f = SecretKeyFactory.getInstance("PBKDF2WithHmacSHA1");
        SecretKey key = new SecretKeySpec(f.generateSecret(spec).getEncoded(), "AES");
        Cipher cipher = Cipher.getInstance("AES/CBC/PKCS5Padding");
        cipher.init(Cipher.ENCRYPT_MODE, key);
        AlgorithmParameters params = cipher.getParameters();
        byte[] iv = params.getParameterSpec(IvParameterSpec.class).getIV();
        String fileName = input.getAbsolutePath();
//        String fileName = input.getParent() + "\\" + "enc_" + input.getName();
        File output = new File(fileName);
//        if (!output.exists()) output.createNewFile();
//        String fileName = "D:\\test\\enc_img.jpg";
//        String fileName = "D:\\test\\encrypted.txt";
        FileInputStream fis = new FileInputStream(input);
        byte[] buffer = new byte[fis.available()];
        fis.read(buffer);
        input.delete();
        CipherOutputStream cos = new CipherOutputStream(new FileOutputStream(output), cipher);
        cos.write(iv);
        cos.write(buffer);
        cos.close();
        fis.close();
    }

//    public static String encryptString(byte[] text, String password) throws Exception {
//
//    }

    public static File decryptFile(File input, String password) throws Exception {
        byte[] salt = "strong salt".getBytes();
        byte[] iv = new byte[16];
        new FileInputStream(input).read(iv);
        KeySpec spec = new PBEKeySpec(password.toCharArray(), salt, 65536, 128); // AES-128
        SecretKeyFactory f = SecretKeyFactory.getInstance("PBKDF2WithHmacSHA1");
        SecretKey key = new SecretKeySpec(f.generateSecret(spec).getEncoded(), "AES");
        Cipher cipher = Cipher.getInstance("AES/CBC/PKCS5Padding");
        cipher.init(Cipher.DECRYPT_MODE, key, new IvParameterSpec(iv));
        String fileName = input.getAbsolutePath();
        File output = null;
        output = File.createTempFile("dec" + fileName, EditNoteActivity.FILE_EXTENSION, null);
        output.deleteOnExit();
        FileInputStream fis = new FileInputStream(input);
        fis.skip(iv.length);
        byte[] buffer = new byte[fis.available()];
        fis.read(buffer);
        CipherOutputStream cos = new CipherOutputStream(new FileOutputStream(output), cipher);
        cos.write(buffer);
        cos.close();
        fis.close();
        return output;
    }

    public static String decryptToString(File input, String password) throws Exception {
        byte[] salt = "strong salt".getBytes();
        byte[] iv = new byte[16];
        FileInputStream fis = new FileInputStream(input);
        fis.read(iv);
        KeySpec spec = new PBEKeySpec(password.toCharArray(), salt, 65536, 128); // AES-128
        SecretKeyFactory f = SecretKeyFactory.getInstance("PBKDF2WithHmacSHA1");
        SecretKey key = new SecretKeySpec(f.generateSecret(spec).getEncoded(), "AES");
        Cipher cipher = Cipher.getInstance("AES/CBC/PKCS5Padding");
        cipher.init(Cipher.DECRYPT_MODE, key, new IvParameterSpec(iv));
        byte[] message = new byte[fis.available()];
        CipherInputStream cis = new CipherInputStream(fis, cipher);
        cis.read(message);
        cis.close();
        fis.close();
        return new String(message, "UTF-8");
    }
}
