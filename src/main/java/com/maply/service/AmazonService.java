package com.maply.service;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import com.amazonaws.auth.AWSCredentials;
import com.amazonaws.auth.BasicAWSCredentials;
import com.amazonaws.regions.Regions;
import com.amazonaws.services.s3.AmazonS3;
import com.amazonaws.services.s3.AmazonS3Client;
import com.amazonaws.services.sns.AmazonSNS;
import com.amazonaws.services.sns.AmazonSNSClient;

@Configuration
public class AmazonService {

	@Value("${cloud.aws.accessKey}")
	private String accessKey;

	@Value("${cloud.aws.secretKey}")
	private String secretKey;

	private AWSCredentials awsCredentials() {
		return new BasicAWSCredentials(accessKey, secretKey);
	}

	@Bean
	public AmazonS3 amazonS3() {
		AmazonS3 amazonS3 = new AmazonS3Client(awsCredentials());
		return amazonS3;
	}

	@Bean
	public AmazonSNS amazonSNS() {
		AmazonSNS amazonSns = new AmazonSNSClient(awsCredentials());
		amazonSns.setRegion(com.amazonaws.regions.Region.getRegion(Regions.fromName("ap-southeast-1")));
		return amazonSns;
	}
}