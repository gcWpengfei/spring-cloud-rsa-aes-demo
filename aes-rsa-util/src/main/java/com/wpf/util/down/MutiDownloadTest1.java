package com.wpf.util.down;


import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.RandomAccessFile;
import java.net.HttpURLConnection;
import java.net.URL;

/**
 * 多线程下载和断点续传
 *
 * @Author: wpf
 * @Date: 16:42 2018/6/12
 * @Description: 
 * @param  * @param null  
 * @return   
 */
public class MutiDownloadTest1 {
    private static final int    THREAD_COUNT    = 5;
    private static final String    DOWNLOAD_URL    = "http://down.360safe.com/se/360se9.1.0.426.exe";
    private static final String    fileName        = "F:\\1\\test\\2/360.exe";
     static final String    filePath        = "F:\\1\\test\\2/";

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

                    Runnable runnable = new DownloadThreadTest1(DOWNLOAD_URL,fileName,i, startIndex, endIndex);
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

class DownloadThreadTest1 implements Runnable {

    // private static final Logger log = Logger.getLogger(DownloadThreadTest.class);

    private String url;
    private String fileName;

    private int  threadId;

    private long startIndex;
    private long endIndex;

    private HttpURLConnection httpURLConnection;
    private RandomAccessFile randomAccessFile;
    private InputStream inputStream;

    public DownloadThreadTest1(String url, String fileName, int threadId, long startIndex, long endIndex) {
        super();
        this.url = url;
        this.fileName = fileName;
        this.threadId = threadId;
        this.startIndex = startIndex;
        this.endIndex = endIndex;

    }

    @Override
    public void run() {
        RandomAccessFile downThreadStream = null;
        /*
             * 查看临时文件
             */
        File downThreadFile = new File(MutiDownloadTest1.filePath, "wpf_thread_"+threadId+".dt");
        try {

            if(downThreadFile.exists()){
                downThreadStream = new RandomAccessFile(downThreadFile,"rwd");
                String startIndex_str = downThreadStream.readLine();
                if(null == startIndex_str || "".equals(startIndex_str)){
                } else {
                    this.startIndex = Long.parseLong(startIndex_str) - 1; // //设置下载起点
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
                    //log.info("线程" + threadId + ":" + total);
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
            // log.info(ExceptionUtils.getFullStackTrace(e));
        } finally {
            try {
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
                cleanTemp(downThreadFile);
            } catch (Exception e) {
                e.printStackTrace();
                //log.info(ExceptionUtils.getFullStackTrace(e));
            }
        }
    }

    //删除线程产生的临时文件
    private synchronized void cleanTemp(File file){
        file.delete();
    }
}