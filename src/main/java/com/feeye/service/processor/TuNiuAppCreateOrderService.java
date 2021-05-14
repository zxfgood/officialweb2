/*
package com.feeye.service.processor;

import com.feeye.util.MD5Util;
import com.feeye.util.PropertiesUtils;
import org.apache.commons.io.IOUtils;
import org.apache.commons.lang.StringUtils;
import org.apache.http.HttpHost;
import org.apache.http.auth.AUTH;
import org.apache.http.auth.AuthScope;
import org.apache.http.auth.MalformedChallengeException;
import org.apache.http.auth.UsernamePasswordCredentials;
import org.apache.http.client.CredentialsProvider;
import org.apache.http.client.config.RequestConfig;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.conn.ssl.SSLConnectionSocketFactory;
import org.apache.http.conn.ssl.SSLContextBuilder;
import org.apache.http.conn.ssl.TrustStrategy;
import org.apache.http.cookie.Cookie;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.*;
import org.apache.http.message.BasicHeader;
import org.apache.http.util.EntityUtils;
import org.json.JSONArray;
import org.json.JSONObject;

import javax.net.ssl.SSLContext;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.URLEncoder;
import java.nio.charset.Charset;
import java.security.cert.CertificateException;
import java.security.cert.X509Certificate;
import java.util.*;

public class TuNiuAppCreateOrderService {
    private static final int timeout = 60000;
    private static Map<String,String> idMap = new HashMap<String,String>();
    public static void main(String[] args) {
        String back = "{\r\n" +
                "    \"id\": \"27886042\",\r\n" +
                "    \"account\": \"13532989542_feeye123\",\r\n" +
                "    \"airline\": \"GT\",\r\n" +
                "    \"orderNo\": \"1524794945160\",\r\n" +
                "    \"username\": \"policytest\",\r\n" +
                "    \"childrenMobile\": \"18617070230\",\r\n" +
                "    \"payType\": \"zfb\",\r\n" +
                "    \"code\": \"\",\r\n" +
                "    \"orderTime\": \"2018-06-13 15:35:16\",\r\n" +
                "    \"codePassword\": \"\",\r\n" +
                "    \"price\": \"439\",\r\n" +
                "    \"departure\": \"CSX\",\r\n" +
                "    \"arrival\": \"NGB\",\r\n" +
                "    \"departureDate\": \"2018-11-07\",\r\n" +
                "    \"flightNo\": \"EU2749\",\r\n" +
                "    \"cabin\": \"B\",\r\n" +
                "    \"mobile\": \"13532989542\",\r\n" +
                "    \"creditNo\": \"18713083283\",\r\n" +
                "    \"expireMonth\": \"null\",\r\n" +
                "    \"expireYear\": \"null\",\r\n" +
                "    \"cvv\": \"15817476200\",\r\n" +
                "    \"ownername\": \"null\",\r\n" +
                "    \"idCardType\": \"null\",\r\n" +
                "    \"idCardNo\": \"null\",\r\n" +
                "    \"payerMobile\": \"12346579\",\r\n" +
                "    \"account_no\": \"null\",\r\n" +
                "    \"deduct_third_code\": \"null\",\r\n" +
                "    \"linkMan\": \"熊焕景\",\r\n" +
                "    \"isOutticket\": \"true\",\r\n" +
                "    \"ytype\": \"其他\",\r\n" +
                "    \"passengers\": [\r\n" +
                "        {\r\n" +
                "            \"passengerName\": \"陈中仁\",\r\n" +
                "            \"idcard\": \"510213197012121235\",\r\n" +
                "            \"passengerType\": \"成人\",\r\n" +
                "            \"passengercardType\": \"身份证\",\r\n" +
                "            \"birthday\": \"1970-12-12\",\r\n" +
                "            \"passengerSex\": \"null\"\r\n" +
                "        }"
                + ",\r\n" +
//				"		{\r\n" +
//				"            \"passengerName\": \"罗轩\",\r\n" +
//				"            \"idcard\": \"PA0189134\",\r\n" +
//				"            \"passengerType\": \"成人\",\r\n" +
//				"            \"passengercardType\": \"护照\",\r\n" +
//				"            \"birthday\": \"1995-12-01\",\r\n" +
//				"            \"passengerSex\": \"null\"\r\n" +
//				"        }\r\n" +
//				"		{\r\n" +
//				"            \"passengerName\": \"陈若愚\",\r\n" +
//				"            \"idcard\": \"361002201112310625\",\r\n" +
//				"            \"passengerType\": \"儿童\",\r\n" +
//				"            \"passengercardType\": \"身份证\",\r\n" +
//				"            \"birthday\": \"2011-12-31\",\r\n" +
//				"            \"passengerSex\": \"null\"\r\n" +
//				"        }\r\n" +
                "    ],\r\n" +
                "    \"ifUsedCoupon\": false,\r\n" +
                "    \"drawerType\": \"GW\",\r\n" +
                "    \"qiangpiao\": \"\",\r\n" +
                "    \"otheraccount\": \"b_b\"\r\n" +
                "}";
        new TuNiuAppCreateOrderService().StartCreateOrder(back, 0);
    }
    public String StartCreateOrder(String orderJson,int retryCount) {
        if (StringUtils.isEmpty(orderJson)) {
            return "ERROR:数据不完整";
        }
        String str ="{\"Flights\":[{\"detail\":[{\"Departure\":{\"IATA\":\"NAY\",\"Airport\":\"北京南苑\",\"TS_CityCode\":\"NAY\",\"Terminal\":\"--\",\"DateTime\":\"2018-09-29T09:00:00\",\"Date\":\"2018-09-29\",\"Time\":\"09:00:00\"},\"Arrival\":{\"IATA\":\"PVG\",\"Airport\":\"上海浦东\",\"TS_CityCode\":\"PVG\",\"Terminal\":\"T1\",\"DateTime\":\"2018-09-29T11:15:00\",\"Date\":\"2018-09-29\",\"Time\":\"11:15:00\"},\"MarketingAirline\":\"KN\",\"OperatingAirline\":\"KN\",\"FlightNumber\":\"5977\",\"AirEquipType\":\"73V\",\"Duration\":\"2:15\",\"StopOver\":false}],\"price\":[{\"FareFamilyCode\":\"BESTSELLING\",\"FareFamilyName\":\"舒心飞\",\"Seq\":\"1\",\"Currency\":\"CNY\",\"Amount\":\"1358.0\",\"Adjusted\":\"1358.0\",\"index\":3},{\"FareFamilyCode\":\"TOURISMROVER\",\"FareFamilyName\":\"大众游\",\"Seq\":\"1\",\"Currency\":\"CNY\",\"Amount\":\"1018.0\",\"Adjusted\":\"1018.0\",\"index\":2},{\"FareFamilyCode\":\"SPECIAL\",\"FareFamilyName\":\"欢乐抢\",\"Seq\":\"1\",\"Currency\":\"CNY\",\"Amount\":\"678.0\",\"Adjusted\":\"678.0\",\"index\":1}],\"sortType\":1,\"flightDate\":\"2018-09-29T09:00:00\"},{\"detail\":[{\"Departure\":{\"IATA\":\"NAY\",\"Airport\":\"北京南苑\",\"TS_CityCode\":\"NAY\",\"Terminal\":\"--\",\"DateTime\":\"2018-09-29T13:40:00\",\"Date\":\"2018-09-29\",\"Time\":\"13:40:00\"},\"Arrival\":{\"IATA\":\"PVG\",\"Airport\":\"上海浦东\",\"TS_CityCode\":\"PVG\",\"Terminal\":\"T1\",\"DateTime\":\"2018-09-29T15:50:00\",\"Date\":\"2018-09-29\",\"Time\":\"15:50:00\"},\"MarketingAirline\":\"KN\",\"OperatingAirline\":\"KN\",\"FlightNumber\":\"5981\",\"AirEquipType\":\"73V\",\"Duration\":\"2:10\",\"StopOver\":false}],\"price\":[{\"FareFamilyCode\":\"BESTSELLING\",\"FareFamilyName\":\"舒心飞\",\"Seq\":\"2\",\"Currency\":\"CNY\",\"Amount\":\"1358.0\",\"Adjusted\":\"1358.0\",\"index\":3},{\"FareFamilyCode\":\"TOURISMROVER\",\"FareFamilyName\":\"大众游\",\"Seq\":\"2\",\"Currency\":\"CNY\",\"Amount\":\"1018.0\",\"Adjusted\":\"1018.0\",\"index\":2},{\"FareFamilyCode\":\"SPECIAL\",\"FareFamilyName\":\"欢乐抢\",\"Seq\":\"2\",\"Currency\":\"CNY\",\"Amount\":\"678.0\",\"Adjusted\":\"678.0\",\"index\":1}],\"sortType\":1,\"flightDate\":\"2018-09-29T13:40:00\"},{\"detail\":[{\"Departure\":{\"IATA\":\"NAY\",\"Airport\":\"北京南苑\",\"TS_CityCode\":\"NAY\",\"Terminal\":\"--\",\"DateTime\":\"2018-09-29T15:15:00\",\"Date\":\"2018-09-29\",\"Time\":\"15:15:00\"},\"Arrival\":{\"IATA\":\"SHA\",\"Airport\":\"上海虹桥\",\"TS_CityCode\":\"SHA\",\"Terminal\":\"T2\",\"DateTime\":\"2018-09-29T17:35:00\",\"Date\":\"2018-09-29\",\"Time\":\"17:35:00\"},\"MarketingAirline\":\"KN\",\"OperatingAirline\":\"KN\",\"FlightNumber\":\"5955\",\"AirEquipType\":\"73V\",\"Duration\":\"2:20\",\"StopOver\":false}],\"price\":[{\"FareFamilyCode\":\"BESTSELLING\",\"FareFamilyName\":\"舒心飞\",\"Seq\":\"3\",\"Currency\":\"CNY\",\"Amount\":\"1358.0\",\"Adjusted\":\"1358.0\",\"index\":3},{\"FareFamilyCode\":\"TOURISMROVER\",\"FareFamilyName\":\"大众游\",\"Seq\":\"3\",\"Currency\":\"CNY\",\"Amount\":\"1018.0\",\"Adjusted\":\"1018.0\",\"index\":2},{\"FareFamilyCode\":\"SPECIAL\",\"FareFamilyName\":\"欢乐抢\",\"Seq\":\"3\",\"Currency\":\"CNY\",\"Amount\":\"608.0\",\"Adjusted\":\"608.0\",\"index\":1}],\"sortType\":1,\"flightDate\":\"2018-09-29T15:15:00\"},{\"detail\":[{\"Departure\":{\"IATA\":\"NAY\",\"Airport\":\"北京南苑\",\"TS_CityCode\":\"NAY\",\"Terminal\":\"--\",\"DateTime\":\"2018-09-29T20:45:00\",\"Date\":\"2018-09-29\",\"Time\":\"20:45:00\"},\"Arrival\":{\"IATA\":\"PVG\",\"Airport\":\"上海浦东\",\"TS_CityCode\":\"PVG\",\"Terminal\":\"T1\",\"DateTime\":\"2018-09-29T22:55:00\",\"Date\":\"2018-09-29\",\"Time\":\"22:55:00\"},\"MarketingAirline\":\"KN\",\"OperatingAirline\":\"KN\",\"FlightNumber\":\"5987\",\"AirEquipType\":\"73V\",\"Duration\":\"2:10\",\"StopOver\":false}],\"price\":[{\"FareFamilyCode\":\"BESTSELLING\",\"FareFamilyName\":\"舒心飞\",\"Seq\":\"4\",\"Currency\":\"CNY\",\"Amount\":\"1358.0\",\"Adjusted\":\"1358.0\",\"index\":3},{\"FareFamilyCode\":\"TOURISMROVER\",\"FareFamilyName\":\"大众游\",\"Seq\":\"4\",\"Currency\":\"CNY\",\"Amount\":\"1018.0\",\"Adjusted\":\"1018.0\",\"index\":2},{\"FareFamilyCode\":\"SPECIAL\",\"FareFamilyName\":\"欢乐抢\",\"Seq\":\"4\",\"Currency\":\"CNY\",\"Amount\":\"748.0\",\"Adjusted\":\"748.0\",\"index\":1}],\"sortType\":1,\"flightDate\":\"2018-09-29T20:45:00\"}],\"Calendar\":[],\"SearchSummary\":{\"deptCityName\":\"北京\",\"arrCityName\":\"上海\",\"FlightType\":\"oneway\",\"isDomestic\":\"true\",\"deptDate\":\"2018-09-29\",\"arrDate\":\"\",\"Passengers\":{\"Adults\":\"1\",\"Children\":\"0\",\"Infants\":\"0\"},\"cals\":false},\"redPacketRule\":{\"W\":4,\"Y\":3,\"B\":3,\"M\":3,\"A\":3,\"E\":3,\"H\":3,\"K\":1,\"L\":1,\"N\":1,\"D\":1,\"R\":1,\"S\":1,\"V\":1,\"J\":1,\"T\":1,\"I\":1,\"Z\":0,\"U\":0},\"price\":[{\"NumAlts\":\"4\",\"SequenceNumber\":\"1\",\"SupplierCode\":\"TSDF\",\"CanBinCode\":\"Y\",\"OriginDestinationRPH\":\"\",\"FlightDetails\":[{\"Code\":\"KN\",\"Duration\":\"2:15\",\"DirectionInd\":\"OneWay\",\"JourneyId\":\"1\",\"Summary\":{\"CabinClass\":\"WW\",\"Departure\":{\"IATA\":\"NAY\",\"Airport\":\"北京南苑\",\"DateTime\":\"2018-09-29T09:00:00\",\"Date\":\"2018-09-29\",\"Time\":\"09:00:00\"},\"Arrival\":{\"IATA\":\"PVG\",\"Airport\":\"上海浦东\",\"DateTime\":\"2018-09-29T11:15:00\",\"Date\":\"2018-09-29\",\"Time\":\"11:15:00\"},\"AirlineList\":{\"Airline\":[{\"MarketingAirline\":\"KN\"}]}},\"FlightLeg\":[{\"InfoSource\":\"Domestic\",\"Loyalty\":\"\",\"FlightLegRPH\":\"1\",\"Duration\":\"2:15\",\"CabinClass\":\"W\",\"FlightNumber\":\"5977\",\"OnlineCheckin\":\"false\",\"Departure\":{\"IATA\":\"NAY\",\"Airport\":\"北京南苑\",\"TS_CityCode\":\"NAY\",\"Terminal\":\"--\",\"DateTime\":\"2018-09-29T09:00:00\",\"Date\":\"2018-09-29\",\"Time\":\"09:00:00\"},\"Arrival\":{\"IATA\":\"PVG\",\"Airport\":\"上海浦东\",\"TS_CityCode\":\"PVG\",\"Terminal\":\"T1\",\"DateTime\":\"2018-09-29T11:15:00\",\"Date\":\"2018-09-29\",\"Time\":\"11:15:00\"},\"MarketingAirline\":\"KN\",\"OperatingAirline\":\"KN\",\"Equipment\":{\"AirEquipType\":\"73V\"},\"BookingClassAvails\":{\"BookingClassAvail\":[{\"RPH\":\"000\",\"ResBookDesigCode\":\"W\",\"ResBookDesigQuantity\":\"A\"},{\"RPH\":\"001\",\"ResBookDesigCode\":\"Y\",\"ResBookDesigQuantity\":\"A\"},{\"RPH\":\"002\",\"ResBookDesigCode\":\"B\",\"ResBookDesigQuantity\":\"A\"},{\"RPH\":\"003\",\"ResBookDesigCode\":\"M\",\"ResBookDesigQuantity\":\"A\"},{\"RPH\":\"004\",\"ResBookDesigCode\":\"A\",\"ResBookDesigQuantity\":\"A\"},{\"RPH\":\"005\",\"ResBookDesigCode\":\"E\",\"ResBookDesigQuantity\":\"A\"},{\"RPH\":\"006\",\"ResBookDesigCode\":\"H\",\"ResBookDesigQuantity\":\"A\"},{\"RPH\":\"007\",\"ResBookDesigCode\":\"K\",\"ResBookDesigQuantity\":\"A\"},{\"RPH\":\"008\",\"ResBookDesigCode\":\"L\",\"ResBookDesigQuantity\":\"A\"},{\"RPH\":\"009\",\"ResBookDesigCode\":\"N\",\"ResBookDesigQuantity\":\"A\"},{\"RPH\":\"0010\",\"ResBookDesigCode\":\"D\",\"ResBookDesigQuantity\":\"A\"},{\"RPH\":\"0011\",\"ResBookDesigCode\":\"R\",\"ResBookDesigQuantity\":\"A\"}]},\"Comment\":[{\"CharSet\":\"\",\"Language\":\"zh\",\"Type\":\"OnlineCheckin\"}]}]}],\"Price\":{\"FareBreakdowns\":[{\"FareFamilyCode\":\"BESTSELLING\",\"FareFamilyName\":\"舒心飞\",\"Seq\":\"1\",\"Total\":[{\"Currency\":\"CNY\",\"Amount\":\"1358\"}],\"FareBreakdown\":[{\"PassengerTypeQuantity\":{\"Code\":\"ADT\",\"Quantity\":\"1\"},\"PassengerFare\":{\"BaseFare\":{\"Amount\":\"1358.0\",\"Currency\":\"CNY\"},\"Taxes\":{\"Tax\":[{\"Amount\":\"0.0\",\"Currency\":\"CNY\",\"TaxCode\":\"CN\"},{\"Amount\":\"0.0\",\"Currency\":\"CNY\",\"TaxCode\":\"YQ\"}]},\"TotalFare\":{\"Adjusted\":\"1358.0\",\"Amount\":\"1358.0\",\"Currency\":\"CNY\"}}}]},{\"FareFamilyCode\":\"TOURISMROVER\",\"FareFamilyName\":\"大众游\",\"Seq\":\"2\",\"Total\":[{\"Currency\":\"CNY\",\"Amount\":\"1018\"}],\"FareBreakdown\":[{\"PassengerTypeQuantity\":{\"Code\":\"ADT\",\"Quantity\":\"1\"},\"PassengerFare\":{\"BaseFare\":{\"Amount\":\"1018.0\",\"Currency\":\"CNY\"},\"Taxes\":{\"Tax\":[{\"Amount\":\"0.0\",\"Currency\":\"CNY\",\"TaxCode\":\"CN\"},{\"Amount\":\"0.0\",\"Currency\":\"CNY\",\"TaxCode\":\"YQ\"}]},\"TotalFare\":{\"Adjusted\":\"1018.0\",\"Amount\":\"1018.0\",\"Currency\":\"CNY\"}}}]},{\"FareFamilyCode\":\"SPECIAL\",\"FareFamilyName\":\"欢乐抢\",\"Seq\":\"3\",\"Total\":[{\"Currency\":\"CNY\",\"Amount\":\"678\"}],\"FareBreakdown\":[{\"PassengerTypeQuantity\":{\"Code\":\"ADT\",\"Quantity\":\"1\"},\"PassengerFare\":{\"BaseFare\":{\"Amount\":\"678.0\",\"Currency\":\"CNY\"},\"Taxes\":{\"Tax\":[{\"Amount\":\"0.0\",\"Currency\":\"CNY\",\"TaxCode\":\"CN\"},{\"Amount\":\"0.0\",\"Currency\":\"CNY\",\"TaxCode\":\"YQ\"}]},\"TotalFare\":{\"Adjusted\":\"678.0\",\"Amount\":\"678.0\",\"Currency\":\"CNY\"}}}]}],\"FareInfos\":[{\"FareFamilyCode\":\"BESTSELLING\",\"Seq\":\"1\",\"FareInfo\":[{\"FlightSegmentRPH\":\"1\",\"datachange_RPH\":\"\",\"FareReference\":{\"ResBookDesigCode\":\"W\"},\"FilingAirline\":{\"Code\":\"KN\"},\"DepartureAirport\":{\"CodeContext\":\"IATA\",\"LocationCode\":\"NAY\"},\"ArrivalAirport\":{\"CodeContext\":\"IATA\",\"LocationCode\":\"PVG\"},\"FareInfo\":[{\"DisCount\":\"\",\"FareBasis\":\"W\",\"FareSystemId\":\"\",\"GdsEi\":\"\",\"RmkOt\":\"\",\"ZValueKey\":\"\",\"Zkey\":\"\",\"Zvalue\":\"-1.000000\",\"can_dateChange\":\"true\",\"can_refund\":\"true\",\"fareRph\":\"\",\"Fare\":{\"BaseAmount\":\"1358.0\",\"TaxAmount\":\"0.0\",\"TotalFare\":\"1358.0\",\"Taxes\":{\"Tax\":[{\"Amount\":\"0.0\",\"CurrencyCode\":\"CNY\",\"OriginalAmount\":\"0.0\",\"OriginalCurrencyCode\":\"CNY\",\"TaxCode\":\"CN\"},{\"Amount\":\"0.0\",\"CurrencyCode\":\"CNY\",\"OriginalAmount\":\"0.0\",\"OriginalCurrencyCode\":\"CNY\",\"TaxCode\":\"YQ\"}]}},\"PTC\":{\"PassengerTypeCode\":\"ADT\",\"Quantity\":\"1\"},\"TPA_Extensions\":{\"ProductInfo\":{\"CKIN\":\"\",\"EI\":\"\",\"Name\":\"舒心飞\",\"Type\":\"\"},\"FareFamilyInfo\":{\"CabinName\":\"\",\"ChangeToOpen\":\"true\",\"Description\":\"自愿变更:航班规定离站时间前12小时（含）之前提出:收取客票价10%的变更费;航班规定离站时间前12小时至2小时（含）之内提出:收取客票价15%的变更费;航班规定离站时间前2小时之内提出:收取客票价20%的变更费;航班规定离站时间之后提出:收取客票价30%的变更费。自愿退票:航班规定离站时间前12小时（含）之前提出:收取客票价30%的退票费;航班规定离站时间前12小时至2小时（含）之内提出:收取客票价50%的退票费;航班规定离站时间前2小时之内提出：收取客票价80%的退票费;航班规定离站时间之后提出:不允许退票，仅限退还已经代为实际征收的民航发展基金和燃油附加费。\",\"DomesticSelfCheckin\":\"true\",\"FareBasis\":\"W\",\"FareFamilyCode\":\"BESTSELLING\",\"FareFamilyName\":\"舒心飞\",\"InternationalSelfCheckin\":\"true\",\"Upgradable\":\"true\",\"UserType\":\"*\",\"Username\":\"*\",\"Refund\":{\"AfterDepartureRate\":\"1.0\",\"Before0To2\":\"0.8\",\"Before0To2MinAmount\":\"50.0\",\"Before2To12\":\"0.5\",\"Before2To12MinAmount\":\"50.0\",\"BeforeDepartureRate\":\"0.4\",\"BeforeLT12\":\"0.3\",\"BeforeLT12MinAmount\":\"50.0\",\"Eligible\":\"true\",\"MinAmount\":\"50.0\",\"TimeThreshold\":\"-4\",\"Voluntary\":\"true\"},\"Datechange\":{\"AfterDepartureRate\":\"0.3\",\"Before0To2\":\"0.2\",\"Before0To2MinAmount\":\"50.0\",\"Before2To12\":\"0.15\",\"Before2To12MinAmount\":\"50.0\",\"BeforeDepartureRate\":\"0.4\",\"BeforeLT12\":\"0.1\",\"BeforeLT12MinAmount\":\"50.0\",\"Limit\":\"*\",\"MinAmount\":\"50.0\",\"TimeThreshold\":\"-4\",\"Type\":\"percent\"}}},\"Loyalty\":{\"Earn\":{\"Quantity\":\"0\"}}}],\"ProductInfo\":[{\"CKIN\":\"\",\"EI\":\"\",\"Name\":\"舒心飞\",\"Type\":\"\"},{\"CKIN\":\"\",\"EI\":\"\",\"Name\":\"舒心飞\",\"Type\":\"\"}],\"FareFamily\":[{\"Name\":\"舒心飞\",\"Code\":\"BESTSELLING\",\"CabinName\":\"\",\"ChangeToOpen\":\"true\",\"Description\":\"自愿变更:航班规定离站时间前12小时（含）之前提出:收取客票价10%的变更费;航班规定离站时间前12小时至2小时（含）之内提出:收取客票价15%的变更费;航班规定离站时间前2小时之内提出:收取客票价20%的变更费;航班规定离站时间之后提出:收取客票价30%的变更费。自愿退票:航班规定离站时间前12小时（含）之前提出:收取客票价30%的退票费;航班规定离站时间前12小时至2小时（含）之内提出:收取客票价50%的退票费;航班规定离站时间前2小时之内提出：收取客票价80%的退票费;航班规定离站时间之后提出:不允许退票，仅限退还已经代为实际征收的民航发展基金和燃油附加费。\",\"DomesticSelfCheckin\":\"true\",\"FareBasis\":\"W\",\"InternationalSelfCheckin\":\"true\",\"Upgradable\":\"true\",\"UserType\":\"*\",\"Username\":\"*\",\"Refund\":{\"AfterDepartureRate\":\"1.0\",\"Before0To2\":\"0.8\",\"Before0To2MinAmount\":\"50.0\",\"Before2To12\":\"0.5\",\"Before2To12MinAmount\":\"50.0\",\"BeforeDepartureRate\":\"0.4\",\"BeforeLT12\":\"0.3\",\"BeforeLT12MinAmount\":\"50.0\",\"Eligible\":\"true\",\"MinAmount\":\"50.0\",\"TimeThreshold\":\"-4\",\"Voluntary\":\"true\"},\"Datechange\":{\"AfterDepartureRate\":\"0.3\",\"Before0To2\":\"0.2\",\"Before0To2MinAmount\":\"50.0\",\"Before2To12\":\"0.15\",\"Before2To12MinAmount\":\"50.0\",\"BeforeDepartureRate\":\"0.4\",\"BeforeLT12\":\"0.1\",\"BeforeLT12MinAmount\":\"50.0\",\"Limit\":\"*\",\"MinAmount\":\"50.0\",\"TimeThreshold\":\"-4\",\"Type\":\"percent\"}}]}]},{\"FareFamilyCode\":\"TOURISMROVER\",\"Seq\":\"2\",\"FareInfo\":[{\"FlightSegmentRPH\":\"1\",\"datachange_RPH\":\"\",\"FareReference\":{\"ResBookDesigCode\":\"H\"},\"FilingAirline\":{\"Code\":\"KN\"},\"DepartureAirport\":{\"CodeContext\":\"IATA\",\"LocationCode\":\"NAY\"},\"ArrivalAirport\":{\"CodeContext\":\"IATA\",\"LocationCode\":\"PVG\"},\"FareInfo\":[{\"DisCount\":\"\",\"FareBasis\":\"H\",\"FareSystemId\":\"\",\"GdsEi\":\"\",\"RmkOt\":\"\",\"ZValueKey\":\"\",\"Zkey\":\"\",\"Zvalue\":\"-1.000000\",\"can_dateChange\":\"true\",\"can_refund\":\"true\",\"fareRph\":\"\",\"Fare\":{\"BaseAmount\":\"1018.0\",\"TaxAmount\":\"0.0\",\"TotalFare\":\"1018.0\",\"Taxes\":{\"Tax\":[{\"Amount\":\"0.0\",\"CurrencyCode\":\"CNY\",\"OriginalAmount\":\"0.0\",\"OriginalCurrencyCode\":\"CNY\",\"TaxCode\":\"CN\"},{\"Amount\":\"0.0\",\"CurrencyCode\":\"CNY\",\"OriginalAmount\":\"0.0\",\"OriginalCurrencyCode\":\"CNY\",\"TaxCode\":\"YQ\"}]}},\"PTC\":{\"PassengerTypeCode\":\"ADT\",\"Quantity\":\"1\"},\"TPA_Extensions\":{\"ProductInfo\":{\"CKIN\":\"\",\"EI\":\"\",\"Name\":\"大众游\",\"Type\":\"\"},\"FareFamilyInfo\":{\"CabinName\":\"\",\"ChangeToOpen\":\"true\",\"Description\":\"自愿变更:航班规定离站时间前12小时（含）之前提出:收取客票价20%的变更费;航班规定离站时间前12小时至2小时（含）之内提出:收取客票价25%的变更费;航班规定离站时间前2小时之内提出:收取客票价30%的变更费;航班规定离站时间之后提出:收取客票价40%的变更费。自愿退票:航班规定离站时间前12小时（含）之前提出:收取客票价50%的退票费;航班规定离站时间前12小时至2小时（含）之内提出:收取客票价70%的退票费;航班规定离站时间前2小时之内提出：收取客票价90%的退票费;航班规定离站时间之后提出:不允许退票，仅限退还已经代为实际征收的民航发展基金和燃油附加费。\",\"DomesticSelfCheckin\":\"true\",\"FareBasis\":\"H\",\"FareFamilyCode\":\"TOURISMROVER\",\"FareFamilyName\":\"大众游\",\"InternationalSelfCheckin\":\"true\",\"Upgradable\":\"true\",\"UserType\":\"*\",\"Username\":\"*\",\"Refund\":{\"AfterDepartureRate\":\"1.0\",\"Before0To2\":\"0.9\",\"Before0To2MinAmount\":\"50.0\",\"Before2To12\":\"0.7\",\"Before2To12MinAmount\":\"50.0\",\"BeforeDepartureRate\":\"0.4\",\"BeforeLT12\":\"0.5\",\"BeforeLT12MinAmount\":\"50.0\",\"Eligible\":\"true\",\"MinAmount\":\"50.0\",\"TimeThreshold\":\"-4\",\"Voluntary\":\"true\"},\"Datechange\":{\"AfterDepartureRate\":\"0.4\",\"Before0To2\":\"0.3\",\"Before0To2MinAmount\":\"50.0\",\"Before2To12\":\"0.25\",\"Before2To12MinAmount\":\"50.0\",\"BeforeDepartureRate\":\"0.4\",\"BeforeLT12\":\"0.2\",\"BeforeLT12MinAmount\":\"50.0\",\"Limit\":\"*\",\"MinAmount\":\"50.0\",\"TimeThreshold\":\"-4\",\"Type\":\"percent\"}}},\"Loyalty\":{\"Earn\":{\"Quantity\":\"0\"}}}],\"ProductInfo\":[{\"CKIN\":\"\",\"EI\":\"\",\"Name\":\"大众游\",\"Type\":\"\"},{\"CKIN\":\"\",\"EI\":\"\",\"Name\":\"大众游\",\"Type\":\"\"}],\"FareFamily\":[{\"Name\":\"大众游\",\"Code\":\"TOURISMROVER\",\"CabinName\":\"\",\"ChangeToOpen\":\"true\",\"Description\":\"自愿变更:航班规定离站时间前12小时（含）之前提出:收取客票价20%的变更费;航班规定离站时间前12小时至2小时（含）之内提出:收取客票价25%的变更费;航班规定离站时间前2小时之内提出:收取客票价30%的变更费;航班规定离站时间之后提出:收取客票价40%的变更费。自愿退票:航班规定离站时间前12小时（含）之前提出:收取客票价50%的退票费;航班规定离站时间前12小时至2小时（含）之内提出:收取客票价70%的退票费;航班规定离站时间前2小时之内提出：收取客票价90%的退票费;航班规定离站时间之后提出:不允许退票，仅限退还已经代为实际征收的民航发展基金和燃油附加费。\",\"DomesticSelfCheckin\":\"true\",\"FareBasis\":\"H\",\"InternationalSelfCheckin\":\"true\",\"Upgradable\":\"true\",\"UserType\":\"*\",\"Username\":\"*\",\"Refund\":{\"AfterDepartureRate\":\"1.0\",\"Before0To2\":\"0.9\",\"Before0To2MinAmount\":\"50.0\",\"Before2To12\":\"0.7\",\"Before2To12MinAmount\":\"50.0\",\"BeforeDepartureRate\":\"0.4\",\"BeforeLT12\":\"0.5\",\"BeforeLT12MinAmount\":\"50.0\",\"Eligible\":\"true\",\"MinAmount\":\"50.0\",\"TimeThreshold\":\"-4\",\"Voluntary\":\"true\"},\"Datechange\":{\"AfterDepartureRate\":\"0.4\",\"Before0To2\":\"0.3\",\"Before0To2MinAmount\":\"50.0\",\"Before2To12\":\"0.25\",\"Before2To12MinAmount\":\"50.0\",\"BeforeDepartureRate\":\"0.4\",\"BeforeLT12\":\"0.2\",\"BeforeLT12MinAmount\":\"50.0\",\"Limit\":\"*\",\"MinAmount\":\"50.0\",\"TimeThreshold\":\"-4\",\"Type\":\"percent\"}}]}]},{\"FareFamilyCode\":\"SPECIAL\",\"Seq\":\"3\",\"FareInfo\":[{\"FlightSegmentRPH\":\"1\",\"datachange_RPH\":\"\",\"FareReference\":{\"ResBookDesigCode\":\"R\"},\"FilingAirline\":{\"Code\":\"KN\"},\"DepartureAirport\":{\"CodeContext\":\"IATA\",\"LocationCode\":\"NAY\"},\"ArrivalAirport\":{\"CodeContext\":\"IATA\",\"LocationCode\":\"PVG\"},\"FareInfo\":[{\"DisCount\":\"\",\"FareBasis\":\"R\",\"FareSystemId\":\"\",\"GdsEi\":\"\",\"RmkOt\":\"\",\"ZValueKey\":\"\",\"Zkey\":\"\",\"Zvalue\":\"-1.000000\",\"can_dateChange\":\"false\",\"can_refund\":\"false\",\"fareRph\":\"\",\"Fare\":{\"BaseAmount\":\"678.0\",\"TaxAmount\":\"0.0\",\"TotalFare\":\"678.0\",\"Taxes\":{\"Tax\":[{\"Amount\":\"0.0\",\"CurrencyCode\":\"CNY\",\"OriginalAmount\":\"0.0\",\"OriginalCurrencyCode\":\"CNY\",\"TaxCode\":\"CN\"},{\"Amount\":\"0.0\",\"CurrencyCode\":\"CNY\",\"OriginalAmount\":\"0.0\",\"OriginalCurrencyCode\":\"CNY\",\"TaxCode\":\"YQ\"}]}},\"PTC\":{\"PassengerTypeCode\":\"ADT\",\"Quantity\":\"1\"},\"TPA_Extensions\":{\"ProductInfo\":{\"CKIN\":\"\",\"EI\":\"\",\"Name\":\"欢乐抢\",\"Type\":\"\"},\"FareFamilyInfo\":{\"CabinName\":\"\",\"ChangeToOpen\":\"true\",\"Description\":\"自愿变更:不允许变更;自愿退票:不允许退票\",\"DomesticSelfCheckin\":\"true\",\"FareBasis\":\"R\",\"FareFamilyCode\":\"SPECIAL\",\"FareFamilyName\":\"欢乐抢\",\"InternationalSelfCheckin\":\"true\",\"Upgradable\":\"true\",\"UserType\":\"*\",\"Username\":\"*\",\"Refund\":{\"AfterDepartureRate\":\"1.0\",\"Before0To2\":\"1.0\",\"Before0To2MinAmount\":\"50.0\",\"Before2To12\":\"1.0\",\"Before2To12MinAmount\":\"50.0\",\"BeforeDepartureRate\":\"0.4\",\"BeforeLT12\":\"1.0\",\"BeforeLT12MinAmount\":\"50.0\",\"Eligible\":\"true\",\"MinAmount\":\"50.0\",\"TimeThreshold\":\"-4\",\"Voluntary\":\"true\"},\"Datechange\":{\"AfterDepartureRate\":\"1.0\",\"Before0To2\":\"1.0\",\"Before0To2MinAmount\":\"50.0\",\"Before2To12\":\"1.0\",\"Before2To12MinAmount\":\"50.0\",\"BeforeDepartureRate\":\"0.4\",\"BeforeLT12\":\"1.0\",\"BeforeLT12MinAmount\":\"50.0\",\"Limit\":\"*\",\"MinAmount\":\"50.0\",\"TimeThreshold\":\"-4\",\"Type\":\"percent\"}}},\"Loyalty\":{\"Earn\":{\"Quantity\":\"0\"}}}],\"ProductInfo\":[{\"CKIN\":\"\",\"EI\":\"\",\"Name\":\"欢乐抢\",\"Type\":\"\"},{\"CKIN\":\"\",\"EI\":\"\",\"Name\":\"欢乐抢\",\"Type\":\"\"}],\"FareFamily\":[{\"Name\":\"欢乐抢\",\"Code\":\"SPECIAL\",\"CabinName\":\"\",\"ChangeToOpen\":\"true\",\"Description\":\"自愿变更:不允许变更;自愿退票:不允许退票\",\"DomesticSelfCheckin\":\"true\",\"FareBasis\":\"R\",\"InternationalSelfCheckin\":\"true\",\"Upgradable\":\"true\",\"UserType\":\"*\",\"Username\":\"*\",\"Refund\":{\"AfterDepartureRate\":\"1.0\",\"Before0To2\":\"1.0\",\"Before0To2MinAmount\":\"50.0\",\"Before2To12\":\"1.0\",\"Before2To12MinAmount\":\"50.0\",\"BeforeDepartureRate\":\"0.4\",\"BeforeLT12\":\"1.0\",\"BeforeLT12MinAmount\":\"50.0\",\"Eligible\":\"true\",\"MinAmount\":\"50.0\",\"TimeThreshold\":\"-4\",\"Voluntary\":\"true\"},\"Datechange\":{\"AfterDepartureRate\":\"1.0\",\"Before0To2\":\"1.0\",\"Before0To2MinAmount\":\"50.0\",\"Before2To12\":\"1.0\",\"Before2To12MinAmount\":\"50.0\",\"BeforeDepartureRate\":\"0.4\",\"BeforeLT12\":\"1.0\",\"BeforeLT12MinAmount\":\"50.0\",\"Limit\":\"*\",\"MinAmount\":\"50.0\",\"TimeThreshold\":\"-4\",\"Type\":\"percent\"}}]}]}]}},{\"NumAlts\":\"4\",\"SequenceNumber\":\"2\",\"SupplierCode\":\"TSDF\",\"CanBinCode\":\"Y\",\"OriginDestinationRPH\":\"\",\"FlightDetails\":[{\"Code\":\"KN\",\"Duration\":\"2:10\",\"DirectionInd\":\"OneWay\",\"JourneyId\":\"1\",\"Summary\":{\"CabinClass\":\"WW\",\"Departure\":{\"IATA\":\"NAY\",\"Airport\":\"北京南苑\",\"DateTime\":\"2018-09-29T13:40:00\",\"Date\":\"2018-09-29\",\"Time\":\"13:40:00\"},\"Arrival\":{\"IATA\":\"PVG\",\"Airport\":\"上海浦东\",\"DateTime\":\"2018-09-29T15:50:00\",\"Date\":\"2018-09-29\",\"Time\":\"15:50:00\"},\"AirlineList\":{\"Airline\":[{\"MarketingAirline\":\"KN\"}]}},\"FlightLeg\":[{\"InfoSource\":\"Domestic\",\"Loyalty\":\"\",\"FlightLegRPH\":\"1\",\"Duration\":\"2:10\",\"CabinClass\":\"W\",\"FlightNumber\":\"5981\",\"OnlineCheckin\":\"false\",\"Departure\":{\"IATA\":\"NAY\",\"Airport\":\"北京南苑\",\"TS_CityCode\":\"NAY\",\"Terminal\":\"--\",\"DateTime\":\"2018-09-29T13:40:00\",\"Date\":\"2018-09-29\",\"Time\":\"13:40:00\"},\"Arrival\":{\"IATA\":\"PVG\",\"Airport\":\"上海浦东\",\"TS_CityCode\":\"PVG\",\"Terminal\":\"T1\",\"DateTime\":\"2018-09-29T15:50:00\",\"Date\":\"2018-09-29\",\"Time\":\"15:50:00\"},\"MarketingAirline\":\"KN\",\"OperatingAirline\":\"KN\",\"Equipment\":{\"AirEquipType\":\"73V\"},\"BookingClassAvails\":{\"BookingClassAvail\":[{\"RPH\":\"000\",\"ResBookDesigCode\":\"W\",\"ResBookDesigQuantity\":\"A\"},{\"RPH\":\"001\",\"ResBookDesigCode\":\"Y\",\"ResBookDesigQuantity\":\"A\"},{\"RPH\":\"002\",\"ResBookDesigCode\":\"B\",\"ResBookDesigQuantity\":\"A\"},{\"RPH\":\"003\",\"ResBookDesigCode\":\"M\",\"ResBookDesigQuantity\":\"A\"},{\"RPH\":\"004\",\"ResBookDesigCode\":\"A\",\"ResBookDesigQuantity\":\"A\"},{\"RPH\":\"005\",\"ResBookDesigCode\":\"E\",\"ResBookDesigQuantity\":\"A\"},{\"RPH\":\"006\",\"ResBookDesigCode\":\"H\",\"ResBookDesigQuantity\":\"A\"},{\"RPH\":\"007\",\"ResBookDesigCode\":\"K\",\"ResBookDesigQuantity\":\"A\"},{\"RPH\":\"008\",\"ResBookDesigCode\":\"L\",\"ResBookDesigQuantity\":\"A\"},{\"RPH\":\"009\",\"ResBookDesigCode\":\"N\",\"ResBookDesigQuantity\":\"A\"},{\"RPH\":\"0010\",\"ResBookDesigCode\":\"D\",\"ResBookDesigQuantity\":\"A\"},{\"RPH\":\"0011\",\"ResBookDesigCode\":\"R\",\"ResBookDesigQuantity\":\"A\"}]},\"Comment\":[{\"CharSet\":\"\",\"Language\":\"zh\",\"Type\":\"OnlineCheckin\"}]}]}],\"Price\":{\"FareBreakdowns\":[{\"FareFamilyCode\":\"BESTSELLING\",\"FareFamilyName\":\"舒心飞\",\"Seq\":\"1\",\"Total\":[{\"Currency\":\"CNY\",\"Amount\":\"1358\"}],\"FareBreakdown\":[{\"PassengerTypeQuantity\":{\"Code\":\"ADT\",\"Quantity\":\"1\"},\"PassengerFare\":{\"BaseFare\":{\"Amount\":\"1358.0\",\"Currency\":\"CNY\"},\"Taxes\":{\"Tax\":[{\"Amount\":\"0.0\",\"Currency\":\"CNY\",\"TaxCode\":\"CN\"},{\"Amount\":\"0.0\",\"Currency\":\"CNY\",\"TaxCode\":\"YQ\"}]},\"TotalFare\":{\"Adjusted\":\"1358.0\",\"Amount\":\"1358.0\",\"Currency\":\"CNY\"}}}]},{\"FareFamilyCode\":\"TOURISMROVER\",\"FareFamilyName\":\"大众游\",\"Seq\":\"2\",\"Total\":[{\"Currency\":\"CNY\",\"Amount\":\"1018\"}],\"FareBreakdown\":[{\"PassengerTypeQuantity\":{\"Code\":\"ADT\",\"Quantity\":\"1\"},\"PassengerFare\":{\"BaseFare\":{\"Amount\":\"1018.0\",\"Currency\":\"CNY\"},\"Taxes\":{\"Tax\":[{\"Amount\":\"0.0\",\"Currency\":\"CNY\",\"TaxCode\":\"CN\"},{\"Amount\":\"0.0\",\"Currency\":\"CNY\",\"TaxCode\":\"YQ\"}]},\"TotalFare\":{\"Adjusted\":\"1018.0\",\"Amount\":\"1018.0\",\"Currency\":\"CNY\"}}}]},{\"FareFamilyCode\":\"SPECIAL\",\"FareFamilyName\":\"欢乐抢\",\"Seq\":\"3\",\"Total\":[{\"Currency\":\"CNY\",\"Amount\":\"678\"}],\"FareBreakdown\":[{\"PassengerTypeQuantity\":{\"Code\":\"ADT\",\"Quantity\":\"1\"},\"PassengerFare\":{\"BaseFare\":{\"Amount\":\"678.0\",\"Currency\":\"CNY\"},\"Taxes\":{\"Tax\":[{\"Amount\":\"0.0\",\"Currency\":\"CNY\",\"TaxCode\":\"CN\"},{\"Amount\":\"0.0\",\"Currency\":\"CNY\",\"TaxCode\":\"YQ\"}]},\"TotalFare\":{\"Adjusted\":\"678.0\",\"Amount\":\"678.0\",\"Currency\":\"CNY\"}}}]}],\"FareInfos\":[{\"FareFamilyCode\":\"BESTSELLING\",\"Seq\":\"1\",\"FareInfo\":[{\"FlightSegmentRPH\":\"1\",\"datachange_RPH\":\"\",\"FareReference\":{\"ResBookDesigCode\":\"W\"},\"FilingAirline\":{\"Code\":\"KN\"},\"DepartureAirport\":{\"CodeContext\":\"IATA\",\"LocationCode\":\"NAY\"},\"ArrivalAirport\":{\"CodeContext\":\"IATA\",\"LocationCode\":\"PVG\"},\"FareInfo\":[{\"DisCount\":\"\",\"FareBasis\":\"W\",\"FareSystemId\":\"\",\"GdsEi\":\"\",\"RmkOt\":\"\",\"ZValueKey\":\"\",\"Zkey\":\"\",\"Zvalue\":\"-1.000000\",\"can_dateChange\":\"true\",\"can_refund\":\"true\",\"fareRph\":\"\",\"Fare\":{\"BaseAmount\":\"1358.0\",\"TaxAmount\":\"0.0\",\"TotalFare\":\"1358.0\",\"Taxes\":{\"Tax\":[{\"Amount\":\"0.0\",\"CurrencyCode\":\"CNY\",\"OriginalAmount\":\"0.0\",\"OriginalCurrencyCode\":\"CNY\",\"TaxCode\":\"CN\"},{\"Amount\":\"0.0\",\"CurrencyCode\":\"CNY\",\"OriginalAmount\":\"0.0\",\"OriginalCurrencyCode\":\"CNY\",\"TaxCode\":\"YQ\"}]}},\"PTC\":{\"PassengerTypeCode\":\"ADT\",\"Quantity\":\"1\"},\"TPA_Extensions\":{\"ProductInfo\":{\"CKIN\":\"\",\"EI\":\"\",\"Name\":\"舒心飞\",\"Type\":\"\"},\"FareFamilyInfo\":{\"CabinName\":\"\",\"ChangeToOpen\":\"true\",\"Description\":\"自愿变更:航班规定离站时间前12小时（含）之前提出:收取客票价10%的变更费;航班规定离站时间前12小时至2小时（含）之内提出:收取客票价15%的变更费;航班规定离站时间前2小时之内提出:收取客票价20%的变更费;航班规定离站时间之后提出:收取客票价30%的变更费。自愿退票:航班规定离站时间前12小时（含）之前提出:收取客票价30%的退票费;航班规定离站时间前12小时至2小时（含）之内提出:收取客票价50%的退票费;航班规定离站时间前2小时之内提出：收取客票价80%的退票费;航班规定离站时间之后提出:不允许退票，仅限退还已经代为实际征收的民航发展基金和燃油附加费。\",\"DomesticSelfCheckin\":\"true\",\"FareBasis\":\"W\",\"FareFamilyCode\":\"BESTSELLING\",\"FareFamilyName\":\"舒心飞\",\"InternationalSelfCheckin\":\"true\",\"Upgradable\":\"true\",\"UserType\":\"*\",\"Username\":\"*\",\"Refund\":{\"AfterDepartureRate\":\"1.0\",\"Before0To2\":\"0.8\",\"Before0To2MinAmount\":\"50.0\",\"Before2To12\":\"0.5\",\"Before2To12MinAmount\":\"50.0\",\"BeforeDepartureRate\":\"0.4\",\"BeforeLT12\":\"0.3\",\"BeforeLT12MinAmount\":\"50.0\",\"Eligible\":\"true\",\"MinAmount\":\"50.0\",\"TimeThreshold\":\"-4\",\"Voluntary\":\"true\"},\"Datechange\":{\"AfterDepartureRate\":\"0.3\",\"Before0To2\":\"0.2\",\"Before0To2MinAmount\":\"50.0\",\"Before2To12\":\"0.15\",\"Before2To12MinAmount\":\"50.0\",\"BeforeDepartureRate\":\"0.4\",\"BeforeLT12\":\"0.1\",\"BeforeLT12MinAmount\":\"50.0\",\"Limit\":\"*\",\"MinAmount\":\"50.0\",\"TimeThreshold\":\"-4\",\"Type\":\"percent\"}}},\"Loyalty\":{\"Earn\":{\"Quantity\":\"0\"}}}],\"ProductInfo\":[{\"CKIN\":\"\",\"EI\":\"\",\"Name\":\"舒心飞\",\"Type\":\"\"},{\"CKIN\":\"\",\"EI\":\"\",\"Name\":\"舒心飞\",\"Type\":\"\"}],\"FareFamily\":[{\"Name\":\"舒心飞\",\"Code\":\"BESTSELLING\",\"CabinName\":\"\",\"ChangeToOpen\":\"true\",\"Description\":\"自愿变更:航班规定离站时间前12小时（含）之前提出:收取客票价10%的变更费;航班规定离站时间前12小时至2小时（含）之内提出:收取客票价15%的变更费;航班规定离站时间前2小时之内提出:收取客票价20%的变更费;航班规定离站时间之后提出:收取客票价30%的变更费。自愿退票:航班规定离站时间前12小时（含）之前提出:收取客票价30%的退票费;航班规定离站时间前12小时至2小时（含）之内提出:收取客票价50%的退票费;航班规定离站时间前2小时之内提出：收取客票价80%的退票费;航班规定离站时间之后提出:不允许退票，仅限退还已经代为实际征收的民航发展基金和燃油附加费。\",\"DomesticSelfCheckin\":\"true\",\"FareBasis\":\"W\",\"InternationalSelfCheckin\":\"true\",\"Upgradable\":\"true\",\"UserType\":\"*\",\"Username\":\"*\",\"Refund\":{\"AfterDepartureRate\":\"1.0\",\"Before0To2\":\"0.8\",\"Before0To2MinAmount\":\"50.0\",\"Before2To12\":\"0.5\",\"Before2To12MinAmount\":\"50.0\",\"BeforeDepartureRate\":\"0.4\",\"BeforeLT12\":\"0.3\",\"BeforeLT12MinAmount\":\"50.0\",\"Eligible\":\"true\",\"MinAmount\":\"50.0\",\"TimeThreshold\":\"-4\",\"Voluntary\":\"true\"},\"Datechange\":{\"AfterDepartureRate\":\"0.3\",\"Before0To2\":\"0.2\",\"Before0To2MinAmount\":\"50.0\",\"Before2To12\":\"0.15\",\"Before2To12MinAmount\":\"50.0\",\"BeforeDepartureRate\":\"0.4\",\"BeforeLT12\":\"0.1\",\"BeforeLT12MinAmount\":\"50.0\",\"Limit\":\"*\",\"MinAmount\":\"50.0\",\"TimeThreshold\":\"-4\",\"Type\":\"percent\"}}]}]},{\"FareFamilyCode\":\"TOURISMROVER\",\"Seq\":\"2\",\"FareInfo\":[{\"FlightSegmentRPH\":\"1\",\"datachange_RPH\":\"\",\"FareReference\":{\"ResBookDesigCode\":\"H\"},\"FilingAirline\":{\"Code\":\"KN\"},\"DepartureAirport\":{\"CodeContext\":\"IATA\",\"LocationCode\":\"NAY\"},\"ArrivalAirport\":{\"CodeContext\":\"IATA\",\"LocationCode\":\"PVG\"},\"FareInfo\":[{\"DisCount\":\"\",\"FareBasis\":\"H\",\"FareSystemId\":\"\",\"GdsEi\":\"\",\"RmkOt\":\"\",\"ZValueKey\":\"\",\"Zkey\":\"\",\"Zvalue\":\"-1.000000\",\"can_dateChange\":\"true\",\"can_refund\":\"true\",\"fareRph\":\"\",\"Fare\":{\"BaseAmount\":\"1018.0\",\"TaxAmount\":\"0.0\",\"TotalFare\":\"1018.0\",\"Taxes\":{\"Tax\":[{\"Amount\":\"0.0\",\"CurrencyCode\":\"CNY\",\"OriginalAmount\":\"0.0\",\"OriginalCurrencyCode\":\"CNY\",\"TaxCode\":\"CN\"},{\"Amount\":\"0.0\",\"CurrencyCode\":\"CNY\",\"OriginalAmount\":\"0.0\",\"OriginalCurrencyCode\":\"CNY\",\"TaxCode\":\"YQ\"}]}},\"PTC\":{\"PassengerTypeCode\":\"ADT\",\"Quantity\":\"1\"},\"TPA_Extensions\":{\"ProductInfo\":{\"CKIN\":\"\",\"EI\":\"\",\"Name\":\"大众游\",\"Type\":\"\"},\"FareFamilyInfo\":{\"CabinName\":\"\",\"ChangeToOpen\":\"true\",\"Description\":\"自愿变更:航班规定离站时间前12小时（含）之前提出:收取客票价20%的变更费;航班规定离站时间前12小时至2小时（含）之内提出:收取客票价25%的变更费;航班规定离站时间前2小时之内提出:收取客票价30%的变更费;航班规定离站时间之后提出:收取客票价40%的变更费。自愿退票:航班规定离站时间前12小时（含）之前提出:收取客票价50%的退票费;航班规定离站时间前12小时至2小时（含）之内提出:收取客票价70%的退票费;航班规定离站时间前2小时之内提出：收取客票价90%的退票费;航班规定离站时间之后提出:不允许退票，仅限退还已经代为实际征收的民航发展基金和燃油附加费。\",\"DomesticSelfCheckin\":\"true\",\"FareBasis\":\"H\",\"FareFamilyCode\":\"TOURISMROVER\",\"FareFamilyName\":\"大众游\",\"InternationalSelfCheckin\":\"true\",\"Upgradable\":\"true\",\"UserType\":\"*\",\"Username\":\"*\",\"Refund\":{\"AfterDepartureRate\":\"1.0\",\"Before0To2\":\"0.9\",\"Before0To2MinAmount\":\"50.0\",\"Before2To12\":\"0.7\",\"Before2To12MinAmount\":\"50.0\",\"BeforeDepartureRate\":\"0.4\",\"BeforeLT12\":\"0.5\",\"BeforeLT12MinAmount\":\"50.0\",\"Eligible\":\"true\",\"MinAmount\":\"50.0\",\"TimeThreshold\":\"-4\",\"Voluntary\":\"true\"},\"Datechange\":{\"AfterDepartureRate\":\"0.4\",\"Before0To2\":\"0.3\",\"Before0To2MinAmount\":\"50.0\",\"Before2To12\":\"0.25\",\"Before2To12MinAmount\":\"50.0\",\"BeforeDepartureRate\":\"0.4\",\"BeforeLT12\":\"0.2\",\"BeforeLT12MinAmount\":\"50.0\",\"Limit\":\"*\",\"MinAmount\":\"50.0\",\"TimeThreshold\":\"-4\",\"Type\":\"percent\"}}},\"Loyalty\":{\"Earn\":{\"Quantity\":\"0\"}}}],\"ProductInfo\":[{\"CKIN\":\"\",\"EI\":\"\",\"Name\":\"大众游\",\"Type\":\"\"},{\"CKIN\":\"\",\"EI\":\"\",\"Name\":\"大众游\",\"Type\":\"\"}],\"FareFamily\":[{\"Name\":\"大众游\",\"Code\":\"TOURISMROVER\",\"CabinName\":\"\",\"ChangeToOpen\":\"true\",\"Description\":\"自愿变更:航班规定离站时间前12小时（含）之前提出:收取客票价20%的变更费;航班规定离站时间前12小时至2小时（含）之内提出:收取客票价25%的变更费;航班规定离站时间前2小时之内提出:收取客票价30%的变更费;航班规定离站时间之后提出:收取客票价40%的变更费。自愿退票:航班规定离站时间前12小时（含）之前提出:收取客票价50%的退票费;航班规定离站时间前12小时至2小时（含）之内提出:收取客票价70%的退票费;航班规定离站时间前2小时之内提出：收取客票价90%的退票费;航班规定离站时间之后提出:不允许退票，仅限退还已经代为实际征收的民航发展基金和燃油附加费。\",\"DomesticSelfCheckin\":\"true\",\"FareBasis\":\"H\",\"InternationalSelfCheckin\":\"true\",\"Upgradable\":\"true\",\"UserType\":\"*\",\"Username\":\"*\",\"Refund\":{\"AfterDepartureRate\":\"1.0\",\"Before0To2\":\"0.9\",\"Before0To2MinAmount\":\"50.0\",\"Before2To12\":\"0.7\",\"Before2To12MinAmount\":\"50.0\",\"BeforeDepartureRate\":\"0.4\",\"BeforeLT12\":\"0.5\",\"BeforeLT12MinAmount\":\"50.0\",\"Eligible\":\"true\",\"MinAmount\":\"50.0\",\"TimeThreshold\":\"-4\",\"Voluntary\":\"true\"},\"Datechange\":{\"AfterDepartureRate\":\"0.4\",\"Before0To2\":\"0.3\",\"Before0To2MinAmount\":\"50.0\",\"Before2To12\":\"0.25\",\"Before2To12MinAmount\":\"50.0\",\"BeforeDepartureRate\":\"0.4\",\"BeforeLT12\":\"0.2\",\"BeforeLT12MinAmount\":\"50.0\",\"Limit\":\"*\",\"MinAmount\":\"50.0\",\"TimeThreshold\":\"-4\",\"Type\":\"percent\"}}]}]},{\"FareFamilyCode\":\"SPECIAL\",\"Seq\":\"3\",\"FareInfo\":[{\"FlightSegmentRPH\":\"1\",\"datachange_RPH\":\"\",\"FareReference\":{\"ResBookDesigCode\":\"R\"},\"FilingAirline\":{\"Code\":\"KN\"},\"DepartureAirport\":{\"CodeContext\":\"IATA\",\"LocationCode\":\"NAY\"},\"ArrivalAirport\":{\"CodeContext\":\"IATA\",\"LocationCode\":\"PVG\"},\"FareInfo\":[{\"DisCount\":\"\",\"FareBasis\":\"R\",\"FareSystemId\":\"\",\"GdsEi\":\"\",\"RmkOt\":\"\",\"ZValueKey\":\"\",\"Zkey\":\"\",\"Zvalue\":\"-1.000000\",\"can_dateChange\":\"false\",\"can_refund\":\"false\",\"fareRph\":\"\",\"Fare\":{\"BaseAmount\":\"678.0\",\"TaxAmount\":\"0.0\",\"TotalFare\":\"678.0\",\"Taxes\":{\"Tax\":[{\"Amount\":\"0.0\",\"CurrencyCode\":\"CNY\",\"OriginalAmount\":\"0.0\",\"OriginalCurrencyCode\":\"CNY\",\"TaxCode\":\"CN\"},{\"Amount\":\"0.0\",\"CurrencyCode\":\"CNY\",\"OriginalAmount\":\"0.0\",\"OriginalCurrencyCode\":\"CNY\",\"TaxCode\":\"YQ\"}]}},\"PTC\":{\"PassengerTypeCode\":\"ADT\",\"Quantity\":\"1\"},\"TPA_Extensions\":{\"ProductInfo\":{\"CKIN\":\"\",\"EI\":\"\",\"Name\":\"欢乐抢\",\"Type\":\"\"},\"FareFamilyInfo\":{\"CabinName\":\"\",\"ChangeToOpen\":\"true\",\"Description\":\"自愿变更:不允许变更;自愿退票:不允许退票\",\"DomesticSelfCheckin\":\"true\",\"FareBasis\":\"R\",\"FareFamilyCode\":\"SPECIAL\",\"FareFamilyName\":\"欢乐抢\",\"InternationalSelfCheckin\":\"true\",\"Upgradable\":\"true\",\"UserType\":\"*\",\"Username\":\"*\",\"Refund\":{\"AfterDepartureRate\":\"1.0\",\"Before0To2\":\"1.0\",\"Before0To2MinAmount\":\"50.0\",\"Before2To12\":\"1.0\",\"Before2To12MinAmount\":\"50.0\",\"BeforeDepartureRate\":\"0.4\",\"BeforeLT12\":\"1.0\",\"BeforeLT12MinAmount\":\"50.0\",\"Eligible\":\"true\",\"MinAmount\":\"50.0\",\"TimeThreshold\":\"-4\",\"Voluntary\":\"true\"},\"Datechange\":{\"AfterDepartureRate\":\"1.0\",\"Before0To2\":\"1.0\",\"Before0To2MinAmount\":\"50.0\",\"Before2To12\":\"1.0\",\"Before2To12MinAmount\":\"50.0\",\"BeforeDepartureRate\":\"0.4\",\"BeforeLT12\":\"1.0\",\"BeforeLT12MinAmount\":\"50.0\",\"Limit\":\"*\",\"MinAmount\":\"50.0\",\"TimeThreshold\":\"-4\",\"Type\":\"percent\"}}},\"Loyalty\":{\"Earn\":{\"Quantity\":\"0\"}}}],\"ProductInfo\":[{\"CKIN\":\"\",\"EI\":\"\",\"Name\":\"欢乐抢\",\"Type\":\"\"},{\"CKIN\":\"\",\"EI\":\"\",\"Name\":\"欢乐抢\",\"Type\":\"\"}],\"FareFamily\":[{\"Name\":\"欢乐抢\",\"Code\":\"SPECIAL\",\"CabinName\":\"\",\"ChangeToOpen\":\"true\",\"Description\":\"自愿变更:不允许变更;自愿退票:不允许退票\",\"DomesticSelfCheckin\":\"true\",\"FareBasis\":\"R\",\"InternationalSelfCheckin\":\"true\",\"Upgradable\":\"true\",\"UserType\":\"*\",\"Username\":\"*\",\"Refund\":{\"AfterDepartureRate\":\"1.0\",\"Before0To2\":\"1.0\",\"Before0To2MinAmount\":\"50.0\",\"Before2To12\":\"1.0\",\"Before2To12MinAmount\":\"50.0\",\"BeforeDepartureRate\":\"0.4\",\"BeforeLT12\":\"1.0\",\"BeforeLT12MinAmount\":\"50.0\",\"Eligible\":\"true\",\"MinAmount\":\"50.0\",\"TimeThreshold\":\"-4\",\"Voluntary\":\"true\"},\"Datechange\":{\"AfterDepartureRate\":\"1.0\",\"Before0To2\":\"1.0\",\"Before0To2MinAmount\":\"50.0\",\"Before2To12\":\"1.0\",\"Before2To12MinAmount\":\"50.0\",\"BeforeDepartureRate\":\"0.4\",\"BeforeLT12\":\"1.0\",\"BeforeLT12MinAmount\":\"50.0\",\"Limit\":\"*\",\"MinAmount\":\"50.0\",\"TimeThreshold\":\"-4\",\"Type\":\"percent\"}}]}]}]}},{\"NumAlts\":\"4\",\"SequenceNumber\":\"3\",\"SupplierCode\":\"TSDF\",\"CanBinCode\":\"Y\",\"OriginDestinationRPH\":\"\",\"FlightDetails\":[{\"Code\":\"KN\",\"Duration\":\"2:20\",\"DirectionInd\":\"OneWay\",\"JourneyId\":\"1\",\"Summary\":{\"CabinClass\":\"WW\",\"Departure\":{\"IATA\":\"NAY\",\"Airport\":\"北京南苑\",\"DateTime\":\"2018-09-29T15:15:00\",\"Date\":\"2018-09-29\",\"Time\":\"15:15:00\"},\"Arrival\":{\"IATA\":\"SHA\",\"Airport\":\"上海虹桥\",\"DateTime\":\"2018-09-29T17:35:00\",\"Date\":\"2018-09-29\",\"Time\":\"17:35:00\"},\"AirlineList\":{\"Airline\":[{\"MarketingAirline\":\"KN\"}]}},\"FlightLeg\":[{\"InfoSource\":\"Domestic\",\"Loyalty\":\"\",\"FlightLegRPH\":\"1\",\"Duration\":\"2:20\",\"CabinClass\":\"W\",\"FlightNumber\":\"5955\",\"OnlineCheckin\":\"false\",\"Departure\":{\"IATA\":\"NAY\",\"Airport\":\"北京南苑\",\"TS_CityCode\":\"NAY\",\"Terminal\":\"--\",\"DateTime\":\"2018-09-29T15:15:00\",\"Date\":\"2018-09-29\",\"Time\":\"15:15:00\"},\"Arrival\":{\"IATA\":\"SHA\",\"Airport\":\"上海虹桥\",\"TS_CityCode\":\"SHA\",\"Terminal\":\"T2\",\"DateTime\":\"2018-09-29T17:35:00\",\"Date\":\"2018-09-29\",\"Time\":\"17:35:00\"},\"MarketingAirline\":\"KN\",\"OperatingAirline\":\"KN\",\"Equipment\":{\"AirEquipType\":\"73V\"},\"BookingClassAvails\":{\"BookingClassAvail\":[{\"RPH\":\"000\",\"ResBookDesigCode\":\"W\",\"ResBookDesigQuantity\":\"A\"},{\"RPH\":\"001\",\"ResBookDesigCode\":\"Y\",\"ResBookDesigQuantity\":\"A\"},{\"RPH\":\"002\",\"ResBookDesigCode\":\"B\",\"ResBookDesigQuantity\":\"A\"},{\"RPH\":\"003\",\"ResBookDesigCode\":\"M\",\"ResBookDesigQuantity\":\"A\"},{\"RPH\":\"004\",\"ResBookDesigCode\":\"A\",\"ResBookDesigQuantity\":\"A\"},{\"RPH\":\"005\",\"ResBookDesigCode\":\"E\",\"ResBookDesigQuantity\":\"A\"},{\"RPH\":\"006\",\"ResBookDesigCode\":\"H\",\"ResBookDesigQuantity\":\"A\"},{\"RPH\":\"007\",\"ResBookDesigCode\":\"K\",\"ResBookDesigQuantity\":\"A\"},{\"RPH\":\"008\",\"ResBookDesigCode\":\"L\",\"ResBookDesigQuantity\":\"A\"},{\"RPH\":\"009\",\"ResBookDesigCode\":\"N\",\"ResBookDesigQuantity\":\"A\"},{\"RPH\":\"0010\",\"ResBookDesigCode\":\"D\",\"ResBookDesigQuantity\":\"A\"},{\"RPH\":\"0011\",\"ResBookDesigCode\":\"R\",\"ResBookDesigQuantity\":\"A\"},{\"RPH\":\"0012\",\"ResBookDesigCode\":\"S\",\"ResBookDesigQuantity\":\"A\"}]},\"Comment\":[{\"CharSet\":\"\",\"Language\":\"zh\",\"Type\":\"OnlineCheckin\"}]}]}],\"Price\":{\"FareBreakdowns\":[{\"FareFamilyCode\":\"BESTSELLING\",\"FareFamilyName\":\"舒心飞\",\"Seq\":\"1\",\"Total\":[{\"Currency\":\"CNY\",\"Amount\":\"1358\"}],\"FareBreakdown\":[{\"PassengerTypeQuantity\":{\"Code\":\"ADT\",\"Quantity\":\"1\"},\"PassengerFare\":{\"BaseFare\":{\"Amount\":\"1358.0\",\"Currency\":\"CNY\"},\"Taxes\":{\"Tax\":[{\"Amount\":\"0.0\",\"Currency\":\"CNY\",\"TaxCode\":\"CN\"},{\"Amount\":\"0.0\",\"Currency\":\"CNY\",\"TaxCode\":\"YQ\"}]},\"TotalFare\":{\"Adjusted\":\"1358.0\",\"Amount\":\"1358.0\",\"Currency\":\"CNY\"}}}]},{\"FareFamilyCode\":\"TOURISMROVER\",\"FareFamilyName\":\"大众游\",\"Seq\":\"2\",\"Total\":[{\"Currency\":\"CNY\",\"Amount\":\"1018\"}],\"FareBreakdown\":[{\"PassengerTypeQuantity\":{\"Code\":\"ADT\",\"Quantity\":\"1\"},\"PassengerFare\":{\"BaseFare\":{\"Amount\":\"1018.0\",\"Currency\":\"CNY\"},\"Taxes\":{\"Tax\":[{\"Amount\":\"0.0\",\"Currency\":\"CNY\",\"TaxCode\":\"CN\"},{\"Amount\":\"0.0\",\"Currency\":\"CNY\",\"TaxCode\":\"YQ\"}]},\"TotalFare\":{\"Adjusted\":\"1018.0\",\"Amount\":\"1018.0\",\"Currency\":\"CNY\"}}}]},{\"FareFamilyCode\":\"SPECIAL\",\"FareFamilyName\":\"欢乐抢\",\"Seq\":\"3\",\"Total\":[{\"Currency\":\"CNY\",\"Amount\":\"608\"}],\"FareBreakdown\":[{\"PassengerTypeQuantity\":{\"Code\":\"ADT\",\"Quantity\":\"1\"},\"PassengerFare\":{\"BaseFare\":{\"Amount\":\"608.0\",\"Currency\":\"CNY\"},\"Taxes\":{\"Tax\":[{\"Amount\":\"0.0\",\"Currency\":\"CNY\",\"TaxCode\":\"CN\"},{\"Amount\":\"0.0\",\"Currency\":\"CNY\",\"TaxCode\":\"YQ\"}]},\"TotalFare\":{\"Adjusted\":\"608.0\",\"Amount\":\"608.0\",\"Currency\":\"CNY\"}}}]}],\"FareInfos\":[{\"FareFamilyCode\":\"BESTSELLING\",\"Seq\":\"1\",\"FareInfo\":[{\"FlightSegmentRPH\":\"1\",\"datachange_RPH\":\"\",\"FareReference\":{\"ResBookDesigCode\":\"W\"},\"FilingAirline\":{\"Code\":\"KN\"},\"DepartureAirport\":{\"CodeContext\":\"IATA\",\"LocationCode\":\"NAY\"},\"ArrivalAirport\":{\"CodeContext\":\"IATA\",\"LocationCode\":\"SHA\"},\"FareInfo\":[{\"DisCount\":\"\",\"FareBasis\":\"W\",\"FareSystemId\":\"\",\"GdsEi\":\"\",\"RmkOt\":\"\",\"ZValueKey\":\"\",\"Zkey\":\"\",\"Zvalue\":\"-1.000000\",\"can_dateChange\":\"true\",\"can_refund\":\"true\",\"fareRph\":\"\",\"Fare\":{\"BaseAmount\":\"1358.0\",\"TaxAmount\":\"0.0\",\"TotalFare\":\"1358.0\",\"Taxes\":{\"Tax\":[{\"Amount\":\"0.0\",\"CurrencyCode\":\"CNY\",\"OriginalAmount\":\"0.0\",\"OriginalCurrencyCode\":\"CNY\",\"TaxCode\":\"CN\"},{\"Amount\":\"0.0\",\"CurrencyCode\":\"CNY\",\"OriginalAmount\":\"0.0\",\"OriginalCurrencyCode\":\"CNY\",\"TaxCode\":\"YQ\"}]}},\"PTC\":{\"PassengerTypeCode\":\"ADT\",\"Quantity\":\"1\"},\"TPA_Extensions\":{\"ProductInfo\":{\"CKIN\":\"\",\"EI\":\"\",\"Name\":\"舒心飞\",\"Type\":\"\"},\"FareFamilyInfo\":{\"CabinName\":\"\",\"ChangeToOpen\":\"true\",\"Description\":\"自愿变更:航班规定离站时间前12小时（含）之前提出:收取客票价10%的变更费;航班规定离站时间前12小时至2小时（含）之内提出:收取客票价15%的变更费;航班规定离站时间前2小时之内提出:收取客票价20%的变更费;航班规定离站时间之后提出:收取客票价30%的变更费。自愿退票:航班规定离站时间前12小时（含）之前提出:收取客票价30%的退票费;航班规定离站时间前12小时至2小时（含）之内提出:收取客票价50%的退票费;航班规定离站时间前2小时之内提出：收取客票价80%的退票费;航班规定离站时间之后提出:不允许退票，仅限退还已经代为实际征收的民航发展基金和燃油附加费。\",\"DomesticSelfCheckin\":\"true\",\"FareBasis\":\"W\",\"FareFamilyCode\":\"BESTSELLING\",\"FareFamilyName\":\"舒心飞\",\"InternationalSelfCheckin\":\"true\",\"Upgradable\":\"true\",\"UserType\":\"*\",\"Username\":\"*\",\"Refund\":{\"AfterDepartureRate\":\"1.0\",\"Before0To2\":\"0.8\",\"Before0To2MinAmount\":\"50.0\",\"Before2To12\":\"0.5\",\"Before2To12MinAmount\":\"50.0\",\"BeforeDepartureRate\":\"0.4\",\"BeforeLT12\":\"0.3\",\"BeforeLT12MinAmount\":\"50.0\",\"Eligible\":\"true\",\"MinAmount\":\"50.0\",\"TimeThreshold\":\"-4\",\"Voluntary\":\"true\"},\"Datechange\":{\"AfterDepartureRate\":\"0.3\",\"Before0To2\":\"0.2\",\"Before0To2MinAmount\":\"50.0\",\"Before2To12\":\"0.15\",\"Before2To12MinAmount\":\"50.0\",\"BeforeDepartureRate\":\"0.4\",\"BeforeLT12\":\"0.1\",\"BeforeLT12MinAmount\":\"50.0\",\"Limit\":\"*\",\"MinAmount\":\"50.0\",\"TimeThreshold\":\"-4\",\"Type\":\"percent\"}}},\"Loyalty\":{\"Earn\":{\"Quantity\":\"0\"}}}],\"ProductInfo\":[{\"CKIN\":\"\",\"EI\":\"\",\"Name\":\"舒心飞\",\"Type\":\"\"},{\"CKIN\":\"\",\"EI\":\"\",\"Name\":\"舒心飞\",\"Type\":\"\"}],\"FareFamily\":[{\"Name\":\"舒心飞\",\"Code\":\"BESTSELLING\",\"CabinName\":\"\",\"ChangeToOpen\":\"true\",\"Description\":\"自愿变更:航班规定离站时间前12小时（含）之前提出:收取客票价10%的变更费;航班规定离站时间前12小时至2小时（含）之内提出:收取客票价15%的变更费;航班规定离站时间前2小时之内提出:收取客票价20%的变更费;航班规定离站时间之后提出:收取客票价30%的变更费。自愿退票:航班规定离站时间前12小时（含）之前提出:收取客票价30%的退票费;航班规定离站时间前12小时至2小时（含）之内提出:收取客票价50%的退票费;航班规定离站时间前2小时之内提出：收取客票价80%的退票费;航班规定离站时间之后提出:不允许退票，仅限退还已经代为实际征收的民航发展基金和燃油附加费。\",\"DomesticSelfCheckin\":\"true\",\"FareBasis\":\"W\",\"InternationalSelfCheckin\":\"true\",\"Upgradable\":\"true\",\"UserType\":\"*\",\"Username\":\"*\",\"Refund\":{\"AfterDepartureRate\":\"1.0\",\"Before0To2\":\"0.8\",\"Before0To2MinAmount\":\"50.0\",\"Before2To12\":\"0.5\",\"Before2To12MinAmount\":\"50.0\",\"BeforeDepartureRate\":\"0.4\",\"BeforeLT12\":\"0.3\",\"BeforeLT12MinAmount\":\"50.0\",\"Eligible\":\"true\",\"MinAmount\":\"50.0\",\"TimeThreshold\":\"-4\",\"Voluntary\":\"true\"},\"Datechange\":{\"AfterDepartureRate\":\"0.3\",\"Before0To2\":\"0.2\",\"Before0To2MinAmount\":\"50.0\",\"Before2To12\":\"0.15\",\"Before2To12MinAmount\":\"50.0\",\"BeforeDepartureRate\":\"0.4\",\"BeforeLT12\":\"0.1\",\"BeforeLT12MinAmount\":\"50.0\",\"Limit\":\"*\",\"MinAmount\":\"50.0\",\"TimeThreshold\":\"-4\",\"Type\":\"percent\"}}]}]},{\"FareFamilyCode\":\"TOURISMROVER\",\"Seq\":\"2\",\"FareInfo\":[{\"FlightSegmentRPH\":\"1\",\"datachange_RPH\":\"\",\"FareReference\":{\"ResBookDesigCode\":\"H\"},\"FilingAirline\":{\"Code\":\"KN\"},\"DepartureAirport\":{\"CodeContext\":\"IATA\",\"LocationCode\":\"NAY\"},\"ArrivalAirport\":{\"CodeContext\":\"IATA\",\"LocationCode\":\"SHA\"},\"FareInfo\":[{\"DisCount\":\"\",\"FareBasis\":\"H\",\"FareSystemId\":\"\",\"GdsEi\":\"\",\"RmkOt\":\"\",\"ZValueKey\":\"\",\"Zkey\":\"\",\"Zvalue\":\"-1.000000\",\"can_dateChange\":\"true\",\"can_refund\":\"true\",\"fareRph\":\"\",\"Fare\":{\"BaseAmount\":\"1018.0\",\"TaxAmount\":\"0.0\",\"TotalFare\":\"1018.0\",\"Taxes\":{\"Tax\":[{\"Amount\":\"0.0\",\"CurrencyCode\":\"CNY\",\"OriginalAmount\":\"0.0\",\"OriginalCurrencyCode\":\"CNY\",\"TaxCode\":\"CN\"},{\"Amount\":\"0.0\",\"CurrencyCode\":\"CNY\",\"OriginalAmount\":\"0.0\",\"OriginalCurrencyCode\":\"CNY\",\"TaxCode\":\"YQ\"}]}},\"PTC\":{\"PassengerTypeCode\":\"ADT\",\"Quantity\":\"1\"},\"TPA_Extensions\":{\"ProductInfo\":{\"CKIN\":\"\",\"EI\":\"\",\"Name\":\"大众游\",\"Type\":\"\"},\"FareFamilyInfo\":{\"CabinName\":\"\",\"ChangeToOpen\":\"true\",\"Description\":\"自愿变更:航班规定离站时间前12小时（含）之前提出:收取客票价20%的变更费;航班规定离站时间前12小时至2小时（含）之内提出:收取客票价25%的变更费;航班规定离站时间前2小时之内提出:收取客票价30%的变更费;航班规定离站时间之后提出:收取客票价40%的变更费。自愿退票:航班规定离站时间前12小时（含）之前提出:收取客票价50%的退票费;航班规定离站时间前12小时至2小时（含）之内提出:收取客票价70%的退票费;航班规定离站时间前2小时之内提出：收取客票价90%的退票费;航班规定离站时间之后提出:不允许退票，仅限退还已经代为实际征收的民航发展基金和燃油附加费。\",\"DomesticSelfCheckin\":\"true\",\"FareBasis\":\"H\",\"FareFamilyCode\":\"TOURISMROVER\",\"FareFamilyName\":\"大众游\",\"InternationalSelfCheckin\":\"true\",\"Upgradable\":\"true\",\"UserType\":\"*\",\"Username\":\"*\",\"Refund\":{\"AfterDepartureRate\":\"1.0\",\"Before0To2\":\"0.9\",\"Before0To2MinAmount\":\"50.0\",\"Before2To12\":\"0.7\",\"Before2To12MinAmount\":\"50.0\",\"BeforeDepartureRate\":\"0.4\",\"BeforeLT12\":\"0.5\",\"BeforeLT12MinAmount\":\"50.0\",\"Eligible\":\"true\",\"MinAmount\":\"50.0\",\"TimeThreshold\":\"-4\",\"Voluntary\":\"true\"},\"Datechange\":{\"AfterDepartureRate\":\"0.4\",\"Before0To2\":\"0.3\",\"Before0To2MinAmount\":\"50.0\",\"Before2To12\":\"0.25\",\"Before2To12MinAmount\":\"50.0\",\"BeforeDepartureRate\":\"0.4\",\"BeforeLT12\":\"0.2\",\"BeforeLT12MinAmount\":\"50.0\",\"Limit\":\"*\",\"MinAmount\":\"50.0\",\"TimeThreshold\":\"-4\",\"Type\":\"percent\"}}},\"Loyalty\":{\"Earn\":{\"Quantity\":\"0\"}}}],\"ProductInfo\":[{\"CKIN\":\"\",\"EI\":\"\",\"Name\":\"大众游\",\"Type\":\"\"},{\"CKIN\":\"\",\"EI\":\"\",\"Name\":\"大众游\",\"Type\":\"\"}],\"FareFamily\":[{\"Name\":\"大众游\",\"Code\":\"TOURISMROVER\",\"CabinName\":\"\",\"ChangeToOpen\":\"true\",\"Description\":\"自愿变更:航班规定离站时间前12小时（含）之前提出:收取客票价20%的变更费;航班规定离站时间前12小时至2小时（含）之内提出:收取客票价25%的变更费;航班规定离站时间前2小时之内提出:收取客票价30%的变更费;航班规定离站时间之后提出:收取客票价40%的变更费。自愿退票:航班规定离站时间前12小时（含）之前提出:收取客票价50%的退票费;航班规定离站时间前12小时至2小时（含）之内提出:收取客票价70%的退票费;航班规定离站时间前2小时之内提出：收取客票价90%的退票费;航班规定离站时间之后提出:不允许退票，仅限退还已经代为实际征收的民航发展基金和燃油附加费。\",\"DomesticSelfCheckin\":\"true\",\"FareBasis\":\"H\",\"InternationalSelfCheckin\":\"true\",\"Upgradable\":\"true\",\"UserType\":\"*\",\"Username\":\"*\",\"Refund\":{\"AfterDepartureRate\":\"1.0\",\"Before0To2\":\"0.9\",\"Before0To2MinAmount\":\"50.0\",\"Before2To12\":\"0.7\",\"Before2To12MinAmount\":\"50.0\",\"BeforeDepartureRate\":\"0.4\",\"BeforeLT12\":\"0.5\",\"BeforeLT12MinAmount\":\"50.0\",\"Eligible\":\"true\",\"MinAmount\":\"50.0\",\"TimeThreshold\":\"-4\",\"Voluntary\":\"true\"},\"Datechange\":{\"AfterDepartureRate\":\"0.4\",\"Before0To2\":\"0.3\",\"Before0To2MinAmount\":\"50.0\",\"Before2To12\":\"0.25\",\"Before2To12MinAmount\":\"50.0\",\"BeforeDepartureRate\":\"0.4\",\"BeforeLT12\":\"0.2\",\"BeforeLT12MinAmount\":\"50.0\",\"Limit\":\"*\",\"MinAmount\":\"50.0\",\"TimeThreshold\":\"-4\",\"Type\":\"percent\"}}]}]},{\"FareFamilyCode\":\"SPECIAL\",\"Seq\":\"3\",\"FareInfo\":[{\"FlightSegmentRPH\":\"1\",\"datachange_RPH\":\"\",\"FareReference\":{\"ResBookDesigCode\":\"S\"},\"FilingAirline\":{\"Code\":\"KN\"},\"DepartureAirport\":{\"CodeContext\":\"IATA\",\"LocationCode\":\"NAY\"},\"ArrivalAirport\":{\"CodeContext\":\"IATA\",\"LocationCode\":\"SHA\"},\"FareInfo\":[{\"DisCount\":\"\",\"FareBasis\":\"S\",\"FareSystemId\":\"\",\"GdsEi\":\"\",\"RmkOt\":\"\",\"ZValueKey\":\"\",\"Zkey\":\"\",\"Zvalue\":\"-1.000000\",\"can_dateChange\":\"false\",\"can_refund\":\"false\",\"fareRph\":\"\",\"Fare\":{\"BaseAmount\":\"608.0\",\"TaxAmount\":\"0.0\",\"TotalFare\":\"608.0\",\"Taxes\":{\"Tax\":[{\"Amount\":\"0.0\",\"CurrencyCode\":\"CNY\",\"OriginalAmount\":\"0.0\",\"OriginalCurrencyCode\":\"CNY\",\"TaxCode\":\"CN\"},{\"Amount\":\"0.0\",\"CurrencyCode\":\"CNY\",\"OriginalAmount\":\"0.0\",\"OriginalCurrencyCode\":\"CNY\",\"TaxCode\":\"YQ\"}]}},\"PTC\":{\"PassengerTypeCode\":\"ADT\",\"Quantity\":\"1\"},\"TPA_Extensions\":{\"ProductInfo\":{\"CKIN\":\"\",\"EI\":\"\",\"Name\":\"欢乐抢\",\"Type\":\"\"},\"FareFamilyInfo\":{\"CabinName\":\"\",\"ChangeToOpen\":\"true\",\"Description\":\"自愿变更:不允许变更;自愿退票:不允许退票\",\"DomesticSelfCheckin\":\"true\",\"FareBasis\":\"S\",\"FareFamilyCode\":\"SPECIAL\",\"FareFamilyName\":\"欢乐抢\",\"InternationalSelfCheckin\":\"true\",\"Upgradable\":\"true\",\"UserType\":\"*\",\"Username\":\"*\",\"Refund\":{\"AfterDepartureRate\":\"1.0\",\"Before0To2\":\"1.0\",\"Before0To2MinAmount\":\"50.0\",\"Before2To12\":\"1.0\",\"Before2To12MinAmount\":\"50.0\",\"BeforeDepartureRate\":\"0.4\",\"BeforeLT12\":\"1.0\",\"BeforeLT12MinAmount\":\"50.0\",\"Eligible\":\"true\",\"MinAmount\":\"50.0\",\"TimeThreshold\":\"-4\",\"Voluntary\":\"true\"},\"Datechange\":{\"AfterDepartureRate\":\"1.0\",\"Before0To2\":\"1.0\",\"Before0To2MinAmount\":\"50.0\",\"Before2To12\":\"1.0\",\"Before2To12MinAmount\":\"50.0\",\"BeforeDepartureRate\":\"0.4\",\"BeforeLT12\":\"1.0\",\"BeforeLT12MinAmount\":\"50.0\",\"Limit\":\"*\",\"MinAmount\":\"50.0\",\"TimeThreshold\":\"-4\",\"Type\":\"percent\"}}},\"Loyalty\":{\"Earn\":{\"Quantity\":\"0\"}}}],\"ProductInfo\":[{\"CKIN\":\"\",\"EI\":\"\",\"Name\":\"欢乐抢\",\"Type\":\"\"},{\"CKIN\":\"\",\"EI\":\"\",\"Name\":\"欢乐抢\",\"Type\":\"\"}],\"FareFamily\":[{\"Name\":\"欢乐抢\",\"Code\":\"SPECIAL\",\"CabinName\":\"\",\"ChangeToOpen\":\"true\",\"Description\":\"自愿变更:不允许变更;自愿退票:不允许退票\",\"DomesticSelfCheckin\":\"true\",\"FareBasis\":\"S\",\"InternationalSelfCheckin\":\"true\",\"Upgradable\":\"true\",\"UserType\":\"*\",\"Username\":\"*\",\"Refund\":{\"AfterDepartureRate\":\"1.0\",\"Before0To2\":\"1.0\",\"Before0To2MinAmount\":\"50.0\",\"Before2To12\":\"1.0\",\"Before2To12MinAmount\":\"50.0\",\"BeforeDepartureRate\":\"0.4\",\"BeforeLT12\":\"1.0\",\"BeforeLT12MinAmount\":\"50.0\",\"Eligible\":\"true\",\"MinAmount\":\"50.0\",\"TimeThreshold\":\"-4\",\"Voluntary\":\"true\"},\"Datechange\":{\"AfterDepartureRate\":\"1.0\",\"Before0To2\":\"1.0\",\"Before0To2MinAmount\":\"50.0\",\"Before2To12\":\"1.0\",\"Before2To12MinAmount\":\"50.0\",\"BeforeDepartureRate\":\"0.4\",\"BeforeLT12\":\"1.0\",\"BeforeLT12MinAmount\":\"50.0\",\"Limit\":\"*\",\"MinAmount\":\"50.0\",\"TimeThreshold\":\"-4\",\"Type\":\"percent\"}}]}]}]}},{\"NumAlts\":\"4\",\"SequenceNumber\":\"4\",\"SupplierCode\":\"TSDF\",\"CanBinCode\":\"Y\",\"OriginDestinationRPH\":\"\",\"FlightDetails\":[{\"Code\":\"KN\",\"Duration\":\"2:10\",\"DirectionInd\":\"OneWay\",\"JourneyId\":\"1\",\"Summary\":{\"CabinClass\":\"WW\",\"Departure\":{\"IATA\":\"NAY\",\"Airport\":\"北京南苑\",\"DateTime\":\"2018-09-29T20:45:00\",\"Date\":\"2018-09-29\",\"Time\":\"20:45:00\"},\"Arrival\":{\"IATA\":\"PVG\",\"Airport\":\"上海浦东\",\"DateTime\":\"2018-09-29T22:55:00\",\"Date\":\"2018-09-29\",\"Time\":\"22:55:00\"},\"AirlineList\":{\"Airline\":[{\"MarketingAirline\":\"KN\"}]}},\"FlightLeg\":[{\"InfoSource\":\"Domestic\",\"Loyalty\":\"\",\"FlightLegRPH\":\"1\",\"Duration\":\"2:10\",\"CabinClass\":\"W\",\"FlightNumber\":\"5987\",\"OnlineCheckin\":\"false\",\"Departure\":{\"IATA\":\"NAY\",\"Airport\":\"北京南苑\",\"TS_CityCode\":\"NAY\",\"Terminal\":\"--\",\"DateTime\":\"2018-09-29T20:45:00\",\"Date\":\"2018-09-29\",\"Time\":\"20:45:00\"},\"Arrival\":{\"IATA\":\"PVG\",\"Airport\":\"上海浦东\",\"TS_CityCode\":\"PVG\",\"Terminal\":\"T1\",\"DateTime\":\"2018-09-29T22:55:00\",\"Date\":\"2018-09-29\",\"Time\":\"22:55:00\"},\"MarketingAirline\":\"KN\",\"OperatingAirline\":\"KN\",\"Equipment\":{\"AirEquipType\":\"73V\"},\"BookingClassAvails\":{\"BookingClassAvail\":[{\"RPH\":\"000\",\"ResBookDesigCode\":\"W\",\"ResBookDesigQuantity\":\"A\"},{\"RPH\":\"001\",\"ResBookDesigCode\":\"Y\",\"ResBookDesigQuantity\":\"A\"},{\"RPH\":\"002\",\"ResBookDesigCode\":\"B\",\"ResBookDesigQuantity\":\"A\"},{\"RPH\":\"003\",\"ResBookDesigCode\":\"M\",\"ResBookDesigQuantity\":\"A\"},{\"RPH\":\"004\",\"ResBookDesigCode\":\"A\",\"ResBookDesigQuantity\":\"A\"},{\"RPH\":\"005\",\"ResBookDesigCode\":\"E\",\"ResBookDesigQuantity\":\"A\"},{\"RPH\":\"006\",\"ResBookDesigCode\":\"H\",\"ResBookDesigQuantity\":\"A\"},{\"RPH\":\"007\",\"ResBookDesigCode\":\"K\",\"ResBookDesigQuantity\":\"A\"},{\"RPH\":\"008\",\"ResBookDesigCode\":\"L\",\"ResBookDesigQuantity\":\"A\"},{\"RPH\":\"009\",\"ResBookDesigCode\":\"N\",\"ResBookDesigQuantity\":\"A\"},{\"RPH\":\"0010\",\"ResBookDesigCode\":\"D\",\"ResBookDesigQuantity\":\"A\"}]},\"Comment\":[{\"CharSet\":\"\",\"Language\":\"zh\",\"Type\":\"OnlineCheckin\"}]}]}],\"Price\":{\"FareBreakdowns\":[{\"FareFamilyCode\":\"BESTSELLING\",\"FareFamilyName\":\"舒心飞\",\"Seq\":\"1\",\"Total\":[{\"Currency\":\"CNY\",\"Amount\":\"1358\"}],\"FareBreakdown\":[{\"PassengerTypeQuantity\":{\"Code\":\"ADT\",\"Quantity\":\"1\"},\"PassengerFare\":{\"BaseFare\":{\"Amount\":\"1358.0\",\"Currency\":\"CNY\"},\"Taxes\":{\"Tax\":[{\"Amount\":\"0.0\",\"Currency\":\"CNY\",\"TaxCode\":\"CN\"},{\"Amount\":\"0.0\",\"Currency\":\"CNY\",\"TaxCode\":\"YQ\"}]},\"TotalFare\":{\"Adjusted\":\"1358.0\",\"Amount\":\"1358.0\",\"Currency\":\"CNY\"}}}]},{\"FareFamilyCode\":\"TOURISMROVER\",\"FareFamilyName\":\"大众游\",\"Seq\":\"2\",\"Total\":[{\"Currency\":\"CNY\",\"Amount\":\"1018\"}],\"FareBreakdown\":[{\"PassengerTypeQuantity\":{\"Code\":\"ADT\",\"Quantity\":\"1\"},\"PassengerFare\":{\"BaseFare\":{\"Amount\":\"1018.0\",\"Currency\":\"CNY\"},\"Taxes\":{\"Tax\":[{\"Amount\":\"0.0\",\"Currency\":\"CNY\",\"TaxCode\":\"CN\"},{\"Amount\":\"0.0\",\"Currency\":\"CNY\",\"TaxCode\":\"YQ\"}]},\"TotalFare\":{\"Adjusted\":\"1018.0\",\"Amount\":\"1018.0\",\"Currency\":\"CNY\"}}}]},{\"FareFamilyCode\":\"SPECIAL\",\"FareFamilyName\":\"欢乐抢\",\"Seq\":\"3\",\"Total\":[{\"Currency\":\"CNY\",\"Amount\":\"748\"}],\"FareBreakdown\":[{\"PassengerTypeQuantity\":{\"Code\":\"ADT\",\"Quantity\":\"1\"},\"PassengerFare\":{\"BaseFare\":{\"Amount\":\"748.0\",\"Currency\":\"CNY\"},\"Taxes\":{\"Tax\":[{\"Amount\":\"0.0\",\"Currency\":\"CNY\",\"TaxCode\":\"CN\"},{\"Amount\":\"0.0\",\"Currency\":\"CNY\",\"TaxCode\":\"YQ\"}]},\"TotalFare\":{\"Adjusted\":\"748.0\",\"Amount\":\"748.0\",\"Currency\":\"CNY\"}}}]}],\"FareInfos\":[{\"FareFamilyCode\":\"BESTSELLING\",\"Seq\":\"1\",\"FareInfo\":[{\"FlightSegmentRPH\":\"1\",\"datachange_RPH\":\"\",\"FareReference\":{\"ResBookDesigCode\":\"W\"},\"FilingAirline\":{\"Code\":\"KN\"},\"DepartureAirport\":{\"CodeContext\":\"IATA\",\"LocationCode\":\"NAY\"},\"ArrivalAirport\":{\"CodeContext\":\"IATA\",\"LocationCode\":\"PVG\"},\"FareInfo\":[{\"DisCount\":\"\",\"FareBasis\":\"W\",\"FareSystemId\":\"\",\"GdsEi\":\"\",\"RmkOt\":\"\",\"ZValueKey\":\"\",\"Zkey\":\"\",\"Zvalue\":\"-1.000000\",\"can_dateChange\":\"true\",\"can_refund\":\"true\",\"fareRph\":\"\",\"Fare\":{\"BaseAmount\":\"1358.0\",\"TaxAmount\":\"0.0\",\"TotalFare\":\"1358.0\",\"Taxes\":{\"Tax\":[{\"Amount\":\"0.0\",\"CurrencyCode\":\"CNY\",\"OriginalAmount\":\"0.0\",\"OriginalCurrencyCode\":\"CNY\",\"TaxCode\":\"CN\"},{\"Amount\":\"0.0\",\"CurrencyCode\":\"CNY\",\"OriginalAmount\":\"0.0\",\"OriginalCurrencyCode\":\"CNY\",\"TaxCode\":\"YQ\"}]}},\"PTC\":{\"PassengerTypeCode\":\"ADT\",\"Quantity\":\"1\"},\"TPA_Extensions\":{\"ProductInfo\":{\"CKIN\":\"\",\"EI\":\"\",\"Name\":\"舒心飞\",\"Type\":\"\"},\"FareFamilyInfo\":{\"CabinName\":\"\",\"ChangeToOpen\":\"true\",\"Description\":\"自愿变更:航班规定离站时间前12小时（含）之前提出:收取客票价10%的变更费;航班规定离站时间前12小时至2小时（含）之内提出:收取客票价15%的变更费;航班规定离站时间前2小时之内提出:收取客票价20%的变更费;航班规定离站时间之后提出:收取客票价30%的变更费。自愿退票:航班规定离站时间前12小时（含）之前提出:收取客票价30%的退票费;航班规定离站时间前12小时至2小时（含）之内提出:收取客票价50%的退票费;航班规定离站时间前2小时之内提出：收取客票价80%的退票费;航班规定离站时间之后提出:不允许退票，仅限退还已经代为实际征收的民航发展基金和燃油附加费。\",\"DomesticSelfCheckin\":\"true\",\"FareBasis\":\"W\",\"FareFamilyCode\":\"BESTSELLING\",\"FareFamilyName\":\"舒心飞\",\"InternationalSelfCheckin\":\"true\",\"Upgradable\":\"true\",\"UserType\":\"*\",\"Username\":\"*\",\"Refund\":{\"AfterDepartureRate\":\"1.0\",\"Before0To2\":\"0.8\",\"Before0To2MinAmount\":\"50.0\",\"Before2To12\":\"0.5\",\"Before2To12MinAmount\":\"50.0\",\"BeforeDepartureRate\":\"0.4\",\"BeforeLT12\":\"0.3\",\"BeforeLT12MinAmount\":\"50.0\",\"Eligible\":\"true\",\"MinAmount\":\"50.0\",\"TimeThreshold\":\"-4\",\"Voluntary\":\"true\"},\"Datechange\":{\"AfterDepartureRate\":\"0.3\",\"Before0To2\":\"0.2\",\"Before0To2MinAmount\":\"50.0\",\"Before2To12\":\"0.15\",\"Before2To12MinAmount\":\"50.0\",\"BeforeDepartureRate\":\"0.4\",\"BeforeLT12\":\"0.1\",\"BeforeLT12MinAmount\":\"50.0\",\"Limit\":\"*\",\"MinAmount\":\"50.0\",\"TimeThreshold\":\"-4\",\"Type\":\"percent\"}}},\"Loyalty\":{\"Earn\":{\"Quantity\":\"0\"}}}],\"ProductInfo\":[{\"CKIN\":\"\",\"EI\":\"\",\"Name\":\"舒心飞\",\"Type\":\"\"},{\"CKIN\":\"\",\"EI\":\"\",\"Name\":\"舒心飞\",\"Type\":\"\"}],\"FareFamily\":[{\"Name\":\"舒心飞\",\"Code\":\"BESTSELLING\",\"CabinName\":\"\",\"ChangeToOpen\":\"true\",\"Description\":\"自愿变更:航班规定离站时间前12小时（含）之前提出:收取客票价10%的变更费;航班规定离站时间前12小时至2小时（含）之内提出:收取客票价15%的变更费;航班规定离站时间前2小时之内提出:收取客票价20%的变更费;航班规定离站时间之后提出:收取客票价30%的变更费。自愿退票:航班规定离站时间前12小时（含）之前提出:收取客票价30%的退票费;航班规定离站时间前12小时至2小时（含）之内提出:收取客票价50%的退票费;航班规定离站时间前2小时之内提出：收取客票价80%的退票费;航班规定离站时间之后提出:不允许退票，仅限退还已经代为实际征收的民航发展基金和燃油附加费。\",\"DomesticSelfCheckin\":\"true\",\"FareBasis\":\"W\",\"InternationalSelfCheckin\":\"true\",\"Upgradable\":\"true\",\"UserType\":\"*\",\"Username\":\"*\",\"Refund\":{\"AfterDepartureRate\":\"1.0\",\"Before0To2\":\"0.8\",\"Before0To2MinAmount\":\"50.0\",\"Before2To12\":\"0.5\",\"Before2To12MinAmount\":\"50.0\",\"BeforeDepartureRate\":\"0.4\",\"BeforeLT12\":\"0.3\",\"BeforeLT12MinAmount\":\"50.0\",\"Eligible\":\"true\",\"MinAmount\":\"50.0\",\"TimeThreshold\":\"-4\",\"Voluntary\":\"true\"},\"Datechange\":{\"AfterDepartureRate\":\"0.3\",\"Before0To2\":\"0.2\",\"Before0To2MinAmount\":\"50.0\",\"Before2To12\":\"0.15\",\"Before2To12MinAmount\":\"50.0\",\"BeforeDepartureRate\":\"0.4\",\"BeforeLT12\":\"0.1\",\"BeforeLT12MinAmount\":\"50.0\",\"Limit\":\"*\",\"MinAmount\":\"50.0\",\"TimeThreshold\":\"-4\",\"Type\":\"percent\"}}]}]},{\"FareFamilyCode\":\"TOURISMROVER\",\"Seq\":\"2\",\"FareInfo\":[{\"FlightSegmentRPH\":\"1\",\"datachange_RPH\":\"\",\"FareReference\":{\"ResBookDesigCode\":\"H\"},\"FilingAirline\":{\"Code\":\"KN\"},\"DepartureAirport\":{\"CodeContext\":\"IATA\",\"LocationCode\":\"NAY\"},\"ArrivalAirport\":{\"CodeContext\":\"IATA\",\"LocationCode\":\"PVG\"},\"FareInfo\":[{\"DisCount\":\"\",\"FareBasis\":\"H\",\"FareSystemId\":\"\",\"GdsEi\":\"\",\"RmkOt\":\"\",\"ZValueKey\":\"\",\"Zkey\":\"\",\"Zvalue\":\"-1.000000\",\"can_dateChange\":\"true\",\"can_refund\":\"true\",\"fareRph\":\"\",\"Fare\":{\"BaseAmount\":\"1018.0\",\"TaxAmount\":\"0.0\",\"TotalFare\":\"1018.0\",\"Taxes\":{\"Tax\":[{\"Amount\":\"0.0\",\"CurrencyCode\":\"CNY\",\"OriginalAmount\":\"0.0\",\"OriginalCurrencyCode\":\"CNY\",\"TaxCode\":\"CN\"},{\"Amount\":\"0.0\",\"CurrencyCode\":\"CNY\",\"OriginalAmount\":\"0.0\",\"OriginalCurrencyCode\":\"CNY\",\"TaxCode\":\"YQ\"}]}},\"PTC\":{\"PassengerTypeCode\":\"ADT\",\"Quantity\":\"1\"},\"TPA_Extensions\":{\"ProductInfo\":{\"CKIN\":\"\",\"EI\":\"\",\"Name\":\"大众游\",\"Type\":\"\"},\"FareFamilyInfo\":{\"CabinName\":\"\",\"ChangeToOpen\":\"true\",\"Description\":\"自愿变更:航班规定离站时间前12小时（含）之前提出:收取客票价20%的变更费;航班规定离站时间前12小时至2小时（含）之内提出:收取客票价25%的变更费;航班规定离站时间前2小时之内提出:收取客票价30%的变更费;航班规定离站时间之后提出:收取客票价40%的变更费。自愿退票:航班规定离站时间前12小时（含）之前提出:收取客票价50%的退票费;航班规定离站时间前12小时至2小时（含）之内提出:收取客票价70%的退票费;航班规定离站时间前2小时之内提出：收取客票价90%的退票费;航班规定离站时间之后提出:不允许退票，仅限退还已经代为实际征收的民航发展基金和燃油附加费。\",\"DomesticSelfCheckin\":\"true\",\"FareBasis\":\"H\",\"FareFamilyCode\":\"TOURISMROVER\",\"FareFamilyName\":\"大众游\",\"InternationalSelfCheckin\":\"true\",\"Upgradable\":\"true\",\"UserType\":\"*\",\"Username\":\"*\",\"Refund\":{\"AfterDepartureRate\":\"1.0\",\"Before0To2\":\"0.9\",\"Before0To2MinAmount\":\"50.0\",\"Before2To12\":\"0.7\",\"Before2To12MinAmount\":\"50.0\",\"BeforeDepartureRate\":\"0.4\",\"BeforeLT12\":\"0.5\",\"BeforeLT12MinAmount\":\"50.0\",\"Eligible\":\"true\",\"MinAmount\":\"50.0\",\"TimeThreshold\":\"-4\",\"Voluntary\":\"true\"},\"Datechange\":{\"AfterDepartureRate\":\"0.4\",\"Before0To2\":\"0.3\",\"Before0To2MinAmount\":\"50.0\",\"Before2To12\":\"0.25\",\"Before2To12MinAmount\":\"50.0\",\"BeforeDepartureRate\":\"0.4\",\"BeforeLT12\":\"0.2\",\"BeforeLT12MinAmount\":\"50.0\",\"Limit\":\"*\",\"MinAmount\":\"50.0\",\"TimeThreshold\":\"-4\",\"Type\":\"percent\"}}},\"Loyalty\":{\"Earn\":{\"Quantity\":\"0\"}}}],\"ProductInfo\":[{\"CKIN\":\"\",\"EI\":\"\",\"Name\":\"大众游\",\"Type\":\"\"},{\"CKIN\":\"\",\"EI\":\"\",\"Name\":\"大众游\",\"Type\":\"\"}],\"FareFamily\":[{\"Name\":\"大众游\",\"Code\":\"TOURISMROVER\",\"CabinName\":\"\",\"ChangeToOpen\":\"true\",\"Description\":\"自愿变更:航班规定离站时间前12小时（含）之前提出:收取客票价20%的变更费;航班规定离站时间前12小时至2小时（含）之内提出:收取客票价25%的变更费;航班规定离站时间前2小时之内提出:收取客票价30%的变更费;航班规定离站时间之后提出:收取客票价40%的变更费。自愿退票:航班规定离站时间前12小时（含）之前提出:收取客票价50%的退票费;航班规定离站时间前12小时至2小时（含）之内提出:收取客票价70%的退票费;航班规定离站时间前2小时之内提出：收取客票价90%的退票费;航班规定离站时间之后提出:不允许退票，仅限退还已经代为实际征收的民航发展基金和燃油附加费。\",\"DomesticSelfCheckin\":\"true\",\"FareBasis\":\"H\",\"InternationalSelfCheckin\":\"true\",\"Upgradable\":\"true\",\"UserType\":\"*\",\"Username\":\"*\",\"Refund\":{\"AfterDepartureRate\":\"1.0\",\"Before0To2\":\"0.9\",\"Before0To2MinAmount\":\"50.0\",\"Before2To12\":\"0.7\",\"Before2To12MinAmount\":\"50.0\",\"BeforeDepartureRate\":\"0.4\",\"BeforeLT12\":\"0.5\",\"BeforeLT12MinAmount\":\"50.0\",\"Eligible\":\"true\",\"MinAmount\":\"50.0\",\"TimeThreshold\":\"-4\",\"Voluntary\":\"true\"},\"Datechange\":{\"AfterDepartureRate\":\"0.4\",\"Before0To2\":\"0.3\",\"Before0To2MinAmount\":\"50.0\",\"Before2To12\":\"0.25\",\"Before2To12MinAmount\":\"50.0\",\"BeforeDepartureRate\":\"0.4\",\"BeforeLT12\":\"0.2\",\"BeforeLT12MinAmount\":\"50.0\",\"Limit\":\"*\",\"MinAmount\":\"50.0\",\"TimeThreshold\":\"-4\",\"Type\":\"percent\"}}]}]},{\"FareFamilyCode\":\"SPECIAL\",\"Seq\":\"3\",\"FareInfo\":[{\"FlightSegmentRPH\":\"1\",\"datachange_RPH\":\"\",\"FareReference\":{\"ResBookDesigCode\":\"D\"},\"FilingAirline\":{\"Code\":\"KN\"},\"DepartureAirport\":{\"CodeContext\":\"IATA\",\"LocationCode\":\"NAY\"},\"ArrivalAirport\":{\"CodeContext\":\"IATA\",\"LocationCode\":\"PVG\"},\"FareInfo\":[{\"DisCount\":\"\",\"FareBasis\":\"D\",\"FareSystemId\":\"\",\"GdsEi\":\"\",\"RmkOt\":\"\",\"ZValueKey\":\"\",\"Zkey\":\"\",\"Zvalue\":\"-1.000000\",\"can_dateChange\":\"false\",\"can_refund\":\"false\",\"fareRph\":\"\",\"Fare\":{\"BaseAmount\":\"748.0\",\"TaxAmount\":\"0.0\",\"TotalFare\":\"748.0\",\"Taxes\":{\"Tax\":[{\"Amount\":\"0.0\",\"CurrencyCode\":\"CNY\",\"OriginalAmount\":\"0.0\",\"OriginalCurrencyCode\":\"CNY\",\"TaxCode\":\"CN\"},{\"Amount\":\"0.0\",\"CurrencyCode\":\"CNY\",\"OriginalAmount\":\"0.0\",\"OriginalCurrencyCode\":\"CNY\",\"TaxCode\":\"YQ\"}]}},\"PTC\":{\"PassengerTypeCode\":\"ADT\",\"Quantity\":\"1\"},\"TPA_Extensions\":{\"ProductInfo\":{\"CKIN\":\"\",\"EI\":\"\",\"Name\":\"欢乐抢\",\"Type\":\"\"},\"FareFamilyInfo\":{\"CabinName\":\"\",\"ChangeToOpen\":\"true\",\"Description\":\"自愿变更:不允许变更;自愿退票:不允许退票\",\"DomesticSelfCheckin\":\"true\",\"FareBasis\":\"D\",\"FareFamilyCode\":\"SPECIAL\",\"FareFamilyName\":\"欢乐抢\",\"InternationalSelfCheckin\":\"true\",\"Upgradable\":\"true\",\"UserType\":\"*\",\"Username\":\"*\",\"Refund\":{\"AfterDepartureRate\":\"1.0\",\"Before0To2\":\"1.0\",\"Before0To2MinAmount\":\"50.0\",\"Before2To12\":\"1.0\",\"Before2To12MinAmount\":\"50.0\",\"BeforeDepartureRate\":\"0.4\",\"BeforeLT12\":\"1.0\",\"BeforeLT12MinAmount\":\"50.0\",\"Eligible\":\"true\",\"MinAmount\":\"50.0\",\"TimeThreshold\":\"-4\",\"Voluntary\":\"true\"},\"Datechange\":{\"AfterDepartureRate\":\"1.0\",\"Before0To2\":\"1.0\",\"Before0To2MinAmount\":\"50.0\",\"Before2To12\":\"1.0\",\"Before2To12MinAmount\":\"50.0\",\"BeforeDepartureRate\":\"0.4\",\"BeforeLT12\":\"1.0\",\"BeforeLT12MinAmount\":\"50.0\",\"Limit\":\"*\",\"MinAmount\":\"50.0\",\"TimeThreshold\":\"-4\",\"Type\":\"percent\"}}},\"Loyalty\":{\"Earn\":{\"Quantity\":\"0\"}}}],\"ProductInfo\":[{\"CKIN\":\"\",\"EI\":\"\",\"Name\":\"欢乐抢\",\"Type\":\"\"},{\"CKIN\":\"\",\"EI\":\"\",\"Name\":\"欢乐抢\",\"Type\":\"\"}],\"FareFamily\":[{\"Name\":\"欢乐抢\",\"Code\":\"SPECIAL\",\"CabinName\":\"\",\"ChangeToOpen\":\"true\",\"Description\":\"自愿变更:不允许变更;自愿退票:不允许退票\",\"DomesticSelfCheckin\":\"true\",\"FareBasis\":\"D\",\"InternationalSelfCheckin\":\"true\",\"Upgradable\":\"true\",\"UserType\":\"*\",\"Username\":\"*\",\"Refund\":{\"AfterDepartureRate\":\"1.0\",\"Before0To2\":\"1.0\",\"Before0To2MinAmount\":\"50.0\",\"Before2To12\":\"1.0\",\"Before2To12MinAmount\":\"50.0\",\"BeforeDepartureRate\":\"0.4\",\"BeforeLT12\":\"1.0\",\"BeforeLT12MinAmount\":\"50.0\",\"Eligible\":\"true\",\"MinAmount\":\"50.0\",\"TimeThreshold\":\"-4\",\"Voluntary\":\"true\"},\"Datechange\":{\"AfterDepartureRate\":\"1.0\",\"Before0To2\":\"1.0\",\"Before0To2MinAmount\":\"50.0\",\"Before2To12\":\"1.0\",\"Before2To12MinAmount\":\"50.0\",\"BeforeDepartureRate\":\"0.4\",\"BeforeLT12\":\"1.0\",\"BeforeLT12MinAmount\":\"50.0\",\"Limit\":\"*\",\"MinAmount\":\"50.0\",\"TimeThreshold\":\"-4\",\"Type\":\"percent\"}}]}]}]}}],\"BrandSwitch\":{\"SPECIAL\":0,\"TOURISMROVER\":1,\"BESTSELLING\":1}}";
//		System.out.println("传过来的数据有" + orderJson);
        String session = UUID.randomUUID().toString();
        // 登陆账号
        SSLConnectionSocketFactory sslsf = null;
        BasicCookieStore cookieStore = new BasicCookieStore();// 一个cookies
        String cookie = "";
        try {
            SSLContext sslContext = new SSLContextBuilder().loadTrustMaterial(null, new TrustStrategy() {
                public boolean isTrusted(X509Certificate[] chain, String authType) {
                    return true;
                }
            }).build();
            sslsf = new SSLConnectionSocketFactory(sslContext,
                    SSLConnectionSocketFactory.ALLOW_ALL_HOSTNAME_VERIFIER);
            // 初始化SSL连接
        } catch (Exception e) {
            e.printStackTrace();
        }
        HttpHost proxy = new HttpHost("http-dyn.abuyun.com", 9020, "http");
        org.apache.http.impl.auth.BasicScheme proxyAuth = new org.apache.http.impl.auth.BasicScheme();
        BasicAuthCache authCache = new BasicAuthCache();
        authCache.put(proxy, proxyAuth);
        CredentialsProvider credsProvider = new BasicCredentialsProvider();
        String proxyUser = "HL7F5JF125K85K8D";
        String proxyPass = "FC393F432489B2E5";
        credsProvider.setCredentials(AuthScope.ANY, new UsernamePasswordCredentials(proxyUser, proxyPass));
        try {
            proxyAuth.processChallenge(new BasicHeader(AUTH.PROXY_AUTH, "BASIC realm=default"));
        } catch (MalformedChallengeException e1) {
        }

        int timeout = 60000;
        RequestConfig config = RequestConfig.custom().setSocketTimeout(timeout).setConnectTimeout(timeout)
                .setConnectionRequestTimeout(timeout).setExpectContinueEnabled(false)
                .setStaleConnectionCheckEnabled(true).build();
        HttpClientBuilder builder = null;
        builder = HttpClients.custom().setSSLSocketFactory(sslsf).setDefaultCookieStore(cookieStore)
                .setDefaultRequestConfig(config).setDefaultCredentialsProvider(credsProvider);
        CloseableHttpClient httpclient = builder.build();
        try {
            JSONObject json = new JSONObject(orderJson);
            cookie = "";
            int flag = 0;
            if (StringUtils.isEmpty(cookie)) {
                session = getSessionId(httpclient, config);
                config = RequestConfig.custom().setSocketTimeout(timeout).setConnectTimeout(timeout)
                        .setConnectionRequestTimeout(timeout).setProxy(proxy).setExpectContinueEnabled(false)
                        .setStaleConnectionCheckEnabled(true).build();
                login(httpclient, config, cookieStore, cookie, orderJson, session);
            }
            config = RequestConfig.custom().setSocketTimeout(timeout).setConnectTimeout(timeout)
                    .setConnectionRequestTimeout(timeout).setExpectContinueEnabled(false)
                    .setStaleConnectionCheckEnabled(true).build();
            Map<String,String> paramMap = new HashMap<String,String>();
            getTicketInfo(httpclient,config,orderJson,session,cookieStore);
//			String queryId = "";
//				queryId = queryFlight(httpclient,config,orderJson,session);
//				if(StringUtils.isEmpty(queryId)) {
//					retryCount = retryCount + 1;
//					if(retryCount<100) {
//						Thread.sleep(1*1000);
//						return StartCreateOrder(orderJson, retryCount);
//					}
//				}
//			if(StringUtils.isNotEmpty(queryId)) {
//				queryFlightNo(httpclient,config,queryId,orderJson,paramMap,session);
//				questOther(httpclient,config,queryId,orderJson,paramMap,session);
//				saveOrder(httpclient,config,queryId,orderJson,paramMap,session);
//			}
        } catch (Exception e) {
            e.printStackTrace();
        }
        return "";
    }
    private void getTicketInfo(CloseableHttpClient httpclient, RequestConfig config, String orderJson, String session, BasicCookieStore cookieStore) {
        List<Cookie> listCookie = cookieStore.getCookies();
        try {
            JSONObject json = new JSONObject(orderJson);

            String param_d = "{\"sessionId\":\""+session+"\"}";
            String param_c = "{\"cc\":619,\"ct\":20,\"dt\":1,\"ov\":20,\"p\":11210,\"v\":\"9.50.1\"}";
            String url = "/members/commondata/paymentWithinSevenDays?d="+URLEncoder.encode(param_d, "utf-8")+"&c="+URLEncoder.encode(param_c, "utf-8");
            HttpGet get = new HttpGet(url);
            HttpHost target = new HttpHost("api.tuniu.com",443,"https");
            get.setConfig(config);
            get.setHeader("Host","api.tuniu.com");
            get.setHeader("User-Agent", "TuNiuApp/8.0.9/Dalvik/1.6.0 (Linux;U;Android 4.4.2;SAMSUNG-SM-N900A Build/KOT49H)");
            CloseableHttpResponse	response = httpclient.execute(target, get);
            String back = EntityUtils.toString(response.getEntity(), "utf-8");
            System.out.println("第一个请求返回:"+back);
            JSONObject jo = new JSONObject(back);
            JSONObject backJson = jo.getJSONObject("data");
            JSONObject creditUserStatus = backJson.getJSONObject("creditUserStatus");
            long userId = creditUserStatus.getLong("userId");


            session = "tuniuuser_id="+userId+"; clientType=20;deviceType=1;TUNIUmuser="+session+"; appVersion=9.50.1; ov=20;";

            param_d = "{\"orderId\":\"1187073763\",\"orderType\":\"37\"}";
            url = "/orderUpgrade/queryV2?d="+URLEncoder.encode(param_d, "utf-8");
            get = new HttpGet(url);
            target = new HttpHost("flight-api.tuniu.com",443,"https");
            get.setConfig(config);
            get.setHeader("Host","flight-api.tuniu.com");
            get.setHeader("User-Agent", "TuNiuApp/8.0.9/Dalvik/1.6.0 (Linux;U;Android 4.4.2;SAMSUNG-SM-N900A Build/KOT49H)");
            get.setHeader("Origin", "https://m.tuniu.com");
            get.setHeader("Referer", "https://m.tuniu.com/flight/domestic/orderDetail/1187073763?orderType=37");
            get.setHeader("Cookie", session);
            response = httpclient.execute(target, get);
            back = EntityUtils.toString(response.getEntity(), "utf-8");
            System.out.println("第3个请求返回:"+back);

            return;
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
    private String getSessionId(CloseableHttpClient httpclient, RequestConfig config) {
        String sessionId = "";
        try {
            HttpHost target = new HttpHost("api.tuniu.com",443,"https");
            String param_c = "{\"cc\":619,\"ct\":20,\"dt\":1,\"ov\":20,\"p\":11210,\"v\":\"9.49.0\"}";
            String url = "/auth/beginSession?c="+URLEncoder.encode(param_c, "utf-8");
            String jsonObject = "{\"activateTimes\":0,\"parameters\":{\"apiType\":1,\"clientType\":20,\"createTime\":\"Oct 24, 2018 3:15:16 PM\",\"deviceType\":1,\"imei\":\"008796757008483\",\"lg\":\"1540365316256\",\"partner\":11210,\"sid\":\"36858323432339213056156813824400212432\",\"token\":\"008796757008483\",\"version\":\"9.49.0\"},\"sessionId\":\"0\"}";
            HttpPost post = new HttpPost(url);
            StringEntity entity = new StringEntity(jsonObject,Charset.forName("utf-8"));
            post.setEntity(entity);
            post.setConfig(config);
            post.setHeader("Host","api.tuniu.com");
            post.setHeader("User-Agent", "TuNiuApp/8.0.9/Dalvik/1.6.0 (Linux;U;Android 4.4.2;SAMSUNG-SM-N900A Build/KOT49H)");
            CloseableHttpResponse response = httpclient.execute(target, post);
            String back = EntityUtils.toString(response.getEntity(), "utf-8");
            System.out.println("beginSession:"+back);
            JSONObject json = new JSONObject(back);
            //{"success":true,"errorCode":710000,"msg":"OK","data":{"sessionId":"76f88f6ce40ba1c707816a920a62042d_","isLogin":0}}
            JSONObject data = json.getJSONObject("data");
            sessionId = data.getString("sessionId");
        } catch (Exception e) {
            e.printStackTrace();
        }
        return sessionId;
    }
    private void questOther(CloseableHttpClient httpclient, RequestConfig config, String queryId, String orderJson, Map<String, String> paramMap,String session) {
        try {
			*/
