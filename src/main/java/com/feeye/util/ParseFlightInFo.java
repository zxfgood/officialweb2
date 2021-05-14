package com.feeye.util;

import net.sf.json.JSONObject;
import org.apache.commons.lang3.StringUtils;
import org.json.JSONArray;
import org.json.JSONException;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.select.Elements;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.util.*;
import java.util.Map.Entry;

public class ParseFlightInFo {
//	public static final String flights = "{\'resultCode\':\'00\',\'title\':null,\'note\':null,\'nextJsonToken\':\'PftpulvqG+Tq3QwZ+3ZyMxIK.mfb2cServer3\',\'orgCity\':\'XMN\',\'orgAirport\':\'厦门高崎机场\',\'dstCity\':\'CGO\',\'dstAirport\':\'郑州新郑机场\',\'takeoffDate\':\'2018-04-05\',\'returnDate\':null,\'tripType\':0,\'lowPrice1\':\'580\',\'lowPrice2\':null,\'flightInfos1\':[{\'airline\':\'MF\',\'carrier\':null,\'fltNo\':\'8275\',\'org\':\'XMN\',\'dst\':\'CGO\',\'takeoffTime\':\'2018-04-05 07:25\',\'arrivalTime\':\'2018-04-05 09:30\',\'equipment\':\'738\',\'meal\':1,\'eTicket\':true,\'codeShare\':false,\'stop\':0,\'cBrand\':{\'id\':\'7\',\'brandName\':\'特价专享\',\'fltNO\':\'8275\',\'cabin\':\'R\',\'price\':\'580\',\'childPrice\':\'680\',\'seats\':\'A\',\'csId\':null,\'mCard\':0,\'coupon\':0,\'rcRemark\':null,\'candidateId\':null,\'isSaleCabin\':0,\'hideCabin\':false,\'dsName\':null,\'dsCount\':null,\'dsSelfCount\':null,\'dsManyPeopleCount\':null,\'psgrNumMin\':0,\'baseHalfPrice\':\'680\',\'hasAOE\':false,\'firstClass\':false,\'candidate\':false,\'highEndOrder\':false,\'codeShareFlight\':false},\'fBrand\':null,\'iBrand\':{\'id\':\'2\',\'brandName\':\'商务舱优惠\',\'fltNO\':\'8275\',\'cabin\':\'I\',\'price\':\'1360\',\'childPrice\':\'1700\',\'seats\':\'2\',\'csId\':null,\'mCard\':2446,\'coupon\':0,\'rcRemark\':null,\'candidateId\':null,\'isSaleCabin\':0,\'hideCabin\':false,\'dsName\':null,\'dsCount\':null,\'dsSelfCount\':null,\'dsManyPeopleCount\':null,\'psgrNumMin\':0,\'baseHalfPrice\':\'1700\',\'hasAOE\':false,\'firstClass\':false,\'candidate\':false,\'highEndOrder\':false,\'codeShareFlight\':true},\'yBrand\':{\'id\':\'10\',\'brandName\':\'经济舱全价\',\'fltNO\':\'8275\',\'cabin\':\'Y\',\'price\':\'1360\',\'childPrice\':\'680\',\'seats\':\'A\',\'csId\':null,\'mCard\':2446,\'coupon\':0,\'rcRemark\':null,\'candidateId\':null,\'isSaleCabin\':0,\'hideCabin\':false,\'dsName\':null,\'dsCount\':null,\'dsSelfCount\':null,\'dsManyPeopleCount\':null,\'psgrNumMin\':0,\'baseHalfPrice\':\'680\',\'hasAOE\':false,\'firstClass\':false,\'candidate\':false,\'highEndOrder\':false,\'codeShareFlight\':true},\'airportTax\':[50.0,0.0],\'fuelTax\':[0.0,0.0],\'term\':\'T3\',\'arrTerm\':\'T2\',\'mutiSegPromotion\':{},\'state\':1,\'termLst\':[{\'dep\':\'T3\',\'arr\':\'T2\'}],\'classes\':[{\'name\':\'J\',\'av\':\'7\',\'subClass\':[]},{\'name\':\'C\',\'av\':\'2\',\'subClass\':[]},{\'name\':\'D\',\'av\':\'2\',\'subClass\':[]},{\'name\':\'I\',\'av\':\'2\',\'subClass\':[]},{\'name\':\'O\',\'av\':\'S\',\'subClass\':[]},{\'name\':\'Y\',\'av\':\'A\',\'subClass\':[]},{\'name\':\'H\',\'av\':\'A\',\'subClass\':[]},{\'name\':\'B\',\'av\':\'A\',\'subClass\':[]},{\'name\':\'M\',\'av\':\'A\',\'subClass\':[]},{\'name\':\'L\',\'av\':\'A\',\'subClass\':[]},{\'name\':\'K\',\'av\':\'A\',\'subClass\':[]},{\'name\':\'N\',\'av\':\'A\',\'subClass\':[]},{\'name\':\'Q\',\'av\':\'A\',\'subClass\':[]},{\'name\':\'V\',\'av\':\'A\',\'subClass\':[]},{\'name\':\'T\',\'av\':\'A\',\'subClass\':[]},{\'name\':\'R\',\'av\':\'A\',\'subClass\':[]},{\'name\':\'U\',\'av\':\'S\',\'subClass\':[]},{\'name\':\'G\',\'av\':\'S\',\'subClass\':[]},{\'name\':\'S\',\'av\':\'7\',\'subClass\':[]},{\'name\':\'Z\',\'av\':\'S\',\'subClass\':[]},{\'name\':\'X\',\'av\':\'S\',\'subClass\':[]},{\'name\':\'E\',\'av\':\'S\',\'subClass\':[]}],\'codeShareList\':null,\'arriveAd\':null,\'skPrice\':null,\'dsCount\':null,\'dsSelfCount\':null,\'dsManyPeopleCount\':null,\'aoeStr\':\'OE\',\'asr\':true},{\'airline\':\'MF\',\'carrier\':null,\'fltNo\':\'8335\',\'org\':\'XMN\',\'dst\':\'CGO\',\'takeoffTime\':\'2018-04-05 10:30\',\'arrivalTime\':\'2018-04-05 12:45\',\'equipment\':\'738\',\'meal\':2,\'eTicket\':true,\'codeShare\':false,\'stop\':0,\'cBrand\':{\'id\':\'7\',\'brandName\':\'特价专享\',\'fltNO\':\'8335\',\'cabin\':\'R\',\'price\':\'580\',\'childPrice\':\'680\',\'seats\':\'A\',\'csId\':null,\'mCard\':0,\'coupon\':0,\'rcRemark\':null,\'candidateId\':null,\'isSaleCabin\':0,\'hideCabin\':false,\'dsName\':null,\'dsCount\':null,\'dsSelfCount\':null,\'dsManyPeopleCount\':null,\'psgrNumMin\':0,\'baseHalfPrice\':\'680\',\'hasAOE\':false,\'firstClass\':false,\'candidate\':false,\'highEndOrder\':false,\'codeShareFlight\':false},\'fBrand\':null,\'iBrand\':null,\'yBrand\':{\'id\':\'10\',\'brandName\':\'经济舱全价\',\'fltNO\':\'8335\',\'cabin\':\'Y\',\'price\':\'1360\',\'childPrice\':\'680\',\'seats\':\'A\',\'csId\':null,\'mCard\':2446,\'coupon\':0,\'rcRemark\':null,\'candidateId\':null,\'isSaleCabin\':0,\'hideCabin\':false,\'dsName\':null,\'dsCount\':null,\'dsSelfCount\':null,\'dsManyPeopleCount\':null,\'psgrNumMin\':0,\'baseHalfPrice\':\'680\',\'hasAOE\':false,\'firstClass\':false,\'candidate\':false,\'highEndOrder\':false,\'codeShareFlight\':true},\'airportTax\':[50.0,0.0],\'fuelTax\':[0.0,0.0],\'term\':\'T3\',\'arrTerm\':\'T2\',\'mutiSegPromotion\':{},\'state\':1,\'termLst\':[{\'dep\':\'T3\',\'arr\':\'T2\'}],\'classes\':[{\'name\':\'J\',\'av\':\'6\',\'subClass\':[]},{\'name\':\'C\',\'av\':\'2\',\'subClass\':[]},{\'name\':\'D\',\'av\':\'2\',\'subClass\':[]},{\'name\':\'I\',\'av\':\'S\',\'subClass\':[]},{\'name\':\'O\',\'av\':\'S\',\'subClass\':[]},{\'name\':\'Y\',\'av\':\'A\',\'subClass\':[]},{\'name\':\'H\',\'av\':\'A\',\'subClass\':[]},{\'name\':\'B\',\'av\':\'A\',\'subClass\':[]},{\'name\':\'M\',\'av\':\'A\',\'subClass\':[]},{\'name\':\'L\',\'av\':\'A\',\'subClass\':[]},{\'name\':\'K\',\'av\':\'A\',\'subClass\':[]},{\'name\':\'N\',\'av\':\'A\',\'subClass\':[]},{\'name\':\'Q\',\'av\':\'A\',\'subClass\':[]},{\'name\':\'V\',\'av\':\'A\',\'subClass\':[]},{\'name\':\'T\',\'av\':\'A\',\'subClass\':[]},{\'name\':\'R\',\'av\':\'A\',\'subClass\':[]},{\'name\':\'U\',\'av\':\'S\',\'subClass\':[]},{\'name\':\'G\',\'av\':\'S\',\'subClass\':[]},{\'name\':\'S\',\'av\':\'5\',\'subClass\':[]},{\'name\':\'Z\',\'av\':\'S\',\'subClass\':[]},{\'name\':\'X\',\'av\':\'S\',\'subClass\':[]},{\'name\':\'E\',\'av\':\'S\',\'subClass\':[]}],\'codeShareList\':null,\'arriveAd\':null,\'skPrice\':null,\'dsCount\':null,\'dsSelfCount\':null,\'dsManyPeopleCount\':null,\'aoeStr\':\'OE\',\'asr\':true},{\'airline\':\'MF\',\'carrier\':null,\'fltNo\':\'848\',\'org\':\'XMN\',\'dst\':\'CGO\',\'takeoffTime\':\'2018-04-05 15:30\',\'arrivalTime\':\'2018-04-05 17:50\',\'equipment\':\'738\',\'meal\':2,\'eTicket\':true,\'codeShare\':false,\'stop\':0,\'cBrand\':{\'id\':\'6\',\'brandName\':\'超值优惠\',\'fltNO\':\'848\',\'cabin\':\'V\',\'price\':\'730\',\'childPrice\':\'680\',\'seats\':\'A\',\'csId\':null,\'mCard\':612,\'coupon\':0,\'rcRemark\':null,\'candidateId\':null,\'isSaleCabin\':0,\'hideCabin\':false,\'dsName\':null,\'dsCount\':null,\'dsSelfCount\':null,\'dsManyPeopleCount\':null,\'psgrNumMin\':0,\'baseHalfPrice\':\'680\',\'hasAOE\':false,\'firstClass\':false,\'candidate\':false,\'highEndOrder\':false,\'codeShareFlight\':true},\'fBrand\':null,\'iBrand\':{\'id\':\'2\',\'brandName\':\'商务舱优惠\',\'fltNO\':\'848\',\'cabin\':\'I\',\'price\':\'1360\',\'childPrice\':\'1700\',\'seats\':\'2\',\'csId\':null,\'mCard\':2446,\'coupon\':0,\'rcRemark\':null,\'candidateId\':null,\'isSaleCabin\':0,\'hideCabin\':false,\'dsName\':null,\'dsCount\':null,\'dsSelfCount\':null,\'dsManyPeopleCount\':null,\'psgrNumMin\':0,\'baseHalfPrice\':\'1700\',\'hasAOE\':false,\'firstClass\':false,\'candidate\':false,\'highEndOrder\':false,\'codeShareFlight\':true},\'yBrand\':{\'id\':\'10\',\'brandName\':\'经济舱全价\',\'fltNO\':\'848\',\'cabin\':\'Y\',\'price\':\'1360\',\'childPrice\':\'680\',\'seats\':\'A\',\'csId\':null,\'mCard\':2446,\'coupon\':0,\'rcRemark\':null,\'candidateId\':null,\'isSaleCabin\':0,\'hideCabin\':false,\'dsName\':null,\'dsCount\':null,\'dsSelfCount\':null,\'dsManyPeopleCount\':null,\'psgrNumMin\':0,\'baseHalfPrice\':\'680\',\'hasAOE\':false,\'firstClass\':false,\'candidate\':false,\'highEndOrder\':false,\'codeShareFlight\':true},\'airportTax\':[50.0,0.0],\'fuelTax\':[0.0,0.0],\'term\':\'T3\',\'arrTerm\':\'T2\',\'mutiSegPromotion\':{},\'state\':1,\'termLst\':[{\'dep\':\'T3\',\'arr\':\'T2\'}],\'classes\':[{\'name\':\'J\',\'av\':\'8\',\'subClass\':[]},{\'name\':\'C\',\'av\':\'2\',\'subClass\':[]},{\'name\':\'D\',\'av\':\'2\',\'subClass\':[]},{\'name\':\'I\',\'av\':\'2\',\'subClass\':[]},{\'name\':\'O\',\'av\':\'S\',\'subClass\':[]},{\'name\':\'Y\',\'av\':\'A\',\'subClass\':[]},{\'name\':\'H\',\'av\':\'A\',\'subClass\':[]},{\'name\':\'B\',\'av\':\'A\',\'subClass\':[]},{\'name\':\'M\',\'av\':\'A\',\'subClass\':[]},{\'name\':\'L\',\'av\':\'A\',\'subClass\':[]},{\'name\':\'K\',\'av\':\'A\',\'subClass\':[]},{\'name\':\'N\',\'av\':\'A\',\'subClass\':[]},{\'name\':\'Q\',\'av\':\'A\',\'subClass\':[]},{\'name\':\'V\',\'av\':\'A\',\'subClass\':[]},{\'name\':\'T\',\'av\':\'S\',\'subClass\':[]},{\'name\':\'R\',\'av\':\'S\',\'subClass\':[]},{\'name\':\'U\',\'av\':\'S\',\'subClass\':[]},{\'name\':\'G\',\'av\':\'S\',\'subClass\':[]},{\'name\':\'S\',\'av\':\'S\',\'subClass\':[]},{\'name\':\'Z\',\'av\':\'S\',\'subClass\':[]},{\'name\':\'X\',\'av\':\'S\',\'subClass\':[]},{\'name\':\'E\',\'av\':\'S\',\'subClass\':[]}],\'codeShareList\':null,\'arriveAd\':null,\'skPrice\':null,\'dsCount\':null,\'dsSelfCount\':null,\'dsManyPeopleCount\':null,\'aoeStr\':\'OE\',\'asr\':true},{\'airline\':\'MF\',\'carrier\':null,\'fltNo\':\'8045\',\'org\':\'XMN\',\'dst\':\'CGO\',\'takeoffTime\':\'2018-04-05 17:30\',\'arrivalTime\':\'2018-04-05 19:40\',\'equipment\':\'738\',\'meal\':2,\'eTicket\':true,\'codeShare\':false,\'stop\':0,\'cBrand\':{\'id\':\'6\',\'brandName\':\'超值优惠\',\'fltNO\':\'8045\',\'cabin\':\'T\',\'price\':\'670\',\'childPrice\':\'680\',\'seats\':\'A\',\'csId\':null,\'mCard\':0,\'coupon\':0,\'rcRemark\':null,\'candidateId\':null,\'isSaleCabin\':0,\'hideCabin\':false,\'dsName\':null,\'dsCount\':null,\'dsSelfCount\':null,\'dsManyPeopleCount\':null,\'psgrNumMin\':0,\'baseHalfPrice\':\'680\',\'hasAOE\':false,\'firstClass\':false,\'candidate\':false,\'highEndOrder\':false,\'codeShareFlight\':false},\'fBrand\':null,\'iBrand\':null,\'yBrand\':{\'id\':\'10\',\'brandName\':\'经济舱全价\',\'fltNO\':\'8045\',\'cabin\':\'Y\',\'price\':\'1360\',\'childPrice\':\'680\',\'seats\':\'A\',\'csId\':null,\'mCard\':2446,\'coupon\':0,\'rcRemark\':null,\'candidateId\':null,\'isSaleCabin\':0,\'hideCabin\':false,\'dsName\':null,\'dsCount\':null,\'dsSelfCount\':null,\'dsManyPeopleCount\':null,\'psgrNumMin\':0,\'baseHalfPrice\':\'680\',\'hasAOE\':false,\'firstClass\':false,\'candidate\':false,\'highEndOrder\':false,\'codeShareFlight\':true},\'airportTax\':[50.0,0.0],\'fuelTax\':[0.0,0.0],\'term\':\'T3\',\'arrTerm\':\'T2\',\'mutiSegPromotion\':{},\'state\':1,\'termLst\':[{\'dep\':\'T3\',\'arr\':\'T2\'}],\'classes\':[{\'name\':\'Y\',\'av\':\'A\',\'subClass\':[]},{\'name\':\'H\',\'av\':\'A\',\'subClass\':[]},{\'name\':\'B\',\'av\':\'A\',\'subClass\':[]},{\'name\':\'M\',\'av\':\'A\',\'subClass\':[]},{\'name\':\'L\',\'av\':\'A\',\'subClass\':[]},{\'name\':\'K\',\'av\':\'A\',\'subClass\':[]},{\'name\':\'N\',\'av\':\'A\',\'subClass\':[]},{\'name\':\'Q\',\'av\':\'A\',\'subClass\':[]},{\'name\':\'V\',\'av\':\'A\',\'subClass\':[]},{\'name\':\'T\',\'av\':\'A\',\'subClass\':[]},{\'name\':\'R\',\'av\':\'S\',\'subClass\':[]},{\'name\':\'U\',\'av\':\'S\',\'subClass\':[]},{\'name\':\'G\',\'av\':\'S\',\'subClass\':[]},{\'name\':\'S\',\'av\':\'7\',\'subClass\':[]},{\'name\':\'Z\',\'av\':\'S\',\'subClass\':[]},{\'name\':\'X\',\'av\':\'S\',\'subClass\':[]},{\'name\':\'E\',\'av\':\'S\',\'subClass\':[]}],\'codeShareList\':null,\'arriveAd\':null,\'skPrice\':null,\'dsCount\':null,\'dsSelfCount\':null,\'dsManyPeopleCount\':null,\'aoeStr\':\'E\',\'asr\':true}],\'flightInfos2\':null,\'segRecomends\':null,\'cabinComList\':null,\'noFlight\':false}";
	//预定机票post请求参数
	
