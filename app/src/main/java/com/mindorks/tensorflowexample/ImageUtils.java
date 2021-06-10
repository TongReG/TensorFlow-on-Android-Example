package com.mindorks.tensorflowexample;

import android.app.Activity;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.ImageFormat;
import android.media.Image;
import android.os.Build;
import android.support.annotation.RequiresApi;
import android.util.Log;
import android.widget.Toast;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.nio.ByteBuffer;

public class ImageUtils {
    static final int YUV420P = 0;
    static final int YUV420SP = 0x23;
    static final int NV16 = 0x10;
    static final int NV21 = 0x11;
    static final int JPEG = 0x100;
    private static final String TAG = "ImageUtils";

    private static int[] RGB_int;
    private static Bitmap FinalMap;

    /***
     * 此方法内注释以640*480为例
     * 未考虑CropRect的
     */
    @RequiresApi(api = Build.VERSION_CODES.KITKAT)
    public static byte[] getImageByte(Image mImage, int type) {
        try {
            // 获取Image对象的平面数据，如果是YUV420_888/444_888 planes.length = 3
            // plane[i]里面的数据可能存在情况 byte[].length <= Buffer Capacity (缓冲区总大小)
            Image.Plane[] planes = mImage.getPlanes();

            Log.i(TAG, "ImagePlanes.length = " + planes.length);

            // 数据有效宽度，一般图片width <= rowStride，这也是导致byte[].length <= Capacity的原因
            // 所以我们只取width部分
            int width = mImage.getWidth();
            int height = mImage.getHeight();

            // 此处用来存储最终的YUV420数据，需要3/2倍图片大小，因为Y:U:V比例为4:1:1，每4个Y共用一对UV值
            // 同理YUV422中Y:U:V比例为4:2:2，YUV444中Y:U:V比例为1:1:1
            byte[] yuvBytes = new byte[width * height * ImageFormat.getBitsPerPixel(ImageFormat.YUV_420_888) / 8];
            // 目标数组进行处理到的位置值
            int dstIndex = 0;

            // 临时存储U/V数据
            byte[] uBytes = new byte[width * height / 4];
            byte[] vBytes = new byte[width * height / 4];
            int uIndex = 0;
            int vIndex = 0;

            int pixelsStride, rowStride;
            for (int i = 0; i < planes.length; i++) {
                pixelsStride = planes[i].getPixelStride();
                rowStride = planes[i].getRowStride();

                ByteBuffer buffer = planes[i].getBuffer();

                Log.i(TAG, "ImagePlanes.BufferSize = " + buffer.capacity());

                //如果pixelsStride为2，一般的Y的buffer长度 w*h，UV的长度=(w*h/2)-1
                //源数据的索引，y的数据是byte中连续的，u的数据是v向左移以为生成的，两者都是偶数位为有效数据
                byte[] bytes = new byte[buffer.capacity()];
                buffer.get(bytes);

                int srcIndex = 0;
                if (i == 0) {
                    //直接取出来所有Y的有效区域，也可以存储成一个临时的bytes，到下一步再copy
                    for (int j = 0; j < height; j++) {
                        System.arraycopy(bytes, srcIndex, yuvBytes, dstIndex, width);
                        srcIndex += rowStride;
                        dstIndex += width;
                    }
                } else if (i == 1) {
                    //根据pixelsStride取相应的数据
                    for (int j = 0; j < (height >> 1); j++) {
                        for (int k = 0; k < (width >> 1); k++) {
                            uBytes[uIndex++] = bytes[srcIndex];
                            srcIndex += pixelsStride;
                        }
                        if (pixelsStride == 2) {
                            srcIndex += rowStride - width;
                        } else if (pixelsStride == 1) {
                            srcIndex += rowStride - (width >> 1);
                        }
                    }
                } else if (i == 2) {
                    //根据pixelsStride取相应的数据
                    for (int j = 0; j < (height >> 1); j++) {
                        for (int k = 0; k < (width >> 1); k++) {
                            vBytes[vIndex++] = bytes[srcIndex];
                            srcIndex += pixelsStride;
                        }
                        if (pixelsStride == 2) {
                            srcIndex += rowStride - width;
                        } else if (pixelsStride == 1) {
                            srcIndex += rowStride - (width >> 1);
                        }
                    }
                }
            }

            //根据要求的结果类型填充到结果
            //420SP NV12先U后V(YCbCr)，NV21先V后U(YCrCb)
            switch (type) {
                case YUV420P:
                    //YYYYYYYYYY.....Y + UUU...U + VVV...V
                    System.arraycopy(uBytes, 0, yuvBytes, dstIndex, uBytes.length);
                    System.arraycopy(vBytes, 0, yuvBytes, dstIndex + uBytes.length, vBytes.length);
                    break;
                case YUV420SP:
                    //YYYYYYYYYY.....Y + UVUVUVUV....UV
                    for (int i = 0; i < vBytes.length; i++) {
                        yuvBytes[dstIndex++] = uBytes[i];
                        yuvBytes[dstIndex++] = vBytes[i];
                    }
                    break;
                case NV21:
                    //YYYYYYYYYY.....Y + VUVUVUVU....VU
                    for (int i = 0; i < vBytes.length; i++) {
                        yuvBytes[dstIndex++] = vBytes[i];
                        yuvBytes[dstIndex++] = uBytes[i];
                    }
                    break;
            }
            return yuvBytes;
        } catch (final Exception e) {
            Log.i(TAG, e.toString());
        }
        return null;
    }

