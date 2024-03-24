package com.itheima.reggie.controller;

import com.itheima.reggie.common.R;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import javax.servlet.ServletOutputStream;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.File;
import java.io.FileInputStream;
import java.util.UUID;

//文件上传和下载
@Slf4j
@RestController
@RequestMapping("/common")
public class CommonController {
    @Value("${reggie.path}")
    private String basePath;

    @PostMapping("/upload")
    public R<String> upload(MultipartFile file) {

        log.info("file是:{}", file.toString());
        String originalFilename = file.getOriginalFilename();
        String suffix = originalFilename.substring(originalFilename.lastIndexOf('.'));
        //使用uuid重新生成文件名，防止文件名称重复造成文件覆盖
        String fileName = UUID.randomUUID().toString() + suffix;

        File dir = new File(basePath);
        //判断当前目录是否存在
        if (!dir.exists()) {
            dir.mkdir();
        }
        try {
            file.transferTo(new File(basePath + fileName));
        } catch (Exception ex) {
            ex.printStackTrace();
        }
        return R.success(fileName);
    }

    //文件下载
    @GetMapping("/download")
    public void download(String name, HttpServletResponse response){
        //下载指定文件
        //通过输入流来读取文件内容
        try {
            FileInputStream fileInputStream = new FileInputStream(new File(basePath+name));
            byte[] bytes = new byte[1024];
            int len = 0;
            response.setContentType("image/jpeg");
            //这个逻辑简单讲一下,就是首先read(bytes)会从输入流中得到数据，然后将数据写入bytes数组中,
            //并且返回本次读入/写回的长度,只有当读完的时候,len才会等于-1
            //然后读完之后,将得到的byte通过write,将byte里面的内容通过len的限制写回输出流
            //循环往复
            while((len = fileInputStream.read(bytes))!= -1){
                response.getOutputStream().write(bytes,0,len);
                response.getOutputStream().flush();
            }
            response.getOutputStream().close();
            fileInputStream.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
        //通过输出流来写回浏览器,在浏览器中展示图片
    }


}
