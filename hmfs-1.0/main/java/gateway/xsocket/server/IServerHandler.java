package gateway.xsocket.server;

import org.xsocket.connection.*;

/**
 * Created by IntelliJ IDEA.
 * User: zhangxiaobo
 * Date: 11-8-10
 * Time: ����9:11
 * To change this template use File | Settings | File Templates.
 */
public interface IServerHandler extends IDataHandler, IConnectHandler, IIdleTimeoutHandler,
        IConnectionTimeoutHandler, IDisconnectHandler, IConnectExceptionHandler {
}
