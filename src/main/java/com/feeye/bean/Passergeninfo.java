package com.feeye.bean;

/**
 * Created by hasee on 2018/7/28.
 */
public class Passergeninfo {
    //乘客姓名
    private String passengerName;
    //卡号
    private String idcard;
    //乘客类型
    private String passengerType;
    //卡类型
    private String passengercardType;
    //出生日期
    private String birthday;
    //性别
    private String passengerSex;

    public String getPassengerName() {
        return passengerName;
    }

    public void setPassengerName(String passengerName) {
        this.passengerName = passengerName;
    }

    public String getIdcard() {
        return idcard;
    }

    public void setIdcard(String idcard) {
        this.idcard = idcard;
    }

    public String getPassengerType() {
        return passengerType;
    }

    public void setPassengerType(String passengerType) {
        this.passengerType = passengerType;
    }

    public String getPassengercardType() {
        return passengercardType;
    }

    public void setPassengercardType(String passengercardType) {
        this.passengercardType = passengercardType;
    }

    public String getBirthday() {
        return birthday;
    }

    public void setBirthday(String birthday) {
        this.birthday = birthday;
    }

    public String getPassengerSex() {
        return passengerSex;
    }

    public void setPassengerSex(String passengerSex) {
        this.passengerSex = passengerSex;
    }
}
