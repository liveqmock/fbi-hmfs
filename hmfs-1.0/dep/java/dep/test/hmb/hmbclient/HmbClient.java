package dep.test.hmb.hmbclient;

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
    protected  static String SEND_SYS_ID =  PropertyManager.getProperty("SEND_SYS_ID");
    protected  static String ORIG_SYS_ID =  PropertyManager.getProperty("ORIG_SYS_ID");
    private static ClassPathXmlApplicationContext context;


    public HmbClient(Socket socket) {
        sock = socket;
    }

    public void run() {
        byte[] lenbuf = new byte[7];
        try {
            while (sock != null && sock.isConnected()) {
                if (sock.getInputStream().read(lenbuf) == 7) {
                    int size = new Integer(new String(lenbuf));
                    byte[] buf = new byte[size];
                    sock.getInputStream().read(buf);
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

        Socket sock = new Socket("localhost", 61600);
        HmbClient client = new HmbClient(sock);
        Thread reader = new Thread(client, "hmb-client");
        reader.start();

        //byte[] txnbuf = "00005495140FE1D8080000000000000000000000001500003181202290009995140001220020014201203092111271#1113491010629869423001381019F9E04000001021461083DC0000150103331050629869423009102000999011裕环路195号017市北区裕环路195号0110574.7101312320000229226351101#1#15100000000000006010030青岛市棚户区改造开发建设指挥部100010010001001010101010101001001081019F9E04000001021461083DC0000050103331050629872726021102000999XXX001001010001#025市北区裕环路195号1单元1010110574.710131232000022925935701232000022922635111510000000000000601#001#11001001#0030|001#10113XXX1131011#01#010".getBytes();
        //byte[] txnbuf = ((HmbClientService)context.getBean("hmbClientService")).getTxnbuf_5210();
        byte[] txnbuf = ((HmbClientService)context.getBean("hmbClientService")).getTxnbuf("5110", "120314000101511000");

        String txnmsg = new String(txnbuf);
        log.info("TXN MSG:" + txnmsg);
        
        sock.getOutputStream().write(txnbuf);
        log.debug("等待响应中...");

        while (sock.isConnected()) {
            Thread.sleep(500);
        }
        client.stop();
        reader.interrupt();
        log.debug("DONE...");
    }


//    private byte[] getTxn5210msg(){
//        context = new ClassPathXmlApplicationContext("hmb-context.xml");
//
//        Msg005 msg005 = new Msg005();
//    }

}
