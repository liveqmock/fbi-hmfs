package dep.util;

/**
 * Created with IntelliJ IDEA.
 * User: zhangxiaobo
 * Date: 12-4-13
 * Time: ����3:19
 * To change this template use File | Settings | File Templates.
 */
public class StringPad {

    public static String rightPad4ChineseToByteLength(String srcStr, int totalByteLength, String padStr) {
        if (srcStr == null) {
            return null;
        }
        int srcByteLength = srcStr.getBytes().length;

        if (padStr == null || "".equals(padStr)) {
            padStr = " ";
        } else if (padStr.getBytes().length > 1 || totalByteLength <= 0 || totalByteLength < srcByteLength) {
            throw new RuntimeException("��������");
        }
        StringBuilder rtnStrBuilder = new StringBuilder(srcStr);
        for (int i = 0; i < totalByteLength - srcByteLength; i ++) {
            rtnStrBuilder.append(padStr);
        }
        return rtnStrBuilder.toString();
    }

    public static void main(String[] args) {
        String str = "����";
        System.out.println(str.length());           // 2
        System.out.println(str.getBytes().length);  // 4

        str = "����aa";
        System.out.println(str.length());          // 4
        System.out.println(str.getBytes().length);  // 6
    }
}
