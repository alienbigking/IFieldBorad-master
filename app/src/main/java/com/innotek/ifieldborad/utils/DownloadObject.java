package com.innotek.ifieldborad.utils;

public class DownloadObject {
	private int infoId;
	private String filename;
	
	public int getInfoId() {
		return infoId;
	}
	public void setInfoId(int infoId) {
		this.infoId = infoId;
	}
	public String getFilename() {
		return filename;
	}
	public void setFilename(String filename) {
		this.filename = filename;
	}
	
	public DownloadObject(int infoId, String filename){
		this.infoId = infoId;
		this.filename = filename;
	}
	
	public DownloadObject(){
		
	}
	
	

}
