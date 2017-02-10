package com.maply.service;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

import com.amazonaws.services.s3.AmazonS3;
import com.amazonaws.services.s3.transfer.TransferManager;
import com.amazonaws.services.s3.transfer.Upload;
import com.maply.util.StringUtility;

@Service
public class MediaService {

	@Autowired
	private AmazonS3 amazonS3;

	@Async
	public String uploadImage(byte[] bytes, String filename, String oldFile, String bucket) throws IOException {
		File file = new File(filename);
		FileOutputStream fos = new FileOutputStream(file);
		fos.write(bytes);
		fos.close();

		TransferManager transferManager = new TransferManager(amazonS3);
		Upload upload = transferManager.upload(bucket, filename, file);
		if (!StringUtility.isNullOrEmpty(oldFile)) {
			amazonS3.deleteObject(bucket, oldFile);
		}

		while (!upload.isDone()) {
			try {
				Thread.sleep(3000);
			} catch (InterruptedException e) {
				break;
			}
		}

		file.delete();
		return filename;
	}
}
