package hmfs.common.util;

import common.repository.hmfs.model.HmTxnVch;
import common.repository.hmfs.model.HmVchJrnl;
import common.repository.hmfs.model.hmfs.HmFundTxnVO;
import common.repository.hmfs.model.hmfs.HmVchTxnVO;
import net.sf.jxls.transformer.XLSTransformer;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import pub.platform.advance.utils.PropertyManager;

import javax.faces.context.FacesContext;
import javax.servlet.ServletOutputStream;
import javax.servlet.http.HttpServletResponse;
import java.io.BufferedInputStream;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * EXCEL输出.
 * User: zhanrui
 * Date: 11-9-29
 * Time: 下午2:48
 * To change this template use File | Settings | File Templates.
 */
public class JxlsManager {
    private static final Logger logger = LoggerFactory.getLogger(JxlsManager.class);

    public String exportIndiviFundTxnList(String filename, List<HmFundTxnVO> records) {
        try {
            Map beansMap = new HashMap();
            beansMap.put("records", records);

            String reportPath = PropertyManager.getProperty("prj_root_dir");
            String templateFileName = reportPath + "/report/indiviFundTxn.xls";

            outputExcel(beansMap, templateFileName, filename);
        } catch (Exception e) {
            logger.error("报表处理错误！", e);
            throw new RuntimeException("报表处理错误！", e);
        }
        return null;
    }

    public String exportTxnvchList(String filename, List<HmTxnVch> records) {
        try {
            Map beansMap = new HashMap();
            beansMap.put("records", records);

            String reportPath = PropertyManager.getProperty("prj_root_dir");
            String templateFileName = reportPath + "/report/txnvchList.xls";

            outputExcel(beansMap, templateFileName, filename);
        } catch (Exception e) {
            logger.error("报表处理错误！", e);
            throw new RuntimeException("报表处理错误！", e);
        }
        return null;
    }

    private void outputExcel(Map beansMap, String templateFileName, String excelFilename) throws IOException {
        ServletOutputStream os = null;
        InputStream is = null;
        try {
            XLSTransformer transformer = new XLSTransformer();
            is = new BufferedInputStream(new FileInputStream(templateFileName));
            HSSFWorkbook wb = transformer.transformXLS(is, beansMap);
            HttpServletResponse response = (HttpServletResponse) FacesContext.getCurrentInstance().getExternalContext().getResponse();
            os = response.getOutputStream();
            response.reset();
            response.setHeader("Content-disposition", "attachment; filename=" + java.net.URLEncoder.encode(excelFilename, "UTF-8"));
            response.setContentType("application/msexcel");
            wb.write(os);
        } finally {
            if (os != null) {
                os.flush();
                os.close();
            }
            if (is != null) {
                is.close();
            }
        }
    }
    public String exportVchRecvList(String filename, List<HmVchJrnl> records,String vchNo) {
        try {
            Map beansMap = new HashMap();
            beansMap.put("records", records);
            beansMap.put("vchNo", vchNo);

            String reportPath = PropertyManager.getProperty("prj_root_dir");
            String templateFileName = reportPath + "/report/vchRecvList.xls";

            outputExcel(beansMap, templateFileName, filename);
        } catch (Exception e) {
            logger.error("报表处理错误！", e);
            throw new RuntimeException("报表处理错误！", e);
        }
        return null;
    }

    public String exportVchInList(String filename, List<HmVchJrnl> records) {
        try {
            Map beansMap = new HashMap();
            beansMap.put("records", records);

            String reportPath = PropertyManager.getProperty("prj_root_dir");
            String templateFileName = reportPath + "/report/vchInList.xls";

            outputExcel(beansMap, templateFileName, filename);
        } catch (Exception e) {
            logger.error("报表处理错误！", e);
            throw new RuntimeException("报表处理错误！", e);
        }
        return null;
    }

    public String exportTxnvchAcctList(String filename, List<HmVchTxnVO> records) {
        try {
            Map beansMap = new HashMap();
            beansMap.put("records", records);

            String reportPath = PropertyManager.getProperty("prj_root_dir");
            String templateFileName = reportPath + "/report/txnvchAcctList.xls";

            outputExcel(beansMap, templateFileName, filename);
        } catch (Exception e) {
            logger.error("报表处理错误！", e);
            throw new RuntimeException("报表处理错误！", e);
        }
        return null;
    }
}
