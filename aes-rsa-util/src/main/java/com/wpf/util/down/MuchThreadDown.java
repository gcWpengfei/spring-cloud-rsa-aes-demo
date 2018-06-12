package com.wpf.util.down;

import java.io.*;
import java.net.HttpURLConnection;
import java.net.URL;

public class MuchThreadDown {


    private static int threadCount = 3;//开启3个线程
    private static int blockSize = 0;//每个线程下载的大小
    private static int runningTrheadCount = 0;//当前运行的线程数
    private static String path = "http://down.360safe.com/se/360se9.1.0.426.exe";
    /**
     * @param args
     */
    public static void main(String[] args) {

        try{
            //1.请求url地址获取服务端资源的大小
            URL url = new URL(path);
            HttpURLConnection openConnection = (HttpURLConnection) url.openConnection();
            openConnection.setRequestMethod("GET");
            openConnection.setConnectTimeout(10*1000);

            int code = openConnection.getResponseCode();
            if(code == 200){
                //获取资源的大小
                int filelength = openConnection.getContentLength();
                //2.在本地创建一个与服务端资源同样大小的一个文件（占位）
                RandomAccessFile randomAccessFile = new RandomAccessFile(new File(getFileName(path)), "rw");
                randomAccessFile.setLength(filelength);//设置随机访问文件的大小

                //3.要分配每个线程下载文件的开始位置和结束位置。
                blockSize = filelength/threadCount;//计算出每个线程理论下载大小
                for(int threadId =0 ;threadId < threadCount;threadId++){
                    int startIndex =  threadId * blockSize;//计算每个线程下载的开始位置
                    int endIndex = (threadId+1)*blockSize -1;//计算每个线程下载的结束位置
                    //如果是最后一个线程，结束位置需要单独计算
                    if(threadId == threadCount-1){
                        endIndex = filelength -1;
                    }

                    //4.开启线程去执行下载
                    new DownloadThread(threadId, startIndex, endIndex).start();


                }


            }


        }catch (Exception e) {
            e.printStackTrace();
        }

    }


    public static class DownloadThread  extends Thread{


        private int threadId;
        private int startIndex;
        private int endIndex;
        private int lastPostion;
        public DownloadThread(int threadId,int startIndex,int endIndex){
            this.threadId = threadId;
            this.startIndex = startIndex;
            this.endIndex = endIndex;
        }

        @Override
        public void run() {

            synchronized (DownloadThread.class) {

                runningTrheadCount = runningTrheadCount +1;//开启一线程，线程数加1
            }

            //分段请求网络连接，分段保存文件到本地
            try{
                URL url = new URL(path);
                HttpURLConnection openConnection = (HttpURLConnection) url.openConnection();
                openConnection.setRequestMethod("GET");
                openConnection.setConnectTimeout(10*1000);


                System.out.println("理论上下载：  线程："+threadId+"，开始位置："+startIndex+";结束位置:"+endIndex);


                //读取上次下载结束的位置,本次从这个位置开始直接下载。
                File file2 = new File(threadId+".txt");
                if(file2.exists()){
                    BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(new FileInputStream(file2)));
                    String lastPostion_str = bufferedReader.readLine();
                    lastPostion = Integer.parseInt(lastPostion_str);//读取文件获取上次下载的位置

                    //设置分段下载的头信息。  Range:做分段数据请求用的。
                    openConnection.setRequestProperty("Range", "bytes:"+lastPostion+"-"+endIndex);//bytes:0-500:请求服务器资源中0-500之间的字节信息  501-1000:
                    System.out.println("实际下载：  线程："+threadId+"，开始位置："+lastPostion+";结束位置:"+endIndex);
                    bufferedReader.close();
                }else{

                    lastPostion = startIndex;
                    //设置分段下载的头信息。  Range:做分段数据请求用的。
                    openConnection.setRequestProperty("Range", "bytes:"+lastPostion+"-"+endIndex);//bytes:0-500:请求服务器资源中0-500之间的字节信息  501-1000:
                    System.out.println("实际下载：  线程："+threadId+"，开始位置："+lastPostion+";结束位置:"+endIndex);
                }







                if(openConnection.getResponseCode() == 206){//200：请求全部资源成功， 206代表部分资源请求成功
                    InputStream inputStream = openConnection.getInputStream();
                    //请求成功将流写入本地文件中，已经创建的占位那个文件中

                    RandomAccessFile randomAccessFile = new RandomAccessFile(new File(getFileName(path)), "rw");
                    randomAccessFile.seek(lastPostion);//设置随机文件从哪个位置开始写。
                    //将流中的数据写入文件
                    byte[] buffer = new byte[1024];
                    int length = -1;
                    int total = 0;//记录本次线程下载的总大小

                    while((length= inputStream.read(buffer)) !=-1){
                        randomAccessFile.write(buffer, 0, length);

                        total = total+ length;
                        //去保存当前线程下载的位置，保存到文件中
                        int currentThreadPostion = lastPostion + total;//计算出当前线程本次下载的位置
                        //创建随机文件保存当前线程下载的位置
                        File file = new File(threadId+".txt");
                        RandomAccessFile accessfile = new RandomAccessFile(file, "rwd");
                        accessfile.write(String.valueOf(currentThreadPostion).getBytes());
                        accessfile.close();



                    }
                    //关闭相关的流信息
                    inputStream.close();
                    randomAccessFile.close();

                    System.out.println("线程："+threadId+"，下载完毕");



                    //当所有线程下载结束，删除存放下载位置的文件。
                    synchronized (DownloadThread.class) {
                        runningTrheadCount = runningTrheadCount -1;//标志着一个线程下载结束。
                        if(runningTrheadCount == 0 ){
                            System.out.println("所有线程下载完成");
                            for(int i =0 ;i< threadCount;i++){
                                File file = new File(i+".txt");
                                System.out.println(file.getAbsolutePath());
                                file.delete();
                            }
                        }

                    }


                }


            }catch (Exception e) {
                e.printStackTrace();
            }



            super.run();
        }

    }


    public static String getFileName(String url){

       return   url.substring(url.lastIndexOf("/"));

    }

}