package pe.bbg.music.catalog.service;

import io.minio.GetObjectArgs;
import io.minio.ListObjectsArgs;
import io.minio.MinioClient;
import io.minio.Result;
import io.minio.messages.Item;
import io.minio.GetPresignedObjectUrlArgs;
import io.minio.http.Method;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.TimeUnit;

@Service
public class MinioService {

    private final MinioClient minioClient;
    private final String bucketName;

    public MinioService(
            @Value("${minio.url}") String url,
            @Value("${minio.access-key}") String accessKey,
            @Value("${minio.secret-key}") String secretKey,
            @Value("${minio.bucket-name}") String bucketName) {
        this.minioClient = MinioClient.builder()
                .endpoint(url)
                .credentials(accessKey, secretKey)
                .build();
        this.bucketName = bucketName;
    }

    public List<Item> listObjects() {
        List<Item> items = new ArrayList<>();
        Iterable<Result<Item>> results = minioClient.listObjects(
                ListObjectsArgs.builder()
                        .bucket(bucketName)
                        .recursive(true)
                        .build());
        
        for (Result<Item> result : results) {
            try {
                items.add(result.get());
            } catch (Exception e) {
                // Log error but continue
                e.printStackTrace();
            }
        }
        return items;
    }

    public InputStream getObject(String objectName) throws Exception {
        return minioClient.getObject(
                GetObjectArgs.builder()
                        .bucket(bucketName)
                        .object(objectName)
                        .build());
    }
    
    public String getObjectUrl(String objectName) throws Exception {
       // Ideally this should generate a presigned URL or just return the path for a proxy controller
       // For now, let's return the direct path assuming public access or internal use
       return objectName;
    }

    public String getPresignedUrl(String objectName) throws Exception {
        return minioClient.getPresignedObjectUrl(
                GetPresignedObjectUrlArgs.builder()
                        .method(Method.GET)
                        .bucket(bucketName)
                        .object(objectName)
                        .expiry(1, TimeUnit.HOURS)
                        .build());
    }
}