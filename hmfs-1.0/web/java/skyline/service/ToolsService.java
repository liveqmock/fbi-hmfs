package skyline.service;

import common.repository.hmfs.dao.hmfs.HmCmnMapper;
import skyline.repository.model.Ptenudetail;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.faces.model.SelectItem;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by IntelliJ IDEA.
 * User: zhanrui
 * Date: 11-4-22
 * Time: ����2:32
 * To change this template use File | Settings | File Templates.
 */
@Service
public class ToolsService {

    @Autowired
    PlatformService platformService;

    @Autowired
    private HmCmnMapper hmCmnMapper;
    /**
     * ����ö�ٱ�������������˵�
     *
     * @param enuName     ö������
     * @param isSelectAll �Ƿ����ȫ����ѡ��
     * @param isExpandID  true:�����б�������ID�� false���б��а���ID
     * @return �����˵�
     */
    public List<SelectItem> getEnuSelectItemList(String enuName, boolean isSelectAll, boolean isExpandID) {
        List<Ptenudetail> records = platformService.selectEnuDetail(enuName);
        List<SelectItem> items = new ArrayList<SelectItem>();
        SelectItem item;
        if (isSelectAll) {
            item = new SelectItem("", "ȫ��");
            items.add(item);
        }
        for (Ptenudetail record : records) {
            if (isExpandID) {
                item = new SelectItem(record.getEnuitemvalue(), record.getEnuitemvalue() + " " + record.getEnuitemlabel());
            } else {
                item = new SelectItem(record.getEnuitemvalue(), record.getEnuitemlabel());
            }
            items.add(item);
        }
        return items;
    }

    /**
     * ��ȡ�������������л����嵥
     *
     * @param branchId ��������
     * @return ����ѡ���б�
     */
    public List<SelectItem> selectBranchList(String branchId) {
        List<String> records = selectBranchLevelString(branchId);
        List<SelectItem> selectItems = new ArrayList<SelectItem>();
        for (String record : records) {
            String[] recordArray = record.split("\\|");
            SelectItem item = new SelectItem(recordArray[0], recordArray[1]);
            selectItems.add(item);
        }
        return selectItems;
    }
    //���� ������|��������(ǰ���ȫ�ǿո�)
    private List<String> selectBranchLevelString(String branchid){
        return hmCmnMapper.selectBranchLevelString(branchid);
    }
}
