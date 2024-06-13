package gifterz.textme.s3Proxy;

import com.amazonaws.services.s3.AmazonS3Client;
import com.amazonaws.services.s3.model.ObjectMetadata;
import gifterz.textme.global.utils.TSIDUtils;
import gifterz.textme.s3Proxy.exception.UploadFileException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.io.InputStream;

@Slf4j
@RequiredArgsConstructor
@Service
public class S3Service {

    @Value("${cloud.aws.s3.bucket}")
    private String bucket;
    private final AmazonS3Client amazonS3Client;

    public String upload(MultipartFile multipartFile) {
        String contentType = multipartFile.getContentType();
        FileUtils.checkContentType(contentType);

        String fileName = TSIDUtils.generateTSID();
        String fileFormatName = FileUtils.getFormatName(multipartFile);
        MultipartFile resizedFile = FileUtils.resizeFile(fileName, fileFormatName, multipartFile);
        FileUtils.checkFileSize(resizedFile);

        ObjectMetadata objMeta = new ObjectMetadata();
        objMeta.setContentLength(resizedFile.getSize());
        objMeta.setContentType(resizedFile.getContentType());
        try (InputStream inputStream = resizedFile.getInputStream()) {
            amazonS3Client.putObject(bucket, fileName, inputStream, objMeta);
        } catch (IOException e) {
            throw new UploadFileException();
        }
        return findFile(fileName);
    }

    public String findFile(String fileName) {
        return amazonS3Client.getUrl(bucket, fileName).toString();
    }


}
