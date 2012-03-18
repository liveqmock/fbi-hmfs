package dep;

import dep.gateway.xsocket.server.XSocketServer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.ApplicationContext;
import org.springframework.context.support.ClassPathXmlApplicationContext;

import java.io.IOException;

public class ContainerManager {

    private static final Logger logger = LoggerFactory.getLogger(ContainerManager.class);
    private static ApplicationContext context;

    private ContainerManager() {
    }

    public static void init() throws IOException {
        logger.info("... Container Manager 初始化开始.......");
        context = new ClassPathXmlApplicationContext(new String[]{"applicationContext.xml"});
        XSocketServer hmbSocketServer = (XSocketServer) getBean("hmbSocketServer");
        hmbSocketServer.start();
        XSocketServer cbsSocketServer = (XSocketServer) getBean("cmbSocketServer");
        cbsSocketServer.start();
        XSocketServer webSocketServer = (XSocketServer) getBean("webSocketServer");
        webSocketServer.start();
    }

    public static Object getBean(String key) {
        /*if (context == null) {
            init();
        }*/
        return context.getBean(key);
    }

    public static void stop(int code) {
        if (code == 0) {
            logger.info(" Container Manager 正常关闭...");
        } else {
            logger.info(" Container Manager 发生异常，即将关闭...");
        }
        System.exit(code);
    }
}
