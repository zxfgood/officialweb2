/*
package com.feeye.test;

import base.ProUtil;
import bean.PlatformInterfaceCode;
import bean.taobaoInter.IeBaseOorderVo;
import bean.taobaoInter.OrderSearchResponse;
import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.caucho.hessian.client.HessianProxyFactory;
import com.feeye.feeyetcb.common.enums.PlatformInterEnum;
import com.feeye.feeyetcb.domain.platformInter.PlatFlightInfo;
import com.feeye.feeyetcb.domain.platformInter.PlatPassengerInfo;
import com.feeye.feeyetcb.domain.platformInter.PlatformAirInfo;
import com.feeye.feeyetcb.domain.platformInter.PlatformInterfaceInfo;
import com.taobao.api.domain.*;
import com.taobao.api.response.AlitripIeAgentOrderGetResponse;
import com.taobao.api.response.AlitripIeAgentOrderSearchResponse;
import dao.PlatformInterfaceDao;
import freemarker.template.utility.StringUtil;
import org.apache.commons.lang.time.DateFormatUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.time.DateUtils;
import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import util.DateUtil;
import util.ImportOrderUtilService;
import util.Logger4JUtil;
import util.PropertiesUtil;

import java.util.*;

*/
/**
 * @Author: zcf
 * @Date: 2018/11/21 16 40
 * @Description: 淘宝国际导单
 **//*

@Service
public class ExportTaobaoInterOrderService {
    private Logger logger = Logger.getLogger(ExportTaobaoInterOrderService.class);

    @Autowired
    private PlatformInterfaceDao platformInterfaceDao;
    @Autowired
    private ImportOrderUtilService importOrderUtilService;
    private static String url = ProUtil.getPropertiesValue("TTSinter_conf.properties", "taobaoInter_router_address"); //聚石塔项目地址
    private static Map<String, Integer> taobaoThreadStateMap = new HashMap<String, Integer>();

*/
/*    public void startJob() {
        logger.error("淘宝国际导单任务开始");
        String platType = PlatformInterEnum.taobaoInternational.getSource();
        try {
            List<PlatformInterfaceInfo> list = platformInterfaceDao.getAllConfs(platType);
            List<PlatformInterfaceInfo> taobaoList = new ArrayList<PlatformInterfaceInfo>();
            for (int i = 0; i < list.size(); i++) {
                PlatformInterfaceInfo info = list.get(i);
                if (info.isPlatisselfgetorder()
                        && StringUtils.isNotEmpty(info.getPlataccountname())
                        && StringUtils.isNotEmpty(info.getPlataccountpwd())
                        && StringUtils.isNotEmpty(info.getSessionkey())) {
                    if (importOrderUtilService.permissionCheck(info.getPlatcreatename())) {
                        taobaoList.add(info);
                    }
                }
            }
            if (taobaoList.size() == 0) {
                logger.info("没有配置淘宝国际账号");
                return;
            }
            startRunInter(taobaoList);
        } catch (Exception e) {
            logger.error("error" , e);
        }
    }*//*


    */
