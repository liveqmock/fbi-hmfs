package hmfs.view;

import com.itextpdf.text.*;
import com.itextpdf.text.pdf.*;
import common.repository.hmfs.model.HmActFund;
import common.repository.hmfs.model.HmMsgIn;
import hmfs.service.ActInfoService;
import hmfs.service.AppMngService;
import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import pub.platform.form.config.SystemAttributeNames;
import pub.platform.security.OperatorManager;
import skyline.common.utils.MessageUtil;

import javax.annotation.PostConstruct;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ManagedProperty;
import javax.faces.bean.ViewScoped;
import javax.faces.context.ExternalContext;
import javax.faces.context.FacesContext;
import javax.servlet.ServletOutputStream;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * 基本的票据查询 状态更新处理.
 * User: zhanrui
 * Date: 12-4-22
 * Time: 上午10:07
 * To change this template use File | Settings | File Templates.
 */

@ManagedBean
@ViewScoped
public class PdfPrintAction {
    private static final Logger logger = LoggerFactory.getLogger(PdfPrintAction.class);
    private String result = "打印票据凭证";
    @ManagedProperty(value = "#{actInfoService}")
    private ActInfoService actInfoService;
    @ManagedProperty(value = "#{appMngService}")
    private AppMngService appMngService;

    @PostConstruct
    public void init() {
        FacesContext context = FacesContext.getCurrentInstance();
        String pkid = (String) context.getExternalContext().getRequestParameterMap().get("pkid");
        HmMsgIn msgIn = appMngService.selectHmMsginByPkid(pkid);
        HmActFund actFund = actInfoService.selectActFundByno(msgIn.getFundActno1());
        try {
            FacesContext ctx = FacesContext.getCurrentInstance();
            HttpServletResponse response = (HttpServletResponse) ctx.getExternalContext().getResponse();
                Document document = new Document(PageSize.A4, 19, 48, 62, 30);
            ByteArrayOutputStream bos = new ByteArrayOutputStream();
            PdfWriter writer = PdfWriter.getInstance(document, bos);
            writer.setPageEvent(new PdfPageEventHelper());
            document.open();
            // 组装数据
//            document.add(transToPdfTable(msgIn, actFund));
            generateTable(document,msgIn,actFund);
            document.close();
            response.reset();
            ServletOutputStream out = response.getOutputStream();
            response.setContentType("application/pdf");
            response.setHeader("Content-disposition", "inline");
            response.setContentLength(bos.size());
            response.setHeader("Cache-Control", "max-age=30");
            bos.writeTo(out);
            out.flush();
            out.close();
            ctx.responseComplete();

        } catch (Exception e) {
            logger.error("凭证打印异常.", e);
            MessageUtil.addError("凭证打印异常." + (e.getMessage() == null ? "" : e.getMessage()));
        }
    }

