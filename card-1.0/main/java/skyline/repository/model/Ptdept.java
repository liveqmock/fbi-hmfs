package skyline.repository.model;

import java.math.BigDecimal;
import java.util.Date;

public class Ptdept {
    /**
     * This field was generated by MyBatis Generator.
     * This field corresponds to the database column PTDEPT.DEPTID
     *
     * @mbggenerated Fri Jul 22 13:16:43 CST 2011
     */
    private String deptid;

    /**
     * This field was generated by MyBatis Generator.
     * This field corresponds to the database column PTDEPT.DEPTNAME
     *
     * @mbggenerated Fri Jul 22 13:16:43 CST 2011
     */
    private String deptname;

    /**
     * This field was generated by MyBatis Generator.
     * This field corresponds to the database column PTDEPT.DEPTDESC
     *
     * @mbggenerated Fri Jul 22 13:16:43 CST 2011
     */
    private String deptdesc;

    /**
     * This field was generated by MyBatis Generator.
     * This field corresponds to the database column PTDEPT.PARENTDEPTID
     *
     * @mbggenerated Fri Jul 22 13:16:43 CST 2011
     */
    private String parentdeptid;

    /**
     * This field was generated by MyBatis Generator.
     * This field corresponds to the database column PTDEPT.DEPTLEAF
     *
     * @mbggenerated Fri Jul 22 13:16:43 CST 2011
     */
    private String deptleaf;

    /**
     * This field was generated by MyBatis Generator.
     * This field corresponds to the database column PTDEPT.DEPTLEVEL
     *
     * @mbggenerated Fri Jul 22 13:16:43 CST 2011
     */
    private Short deptlevel;

    /**
     * This field was generated by MyBatis Generator.
     * This field corresponds to the database column PTDEPT.DEPTSTATUS
     *
     * @mbggenerated Fri Jul 22 13:16:43 CST 2011
     */
    private String deptstatus;

    /**
     * This field was generated by MyBatis Generator.
     * This field corresponds to the database column PTDEPT.ISDUMMY
     *
     * @mbggenerated Fri Jul 22 13:16:43 CST 2011
     */
    private String isdummy;

    /**
     * This field was generated by MyBatis Generator.
     * This field corresponds to the database column PTDEPT.FILLSTR10
     *
     * @mbggenerated Fri Jul 22 13:16:43 CST 2011
     */
    private String fillstr10;

    /**
     * This field was generated by MyBatis Generator.
     * This field corresponds to the database column PTDEPT.FILLSTR20
     *
     * @mbggenerated Fri Jul 22 13:16:43 CST 2011
     */
    private String fillstr20;

    /**
     * This field was generated by MyBatis Generator.
     * This field corresponds to the database column PTDEPT.FILLSTR100
     *
     * @mbggenerated Fri Jul 22 13:16:43 CST 2011
     */
    private String fillstr100;

    /**
     * This field was generated by MyBatis Generator.
     * This field corresponds to the database column PTDEPT.FILLSTR150
     *
     * @mbggenerated Fri Jul 22 13:16:43 CST 2011
     */
    private String fillstr150;

    /**
     * This field was generated by MyBatis Generator.
     * This field corresponds to the database column PTDEPT.FILLINT4
     *
     * @mbggenerated Fri Jul 22 13:16:43 CST 2011
     */
    private Short fillint4;

    /**
     * This field was generated by MyBatis Generator.
     * This field corresponds to the database column PTDEPT.FILLINT6
     *
     * @mbggenerated Fri Jul 22 13:16:43 CST 2011
     */
    private Integer fillint6;

    /**
     * This field was generated by MyBatis Generator.
     * This field corresponds to the database column PTDEPT.FILLINT8
     *
     * @mbggenerated Fri Jul 22 13:16:43 CST 2011
     */
    private Integer fillint8;

    /**
     * This field was generated by MyBatis Generator.
     * This field corresponds to the database column PTDEPT.FILLDBL2
     *
     * @mbggenerated Fri Jul 22 13:16:43 CST 2011
     */
    private BigDecimal filldbl2;

