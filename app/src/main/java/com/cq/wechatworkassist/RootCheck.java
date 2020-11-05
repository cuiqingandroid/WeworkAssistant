package com.cq.wechatworkassist;

import android.util.Log;

import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.File;
import java.io.IOException;
import java.io.InputStreamReader;

/**
 * 判断Android设备是否拥有Root权限
 */
public class RootCheck {

    private final static String TAG = "RootUtil";

    public static boolean isRoot() {
        String binPath = "/system/bin/su";
        String xBinPath = "/system/xbin/su";
        if (new File(binPath).exists() && isExecutable(binPath))
            return true;
        if (new File(xBinPath).exists() && isExecutable(xBinPath))
            return true;
        return false;
    }

    private static boolean isExecutable(String filePath) {
        Process p = null;
        try {
            p = Runtime.getRuntime().exec("ls -l " + filePath);
            // 获取返回内容
            BufferedReader in = new BufferedReader(new InputStreamReader(p.getInputStream()));
            String str = in.readLine();
            Log.i(TAG, str);
            if (str.length() >= 4) {
                char flag = str.charAt(3);
                if (flag == 's' || flag == 'x')
                    return true;
            }
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            if (p != null) {
                p.destroy();
            }
        }
        return false;
    }


    public static boolean exeCmdWithRoot(String cmdStr) {
        if (null == cmdStr || "".equals(cmdStr)) {
            Log.d(TAG, "bad cmdStr");
            return false;
        }
        Process process = null;
        String[] cmds = new String[]{cmdStr};
        try {
            process = Runtime.getRuntime().exec("su");

            DataOutputStream os = new DataOutputStream(process.getOutputStream());
            for (String cmd : cmds) {
                os.write(new String(cmd + "\n").getBytes());
            }
            os.flush();
            os.close();
        } catch (Exception e) {
            String message = "executeCmd: " + cmdStr + " error: " + e.toString();
            Log.d(TAG, "errorMessage:" + message);
        }

        if (process != null) {
            Log.d(TAG, "process wait execed");
            try {
                int status = process.waitFor();
                process.getOutputStream().close();
                process.getErrorStream().close();
                process.getInputStream().close();
                Log.d(TAG, "status:" + status);
                //这里是关键代码，其实只有status为1的时候是没有权限，这里个人直接把所有运行shell命令的异常都归为失败
                if (0 == status) {
                    return true;
                } else {
                    return false;
                }
            } catch (InterruptedException e) {
                Log.d(TAG, "InterruptedException");
                e.printStackTrace();
            } catch (IOException e) {
                Log.d(TAG, "IOException");
                e.printStackTrace();
            }
        }
        return false;
    }

    /**
     * 是否有root可执行权限
     */
    public static boolean hasRootPermission() {
        Process process = null;
        DataOutputStream os = null;
        try {
            process = Runtime.getRuntime().exec("su");
            os = new DataOutputStream(process.getOutputStream());
            os.writeBytes("exit\n");
            os.flush();
            int exitValue = process.waitFor();
            if (exitValue == 0) {
                return true;
            } else {
                return false;
            }
        } catch (Exception e) {
            Log.d(TAG, "Unexpected error - Here is what I know: "
                    + e.getMessage());
            return false;
        } finally {
            try {
                if (os != null) {
                    os.close();
                }
                process.destroy();
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }
}