    private void generateTable(Document document,HmMsgIn msgIn, HmActFund actFund) throws IOException, DocumentException {
        ExternalContext extContext = FacesContext.getCurrentInstance().getExternalContext();
        HttpSession session = (HttpSession) extContext.getSession(true);
        OperatorManager om = (OperatorManager) session.getAttribute(SystemAttributeNames.USER_INFO_NAME);
        BaseFont bfChinese = BaseFont.createFont("c:\\windows\\fonts\\simsun.ttc,1", BaseFont.IDENTITY_H, BaseFont.EMBEDDED);
        //打印需要的数据
        SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMdd");
        String strDate = sdf.format(new Date());
        String strYear = strDate.substring(0,4);
        String strMonth = strDate.substring(4,6);
        String strDay = strDate.substring(6,8);
        String projAmt = "";
        String payPart = "";
        String accountName = actFund.getInfoName() == null ? "" : actFund.getInfoName();   //21
        String txAmt = String.format("%.2f", msgIn.getTxnAmt1());
        String address = msgIn.getInfoAddr();    //22
        String houseArea = StringUtils.isEmpty(msgIn.getBuilderArea()) ? "" : msgIn.getBuilderArea();
        // TODO 对勾符号
        String houseType = "1".equals(actFund.getHouseDepType()) ? "√" : "*";
        String phoneNo = actFund.getHouseCustPhone() == null ? "" : actFund.getHouseCustPhone();
        String field83 = actFund.getDepStandard2();
        if (field83 == null) {
            projAmt = "";
            payPart = "";
        } else if (field83.endsWith("|") || !field83.contains("|")) {
            projAmt = new StringBuilder(field83).deleteCharAt(field83.length() - 1).toString();
            payPart = "";
        } else {
            String[] fields83 = field83.split("\\|");
            projAmt = fields83[0];
            payPart = fields83[1];
        }
        String accountNo = msgIn.getFundActno1();  // 业主核算户账号(维修资金账号)
        // 网点名称
        String bkDeptName = om.getOperator().getPtDeptBean().getDeptname();

        Font headFont1 = new Font(bfChinese, 14, Font.BOLD);// 设置字体大小
        Font headFont2 = new Font(bfChinese, 10, Font.NORMAL);// 设置字体大小

        PdfPCell cell = null;
        //日期打印
        float[] widths = { 185f, 28f, 28f, 192f };
        PdfPTable table = new PdfPTable(widths);
        table.getDefaultCell().setBorder(0);//设置表格默认为无边框
        cell = new PdfPCell(new Paragraph(strYear,headFont2));
        cell.setBorder(0);
        cell.setHorizontalAlignment(Element.ALIGN_RIGHT);
        table.addCell(cell);
        cell = new PdfPCell(new Paragraph(strMonth,headFont2));
        cell.setBorder(0);
        cell.setHorizontalAlignment(Element.ALIGN_RIGHT);
        table.addCell(cell);
        cell = new PdfPCell(new Paragraph(strDay,headFont2));
        cell.setBorder(0);
        cell.setHorizontalAlignment(Element.ALIGN_RIGHT);
        table.addCell(cell);
        cell = new PdfPCell(new Paragraph("",headFont2));
        cell.setBorder(0);
        cell.setHorizontalAlignment(Element.ALIGN_LEFT);
        cell.setVerticalAlignment(Element.ALIGN_MIDDLE);
        cell.setFixedHeight(17f);
        table.addCell(cell);
        document.add(table);
        //第一行

        float[] widths1 = { 51f, 190f, 67f, 125f };
        table.deleteBodyRows();
        table.setWidths(widths1);
        table.getDefaultCell().setBorder(0);//设置表格默认为无边框
        cell = new PdfPCell(new Paragraph("业主姓名",headFont2));
        cell.setBorder(0);
        cell.setHorizontalAlignment(Element.ALIGN_CENTER);
        table.addCell(cell);
        cell = new PdfPCell(new Paragraph(("").equals(accountName)?accountNo:accountName,headFont2));
        cell.setBorder(0);
        cell.setHorizontalAlignment(Element.ALIGN_LEFT);
        cell.setVerticalAlignment(Element.ALIGN_MIDDLE);
        table.addCell(cell);
        cell = new PdfPCell(new Paragraph("住宅建筑面积",headFont2));
        cell.setBorder(0);
        cell.setHorizontalAlignment(Element.ALIGN_CENTER);
        table.addCell(cell);
        cell = new PdfPCell(new Paragraph(houseArea,headFont2));
        cell.setBorder(0);
        cell.setHorizontalAlignment(Element.ALIGN_LEFT);
        cell.setVerticalAlignment(Element.ALIGN_MIDDLE);
        cell.setFixedHeight(31f);
        table.addCell(cell);
        document.add(table);

        //第二行
        float[] widths2 = { 51f, 269f, 28f, 85f };
        table.deleteBodyRows();
        table.setWidths(widths2);
        table.getDefaultCell().setBorder(0);//设置表格默认为无边框
        cell = new PdfPCell(new Paragraph("住宅地址",headFont2));
        cell.setBorder(0);
        cell.setHorizontalAlignment(Element.ALIGN_CENTER);
        table.addCell(cell);
        cell = new PdfPCell(new Paragraph(address,headFont2));
        cell.setBorder(0);
        cell.setHorizontalAlignment(Element.ALIGN_LEFT);
        cell.setVerticalAlignment(Element.ALIGN_MIDDLE);
        table.addCell(cell);
        cell = new PdfPCell(new Paragraph("电话：",headFont2));
        cell.setBorder(0);
        cell.setHorizontalAlignment(Element.ALIGN_CENTER);
        table.addCell(cell);
        cell = new PdfPCell(new Paragraph(phoneNo,headFont2));
        cell.setBorder(0);
        cell.setHorizontalAlignment(Element.ALIGN_LEFT);
        cell.setVerticalAlignment(Element.ALIGN_MIDDLE);
        cell.setFixedHeight(31f);
        table.addCell(cell);
        document.add(table);

        //第三行
        float[] widths3 = { 51f, 190f, 67f, 125f };
        table.deleteBodyRows();
        table.setWidths(widths3);
        table.getDefaultCell().setBorder(0);//设置表格默认为无边框
        cell = new PdfPCell(new Paragraph("商品住宅",headFont2));
        cell.setBorder(0);
        cell.setHorizontalAlignment(Element.ALIGN_CENTER);
        table.addCell(cell);
        cell = new PdfPCell(new Paragraph(houseType,headFont2));
        cell.setBorder(0);
        cell.setHorizontalAlignment(Element.ALIGN_LEFT);
        cell.setVerticalAlignment(Element.ALIGN_MIDDLE);
        table.addCell(cell);
        cell = new PdfPCell(new Paragraph("已售公房",headFont2));
        cell.setBorder(0);
        cell.setHorizontalAlignment(Element.ALIGN_CENTER);
        table.addCell(cell);
        cell = new PdfPCell(new Paragraph(houseType,headFont2));
        cell.setBorder(0);
        cell.setHorizontalAlignment(Element.ALIGN_LEFT);
        cell.setVerticalAlignment(Element.ALIGN_MIDDLE);
        cell.setFixedHeight(28f);
        table.addCell(cell);
        document.add(table);

        //第四行
        float[] widths4 = { 51f, 190f, 67f, 125f };
        table.deleteBodyRows();
        table.setWidths(widths4);
        table.getDefaultCell().setBorder(0);//设置表格默认为无边框
        cell = new PdfPCell(new Paragraph("住宅建筑面积",headFont2));
        cell.setBorder(0);
        cell.setHorizontalAlignment(Element.ALIGN_CENTER);
        table.addCell(cell);
        cell = new PdfPCell(new Paragraph(houseArea,headFont2));
        cell.setBorder(0);
        cell.setHorizontalAlignment(Element.ALIGN_LEFT);
        cell.setVerticalAlignment(Element.ALIGN_MIDDLE);
        table.addCell(cell);
        cell = new PdfPCell(new Paragraph("住宅建筑面积",headFont2));
        cell.setBorder(0);
        cell.setHorizontalAlignment(Element.ALIGN_CENTER);
        table.addCell(cell);
        cell = new PdfPCell(new Paragraph(houseArea,headFont2));
        cell.setBorder(0);
        cell.setHorizontalAlignment(Element.ALIGN_LEFT);
        cell.setVerticalAlignment(Element.ALIGN_MIDDLE);
        cell.setFixedHeight(24f);
        table.addCell(cell);
        document.add(table);

        //第五行
        float[] widths5 = { 51f, 190f, 67f, 125f };
        table.deleteBodyRows();
        table.setWidths(widths5);
        table.getDefaultCell().setBorder(0);//设置表格默认为无边框
        cell = new PdfPCell(new Paragraph("工程造价",headFont2));
        cell.setBorder(0);
        cell.setHorizontalAlignment(Element.ALIGN_CENTER);
        table.addCell(cell);
        cell = new PdfPCell(new Paragraph(projAmt,headFont2));
        cell.setBorder(0);
        cell.setHorizontalAlignment(Element.ALIGN_LEFT);
        cell.setVerticalAlignment(Element.ALIGN_MIDDLE);
        table.addCell(cell);
        cell = new PdfPCell(new Paragraph("工程造价",headFont2));
        cell.setBorder(0);
        cell.setHorizontalAlignment(Element.ALIGN_CENTER);
        table.addCell(cell);
        cell = new PdfPCell(new Paragraph(projAmt,headFont2));
        cell.setBorder(0);
        cell.setHorizontalAlignment(Element.ALIGN_LEFT);
        cell.setVerticalAlignment(Element.ALIGN_MIDDLE);
        cell.setFixedHeight(24f);
        table.addCell(cell);
        document.add(table);

        //第六行
        float[] widths6 = { 51f, 34f,34f,34f,34f,34f,34f,34f,34f,34f,34f,34f, 67f };
        PdfPTable table6 = new PdfPTable(widths6);
        table6.getDefaultCell().setBorder(0);
        cell = new PdfPCell(new Paragraph("交存金额",headFont2));
        cell.setBorder(0);
        table6.addCell(cell);
        cell = new PdfPCell(new Paragraph("壹",headFont2));
        cell.setBorder(0);
        table6.addCell(cell);
        cell = new PdfPCell(new Paragraph("壹",headFont2));
        cell.setBorder(0);
        table6.addCell(cell);
        cell = new PdfPCell(new Paragraph("壹",headFont2));
        cell.setBorder(0);
        table6.addCell(cell);
        cell = new PdfPCell(new Paragraph("壹",headFont2));
        cell.setBorder(0);
        table6.addCell(cell);
        cell = new PdfPCell(new Paragraph("壹",headFont2));
        cell.setBorder(0);
        table6.addCell(cell);
        cell = new PdfPCell(new Paragraph("壹",headFont2));
        cell.setBorder(0);
        table6.addCell(cell);
        cell = new PdfPCell(new Paragraph("壹",headFont2));
        cell.setBorder(0);
        table6.addCell(cell);
        cell = new PdfPCell(new Paragraph("壹",headFont2));
        cell.setBorder(0);
        table6.addCell(cell);
        cell = new PdfPCell(new Paragraph("壹",headFont2));
        cell.setBorder(0);
        table6.addCell(cell);
        cell = new PdfPCell(new Paragraph("壹",headFont2));
        cell.setBorder(0);
        table6.addCell(cell);
        table6.addCell("");
        cell = new PdfPCell(new Paragraph(txAmt,headFont2));
        cell.setBorder(0);
        cell.setFixedHeight(37f);
        table6.addCell(cell);
        document.add(table6);


        //第七行
        float[] widths7 = { 51f, 190f, 67f, 125f };
        table.deleteBodyRows();
        table.setWidths(widths7);
        table.getDefaultCell().setBorder(0);//设置表格默认为无边框
        //第一行
        cell = new PdfPCell(new Paragraph("业主姓名",headFont2));
        cell.setBorder(0);
        cell.setHorizontalAlignment(Element.ALIGN_CENTER);
        table.addCell(cell);
        cell = new PdfPCell(new Paragraph(("").equals(accountName)?accountNo:accountName,headFont2));
        cell.setBorder(0);
        cell.setHorizontalAlignment(Element.ALIGN_LEFT);
        cell.setVerticalAlignment(Element.ALIGN_MIDDLE);
        table.addCell(cell);
        cell = new PdfPCell(new Paragraph("住宅建筑面积",headFont2));
        cell.setBorder(0);
        cell.setHorizontalAlignment(Element.ALIGN_CENTER);
        table.addCell(cell);
        cell = new PdfPCell(new Paragraph(houseArea,headFont2));
        cell.setBorder(0);
        cell.setHorizontalAlignment(Element.ALIGN_LEFT);
        cell.setVerticalAlignment(Element.ALIGN_MIDDLE);
        cell.setFixedHeight(31f);
        table.addCell(cell);
        document.add(table);

    }

