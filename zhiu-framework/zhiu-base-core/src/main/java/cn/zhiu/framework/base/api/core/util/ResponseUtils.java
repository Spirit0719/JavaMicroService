package cn.zhiu.framework.base.api.core.util;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.servlet.ServletOutputStream;
import javax.servlet.http.HttpServletResponse;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.URLEncoder;

public class ResponseUtils {

    protected static Logger logger = LoggerFactory.getLogger(ResponseUtils.class);


    /**
     * 写入文件流进行下载文件
     *
     * @param response the response
     * @param file     the file
     * @param fileName the file name
     *
     * @throws IOException the io exception
     * @author zhuzz
     * @time 2019 /12/27 14:26:41
     */
    public static void writeStreamForDownload(HttpServletResponse response, File file, String fileName) throws IOException {

        InputStream in = new FileInputStream(file);
        ServletOutputStream outputStream = response.getOutputStream();

        response.setCharacterEncoding("UTF-8");
        response.setContentType("application/octet-stream");
        response.addHeader("Content-Length", "" + file.length());

        response.setHeader("Content-Disposition", "attachment;filename=" + URLEncoder.encode(fileName, "UTF-8").replaceAll("\\+", "%20"));

        try {
            byte[] buffer = new byte[1024 * 1024];
            int len;
            // 获得输出流
            while ((len = in.read(buffer)) > 0) {
                outputStream.write(buffer, 0, len);
            }
        } catch (Exception ex) {
            logger.error("download error {}", ex.getMessage());
        } finally {
            in.close();
            outputStream.flush();
            outputStream.close();
        }
    }

    /**
     * 写入自定义字节流进行文件下载
     *
     * @param response the response
     * @param bytes    the bytes
     * @param fileName the file name
     *
     * @throws IOException the io exception
     * @author zhuzz
     * @time 2019 /12/27 14:47:22
     */
    public static void writeStreamForDownload(HttpServletResponse response, byte[] bytes, String fileName) throws IOException {

        ServletOutputStream outputStream = response.getOutputStream();

        response.setCharacterEncoding("UTF-8");
        response.setContentType("application/octet-stream");
        response.addHeader("Content-Length", "" + bytes.length);

        response.setHeader("Content-Disposition", "attachment;filename=" + URLEncoder.encode(fileName, "UTF-8").replaceAll("\\+", "%20"));

        try {
            outputStream.write(bytes);
        } catch (Exception ex) {
            logger.error("download error {}", ex.getMessage());
        } finally {
            outputStream.flush();
            outputStream.close();
        }
    }

}
