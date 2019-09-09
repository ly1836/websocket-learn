package com.example.websocketlearn.comm.util;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.zip.GZIPInputStream;

public class ZipUtil {
	/**
	 * 解压zip并且转成string
	 * @param inputStream
	 * @return
	 */
	public static String unZip2String(InputStream inputStream){
		try {
			GZIPInputStream gzipIn = new GZIPInputStream(inputStream);
			if (gzipIn != null) {
                StringBuilder sb = new StringBuilder();
                String line;
                try {
                    BufferedReader reader = new BufferedReader(new InputStreamReader(gzipIn, "utf-8"));
                    while ((line = reader.readLine()) != null) {
                        sb.append(line);
                    }
                } finally {
                	gzipIn.close();
                }
                return sb.toString();
            }
		} catch (IOException e) {
			e.printStackTrace();
		}
		return null;
	 }
}
