package com.sorbonne.daar.utils.graph;

import java.io.BufferedOutputStream;
import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.zip.ZipEntry;
import java.util.zip.ZipInputStream;

public class Zip {
	
	/** https://stackoverflow.com/questions/36977304/extract-contents-of-zip-file-downloaded-using-a-http-get-request */

	private static final int BUFFER_SIZE = 4096;
	
	public static void unzip(byte[] data, String filePath) throws IOException {
        ZipInputStream zipIn = new ZipInputStream(new ByteArrayInputStream(data));
        ZipEntry entry = zipIn.getNextEntry();

        while (entry != null) {
            if (!entry.isDirectory()) {
                // if the entry is a file, extracts it
                extractFile(zipIn, filePath);
            } else {
                // if the entry is a directory, make the directory
                File dir = new File(filePath);
                dir.mkdir();
            }
            zipIn.closeEntry();
            entry = zipIn.getNextEntry();
        }
        zipIn.close();
    }
	
	private static void extractFile(ZipInputStream zipIn, String filePath) throws IOException {
        BufferedOutputStream bos = new BufferedOutputStream(new FileOutputStream(filePath));
        byte[] bytesIn = new byte[BUFFER_SIZE];
        int read = 0;
        while ((read = zipIn.read(bytesIn)) != -1) {
            bos.write(bytesIn, 0, read);
        }
        bos.close();
    }
}
