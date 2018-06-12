package com.wpf.controller;

import org.apache.commons.lang.exception.ExceptionUtils;
import org.apache.log4j.Logger;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.RandomAccessFile;
import java.net.HttpURLConnection;
import java.net.URL;

public class DownloadThreadTest1 implements Runnable {

    private static final Logger log = Logger.getLogger(DownloadThreadTest1.class);

    private String url;
    private String fileName;

    private int  threadId;

    private long startIndex;
    private long endIndex;

    private HttpURLConnection httpURLConnection;
    private RandomAccessFile randomAccessFile;
    private InputStream inputStream;
    private HttpServletRequest request;
    private HttpServletResponse response;

    DownloadThreadTest1(HttpServletRequest request, HttpServletResponse response,
                        String url, String fileName, int threadId, long startIndex, long endIndex) {
        super();
        this.url = url;
        this.fileName = fileName;
        this.threadId = threadId;
        this.startIndex = startIndex;
        this.endIndex = endIndex;
        this.request = request;
        this.response = response;

    }

    @Override
    public void run() {
        RandomAccessFile downThreadStream = null;
        /*
             * 查看临时文件
             */
        File downThreadFile = new File("F:\\1\\test\\2/", "wpf_thread_"+threadId+".dt");
        try {

            if(downThreadFile.exists()){
                downThreadStream = new RandomAccessFile(downThreadFile,"rwd");
                String startIndex_str = downThreadStream.readLine();
                if(null == startIndex_str || "".equals(startIndex_str)){
                } else {
                    this.startIndex = Long.parseLong(startIndex_str) - 1; // //设置下载起点
                    log.info(this.startIndex);
                }
            } else {
                downThreadStream = new RandomAccessFile(downThreadFile, "rwd");
            }


            httpURLConnection = (HttpURLConnection) new URL(url + "?ts=" + System.currentTimeMillis()).openConnection();
            httpURLConnection.setRequestMethod("GET");
            httpURLConnection.setConnectTimeout(8000);
            httpURLConnection.setReadTimeout(8000);
            /**
             * 设置请求范围.
             */
            httpURLConnection.setRequestProperty("RANGE", "bytes=" + startIndex + "-" + endIndex);

            /**
             * 当请求部分数据成功的时候,返回http状态码206
             */
            if (httpURLConnection.getResponseCode() == 206){

                inputStream = httpURLConnection.getInputStream();
                randomAccessFile = new RandomAccessFile(fileName, "rwd");
                /**
                 * 把开始写的位置设置为startIndex,与请求数据的位置一致
                 */
                randomAccessFile.seek(startIndex);

                byte[] bytes = new byte[1024];
                int len;
                int total = 0;
                while((len = inputStream.read(bytes)) != -1){
                    total += len;
                    log.info("线程" + threadId + ":" + total);
                    System.out.println("线程: " +  threadId + ":" + total);
                    randomAccessFile.write(bytes, 0, len);

                     /*
                         * 将当前现在到的位置保存到文件中
                         */
                    downThreadStream.seek(0);
                    downThreadStream.write((startIndex + total + "").getBytes("UTF-8"));
                }


            }
        } catch (IOException e) {
            e.printStackTrace();
            log.info(ExceptionUtils.getFullStackTrace(e));
        } finally {
            try {
                cleanTemp(downThreadFile);
                if(null != downThreadStream){
                    downThreadStream.close();

                }
                if(null != httpURLConnection){
                    httpURLConnection.disconnect();
                }
                if(null != inputStream){
                    inputStream.close();
                }
                if(null != randomAccessFile){
                    randomAccessFile.close();
                }

            } catch (Exception e) {
                e.printStackTrace();
                log.info(ExceptionUtils.getFullStackTrace(e));
            }
        }
    }

    //删除线程产生的临时文件
    private  void cleanTemp(File file){
        file.delete();
    }
}