    private PdfPTable transToPdfTable(HmMsgIn msgIn, HmActFund actFund) throws IOException, DocumentException {
        PdfPTable table = new PdfPTable(new float[]{900f});// 建立一个pdf表格
        ExternalContext extContext = FacesContext.getCurrentInstance().getExternalContext();
        HttpSession session = (HttpSession) extContext.getSession(true);
        OperatorManager om = (OperatorManager) session.getAttribute(SystemAttributeNames.USER_INFO_NAME);

        table.setSpacingBefore(130f);// 设置表格上面空白宽度
        table.setTotalWidth(835);// 设置表格的宽度
        table.setLockedWidth(false);// 设置表格的宽度固定
        table.setSpacingAfter(120f);
        table.getDefaultCell().setBorder(0);//设置表格默认为无边框
        BaseFont bfChinese = BaseFont.createFont("c:\\windows\\fonts\\simsun.ttc,1", BaseFont.IDENTITY_H, BaseFont.EMBEDDED);
        Font headFont1 = new Font(bfChinese, 14, Font.BOLD);// 设置字体大小
        Font headFont2 = new Font(bfChinese, 10, Font.NORMAL);// 设置字体大小
        PdfPCell cell = new PdfPCell(new Paragraph("", headFont1));
        cell.setBorder(0);
        cell.setFixedHeight(40);//单元格高度
        cell.setHorizontalAlignment(Element.ALIGN_CENTER);// 设置内容水平居中显示
        cell.setVerticalAlignment(Element.ALIGN_MIDDLE);
        table.addCell(cell);
        String projAmt = "";
        String payPart = "";

        String accountName = actFund.getInfoName() == null ? "" : actFund.getInfoName();   //21
        String txAmt = String.format("%.2f", msgIn.getTxnAmt1());
        String address = msgIn.getInfoAddr();    //22
        String houseArea = StringUtils.isEmpty(msgIn.getBuilderArea()) ? "" : msgIn.getBuilderArea();
        // TODO 对勾符号
        String houseType = "1".equals(actFund.getHouseDepType()) ? "√" : "*";
        String phoneNo = actFund.getHouseCustPhone() == null ? "" : actFund.getHouseCustPhone();
        String field83 = actFund.getDepStandard2();
        if (field83 == null) {
            projAmt = "";
            payPart = "";
        } else if (field83.endsWith("|") || !field83.contains("|")) {
            projAmt = new StringBuilder(field83).deleteCharAt(field83.length() - 1).toString();
            payPart = "";
        } else {
            String[] fields83 = field83.split("\\|");
            projAmt = fields83[0];
            payPart = fields83[1];
        }
        String accountNo = msgIn.getFundActno1();  // 业主核算户账号(维修资金账号)
        // 网点名称
        String bkDeptName = om.getOperator().getPtDeptBean().getDeptname();

        String[] singleRows = new String[]{
//                "资金账号：" + accountNo,
//                "业主户名：" + accountName,
//                "缴款金额：" + txAmt,
//                "地址：" + address,
//                "建筑面积：" + houseArea,
//                "房屋类型：商品房：" + houseType,
//                "电话：" + phoneNo,
//                "工程造价：" + projAmt,
//                "缴存比例：" + payPart,
//                "营业网点：" + bkDeptName
            rightPad4ChineseToByteLength(("").equals(accountName)?accountNo:accountName,20,"")+leftPad4ChineseToByteLength(houseArea,40,""),
            rightPad4ChineseToByteLength(address,40,"") + leftPad4ChineseToByteLength(phoneNo,12,""),
            leftPad4ChineseToByteLength(" ",30,"")+houseType,
            leftPad4ChineseToByteLength(houseArea,15,""),
            leftPad4ChineseToByteLength(projAmt,12,"")+leftPad4ChineseToByteLength(payPart,24,""),
            leftPad4ChineseToByteLength(txAmt,12,""),
            leftPad4ChineseToByteLength(bkDeptName,16,"")
        };
        for (String snglRow : singleRows) {
            cell = new PdfPCell(new Paragraph(snglRow, headFont2));
            cell.setBorder(0);
            cell.setFixedHeight(40);
            cell.setPaddingLeft(50);
            cell.setHorizontalAlignment(Element.ALIGN_LEFT);
            table.addCell(cell);
        }
        return table;

    }

