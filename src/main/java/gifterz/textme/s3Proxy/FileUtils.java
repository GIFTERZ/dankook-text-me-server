package gifterz.textme.s3Proxy;

import gifterz.textme.s3Proxy.exception.InvalidFileException;
import gifterz.textme.s3Proxy.exception.ResizeFileException;
import marvin.image.MarvinImage;
import org.marvinproject.image.transform.scale.Scale;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.stereotype.Component;
import org.springframework.web.multipart.MultipartFile;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.ByteArrayOutputStream;
import java.io.IOException;

@Component
public class FileUtils {

    public static MultipartFile resizeFile(String fileName, String fileFormatName, MultipartFile originalFile) {
        int targetWidth = 700;
        try {
            BufferedImage image = ImageIO.read(originalFile.getInputStream());
            if (image == null) {
                throw new InvalidFileException();
            }
            int originWidth = image.getWidth();
            int originHeight = image.getHeight();

            if (originWidth < targetWidth) {
                return originalFile;
            }

            MarvinImage imageMarvin = new MarvinImage(image);

            Scale scale = new Scale();
            scale.load();
            scale.setAttribute("newWidth", targetWidth);
            scale.setAttribute("newHeight", targetWidth * originHeight / originWidth);
            scale.process(imageMarvin.clone(), imageMarvin, null, null, false);

            BufferedImage imageNoAlpha = imageMarvin.getBufferedImageNoAlpha();
            ByteArrayOutputStream baos = new ByteArrayOutputStream();
            ImageIO.write(imageNoAlpha, fileFormatName, baos);
            baos.flush();

            return new MockMultipartFile(fileName, baos.toByteArray());

        } catch (IOException e) {
            throw new ResizeFileException();
        }
    }


}
