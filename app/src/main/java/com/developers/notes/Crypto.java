package com.developers.notes;

import android.os.AsyncTask;
import android.util.Log;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.security.AlgorithmParameters;
import java.security.spec.KeySpec;
import java.text.SimpleDateFormat;
import java.util.Date;

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
public class Crypto extends AsyncTask<String, Void, String> {

    private static String salt = "hg3ag89hqh4og98h83hgh82";
    private static String validation = "#validation#";
    @Override
    protected String doInBackground(String... params) {
        String task = params[0];
        String fileName = params[1];
        String password = params[2];
        String text = params[3];
        String result = null;
        Log.d("crypto input", fileName);
        File myNote = new File(fileName);
        try {
            if (task.equals("encrypt")) {
                write(myNote, password, text);
                Log.d("crypto", "writing decrypted");
            }
            if (task.equals("decrypt")) {
                //TODO check password
                result = read(myNote, password);
                Log.d("crypto", "reading encrypted");
            }
        } catch (Throwable t) {
            Log.d("Exception crypto", t.toString() );
        }
        return result;
    }

    public static String read(File input, String password) throws Exception {
        byte[] saltBytes = salt.getBytes("UTF-8");
        byte[] iv = new byte[16];
        byte[] validMark = validation.getBytes("UTF-8");
        FileInputStream fis = new FileInputStream(input);
        fis.read(iv);
//        fis.read(validMark);
        KeySpec spec = new PBEKeySpec(password.toCharArray(), saltBytes, 65536, 128); // AES-128
        SecretKeyFactory f = SecretKeyFactory.getInstance("PBKDF2WithHmacSHA1");
        SecretKey key = new SecretKeySpec(f.generateSecret(spec).getEncoded(), "AES");
        Cipher cipher = Cipher.getInstance("AES/CBC/PKCS5Padding");
        cipher.init(Cipher.DECRYPT_MODE, key, new IvParameterSpec(iv));
//        validMark = cipher.doFinal(validMark);
//        String checkValid = new String(validMark, "UTF-8");
//        if (!checkValid.equals(validation)) {
//            Log.d("pass check", "invalid password");
//            return "##err:wrong_password";
//        }
        byte[] message = new byte[fis.available()];
        fis.read(message);
        message = cipher.doFinal(message);
        fis.close();
        String text = new String(message, "UTF-8");
        return text;
    }

