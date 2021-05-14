package com.feeye.bean;

import java.util.*;

public class OfficeFlightInfoUtil {
	/**
	 * 旧接口获取舱位类型
	 * @param resList
	 * @param flightNo
	 * @return
	 */
	public static String getResult(List<FlightInfo> resList,String flightNo){
		boolean result = false;
		String strResult = "";
		
		String strcabin = "";
		String strprice = "";
		String strtype = "";
		String strseat = "";
		FlightInfo flightinfo = new FlightInfo();
		for(int i=0;i<resList.size();i++){
			String resflightno = resList.get(i).getFlightNo();
			if(resflightno!=null&&resflightno.equals(flightNo)){
				flightinfo = resList.get(i);
			}
		}
		List<CabinInfo> cabins = flightinfo.getCabins();
		Collections.sort(cabins, new Comparator<CabinInfo>() {
			public int compare(CabinInfo p1,CabinInfo p2){
				Float price1 = Float.parseFloat(p1.getPrice());
				Float price2 = Float.parseFloat(p2.getPrice());
				return price1.compareTo(price2);
			}
		});
		String airline = resList.get(0).getFlightNo().substring(0,2);
		for(CabinInfo cabininfo:cabins){
			result = true;
			strcabin = strcabin+"-"+cabininfo.getCabinCode();//+"("+cabininfo.getLastSeat()+")";
			strprice = strprice+"-"+cabininfo.getPrice();
			String seat = cabininfo.getLastSeat();
			seat = seat==null?"*":seat;
			strseat = strseat+"-"+seat;
//			if(airline.equals("8L")){
//				String type = cabininfo.getPriceType();
//				type = getTypeMap().get(type);
//				strtype = strtype+"-"+type;
//			}else
				if("9C".equalsIgnoreCase(airline)){
				String type = cabininfo.getPriceType();
				strtype = strtype+"-"+type;
			}else{
				String type = cabininfo.getBaseCabin();
				strtype = strtype+"-"+type;
			}
		}
		if(result&&strcabin.length()>=1){
			strcabin = strcabin.substring(1);
			strprice = strprice.substring(1);
			strtype = strtype.substring(1);
			strseat = strseat.substring(1);
		}
		strResult = "{'result':'"+result+"','strcabin':'"+strcabin+"','strprice':'"+strprice+"','strtype':'"+strtype+"','strseat':'"+strseat+"'}";
		return strResult;
	}
	/**
	 * 根据舱位获取指定舱位信息
	 * @param infolist
	 * @param cabin
	 * @return
	 */
	public static List<FlightInfo> getCabins(List<FlightInfo> infolist, String cabin) {
		List<CabinInfo> cabins = new ArrayList<CabinInfo>();
		for(FlightInfo info : infolist){
			List<CabinInfo> cabinlist  =info.getCabins();
			for(CabinInfo cabininfo:cabinlist)
			{
				String cabincode = cabininfo.getCabinCode();
				if(cabin.equals(cabincode))
				{
					cabins.add(cabininfo);
				}
			}
			info.setCabins(cabins);
		}
		return infolist;
		
	}

	public static  Map<String, String> getTypeMap() {
		Map<String, String> map = new HashMap<String, String>();
		map.put("WEB", "轻松行");
		map.put("BJHH", "保价护航");
		map.put("XLWY", "随心飞");
		map.put("PCXF", "轻松行");
		map.put("SWC","标准公务舱");
		map.put("FXP","超值公务舱");
		map.put("MZMK","全价经济舱");
		map.put("JJC", "残疾军人/伤残人民警察优惠票");
		map.put("ZXJJ","智选经济舱");
		return map;
	}
	
	/**
	 * 旧接口获取舱位类型
	 * @param resList
	 * @param flightNo
	 * @return
	 */
	public static String getResultNotDistAir(List<FlightInfo> resList,String flightNo)
	{
		boolean result = false;
		String strResult = "";
		
		String strcabin = "";
		String strprice = "";
		String strtype = "";
		String strseat = "";
		FlightInfo flightinfo = new FlightInfo();
		for(int i=0;i<resList.size();i++)
		{
			String resflightno = resList.get(i).getFlightNo();
			if(resflightno!=null&&resflightno.equals(flightNo))
			{
				flightinfo = resList.get(i);
			}
		}
		List<CabinInfo> cabins = flightinfo.getCabins();
		Collections.sort(cabins, new Comparator<CabinInfo>() {
			public int compare(CabinInfo p1,CabinInfo p2){
				Float price1 = Float.parseFloat(p1.getPrice());
				Float price2 = Float.parseFloat(p2.getPrice());
				return price1.compareTo(price2);
			}
		});
		String airline = resList.get(0).getFlightNo().substring(0,2);
		for(CabinInfo cabininfo:cabins)
		{
			Map<String,String> map = new HashMap<String,String>();
			result = true;
			strcabin = strcabin+"-"+cabininfo.getCabinCode();//+"("+cabininfo.getLastSeat()+")";
			strprice = strprice+"-"+cabininfo.getPrice();
			String seat = cabininfo.getLastSeat();
			seat = seat==null?"*":seat;
			strseat = strseat+"-"+seat;
			String type = cabininfo.getBaseCabin();
			strtype = strtype+"-"+type;
		}
		if(result&&strcabin.length()>=1)
		{
			strcabin = strcabin.substring(1);
			strprice = strprice.substring(1);
			strtype = strtype.substring(1);
			strseat = strseat.substring(1);
		}
		strResult = "{'result':'"+result+"','strcabin':'"+strcabin+"','strprice':'"+strprice+"','strtype':'"+strtype+"','strseat':'"+strseat+"'}";
		return strResult;
	}
}
