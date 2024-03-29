package com.innotek.ifieldborad.database;

import java.util.Date;

public abstract class Message {
	
	
	private Long infoId;
	private int infoType;
	private String modifiedTime;
	private String overTime;
	private int playTimes;
	private String publishTime;
	private String publisher;
	private Long publisherOrgid;
	private Long publisherUserid;
	private Date startTime;

	/**
	 * 
	 */
	public abstract void startBroadcast();

	public Long getInfoId() {
		return infoId;
	}

	public void setInfoId(Long infoId) {
		this.infoId = infoId;
	}

	public int getInfoType() {
		return infoType;
	}

	public void setInfoType(int infoType) {
		this.infoType = infoType;
	}

	public String getModifiedTime() {
		return modifiedTime;
	}

	public void setModifiedTime(String modifiedTime) {
		this.modifiedTime = modifiedTime;
	}

	public String getOverTime() {
		return overTime;
	}

	public void setOverTime(String overTime) {
		this.overTime = overTime;
	}

	public int getPlayTimes() {
		return playTimes;
	}

	public void setPlayTimes(int playTimes) {
		this.playTimes = playTimes;
	}

	public String getPublishTime() {
		return publishTime;
	}

	public void setPublishTime(String publishTime) {
		this.publishTime = publishTime;
	}

	public String getPublisher() {
		return publisher;
	}

	public void setPublisher(String publisher) {
		this.publisher = publisher;
	}

	public Long getPublisherOrgid() {
		return publisherOrgid;
	}

	public void setPublisherOrgid(Long publisherOrgid) {
		this.publisherOrgid = publisherOrgid;
	}

	public Long getPublisherUserid() {
		return publisherUserid;
	}

	public void setPublisherUserid(Long publisherUserid) {
		this.publisherUserid = publisherUserid;
	}

	public Date getStartTime() {
		return startTime;
	}

	public void setStartTime(Date startTime) {
		this.startTime = startTime;
	}

	@Override
	public String toString() {
		return "Message{" +
				"infoId=" + infoId +
				", infoType=" + infoType +
				", modifiedTime=" + modifiedTime +
				", overTime=" + overTime +
				", playTimes=" + playTimes +
				", publishTime=" + publishTime +
				", publisher='" + publisher + '\'' +
				", publisherOrgid=" + publisherOrgid +
				", publisherUserid=" + publisherUserid +
				", startTime=" + startTime +
				'}';
	}
}