/**
     * 调用淘宝国际订单列表查询接口
     *
     * @param platformInterfaceInfo
     * @return
     *//*

    private List<Integer> orderSearch(PlatformInterfaceInfo info) {
//        OrderSearchResponse orderSearchResponse = null;
        //当前页  页大小20
        Long currpage = 0L;
        //保存所有交易订单id
        List<Integer> tradeOrderIdList = new ArrayList<>();
        try {
            HessianProxyFactory factory = new HessianProxyFactory();
            //增加超时时间设置
            factory.setReadTimeout(60 * 1000);
            factory.setConnectTimeout(60 * 1000);

            TaobaoService taobaoService = (TaobaoService) factory.create(TaobaoService.class , "http://121.41.178.31:30006/router/taobaoservice");
            do {
                Date endTime = new Date();
                Date beginTime = null;
                String responseStr = "";
                //判断导单配置中是否有设置导单时间,没有就默认导一天前的单
                if (StringUtils.isNotEmpty(info.getImportDate())) {
                    beginTime = DateUtils.parseDate(info.getImportDate() , "yyyy-MM-dd HHmm");
                } else {
                    Calendar c = Calendar.getInstance();
                    c.add(Calendar.DATE , -1);
                    beginTime = c.getTime();
                }
                AlitripIeAgentOrderSearchResponse rsp = taobaoService.intlOrderSearchNew(info.getPlataccountname() , info.getPlataccountpwd() , info.getSessionkey() , 9L , beginTime , endTime , ++currpage , null);
                responseStr = JSON.parseObject(rsp.getBody()).getString("alitrip_ie_agent_order_search_response");
                logger.info("淘宝国际订单列表查询接口响应结果：" + responseStr.replace("\n" , ""));
                orderSearchResponse = JSON.parseObject(responseStr , OrderSearchResponse.class);
                for (IeBaseOorderVo ieBaseOorderVo : orderSearchResponse.getBase_order_vos().getIe_base_order_vo()) {
                    tradeOrderIdList.add(ieBaseOorderVo.getTrade_order_id());
                }
            } while (orderSearchResponse.getBase_order_vos().getIe_base_order_vo().size() == 20);
        } catch (Exception e) {
            logger.error("调用淘宝国际订单列表接口异常" , e);
        }
        return tradeOrderIdList;
    }

    */
/**
     * 调用淘宝国际订单详情接口
     *  @param platformInterfaceInfo
     * @param orderid
     *//*

    private IeOrderVo orderGet(PlatformInterfaceInfo info, Long tradeOrderId) {
        try {
            HessianProxyFactory factory = new HessianProxyFactory();
            //增加超时时间设置
            factory.setReadTimeout(60 * 1000);
            factory.setConnectTimeout(60 * 1000);

            TaobaoService taobaoService = (TaobaoService) factory.create(TaobaoService.class , url);
            AlitripIeAgentOrderGetResponse rsp = taobaoService.intlOrderGetNew(info.getPlataccountname() , info.getPlataccountpwd() , info.getSessionkey() , tradeOrderId);
            logger.info("淘宝国际订单详情接口响应结果：" + JSON.toJSONString(rsp).toString().replace("\n" , ""));
            return rsp.getOrderVo();
        } catch (Exception e) {
            logger.error("淘宝国际订单详情接口调用异常" , e);
        }
        return null;
    }

    */