    /**
     * This field was generated by MyBatis Generator.
     * This field corresponds to the database column PTDEPT.FILLDBL22
     *
     * @mbggenerated Fri Jul 22 13:16:43 CST 2011
     */
    private BigDecimal filldbl22;

    /**
     * This field was generated by MyBatis Generator.
     * This field corresponds to the database column PTDEPT.FILLDBL4
     *
     * @mbggenerated Fri Jul 22 13:16:43 CST 2011
     */
    private BigDecimal filldbl4;

    /**
     * This field was generated by MyBatis Generator.
     * This field corresponds to the database column PTDEPT.FILLDATE1
     *
     * @mbggenerated Fri Jul 22 13:16:43 CST 2011
     */
    private Date filldate1;

    /**
     * This field was generated by MyBatis Generator.
     * This field corresponds to the database column PTDEPT.FILLDATE2
     *
     * @mbggenerated Fri Jul 22 13:16:43 CST 2011
     */
    private Date filldate2;

    /**
     * This field was generated by MyBatis Generator.
     * This field corresponds to the database column PTDEPT.DQDM
     *
     * @mbggenerated Fri Jul 22 13:16:43 CST 2011
     */
    private String dqdm;

    /**
     * This field was generated by MyBatis Generator.
     * This field corresponds to the database column PTDEPT.MKDM
     *
     * @mbggenerated Fri Jul 22 13:16:43 CST 2011
     */
    private String mkdm;

    /**
     * This field was generated by MyBatis Generator.
     * This field corresponds to the database column PTDEPT.DEPTINDEX
     *
     * @mbggenerated Fri Jul 22 13:16:43 CST 2011
     */
    private BigDecimal deptindex;

    /**
     * This field was generated by MyBatis Generator.
     * This field corresponds to the database column PTDEPT.DEPTGUID
     *
     * @mbggenerated Fri Jul 22 13:16:43 CST 2011
     */
    private String deptguid;

    /**
     * This method was generated by MyBatis Generator.
     * This method returns the value of the database column PTDEPT.DEPTID
     *
     * @return the value of PTDEPT.DEPTID
     *
     * @mbggenerated Fri Jul 22 13:16:43 CST 2011
     */
    public String getDeptid() {
        return deptid;
    }

    /**
     * This method was generated by MyBatis Generator.
     * This method sets the value of the database column PTDEPT.DEPTID
     *
     * @param deptid the value for PTDEPT.DEPTID
     *
     * @mbggenerated Fri Jul 22 13:16:43 CST 2011
     */
    public void setDeptid(String deptid) {
        this.deptid = deptid == null ? null : deptid.trim();
    }

    /**
     * This method was generated by MyBatis Generator.
     * This method returns the value of the database column PTDEPT.DEPTNAME
     *
     * @return the value of PTDEPT.DEPTNAME
     *
     * @mbggenerated Fri Jul 22 13:16:43 CST 2011
     */
    public String getDeptname() {
        return deptname;
    }

    /**
     * This method was generated by MyBatis Generator.
     * This method sets the value of the database column PTDEPT.DEPTNAME
     *
     * @param deptname the value for PTDEPT.DEPTNAME
     *
     * @mbggenerated Fri Jul 22 13:16:43 CST 2011
     */
    public void setDeptname(String deptname) {
        this.deptname = deptname == null ? null : deptname.trim();
    }

    /**
     * This method was generated by MyBatis Generator.
     * This method returns the value of the database column PTDEPT.DEPTDESC
     *
     * @return the value of PTDEPT.DEPTDESC
     *
     * @mbggenerated Fri Jul 22 13:16:43 CST 2011
     */
    public String getDeptdesc() {
        return deptdesc;
    }

