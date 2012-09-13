package dep.mocktool.hmb.hmbserver;

import dep.gateway.hmb8583.HmbMessageFactory;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.support.ClassPathXmlApplicationContext;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;

/**
 * 国土局端模拟测试SERVER
 * User: zhanrui
 * Date: 12-3-11
 * Time: 上午10:12
 * To change this template use File | Settings | File Templates.
 */
public class HmbServer implements Runnable {
    private static final Logger log = LoggerFactory.getLogger(HmbServer.class);

    private static ScheduledExecutorService threadPool = Executors.newScheduledThreadPool(1);
    private static HmbMessageFactory messageFactory;

    private static ClassPathXmlApplicationContext context;

    private Socket socket;

    //private int  processCount;

    HmbServer(Socket sock ) throws IOException {
        socket = sock;
        log.info("socket:" + sock.toString());

    }

    public void run() {
        int count = 0;
        byte[] lenbuf = new byte[7];
        try {
            int step = 0;
            while (socket != null && socket.isConnected() && !socket.isClosed()
                    && Thread.currentThread().isAlive() && !Thread.currentThread().isInterrupted()) {
                int readnumber = socket.getInputStream().read(lenbuf);
                if (readnumber == 7) {
                    int size = new Integer(new String(lenbuf));
                    byte[] buf = new byte[size + 4];
                    socket.getInputStream().read(buf);
                    count++;
                    //threadPool.schedule(new Processor(buf, socket), 400, TimeUnit.MILLISECONDS);
                    Thread.sleep(500);
                    new Processor(buf, socket).run();
                }
                Thread.sleep(500);
                step++;
                log.info("循环读取" + step);
            }
        } catch (Exception ex) {
            log.error("err", ex);
        }
        log.info("已处理请求数量：" + count);
        try {
            log.info("close socket");
            socket.close();
        } catch (IOException ex) {
            ex.printStackTrace();
        }
    }

    private class Processor implements Runnable {

        private byte[] msg;
        private Socket sock;

        Processor(byte[] buf, Socket s) {
            msg = buf;
            sock = s;
        }

        public void run() {
            try {
                String msgin = new String(msg);
                log.info("Parsing incoming: '{}'", msgin);
                String txncode = msgin.substring(0,4);

                AbstractTxnProcessor processor = (AbstractTxnProcessor) context.getBean("txn" + txncode + "Processor");
                processor.init(msg);

                Thread.sleep(200);

                byte[] gbks = processor.process();
                //socket.getOutputStream().write(gbks);


                byte[] tmp = new byte[5];
                socket.getOutputStream().write(tmp);



                Thread.sleep(1000);
                socket.close();

            } catch (Exception ex) {
                log.error("响应错误:", ex);
            }
        }
    }


    //==========================================================================
    public static void main(String[] args) throws Exception {
        //messageFactory = new HmbMessageFactory();
        context = new ClassPathXmlApplicationContext("hmb-context.xml");

        ServerSocket server = new ServerSocket(42014);
        log.info("开始接收报文...");
        int  processCount = 0;
        while (true) {
            Socket sock = server.accept();
            log.info("新连接 {}:{}", sock.getInetAddress(), sock.getPort());
            //new Thread(new HmbServer(sock), "hmb-server").start();
            new Thread(new HmbServer(sock)).start();
            processCount ++;
            log.info("线程数量：" + processCount);
        }
    }
}
