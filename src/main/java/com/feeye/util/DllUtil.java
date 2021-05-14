package com.feeye.util;

import com.sun.jna.Library;
import com.sun.jna.Native;
import com.sun.jna.NativeLong;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;

/**
 * @author xs
 * @description 验证码识别工具类
 * @date 2019/4/3
 */
public class DllUtil {
    public interface WmCode extends Library {
        WmCode instance = (WmCode) Native.loadLibrary("WmCode", WmCode.class);

        /**
         * 设置文本参数为unicode
         *
         * @param OptionIndex 传入是否使用unicode格式 取值范围：0～1   默认为0使用ansi格式，为1使用unicode文本
         * @param OptionValue 传出是否使用unicode格式 取值范围：0～1   默认为0使用ansi格式，为1使用unicode文本
         * @return
         */
        boolean UseUnicodeString(Long OptionIndex, Long OptionValue);

        /**
         * 从文件中载入识别库文件，成功返回True，否则返回False
         *
         * @param FilePath 识别库文件所在全路径
         * @param Password 识别库调用密码
         * @return
         */
        boolean LoadWmFromFile(String FilePath, String Password);

        /**
         * 识别一个图像文件，成功返回True，否则返回False。
         *
         * @param FilePath 图像文件所在全路径
         * @param bytes    返回的验证码字符串，使用该参数前需要将一个足够长的空白字符串赋值给它
         * @return
         */
        boolean GetImageFromFile(String FilePath, byte[] bytes);

        /**
         * 识别一个记录了图像文件的二进制数据的字节数组，或一块同样功能的内存区域，成功返回True，否则返回False。
         *
         * @param FileBuffer 记录了图像文件的二进制数据的字节数组
         * @param ImgBufLen  字节数组的数组长度
         * @param Vcode      返回的验证码字符串，使用该参数前需要将一个足够长的空白字符串赋值给它
         * @return
         */
        boolean GetImageFromBuffer(byte[] FileBuffer, NativeLong ImgBufLen, byte[] Vcode);

        /**
         * ***********************************************************************设定识别库选项参数详解***********************************************************************************
         OptionIndex	         OptionValue
         1.返回方式  取值范围：0～1    默认为0,直接返回验证码,为1返回验证码字符和矩形范围形如：S,10,11,12,13|A,1,2,3,4 表示识别到文本 S 左边横坐标10,左边纵坐标11,右边横坐标,右边纵坐标12
         2.识别方式  取值范围：0～4    默认为0,0整体识别,1连通分割识别,2纵分割识别,3横分割识别,4横纵分割识别。可以进行分割的验证码，建议优先使用分割识别，因为分割后不仅能提高识别率，而且还能提高识别速度
         3.识别模式  取值范围：0～1    默认为0,0识图模式,1为识字模式。识图模式指的是背景白色视为透明不进行对比，识字模式指的是白色不视为透明，也加入对比。绝大多数我们都是使用识图模式，但是有少数部分验证码，使用识字模式更佳。
         4.识别加速  取值范围：0～1    默认为0,0为不加速,1为使用加速。一般我们建议开启加速功能，开启后对识别率几乎不影响。而且能提高3-5倍识别速度。
         5.加速返回  取值范围：0～1    默认为0,0为不加速返回,1为使用加速返回。使用加速返回一般用在粗体字识别的时候，可以大大提高识别速度，但是使用后，会稍微影响识别率。识别率有所下降。一般不是粗体字比较耗时的验证码，一般不用开启
         6.最小相似度 取值范围：0～100  默认为90
         7.字符间隙  取值范围：-10～0  默认为0,如果字符重叠,根据实际情况填写,如-3允许重叠3像素,如果不重叠的话,直接写0，注意：重叠和粘连概念不一样，粘连的话，其实字符间隙为0.
         */

        /**
         * 设定识别库选项
         *
         * @param OptionIndex 选项索引，取值范围1～7
         * @param OptionValue 选项数值
         * @return
         */
        boolean SetWmOption(Long OptionIndex, long OptionValue);
    }

    /**
     * 识别验证码
     *
     * @return
     */
    public static String getVerifyCode() {
        String verifyCode = "";
        if (WmCode.instance.UseUnicodeString(1L, 1L)) {
            if (WmCode.instance.LoadWmFromFile("C:\\JavaExample\\FU.dat", "111111")) {
                byte[] bytes = new byte[20];
                WmCode.instance.SetWmOption(4L, 1L); //开启识别加速
                WmCode.instance.SetWmOption(5L, 1L); //开启加速返回
                WmCode.instance.SetWmOption(7L, 85L); //最小相似度设置为85
                WmCode.instance.SetWmOption(7L, -3); //重叠3像素
                if (WmCode.instance.GetImageFromFile("C:\\JavaExample\\999.jpg", bytes)) {
                    verifyCode = new String(bytes);
                }
            }
        }
        return verifyCode;
    }


    public static void main(String[] args) throws IOException {

        WmCode.instance.UseUnicodeString(1L, 1L);
        WmCode.instance.LoadWmFromFile("C:\\JavaExample\\FU.dat", "111111");
        WmCode.instance.SetWmOption(4L, 1L); //开启识别加速
        WmCode.instance.SetWmOption(5L, 1L); //开启加速返回
        byte[] bytes = new byte[20];
//        //以下使用GetImageFromFile接口
//        WmCode.instance.GetImageFromFile("C:\\JavaExample\\time.png", bytes);
//        System.out.println(new String(bytes));

        //以下使用GetImageFromBuffer接口
        String ImgPath = "C:\\JavaExample\\time.png";
        File file = new File(ImgPath);
        InputStream is = new FileInputStream(file);
        int fileLen = (int) file.length();
        byte[] buffer = new byte[fileLen];
        new NativeLong(file.length());
        byte[] result = new byte[6];
        is.read(buffer, 0, fileLen);
        is.close();
        if (WmCode.instance.GetImageFromBuffer(buffer, new NativeLong(file.length()), result)) {
            System.out.println(new String(result));
        }
    }
}