/*
			 * paramMap.put("vendorId", vendorId);
						paramMap.put("ruleCode", ruleCode)
			 *//*

            JSONObject json = new JSONObject(orderJson);
            String dep = json.getString("departure");
            String arr = json.getString("arrival");
            dep = dep.replace("PEK", "BJS").replace("NAY", "BJS").replace("XIY", "SIA").replace("PVG", "SHA");
            arr = arr.replace("PEK", "BJS").replace("NAY", "BJS").replace("XIY", "SIA").replace("PVG", "SHA");
            String mobile = json.getString("mobile");
            String flightNo = json.getString("flightNo");
            HttpHost target = new HttpHost("flight-api.tuniu.com",443,"https");
            String param_d = "{\"cabinCodes\":\""+paramMap.get("cabinCode")+"\",\"flightNos\":\""+flightNo+"\",\"priceInfoId\":\"\",\"queryId\":\""+queryId+"\",\"ruleCode\":\""+paramMap.get("ruleCode")+"\",\"specVendorId\":\"\",\"vendorId\":\""+paramMap.get("vendorId")+"\",\"bif\":{\"abToken\":\"展示活动\",\"clientMac\":\"008796754730510\",\"deviceToken\":\"008796754730510\",\"intel\":0,\"localCity\":\"当前定位省份未知++当前定位区未知\"},\"deviceId\":\"008796754730510\",\"sessionId\":\""+session+"\",\"systemId\":54}";
            String param_c = "{\"cc\":619,\"ct\":20,\"dt\":1,\"ov\":20,\"p\":11210,\"v\":\"9.49.0\"}";
            String url = "/wzt/flight/v1/singleCabinDetail?d="+URLEncoder.encode(param_d, "utf-8")+"&c="+URLEncoder.encode(param_c, "utf-8");
            HttpGet get = new HttpGet(url);
            get.setConfig(config);
            get.setHeader("Host","flight-api.tuniu.com");
            get.setHeader("User-Agent", "TuNiuApp/8.0.9/Dalvik/1.6.0 (Linux;U;Android 4.4.2;SAMSUNG-SM-N900A Build/KOT49H)");
            CloseableHttpResponse	response = httpclient.execute(target, get);
            String back = EntityUtils.toString(response.getEntity(), "utf-8");
            System.out.println("singleCabinDetail:"+back);
            JSONObject jo = new JSONObject(back);
            JSONObject data = jo.getJSONObject("data");
            JSONArray fareListArr = data.getJSONArray("fareList");
            JSONObject fareList = fareListArr.getJSONObject(0);
            JSONArray flightOptionsArr = fareList.getJSONArray("flightOptions");
            JSONObject flightOptions = flightOptionsArr.getJSONObject(0);
            JSONArray flightItemsArr = flightOptions.getJSONArray("flightItems");
            JSONObject flightItems = flightItemsArr.getJSONObject(0);
            String airlineCompany = flightItems.getString("airlineCompany");
            String airlineIataCode = flightItems.getString("airlineIataCode");
            String departureDate = flightItems.getString("departureDate");
            String arrivalDate = flightItems.getString("arrivalDate");
            String aPortIataCode = flightItems.getString("aPortIataCode");
            String aCityName = flightItems.getString("aCityName");
            String arrivalTime = flightItems.getString("arrivalTime");
            String departureTime = flightItems.getString("departureTime");
            String dPortIataCode = flightItems.getString("dPortIataCode");
            String dCityName = flightItems.getString("dCityName");

            JSONArray productCategorysArr = data.getJSONArray("productCategorys");
            JSONObject productCategorys = productCategorysArr.getJSONObject(0);
            JSONArray cabinPricesArr = productCategorys.getJSONArray("cabinPrices");
            JSONObject cabinPrices = cabinPricesArr.getJSONObject(0);
            String vendorId = cabinPrices.getString("vendorId");
            JSONArray priceJourneyCabinListArr = cabinPrices.getJSONArray("priceJourneyCabinList");
            JSONObject priceJourneyCabinList = priceJourneyCabinListArr.getJSONObject(0);
            JSONObject priceFlightCabinList = priceJourneyCabinList.getJSONArray("priceFlightCabinList").getJSONObject(0);
            int journeyNumber = priceFlightCabinList.getInt("journeyNumber");
            int segmentNumber = priceFlightCabinList.getInt("segmentNumber");
            paramMap.put("airComName", airlineCompany);
            paramMap.put("airComIata", airlineIataCode);
            paramMap.put("departDate", departureDate);
            paramMap.put("dstAirportCode", aPortIataCode);
            paramMap.put("dstCityName", aCityName);
            paramMap.put("flightNo", flightNo);
            paramMap.put("flyTimeEnd", arrivalTime);
            paramMap.put("flyTimeStart", departureTime);
            paramMap.put("orgAirportCode", dPortIataCode);
            paramMap.put("orgCityName", dCityName);
            paramMap.put("vendorId", vendorId);
            paramMap.put("journeyNumber", journeyNumber+"");
            paramMap.put("segmentNumber", segmentNumber+"");
            paramMap.put("backDate", arrivalDate);

            //checkedID
			*/
