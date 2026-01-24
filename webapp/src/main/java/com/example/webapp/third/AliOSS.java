package com.example.webapp.third;


import com.aliyun.oss.ClientException;
import com.aliyun.oss.OSS;
import com.aliyun.oss.OSSException;
import com.aliyun.oss.model.*;
import com.aliyun.oss.OSSClient;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.exception.ExceptionUtils;
import org.springframework.web.multipart.MultipartFile;

import java.io.*;
import java.util.ArrayList;
import java.util.List;

@Slf4j
public class AliOSS {
    /**
     * 对返回的文件名称进行编码。编码类型目前仅支持url
     */
    public static final String ENDCODING_TYPE = "url";

    public static String getUrlDomain(){
        return "https://"+AccessKeyIdSecretEnum.ALI_OSS.getBucketName()+"."+AccessKeyIdSecretEnum.ALI_OSS.getEp()+"/";
    }
    /**
     * 上传简单对象
     * @param objectKey
     */
    public static void putObject(String objectKey, InputStream is){
        OSS ossClient = new OSSClientBuilder().build(AccessKeyIdSecretEnum.ALI_OSS.getEp(), AccessKeyIdSecretEnum.ALI_OSS.getAk(), AccessKeyIdSecretEnum.ALI_OSS.getAks());
        try{
            ossClient.putObject(AccessKeyIdSecretEnum.ALI_OSS.getBucketName(), objectKey, is);
        }finally {
            // 关闭OSSClient。
            ossClient.shutdown();
        }
    }

    /**
     * 分片上传对象
     * @param objectKey
     */
    public static void putObjectMultipart(String objectKey, MultipartFile file) throws IOException {
        OSS ossClient = new OSSClientBuilder().build(AccessKeyIdSecretEnum.ALI_OSS.getEp(), AccessKeyIdSecretEnum.ALI_OSS.getAk(), AccessKeyIdSecretEnum.ALI_OSS.getAks());
        try{
            // 创建InitiateMultipartUploadRequest对象。
            InitiateMultipartUploadRequest request = new InitiateMultipartUploadRequest(AccessKeyIdSecretEnum.ALI_OSS.getBucketName(), objectKey);
            // 初始化分片。
            InitiateMultipartUploadResult upresult = ossClient.initiateMultipartUpload(request);
            // 返回uploadId，它是分片上传事件的唯一标识，您可以根据这个uploadId发起相关的操作，如取消分片上传、查询分片上传等。
            String uploadId = upresult.getUploadId();
            // partETags是PartETag的集合。PartETag由分片的ETag和分片号组成。
            List<PartETag> partETags =  new ArrayList<PartETag>();
            InputStream in = file.getInputStream();
            byte[] dataBytes = getBytes(in);
            // 计算文件有多少个分片。
            // 5MB
            final long partSize = 5 * 1024 * 1024L;
            long fileLength = file.getSize();
            int partCount = (int) (fileLength / partSize);
            if (fileLength % partSize != 0) {
                partCount++;
            }
            // 遍历分片上传。
            for (int i = 0; i < partCount; i++) {
                long startPos = i * partSize;
                long curPartSize = (i + 1 == partCount) ? (fileLength - startPos) : partSize;
                InputStream instream = new ByteArrayInputStream(dataBytes);
                // 跳过已经上传的分片。
                instream.skip(startPos);
                UploadPartRequest uploadPartRequest = new UploadPartRequest();
                uploadPartRequest.setBucketName(AccessKeyIdSecretEnum.ALI_OSS.getBucketName());
                uploadPartRequest.setKey(objectKey);
                uploadPartRequest.setUploadId(uploadId);
                uploadPartRequest.setInputStream(instream);
                // 设置分片大小。除了最后一个分片没有大小限制，其他的分片最小为100 KB。
                uploadPartRequest.setPartSize(curPartSize);
                // 设置分片号。每一个上传的分片都有一个分片号，取值范围是1~10000，如果超出这个范围，OSS将返回InvalidArgument的错误码。
                uploadPartRequest.setPartNumber( i + 1);
                // 每个分片不需要按顺序上传，甚至可以在不同客户端上传，OSS会按照分片号排序组成完整的文件。
                UploadPartResult uploadPartResult = ossClient.uploadPart(uploadPartRequest);
                // 每次上传分片之后，OSS的返回结果包含PartETag。PartETag将被保存在partETags中。
                partETags.add(uploadPartResult.getPartETag());
            }
            // 创建CompleteMultipartUploadRequest对象。
            // 在执行完成分片上传操作时，需要提供所有有效的partETags。OSS收到提交的partETags后，会逐一验证每个分片的有效性。当所有的数据分片验证通过后，OSS将把这些分片组合成一个完整的文件。
            CompleteMultipartUploadRequest completeMultipartUploadRequest = new CompleteMultipartUploadRequest(AccessKeyIdSecretEnum.ALI_OSS.getBucketName(), objectKey, uploadId, partETags);
            // 如果需要在完成文件上传的同时设置文件访问权限
            completeMultipartUploadRequest.setObjectACL(CannedAccessControlList.PublicRead);
            // 完成上传。
            CompleteMultipartUploadResult completeMultipartUploadResult = ossClient.completeMultipartUpload(completeMultipartUploadRequest);
        }finally {
            // 关闭OSSClient。
            ossClient.shutdown();
        }
    }

