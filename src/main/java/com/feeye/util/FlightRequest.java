package com.feeye.util;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

/**
 * @description: This is a class!
 * @author: domcj
 * @date: 2021/03/09 20:25
 */
public class FlightRequest {
	public static final String DIRECT_TYPE_DIRECT = "D";
	public static final String FLIGHT_TYPE_ROUND = "RT";
	public static final String FLIGHT_TYPE_SINGLE = "OW";
	public static final String TRIP_TYPE_DOMESTIC = "D";
	public static final String TRIP_TYPE_INTERNATIONAL = "I";
	public static final String CHANNEL = "MOBILE";
	public static final String CLIENT_VERSION = "6.3.0";
	public static final String PLATFORMINFO = "android";
	public static final int VERSIONCODE = 63000;

	@SerializedName("channelCode")
	@Expose
	public String channelCode = CHANNEL;
	@SerializedName("clientVersion")
	@Expose
	public String clientVersion = CLIENT_VERSION;

	@SerializedName("platformInfo")
	@Expose
	public String platformInfo = PLATFORMINFO;

	@SerializedName("arrAirportCode")
	@Expose
	public String arrAirportCode;
	@SerializedName("arrCode")
	@Expose
	public String arrCity;
	@SerializedName("blackBox")
	@Expose
	public String blackBox;
	@SerializedName("sendCode")
	@Expose
	public String depCity;
	@SerializedName("departureDate")
	@Expose
	public String departureDate;
	@SerializedName("directType")
	@Expose
	public String directType = "D";
	@SerializedName("ffpCardNo")
	@Expose
	public String ffpCardNo;
	@SerializedName("ffpId")
	@Expose
	public String ffpId;
	@SerializedName("flightType")
	@Expose
	public String flightType;
	@SerializedName("tripType")
	@Expose
	public String internationalFlag;
	@SerializedName("loginKeyInfo")
	@Expose
	public String loginKeyInfo;
	@SerializedName("returnDate")
	@Expose
	public String returnDate;
	@SerializedName("sendAirportCode")
	@Expose
	public String sendAirportCode;
	@SerializedName("sign")
	@Expose
	public String sign;

}
