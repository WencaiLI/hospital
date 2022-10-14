package com.thtf.office.common.util;

import com.thtf.common.response.JsonResult;
import lombok.extern.slf4j.Slf4j;

import javax.servlet.http.HttpServletResponse;
import java.io.*;

/**
 * @Author: liwencai
 * @Date: 2022/10/14 15:59
 * @Description:
 */
@Slf4j
public class FileUtils {
    /**
     * 下载项目根目录下doc下的文件
     * @param response response
     * @param fileName 文件名
     * @return 返回结果 成功或者文件不存在
     */
    public static JsonResult downloadStaticExcelFile(HttpServletResponse response,String sourcePath,String fileName) {
        // sourcePath = "static/x"
        InputStream stream = FileUtils.class.getClassLoader().getResourceAsStream(sourcePath);
        /*InputStream stream = null;
        try {
            stream = new ClassPathResource("excel/" + fileName).getInputStream();
        } catch (IOException e) {
            e.printStackTrace();
        }*/
        if (stream == null) {
            log.error("文件没有找到");
        }
        //response.setHeader("content-type", "application/octet-stream");
        // response.setContentType("application/octet-stream");
        response.setHeader("content-Type", "application/vnd.ms-excel");
        try {
            String name = java.net.URLEncoder.encode(fileName, "UTF-8");
            response.setHeader("Content-Disposition", "attachment;filename=" + java.net.URLDecoder.decode(name, "UTF-8"));
        } catch (UnsupportedEncodingException e) {
            log.error("文件名转换编码出现问题:" + e.getMessage());
        }
        byte[] buff = new byte[1024];
        BufferedInputStream bis = null;
        OutputStream os = null;
        try {
            os = response.getOutputStream();
            bis = new BufferedInputStream(stream);
            int i = bis.read(buff);
            while (i != -1) {
                os.write(buff, 0, buff.length);
                os.flush();
                i = bis.read(buff);
            }
        } catch (FileNotFoundException e) {
            e.printStackTrace();
            log.error("系统找不到指定的文件:" + e.getMessage());
            return JsonResult.error("系统找不到指定的文件");
        } catch (IOException e) {
            e.printStackTrace();
            log.error("Io流写入出现错误:" + e.getMessage());
            return JsonResult.error("Io流写入出现错误");
        } finally {
            if (bis != null) {
                try {
                    bis.close();
                } catch (IOException e) {
                    e.printStackTrace();
                    log.error("关闭流出现错误:" + e.getMessage());
                    return JsonResult.error("关闭流出现错误");
                }
            }
        }
        return JsonResult.success();
    }

}
