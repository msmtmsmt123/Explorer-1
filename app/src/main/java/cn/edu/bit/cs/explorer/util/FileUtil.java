package cn.edu.bit.cs.explorer.util;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.util.Log;
import android.widget.Toast;

import com.gc.materialdesign.views.ProgressBarIndeterminate;

import java.io.File;
import java.io.IOException;

/**
 * Created by entalent on 2015/11/20.
 */
public class FileUtil {
    static {
        System.loadLibrary("explorerNative");
    }

    public static boolean isChildDirectoty(File parentDirectory, File childDirectory) {
        return childDirectory.getAbsolutePath().startsWith(parentDirectory.getAbsolutePath());
    }

    public static void openFile(Context context, File file) {
        Intent intent = new Intent();
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        intent.setAction(android.content.Intent.ACTION_VIEW);
        String type = TextUtil.getMimeTypeFromFile(file);
        intent.setDataAndType(Uri.fromFile(file), type);
        try {
            context.startActivity(intent);
        } catch (Exception e){
            Toast.makeText(context, "unable to open file", Toast.LENGTH_SHORT).show();
        }
    }

    public static int cutFile(File srcFile, File dstPath, IFileUtil handler)
            throws IllegalArgumentException{

        String srcFileName = srcFile.getName();
        String dstFileName = dstPath + File.separator + srcFileName;
        File dstFile = new File(dstFileName);

        if(srcFile.getParentFile().equals(dstPath)){
            throw new IllegalArgumentException("source directory equals with destination directory");
        }

        if(srcFile.isDirectory()) {
            if(!dstFile.mkdirs()){
                //The directory exists, and not override
                if(!(handler != null && handler.onDestiationFileExist(srcFile, dstFile))) {
                    return 0;
                }
            }
            File[] srcFiles = srcFile.listFiles();
            int successCnt = 0;
            for(File i : srcFiles) {
                successCnt += cutFile(i, dstFile, handler);
            }
            srcFile.delete();
            return successCnt;
        } else {
            //cut a single file

            if(dstFile.exists()) {
                if(handler != null && handler.onDestiationFileExist(srcFile, dstFile)) {
                    //force overricde
                    dstFile.delete();
                } else {
                    //cancel copy process
                    return 0;
                }
            }

            //source file and destination file might not be on the same storage device!!!
            if(srcFile.renameTo(dstFile)){
                return 1;
            } else if(copyThenDelete(srcFile, dstFile)){
                return 1;
            } else
                return 0;
        }
    }

    public static int copyFile(File srcFile, File dstPath, IFileUtil handler)
        throws IllegalArgumentException{

        String srcFileName = srcFile.getName();
        String dstFileName = dstPath + File.separator + srcFileName;
        File dstFile = new File(dstFileName);

        if(srcFile.getParentFile().equals(dstPath)){
            throw new IllegalArgumentException("source directory equals with destination directory");
        }

        if(srcFile.isDirectory()) {
            //copy a directory recursively
            if(!dstFile.mkdirs()){
                //The directory exists, and not override
                if(!(handler != null && handler.onDestiationFileExist(srcFile, dstFile))) {
                    return 0;
                }
            }
            File[] srcFiles = srcFile.listFiles();
            int successCnt = 0;
            for(File i : srcFiles) {
                successCnt += copyFile(i, dstFile, handler);
            }
            return successCnt;
        } else {
            //copy a single file

            if(dstFile.exists()) {
                if(handler != null && handler.onDestiationFileExist(srcFile, dstFile)) {
                    //force overricde
                    dstFile.delete(); //TODO: is delete necessary?
                } else {
                    //cancel copy process
                    return 0;
                }
            }
            if (copyFile(srcFile.getAbsolutePath(), dstFileName) != 1){
                return 0;
            } else {
                return 1;
            }
        }
    }

    public static int deleteFile (File file) {
        if(file.isDirectory()){
            int cnt = 0;
            File[] files = file.listFiles();
            if(files != null) {
                for(File i : files){
                    cnt += deleteFile(i);
                }
            }
            if(file.delete()) {
                cnt++;
            }
            return cnt;
        } else {
            if(file.delete()) {
                return 1;
            } else {
                return 0;
            }
        }
    }

    public static native int copyFile(String srcFile, String dstFile);

    private static boolean copyThenDelete(File srcFile, File dstFile) {
        if(1 != copyFile(srcFile.getAbsolutePath(), dstFile.getAbsolutePath())) {
            return false;
        }
        return srcFile.delete();
    }

    public interface IFileUtil {
        boolean onDestiationFileExist(File srcFile, File dstFile);
    }
}
