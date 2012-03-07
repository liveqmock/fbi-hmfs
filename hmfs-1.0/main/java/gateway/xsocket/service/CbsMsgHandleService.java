package gateway.xsocket.service;

import gateway.txn.cbs.DataHead;
import gateway.txn.cbs.PayDetail;
import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by IntelliJ IDEA.
 * User: zhangxiaobo
 * Date: 11-8-18
 * Time: 上午2:27
 * To change this template use File | Settings | File Templates.
 */
/*
A2流水号	（16位）
A3错误码	（4位）
A4服务类型	（4位）
A5包编号	（4位）
A6最后一包标志	（1位）
 */

@Service
public class CbsMsgHandleService implements IMessageHandler {

    private static final Logger logger = LoggerFactory.getLogger(CbsMsgHandleService.class);

    public CbsMsgHandleService() {

    }

    @Override
    public byte[] handleMessage(byte[] bytes) {
        // TODO  测试缴款通知书缴费
        DataHead dataHead = new DataHead();
        dataHead.transBytesToFields(bytes);

        // TODO start from 29
        String datagram = new String(bytes, 29, bytes.length - 29);
        logger.info(" ==== 报文正文：" + datagram);

        // TODO
        // 缴款通知书编号	18	维修资金缴款通知书编号
        // 缴费金额	16
        logger.info("缴款通知书编号：" + datagram.substring(0, 18) + "    缴费金额：" + datagram.substring(18, 34));
        /*for(String detail : datagram.split("\n")) {
            logger.info("=== 报文项目内容： " + detail);
            for(String field : detail.split("|")) {
                logger.info("=== 报文字段内容： " + field);
            }
        }
*/
        PayDetail payDetail = new PayDetail();
        payDetail.setAccountName("张三");
        payDetail.setTxAmt("123456.78");
        payDetail.setAddress("青岛市北区");
        payDetail.setHouseArea("98.5");
        payDetail.setHouseType("0");
        payDetail.setPhoneNo("89901100");
        payDetail.setProjAmt("1001234567.89");
        payDetail.setPayPart("80%");

        PayDetail detail = new PayDetail();
        detail.setAccountName("李四");
        detail.setTxAmt("1234.56");
        detail.setAddress("青岛崂山区");
        detail.setHouseArea("89.6");
        detail.setHouseType("1");
        detail.setPhoneNo("89931100");
        detail.setProjAmt("1234567.89");
        detail.setPayPart("70%");

        // 生成返回报文头
        DataHead rtnDataHead = new DataHead();
        rtnDataHead.setSerialNo(dataHead.getSerialNo());
        rtnDataHead.setErrorCode("0000");
        rtnDataHead.setLast(true);
        rtnDataHead.setPkgNo("1");
        rtnDataHead.setTxnCode(dataHead.getTxnCode());

        // 报文内容
        List<PayDetail> payDetailList = new ArrayList<PayDetail>();
        payDetailList.add(payDetail);
        payDetailList.add(detail);

        StringBuilder rtnStrBuilder = new StringBuilder();
        rtnStrBuilder.append(rtnDataHead.toStringByFieldLength());
        rtnStrBuilder.append(datagram.substring(0, 18) + "2   ");
        rtnStrBuilder.append(toDatagramString(payDetailList));
        return (StringUtils.rightPad(String.valueOf(rtnStrBuilder.length() + 6), 6, " ") + rtnStrBuilder.toString()).getBytes();
    }
    
    private String toDatagramString(List<PayDetail> payDetailList) {
        StringBuilder strDatagramBuilder = new StringBuilder();
        for(PayDetail payDetail : payDetailList) {
            strDatagramBuilder.append(payDetail.toStringByDelimiter("|")).append("\n");
        }
        strDatagramBuilder.deleteCharAt(strDatagramBuilder.length() - 1);
        return strDatagramBuilder.toString();
    }
}
