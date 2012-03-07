package gateway.xsocket;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;

/**
 * Created by IntelliJ IDEA.
 * User: zhangxiaobo
 * Date: 11-8-26
 * Time: 上午11:54
 * To change this template use File | Settings | File Templates.
 */
public class Bootstrap {
     private static final Logger logger = LoggerFactory.getLogger(Bootstrap.class);

    /**
     * @param args
     */
    public static void main(String[] args) {
        try {
            ContainerManager.init();
            logger.info("====== Socket Server 初始化结束==========");

        } catch (IOException e) {
            e.printStackTrace();
            logger.error("====== Socket Server 初始化失败==========");
        }
    }
}
