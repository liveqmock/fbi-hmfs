package dep.util;

import org.apache.log4j.DailyRollingFileAppender;

import java.io.IOException;

/**
 * log4j扩展：设置日志输出路径.
 * User: zhanrui
 * Date: 12-8-10
 * Time: 下午4:15
 */
public class ProjectRollingFileAppender extends DailyRollingFileAppender {
    @Override
    public synchronized void setFile(String fileName, boolean append,
                                     boolean bufferedIO, int bufferSize) throws IOException {
        String prj_root_dir = "prj_root_dir";
        String prjRoot = PropertyManager.getProperty(prj_root_dir);
        if (prjRoot == null || "".equals(prjRoot)) {
            throw new RuntimeException("项目配置参数错误，未配置：" + prj_root_dir);
        }
        fileName = prjRoot + fileName;
        super.setFile(fileName, append, bufferedIO, bufferSize);
    }
}
