package com.feeye.test;

import org.apache.commons.codec.binary.Base64;
import org.junit.Test;

import java.io.UnsupportedEncodingException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * @author xs
 * @description
 * @date 2019/6/17
 */
public class DateTest {
    public static void main(String[] args) throws UnsupportedEncodingException {
/*        Map <String, String> map = new LinkedHashMap <String, String>();
        String s ="https://mas.chinapnr.com/gar/RecvMerchant.do?OrdAmt=230.00&BgRetUrl=https://pay.xiamenair.com/xmair-trade-app/chinapnr/pc/callBack&RetUrl=https://www.xiamenair.com/api/ipay/ticket/res&MerPriv=&UsrMp=&GateId=&PayUsrId=874039&DivDetails=&CurCode=&ChkValue=H4sIAAAAAAAAAG1WR5urOBD8QT6QnDjsQUJCJIEJNuAbySTjMA4Cfv1q5oWd3X0HPkEjlahSdzXUNA2HpZZ9PZrNu3CBX2hXHjORpsGRacDHozYDC9b7D1RTjdR7QE1yhnV%2Fb%2FqWqEyEkJG6Pha4PvojxZZURbXiIjB7kTnzUXQ7wCgCzEX96CLML3Om2pJR0oilAWev3b5LpVScS3NLY%2BmcD%2FqtaFddLovvNIE3Rz6zNOnfJTl3mXwQHcU6F%2FPPPeJ%2FMAqir%2F6EURGpyRLzWRpWcyT7J8dkjuxes3jVFGQ1OQrs%2BHOTk1FLY%2FeD88fSJ38XsLr2lldAuQ6mBvd6YA51SIFItPBOQjNXkI%2Bhxrgmev1TJ%2FcAQRqB3vVoBJhTp%2Bjg%2BzbSSpSR8ysNIfncNx9c%2Fp2s9uXDxHkNWew2fHznLYRlElxzxbqVRj%2FqMzj8wKQU9S7xRVz7ezcysYr8iI4ULhMU4SWNuK4dUPgoUenKY1zjf8fYacYOBT0B0h7Dhmq%2BuPyGf%2F0zfoAZYl8cHAQamn%2FqpMHQj%2FCRwuILS2PMjxTrksarWzXoYharL87tyfe9Ue3HHMBYTdF%2B9NHPc9tDz4vw7EZg4rkxupG%2F%2BswXKjW6jzDXHbf%2F1Rn4ewCWJkTs81ywDa4mj2kFmsLTe3k8seD9gklXkvDobeBDkIV5WngBJrsBqbmdKOfXWnF3NHl3mXWjCv2YLv0kQUO%2BODQrb8U%2BCPZlGyt6A4Umx1Jxg%2FiS6mijBk1qxtTZ9VPVZbH2OqXNU307ZRT1XYMeKL%2B2J%2F8gnKZ1qIVkb9BXdlScnZmuyNgml6O7LsGD0CXWBEk3gLQWusnfjivqgddYOK9ijba91Zt6D0%2Fi6JKut2e7KuCmLt7%2Bq%2FL2z7gAzVE7gF1VkD4%2FXbrr4aw8snGR3A9mnrnVLheeWRyXJ%2BljsVSN7plt3ubRxefadPYmmZ7UOfnhdgNeyjp4n5sXwL5PUuPubvIbvou9UQofPdjJhtGvEKgpBIB0da0vqWnCEsEHrOsPWGMd%2BgXXPaipvmQ2SE2bpRD6nDQgxPiqabB2JvWdKi5zBndIZfxKZfXJ71%2BOrHaFwv2g%2Fr7OJtbwe11XDIzPP3Zp%2BFW%2F%2FH75%2Fsw7OuNfI48dJgq3n7lVmsxPKcyA7tmiVrzdNNPn8HIWtukdaaosnq6spIB9zcUMQ4H5GgWAhYDndSDWEGO2m4EK66aug4lspiuEPCeptiWa9iA873TITKi37rU0Avbbr%2F7LTTnMX1eERYfzyaNPTPdgBFvM61CkyK6pDV5NO%2F7GSRXr8SeNUl57jmRFfvilB%2FfMfsnfTTkC3hemv4XgtOVFQTX4AMzwv7h4EKZYp%2BqyTWbNGE6es3YC5EqxqSv1PVVnCsUvHSzfjyn0v%2FHjOP%2BcL%2BL83e8eD%2FyHDoD32Q9AsYOX%2Fo7AObbdm%2BzutY2aiJUJxc3hehhAuivf0822y1idWBEHYCG1ryKV%2FHuMchgti50VMrmCl0QNstscDz3tuIFfdZA6aJe45gL4S0UohfZMhSqol3fgSOOruNRdGVV46HTULRJ3vXDn4LRtqDs6uiMON6Ed2gnUF%2BberMJQiPg6RkulanBbrbTDupqfzw0ZiseQVP25OmvXrMlgU7%2Bn3cHcrgsuzFOuhKS1FQe0TaMW1kbmbl3YOXhgSZbw%2BsoCTTrtHKVNLHYIfKLSO858oY%2BJdxjyw52yHXJVQ9lMwrPbZtfHLOJKhNrqFiprby8vYmN%2BB%2FNWvYRxpTpmwUYtET3JMJYz0UBac18jGNQYUoJZjP7v19wrX7%2F82hJ%2F9DATH2%2B83%2FH8OX96MzomFvfg4y2Vf3ixiXWpJLy7D2exinBC4f6XJ3u%2BeER%2ByMtQonUkupDX1rc%2B1xMYPjzAzAz6gCH%2F%2B38C5osA5j4MqlWshtf9eL871SLRzURO15gcYqjVuzZZkNVdV66Jw%2FJAl8Po9MF%2FH6Z1bo0pOOonrzX2l3NwWQjrpjO3yB6HQgk2ud27wQcKlaSWb8esZPoVHfHiMWr3tiO2ZjrEGVcEPN6hSJWTtdg6kH9fUwuXk2jvOud17uwqMC%2BFrBlFrRNJU67rkHzsDekcPKN1u5VeIrBlUala8y6ud6vt29IW59i9IabKuFV87gXgMXjT6MWqa7wsvNA3884%2FCtpOFD5uq%2BlZuHpj%2BOUCMkGwN6ru6Zu8veVpFn%2FIF3O76rQrKhK9TmV50a%2BTXSguxC6%2Fp8tIy0J9sXuWY2siUcGLYZOE%2BVAI1A902q1Gvf7rr78BzOTfzpwJAAA%3D&MerId=874039&OrdId=234046000004914114&Version=10&Pid=&CmdId=Buy";
        System.out.println(s.substring(45));
        String[] paramAr = s.substring(45).split("&");
        for (int i = 0; i < paramAr.length; i++) {
            String[] split = paramAr[i].split("=");
            if ("MerId".equals(split[0]) || "OrdId".equals(split[0]) || "OrdAmt".equals(split[0])) {
                map.put(split[0], split[1]);
            }
        }
        System.out.println(map.toString());*/
        String account = "OTk1ODE2ODE2QHFxLmNvbQ%3D%3D";
        String password ="ejE2ODE2OA%3D%3D";

        String name = new String(Base64.encodeBase64String("995816816@qq.com".getBytes("utf-8")));
        String pwd = new String(Base64.encodeBase64String("z168168".getBytes("utf-8")));
        System.out.println(name +"\n" +pwd);

    }

    /**
     * 计算时间
     * @param startTime ： 开始时间
     * @param endTime  ： 结束时间
     * @return
     */
    public static int caculateTotalTime(String startTime,String endTime) throws java.text.ParseException {
        SimpleDateFormat formatter =   new SimpleDateFormat( "yyyy-MM-dd");
        Date date1=null;
        Date date = null;
        Long l = 0L;
        date = formatter.parse(startTime);
        long ts = date.getTime();
        date1 =  formatter.parse(endTime);
        long ts1 = date1.getTime();

        l = (ts - ts1) / (1000 * 60 * 60 * 24);

        return l.intValue();
    }
    @Test
    public void a() throws ParseException {
        System.out.println(caculateTotalTime("2020-05-10","2019-09-17"));;
    }
}