/*
			 * 	paramMap.put(psgType+"baseFare", baseFare+"");
					paramMap.put(psgType+"fuleTax", fuleTax+"");
					paramMap.put(psgType+"airportTax", airportTax+"");
			 *//*

            param_d = "{\"cabinCodes\":\""+paramMap.get("cabinCode")+"\",\"checkID\":\"\",\"fareBreakdownList\":[{\"adultQuantity\":1,\"airportTax\":"+paramMap.get("ADTairportTax")+",\"babyQuantity\":0,\"baseFare\":"+paramMap.get("ADTbaseFare")+",\"childQuantity\":0,\"fuleTax\":"+paramMap.get("ADTfuleTax")+",\"gmjcQuantity\":0}],\"flightId\":\""+paramMap.get("flightId")+"\",\"flightNos\":\""+flightNo+"\",\"priceInfoId\":\"\",\"queryId\":\""+queryId+"\",\"specVendorId\":\"\",\"type\":3,\"vendorId\":\""+vendorId+"\",\"bif\":{\"abToken\":\"展示活动\",\"clientMac\":\"008796754730510\",\"deviceToken\":\"008796754730510\",\"intel\":0,\"localCity\":\"当前定位省份未知++当前定位区未知\"},\"deviceId\":\"008796754730510\",\"sessionId\":\""+session+"\",\"systemId\":54}";
            param_c = "{\"cc\":619,\"ct\":20,\"dt\":1,\"ov\":20,\"p\":11210,\"v\":\"9.49.0\"}";
            url = "/wzt/flight/check/v1/checkPrice?d="+URLEncoder.encode(param_d, "utf-8")+"&c="+URLEncoder.encode(param_c, "utf-8");
            get = new HttpGet(url);
            get.setConfig(config);
            get.setHeader("Host","flight-api.tuniu.com");
            get.setHeader("User-Agent", "TuNiuApp/8.0.9/Dalvik/1.6.0 (Linux;U;Android 4.4.2;SAMSUNG-SM-N900A Build/KOT49H)");
            response = httpclient.execute(target, get);
            back = EntityUtils.toString(response.getEntity(), "utf-8");
            System.out.println("checkPrice:"+back);
            JSONObject backObj = new JSONObject(back);
            data = backObj.getJSONObject("data");
            String checkID = data.getString("checkID");
            paramMap.put("checkID", checkID);
            JSONArray vendorListArr = data.getJSONArray("vendorList");
            JSONObject vendorList = vendorListArr.getJSONObject(0);
            JSONObject jouneyList = vendorList.getJSONArray("jouneyList").getJSONObject(0);
            JSONObject priceList = jouneyList.getJSONArray("priceList").getJSONObject(0);
            int adultPrice = priceList.getInt("adultPrice");
            int adultBasePrice = priceList.getInt("adultBasePrice");
            int adultAirportTax = priceList.getInt("adultAirportTax");
            int adultFuleTax = priceList.getInt("adultFuleTax");
            int childPrice = priceList.getInt("childPrice");
            int childBasePrice = priceList.getInt("childBasePrice");
            int childAirportTax = priceList.getInt("childAirportTax");
            int childFuleTax = priceList.getInt("childFuleTax");
            int babyPrice = priceList.getInt("babyPrice");
            int babyBasePrice = priceList.getInt("babyBasePrice");
            int babyAirportTax = priceList.getInt("babyAirportTax");
            int babyFuleTax = priceList.getInt("babyFuleTax");
            int gmjcPrice = priceList.getInt("gmjcPrice");
            int gmjcBasePrice = priceList.getInt("gmjcBasePrice");
            int gmjcAirportTax = priceList.getInt("gmjcAirportTax");
            int gmjcFuleTax = priceList.getInt("gmjcFuleTax");
            paramMap.put("adultPrice", adultPrice+"");
            paramMap.put("adultBasePrice", adultBasePrice+"");
            paramMap.put("adultAirportTax", adultAirportTax+"");
            paramMap.put("adultFuleTax", adultFuleTax+"");
            paramMap.put("childPrice", childPrice+"");
            paramMap.put("childBasePrice", childBasePrice+"");
            paramMap.put("childAirportTax", childAirportTax+"");
            paramMap.put("childFuleTax", childFuleTax+"");
            paramMap.put("babyPrice", babyPrice+"");
            paramMap.put("babyBasePrice", babyBasePrice+"");
            paramMap.put("babyAirportTax", babyAirportTax+"");
            paramMap.put("babyFuleTax", babyFuleTax+"");
            paramMap.put("gmjcPrice", gmjcPrice+"");
            paramMap.put("gmjcBasePrice", gmjcBasePrice+"");
            paramMap.put("gmjcAirportTax", gmjcAirportTax+"");
            paramMap.put("gmjcFuleTax", gmjcFuleTax+"");

            //查询优惠券
            JSONArray passengers = json.getJSONArray("passengers");
            int adtCount = 0;
            int chdCount = 0;
            int infCount = 0;
            for(int i=0;i<passengers.length();i++) {
                JSONObject passenger = passengers.getJSONObject(i);
                String passengerType = passenger.getString("passengerType");
                if("成人".equals(passengerType)) {
                    adtCount++;
                }else if("儿童".equals(passengerType)) {
                    chdCount++;
                }else if("婴儿".equals(passengerType)) {
                    infCount++;
                }
            }
            param_d = "{\"bindingCouponPrice\":0,\"com\":{\"evps\":[{\"evpType\":1,\"resIds\":[]}]},\"goTrip\":{\"airCom\":\""+flightNo.substring(0, 2)+"\",\"airline\":\""+dep+arr+"\",\"cabinClass\":\""+paramMap.get("cabinClass")+"\",\"departureDate\":\""+departureDate+"\",\"dstCityIataCode\":\""+arr+"\",\"flightNo\":\""+flightNo+"\",\"orgCityIataCode\":\""+dep+"\",\"solutionId\":8},\"insurePrice\":0,\"postageCost\":0,\"resource\":[{\"adultCount\":"+adtCount+",\"adultFuel\":"+adultFuleTax+",\"adultPrice\":"+adultBasePrice+",\"adultTax\":"+adultAirportTax+",\"childCount\":"+chdCount+",\"childFuel\":"+childFuleTax+",\"childPrice\":"+childBasePrice+",\"childTax\":"+childAirportTax+",\"departDate\":\""+departureDate+"\",\"infantCount\":"+infCount+",\"infantFuel\":"+babyFuleTax+",\"infantPrice\":"+babyBasePrice+",\"infantTax\":"+babyAirportTax+",\"productPrice\":0,\"rebateReduce\":0,\"resId\":0,\"solutionId\":0}],\"bif\":{\"abToken\":\"展示活动\",\"clientMac\":\"008796754730510\",\"deviceToken\":\"008796754730510\",\"intel\":0,\"localCity\":\"当前定位省份未知++当前定位区未知\"},\"deviceId\":\"008796754730510\",\"sessionId\":\""+session+"\",\"systemId\":54}";
            param_c = "{\"cc\":619,\"ct\":20,\"dt\":1,\"ov\":20,\"p\":11210,\"v\":\"9.49.0\"}";
            url = "/app/query/queryPromotion?d="+URLEncoder.encode(param_d, "utf-8")+"&c="+URLEncoder.encode(param_c, "utf-8");
            get = new HttpGet(url);
            get.setConfig(config);
            get.setHeader("Host","flight-api.tuniu.com");
            get.setHeader("User-Agent", "TuNiuApp/8.0.9/Dalvik/1.6.0 (Linux;U;Android 4.4.2;SAMSUNG-SM-N900A Build/KOT49H)");
            response = httpclient.execute(target, get);
            back = EntityUtils.toString(response.getEntity(), "utf-8");
            System.out.println("优惠券返回:"+back);
            try {
                JSONObject queryPromotion = new JSONObject(back);
                data = queryPromotion.getJSONObject("data");
                JSONObject availableList = data.getJSONArray("availableList").getJSONObject(0);
                int type = availableList.getInt("type");
                String campaign_id = availableList.getString("campaign_id");
                JSONObject reductions = availableList.getJSONArray("reductions").getJSONObject(0);
                int totalReduction = reductions.getInt("totalReduction");
                paramMap.put("dicountMoney", totalReduction + "");
                paramMap.put("promotionId", campaign_id);
                paramMap.put("promotionType", type + "");
            } catch (Exception e) {
                // TODO: handle exception
            }
//			//查看常用乘客表
//			param_d = "{\"page\":1,\"size\":100,\"bif\":{\"abToken\":\"展示活动\",\"clientMac\":\"008796754730510\",\"deviceToken\":\"008796754730510\",\"intel\":0,\"localCity\":\"当前定位省份未知++当前定位区未知\"},\"deviceId\":\"008796754730510\",\"sessionId\":\""+session+"\",\"systemId\":54}";
//			param_c = "{\"cc\":619,\"ct\":20,\"dt\":1,\"ov\":20,\"p\":11210,\"v\":\"9.49.0\"}";
//			url = "/app/user-login/contact/getList?d="+URLEncoder.encode(param_d, "utf-8")+"&c="+URLEncoder.encode(param_c, "utf-8");
//			get = new HttpGet(url);
//			get.setConfig(config);
//			get.setHeader("Host","flight-api.tuniu.com");
//			get.setHeader("User-Agent", "TuNiuApp/8.0.9/Dalvik/1.6.0 (Linux;U;Android 4.4.2;SAMSUNG-SM-N900A Build/KOT49H)");
//			response = httpclient.execute(target, get);
//			back = EntityUtils.toString(response.getEntity(), "utf-8");
//			System.out.println("常用乘客表内容:"+back);
//			jo = new JSONObject(back);
//			data = jo.getJSONObject("data");
//			List<String> contacterIdList = new ArrayList<String>();
//			JSONArray contactersArr = data.getJSONArray("contacters");
//			for(int i=0;i<contactersArr.length();i++) {
//				JSONObject contacters = contactersArr.getJSONObject(i);
//				String contacterId = contacters.getString("contacterId");
//				contacterIdList.add(contacterId);
//			}
//
//
//			//清空常用乘客表
//			for(String contactId:contacterIdList) {
//				param_d = "{\"contacterId\":"+contactId+",\"bif\":{\"abToken\":\"展示活动\",\"clientMac\":\"008796754730510\",\"deviceToken\":\"008796754730510\",\"intel\":0,\"localCity\":\"当前定位省份未知++当前定位区未知\"},\"deviceId\":\"008796754730510\",\"sessionId\":\""+session+"\",\"systemId\":54}";
//				param_c = "{\"cc\":619,\"ct\":20,\"dt\":1,\"ov\":20,\"p\":11210,\"v\":\"9.49.0\"}";
//				url = "/app/user-login/contact/delete?d="+URLEncoder.encode(param_d, "utf-8")+"&c="+URLEncoder.encode(param_c, "utf-8");
//				get = new HttpGet(url);
//				get.setConfig(config);
//				get.setHeader("Host","flight-api.tuniu.com");
//				get.setHeader("User-Agent", "TuNiuApp/8.0.9/Dalvik/1.6.0 (Linux;U;Android 4.4.2;SAMSUNG-SM-N900A Build/KOT49H)");
//				response = httpclient.execute(target, get);
//				back = EntityUtils.toString(response.getEntity(), "utf-8");
//			}
//
//			//新增乘客
//			for(int i=0;i<passengers.length();i++) {
//				JSONObject passenger = passengers.getJSONObject(i);
//				String passengerName = passenger.getString("passengerName");
//				String birthday = passenger.getString("birthday");
//				String cardNo = passenger.getString("idcard");
//				String idType = passenger.getString("passengercardType");
//				String passengerSex = passenger.getString("passengerSex");
//				if("男".equals(passengerSex)) {
//					passengerSex = "1";
//				}else {
//					passengerSex = "0";
//				}
//				if("身份证".equals(idType)) {
//					param_d = "{\"birthday\":\""+birthday+"\",\"contacterId\":0,\"documents\":{\"idCard\":{\"id\":0,\"number\":\""+cardNo+"\"},\"otherCard\":{}},\"name\":\""+passengerName+"\",\"phoneNumber\":\""+mobile+"\",\"sex\":"+passengerSex+",\"touristType\":0,\"bif\":{\"abToken\":\"展示活动\",\"clientMac\":\"008796754730510\",\"deviceToken\":\"008796754730510\",\"intel\":0,\"localCity\":\"当前定位省份未知++当前定位区未知\"},\"deviceId\":\"008796754730510\",\"sessionId\":\""+session+"\",\"systemId\":54}";
//					param_c = "{\"cc\":619,\"ct\":20,\"dt\":1,\"ov\":20,\"p\":11210,\"v\":\"9.49.0\"}";
//					url = "/app/user-login/contact/update?d="+URLEncoder.encode(param_d, "utf-8")+"&c="+URLEncoder.encode(param_c, "utf-8");
//					get = new HttpGet(url);
//					get.setConfig(config);
//					get.setHeader("Host","flight-api.tuniu.com");
//					get.setHeader("User-Agent", "TuNiuApp/8.0.9/Dalvik/1.6.0 (Linux;U;Android 4.4.2;SAMSUNG-SM-N900A Build/KOT49H)");
//					response = httpclient.execute(target, get);
//					back = EntityUtils.toString(response.getEntity(), "utf-8");
//				}else if("护照".equals(idType)){
//					String firstName = passengerName.split("/")[1].replaceAll(" ", "");
//					String lastName = passengerName.split("/")[0].replaceAll(" ", "");
//					param_d = "{\"birthday\":\""+birthday+"\",\"contacterId\":0,\"documents\":{\"otherCard\":{},\"passport\":{\"countryId\":0,\"id\":0,\"number\":\""+cardNo+"\",\"type\":1}},\"firstName\":\""+firstName+"\",\"lastName\":\""+lastName+"\",\"name\":\""+lastName+" "+firstName+"\",\"phoneNumber\":\""+mobile+"\",\"sex\":"+passengerSex+",\"touristType\":0,\"bif\":{\"abToken\":\"展示活动\",\"clientMac\":\"008796754730510\",\"deviceToken\":\"008796754730510\",\"intel\":0,\"localCity\":\"当前定位省份未知++当前定位区未知\"},\"deviceId\":\"008796754730510\",\"sessionId\":\""+session+"\",\"systemId\":54}";
//					param_c = "{\"cc\":619,\"ct\":20,\"dt\":1,\"ov\":20,\"p\":11210,\"v\":\"9.49.0\"}";
//					url = "/app/user-login/contact/update?d="+URLEncoder.encode(param_d, "utf-8")+"&c="+URLEncoder.encode(param_c, "utf-8");
//					get = new HttpGet(url);
//					get.setConfig(config);
//					get.setHeader("Host","flight-api.tuniu.com");
//					get.setHeader("User-Agent", "TuNiuApp/8.0.9/Dalvik/1.6.0 (Linux;U;Android 4.4.2;SAMSUNG-SM-N900A Build/KOT49H)");
//					response = httpclient.execute(target, get);
//					back = EntityUtils.toString(response.getEntity(), "utf-8");
//				}
//			}
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
    private void saveOrder(CloseableHttpClient httpclient, RequestConfig config, String queryId, String orderJson,Map<String,String> paramMap, String session) {
        try {
			*/