    protected static byte[] getBytes(InputStream source) throws IOException {
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        byte[] buffer = new byte[1024];
        int len;
        while ((len = source.read(buffer)) > -1) {
            baos.write(buffer, 0, len);
        }
        baos.flush();
        return baos.toByteArray();
    }

    /**
     * 下载对象
     * @param objectKey
     * @return
     */
    public static InputStream getObject(String objectKey){
        OSS ossClient = new OSSClientBuilder().build(AccessKeyIdSecretEnum.ALI_OSS.getEp(), AccessKeyIdSecretEnum.ALI_OSS.getAk(), AccessKeyIdSecretEnum.ALI_OSS.getAks());
        OSSObject ossObject = ossClient.getObject(AccessKeyIdSecretEnum.ALI_OSS.getBucketName(), objectKey);
        InputStream inputStream = ossObject.getObjectContent();
        return inputStream;
    }

    /**
     * 删除对象
     * @param objectKey
     * @return
     */
    public static void deleteObject(String objectKey){
        OSS ossClient = new OSSClientBuilder().build(AccessKeyIdSecretEnum.ALI_OSS.getEp(), AccessKeyIdSecretEnum.ALI_OSS.getAk(), AccessKeyIdSecretEnum.ALI_OSS.getAks());
        ossClient.deleteObject(AccessKeyIdSecretEnum.ALI_OSS.getBucketName(), objectKey);
    }

