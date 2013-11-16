package com.latebutlucky.beemote_controller;


public class ItemInfo {
	
	//화면
	public int screenIdx;
	
	//버튼 번호
	public int beemoteIdx;
	
	//타입
	public int beemoteType = BGlobal.BEEBUTTON_TYPE_NONE;
	
	//채널번호
	public String channelNo = "-";
	
	//어플관련
	public String appId = "-";
	public String appName = "-";
	public String contentId = "-";
	public byte[] appImg = null;
	
	//검색어
	public String keyWord="-";
	
	//기능키
	public String functionKey="-";
	
}