/*
			 * paramMap.put("flightId", flightId);
				paramMap.put("baseFare", baseFare+"");
				paramMap.put("cabinCode", cabinCode);
				paramMap.put("cabinClass", cabinClass);
				paramMap.put("cabinStatus", cabinStatus+"");
				infCabinStatus

					paramMap.put(psgType+"baseFare", baseFare+"");
					paramMap.put(psgType+"fuleTax", fuleTax+"");
					paramMap.put(psgType+"airportTax", airportTax+"");
			 *//*

            JSONObject json = new JSONObject(orderJson);
            String mobile = json.getString("mobile");
            String linkMan = json.getString("linkMan");
            StringBuilder touristList = new StringBuilder();
            JSONArray passengers = json.getJSONArray("passengers");
            int adtCount = 0;
            int chdCount = 0;
            int infCount = 0;
            for(int i=0;i<passengers.length();i++) {
                JSONObject passenger = passengers.getJSONObject(i);
                String passengerName = passenger.getString("passengerName");
                String birthday = passenger.getString("birthday");
                String cardNo = passenger.getString("idcard");
                String idType = passenger.getString("passengercardType");
                String passengerSex = passenger.getString("passengerSex");
                if("男".equals(passengerSex)) {
                    passengerSex = "1";
                }else {
                    passengerSex = "0";
                }
                String passengerType = passenger.getString("passengerType");
                String touristType = "1";
                if("成人".equals(passengerType)) {
                    adtCount++;
                }else if("儿童".equals(passengerType)) {
                    touristType = "2";
                    chdCount++;
                }else if("婴儿".equals(passengerType)) {
                    touristType = "3";
                    infCount++;
                }
                if("身份证".equals(idType)) {
                    touristList.append("{\"birthday\":\""+birthday+"\",\"country\":\"100\",\"index\":"+i+",\"name\":\""+passengerName+"\",\"psptId\":\""+cardNo+"\",\"psptType\":\"1\",\"sex\":\""+passengerSex+"\",\"tel\":\"\",\"telCountryId\":0,\"touristType\":\""+touristType+"\"},");
                }else if("护照".equals(idType)) {
                    if(passengerName.contains("/")) {
                        String firstName = passengerName.split("/")[1].replaceAll(" ", "");
                        String lastName = passengerName.split("/")[0].replaceAll(" ", "");
                        touristList.append("{\"birthday\":\""+birthday+"\",\"country\":\"0\",\"index\":"+i+",\"firstname\":\""+firstName+"\",\"lastname\":\""+lastName+"\",\"name\":\""+passengerName.replaceAll(" ", "")+"\",\"psptId\":\""+cardNo+"\",\"psptType\":\"2\",\"sex\":\""+passengerSex+"\",\"tel\":\"\",\"telCountryId\":0,\"touristType\":\""+touristType+"\"},");
                    }else {
                        touristList.append("{\"birthday\":\""+birthday+"\",\"country\":\"0\",\"index\":"+i+",\"name\":\""+passengerName.replaceAll(" ", "")+"\",\"psptId\":\""+cardNo+"\",\"psptType\":\"2\",\"sex\":\""+passengerSex+"\",\"tel\":\"\",\"telCountryId\":0,\"touristType\":\""+touristType+"\"},");
                    }
                }
            }
            touristList.delete(touristList.length()-1, touristList.length());
            int price = Integer.parseInt(paramMap.get("adultPrice"))*adtCount + Integer.parseInt(paramMap.get("childPrice"))*chdCount + Integer.parseInt(paramMap.get("babyPrice"))*infCount;
            String dep = json.getString("departure");
            String arr = json.getString("arrival");
            dep = dep.replace("PEK", "BJS").replace("NAY", "BJS").replace("XIY", "SIA").replace("PVG", "SHA");
            arr = arr.replace("PEK", "BJS").replace("NAY", "BJS").replace("XIY", "SIA").replace("PVG", "SHA");
            HttpHost target = new HttpHost("flight-api.tuniu.com",443,"https");
            String param_c = "{\"cc\":619,\"ct\":20,\"dt\":1,\"ov\":20,\"p\":11210,\"v\":\"9.49.0\"}";
            String jsonObject = "{\"activityList\":[{\"activityId\":0,\"businessCouponUseAmount\":0,\"directTravelCouponId\":0,\"directTravelCouponUseAmount\":0,\"reduceAmount\":0,\"travelCouponUseAmount\":0,\"voucherCouponUseAmount\":0}]"
                    + ",\"appSpecial\":{\"deviceId\":\"008796754730510\",\"la\":0.0,\"lng\":0.0,\"sessionId\":\""+session+"\",\"verificationCode\":\"\",\"version\":\"9.49.0\"}"
                    + ",\"armFlights\":[{\"airComIata\":\""+paramMap.get("airComIata")+"\",\"airComName\":\""+paramMap.get("airComName")+"\",\"departDate\":\""+paramMap.get("departDate")+"\",\"dstAirportCode\":\""+paramMap.get("dstAirportCode")+"\",\"dstCityName\":\""+paramMap.get("dstCityName")+"\",\"flightNo\":\""+paramMap.get("flightNo")+"\",\"flyTimeEnd\":\""+paramMap.get("flyTimeEnd")+"\",\"flyTimeStart\":\""+paramMap.get("flyTimeStart")+"\",\"orgAirportCode\":\""+paramMap.get("orgAirportCode")+"\",\"orgCityName\":\""+paramMap.get("orgCityName")+"\",\"vendorId\":"+paramMap.get("vendorId")+"}]"
                    + ",\"cabinList\":[{\"cabinCode\":\""+paramMap.get("cabinCode")+"\",\"journeyNumber\":"+paramMap.get("journeyNumber")+",\"segmentNumber\":"+paramMap.get("segmentNumber")+",\"toursitType\":2}]"
                    + ",\"categoryMap\":{";
            try {
                if(paramMap.get("pmtId")!=null) {
                    jsonObject = jsonObject + "\"pmt#+"+paramMap.get("pmtId")+"\":{\"costPrice\":0,\"denomination\":0,\"reducePrice\":"+paramMap.get("reducePrice")+",\"resourceId\":0,\"salePrice\":0,\"tieType\":"+paramMap.get("tieType")+"}";
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
            jsonObject = jsonObject+ "}"
                    + ",\"common\":{\"commentFlag\":0,\"level\":0,\"orderFlightType\":1,\"source\":0,\"systemId\":54,\"voucherType\":5}"
                    + ",\"contactList\":[{\"areaCode\":\"0086\",\"name\":\""+linkMan+"\",\"tel\":\""+mobile+"\"}]"
                    + ",\"flightInfo\":{\"adultSeatNum\":"+paramMap.get("cabinStatus")+",\"cabinClasses\":\""+paramMap.get("cabinClass")+"\",\"cabinCodes\":\""+paramMap.get("cabinCode")+"\",\"checkID\":\""+paramMap.get("checkID")+"\",\"childSeatNum\":"+paramMap.get("infCabinStatus")+",\"flightId\":\""+paramMap.get("flightId")+"\",\"flightNos\":\""+paramMap.get("flightNo")+"\",\"isShareFlight\":0,\"priceInfoId\":\"\",\"queryId\":\""+queryId+"\",\"specVendorId\":\"\",\"vendorId\":\""+paramMap.get("vendorId")+"\"}"
                    + ",\"priceDetail\":{\"adultBasePrice\":"+paramMap.get("adultBasePrice")+",\"adultFuel\":"+paramMap.get("adultFuleTax")+",\"adultPrice\":"+paramMap.get("adultPrice")+",\"adultQuantity\":"+adtCount+",\"adultTax\":"+paramMap.get("adultAirportTax")+",\"babyBasePrice\":"+paramMap.get("babyBasePrice")+",\"babyFuel\":"+paramMap.get("babyFuleTax")+",\"babyPrice\":"+paramMap.get("babyPrice")+",\"babyQuantity\":"+infCount+",\"babyTax\":"+paramMap.get("babyAirportTax")+",\"childBasePrice\":"+paramMap.get("childBasePrice")+",\"childFuel\":"+paramMap.get("childFuleTax")+",\"childPrice\":"+paramMap.get("childPrice")+",\"childQuantity\":"+chdCount+",\"childTax\":"+paramMap.get("childAirportTax")+",\"fullPrice\":"+paramMap.get("fullPrice")+",\"gmjcBasePrice\":"+paramMap.get("gmjcBasePrice")+",\"gmjcFuel\":"+paramMap.get("gmjcFuleTax")+",\"gmjcPrice\":"+paramMap.get("gmjcPrice")+",\"gmjcQuantity\":0,\"gmjcTax\":"+paramMap.get("gmjcAirportTax")+",\"price\":"+price+"}"
                    + ",\"promotionList\":[";
            try {
                if(paramMap.get("dicountMoney")!=null&&!paramMap.get("dicountMoney").equals("0")) {
                    jsonObject = jsonObject + "{\"dicountMoney\":"+paramMap.get("dicountMoney")+",\"promotionId\":"+paramMap.get("promotionId")+",\"promotionType\":"+paramMap.get("promotionType")+"}";
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
            jsonObject = jsonObject+ "]"
                    + ",\"qtzList\":[]"
                    + ",\"requirement\":{\"backDate\":\""+paramMap.get("backDate")+"\",\"departureDate\":\""+paramMap.get("departDate")+"\",\"desCityCode\":\""+ PropertiesUtils.getProperty("tuniuCityCode.properties", arr)+"\",\"startCityCode\":\""+PropertiesUtils.getProperty("tuniuCityCode.properties", dep)+"\",\"startDate\":\""+paramMap.get("departDate")+"\"}"
                    + ",\"touristList\":["+touristList.toString()+"]"
                    + ",\"bif\":{\"abToken\":\"展示活动\",\"clientMac\":\"008796754730510\",\"deviceToken\":\"008796754730510\",\"intel\":0,\"localCity\":\"当前定位省份未知++当前定位区未知\"}"
                    + ",\"deviceId\":\"008796754730510\",\"sessionId\":\""+session+"\",\"systemId\":54}";
            String url = "/wzt/flight/order/v1/saveOrder?c="+URLEncoder.encode(param_c, "utf-8");
            HttpPost post = new HttpPost(url);
            StringEntity entity = new StringEntity(jsonObject,Charset.forName("utf-8"));
            post.setEntity(entity);
            post.setConfig(config);
            post.setHeader("Host","flight-api.tuniu.com");
            post.setHeader("Content-Type","application/json;charset=UTF-8");
            post.setHeader("User-Agent", "TuNiuApp/8.0.9/Dalvik/1.6.0 (Linux;U;Android 4.4.2;SAMSUNG-SM-N900A Build/KOT49H)");
            CloseableHttpResponse response = httpclient.execute(target, post);
            String back = EntityUtils.toString(response.getEntity(), "utf-8");
            System.out.println("payCash请求返回:"+back);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
    */
