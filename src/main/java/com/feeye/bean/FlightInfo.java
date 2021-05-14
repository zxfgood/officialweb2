package com.feeye.bean;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;



public class FlightInfo {
	

	private long id;
	private String fuelTax;
	private String AirportTax;
	private String flightNo;

	private List<CabinInfo> cabins = new ArrayList<CabinInfo>();
	
	private List<Products> products = new ArrayList<Products>();

	public List<Products> getProducts() {
		return products;
	}
	public void setProducts(List<Products> products) {
		this.products = products;
	}
	//是否包含航意险
	private boolean hasInsurance;

	public boolean isHasInsurance() {
		return hasInsurance;
	}

	public void setHasInsurance(boolean hasInsurance) {
		this.hasInsurance = hasInsurance;
	}

	/**
	 * 新增2014-11-27中转仓位信息
	 */
	private String isTransfer;//是否中转    （1代表中转）
	private String Transferline;//中转航线
	
	private String deparutre;
	private String arrival;
	private String deparutreDate;
	private String deparutreTime;
	private String arriveDate;//用来存放到达日期
	private String arriveTime;
	/*
	 * 往返字段（以下）
	 */
	private String deparutreDate1;
	private String deparutreTime1;
	private String arriveDate1;//用来存放到达日期
	private String arriveTime1;
	/*
	 * 往返字段（以上）
	 */
	private String webType;
	//工作时间
	private String workTime;
	//航班周期
	private String flightCycle;
	
	private String ip;
	private Date updateTime;
	private int flightHash;
	private String planeType;
	private String shareairflightNoIncluding;
	/**
	 * 共享航班
	 */
	private String shareFlightNo;
	//请求口令
	private String token;
	//请求参数
	private String parames;

