package com.feeye.bean;

import java.util.List;

/**
 * @Author: zcf
 * @Date: 2018/7/30 16 02
 * @Description:
 **/
public class Orderinfo {

    //账单号
    private String billNo;

    public String getBillNo() {
        return billNo;
    }

    public void setBillNo(String billNo) {
        this.billNo = billNo;
    }

    //新订单编号
    private String newOrderNo;

    public String getNewOrderNo() {
        return newOrderNo;
    }

    public void setNewOrderNo(String newOrderNo) {
        this.newOrderNo = newOrderNo;
    }

    //过期年月
    private String expireMonth;
    private String expireYear;

    public String getExpireMonth() {
        return expireMonth;
    }

    public void setExpireMonth(String expireMonth) {
        this.expireMonth = expireMonth;
    }

    public String getExpireYear() {
        return expireYear;
    }

    public void setExpireYear(String expireYear) {
        this.expireYear = expireYear;
    }

    //证件号码
    private String idCardNo;

    public String getIdCardNo() {
        return idCardNo;
    }

    public void setIdCardNo(String idCardNo) {
        this.idCardNo = idCardNo;
    }

    //持卡人姓名
    private String ownername;

    public String getOwnername() {
        return ownername;
    }

    public void setOwnername(String ownername) {
        this.ownername = ownername;
    }

    //易宝支付手机号
    private String  cvv;

    //联系人姓名
    private String linkMan;
    //联系人手机号
    private String mobile;
    //乘客信息
    private List<Passergeninfo> passengers;

    //官网账号 以_分隔
    private String account;
    //订单系统用户主账号名
    private String username;
    //Feeye订单系统订单号
    private String id;
    //验证码账号 以_分隔
    private String otheraccount;

    //易宝账号
    private String creditNo;
    //密码
    private String payerMobile;

    private List<FlightinfoVo> flights;

    //支付方式
    private String payType;

    public String getPayType() {
        return payType;
    }

    public void setPayType(String payType) {
        this.payType = payType;
    }

    public List<FlightinfoVo> getFlights() {
        return flights;
    }

    public void setFlights(List<FlightinfoVo> flights) {
        this.flights = flights;
    }

    public String getCvv() {
        return cvv;
    }

    public void setCvv(String cvv) {
        this.cvv = cvv;
    }
    public String getLinkMan() {
        return linkMan;
    }

    public void setLinkMan(String linkMan) {
        this.linkMan = linkMan;
    }

    public String getMobile() {
        return mobile;
    }

    public void setMobile(String mobile) {
        this.mobile = mobile;
    }

    public List<Passergeninfo> getPassengers() {
        return passengers;
    }

    public void setPassengers(List<Passergeninfo> passengers) {
        this.passengers = passengers;
    }
    public String getCreditNo() {
        return creditNo;
    }

    public void setCreditNo(String creditNo) {
        this.creditNo = creditNo;
    }

    public String getPayerMobile() {
        return payerMobile;
    }

    public void setPayerMobile(String payerMobile) {
        this.payerMobile = payerMobile;
    }

    public String getAccount() {
        return account;
    }

    public void setAccount(String account) {
        this.account = account;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getOtheraccount() {
        return otheraccount;
    }

    public void setOtheraccount(String otheraccount) {
        this.otheraccount = otheraccount;
    }
}
