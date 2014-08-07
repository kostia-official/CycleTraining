package com.kozzztya.cycletraining.utils;

import android.os.Environment;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.channels.FileChannel;

public class FileUtils {

    public static void copyFile(File from, File to) throws IOException {
        if (Environment.getExternalStorageDirectory().canWrite()) {
            FileChannel dst = new FileOutputStream(to).getChannel();
            FileChannel src = new FileInputStream(from).getChannel();
            dst.transferFrom(src, 0, src.size());
            src.close();
            dst.close();
        }
    }

    public static String[] getDirectoryFileNames(String directoryPath) {
        File f = new File(Environment.getExternalStorageDirectory(), directoryPath);
        return f.list();
    }
}