    /**
     * This method was generated by MyBatis Generator.
     * This method sets the value of the database column PTDEPT.DEPTDESC
     *
     * @param deptdesc the value for PTDEPT.DEPTDESC
     *
     * @mbggenerated Fri Jul 22 13:16:43 CST 2011
     */
    public void setDeptdesc(String deptdesc) {
        this.deptdesc = deptdesc == null ? null : deptdesc.trim();
    }

    /**
     * This method was generated by MyBatis Generator.
     * This method returns the value of the database column PTDEPT.PARENTDEPTID
     *
     * @return the value of PTDEPT.PARENTDEPTID
     *
     * @mbggenerated Fri Jul 22 13:16:43 CST 2011
     */
    public String getParentdeptid() {
        return parentdeptid;
    }

    /**
     * This method was generated by MyBatis Generator.
     * This method sets the value of the database column PTDEPT.PARENTDEPTID
     *
     * @param parentdeptid the value for PTDEPT.PARENTDEPTID
     *
     * @mbggenerated Fri Jul 22 13:16:43 CST 2011
     */
    public void setParentdeptid(String parentdeptid) {
        this.parentdeptid = parentdeptid == null ? null : parentdeptid.trim();
    }

    /**
     * This method was generated by MyBatis Generator.
     * This method returns the value of the database column PTDEPT.DEPTLEAF
     *
     * @return the value of PTDEPT.DEPTLEAF
     *
     * @mbggenerated Fri Jul 22 13:16:43 CST 2011
     */
    public String getDeptleaf() {
        return deptleaf;
    }

    /**
     * This method was generated by MyBatis Generator.
     * This method sets the value of the database column PTDEPT.DEPTLEAF
     *
     * @param deptleaf the value for PTDEPT.DEPTLEAF
     *
     * @mbggenerated Fri Jul 22 13:16:43 CST 2011
     */
    public void setDeptleaf(String deptleaf) {
        this.deptleaf = deptleaf == null ? null : deptleaf.trim();
    }

    /**
     * This method was generated by MyBatis Generator.
     * This method returns the value of the database column PTDEPT.DEPTLEVEL
     *
     * @return the value of PTDEPT.DEPTLEVEL
     *
     * @mbggenerated Fri Jul 22 13:16:43 CST 2011
     */
    public Short getDeptlevel() {
        return deptlevel;
    }

    /**
     * This method was generated by MyBatis Generator.
     * This method sets the value of the database column PTDEPT.DEPTLEVEL
     *
     * @param deptlevel the value for PTDEPT.DEPTLEVEL
     *
     * @mbggenerated Fri Jul 22 13:16:43 CST 2011
     */
    public void setDeptlevel(Short deptlevel) {
        this.deptlevel = deptlevel;
    }

    /**
     * This method was generated by MyBatis Generator.
     * This method returns the value of the database column PTDEPT.DEPTSTATUS
     *
     * @return the value of PTDEPT.DEPTSTATUS
     *
     * @mbggenerated Fri Jul 22 13:16:43 CST 2011
     */
    public String getDeptstatus() {
        return deptstatus;
    }

    /**
     * This method was generated by MyBatis Generator.
     * This method sets the value of the database column PTDEPT.DEPTSTATUS
     *
     * @param deptstatus the value for PTDEPT.DEPTSTATUS
     *
     * @mbggenerated Fri Jul 22 13:16:43 CST 2011
     */
    public void setDeptstatus(String deptstatus) {
        this.deptstatus = deptstatus == null ? null : deptstatus.trim();
    }

    /**
     * This method was generated by MyBatis Generator.
     * This method returns the value of the database column PTDEPT.ISDUMMY
     *
     * @return the value of PTDEPT.ISDUMMY
     *
     * @mbggenerated Fri Jul 22 13:16:43 CST 2011
     */
    public String getIsdummy() {
        return isdummy;
    }

    /**
     * This method was generated by MyBatis Generator.
     * This method sets the value of the database column PTDEPT.ISDUMMY
     *
     * @param isdummy the value for PTDEPT.ISDUMMY
     *
     * @mbggenerated Fri Jul 22 13:16:43 CST 2011
     */
    public void setIsdummy(String isdummy) {
        this.isdummy = isdummy == null ? null : isdummy.trim();
    }

