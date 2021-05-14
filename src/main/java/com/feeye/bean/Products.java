package com.feeye.bean;

import java.util.List;

public class Products { 
	private String code;    //类别
	private String name;  //名称
	private List<CabinInfo> cabinInfoList;
	public List<CabinInfo> getCabinInfoList() {
		return cabinInfoList;
	}
	public void setCabinInfoList(List<CabinInfo> cabinInfoList) {
		this.cabinInfoList = cabinInfoList;
	}
	public String getCode() {
		return code;
	}
	public void setCode(String code) {
		this.code = code;
	}
	public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name;
	}
	
	

}
