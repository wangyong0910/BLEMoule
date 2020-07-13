package www.hjrobot.blemoule.util;

/**
 * @author WangYong
 * create at 2020/6/28 13:06
 * @Description
 */
public class BytesUtil {
    //高字节在前
    //将每个byte依次搬运到int相应的位置
    public static short bytes2ShortHH(byte[] bytes) {
        short result = 0;
        for(int i=0;i<bytes.length;i++){
            result += (bytes[i] & 0xff) << ((1-i)*8);
        }
        return result;
    }

    //高字节在前
    public static byte[] short2BytesHH(short num) {
        byte[] bytes = new byte[2];
        //通过移位运算，截取低8位的方式，将int保存到byte数组
        bytes[0] = (byte) (num >> 8 & 0xff);
        bytes[1] = (byte) (num & 0xff);
        return bytes;
    }

    //低字节在前
    public static short bytes2ShortLL(byte[] bytes) {
        short result = 0;
        for(int i=0;i<bytes.length;i++){
            result += (bytes[i] & 0xff) << (i*8);
        }
        return result;
    }

    //低字节在前
    public static byte[] short2BytesLL(short num) {
        byte[] bytes = new byte[2];
        //通过移位运算，截取低8位的方式，将int保存到byte数组
        bytes[0] = (byte) (num & 0xff);
        bytes[1] = (byte) (num >> 8 & 0xff);
        return bytes;
    }
}
