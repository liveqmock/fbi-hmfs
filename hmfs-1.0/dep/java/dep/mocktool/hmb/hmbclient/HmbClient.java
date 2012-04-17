package dep.mocktool.hmb.hmbclient;

import dep.gateway.hmb8583.HmbMessageFactory;
import dep.util.PropertyManager;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.support.ClassPathXmlApplicationContext;

import java.io.IOException;
import java.net.Socket;

public class HmbClient implements Runnable {
    private static final Logger log = LoggerFactory.getLogger(HmbClient.class);
    private static HmbMessageFactory mfact;

    private Socket sock;
    private boolean done = false;

    //private ApplicationContext context;
    protected static String SEND_SYS_ID = PropertyManager.getProperty("SEND_SYS_ID");
    protected static String ORIG_SYS_ID = PropertyManager.getProperty("ORIG_SYS_ID");
    private static ClassPathXmlApplicationContext context;


    public HmbClient(Socket socket) {
        sock = socket;
    }

    public void run() {
        byte[] lenbuf = new byte[7];
        try {
            while (sock != null && sock.isConnected()) {
                if (sock.getInputStream().read(lenbuf) == 7) {
                    int size = new Integer(new String(lenbuf)) + 4;
                    byte[] buf = new byte[size];
                    sock.getInputStream().read(buf);
                    log.info("HMB Client 接收响应:" + new String(buf));

                    //sock.close();

                } else {
                    return;
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            if (sock != null) {
                try {
                    sock.close();
                } catch (IOException ex) {
                }
            }
        }

    }

    protected void stop() {
        done = true;
        try {
            sock.close();
        } catch (IOException ex) {
            log.error("socket err");
        }
        sock = null;
    }

    public static void main(String[] args) throws Exception {
        context = new ClassPathXmlApplicationContext("hmb-context.xml");

        Socket sock = new Socket("localhost", 41014);
        HmbClient client = new HmbClient(sock);
        Thread reader = new Thread(client, "hmb-client");
        reader.start();

        //byte[] txnbuf = "00005495140FE1D8080000000000000000000000001500003181202290009995140001220020014201203092111271#1113491010629869423001381019F9E04000001021461083DC0000150103331050629869423009102000999011裕环路195号017市北区裕环路195号0110574.7101312320000229226351101#1#15100000000000006010030青岛市棚户区改造开发建设指挥部100010010001001010101010101001001081019F9E04000001021461083DC0000050103331050629872726021102000999XXX001001010001#025市北区裕环路195号1单元1010110574.710131232000022925935701232000022922635111510000000000000601#001#11001001#0030|001#10113XXX1131011#01#010".getBytes();
        //byte[] txnbuf = ((HmbClientService)context.getBean("hmbClientService")).getTxnbuf_5210();

        //5110
//        byte[] txnbuf = ((HmbClientService)context.getBean("hmbClientService")).getTxnbuf("5110", "120314000101511000");

        //5210  1+1
        byte[] txnbuf = ((HmbClientService) context.getBean("hmbClientService")).getTxnbuf("5210", "120316004833521000");

        //5210 1+7200
//        byte[] txnbuf = ((HmbClientService) context.getBean("hmbClientService")).getTxnbuf("5210", "120314000001521000");

        //5120 国土局提供
//        byte[] txnbuf = "00003245120FE1D8080000000000000000000000001500003181203150048295120001120020014201203150903201#11134212107139449423001281019F9E04000001021461083DC00000501033310507139449423009101004763014青岛测试项目11014青岛测试项目1101001001212320001325026351101#1#15100000000000003010028和记黄埔地产（青岛）有限公司1000100100010010101010101010010010".getBytes();

        String txnmsg = new String(txnbuf);
        System.out.println("TXN MSG:" + txnmsg);

        sock.getOutputStream().write(txnbuf);
        System.out.println("等待响应中...");

        Thread.sleep(3000);

        sock.getOutputStream().write(txnbuf);
        System.out.println("等待响应中...");



        while (sock.isConnected()) {
            Thread.sleep(500);
        }
        client.stop();
        reader.interrupt();
        System.out.println("DONE...");
    }

}
