package com.maply.util;

public class Enumeration {

	public enum DeviceType {
		Ios, Android
	}

	public enum UserStatus {
		Live, Away
	}

	public enum MessageType {
		direct, sent
	}

	public enum UserRequest {
		None, Request, Block, Friend, Live, LiveRequest, Decline, Disconnect
	}

	public enum LiveRequestType {
		H24, H72, UntilDisconnect
	}

	public enum NotificationType {
		UMC, GO_ONLINE
	}

	public enum MediaType {
		IMAGE, VIDEO, GIF
	}
	public enum UserRole {
		USER, ADMIN
	}

}