    public static void write(File output, String password, String text) throws Exception {
        byte[] saltBytes = salt.getBytes("UTF-8");
        byte[] validMark = validation.getBytes("UTF-8");
        KeySpec spec = new PBEKeySpec(password.toCharArray(), saltBytes, 65536, 128); // AES-128
        SecretKeyFactory f = SecretKeyFactory.getInstance("PBKDF2WithHmacSHA1");
        SecretKey key = new SecretKeySpec(f.generateSecret(spec).getEncoded(), "AES");
        Cipher cipher = Cipher.getInstance("AES/CBC/PKCS5Padding");
        cipher.init(Cipher.ENCRYPT_MODE, key);
        AlgorithmParameters params = cipher.getParameters();
        byte[] iv = params.getParameterSpec(IvParameterSpec.class).getIV();
        FileOutputStream fos = new FileOutputStream(output);
        fos.write(iv);
        byte[] message = text.getBytes("UTF-8");
//        fos.write(cipher.doFinal(validMark));
        fos.write(cipher.doFinal(message));
    }
//    public static void encryptFile(File input, String password) throws Exception {
//        byte[] salt = "strong salt".getBytes();
//        KeySpec spec = new PBEKeySpec(password.toCharArray(), salt, 65536, 128); // AES-128
//        SecretKeyFactory f = SecretKeyFactory.getInstance("PBKDF2WithHmacSHA1");
//        SecretKey key = new SecretKeySpec(f.generateSecret(spec).getEncoded(), "AES");
//        Cipher cipher = Cipher.getInstance("AES/CBC/PKCS5Padding");
//        cipher.init(Cipher.ENCRYPT_MODE, key);
//        AlgorithmParameters params = cipher.getParameters();
//        byte[] iv = params.getParameterSpec(IvParameterSpec.class).getIV();
//        String fileName = input.getAbsolutePath();
////        String fileName = input.getParent() + "\\" + "enc_" + input.getName();
////        if (!output.exists()) output.createNewFile();
////        String fileName = "D:\\test\\enc_img.jpg";
////        String fileName = "D:\\test\\encrypted.txt";
//        FileInputStream fis = new FileInputStream(input);
//        byte[] buffer = new byte[fis.available()];
//        fis.read(buffer);
//        input.delete();
//        File output = new File(fileName);
//        if (!output.exists()) output.createNewFile();
//        CipherOutputStream cos = new CipherOutputStream(new FileOutputStream(output), cipher);
//        cos.write(iv);
//        cos.write(buffer);
//        cos.close();
//        fis.close();
//    }

//    public static void writeDecrypted(File output, String content, String password) throws Exception{
//        if (!output.exists()) output.createNewFile();
//        byte[] salt = "strong salt".getBytes();
//        KeySpec spec = new PBEKeySpec(password.toCharArray(), salt, 65536, 128); // AES-128
//        SecretKeyFactory f = SecretKeyFactory.getInstance("PBKDF2WithHmacSHA1");
//        SecretKey key = new SecretKeySpec(f.generateSecret(spec).getEncoded(), "AES");
//        Cipher cipher = Cipher.getInstance("AES/CBC/PKCS5Padding");
//        cipher.init(Cipher.ENCRYPT_MODE, key);
//        AlgorithmParameters params = cipher.getParameters();
//        byte[] iv = params.getParameterSpec(IvParameterSpec.class).getIV();
//        FileOutputStream fos = new FileOutputStream(output);
//        fos.write(cipher.doFinal(iv));
//        fos.write(cipher.doFinal(content.getBytes("UTF-8")));
//        fos.close();
//    }

//    public static String encryptString(byte[] text, String password) throws Exception {
//
//    }

//    public static File decryptFile(File input, String password) throws Exception {
//        byte[] salt = "strong salt".getBytes();
//        byte[] iv = new byte[16];
//        new FileInputStream(input).read(iv);
//        KeySpec spec = new PBEKeySpec(password.toCharArray(), salt, 65536, 128); // AES-128
//        SecretKeyFactory f = SecretKeyFactory.getInstance("PBKDF2WithHmacSHA1");
//        SecretKey key = new SecretKeySpec(f.generateSecret(spec).getEncoded(), "AES");
//        Cipher cipher = Cipher.getInstance("AES/CBC/PKCS5Padding");
//        cipher.init(Cipher.DECRYPT_MODE, key, new IvParameterSpec(iv));
//        String fileName = input.getAbsolutePath();
//        File output = null;
//        output = File.createTempFile("dec" + fileName, EditNoteActivity.FILE_EXTENSION, null);
//        output.deleteO
//        FileInputStream fis = new FileInputStream(input);
//        fis.skip(iv.length);
//        byte[] buffer = new byte[fis.available()];
//        fis.read(buffer);
//        CipherOutputStream cos = new CipherOutputStream(new FileOutputStream(output), cipher);
//        cos.write(buffer);
//        cos.close();
//        fis.close();
//        return output;
//    }
//    public static File decryptFile(File input, String password) throws Exception {
//        byte[] salt = "strong salt".getBytes();
//        byte[] iv = new byte[16];
//        new FileInputStream(input).read(iv);
//        KeySpec spec = new PBEKeySpec(password.toCharArray(), salt, 65536, 128); // AES-128
//        SecretKeyFactory f = SecretKeyFactory.getInstance("PBKDF2WithHmacSHA1");
//        SecretKey key = new SecretKeySpec(f.generateSecret(spec).getEncoded(), "AES");
//        Cipher cipher = Cipher.getInstance("AES/CBC/PKCS5Padding");
//        cipher.init(Cipher.DECRYPT_MODE, key, new IvParameterSpec(iv));
//        String fileName = input.getAbsolutePath();
//        Log.d("filename", fileName);
//        File output = new File("temp.note");
//        output.createNewFile();
//        String tempName = new SimpleDateFormat("yyyy_MM_dd_HH_mm_ss").format(new Date());
//        output.createTempFile("dec", ".note");
//        File output = new File("dec_" + fileName + "." + CreateNoteActivity.FILE_EXTENSION);
//        boolean b = false;
//        if (!output.exists()) b = output.createNewFile();
//        Log.d("create dec_" , output.getAbsolutePath() + " ");
//        FileInputStream fis = new FileInputStream(input);
//        fis.skip(iv.length);
//        byte[] buffer = new byte[fis.available()];
//        fis.read(buffer);
////        input.delete();
//        CipherOutputStream cos = new CipherOutputStream(new FileOutputStream(output), cipher);
//        cos.write(buffer);
//        cos.close();
//        fis.close();
//        return output;
//    }
//
//    public static String readEncrypted(File input, String password) throws Exception {
//        byte[] salt = "strong salt".getBytes();
//        byte[] iv = new byte[16];
//        FileInputStream fis = new FileInputStream(input);
//        fis.read(iv);
//        KeySpec spec = new PBEKeySpec(password.toCharArray(), salt, 65536, 128); // AES-128
//        SecretKeyFactory f = SecretKeyFactory.getInstance("PBKDF2WithHmacSHA1");
//        SecretKey key = new SecretKeySpec(f.generateSecret(spec).getEncoded(), "AES");
//        Cipher cipher = Cipher.getInstance("AES/CBC/PKCS5Padding");
//        cipher.init(Cipher.DECRYPT_MODE, key, new IvParameterSpec(iv));
//        byte[] message = new byte[fis.available()];
//        Log.d("message len", message.length + "  ");
//        fis.read(message);
//        message = cipher.doFinal(message);
//        fis.close();
//        return new String(message, "UTF-8");
//    }
}
