package gifterz.textme.s3Proxy;

import com.amazonaws.services.s3.AmazonS3Client;
import com.amazonaws.services.s3.model.ObjectMetadata;
import gifterz.textme.s3Proxy.exception.InvalidFileContentException;
import gifterz.textme.s3Proxy.exception.InvalidFileImage;
import gifterz.textme.s3Proxy.exception.failFileResize;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import marvin.image.MarvinImage;
import org.apache.commons.lang3.ObjectUtils;
import org.marvinproject.image.transform.scale.Scale;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.server.ResponseStatusException;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.UUID;

@Slf4j
@RequiredArgsConstructor
@Service
public class S3Service {

    @Value("${cloud.aws.s3.bucket}")
    private String bucket;

    private final AmazonS3Client amazonS3Client;

    public String upload(MultipartFile multipartFile) {
        String contentType = multipartFile.getContentType();
        String s3FileName = UUID.randomUUID() + "-" + multipartFile.getOriginalFilename();
        String fileFormatName = multipartFile.getContentType()
                .substring(multipartFile.getContentType().lastIndexOf("/") + 1);
        FileUtils.checkContentType(contentType);

        MultipartFile resizedFile = FileUtils.resizeFile(fileName, fileFormatName, multipartFile);
        FileUtils.checkFileSize(resizedFile);

        ObjectMetadata objMeta = new ObjectMetadata();
        objMeta.setContentLength(resizedFile.getSize());
        objMeta.setContentType(resizedFile.getContentType());

        try (InputStream inputStream = resizedFile.getInputStream()) {
            amazonS3Client.putObject(bucket, s3FileName, inputStream, objMeta);
        } catch (IOException e) {
            throw new UploadFileException();
        }
        return findFile(s3FileName);
    }

    public String findFile(String fileName) {
        return amazonS3Client.getUrl(bucket, fileName).toString();
    }


}