	/**segIndex:1                              
	 b2cBrandId:6                         
	 adultPrice:1250
	 cabin:T
	 flightNo:8093
	 airline:MF
	 takeoffDatetime:2018-03-31 06:45
	 arrivalDatetime:2018-03-31 12:20
	 planeModel:738
	 departure:XMN
	 * 
	 */
	 
	/**
	 * segIndex:1
	 b2cBrandId:6
	 adultPrice:1250
	 cabin:T
	 flightNo:8093
	 airline:MF
	 takeoffDatetime:2018-03-31 06:45
	 arrivalDatetime:2018-03-31 12:20
	 planeModel:738
	 departure:XMN
	 candidateCabin:false
	 */
	
	/**
	 *  baseHalfPrice:1280
	 halfAdultFuelFee:0
	 aoeStr:E
	 dsSelfCount:
	 dsManyPeopleCount:
	fqc.tripType:0
	fqc.departureDateStr:2018-03-31
	fqc.returnDateStr:null
	fqc.orgCity:XMN
	fqc.dstCity:HRB
	 * @return
	 */
	
	public static Map<String,String> SetParams(String json,Map<String,String> resMap){
		
//		fqc.tripType:0
//		fqc.departureDateStr:2018-04-02
//		fqc.returnDateStr:null
//		fqc.orgCity:XMN
//		fqc.dstCity:PEK
		
		Map<String,String> paramMap  = new HashMap<String, String>();
		String flightInfo1 = "flightInfos1";
		//航线
		String no = resMap.get("no");
		String brand = resMap.get("brand");
		
		Map<String,Object> jsonMap = parseStoMap(json);
//		Map<String, String> map =  new HashMap<String, String>();
		String dstCity = jsonMap.get("dstCity").toString(); 
		String orgCity = jsonMap.get("orgCity").toString(); 
		String returnDateStr = jsonMap.get("returnDate").toString(); 
		String departureDateStr = jsonMap.get("takeoffDate").toString(); 
		String tripType = jsonMap.get("tripType").toString(); 
		paramMap.put("fqc.dstCity", dstCity);
		paramMap.put("fqc.orgCity", orgCity);
		paramMap.put("fqc.returnDateStr", returnDateStr);
		paramMap.put("fqc.departureDateStr", departureDateStr);
		paramMap.put("fqc.tripType", tripType);
		//获取套餐中的信息
		String dsManyPeopleCount = null;
		String dsSelfCount = null;
		String aoeStr = null;
		String halfAdultFuelFee = null;
		String baseHalfPrice = null;
		String candidateCabin = null;
		String isSaleCabin = null;
		String childPrice = null;
		String cabinNum = null;
		String childFuelFee = null;
		String adultFuelFee = null;
		String childAirportFee = null;
		String adultAirportFee = null;
		String arrival = null;
		String stop = null;
		String codeShare = null;
		String departure = null;
		String planeModel = null;
		String arrivalDatetime = null;
		String takeoffDatetime = null;
		String airline = null;
		String flightNo = null;
		String cabin = null;
		String adultPrice = null;
		String b2cBrandId = null;
		String segIndex = "1";
		paramMap.put("flights[0].segIndex", segIndex);
		JSONArray  flightArr = parseStoList(jsonMap.get(flightInfo1).toString());
		Map<String, Object> airlineMap = null;
		Map<String, Object> brandMap = null;
		try {
			airlineMap = parseStoMap(flightArr.get(Integer.parseInt(no)).toString());
			if(airlineMap!=null){
				//航线信息
				aoeStr = airlineMap.get("aoeStr").toString();
				halfAdultFuelFee = Integer.parseInt( parseStoList(airlineMap.get("fuelTax").toString()).get(0)+"")/2+"";
				childFuelFee = parseStoList(airlineMap.get("fuelTax").toString()).get(1).toString();
				adultFuelFee = parseStoList(airlineMap.get("fuelTax").toString()).get(0).toString();
				childAirportFee = parseStoList(airlineMap.get("airportTax").toString()).get(1).toString();
				adultAirportFee = parseStoList(airlineMap.get("airportTax").toString()).get(0).toString();
				stop = airlineMap.get("stop").toString();
				codeShare = airlineMap.get("codeShare").toString();
				departure = airlineMap.get("org").toString();
				planeModel = airlineMap.get("equipment").toString();
				arrivalDatetime = airlineMap.get("arrivalTime").toString();
				takeoffDatetime = airlineMap.get("takeoffTime").toString();
				airline = airlineMap.get("airline").toString();
				flightNo = airlineMap.get("fltNo").toString();
				arrival = airlineMap.get("dst").toString(); 
				paramMap.put("flights[0].aoeStr", aoeStr);
				paramMap.put("flights[0].halfAdultFuelFee", halfAdultFuelFee);
				paramMap.put("flights[0].childFuelFee", childFuelFee);
				paramMap.put("flights[0].adultFuelFee", adultFuelFee);
				paramMap.put("flights[0].childAirportFee", childAirportFee);
				paramMap.put("flights[0].adultAirportFee", adultAirportFee);
				paramMap.put("flights[0].stop", stop);
				paramMap.put("flights[0].codeShare", codeShare);
				paramMap.put("flights[0].departure", departure);
				paramMap.put("flights[0].planeModel", planeModel);
				paramMap.put("flights[0].arrivalDatetime", arrivalDatetime);
				paramMap.put("flights[0].takeoffDatetime", takeoffDatetime);
				paramMap.put("flights[0].airline", airline);
				paramMap.put("flights[0].flightNo", flightNo);
				paramMap.put("flights[0].arrival", arrival);
				//套餐信息
				brandMap = parseStoMap( airlineMap.get(brand).toString());
				if(brandMap!=null){
					dsManyPeopleCount = !brandMap.get("dsManyPeopleCount").toString().equals("null")? brandMap.get("dsManyPeopleCount").toString():""; 
					dsSelfCount = brandMap.get("dsSelfCount").toString().equals("null")?"": brandMap.get("dsSelfCount").toString(); 
					baseHalfPrice = brandMap.get("baseHalfPrice").toString(); 
					candidateCabin = brandMap.get("hideCabin").toString(); 
					isSaleCabin = brandMap.get("isSaleCabin").toString(); 
					childPrice = brandMap.get("childPrice").toString(); 
					cabinNum = brandMap.get("id").toString(); //有疑问
					
					cabin = brandMap.get("cabin").toString(); 
					adultPrice = brandMap.get("price").toString(); 
					b2cBrandId = brandMap.get("id").toString(); //有疑问
					
					paramMap.put("flights[0].dsManyPeopleCount", dsManyPeopleCount);
					paramMap.put("flights[0].dsSelfCount", dsSelfCount);
					paramMap.put("flights[0].baseHalfPrice", baseHalfPrice);
					paramMap.put("flights[0].candidateCabin", candidateCabin);
					paramMap.put("flights[0].isSaleCabin", isSaleCabin);
					paramMap.put("flights[0].childPrice", childPrice);
					paramMap.put("flights[0].cabinNum", cabinNum);
					paramMap.put("flights[0].cabin", cabin);
					paramMap.put("flights[0].adultPrice", adultPrice);
					paramMap.put("flights[0].b2cBrandId", b2cBrandId);
				}
				
			}
		} catch (NumberFormatException e) {
			e.printStackTrace();
		} catch (JSONException e) {
			e.printStackTrace();
		}
		return paramMap;
	}
	
	
	
	
	//查询航班并返回相关信息
	public static Map<String,String> checkFlightIsExist(String fltno,String price ,
			String json) {
		Map<String,Object> map = parseStoMap(json);
		Map<String,String> resultMap = null;
		Object obj = map.get("flightInfos1");
		JSONArray list = null;
		if(StringUtils.isNotEmpty(obj.toString())&&obj.toString()!=null){
			list = new JSONArray(obj.toString());
			for(int i = 0;i<list.length();i++){//循环flightInfos
				//获得航班
				Map<String,Object> map_i = parseStoMap(list.get(i).toString());
				//判断航班和航班号
				String airline = map_i.get("airline").toString();
				String fltNo   = map_i.get("fltNo").toString();
				if(airline.concat(fltNo).equals(fltno)){
					Iterator<Entry<String,Object>> entry = map_i.entrySet().iterator();
					//遍历航班
					while(entry.hasNext()){
						Entry<String, Object> ety = entry.next();
						//匹配套餐
						if(ety.getKey().contains("Brand")){
							if(ety.getValue().toString().equals("null")){
								continue;
							}else{
								Map<String,Object> brand = parseStoMap(ety.getValue().toString());
								//匹配价格
								double orderPrice = 0d;
								double gwPrice = 0d;
								if(StringUtils.isNotEmpty(price)){
									orderPrice = Double.parseDouble(price);
								}
								if(StringUtils.isNotEmpty(brand.get("price").toString())){
									gwPrice = Double.parseDouble(brand.get("price").toString());
								}
								if(orderPrice==gwPrice){
									resultMap = new HashMap<String, String>();
									resultMap.put("status", "1");//0表示匹配失败1表示匹配成功
									resultMap.put("no", i+"");
									resultMap.put("brand", ety.getKey());
									return resultMap;
								}
							}
						}
                    }
				}else{
					continue;
				}
				if(resultMap==null){
					resultMap = new HashMap<String, String>();
					resultMap.put("status", "0");//0表示匹配失败1表示匹配成功
				}
			}
		}else{
			resultMap = new HashMap<String, String>();
			resultMap.put("status", "0");//0表示匹配失败1表示匹配成功
			System.out.println("没有该航班");
			return resultMap;
		}
		return resultMap;
	}
	
	
	/**
	 * 
		discount_type0:1
		passengers[0].gmjc_discount_type:0
		passengers[0].idTypeOfChild:出生证明
		passengers[0].year:0
		passengers[0].month:0
		passengers[0].day:0
		passengers[0].idNoOfChild:
		passengers[0].childticket:on
		passengers[0].cardType:
		passengers[0].cardNo:
		passengers[0].mobile:15801439012
		passengers[0].airAccidentInsuranceNum:0
		coupon.code:
		coupon.pwd:
	 * @param respoHtml
	 * @return
	 */
	//点击确认信息时提交的post参数
	public static List<Map<String,String>> clickMakeSureMassage(String respoHtml ,String orderJson ,String price){
		Map<String,Object> orderMap = JSONObject.fromObject(orderJson);
		List<Map<String,String>> paramList = new ArrayList<Map<String,String>>();
		Map<String,String> paramMap  =  null;
		List<Map<String,String>>  list  = jsonListToArr(orderMap.get("passengers").toString());
		Document doc = Jsoup.parse(respoHtml);
		Elements element = doc.getElementsByAttributeValue("name","baseHalfPrice");
		/**
		 *  passengers[0].type:0
		passengers[0].name:李建  
		passengers[0].is_gmjc_order:0
		passengers[0].gmjcType:1
		passengers[0].gmjcNo:
		passengers[0].idType:身份证
		passengers[0].idNo:411327199110112092
		
		discount_type0:1
		passengers[0].gmjc_discount_type:0
		
		
		passengers[0].idTypeOfChild:出生证明
		passengers[0].year:0
		passengers[0].month:0
		passengers[0].day:0
		passengers[0].idNoOfChild:
		passengers[0].childticket:on
		passengers[0].cardType:
		passengers[0].cardNo:
		passengers[0].mobile:15801439012
		passengers[0].airAccidentInsuranceNum:0
		coupon.code:
		coupon.pwd:
		 */
		for(int i = 0 ; i<list.size();i++){
			paramMap = new HashMap<String, String>();
			Map<String,String> map = list.get(i);
			String passengerType = map.get("passengerType");
			if(StringUtils.isNotEmpty(passengerType)&&"成人".equals(passengerType)){
				passengerType = "0";
			}else if(StringUtils.isNotEmpty(passengerType)&&"儿童".equals(passengerType)){
				passengerType = "1";
			}
			paramMap.put("passengers["+i+"].type", passengerType);
//			paramMap.put("passengers["+i+"].type", "0");
			paramMap.put("passengers["+i+"].name", map.get("passengerName"));
//			paramMap.put("passengers["+i+"].name", "李建 ");
			paramMap.put("passengers["+i+"].is_gmjc_order", "0");
			paramMap.put("passengers["+i+"].gmjcType", "1");
			paramMap.put("passengers["+i+"].gmjcNo", "");
			paramMap.put("passengers["+i+"].idType", map.get("passengercardType"));
//			paramMap.put("passengers["+i+"].idType", "身份证");
			paramMap.put("passengers["+i+"].idNo", map.get("idcard"));
//			paramMap.put("passengers["+i+"].idNo", "411327199110112092");
			
			
			paramMap.put("discount_type"+i+"", "1");
			paramMap.put("passengers["+i+"].gmjc_discount_type", "0");
			
//			paramMap.put("passengers["+i+"].idTypeOfChild", map.get("passengercardType"));
			paramMap.put("passengers["+i+"].idTypeOfChild", "出生证明");
//			paramMap.put("passengers["+i+"].year", "");
//			paramMap.put("passengers["+i+"].month", "");
//			paramMap.put("passengers["+i+"].day", "");
//			paramMap.put("passengers["+i+"].idNoOfChild", map.get("idcard"));
			paramMap.put("passengers["+i+"].idNoOfChild", "");
			paramMap.put("passengers["+i+"].childticket", "on");
			paramMap.put("passengers["+i+"].cardType", "");
			paramMap.put("passengers["+i+"].cardNo", "");
//			paramMap.put("passengers["+i+"].mobile", orderMap.get("mobile").toString());
			paramMap.put("passengers["+i+"].mobile", "13532989542");
			paramMap.put("passengers["+i+"].airAccidentInsuranceNum", "0");
			
			paramMap.put("coupon.code", "");
			paramMap.put("coupon.pwd", "");
//			if("0".equals("0")){//成人
//				paramMap.put("passengers["+i+"].idTypeOfChild", "");
//				paramMap.put("passengers["+i+"].idNoOfChild", "");
//			}else if("1".equals("1")){//小孩
//				paramMap.put("passengers["+i+"].idType", "");
//				paramMap.put("passengers["+i+"].idNo", "");
//				
//			}
			/*
			 *  contact.name:李建
				contact.mobile:15801439012
				contact.email:504799954@qq.com
				itinerary.postType:1
				itinerary.name:
				itinerary.mobile:
				itinerary.province:安徽省
				itinerary.addr:
				itinerary.postNo:
				baseHalfPrice:880
				halfAdultFuelFee:0
				gmjcAdultPri:1230
				childPriceType:1
				AirDelayNum:0
			 */
			
			if(i==0){//联系人信息
				paramMap.put("contact.name", orderMap.get("linkMan").toString());
//				paramMap.put("contact.name", "李建");
				paramMap.put("contact.mobile", orderMap.get("mobile").toString());
//				paramMap.put("contact.mobile", "15801439012");
				paramMap.put("contact.email", "");
				paramMap.put("itinerary.postType", "1");
				paramMap.put("itinerary.name", "");
				paramMap.put("itinerary.mobile", "");
				paramMap.put("itinerary.province", "");
				paramMap.put("itinerary.addr", "");
				paramMap.put("itinerary.postNo", "");
				paramMap.put("baseHalfPrice", element.val());
//				paramMap.put("baseHalfPrice", "880");
				paramMap.put("halfAdultFuelFee", doc.getElementsByAttributeValue("name", "halfAdultFuelFee").val());
//				paramMap.put("halfAdultFuelFee", "0");
				String gmjcAdultPri = doc.getElementsByAttributeValue("name", "gmjcAdultPri").val();
				paramMap.put("gmjcAdultPri",  gmjcAdultPri);
				double p1 = 0d;
				double p2 = 0d;
				if(StringUtils.isNotEmpty(price)){
					p1 = Double.parseDouble(price);
				}
				if(StringUtils.isNotEmpty(gmjcAdultPri)){
					p2 = Double.parseDouble(gmjcAdultPri);
				}
				if(p1!=p2){
					System.out.println("选择的价格是:"+price+",创单的价格是:"+gmjcAdultPri+".0");
					return null;
				}
//				paramMap.put("gmjcAdultPri", "1230");
				paramMap.put("childPriceType", "1");
				paramMap.put("AirDelayNum", "0");
			}
			paramList.add(paramMap);
		}
		return paramList;
	}
	
	
	public static List<Map<String,String>>  jsonListToArr(String jsonList){
		List<Map<String,String>> list = new ArrayList<Map<String,String>>();
		try {
			JSONArray jsonArr =  new JSONArray(jsonList);
			for (int i = 0; i<jsonArr.length();i++) {
				Map<String,String > map = parseStoMapS(jsonArr.get(i).toString());
				if(map!=null){
					list.add(map);
				}else{
					continue;
				}
				
			}
		} catch (JSONException e) {
			e.printStackTrace();
		}
		return list;
	}
	
	
	//json字符窜转map
	public static Map<String,Object> parseStoMap(String str){
		Map<String,Object> map = null;
		JSONObject json =  new JSONObject();
		if(str!=null&&!"".equals(str)){
			map = JSONObject.fromObject(str);
		}
		return map;
	}
	public static Map<String,String> parseStoMapS(String str){
		Map<String,String> map = null;
		JSONObject json =  new JSONObject();
		if(str!=null&&!"".equals(str)){
			map = JSONObject.fromObject(str);
		}
		return map;
	}
	//json字符窜转list
	public static JSONArray parseStoList(String str){
		JSONArray jsonArr =  null;
		if(str!=null&&!"".equals(str)){
			try {
				jsonArr = new JSONArray(str);
			} catch (JSONException e) {
				e.printStackTrace();
			}
		}
		return jsonArr;
	}
	//map转json字符窜
	
