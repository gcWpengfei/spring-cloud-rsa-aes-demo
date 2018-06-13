package com.wpf.util; /**
 * Project Name:MonitoringPlatform-commons-util
 * File Name:com.wpf.util.ZipCompressing.java
 * Package Name:com.monitoring.common.util
 * Date:2017年4月20日上午11:19:35
 * Copyright (c) 2017, chenzhou1025@126.com All Rights Reserved.
 *
*/

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.channels.FileChannel;
import java.util.Date;
import java.util.List;
import java.util.zip.ZipEntry;
import java.util.zip.ZipOutputStream;

import org.apache.commons.lang.StringUtils;
import org.apache.log4j.Logger;
 

/**
 * ClassName: com.wpf.util.ZipCompressing <br/>
 * Function:  文件压缩成zip. <br/>
 * Reason:  ADD REASON(可选). <br/>
 * date: 2017年4月20日 上午11:41:01 <br/>
 *
 * @author wpengfei
 * @version 
 * @since JDK 1.6
 */
public class ZipCompressing {
    private static final Logger logger = Logger.getLogger(ZipCompressing.class);
 
    static int k = 1; // 定义递归次数变量
    public ZipCompressing() {}
 
 
    /**
     * 压缩指定的单个或多个文件，如果是目录，则遍历目录下所有文件进行压缩
     * @param zipFileName ZIP文件名包含全路径
     * @param files  文件列表
     */
    @SuppressWarnings("unused")
    public static boolean zip(String zipFileName, File... files) {
        logger.info("压缩: "+zipFileName);
        ZipOutputStream out = null;
        BufferedOutputStream bo = null;
        try {
            createDir(zipFileName);
            out = new ZipOutputStream(new FileOutputStream(zipFileName));
            for (int i = 0; i < files.length; i++) {
                if (null != files[i]) {
                    zip(out, files[i], files[i].getName());
                }
            }
            out.close(); // 输出流关闭
            logger.info("压缩完成");
            return true;
        } catch (Exception e) {
            logger.error(e.getMessage());
            e.printStackTrace();
        }
        return false;
    }
 
    
    public static boolean zip(String zipFileName, List<File> fileList) {
    	
    	ZipOutputStream out = null;
        BufferedOutputStream bo = null;
        try {
            createDir(zipFileName);
            out = new ZipOutputStream(new FileOutputStream(zipFileName));
            for (int i = 0; i < fileList.size(); i++) {
                if (null != fileList.get(i)) {
                    zip(out, fileList.get(i), fileList.get(i).getName());
                }
            }
            out.close(); // 输出流关闭
            logger.info("压缩完成");
            return true;
        } catch (Exception e) {
            logger.error(e.getMessage());
            e.printStackTrace();
        }
        return false;
    }
    
    /**
     * 执行压缩
     * @param out ZIP输入流
     * @param f   被压缩的文件
     * @param base  被压缩的文件名
     */
    private static void zip(ZipOutputStream out, File f, String base) { // 方法重载
        try {
            if (f.isDirectory()) {//压缩目录
                try {
                    File[] fl = f.listFiles();
                    if (fl.length == 0) {
                        out.putNextEntry(new ZipEntry(base + "/"));  // 创建zip实体
                        logger.info(base + "/");
                    }
                    for (int i = 0; i < fl.length; i++) {
                        zip(out, fl[i], base + "/" + fl[i].getName()); // 递归遍历子文件夹
                    }
                    //System.out.println("第" + k + "次递归");
                    k++;
                } catch (IOException e) {
                    logger.error(e.getMessage());
                    e.printStackTrace();
                }
            }else{ //压缩单个文件
                logger.info(base);
                out.putNextEntry(new ZipEntry(base)); // 创建zip实体
                FileInputStream in = new FileInputStream(f);
                BufferedInputStream bi = new BufferedInputStream(in);
                int b;
                while ((b = bi.read()) != -1) {
                    out.write(b); // 将字节流写入当前zip目录
                }
                out.closeEntry(); //关闭zip实体
                in.close(); // 输入流关闭
            }
 
        } catch (IOException e) {
            logger.error(e.getMessage());
            e.printStackTrace();
        }
    }
 
 
    /**
     * 目录不存在时，先创建目录
     * @param zipFileName
     */
    private static void createDir(String zipFileName){
        String filePath = StringUtils.substringBeforeLast(zipFileName, "/");
        File targetFile = new File(filePath);
        if (!targetFile.exists()) {//目录不存在时，先创建目录
            targetFile.mkdirs();
        }
    }
 
 
    /**
     * @param args
     */
    public static void main(String[] args) {
        try {
        	
//            com.wpf.util.ZipCompressing.zip("E:\\hospital\\trunk\\MonitoringPlatform-commons-util\\src\\main\\java\\com\\monitoring\\common\\util\\test1.zip",new File("E:\\hospital\\trunk\\MonitoringPlatform-commons-util\\src\\main\\java\\com\\monitoring\\common\\util\\Constants.java"));    //测试单个文件
           // com.wpf.util.ZipCompressing.zip("F:/test2.zip", new File("F:/common.mdb"), new File("F:/《浙江省食品安全风险监测信息》开发计划.xlsx"));   //测试多个文件
           // com.wpf.util.ZipCompressing.zip("F:/test3.zip", new File("F:/test")); //测试压缩目录
            
        	
//        	com.wpf.util.ZipCompressing.zip("F:\\1\\test/test3.zip", new File("F:\\1\\test\\2/1.xlsx"), new File("F:\\1\\test\\2/2.xlsx"));
        	
        	/*List<File> list = new ArrayList<File>();
        	list.add(new File("F:\\1\\test\\2/1.xlsx"));
        	list.add(new File("F:\\1\\test\\2/2.xlsx"));
        	
        	com.wpf.util.ZipCompressing.zip("F:\\1\\test/test3.zip", list);*/
        	
        	File oldFile = new File("F:\\\\1\\\\test\\\\2/1.xlsx");
        	File newFile = new File("F:\\\\1\\\\test\\\\2/1COPY.xlsx");
        	forChannel(oldFile, newFile);
            
        } catch (Exception e) {
            e.printStackTrace();
        }
 
    }
    
    public static long forChannel(File f1,File f2) throws Exception{
        long time=new Date().getTime();
        int length=2097152;
        FileInputStream in=new FileInputStream(f1);
        FileOutputStream out=new FileOutputStream(f2);
        FileChannel inC=in.getChannel();
        FileChannel outC=out.getChannel();
        ByteBuffer b=null;
        while(true){
            if(inC.position()==inC.size()){
                inC.close();
                outC.close();
                return new Date().getTime()-time;
            }
            if((inC.size()-inC.position())<length){
                length=(int)(inC.size()-inC.position());
            }else
                length=2097152;
            b=ByteBuffer.allocateDirect(length);
            inC.read(b);
            b.flip();
            outC.write(b);
            outC.force(false);
        }
    }
}
