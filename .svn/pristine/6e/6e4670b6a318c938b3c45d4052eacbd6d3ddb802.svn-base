/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package executables;

import dataaccess.MysqlConnector;
import dataaccess.OracleConnector;
import static executables.test.conMarksys;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.net.Inet4Address;
import java.net.InetAddress;
import java.net.NetworkInterface;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.text.DecimalFormat;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.Map;
import java.util.Properties;
import java.util.TreeMap;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.mail.Message;
import javax.mail.Multipart;
import javax.mail.Session;
import javax.mail.Transport;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeBodyPart;
import javax.mail.internet.MimeMessage;
import javax.mail.internet.MimeMultipart;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;

/**
 *
 * @author pabodhar
 */
public class CRMPointsUpdateWithPOPR {

//    OracleConnector conCrm = null;
//    MysqlConnector conInsurance = new MysqlConnector("10.10.10.10", "marksys", "crm", "crm@ail#2012");
    MysqlConnector conInsurance;
    OracleConnector conMarksys = null;
    OracleConnector conMarksys1 = null;
    Exception _exception = null;
    String strDocCode = "";
    int strDocNo = 0;
    String strSysDate = "";
    String strSysDate1 = "";
    String _strIpAddress = "";

    public void updatePoints() {
        //String sbuCode, String paraCode, String strLoyType
        String sbuCode = "830";
        String paraCode = "POSACCDOC";
        String strLoyType = "110";
        Statement stmnt = null;
        ResultSet rs = null;
        Statement stmntup = null;
        Statement stmntsel = null;
        ResultSet rssel = null;
        int m_intDocSeq = 0;
        int m_intexces_points = 0;
        int m_intamt = 0;
        int m_intrec_seq_no = 0;
        try {
//            conCrm = new OracleConnector("192.168.1.27", "rpd2", "CRM", new jText.TextUti().getText("crm"));
            conMarksys = new OracleConnector("192.168.1.27", "rpd2", "MARKSYS", new jText.TextUti().getText("oracle"));
            if (startTransactionMarksys() == 0) {
                throw new Exception(_exception.getLocalizedMessage());
            }
            String sql = "SELECT NVL(char_val,'NA') doc_code,to_char(sysdate,'DD-Mon-YYYY') upday,to_char(sysdate-1,'DD-Mon-YYYY') upday1 "
                    + " FROM crm.CRM_SYSPARAMS  "
                    + " WHERE sbu_code='" + sbuCode + "' AND para_code='" + paraCode + "'";
            stmnt = conMarksys.getConn().createStatement(ResultSet.TYPE_SCROLL_SENSITIVE, ResultSet.CONCUR_READ_ONLY);
            rs = stmnt.executeQuery(sql);
            if (rs.next()) {
                strDocCode = rs.getString("doc_code");
                strSysDate = rs.getString("upday") == null ? "" : rs.getString("upday");
                strSysDate1 = rs.getString("upday1") == null ? "" : rs.getString("upday1");
            } else {
                throw new Exception("Parameter is not set for para code CRM.");
            }
            rs.close();
            rs = null;
            String strSetrial = "select SERIAL_GEN_F,SERIAL_NO,SERIAL_START,SERIAL_END "
                    + " from crm.crm_doc_codes where sbu_code='" + sbuCode + "' and doc_code='" + strDocCode + "'  for update";
            rs = stmnt.executeQuery(strSetrial);
            if (rs.next()) {
                strDocNo = Integer.parseInt(rs.getString("SERIAL_NO"));
                if (rs.getString("SERIAL_GEN_F").equals("F")) {
                    System.out.println("Document is not set to auto serial generation type: " + strDocCode);
                    throw new Exception("Document is not set to auto serial generation type: " + strDocCode);
                }
                if (strDocNo > Integer.parseInt(rs.getString("SERIAL_END"))) {
                    System.out.println("Document Serial No has reached the max no for: " + strDocCode);
                    throw new Exception("Document Serial No has reached the max no for: " + strDocCode);
                }
                if (strDocNo < Integer.parseInt(rs.getString("SERIAL_START"))) {
                    System.out.println("The Start Document serial is lower than the current serial no for: " + strDocCode);
                    throw new Exception("The Start Document serial is lower than the current serial no for: " + strDocCode);
                }
                strDocNo = strDocNo + 1;
                String strDocUp = "update crm.crm_doc_codes set SERIAL_NO='" + strDocNo + "'"
                        + " where sbu_code='" + sbuCode + "' and doc_code='" + strDocCode + "' ";
                stmntup = conMarksys.getConn().createStatement();
                if (stmntup.executeUpdate(strDocUp) <= 0) {
                    abortTransactionMarksys();
                    System.out.println("Serial Number Generation Failed.");
                    throw new Exception("Serial Number Generation Failed.");
                }
                if (endTransactionMarksys() == 0) {
                    throw new Exception(_exception.getLocalizedMessage());
                }
                if (!strSysDate.equals("")) {
                    if (startTransactionMarksys() == 0) {
                        throw new Exception(_exception.getLocalizedMessage());
                    }
                    String strPoints = "SELECT SBU_CODE,LOC_CODE,MACH_CODE,RECEIPTNO,to_char(to_date(TXN_DATE),'DD-Mon-YYYY') TXN_DATE,"
                            + " LOYALTY_TYPE,USER_ID,SUBSTR(MEM_ID,4,10) MEM_ID,POINTS,REMARKS,DOC_CODE,DOC_NO,PROMO_POINTS,"
                            + " nvl(BNKCOD,'') as bnkcod,nvl(BNK_POINTS,'') as bnk_points,nvl(BNK_RATIO,'') as bnk_ratio,SUBSTR(cust_sup_id,4,10) cust_sup_id,nvl(ex_points,0)ex_points "
                            + " FROM crm.CRM_POS_LOYALTY_TXN  "
                            + " WHERE SBU_CODE='" + sbuCode + "' "
                            + " AND TXN_DATE BETWEEN to_date(sysdate)-1  and to_date(sysdate) "
                            + " AND LOYALTY_TYPE='" + strLoyType + "' AND UPD_FLG='F' AND POINTS IS NOT NULL "
                            + " FOR UPDATE";
                    stmntsel = conMarksys.getConn().createStatement(ResultSet.TYPE_SCROLL_SENSITIVE, ResultSet.CONCUR_READ_ONLY);
                    rssel = stmntsel.executeQuery(strPoints);
                    if (!rssel.next()) {
                        System.out.println("Data not Found in CRM_POS_LOYALTY_TXN for: " + strSysDate1 + " and " + strSysDate);
                        throw new Exception("Data not Found in CRM_POS_LOYALTY_TXN.");
                    }
                    rssel.beforeFirst();
                    while (rssel.next()) {
//                        if (startTransaction() == 0) {
//                            throw new Exception(_exception.getLocalizedMessage());
//                        }
                        if (insertTxnSummary(rssel.getString("SBU_CODE"),
                                strDocCode, strDocNo, m_intDocSeq, rssel.getString("LOYALTY_TYPE"),
                                rssel.getString("MEM_ID"), rssel.getString("TXN_DATE"),
                                Double.parseDouble(rssel.getString("POINTS")),
                                m_intexces_points, m_intamt, rssel.getString("LOC_CODE"),
                                rssel.getString("MACH_CODE"), rssel.getString("RECEIPTNO"),
                                m_intrec_seq_no, rssel.getString("USER_ID"), "SYS",
                                Double.parseDouble(rssel.getString("PROMO_POINTS")), rssel.getString("bnkcod"),
                                rssel.getString("bnk_points"), rssel.getString("bnk_ratio"),
                                rssel.getString("cust_sup_id"), rssel.getString("ex_points")) == 0) {
                            System.out.println("Data not inserted to crm_txn_summary for Customer: " + rssel.getString("MEM_ID") + "-" + rssel.getString("LOC_CODE") + "-" + rssel.getString("MACH_CODE")
                                    + "-" + rssel.getString("RECEIPTNO") + "-" + rssel.getString("USER_ID") + "-" + rssel.getString("TXN_DATE"));
                            abortTransactionMarksys();
                            throw new Exception("Error on inserting to Txn Summary.");
//                            continue;
                        }
                        if (updateCustLoyalPoints(rssel.getString("SBU_CODE"), rssel.getString("MEM_ID"),
                                Double.parseDouble(rssel.getString("POINTS")), Double.parseDouble(rssel.getString("PROMO_POINTS")),
                                rssel.getString("TXN_DATE"), rssel.getString("LOYALTY_TYPE")) == 0) {
                            System.out.println("Error on update in Loyalty Master for Customer: " + rssel.getString("MEM_ID") + "-" + rssel.getString("LOC_CODE") + "-" + rssel.getString("MACH_CODE")
                                    + "-" + rssel.getString("RECEIPTNO") + "-" + rssel.getString("USER_ID") + "-" + rssel.getString("TXN_DATE"));
                            abortTransactionMarksys();
                            throw new Exception("Error on update in Loyalty Master.");
//                            abortTransaction();
//                            continue;
                        }
                        if (updatePOSLoyaltyTxn(rssel.getString("SBU_CODE"), rssel.getString("LOC_CODE"),
                                rssel.getString("MACH_CODE"), rssel.getString("RECEIPTNO"), rssel.getString("TXN_DATE"),
                                rssel.getString("LOYALTY_TYPE"), rssel.getString("USER_ID"), rssel.getString("MEM_ID")) == 0) {
                            System.out.println("Error on update in POS Loyalty Txn for Customer: " + rssel.getString("MEM_ID") + "-" + rssel.getString("LOC_CODE") + "-" + rssel.getString("MACH_CODE")
                                    + "-" + rssel.getString("RECEIPTNO") + "-" + rssel.getString("USER_ID") + "-" + rssel.getString("TXN_DATE"));
                            abortTransactionMarksys();
                            throw new Exception("Error on update in POS Loyalty Txn.");
//                            abortTransaction();
//                            continue;
                        }
//                        if (endTransaction() == 0) {
//                            throw new Exception(_exception.getLocalizedMessage());
//                        }
                        m_intDocSeq = m_intDocSeq + 1;
                    }
                    if (endTransactionMarksys() == 0) {
                        throw new Exception(_exception.getLocalizedMessage());
                    }
                    System.out.println("Points Updated Successfully...for: " + strSysDate1 + " and " + strSysDate);
                }

            } else {
                System.out.println("Serial no not found.");
                throw new Exception("Serial no not found.");
            }

            updateInvalidApps(sbuCode, strSysDate1, strSysDate);
            GetIP();
        } catch (Exception e) {
            e.printStackTrace();
//            abortTransaction();
        } finally {
            try {
                if (rs != null) {
                    rs.close();
                    rs = null;
                }
                if (rssel != null) {
                    rssel.close();
                    rssel = null;
                }
                if (stmnt != null) {
                    stmnt.close();
                    stmnt = null;
                }
                if (stmntup != null) {
                    stmntup.close();
                    stmntup = null;
                }
                if (stmntsel != null) {
                    stmntsel.close();
                    stmntsel = null;
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    private void GetIP() {
        try {
            if (System.getProperty("os.name").toLowerCase().equalsIgnoreCase("linux")) {
                Enumeration<NetworkInterface> interfaces = NetworkInterface.getNetworkInterfaces();
                while (interfaces.hasMoreElements()) {
                    NetworkInterface nif = interfaces.nextElement();
                    Enumeration<InetAddress> inetAddresses = nif.getInetAddresses();
                    while (inetAddresses.hasMoreElements()) {
                        InetAddress inetAddr = inetAddresses.nextElement();
                        if (inetAddr instanceof Inet4Address) {
                            String address = inetAddr.getCanonicalHostName().trim();
                            if (address.trim().substring(0, 3).equals("192")) {
                                _strIpAddress = address;
                            }
                        }
                    }
                }
            } else {
                _strIpAddress = InetAddress.getLocalHost().getHostAddress();
            }
        } catch (Exception e) {
        }
    }

    public int insertTxnSummary(String prm_strSBUCode, String prm_strDocCode, int prm_intDocNo,
            int prm_intSeqNo, String prm_strLoyType, String prm_strMemID, String prm_strTxnDate,
            Double prm_dblPoints, int prm_intExcesPoints, int prm_intamt, String prm_strLocCode,
            String prm_strMachCode, String prm_strReceiptNo, int prm_intrecSeqNo, String prm_strUserID,
            String prm_strcreby, Double prm_dblPromoPoints, String prm_strBankCode, String prm_strBank_Points, String prm_strRatio,
            String prm_strSuplimId, String prm_strPurchasePoints) {
        Statement _stmt = null;
        Statement _stmtin = null;
        ResultSet _rs = null;
        try {
            String m_strSQL = "select cust_id from crm.CRM_CUST_MAST where sbu_code='" + prm_strSBUCode + "' and cust_id='" + prm_strMemID + "'";
            _stmt = conMarksys.getConn().createStatement();
            _rs = _stmt.executeQuery(m_strSQL);
            if (!_rs.next()) {
                throw new Exception("Can't find Customer");
            }
            _stmtin = conMarksys.getConn().createStatement();
            prm_strBankCode = prm_strBankCode == null ? "" : prm_strBankCode;
            prm_strBank_Points = prm_strBank_Points == null ? "" : prm_strBank_Points;
            prm_strRatio = prm_strRatio == null ? "" : prm_strRatio;
            prm_strPurchasePoints = prm_strPurchasePoints == null ? "0" : prm_strPurchasePoints;
            String _strSQL = "insert into crm.CRM_TXN_SUMMARY (SBU_CODE,DOC_CODE,DOC_NO,SEQ_NO,LOYAL_CODE,"
                    + " CUST_CODE,TXN_DATE,POINTS,EXCES_POINTS,AMT,LOC_CODE,MACH_CODE,"
                    + " RECEIPTNO,REC_SEQ_NO,USER_ID,CRE_BY,CRE_DATE,PROMO_POINTS,OLD_CARD_NO,BNKCOD,BNK_POINTS,BNK_RATIO,cust_sup_id,ex_points)"
                    + "  values ('" + prm_strSBUCode + "','" + prm_strDocCode + "','" + prm_intDocNo + "',"
                    + "'" + prm_intSeqNo + "','" + prm_strLoyType + "','" + prm_strMemID + "',"
                    + "'" + prm_strTxnDate + "','" + prm_dblPoints + "','" + prm_intExcesPoints + "',"
                    + "'" + prm_intamt + "','" + prm_strLocCode + "','" + prm_strMachCode + "',"
                    + "'" + prm_strReceiptNo + "','" + prm_intrecSeqNo + "','" + prm_strUserID + "',"
                    + "'" + prm_strcreby + "',to_char(sysdate,'DD-Mon-YYYY'),"
                    + "'" + prm_dblPromoPoints + "','" + prm_strMemID + "','" + prm_strBankCode + "',"
                    + "'" + prm_strBank_Points + "','" + prm_strRatio + "','" + prm_strSuplimId + "','" + prm_strPurchasePoints + "')";
            if (_stmtin.executeUpdate(_strSQL) <= 0) {
                throw new Exception("Error on inserting to Txn Summary");
            }
            return 1;
        } catch (Exception e) {
            e.printStackTrace();
            _exception = e;
            return 0;
        } finally {
            try {
                if (_rs != null) {
                    _rs.close();
                    _rs = null;
                }
                if (_stmt != null) {
                    _stmt.close();
                    _stmt = null;
                }
                if (_stmtin != null) {
                    _stmtin.close();
                    _stmtin = null;
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    public int updateCustLoyalPoints(String prm_strSBUCode, String prm_strCustCode, Double prm_strLoyalPoints,
            Double prm_strPromoPoints, String prm_strTxnDate, String prm_strLoyalType) {
        Statement _stmtin = null;
        try {
            _stmtin = conMarksys.getConn().createStatement();
            String _strSQL = "UPDATE crm.CRM_CUST_LOYALTY_MAST SET LOYAL_POINTS = NVL((LOYAL_POINTS+'" + prm_strLoyalPoints + "'),LOYAL_POINTS), "
                    + " CUMM_POINTS  = NVL((CUMM_POINTS+'" + prm_strLoyalPoints + "'),CUMM_POINTS), "
                    + " PROMO_POINTS  = NVL((PROMO_POINTS+'" + prm_strPromoPoints + "'),PROMO_POINTS), "
                    + " LAST_PUR_DATE= NVL(NVL(GREATEST(LAST_PUR_DATE,'" + prm_strTxnDate + "'),'" + prm_strTxnDate + "'),LAST_PUR_DATE) "
                    + " WHERE SBU_CODE   = '" + prm_strSBUCode + "' AND CUST_ID = '" + prm_strCustCode + "' AND LOYAL_TYPE = '" + prm_strLoyalType + "'";
            if (_stmtin.executeUpdate(_strSQL) <= 0) {
                throw new Exception("Error on update in Loyalty Master");
            }
            return 1;
        } catch (Exception e) {
            e.printStackTrace();
            _exception = e;
            return 0;
        } finally {
            try {
                if (_stmtin != null) {
                    _stmtin.close();
                    _stmtin = null;
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    public int updatePOSLoyaltyTxn(String prm_strSBUCode, String prm_strLocCode, String prm_strMachCode,
            String prm_strReceiptNo, String prm_strTxnDate, String prm_strLoyalType, String prm_strUserID, String prm_strMemID) {
        Statement _stmtin = null;
        try {
            _stmtin = conMarksys.getConn().createStatement();
            String _strSQL = "UPDATE crm.CRM_POS_LOYALTY_TXN SET UPD_FLG='T' "
                    + " WHERE  SBU_CODE =  '" + prm_strSBUCode + "' AND "
                    + " LOC_CODE  =  '" + prm_strLocCode + "' AND "
                    + " MACH_CODE =  '" + prm_strMachCode + "' AND "
                    + " RECEIPTNO = '" + prm_strReceiptNo + "' AND "
                    + " TXN_DATE  = '" + prm_strTxnDate + "' AND "
                    + " LOYALTY_TYPE =  '" + prm_strLoyalType + "' AND "
                    + " USER_ID =  '" + prm_strUserID + "' AND "
                    + " SUBSTR(MEM_ID,4,10)  = '" + prm_strMemID + "'";
            if (_stmtin.executeUpdate(_strSQL) <= 0) {
                throw new Exception("Error on update in POS Loyalty Txn.");
            }
            return 1;
        } catch (Exception e) {
            e.printStackTrace();
            _exception = e;
            return 0;
        } finally {
            try {
                if (_stmtin != null) {
                    _stmtin.close();
                    _stmtin = null;
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    public int startTransactionMarksys() {
        try {
            if (conMarksys.getConn().getAutoCommit() == false) {
                conMarksys.rollback();
                conMarksys.getConn().setAutoCommit(true);
            }
            conMarksys.getConn().setAutoCommit(false);
            return 1;
        } catch (Exception e) {
            e.printStackTrace();
            return 0;
        }
    }

    public int endTransactionMarksys() {
        try {
            conMarksys.getConn().commit();
            conMarksys.getConn().setAutoCommit(true);
            return 1;
        } catch (Exception e) {
            e.printStackTrace();
            return 0;
        }
    }

    public void abortTransactionMarksys() {
        try {
            if (conMarksys.getConn().getAutoCommit() == false) {
                conMarksys.getConn().rollback();
                conMarksys.getConn().setAutoCommit(true);
            }

        } catch (Exception e) {
            e.printStackTrace();

        }
    }

    public int startTransactionMySql() {
        try {
            if (conInsurance.getConn().getAutoCommit() == false) {
                conInsurance.rollback();
                conInsurance.getConn().setAutoCommit(true);
            }
            conInsurance.getConn().setAutoCommit(false);
            return 1;
        } catch (Exception e) {
            e.printStackTrace();
            return 0;
        }
    }

    public int endTransactionMySql() {
        try {
            conInsurance.getConn().commit();
            conInsurance.getConn().setAutoCommit(true);
            return 1;
        } catch (Exception e) {
            e.printStackTrace();
            return 0;
        }
    }

    public void abortTransactionMySql() {
        try {
            if (conInsurance.getConn().getAutoCommit() == false) {
                conInsurance.getConn().rollback();
                conInsurance.getConn().setAutoCommit(true);
            }

        } catch (Exception e) {
            e.printStackTrace();

        }
    }

    public void createInsurancePolicies() {
        boolean _bolsuccess = true;
        ResultSet m_rs = null;
        ResultSet m_rsdoc = null;
        Statement m_stmtOra = null;
        Statement m_stmtOra1 = null;
        Statement m_stmtOra2 = null;
        Statement m_stmtOra3 = null;
        Statement m_stmtup = null;
        Statement m_stmtInMysql = null;
        String m_strTxnDate = "";
        try {
            conInsurance = new MysqlConnector("10.10.10.10", "marksys", "crm", "crm@ail#2012");
            String m_strSBU = "830";
            String m_strLoc = "100";
            String m_strCRMDocCode = "INSP";
            String m_strInsDocCode = "CRMP";
            String m_strSeqOBj = "SQCRMP";
            int m_intSEQNo = 0;
            conMarksys = new OracleConnector("192.168.1.27", "rpd2", "MARKSYS", new jText.TextUti().getText("oracle"));
            m_stmtOra = conMarksys.getConn().createStatement(ResultSet.TYPE_SCROLL_INSENSITIVE, ResultSet.CONCUR_READ_ONLY);
            m_stmtOra1 = conMarksys.getConn().createStatement(ResultSet.TYPE_SCROLL_INSENSITIVE, ResultSet.CONCUR_READ_ONLY);
            m_stmtOra2 = conMarksys.getConn().createStatement(ResultSet.TYPE_SCROLL_INSENSITIVE, ResultSet.CONCUR_READ_ONLY);
            m_stmtOra3 = conMarksys.getConn().createStatement(ResultSet.TYPE_SCROLL_INSENSITIVE, ResultSet.CONCUR_READ_ONLY);
            m_stmtup = conMarksys.getConn().createStatement();
            m_stmtInMysql = conInsurance.getConn().createStatement();

            String m_strSQL = "select '450' as sbucod, 'HO' as loccod,"
                    + " case when (a.insnam is null or a.insnam='') then a.cust_app_name else a.insnam end as ppdnam,"
                    + "  '' ppdini,"
                    + " case when (a.insad1 is null or a.insad1='') then a.cust_pre_add1 else a.insad1 end as ppdad1,"
                    + " case when (a.insad2 is null or a.insad2='') then a.cust_pre_add2 else a.insad2 end as ppdad2, "
                    + " case when (a.insad3 is null or a.insad3='') then a.cust_pre_add3 else a.insad3 end as ppdad3, "
                    + " case when (a.inscty is null or a.inscty='') then a.cust_pre_city else a.inscty end as ppdad4, "
                    //                    + " case when (a.insdob is null or a.insdob='') then nvl(to_char(a.cust_dob,'YYYY-MM-DD'),'') "
                    + " case when (a.insdob is null or a.insdob='') then to_char(a.cust_dob,'YYYY-MM-DD') "
                    + " else to_char(a.insdob,'YYYY-MM-DD') end as ppddob,"
                    + " case when (a.insnic is null or a.insnic='') then nvl(a.cust_nic,'') else a.insnic end as ppdnic,"
                    + " case when (a.insmb1 is null or a.insmb1='') then nvl(a.cust_mob_no1,'') else a.insmb1 end||'/'"
                    + " ||case when (a.insmb2 is null or a.insmb2='') then nvl(a.cust_mob_no2,'') else a.insmb2 end||'/'"
                    + "  ||case when (a.instl1 is null or a.instl1='') then nvl(a.cust_tel_no1,'') else a.instl1 end||'/'"
                    + " ||case when (a.instl2 is null or a.instl2='') then nvl(a.cust_tel_no2,'') else a.instl2 end as ppdtel,"
                    + " case when (a.cust_email1 is not null or a.cust_email1<>'')  and (a.cust_email2 is null or a.cust_email2='')"
                    + "  then a.cust_email1 when (a.cust_email2 is not null or a.cust_email2<>'') "
                    + "  and (a.cust_email1 is null or a.cust_email1='') then a.cust_email2"
                    + " when (a.cust_email2 is not null or a.cust_email2<>'')  and (a.cust_email1 is not null or a.cust_email1<>'') then"
                    + " a.cust_email1||'/'||a.cust_email2  when (a.cust_email2 is null or a.cust_email2='') "
                    + "  and (a.cust_email1 is null or a.cust_email1='') then '' end as ppdeml,"
                    + " '' as pprnum,  '' as toptrm,  '' as paytrm,  '' as bassum, (b.points-(b.points*b.comisn/100))*-1 as premum,"
                    + " nvl(a.insnom,'') as nomnam, 'HO' as brncod, '' as advcod,  '' as polnum,  '" + m_strInsDocCode + "' as doccod,   'system' as creaby, "
                    + "  to_char(to_date(sysdate),'YYYY-MM-DD') as creadt, "
                    + " nvl(a.instyp,'1') as poltyp, '' as prdcod,  '' as ntitle,  '' as lockin,  "
                    + " '' as sinprm,  'L1' as pprsta, b.cust_code as cscode,"
                    + " '' as nomdob,  b.points*-1 as totprm,'' as sumrkm,  to_char(b.txn_date,'YYYY-MM-DD') as comdat, '' as expdat, "
                    + " '' as prpseq,   to_char(b.txn_date,'YYYY-MM-DD') as icpdat,  '' as poldat,   to_char(b.txn_date,'YYYY-MM-DD') as txndat, "
                    + " '' as taxamt,  '' as grsprm,b.doc_code,b.doc_no,to_char(to_date(b.txn_date),'DD-Mon-YYYY') as crmtxndate,"
                    + " doc.sqobnm as seqobj,to_char(txn_date,'YYYY-MM-DD HH24:MI:SS') as issueddatetime,b.insrnw,b.polyno "
                    + " from crm.crm_txn_summary b inner join crm.crm_cust_mast a"
                    + " on a.sbu_code=b.sbu_code and a.cust_id=b.cust_code"
                    + " inner join marksys.rms_doc_codes doc"
                    + " on b.sbu_code=doc.sbu_code and b.doc_code=doc.doc_code"
                    + " where b.sbu_code='" + m_strSBU + "'  and b.doc_code='" + m_strCRMDocCode + "'"
                    //  + " and b.txn_date =to_char(sysdate,'DD-Mon-YYYY') "
                    //  + " and b.txn_date between to_date(sysdate)-1  and to_date(sysdate)"
                    + " and a.cust_id not in  ('0000901365','0009999999','0000901389','0000901341','0000924470')"
                    + " and b.loc_code='" + m_strLoc + "'"
                    + " and (insupd is null or insupd='')"
                    + " and (b.insrnw is null or b.insrnw='')";
            m_rs = m_stmtOra3.executeQuery(m_strSQL);
            if (m_rs.next()) {
//                throw new Exception("Data not found...");
//            }
                m_rs.beforeFirst();
                String m_strin = "";
                String m_strup = "";
                String m_strppdini = "";
                String m_strppddob = "";
                String m_strppdnic = "";
                String m_strppdtel = "";
                String m_strppdeml = "";
                String m_strpprnum = "";
                String m_strtoptrm = "";
                String m_strpaytrm = "";
                String m_strbassum = "";
                String m_strnomnam = "";
                String m_stradvcod = "";
                String m_strpolnum = "";
                String m_strprdcod = "";
                String m_strntitle = "";
                String m_strsinprm = "";
                String m_strpprsta = "";
                String m_strnomdob = "";
                String m_strsumrkm = "";
                String m_strexpdat = "";
                String m_strprpseq = "";
                String m_strpoldat = "";
                String m_strtaxamt = "";
                String m_strgrsprm = "";
                String m_strRenewal = "";
                String m_strPolicyNo = "";
                while (m_rs.next()) {
                    if (startTransactionMarksys() == 0) {
                        _bolsuccess = false;
                        throw new Exception(_exception.getLocalizedMessage());
                    }
                    if (startTransactionMarksys() == 0) {
                        _bolsuccess = false;
                        throw new Exception(_exception.getLocalizedMessage());
                    }
                    String strSetrial = "select * "
                            + " from marksys.smsequence where sbucod='" + m_strSBU + "' and loccod='" + m_strLoc + "' "
                            + " and seqid='" + m_strSeqOBj + "' for update";
                    m_rsdoc = m_stmtOra1.executeQuery(strSetrial);
                    if (m_rsdoc.next()) {
                        m_intSEQNo = Integer.parseInt(m_rsdoc.getString("curv"));
                        if (m_intSEQNo > Integer.parseInt(m_rsdoc.getString("maxv"))) {
                            System.out.println("Document Serial No has reached the max no for: " + strDocCode);
                            _bolsuccess = false;
                            throw new Exception("Document Serial No has reached the max no for: " + strDocCode);
                        }
                        if (m_intSEQNo < Integer.parseInt(m_rsdoc.getString("minv"))) {
                            System.out.println("The Start Document serial is lower than the current serial no for: " + strDocCode);
                            _bolsuccess = false;
                            throw new Exception("The Start Document serial is lower than the current serial no for: " + strDocCode);
                        }
                        m_intSEQNo = m_intSEQNo + 1;
                    }
                    m_strin = "";
                    m_strppdini = "";
                    m_strppddob = "";
                    m_strppdnic = "";
                    m_strppdtel = "";
                    m_strppdeml = "";
                    m_strpprnum = "";
                    m_strtoptrm = "";
                    m_strpaytrm = "";
                    m_strbassum = "";
                    m_strnomnam = "";
                    m_stradvcod = "";
                    m_strpolnum = "";
                    m_strprdcod = "";
                    m_strntitle = "";
                    m_strsinprm = "";
                    m_strpprsta = "";
                    m_strnomdob = "";
                    m_strsumrkm = "";
                    m_strexpdat = "";
                    m_strprpseq = "";
                    m_strpoldat = "";
                    m_strtaxamt = "";
                    m_strgrsprm = "";
                    m_strRenewal = "";
                    m_strPolicyNo = "";

                    m_strppdini = m_rs.getString("ppdini") == null ? "" : m_rs.getString("ppdini");
                    m_strppddob = m_rs.getString("ppddob") == null ? null : m_rs.getString("ppddob");
                    m_strppdnic = m_rs.getString("ppdnic") == null ? "" : m_rs.getString("ppdnic");
                    m_strppdtel = m_rs.getString("ppdtel") == null ? "" : m_rs.getString("ppdtel");
                    m_strppdeml = m_rs.getString("ppdeml") == null ? "" : m_rs.getString("ppdeml");
                    m_strtoptrm = m_rs.getString("toptrm") == null ? "" : m_rs.getString("toptrm");
                    m_strpaytrm = m_rs.getString("paytrm") == null ? "" : m_rs.getString("paytrm");
                    m_strbassum = m_rs.getString("bassum") == null ? "" : m_rs.getString("bassum");
                    m_strnomnam = m_rs.getString("nomnam") == null ? "" : m_rs.getString("nomnam").replace("\\", "");
                    m_stradvcod = m_rs.getString("advcod") == null ? "" : m_rs.getString("advcod");
                    m_strpolnum = m_rs.getString("polnum") == null ? "" : m_rs.getString("polnum");
                    m_strprdcod = m_rs.getString("prdcod") == null ? "" : m_rs.getString("prdcod");
                    m_strntitle = m_rs.getString("ntitle") == null ? "" : m_rs.getString("ntitle");
                    m_strsinprm = m_rs.getString("sinprm") == null ? "" : m_rs.getString("sinprm");
                    m_strpprsta = m_rs.getString("pprsta") == null ? "" : m_rs.getString("pprsta");
//                m_strnomdob = m_rs.getString("nomdob");
                    m_strsumrkm = m_rs.getString("sumrkm") == null ? "" : m_rs.getString("sumrkm");
//                m_strexpdat = m_rs.getString("expdat");
                    m_strprpseq = m_rs.getString("prpseq") == null ? "" : m_rs.getString("prpseq");
//                m_strpoldat = m_rs.getString("poldat");
                    m_strtaxamt = m_rs.getString("taxamt") == null ? "" : m_rs.getString("taxamt");
                    m_strgrsprm = m_rs.getString("grsprm") == null ? "" : m_rs.getString("grsprm");
                    m_strTxnDate = m_rs.getString("crmtxndate");
                    m_strRenewal = m_rs.getString("insrnw") == null ? "" : m_rs.getString("insrnw");
                    m_strPolicyNo = m_rs.getString("polyno") == null ? "" : m_rs.getString("polyno");
                    if (startTransactionMySql() == 0) {
                        _bolsuccess = false;
                        throw new Exception(_exception.getLocalizedMessage());
                    }
                    if (m_rs.getString("ppddob") != null) {
                        m_strin = "insert into incrmpolicies (sbucod, loccod, ppdnam, ppdini, ppdad1, ppdad2,"
                                + " ppdad3, ppdad4, ppddob, ppdnic, ppdtel, ppdeml,"
                                + " pprnum, toptrm, paytrm, bassum, premum, nomnam,"
                                + " brncod, advcod, polnum, doccod, creaby, creadt, "
                                + "poltyp, prdcod, ntitle, lockin, sinprm, pprsta, "
                                + "cscode, totprm, sumrkm, comdat, "
                                + "prpseq, icpdat,  txndat, taxamt, grsprm,agrtim"
                                // + "nomdob,poldat,expdat"
                                + ") values("
                                + " '" + m_rs.getString("sbucod") + "','" + m_rs.getString("loccod") + "','" + m_rs.getString("ppdnam") + "','" + m_strppdini + "','" + m_rs.getString("ppdad1") + "','" + m_rs.getString("ppdad2") + "',"
                                + " '" + m_rs.getString("ppdad3") + "','" + m_rs.getString("ppdad4") + "','" + m_strppddob + "','" + m_strppdnic + "','" + m_strppdtel + "','" + m_strppdeml + "',"
                                + " '" + m_intSEQNo + "','" + m_strtoptrm + "','" + m_strpaytrm + "','" + m_strbassum + "','" + m_rs.getString("premum") + "','" + m_strnomnam + "',"
                                + " '" + m_rs.getString("brncod") + "','" + m_stradvcod + "','" + m_strpolnum + "','" + m_rs.getString("doccod") + "','" + m_rs.getString("creaby") + "','" + m_rs.getString("creadt") + "',"
                                + " '" + m_rs.getString("poltyp") + "','" + m_strprdcod + "','" + m_strntitle + "',now(),'" + m_strsinprm + "','" + m_strpprsta + "',"
                                + " '" + m_rs.getString("cscode") + "','" + m_rs.getString("totprm") + "','" + m_strsumrkm + "','" + m_rs.getString("comdat") + "',"
                                + " '" + m_strprpseq + "','" + m_rs.getString("icpdat") + "','" + m_rs.getString("txndat") + "','" + m_strtaxamt + "','" + m_strgrsprm + "',"
                                + " '" + m_rs.getString("issueddatetime") + "'"
                                + ")";
                    } else {
                        m_strin = "insert into incrmpolicies (sbucod, loccod, ppdnam, ppdini, ppdad1, ppdad2,"
                                + " ppdad3, ppdad4, ppdnic, ppdtel, ppdeml,"
                                + " pprnum, toptrm, paytrm, bassum, premum, nomnam,"
                                + " brncod, advcod, polnum, doccod, creaby, creadt, "
                                + "poltyp, prdcod, ntitle, lockin, sinprm, pprsta, "
                                + "cscode, totprm, sumrkm, comdat, "
                                + "prpseq, icpdat,  txndat, taxamt, grsprm,agrtim"
                                + ") values("
                                + " '" + m_rs.getString("sbucod") + "','" + m_rs.getString("loccod") + "','" + m_rs.getString("ppdnam") + "','" + m_strppdini + "','" + m_rs.getString("ppdad1") + "','" + m_rs.getString("ppdad2") + "',"
                                + " '" + m_rs.getString("ppdad3") + "','" + m_rs.getString("ppdad4") + "','" + m_strppdnic + "','" + m_strppdtel + "','" + m_strppdeml + "',"
                                + " '" + m_intSEQNo + "','" + m_strtoptrm + "','" + m_strpaytrm + "','" + m_strbassum + "','" + m_rs.getString("premum") + "','" + m_strnomnam + "',"
                                + " '" + m_rs.getString("brncod") + "','" + m_stradvcod + "','" + m_strpolnum + "','" + m_rs.getString("doccod") + "','" + m_rs.getString("creaby") + "','" + m_rs.getString("creadt") + "',"
                                + " '" + m_rs.getString("poltyp") + "','" + m_strprdcod + "','" + m_strntitle + "',now(),'" + m_strsinprm + "','" + m_strpprsta + "',"
                                + " '" + m_rs.getString("cscode") + "','" + m_rs.getString("totprm") + "','" + m_strsumrkm + "','" + m_rs.getString("comdat") + "',"
                                + " '" + m_strprpseq + "','" + m_rs.getString("icpdat") + "','" + m_rs.getString("txndat") + "','" + m_strtaxamt + "','" + m_strgrsprm + "',"
                                + " '" + m_rs.getString("issueddatetime") + "'"
                                + ")";
                    }

                    if (m_stmtInMysql.executeUpdate(m_strin) <= 0) {
                        _bolsuccess = false;
                        m_stmtInMysql.close();
                        m_stmtInMysql = null;
                        throw new Exception("Error occured in inserting data:");
                    }
                    m_strup = "";
                    m_strup = "update crm.crm_txn_summary set insupd='T'"
                            + " where sbu_code='" + m_strSBU + "' and doc_code='" + m_rs.getString("doc_code") + "' "
                            + " and to_date(txn_date)='" + m_rs.getString("crmtxndate") + "'"
                            + " and doc_no='" + m_rs.getString("doc_no") + "' and cust_code='" + m_rs.getString("cscode") + "'"
                            + " and loc_code='" + m_strLoc + "'";
                    if (m_stmtup.executeUpdate(m_strup) <= 0) {
                        _bolsuccess = false;
                        m_stmtup.close();
                        m_stmtup = null;
                        throw new Exception("Error occured in updating data:");
                    }
                    String strDocUp = "update marksys.smsequence set curv='" + m_intSEQNo + "'"
                            + " where sbucod='" + m_strSBU + "' and loccod='" + m_strLoc + "' and seqid='" + m_strSeqOBj + "' ";
                    if (m_stmtOra2.executeUpdate(strDocUp) <= 0) {
                        _bolsuccess = false;
                        m_stmtOra2.close();
                        m_stmtOra2 = null;
                        throw new Exception("Serial Number updation Failed.");
                    }
//                m_intSEQNo++;
                    if (endTransactionMySql() == 0) {
                        _bolsuccess = false;
                        throw new Exception(_exception.getLocalizedMessage());
                    }
                    if (endTransactionMarksys() == 0) {
                        _bolsuccess = false;
                        throw new Exception(_exception.getLocalizedMessage());
                    }
                    if (endTransactionMarksys() == 0) {
                        _bolsuccess = false;
                        throw new Exception(_exception.getLocalizedMessage());
                    }
                }
            } else {
                System.out.println("Data not found for new customers....");
            }
            String m_result = createRenewedPolicies(m_strSBU, m_strLoc, m_strInsDocCode, m_strCRMDocCode);
            if (!m_result.equals("")) {
                _bolsuccess = false;
                throw new Exception(_exception.getLocalizedMessage());
            }
            transferPoliciesIssuedAtSc();

        } catch (Exception e) {
            _bolsuccess = false;
            e.printStackTrace();
            abortTransactionMarksys();
            abortTransactionMySql();
            abortTransactionMarksys();
            _exception = e;
        } finally {
            try {
                startTransactionMarksys();
                if (!_bolsuccess) {
                    System.out.println("Policies Transfer Failed....:" + m_strTxnDate + _exception.getLocalizedMessage());
                    INSERT_CRM_CUSTTRAN_LOG(m_strTxnDate, "Failed " + _exception.getLocalizedMessage().substring(0, 140));
                } else {
                    System.out.println("Policies Transfer Completed Successfully....:" + m_strTxnDate);
                    INSERT_CRM_CUSTTRAN_LOG(m_strTxnDate, "Successful");
                }
                endTransactionMarksys();
                if (m_rs != null) {
                    m_rs.close();
                    m_rs = null;
                }
                if (m_rsdoc != null) {
                    m_rsdoc.close();
                    m_rsdoc = null;
                }
                if (m_stmtOra != null) {
                    m_stmtOra.close();
                    m_stmtOra = null;
                }
                if (m_stmtOra1 != null) {
                    m_stmtOra1.close();
                    m_stmtOra1 = null;
                }
                if (m_stmtOra2 != null) {
                    m_stmtOra2.close();
                    m_stmtOra2 = null;
                }
                if (m_stmtOra3 != null) {
                    m_stmtOra3.close();
                    m_stmtOra3 = null;
                }
                if (m_stmtup != null) {
                    m_stmtup.close();
                    m_stmtup = null;
                }
                if (m_stmtInMysql != null) {
                    m_stmtInMysql.close();
                    m_stmtInMysql = null;
                }
                closeConnection();
            } catch (Exception e) {
                e.printStackTrace();
                abortTransactionMarksys();
                closeConnection();
            }
        }
    }

    public String createRenewedPolicies(String prm_strSBU, String prm_strLoc, String prm_strInsDoc,
            String prm_strCrmDoc) {
        String m_strResult = "";
        ResultSet m_rsRenew = null;
        Statement m_stmtOraRenew = null;
        Statement m_stmtInMysql = null;
        Statement m_stmtSelMysql = null;
        Statement m_stmtup = null;
        try {

            m_stmtOraRenew = conMarksys.getConn().createStatement(ResultSet.TYPE_SCROLL_INSENSITIVE, ResultSet.CONCUR_READ_ONLY);
//            m_stmtOra3 = conMarksys.getConn().createStatement(ResultSet.TYPE_SCROLL_INSENSITIVE, ResultSet.CONCUR_READ_ONLY);
            m_stmtup = conMarksys.getConn().createStatement();
            m_stmtInMysql = conInsurance.getConn().createStatement();
            String m_strSQL = "select '450' as sbucod, 'HO' as loccod,"
                    + " case when (a.insnam is null or a.insnam='') then a.cust_app_name else a.insnam end as ppdnam,"
                    + "  '' ppdini,"
                    + " case when (a.insad1 is null or a.insad1='') then a.cust_pre_add1 else a.insad1 end as ppdad1,"
                    + " case when (a.insad2 is null or a.insad2='') then a.cust_pre_add2 else a.insad2 end as ppdad2, "
                    + " case when (a.insad3 is null or a.insad3='') then a.cust_pre_add3 else a.insad3 end as ppdad3, "
                    + " case when (a.inscty is null or a.inscty='') then a.cust_pre_city else a.inscty end as ppdad4, "
                    + " case when (a.insdob is null or a.insdob='') then to_char(a.cust_dob,'YYYY-MM-DD') "
                    + " else to_char(a.insdob,'YYYY-MM-DD') end as ppddob,"
                    + " case when (a.insnic is null or a.insnic='') then nvl(a.cust_nic,'') else a.insnic end as ppdnic,"
                    + " case when (a.insmb1 is null or a.insmb1='') then nvl(a.cust_mob_no1,'') else a.insmb1 end||'/'"
                    + " ||case when (a.insmb2 is null or a.insmb2='') then nvl(a.cust_mob_no2,'') else a.insmb2 end||'/'"
                    + "  ||case when (a.instl1 is null or a.instl1='') then nvl(a.cust_tel_no1,'') else a.instl1 end||'/'"
                    + " ||case when (a.instl2 is null or a.instl2='') then nvl(a.cust_tel_no2,'') else a.instl2 end as ppdtel,"
                    + " case when (a.cust_email1 is not null or a.cust_email1<>'')  and (a.cust_email2 is null or a.cust_email2='')"
                    + "  then a.cust_email1 when (a.cust_email2 is not null or a.cust_email2<>'') "
                    + "  and (a.cust_email1 is null or a.cust_email1='') then a.cust_email2"
                    + " when (a.cust_email2 is not null or a.cust_email2<>'')  and (a.cust_email1 is not null or a.cust_email1<>'') then"
                    + " a.cust_email1||'/'||a.cust_email2  when (a.cust_email2 is null or a.cust_email2='') "
                    + "  and (a.cust_email1 is null or a.cust_email1='') then '' end as ppdeml,"
                    + " '' as pprnum,  '' as toptrm,  '' as paytrm,  '' as bassum, (b.points-(b.points*b.comisn/100))*-1 as premum,"
                    + " nvl(a.insnom,'') as nomnam, 'HO' as brncod, '' as advcod,  '' as polnum,  '" + prm_strInsDoc + "' as doccod,   'system' as creaby, "
                    + "  to_char(to_date(sysdate),'YYYY-MM-DD') as creadt, "
                    + " nvl(a.instyp,'1') as poltyp, '' as prdcod,  '' as ntitle,  '' as lockin,  "
                    + " '' as sinprm,  'L1' as pprsta, b.cust_code as cscode,"
                    + " '' as nomdob,  b.points*-1 as totprm,'' as sumrkm,  to_char(b.txn_date,'YYYY-MM-DD') as comdat, '' as expdat, "
                    + " '' as prpseq,   to_char(b.txn_date,'YYYY-MM-DD') as icpdat,  '' as poldat,   to_char(b.txn_date,'YYYY-MM-DD') as txndat, "
                    + " '' as taxamt,  '' as grsprm,b.doc_code,b.doc_no,to_char(to_date(b.txn_date),'DD-Mon-YYYY') as crmtxndate,"
                    + " doc.sqobnm as seqobj,to_char(txn_date,'YYYY-MM-DD HH24:MI:SS') as issueddatetime,b.insrnw,nvl(b.polyno,'') as polyno "
                    + " from crm.crm_txn_summary b inner join crm.crm_cust_mast a"
                    + " on a.sbu_code=b.sbu_code and a.cust_id=b.cust_code"
                    + " inner join marksys.rms_doc_codes doc"
                    + " on b.sbu_code=doc.sbu_code and b.doc_code=doc.doc_code"
                    + " where b.sbu_code='" + prm_strSBU + "'  and b.doc_code='" + prm_strCrmDoc + "'"
                    + " and a.cust_id not in  ('0000901365','0009999999','0000901389','0000901341','0000924470')"
                    + " and b.loc_code='" + prm_strLoc + "'"
                    + " and (insupd is null or insupd='')"
                    + " and (b.insrnw is not null or b.insrnw<>'')";
            m_rsRenew = m_stmtOraRenew.executeQuery(m_strSQL);
            if (m_rsRenew.next()) {
                m_rsRenew.beforeFirst();
                int m_intprpseq = 0;
                String m_strin = "";
                while (m_rsRenew.next()) {
                    if (startTransactionMarksys() == 0) {
                        throw new Exception(_exception.getLocalizedMessage());
                    }
                    if (startTransactionMySql() == 0) {
                        throw new Exception(_exception.getLocalizedMessage());
                    }
                    if (m_rsRenew.getString("polyno") != null || !m_rsRenew.getString("polyno").equals("")) {
                        m_stmtSelMysql = conInsurance.getConn().createStatement();
                        String m_sql = "select max(prpseq) as maxprpseq from incrmpolicies"
                                + " where sbucod='" + m_rsRenew.getString("sbucod") + "'"
                                + " and polnum='" + m_rsRenew.getString("polyno") + "'"
                                + " and pprsta='PLISU'";
                        ResultSet m_rssel = m_stmtSelMysql.executeQuery(m_sql);
                        if (m_rssel.next()) {
                            m_intprpseq = Integer.parseInt(m_rssel.getString("maxprpseq"));
                            m_strin = "";
                            m_strin = "insert into incrmpolicies (sbucod, loccod, ppdnam, ppdini, ppdad1, ppdad2,"
                                    + " ppdad3, ppdad4, ppdnic, ppdtel, ppdeml,"
                                    + " pprnum,  premum, nomnam,"
                                    + " brncod,  polnum, doccod, creaby, creadt, "
                                    + " poltyp,  lockin,  pprsta, "
                                    + " cscode, totprm,  comdat, "
                                    + " prpseq, icpdat,  txndat, agrtim,ppddob,expdat)"
                                    + " select sbucod, loccod, ppdnam, ppdini, ppdad1, ppdad2,"
                                    + " ppdad3, ppdad4, ppdnic, ppdtel, ppdeml,"
                                    + " pprnum,'" + m_rsRenew.getString("premum") + "', nomnam,brncod,polnum,"
                                    + " '" + m_rsRenew.getString("doccod") + "', '" + m_rsRenew.getString("creaby") + "','" + m_rsRenew.getString("creadt") + "',"
                                    + " poltyp,now(),'L1','" + m_rsRenew.getString("cscode") + "', "
                                    + "'" + m_rsRenew.getString("totprm") + "','" + m_rsRenew.getString("comdat") + "',"
                                    + " " + m_intprpseq + "+1, "
                                    //                                    + "'" + m_rsRenew.getString("icpdat") + "', "
                                    //                                    + "'" + m_rsRenew.getString("txndat") + "' ,"
                                    + "date_add(expdat,interval 1 day), "
                                    + "date_add(expdat,interval 1 day),"
                                    + "'" + m_rsRenew.getString("issueddatetime") + "',ppddob,"
                                    + "date_add(expdat,interval 1 year) "
                                    + " from incrmpolicies"
                                    + " where sbucod='" + m_rsRenew.getString("sbucod") + "'"
                                    + " and polnum='" + m_rsRenew.getString("polyno") + "'"
                                    + " and prpseq='" + m_intprpseq + "'";
                        } else {
                            throw new Exception("Error occured in inserting data:Invalid Policy No:" + m_rsRenew.getString("polyno"));
                        }
                        if (m_stmtInMysql.executeUpdate(m_strin) <= 0) {
                            m_stmtInMysql.close();
                            m_stmtInMysql = null;
                            throw new Exception("Error occured in inserting data:");
                        }
                        m_strin = "";
                        m_strin = "update crm.crm_txn_summary set insupd='T'"
                                + " where sbu_code='" + prm_strSBU + "' and doc_code='" + m_rsRenew.getString("doc_code") + "' "
                                + " and to_date(txn_date)='" + m_rsRenew.getString("crmtxndate") + "'"
                                + " and doc_no='" + m_rsRenew.getString("doc_no") + "' and cust_code='" + m_rsRenew.getString("cscode") + "'"
                                + " and loc_code='" + prm_strLoc + "'";
                        if (m_stmtup.executeUpdate(m_strin) <= 0) {
                            m_stmtup.close();
                            m_stmtup = null;
                            throw new Exception("Error occured in updating data:");
                        }
                        if (m_rssel != null) {
                            m_rssel.close();
                            m_rssel = null;
                        }
                        if (m_stmtSelMysql != null) {
                            m_stmtSelMysql.close();
                            m_stmtSelMysql = null;
                        }
                        if (endTransactionMySql() == 0) {
                            throw new Exception(_exception.getLocalizedMessage());
                        }
                        if (endTransactionMarksys() == 0) {
                            throw new Exception(_exception.getLocalizedMessage());
                        }
                    }
                }
            } else {
                System.out.println("Renewed Customers not found.");
            }

        } catch (Exception e) {
            e.printStackTrace();
            _exception = e;
            m_strResult = "ERR:" + e.getLocalizedMessage();
            abortTransactionMarksys();
            abortTransactionMySql();
        } finally {
            try {
                if (m_rsRenew != null) {
                    m_rsRenew.close();
                    m_rsRenew = null;
                }
                if (m_stmtOraRenew != null) {
                    m_stmtOraRenew.close();
                    m_stmtOraRenew = null;
                }
                if (m_stmtup != null) {
                    m_stmtup.close();
                    m_stmtup = null;
                }
                if (m_stmtInMysql != null) {
                    m_stmtInMysql.close();
                    m_stmtInMysql = null;
                }
            } catch (Exception e) {
            }
        }
        return m_strResult;
    }

    private void closeConnection() {
        try {
            if (conInsurance != null) {
                conInsurance.getConn().close();
                conInsurance = null;
            }
//            if (conCrm != null) {
//                conCrm.getConn().close();
//                conCrm = null;
//            }
            if (conMarksys != null) {
                conMarksys.getConn().close();
                conMarksys = null;
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void INSERT_CRM_CUSTTRAN_LOG(String prm_strDate, String prm_strRemark) {
        try {
            Statement m_stmnt = conMarksys.getConn().createStatement();
            String m_strSQL = "INSERT INTO crm.CRM_CUSTTRAN_LOG "
                    + "(sbu_code,old_card_no,new_card_no,cre_by,cre_date,LOG_REM,doc_code) "
                    + "VALUES('830','0000000000','0000000000','system',sysdate,'INSURANCE POLICY TRANSFER:" + prm_strRemark + " :" + prm_strDate + "','INSU')";
            if (m_stmnt.executeUpdate(m_strSQL) <= 0) {
                m_stmnt.close();
                m_stmnt = null;
                throw new Exception(" Error occured in Inserting to CRM_CUSTTRAN_LOG");
            }
            m_stmnt.close();
            m_stmnt = null;
        } catch (Exception e) {
            e.printStackTrace();
            _exception = e;
        }
    }

    //transfer points of the invalid application ids to corresponding new card ids
    public int updateInvalidApps(String prm_strSBu, String prm_strfrmdt, String prm_strtodt) {
        ResultSet _rs3 = null;
        Statement _stmt6 = null;
        try {
            String m_strSQL = "select a.old_card_no,a.new_card_no,c.loyal_points, c.cumm_points,c.promo_points,c.last_pur_date"
                    + " from crm.crm_custtran_log a "
                    + " inner join crm.crm_cust_mast b on a.sbu_code=b.sbu_code and a.old_card_no=b.cust_id "
                    + " inner join crm.crm_cust_loyalty_mast c on b.sbu_code=c.sbu_code and a.old_card_no=c.cust_id "
                    + " where a.sbu_code='" + prm_strSBu + "'"
                    + " and a.cre_date between to_date('" + prm_strfrmdt + "','DD-Mon-YYYY') and to_date('" + prm_strtodt + "','DD-Mon-YYYY')"
                    + " and (a.log_rem='New Application Entry' or a.log_rem='RENEWAL' or a.log_rem='REPLACEMENT' or a.log_rem='Suplimentry Entry')"
                    + " and a.doc_num is not null and b.cust_stat='INVLD' and c.loyal_points<>0";
            _stmt6 = conMarksys.getConn().createStatement(ResultSet.TYPE_SCROLL_SENSITIVE, ResultSet.CONCUR_READ_ONLY);
            _rs3 = _stmt6.executeQuery(m_strSQL);
            if (!_rs3.next()) {
                throw new Exception("Details not found of Invalid Customers.");
            }
            _rs3.beforeFirst();
            while (_rs3.next()) {
                if (startTransactionMarksys() == 0) {
                    throw new Exception(_exception.getLocalizedMessage());
                }
                if (updateCustLoyalPoints(prm_strSBu, _rs3.getString("old_card_no"), _rs3.getString("new_card_no")) == 0) {
                    throw new Exception(_exception.getLocalizedMessage());
                }
                if (UpdateTxnSummarywithNewID(prm_strSBu, _rs3.getString("old_card_no"), _rs3.getString("new_card_no")) == 0) {
                    throw new Exception(_exception.getLocalizedMessage());
                }
                if (endTransactionMarksys() == 0) {
                    throw new Exception(_exception.getLocalizedMessage());
                }
            }
            return 1;
        } catch (Exception e) {
            e.printStackTrace();
            abortTransactionMarksys();
            _exception = e;
            return 0;
        } finally {
            try {
                if (_rs3 != null) {
                    _rs3.close();
                    _rs3 = null;
                }
                if (_stmt6 != null) {
                    _stmt6.close();
                    _stmt6 = null;
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    public int updateCustLoyalPoints(String prm_strSBU, String prm_oldcustID, String prm_newcustID) {
        Statement _stmt = null;
        ResultSet _rs = null;
        try {
            String m_strSQL = "";
            m_strSQL = "select LOYAL_POINTS,CUMM_POINTS,CUMM_REDEM,"
                    + " to_char(LAST_PUR_DATE,'DD-MON-YYYY') as LAST_PUR_DATE ,PROMO_POINTS"
                    + " from crm.CRM_CUST_LOYALTY_MAST"
                    + " WHERE SBU_CODE='" + prm_strSBU + "' AND CUST_ID='" + prm_oldcustID + "' AND LOYAL_TYPE='110'";
            _stmt = conMarksys.getConn().createStatement();
            _rs = _stmt.executeQuery(m_strSQL);
            if (!_rs.next()) {
                throw new Exception("No Data Found for this Customer " + prm_oldcustID);
            }
            String old_loyal = _rs.getString("LOYAL_POINTS");
            String old_cum = _rs.getString("CUMM_POINTS");
            String old_redim = _rs.getString("CUMM_REDEM");
            String old_promo = _rs.getString("PROMO_POINTS");
            String old_lstpur = _rs.getString("LAST_PUR_DATE") == null ? "" : _rs.getString("LAST_PUR_DATE");

            m_strSQL = "SELECT LOYAL_POINTS*-1 as LOLPOIN ,CUMM_POINTS*-1 as CUMPOIN,"
                    + " CUMM_REDEM*-1 as CUMRED, "
                    + " case when LAST_PUR_DATE is null then '' else to_char(LAST_PUR_DATE,'DD-MON-YYYY' ) end  as LSTPUR ,"
                    + " PROMO_POINTS*-1 as PROPOIN "
                    + " FROM crm.CRM_CUST_LOYALTY_MAST "
                    + " WHERE SBU_CODE='" + prm_strSBU + "' AND CUST_ID='" + prm_oldcustID + "' AND LOYAL_TYPE='110'";
            _stmt = conMarksys.getConn().createStatement();
            _rs = _stmt.executeQuery(m_strSQL);

            if (!_rs.next()) {
                throw new Exception("No Data Found for this Customer " + prm_oldcustID);
            }
            String lst_pur = _rs.getString("LSTPUR") == null ? "" : _rs.getString("LSTPUR");
            m_strSQL = "UPDATE crm.CRM_CUST_LOYALTY_MAST "
                    + " SET LOYAL_POINTS=(LOYAL_POINTS + " + _rs.getString("LOLPOIN") + ")"
                    + " ,CUMM_REDEM = CUMM_REDEM + " + _rs.getString("CUMRED") + " "
                    + " ,CUMM_POINTS = CUMM_POINTS + " + _rs.getString("CUMPOIN") + " "
                    + " ,LAST_PUR_DATE = '" + lst_pur + "' "
                    + " ,PROMO_POINTS = PROMO_POINTS + " + _rs.getString("PROPOIN") + " "
                    + " WHERE SBU_CODE='" + prm_strSBU + "' AND CUST_ID='" + prm_oldcustID + "' AND LOYAL_TYPE='110'";
            if (_stmt.executeUpdate(m_strSQL) <= 0) {
                throw new Exception("Error occured in Update CRM_CUST_LOYALTY_MAST OLD ID " + prm_oldcustID);
            }
            _rs = null;
            m_strSQL = "";
            m_strSQL = "SELECT LOYAL_POINTS as LOLPOIN ,CUMM_POINTS as CUMPOIN, "
                    + " CUMM_REDEM as CUMRED, "
                    + " case when LAST_PUR_DATE is null then '' else to_char(LAST_PUR_DATE,'DD-MON-YYYY' ) end  as LSTPUR,"
                    + " PROMO_POINTS as PROPOIN "
                    + " FROM crm.CRM_CUST_LOYALTY_MAST "
                    + " WHERE SBU_CODE='" + prm_strSBU + "' AND CUST_ID='" + prm_newcustID + "' AND LOYAL_TYPE='110'";
            _stmt = conMarksys.getConn().createStatement();
            _rs = _stmt.executeQuery(m_strSQL);
            if (!_rs.next()) {
                throw new Exception("No Data Found for this Customer " + prm_newcustID);
            }
            m_strSQL = "UPDATE crm.CRM_CUST_LOYALTY_MAST "
                    + " SET LOYAL_POINTS=(LOYAL_POINTS + '" + old_loyal + "')"
                    + " ,CUMM_REDEM = CUMM_REDEM + '" + old_redim + "'"
                    + " ,CUMM_POINTS = CUMM_POINTS + '" + old_cum + "'"
                    + " ,LAST_PUR_DATE = '" + old_lstpur + "'"
                    + " ,PROMO_POINTS = PROMO_POINTS + '" + old_promo + "'"
                    + " WHERE SBU_CODE='" + prm_strSBU + "' AND CUST_ID='" + prm_newcustID + "' AND LOYAL_TYPE='110'";

            if (_stmt.executeUpdate(m_strSQL) <= 0) {
                throw new Exception("Error occured in Update CRM_CUST_LOYALTY_MAST NEW ID " + prm_newcustID);
            }

            return 1;
        } catch (Exception e) {
            e.printStackTrace();
            _exception = e;
            return 0;
        } finally {
            try {
                if (_rs != null) {
                    _rs.close();
                    _rs = null;
                }
                if (_stmt != null) {
                    _stmt.close();
                    _stmt = null;
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    public int UpdateTxnSummarywithNewID(String prm_strSBU, String prm_strOldID, String prm_strNewID) {
        try {
            Statement _stmt3 = conMarksys.getConn().createStatement();
            String _strSQL = "update crm.CRM_TXN_SUMMARY set cust_code='" + prm_strNewID + "' "
                    + " where sbu_code='" + prm_strSBU + "' "
                    + " and cust_code='" + prm_strOldID + "'";
            if (_stmt3.executeUpdate(_strSQL) <= 0) {
                _stmt3.close();
                _stmt3 = null;
            }
            _stmt3.close();
            _stmt3 = null;
            return 1;
        } catch (Exception e) {
            e.printStackTrace();
            _exception = e;
            return 0;
        }
    }

    public void transferPoliciesIssuedAtSc() {
        boolean _bolsuccess = true;
        ResultSet m_rs = null;
        ResultSet m_rsdoc = null;
        Statement m_stmtOra1 = null;
        Statement m_stmtOra2 = null;
        Statement m_stmtOra3 = null;
        Statement m_stmtup = null;
        Statement m_stmtInMysql = null;
        String m_strTxnDate = "";
        try {
//            conInsurance = new MysqlConnector("10.10.10.10", "marksys", "crm", "crm@ail#2012");
            String m_strSBU = "830";
            String m_strLoc = "100";
//            String m_strInsDocCode = "CRMP";
            String m_strSeqOBj = "SQRCNB";
            String m_strin = "";
            String m_strup = "";
            int m_intSEQNo = 0;
//            conMarksys = new OracleConnector("192.168.1.27", "rpd2", "MARKSYS", new jText.TextUti().getText("oracle"));           
            m_stmtOra1 = conMarksys.getConn().createStatement(ResultSet.TYPE_SCROLL_INSENSITIVE, ResultSet.CONCUR_READ_ONLY);
            m_stmtOra2 = conMarksys.getConn().createStatement(ResultSet.TYPE_SCROLL_INSENSITIVE, ResultSet.CONCUR_READ_ONLY);
            m_stmtOra3 = conMarksys.getConn().createStatement(ResultSet.TYPE_SCROLL_INSENSITIVE, ResultSet.CONCUR_READ_ONLY);
            m_stmtup = conMarksys.getConn().createStatement();
            m_stmtInMysql = conInsurance.getConn().createStatement();
            String m_strSQL = " select '450' inssbucod,loccod,'RCNB' doccod,'' docnum,'1' linnum,reftx1,paymod,amount,"
                    + "sbucod,maccod,receno,comcod,coltyp,userid,txndat,"
                    + "to_char(to_date(txndat),'YYYY-MM-DD') as insdat"
                    + "  from rmis.rmis_rms_pos_bc_txn_mas "
                    + "  where sbucod='" + m_strSBU + "' and comcod='ARPINS' "
                    + "  and coltyp='INS' and (transd is null or transd='F')";
            m_rs = m_stmtOra3.executeQuery(m_strSQL);
            if (m_rs.next()) {
                m_rs.beforeFirst();
                while (m_rs.next()) {
                    if (startTransactionMarksys() == 0) {
                        _bolsuccess = false;
                        throw new Exception(_exception.getLocalizedMessage());
                    }
                    if (startTransactionMarksys() == 0) {
                        _bolsuccess = false;
                        throw new Exception(_exception.getLocalizedMessage());
                    }
                    String strSetrial = "select * "
                            + " from marksys.smsequence where sbucod='" + m_strSBU + "' and loccod='" + m_strLoc + "' "
                            + " and seqid='" + m_strSeqOBj + "' for update";
                    m_rsdoc = m_stmtOra1.executeQuery(strSetrial);
                    if (m_rsdoc.next()) {
                        m_intSEQNo = Integer.parseInt(m_rsdoc.getString("curv"));
                        if (m_intSEQNo > Integer.parseInt(m_rsdoc.getString("maxv"))) {
                            System.out.println("Document Serial No has reached the max no for: " + strDocCode);
                            _bolsuccess = false;
                            throw new Exception("Document Serial No has reached the max no for: " + strDocCode);
                        }
                        if (m_intSEQNo < Integer.parseInt(m_rsdoc.getString("minv"))) {
                            System.out.println("The Start Document serial is lower than the current serial no for: " + strDocCode);
                            _bolsuccess = false;
                            throw new Exception("The Start Document serial is lower than the current serial no for: " + strDocCode);
                        }
                        m_intSEQNo = m_intSEQNo + 1;
                    }

                    if (startTransactionMySql() == 0) {
                        _bolsuccess = false;
                        throw new Exception(_exception.getLocalizedMessage());
                    }
                    m_strin = "";
                    m_strin = "insert into insptransactions (sbucod, loccod, doccod, docnum, linnum, pprnum,totprm,paymod,creadt,txndat) values"
                            + "('" + m_rs.getString("inssbucod") + "','" + m_rs.getString("loccod") + "','" + m_rs.getString("doccod") + "',"
                            + "'" + m_intSEQNo + "','1','" + m_rs.getString("reftx1") + "','" + m_rs.getString("amount") + "'"
                            + ",'" + m_rs.getString("paymod") + "',now(),'" + m_rs.getString("insdat") + "')";
                    if (m_stmtInMysql.executeUpdate(m_strin) <= 0) {
                        _bolsuccess = false;
                        m_stmtInMysql.close();
                        m_stmtInMysql = null;
                        throw new Exception("Error occured in inserting data:");
                    }
                    m_strup = "";
                    m_strup = "update rmis.rmis_rms_pos_bc_txn_mas set transd='T'"
                            + " where sbucod='" + m_rs.getString("sbucod") + "' "
                            + " and loccod='" + m_rs.getString("loccod") + "' "
                            + " and maccod='" + m_rs.getString("maccod") + "'"
                            + " and receno='" + m_rs.getString("receno") + "'"
                            + " and comcod='" + m_rs.getString("comcod") + "'"
                            + " and coltyp='" + m_rs.getString("coltyp") + "'"
                            + " and userid='" + m_rs.getString("userid") + "'"
                            + " and txndat=to_char(to_date('" + m_rs.getString("txndat") + "','MM/DD/YYYY HH24:MI:SS'),'DD-Mon-YYYY')";
                    if (m_stmtup.executeUpdate(m_strup) <= 0) {
                        _bolsuccess = false;
                        m_stmtup.close();
                        m_stmtup = null;
                        throw new Exception("Error occured in updating data:");
                    }
                    String strDocUp = "update marksys.smsequence set curv='" + m_intSEQNo + "'"
                            + " where sbucod='" + m_strSBU + "' and loccod='" + m_strLoc + "' and seqid='" + m_strSeqOBj + "' ";
                    if (m_stmtOra2.executeUpdate(strDocUp) <= 0) {
                        _bolsuccess = false;
                        m_stmtOra2.close();
                        m_stmtOra2 = null;
                        throw new Exception("Serial Number updation Failed.");
                    }
                    if (endTransactionMySql() == 0) {
                        _bolsuccess = false;
                        throw new Exception(_exception.getLocalizedMessage());
                    }
                    if (endTransactionMarksys() == 0) {
                        _bolsuccess = false;
                        throw new Exception(_exception.getLocalizedMessage());
                    }
                    if (endTransactionMarksys() == 0) {
                        _bolsuccess = false;
                        throw new Exception(_exception.getLocalizedMessage());
                    }
                }
            } else {
                System.out.println("Data not found for super center receipts....");
            }
            System.out.println("Super centre receipt transfer successful...");
        } catch (Exception e) {
            _bolsuccess = false;
            e.printStackTrace();
            abortTransactionMarksys();
            abortTransactionMySql();
            _exception = e;
        } finally {
            try {
                if (m_rs != null) {
                    m_rs.close();
                    m_rs = null;
                }
                if (m_rsdoc != null) {
                    m_rsdoc.close();
                    m_rsdoc = null;
                }
                if (m_stmtOra1 != null) {
                    m_stmtOra1.close();
                    m_stmtOra1 = null;
                }
                if (m_stmtOra2 != null) {
                    m_stmtOra2.close();
                    m_stmtOra2 = null;
                }
                if (m_stmtOra3 != null) {
                    m_stmtOra3.close();
                    m_stmtOra3 = null;
                }
                if (m_stmtup != null) {
                    m_stmtup.close();
                    m_stmtup = null;
                }
                if (m_stmtInMysql != null) {
                    m_stmtInMysql.close();
                    m_stmtInMysql = null;
                }
//                closeConnection();
            } catch (Exception e) {
                e.printStackTrace();
                abortTransactionMarksys();
//                closeConnection();
            }
        }
    }

    ////////// sales Order
    public void ExpireLMDOrders() {
        //String sbuCode, String paraCode, String strLoyType
        String sbuCode = "830";
        Statement stmnt = null;
        Statement stmnt_1 = null;
        Statement stmnt_2 = null;
        ResultSet rs = null;
        JSONObject objtmpdet;
        JSONArray objdetail = new JSONArray();
        String m_msgbody2 = "", m_msgbody1 = "";
        JSONObject jsontemp = null;

        try {
            poexpire();
            pendingPR();
            conMarksys = new OracleConnector("192.168.1.27", "rpd2", "LMD", new jText.TextUti().getText("oracle"));
//            conMarksys = new OracleConnector("192.168.1.27", "rpd2", "TLMD", "TLMD");
            if (startTransactionMarksys() == 0) {
                throw new Exception(_exception.getLocalizedMessage());
            }
//            String sql = "select distinct a.sbu_code,a.loc_code,a.txn_date ,a.doc_code,a.doc_no,a.cust_sup_code,c.name,\n"
//                    + " to_char(a.inv_amt,'999,999,999,999,999.99') inv_amt from rms_doc_txnm a\n"
//                    + " inner join rms_doc_txnd b\n"
//                    + " on a.sbu_code=b.sbu_code and a.loc_code=b.loc_code and a.doc_code=b.doc_code and a.doc_no=b.doc_no\n"
//                    + " inner join rms_cust_sup c\n"
//                    + " on a.sbu_code=c.sbu_code and c.cust_sup='C' and a.cust_sup_code=c.cs_code\n"
//                    + " where a.sbu_code='680' and a.loc_code='8' and a.doc_code='SAOR'\n"
//                    + " and a.mstat='VAL' and b.dstat='VAL' and a.txn_date ='10-Jul-2012'";

            String sql = " select "
                    + " distinct a.sbu_code,a.loc_code,to_char(to_date(a.txn_date),'DD/MM/YYYY') as txn_date, "
                    + "  a.doc_code,a.doc_no,a.cust_sup_code,c.name, "
                    + " to_char(a.inv_amt,'999,999,999,999,999.99') inv_amt ,a.mstat,b.dstat "
                    + "  from rms_doc_txnd d"
                    + "  inner join rms_doc_txnd b"
                    + "  on d.sbu_code='833' and d.loc_code='100' and d.doc_code=nvl(b.dimm06,'') and "
                    + " d.doc_no=nvl(b.dimm07,'') "
                    + "  inner join rms_doc_txnm a "
                    + "   on a.sbu_code=b.sbu_code and a.loc_code=b.loc_code and "
                    + " a.doc_code=b.doc_code and a.doc_no=b.doc_no "
                    + "    inner join rms_cust_sup c "
                    + "  on a.sbu_code=c.sbu_code and c.cust_sup='C' and a.cust_sup_code=c.cs_code "
                    + "  where d.sbu_code like '%' and d.loc_code like '%' and d.doc_code='SORD' and b.doc_code='SAOR' "
                    + "  and a.mstat='VAL' and b.dstat='VAL' "
                    + "  and a.txn_date < to_char(sysdate-(select cval01 from smsyspara where sbucod = 'XXX' and loccod = 'XXX' and parcod = 'SOEXP'),'DD-Mon-YYYY')";

//                    + " and a.txn_date < to_char(sysdate-3,'DD-Mon-YYYY')";
            stmnt = conMarksys.getConn().createStatement(ResultSet.TYPE_SCROLL_SENSITIVE, ResultSet.CONCUR_READ_ONLY);
            rs = stmnt.executeQuery(sql);
            if (!rs.next()) {
                throw new Exception("Data not found to expire orders.");
            } else {

                rs.beforeFirst();
                int i = 0;
                while (rs.next()) {
                    objtmpdet = new JSONObject();
                    objtmpdet.put("sbucode", rs.getString("sbu_code"));
                    objtmpdet.put("loccode", rs.getString("loc_code"));
                    objtmpdet.put("txndate", rs.getString("txn_date"));
                    objtmpdet.put("doccode", rs.getString("doc_code"));
                    objtmpdet.put("docno", rs.getInt("doc_no"));
                    objtmpdet.put("custsupcode", rs.getString("cust_sup_code"));
                    objtmpdet.put("name", rs.getString("name"));
                    objtmpdet.put("invamt", rs.getString("inv_amt"));

                    String m_strqry = "update rms_doc_txnm "
                            + " set mstat = 'EXP' "
                            + " where sbu_code = '" + objtmpdet.get("sbucode") + "' and "
                            + " loc_code = '" + objtmpdet.get("loccode") + "' and "
                            + " doc_code = '" + objtmpdet.get("doccode") + "' and "
                            + " doc_no = '" + objtmpdet.get("docno") + "' ";

                    stmnt_1 = conMarksys.getConn().createStatement(ResultSet.TYPE_SCROLL_SENSITIVE, ResultSet.CONCUR_READ_ONLY);
                    if (stmnt_1.executeUpdate(m_strqry) <= 0) {
                        throw new Exception(" Error occured in updating  in rms_doc_txnm");
                    }

                    String m_strqry_1 = "update rms_doc_txnd "
                            + " set dstat = 'EXP' "
                            + " where sbu_code = '" + objtmpdet.get("sbucode") + "' and "
                            + " loc_code = '" + objtmpdet.get("loccode") + "' and "
                            + " doc_code = '" + objtmpdet.get("doccode") + "' and "
                            + " doc_no = '" + objtmpdet.get("docno") + "' ";

                    stmnt_2 = conMarksys.getConn().createStatement(ResultSet.TYPE_SCROLL_SENSITIVE, ResultSet.CONCUR_READ_ONLY);

                    if (stmnt_2.executeUpdate(m_strqry_1) <= 0) {
                        throw new Exception(" Error occured in updating  in rms_doc_txnd");
                    }

                    objdetail.add(i, objtmpdet);
                    i++;

                    if (stmnt_1 != null) {
                        stmnt_1.close();
                        stmnt_1 = null;
                    }
                    if (stmnt_2 != null) {
                        stmnt_2.close();
                        stmnt_2 = null;
                    }
                }
                m_msgbody2 = "<br><table style='text-align: center;central;font-size: 13px;'>"
                        + "<tr>"
                        + "<br>"
                        + "     <th style='width: 100px;'>SBU CODE</td>"
                        + "     <th style='width: 100px;'>LOC CODE</td>"
                        + "     <th style='width: 100px;'>TXN DATE</td>"
                        + "     <th style='width: 100px;'>DOCUMENT CODE</td>"
                        + "     <th style='width: 100px;'>DOCUMENT No</td>"
                        + "     <th style='width: 100px;'>SUPPLIER CODE</td>"
                        + "     <th  style='width: 100px;'>NAME</td>"
                        + "     <th  style='width: 100px;'>AMOUNT</td>"
                        + " </tr>";

                int count = 0;
                while (count < objdetail.size()) {
                    jsontemp = (JSONObject) objdetail.get(count);
                    m_msgbody2 += " <tr>"
                            //                        + "     <td>" + objdetail.get(count) + "</td>"
                            + "     <td>" + jsontemp.get("sbucode").toString() + "</td>"
                            + "     <td>" + jsontemp.get("loccode").toString() + "</td>"
                            + "     <td>" + jsontemp.get("txndate").toString() + "</td>"
                            + "     <td>" + jsontemp.get("doccode").toString() + "</td>"
                            + "     <td>" + jsontemp.get("docno").toString() + "</td>"
                            + "     <td>" + jsontemp.get("custsupcode").toString() + "</td>"
                            + "     <td>" + jsontemp.get("name").toString() + "</td>"
                            + "     <td>" + jsontemp.get("invamt").toString() + "</td>"
                            + " </tr>";
                    count++;
                }

                if (endTransactionMarksys() == 0) {
                    throw new Exception(_exception.getLocalizedMessage());
                }

                m_msgbody2 += "</table>";

                String m_msgbody = "<strong>List of expired Sales orders</strong>"
                        + "<hr>";
                String emailList = getEmailAddress();
                if (!emailList.equals("nodata")) {
                    sendEmail(emailList, "e_marksys@arpico.com", "Expired Sales Orders: ", m_msgbody, m_msgbody2, "");
                }
            }

////////////////////////////////////////////////////////////////////////
////////////////////////////////////////////////////////////////////////
        } catch (Exception e) {
            e.printStackTrace();
            abortTransactionMarksys();
        } finally {
            try {
                if (rs != null) {
                    rs.close();
                    rs = null;
                }
                if (stmnt != null) {
                    stmnt.close();
                    stmnt = null;
                }
                if (stmnt_1 != null) {
                    stmnt_1.close();
                    stmnt_1 = null;
                }
                if (stmnt_2 != null) {
                    stmnt_2.close();
                    stmnt_2 = null;
                }
                if (conMarksys != null) {
                    conMarksys.getConn().close();
                    conMarksys = null;
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    public void sendEmail(String prm_strNextAppName, String prm_strNextAppemail, String prm_strHeader, String prm_strBody1, String prm_strBody2,
            String prm_strLastAppEmail) {
        try {
            CreateMail(prm_strNextAppName, prm_strNextAppemail, "", prm_strHeader, "", "", "",
                    prm_strBody1, prm_strBody2, "<br><br><br> This is an automated message from sales order expiry process.", "");
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }

    public boolean CreateMail(String strtoAddress, String prm_strFromAddress, String prm_strCCAddress, String prm_strHeader, String strFilename1, String strFilename2, String strFilename3,
            String prm_strboodyText1, String prm_strboodyText2, String prm_strbodytext3, String prm_strbodytext4) throws Exception {
        boolean result = false;
        try {
//            String[] to = null;
//            String[] cc = null;
//            String from = prm_strFromAddress;
//            if (strtoAddress != null && strtoAddress.contains(",")) {
//                to = strtoAddress.split(",");
//            } else if (strtoAddress != null && strtoAddress.contains(";")) {
//                to = strtoAddress.split(";");
//            } else {
//                to = "".split(",");
//            }
//            if (prm_strCCAddress != null && prm_strCCAddress.contains(",")) {
//                cc = prm_strCCAddress.split(",");
//            } else if (prm_strCCAddress != null && prm_strCCAddress.contains(";")) {
//                cc = prm_strCCAddress.split(";");
//            } else {
//                cc = "".split(",");
//            }
            String from = prm_strFromAddress;
            String[] to = strtoAddress.split(",");
            String[] cc = prm_strCCAddress.split(",");
            String fileAttachment1 = strFilename1;
            String fileAttachment2 = strFilename2;
            String fileAttachment3 = strFilename3;
            String bodytext1 = prm_strboodyText1;
            String bodytext2 = prm_strboodyText2;
            String bodytext3 = prm_strbodytext3;//"This is System Generated email.For any queries please contact the sender of this mail.<br><br>Regards,<br><br>";
            String bodytext4 = prm_strbodytext4;//_audit.getStrUserName();
            Properties props = System.getProperties();
            props.put("mail.transport.protocol", "smtp");
            props.put("mail.smtp.host", "192.168.1.19");
            props.put("mail.smtp.localhost", _strIpAddress);
            props.put("mail.smtp.sendpartial", "true");

            Session session = Session.getInstance(props);
            MimeMessage message = new MimeMessage(session);
            message.setFrom(new InternetAddress(from));

            for (int i = 0; i < cc.length; i++) {
                if (!cc[i].equals("")) {
                    message.addRecipient(Message.RecipientType.CC, new InternetAddress(cc[i]));
                }
            }
            for (int i = 0; i < to.length; i++) {
                if (!to[i].equals("")) {
                    message.addRecipient(Message.RecipientType.TO, new InternetAddress(to[i]));
                }
            }
            message.setSubject(prm_strHeader);
            MimeBodyPart messageBodyPart1 = null;
            MimeBodyPart messageBodyPart2 = null;
            MimeBodyPart messageBodyPart3 = null;
            if (!fileAttachment1.trim().equals("")) {
                messageBodyPart1 = new MimeBodyPart();
                messageBodyPart1.attachFile(fileAttachment1);
            }
            if (!fileAttachment2.trim().equals("")) {
                messageBodyPart2 = new MimeBodyPart();
                messageBodyPart2.attachFile(fileAttachment2);
            }
            if (!fileAttachment3.trim().equals("")) {
                messageBodyPart3 = new MimeBodyPart();
                messageBodyPart3.attachFile(fileAttachment3);
            }

            Multipart multipart = new MimeMultipart();
            MimeBodyPart textPart = new MimeBodyPart();
            textPart.setContent(bodytext1 + bodytext2 + bodytext3 + bodytext4, "text/html");
            multipart.addBodyPart(textPart);
            if (!fileAttachment1.trim().equals("")) {
                multipart.addBodyPart(messageBodyPart1);
            }
            if (!fileAttachment2.trim().equals("")) {
                multipart.addBodyPart(messageBodyPart2);
            }
            if (!fileAttachment3.trim().equals("")) {
                multipart.addBodyPart(messageBodyPart3);
            }
            message.setContent(multipart);
            Transport.send(message);
            result = true;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return result;
    }

    public String getEmailAddress() {
        String m_strQty = "nodata";
        String condition = "";
        String m_sql = "";
        try {
            conMarksys1 = new OracleConnector("192.168.1.27", "rpd2", "LMD", new jText.TextUti().getText("oracle"));
//            conMarksys1 = new OracleConnector("192.168.1.27", "rpd2", "TLMD", "TLMD");
            Statement m_stmntso = conMarksys.getConn().createStatement(ResultSet.TYPE_SCROLL_SENSITIVE, ResultSet.CONCUR_READ_ONLY);

            m_sql = " select cval01 from smsyspara where sbucod = 'XXX' and loccod = 'XXX' and parcod = 'SOMAIL'";

            ResultSet rsso = m_stmntso.executeQuery(m_sql);
            if (rsso.next()) {
                m_strQty = rsso.getString("cval01");
            } else {
                return m_strQty;
//                throw new Exception("Please Set the Email Informations.");
            }
            if (m_stmntso != null) {
                m_stmntso.close();
                m_stmntso = null;
            }
            if (rsso != null) {
                rsso.close();
                rsso = null;
            }
        } catch (Exception e) {
            _exception = e;
            e.printStackTrace();
        }
        return m_strQty;
    }

    public void sendEmailPurchaseOrders(String prm_strNextTo, String prm_strNextAppemail, String prm_strHeader, String prm_strBody1, String prm_strBody2,
            String prm_strLastAppEmail) {
        try {
            CreateMailPurchaseOrders(prm_strNextTo, prm_strNextAppemail, "", prm_strHeader, "", "", "",
                    //            CreateMailPurchaseOrders(getEmailAddressPurchaseOrders(), "salesorder.exp@arpico.com", "", prm_strHeader, "", "", "",
                    prm_strBody1, prm_strBody2, prm_strLastAppEmail, "");
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }

    public boolean CreateMailPurchaseOrders(String strtoAddress, String prm_strFromAddress, String prm_strCCAddress, String prm_strHeader, String strFilename1, String strFilename2, String strFilename3,
            String prm_strboodyText1, String prm_strboodyText2, String prm_strbodytext3, String prm_strbodytext4) throws Exception {
        boolean result = false;
        try {
            String from = prm_strFromAddress;
            String[] to = strtoAddress.split(",");
            String[] cc = prm_strCCAddress.split(",");
            String fileAttachment1 = strFilename1;
            String fileAttachment2 = strFilename2;
            String fileAttachment3 = strFilename3;
            String bodytext1 = prm_strboodyText1;
            String bodytext2 = prm_strboodyText2;
            String bodytext3 = prm_strbodytext3;//"This is System Generated email.For any queries please contact the sender of this mail.<br><br>Regards,<br><br>";
            String bodytext4 = prm_strbodytext4;//_audit.getStrUserName();
            Properties props = System.getProperties();
            props.put("mail.transport.protocol", "smtp");
            props.put("mail.smtp.host", "192.168.1.19");
            props.put("mail.smtp.localhost", _strIpAddress);
            props.put("mail.smtp.sendpartial", "true");

            Session session = Session.getInstance(props);
            MimeMessage message = new MimeMessage(session);
            message.setFrom(new InternetAddress(from));

            for (int i = 0; i < cc.length; i++) {
                if (!cc[i].equals("")) {
                    message.addRecipient(Message.RecipientType.CC, new InternetAddress(cc[i]));
                }
            }
            for (int i = 0; i < to.length; i++) {
                if (!to[i].equals("")) {
                    message.addRecipient(Message.RecipientType.TO, new InternetAddress(to[i]));
                }
            }
            message.setSubject(prm_strHeader);
            MimeBodyPart messageBodyPart1 = null;
            MimeBodyPart messageBodyPart2 = null;
            MimeBodyPart messageBodyPart3 = null;
            if (!fileAttachment1.trim().equals("")) {
                messageBodyPart1 = new MimeBodyPart();
                messageBodyPart1.attachFile(fileAttachment1);
            }
            if (!fileAttachment2.trim().equals("")) {
                messageBodyPart2 = new MimeBodyPart();
                messageBodyPart2.attachFile(fileAttachment2);
            }
            if (!fileAttachment3.trim().equals("")) {
                messageBodyPart3 = new MimeBodyPart();
                messageBodyPart3.attachFile(fileAttachment3);
            }

            Multipart multipart = new MimeMultipart();
            MimeBodyPart textPart = new MimeBodyPart();
            textPart.setContent(bodytext1 + bodytext2 + bodytext3 + bodytext4, "text/html");
            multipart.addBodyPart(textPart);
            if (!fileAttachment1.trim().equals("")) {
                multipart.addBodyPart(messageBodyPart1);
            }
            if (!fileAttachment2.trim().equals("")) {
                multipart.addBodyPart(messageBodyPart2);
            }
            if (!fileAttachment3.trim().equals("")) {
                multipart.addBodyPart(messageBodyPart3);
            }
            message.setContent(multipart);
            Transport.send(message);
            result = true;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return result;
    }

    public double round(double value, int places) {
        if (places < 0) {
            throw new IllegalArgumentException();
        }
        String s = value + "";
        if (s.length() < places) {
            return value;
        }

        BigDecimal bd = new BigDecimal(Double.toString(value));
        bd = bd.setScale(places, RoundingMode.HALF_UP);
        return bd.doubleValue();
    }

    public BigDecimal round(BigDecimal d, int scale, boolean roundUp) {
        int mode = (roundUp) ? BigDecimal.ROUND_UP : BigDecimal.ROUND_DOWN;
        return d.setScale(scale, mode);
    }

    public String setDecimalSplitor(Double withoutSeperator) {
        String pattern = ",###,###.00";
        return (new DecimalFormat(pattern).format(withoutSeperator));

    }

    public void poexpire() {
        /////////////////////////////////////////////////////////////////////////
        ////////// po expire @me
        ///////////////////////////////////
        String m_msgpobody2 = "", m_msgpobody1 = "";
        Statement stmntpo = null;
        Statement stmntpo_2 = null;
        Statement stmntpo_1 = null;
        ResultSet rspo = null;

        HashMap<String, HashMap<String, HashMap<String, HashMap<String, HashMap<String, String>>>>> hashHead = new HashMap<>();
        HashMap<String, HashMap<String, HashMap<String, HashMap<String, String>>>> hashC = new HashMap<>();
        HashMap<String, HashMap<String, HashMap<String, String>>> hashP;
        HashMap<String, HashMap<String, String>> hashD;
        HashMap<String, String> hashIt;
        HashMap<String, String> hashNoAddress = new HashMap<>();
        HashMap<String, String> hashNoAddressHead = new HashMap<>();
        String keyId = "";
        String HeadkeyId = "";
        String supName = "";
        String distributerHeadEmail = "";
        String distributerName = "";
        String distributerEmail = "";

        double TotalEAmountDIS = 0.0;
        double TotalEAmountDISTot = 0.0;
        double TotalEAmountASM = 0.0;

        try {
            conMarksys = new OracleConnector("192.168.1.27", "rpd2", "LMD", new jText.TextUti().getText("oracle"));
//            if (startTransactionMarksys() == 0) {
//                throw new Exception(_exception.getLocalizedMessage());
//            }
            String sqlpo = "select to_char(to_date(m.txn_date),'DD/MM/YYYY') txn_date, m.DOC_CODE,"
                    + "m.doc_no,"
                    + "d.source_doc_no ,\n"
                    + "m.cust_sup_code ,\n"
                    + "c.cuscod ,\n"
                    + "cs.name ,cs.email, d.item_code,\n"
                    + "it.itm_desc,\n"
                    + "d.SEQ_NO,\n"
                    + "d.qty,(d.qty-nvl(d.issued_qty,0)) as EQty ,\n"
                    //                    + "--CASE WHEN (d.qty-nvl(d.issued_qty,0)) = 0 THEN 999 ELSE (d.qty-nvl(d.issued_qty,0)) END as Eqty11 ,\n"
                    //                    + "--CASE WHEN d.qty-nvl(d.issued_qty,0) = 0 THEN 'Order complete' ELSE to_char(d.qty-nvl(d.issued_qty,0)) END as Eqty ,\n"
                    + "d.price *(d.qty-nvl(d.issued_qty,0)) as EAmount,m.sbu_code,c.sbucod as cusSbu,m.loc_code,ca.email as hemail,ca.area,ca.sbucod,ca.dsca as hname \n"
                    + "from rms_doc_txnm m INNER join rms_doc_txnd d \n"
                    + "ON m.sbu_code=d.sbu_code and m.loc_code=d.loc_code AND  m.doc_code = d.doc_code and m.doc_no=d.doc_no \n"
                    + "inner join lmd.custdistrmap c\n"
                    + "ON c.sbucod=d.job_hid  and c.DISCOD=m.cust_sup_code\n"
                    + "inner join rms_cust_sup cs\n"
                    + "ON cs.sbu_code=c.sbucod  and cs.cust_sup='C'  and cs.cs_code=c.cuscod\n"
                    + "inner join rms_itmmaster it\n"
                    + "ON it.sbu_code=c.sbucod and it.item_code=d.item_code\n"
                    + "inner join cust_area ca\n"
                    + "ON ca.sbucod=cs.sbu_code and ca.area=cs.area_code \n"
                    + "where m.sbu_code='833' and m.loc_code='100' and m.doc_code='SORD'"
                    //                    + " and m.doc_no in ('5743','5723') \n"
                    + " and m.mstat='VAL' \n"
                    + " and d.dstat='VAL' \n"
                    + "and m.txn_date < to_char(sysdate-(select cval01 from smsyspara where sbucod = 'XXX' and loccod = 'XXX' and parcod = 'POEXP'),'DD-Mon-YYYY')\n"
                    //                    + " and m.txn_date<'23-Jan-2019'"
                    + " and (d.qty-nvl(d.issued_qty,0)>0)\n"
                    + " order by m.txn_date desc ";

            stmntpo = conMarksys.getConn().createStatement(ResultSet.TYPE_SCROLL_SENSITIVE, ResultSet.CONCUR_READ_ONLY);
//            System.out.println(sqlpo);
//            System.out.println("\n");
            rspo = stmntpo.executeQuery(sqlpo);
            int t = 0;
            if (!rspo.next()) {
//                return false;
//                System.out.println("no data found po expire");
//                return;
                throw new Exception("Purchase Orders not found to expire.");
            }
            rspo.beforeFirst();
            while (rspo.next()) {
                HeadkeyId = rspo.getString("area") + "_" + rspo.getString("sbucod") + "-" + rspo.getString("hname").trim();
                keyId = rspo.getString("cusSbu") + "--" + rspo.getString("cuscod") + "__" + rspo.getString("name");

                if (hashHead.containsKey(HeadkeyId)) {
                    hashC = hashHead.get(HeadkeyId);
                    if (hashC.containsKey(keyId)) {
                        hashP = hashC.get(keyId);
                        if (hashP.containsKey(rspo.getString("source_doc_no") + "_" + rspo.getString("TXN_DATE"))) {
                            hashD = hashP.get(rspo.getString("source_doc_no") + "_" + rspo.getString("TXN_DATE"));
                            if (hashD.containsKey(rspo.getString("SEQ_NO"))) {
                                hashIt = hashD.get(rspo.getString("SEQ_NO"));
// 
                                hashIt.put("SBU_CODE", rspo.getString("SBU_CODE"));
                                hashIt.put("cusSbu", rspo.getString("cusSbu"));
                                hashIt.put("LOC_CODE", rspo.getString("LOC_CODE"));

                                hashIt.put("name", rspo.getString("name"));
                                hashIt.put("cuscod", rspo.getString("cuscod"));

                                hashIt.put("ITEM_CODE", rspo.getString("ITEM_CODE"));
                                hashIt.put("QTY", rspo.getString("QTY"));
                                hashIt.put("EQTY", rspo.getString("EQTY"));
                                hashIt.put("EAMOUNT", rspo.getString("EAMOUNT"));
                                hashIt.put("ITM_DESC", rspo.getString("ITM_DESC"));
                                hashIt.put("TXN_DATE", rspo.getString("TXN_DATE"));
                                hashIt.put("DOC_CODE", rspo.getString("DOC_CODE"));
                                hashIt.put("DOC_NO", rspo.getString("doc_no"));
                                hashIt.put("EMAIL", rspo.getString("EMAIL"));
                                hashIt.put("hemail", rspo.getString("hemail"));

                                hashD.replace(rspo.getString("SEQ_NO"), hashIt);
                                hashP.replace(rspo.getString("source_doc_no") + "_" + rspo.getString("TXN_DATE"), hashD);
                                hashC.replace(keyId, hashP);
                                hashHead.replace(rspo.getString("area"), hashC);

                            } else {
                                hashIt = new HashMap<>();

                                hashIt.put("SBU_CODE", rspo.getString("SBU_CODE"));
                                hashIt.put("cusSbu", rspo.getString("cusSbu"));
                                hashIt.put("LOC_CODE", rspo.getString("LOC_CODE"));

                                hashIt.put("name", rspo.getString("name"));
                                hashIt.put("cuscod", rspo.getString("cuscod"));

                                hashIt.put("ITEM_CODE", rspo.getString("ITEM_CODE"));
                                hashIt.put("QTY", rspo.getString("QTY"));
                                hashIt.put("EQTY", rspo.getString("EQTY"));
                                hashIt.put("EAMOUNT", rspo.getString("EAMOUNT"));
                                hashIt.put("ITM_DESC", rspo.getString("ITM_DESC"));
                                hashIt.put("TXN_DATE", rspo.getString("TXN_DATE"));
                                hashIt.put("DOC_CODE", rspo.getString("DOC_CODE"));
                                hashIt.put("DOC_NO", rspo.getString("doc_no"));
                                hashIt.put("EMAIL", rspo.getString("EMAIL"));
                                hashIt.put("hemail", rspo.getString("hemail"));

                                hashD.put(rspo.getString("SEQ_NO"), hashIt);
                                hashP.replace(rspo.getString("source_doc_no") + "_" + rspo.getString("TXN_DATE"), hashD);
                                hashC.replace(keyId, hashP);
                                hashHead.replace(rspo.getString("area"), hashC);
                            }
                        } else {
                            hashP = hashC.get(keyId);

                            hashD = new HashMap<>();
                            hashIt = new HashMap<>();

                            hashIt.put("SBU_CODE", rspo.getString("SBU_CODE"));
                            hashIt.put("cusSbu", rspo.getString("cusSbu"));
                            hashIt.put("LOC_CODE", rspo.getString("LOC_CODE"));

                            hashIt.put("name", rspo.getString("name"));
                            hashIt.put("cuscod", rspo.getString("cuscod"));

                            hashIt.put("ITEM_CODE", rspo.getString("ITEM_CODE"));
                            hashIt.put("QTY", rspo.getString("QTY"));
                            hashIt.put("EQTY", rspo.getString("EQTY"));
                            hashIt.put("EAMOUNT", rspo.getString("EAMOUNT"));
                            hashIt.put("ITM_DESC", rspo.getString("ITM_DESC"));
                            hashIt.put("TXN_DATE", rspo.getString("TXN_DATE"));
                            hashIt.put("DOC_CODE", rspo.getString("DOC_CODE"));
                            hashIt.put("DOC_NO", rspo.getString("doc_no"));
                            hashIt.put("EMAIL", rspo.getString("EMAIL"));
                            hashIt.put("hemail", rspo.getString("hemail"));

                            hashD.put(rspo.getString("SEQ_NO"), hashIt);
                            hashP.put(rspo.getString("source_doc_no") + "_" + rspo.getString("TXN_DATE"), hashD);

                            hashC.replace(keyId, hashP);
                            hashHead.replace(rspo.getString("area"), hashC);

                        }
                    } else {
                        hashP = new HashMap<>();
                        hashD = new HashMap<>();
                        hashIt = new HashMap<>();

                        hashIt.put("SBU_CODE", rspo.getString("SBU_CODE"));
                        hashIt.put("cusSbu", rspo.getString("cusSbu"));
                        hashIt.put("LOC_CODE", rspo.getString("LOC_CODE"));

                        hashIt.put("name", rspo.getString("name"));
                        hashIt.put("cuscod", rspo.getString("cuscod"));

                        hashIt.put("ITEM_CODE", rspo.getString("ITEM_CODE"));
                        hashIt.put("QTY", rspo.getString("QTY"));
                        hashIt.put("EQTY", rspo.getString("EQTY"));
                        hashIt.put("EAMOUNT", rspo.getString("EAMOUNT"));
                        hashIt.put("ITM_DESC", rspo.getString("ITM_DESC"));
                        hashIt.put("TXN_DATE", rspo.getString("TXN_DATE"));
                        hashIt.put("DOC_CODE", rspo.getString("DOC_CODE"));
                        hashIt.put("DOC_NO", rspo.getString("doc_no"));
                        hashIt.put("EMAIL", rspo.getString("EMAIL"));
                        hashIt.put("hemail", rspo.getString("hemail"));

                        hashD.put(rspo.getString("SEQ_NO"), hashIt);
                        hashP.put(rspo.getString("source_doc_no") + "_" + rspo.getString("TXN_DATE"), hashD);

                        hashC.put(keyId, hashP);
                        hashHead.replace(rspo.getString("area"), hashC);
                    }

                } else {

                    hashC = new HashMap<>();
                    hashP = new HashMap<>();
                    hashD = new HashMap<>();
                    hashIt = new HashMap<>();

                    hashIt.put("SBU_CODE", rspo.getString("SBU_CODE"));
                    hashIt.put("cusSbu", rspo.getString("cusSbu"));
                    hashIt.put("LOC_CODE", rspo.getString("LOC_CODE"));

                    hashIt.put("name", rspo.getString("name"));
                    hashIt.put("cuscod", rspo.getString("cuscod"));

                    hashIt.put("ITEM_CODE", rspo.getString("ITEM_CODE"));
                    hashIt.put("QTY", rspo.getString("QTY"));
                    hashIt.put("EQTY", rspo.getString("EQTY"));
                    hashIt.put("EAMOUNT", rspo.getString("EAMOUNT"));
                    hashIt.put("ITM_DESC", rspo.getString("ITM_DESC"));
                    hashIt.put("TXN_DATE", rspo.getString("TXN_DATE"));
                    hashIt.put("DOC_CODE", rspo.getString("DOC_CODE"));
                    hashIt.put("DOC_NO", rspo.getString("doc_no"));
                    hashIt.put("EMAIL", rspo.getString("EMAIL"));
                    hashIt.put("hemail", rspo.getString("hemail"));

                    hashD.put(rspo.getString("SEQ_NO"), hashIt);
                    hashP.put(rspo.getString("source_doc_no") + "_" + rspo.getString("TXN_DATE"), hashD);
                    hashC.put(keyId, hashP);
                    hashHead.put(HeadkeyId, hashC);
                }
                t += 1;

            }

            HashMap<String, HashMap<String, HashMap<String, HashMap<String, String>>>> hashmC = new HashMap<>();
            HashMap<String, HashMap<String, HashMap<String, String>>> hashmP = new HashMap<>();
            HashMap<String, HashMap<String, String>> hashmD = new HashMap<>();
            HashMap<String, String> hashmIt = new HashMap<>();

            String m_msgbodyHead = "";
            for (Map.Entry hashHData : hashHead.entrySet()) {

                m_msgbodyHead = "<br><table border=\"1\" style='text-align: center;central;font-size: 13px;'>"
                        + "<tr>"
                        + "<br>"
                        //                        + "     <th style='width: 100px;'>ORDER DATE</td>"
                        + "     <th  style='width: 100px;'>ITEM CODE</td>"
                        + "     <th  style='width: 200px;'>ITEM DESCRIPTION</td>"
                        + "     <th  style='width: 50px;'>ORDER QTY</td>"
                        + "     <th  style='width: 50px;'>EXPIRE QTY</td>"
                        + " </tr>";

                hashmC = (HashMap<String, HashMap<String, HashMap<String, HashMap<String, String>>>>) hashHData.getValue();

                for (Map.Entry hashCData : hashmC.entrySet()) {
                    supName = "";
                    m_msgpobody2 = "<br><table border=\"1\" style='text-align: center;central;font-size: 13px;'>"
                            + "<tr>"
                            + "<br>"
                            //                            + "     <th style='width: 100px;'>ORDER DATE</td>"
                            + "     <th  style='width: 100px;'>ITEM CODE</td>"
                            + "     <th  style='width: 200px;'>ITEM DESCRIPTION</td>"
                            + "     <th  style='width: 50px;'>ORDER QTY</td>"
                            + "     <th  style='width: 50px;'>EXPIRE QTY</td>"
                            + " </tr>";

                    hashmP = (HashMap<String, HashMap<String, HashMap<String, String>>>) hashCData.getValue();

                    m_msgbodyHead += " <tr>"
                            + "    <td align=\"left\" colspan=\"5\"><span style=\"font-weight: bold;font-size: 14px;\">"
                            //                            + "SBU CODE " + hashCData.getKey().toString().split("--")[0] + " "
                            + "Customer :- " + hashCData.getKey().toString().split("__")[1] + "</span></td>"
                            + " </tr> ";

                    m_msgbodyHead += " <tr>";

                    for (Map.Entry hashPData : hashmP.entrySet()) {
                        hashmD = (HashMap<String, HashMap<String, String>>) hashPData.getValue();
                        m_msgbodyHead += "    <td align=\"left\" colspan=\"4\"><span style=\"font-weight: bold;font-size: 14px;\">order date " + hashPData.getKey().toString().split("_")[1] + " and Po Number  :- " + hashPData.getKey().toString().split("_")[0] + "</span></td>";
//                        m_msgbodyHead += "    <td align=\"left\" colspan=\"3\"><span style=\"font-weight: bold;font-size: 14px;\">Po Number :- " + hashPData.getKey().toString().split("_")[0] + "</span></td>";

                        m_msgpobody2 += " <tr>"
                                + "    <td align=\"left\" colspan=\"4\"><span style=\"font-weight: bold;font-size: 14px;\">order date " + hashPData.getKey().toString().split("_")[1] + " and Po Number  :- " + hashPData.getKey().toString().split("_")[0] + "</span></td>"
                                //                                + "    <td align=\"left\" colspan=\"3\"><span style=\"font-weight: bold;font-size: 14px;\">Po Number :- " + hashPData.getKey().toString().split("_")[0] + "</span></td>"
                                + " </tr> ";

                        if (startTransactionMarksys() == 0) {
                            throw new Exception("Error in starting txn.");
                        }
                        for (Map.Entry hashDData : hashmD.entrySet()) {

                            hashmIt = (HashMap<String, String>) hashDData.getValue();
                            String keyNoadderss = "";
                            if (hashmIt.get("EMAIL") == null) {
                                keyNoadderss = hashmIt.get("cusSbu") + "_" + hashmIt.get("cuscod");
                                if (!hashNoAddress.containsKey(keyNoadderss)) {
                                    hashNoAddress.put(keyNoadderss, hashmIt.get("name"));
                                }
                            }

                            String m_strqry_1 = "update rms_doc_txnd "
                                    + " set dstat = 'EXP' "
                                    + " where sbu_code = '" + hashmIt.get("SBU_CODE") + "' and "
                                    + " loc_code = '" + hashmIt.get("LOC_CODE") + "' and "
                                    + " doc_code = '" + hashmIt.get("DOC_CODE") + "' and "
                                    + " doc_no = '" + hashmIt.get("DOC_NO") + "' and "
                                    + " seq_no = '" + hashDData.getKey() + "' ";

//                            System.out.println("--" + "rms_doc_txnd");
//                            System.out.println("--" + m_strqry_1);
//                            System.out.println("");
//                            System.out.println("");
                            stmntpo_1 = conMarksys.getConn().createStatement(ResultSet.TYPE_SCROLL_SENSITIVE, ResultSet.CONCUR_READ_ONLY);
                            if (stmntpo_1.executeUpdate(m_strqry_1) <= 0) {
                                throw new Exception(" Error occured in updating  in rms_doc_txnd");
                            }
                            m_msgbodyHead += " <tr>"
                                    //                                    + "     <td align=\"left\">" + hashmIt.get("TXN_DATE") + "</td>"
                                    + "     <td align=\"left\">" + hashmIt.get("ITEM_CODE") + "</td>"
                                    + "     <td align=\"left\">" + hashmIt.get("ITM_DESC") + "</td>"
                                    + "     <td align=\"left\"> " + hashmIt.get("QTY") + "</td>"
                                    + "     <td align=\"left\">" + hashmIt.get("EQTY") + "</td>"
                                    + " </tr> ";

                            m_msgpobody2 += " <tr>"
                                    //                                    + "     <td align=\"left\">" + hashmIt.get("TXN_DATE") + "</td>"
                                    + "     <td align=\"left\">" + hashmIt.get("ITEM_CODE") + "</td>"
                                    + "     <td align=\"left\">" + hashmIt.get("ITM_DESC") + "</td>"
                                    + "     <td align=\"left\"> " + hashmIt.get("QTY") + "</td>"
                                    + "     <td align=\"left\">" + hashmIt.get("EQTY") + "</td>"
                                    + " </tr> ";
                            supName = hashmIt.get("name");
                            distributerHeadEmail = hashmIt.get("hemail");
                            distributerName = hashCData.getKey().toString().split("__")[1];
                            distributerEmail = hashmIt.get("EMAIL");
                            TotalEAmountDIS += Double.parseDouble(hashmIt.get("EAMOUNT"));

                        }
                        String m_strqry = "update rms_doc_txnm "
                                + " set mstat = 'EXP' "
                                + " where sbu_code = '" + hashmIt.get("SBU_CODE") + "' and "
                                + " loc_code = '" + hashmIt.get("LOC_CODE") + "' and "
                                + " doc_code = '" + hashmIt.get("DOC_CODE") + "' and "
                                + " doc_no = '" + hashmIt.get("DOC_NO") + "' ";

//                        System.out.printlnSystem.out.println("--" + "rms_doc_txnm");
//                        System.out.println("--" + m_strqry);
//                        System.out.println("/////////////////////////////////////////////////////////");
                        stmntpo_2 = conMarksys.getConn().createStatement(ResultSet.TYPE_SCROLL_SENSITIVE, ResultSet.CONCUR_READ_ONLY);
                        if (stmntpo_2.executeUpdate(m_strqry) <= 0) {
                            throw new Exception(" Error occured in updating  in rms_doc_txnm");
                        }

                        m_msgpobody2 += " <tr>"
                                + "    <td align=\"left\" colspan=\"2\"><span style=\"font-weight: bold;font-size: 14px;\">Total Amount Of Expire Order </span></td>"
                                + "    <td  align=\"right\"  colspan=\"2\"><span style=\"font-weight: bold;font-size: 14px;\">" + setDecimalSplitor(round(TotalEAmountDIS, 8)) + " </span></td>"
                                + " </tr> ";
                        m_msgbodyHead += " </tr> ";

                        m_msgbodyHead += " <tr>"
                                + "    <td align=\"left\" colspan=\"2\"><span style=\"font-weight: bold;font-size: 14px;\">Total Amount Of Expire Order  </span></td>"
                                + "    <td  align=\"right\" colspan=\"2\"><span style=\"font-weight: bold;font-size: 14px;\">" + setDecimalSplitor(round(TotalEAmountDIS, 8)) + " </span></td>"
                                + " </tr> ";
                        TotalEAmountASM += TotalEAmountDIS;
                        TotalEAmountDISTot += TotalEAmountDIS;
                        TotalEAmountDIS = 0.0;

                        if (endTransactionMarksys() == 0) {
                            throw new Exception("Error in starting txn.");
                        }

                    }
                    m_msgpobody2 += " <tr>"
                            + "    <td align=\"left\" colspan=\"2\"><span style=\"font-weight: bold;font-size: 14px;\">All Amount Of Expire Orders </span></td>"
                            + "    <td  align=\"right\"  colspan=\"2\"><span style=\"font-weight: bold;font-size: 14px;\">" + setDecimalSplitor(round(TotalEAmountDISTot, 8)) + " </span></td>"
                            + " </tr> ";
                    TotalEAmountDISTot = 0.00;

                    m_msgpobody2 += "</table><br>";
                    String m_msgpobody = "<strong>List of Expired Purchase Orders Of " + supName + "</strong>";
//                    System.out.println(m_msgpobody2);
//                    sendEmailPurchaseOrders("menaka.p@arpico.com", "salesorder.exp@arpico.com", "Expired Purchase Orders - Richard Pieris & Company ", m_msgpobody, m_msgpobody2, "<br><br><br> This is an automated message from Purchase order expiry process.");
                    if (distributerEmail != null) {
                        sendEmailPurchaseOrders(distributerEmail, "e_marksys@arpico.com", "Expired Purchase Orders - Richard Pieris & Company ", m_msgpobody, m_msgpobody2, "<br><br><br> This is an automated message from Purchase order expiry process.");
                    }

                }
                m_msgbodyHead += " <tr>"
                        + "    <td align=\"left\" colspan=\"2\"><span style=\"font-weight: bold;font-size: 14px;\">"
                        //                            + "" + hashHData.getKey().toString().split("_")[1].split("-")[1] + " "
                        + "Total Expire Amount  </span></td>"
                        + "    <td  align=\"right\" colspan=\"2\"><span style=\"font-weight: bold;font-size: 14px;\">" + setDecimalSplitor(round(TotalEAmountASM, 8)) + " </span></td>"
                        + " </tr> ";

                m_msgbodyHead += "</table><br>";
//                System.out.println("\n ");
//                System.out.println("ASM Send Email " + distributerHeadEmail);
//                System.out.println(m_msgbodyHead);
                TotalEAmountDIS = 0.0;

//                sendEmailPurchaseOrders(""
//                        //                                + "" + distributerHeadEmail + ","
//                        + "menaka.p@arpico.com", "salesorder.exp@arpico.com", "Expired Purchase Orders (ASM) - Richard Pieris & Company ", "<strong>List of Expired Purchase Orders Of "
//                        + "" + hashHData.getKey().toString().split("_")[1].split("-")[0] + " "
//                        + "" + hashHData.getKey().toString().split("_")[0] + " "
//                        + "" + hashHData.getKey().toString().split("_")[1].split("-")[1]
//                        + "</strong>", m_msgbodyHead, "<br><br><br> This is an automated message from Purchase order expiry process.");
                if (distributerHeadEmail != null) {
                    sendEmailPurchaseOrders(""
                            + "" + distributerHeadEmail, "e_marksys@arpico.com", "Expired Purchase Orders (ASM) - Richard Pieris & Company ", "<strong>List of Expired Purchase Orders Of "
                            + "" + hashHData.getKey().toString().split("_")[1].split("-")[0] + " "
                            + "" + hashHData.getKey().toString().split("_")[0] + " "
                            + "" + hashHData.getKey().toString().split("_")[1].split("-")[1]
                            + "</strong>", m_msgbodyHead, "<br><br><br> This is an automated message from Purchase order expiry process.");

                } else {
                    if (!hashNoAddressHead.containsKey(hashHData.getKey())) {
                        hashNoAddressHead.put(hashHData.getKey().toString(), hashHData.getKey().toString().split("-")[1]);
                    }
                }

                distributerHeadEmail = "";
                distributerName = "";
                System.out.println("\n");
                TotalEAmountASM = 0.0;

            }

            if (hashNoAddress.size() > 0 || hashNoAddressHead.size() > 0) {

                String ListBody = "<br><table border=\"2\" style='text-align: center;central;font-size: 13px;'>"
                        + "<tr>"
                        + "<br>"
                        + "     <th align=\"left\" style='width: 200px;'>SBU CODE</td>"
                        + "     <th align=\"left\" style='width: 200px;'>Customer CODE / Area Code</td>"
                        + "     <th align=\"left\" style='width: 300px;'> CUSTOMER / Head Of distributer </td>"
                        + " </tr>";
                for (Map.Entry hashNoAddressData : hashNoAddress.entrySet()) {

                    ListBody += " <tr>"
                            + "     <td align=\"left\">" + hashNoAddressData.getKey().toString().split("_")[0] + "</td>"
                            + "     <td align=\"left\">" + hashNoAddressData.getKey().toString().split("_")[1] + "</td>"
                            + "    <td align=\"left\">" + hashNoAddressData.getValue() + "</td>"
                            + " </tr> ";
                }
                if (hashNoAddressHead.size() > 0) {

                    ListBody += " <tr>"
                            + "     <td colspan=\"3\" align=\"left\"><br></td>"
                            + " </tr> ";
                    ListBody += " <tr>"
                            + "     <td colspan=\"3\" >ASM Email Address Blank List</td>"
                            + " </tr> ";
                    for (Map.Entry hashNoAddressDataHead : hashNoAddressHead.entrySet()) {

                        ListBody += " <tr>"
                                + "     <td align=\"left\">" + hashNoAddressDataHead.getKey().toString().split("_")[1].toString().split("-")[0] + "</td>"
                                + "     <td align=\"left\">" + hashNoAddressDataHead.getKey().toString().split("_")[0] + "</td>"
                                + "    <td align=\"left\">" + hashNoAddressDataHead.getValue() + "</td>"
                                + " </tr> ";
                    }
                }

                ListBody += "</table>";

                sendEmailPurchaseOrders(getEmailAddressPurchaseOrders(), "e_marksys@arpico.com", "Blank Email Address List for Expired Purchase Orders :- ", "Customer Details ", ListBody, "<br><br><br> This is an automated message from Purchase order expiry process Email ERR Customer List .");
//                sendEmailPurchaseOrders("menaka.p@arpico.com,sudeepf@arpico.com", "salesorder.exp@arpico.com", "Blank Email Address List for Expired Purchase Orders :- ", "Customer Details ", ListBody, "<br><br><br> This is an automated message from Purchase order expiry process Email ERR Customer List .");
            }

        } catch (Exception ex) {
            ex.printStackTrace();
            abortTransactionMarksys();
        } finally {
            try {
                if (rspo != null) {
                    rspo.close();
                    rspo = null;
                }
                if (stmntpo != null) {
                    stmntpo.close();
                    stmntpo = null;
                }
                if (stmntpo_1 != null) {
                    stmntpo_1.close();
                    stmntpo_1 = null;
                }
                if (stmntpo_2 != null) {
                    stmntpo_2.close();
                    stmntpo_2 = null;
                }
                if (conMarksys != null) {
                    conMarksys.getConn().close();
                    conMarksys = null;
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }

    }

    public void pendingPR() {

        String m_msgbody1 = "", m_msgheader = "", m_msgbody2 = "";
        HashMap<String, HashMap<String, HashMap<String, String>>> hashPendingPRApprovalHead;
        HashMap<String, HashMap<String, String>> hashPendingPRApproval;
        HashMap<String, String> hashPendingPRApprovalData;
        

        Statement stmnt = null;
        ResultSet rs = null;
         try {
             
        String userEmpNo = getUserListPRApproval();
        
        

        String sql = "SELECT distinct COMCOD com_sbu,(REQSNO)Pr_no,to_char(a.REQDAT,'DD/MM/YYYY') pr_date,b.sbu_name,nxtapp FROM purchasereqs a\n"
                + "inner join rms_sbu b on a.comcod=b.sbu_code\n"
                + "where nxtapp in ("+userEmpNo+")  and status<20\n"
                + "order by com_sbu,COMCOD,reqsno";

        sql = "select com_sbu,Pr_no,pr_date,sbu_name,nxtapp from\n"
                + "(SELECT distinct COMCOD com_sbu,(REQSNO)Pr_no,to_char(a.REQDAT,'DD/MM/YYYY') pr_date,b.sbu_name,nxtapp \n"
                + "FROM LMD.purchasereqs a\n"
                + "inner join LMD.rms_sbu b on a.comcod=b.sbu_code\n"
                + "where nxtapp in ("+userEmpNo+")  and status<20\n"
                + "UNION ALL\n"
                + "SELECT distinct COMCOD com_sbu,(REQSNO)Pr_no,to_char(a.REQDAT,'DD/MM/YYYY') pr_date,b.sbu_name,nxtapp \n"
                + "FROM MARKSYS.purchasereqs a\n"
                + "inner join MARKSYS.rms_sbu b on a.comcod=b.sbu_code\n"
                + "where nxtapp in ("+userEmpNo+")  and status<20)c\n"
                + "order by com_sbu,Pr_no";

       
            conMarksys = new OracleConnector("192.168.1.27", "rpd2", "LMD", new jText.TextUti().getText("oracle"));

            stmnt = conMarksys.getConn().createStatement(ResultSet.TYPE_SCROLL_SENSITIVE, ResultSet.CONCUR_READ_ONLY);
            rs = stmnt.executeQuery(sql);

            if (!rs.next()) {
                throw new Exception("err");
            }
            rs.beforeFirst();
            hashPendingPRApprovalHead = new HashMap<>();

            while (rs.next()) {
                if (!hashPendingPRApprovalHead.containsKey(rs.getString("COM_SBU") + "__" + rs.getString("SBU_NAME") + "@@" + rs.getString("nxtapp"))) {
                    hashPendingPRApproval = new HashMap<>();
                    hashPendingPRApprovalData = new HashMap<>();

                    hashPendingPRApprovalData.put("COM_SBU", rs.getString("COM_SBU"));
                    hashPendingPRApprovalData.put("SBU_NAME", rs.getString("SBU_NAME"));
                    hashPendingPRApprovalData.put("PR_NO", rs.getString("PR_NO"));
                    hashPendingPRApprovalData.put("PR_DATE", rs.getString("PR_DATE"));
                    hashPendingPRApproval.put(rs.getString("PR_NO"), hashPendingPRApprovalData);
                    hashPendingPRApprovalHead.put(rs.getString("COM_SBU") + "__" + rs.getString("SBU_NAME") + "@@" + rs.getString("nxtapp"), hashPendingPRApproval);
                } else {
                    hashPendingPRApproval = hashPendingPRApprovalHead.get(rs.getString("COM_SBU") + "__" + rs.getString("SBU_NAME") + "@@" + rs.getString("nxtapp"));
                    hashPendingPRApprovalData = new HashMap<>();
                    hashPendingPRApprovalData.put("COM_SBU", rs.getString("COM_SBU"));
                    hashPendingPRApprovalData.put("SBU_NAME", rs.getString("SBU_NAME"));
                    hashPendingPRApprovalData.put("PR_NO", rs.getString("PR_NO"));
                    hashPendingPRApprovalData.put("PR_DATE", rs.getString("PR_DATE"));

                    hashPendingPRApproval.put(rs.getString("PR_NO"), hashPendingPRApprovalData);
                    hashPendingPRApprovalHead.replace(rs.getString("COM_SBU") + "__" + rs.getString("SBU_NAME") + "@@" + rs.getString("nxtapp"), hashPendingPRApproval);

                }
            }

            m_msgbody1 = "<br><table border=\"1\" style='text-align: center;central;font-size: 13px;'>"
                    + "<tr>"
                    + "<br>"
                    + "    <td align=\"left\" colspan=\"2\"><span style=\"font-weight: bold;font-size: 14px;\">Pending for checking </span></td>"
                    + " </tr>"
                    + "<tr>"
                    + "<br>"
                    + "     <th  style='width: 70px;'>PR DATE</td>"
                    + "     <th  style='width: 250px;'>PR NO</td>"
                    + " </tr>";
            m_msgbody2 = "<table border=\"1\" style='text-align: center;central;font-size: 13px;'>"
                    + "<tr>"
                    + "<br>"
                    + "    <td align=\"left\" colspan=\"2\"><span style=\"font-weight: bold;font-size: 14px;\">Pending GCFO approval</span></td>"
                    + " </tr>"
                    + "<tr>"
                    + "<br>"
                    + "     <th  style='width: 70px;'>PR DATE</td>"
                    + "     <th  style='width: 250px;'>PR NO</td>"
                    + " </tr>";

            //FOR SORTING
            Map<String, HashMap<String, HashMap<String, String>>> treeMap = new TreeMap<String, HashMap<String, HashMap<String, String>>>(hashPendingPRApprovalHead);
            for (Map.Entry hashHData : treeMap.entrySet()) {
                hashHData.getKey();
                if (hashHData.getKey().toString().split("@@")[1].equals(userEmpNo.split(",")[0].replace("'", ""))) {
                    HashMap<String, HashMap<String, String>> hm = (HashMap<String, HashMap<String, String>>) hashHData.getValue();
                    m_msgbody2 += " <tr>"
                            + "    <td align=\"left\" colspan=\"2\"><span style=\"font-weight: bold;font-size: 14px;\">" + hashHData.getKey().toString().split("__")[0] + "\t:- " + hashHData.getKey().toString().split("__")[1].split("@@")[0] + "</span></td>"
                            + " </tr> ";
                    for (Map.Entry hashHDataap : hm.entrySet()) {
                        HashMap<String, String> hashMap = (HashMap<String, String>) hashHDataap.getValue();
                        m_msgbody2 += " <tr>"
                                + "     <td align=\"left\">" + hashMap.get("PR_DATE") + "</td>"
                                + "     <td align=\"left\">" + hashMap.get("PR_NO") + "</td>"
                                + " </tr> ";
                    }
                } else if (hashHData.getKey().toString().split("@@")[1].equals(userEmpNo.split(",")[1].replace("'", ""))) {
                    HashMap<String, HashMap<String, String>> hm = (HashMap<String, HashMap<String, String>>) hashHData.getValue();
                    m_msgbody1 += " <tr>"
                            + "    <td align=\"left\" colspan=\"2\"><span style=\"font-weight: bold;font-size: 14px;\">" + hashHData.getKey().toString().split("__")[0] + "\t:- " + hashHData.getKey().toString().split("__")[1].split("@@")[0] + "</span></td>"
                            + " </tr> ";
                    for (Map.Entry hashHDataap : hm.entrySet()) {
                        HashMap<String, String> hashMap = (HashMap<String, String>) hashHDataap.getValue();

                        m_msgbody1 += " <tr>"
                                + "     <td align=\"left\">" + hashMap.get("PR_DATE") + "</td>"
                                + "     <td align=\"left\">" + hashMap.get("PR_NO") + "</td>"
                                + " </tr> ";

                    }
                }

            }
            m_msgbody1 += "</table>";
            m_msgbody2 += "</table><br>";
            System.out.println(m_msgbody1 + "" + m_msgbody2);
//            sendEmailPendingPRApproval(getEmailAddressPendingPRApproval(), "salesorder.exp@arpico.com", "Pending PR Approval of GCFO", "Please see below for pending PR list.", m_msgbody1 + "" + m_msgbody2, "This is System Generated email.For any queries please contact the sender of this mail.<br><br>Regards,<br><br>");
//trevin@arpico.com,sudeepf@arpico.com,   menaka.p@arpico.com,trevin@arpico.com          
            sendEmailPendingPRApproval(getEmailAddressPendingPRApproval(), "e_marksys@arpico.com", "Pending PR Approval of GCFO", "Please see below for pending PR list.", m_msgbody1 + "" + m_msgbody2, "This is System Generated email.For any queries please contact Group IT - Application Support Team.<br><br>Regards,<br><br>");

        } catch (Exception ex) {
            Logger.getLogger(CRMPointsUpdateWithPOPR.class.getName()).log(Level.SEVERE, null, ex);
        } finally {
            try {
                if (rs != null) {
                    rs.close();
                    rs = null;
                }
                if (stmnt != null) {
                    stmnt.close();
                    stmnt = null;
                }
                if (conMarksys != null) {
                    conMarksys.getConn().close();
                    conMarksys = null;
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }
    ///////////////////////////////////////////////////////////////////////////////////////////////////////
//        Statement stmnt = null;
//        ResultSet rs = null;
//        String m_msgbody2 = "", m_msgheader = "";
//
//        HashMap<String, HashMap<String, HashMap<String, String>>> hashPendingPRApprovalHead;
//        HashMap<String, HashMap<String, String>> hashPendingPRApproval;
//        HashMap<String, String> hashPendingPRApprovalData;
//
//        String sql = "SELECT distinct COMCOD com_sbu,(REQSNO)Pr_no,to_char(a.REQDAT,'DD/MM/YYYY') pr_date,b.sbu_name FROM purchasereqs a\n"
//                + "inner join rms_sbu b on a.comcod=b.sbu_code\n"
//                + "where   nxtapp='1200'  and status<20\n"
//                + "order by COMCOD,reqsno";
//
//        try {
//            conMarksys = new OracleConnector("192.168.1.27", "rpd2", "LMD", new jText.TextUti().getText("oracle"));
//            if (startTransactionMarksys() == 0) {
//                throw new Exception(_exception.getLocalizedMessage());
//            }
//
//            stmnt = conMarksys.getConn().createStatement(ResultSet.TYPE_SCROLL_SENSITIVE, ResultSet.CONCUR_READ_ONLY);
//            rs = stmnt.executeQuery(sql);
//
//            int t = 0;
//            if (!rs.next()) {
//                throw new Exception("err");
//            }
//            rs.beforeFirst();
//            hashPendingPRApprovalHead = new HashMap<>();
//            while (rs.next()) {
//                if (!hashPendingPRApprovalHead.containsKey(rs.getString("COM_SBU") + "__" + rs.getString("SBU_NAME"))) {
//                    hashPendingPRApproval = new HashMap<>();
//                    hashPendingPRApprovalData = new HashMap<>();
//
//                    hashPendingPRApprovalData.put("COM_SBU", rs.getString("COM_SBU"));
//                    hashPendingPRApprovalData.put("SBU_NAME", rs.getString("SBU_NAME"));
//                    hashPendingPRApprovalData.put("PR_NO", rs.getString("PR_NO"));
//                    hashPendingPRApprovalData.put("PR_DATE", rs.getString("PR_DATE"));
//                    hashPendingPRApproval.put(rs.getString("PR_NO"), hashPendingPRApprovalData);
//                    hashPendingPRApprovalHead.put(rs.getString("COM_SBU") + "__" + rs.getString("SBU_NAME"), hashPendingPRApproval);
//                } else {
//                    hashPendingPRApproval = hashPendingPRApprovalHead.get(rs.getString("COM_SBU") + "__" + rs.getString("SBU_NAME"));
//                    hashPendingPRApprovalData = new HashMap<>();
//                    hashPendingPRApprovalData.put("COM_SBU", rs.getString("COM_SBU"));
//                    hashPendingPRApprovalData.put("SBU_NAME", rs.getString("SBU_NAME"));
//                    hashPendingPRApprovalData.put("PR_NO", rs.getString("PR_NO"));
//                    hashPendingPRApprovalData.put("PR_DATE", rs.getString("PR_DATE"));
//
//                    hashPendingPRApproval.put(rs.getString("PR_NO"), hashPendingPRApprovalData);
//                    hashPendingPRApprovalHead.replace(rs.getString("COM_SBU") + "__" + rs.getString("SBU_NAME"), hashPendingPRApproval);
//
//                }
//            }
//            m_msgbody2 = "<br><table border=\"1\" style='text-align: center;central;font-size: 13px;'>"
//                    + "<tr>"
//                    + "<br>" //, , ,                  
//                    + "     <th  style='width: 70px;'>PR DATE</td>"
//                    + "     <th  style='width: 250px;'>PR NO</td>"
//                     + " </tr>";
//
//            for (Map.Entry hashHData : hashPendingPRApprovalHead.entrySet()) {
//                hashHData.getKey();
//
//                HashMap<String, HashMap<String, String>> hm = (HashMap<String, HashMap<String, String>>) hashHData.getValue();
//                m_msgbody2 += " <tr>"
//                        + "    <td align=\"left\" colspan=\"2\"><span style=\"font-weight: bold;font-size: 14px;\">" + hashHData.getKey().toString().split("__")[0] + "\t:- " + hashHData.getKey().toString().split("__")[1] + "</span></td>"
//                        + " </tr> ";
//                for (Map.Entry hashHDataap : hm.entrySet()) {
//                    HashMap<String, String> hashMap = (HashMap<String, String>) hashHDataap.getValue();
//
//                    m_msgbody2 += " <tr>"
//                         + "     <td align=\"left\">" + hashMap.get("PR_DATE") + "</td>"
//                            + "     <td align=\"left\">" + hashMap.get("PR_NO") + "</td>"
//                            + " </tr> ";
//
//                }
//
//            }
//            m_msgbody2 += "</table><br>";
//            System.out.println(m_msgbody2);
//            sendEmailPendingPRApproval(getEmailAddressPendingPRApproval(), "salesorder.exp@arpico.com", "Pending PR Approval of GCFO", "Please see below for pending PR list.", m_msgbody2, "This is System Generated email.For any queries please contact the sender of this mail.<br><br>Regards,<br><br>");
//
//        } catch (Exception ex) {
//            Logger.getLogger(CRMPointsUpdate.class.getName()).log(Level.SEVERE, null, ex);
//        }
//}

    public void sendEmailPendingPRApproval(String prm_strNextTo, String prm_strNextAppemail, String prm_strHeader, String prm_strBody1, String prm_strBody2,
            String prm_strLastAppEmail) {
        try {
            CreateMailPurchaseOrders(prm_strNextTo, prm_strNextAppemail, "", prm_strHeader, "", "", "",
                    //            CreateMailPurchaseOrders(getEmailAddressPurchaseOrders(), "salesorder.exp@arpico.com", "", prm_strHeader, "", "", "",
                    prm_strBody1, prm_strBody2, prm_strLastAppEmail, "");
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }

    public String getEmailAddressPurchaseOrders() {
        String m_strQty = "";
        String condition = "";
        String m_sql = "";
        try {
            conMarksys1 = new OracleConnector("192.168.1.27", "rpd2", "LMD", new jText.TextUti().getText("oracle"));
            Statement m_stmnt = conMarksys.getConn().createStatement(ResultSet.TYPE_SCROLL_SENSITIVE, ResultSet.CONCUR_READ_ONLY);

            m_sql = "select cval01 from smsyspara where sbucod = 'XXX' and loccod = 'XXX' and parcod = 'POERR'";

            ResultSet rs = m_stmnt.executeQuery(m_sql);
            if (rs.next()) {
                m_strQty = rs.getString("cval01");
            } else {
                throw new Exception("Please Set the Email Informations.");
            }
            if (m_stmnt != null) {
                m_stmnt.close();
                m_stmnt = null;
            }
            if (rs != null) {
                rs.close();
                rs = null;
            }
        } catch (Exception e) {
            _exception = e;
            e.printStackTrace();
        }
        return m_strQty;
    }

    public String getEmailAddressPendingPRApproval() {
        String m_strQty = "";
        String condition = "";
        String m_sql = "";
        try {
            conMarksys1 = new OracleConnector("192.168.1.27", "rpd2", "LMD", new jText.TextUti().getText("oracle"));
            Statement m_stmnt = conMarksys.getConn().createStatement(ResultSet.TYPE_SCROLL_SENSITIVE, ResultSet.CONCUR_READ_ONLY);

            m_sql = "select cval01 from smsyspara where sbucod = 'XXX' and loccod = 'XXX' and parcod = 'PRMAIL'";

            ResultSet rs = m_stmnt.executeQuery(m_sql);
            if (rs.next()) {
                m_strQty = rs.getString("cval01");
            } else {
                throw new Exception("Please Set the Email Informations.");
            }
            if (m_stmnt != null) {
                m_stmnt.close();
                m_stmnt = null;
            }
            if (rs != null) {
                rs.close();
                rs = null;
            }
        } catch (Exception e) {
            _exception = e;
            e.printStackTrace();
        }
        return m_strQty;
    }
    
        public String getUserListPRApproval() throws SQLException {
            
            
        String m_strQty = "";
        String m_sql = "";
        ResultSet rs = null;
        Statement m_stmnt = null;
        try {
            conMarksys1 = new OracleConnector("192.168.1.27", "rpd2", "LMD", new jText.TextUti().getText("oracle"));
             m_stmnt = conMarksys1.getConn().createStatement(ResultSet.TYPE_SCROLL_SENSITIVE, ResultSet.CONCUR_READ_ONLY);

            m_sql = "SELECT CVAL01 FROM smsyspara WHERE SBUCOD = 'XXX' AND LOCCOD = 'XXX' AND PARCOD='PRUSERS'";

             rs = m_stmnt.executeQuery(m_sql);
            if (rs.next()) {
                m_strQty = rs.getString("CVAL01");
                
            } else {
                throw new Exception("Please Set the User Informations.");
            }
          
        } catch (Exception e) {
            _exception = e;
            e.printStackTrace();
        }finally{
              if (m_stmnt != null) {
                m_stmnt.close();
                m_stmnt = null;
            }
            if (rs != null) {
                rs.close();
                rs = null;
            }
        }
        return m_strQty;
    }
}
