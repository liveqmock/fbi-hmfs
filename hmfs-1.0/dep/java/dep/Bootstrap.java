package dep;

import dep.util.PropertyManager;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

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
            logger.info("����ǰϵͳ״̬����"+ PropertyManager.getProperty("hmfs_sys_status_flag") + "��");
            logger.info("====== Dep Server ��ʼ������==========");
        } catch (Exception e) {
            logger.error("====== Dep Server ��ʼ��ʧ��==========", e);
            System.exit(1);
        }
    }
}
