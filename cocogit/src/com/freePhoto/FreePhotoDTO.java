package com.freePhoto;

import java.sql.Date;

public class FreePhotoDTO {
private int num, listNum;
private int hitCount;
private Date birth;
private String userId, userName;
private String subject, content, imageFilename;
private String created;



public Date getBirth() {
	return birth;
}
public void setBirth(Date birth) {
	this.birth = birth;
}
public int getNum() {
	return num;
}
public void setNum(int num) {
	this.num = num;
}
public int getListNum() {
	return listNum;
}
public void setListNum(int listNum) {
	this.listNum = listNum;
}
public int getHitCount() {
	return hitCount;
}
public void setHitCount(int hitCount) {
	this.hitCount = hitCount;
}
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
public String getSubject() {
	return subject;
}
public void setSubject(String subject) {
	this.subject = subject;
}
public String getContent() {
	return content;
}
public void setContent(String content) {
	this.content = content;
}
public String getImageFilename() {
	return imageFilename;
}
public void setImageFilename(String imageFilename) {
	this.imageFilename = imageFilename;
}
public String getCreated() {
	return created;
}
public void setCreated(String created) {
	this.created = created;
}

}