    /***
     * YUV420 转化成 RGB
     */
    public static int[] decodeYUV420SP(byte[] yuv420sp, int width, int height) {
        final int frameSize = width * height;
        int[] rgb = new int[frameSize];
        for (int j = 0, index = 0; j < height; j++) {
            int uvindex = frameSize + (j >> 1) * width, u = 0, v = 0;
            for (int i = 0; i < width; i++, index++) {
                int y = (0xff & ((int) yuv420sp[index])) - 0x10;
                if (y < 0)
                    y = 0;
                if ((i & 1) == 0) {
                    v = (0xff & yuv420sp[uvindex++]) - 0x80;
                    u = (0xff & yuv420sp[uvindex++]) - 0x80;
                }
                int y1192 = 1192 * y;
                int r = (y1192 + 1634 * v);
                int g = (y1192 - 833 * v - 400 * u);
                int b = (y1192 + 2066 * u);
                if (r < 0)
                    r = 0;
                else if (r > 262143)
                    r = 262143;
                if (g < 0)
                    g = 0;
                else if (g > 262143)
                    g = 262143;
                if (b < 0)
                    b = 0;
                else if (b > 262143)
                    b = 262143;
                rgb[index] = 0xff000000 | ((r << 6) & 0xff0000)
                        | ((g >> 2) & 0xff00) | ((b >> 10) & 0xff);
            }
        }
        return rgb;
    }


    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
    public static Bitmap getBitmap() {
        int width = Camera2Utils.mImageWidth;
        int height = Camera2Utils.mImageHeight;
        byte[] ImageByte = Camera2Utils.mImageBytes;
        RGB_int = ImageUtils.decodeYUV420SP(ImageByte, width, height);
        Log.i(TAG, "mImage Width " + width + " Height " + height);
        FinalMap = Bitmap.createBitmap(RGB_int, 0, width, width, height,
                Bitmap.Config.ARGB_8888);

        return FinalMap;
    }

    //https://www.jianshu.com/p/9ad01d4f824c
    //https://www.cnblogs.com/cmai/p/8372607.html
    public static byte[] rotateYUV420_90(byte[] data, int imageWidth, int imageHeight) {
        byte[] yuv = new byte[imageWidth * imageHeight * 3 / 2];
        int i = 0;
        int yLength = imageWidth * imageHeight;
        for (int x = 0; x < imageWidth; x++) {
            for (int y = imageHeight - 1; y >= 0; y--) {
                yuv[i++] = data[y * imageWidth + x];
            }
        }
        i = imageWidth * imageHeight * 3 / 2 - 1;
        for (int x = imageWidth - 1; x > 0; x = x - 2) {
            for (int y = 0; y < imageHeight / 2; y++) {
                yuv[i--] = data[yLength + (y * imageWidth) + x];
                yuv[i--] = data[yLength + (y * imageWidth) + (x - 1)];
            }
        }
        return yuv;
    }

    public static byte[] rotateYUV420_180(byte[] data, int imageWidth, int imageHeight) {
        int i = 0;
        int count = 0;
        int yLength = imageWidth * imageHeight;
        byte[] yuv = new byte[yLength * 3 / 2];
        for (i = yLength - 1; i >= 0; i--) {
            yuv[count++] = data[i];
        }
        for (i = yLength * 3 / 2 - 1; i >= yLength; i -= 2) {
            yuv[count++] = data[i - 1];
            yuv[count++] = data[i];
        }
        return yuv;
    }

    public static byte[] rotateYUV420_270(byte[] data, int imageWidth,
                                          int imageHeight) {
        int yLength = imageWidth * imageHeight;
        byte[] yuv = new byte[yLength * 3 / 2];
        int wh = 0;
        int uvHeight = 0;
        if (imageWidth != 0 || imageHeight != 0) {
            wh = yLength;
            uvHeight = imageHeight >> 1;
            // uvHeight = height / 2
        }

        int k = 0;
        for (int i = 0; i < imageWidth; i++) {
            int Pos = 0;
            for (int j = 0; j < imageHeight; j++) {
                yuv[k++] = data[Pos + i];
                Pos += imageWidth;
            }
        }
        for (int i = 0; i < imageWidth; i += 2) {
            int Pos = wh;
            for (int j = 0; j < uvHeight; j++) {
                yuv[k++] = data[Pos + i];
                yuv[k++] = data[Pos + i + 1];
                Pos += imageWidth;
            }
        }
        return yuv; //return rotateYUV420_180(rotateYUV420_90(data, imageWidth, imageHeight), imageWidth, imageHeight);
    }


}
