package com.feeye.util;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;

/**
 * @author xs
 * @description 图片滑动验证码处理
 * @date 2019/10/25
 */
public class slideImageUtil {
    private static final Logger logger = LoggerFactory.getLogger(slideImageUtil.class);

    BufferedImage screenShotImage;    //屏幕截图
    BufferedImage keyImage;           //查找目标图片

    int scrShotImgWidth;              //屏幕截图宽度
    int scrShotImgHeight;             //屏幕截图高度

    int keyImgWidth;                  //查找目标图片宽度
    int keyImgHeight;                 //查找目标图片高度

    int[][] screenShotImageRGBData;   //屏幕截图RGB数据
    int[][] keyImageRGBData;          //查找目标图片RGB数据

    int[][][] findImgData;            //查找结果，目标图标位于屏幕截图上的坐标数据


    public slideImageUtil(String keyImagePath, String bgImagePath) {
//        screenShotImage = this.getFullScreenShot();

        screenShotImage = this.getBfImageFromPath(bgImagePath);
        screenShotImageRGBData = this.getImageGRB(screenShotImage);
        scrShotImgWidth = screenShotImage.getWidth();
        scrShotImgHeight = screenShotImage.getHeight();

        keyImage = this.getBfImageFromPath(keyImagePath);
        keyImageRGBData = this.getImageGRB(keyImage);
        keyImgWidth = keyImage.getWidth();
        keyImgHeight = keyImage.getHeight();

        //开始查找
        this.findImage();

    }

    /**
     * 全屏截图
     *
     * @return 返回BufferedImage
     */
    public BufferedImage getFullScreenShot() {
        BufferedImage bfImage = null;
        int width = (int) Toolkit.getDefaultToolkit().getScreenSize().getWidth();
        int height = (int) Toolkit.getDefaultToolkit().getScreenSize().getHeight();
        try {
            Robot robot = new Robot();
            bfImage = robot.createScreenCapture(new Rectangle(0, 0, width, height));
        } catch (AWTException e) {
            logger.error("error", e);
        }
        return bfImage;
    }

    /**
     * 从本地文件读取目标图片
     *
     * @param keyImagePath - 图片绝对路径
     * @return 本地图片的BufferedImage对象
     */
    public BufferedImage getBfImageFromPath(String keyImagePath) {
        BufferedImage bfImage = null;
        try {
            bfImage = ImageIO.read(new File(keyImagePath));
        } catch (IOException e) {
            logger.error("error", e);
        }
        return bfImage;
    }

    /**
     * 根据BufferedImage获取图片RGB数组
     *
     * @param bfImage
     * @return
     */
    public int[][] getImageGRB(BufferedImage bfImage) {
        int width = bfImage.getWidth();
        int height = bfImage.getHeight();
        int[][] result = new int[height][width];
        for (int h = 0; h < height; h++) {
            for (int w = 0; w < width; w++) {
                //使用getRGB(w, h)获取该点的颜色值是ARGB，而在实际应用中使用的是RGB，所以需要将ARGB转化成RGB，即bufImg.getRGB(w, h) & 0xFFFFFF。
                result[h][w] = bfImage.getRGB(w, h) & 0xFFFFFF;
            }
        }
        return result;
    }


    /**
     * 查找图片
     */
    public void findImage() {
        findImgData = new int[keyImgHeight][keyImgWidth][2];
        //遍历屏幕截图像素点数据
        for (int y = 0; y < scrShotImgHeight - keyImgHeight/2; y++) {
            for (int x = 0; x < scrShotImgWidth - keyImgWidth; x++) {
                //根据目标图的尺寸，得到目标图四个角映射到屏幕截图上的四个点，
                //判断截图上对应的四个点与图B的四个角像素点的值是否相同，
                //如果相同就将屏幕截图上映射范围内的所有的点与目标图的所有的点进行比较。
                if ((keyImageRGBData[0][0] ^ screenShotImageRGBData[y][x]) == 0
                        && (keyImageRGBData[0][keyImgWidth - 1] ^ screenShotImageRGBData[y][x + keyImgWidth - 1]) == 0
                        && (keyImageRGBData[keyImgHeight - 1][keyImgWidth - 1] ^ screenShotImageRGBData[y + keyImgHeight - 1][x + keyImgWidth - 1]) == 0
                        && (keyImageRGBData[keyImgHeight - 1][0] ^ screenShotImageRGBData[y + keyImgHeight - 1][x]) == 0) {

                    boolean isFinded = isMatchAll(y, x);
                    //如果比较结果完全相同，则说明图片找到，填充查找到的位置坐标数据到查找结果数组。
                    if (isFinded) {
                        for (int h = 0; h < keyImgHeight; h++) {
                            for (int w = 0; w < keyImgWidth; w++) {
                                findImgData[h][w][0] = y + h;
                                findImgData[h][w][1] = x + w;
                            }
                        }
                        return;
                    }
                }
            }
        }
    }