    /**
     * This method was generated by MyBatis Generator.
     * This method returns the value of the database column PTDEPT.FILLSTR10
     *
     * @return the value of PTDEPT.FILLSTR10
     *
     * @mbggenerated Fri Jul 22 13:16:43 CST 2011
     */
    public String getFillstr10() {
        return fillstr10;
    }

    /**
     * This method was generated by MyBatis Generator.
     * This method sets the value of the database column PTDEPT.FILLSTR10
     *
     * @param fillstr10 the value for PTDEPT.FILLSTR10
     *
     * @mbggenerated Fri Jul 22 13:16:43 CST 2011
     */
    public void setFillstr10(String fillstr10) {
        this.fillstr10 = fillstr10 == null ? null : fillstr10.trim();
    }

    /**
     * This method was generated by MyBatis Generator.
     * This method returns the value of the database column PTDEPT.FILLSTR20
     *
     * @return the value of PTDEPT.FILLSTR20
     *
     * @mbggenerated Fri Jul 22 13:16:43 CST 2011
     */
    public String getFillstr20() {
        return fillstr20;
    }

    /**
     * This method was generated by MyBatis Generator.
     * This method sets the value of the database column PTDEPT.FILLSTR20
     *
     * @param fillstr20 the value for PTDEPT.FILLSTR20
     *
     * @mbggenerated Fri Jul 22 13:16:43 CST 2011
     */
    public void setFillstr20(String fillstr20) {
        this.fillstr20 = fillstr20 == null ? null : fillstr20.trim();
    }

    /**
     * This method was generated by MyBatis Generator.
     * This method returns the value of the database column PTDEPT.FILLSTR100
     *
     * @return the value of PTDEPT.FILLSTR100
     *
     * @mbggenerated Fri Jul 22 13:16:43 CST 2011
     */
    public String getFillstr100() {
        return fillstr100;
    }

    /**
     * This method was generated by MyBatis Generator.
     * This method sets the value of the database column PTDEPT.FILLSTR100
     *
     * @param fillstr100 the value for PTDEPT.FILLSTR100
     *
     * @mbggenerated Fri Jul 22 13:16:43 CST 2011
     */
    public void setFillstr100(String fillstr100) {
        this.fillstr100 = fillstr100 == null ? null : fillstr100.trim();
    }

    /**
     * This method was generated by MyBatis Generator.
     * This method returns the value of the database column PTDEPT.FILLSTR150
     *
     * @return the value of PTDEPT.FILLSTR150
     *
     * @mbggenerated Fri Jul 22 13:16:43 CST 2011
     */
    public String getFillstr150() {
        return fillstr150;
    }

    /**
     * This method was generated by MyBatis Generator.
     * This method sets the value of the database column PTDEPT.FILLSTR150
     *
     * @param fillstr150 the value for PTDEPT.FILLSTR150
     *
     * @mbggenerated Fri Jul 22 13:16:43 CST 2011
     */
    public void setFillstr150(String fillstr150) {
        this.fillstr150 = fillstr150 == null ? null : fillstr150.trim();
    }

    /**
     * This method was generated by MyBatis Generator.
     * This method returns the value of the database column PTDEPT.FILLINT4
     *
     * @return the value of PTDEPT.FILLINT4
     *
     * @mbggenerated Fri Jul 22 13:16:43 CST 2011
     */
    public Short getFillint4() {
        return fillint4;
    }

    /**
     * This method was generated by MyBatis Generator.
     * This method sets the value of the database column PTDEPT.FILLINT4
     *
     * @param fillint4 the value for PTDEPT.FILLINT4
     *
     * @mbggenerated Fri Jul 22 13:16:43 CST 2011
     */
    public void setFillint4(Short fillint4) {
        this.fillint4 = fillint4;
    }

