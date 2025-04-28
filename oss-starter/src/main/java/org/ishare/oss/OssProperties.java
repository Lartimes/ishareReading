package org.ishare.oss;

import org.springframework.boot.context.properties.ConfigurationProperties;

import java.util.Map;

@ConfigurationProperties(prefix = "aliyun.oss")
public class OssProperties {
    private Map<String, BucketConfig> buckets;

    @Override
    public String toString() {
        return "OssProperties{" +
                "buckets=" + buckets +
                '}';
    }

    public Map<String, BucketConfig> getBuckets() {
        return buckets;
    }

    public void setBuckets(Map<String, BucketConfig> buckets) {
        this.buckets = buckets;
    }

    public static class BucketConfig {
        private String endpoint;
        private String accessKeyId;
        private String accessKeySecret;
        private String bucketName;

        @Override
        public String toString() {
            return "BucketConfig{" +
                    "endpoint='" + endpoint + '\'' +
                    ", accessKeyId='" + accessKeyId + '\'' +
                    ", accessKeySecret='" + accessKeySecret + '\'' +
                    ", bucketName='" + bucketName + '\'' +
                    '}';
        }

        public String getEndpoint() {
            return endpoint;
        }

        public void setEndpoint(String endpoint) {
            this.endpoint = endpoint;
        }

        public String getAccessKeyId() {
            return accessKeyId;
        }

        public void setAccessKeyId(String accessKeyId) {
            this.accessKeyId = accessKeyId;
        }

        public String getAccessKeySecret() {
            return accessKeySecret;
        }

        public void setAccessKeySecret(String accessKeySecret) {
            this.accessKeySecret = accessKeySecret;
        }

        public String getBucketName() {
            return bucketName;
        }

        public void setBucketName(String bucketName) {
            this.bucketName = bucketName;
        }
    }
}