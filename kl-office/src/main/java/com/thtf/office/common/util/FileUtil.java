package com.thtf.office.common.util;

import com.thtf.office.common.exception.AddException;
import io.minio.GetObjectArgs;
import io.minio.MinioClient;
import io.minio.PutObjectArgs;
import io.minio.RemoveObjectArgs;
import io.minio.errors.*;
import io.minio.messages.Bucket;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;
import org.springframework.util.Assert;
import org.springframework.util.CollectionUtils;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.util.List;

/**
 * @ClassName : FileUtil
 * @Description : 文件存储工具类
 * @Author : zhaosy
 * @Date: 2020-10-13 19:39
 */
@Slf4j
@Configuration
public class FileUtil {

    @Value("${minio.endpoint:http://localhost}")
    private String endpoint;
    @Value("${minio.port:9000}")
    private Integer port;
    @Value("${minio.accessKey:admin}")
    private String accessKey;
    @Value("${minio.secretKey:admin}")
    private String secretKey;
    @Value("${minio.bucketName:ibmsbucket}")
    private String bucketName;

    private MinioClient minioClient;

    @Autowired
    private FileUtil fileUtil;

    public MinioClient getInstance() {
        if (minioClient == null) {
            minioClient = MinioClient.builder().endpoint(endpoint, port, false).credentials(accessKey, secretKey).build();
        }
        return minioClient;
    }

    /**
     * @return java.util.List<io.minio.messages.Bucket>
     * @Description 获取minio所有的桶
     **/
    public List<Bucket> getAllBucket() throws IOException, InvalidKeyException, InvalidResponseException, InsufficientDataException, NoSuchAlgorithmException, ServerException, InternalException, XmlParserException, InvalidBucketNameException, ErrorResponseException {
        // 获取minio中所以的bucket
        List<Bucket> buckets = getInstance().listBuckets();

        for (Bucket bucket : buckets) {
            log.info("bucket 名称:  {}      bucket 创建时间: {}", bucket.name(), bucket.creationDate());
        }
        return buckets;
    }

    /**
     * @param inputStream      输入流
     * @param fileOriginalName 原始文件名
     * @Description 上传文件到存储桶内
     */
    public boolean uploadFileToMinio(InputStream inputStream, String fileOriginalName) throws IOException, InvalidKeyException, InvalidResponseException, InsufficientDataException, NoSuchAlgorithmException, ServerException, InternalException, XmlParserException, InvalidBucketNameException, ErrorResponseException {
        //根据文件扩展名，获得contentType
        String extensionName = fileOriginalName.substring(fileOriginalName.lastIndexOf('.'));
        String contentType = getContentType(extensionName);
        if(contentType == null){
            return false;
        }
        long size = inputStream.available();
        PutObjectArgs putObjectArgs = PutObjectArgs.builder()
                .bucket(bucketName)
                .object(fileOriginalName)
                .stream(inputStream, size, -1)
                .contentType(contentType)
                .build();
        // 上传到minio
        getInstance().putObject(putObjectArgs);
        inputStream.close();
        return true;
    }

    /**
     * 删除文件
     *
     * @param fileName 文件名
     * @return 是否删除成功
     * @author ligh
     * @date 2020-12-10
     **/
    public boolean removeFileFromMinio(String fileName) throws IOException, InvalidKeyException, InvalidResponseException, InsufficientDataException, NoSuchAlgorithmException, ServerException, InternalException, XmlParserException, InvalidBucketNameException, ErrorResponseException {
        RemoveObjectArgs removeObjectArgs = RemoveObjectArgs.builder().
                bucket(bucketName).
                object(fileName).build();
        getInstance().removeObject(removeObjectArgs);
        return true;
    }
    /**
     *
     * @Description 删除一个对象
     * @param objectName: 对象的名称
     * @return java.lang.String
     **/
    public void removeObject(String bucketName, String objectName){
        try {
            // 从mybucket中删除myobject。
            minioClient.removeObject(bucketName, objectName);
        } catch (Exception e) {
            log.error(e.getMessage());
            e.printStackTrace();
        }
    }

    /**
     * @param objectName: 对象的名称
     * @return java.lang.String
     * @Description 根据指定的objectName获取下载链接，需要bucket设置可下载的策略
     **/
    public String getUrlByObjectName(String objectName) throws IOException, InvalidKeyException, InvalidResponseException, InsufficientDataException, NoSuchAlgorithmException, ServerException, InternalException, XmlParserException, InvalidBucketNameException, ErrorResponseException {
        String objectUrl = null;
        objectUrl = getInstance().getObjectUrl(bucketName, objectName);
        return objectUrl;
    }


