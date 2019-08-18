package com.oliver.sdk.util;

import android.text.TextUtils;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;


public class FileUtils {

    /**
     * 读取文件
     *
     * @param filePath 文件路径
     * @return
     */
    public static String readFile(String filePath) throws FileNotFoundException {
        if (TextUtils.isEmpty(filePath)) {
            return "";
        }
        return readFile(new File(filePath));
    }

    public static String readFile(File file) throws FileNotFoundException {
        if (file == null || !file.exists()) {
            return "";
        }
        BufferedReader reader = null;
        String line = null;
        StringBuilder sb = new StringBuilder();

        try {
            reader = new BufferedReader(new FileReader(file));
            while ((line = reader.readLine()) != null) {
                sb.append(line);
            }

            return sb.toString();
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            try {
                if (reader != null) {
                    reader.close();
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        return "";
    }

    public static String readFile(InputStream is) throws FileNotFoundException {
        if (is == null) {
            return "";
        }
        BufferedReader reader = null;
        String line = null;
        StringBuilder sb = new StringBuilder();

        try {
            reader = new BufferedReader(new InputStreamReader(is));
            while ((line = reader.readLine()) != null) {
                sb.append(line);
            }

            return sb.toString();
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            try {
                if (reader != null) {
                    reader.close();
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        return "";
    }

}
