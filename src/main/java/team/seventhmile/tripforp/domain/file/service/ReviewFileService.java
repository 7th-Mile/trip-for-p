package team.seventhmile.tripforp.domain.file.service;

import com.amazonaws.services.s3.AmazonS3;
import com.amazonaws.services.s3.model.ObjectMetadata;
import com.amazonaws.services.s3.model.PutObjectRequest;
import java.io.IOException;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import team.seventhmile.tripforp.domain.file.entity.ReviewFile;

@Service
@RequiredArgsConstructor
public class ReviewFileService {

	private final AmazonS3 s3Client;
	@Value("${cloud.aws.s3.bucket}")
	private String bucket;

	public ReviewFile saveFile(MultipartFile file) throws IOException {
		String fileName = generateFileName(file);
		ObjectMetadata metaData = new ObjectMetadata();
		metaData.setContentType(file.getContentType());
		metaData.setContentLength(file.getSize());

		s3Client.putObject(
			new PutObjectRequest(bucket, fileName, file.getInputStream(), metaData));
		String url = s3Client.getUrl(bucket, fileName).toString();


		return ReviewFile.builder()
			.fileName(fileName)
			.url(url)
			.build();
	}

	public ReviewFile updateFile(String oldFileName, MultipartFile newFile) throws IOException {
		if (!fileExists(oldFileName)) {
			throw new IllegalArgumentException("File does not exist: " + oldFileName);
		}
		deleteFile(oldFileName);

		return saveFile(newFile);
	}

	public void deleteFile(String fileName) {
		if (fileExists(fileName)) {
			s3Client.deleteObject(bucket, fileName);
		} else {
			throw new IllegalArgumentException("File does not exist: " + fileName);
		}
	}

	private String generateFileName(MultipartFile file) {
		return UUID.randomUUID().toString() + "-" + file.getOriginalFilename();
	}

	public boolean fileExists(String fileName) {
		return s3Client.doesObjectExist(bucket, fileName);
	}
}
