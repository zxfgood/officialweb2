package com.feeye.util;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

/**
 * Created by hasee on 2019/1/10.
 */
public class ProcessUtil implements Runnable {

    //传入进程名称processName
    public static boolean findAddKillProcess(String processName) {
        BufferedReader bufferedReader = null;
        try {
            Process proc = Runtime.getRuntime().exec("c:\\windows\\system32\\tasklist -fi " + '"' + "imagename eq " + processName + '"');
            bufferedReader = new BufferedReader(new InputStreamReader(proc.getInputStream()));
            String line = null;
            while ((line = bufferedReader.readLine()) != null) {
                if (line.contains(processName)) {
                    killProcess(processName);
                    return true;
                }
            }
            return false;
        } catch (Exception ex) {
            ex.printStackTrace();
            return false;
        } finally {
            if (bufferedReader != null) {
                try {
                    bufferedReader.close();
                } catch (Exception ex) {
                }
            }
        }
    }

    //通过进程名称杀死进程
    public static void killProcess(String processName) {
        try {
            if (processName != null) {
                Process pro = Runtime.getRuntime().exec("c:\\windows\\system32\\taskkill /F /im " + processName);
                BufferedReader brStd = new BufferedReader(new InputStreamReader(pro.getInputStream()));
                BufferedReader brErr = new BufferedReader(new InputStreamReader(pro.getErrorStream()));
                long time = System.currentTimeMillis();
                while (true) {
                    if (brStd.ready()) {
                        break;
                    }
                    if (brErr.ready()) {
                        break;
                    }
                    if (System.currentTimeMillis() - time > 3000) {
                        return;
                    }
                }
            }
        } catch (IOException e1) {
            e1.printStackTrace();
        }
    }

    public static void main(String[] argus) {
        /**
         *每一个小时清理一次webdriver
         */
        ScheduledExecutorService service = Executors.newScheduledThreadPool(10);
        long initialDelay = 5;
        long period = 60*60;
        // 从现在开始5秒钟之后，每隔1个小时执行一次清理任务
        service.scheduleAtFixedRate(new ProcessUtil(), initialDelay, period, TimeUnit.SECONDS);


    }

    public void run() {
        System.out.println("开始清理webdriver进程");
        findAddKillProcess("phantomjs.exe");
//        System.out.print("开始清理chrome.exe进程");
//        findAddKillProcess("chrome.exe");
        System.out.println("清理完成");
    }
}