    /**
     * 判断屏幕截图上目标图映射范围内的全部点是否全部和小图的点一一对应。
     *
     * @param y - 与目标图左上角像素点想匹配的屏幕截图y坐标
     * @param x - 与目标图左上角像素点想匹配的屏幕截图x坐标
     * @return
     */
    public boolean isMatchAll(int y, int x) {
        int biggerY = 0;
        int biggerX = 0;
        int xor = 0;
        for (int smallerY = 0; smallerY < keyImgHeight; smallerY++) {
            biggerY = y + smallerY;
            for (int smallerX = 0; smallerX < keyImgWidth; smallerX++) {
                biggerX = x + smallerX;
                if (biggerY >= scrShotImgHeight || biggerX >= scrShotImgWidth) {
                    return false;
                }
                xor = keyImageRGBData[smallerY][smallerX] ^ screenShotImageRGBData[biggerY][biggerX];
                if (xor != 0) {
                    return false;
                }
            }
            biggerX = x;
        }
        return true;
    }

    /**
     * 输出查找到的坐标数据
     */
    private void printFindData() {
        for (int y = 0; y < keyImgHeight; y++) {
            for (int x = 0; x < keyImgWidth; x++) {
                System.out.print("(" + this.findImgData[y][x][0] + ", " + this.findImgData[y][x][1] + ")");
            }
            System.out.println();
        }
    }


    public static void main(String[] args) {
        String keyImagePath = "C:\\testImg\\1572428055959.block.png";
        String bgImagePath = "C:\\testImg\\1572428055969.bg.png";
//        slideImageUtil demo = new slideImageUtil(keyImagePath, bgImagePath);
//        demo.printFindData();

        slideImageUtil.findXDiffRectangeImage(bgImagePath);
    }





    /**
     * 通过背景的完整图片与有缺失的图片进行对比，得到偏移量
     * @param imgSrc  图片路径
     * @return left 偏移量
     */
    public static int findXDiffRectangeImage(String imgSrc) {
        try {
            BufferedImage image = ImageIO.read(new File(imgSrc));
            int width = image.getWidth();
            int height = image.getHeight();

            int left = 0;
            /**
             * 从左至右扫描
             */
            boolean flag = false;
            for (int i = 0; i < width; i++) {
                for (int j = 0; j < height; j++)
                    if (isPixelNotEqual(image, i, j)) {
                        left = i;
                        flag = true;
                        break;
                    }
                if (flag) {
                    break;
                }
            }
            System.out.println("计算出来的偏移值: " + left);
            logger.info("计算出来的偏移值: " + left);
            return left;
        } catch (Exception e) {
            logger.error("error", e);
            return -1;
        }
    }


    private static boolean isPixelNotEqual(BufferedImage image, int i, int j) {
        int pixel = image.getRGB(i, j);
        int[] rgb = new int[3];
        rgb[0] = (pixel & 0xff0000) >> 16;
        rgb[1] = (pixel & 0xff00) >> 8;
        rgb[2] = (pixel & 0xff);

        //移动图的左顶点rgb
        int[] rgbLfet = new int[3];
        rgbLfet[0] = (245);
        rgbLfet[1] = (245);
        rgbLfet[2] = (245);

        //移动图的左内侧顶点rgb
        int[] rgb1 = new int[3];
        rgb1[0] = (210);
        rgb1[1] = (210);
        rgb1[2] = (230);

        //RGB差值
        int r = Math.abs(rgb[0] - rgbLfet[0]);
        int g = Math.abs(rgb[1] - rgbLfet[1]);
        int b = Math.abs(rgb[2] - rgbLfet[2]);

        //RGB差值
        int r1 = Math.abs(rgb[0] - rgb1[0]);
        int g1 = Math.abs(rgb[1] - rgb1[1]);
        int b1 = Math.abs(rgb[2] - rgb1[2]);

        //默认误差值
        int defaultNum = 25;
        if (r < defaultNum && g < defaultNum && b < defaultNum || r1 < defaultNum && g1 < defaultNum && b1 < defaultNum) {
            //过滤偏移值
            if (i <= 165) {
                return false;
            }
//            System.out.println("坐标:(" + i + "," + j + ")" + " RGB(" + rgb[0] + "," + rgb[1] + "," + rgb[2] + ")");
            return true;
        } else {
            return false;
        }
    }
}
