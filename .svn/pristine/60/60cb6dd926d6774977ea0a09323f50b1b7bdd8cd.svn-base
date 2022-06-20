/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package executables;

/**
 *
 * @author eranda.ak
 */
import dataaccess.OracleConnector;

import java.net.Inet4Address;
import java.net.InetAddress;
import java.net.NetworkInterface;
import java.net.URL;
import java.sql.Statement;
import java.sql.ResultSet;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.Properties;
import javax.mail.Message;
import javax.mail.Multipart;
import javax.mail.Session;
import javax.mail.Transport;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeBodyPart;
import javax.mail.internet.MimeMessage;
import javax.mail.internet.MimeMultipart;
import net.sf.jasperreports.engine.JasperExportManager;
import net.sf.jasperreports.engine.JasperFillManager;
import net.sf.jasperreports.engine.JasperPrint;
import net.sf.jasperreports.engine.JasperReport;
import net.sf.jasperreports.engine.util.JRLoader;
//import utility.GetSequence;

public class FurWHAutoOrderCreation {

    private Exception _exception;
    // private DBConnection 
    OracleConnector _dbconnection;
//    private AuditInformation _audit;
//    private RowSet _rset;
    private Statement _stmt, _stmt2;
    private ResultSet _rs;
//    private EntityGenerator _entgenShareLedger;//entity generator for share ledger
//    private DBCommandBuilder cmdbulder; //command Builder
    private String[] _strColumnHeader;
    private String[] _strColumnName;
    private int m_strseq;
    private Integer m_DocNo;
    // GetSequence _seqno;
    private String _strIpAddress;
    private String _strSbuCod, _strLocCod;

    public FurWHAutoOrderCreation() {
        _exception = null;
    }

    public String getEnclosedText(String prm_syspara) {
        try {

            String wk_string = "";
            String wk_prarray = "";
            String[] arrpara;
            Statement _stmt3 = _dbconnection.getConn().createStatement();

            String m_query = "select * from rms_sys_parameters where sbu_code='" + _strSbuCod + "' and"
                    + " loc_code='" + _strLocCod + "' and para_code='" + prm_syspara + "'";
            ResultSet rset = _stmt3.executeQuery(m_query);

            if (!rset.next()) {
                throw new Exception("Sys.Parameters not found..!");
            }
            wk_prarray = rset.getString("comments");

            if (wk_prarray.equals("")) {
                throw new Exception("Unable to find parameter:" + prm_syspara);
            }
            arrpara = wk_prarray.split(";");
            for (int i = 0; i < arrpara.length; i++) {
                wk_string = wk_string.concat("'").concat(arrpara[i]).concat("'").concat(",");
            }
            if (wk_string.length() > 0) {
                wk_string = wk_string.substring(0, wk_string.length() - 1);
            }
            if (rset != null) {
                rset.close();
                rset = null;

            }
            if (_stmt3 != null) {
                _stmt3.close();
                _stmt3 = null;

            }
            return wk_string;
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }

    }

