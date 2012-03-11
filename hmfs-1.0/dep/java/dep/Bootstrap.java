package dep;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;

/**
 * Created by IntelliJ IDEA.
 * User: zhangxiaobo
 * Date: 11-8-26
 * Time: ����11:54
 * To change this template use File | Settings | File Templates.
 */
public class Bootstrap {
    private static final Logger logger = LoggerFactory.getLogger(Bootstrap.class);

    public static void main(String[] args) {
        try {
            ContainerManager.init();
            logger.info("====== Dep Server ��ʼ������==========");
        } catch (IOException e) {
            logger.error("====== Dep Server ��ʼ��ʧ��==========", e);
        }
    }
}