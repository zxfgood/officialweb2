package com.feeye.bean;

import org.apache.commons.lang3.StringUtils;

/**
 * @author xs
 * @description
 * @date 2019/4/28
 */
public class Test {

    public static void main(String[] args) {
//        Calendar c = Calendar.getInstance();
//        c.add(Calendar.DATE,-1);
//        System.out.println(c.getTime());
        String s = "{\"bindRRFT\":false,\"commonRes\":{\"code\":\"PREVENT0001\",\"isOk\":false,\"message\":\"请先登录！\",\"setChannel\":false,\"setCode\":true,\"setIsOk\":true,\"setMessage\":true},\"contactsSize\":0,\"customersSize\":0,\"gmjc\":false,\"passengersSize\":0,\"setBindRRFT\":false,\"setCommonRes\":true,\"setContacts\":false,\"setCustomers\":false,\"setGmjc\":false,\"setGoFlightInfos\":false,\"setPassengers\":false,\"setReFlightInfos\":false,\"setRrftNotice\":false,\"setTaxs\":false,\"taxsSize\":0}";
        if(StringUtils.isNotEmpty(s) && s.contains("航班") || s.contains("登录"))  {
            System.out.println("11111");
        }
    }
}
