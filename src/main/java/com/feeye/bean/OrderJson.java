package com.feeye.bean;

import java.util.List;

/**
 * @author xs
 * @date 2019/3/11 17:31
 */
public class OrderJson {

    /**
     * account : 1536692581_hnfzy666
     * airline : MF
     * bankType : CMB
     * childrenMobile :
     * code :
     * codePassword :
     * creditNo : 5187188380513124
     * cvv : 369
     * drawerType : GW
     * expireMonth : 05
     * expireYear : 2028
     * flights : [{"airline":"MF","arrival":"XMN","cabin":"","departure":"WUH","departureDate":"2019-03-14","fType":"go","flightNo":"MF8338","price":"430.0"}]
     * id : 32550175
     * idCardNo : 410781197310154713
     * idCardType : IDCARD
     * ifUsedCoupon : false
     * isOutticket : false
     * linkMan : 华勇
     * matchCabin : false
     * mobile : 13072660551
     * orderNo : 406786710
     * orderTime : 2019-03-11 00:12:13
     * otheraccount : fyyzm1_wo4feizhiyou
     * ownername : 王涛
     * passengers : [{"birthday":"1984-07-19 00:00:00+08:00","id":"37617002","idcard":"411425198407198112","passengerCardType":"身份证","passengerName":"邵玉风","passengerSex":"男","passengerType":"成人"},{"birthday":"1975-11-29 00:00:00+08:00","id":"37617005","idcard":"532225197511291117","passengerCardType":"身份证","passengerName":"尹建飞","passengerSex":"男","passengerType":"成人"},{"birthday":"1983-11-21 00:00:00+08:00","id":"37617004","idcard":"350781198311212039","passengerCardType":"身份证","passengerName":"梁邵平","passengerSex":"男","passengerType":"成人"},{"birthday":"1983-11-30 00:00:00+08:00","id":"37617003","idcard":"350802198311306035","passengerCardType":"身份证","passengerName":"林炜","passengerSex":"男","passengerType":"成人"}]
     * payType : xyk
     * payerMobile : 18530203144
     * qiangpiao : 2
     * username : fzybian
     * ytype :
     */

    private String account;
    private String airline;
    private String bankType;
    private String childrenMobile;
    private String code;
    private String codePassword;
    private String creditNo;
    private String cvv;
    private String drawerType;
    private String expireMonth;
    private String expireYear;
    private String id;
    private String idCardNo;
    private String idCardType;
    private boolean ifUsedCoupon;
    private String isOutticket;
    private String linkMan;
    private boolean matchCabin;
    private String mobile;
    private String orderNo;
    private String orderTime;
    private String otheraccount;
    private String ownername;
    private String payType;
    private String payerMobile;
    private String qiangpiao;
    private String username;
    private String ytype;
    private List <FlightsBean> flights;
    private List <PassengersBean> passengers;
    private String billNo; //订单流水号
    private String newOrderNo; //订单编号

    public String getAccount() {
        return account;
    }

    public void setAccount(String account) {
        this.account = account;
    }

    public String getAirline() {
        return airline;
    }

    public void setAirline(String airline) {
        this.airline = airline;
    }

    public String getBankType() {
        return bankType;
    }

    public void setBankType(String bankType) {
        this.bankType = bankType;
    }

    public String getChildrenMobile() {
        return childrenMobile;
    }

    public void setChildrenMobile(String childrenMobile) {
        this.childrenMobile = childrenMobile;
    }

    public String getCode() {
        return code;
    }

    public void setCode(String code) {
        this.code = code;
    }

    public String getCodePassword() {
        return codePassword;
    }

    public void setCodePassword(String codePassword) {
        this.codePassword = codePassword;
    }

    public String getCreditNo() {
        return creditNo;
    }

    public void setCreditNo(String creditNo) {
        this.creditNo = creditNo;
    }

    public String getCvv() {
        return cvv;
    }

    public void setCvv(String cvv) {
        this.cvv = cvv;
    }

    public String getDrawerType() {
        return drawerType;
    }

    public void setDrawerType(String drawerType) {
        this.drawerType = drawerType;
    }

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

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getIdCardNo() {
        return idCardNo;
    }

    public void setIdCardNo(String idCardNo) {
        this.idCardNo = idCardNo;
    }

    public String getIdCardType() {
        return idCardType;
    }

    public void setIdCardType(String idCardType) {
        this.idCardType = idCardType;
    }

    public boolean isIfUsedCoupon() {
        return ifUsedCoupon;
    }

    public void setIfUsedCoupon(boolean ifUsedCoupon) {
        this.ifUsedCoupon = ifUsedCoupon;
    }

    public String getIsOutticket() {
        return isOutticket;
    }

    public void setIsOutticket(String isOutticket) {
        this.isOutticket = isOutticket;
    }

    public String getLinkMan() {
        return linkMan;
    }

    public void setLinkMan(String linkMan) {
        this.linkMan = linkMan;
    }

