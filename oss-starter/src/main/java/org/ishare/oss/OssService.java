package org.ishare.oss;

import com.aliyun.oss.OSS;
import com.aliyun.oss.OSSClientBuilder;
import com.aliyun.oss.model.ListObjectsRequest;
import com.aliyun.oss.model.OSSObjectSummary;
import com.aliyun.oss.model.ObjectListing;
import com.aliyun.oss.model.ObjectMetadata;
import org.springframework.stereotype.Component;

import java.io.ByteArrayInputStream;
import java.io.InputStream;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Component
public class OssService {
    //    bucket --> OSS

    private final OssProperties properties;
    //
    private final Map<String, OSS> ossClients;

    public OssService(OssProperties properties) {
        this.properties = properties;
        Map<String, OSS> map = new HashMap<>();
        properties.getBuckets().forEach((name, config) -> {
            OSS ossClient = new OSSClientBuilder().build(
                    config.getEndpoint(),
                    config.getAccessKeyId(),
                    config.getAccessKeySecret()
            );
            map.put(name, ossClient);
        });
        this.ossClients = map;
    }

    /**
     * Upload file to OSS
     *
     * @param bucket      bucket identifier
     * @param inputStream file input stream
     */
    public String upload(String bucket, InputStream inputStream, String fileName) {
        OSS client = ossClients.get(bucket);
        String bucketName = properties.getBuckets().get(bucket).getBucketName();
        String key = null;

        try {
            byte[] bytes = inputStream.readAllBytes();
            ByteArrayInputStream byteArrayInputStream = new ByteArrayInputStream(bytes);
            key = ObjectNameUtil.chunkSha256(byteArrayInputStream);
            key += "_" + fileName;
            client.putObject(bucketName, key, new ByteArrayInputStream(bytes));
        } catch (Exception e) {
            throw new RuntimeException(e);
        }

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