    public int FurOrdCreationProcess() {
        //SICFUR/MRPF/SIC Furniture Ord.Creation/L02117/L02118
        ResultSet m_rsetCommon = null;
        String m_toEmailList = "";
        String m_currentDate = "";
        String prm_sbucod = "830";
        String prm_loccod = "40";
        String prm_sysparcod = "SICFUR";
        String prm_strDocCode = "MRPF";
        String prm_logComment = "Furniture_Auto_Order";
        String prm_fromGrp = "L02117";
        String prm_toGrp = "L02118";
        _strSbuCod = prm_sbucod;
        _strLocCod = prm_loccod;
        try {
            _dbconnection = new OracleConnector("192.168.1.27", "RPD2", "MARKSYS", new jText.TextUti().getText("oracle"));
            _stmt = _dbconnection.getConn().createStatement(ResultSet.TYPE_SCROLL_INSENSITIVE, ResultSet.CONCUR_READ_ONLY);
            _stmt2 = _dbconnection.getConn().createStatement(ResultSet.TYPE_SCROLL_INSENSITIVE, ResultSet.CONCUR_READ_ONLY);

            // GET EMAIL LIST//
            String _strSql = "select to_char(sysdate,'DD-Mon-YYYY') as datea,CVAL01 as comments from SMSYSPARA where "
                    + "sbucod='" + prm_sbucod + "' and loccod='" + prm_loccod + "' "
                    + "and parcod='FUREMAIL'";
            m_rsetCommon = _stmt.executeQuery(_strSql);
            if (m_rsetCommon.next()) {
                m_toEmailList = m_rsetCommon.getString("comments");
                m_currentDate = m_rsetCommon.getString("datea");
            }

            // GET LAST PROCCESSING DATE //
            _strSql = "select * from rms_system_log where "
                    + "sbu_code='" + prm_sbucod + "' and loc_code='" + prm_loccod + "' "
                    + "and program_id='" + prm_sysparcod + "' and log_date='" + m_currentDate + "'";

            m_rsetCommon = _stmt.executeQuery(_strSql);

            if (m_rsetCommon.next()) {
                m_rsetCommon.close();
                throw new Exception("Process Already done for today.");
            }

            if (m_rsetCommon != null) {
                m_rsetCommon.close();
                m_rsetCommon = null;
            }
            if (_stmt != null) {
                _stmt.close();
                _stmt = null;
            }

            if (startTransaction() == 0) {
                throw new Exception(getException().getLocalizedMessage());
            }
            _stmt = _dbconnection.getConn().createStatement(ResultSet.TYPE_SCROLL_INSENSITIVE, ResultSet.CONCUR_READ_ONLY);

            _strSql = "select x.item_code,i.itm_desc,i.ws_price,"
                    + " max(month1q) as month_1_qty,"
                    + " max(month2q) as month_2_qty,"
                    + " max(month3q) as month_3_qty,"
                    + " round((max(month1q)*.5+max(month2q)*.3+max(month3q)*.2)/30,0) as avg_sales,"
                    + " sum( case when d.loc_Code not in ('41','40','140','240') then d.on_hand_q else 0 end ) as loc_stock,"
                    + " sum( case when d.loc_Code in ('40','140','240') then d.on_hand_q else 0 end ) as DC_stock,"
                    + " sum( case when d.loc_Code not in ('41') then d.on_hand_q else 0 end ) as tot_stock,"
                    + " round((max(month1q)*.5 + max(month2q)*.3 + max(month3q)*.2 )*3,0) as D90Sales,"//  90
                    + " round((max(month1q)+ max(month2q)+ max(month3q))/3,0) as avg30Sales,"//  30
                    + " max(i.lst_grn_date) as last_grn_date,max(last_grn_price) as last_grn_price,max(i.SUPPLIER) as SUPPLIER,"
                    + " max(i.unit) as unit,max(d.on_hand_q) as QTYONH,max(i.inv_on_order) as QTYONO,max(i.re_ord_pnt) as REORPT,"
                    + " max(i.max_invent) as MAXINV,max(i.unitise_pack) as UNIPAK,'0.0000' as RPLQTY, max(i.itm_group) itm_group,"
                    + " max(i.cost_price) as PURPRI,'0.0000' as SYSQTY, 'SIC Order Gen 4 Fur.Items' as HEDREM,'SIC Order Gen 4 Fur.Items' as DETREM, "
                    + " max(i.buyer) as BUYER,'" + m_currentDate + "' as POEXPD,"
                    + " max(i.tax_code) as  TAXCD1,max(i.tax_code2) as  TAXCD2,max(i.tax_code3) as  TAXCD3,max(i.tax_code4) as  TAXCD4,"
                    + " '' CUSNAM,max(i.itm_desc) as ITMDES, max(i.unit_price) as RETPRI"
                    + " from"
                    + " ("
                    + " select b.item_code,"
                    + " sum(case when a.txn_date between sysdate - 31 and sysdate -1 then qty*-1 else 0 end) as month1q,"
                    + " sum(case when a.txn_date between sysdate - 62 and sysdate -32 then qty*-1 else 0 end) as month2q,"
                    + " sum(case when a.txn_date between sysdate - 93 and sysdate -63 then qty*-1 else 0 end) as month3q"
                    + " from rms_doc_txnm a inner join rms_doc_txnd b on a.sbu_code=b.sbu_code and"
                    + " a.loc_code=b.loc_code and a.doc_code=b.doc_code and a.doc_no=b.doc_no"
                    + " where a.sbu_code='" + prm_sbucod + "' and a.txn_date between sysdate-94 and sysdate -1 and "
                    + " a.doc_code in ('POSS','POSR'," + getEnclosedText("SYSINVOICE") + "," + getEnclosedText("SYSCRDN") + ") and "
                    + "    b.itmgrp between '" + prm_fromGrp + "' and '" + prm_toGrp + "' " //'L02117' and 'L02118'
                    + " group by b.item_code"
                    + " ) x"
                    + " inner join rms_stockloc_inv d on d.sbu_code='" + prm_sbucod + "' and d.item_code=x.item_code"
                    + " inner join rms_itmmaster i on i.sbu_code='" + prm_sbucod + "' and i.item_code=x.item_code"
                    + " where i.buyer like '%'"
                    + " group by x.item_code,i.itm_desc,i.ws_price";

            ResultSet rs = _stmt.executeQuery(_strSql);
            boolean m_bolFound = false;
            if (!rs.next()) {
                throw new Exception("Data not found for furniture order creation.");
            }
            rs.beforeFirst();

            // PREVIOUS DATA DELETE
            _strSql = "INSERT INTO RMS_REQUISITIONS_HIST "
                    + "(SBU_CODE, LOC_CODE, DOC_CODE, REQ_NO, ITEM_CODE, ORDQTY, ORD_DATE, DEL_DATE, SUPPLIER, UNIT, AUT_BY, AUT_DATE, REQ_STAT, TRANS_STAT, TRANS_BY, TRANS_DATE, CRE_BY, CRE_DATE, MOD_BY, MOD_DATE, ALT_DOC_CODE, ALT_DOC_NO, ALT_SEQ_NO, LINE_STATUS, DESLOC, TRNLOC, QTYONH, QTYONO, REORPT, MAXINV, UNIPAK, RPLQTY, ITMGRP, PURPRI, SYSQTY, HEDREM, DETREM, BUYER, POEXPD, PRWKSL, PRMNSL, PRYRSL, STKOUT, TAXCD1, TAXCD2, TAXCD3, TAXCD4, TAXRT1, TAXRT2, TAXRT3, TAXRT4, TAXAMT1, TAXAMT2, TAXAMT3, TAXAMT4, RETPRI, CUSCOD, CUSNAM, ITMDES, ADVRNO, ADVRAT)"
                    + " select SBU_CODE, LOC_CODE, DOC_CODE, REQ_NO, ITEM_CODE, ORDQTY, ORD_DATE, DEL_DATE, SUPPLIER, UNIT, AUT_BY, AUT_DATE, REQ_STAT, TRANS_STAT, TRANS_BY, TRANS_DATE, CRE_BY, CRE_DATE, MOD_BY, MOD_DATE, ALT_DOC_CODE, ALT_DOC_NO, ALT_SEQ_NO, LINE_STATUS, DESLOC, TRNLOC, QTYONH, QTYONO, REORPT, MAXINV, UNIPAK, RPLQTY, ITMGRP, PURPRI, SYSQTY, HEDREM, DETREM, BUYER, POEXPD, PRWKSL, PRMNSL, PRYRSL, STKOUT, TAXCD1, TAXCD2, TAXCD3, TAXCD4, TAXRT1, TAXRT2, TAXRT3, TAXRT4, TAXAMT1, TAXAMT2, TAXAMT3, TAXAMT4, RETPRI, CUSCOD, CUSNAM, ITMDES, ADVRNO, ADVRAT"
                    + " from RMS_REQUISITIONS where SBU_CODE='830' AND LOC_CODE='100' AND DOC_CODE='MRPF'";
            if (_stmt2.executeUpdate(_strSql) <= 0) {
                throw new Exception("Error: Data to History.");
            }
            _strSql = "delete from RMS_REQUISITIONS where SBU_CODE='830' AND LOC_CODE='100' AND DOC_CODE='MRPF'";
            if (_stmt2.executeUpdate(_strSql) <= 0) {
                throw new Exception("Error: Previous data delete.");
            }
            // --------------------

            int m_intStartReq = 0, m_intEndReq = 0;
            String req_no = "";
            while (rs.next()) {

                double m_avg30DSale = Double.parseDouble(rs.getString("avg30Sales"));
                double m_D90sale = Double.parseDouble(rs.getString("D90Sales"));
                double m_avgSale = m_avg30DSale == 0.00 ? 0.00 : ((3 / m_avg30DSale) * m_D90sale);
                double m_D90withSafety = Double.parseDouble(rs.getString("D90Sales")) + m_avgSale;

//                if (m_D90withSafety - Double.parseDouble(rs.getString("tot_stock")) > 0) {
                double m_ordQty = Double.parseDouble(rs.getString("D90Sales"));// - Double.parseDouble(rs.getString("tot_stock"));
                if (m_D90withSafety - Double.parseDouble(rs.getString("tot_stock")) < 0) {
                    m_ordQty = 0;
                }

                /**
                 * *
                 * 2017-01-10 changed sequence reset issue.if data found then
                 * only update sequence otherwise throw err
                 */
                req_no = CreateSeqNo(prm_sbucod, prm_loccod, "SQFUPR");
                if (req_no == null) {
                    throw new Exception(getException().getLocalizedMessage());
                } else if (req_no.trim().length() <= 0) {
                    throw new Exception("Order creation number not genarated properly..!");
                } else if (m_intStartReq == 0) {
                    // GET FIRST REQ.NO
                    m_intStartReq = Integer.parseInt(req_no);
                }

                _strSql = " insert into  rms_requisitions(SBU_CODE, LOC_CODE, DOC_CODE, REQ_NO, "
                        + " ITEM_CODE, ORDQTY, ORD_DATE, DEL_DATE, "
                        + " SUPPLIER, UNIT, AUT_BY, AUT_DATE, "
                        + " REQ_STAT, TRANS_STAT, TRANS_BY, TRANS_DATE, "
                        + " CRE_BY, CRE_DATE, LINE_STATUS, "
                        + " PRWKSL, PRMNSL, PRYRSL, STKOUT, DESLOC, TRNLOC, "
                        + " QTYONH, QTYONO, REORPT, MAXINV, UNIPAK, RPLQTY, "
                        + " ITMGRP, PURPRI, SYSQTY, HEDREM, DETREM, "
                        + " BUYER, POEXPD, "
                        + " TAXCD1, TAXCD2, TAXCD3, TAXCD4, "
                        + " TAXRT1, TAXRT2, TAXRT3, TAXRT4, "
                        + " TAXAMT1, TAXAMT2, TAXAMT3, TAXAMT4, "
                        + " CUSNAM, ITMDES, RETPRI) values ('"
                        + prm_sbucod + "','100','" + prm_strDocCode + "','"
                        + req_no + "','"
                        + rs.getString("ITEM_CODE") + "','"
                        + m_ordQty + "',"
                        + "TO_DATE(sysdate),TO_DATE(sysdate),'"
                        + rs.getString("SUPPLIER") + "','" //xxxxxxxxxxxxxx
                        + rs.getString("unit") + "','','','CON',"
                        + "'F','cronuser','"
                        + m_currentDate + "','cronuser','"
                        + m_currentDate + "','3','0.0000','" + m_avg30DSale + "','0.0000','0.0000','"
                        + prm_loccod + "','','"
                        + rs.getString("QTYONH") + "','"
                        + rs.getString("QTYONO") + "','"
                        + rs.getString("REORPT") + "','"
                        + rs.getString("MAXINV") + "','"
                        + rs.getString("UNIPAK") + "','"
                        + rs.getString("RPLQTY") + "','"
                        + rs.getString("itm_group") + "','"
                        + rs.getString("PURPRI") + "','"
                        + rs.getString("SYSQTY") + "','"
                        + rs.getString("HEDREM") + "','"
                        + rs.getString("DETREM") + "','"
                        + rs.getString("BUYER") + "','"
                        + rs.getString("POEXPD") + "','"
                        + rs.getString("TAXCD1") + "','"
                        + rs.getString("TAXCD2") + "','"
                        + rs.getString("TAXCD3") + "','"
                        + rs.getString("TAXCD4") + "',"
                        + "'0','0','0','0',"
                        + "'0','0','0','0','"
                        + rs.getString("CUSNAM") + "','"
                        + rs.getString("ITMDES").trim() + "','"
                        + rs.getString("RETPRI") + "')";
                if (_stmt2.executeUpdate(_strSql) <= 0) {
                    throw new Exception("No data found for ordering");
                }
                m_bolFound = true;
                UpdateSeqNo(prm_sbucod, prm_loccod, "SQFUPR");
//                }
            }
            // GET LAST REQ.NO
            m_intEndReq = Integer.parseInt(req_no);

            // IF DATA FOUND //
            if (m_bolFound) {

                // SEND EMAIL //
                sendmail("100", "ordreq", "admin@arpico.com", m_toEmailList,
                        "MRPF", String.valueOf(m_intStartReq), String.valueOf(m_intEndReq));
                if (_exception != null) {
                    throw new Exception("Send Mail :" + _exception);
                }

                _strSql = " insert into rms_system_log "
                        + " (SBU_CODE, LOC_CODE, PROGRAM_ID, USER_ID, LOG_DATE, COMMENTS, PC_ID) "
                        + " values('" + prm_sbucod + "','" + prm_loccod + "','" + prm_sysparcod + "',"
                        + " 'cronuser','" + m_currentDate + "','" + prm_logComment + "',' ')";

                if (_stmt.executeUpdate(_strSql) <= 0) {
                    throw new Exception(" Error occured in Insert rms_system_log");
                }
            } else {
                throw new Exception("Data not found for furniture order creation.");
            }

            closeStatement();
            if (endTransaction() == 0) {
                throw new Exception(getException().getLocalizedMessage());
            }
            System.out.println("Sucess");
            return 1;
        } catch (Exception e) {
            abortTransaction();
            e.printStackTrace();
            _exception = e;
            return 0;
        }
    }