    /**
     * @param objectName: objectName
     * @param fileName:   文件名称
     * @param dir:        文件目录
     * @return void
     * @Description 根据objectName从minio中下载文件到指定的目录
     **/
    public void downloadImageFromMinioToFile(String objectName, String fileName, String dir) throws IOException, InvalidKeyException, InvalidResponseException, InsufficientDataException, NoSuchAlgorithmException, ServerException, InternalException, XmlParserException, InvalidBucketNameException, ErrorResponseException {
        GetObjectArgs objectArgs = GetObjectArgs.builder()
                .bucket(bucketName)
                .object(objectName)
                .build();
        File file = new File(dir);
        if (!file.exists()) {
            file.mkdirs();
        }

        try (InputStream inputStream = getInstance().getObject(objectArgs);
             FileOutputStream outputStream = new FileOutputStream(new File(dir, fileName.substring(fileName.lastIndexOf('/') + 1)))) {
            int length;
            byte[] buffer = new byte[1024];
            while ((length = inputStream.read(buffer)) != -1) {
                outputStream.write(buffer, 0, length);
            }
        }
    }

    /**
     * 根据后缀名判断文件的 ContentType
     *
     * @param fileNameExtension
     * @return getContentType
     */
    public String getContentType(String fileNameExtension) {
        if (StringUtils.equalsIgnoreCase(FileExtension.BMP.getValue(), fileNameExtension)) {
            return "image/bmp";
        }
        if (StringUtils.equalsIgnoreCase(FileExtension.GIF.getValue(), fileNameExtension)) {
            return "image/gif";
        }
        if (StringUtils.equalsIgnoreCase(FileExtension.JPEG.getValue(), fileNameExtension)
                || StringUtils.equalsIgnoreCase(FileExtension.PNG.getValue(), fileNameExtension)
                || StringUtils.equalsIgnoreCase(FileExtension.JPG.getValue(), fileNameExtension)) {
            return "image/jpeg";
        }
        if (StringUtils.equalsIgnoreCase(FileExtension.HTML.getValue(), fileNameExtension)) {
            return "text/html";
        }
        if (StringUtils.equalsIgnoreCase(FileExtension.TXT.getValue(), fileNameExtension)) {
            return "text/plain";
        }
        if (StringUtils.equalsIgnoreCase(FileExtension.MP4.getValue(), fileNameExtension)) {
            return "video/mp4";
        }
        if (StringUtils.equalsIgnoreCase(FileExtension.VSD.getValue(), fileNameExtension)) {
            return "application/vnd.visio";
        }
        if (StringUtils.equalsIgnoreCase(FileExtension.PPT.getValue(), fileNameExtension) || StringUtils.equalsIgnoreCase(FileExtension.PPTX.getValue(), fileNameExtension)) {
            return "application/vnd.ms-powerpoint";
        }
        if (StringUtils.equalsIgnoreCase(FileExtension.DOC.getValue(), fileNameExtension) || StringUtils.equalsIgnoreCase(FileExtension.DOCX.getValue(), fileNameExtension)) {
            return "application/msword";
        }
        if (StringUtils.equalsIgnoreCase(FileExtension.XML.getValue(), fileNameExtension)) {
            return "text/xml";
        }
        if (StringUtils.equalsIgnoreCase(FileExtension.PDF.getValue(), fileNameExtension)) {
            return "application/pdf";
        }
        return null;
    }

    public enum FileExtension {
        BMP(".BMP"),
        GIF(".GIF"),
        JPEG(".JPEG"),
        PNG(".PNG"),
        HTML(".HTML"),
        TXT(".TXT"),
        MP4(".MP4"),
        VSD(".VSD"),
        PPT(".PPT"),
        PPTX(".PPTX"),
        DOC(".DOC"),
        DOCX(".DOCX"),
        XML(".XML"),
        PDF(".PDF"),
        JPG(".JPG");

        private String value;

        FileExtension(String value) {
            this.value = value;
        }

        public String getValue(){
            return value;
        }
    }

    /**
     * 上传文件
     *
     * @param uploadFile 文件
     * @param fileName 文件名
     * @param fileUrl 文件url
     * @return
     * @author ligh
     * @date 2021-05-21
     */
    public void uploadFile(List<MultipartFile> uploadFile, StringBuilder fileName, StringBuilder fileUrl) throws Exception{
        if (!CollectionUtils.isEmpty(uploadFile)) {
            for (int i = 0; i < uploadFile.size(); i++) {
                MultipartFile file = uploadFile.get(i);
                String originalFilename = file.getOriginalFilename();
                Assert.notNull(originalFilename, "文件名不能为null");
                int index = originalFilename.lastIndexOf(".");
                Assert.isTrue(index > 0, "文件名必须包含.");
                String newFileName = System.currentTimeMillis() + originalFilename.substring(index);
                boolean flag = fileUtil.uploadFileToMinio(file.getInputStream(), newFileName);
                if (!flag) {
                    throw new AddException("上传文件失败!");
                }

                if(fileUrl != null){
                    fileUrl.append(fileUtil.getUrlByObjectName(newFileName));
                    if(i < uploadFile.size() - 1){
                        fileUrl.append(",");
                    }
                }

                if(fileName != null){
                    fileName.append(originalFilename);
                    if(i < uploadFile.size() - 1){
                        fileName.append(",");
                    }
                }
            }
        }


    }
}