    public boolean isMatchCabin() {
        return matchCabin;
    }

    public void setMatchCabin(boolean matchCabin) {
        this.matchCabin = matchCabin;
    }

    public String getMobile() {
        return mobile;
    }

    public void setMobile(String mobile) {
        this.mobile = mobile;
    }

    public String getOrderNo() {
        return orderNo;
    }

    public void setOrderNo(String orderNo) {
        this.orderNo = orderNo;
    }

    public String getOrderTime() {
        return orderTime;
    }

    public void setOrderTime(String orderTime) {
        this.orderTime = orderTime;
    }

    public String getOtheraccount() {
        return otheraccount;
    }

    public void setOtheraccount(String otheraccount) {
        this.otheraccount = otheraccount;
    }

    public String getOwnername() {
        return ownername;
    }

    public void setOwnername(String ownername) {
        this.ownername = ownername;
    }

    public String getPayType() {
        return payType;
    }

    public void setPayType(String payType) {
        this.payType = payType;
    }

    public String getPayerMobile() {
        return payerMobile;
    }

    public void setPayerMobile(String payerMobile) {
        this.payerMobile = payerMobile;
    }

    public String getQiangpiao() {
        return qiangpiao;
    }

    public void setQiangpiao(String qiangpiao) {
        this.qiangpiao = qiangpiao;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getYtype() {
        return ytype;
    }

    public void setYtype(String ytype) {
        this.ytype = ytype;
    }

    public String getBillNo() {
        return billNo;
    }

    public void setBillNo(String billNo) {
        this.billNo = billNo;
    }

    public String getNewOrderNo() {
        return newOrderNo;
    }

    public void setNewOrderNo(String newOrderNo) {
        this.newOrderNo = newOrderNo;
    }

    public List <FlightsBean> getFlights() {
        return flights;
    }

    public void setFlights(List <FlightsBean> flights) {
        this.flights = flights;
    }

    public List <PassengersBean> getPassengers() {
        return passengers;
    }

    public void setPassengers(List <PassengersBean> passengers) {
        this.passengers = passengers;
    }

    public static class FlightsBean {
        /**
         * airline : MF
         * arrival : XMN
         * cabin :
         * departure : WUH
         * departureDate : 2019-03-14
         * fType : go
         * flightNo : MF8338
         * price : 430.0
         */

        private String airline;
        private String arrival;
        private String cabin;
        private String departure;
        private String departureDate;
        private String fType;
        private String flightNo;
        private String price;

        public String getAirline() {
            return airline;
        }

        public void setAirline(String airline) {
            this.airline = airline;
        }

        public String getArrival() {
            return arrival;
        }

        public void setArrival(String arrival) {
            this.arrival = arrival;
        }

        public String getCabin() {
            return cabin;
        }

        public void setCabin(String cabin) {
            this.cabin = cabin == null ? "" : cabin.trim();
        }

        public String getDeparture() {
            return departure;
        }

        public void setDeparture(String departure) {
            this.departure = departure;
        }

        public String getDepartureDate() {
            return departureDate;
        }

        public void setDepartureDate(String departureDate) {
            this.departureDate = departureDate;
        }

        public String getFType() {
            return fType;
        }

        public void setFType(String fType) {
            this.fType = fType;
        }

        public String getFlightNo() {
            return flightNo;
        }

        public void setFlightNo(String flightNo) {
            this.flightNo = flightNo;
        }

        public String getPrice() {
            return price;
        }

        public void setPrice(String price) {
            this.price = price;
        }
    }

    public static class PassengersBean {
        /**
         * birthday : 1984-07-19 00:00:00+08:00
         * id : 37617002
         * idcard : 411425198407198112
         * passengerCardType : 身份证
         * passengerName : 邵玉风
         * passengerSex : 男
         * passengerType : 成人
         */

        private String birthday;
        private String id;
        private String idcard;
        private String passengerCardType;
        private String passengerName;
        private String passengerSex;
        private String passengerType;

        public String getBirthday() {
            return birthday;
        }

        public void setBirthday(String birthday) {
            this.birthday = birthday;
        }

        public String getId() {
            return id;
        }

        public void setId(String id) {
            this.id = id;
        }

        public String getIdcard() {
            return idcard;
        }

        public void setIdcard(String idcard) {
            this.idcard = idcard;
        }

        public String getPassengerCardType() {
            return passengerCardType;
        }

        public void setPassengerCardType(String passengerCardType) {
            this.passengerCardType = passengerCardType;
        }

        public String getPassengerName() {
            return passengerName;
        }

        public void setPassengerName(String passengerName) {
            this.passengerName = passengerName;
        }

        public String getPassengerSex() {
            return passengerSex;
        }

        public void setPassengerSex(String passengerSex) {
            this.passengerSex = passengerSex;
        }

        public String getPassengerType() {
            return passengerType;
        }

        public void setPassengerType(String passengerType) {
            this.passengerType = passengerType;
        }
    }
}