    /**
     * 删除对象
     * @param objectKeys
     * @return
     */
    public static void deleteObjectByKeys(List<String> objectKeys){
        OSS ossClient = new OSSClientBuilder().build(AccessKeyIdSecretEnum.ALI_OSS.getEp(), AccessKeyIdSecretEnum.ALI_OSS.getAk(), AccessKeyIdSecretEnum.ALI_OSS.getAks());
        DeleteObjectsResult deleteObjectsResult = ossClient.deleteObjects(new DeleteObjectsRequest(AccessKeyIdSecretEnum.ALI_OSS.getBucketName()).withKeys(objectKeys).withEncodingType(ENDCODING_TYPE));
    }
    public static void main(String[] args) {

        // 日志配置，OSS Java SDK使用log4j记录错误信息。示例程序会在工程目录下生成“oss-demo.log”日志文件，默认日志级别是INFO。
        // 日志的配置文件是“conf/log4j.properties”，如果您不需要日志，可以没有日志配置文件和下面的日志配置。

        String firstKey = "my-first-key";
        // 生成OSSClient，您可以指定一些参数，详见“SDK手册 > Java-SDK > 初始化”，
        // 链接地址是：https://help.aliyun.com/document_detail/oss/sdk/java-sdk/init.html?spm=5176.docoss/sdk/java-sdk/get-start
        OSS ossClient = new OSSClientBuilder().build(AccessKeyIdSecretEnum.ALI_OSS.getEp(), AccessKeyIdSecretEnum.ALI_OSS.getAk(), AccessKeyIdSecretEnum.ALI_OSS.getAks());

        try {

            // 判断Bucket是否存在。详细请参看“SDK手册 > Java-SDK > 管理Bucket”。
            // 链接地址是：https://help.aliyun.com/document_detail/oss/sdk/java-sdk/manage_bucket.html?spm=5176.docoss/sdk/java-sdk/init
            if (ossClient.doesBucketExist(AccessKeyIdSecretEnum.ALI_OSS.getBucketName())) {
                System.out.println("您已经创建Bucket：" + AccessKeyIdSecretEnum.ALI_OSS.getBucketName() + "。");
            } else {
                System.out.println("您的Bucket不存在，创建Bucket：" + AccessKeyIdSecretEnum.ALI_OSS.getBucketName() + "。");
                // 创建Bucket。详细请参看“SDK手册 > Java-SDK > 管理Bucket”。
                // 链接地址是：https://help.aliyun.com/document_detail/oss/sdk/java-sdk/manage_bucket.html?spm=5176.docoss/sdk/java-sdk/init
                ossClient.createBucket(AccessKeyIdSecretEnum.ALI_OSS.getBucketName());
            }

            // 查看Bucket信息。详细请参看“SDK手册 > Java-SDK > 管理Bucket”。
            // 链接地址是：https://help.aliyun.com/document_detail/oss/sdk/java-sdk/manage_bucket.html?spm=5176.docoss/sdk/java-sdk/init
            BucketInfo info = ossClient.getBucketInfo(AccessKeyIdSecretEnum.ALI_OSS.getBucketName());
            System.out.println("Bucket " + AccessKeyIdSecretEnum.ALI_OSS.getBucketName() + "的信息如下：");
            System.out.println("\t数据中心：" + info.getBucket().getLocation());
            System.out.println("\t创建时间：" + info.getBucket().getCreationDate());
            System.out.println("\t用户标志：" + info.getBucket().getOwner());

            // 把字符串存入OSS，Object的名称为firstKey。详细请参看“SDK手册 > Java-SDK > 上传文件”。
            // 链接地址是：https://help.aliyun.com/document_detail/oss/sdk/java-sdk/upload_object.html?spm=5176.docoss/user_guide/upload_object
            InputStream is = new ByteArrayInputStream("Hello OSS".getBytes());
            ossClient.putObject(AccessKeyIdSecretEnum.ALI_OSS.getBucketName(), firstKey, is);
            System.out.println("Object：" + firstKey + "存入OSS成功。");

            // 下载文件。详细请参看“SDK手册 > Java-SDK > 下载文件”。
            // 链接地址是：https://help.aliyun.com/document_detail/oss/sdk/java-sdk/download_object.html?spm=5176.docoss/sdk/java-sdk/manage_object
            OSSObject ossObject = ossClient.getObject(AccessKeyIdSecretEnum.ALI_OSS.getBucketName(), firstKey);
            InputStream inputStream = ossObject.getObjectContent();
            StringBuilder objectContent = new StringBuilder();
            BufferedReader reader = new BufferedReader(new InputStreamReader(inputStream));
            while (true) {
                String line = reader.readLine();
                if (line == null){
                    break;
                }
                objectContent.append(line);
            }
            inputStream.close();
            System.out.println("Object：" + firstKey + "的内容是：" + objectContent);

            // 文件存储入OSS，Object的名称为fileKey。详细请参看“SDK手册 > Java-SDK > 上传文件”。
            // 链接地址是：https://help.aliyun.com/document_detail/oss/sdk/java-sdk/upload_object.html?spm=5176.docoss/user_guide/upload_object
            String fileKey = "README.md";
            ossClient.putObject(AccessKeyIdSecretEnum.ALI_OSS.getBucketName(), fileKey, new File("README.md"));
            System.out.println("Object：" + fileKey + "存入OSS成功。");

            // 查看Bucket中的Object。详细请参看“SDK手册 > Java-SDK > 管理文件”。
            // 链接地址是：https://help.aliyun.com/document_detail/oss/sdk/java-sdk/manage_object.html?spm=5176.docoss/sdk/java-sdk/manage_bucket
            ObjectListing objectListing = ossClient.listObjects(AccessKeyIdSecretEnum.ALI_OSS.getBucketName());
            List<OSSObjectSummary> objectSummary = objectListing.getObjectSummaries();
            System.out.println("您有以下Object：");
            for (OSSObjectSummary object : objectSummary) {
                System.out.println("\t" + object.getKey());
            }
            // 删除Object。详细请参看“SDK手册 > Java-SDK > 管理文件”。
            // 链接地址是：https://help.aliyun.com/document_detail/oss/sdk/java-sdk/manage_object.html?spm=5176.docoss/sdk/java-sdk/manage_bucket
            ossClient.deleteObject(AccessKeyIdSecretEnum.ALI_OSS.getBucketName(), firstKey);
            System.out.println("删除Object：" + firstKey + "成功。");
            ossClient.deleteObject(AccessKeyIdSecretEnum.ALI_OSS.getBucketName(), fileKey);
            System.out.println("删除Object：" + fileKey + "成功。");
        } catch (OSSException oe) {
            oe.printStackTrace();
        } catch (ClientException ce) {
            ce.printStackTrace();
        } catch (Exception e) {
            log.error("{}", ExceptionUtils.getStackTrace(e));
        } finally {
            ossClient.shutdown();
        }

    }
}
