package com.maply.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.amazonaws.services.sns.AmazonSNS;
import com.amazonaws.services.sns.model.CreatePlatformEndpointRequest;
import com.amazonaws.services.sns.model.CreatePlatformEndpointResult;
import com.amazonaws.services.sns.model.DeleteEndpointRequest;
import com.amazonaws.services.sns.model.PublishRequest;
import com.maply.Constant;
import com.maply.entity.Notification;
import com.maply.repository.NotificationRepository;
import com.maply.util.Enumeration.DeviceType;
import com.maply.util.Enumeration.NotificationType;

@Service
public class NotificationService {

	@Autowired
	private AmazonSNS amazonSNS;

	@Autowired
	private NotificationRepository notificationRepository;

	public String subscribe(String deviceToken, DeviceType deviceType) {
		CreatePlatformEndpointRequest createPlatformEndpointRequest = new CreatePlatformEndpointRequest();
		createPlatformEndpointRequest.setToken(deviceToken);
		if (deviceType.equals(DeviceType.Ios)) {
			createPlatformEndpointRequest.setPlatformApplicationArn(Constant.IOS_ARN);
			CreatePlatformEndpointResult result = amazonSNS.createPlatformEndpoint(createPlatformEndpointRequest);
			System.out.println("Arn : " + result.getEndpointArn());
			return result.getEndpointArn();
		}

		return "";
	}

	public void delete(String endpointArn) {
		DeleteEndpointRequest deleteEndpointRequest = new DeleteEndpointRequest();
		deleteEndpointRequest.setEndpointArn(endpointArn);
		amazonSNS.deleteEndpoint(deleteEndpointRequest);
	}

	public void sendIosNotification(String targetArn, String message, NotificationType type, String key, Long value,
			Long unReadCount, Long userId) {
		String sound = "";
		Notification notification = notificationRepository.findByUserId(userId);
		if (notification != null && notification.getSound() != null && notification.getSound()) {
			sound = "default";
		}

		try {
			PublishRequest publishRequest = new PublishRequest();
			publishRequest.setMessageStructure("json");
			message = String.format("{\"apn\": { \n"
					+ "    \"APNS\": \"{ \\\"aps\\\": { \\\"%s\\\" : \\\"%s\\\", \\\"type\\\" : \\\"%s\\\",  \\\"alert\\\":\\\"%s\\\", \\\"unread\\\":\\\"%s\\\", \\\"badge\\\": 1, \\\"sound\\\": \\\"%s\\\" }}\"\n"
					+ "}}", key, value, type.toString(), message, unReadCount, sound);
			publishRequest.setMessage(message);
			publishRequest.setTargetArn(targetArn);
			amazonSNS.publish(publishRequest);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

}
