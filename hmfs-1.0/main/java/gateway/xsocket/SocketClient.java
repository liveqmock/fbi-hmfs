package gateway.xsocket;

import java.io.*;
import java.net.Socket;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by IntelliJ IDEA.
 * User: zhangxiaobo
 * Date: 12-2-22
 * Time: 下午5:24
 * To change this template use File | Settings | File Templates.
 */
public class SocketClient {
    public static void main(String[] args) {
        try {
            String header = "0000031990900TPEI8119  010       MT01MT01                       ";
            List<String> tiaList = new ArrayList<String>();
            byte[] tiaBuf = new byte[32000];
            byte[] headerBytes = header.getBytes();
            System.arraycopy(headerBytes, 0, tiaBuf, 0, headerBytes.length);
            tiaList.add("000003");
            tiaList.add("801000003202011001    801000003402011001    801000003502011001    ");
            // 添加换行符
            tiaList.add("\n");
            setBufferValues(tiaList, tiaBuf);

            System.out.println(tiaBuf.length);
            Socket socket = new Socket("127.0.0.1", 61600);
            OutputStream os = socket.getOutputStream();
            os.write(tiaBuf);
            os.flush();

            InputStream is = socket.getInputStream();
            byte[] bytes = readBytesFromInputStream(is);
            System.out.println("字节总长度： " + bytes.length);
            System.out.println("报文内容： " + new String(bytes));
            //System.out.println("报文内容： " + format16(bytes));
            os.close();
            socket.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static byte[] readBytesFromInputStream(InputStream is) throws IOException {
        if (is != null) {
            BufferedInputStream bis = new BufferedInputStream(is);
            byte[] byteBuffer = new byte[1024];
            ByteArrayOutputStream baos = new ByteArrayOutputStream();
            int len = 0;
            while ((len = bis.read(byteBuffer)) != -1) {
                baos.write(byteBuffer, 0, len);
            }
            baos.flush();
            bis.close();
            is.close();
            return baos.toByteArray();
        } else
            return null;
    }

    private static void setBufferValues(List list, byte[] bb) throws UnsupportedEncodingException {
            int start = 51;
            for (int i = 1; i <= list.size(); i++) {
                String value = list.get(i - 1).toString();
                setVarData(start, value, bb);
                start = start + value.getBytes("GBK").length + 2;
            }
        }

        private static void setVarData(int pos, String data, byte[] aa) throws UnsupportedEncodingException {
            short len = (short) data.getBytes("GBK").length;

            byte[] slen = new byte[2];
            slen[0] = (byte) (len >> 8);
            slen[1] = (byte) (len);
            System.arraycopy(slen, 0, aa, pos, 2);
            System.arraycopy(data.getBytes(), 0, aa, pos + 2, len);
        }

    private static String format16(byte[] buffer) {
            StringBuilder result = new StringBuilder();
            result.append("\n");
            int n = 0;
            byte[] lineBuffer = new byte[16];
            for (byte b : buffer) {
                if (n % 16 == 0) {
                    result.append(String.format("%05x: ", n));
                    lineBuffer = new byte[16];
                }
                result.append(String.format("%02x ", b));
                lineBuffer[n % 16] = b;
                n++;
                if (n % 16 == 0) {
                    result.append(new String(lineBuffer));
                    result.append("\n");
                }

                //TODO
                if (n >= 1024) {
                    result.append("报文过大，已截断...");
                    break;
                }

            }
            for (int k = 0; k < (16 - n % 16); k++) {
                result.append("   ");
            }
            result.append(new String(lineBuffer));
            result.append("\n");
            return result.toString();
        }
}