    /**
     * This method was generated by MyBatis Generator.
     * This method returns the value of the database column PTDEPT.FILLINT6
     *
     * @return the value of PTDEPT.FILLINT6
     *
     * @mbggenerated Fri Jul 22 13:16:43 CST 2011
     */
    public Integer getFillint6() {
        return fillint6;
    }

    /**
     * This method was generated by MyBatis Generator.
     * This method sets the value of the database column PTDEPT.FILLINT6
     *
     * @param fillint6 the value for PTDEPT.FILLINT6
     *
     * @mbggenerated Fri Jul 22 13:16:43 CST 2011
     */
    public void setFillint6(Integer fillint6) {
        this.fillint6 = fillint6;
    }

    /**
     * This method was generated by MyBatis Generator.
     * This method returns the value of the database column PTDEPT.FILLINT8
     *
     * @return the value of PTDEPT.FILLINT8
     *
     * @mbggenerated Fri Jul 22 13:16:43 CST 2011
     */
    public Integer getFillint8() {
        return fillint8;
    }

    /**
     * This method was generated by MyBatis Generator.
     * This method sets the value of the database column PTDEPT.FILLINT8
     *
     * @param fillint8 the value for PTDEPT.FILLINT8
     *
     * @mbggenerated Fri Jul 22 13:16:43 CST 2011
     */
    public void setFillint8(Integer fillint8) {
        this.fillint8 = fillint8;
    }

    /**
     * This method was generated by MyBatis Generator.
     * This method returns the value of the database column PTDEPT.FILLDBL2
     *
     * @return the value of PTDEPT.FILLDBL2
     *
     * @mbggenerated Fri Jul 22 13:16:43 CST 2011
     */
    public BigDecimal getFilldbl2() {
        return filldbl2;
    }

    /**
     * This method was generated by MyBatis Generator.
     * This method sets the value of the database column PTDEPT.FILLDBL2
     *
     * @param filldbl2 the value for PTDEPT.FILLDBL2
     *
     * @mbggenerated Fri Jul 22 13:16:43 CST 2011
     */
    public void setFilldbl2(BigDecimal filldbl2) {
        this.filldbl2 = filldbl2;
    }

    /**
     * This method was generated by MyBatis Generator.
     * This method returns the value of the database column PTDEPT.FILLDBL22
     *
     * @return the value of PTDEPT.FILLDBL22
     *
     * @mbggenerated Fri Jul 22 13:16:43 CST 2011
     */
    public BigDecimal getFilldbl22() {
        return filldbl22;
    }

    /**
     * This method was generated by MyBatis Generator.
     * This method sets the value of the database column PTDEPT.FILLDBL22
     *
     * @param filldbl22 the value for PTDEPT.FILLDBL22
     *
     * @mbggenerated Fri Jul 22 13:16:43 CST 2011
     */
    public void setFilldbl22(BigDecimal filldbl22) {
        this.filldbl22 = filldbl22;
    }

    /**
     * This method was generated by MyBatis Generator.
     * This method returns the value of the database column PTDEPT.FILLDBL4
     *
     * @return the value of PTDEPT.FILLDBL4
     *
     * @mbggenerated Fri Jul 22 13:16:43 CST 2011
     */
    public BigDecimal getFilldbl4() {
        return filldbl4;
    }

    /**
     * This method was generated by MyBatis Generator.
     * This method sets the value of the database column PTDEPT.FILLDBL4
     *
     * @param filldbl4 the value for PTDEPT.FILLDBL4
     *
     * @mbggenerated Fri Jul 22 13:16:43 CST 2011
     */
    public void setFilldbl4(BigDecimal filldbl4) {
        this.filldbl4 = filldbl4;
    }

    /**
     * This method was generated by MyBatis Generator.
     * This method returns the value of the database column PTDEPT.FILLDATE1
     *
     * @return the value of PTDEPT.FILLDATE1
     *
     * @mbggenerated Fri Jul 22 13:16:43 CST 2011
     */
    public Date getFilldate1() {
        return filldate1;
    }