    public void closeStatement() {
        try {
            if (_stmt != null) {
                _stmt.close();
            }
            if (_stmt2 != null) {
                _stmt2.close();
            }

            if (_rs != null) {
                _rs.close();
            }

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public Exception getException() {
        return _exception;
    }

    public int startTransaction() {
        try {
            if (_dbconnection.getConn().getAutoCommit() == false) {
                _dbconnection.getConn().rollback();
                _dbconnection.getConn().setAutoCommit(true);
            }

            _dbconnection.getConn().setAutoCommit(false);

            return 1;
        } catch (Exception e) {
            e.printStackTrace();
            _exception = e;
            return 0;
        }

    }

    public int endTransaction() {
        try {
            _dbconnection.getConn().commit();
            _dbconnection.getConn().setAutoCommit(true);
            return 1;
        } catch (Exception e) {
            e.printStackTrace();
            _exception = e;
            return 0;
        }

    }

    public int abortTransaction() {
        try {
            if (_dbconnection.getConn().getAutoCommit() == false) {
                _dbconnection.getConn().rollback();
                _dbconnection.getConn().setAutoCommit(true);
            }

            return 1;
        } catch (Exception e) {
            e.printStackTrace();
            _exception = e;
            return 0;
        }
    }

    public String CreateSeqNo(String prm_sbucod, String prm_loccod, String prm_strSeqNoID) {
        try {
            int m_seqNo = 0;
            String m_sql = "select * "
                    + " from marksys.smsequence where sbucod='" + prm_sbucod + "' and loccod='" + prm_loccod + "' "
                    + " and seqid='" + prm_strSeqNoID + "' for update";
            ResultSet rs = _stmt2.executeQuery(m_sql);
            if (rs.next()) {
                m_seqNo = Integer.parseInt(rs.getString("curv"));
                m_seqNo++;
            }
            return String.valueOf(m_seqNo);
        } catch (Exception e) {
            e.printStackTrace();
            return "";
        }
    }

    public int UpdateSeqNo(String prm_sbucod, String prm_loccod, String prm_strSeqNoID) {
        try {
            int m_seqNo = 0;
            String m_sql = "update smsequence set curv=(curv+1) where sbucod='" + prm_sbucod + "' and loccod='" + prm_loccod + "' "
                    + " and seqid='" + prm_strSeqNoID + "'";
            _stmt2.executeUpdate(m_sql);
            return 1;
        } catch (Exception e) {
            e.printStackTrace();
            return 0;
        }
    }

    public int sendmail(String prm_location, String prm_strRepId,
            String prm_strFromAddress, String prm_strTOAddress,
            String prm_doccod, String prm_startReq, String prm_endReq) {
        try {
            HashMap<String, String> para = new HashMap<String, String>();

            String m_strQry;
            String m_strRepPath;
            String m_strFileName1;

            m_strQry = "select reppth from smrptheaderm where "
                    + " sbucod='" + _strSbuCod + "' and "
                    + " loccod='" + _strLocCod + "' and "
                    + " repid='" + prm_strRepId + "'";
            Statement m_stmnt = _dbconnection.getConn().createStatement(ResultSet.TYPE_SCROLL_SENSITIVE, ResultSet.CONCUR_READ_ONLY);
            ResultSet rs = null;
            rs = m_stmnt.executeQuery(m_strQry);
            if (rs.next()) {
                m_strRepPath = rs.getString("reppth");
            } else {
                m_stmnt.close();
                throw new Exception("Unable to find Report path for report id: " + prm_strRepId);
            }
            m_stmnt.close();

            GetIP();
            m_strFileName1 = "furnitureOrder.pdf";
            para.put("p_sbucod", _strSbuCod);
            para.put("p_loccod", prm_location);
            para.put("p_doccod", prm_doccod);
            para.put("p_startReq", prm_startReq);
            para.put("p_endReq", prm_endReq);

            String isURL = "F";
            URL m_url;
            if (m_strRepPath.length() > 4) {
                if (m_strRepPath.substring(0, 3).equalsIgnoreCase("ftp")) {
                    isURL = "T";
                } else {
                    isURL = "F";
                }
            }
            JasperReport jasperReport = null;
            if (isURL.trim().equalsIgnoreCase("T")) {
                m_url = new URL(m_strRepPath);
                jasperReport = (JasperReport) JRLoader.loadObject(m_url);
            } else {
                jasperReport = (JasperReport) JRLoader.loadObject(m_strRepPath);
            }

            // JASPER REPORT EXPORT TO FILE
            JasperPrint jasperPrint = JasperFillManager.fillReport(jasperReport, para, _dbconnection.getConn());
            JasperExportManager.exportReportToPdfFile(jasperPrint, m_strFileName1);

            if (!prm_strTOAddress.trim().equals("")) {
                CreateMail(prm_strTOAddress, prm_strFromAddress, "Furniture Auto Order Creation", m_strFileName1);
                if (_exception != null) {
                    throw new Exception("Create Mail :" + _exception);
                }
            }

            return 1;
        } catch (Exception e) {
            e.printStackTrace();
            _exception = e;
            return 0;
        }
    }

    public void CreateMail(String prm_strtoAddress, String prm_strFromAddress, String prm_strHeader, String prm_strFilePath) throws Exception {

        String from = prm_strFromAddress;
        String[] to = prm_strtoAddress.split(";");
        String fileAttachment = prm_strFilePath;
//        String fileAttachment = strFilename1;
//        String fileAttachment = strFilename;
        String bodytext1 = "Please find the attached funiture auto order creation list from Richard Pieris & Company PLC.<br><br>";

        Properties props = System.getProperties();
        props.put("mail.transport.protocol", "smtp");
        props.put("mail.smtp.host", "192.168.1.19");
        props.put("mail.smtp.localhost", _strIpAddress);

        Session session = Session.getInstance(props);
        MimeMessage message = new MimeMessage(session);
        message.setFrom(new InternetAddress(from));

        for (int i = 0; i < to.length; i++) {
            message.addRecipient(Message.RecipientType.TO, new InternetAddress(to[i]));
            System.out.println("Masge sent to : " + to[i]);
            message.setSubject(prm_strHeader);
        }

        Multipart multipart = new MimeMultipart();
        MimeBodyPart textPart = new MimeBodyPart();
        textPart.setContent(bodytext1, "text/html");
        multipart.addBodyPart(textPart);

        if (prm_strFilePath != null) {
            MimeBodyPart messageBodyPart = new MimeBodyPart();
            messageBodyPart.attachFile(fileAttachment);
            multipart.addBodyPart(messageBodyPart);
        }

        message.setContent(multipart);
        Transport.send(message);

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
}
