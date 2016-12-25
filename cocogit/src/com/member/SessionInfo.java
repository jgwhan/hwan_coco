package com.member;

// 세션에 저장할 정보를 가진 클래스
public class SessionInfo {
	
	private String userId, userName;

	public String getUserId() {
		return userId;
	}

	public void setUserId(String userId) {
		this.userId = userId;
	}

	public String getUserName() {
		return userName;
	}

	public void setUserName(String userName) {
		this.userName = userName;
	}
	
}
