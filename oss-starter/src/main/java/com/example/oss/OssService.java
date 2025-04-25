package com.example.oss;

import com.aliyun.oss.OSS;
import com.aliyun.oss.model.ListObjectsRequest;
import com.aliyun.oss.model.OSSObjectSummary;
import com.aliyun.oss.model.ObjectListing;
import com.aliyun.oss.model.ObjectMetadata;

import java.io.InputStream;
import java.util.List;
import java.util.Map;

public class OssService {
    //    bucket --> OSS
//
    private final Map<String, OSS> ossClients;
    private final OssProperties properties;

    public OssService(Map<String, OSS> ossClients, OssProperties properties) {
        this.ossClients = ossClients;
        this.properties = properties;
    }

    /**
     * Upload file to OSS
     *
     * @param bucket      bucket identifier
     * @param inputStream file input stream
     */
    public String upload(String bucket, InputStream inputStream) {
        OSS client = ossClients.get(bucket);
        String bucketName = properties.getBuckets().get(bucket).getBucketName();
        String key = null;
        try {
            key = ObjectNameUtil.chunkSha256(inputStream);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
        client.putObject(bucketName, key, inputStream);
        return key;
    }


    /**
     * Download file from OSS
     *
     * @param bucket     bucket identifier
     * @param objectName object name in OSS
     *
     * @return file input stream
     */
    public InputStream download(String bucket, String objectName) {
        OSS client = ossClients.get(bucket);
        String bucketName = properties.getBuckets().get(bucket).getBucketName();
        return client.getObject(bucketName, objectName).getObjectContent();
    }

    /**
     * Delete file from OSS
     *
     * @param bucket     bucket identifier
     * @param objectName object name in OSS
     */
    public void delete(String bucket, String objectName) {
        OSS client = ossClients.get(bucket);
        String bucketName = properties.getBuckets().get(bucket).getBucketName();
        client.deleteObject(bucketName, objectName);
    }

    /**
     * List files in OSS bucket
     *
     * @param bucket  bucket identifier
     * @param prefix  file prefix to filter
     * @param maxKeys maximum number of keys to return
     *
     * @return list of object summaries
     */
    public List<OSSObjectSummary> listObjects(String bucket, String prefix, int maxKeys) {
        OSS client = ossClients.get(bucket);
        String bucketName = properties.getBuckets().get(bucket).getBucketName();

        ListObjectsRequest request = new ListObjectsRequest(bucketName);
        request.setPrefix(prefix);
        request.setMaxKeys(maxKeys);

        ObjectListing objectListing = client.listObjects(request);
        return objectListing.getObjectSummaries();
    }

    /**
     * Check if file exists in OSS
     *
     * @param bucket     bucket identifier
     * @param objectName object name in OSS
     *
     * @return true if exists, false otherwise
     */
    public boolean doesObjectExist(String bucket, String objectName) {
        OSS client = ossClients.get(bucket);
        String bucketName = properties.getBuckets().get(bucket).getBucketName();
        return client.doesObjectExist(bucketName, objectName);
    }

    /**
     * Get file metadata from OSS
     *
     * @param bucket     bucket identifier
     * @param objectName object name in OSS
     *
     * @return object metadata
     */
    public ObjectMetadata getObjectMetadata(String bucket, String objectName) {
        OSS client = ossClients.get(bucket);
        String bucketName = properties.getBuckets().get(bucket).getBucketName();
        return client.getObjectMetadata(bucketName, objectName);
    }


}