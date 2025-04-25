package com.example.oss;

import java.io.InputStream;
import java.security.MessageDigest;
import java.util.ArrayList;
import java.util.HexFormat;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;

public class ObjectNameUtil {


    /**
     * 对输入流进行分片并发SHA-256摘要，并生成唯一key
     *
     * @param inputStream 输入流 5 分片大小（单位MB）
     * 一定是顺序性 + 幂等性 + 唯一性
     * @return 分片摘要列表，最后一个为全局唯一key
     */
    public static String chunkSha256(InputStream inputStream) throws Exception {
        int chunkSize = 5 * 1024 * 1024;
        List<byte[]> chunkList = new ArrayList<>();
        byte[] buffer = new byte[chunkSize];
        int len;
        while ((len = inputStream.read(buffer)) != -1) {
            byte[] chunk = new byte[len];
            System.arraycopy(buffer, 0, chunk, 0, len);
            chunkList.add(chunk);
        }

        ExecutorService executor = Executors.newVirtualThreadPerTaskExecutor();
        List<Future<String>> futures = new ArrayList<>();
        for (byte[] chunk : chunkList) {
            futures.add(executor.submit(() -> {
                MessageDigest md = MessageDigest.getInstance("SHA-256");
                md.update(chunk);
                return HexFormat.of().formatHex(md.digest());
            }));
        }

        List<String> chunkHashes = new ArrayList<>();
        for (Future<String> f : futures) {
            chunkHashes.add(f.get());
        }
        executor.shutdown();

        // 生成全局唯一key（将所有分片hash拼接后再做一次SHA-256）
        MessageDigest md = MessageDigest.getInstance("SHA-256");
        for (String hash : chunkHashes) {
            md.update(hash.getBytes());
        }
        String globalKey = HexFormat.of().formatHex(md.digest());
        executor.close();
        return globalKey;
    }
}
