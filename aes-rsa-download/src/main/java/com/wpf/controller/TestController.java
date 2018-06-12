package com.wpf.controller;

import org.apache.commons.lang.exception.ExceptionUtils;
import org.apache.log4j.Logger;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.RandomAccessFile;
import java.net.HttpURLConnection;
import java.net.URL;

@Controller
@RequestMapping("/")
public class TestController {

    private static final Logger log = Logger.getLogger(DownloadThreadTest1.class);
    private static final int    THREAD_COUNT    = 5;
    private static final String    DOWNLOAD_URL    = "http://down.360safe.com/se/360se9.1.0.426.exe";
    private static final String    fileName        = "F:\\1\\test\\2/360.exe";
    static final String    filePath        = "F:\\1\\test\\2/";

    @GetMapping("/download")
    public void download(HttpServletRequest request, HttpServletResponse response){

        long fileSize = 0;
        HttpURLConnection connection = null;
        try {
            connection = (HttpURLConnection)new URL(DOWNLOAD_URL).openConnection();
            connection.setRequestMethod("GET");
            connection.setConnectTimeout(8000);
            connection.setReadTimeout(8000);
            log.info(connection);

            /**
             * 当请求成功时,返回http状态码200
             */
            if (connection.getResponseCode() == 200) {
                /**
                 * 打开一个RandomAccessFile文件,打开方式为读写(rw)
                 * setLength是先在存储设备占用一块空间,防止下载到一半空间不足
                 */
                RandomAccessFile randomAccessFile = new RandomAccessFile(fileName , "rw");
                fileSize = connection.getContentLength();
                randomAccessFile.setLength(fileSize);
                randomAccessFile.close();

                long eachSize = fileSize/THREAD_COUNT;
                log.info(eachSize);
                for(int i=0; i<THREAD_COUNT; i++){
                    long startIndex = i*eachSize;
                    long endIndex = (i+1)*eachSize - 1;
                    log.info(startIndex);
                    log.info(endIndex);
                    /**
                     * 当时最后一个线程的时候,endIndex的值就由文件大小
                     */
                    if(i == THREAD_COUNT - 1){
                        endIndex = fileSize;
                    }

                    Runnable runnable = new DownloadThreadTest1(request, response,DOWNLOAD_URL,fileName,i, startIndex, endIndex);
                    new Thread(runnable).start();

                }
            }

        } catch (IOException e) {
            e.printStackTrace();
            log.info(ExceptionUtils.getFullStackTrace(e));
        } finally {
            if(null != connection){
                connection.disconnect();
            }
        }
    }
}