	public static String mapToJsonStr(Map<String,String> map){
		return JSONObject.fromObject(map).toString();
	}
	//getMFPrepareParam
	public static Map<String,String> getMFPreparam(Document doc){
		Elements elements = null;
		Map<String,String> paramMap = new HashMap<String, String>();
		String associateId = ""	;//
		String bank	= "YEEPAY"; 
		String elecInvoicecompanyName	= "" ;
		String elecInvoicepersonalName	= "" ;
		String elecInvoicetaxNo	= "" ;
		String emdInfo	= "" ;
		String entityId	= "" ;
		String invoiceInfoaccount	= "" ;
		String invoiceInfobank	= "" ;
		String invoiceInfocompanyAddres	= "" ;
		String invoiceInfocompanyName	= "" ;
		String invoiceInfoinvoiceType = "" ;
		String invoiceInfoname	= "" ;
		String invoiceInfophone	= "" ;
		String invoiceInfopostAddress	= "" ;
		String invoiceInfopostCode	= "" ;
		String invoiceInfopostContacts	= "" ;
		String invoiceInfotaxpayerId	= "" ;
		String judge = "";	//true
		String  payType2	= ""	;
		String seatInfoaddressee	= ""	;
		String seatInfoaddresseePhone	= ""	;
		String seatInfobank	= ""	;
		String seatInfocompany	= ""	;
		String seatInfocompanyAccount	= ""	;
		String seatInfocompanyAddress	= ""	;
		String seatInfocompanyPhone	= ""	;
		String seatInfoinvoiceType	= ""	;
		String seatInfomailAddress	= ""	;
		String seatInfoname	= ""	;
		String seatInfopostalcode	= ""	;
		String seatInfoseatInfoID	= ""	;
		String seatInfotaxpayerId  = ""	; 
		String[] values = {associateId,
				bank,
				elecInvoicecompanyName	,
				elecInvoicepersonalName	,
				elecInvoicetaxNo,	
				emdInfo	,
				entityId,	
				invoiceInfoaccount	,
				invoiceInfobank,	
				invoiceInfocompanyAddres,
				invoiceInfocompanyName	,
				invoiceInfoinvoiceType	,
				invoiceInfoname,	
				invoiceInfophone,	
				invoiceInfopostAddress	,
				invoiceInfopostCode	,
				invoiceInfopostContacts,	
				invoiceInfotaxpayerId	,
				judge	,
				payType2	,
				seatInfoaddressee	,
				seatInfoaddresseePhone	,
				seatInfobank	,
				seatInfocompany	,
				seatInfocompanyAccount	,
				seatInfocompanyAddress	,
				seatInfocompanyPhone	,
				seatInfoinvoiceType	,
				seatInfomailAddress,	
				seatInfoname	,
				seatInfopostalcode	,
				seatInfoseatInfoID	,
				seatInfotaxpayerId};
		String[] keys = {"associateId",
				"bank"	,
				"elecInvoice.companyName",	
				"elecInvoice.personalName",	
				"elecInvoice.taxNo",	
				"emdInfo",
				"entityId",	
				"invoiceInfo.account"	,
				"invoiceInfo.bank",	
				"invoiceInfo.companyAddress",	
				"invoiceInfo.invoiceType",
				"invoiceInfo.name",
				"invoiceInfo.phone",
				"invoiceInfo.postAddress",	
				"invoiceInfo.postCode",	
				"invoiceInfo.postContacts",	
				"invoiceInfo.taxpayerId",
				"invoiceInfo.companyName",
				"judge",
				"payType",
				"seatInfo.addressee",	
				"seatInfo.addresseePhone",	
				"seatInfo.bank",
				"seatInfo.company",
				"seatInfo.companyAccount",
				"seatInfo.companyAddress",
				"seatInfo.companyPhone",
				"seatInfo.invoiceType",
				"seatInfo.mailAddress"	,
				"seatInfo.name",
				"seatInfo.postalcode",	
				"seatInfo.seatInfoID",
				"seatInfo.taxpayerId"};
	
		for(int i = 0 ; i<keys.length;i++){
			if(keys[i].equals("associateId")){
				 elements  = doc.getElementsByAttributeValue("name", "associateId");
				if(elements.size()>0){
					values[i] = elements.get(0).val();
				}
			}else if(keys[i].equals("invoiceInfo.invoiceType")){
				 elements  = doc.getElementsByAttributeValue("name", "invoiceInfo.invoiceType");
					if(elements.size()>0){
						values[i] = elements.get(0).val();
					}
			}else if(keys[i].equals("judge")){
				elements  = doc.getElementsByAttributeValue("name", "judge");
				if(elements.size()>0){
					values[i] = elements.get(0).val();
				}
			}else if(keys[i].equals("payType")){
				elements  = doc.getElementsByAttributeValue("name", "payType");
				if(elements.size()>0){
					values[i] = elements.get(0).val();
				}
			}else if(keys[i].equals("seatInfo.invoiceType")){
				elements  = doc.getElementsByAttributeValue("name", "seatInfo.invoiceType");
				if(elements.size()>0){
					values[i] = elements.get(0).val();
				}
			}
			paramMap.put(keys[i],values[i]);
			
		}
		
		return paramMap;
	}
	//getMFServletParam
	public static Map<String,String> getMFServletParam(Document doc){
		Map<String, String> paramMap = new HashMap<String, String>();
		Elements eles = null;
		String[] keys = {"AppType"	,"BankId","BillNo" ,"Ext1" ,"Ext2",
				"Lan", "Msg" ,"OrderAmount" ,"OrderCurtype", "OrderDate",
				"OrderNo" ,"OrderTime" , "OrderType", "Orderinfo" ,"OrgId",
				"ReturnId" ,"SIGNATURE" ,"Version", "gateid" ,"ordername",
				"username" ,"usrid"};
		for(String key:keys){
			paramMap.put(key, "");
			if(key.equals("AppType")){
				 eles =   doc.getElementsByAttributeValue("name", "AppType");
				if(eles.size()>0){
					paramMap.put(key,eles.get(0).val());
				}
			}else if(key.equals("BillNo")){
				 eles =   doc.getElementsByAttributeValue("name", "BillNo");
					if(eles.size()>0){
						paramMap.put(key,eles.get(0).val());
					}
			}else if(key.equals("BankId")){
				 eles =   doc.getElementsByAttributeValue("name", "BankId");
				if(eles.size()>0){
					paramMap.put(key,eles.get(0).val());
				}
			}else if(key.equals("Lan")){
				 eles =   doc.getElementsByAttributeValue("name", "Lan");
				if(eles.size()>0){
					paramMap.put(key,eles.get(0).val());
				}
			}else if(key.equals("Msg")){
				 eles =   doc.getElementsByAttributeValue("name", "Msg");
				if(eles.size()>0){
					paramMap.put(key,eles.get(0).val());
				}
			}else if(key.equals("OrderAmount")){
				 eles =   doc.getElementsByAttributeValue("name", "OrderAmount");
				if(eles.size()>0){
					paramMap.put(key,eles.get(0).val());
				}
			}else if(key.equals("OrderCurtype")){
				 eles =   doc.getElementsByAttributeValue("name", "OrderCurtype");
				if(eles.size()>0){
					paramMap.put(key,eles.get(0).val());
				}
			}else if(key.equals("OrderDate")){
				 eles =   doc.getElementsByAttributeValue("name", "OrderDate");
				if(eles.size()>0){
					paramMap.put(key,eles.get(0).val());
				}
			}else if(key.equals("OrderNo")){
				 eles =   doc.getElementsByAttributeValue("name", "OrderNo");
				if(eles.size()>0){
					paramMap.put(key,eles.get(0).val());
				}
			}else if(key.equals("OrderTime")){
				 eles =   doc.getElementsByAttributeValue("name", "OrderTime");
				if(eles.size()>0){
					paramMap.put(key,eles.get(0).val());
				}
			}else if(key.equals("OrderType")){
				 eles =   doc.getElementsByAttributeValue("name", "OrderType");
				if(eles.size()>0){
					paramMap.put(key,eles.get(0).val());
				}
			}else if(key.equals("Orderinfo")){
				 eles =   doc.getElementsByAttributeValue("name", "Orderinfo");
				if(eles.size()>0){
					paramMap.put(key,eles.get(0).val());
				}
			}else if(key.equals("OrgId")){
				 eles =   doc.getElementsByAttributeValue("name", "OrgId");
				if(eles.size()>0){
					paramMap.put(key,eles.get(0).val());
				}
			}else if(key.equals("ReturnId")){
				 eles =   doc.getElementsByAttributeValue("name", "ReturnId");
				if(eles.size()>0){
					paramMap.put(key,eles.get(0).val());
				}
			}else if(key.equals("SIGNATURE")){
				 eles =   doc.getElementsByAttributeValue("name", "SIGNATURE");
				if(eles.size()>0){
					paramMap.put(key,eles.get(0).val());
				}
			}else if(key.equals("Version")){
				 eles =   doc.getElementsByAttributeValue("name", "Version");
				if(eles.size()>0){
					paramMap.put(key,eles.get(0).val());
				}
			}else if(key.equals("ordername")){
				 eles =   doc.getElementsByAttributeValue("name", "ordername");
				if(eles.size()>0){
					paramMap.put(key,eles.get(0).val());
				}
			}else if(key.equals("usrid")){
				 eles =   doc.getElementsByAttributeValue("name", "usrid");
				if(eles.size()>0){
					paramMap.put(key,eles.get(0).val());
				}
			}
		}
		return paramMap;
	}
	//获取最终参数用以绑卡
	public static Map<String,String> getFinalParam(Document doc){
		Map<String, String> paramMap = new HashMap<String, String>();
		Elements eles = null;
			eles = doc.getElementsByAttributeValue("name", "p0_Cmd");
			if(eles.size()>0){
				paramMap.put("p0_Cmd", eles.get(0).val());
			}
			eles = doc.getElementsByAttributeValue("name", "p1_MerId");
			if(eles.size()>0){
				paramMap.put("p1_MerId", eles.get(0).val());
			}
			eles = doc.getElementsByAttributeValue("name", "p2_Order");
			if(eles.size()>0){
				paramMap.put("p2_Order", eles.get(0).val());
			}
			eles = doc.getElementsByAttributeValue("name", "p3_Amt");
			if(eles.size()>0){
				paramMap.put("p3_Amt", eles.get(0).val());
			}
			eles = doc.getElementsByAttributeValue("name", "p4_Cur");
			if(eles.size()>0){
				paramMap.put("p4_Cur", eles.get(0).val());
			}
			eles = doc.getElementsByAttributeValue("name", "p5_Pid");
			if(eles.size()>0){
				paramMap.put("p5_Pid", eles.get(0).val());
			}
			eles = doc.getElementsByAttributeValue("name", "p6_Url");
			if(eles.size()>0){
				paramMap.put("p6_Url", eles.get(0).val());
			}
			eles = doc.getElementsByAttributeValue("name", "p7_MP");
			if(eles.size()>0){
				paramMap.put("p7_MP", eles.get(0).val());
			}
			eles = doc.getElementsByAttributeValue("name", "p8_FrpId");
			if(eles.size()>0){
				paramMap.put("p8_FrpId", eles.get(0).val());
			}
			eles = doc.getElementsByAttributeValue("name", "p9_TelNum");
			if(eles.size()>0){
				paramMap.put("p9_TelNum", eles.get(0).val());
			}
			eles = doc.getElementsByAttributeValue("name", "pa_Detail");
			if(eles.size()>0){
				paramMap.put("pa_Detail", eles.get(0).val());
			}
			eles = doc.getElementsByAttributeValue("name", "pb_OrderType");
			if(eles.size()>0){
				paramMap.put("pb_OrderType", eles.get(0).val());
			}
			eles = doc.getElementsByAttributeValue("name", "pc_AutoSplit");
			if(eles.size()>0){
				paramMap.put("pc_AutoSplit", eles.get(0).val());
			}
			eles = doc.getElementsByAttributeValue("name", "pc_CreditDetails");
			if(eles.size()>0){
				paramMap.put("pc_CreditDetails", eles.get(0).val());
			}
			eles = doc.getElementsByAttributeValue("name", "pd_BankBranch");
			if(eles.size()>0){
				paramMap.put("pd_BankBranch", eles.get(0).val());
			}
			eles = doc.getElementsByAttributeValue("name", "pd_NotifyExt");
			if(eles.size()>0){
				paramMap.put("pd_NotifyExt", eles.get(0).val());
			}
			eles = doc.getElementsByAttributeValue("name", "pf_RemoteLoginName");
			if(eles.size()>0){
				paramMap.put("pf_RemoteLoginName", eles.get(0).val());
			}
			eles = doc.getElementsByAttributeValue("name", "ph_SrcFee");
			if(eles.size()>0){
				paramMap.put("ph_SrcFee", eles.get(0).val());
			}
			eles = doc.getElementsByAttributeValue("name", "ph_TgtFee");
			if(eles.size()>0){
				paramMap.put("ph_TgtFee", eles.get(0).val());
			}
			eles = doc.getElementsByAttributeValue("name", "pm_Period");
			if(eles.size()>0){
				paramMap.put("pm_Period", eles.get(0).val());
			}
			eles = doc.getElementsByAttributeValue("name", "pn_Unit");
			if(eles.size()>0){
				paramMap.put("pn_Unit", eles.get(0).val());
			}
			eles = doc.getElementsByAttributeValue("name", "pr_NeedResponse");
			if(eles.size()>0){
				paramMap.put("pr_NeedResponse", eles.get(0).val());
			}
			eles = doc.getElementsByAttributeValue("name", "hmac");
			if(eles.size()>0){
				paramMap.put("hmac", eles.get(0).val());
			}
		return paramMap;
	}
	public static void main(String[] args) throws Exception{
		File file = new File("F:/1234.txt");
		StringBuilder text = new StringBuilder();
		String sr = null;
		BufferedReader br = new BufferedReader(new FileReader(file));
		while ((sr = br.readLine()) != null) {
				text.append(sr);
		}
		org.jsoup.nodes.Document doc = Jsoup.parse(text.toString());
		getMFServletParam(doc);
	}




