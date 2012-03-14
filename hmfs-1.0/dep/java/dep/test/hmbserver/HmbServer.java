package dep.test.hmbserver;

import dep.gateway.hmb8583.HmbMessageFactory;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

/**
 * Created by IntelliJ IDEA.
 * User: zhanrui
 * Date: 12-3-14
 * Time: 上午10:12
 * To change this template use File | Settings | File Templates.
 */
public class HmbServer implements Runnable {
    private static final Logger log = LoggerFactory.getLogger(HmbServer.class);

    private static ScheduledExecutorService threadPool = Executors.newScheduledThreadPool(1);
    private static HmbMessageFactory messageFactory;

    private Socket socket;

    HmbServer(Socket sock) throws IOException {
        socket = sock;
    }

    public void run() {
        int count = 0;
        byte[] lenbuf = new byte[7];
        try {
            while (socket != null && socket.isConnected()
                    && Thread.currentThread().isAlive() && !Thread.currentThread().isInterrupted()) {
                if (socket.getInputStream().read(lenbuf) == 7) {
                    int size = new Integer(new String(lenbuf));
                    byte[] buf = new byte[size];
                    socket.getInputStream().read(buf);
                    count++;
                    threadPool.schedule(new Processor(buf, socket), 400, TimeUnit.MILLISECONDS);
                }
            }
        } catch (IOException ex) {
            log.error("err", ex);
        }
        log.debug("已处理请求数量：", count);
        try {
            socket.close();
        } catch (IOException ex) {}
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
                log.debug("Parsing incoming: '{}'", new String(msg));
                byte[] gbks = "00005495140FE1D8080000000000000000000000001500003181202290009995140001220020014201203092111271#1113491010629869423001381019F9E04000001021461083DC0000150103331050629869423009102000999011裕环路195号017市北区裕环路195号0110574.7101312320000229226351101#1#15100000000000006010030青岛市棚户区改造开发建设指挥部100010010001001010101010101001001081019F9E04000001021461083DC0000050103331050629872726021102000999XXX001001010001#025市北区裕环路195号1单元1010110574.710131232000022925935701232000022922635111510000000000000601#001#11001001#0030|001#10113XXX1131011#01#010".getBytes("GBK");
                socket.getOutputStream().write(gbks);
            } catch (Exception ex) {
                log.error("Sending response", ex);
            }
        }
    }

    public static void main(String[] args) throws Exception {
        messageFactory = new HmbMessageFactory();
        ServerSocket server = new ServerSocket(60000);
        log.info("Waiting for connections...");
        while (true) {
            Socket sock = server.accept();
            log.info("New connection from {}:{}", sock.getInetAddress(), sock.getPort());
            new Thread(new HmbServer(sock), "j8583-server").start();
        }
    }

}
