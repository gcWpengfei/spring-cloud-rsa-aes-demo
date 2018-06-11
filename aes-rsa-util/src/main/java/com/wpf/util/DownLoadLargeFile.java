package com.wpf.util;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.RandomAccessFile;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.regex.Pattern;

import org.apache.commons.lang3.exception.ExceptionUtils;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.impl.conn.PoolingHttpClientConnectionManager;
import org.apache.http.protocol.BasicHttpContext;
import org.apache.http.protocol.HttpContext;
import org.apache.log4j.Logger;

/**
 * 多线程下载文件，经测试4G以上的文件也可正常下载
 */
public class DownLoadLargeFile {

    private static final Logger log = Logger.getLogger(DownLoadLargeFile.class);

    // 测试标记
    private static boolean isTest = true;

    private CloseableHttpClient httpClient;

    public static void main(String[] args) {
        long starttimes = System.currentTimeMillis();
        DownLoadLargeFile app = new DownLoadLargeFile();

        PoolingHttpClientConnectionManager cm = new PoolingHttpClientConnectionManager();
        // 设置整个连接池最大连接数 20
        cm.setMaxTotal(20);
        app.httpClient = HttpClients.custom().setConnectionManager(cm).build();
        try {
            app.doDownload("http://localhost:8082/corporate/download?username=222017", "D:/l/sts-bundle/");
            // app.doDownload("d:/doctohtml/HrMngDB20170624.bak.cfg");
            app.httpClient.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
        System.out.println((System.currentTimeMillis() - starttimes) + "ms");
        cm.shutdown();
        cm.close();
        cm = null;
    }

    /**
     * 启动多个线程下载文件
     *
     * @param remoteUrl
     * @param localPath
     * @throws IOException
     */
    public void doDownload(String remoteUrl, String localPath) throws IOException {
        FileCfg fileCfg = new FileCfg(localPath, remoteUrl);
        if (fileCfg.getDnldStatus() == 0) {
            download(fileCfg);
        } else {
            System.out.println("解析错误，无法下载");
        }
    }

    /**
     * 读取配置文件并按照其内容启动多个线程下载文件未下载完毕的部分
     *
     * @param cfgFile
     * @throws IOException
     */
    public void doDownload(String cfgFile) throws IOException {
        FileCfg fileCfg = new FileCfg(cfgFile);
        if (fileCfg.getDnldStatus() == 0) {
            download(fileCfg);
        } else {
            System.out.println("解析错误，无法下载");
        }
    }

    /**
     * 根据配置文件下载文件
     *
     * @param fileCfg
     * @throws IOException
     */
    public void download(FileCfg fileCfg) throws IOException {
        if (fileCfg.getDnldStatus() == 0) {
            if (fileCfg.getFilePartList() != null && fileCfg.getFilePartList().size() > 0) {
                CountDownLatch end = new CountDownLatch(fileCfg.getFilePartList().size());
                ExecutorService cachedThreadPool = Executors.newCachedThreadPool();

                for (FilePart filePart : fileCfg.getFilePartList()) {
                    if (!filePart.isFinish()) {
                        // 仅下载未完成的文件片段
                        DownloadThread downloadThread = new DownloadThread(filePart, end, httpClient);
                        cachedThreadPool.execute(downloadThread);
                    }
                }
                try {
                    end.await();
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
                log.info("下载完成！");

                log.info(fileCfg.getDnldTmpFile());
            } else {
                System.out.println("没有需要下载的内容");
            }
        } else {
            System.out.println("解析错误，无法下载");
        }
    }

    public static void callback(FilePart filePart) {
        if (isTest) {
            System.out.println(">>>子线程执行之后的值是：" + filePart.toString());
        }
        // 保存线程执行结果
        File newFile = new File(filePart.getPartCfgfileName());

        try {
            // byte，char 1字节
            // short 2字节
            // int 4字节
            // long 8字节
            // Boolean 1字节
            RandomAccessFile raf = new RandomAccessFile(newFile, "rws");
            raf.seek(filePart.getThreadId() * 21 + 20);
            raf.writeBoolean(filePart.isFinish());
            raf.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    // 待下载文件任务信息
    public class FileCfg {

        // 待下载的文件链接
        private String url = null;
        // 待下载的文件链接
        private String fileName = null;
        // 待下载的文件长度
        private long fileSize = 0l;
        // 每个线程下载的字节数
        private int unitSize = 1024000;
        // 下载状态
        private short dnldStatus = -1;
        // 下载路径
        private String dnldPath = "d:/download";

        private List<FilePart> filePartList = null;

        public FileCfg(String cfgFile) {
            try {
                // 读取配置文件
                DataInputStream is = new DataInputStream(new BufferedInputStream(new FileInputStream(cfgFile)));
                this.url = is.readUTF();
                this.fileName = is.readUTF();
                this.dnldPath = is.readUTF();
                this.fileSize = is.readLong();
                this.unitSize = is.readInt();
                this.dnldStatus = is.readShort();

                // 下载片段数
                int partSize = is.readInt();
                is.close();
                if (isTest) {
                    System.out.println("FileCfg--->" + toString());
                }

                boolean reDownload = false;
                File downloadFile = new File(getDnldTmpFile());
                if (!downloadFile.exists() || !downloadFile.isFile()) {
                    // 重新下载文件
                    RandomAccessFile raf;
                    try {
                        raf = new RandomAccessFile(getDnldTmpFile(), "rw");
                        raf.setLength(fileSize);
                        raf.close();
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                    reDownload = true;
                }

                // 读取文件下载片段信息
                filePartList = new ArrayList<>(partSize);
                is = new DataInputStream(new BufferedInputStream(new FileInputStream(cfgFile + ".part")));
                for (int i = 0; i < partSize; i++) {
                    FilePart part = new FilePart(getUrl(), getDnldTmpFile(), getDnldPartFile(), is.readInt(),
                            is.readLong(), is.readLong());
                    boolean finish = is.readBoolean();
                    if (!reDownload) {
                        part.setFinish(finish);
                    }
                    if (isTest) {
                        System.out.println(i + "--->" + part.toString());
                    }
                    filePartList.add(part);
                }
                is.close();
            } catch (IOException e) {

            }
        }

        public FileCfg(String dnldPath, String url) throws IOException {
            this.url = url;
            this.dnldPath = dnldPath;

            HttpURLConnection httpConnection = (HttpURLConnection) new URL(url).openConnection();
            httpConnection.setRequestMethod("HEAD");
            int responseCode = httpConnection.getResponseCode();
            if (responseCode >= 400) {
                System.out.println("Web服务器响应错误!");
                return;
            }

            String lengthField = httpConnection.getHeaderField("Content-Length");
            if (lengthField != null && isLong(lengthField)) {
                System.out.println("文件大小:[" + lengthField + "]");
                this.fileSize = Long.parseLong(lengthField);
            }

            String nameField = httpConnection.getHeaderField("Content-Disposition");
            if (nameField != null) {
                String mark = "filename=\"";
                if (isTest) {
                    System.out.println("字符串:[" + nameField + "]");
                }
                int idx = nameField.indexOf(mark);
                this.fileName = nameField.substring(idx + mark.length(), nameField.length() - 1);
                // 如果没有解析到文件名称，则从url中获取
                if (this.fileName == null || this.fileName.length() < 1) {
                    this.fileName = url.substring(url.lastIndexOf("/") + 1, url.length()).replace("%20", " ");
                }
                if (isTest) {
                    System.out.println("文件名称:[" + fileName + "]");
                }
            }

            if (isTest) {
                // 读取所有的Head信息
                httpConnection.getContentLength();
                httpConnection.getContentLengthLong();
                httpConnection.getHeaderFields();
                for (Iterator<String> iter = httpConnection.getHeaderFields().keySet().iterator(); iter.hasNext();) {
                    String key = iter.next();
                    System.out.println("[" + key + "][" + httpConnection.getHeaderField(key) + "]");
                }
            }

            calFileInfo();
        }

        public FileCfg(String dnldPath, String url, String fileName, long fileSize) {
            this.url = url;
            this.fileName = fileName;
            this.fileSize = fileSize;
            this.dnldPath = dnldPath;
            calFileInfo();
        }

        /**
         * 判断指定的字符串是否可转为Long型数据
         *
         * @param str
         * @return
         */
        public boolean isLong(String str) {
            if (str == null || str.trim().length() < 1) {
                return false;
            }
            Pattern pattern = Pattern.compile("[0-9]*");
            return pattern.matcher(str).matches();
        }

        public String getUrl() {
            return url;
        }

        public void setUrl(String url) {
            this.url = url;
        }

        public String getFileName() {
            return fileName;
        }

        public void setFileName(String fileName) {
            this.fileName = fileName;
        }

        public long getFileSize() {
            return fileSize;
        }

        public void setFileSize(long fileSize) {
            this.fileSize = fileSize;
        }

        public int getUnitSize() {
            return unitSize;
        }

        public void setUnitSize(int unitSize) {
            this.unitSize = unitSize;
        }

        public short getDnldStatus() {
            return dnldStatus;
        }

        public void setDnldStatus(short dnldStatus) {
            this.dnldStatus = dnldStatus;
        }

        public String getDnldPath() {
            return dnldPath;
        }

        public void setDnldPath(String dnldPath) {
            this.dnldPath = dnldPath;
        }

        public List<FilePart> getFilePartList() {
            return filePartList;
        }

        public void setFilePartList(List<FilePart> filePartList) {
            this.filePartList = filePartList;
        }

        public String getDnldCfgFile() {
            return dnldPath + "/" + fileName + ".cfg";
        }

        public String getDnldPartFile() {
            return dnldPath + "/" + fileName + ".cfg.part";
        }

        public String getDnldTmpFile() {
            return dnldPath + "/" + fileName + ".tmp";
        }

        public String getDnldFile() {
            return dnldPath + "/" + fileName;
        }

        public void calFileInfo() {
            // 计算文件片段数量
            if (fileSize < 1) {
                // 没有需要下载的文件
                dnldStatus = -2;
                return;
            }
            long filePartSize = (fileSize - 1) / unitSize + 1;
            if (filePartSize > Integer.MAX_VALUE) {
                // 文件过大，不能下载
                dnldStatus = -10;
                return;
            }

            // 构建文件片段列表
            filePartList = new ArrayList<>((int) filePartSize);
            for (int i = 0; i < filePartSize; i++) {
                long offset = i * unitSize;
                long length = unitSize;
                // 读取数量超过文件大小
                if ((offset + length) > this.fileSize) {
                    length = this.fileSize - offset;
                }
                FilePart part = new FilePart(getUrl(), getDnldTmpFile(), getDnldPartFile(), i, offset, length);
                if (isTest) {
                    System.out.println(i + "--->" + part.toString());
                }
                filePartList.add(part);
            }
            dnldStatus = 0;

            // 构建完成，保存信息到文档
            writeFile();

            // 检查下载文件是否存在
            File newFile = new File(fileName);
            if (!newFile.exists() || !newFile.isFile()) {
                // 文件不存在，则重新创建，如存在，则保持原状，用于断点续传
                RandomAccessFile raf;
                try {
                    raf = new RandomAccessFile(newFile, "rw");
                    raf.setLength(fileSize);
                    raf.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }

        /**
         * 将下载信息保存到文件中，以便断点续传
         */
        public void writeFile() {
            // 文件下载信息
            String dnldFile = getDnldCfgFile();
            // 文件片段信息
            String dnldPartFile = getDnldPartFile();

            try {
                // 保存文件下载信息到文件
                DataOutputStream os = new DataOutputStream(new BufferedOutputStream(new FileOutputStream(dnldFile)));
                os.writeUTF(url);
                os.writeUTF(fileName);
                os.writeUTF(dnldPath);
                os.writeLong(fileSize);
                os.writeInt(unitSize);
                os.writeShort(dnldStatus);
                os.writeInt(this.filePartList.size());
                os.flush();
                os.close();

                // 保存文件片段信息到文件
                os = new DataOutputStream(new BufferedOutputStream(new FileOutputStream(dnldPartFile)));
                for (int i = 0, p = filePartList.size(); i < p; i++) {
                    FilePart part = filePartList.get(i);
                    os.writeInt(part.getThreadId());
                    os.writeLong(part.getOffset());
                    os.writeLong(part.getLength());
                    os.writeBoolean(part.isFinish());
                    os.flush();
                }
                os.close();

                // 生成文件，并指定大小（与待下载的文件相同）
                File saveFile = new File(getDnldTmpFile());
                RandomAccessFile raf = new RandomAccessFile(saveFile, "rw");
                raf.setLength(fileSize);
                raf.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

        public void clearFile() {
            // 文件下载信息
            String dnldFile = getDnldCfgFile();
            // 文件片段信息
            String dnldPartFile = getDnldPartFile();

            File file = new File(dnldFile);
            // 如果文件路径所对应的文件存在，并且是一个文件，则直接删除
            if (file.exists() && file.isFile()) {
                if (file.delete()) {
                    System.out.println("删除文件" + dnldFile + "成功！");
                } else {
                    System.out.println("删除文件" + dnldFile + "失败！");
                }
            } else {
                System.out.println("删除文件失败：" + dnldFile + "不存在！");
            }

            file = new File(dnldPartFile);
            // 如果文件路径所对应的文件存在，并且是一个文件，则直接删除
            if (file.exists() && file.isFile()) {
                if (file.delete()) {
                    System.out.println("删除文件" + dnldPartFile + "成功！");
                } else {
                    System.out.println("删除文件" + dnldPartFile + "失败！");
                }
            } else {
                System.out.println("删除文件失败：" + dnldPartFile + "不存在！");
            }

            // 下载完成后的临时文件名改为正式名称
            File oldFile = new File(getDnldTmpFile());
            File newFile = new File(getDnldFile());
            if (oldFile.exists()) {
                // 重命名文件存在
                if (!newFile.exists()) {
                    oldFile.renameTo(newFile);
                } else {
                    // 若在该目录下已经有一个文件和新文件名相同，则不允许重命名
                    System.out.println(newFile + "已经存在！");
                }
            }
        }

        public String toString() {
            return "isTest[" + isTest + "]url[" + url + "]fileName[" + fileName + "]fileSize[" + fileSize + "]unitSize["
                    + unitSize + "]dnldStatus[" + dnldStatus + "]dnldPath[" + dnldPath + "]filePartList["
                    + ((filePartList != null) ? filePartList.size() : 0) + "]";
        }
    }

    /**
     * 文件片段信息
     */
    public class FilePart {

        // 待下载的文件
        private String url = null;
        // 本地文件名
        private String fileName = null;
        // 本地文件名
        private String partCfgfileName = null;
        // 当前第几个线程
        private int threadId = 0;
        // 偏移量
        private long offset = 0;
        // 分配给本线程的下载字节数
        private long length = 0;
        // 监听本线程下载是否完成
        private boolean finish = false;

        public FilePart(String url, String fileName, String partCfgfileName, int threadId, long offset, long length) {
            this.url = url;
            this.fileName = fileName;
            this.partCfgfileName = partCfgfileName;
            this.threadId = threadId;
            this.offset = offset;
            this.length = length;
        }

        public String getUrl() {
            return url;
        }

        public void setUrl(String url) {
            this.url = url;
        }

        public String getFileName() {
            return fileName;
        }

        public void setFileName(String fileName) {
            this.fileName = fileName;
        }

        public String getPartCfgfileName() {
            return partCfgfileName;
        }

        public void setPartCfgfileName(String partCfgfileName) {
            this.partCfgfileName = partCfgfileName;
        }

        public int getThreadId() {
            return threadId;
        }

        public void setThreadId(int threadId) {
            this.threadId = threadId;
        }

        public long getOffset() {
            return offset;
        }

        public void setOffset(long offset) {
            this.offset = offset;
        }

        public long getLength() {
            return length;
        }

        public void setLength(long length) {
            this.length = length;
        }

        public boolean isFinish() {
            return finish;
        }

        public void setFinish(boolean finish) {
            this.finish = finish;
        }

        public String toString() {
            return "threadId[" + threadId + "]offset[" + offset + "]length[" + length + "]finish[" + finish + "]";
        }

    }

    /**
     * 文件下载线程
     */
    public class DownloadThread extends Thread {

        // // 待下载的文件
        // private String url = null;
        // // 本地文件名
        // private String fileName = null;
        // 待下载文件片段
        private FilePart filePart = null;
        // 通知服务器文件的取值范围
        private String rangeStr = "";
        // 同步工具类,允许一个或多个线程一直等待,直到其他线程的操作执行完后再执行
        private CountDownLatch end;
        // http客户端
        private CloseableHttpClient httpClient;
        // 上下文
        private HttpContext context;

        /**
         * @param part
         * @param end
         * @param hc
         */
        public DownloadThread(FilePart part, CountDownLatch end, CloseableHttpClient hc) {
            this.filePart = part;
            this.end = end;
            this.httpClient = hc;
            this.context = new BasicHttpContext();
            this.rangeStr = "bytes=" + filePart.getOffset() + "-" + (filePart.getOffset() + filePart.getLength());
            if (isTest) {
                System.out.println("rangeStr[" + rangeStr + "]");
                System.out.println("偏移量=" + filePart.getOffset() + ";字节数=" + filePart.getLength());
            }
        }

        public void run() {
            try {
                HttpGet httpGet = new HttpGet(filePart.getUrl());
                httpGet.addHeader("Range", rangeStr);
                CloseableHttpResponse response = httpClient.execute(httpGet, context);
                BufferedInputStream bis = new BufferedInputStream(response.getEntity().getContent());

                byte[] buff = new byte[1024];
                int bytesRead;
                File newFile = new File(filePart.getFileName());
                // Rws模式就是同步模式，每write修改一个byte，立马写到磁盘。当然性能差点儿,适合小的文件，debug模式，或者需要安全性高的时候。
                // Rwd模式跟个rws基础的一样，不过只对“文件的内容”同步更新到磁盘，而不对metadata同步更新。
                // 默认情形下(rw模式下),是使用buffer的,只有cache满的或者使用RandomAccessFile.close()关闭流的时候儿才真正的写到文件。
                // 这个会有两个问题:
                // 1.调试麻烦的--->使用write方法修改byte的时候儿,只修改到个内存内,还没到个文件,不能使用notepad++工具立即看见修改效果。
                // 2.当系统halt的时候儿,不能写到文件，安全性稍微差点儿。
                RandomAccessFile raf = new RandomAccessFile(newFile, "rws");

                long offset = filePart.getOffset();
                while ((bytesRead = bis.read(buff, 0, buff.length)) != -1) {
                    raf.seek(offset);
                    raf.write(buff, 0, bytesRead);
                    offset += bytesRead;
                }
                raf.close();
                bis.close();
                // 下载线程执行完毕
                filePart.setFinish(true);
                // 调用回调函数告诉主进程该线程已执行完毕
                DownLoadLargeFile.callback(filePart);
            } catch (ClientProtocolException e) {
                //log.error("文件下载异常信息:{}", ExceptionUtils.getStackTrace(e));
                log.error("文件下载异常信息:{}");
                log.error(ExceptionUtils.getStackTrace(e));
            } catch (IOException e) {
                log.error("文件下载异常信息:{}");
                log.error(ExceptionUtils.getStackTrace(e));
            } finally {
                end.countDown();
                if (isTest) {
                    log.info("剩余线程[" + end.getCount() + "]继续执行!");
                }
            }
        }
    }

}