	public static String CheckNewMfFlightIsExist(String fltno, String price, String back, String dep, String arr, String depdate) {
		if(StringUtils.isEmpty(back)) {
			return null;
		}
		String resultString = "{\"searchParam\":{\"dOrI\":\"d\",\"adtCount\":1,\"chdCount\":0,\"infCount\":0,\"odList\":[{\"org\":\""+dep+"\",\"dst\":\""+arr+"\",\"orgDate\":\""+depdate+"\"}],\"dstDate\":\"\",\"tripType\":\"OW\"},\"selectedOdOnlyContainIdList\":[{\"segmentId\":\"paramSegmentId\",\"itemId\":\"paramItemId\"}]}";
		net.sf.json.JSONObject backObj = JSONObject.fromObject(back);
		net.sf.json.JSONObject resultObj = backObj.getJSONObject("result");
		net.sf.json.JSONObject sepa = resultObj.getJSONObject("sepa");
		net.sf.json.JSONObject airlineObj = sepa.getJSONObject(dep+"-"+arr);
		Iterator<String> it = airlineObj.keys();
		String paramItemId = "";
		String paramSegmentId = "";
		while(it.hasNext()) {
			String key = it.next();
			if(!key.contains(fltno)||!key.contains(dep)||!key.contains(arr)||!key.contains(depdate)) {
				continue;
			}
			net.sf.json.JSONObject jsonObj = airlineObj.getJSONObject(key);
			net.sf.json.JSONObject itemsAll = jsonObj.getJSONObject("itemsAll");
			net.sf.json.JSONObject jsonobj = itemsAll.getJSONObject(key);
			net.sf.json.JSONArray ADT1Arr = jsonobj.getJSONArray("[ADT1]");
			for(int i=0;i<ADT1Arr.size();i++) {
				net.sf.json.JSONObject ADT1 = ADT1Arr.getJSONObject(i);
				String price1 = ADT1.getJSONObject("price").getString("baseAmount");
				if(Float.parseFloat(price1)!=Float.parseFloat(price)) {
					continue;
				}
				net.sf.json.JSONObject paxSeg = ADT1.getJSONObject("paxSeg");
				net.sf.json.JSONObject deparr = paxSeg.getJSONArray(dep+"-"+arr).getJSONObject(0);
				String code = deparr.getString("code");
				String brandName = deparr.getString("brandName");
				String inventory = ADT1.getString("inventory");
				paramItemId = ADT1.getString("itemid");
				paramSegmentId = key;
				System.out.println("itemid:"+paramItemId+",price1:"+price1+",code:"+code+",brandName:"+brandName+",inventory:"+inventory);
			}
		}
		if(StringUtils.isEmpty(paramItemId)||StringUtils.isEmpty(paramSegmentId)) {
			return "";
		}
		resultString = resultString.replace("paramSegmentId",paramSegmentId).replace("paramItemId", paramItemId);
		
		
		
//		net.sf.json.JSONObject flightSearchItemListObj = resultObj.getJSONObject("flightSearchItemList");
//		net.sf.json.JSONArray airlineArr = flightSearchItemListObj.getJSONArray(depdate+","+dep+","+arr);
//		net.sf.json.JSONObject airlineObj = null;
//		/*
//		 * 先定义需要后面作为传参的字段
//		 */
//		String selectedFlightsJson = "";
//		for(int i=0;i<airlineArr.size();i++) {
//			airlineObj = JSONObject.fromObject(airlineArr.get(i));
//			String operatingFlightNumber = airlineObj.getString("operatingFlightNumber");
//			if(StringUtils.isEmpty(operatingFlightNumber)||!operatingFlightNumber.equals(fltno)) {
//				continue;
//			}
//			net.sf.json.JSONArray cabinInfosArr = airlineObj.getJSONArray("cabinInfos");
//			net.sf.json.JSONObject cabinInfosObj = null;
//			for(int j=0;j<cabinInfosArr.size();j++) {
//				cabinInfosObj = JSONObject.fromObject(cabinInfosArr.get(j));
//				int amountStr = 0;
//				int airportTax = 0;
//				int fuelTax = 0;
//				try {
//					amountStr = cabinInfosObj.getInt("amount");
//					airportTax = cabinInfosObj.getInt("airportTax");
//					fuelTax = cabinInfosObj.getInt("fuelTax");
//				} catch (Exception e) {}
//				if(StringUtils.isEmpty(price)) {
//					continue;
//				}
//				if(Float.parseFloat(price)!=(amountStr)) {
//					continue;
//				}
//				String airlineString = airlineObj.toString().replace(cabinInfosArr.toString(), "[]");
//				JSONObject jsonAirLineString = JSONObject.fromObject(airlineString);
//				selectedFlightsJson = "{\"selectedFlights\":{\"tripType\":\"OW\",\"dori\":false,\"flightList\":[{\"index\":0,\"segment\":"
//						+ jsonAirLineString.toString() +",\"cabin\":"+cabinInfosObj.toString()+"}]},\"opportunities\":{}}";
//			}
//		}
//		System.out.println(selectedFlightsJson);
//		JSONArray list = null;
//		list = new JSONArray(obj.toString());
//		for(int i=0;i<list.length();i++) {
//			
//		}
//		System.out.println(map.get("result"));
		return resultString;
	}
}
