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
import java.util.HashMap;
import java.util.Map;

/**
 * ������Ʊ�ݲ�ѯ ״̬���´���.
 * User: zhanrui
 * Date: 12-4-22
 * Time: ����10:07
 * To change this template use File | Settings | File Templates.
 */

@ManagedBean
@ViewScoped
public class PdfPrintAction {
    private static final Logger logger = LoggerFactory.getLogger(PdfPrintAction.class);
    private String result = "��ӡƱ��ƾ֤";
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
                Document document = new Document(PageSize.A4, 19, 48, 60, 30);
            ByteArrayOutputStream bos = new ByteArrayOutputStream();
            PdfWriter writer = PdfWriter.getInstance(document, bos);
            writer.setPageEvent(new PdfPageEventHelper());
            document.open();
            // ��װ����
//            document.add(transToPdfTable(msgIn, actFund));
            //��װ���� 2012-10-19
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
            logger.error("ƾ֤��ӡ�쳣.", e);
            MessageUtil.addError("ƾ֤��ӡ�쳣." + (e.getMessage() == null ? "" : e.getMessage()));
        }
    }

    private void generateTable(Document document,HmMsgIn msgIn, HmActFund actFund) throws IOException, DocumentException {
        ExternalContext extContext = FacesContext.getCurrentInstance().getExternalContext();
        HttpSession session = (HttpSession) extContext.getSession(true);
        OperatorManager om = (OperatorManager) session.getAttribute(SystemAttributeNames.USER_INFO_NAME);
        BaseFont bfChinese = BaseFont.createFont("c:\\windows\\fonts\\simsun.ttc,1", BaseFont.IDENTITY_H, BaseFont.EMBEDDED);
        //��ӡ��Ҫ������
        SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMdd");
        String strDate = sdf.format(new Date());
        String strYear = strDate.substring(0,4);
        String strMonth = strDate.substring(4,6);
        String strDay = strDate.substring(6,8);
        String projAmt = "";
        String payPart = "";
        String accountName = actFund.getInfoName() == null ? "" : actFund.getInfoName();   //21
        String txAmt = String.format("%.2f", msgIn.getTxnAmt1());
        Map mapNum = null;
        mapNum = this.transToRMB(txAmt);
        String address = msgIn.getInfoAddr();    //22
        String houseArea = StringUtils.isEmpty(msgIn.getBuilderArea()) ? "" : msgIn.getBuilderArea();
        // TODO �Թ�����
        String houseType = "1".equals(actFund.getHouseDepType()) ? "��" : "*";
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
        String accountNo = msgIn.getFundActno1();  // ҵ�����㻧�˺�(ά���ʽ��˺�)
        // ��������
        String bkDeptName = om.getOperator().getPtDeptBean().getDeptname();

        Font headFont1 = new Font(bfChinese, 14, Font.BOLD);// ���������С
        Font headFont2 = new Font(bfChinese, 10, Font.NORMAL);// ���������С

        PdfPCell cell = null;
        //���ڴ�ӡ
        float[] widths = { 190f, 28f, 28f, 187f };
        PdfPTable table = new PdfPTable(widths);
        table.getDefaultCell().setBorder(0);//���ñ��Ĭ��Ϊ�ޱ߿�
        cell = new PdfPCell(new Paragraph(strYear,headFont2));
        cell.setBorder(0);
        cell.setHorizontalAlignment(Element.ALIGN_RIGHT);
        cell.setVerticalAlignment(Element.ALIGN_TOP);
        table.addCell(cell);
        cell = new PdfPCell(new Paragraph(strMonth,headFont2));
        cell.setBorder(0);
        cell.setHorizontalAlignment(Element.ALIGN_RIGHT);
        cell.setVerticalAlignment(Element.ALIGN_TOP);
        table.addCell(cell);
        cell = new PdfPCell(new Paragraph(strDay,headFont2));
        cell.setBorder(0);
        cell.setHorizontalAlignment(Element.ALIGN_RIGHT);
        cell.setVerticalAlignment(Element.ALIGN_TOP);
        table.addCell(cell);
        cell = new PdfPCell(new Paragraph("",headFont2));
        cell.setBorder(0);
        cell.setFixedHeight(20f);
        table.addCell(cell);
        document.add(table);
        //��һ��

        float[] widths1 = { 51f, 190f, 87f, 105f };
        table.deleteBodyRows();
        table.setWidths(widths1);
        table.getDefaultCell().setBorder(0);//���ñ��Ĭ��Ϊ�ޱ߿�
        cell = new PdfPCell(new Paragraph("",headFont2));  //ҵ������
        cell.setBorder(0);
        cell.setHorizontalAlignment(Element.ALIGN_CENTER);
        table.addCell(cell);
        cell = new PdfPCell(new Paragraph(("").equals(accountName)?accountNo:accountName,headFont2));
        cell.setBorder(0);
        cell.setVerticalAlignment(Element.ALIGN_MIDDLE);
        table.addCell(cell);
        cell = new PdfPCell(new Paragraph("",headFont2));  //סլ�������
        cell.setBorder(0);
        cell.setHorizontalAlignment(Element.ALIGN_CENTER);
        table.addCell(cell);
        cell = new PdfPCell(new Paragraph(houseArea,headFont2));
        cell.setBorder(0);
        cell.setVerticalAlignment(Element.ALIGN_MIDDLE);
        cell.setFixedHeight(31f);
        table.addCell(cell);
        document.add(table);

        //�ڶ���
        float[] widths2 = { 51f, 272f, 40f, 70f };
        table.deleteBodyRows();
        table.setWidths(widths2);
        table.getDefaultCell().setBorder(0);//���ñ��Ĭ��Ϊ�ޱ߿�
        cell = new PdfPCell(new Paragraph("",headFont2));  //סլ��ַ
        cell.setBorder(0);
        cell.setHorizontalAlignment(Element.ALIGN_CENTER);
        table.addCell(cell);
        cell = new PdfPCell(new Paragraph(address,headFont2));
        cell.setBorder(0);
        cell.setVerticalAlignment(Element.ALIGN_MIDDLE);
        table.addCell(cell);
        cell = new PdfPCell(new Paragraph("",headFont2));  //�绰
        cell.setBorder(0);
        cell.setHorizontalAlignment(Element.ALIGN_CENTER);
        table.addCell(cell);
        cell = new PdfPCell(new Paragraph(phoneNo,headFont2));
        cell.setBorder(0);
        cell.setVerticalAlignment(Element.ALIGN_MIDDLE);
        cell.setFixedHeight(31f);
        table.addCell(cell);
        document.add(table);

        //������
        float[] widths3 = { 51f, 140f, 110f, 132f };
        table.deleteBodyRows();
        table.setWidths(widths3);
        table.getDefaultCell().setBorder(0);//���ñ��Ĭ��Ϊ�ޱ߿�
        cell = new PdfPCell(new Paragraph("",headFont2));  //��Ʒסլ
        cell.setBorder(0);
        cell.setHorizontalAlignment(Element.ALIGN_CENTER);
        table.addCell(cell);
        cell = new PdfPCell(new Paragraph(houseType,headFont2));
        cell.setBorder(0);
        cell.setHorizontalAlignment(Element.ALIGN_RIGHT);
        cell.setVerticalAlignment(Element.ALIGN_MIDDLE);
        table.addCell(cell);
        cell = new PdfPCell(new Paragraph("",headFont2));     //���۹���
        cell.setBorder(0);
        cell.setHorizontalAlignment(Element.ALIGN_CENTER);
        table.addCell(cell);
        cell = new PdfPCell(new Paragraph("",headFont2));
        cell.setBorder(0);
        cell.setVerticalAlignment(Element.ALIGN_MIDDLE);
        cell.setFixedHeight(28f);
        table.addCell(cell);
        document.add(table);

        //������
        float[] widths4 = { 51f,64f,30f,96f,72f,30f,90f };
        PdfPTable table4 = new PdfPTable(widths4);
        table4.getDefaultCell().setBorder(0);//���ñ��Ĭ��Ϊ�ޱ߿�
        cell = new PdfPCell(new Paragraph("",headFont2));
        cell.setBorder(0);
        cell.setHorizontalAlignment(Element.ALIGN_CENTER);
        table4.addCell(cell);
        cell = new PdfPCell(new Paragraph("",headFont2));
        cell.setBorder(0);
        cell.setHorizontalAlignment(Element.ALIGN_CENTER);
        table4.addCell(cell);
        cell = new PdfPCell(new Paragraph(houseArea,headFont2));
        cell.setBorder(0);
        cell.setVerticalAlignment(Element.ALIGN_MIDDLE);
        table4.addCell(cell);
        cell = new PdfPCell(new Paragraph("",headFont2));
        cell.setBorder(0);
        cell.setHorizontalAlignment(Element.ALIGN_CENTER);
        table4.addCell(cell);
        cell = new PdfPCell(new Paragraph("",headFont2));
        cell.setBorder(0);
        cell.setHorizontalAlignment(Element.ALIGN_CENTER);
        table4.addCell(cell);
        cell = new PdfPCell(new Paragraph("",headFont2));
        cell.setBorder(0);
        cell.setVerticalAlignment(Element.ALIGN_MIDDLE);
        table4.addCell(cell);
        cell = new PdfPCell(new Paragraph("",headFont2));
        cell.setBorder(0);
        cell.setHorizontalAlignment(Element.ALIGN_CENTER);
        cell.setFixedHeight(24f);
        table4.addCell(cell);
        document.add(table4);

        //������
        float[] widths5 = { 51f,43f,79f,30f,46f,55f,77f,52f };
        PdfPTable table5 = new PdfPTable(widths5);
        table5.getDefaultCell().setBorder(0);//���ñ��Ĭ��Ϊ�ޱ߿�
        cell = new PdfPCell(new Paragraph("",headFont2));
        cell.setBorder(0);
        cell.setHorizontalAlignment(Element.ALIGN_CENTER);
        table5.addCell(cell);
        cell = new PdfPCell(new Paragraph("",headFont2));
        cell.setBorder(0);
        cell.setHorizontalAlignment(Element.ALIGN_CENTER);
        table5.addCell(cell);
        cell = new PdfPCell(new Paragraph(projAmt,headFont2));
        cell.setBorder(0);
        cell.setVerticalAlignment(Element.ALIGN_MIDDLE);
        table5.addCell(cell);
        cell = new PdfPCell(new Paragraph("",headFont2));
        cell.setBorder(0);
        cell.setHorizontalAlignment(Element.ALIGN_CENTER);
        table5.addCell(cell);
        cell = new PdfPCell(new Paragraph(payPart,headFont2));
        cell.setBorder(0);
        cell.setVerticalAlignment(Element.ALIGN_MIDDLE);
        table5.addCell(cell);
        cell = new PdfPCell(new Paragraph("",headFont2));
        cell.setBorder(0);
        cell.setVerticalAlignment(Element.ALIGN_MIDDLE);
        table5.addCell(cell);
        cell = new PdfPCell(new Paragraph("",headFont2));
        cell.setBorder(0);
        cell.setHorizontalAlignment(Element.ALIGN_CENTER);
        table5.addCell(cell);
        cell = new PdfPCell(new Paragraph("",headFont2));
        cell.setBorder(0);
        cell.setVerticalAlignment(Element.ALIGN_MIDDLE);
        cell.setFixedHeight(24f);
        table5.addCell(cell);
        document.add(table5);

        //������
        float[] widths6 = { 51f, 30f,30f,30f,30f,30f,30f,30f,30f,30f,30f,30f, 52f };
        PdfPTable table6 = new PdfPTable(widths6);
        table6.getDefaultCell().setBorder(0);
        cell = new PdfPCell(new Paragraph("",headFont2));     //������
        cell.setBorder(0);
        table6.addCell(cell);
        cell = new PdfPCell(new Paragraph(mapNum.get("9").toString(),headFont2));
        cell.setVerticalAlignment(Element.ALIGN_MIDDLE);
        cell.setBorder(0);
        table6.addCell(cell);
        cell = new PdfPCell(new Paragraph(mapNum.get("8").toString(),headFont2));
        cell.setVerticalAlignment(Element.ALIGN_MIDDLE);
        cell.setBorder(0);
        table6.addCell(cell);
        cell = new PdfPCell(new Paragraph(mapNum.get("7").toString(),headFont2));
        cell.setVerticalAlignment(Element.ALIGN_MIDDLE);
        cell.setBorder(0);
        table6.addCell(cell);
        cell = new PdfPCell(new Paragraph(mapNum.get("6").toString(),headFont2));
        cell.setVerticalAlignment(Element.ALIGN_MIDDLE);
        cell.setBorder(0);
        table6.addCell(cell);
        cell = new PdfPCell(new Paragraph(mapNum.get("5").toString(),headFont2));
        cell.setVerticalAlignment(Element.ALIGN_MIDDLE);
        cell.setBorder(0);
        table6.addCell(cell);
        cell = new PdfPCell(new Paragraph(mapNum.get("4").toString(),headFont2));
        cell.setVerticalAlignment(Element.ALIGN_MIDDLE);
        cell.setBorder(0);
        table6.addCell(cell);
        cell = new PdfPCell(new Paragraph(mapNum.get("3").toString(),headFont2));
        cell.setVerticalAlignment(Element.ALIGN_MIDDLE);
        cell.setBorder(0);
        table6.addCell(cell);
        cell = new PdfPCell(new Paragraph(mapNum.get("2").toString(),headFont2));
        cell.setVerticalAlignment(Element.ALIGN_MIDDLE);
        cell.setBorder(0);
        table6.addCell(cell);
        cell = new PdfPCell(new Paragraph(mapNum.get("1").toString(),headFont2));
        cell.setVerticalAlignment(Element.ALIGN_MIDDLE);
        cell.setBorder(0);
        table6.addCell(cell);
        cell = new PdfPCell(new Paragraph(mapNum.get("0").toString(),headFont2));
        cell.setVerticalAlignment(Element.ALIGN_MIDDLE);
        cell.setBorder(0);
        table6.addCell(cell);
        table6.addCell("");
        cell = new PdfPCell(new Paragraph(txAmt,headFont2));
        cell.setVerticalAlignment(Element.ALIGN_MIDDLE);
        cell.setBorder(0);
        cell.setFixedHeight(37f);
        table6.addCell(cell);
        document.add(table6);


        //������
        float[] widths7 = { 72f, 99f,36f,75f,28f,123f };
        PdfPTable table7 = new PdfPTable(widths7);
        table7.getDefaultCell().setBorder(0);
        cell = new PdfPCell(new Paragraph("",headFont2));      //�տλ
        cell.setBorder(0);
        table7.addCell(cell);
        cell = new PdfPCell(new Paragraph(bkDeptName,headFont2));
        cell.setVerticalAlignment(Element.ALIGN_MIDDLE);
        cell.setBorder(0);
        table7.addCell(cell);
        cell = new PdfPCell(new Paragraph("",headFont2));        //��Ʊ��
        cell.setBorder(0);
        table7.addCell(cell);
        cell = new PdfPCell(new Paragraph("",headFont2));
        cell.setBorder(0);
        table7.addCell(cell);
        cell = new PdfPCell(new Paragraph("",headFont2));    //����
        cell.setBorder(0);
        table7.addCell(cell);
        cell = new PdfPCell(new Paragraph("",headFont2));
        cell.setBorder(0);
        table7.addCell(cell);
        cell.setFixedHeight(32f);
        table7.addCell(cell);
        document.add(table7);
    }

    private PdfPTable transToPdfTable(HmMsgIn msgIn, HmActFund actFund) throws IOException, DocumentException {
        PdfPTable table = new PdfPTable(new float[]{900f});// ����һ��pdf���
        ExternalContext extContext = FacesContext.getCurrentInstance().getExternalContext();
        HttpSession session = (HttpSession) extContext.getSession(true);
        OperatorManager om = (OperatorManager) session.getAttribute(SystemAttributeNames.USER_INFO_NAME);

        table.setSpacingBefore(130f);// ���ñ������հ׿��
        table.setTotalWidth(835);// ���ñ��Ŀ��
        table.setLockedWidth(false);// ���ñ��Ŀ�ȹ̶�
        table.setSpacingAfter(120f);
        table.getDefaultCell().setBorder(0);//���ñ��Ĭ��Ϊ�ޱ߿�
        BaseFont bfChinese = BaseFont.createFont("c:\\windows\\fonts\\simsun.ttc,1", BaseFont.IDENTITY_H, BaseFont.EMBEDDED);
        Font headFont1 = new Font(bfChinese, 14, Font.BOLD);// ���������С
        Font headFont2 = new Font(bfChinese, 10, Font.NORMAL);// ���������С
        PdfPCell cell = new PdfPCell(new Paragraph("", headFont1));
        cell.setBorder(0);
        cell.setFixedHeight(40);//��Ԫ��߶�
        cell.setHorizontalAlignment(Element.ALIGN_CENTER);// ��������ˮƽ������ʾ
        cell.setVerticalAlignment(Element.ALIGN_MIDDLE);
        table.addCell(cell);
        String projAmt = "";
        String payPart = "";

        String accountName = actFund.getInfoName() == null ? "" : actFund.getInfoName();   //21
        String txAmt = String.format("%.2f", msgIn.getTxnAmt1());
        String address = msgIn.getInfoAddr();    //22
        String houseArea = StringUtils.isEmpty(msgIn.getBuilderArea()) ? "" : msgIn.getBuilderArea();
        // TODO �Թ�����
        String houseType = "1".equals(actFund.getHouseDepType()) ? "��" : "*";
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
        String accountNo = msgIn.getFundActno1();  // ҵ�����㻧�˺�(ά���ʽ��˺�)
        // ��������
        String bkDeptName = om.getOperator().getPtDeptBean().getDeptname();

        String[] singleRows = new String[]{
//                "�ʽ��˺ţ�" + accountNo,
//                "ҵ��������" + accountName,
//                "�ɿ��" + txAmt,
//                "��ַ��" + address,
//                "���������" + houseArea,
//                "�������ͣ���Ʒ����" + houseType,
//                "�绰��" + phoneNo,
//                "������ۣ�" + projAmt,
//                "�ɴ������" + payPart,
//                "Ӫҵ���㣺" + bkDeptName
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
            throw new RuntimeException("��������");
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
            throw new RuntimeException("��������");
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

    private Map transToRMB(String strAmt){
        //TODO
        char[] digit={'��','Ҽ','��','��','��','��','½','��','��','��'};
        char[] unit={'0','1','2','3','4','5','6','7','8','9'};
        Map mapNum = new HashMap();
        double  value = Double.valueOf(strAmt);
        //ת��������
        long midVal = (long)(value*100);
        //ת�����ַ���
        String valStr=String.valueOf(midVal);
        //ת��������
        char[] chDig = valStr.toCharArray();
        int lenchDig = chDig.length;
        for(int i = 0;i<10;i++){
            String strName = "";
            String strValue = "";
            strName = String.valueOf(unit[i]);
            int j = lenchDig-i-1;
            if (j>=0){
                strValue = String.valueOf(digit[chDig[j]-'0']);
            } else{
                strValue= "";
            }
            mapNum.put(strName,strValue);
        }
        return mapNum;
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