/**
     * 接口返回数据转换为订单数据
     * @param bean 接口返回数据实体
     * @param userName 主账号
     * @param interfaceInfo 导单接口配置
     * @return
     *//*

    public List<PlatformAirInfo> translateInterOrder(IeOrderVo bean , String userName , PlatformInterfaceInfo interfaceInfo) {

        List<PlatformAirInfo> infos = new ArrayList<PlatformAirInfo>();
        PlatformAirInfo info = new PlatformAirInfo();
        try {
            IeBaseOrderVo baseOrderVo = bean.getBaseOrderVo();

            info.setOrderNo(String.valueOf(baseOrderVo.getTradeOrderId()));
            info.setAllPrice(Float.valueOf(baseOrderVo.getTotalTicketPrice()));
            info.setTotalPay(0f);
            info.setRealPay(0);
            info.setRealPayCH(0);
            info.setOrderStatus("等待出票");
            info.setContact(bean.getContactsVo().getName());
            info.setContactMob(bean.getContactsVo().getPhone());
            info.setContactEmail(bean.getContactsVo().getEmail());
            info.setOrderCreateDate(baseOrderVo.getGmtCreateTime());
            //设置是否可降舱 1 可降舱
            info.setIsDownCabin("1");
            JSONObject jsonRemark = new JSONObject();
            jsonRemark.put("appkey" , interfaceInfo.getPlataccountname());
            jsonRemark.put("secret" , interfaceInfo.getPlataccountpwd());
            jsonRemark.put("sessionKey" , interfaceInfo.getSessionkey());
            info.setAutoOutMessage(jsonRemark.toString());
            info.setUpdateDate(new Date()); //操作时间
            info.setNeedPS("否");
            info.setAddress("");
            info.setXcdPrice(0);
            info.setInsurancePrices(0);
            info.setContactTel("");
            info.setSource("淘宝国际");
            info.setPayTransactionid("");
            info.setPayTransactionidCH("");
            info.setPolicyCode("");
            info.setPolicyId("");
            info.setCommisionPoint(String.valueOf(0));
            info.setCCommisionPoint("0");
            info.setDeadlineDate(""); //最晚出票时间与出票时间
            info.setNewOrderNo("");
            info.setNewOrderNoCH("");
            info.setHaveNotify(false);
            info.setPlatform(interfaceInfo.getPlatnetroot());
            info.setPlatformType(PlatformInterEnum.taobaoInternational.getSource());
            info.setUsername(userName);
            info.setImportDate(new Date());
            info.setIflock("0");
            info.setOperator(userName);
            info.setInteType("1");
            info.setPayTax(0);
            if ("OneWay".equalsIgnoreCase(baseOrderVo.getTripType())) {
                info.setOrderType("1");
            } else if ("RoundTrip".equalsIgnoreCase(baseOrderVo.getTripType())) {
                info.setOrderType("2");
            } else {
                info.setOrderType(baseOrderVo.getTripType());
            }
            info.setRejectCause("");
            info.setConfirmTaobao(true);
            info.setTicketMoney(Float.valueOf(baseOrderVo.getTotalTicketPrice()));
            info.setIfExcepOrder("0");
            info.setTicketTime(new Date());
            info.setIsinternational(true);
            List<PlatFlightInfo> flightInfos = new ArrayList<PlatFlightInfo>();
            info.setFlightInfos(flightInfos);

            List<IeFlightVo> flights = bean.getItemVo().getFlights();
            for (IeFlightVo flight : flights) {
                PlatFlightInfo flightInfo = new PlatFlightInfo();
                flightInfos.add(flightInfo);

                flightInfo.setRealCode(flight.getOperatingFlightNumber());
                flightInfo.setFlightNo(flight.getFlightNumber());
                flightInfo.setDeparture(flight.getDepAirport());
                flightInfo.setArrival(flight.getArrAirport());
                flightInfo.setDepartureDateTime(DateFormatUtils.format(flight.getDepTime() , "yyyy-MM-dd HH:mm:ss"));
                flightInfo.setDepartureDate(DateFormatUtils.format(flight.getDepTime() , "yyyy-MM-dd"));
                flightInfo.setArrivalDateTime(DateFormatUtils.format(flight.getArrTime() , "yyyy-MM-dd HH:mm:ss"));
                String pnrNo = bean.getBookOrderVos().get(0).getBookPnrVos().get(0).getPnrNo();
                String pnrType = bean.getBookOrderVos().get(0).getBookPnrVos().get(0).getPnrType();
                flightInfo.setPnr(pnrNo);
                flightInfo.setPolicyType(pnrType);
                flightInfo.setCcabin(flight.getFlightCabin());
                flightInfo.setCpnr("");
                flightInfo.setFid("");
                flightInfo.setBacknote("");
                flightInfo.setCabinnote("");
                flightInfo.setyPrice(0);
                flightInfo.setNpnr("");
                String directionType = bean.getBookOrderVos().get(0).getBookFlightVos().get(0).getDirectionType();
//                if ("Outbound".equalsIgnoreCase(directionType)) {
//                    flightInfo.setFlightType("1");
//                } else if ("Inbound".equalsIgnoreCase(directionType)) {
//                    flightInfo.setFlightType("2");
//                }
                flightInfo.setFlightType("1");
                flightInfo.setPlatformAirInfo_id("");
                flightInfo.setConstructionFee(String.valueOf(baseOrderVo.getTotalTaxPrice() / baseOrderVo.getPassengerCount()));
                flightInfo.setFuelTax("");
                flightInfo.setCabinNum("");
                flightInfo.setRtCmdIndex("");
                flightInfo.setAvhCmdIndex("");
            }

            List<PlatPassengerInfo> passengers = new ArrayList<PlatPassengerInfo>();
            info.setPassengers(passengers);
            for (IePassgenerVo mBean : bean.getPassgenerVos()) {
                PlatPassengerInfo pInfo = new PlatPassengerInfo();
                passengers.add(pInfo);
                pInfo.setTicketNo("");
                pInfo.setTicketTime("");
                pInfo.setPid("");
                pInfo.setPassengerName(mBean.getName());
                if ("Adult".equalsIgnoreCase(mBean.getPassengerType())) {
                    pInfo.setPassengerType("成人");
                    pInfo.setViewPrice(bean.getItemVo().getAdultPrice().floatValue()); //票面价
                    Long ticketPrice = bean.getBookOrderVos().get(0).getBookTicketVos().get(0).getTicketPrice();
                    pInfo.setPrice(ticketPrice); //销售价
                } else if ("Child".equalsIgnoreCase(mBean.getPassengerType())) {
                    pInfo.setPassengerType("儿童");
                    info.setIfChild(true);
                    pInfo.setViewPrice(bean.getItemVo().getChildPrice()); //票面价
                    pInfo.setPrice(bean.getItemVo().getChildPrice());
                } else if ("Infant".equalsIgnoreCase(mBean.getPassengerType())) {
                    pInfo.setPassengerType("婴儿");
                    info.setIfChild(true);
                } else {
                    pInfo.setPassengerType("留学生");
                }
                pInfo.setBirthday(String.valueOf(mBean.getBirthday()));
                pInfo.setPassengercardType(getCardType(mBean.getCertType()));

                pInfo.setPassengerInfo(mBean.getCertNo());
                pInfo.setInsuranceCount("");
                pInfo.setInsurancePrice(0);
                pInfo.setInsuranceNo("");
                if ("MALE".equalsIgnoreCase(mBean.getGender())) {
                    pInfo.setSex("男");
                } else if ("FEMALE".equalsIgnoreCase(mBean.getGender())) {
                    pInfo.setSex("女");
                } else {
                    pInfo.setSex("未知");
                }

                pInfo.setNewViewPrice(0);
                pInfo.setPayTax(0);

                pInfo.setConstructionFee("");
                pInfo.setFuelTax("");
                pInfo.setOrderStatus("正常");
                pInfo.setReturnTicket("");
                pInfo.setCostRate("");
                pInfo.setCost("");
                pInfo.setRefundCustomer(Float.valueOf(0));
                pInfo.setPlatformAirInfo_id("");
                String nationality = "";
                try {
                    nationality = PropertiesUtil.getPropertiesValue("nationality" , mBean.getNationality().trim());
                } catch (Exception e) {
                }
                pInfo.setNationality(nationality); //国籍
                pInfo.setCardExpired(DateUtil.date2String(mBean.getCertPeriod() , "yyyy-MM-dd")); //证件过期时间
            }

            info.setAutoOutState("");
            info.setPolicyOfficeNo("");
            info.setOperatorName("");
            info.setPeoples(baseOrderVo.getPassengerCount().intValue());
            info.setAutoOutScheduleInfo(null);
            infos.add(info);
        }catch (Exception e) {
            Logger4JUtil.getInfoLogger(interfaceInfo.getPlatcreatename(), interfaceInfo.getPlatnetroot() ,logger).error("error", e);
        }
        return infos;
    }

    */