    private String rightPad4ChineseToByteLength(String srcStr, int totalByteLength, String padStr) {
        if (srcStr == null) {
            return null;
        }
        int srcByteLength = srcStr.getBytes().length;

        if (padStr == null || "".equals(padStr)) {
            padStr = " ";
        } else if (padStr.getBytes().length > 1 || totalByteLength <= 0) {
            throw new RuntimeException("参数错误");
        }
        StringBuilder rtnStrBuilder = new StringBuilder();
        if (totalByteLength >= srcByteLength) {
            rtnStrBuilder.append(srcStr);
            for (int i = 0; i < totalByteLength - srcByteLength; i++) {
                rtnStrBuilder.append(padStr);
            }
        } else {
            byte[] rtnBytes = new byte[totalByteLength];
            System.arraycopy(srcStr.getBytes(), 0, rtnBytes, 0, totalByteLength);
            rtnStrBuilder.append(rtnBytes);
        }
        return rtnStrBuilder.toString();
    }

    private String leftPad4ChineseToByteLength(String srcStr, int totalByteLength, String padStr) {
        if (srcStr == null) {
            return null;
        }
        int srcByteLength = srcStr.getBytes().length;

        if (padStr == null || "".equals(padStr)) {
            padStr = " ";
        } else if (padStr.getBytes().length > 1 || totalByteLength <= 0) {
            throw new RuntimeException("参数错误");
        }
        StringBuilder rtnStrBuilder = new StringBuilder();
        if (totalByteLength >= srcByteLength) {
            for (int i = 0; i < totalByteLength - srcByteLength; i++) {
                rtnStrBuilder.append(padStr);
            }
            rtnStrBuilder.append(srcStr);
        } else {
            byte[] rtnBytes = new byte[totalByteLength];
            System.arraycopy(srcStr.getBytes(), 0, rtnBytes, 0, totalByteLength);
            rtnStrBuilder.append(rtnBytes);
        }
        return rtnStrBuilder.toString();
    }

    // -------------------------------

    public ActInfoService getActInfoService() {
        return actInfoService;
    }

    public void setActInfoService(ActInfoService actInfoService) {
        this.actInfoService = actInfoService;
    }

    public AppMngService getAppMngService() {
        return appMngService;
    }

    public void setAppMngService(AppMngService appMngService) {
        this.appMngService = appMngService;
    }

    public String getResult() {
        return result;
    }

    public void setResult(String result) {
        this.result = result;
    }
}
