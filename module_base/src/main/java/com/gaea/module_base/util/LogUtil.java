package com.gaea.module_base.util;

import android.os.Build;
import android.util.Log;

import com.gaea.module_base.BuildConfig;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.io.StringWriter;


/**
 * log工具类
 */
public class LogUtil {
    
    /** Log 总开关 */
    // log打开
    private static boolean LOG_ON = BuildConfig.DEBUG;
    
    /** 用于跟踪时格式化输出,只有 android.util.Log.DEBUG 级别的日志才会输出此信息 */
    private static final String CLASS_METHOD_LINE_FORMAT = "%s.%s() %s:%d : %s";
    
    private static final String EMPTY = "";
    
    private LogUtil() {
        LOG_ON = BuildConfig.DEBUG;
    }
    
    /**
     * 输出 DEBUG 级别日志
     */
    public static void debug() {
        buildLog(Log.DEBUG, EMPTY, null);
    }
    
    /**
     * 输出 DEBUG 级别日志
     */
    public static void debug(String logMessage) {
        buildLog(Log.DEBUG, logMessage, null);
    }
    
    /**
     * 输出 ERROR 级别日志
     */
    public static void error(Throwable throwable) {
        sendLog(throwable.getMessage(), throwable);
        buildLog(Log.ERROR, throwable.getMessage(), throwable);
    }
    
    /**
     * 输出 ERROR 级别日志
     */
    public static void error(String logMessage, Throwable throwable) {
        sendLog(logMessage, throwable);
        buildLog(Log.ERROR, logMessage, throwable);
    }
    
    /**
     * 输出 INOF 级别日志
     */
    public static void info(String logMessage) {
        buildLog(Log.INFO, logMessage, null);
    }
    
    /**
     * 输出 VERBOSE 级别日志
     */
    public static void verbose(String logMessage) {
        buildLog(Log.VERBOSE, logMessage, null);
    }
    
    /**
     * 输出 WARN 级别日志
     */
    public static void warn(String logMessage) {
        buildLog(Log.WARN, logMessage, null);
    }
    
    /**
     * 控制台输出日志
     */
    private static void buildLog(int logLevel,
                                 String logMessage,
                                 Throwable throwable) {
        if (!LOG_ON){
            return;
        }

        // 获取出错信息堆栈元素，用于获取异常信息
        StackTraceElement stackTraceElement = getStackTraceElement(5);
        String tag = stackTraceElement.getFileName();
        
        switch (logLevel) {
            case Log.DEBUG:
                logMessage = getDebugLogMessage(stackTraceElement, logMessage);
                Log.d(tag, logMessage, throwable);
                break;
            case Log.ERROR:
                Log.e(tag, logMessage, throwable);
                break;
            case Log.INFO:
                Log.i(tag, logMessage, throwable);
                break;
            case Log.VERBOSE:
                Log.v(tag, logMessage, throwable);
                break;
            case Log.WARN:
                Log.w(tag, logMessage, throwable);
                break;
        }
    }
    
    /**
     * 获取堆栈信息
     */
    private static StackTraceElement getStackTraceElement(int i) {
        return Thread.currentThread().getStackTrace()[i];
    }
    
    /**
     * 为 DEBUG 级别日志信息组装堆栈信息 从堆栈信息中获取当前被调用的方法信息
     */
    private static String getDebugLogMessage(StackTraceElement traceElement,
                                             String logMessage) {
        return String.format(CLASS_METHOD_LINE_FORMAT,
                             traceElement.getClassName(),
                             traceElement.getMethodName(),
                             traceElement.getFileName(),
                             traceElement.getLineNumber(),
                             logMessage);
    }
    
    /**
     * 向服务器发送日志
     */
    private static void sendLog(String logMessage, Throwable throwable) {
        StringWriter sw = new StringWriter();
        PrintWriter pw = new PrintWriter(sw);
        if(throwable!=null){
            throwable.printStackTrace(pw);
        }

        StringBuilder sb = new StringBuilder();
        sb.append("Version code is ");
        sb.append(Build.VERSION.SDK_INT + "\n"); // 设备的Android版本号
        sb.append("Model is ");
        sb.append(Build.MODEL + "\n"); // 设备型号
        sb.append(sw.toString());
        
    }
    
    /**
     * Writes the current app logcat to a file.
     *
     * @param filename The filename to save it as
     * @throws IOException
     */
    public static void writeLogcat(String filename) throws IOException {
        String[] args = { "logcat", "-v", "time", "-d" };

        Process process = Runtime.getRuntime().exec(args);

        InputStreamReader input = new InputStreamReader(process.getInputStream());

        FileOutputStream fileStream;
        try {
            fileStream = new FileOutputStream(filename);
        } catch( FileNotFoundException e) {
            return;
        }

        OutputStreamWriter output = new OutputStreamWriter(fileStream);
        BufferedReader br = new BufferedReader(input);
        BufferedWriter bw = new BufferedWriter(output);

        try {
            String line;
            while ((line = br.readLine()) != null) {
                bw.write(line);
                bw.newLine();
            }
        }catch(Exception e) {}
        finally {
            bw.close();
            output.close();
            br.close();
            input.close();
        }
    }
}
