package com.feeye.bean;

/**
 * 查询参数对象
 * 
 * @ClassName: CrawlParam
 * @Description: TODO(这里用一句话描述这个类的作用)
 * @author xuyx
 * @date 2014-11-25 下午05:23:07
 */
public class CrawlParam {
	private String airline;
	private String departure; // 出发地
	private String arrival; // 到达地
	private String deparuteDate; // 出发时间
	private String arrivalDate; // 返回时间
	private String flightNo; // 航班号 多个用|分隔
	private String cabinCode; // 多个用|分隔
	private String flightWay;  //S 代表单程  D代表往返  (建议改为OW代表单程，RT代表往返)
	private String adtNum;    //成人数

	public CrawlParam(String departure, String arrival) {
		super();
		this.departure = departure;
		this.arrival = arrival;
	}

	public String getAirline() {
		return airline;
	}

	public void setAirline(String airline) {
		this.airline = airline;
	}

	public String getDeparture() {
		return departure;
	}

	public void setDeparture(String departure) {
		this.departure = departure;
	}

	public String getArrival() {
		return arrival;
	}

	public void setArrival(String arrival) {
		this.arrival = arrival;
	}

	public String getDeparuteDate() {
		return deparuteDate;
	}

	public void setDeparuteDate(String deparuteDate) {
		this.deparuteDate = deparuteDate;
	}

	public String getArrivalDate() {
		return arrivalDate;
	}

	public void setArrivalDate(String arrivalDate) {
		this.arrivalDate = arrivalDate;
	}

	public String getFlightNo() {
		return flightNo;
	}

	public void setFlightNo(String flightNo) {
		this.flightNo = flightNo;
	}

	public String getCabinCode() {
		return cabinCode;
	}

	public void setCabinCode(String cabinCode) {
		this.cabinCode = cabinCode;
	}
    
	public String getFlightWay() {
		return flightWay;
	}

	public void setFlightWay(String flightWay) {
		this.flightWay = flightWay;
	}

	public String getAdtNum() {
		return adtNum;
	}

	public void setAdtNum(String atuNum) {
		this.adtNum = atuNum;
	}

	@Override
	public String toString() {
		return "CrawlParam [airline=" + airline + ", departure=" + departure + ", arrival=" + arrival
				+ ", deparuteDate=" + deparuteDate + ", arrivalDate=" + arrivalDate + ", flightNo=" + flightNo
				+ ", cabinCode=" + cabinCode + ", flightWay=" + flightWay + ", atuNum=" + adtNum + "]";
	}

	public CrawlParam() {
		super();
	}

	public CrawlParam(String departure, String arrival, String deparuteDate) {
		super();
		this.departure = departure;
		this.arrival = arrival;
		this.deparuteDate = deparuteDate;
	}

}