	private String term;
	private String snk;
	private String index;
	private String proxyIpPort;
	private String depStr;
	private String arrStr;
	private String sessionId;
	private String Referer;
	private String JSESSIONID;
	private String shoppingID;
	private String deparutreCN;
	private String arrivalCN;
	private String session;
	private String solutionSet;
	
	
	public String getDeparutreDate1() {
		return deparutreDate1;
	}
	public void setDeparutreDate1(String deparutreDate1) {
		this.deparutreDate1 = deparutreDate1;
	}
	public String getDeparutreTime1() {
		return deparutreTime1;
	}
	public void setDeparutreTime1(String deparutreTime1) {
		this.deparutreTime1 = deparutreTime1;
	}
	public String getArriveDate1() {
		return arriveDate1;
	}
	public void setArriveDate1(String arriveDate1) {
		this.arriveDate1 = arriveDate1;
	}
	public String getArriveTime1() {
		return arriveTime1;
	}
	public void setArriveTime1(String arriveTime1) {
		this.arriveTime1 = arriveTime1;
	}
	public String getWebType() {
		return webType;
	}
	public void setWebType(String webType) {
		this.webType = webType;
	}
	public long getId() {
		return id;
	}
	public void setId(long id) {
		this.id = id;
	}
	public String getFuelTax() {
		return fuelTax;
	}
	public void setFuelTax(String fuelTax) {
		this.fuelTax = fuelTax;
	}
	public String getAirportTax() {
		return AirportTax;
	}
	public void setAirportTax(String airportTax) {
		AirportTax = airportTax;
	}
	public List<CabinInfo> getCabins() {
		return cabins;
	}
	public void setCabins(List<CabinInfo> cabins) {
		this.cabins = cabins;
	}
	public String getDeparutre() {
		return deparutre;
	}
	public void setDeparutre(String deparutre) {
		this.deparutre = deparutre;
	}
	public String getArrival() {
		return arrival;
	}
	public void setArrival(String arrival) {
		this.arrival = arrival;
	}
	public String getDeparutreDate() {
		return deparutreDate;
	}
	public void setDeparutreDate(String deparutreDate) {
		this.deparutreDate = deparutreDate;
	}
	public String getDeparutreTime() {
		return deparutreTime;
	}
	public void setDeparutreTime(String deparutreTime) {
		this.deparutreTime = deparutreTime;
	}
	public Date getUpdateTime() {
		return updateTime;
	}
	public void setUpdateTime(Date updateTime) {
		this.updateTime = updateTime;
	}
	public String getFlightNo() {
		return flightNo;
	}
	public void setFlightNo(String flightNo) {
		this.flightNo = flightNo;
	}
	public String getArriveTime() {
		return arriveTime;
	}
	public void setArriveTime(String arriveTime) {
		this.arriveTime = arriveTime;
	}
	public int getFlightHash() {
		return flightHash;
	}
	public void setFlightHash(int flightHash) {
		this.flightHash = flightHash;
	}
	public String getPlaneType() {
		return planeType;
	}
	public void setPlaneType(String planeType) {
		this.planeType = planeType;
	}
	public String getShareFlightNo() {
		return shareFlightNo;
	}
	public void setShareFlightNo(String shareFlightNo) {
		this.shareFlightNo = shareFlightNo;
	}
	public String getIp() {
		return ip;
	}
	public void setIp(String ip) {
		this.ip = ip;
	}
	public String getWorkTime() {
		return workTime;
	}
	public void setWorkTime(String workTime) {
		this.workTime = workTime;
	}
	public String getFlightCycle() {
		return flightCycle;
	}
	public void setFlightCycle(String flightCycle) {
		this.flightCycle = flightCycle;
	}
	public String getShareairflightNoIncluding() {
		return shareairflightNoIncluding;
	}
	public void setShareairflightNoIncluding(String shareairflightNoIncluding) {
		this.shareairflightNoIncluding = shareairflightNoIncluding;
	}
	public String getIsTransfer() {
		return isTransfer;
	}
	public void setIsTransfer(String isTransfer) {
		this.isTransfer = isTransfer;
	}
	public String getTransferline() {
		return Transferline;
	}
	public void setTransferline(String transferline) {
		Transferline = transferline;
	}
	public String getToken() {
		return token;
	}
	public void setToken(String token) {
		this.token = token;
	}
	public String getParames() {
		return parames;
	}
	public void setParames(String parames) {
		this.parames = parames;
	}
	public String getArriveDate() {
		return arriveDate;
	}
	public void setArriveDate(String arriveDate) {
		this.arriveDate = arriveDate;
	}
	public String getTerm() {
		return term;
	}
	public void setTerm(String term) {
		this.term = term;
	}
	public String getSnk() {
		return snk;
	}
	public void setSnk(String snk) {
		this.snk = snk;
	}
	public String getIndex() {
		return index;
	}
	public void setIndex(String index) {
		this.index = index;
	}
	public String getProxyIpPort() {
		return proxyIpPort;
	}
	public void setProxyIpPort(String proxyIpPort) {
		this.proxyIpPort = proxyIpPort;
	}
	public String getDepStr() {
		return depStr;
	}
	public void setDepStr(String depStr) {
		this.depStr = depStr;
	}
	public String getArrStr() {
		return arrStr;
	}
	public void setArrStr(String arrStr) {
		this.arrStr = arrStr;
	}
	public String getSessionId() {
		return sessionId;
	}
	public void setSessionId(String sessionId) {
		this.sessionId = sessionId;
	}
	public String getReferer() {
		return Referer;
	}
	public void setReferer(String referer) {
		Referer = referer;
	}
	public String getJSESSIONID() {
		return JSESSIONID;
	}
	public void setJSESSIONID(String jSESSIONID) {
		JSESSIONID = jSESSIONID;
	}
	public String getShoppingID() {
		return shoppingID;
	}
	public void setShoppingID(String shoppingID) {
		this.shoppingID = shoppingID;
	}
	public String getDeparutreCN() {
		return deparutreCN;
	}
	public void setDeparutreCN(String deparutreCN) {
		this.deparutreCN = deparutreCN;
	}
	public String getArrivalCN() {
		return arrivalCN;
	}
	public void setArrivalCN(String arrivalCN) {
		this.arrivalCN = arrivalCN;
	}
	public String getSession() {
		return session;
	}
	public void setSession(String session) {
		this.session = session;
	}
	public String getSolutionSet() {
		return solutionSet;
	}
	public void setSolutionSet(String solutionSet) {
		this.solutionSet = solutionSet;
	}
	
	
}
