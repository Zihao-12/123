package com.example.webapp.utils.uploadDownload;

import com.obs.services.ObsClient;
import com.obs.services.ObsConfiguration;
import com.obs.services.model.*;

import java.io.InputStream;

/**
 * @author wujun
 * @description
 * @date 2020/11/23
 */
public class HuaWeiObsUtil {
    private String endPoint;
    private String ak;
    private String sk;
    private String bucketName;
    private ObsClient obsClient;

    public HuaWeiObsUtil(String endPoint, String ak, String sk, String bucketName) {
        this.endPoint = endPoint;
        this.ak = ak;
        this.sk = sk;
        this.bucketName = bucketName;
        ObsConfiguration config = new ObsConfiguration();
        config.setSocketTimeout(25000);
        config.setConnectionTimeout(20000);
        config.setEndPoint(endPoint);
        obsClient = new ObsClient(ak, sk, config);
    }

    /**
     * 上传对象
     * PutObjectResult	putObject(PutObjectRequest request)
     * PutObjectResult	putObject(String bucketName, String objectKey, File file)
     * PutObjectResult	putObject(String bucketName, String objectKey, File file, ObjectMetadata metadata)
     * PutObjectResult	putObject(String bucketName, String objectKey, InputStream input)
     * PutObjectResult	putObject(String bucketName, String objectKey, InputStream input, ObjectMetadata metadata)
     * 批量上传文件
     * UploadProgressStatus	putObjects(PutObjectsRequest request)
     * @param objectKey
     * @param in
     * @return
     */
    public PutObjectResult putObject(String objectKey, InputStream in, ObjectMetadata metadata){
        PutObjectResult result = obsClient.putObject(bucketName, objectKey, in, metadata);
        obsClient.setObjectAcl(bucketName, objectKey, AccessControlList.REST_CANNED_PUBLIC_READ);
        return result;
    }

    /**
     * 上传对象
     * PutObjectResult	putObject(PutObjectRequest request)
     * PutObjectResult	putObject(String bucketName, String objectKey, File file)
     * PutObjectResult	putObject(String bucketName, String objectKey, File file, ObjectMetadata metadata)
     * PutObjectResult	putObject(String bucketName, String objectKey, InputStream input)
     * PutObjectResult	putObject(String bucketName, String objectKey, InputStream input, ObjectMetadata metadata)
     * 批量上传文件
     * UploadProgressStatus	putObjects(PutObjectsRequest request)
     * @param objectKey
     * @param in
     * @return
     */
    public PutObjectResult putObject(String objectKey, InputStream in){
        PutObjectResult result = obsClient.putObject(bucketName, objectKey, in);
        obsClient.setObjectAcl(bucketName, objectKey, AccessControlList.REST_CANNED_PUBLIC_READ);
        return result;
    }

    /**
     * 下载对象
     * ObsObject	getObject(GetObjectRequest request)
     * ObsObject	getObject(String bucketName, String objectKey)
     * ObsObject	getObject(String bucketName, String objectKey, String versionId)
     * @param objectKey
     * @return
     */
    public ObsObject getObject(String objectKey){
        ObsObject obsObject = obsClient.getObject(bucketName, objectKey);
        return obsObject;
    }

    /**
     * 删除对象
     * DeleteObjectResult	deleteObject(DeleteObjectRequest request)
     * DeleteObjectResult	deleteObject(String bucketName, String objectKey)
     * DeleteObjectResult	deleteObject(String bucketName, String objectKey, String versionId)
     * 批量删除对象
     * DeleteObjectsResult	deleteObjects(DeleteObjectsRequest deleteObjectsRequest)
     * @param objectKey
     * @return
     */
    public DeleteObjectResult deleteObject(String objectKey) {
        return obsClient.deleteObject(bucketName, objectKey);
    }

    /**
     * 复制对象(不同桶复制对象)
     * CopyObjectResult	copyObject(CopyObjectRequest request)
     * CopyObjectResult	copyObject(String sourceBucketName, String sourceObjectKey, String destBucketName, String destObjectKey)
     * @param sourceBucketName
     * @param sourceObjectKey
     * @param destBucketName
     * @param destObjectKey
     * @return
     */
    public CopyObjectResult copyObject(String sourceBucketName, String sourceObjectKey, String destBucketName, String destObjectKey) {
        CopyObjectResult result = obsClient.copyObject(sourceBucketName, sourceObjectKey, destBucketName, destObjectKey);
        return result;
    }

    /**
     * 复制对象(相同桶复制对象)
     * CopyObjectResult	copyObject(CopyObjectRequest request)
     * CopyObjectResult	copyObject(String sourceBucketName, String sourceObjectKey, String destBucketName, String destObjectKey)
     * @param sourceObjectKey
     * @param destObjectKey
     * @return
     */
    public CopyObjectResult copyObjectSameBucketName(String sourceObjectKey, String destObjectKey) {
        CopyObjectResult result = obsClient.copyObject(bucketName, sourceObjectKey, bucketName, destObjectKey);
        return result;
    }

}