    /**
     * This method was generated by MyBatis Generator.
     * This method sets the value of the database column PTDEPT.FILLDATE1
     *
     * @param filldate1 the value for PTDEPT.FILLDATE1
     *
     * @mbggenerated Fri Jul 22 13:16:43 CST 2011
     */
    public void setFilldate1(Date filldate1) {
        this.filldate1 = filldate1;
    }

    /**
     * This method was generated by MyBatis Generator.
     * This method returns the value of the database column PTDEPT.FILLDATE2
     *
     * @return the value of PTDEPT.FILLDATE2
     *
     * @mbggenerated Fri Jul 22 13:16:43 CST 2011
     */
    public Date getFilldate2() {
        return filldate2;
    }

    /**
     * This method was generated by MyBatis Generator.
     * This method sets the value of the database column PTDEPT.FILLDATE2
     *
     * @param filldate2 the value for PTDEPT.FILLDATE2
     *
     * @mbggenerated Fri Jul 22 13:16:43 CST 2011
     */
    public void setFilldate2(Date filldate2) {
        this.filldate2 = filldate2;
    }

    /**
     * This method was generated by MyBatis Generator.
     * This method returns the value of the database column PTDEPT.DQDM
     *
     * @return the value of PTDEPT.DQDM
     *
     * @mbggenerated Fri Jul 22 13:16:43 CST 2011
     */
    public String getDqdm() {
        return dqdm;
    }

    /**
     * This method was generated by MyBatis Generator.
     * This method sets the value of the database column PTDEPT.DQDM
     *
     * @param dqdm the value for PTDEPT.DQDM
     *
     * @mbggenerated Fri Jul 22 13:16:43 CST 2011
     */
    public void setDqdm(String dqdm) {
        this.dqdm = dqdm == null ? null : dqdm.trim();
    }

    /**
     * This method was generated by MyBatis Generator.
     * This method returns the value of the database column PTDEPT.MKDM
     *
     * @return the value of PTDEPT.MKDM
     *
     * @mbggenerated Fri Jul 22 13:16:43 CST 2011
     */
    public String getMkdm() {
        return mkdm;
    }

    /**
     * This method was generated by MyBatis Generator.
     * This method sets the value of the database column PTDEPT.MKDM
     *
     * @param mkdm the value for PTDEPT.MKDM
     *
     * @mbggenerated Fri Jul 22 13:16:43 CST 2011
     */
    public void setMkdm(String mkdm) {
        this.mkdm = mkdm == null ? null : mkdm.trim();
    }

    /**
     * This method was generated by MyBatis Generator.
     * This method returns the value of the database column PTDEPT.DEPTINDEX
     *
     * @return the value of PTDEPT.DEPTINDEX
     *
     * @mbggenerated Fri Jul 22 13:16:43 CST 2011
     */
    public BigDecimal getDeptindex() {
        return deptindex;
    }

    /**
     * This method was generated by MyBatis Generator.
     * This method sets the value of the database column PTDEPT.DEPTINDEX
     *
     * @param deptindex the value for PTDEPT.DEPTINDEX
     *
     * @mbggenerated Fri Jul 22 13:16:43 CST 2011
     */
    public void setDeptindex(BigDecimal deptindex) {
        this.deptindex = deptindex;
    }

    /**
     * This method was generated by MyBatis Generator.
     * This method returns the value of the database column PTDEPT.DEPTGUID
     *
     * @return the value of PTDEPT.DEPTGUID
     *
     * @mbggenerated Fri Jul 22 13:16:43 CST 2011
     */
    public String getDeptguid() {
        return deptguid;
    }

    /**
     * This method was generated by MyBatis Generator.
     * This method sets the value of the database column PTDEPT.DEPTGUID
     *
     * @param deptguid the value for PTDEPT.DEPTGUID
     *
     * @mbggenerated Fri Jul 22 13:16:43 CST 2011
     */
    public void setDeptguid(String deptguid) {
        this.deptguid = deptguid == null ? null : deptguid.trim();
    }
}