/*
 * flightId、价格、舱位、舱位等级、成人座位数、儿童座位数
 *//*

    private void queryFlightNo(CloseableHttpClient httpclient, RequestConfig config, String queryId, String orderJson, Map<String, String> paramMap, String session) {
        try {
            JSONObject json = new JSONObject(orderJson);
            String flightNo = json.getString("flightNo");
            float price = Float.parseFloat(json.getString("price"));
            String ytype = json.getString("ytype");
//			String param_d = "{\"flightNos\":\""+flightNo+"\",\"queryId\":\""+queryId+"\",\"bif\":{\"abToken\":\"展示活动\",\"clientMac\":\"008796754730510\",\"deviceToken\":\"008796754730510\",\"intel\":0,\"localCity\":\"当前定位省份未知++当前定位区未知\"},\"deviceId\":\"008796754730510\",\"sessionId\":\""+session+"\",\"systemId\":54}";
//			String param_c = "{\"cc\":619,\"ct\":20,\"dt\":1,\"ov\":20,\"p\":11210,\"v\":\"9.49.0\"}";
//			String url = "/wzt/flight/v2/multiCabinDetails?d="+URLEncoder.encode(param_d, "utf-8")+"&c="+URLEncoder.encode(param_c, "utf-8");
//			HttpGet get = new HttpGet(url);
//			HttpHost target = new HttpHost("flight-api.tuniu.com",443,"https");
//			get.setConfig(config);
//			get.setHeader("Host","flight-api.tuniu.com");
//			get.setHeader("User-Agent", "TuNiuApp/8.0.9/Dalvik/1.6.0 (Linux;U;Android 4.4.2;SAMSUNG-SM-N900A Build/KOT49H)");
//			CloseableHttpResponse	response = httpclient.execute(target, get);
//			String back = EntityUtils.toString(response.getEntity(), "utf-8");
            String back = "{\"data\":{\"airComMap\":{\"EU\":{\"agrees\":[{\"field\":\"insuranceProtocol \",\"label\":\"保险经纪委托合同及客户告知书\",\"url\":\"http://baoxian.tuniu.com/ins/resource/bxjjwtht\"},{\"field\":\"dangerousNotice\",\"label\":\"锂电池及危险品须知\",\"url\":\"https://m.tuniu.com/m2015/flight/insDoc?id=277\"}],\"showCabin\":false,\"showLuggage\":true,\"showTicketNotice\":false}},\"blackCard\":null,\"fareList\":[{\"flightId\":\"b1df828110ea426893932283fcf2165b\",\"flightOptions\":[{\"aCityName\":\"宁波\",\"airCodes\":\"EU\",\"airPorts\":\"CSX,NGB\",\"dCityName\":\"长沙\",\"departureDate\":\"2018-11-07\",\"flightItems\":[{\"aCityIataCode\":\"NGB\",\"aCityName\":\"宁波\",\"aPortIataCode\":\"NGB\",\"aPortName\":\"栎社机场\",\"aTerminal\":\"\",\"aagStrUpNotice\":null,\"airChangeNotice\":null,\"airComImageUrl\":\"http://m.tuniucdn.com/fb2/t1/G1/M00/08/69/Cii9EVbNihWIegOqAAArD5I8iKwAACRoAOPieAAACsn358.png\",\"airlineCompany\":\"成都航\",\"airlineIataCode\":\"EU\",\"arrivalDate\":\"2018-11-07\",\"arrivalTime\":\"19:20\",\"avgDelayTime\":\"5\",\"codeShare\":\"\",\"craftType\":\"320\",\"craftTypeName\":\"空客A320\",\"dCityIataCode\":\"CSX\",\"dCityName\":\"长沙\",\"dPortIataCode\":\"CSX\",\"dPortName\":\"黄花机场\",\"dTerminal\":\"T1\",\"departureDate\":\"2018-11-07\",\"departureTime\":\"17:45\",\"flightNo\":\"EU2749\",\"flightTime\":\"95\",\"flightYear\":\"2.2\",\"hotelNotice\":null,\"isAirChange\":null,\"isBagStrUp\":null,\"isOvernight\":null,\"mealName\":\"有餐\",\"onTimeRate\":\"96%\",\"planeModel\":2,\"planeModelName\":\"中\",\"sAirComImageUrl\":null,\"sAirComName\":null,\"stopInformation\":\"\",\"stopNum\":0,\"stopPoints\":[],\"voyageNotice\":null}],\"flightNos\":\"EU2749\",\"hanging\":null,\"totalTransfer\":\"1\",\"transferPoints\":null,\"transferTime\":\"\",\"transferTips\":null}],\"tagList\":null}],\"productCategorys\":[{\"cabinPrices\":[{\"aduDivideBaby\":\"1/1\",\"aduDivideChd\":null,\"aduDivideChdBaby\":\"1/2\",\"applyType\":\"1/2\",\"fareBreakdownList\":[{\"airportTax\":50,\"baseFare\":439,\"discount\":0.43,\"fuleTax\":50,\"optionFareBreakdowns\":[{\"airportTax\":50,\"baseFare\":439,\"fuleTax\":50,\"policyId\":null,\"policyType\":0}],\"psgType\":\"ADT\",\"specCabinStatusChange\":\"0\",\"specVendorId\":\"\"},{\"airportTax\":0,\"baseFare\":556,\"fuleTax\":20,\"optionFareBreakdowns\":[{\"airportTax\":0,\"baseFare\":556,\"fuleTax\":20,\"policyId\":null,\"policyType\":0}],\"psgType\":\"CHD\",\"specCabinStatusChange\":\"0\",\"specVendorId\":\"\"}],\"groupDescribe\":\"除直连和IBE\",\"groupId\":\"PRICEGROUP_1524811839460\",\"groupName\":\"其他\",\"iconPath\":\"\",\"isShow\":1,\"lowestRefundList\":[{\"adultLowestRefund\":\"退改?87起\",\"childLowestRefund\":\"\",\"flightNos\":\"EU2749\"}],\"optionExtras\":[{\"additionInfo\":{\"saleControl\":{\"aduDivideBaby\":\"1/1\",\"aduDivideChd\":null,\"aduDivideChdBaby\":\"1/2\",\"ageLimit\":null,\"fixedPsgCnt\":null,\"idTypeText\":{\"ADT\":\"成人限证件类型：身份证，护照，军官证，台胞证，回乡证，户口薄，出生证明。\",\"CHD\":\"儿童限证件类型：身份证，护照，军官证，台胞证，回乡证，户口薄，出生证明。\"},\"maxPsgCnt\":\"9\",\"minPsgCnt\":\"1\",\"prohibitSale\":false,\"psgTypeLimit\":[{\"allowIdType\":[\"ID\",\"PP\",\"MI\",\"MT\",\"HV\",\"RBT\",\"BRC\"],\"allowPsgType\":\"ADT\",\"maxCnt\":0,\"minCnt\":0},{\"allowIdType\":[\"ID\",\"PP\",\"MI\",\"MT\",\"HV\",\"RBT\",\"BRC\"],\"allowPsgType\":\"CHD\",\"maxCnt\":0,\"minCnt\":0}]}},\"drawTicketTime\":225,\"extraInfo\":\"eyJleHRyYUluZm8iOiIzNDUwNS1ZU0gtREVGIn0=\",\"flightNos\":null,\"iataCode\":\"08071206\",\"iataName\":\"天津市经典假期航空票务服务有限公司\",\"realVendorId\":\"13\"}],\"priceInfoId\":\"\",\"priceJourneyCabinList\":[{\"priceFlightCabinList\":[{\"actionCode\":\"0\",\"actionName\":null,\"baggageInfo\":\"长沙-宁波：免费托运行李20公斤 ;\",\"baggageInfoBaby\":null,\"baggageInfoCHD\":\"长沙-宁波：免费托运行李20公斤 ;\",\"baggageInfoEntity\":{\"check\":{\"number\":\"\",\"size\":\"\",\"weight\":20},\"portable\":{\"number\":null,\"size\":null,\"weight\":0},\"remark\":\"\"},\"baggageInfoEntityBaby\":null,\"baggageInfoEntityCHD\":{\"check\":{\"number\":\"\",\"size\":\"\",\"weight\":20},\"portable\":{\"number\":null,\"size\":null,\"weight\":0},\"remark\":\"\"},\"cabinClass\":\"Y\",\"cabinCode\":\"V\",\"cabinKey\":\"0-0#EU2749#2018-11-07#CSX#NGB#V\",\"cabinStatus\":10,\"cabinType\":1,\"cabinTypeName\":\"经济舱\",\"fullPrice\":1020,\"infCabinStatus\":4,\"insureList\":null,\"journeyNumber\":0,\"mealCode\":\"D\",\"mealCouponList\":null,\"mealName\":\"有餐\",\"seatAliasName\":\"\",\"seatClassDesc\":\"\",\"segmentNumber\":0,\"specCabinCode\":\"V\",\"specCabinStatus\":10,\"vipRoomList\":null}]}],\"prodClassific\":{\"ruleCode\":\"130\"},\"promotionList\":null,\"saleControl\":{\"aduDivideBaby\":\"1/1\",\"aduDivideChd\":null,\"aduDivideChdBaby\":\"1/2\",\"ageLimit\":null,\"fixedPsgCnt\":null,\"idTypeText\":{\"ADT\":\"成人限证件类型：身份证，护照，军官证，台胞证，回乡证，户口薄，出生证明。\",\"CHD\":\"儿童限证件类型：身份证，护照，军官证，台胞证，回乡证，户口薄，出生证明。\"},\"maxPsgCnt\":\"9\",\"minPsgCnt\":\"1\",\"prohibitSale\":false,\"psgTypeLimit\":[{\"allowIdType\":[\"ID\",\"PP\",\"MI\",\"MT\",\"HV\",\"RBT\",\"BRC\"],\"allowPsgType\":\"ADT\",\"maxCnt\":0,\"minCnt\":0},{\"allowIdType\":[\"ID\",\"PP\",\"MI\",\"MT\",\"HV\",\"RBT\",\"BRC\"],\"allowPsgType\":\"CHD\",\"maxCnt\":0,\"minCnt\":0}]},\"tieCouponList\":[{\"autoCheck\":0,\"categoryAmount\":40.0,\"categoryId\":28,\"categoryLabelDesc\":\"1.优惠券由途牛旅游网提供，优惠券在PC和手机端均可使用，该券有效期为购买绑定之日起30天内有效。\\n2.优惠券适用于途牛国内酒店频道的国内酒店房型。\\n3.单个在线支付的订单可享受订单每满500优惠40元，最高优惠40元，到店支付的订单入住点评后每满500返40元（最高40元）至现金账户。\\n4.单张券一次性使用，不拆分，不转赠，不提现，单个订单仅能使用一张优惠券，不与其他优惠券叠加使用。#1.优惠券不支持单独退订；\\n2.如果套餐订单发生退订，优惠券将失效，购买券的费用退还（券未使用）；\\n3.如果套餐订单发生退订，购买券的费用不退还（券已使用/已过期）。\",\"categoryName\":\"酒店超值券\",\"categoryPmfId\":152695,\"copy\":1,\"detailId\":205,\"detailReduceAmount\":0.0,\"detailSalePrice\":14.0,\"detailShowLabel\":\"\",\"infoId\":92,\"isRmd\":0},{\"autoCheck\":1,\"categoryAmount\":50.0,\"categoryId\":29,\"categoryLabelDesc\":\"1.单笔订单只能使用一张优惠券，订单每满100元减5元，最高优惠50元，仅限国内接送产品使用，预订请前往途牛APP-租车接送频道，选择带有优惠券标签的产品；\\n2.优惠券使用有效期为实际领取后45天，过期失效；\\n3.优惠券仅能一次性使用，不转赠、不提现；\\n4.优惠券不能与其他优惠活动（包括但不限于银行优惠活动、会员优惠活动、新用户立减活动等）同时享用；\\n5.使用优惠券预订成功的订单，若预订失败或取消，优惠券予以退还；一旦使用车辆，优惠券不予退还；\\n6.参加活动的用户若存在不正当行为（包括但不限于恶意套现、机器作弊等），途牛旅游网在法律允许的范围内保留对本次活动的变更权，包括但不限于取消参与资格、取消所获得的奖励、暂停或取消本次活动等；#1.优惠券不支持单独退订；\\n2.如果套餐订单发生退订，优惠券将失效，购买券的费用退还（券未使用）；\\n3.如果套餐订单发生退订，购买券的费用不退还（券已使用/已过期）。\",\"categoryName\":\"接送机券\",\"categoryPmfId\":151834,\"copy\":1,\"detailId\":206,\"detailReduceAmount\":0.0,\"detailSalePrice\":0.0,\"detailShowLabel\":\"\",\"infoId\":92,\"isRmd\":0},{\"autoCheck\":1,\"categoryAmount\":55.0,\"categoryId\":34,\"categoryLabelDesc\":\"1、红包仅适用于途牛单签证参加活动的产品。\\n2、一个用户只能领取一次。\\n3、优惠券有效期10天\\n4、此券仅用于途牛单签证产品（不含特价、秒杀、甩卖、特例产品）。单张券一次性使用，不拆分，不转赠，不提现，单个订单仅能使用一张优惠券，不与其他优惠券叠加使用\\n5、若订单在已支付情况下产生退改时，红包不予退还。\\n6、优惠券不与其他任何优惠活动叠加使用。\\n7、途牛旅游网站、手机客户端均可使用此红包。\\n8、途牛旅游网在法律允许的范围内保留对本次活动的变更权，包括但不限于参加资格、消费时间及奖励方式、暂停或取消本活动等，并经相关途径（如网站、对账单、短信、报刊或各分支网点等）公告后生效。#1.优惠券不支持单独退订；\\n2.如果套餐订单发生退订，优惠券将失效，购买券的费用退还（券未使用）；\\n3.如果套餐订单发生退订，购买券的费用不退还（券已使用/已过期）。\",\"categoryName\":\"签证优惠券\",\"categoryPmfId\":153762,\"copy\":1,\"detailId\":207,\"detailReduceAmount\":0.0,\"detailSalePrice\":0.0,\"detailShowLabel\":\"\",\"infoId\":92,\"isRmd\":0}],\"tieInfoList\":null,\"vendorId\":\"13\"},{\"aduDivideBaby\":\"1/1\",\"aduDivideChd\":null,\"aduDivideChdBaby\":\"1/2\",\"applyType\":\"1/2\",\"fareBreakdownList\":[{\"airportTax\":50,\"baseFare\":520,\"discount\":0.51,\"fuleTax\":50,\"optionFareBreakdowns\":[{\"airportTax\":50,\"baseFare\":520,\"fuleTax\":50,\"policyId\":null,\"policyType\":0}],\"psgType\":\"ADT\",\"specCabinStatusChange\":\"0\",\"specVendorId\":\"\"},{\"airportTax\":0,\"baseFare\":556,\"fuleTax\":20,\"optionFareBreakdowns\":[{\"airportTax\":0,\"baseFare\":556,\"fuleTax\":20,\"policyId\":null,\"policyType\":0}],\"psgType\":\"CHD\",\"specCabinStatusChange\":\"0\",\"specVendorId\":\"\"}],\"groupDescribe\":\"除直连和IBE\",\"groupId\":\"PRICEGROUP_1524811839460\",\"groupName\":\"其他\",\"iconPath\":\"\",\"isShow\":1,\"lowestRefundList\":[{\"adultLowestRefund\":\"退改?103起\",\"childLowestRefund\":\"\",\"flightNos\":\"EU2749\"}],\"optionExtras\":[{\"additionInfo\":{\"saleControl\":{\"aduDivideBaby\":\"1/1\",\"aduDivideChd\":null,\"aduDivideChdBaby\":\"1/2\",\"ageLimit\":null,\"fixedPsgCnt\":null,\"idTypeText\":{\"ADT\":\"成人限证件类型：身份证，护照，军官证，台胞证，回乡证，户口薄，出生证明。\",\"CHD\":\"儿童限证件类型：身份证，护照，军官证，台胞证，回乡证，户口薄，出生证明。\"},\"maxPsgCnt\":\"9\",\"minPsgCnt\":\"1\",\"prohibitSale\":false,\"psgTypeLimit\":[{\"allowIdType\":[\"ID\",\"PP\",\"MI\",\"MT\",\"HV\",\"RBT\",\"BRC\"],\"allowPsgType\":\"ADT\",\"maxCnt\":0,\"minCnt\":0},{\"allowIdType\":[\"ID\",\"PP\",\"MI\",\"MT\",\"HV\",\"RBT\",\"BRC\"],\"allowPsgType\":\"CHD\",\"maxCnt\":0,\"minCnt\":0}]}},\"drawTicketTime\":225,\"extraInfo\":\"eyJleHRyYUluZm8iOiIzNDUwNS1ZU0gtREVGIn0=\",\"flightNos\":null,\"iataCode\":\"08071206\",\"iataName\":\"天津市经典假期航空票务服务有限公司\",\"realVendorId\":\"13\"}],\"priceInfoId\":\"\",\"priceJourneyCabinList\":[{\"priceFlightCabinList\":[{\"actionCode\":\"0\",\"actionName\":null,\"baggageInfo\":\"长沙-宁波：免费托运行李20公斤 ;\",\"baggageInfoBaby\":null,\"baggageInfoCHD\":\"长沙-宁波：免费托运行李20公斤 ;\",\"baggageInfoEntity\":{\"check\":{\"number\":\"\",\"size\":\"\",\"weight\":20},\"portable\":{\"number\":null,\"size\":null,\"weight\":0},\"remark\":\"\"},\"baggageInfoEntityBaby\":null,\"baggageInfoEntityCHD\":{\"check\":{\"number\":\"\",\"size\":\"\",\"weight\":20},\"portable\":{\"number\":null,\"size\":null,\"weight\":0},\"remark\":\"\"},\"cabinClass\":\"Y\",\"cabinCode\":\"E\",\"cabinKey\":\"0-1#EU2749#2018-11-07#CSX#NGB#E\",\"cabinStatus\":10,\"cabinType\":1,\"cabinTypeName\":\"经济舱\",\"fullPrice\":1020,\"infCabinStatus\":4,\"insureList\":null,\"journeyNumber\":0,\"mealCode\":\"D\",\"mealCouponList\":null,\"mealName\":\"有餐\",\"seatAliasName\":\"\",\"seatClassDesc\":\"\",\"segmentNumber\":0,\"specCabinCode\":\"E\",\"specCabinStatus\":10,\"vipRoomList\":null}]}],\"prodClassific\":{\"ruleCode\":\"140\"},\"promotionList\":null,\"saleControl\":{\"aduDivideBaby\":\"1/1\",\"aduDivideChd\":null,\"aduDivideChdBaby\":\"1/2\",\"ageLimit\":null,\"fixedPsgCnt\":null,\"idTypeText\":{\"ADT\":\"成人限证件类型：身份证，护照，军官证，台胞证，回乡证，户口薄，出生证明。\",\"CHD\":\"儿童限证件类型：身份证，护照，军官证，台胞证，回乡证，户口薄，出生证明。\"},\"maxPsgCnt\":\"9\",\"minPsgCnt\":\"1\",\"prohibitSale\":false,\"psgTypeLimit\":[{\"allowIdType\":[\"ID\",\"PP\",\"MI\",\"MT\",\"HV\",\"RBT\",\"BRC\"],\"allowPsgType\":\"ADT\",\"maxCnt\":0,\"minCnt\":0},{\"allowIdType\":[\"ID\",\"PP\",\"MI\",\"MT\",\"HV\",\"RBT\",\"BRC\"],\"allowPsgType\":\"CHD\",\"maxCnt\":0,\"minCnt\":0}]},\"tieCouponList\":null,\"tieInfoList\":null,\"vendorId\":\"13\"},{\"aduDivideBaby\":\"1/1\",\"aduDivideChd\":null,\"aduDivideChdBaby\":\"1/2\",\"applyType\":\"1\",\"fareBreakdownList\":[{\"airportTax\":50,\"baseFare\":540,\"discount\":0.53,\"fuleTax\":50,\"optionFareBreakdowns\":[{\"airportTax\":50,\"baseFare\":540,\"fuleTax\":50,\"policyId\":\"\",\"policyType\":0}],\"psgType\":\"ADT\",\"specCabinStatusChange\":\"0\",\"specVendorId\":\"\"},{\"airportTax\":0,\"baseFare\":110,\"fuleTax\":0,\"optionFareBreakdowns\":[{\"airportTax\":0,\"baseFare\":110,\"fuleTax\":0,\"policyId\":\"\",\"policyType\":0}],\"psgType\":\"INF\",\"specCabinStatusChange\":\"0\",\"specVendorId\":\"\"},{\"airportTax\":0,\"baseFare\":510,\"fuleTax\":20,\"optionFareBreakdowns\":[{\"airportTax\":0,\"baseFare\":510,\"fuleTax\":20,\"policyId\":\"\",\"policyType\":0}],\"psgType\":\"CHD\",\"specCabinStatusChange\":\"0\",\"specVendorId\":\"\"}],\"groupDescribe\":\"航信\",\"groupId\":\"PRICEGROUP_1524811797721\",\"groupName\":\"航信\",\"iconPath\":\"\",\"isShow\":1,\"lowestRefundList\":[{\"adultLowestRefund\":\"退改?108起\",\"childLowestRefund\":\"\",\"flightNos\":\"EU2749\"}],\"optionExtras\":[{\"additionInfo\":{\"saleControl\":{\"aduDivideBaby\":\"1/1\",\"aduDivideChd\":null,\"aduDivideChdBaby\":\"1/2\",\"ageLimit\":null,\"fixedPsgCnt\":null,\"idTypeText\":{\"INF\":\"婴儿限证件类型：身份证，出生证明，户口薄。\"},\"maxPsgCnt\":\"9\",\"minPsgCnt\":\"1\",\"prohibitSale\":false,\"psgTypeLimit\":[{\"allowIdType\":[\"ID\",\"BRC\",\"RBT\"],\"allowPsgType\":\"INF\",\"maxCnt\":2,\"minCnt\":0}]}},\"drawTicketTime\":20,\"extraInfo\":\"eyJmYXJlQmFzaXMiOiJFIiwiZmRUeXBlIjoiRkQifQ==\",\"flightNos\":null,\"iataCode\":\"08319463\",\"iataName\":\"南京途之旅票务服务有限公司\",\"realVendorId\":\"1\"}],\"priceInfoId\":\"\",\"priceJourneyCabinList\":[{\"priceFlightCabinList\":[{\"actionCode\":\"0\",\"actionName\":null,\"baggageInfo\":\"长沙-宁波：免费托运行李20公斤 ;\",\"baggageInfoBaby\":null,\"baggageInfoCHD\":\"长沙-宁波：免费托运行李20公斤 ;\",\"baggageInfoEntity\":{\"check\":{\"number\":\"\",\"size\":\"\",\"weight\":20},\"portable\":{\"number\":null,\"size\":null,\"weight\":0},\"remark\":\"\"},\"baggageInfoEntityBaby\":null,\"baggageInfoEntityCHD\":{\"check\":{\"number\":\"\",\"size\":\"\",\"weight\":20},\"portable\":{\"number\":null,\"size\":null,\"weight\":0},\"remark\":\"\"},\"cabinClass\":\"Y\",\"cabinCode\":\"E\",\"cabinKey\":\"0-2#EU2749#2018-11-07#CSX#NGB#E\",\"cabinStatus\":10,\"cabinType\":1,\"cabinTypeName\":\"经济舱\",\"fullPrice\":1020,\"infCabinStatus\":4,\"insureList\":null,\"journeyNumber\":0,\"mealCode\":\"D\",\"mealCouponList\":null,\"mealName\":\"有餐\",\"seatAliasName\":\"\",\"seatClassDesc\":\"\",\"segmentNumber\":0,\"specCabinCode\":\"Y\",\"specCabinStatus\":10,\"vipRoomList\":null}]}],\"prodClassific\":{\"ruleCode\":\"140\"},\"promotionList\":null,\"saleControl\":{\"aduDivideBaby\":\"1/1\",\"aduDivideChd\":null,\"aduDivideChdBaby\":\"1/2\",\"ageLimit\":null,\"fixedPsgCnt\":null,\"idTypeText\":{\"INF\":\"婴儿限证件类型：身份证，出生证明，户口薄。\"},\"maxPsgCnt\":\"9\",\"minPsgCnt\":\"1\",\"prohibitSale\":false,\"psgTypeLimit\":[{\"allowIdType\":[\"ID\",\"BRC\",\"RBT\"],\"allowPsgType\":\"INF\",\"maxCnt\":2,\"minCnt\":0}]},\"tieCouponList\":null,\"tieInfoList\":null,\"vendorId\":\"1\"},{\"aduDivideBaby\":\"1/1\",\"aduDivideChd\":null,\"aduDivideChdBaby\":\"1/2\",\"applyType\":\"1/2\",\"fareBreakdownList\":[{\"airportTax\":50,\"baseFare\":613,\"discount\":0.6,\"fuleTax\":50,\"optionFareBreakdowns\":[{\"airportTax\":50,\"baseFare\":613,\"fuleTax\":50,\"policyId\":null,\"policyType\":0}],\"psgType\":\"ADT\",\"specCabinStatusChange\":\"0\",\"specVendorId\":\"\"},{\"airportTax\":0,\"baseFare\":556,\"fuleTax\":20,\"optionFareBreakdowns\":[{\"airportTax\":0,\"baseFare\":556,\"fuleTax\":20,\"policyId\":null,\"policyType\":0}],\"psgType\":\"CHD\",\"specCabinStatusChange\":\"0\",\"specVendorId\":\"\"}],\"groupDescribe\":\"除直连和IBE\",\"groupId\":\"PRICEGROUP_1524811839460\",\"groupName\":\"其他\",\"iconPath\":\"\",\"isShow\":1,\"lowestRefundList\":[{\"adultLowestRefund\":\"退改?122起\",\"childLowestRefund\":\"\",\"flightNos\":\"EU2749\"}],\"optionExtras\":[{\"additionInfo\":{\"saleControl\":{\"aduDivideBaby\":\"1/1\",\"aduDivideChd\":null,\"aduDivideChdBaby\":\"1/2\",\"ageLimit\":null,\"fixedPsgCnt\":null,\"idTypeText\":{\"ADT\":\"成人限证件类型：身份证，护照，军官证，台胞证，回乡证，户口薄，出生证明。\",\"CHD\":\"儿童限证件类型：身份证，护照，军官证，台胞证，回乡证，户口薄，出生证明。\"},\"maxPsgCnt\":\"9\",\"minPsgCnt\":\"1\",\"prohibitSale\":false,\"psgTypeLimit\":[{\"allowIdType\":[\"ID\",\"PP\",\"MI\",\"MT\",\"HV\",\"RBT\",\"BRC\"],\"allowPsgType\":\"ADT\",\"maxCnt\":0,\"minCnt\":0},{\"allowIdType\":[\"ID\",\"PP\",\"MI\",\"MT\",\"HV\",\"RBT\",\"BRC\"],\"allowPsgType\":\"CHD\",\"maxCnt\":0,\"minCnt\":0}]}},\"drawTicketTime\":225,\"extraInfo\":\"eyJleHRyYUluZm8iOiIzNDUwNS1ZU0gtREVGIn0=\",\"flightNos\":null,\"iataCode\":\"08071206\",\"iataName\":\"天津市经典假期航空票务服务有限公司\",\"realVendorId\":\"13\"}],\"priceInfoId\":\"\",\"priceJourneyCabinList\":[{\"priceFlightCabinList\":[{\"actionCode\":\"0\",\"actionName\":null,\"baggageInfo\":\"长沙-宁波：免费托运行李20公斤 ;\",\"baggageInfoBaby\":null,\"baggageInfoCHD\":\"长沙-宁波：免费托运行李20公斤 ;\",\"baggageInfoEntity\":{\"check\":{\"number\":\"\",\"size\":\"\",\"weight\":20},\"portable\":{\"number\":null,\"size\":null,\"weight\":0},\"remark\":\"\"},\"baggageInfoEntityBaby\":null,\"baggageInfoEntityCHD\":{\"check\":{\"number\":\"\",\"size\":\"\",\"weight\":20},\"portable\":{\"number\":null,\"size\":null,\"weight\":0},\"remark\":\"\"},\"cabinClass\":\"Y\",\"cabinCode\":\"L\",\"cabinKey\":\"0-3#EU2749#2018-11-07#CSX#NGB#L\",\"cabinStatus\":10,\"cabinType\":1,\"cabinTypeName\":\"经济舱\",\"fullPrice\":1020,\"infCabinStatus\":4,\"insureList\":null,\"journeyNumber\":0,\"mealCode\":\"D\",\"mealCouponList\":null,\"mealName\":\"有餐\",\"seatAliasName\":\"\",\"seatClassDesc\":\"\",\"segmentNumber\":0,\"specCabinCode\":\"L\",\"specCabinStatus\":10,\"vipRoomList\":null}]}],\"prodClassific\":{\"ruleCode\":\"140\"},\"promotionList\":null,\"saleControl\":{\"aduDivideBaby\":\"1/1\",\"aduDivideChd\":null,\"aduDivideChdBaby\":\"1/2\",\"ageLimit\":null,\"fixedPsgCnt\":null,\"idTypeText\":{\"ADT\":\"成人限证件类型：身份证，护照，军官证，台胞证，回乡证，户口薄，出生证明。\",\"CHD\":\"儿童限证件类型：身份证，护照，军官证，台胞证，回乡证，户口薄，出生证明。\"},\"maxPsgCnt\":\"9\",\"minPsgCnt\":\"1\",\"prohibitSale\":false,\"psgTypeLimit\":[{\"allowIdType\":[\"ID\",\"PP\",\"MI\",\"MT\",\"HV\",\"RBT\",\"BRC\"],\"allowPsgType\":\"ADT\",\"maxCnt\":0,\"minCnt\":0},{\"allowIdType\":[\"ID\",\"PP\",\"MI\",\"MT\",\"HV\",\"RBT\",\"BRC\"],\"allowPsgType\":\"CHD\",\"maxCnt\":0,\"minCnt\":0}]},\"tieCouponList\":null,\"tieInfoList\":null,\"vendorId\":\"13\"},{\"aduDivideBaby\":\"1/1\",\"aduDivideChd\":null,\"aduDivideChdBaby\":\"1/2\",\"applyType\":\"1\",\"fareBreakdownList\":[{\"airportTax\":50,\"baseFare\":640,\"discount\":0.63,\"fuleTax\":50,\"optionFareBreakdowns\":[{\"airportTax\":50,\"baseFare\":640,\"fuleTax\":50,\"policyId\":\"\",\"policyType\":0}],\"psgType\":\"ADT\",\"specCabinStatusChange\":\"0\",\"specVendorId\":\"\"},{\"airportTax\":0,\"baseFare\":110,\"fuleTax\":0,\"optionFareBreakdowns\":[{\"airportTax\":0,\"baseFare\":110,\"fuleTax\":0,\"policyId\":\"\",\"policyType\":0}],\"psgType\":\"INF\",\"specCabinStatusChange\":\"0\",\"specVendorId\":\"\"},{\"airportTax\":0,\"baseFare\":510,\"fuleTax\":20,\"optionFareBreakdowns\":[{\"airportTax\":0,\"baseFare\":510,\"fuleTax\":20,\"policyId\":\"\",\"policyType\":0}],\"psgType\":\"CHD\",\"specCabinStatusChange\":\"0\",\"specVendorId\":\"\"}],\"groupDescribe\":\"航信\",\"groupId\":\"PRICEGROUP_1524811797721\",\"groupName\":\"航信\",\"iconPath\":\"\",\"isShow\":1,\"lowestRefundList\":[{\"adultLowestRefund\":\"退改?128起\",\"childLowestRefund\":\"\",\"flightNos\":\"EU2749\"}],\"optionExtras\":[{\"additionInfo\":{\"saleControl\":{\"aduDivideBaby\":\"1/1\",\"aduDivideChd\":null,\"aduDivideChdBaby\":\"1/2\",\"ageLimit\":null,\"fixedPsgCnt\":null,\"idTypeText\":{\"INF\":\"婴儿限证件类型：身份证，出生证明，户口薄。\"},\"maxPsgCnt\":\"9\",\"minPsgCnt\":\"1\",\"prohibitSale\":false,\"psgTypeLimit\":[{\"allowIdType\":[\"ID\",\"BRC\",\"RBT\"],\"allowPsgType\":\"INF\",\"maxCnt\":2,\"minCnt\":0}]}},\"drawTicketTime\":20,\"extraInfo\":\"eyJmYXJlQmFzaXMiOiJMIiwiZmRUeXBlIjoiRkQifQ==\",\"flightNos\":null,\"iataCode\":\"08319463\",\"iataName\":\"南京途之旅票务服务有限公司\",\"realVendorId\":\"1\"}],\"priceInfoId\":\"\",\"priceJourneyCabinList\":[{\"priceFlightCabinList\":[{\"actionCode\":\"0\",\"actionName\":null,\"baggageInfo\":\"长沙-宁波：免费托运行李20公斤 ;\",\"baggageInfoBaby\":null,\"baggageInfoCHD\":\"长沙-宁波：免费托运行李20公斤 ;\",\"baggageInfoEntity\":{\"check\":{\"number\":\"\",\"size\":\"\",\"weight\":20},\"portable\":{\"number\":null,\"size\":null,\"weight\":0},\"remark\":\"\"},\"baggageInfoEntityBaby\":null,\"baggageInfoEntityCHD\":{\"check\":{\"number\":\"\",\"size\":\"\",\"weight\":20},\"portable\":{\"number\":null,\"size\":null,\"weight\":0},\"remark\":\"\"},\"cabinClass\":\"Y\",\"cabinCode\":\"L\",\"cabinKey\":\"0-4#EU2749#2018-11-07#CSX#NGB#L\",\"cabinStatus\":10,\"cabinType\":1,\"cabinTypeName\":\"经济舱\",\"fullPrice\":1020,\"infCabinStatus\":4,\"insureList\":null,\"journeyNumber\":0,\"mealCode\":\"D\",\"mealCouponList\":null,\"mealName\":\"有餐\",\"seatAliasName\":\"\",\"seatClassDesc\":\"\",\"segmentNumber\":0,\"specCabinCode\":\"Y\",\"specCabinStatus\":10,\"vipRoomList\":null}]}],\"prodClassific\":{\"ruleCode\":\"140\"},\"promotionList\":null,\"saleControl\":{\"aduDivideBaby\":\"1/1\",\"aduDivideChd\":null,\"aduDivideChdBaby\":\"1/2\",\"ageLimit\":null,\"fixedPsgCnt\":null,\"idTypeText\":{\"INF\":\"婴儿限证件类型：身份证，出生证明，户口薄。\"},\"maxPsgCnt\":\"9\",\"minPsgCnt\":\"1\",\"prohibitSale\":false,\"psgTypeLimit\":[{\"allowIdType\":[\"ID\",\"BRC\",\"RBT\"],\"allowPsgType\":\"INF\",\"maxCnt\":2,\"minCnt\":0}]},\"tieCouponList\":null,\"tieInfoList\":null,\"vendorId\":\"1\"},{\"aduDivideBaby\":\"1/1\",\"aduDivideChd\":null,\"aduDivideChdBaby\":\"1/2\",\"applyType\":\"1/2\",\"fareBreakdownList\":[{\"airportTax\":50,\"baseFare\":714,\"discount\":0.7,\"fuleTax\":50,\"optionFareBreakdowns\":[{\"airportTax\":50,\"baseFare\":714,\"fuleTax\":50,\"policyId\":null,\"policyType\":0}],\"psgType\":\"ADT\",\"specCabinStatusChange\":\"0\",\"specVendorId\":\"\"},{\"airportTax\":0,\"baseFare\":556,\"fuleTax\":20,\"optionFareBreakdowns\":[{\"airportTax\":0,\"baseFare\":556,\"fuleTax\":20,\"policyId\":null,\"policyType\":0}],\"psgType\":\"CHD\",\"specCabinStatusChange\":\"0\",\"specVendorId\":\"\"}],\"groupDescribe\":\"除直连和IBE\",\"groupId\":\"PRICEGROUP_1524811839460\",\"groupName\":\"其他\",\"iconPath\":\"\",\"isShow\":1,\"lowestRefundList\":[{\"adultLowestRefund\":\"退改?71起\",\"childLowestRefund\":\"\",\"flightNos\":\"EU2749\"}],\"optionExtras\":[{\"additionInfo\":{\"saleControl\":{\"aduDivideBaby\":\"1/1\",\"aduDivideChd\":null,\"aduDivideChdBaby\":\"1/2\",\"ageLimit\":null,\"fixedPsgCnt\":null,\"idTypeText\":{\"ADT\":\"成人限证件类型：身份证，护照，军官证，台胞证，回乡证，户口薄，出生证明。\",\"CHD\":\"儿童限证件类型：身份证，护照，军官证，台胞证，回乡证，户口薄，出生证明。\"},\"maxPsgCnt\":\"9\",\"minPsgCnt\":\"1\",\"prohibitSale\":false,\"psgTypeLimit\":[{\"allowIdType\":[\"ID\",\"PP\",\"MI\",\"MT\",\"HV\",\"RBT\",\"BRC\"],\"allowPsgType\":\"ADT\",\"maxCnt\":0,\"minCnt\":0},{\"allowIdType\":[\"ID\",\"PP\",\"MI\",\"MT\",\"HV\",\"RBT\",\"BRC\"],\"allowPsgType\":\"CHD\",\"maxCnt\":0,\"minCnt\":0}]}},\"drawTicketTime\":225,\"extraInfo\":\"eyJleHRyYUluZm8iOiIzNDUwNS1ZU0gtREVGIn0=\",\"flightNos\":null,\"iataCode\":\"08071206\",\"iataName\":\"天津市经典假期航空票务服务有限公司\",\"realVendorId\":\"13\"}],\"priceInfoId\":\"\",\"priceJourneyCabinList\":[{\"priceFlightCabinList\":[{\"actionCode\":\"0\",\"actionName\":null,\"baggageInfo\":\"长沙-宁波：免费托运行李20公斤 ;\",\"baggageInfoBaby\":null,\"baggageInfoCHD\":\"长沙-宁波：免费托运行李20公斤 ;\",\"baggageInfoEntity\":{\"check\":{\"number\":\"\",\"size\":\"\",\"weight\":20},\"portable\":{\"number\":null,\"size\":null,\"weight\":0},\"remark\":\"\"},\"baggageInfoEntityBaby\":null,\"baggageInfoEntityCHD\":{\"check\":{\"number\":\"\",\"size\":\"\",\"weight\":20},\"portable\":{\"number\":null,\"size\":null,\"weight\":0},\"remark\":\"\"},\"cabinClass\":\"Y\",\"cabinCode\":\"G\",\"cabinKey\":\"0-5#EU2749#2018-11-07#CSX#NGB#G\",\"cabinStatus\":10,\"cabinType\":1,\"cabinTypeName\":\"经济舱\",\"fullPrice\":1020,\"infCabinStatus\":4,\"insureList\":null,\"journeyNumber\":0,\"mealCode\":\"D\",\"mealCouponList\":null,\"mealName\":\"有餐\",\"seatAliasName\":\"\",\"seatClassDesc\":\"\",\"segmentNumber\":0,\"specCabinCode\":\"G\",\"specCabinStatus\":10,\"vipRoomList\":null}]}],\"prodClassific\":{\"ruleCode\":\"140\"},\"promotionList\":null,\"saleControl\":{\"aduDivideBaby\":\"1/1\",\"aduDivideChd\":null,\"aduDivideChdBaby\":\"1/2\",\"ageLimit\":null,\"fixedPsgCnt\":null,\"idTypeText\":{\"ADT\":\"成人限证件类型：身份证，护照，军官证，台胞证，回乡证，户口薄，出生证明。\",\"CHD\":\"儿童限证件类型：身份证，护照，军官证，台胞证，回乡证，户口薄，出生证明。\"},\"maxPsgCnt\":\"9\",\"minPsgCnt\":\"1\",\"prohibitSale\":false,\"psgTypeLimit\":[{\"allowIdType\":[\"ID\",\"PP\",\"MI\",\"MT\",\"HV\",\"RBT\",\"BRC\"],\"allowPsgType\":\"ADT\",\"maxCnt\":0,\"minCnt\":0},{\"allowIdType\":[\"ID\",\"PP\",\"MI\",\"MT\",\"HV\",\"RBT\",\"BRC\"],\"allowPsgType\":\"CHD\",\"maxCnt\":0,\"minCnt\":0}]},\"tieCouponList\":null,\"tieInfoList\":null,\"vendorId\":\"13\"},{\"aduDivideBaby\":\"1/1\",\"aduDivideChd\":null,\"aduDivideChdBaby\":\"1/2\",\"applyType\":\"1\",\"fareBreakdownList\":[{\"airportTax\":50,\"baseFare\":740,\"discount\":0.73,\"fuleTax\":50,\"optionFareBreakdowns\":[{\"airportTax\":50,\"baseFare\":740,\"fuleTax\":50,\"policyId\":\"\",\"policyType\":0}],\"psgType\":\"ADT\",\"specCabinStatusChange\":\"0\",\"specVendorId\":\"\"},{\"airportTax\":0,\"baseFare\":110,\"fuleTax\":0,\"optionFareBreakdowns\":[{\"airportTax\":0,\"baseFare\":110,\"fuleTax\":0,\"policyId\":\"\",\"policyType\":0}],\"psgType\":\"INF\",\"specCabinStatusChange\":\"0\",\"specVendorId\":\"\"},{\"airportTax\":0,\"baseFare\":510,\"fuleTax\":20,\"optionFareBreakdowns\":[{\"airportTax\":0,\"baseFare\":510,\"fuleTax\":20,\"policyId\":\"\",\"policyType\":0}],\"psgType\":\"CHD\",\"specCabinStatusChange\":\"0\",\"specVendorId\":\"\"}],\"groupDescribe\":\"航信\",\"groupId\":\"PRICEGROUP_1524811797721\",\"groupName\":\"航信\",\"iconPath\":\"\",\"isShow\":1,\"lowestRefundList\":[{\"adultLowestRefund\":\"退改?74起\",\"childLowestRefund\":\"\",\"flightNos\":\"EU2749\"}],\"optionExtras\":[{\"additionInfo\":{\"saleControl\":{\"aduDivideBaby\":\"1/1\",\"aduDivideChd\":null,\"aduDivideChdBaby\":\"1/2\",\"ageLimit\":null,\"fixedPsgCnt\":null,\"idTypeText\":{\"INF\":\"婴儿限证件类型：身份证，出生证明，户口薄。\"},\"maxPsgCnt\":\"9\",\"minPsgCnt\":\"1\",\"prohibitSale\":false,\"psgTypeLimit\":[{\"allowIdType\":[\"ID\",\"BRC\",\"RBT\"],\"allowPsgType\":\"INF\",\"maxCnt\":2,\"minCnt\":0}]}},\"drawTicketTime\":20,\"extraInfo\":\"eyJmYXJlQmFzaXMiOiJHIiwiZmRUeXBlIjoiRkQifQ==\",\"flightNos\":null,\"iataCode\":\"08319463\",\"iataName\":\"南京途之旅票务服务有限公司\",\"realVendorId\":\"1\"}],\"priceInfoId\":\"\",\"priceJourneyCabinList\":[{\"priceFlightCabinList\":[{\"actionCode\":\"0\",\"actionName\":null,\"baggageInfo\":\"长沙-宁波：免费托运行李20公斤 ;\",\"baggageInfoBaby\":null,\"baggageInfoCHD\":\"长沙-宁波：免费托运行李20公斤 ;\",\"baggageInfoEntity\":{\"check\":{\"number\":\"\",\"size\":\"\",\"weight\":20},\"portable\":{\"number\":null,\"size\":null,\"weight\":0},\"remark\":\"\"},\"baggageInfoEntityBaby\":null,\"baggageInfoEntityCHD\":{\"check\":{\"number\":\"\",\"size\":\"\",\"weight\":20},\"portable\":{\"number\":null,\"size\":null,\"weight\":0},\"remark\":\"\"},\"cabinClass\":\"Y\",\"cabinCode\":\"G\",\"cabinKey\":\"0-6#EU2749#2018-11-07#CSX#NGB#G\",\"cabinStatus\":10,\"cabinType\":1,\"cabinTypeName\":\"经济舱\",\"fullPrice\":1020,\"infCabinStatus\":4,\"insureList\":null,\"journeyNumber\":0,\"mealCode\":\"D\",\"mealCouponList\":null,\"mealName\":\"有餐\",\"seatAliasName\":\"\",\"seatClassDesc\":\"\",\"segmentNumber\":0,\"specCabinCode\":\"Y\",\"specCabinStatus\":10,\"vipRoomList\":null}]}],\"prodClassific\":{\"ruleCode\":\"140\"},\"promotionList\":null,\"saleControl\":{\"aduDivideBaby\":\"1/1\",\"aduDivideChd\":null,\"aduDivideChdBaby\":\"1/2\",\"ageLimit\":null,\"fixedPsgCnt\":null,\"idTypeText\":{\"INF\":\"婴儿限证件类型：身份证，出生证明，户口薄。\"},\"maxPsgCnt\":\"9\",\"minPsgCnt\":\"1\",\"prohibitSale\":false,\"psgTypeLimit\":[{\"allowIdType\":[\"ID\",\"BRC\",\"RBT\"],\"allowPsgType\":\"INF\",\"maxCnt\":2,\"minCnt\":0}]},\"tieCouponList\":null,\"tieInfoList\":null,\"vendorId\":\"1\"},{\"aduDivideBaby\":\"1/1\",\"aduDivideChd\":null,\"aduDivideChdBaby\":\"1/2\",\"applyType\":\"1/2\",\"fareBreakdownList\":[{\"airportTax\":50,\"baseFare\":811,\"discount\":0.8,\"fuleTax\":50,\"optionFareBreakdowns\":[{\"airportTax\":50,\"baseFare\":811,\"fuleTax\":50,\"policyId\":null,\"policyType\":0}],\"psgType\":\"ADT\",\"specCabinStatusChange\":\"0\",\"specVendorId\":\"\"},{\"airportTax\":0,\"baseFare\":556,\"fuleTax\":20,\"optionFareBreakdowns\":[{\"airportTax\":0,\"baseFare\":556,\"fuleTax\":20,\"policyId\":null,\"policyType\":0}],\"psgType\":\"CHD\",\"specCabinStatusChange\":\"0\",\"specVendorId\":\"\"}],\"groupDescribe\":\"除直连和IBE\",\"groupId\":\"PRICEGROUP_1524811839460\",\"groupName\":\"其他\",\"iconPath\":\"\",\"isShow\":1,\"lowestRefundList\":[{\"adultLowestRefund\":\"退改?81起\",\"childLowestRefund\":\"\",\"flightNos\":\"EU2749\"}],\"optionExtras\":[{\"additionInfo\":{\"saleControl\":{\"aduDivideBaby\":\"1/1\",\"aduDivideChd\":null,\"aduDivideChdBaby\":\"1/2\",\"ageLimit\":null,\"fixedPsgCnt\":null,\"idTypeText\":{\"ADT\":\"成人限证件类型：身份证，护照，军官证，台胞证，回乡证，户口薄，出生证明。\",\"CHD\":\"儿童限证件类型：身份证，护照，军官证，台胞证，回乡证，户口薄，出生证明。\"},\"maxPsgCnt\":\"9\",\"minPsgCnt\":\"1\",\"prohibitSale\":false,\"psgTypeLimit\":[{\"allowIdType\":[\"ID\",\"PP\",\"MI\",\"MT\",\"HV\",\"RBT\",\"BRC\"],\"allowPsgType\":\"ADT\",\"maxCnt\":0,\"minCnt\":0},{\"allowIdType\":[\"ID\",\"PP\",\"MI\",\"MT\",\"HV\",\"RBT\",\"BRC\"],\"allowPsgType\":\"CHD\",\"maxCnt\":0,\"minCnt\":0}]}},\"drawTicketTime\":225,\"extraInfo\":\"eyJleHRyYUluZm8iOiIzNDUwNS1ZU0gtREVGIn0=\",\"flightNos\":null,\"iataCode\":\"08071206\",\"iataName\":\"天津市经典假期航空票务服务有限公司\",\"realVendorId\":\"13\"}],\"priceInfoId\":\"\",\"priceJourneyCabinList\":[{\"priceFlightCabinList\":[{\"actionCode\":\"0\",\"actionName\":null,\"baggageInfo\":\"长沙-宁波：免费托运行李20公斤 ;\",\"baggageInfoBaby\":null,\"baggageInfoCHD\":\"长沙-宁波：免费托运行李20公斤 ;\",\"baggageInfoEntity\":{\"check\":{\"number\":\"\",\"size\":\"\",\"weight\":20},\"portable\":{\"number\":null,\"size\":null,\"weight\":0},\"remark\":\"\"},\"baggageInfoEntityBaby\":null,\"baggageInfoEntityCHD\":{\"check\":{\"number\":\"\",\"size\":\"\",\"weight\":20},\"portable\":{\"number\":null,\"size\":null,\"weight\":0},\"remark\":\"\"},\"cabinClass\":\"Y\",\"cabinCode\":\"H\",\"cabinKey\":\"0-7#EU2749#2018-11-07#CSX#NGB#H\",\"cabinStatus\":10,\"cabinType\":1,\"cabinTypeName\":\"经济舱\",\"fullPrice\":1020,\"infCabinStatus\":4,\"insureList\":null,\"journeyNumber\":0,\"mealCode\":\"D\",\"mealCouponList\":null,\"mealName\":\"有餐\",\"seatAliasName\":\"\",\"seatClassDesc\":\"\",\"segmentNumber\":0,\"specCabinCode\":\"H\",\"specCabinStatus\":10,\"vipRoomList\":null}]}],\"prodClassific\":{\"ruleCode\":\"140\"},\"promotionList\":null,\"saleControl\":{\"aduDivideBaby\":\"1/1\",\"aduDivideChd\":null,\"aduDivideChdBaby\":\"1/2\",\"ageLimit\":null,\"fixedPsgCnt\":null,\"idTypeText\":{\"ADT\":\"成人限证件类型：身份证，护照，军官证，台胞证，回乡证，户口薄，出生证明。\",\"CHD\":\"儿童限证件类型：身份证，护照，军官证，台胞证，回乡证，户口薄，出生证明。\"},\"maxPsgCnt\":\"9\",\"minPsgCnt\":\"1\",\"prohibitSale\":false,\"psgTypeLimit\":[{\"allowIdType\":[\"ID\",\"PP\",\"MI\",\"MT\",\"HV\",\"RBT\",\"BRC\"],\"allowPsgType\":\"ADT\",\"maxCnt\":0,\"minCnt\":0},{\"allowIdType\":[\"ID\",\"PP\",\"MI\",\"MT\",\"HV\",\"RBT\",\"BRC\"],\"allowPsgType\":\"CHD\",\"maxCnt\":0,\"minCnt\":0}]},\"tieCouponList\":null,\"tieInfoList\":null,\"vendorId\":\"13\"},{\"aduDivideBaby\":\"1/1\",\"aduDivideChd\":null,\"aduDivideChdBaby\":\"1/2\",\"applyType\":\"1\",\"fareBreakdownList\":[{\"airportTax\":50,\"baseFare\":850,\"discount\":0.83,\"fuleTax\":50,\"optionFareBreakdowns\":[{\"airportTax\":50,\"baseFare\":850,\"fuleTax\":50,\"policyId\":\"\",\"policyType\":0}],\"psgType\":\"ADT\",\"specCabinStatusChange\":\"0\",\"specVendorId\":\"\"},{\"airportTax\":0,\"baseFare\":110,\"fuleTax\":0,\"optionFareBreakdowns\":[{\"airportTax\":0,\"baseFare\":110,\"fuleTax\":0,\"policyId\":\"\",\"policyType\":0}],\"psgType\":\"INF\",\"specCabinStatusChange\":\"0\",\"specVendorId\":\"\"},{\"airportTax\":0,\"baseFare\":510,\"fuleTax\":20,\"optionFareBreakdowns\":[{\"airportTax\":0,\"baseFare\":510,\"fuleTax\":20,\"policyId\":\"\",\"policyType\":0}],\"psgType\":\"CHD\",\"specCabinStatusChange\":\"0\",\"specVendorId\":\"\"}],\"groupDescribe\":\"航信\",\"groupId\":\"PRICEGROUP_1524811797721\",\"groupName\":\"航信\",\"iconPath\":\"\",\"isShow\":1,\"lowestRefundList\":[{\"adultLowestRefund\":\"退改?85起\",\"childLowestRefund\":\"\",\"flightNos\":\"EU2749\"}],\"optionExtras\":[{\"additionInfo\":{\"saleControl\":{\"aduDivideBaby\":\"1/1\",\"aduDivideChd\":null,\"aduDivideChdBaby\":\"1/2\",\"ageLimit\":null,\"fixedPsgCnt\":null,\"idTypeText\":{\"INF\":\"婴儿限证件类型：身份证，出生证明，户口薄。\"},\"maxPsgCnt\":\"9\",\"minPsgCnt\":\"1\",\"prohibitSale\":false,\"psgTypeLimit\":[{\"allowIdType\":[\"ID\",\"BRC\",\"RBT\"],\"allowPsgType\":\"INF\",\"maxCnt\":2,\"minCnt\":0}]}},\"drawTicketTime\":20,\"extraInfo\":\"eyJmYXJlQmFzaXMiOiJIIiwiZmRUeXBlIjoiRkQifQ==\",\"flightNos\":null,\"iataCode\":\"08319463\",\"iataName\":\"南京途之旅票务服务有限公司\",\"realVendorId\":\"1\"}],\"priceInfoId\":\"\",\"priceJourneyCabinList\":[{\"priceFlightCabinList\":[{\"actionCode\":\"0\",\"actionName\":null,\"baggageInfo\":\"长沙-宁波：免费托运行李20公斤 ;\",\"baggageInfoBaby\":null,\"baggageInfoCHD\":\"长沙-宁波：免费托运行李20公斤 ;\",\"baggageInfoEntity\":{\"check\":{\"number\":\"\",\"size\":\"\",\"weight\":20},\"portable\":{\"number\":null,\"size\":null,\"weight\":0},\"remark\":\"\"},\"baggageInfoEntityBaby\":null,\"baggageInfoEntityCHD\":{\"check\":{\"number\":\"\",\"size\":\"\",\"weight\":20},\"portable\":{\"number\":null,\"size\":null,\"weight\":0},\"remark\":\"\"},\"cabinClass\":\"Y\",\"cabinCode\":\"H\",\"cabinKey\":\"0-8#EU2749#2018-11-07#CSX#NGB#H\",\"cabinStatus\":10,\"cabinType\":1,\"cabinTypeName\":\"经济舱\",\"fullPrice\":1020,\"infCabinStatus\":4,\"insureList\":null,\"journeyNumber\":0,\"mealCode\":\"D\",\"mealCouponList\":null,\"mealName\":\"有餐\",\"seatAliasName\":\"\",\"seatClassDesc\":\"\",\"segmentNumber\":0,\"specCabinCode\":\"Y\",\"specCabinStatus\":10,\"vipRoomList\":null}]}],\"prodClassific\":{\"ruleCode\":\"140\"},\"promotionList\":null,\"saleControl\":{\"aduDivideBaby\":\"1/1\",\"aduDivideChd\":null,\"aduDivideChdBaby\":\"1/2\",\"ageLimit\":null,\"fixedPsgCnt\":null,\"idTypeText\":{\"INF\":\"婴儿限证件类型：身份证，出生证明，户口薄。\"},\"maxPsgCnt\":\"9\",\"minPsgCnt\":\"1\",\"prohibitSale\":false,\"psgTypeLimit\":[{\"allowIdType\":[\"ID\",\"BRC\",\"RBT\"],\"allowPsgType\":\"INF\",\"maxCnt\":2,\"minCnt\":0}]},\"tieCouponList\":null,\"tieInfoList\":null,\"vendorId\":\"1\"},{\"aduDivideBaby\":\"1/1\",\"aduDivideChd\":null,\"aduDivideChdBaby\":\"1/2\",\"applyType\":\"1/2\",\"fareBreakdownList\":[{\"airportTax\":50,\"baseFare\":913,\"discount\":0.9,\"fuleTax\":50,\"optionFareBreakdowns\":[{\"airportTax\":50,\"baseFare\":913,\"fuleTax\":50,\"policyId\":null,\"policyType\":0}],\"psgType\":\"ADT\",\"specCabinStatusChange\":\"0\",\"specVendorId\":\"\"},{\"airportTax\":0,\"baseFare\":556,\"fuleTax\":20,\"optionFareBreakdowns\":[{\"airportTax\":0,\"baseFare\":556,\"fuleTax\":20,\"policyId\":null,\"policyType\":0}],\"psgType\":\"CHD\",\"specCabinStatusChange\":\"0\",\"specVendorId\":\"\"}],\"groupDescribe\":\"除直连和IBE\",\"groupId\":\"PRICEGROUP_1524811839460\",\"groupName\":\"其他\",\"iconPath\":\"\",\"isShow\":1,\"lowestRefundList\":[{\"adultLowestRefund\":\"退改?91起\",\"childLowestRefund\":\"\",\"flightNos\":\"EU2749\"}],\"optionExtras\":[{\"additionInfo\":{\"saleControl\":{\"aduDivideBaby\":\"1/1\",\"aduDivideChd\":null,\"aduDivideChdBaby\":\"1/2\",\"ageLimit\":null,\"fixedPsgCnt\":null,\"idTypeText\":{\"ADT\":\"成人限证件类型：身份证，护照，军官证，台胞证，回乡证，户口薄，出生证明。\",\"CHD\":\"儿童限证件类型：身份证，护照，军官证，台胞证，回乡证，户口薄，出生证明。\"},\"maxPsgCnt\":\"9\",\"minPsgCnt\":\"1\",\"prohibitSale\":false,\"psgTypeLimit\":[{\"allowIdType\":[\"ID\",\"PP\",\"MI\",\"MT\",\"HV\",\"RBT\",\"BRC\"],\"allowPsgType\":\"ADT\",\"maxCnt\":0,\"minCnt\":0},{\"allowIdType\":[\"ID\",\"PP\",\"MI\",\"MT\",\"HV\",\"RBT\",\"BRC\"],\"allowPsgType\":\"CHD\",\"maxCnt\":0,\"minCnt\":0}]}},\"drawTicketTime\":225,\"extraInfo\":\"eyJleHRyYUluZm8iOiIzNDUwNS1ZU0gtREVGIn0=\",\"flightNos\":null,\"iataCode\":\"08071206\",\"iataName\":\"天津市经典假期航空票务服务有限公司\",\"realVendorId\":\"13\"}],\"priceInfoId\":\"\",\"priceJourneyCabinList\":[{\"priceFlightCabinList\":[{\"actionCode\":\"0\",\"actionName\":null,\"baggageInfo\":\"长沙-宁波：免费托运行李20公斤 ;\",\"baggageInfoBaby\":null,\"baggageInfoCHD\":\"长沙-宁波：免费托运行李20公斤 ;\",\"baggageInfoEntity\":{\"check\":{\"number\":\"\",\"size\":\"\",\"weight\":20},\"portable\":{\"number\":null,\"size\":null,\"weight\":0},\"remark\":\"\"},\"baggageInfoEntityBaby\":null,\"baggageInfoEntityCHD\":{\"check\":{\"number\":\"\",\"size\":\"\",\"weight\":20},\"portable\":{\"number\":null,\"size\":null,\"weight\":0},\"remark\":\"\"},\"cabinClass\":\"Y\",\"cabinCode\":\"T\",\"cabinKey\":\"0-9#EU2749#2018-11-07#CSX#NGB#T\",\"cabinStatus\":10,\"cabinType\":1,\"cabinTypeName\":\"经济舱\",\"fullPrice\":1020,\"infCabinStatus\":4,\"insureList\":null,\"journeyNumber\":0,\"mealCode\":\"D\",\"mealCouponList\":null,\"mealName\":\"有餐\",\"seatAliasName\":\"\",\"seatClassDesc\":\"\",\"segmentNumber\":0,\"specCabinCode\":\"T\",\"specCabinStatus\":10,\"vipRoomList\":null}]}],\"prodClassific\":{\"ruleCode\":\"140\"},\"promotionList\":null,\"saleControl\":{\"aduDivideBaby\":\"1/1\",\"aduDivideChd\":null,\"aduDivideChdBaby\":\"1/2\",\"ageLimit\":null,\"fixedPsgCnt\":null,\"idTypeText\":{\"ADT\":\"成人限证件类型：身份证，护照，军官证，台胞证，回乡证，户口薄，出生证明。\",\"CHD\":\"儿童限证件类型：身份证，护照，军官证，台胞证，回乡证，户口薄，出生证明。\"},\"maxPsgCnt\":\"9\",\"minPsgCnt\":\"1\",\"prohibitSale\":false,\"psgTypeLimit\":[{\"allowIdType\":[\"ID\",\"PP\",\"MI\",\"MT\",\"HV\",\"RBT\",\"BRC\"],\"allowPsgType\":\"ADT\",\"maxCnt\":0,\"minCnt\":0},{\"allowIdType\":[\"ID\",\"PP\",\"MI\",\"MT\",\"HV\",\"RBT\",\"BRC\"],\"allowPsgType\":\"CHD\",\"maxCnt\":0,\"minCnt\":0}]},\"tieCouponList\":null,\"tieInfoList\":null,\"vendorId\":\"13\"},{\"aduDivideBaby\":\"1/1\",\"aduDivideChd\":null,\"aduDivideChdBaby\":\"1/2\",\"applyType\":\"1\",\"fareBreakdownList\":[{\"airportTax\":50,\"baseFare\":950,\"discount\":0.93,\"fuleTax\":50,\"optionFareBreakdowns\":[{\"airportTax\":50,\"baseFare\":950,\"fuleTax\":50,\"policyId\":\"\",\"policyType\":0}],\"psgType\":\"ADT\",\"specCabinStatusChange\":\"0\",\"specVendorId\":\"\"},{\"airportTax\":0,\"baseFare\":110,\"fuleTax\":0,\"optionFareBreakdowns\":[{\"airportTax\":0,\"baseFare\":110,\"fuleTax\":0,\"policyId\":\"\",\"policyType\":0}],\"psgType\":\"INF\",\"specCabinStatusChange\":\"0\",\"specVendorId\":\"\"},{\"airportTax\":0,\"baseFare\":510,\"fuleTax\":20,\"optionFareBreakdowns\":[{\"airportTax\":0,\"baseFare\":510,\"fuleTax\":20,\"policyId\":\"\",\"policyType\":0}],\"psgType\":\"CHD\",\"specCabinStatusChange\":\"0\",\"specVendorId\":\"\"}],\"groupDescribe\":\"航信\",\"groupId\":\"PRICEGROUP_1524811797721\",\"groupName\":\"航信\",\"iconPath\":\"\",\"isShow\":1,\"lowestRefundList\":[{\"adultLowestRefund\":\"退改?95起\",\"childLowestRefund\":\"\",\"flightNos\":\"EU2749\"}],\"optionExtras\":[{\"additionInfo\":{\"saleControl\":{\"aduDivideBaby\":\"1/1\",\"aduDivideChd\":null,\"aduDivideChdBaby\":\"1/2\",\"ageLimit\":null,\"fixedPsgCnt\":null,\"idTypeText\":{\"INF\":\"婴儿限证件类型：身份证，出生证明，户口薄。\"},\"maxPsgCnt\":\"9\",\"minPsgCnt\":\"1\",\"prohibitSale\":false,\"psgTypeLimit\":[{\"allowIdType\":[\"ID\",\"BRC\",\"RBT\"],\"allowPsgType\":\"INF\",\"maxCnt\":2,\"minCnt\":0}]}},\"drawTicketTime\":20,\"extraInfo\":\"eyJmYXJlQmFzaXMiOiJUIiwiZmRUeXBlIjoiRkQifQ==\",\"flightNos\":null,\"iataCode\":\"08319463\",\"iataName\":\"南京途之旅票务服务有限公司\",\"realVendorId\":\"1\"}],\"priceInfoId\":\"\",\"priceJourneyCabinList\":[{\"priceFlightCabinList\":[{\"actionCode\":\"0\",\"actionName\":null,\"baggageInfo\":\"长沙-宁波：免费托运行李20公斤 ;\",\"baggageInfoBaby\":null,\"baggageInfoCHD\":\"长沙-宁波：免费托运行李20公斤 ;\",\"baggageInfoEntity\":{\"check\":{\"number\":\"\",\"size\":\"\",\"weight\":20},\"portable\":{\"number\":null,\"size\":null,\"weight\":0},\"remark\":\"\"},\"baggageInfoEntityBaby\":null,\"baggageInfoEntityCHD\":{\"check\":{\"number\":\"\",\"size\":\"\",\"weight\":20},\"portable\":{\"number\":null,\"size\":null,\"weight\":0},\"remark\":\"\"},\"cabinClass\":\"Y\",\"cabinCode\":\"T\",\"cabinKey\":\"0-10#EU2749#2018-11-07#CSX#NGB#T\",\"cabinStatus\":10,\"cabinType\":1,\"cabinTypeName\":\"经济舱\",\"fullPrice\":1020,\"infCabinStatus\":4,\"insureList\":null,\"journeyNumber\":0,\"mealCode\":\"D\",\"mealCouponList\":null,\"mealName\":\"有餐\",\"seatAliasName\":\"\",\"seatClassDesc\":\"\",\"segmentNumber\":0,\"specCabinCode\":\"Y\",\"specCabinStatus\":10,\"vipRoomList\":null}]}],\"prodClassific\":{\"ruleCode\":\"140\"},\"promotionList\":null,\"saleControl\":{\"aduDivideBaby\":\"1/1\",\"aduDivideChd\":null,\"aduDivideChdBaby\":\"1/2\",\"ageLimit\":null,\"fixedPsgCnt\":null,\"idTypeText\":{\"INF\":\"婴儿限证件类型：身份证，出生证明，户口薄。\"},\"maxPsgCnt\":\"9\",\"minPsgCnt\":\"1\",\"prohibitSale\":false,\"psgTypeLimit\":[{\"allowIdType\":[\"ID\",\"BRC\",\"RBT\"],\"allowPsgType\":\"INF\",\"maxCnt\":2,\"minCnt\":0}]},\"tieCouponList\":null,\"tieInfoList\":null,\"vendorId\":\"1\"},{\"aduDivideBaby\":\"1/1\",\"aduDivideChd\":null,\"aduDivideChdBaby\":\"1/2\",\"applyType\":\"1/2\",\"fareBreakdownList\":[{\"airportTax\":50,\"baseFare\":987,\"discount\":0.97,\"fuleTax\":50,\"optionFareBreakdowns\":[{\"airportTax\":50,\"baseFare\":987,\"fuleTax\":50,\"policyId\":null,\"policyType\":0}],\"psgType\":\"ADT\",\"specCabinStatusChange\":\"0\",\"specVendorId\":\"\"},{\"airportTax\":0,\"baseFare\":556,\"fuleTax\":20,\"optionFareBreakdowns\":[{\"airportTax\":0,\"baseFare\":556,\"fuleTax\":20,\"policyId\":null,\"policyType\":0}],\"psgType\":\"CHD\",\"specCabinStatusChange\":\"0\",\"specVendorId\":\"\"}],\"groupDescribe\":\"除直连和IBE\",\"groupId\":\"PRICEGROUP_1524811839460\",\"groupName\":\"其他\",\"iconPath\":\"\",\"isShow\":1,\"lowestRefundList\":[{\"adultLowestRefund\":\"退改?49起\",\"childLowestRefund\":\"\",\"flightNos\":\"EU2749\"}],\"optionExtras\":[{\"additionInfo\":{\"saleControl\":{\"aduDivideBaby\":\"1/1\",\"aduDivideChd\":null,\"aduDivideChdBaby\":\"1/2\",\"ageLimit\":null,\"fixedPsgCnt\":null,\"idTypeText\":{\"ADT\":\"成人限证件类型：身份证，护照，军官证，台胞证，回乡证，户口薄，出生证明。\",\"CHD\":\"儿童限证件类型：身份证，护照，军官证，台胞证，回乡证，户口薄，出生证明。\"},\"maxPsgCnt\":\"9\",\"minPsgCnt\":\"1\",\"prohibitSale\":false,\"psgTypeLimit\":[{\"allowIdType\":[\"ID\",\"PP\",\"MI\",\"MT\",\"HV\",\"RBT\",\"BRC\"],\"allowPsgType\":\"ADT\",\"maxCnt\":0,\"minCnt\":0},{\"allowIdType\":[\"ID\",\"PP\",\"MI\",\"MT\",\"HV\",\"RBT\",\"BRC\"],\"allowPsgType\":\"CHD\",\"maxCnt\":0,\"minCnt\":0}]}},\"drawTicketTime\":225,\"extraInfo\":\"eyJleHRyYUluZm8iOiIzNDUwNS1ZU0gtREVGIn0=\",\"flightNos\":null,\"iataCode\":\"08071206\",\"iataName\":\"天津市经典假期航空票务服务有限公司\",\"realVendorId\":\"13\"}],\"priceInfoId\":\"\",\"priceJourneyCabinList\":[{\"priceFlightCabinList\":[{\"actionCode\":\"0\",\"actionName\":null,\"baggageInfo\":\"长沙-宁波：免费托运行李20公斤 ;\",\"baggageInfoBaby\":null,\"baggageInfoCHD\":\"长沙-宁波：免费托运行李20公斤 ;\",\"baggageInfoEntity\":{\"check\":{\"number\":\"\",\"size\":\"\",\"weight\":20},\"portable\":{\"number\":null,\"size\":null,\"weight\":0},\"remark\":\"\"},\"baggageInfoEntityBaby\":null,\"baggageInfoEntityCHD\":{\"check\":{\"number\":\"\",\"size\":\"\",\"weight\":20},\"portable\":{\"number\":null,\"size\":null,\"weight\":0},\"remark\":\"\"},\"cabinClass\":\"Y\",\"cabinCode\":\"Y\",\"cabinKey\":\"0-11#EU2749#2018-11-07#CSX#NGB#Y\",\"cabinStatus\":10,\"cabinType\":1,\"cabinTypeName\":\"经济舱\",\"fullPrice\":1020,\"infCabinStatus\":4,\"insureList\":null,\"journeyNumber\":0,\"mealCode\":\"D\",\"mealCouponList\":null,\"mealName\":\"有餐\",\"seatAliasName\":\"\",\"seatClassDesc\":\"\",\"segmentNumber\":0,\"specCabinCode\":\"Y\",\"specCabinStatus\":10,\"vipRoomList\":null}]}],\"prodClassific\":{\"ruleCode\":\"140\"},\"promotionList\":null,\"saleControl\":{\"aduDivideBaby\":\"1/1\",\"aduDivideChd\":null,\"aduDivideChdBaby\":\"1/2\",\"ageLimit\":null,\"fixedPsgCnt\":null,\"idTypeText\":{\"ADT\":\"成人限证件类型：身份证，护照，军官证，台胞证，回乡证，户口薄，出生证明。\",\"CHD\":\"儿童限证件类型：身份证，护照，军官证，台胞证，回乡证，户口薄，出生证明。\"},\"maxPsgCnt\":\"9\",\"minPsgCnt\":\"1\",\"prohibitSale\":false,\"psgTypeLimit\":[{\"allowIdType\":[\"ID\",\"PP\",\"MI\",\"MT\",\"HV\",\"RBT\",\"BRC\"],\"allowPsgType\":\"ADT\",\"maxCnt\":0,\"minCnt\":0},{\"allowIdType\":[\"ID\",\"PP\",\"MI\",\"MT\",\"HV\",\"RBT\",\"BRC\"],\"allowPsgType\":\"CHD\",\"maxCnt\":0,\"minCnt\":0}]},\"tieCouponList\":null,\"tieInfoList\":null,\"vendorId\":\"13\"},{\"aduDivideBaby\":\"1/1\",\"aduDivideChd\":null,\"aduDivideChdBaby\":\"1/2\",\"applyType\":\"1\",\"fareBreakdownList\":[{\"airportTax\":50,\"baseFare\":1020,\"discount\":1.0,\"fuleTax\":50,\"optionFareBreakdowns\":[{\"airportTax\":50,\"baseFare\":1020,\"fuleTax\":50,\"policyId\":\"\",\"policyType\":0}],\"psgType\":\"ADT\",\"specCabinStatusChange\":\"0\",\"specVendorId\":\"\"},{\"airportTax\":0,\"baseFare\":110,\"fuleTax\":0,\"optionFareBreakdowns\":[{\"airportTax\":0,\"baseFare\":110,\"fuleTax\":0,\"policyId\":\"\",\"policyType\":0}],\"psgType\":\"INF\",\"specCabinStatusChange\":\"0\",\"specVendorId\":\"\"},{\"airportTax\":0,\"baseFare\":510,\"fuleTax\":20,\"optionFareBreakdowns\":[{\"airportTax\":0,\"baseFare\":510,\"fuleTax\":20,\"policyId\":\"\",\"policyType\":0}],\"psgType\":\"CHD\",\"specCabinStatusChange\":\"0\",\"specVendorId\":\"\"}],\"groupDescribe\":\"航信\",\"groupId\":\"PRICEGROUP_1524811797721\",\"groupName\":\"航信\",\"iconPath\":\"\",\"isShow\":1,\"lowestRefundList\":[{\"adultLowestRefund\":\"退改?51起\",\"childLowestRefund\":\"\",\"flightNos\":\"EU2749\"}],\"optionExtras\":[{\"additionInfo\":{\"saleControl\":{\"aduDivideBaby\":\"1/1\",\"aduDivideChd\":null,\"aduDivideChdBaby\":\"1/2\",\"ageLimit\":null,\"fixedPsgCnt\":null,\"idTypeText\":{\"INF\":\"婴儿限证件类型：身份证，出生证明，户口薄。\"},\"maxPsgCnt\":\"9\",\"minPsgCnt\":\"1\",\"prohibitSale\":false,\"psgTypeLimit\":[{\"allowIdType\":[\"ID\",\"BRC\",\"RBT\"],\"allowPsgType\":\"INF\",\"maxCnt\":2,\"minCnt\":0}]}},\"drawTicketTime\":20,\"extraInfo\":\"eyJmYXJlQmFzaXMiOiJZIiwiZmRUeXBlIjoiRkQifQ==\",\"flightNos\":null,\"iataCode\":\"08319463\",\"iataName\":\"南京途之旅票务服务有限公司\",\"realVendorId\":\"1\"}],\"priceInfoId\":\"\",\"priceJourneyCabinList\":[{\"priceFlightCabinList\":[{\"actionCode\":\"0\",\"actionName\":null,\"baggageInfo\":\"长沙-宁波：免费托运行李20公斤 ;\",\"baggageInfoBaby\":null,\"baggageInfoCHD\":\"长沙-宁波：免费托运行李20公斤 ;\",\"baggageInfoEntity\":{\"check\":{\"number\":\"\",\"size\":\"\",\"weight\":20},\"portable\":{\"number\":null,\"size\":null,\"weight\":0},\"remark\":\"\"},\"baggageInfoEntityBaby\":null,\"baggageInfoEntityCHD\":{\"check\":{\"number\":\"\",\"size\":\"\",\"weight\":20},\"portable\":{\"number\":null,\"size\":null,\"weight\":0},\"remark\":\"\"},\"cabinClass\":\"Y\",\"cabinCode\":\"Y\",\"cabinKey\":\"0-12#EU2749#2018-11-07#CSX#NGB#Y\",\"cabinStatus\":10,\"cabinType\":1,\"cabinTypeName\":\"经济舱\",\"fullPrice\":1020,\"infCabinStatus\":4,\"insureList\":null,\"journeyNumber\":0,\"mealCode\":\"D\",\"mealCouponList\":null,\"mealName\":\"有餐\",\"seatAliasName\":\"\",\"seatClassDesc\":\"\",\"segmentNumber\":0,\"specCabinCode\":\"Y\",\"specCabinStatus\":10,\"vipRoomList\":null}]}],\"prodClassific\":{\"ruleCode\":\"140\"},\"promotionList\":null,\"saleControl\":{\"aduDivideBaby\":\"1/1\",\"aduDivideChd\":null,\"aduDivideChdBaby\":\"1/2\",\"ageLimit\":null,\"fixedPsgCnt\":null,\"idTypeText\":{\"INF\":\"婴儿限证件类型：身份证，出生证明，户口薄。\"},\"maxPsgCnt\":\"9\",\"minPsgCnt\":\"1\",\"prohibitSale\":false,\"psgTypeLimit\":[{\"allowIdType\":[\"ID\",\"BRC\",\"RBT\"],\"allowPsgType\":\"INF\",\"maxCnt\":2,\"minCnt\":0}]},\"tieCouponList\":null,\"tieInfoList\":null,\"vendorId\":\"1\"}],\"categoryInfo\":{\"category\":0,\"categoryMarks\":null,\"categoryName\":null,\"ruleCode\":null,\"showCategoryName\":false}}],\"tie\":null,\"wztConfig\":{\"blackCardCopywriting\":null,\"transferNeedKnow\":\"1. 每段航班均需缴纳机建和燃油税；\\n2. 建议购买同一航空公司的中转联程机票，便于确认机票，及发生航班延误时保障权益。\\n3. 因天气、航空公司、铁路部门及突发事件等原因，可能影响乘客登机或乘车。请确认换乘距离和中转时间，以便确定合适的航班、车次。\\n4. 因航空公司、铁路方面原因或不可抗力原因，影响乘客行程所产生的损失，属于途牛旅游网免责范畴之内。请及时关注天气及路况信息。\\n5. 如发生航班延误，您有权索要延误证明以便于与航空公司协商处理。\\n6. 该中转航线是否赠送行李直挂和过夜住宿，烦请乘客自行联系航司和机场确认。\\n参考：《中华人民共和国民用航空法》第一百二十六条 旅客、行李或者货物在航空运输中因延误造成的损失，承运人应当承担责任；但是，承运人证明本人或者其受雇人、代理人为了避免损失的发生，已经采取一切必要措施或者不可能采取此种措施的，不承担责任。\"}},\"errorCode\":170000,\"msg\":\"\",\"success\":true}\r\n" +
                    "";
            System.out.println("查询航班返回:"+back);
            JSONObject jo = new JSONObject(back);
            JSONObject data = jo.getJSONObject("data");
            JSONArray fareListArr = data.getJSONArray("fareList");
            JSONObject fareList = fareListArr.getJSONObject(0);
            String flightId = fareList.getString("flightId");
            JSONArray productCategorysArr = data.getJSONArray("productCategorys");
            JSONObject productCategorys = productCategorysArr.getJSONObject(0);
            JSONArray cabinPricesArr = productCategorys.getJSONArray("cabinPrices");
            for(int i=0;i<cabinPricesArr.length();i++) {
                JSONObject cabinPrices = cabinPricesArr.getJSONObject(i);
                JSONArray fareBreakdownListArr = cabinPrices.getJSONArray("fareBreakdownList");
                String groupName = cabinPrices.getString("groupName");
                if(!ytype.equals(groupName)) {
                    continue;
                }
                for(int j=0;j<fareBreakdownListArr.length();j++) {
                    JSONObject fareBreakdownList = fareBreakdownListArr.getJSONObject(j);
                    int baseFare = fareBreakdownList.getInt("baseFare");
                    int fuleTax = fareBreakdownList.getInt("fuleTax");
                    int airportTax = fareBreakdownList.getInt("airportTax");
                    String psgType = fareBreakdownList.getString("psgType");
                    if("ADT".equals(psgType)) {
                        if(price!=baseFare) {
                            continue;
                        }
                        JSONArray priceJourneyCabinListArr = cabinPrices.getJSONArray("priceJourneyCabinList");
                        JSONObject priceJourneyCabinList = priceJourneyCabinListArr.getJSONObject(0);
                        JSONArray priceFlightCabinListArr = priceJourneyCabinList.getJSONArray("priceFlightCabinList");
                        JSONObject priceFlightCabinList = priceFlightCabinListArr.getJSONObject(0);
                        String cabinCode = priceFlightCabinList.getString("cabinCode");
                        int cabinStatus = priceFlightCabinList.getInt("cabinStatus");
                        String cabinClass = priceFlightCabinList.getString("cabinClass");
                        int infCabinStatus = priceFlightCabinList.getInt("infCabinStatus");
                        int fullPrice = priceFlightCabinList.getInt("fullPrice");
                        paramMap.put("cabinCode", cabinCode);
                        paramMap.put("cabinClass", cabinClass);
                        paramMap.put("cabinStatus", cabinStatus+"");
                        paramMap.put("infCabinStatus", infCabinStatus+"");
                        paramMap.put("fullPrice", fullPrice+"");
                        paramMap.put("flightId", flightId);
                        String vendorId = cabinPrices.getString("vendorId");
                        JSONObject prodClassific = cabinPrices.getJSONObject("prodClassific");
                        String ruleCode = prodClassific.getString("ruleCode");
                        paramMap.put("vendorId", vendorId);
                        paramMap.put("ruleCode", ruleCode);
                    }
                    paramMap.put(psgType+"baseFare", baseFare+"");
                    paramMap.put(psgType+"fuleTax", fuleTax+"");
                    paramMap.put(psgType+"airportTax", airportTax+"");

                }


                try {
                    JSONArray promotionListArr = cabinPrices.getJSONArray("promotionList");
                    if(promotionListArr!=null) {
                        JSONObject promotionList = promotionListArr.getJSONObject(0);
                        float amount = promotionList.getFloat("amount");
                        int id = promotionList.getInt("id");
                        String description = promotionList.getString("description");
                        int tieType = description.split("；").length;
                        paramMap.put("reducePrice", amount+"");
                        paramMap.put("pmtId", id+"");
                        paramMap.put("tieType", tieType+"");
                    }
                } catch (Exception e) {
                }

//				System.out.println("价格："+baseFare+",产品类型："+groupName+",舱位："+cabinCode+",座位数："+cabinStatus);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
    private String queryFlight(CloseableHttpClient httpclient, RequestConfig config, String orderJson, String session) {
        try {
            JSONObject json = new JSONObject(orderJson);
            String dep = json.getString("departure");
            String arr = json.getString("arrival");
            String depdate = json.getString("departureDate");
            dep = dep.replace("PEK", "BJS").replace("NAY", "BJS").replace("XIY", "SIA").replace("PVG", "SHA");
            arr = arr.replace("PEK", "BJS").replace("NAY", "BJS").replace("XIY", "SIA").replace("PVG", "SHA");


            String param_d = "{\"sessionId\":\""+session+"\",\"systemId\":0}";
            String param_c = "{\"cc\":619,\"ct\":20,\"dt\":1,\"ov\":20,\"p\":11210,\"v\":\"9.49.0\"}";
            String url = "/common/getTime?d="+URLEncoder.encode(param_d, "utf-8")+"&c="+URLEncoder.encode(param_c, "utf-8");
            HttpGet get = new HttpGet(url);
            HttpHost target = new HttpHost("flight-api.tuniu.com",443,"https");
            get.setConfig(config);
            get.setHeader("Host","flight-api.tuniu.com");
            get.setHeader("User-Agent", "TuNiuApp/8.0.9/Dalvik/1.6.0 (Linux;U;Android 4.4.2;SAMSUNG-SM-N900A Build/KOT49H)");
            CloseableHttpResponse	response = httpclient.execute(target, get);
            String back = EntityUtils.toString(response.getEntity(), "utf-8");
            System.out.println("第一个请求返回:"+back);


            param_d = "{\"adultQuantity\":1,\"babyQuantity\":0,\"channelCount\":0,\"childQuantity\":0,\"pollTag\":0,\"rph\":0,\"segmentList\":[{\"aCityCode\":\""+PropertiesUtils.getProperty("tuniuCityCode.properties", arr)+"\",\"aCityIataCode\":\""+arr+"\",\"dCityCode\":\""+PropertiesUtils.getProperty("tuniuCityCode.properties", dep)+"\",\"dCityIataCode\":\""+dep+"\",\"departDate\":\""+depdate+"\"}],\"bif\":{\"abToken\":\"展示活动\",\"clientMac\":\"008796754730510\",\"deviceToken\":\"008796754730510\",\"intel\":0,\"localCity\":\"当前定位省份未知++当前定位区未知\"},\"deviceId\":\"008796754730510\",\"sessionId\":\""+session+"\",\"systemId\":54}";
            param_c = "{\"cc\":619,\"ct\":20,\"dt\":1,\"ov\":20,\"p\":11210,\"v\":\"9.49.0\"}";
            url = "/wzt/flight/v1/listFlight?d="+URLEncoder.encode(param_d, "utf-8")+"&c="+URLEncoder.encode(param_c, "utf-8");
            get = new HttpGet(url);
            target = new HttpHost("flight-api.tuniu.com",443,"https");
            get.setConfig(config);
            get.setHeader("Host","flight-api.tuniu.com");
            get.setHeader("User-Agent", "TuNiuApp/8.0.9/Dalvik/1.6.0 (Linux;U;Android 4.4.2;SAMSUNG-SM-N900A Build/KOT49H)");
            response = httpclient.execute(target, get);
            back = EntityUtils.toString(response.getEntity(), "utf-8");
            System.out.println("查询返回:"+back);
            JSONObject backObj = new JSONObject(back);
            JSONObject dataObj = backObj.getJSONObject("data");
            String queryId = "";
            try {
                queryId = dataObj.getString("queryId");
            } catch (Exception e) {
            }

            if(StringUtils.isNotEmpty(queryId)) {
                return queryId;
            }else {
                param_d = "{\"departureDate\":\""+depdate+"\",\"dstCityCode\":"+PropertiesUtils.getProperty("tuniuCityCode.properties", arr)+",\"dstCityIataCode\":\""+arr+"\",\"orgCityCode\":"+PropertiesUtils.getProperty("tuniuCityCode.properties", dep)+",\"orgCityIataCode\":\""+dep+"\",\"serverMonth\":12,\"type\":1,\"bif\":{\"abToken\":\"展示活动\",\"clientMac\":\"008796754730510\",\"deviceToken\":\"008796754730510\",\"intel\":0,\"localCity\":\"当前定位省份未知++当前定位区未知\"},\"deviceId\":\"008796754730510\",\"sessionId\":\""+session+"\",\"systemId\":54}";
                param_c = "{\"cc\":619,\"ct\":20,\"dt\":1,\"ov\":20,\"p\":11210,\"v\":\"9.49.0\"}";
                url = "/query/flight/v1/calendar?d="+URLEncoder.encode(param_d, "utf-8")+"&c="+URLEncoder.encode(param_c, "utf-8");
                get = new HttpGet(url);
                target = new HttpHost("flight-api.tuniu.com",443,"https");
                get.setConfig(config);
                get.setHeader("Host","flight-api.tuniu.com");
                get.setHeader("User-Agent", "TuNiuApp/8.0.9/Dalvik/1.6.0 (Linux;U;Android 4.4.2;SAMSUNG-SM-N900A Build/KOT49H)");
                response = httpclient.execute(target, get);
                back = EntityUtils.toString(response.getEntity(), "utf-8");
                System.out.println("日期查询返回:"+back);


                param_d = "{\"adultQuantity\":1,\"babyQuantity\":0,\"channelCount\":0,\"childQuantity\":0,\"pollTag\":0,\"rph\":0,\"segmentList\":[{\"aCityCode\":\""+PropertiesUtils.getProperty("tuniuCityCode.properties", arr)+"\",\"aCityIataCode\":\""+arr+"\",\"dCityCode\":\""+PropertiesUtils.getProperty("tuniuCityCode.properties", dep)+"\",\"dCityIataCode\":\""+dep+"\",\"departDate\":\""+depdate+"\"}],\"bif\":{\"abToken\":\"展示活动\",\"clientMac\":\"008796754730510\",\"deviceToken\":\"008796754730510\",\"intel\":0,\"localCity\":\"当前定位省份未知++当前定位区未知\"},\"deviceId\":\"008796754730510\",\"sessionId\":\""+session+"\",\"systemId\":54}";
                param_c = "{\"cc\":619,\"ct\":20,\"dt\":1,\"ov\":20,\"p\":11210,\"v\":\"9.49.0\"}";
                url = "/wzt/flight/v1/listFlight?d="+URLEncoder.encode(param_d, "utf-8")+"&c="+URLEncoder.encode(param_c, "utf-8");
                get = new HttpGet(url);
                target = new HttpHost("flight-api.tuniu.com",443,"https");
                get.setConfig(config);
                get.setHeader("Host","flight-api.tuniu.com");
                get.setHeader("User-Agent", "TuNiuApp/8.0.9/Dalvik/1.6.0 (Linux;U;Android 4.4.2;SAMSUNG-SM-N900A Build/KOT49H)");
                response = httpclient.execute(target, get);
                back = EntityUtils.toString(response.getEntity(), "utf-8");
                System.out.println("查询返回:"+back);
                backObj = new JSONObject(back);
                dataObj = backObj.getJSONObject("data");
                try {
                    queryId = dataObj.getString("queryId");
                } catch (Exception e) {
                }
            }

            return queryId;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }
    private String login(CloseableHttpClient httpclient, RequestConfig config, BasicCookieStore cookieStore,
                         String cookie, String orderJson, String session) {
//		String password = "t13734596";
//		String username = "13734596341";
        String password = "feeye123";
        String username = "17673049327";
        StringBuilder strb = new StringBuilder();
        InputStream re =null;
        try {
            String capt = "";
            try {
                String param_d = "{\"height\":34,\"sessionId\":\""+session+"\",\"type\":0,\"width\":93}";
                String param_c = "{\"cc\":619,\"ct\":20,\"dt\":1,\"ov\":20,\"p\":11210,\"v\":\"9.49.0\"}";
                String url = "/api/user/auth/captcha?d="+URLEncoder.encode(param_d, "utf-8")+"&c="+URLEncoder.encode(param_c, "utf-8");
                HttpGet get = new HttpGet(url);
                HttpHost target = new HttpHost("m.tuniu.com",443,"https");
                get.setConfig(config);
                get.setHeader("Host","m.tuniu.com");
                get.setHeader("User-Agent", "TuNiuApp/8.0.9/Dalvik/1.6.0 (Linux;U;Android 4.4.2;SAMSUNG-SM-N900A Build/KOT49H)");
                CloseableHttpResponse	response = httpclient.execute(target, get);
                String back = EntityUtils.toString(response.getEntity(), "utf-8");
                System.out.println("获取验证码返回:"+back);
                JSONObject captcha = new JSONObject(back);
                JSONObject data = captcha.getJSONObject("data");
                String imageUrl = data.getString("imageUrl");

                if(StringUtils.isNotEmpty(imageUrl)) {
                    get = new HttpGet(imageUrl);
                    get.setConfig(config);
                    target = new HttpHost("m.tuniu.com", 443, "https");
//			get.setHeader("Referer",
//					aHref);
                    get.setHeader("User-Agent","TuNiuApp/8.0.9/Dalvik/1.6.0 (Linux;U;Android 4.4.2;SAMSUNG-SM-N900A Build/KOT49H)");
                    get.setHeader("Host", "m.tuniu.com");
                    response = httpclient.execute(target, get);
                    OutputStream os = null;
                    String random = "0." + (long) ((Math.random() + 1) * 10000000000000000L);
                    String fileUri = "C://testImg//" + random + ".jpg";
                    re = response.getEntity().getContent();
                    os = new FileOutputStream(fileUri);
                    IOUtils.copy(re, os);
                    os.close();

                    Scanner sc = new Scanner(System.in);
                    capt = sc.nextLine();
                }
            } catch (Exception e) {

            }
            String param_d = "{\"captcha\":\""+capt+"\",\"deviceId\":\"\",\"isDynamic\":0,\"loginId\":\""+username+"\",\"password\":\""+MD5Util.getMD5(password)+"\",\"sessionId\":\""+session+"\"}";
            String param_c = "{\"cc\":619,\"ct\":20,\"dt\":1,\"ov\":20,\"p\":11210,\"v\":\"9.49.0\"}";
            String url = "/api/user/auth/login?d="+URLEncoder.encode(param_d, "utf-8")+"&c="+URLEncoder.encode(param_c, "utf-8");
            HttpGet get = new HttpGet(url);
            HttpHost target = new HttpHost("m.tuniu.com",443,"https");
            get.setConfig(config);
            get.setHeader("Host","m.tuniu.com");
            get.setHeader("User-Agent", "TuNiuApp/8.0.9/Dalvik/1.6.0 (Linux;U;Android 4.4.2;SAMSUNG-SM-N900A Build/KOT49H)");
            CloseableHttpResponse response = httpclient.execute(target, get);
            String back = EntityUtils.toString(response.getEntity(), "utf-8");
            System.out.println("登录返回:"+back);
            List<Cookie> listCookie = cookieStore.getCookies();
            StringBuffer buf = new StringBuffer();
            if (null != listCookie && listCookie.size() > 0) {
                for (int i = 0; i < listCookie.size(); i++) {
                    buf.append(listCookie.get(i).getName() + "=" + listCookie.get(i).getValue() + ";");
                }
            }
            cookie = buf.toString();
            System.out.println("登录返回cookie:"+cookie);
        }catch (Exception e) {
            e.printStackTrace();
        }finally {
            if(re != null) {
                try {
                    re.close();
                } catch (IOException e) {
                    // TODO Auto-generated catch block
                    e.printStackTrace();
                }
            }
        }
        return cookie;
    }
}
*/
