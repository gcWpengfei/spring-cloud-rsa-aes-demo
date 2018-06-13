package com.wpf.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.*;
import java.util.ArrayList;
import java.util.List;
import java.util.zip.ZipEntry;
import java.util.zip.ZipOutputStream;

@Controller
@RequestMapping("/t")
public class DownloadController {

    /**
     * 多文件边压缩边下载
     *
     * @Author: wpf
     * @Date: 11:09 2018/6/13
     * @Description:
     * @param  * @param null
     * @return
     */
    @ResponseBody
    @GetMapping("/download")
    public void downloadFiles(HttpServletRequest request, HttpServletResponse response){
        /*
        *  test
        * */
        List<String> list = new ArrayList<>();
        list.add("F:\\1\\test\\2\\1.exe");
        list.add("F:\\1\\test\\2\\2.exe");
        list.add("F:\\1\\test\\2\\3.exe");

        //响应头的设置
        response.reset();
        response.setCharacterEncoding("utf-8");
        response.setContentType("application/octet-stream;charset=utf-8");// 设置response内容的类型

        //设置压缩包的名字
        //解决不同浏览器压缩包名字含有中文时乱码的问题
        String downloadName = "test.zip";
        String agent = request.getHeader("USER-AGENT");
        ZipOutputStream zipos = null;
        //循环将文件写入压缩流
        DataOutputStream os = null;
        try {
            if (agent.contains("MSIE")||agent.contains("Trident")) {
                downloadName = java.net.URLEncoder.encode(downloadName, "UTF-8");
            } else {
                downloadName = new String(downloadName.getBytes("UTF-8"),"ISO-8859-1");
            }
            response.setHeader("Content-Disposition", "attachment;fileName=\"" + downloadName + "\"");

            //设置压缩流：直接写入response，实现边压缩边下载
            zipos = new ZipOutputStream(new BufferedOutputStream(response.getOutputStream()));
            zipos.setMethod(ZipOutputStream.DEFLATED); //设置压缩方法

            for(int i = 0; i < list.size(); i++ ){

                InputStream is = null;
                try{
                    File file = new File(list.get(i));
                    if(file.exists()){
                        //添加ZipEntry，并ZipEntry中写入文件流
                        //这里，加上i是防止要下载的文件有重名的导致下载失败
                        zipos.putNextEntry(new ZipEntry(i + "_" + file.getName()));
                        os = new DataOutputStream(zipos);
                        is = new FileInputStream(file);
                        byte[] b = new byte[1024];
                        int length = 0;
                        while((length = is.read(b))!= -1){
                            os.write(b, 0, length);
                        }
                    }
                } finally {
                    if(null != is){
                        is.close();
                    }
                    zipos.closeEntry();
                }

            }
            if(null != os){
                os.flush();
            }


        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            //关闭流
            try {
                if(null != os){
                    os.close();
                }
                if(null != zipos){
                    zipos.close();
                }

            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    public static void main(String[] args) {
        File file = new File("F:\\1\\test\\2\\1.exe");
        System.out.println(file.getName());
    }
}
