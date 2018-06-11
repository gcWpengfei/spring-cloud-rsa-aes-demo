package com.wpf.util;


import java.io.IOException;
import java.io.InputStream;
import java.io.RandomAccessFile;
import java.net.HttpURLConnection;
import java.net.URL;


public class MutiDownloadTest {
    private static final int    THREAD_COUNT    = 5;
    private static final String    DOWNLOAD_URL    = "http://down.360safe.com/se/360se9.1.0.426.exe";
    private static final String    fileName        = "D:\\l\\sts-bundle/360.exe";

    public static void main(String[] args) {

        long fileSize = 0;
        HttpURLConnection connection = null;
        try {
            connection = (HttpURLConnection)new URL(DOWNLOAD_URL).openConnection();
            connection.setRequestMethod("GET");
            connection.setConnectTimeout(8000);
            connection.setReadTimeout(8000);

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
                for(int i=0; i<THREAD_COUNT; i++){
                    long startIndex = i*eachSize;
                    long endIndex = (i+1)*eachSize - 1;
                    /**
                     * 当时最后一个线程的时候,endIndex的值就由文件大小
                     */
                    if(i == THREAD_COUNT - 1){
                        endIndex = fileSize;
                    }

                    Runnable runnable = new DownloadThreadTest(DOWNLOAD_URL,fileName,i, startIndex, endIndex);
                    new Thread(runnable).start();

                }
            }

        } catch (IOException e) {
            e.printStackTrace();
            // log.info(ExceptionUtils.getFullStackTrace(e));
        } finally {
            if(null != connection){
                connection.disconnect();
            }
        }
    }
}

class DownloadThreadTest implements Runnable {

    // private static final Logger log = Logger.getLogger(DownloadThreadTest.class);

    private String url;
    private String fileName;

    private int  threadId;

    private long startIndex;
    private long endIndex;

    private HttpURLConnection httpURLConnection;
    private RandomAccessFile randomAccessFile;
    private InputStream inputStream;

    public DownloadThreadTest(String url, String fileName, int threadId, long startIndex, long endIndex) {
        super();
        this.url = url;
        this.fileName = fileName;
        this.threadId = threadId;
        this.startIndex = startIndex;
        this.endIndex = endIndex;

    }

    @Override
    public void run() {

        try {
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
                    //log.info("线程" + threadId + ":" + total);
                    System.out.println("线程: " +  threadId + ":" + total);
                    randomAccessFile.write(bytes, 0, len);
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
            // log.info(ExceptionUtils.getFullStackTrace(e));
        } finally {
            try {
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
                //log.info(ExceptionUtils.getFullStackTrace(e));
            }
        }
    }
}