/**
     * 证件类型转换
     * @param cardType
     * @return
     *//*

    private String getCardType(String cardType) {
        String result = "";
        switch (cardType) {
            case "Passport":
                result = "护照";
                break;
            case "Hongkong_Macao":
                result = "港澳通行证";
                break;
            case "Taiwan_MTP":
                result = "台胞证";
                break;
            case "Home_Return_Permit":
                result = "回乡证";
                break;
            case "Taiwan_Pass":
                result = "台湾通行证";
                break;
            case "Entry_Taiwan_Permit":
                result = "入台证";
                break;
            default:
                result = "未知";
                break;
        }
        return result;
    }

    */
/**
     * @param list 需要导单的用户
     *//*

    public void startRunInter(List<PlatformInterfaceInfo> list) {
        for (int i = 0; i < list.size(); i++) {
            PlatformInterfaceInfo info = list.get(i);
            taobaoInterThread2(info); // 启动线程获取数据
        }
    }

    public void taobaoInterThread2(final PlatformInterfaceInfo info) {

        Integer runCount = taobaoThreadStateMap.get(info.getPlatcreatename() + info.getPlatnetroot() + "runCount");

        Integer runCountComplete = taobaoThreadStateMap.get(info.getPlatcreatename() + info.getPlatnetroot() + "runCountComplete");

        Integer mainThreadEnd = taobaoThreadStateMap.get(info.getPlatcreatename() + info.getPlatnetroot() + "mainThreadEnd");

        Logger4JUtil.getInfoLogger(info.getPlatcreatename() , info.getPlatnetroot() , logger).info(
                info.getPlatcreatename() + info.getPlatnetroot() + "--runCount : " + runCount +
                        " ---- runCountComplete : " + runCountComplete + "----mainThreadEnd:" + mainThreadEnd);
        // 判断上次线程是否执行完成
        if (((runCount == null && runCountComplete == null)
                || runCount <= runCountComplete) && (mainThreadEnd == null || mainThreadEnd == 1)) {
            new Thread(new Runnable() {
                public void run() {
                    taobaoThreadStateMap.put(info.getPlatcreatename() + info.getPlatnetroot() + "mainThreadEnd", 0);
                    //历史记录
                    PlatformInterfaceCode code = new PlatformInterfaceCode();
                    code.setAppmark(info.getPlatnetroot());
                    code.setUsername(info.getPlatcreatename());
                    code.setStartTime(new Date());
                    try {
                        try {
                            List<PlatformAirInfo> orders = null;
                            //查询订单列表
                            List<Integer> tradeOrderIdList = orderSearch(info);
                            for (Integer tradeOrderId : tradeOrderIdList) {
                                //查询订单详情
                                IeOrderVo orderVo = orderGet(info , tradeOrderId.longValue());
                                //订单详情数据转换为订单数据
                                orders = translateInterOrder(orderVo , info.getPlatcreatename() , info);
                            }
                            List<String> dbOrders = platformInterfaceDao.findOrderByDateAndPlatfrom(info.getPlatcreatename(), info.getPlatnetroot(), null);
                            logger.error(info.getPlatcreatename() + "taobao数据库中查询到了" + dbOrders.size() + "个订单");
                            Set<String> DBorderSet = new HashSet<String>();
                            for (String dborder : dbOrders) {
                                DBorderSet.add(dborder);
                            }
                            // logger.info("数据库中存在的订单号"+JSONArray.fromObject(DBorderSet).toString());
                            Iterator<PlatformAirInfo> itr = orders.iterator();
                            while (itr.hasNext()) {
                                PlatformAirInfo airInfo = itr.next();
                                if (DBorderSet.contains(airInfo.getOrderNo())) {
                                    itr.remove();
                                }
                            }

                            boolean isSave = platformInterfaceDao.batchInsertAirInfo(orders, orders.size(), info.getPlatcreatename(), false);
                            if (!isSave) {
                                logger.error(info.getPlatcreatename() + "淘宝国际导单保存入库失败");
                            } else {
                                logger.error(info.getPlatcreatename() + "淘宝国际导单成功 导入数据：" + orders.size() + "条");
                            }
                        } catch (Exception e) {
                            logger.error("订单转换出错", e);
                        }
                    } catch (Exception e) {
                        Logger4JUtil.getInfoLogger(info.getPlatcreatename(), info.getPlatnetroot(), logger).error
                                ("error", e);
                    } finally {
                        taobaoThreadStateMap.put(info.getPlatcreatename() + info.getPlatnetroot() + "mainThreadEnd", 1);  //导单完成
                    }
                }
            }).start();
        }
    }

    public static void main(String[] args) {
        ExportTaobaoInterOrderService s = new ExportTaobaoInterOrderService();
        PlatformInterfaceInfo info = new PlatformInterfaceInfo();
        info.setPlataccountname("23600463");
        info.setPlataccountpwd("dff280bb8cf1b809f22ad31b9ccf524f");
        info.setSessionkey("6202503020fhj49e04187edd037c850f83d532acb7d774d2204126675966");
        s.orderSearch(info);
    }
}
*/
