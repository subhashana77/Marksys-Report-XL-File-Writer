/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package executables;

import dataaccess.MysqlConnector;
import dataaccess.OracleConnector;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.sql.ResultSet;
import java.sql.Statement;
import java.text.DecimalFormat;
import java.util.HashMap;
import java.util.Map;
import java.util.Properties;
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

/**
 *
 * @author menaka.p
 */
public class test {

    static MysqlConnector conInsurance;
    static OracleConnector conMarksys = null;
    static OracleConnector conMarksys1 = null;
    static Exception _exception = null;
    static String _strIpAddress = "";

    public static void main(String[] args) {
        String m_msgbody2 = "", m_msgbody1 = "";
        Statement stmnt = null;
        Statement stmnt_2 = null;
        Statement stmnt_1 = null;
        ResultSet rs = null;

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
        double TotalEAmountASM = 0.0;

        try {
            conMarksys = new OracleConnector("192.168.1.27", "rpd2", "LMD", new jText.TextUti().getText("oracle"));
            if (startTransactionMarksys() == 0) {
                throw new Exception(_exception.getLocalizedMessage());
            }
            String sql = "select to_char(to_date(m.txn_date),'DD/MM/YYYY') txn_date, m.DOC_CODE,"
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
                    + "where m.sbu_code='833' and m.loc_code='100' and m.doc_code='SORD' and m.doc_no='4937' \n"
                    + "and m.mstat='VAL' \n"
                    + "and d.dstat='VAL' \n"
                    + "and m.txn_date < to_char(sysdate-(select cval01 from smsyspara where sbucod = 'XXX' and loccod = 'XXX' and parcod = 'POEXP'),'DD-Mon-YYYY')\n"
                    + "and (d.qty-nvl(d.issued_qty,0)>0)\n"
                    + "order by m.txn_date desc ";

            stmnt = conMarksys.getConn().createStatement(ResultSet.TYPE_SCROLL_SENSITIVE, ResultSet.CONCUR_READ_ONLY);
            System.out.println(sql);
            System.out.println("\n");
            rs = stmnt.executeQuery(sql);
            int t = 0;
            if (!rs.next()) {
                throw new Exception("Purchase Orders not found to expire.");
            }
            rs.beforeFirst();
            while (rs.next()) {
                HeadkeyId = rs.getString("area") + "_" + rs.getString("sbucod") + "-" + rs.getString("hname").trim();
                keyId = rs.getString("cusSbu") + "--" + rs.getString("cuscod") + "__" + rs.getString("name");

                if (hashHead.containsKey(HeadkeyId)) {
                    hashC = hashHead.get(HeadkeyId);
                    if (hashC.containsKey(keyId)) {
                        hashP = hashC.get(keyId);
                        if (hashP.containsKey(rs.getString("source_doc_no") + "_" + rs.getString("TXN_DATE"))) {
                            hashD = hashP.get(rs.getString("source_doc_no") + "_" + rs.getString("TXN_DATE"));
                            if (hashD.containsKey(rs.getString("SEQ_NO"))) {
                                hashIt = hashD.get(rs.getString("SEQ_NO"));
// 
                                hashIt.put("SBU_CODE", rs.getString("SBU_CODE"));
                                hashIt.put("cusSbu", rs.getString("cusSbu"));
                                hashIt.put("LOC_CODE", rs.getString("LOC_CODE"));

                                hashIt.put("name", rs.getString("name"));
                                hashIt.put("cuscod", rs.getString("cuscod"));

                                hashIt.put("ITEM_CODE", rs.getString("ITEM_CODE"));
                                hashIt.put("QTY", rs.getString("QTY"));
                                hashIt.put("EQTY", rs.getString("EQTY"));
                                hashIt.put("EAMOUNT", rs.getString("EAMOUNT"));
                                hashIt.put("ITM_DESC", rs.getString("ITM_DESC"));
                                hashIt.put("TXN_DATE", rs.getString("TXN_DATE"));
                                hashIt.put("DOC_CODE", rs.getString("DOC_CODE"));
                                hashIt.put("DOC_NO", rs.getString("doc_no"));
                                hashIt.put("EMAIL", rs.getString("EMAIL"));
                                hashIt.put("hemail", rs.getString("hemail"));

                                hashD.replace(rs.getString("SEQ_NO"), hashIt);
                                hashP.replace(rs.getString("source_doc_no") + "_" + rs.getString("TXN_DATE"), hashD);
                                hashC.replace(keyId, hashP);
                                hashHead.replace(rs.getString("area"), hashC);

                            } else {
                                hashIt = new HashMap<>();

                                hashIt.put("SBU_CODE", rs.getString("SBU_CODE"));
                                hashIt.put("cusSbu", rs.getString("cusSbu"));
                                hashIt.put("LOC_CODE", rs.getString("LOC_CODE"));

                                hashIt.put("name", rs.getString("name"));
                                hashIt.put("cuscod", rs.getString("cuscod"));

                                hashIt.put("ITEM_CODE", rs.getString("ITEM_CODE"));
                                hashIt.put("QTY", rs.getString("QTY"));
                                hashIt.put("EQTY", rs.getString("EQTY"));
                                hashIt.put("EAMOUNT", rs.getString("EAMOUNT"));
                                hashIt.put("ITM_DESC", rs.getString("ITM_DESC"));
                                hashIt.put("TXN_DATE", rs.getString("TXN_DATE"));
                                hashIt.put("DOC_CODE", rs.getString("DOC_CODE"));
                                hashIt.put("DOC_NO", rs.getString("doc_no"));
                                hashIt.put("EMAIL", rs.getString("EMAIL"));
                                hashIt.put("hemail", rs.getString("hemail"));

                                hashD.put(rs.getString("SEQ_NO"), hashIt);
                                hashP.replace(rs.getString("source_doc_no") + "_" + rs.getString("TXN_DATE"), hashD);
                                hashC.replace(keyId, hashP);
                                hashHead.replace(rs.getString("area"), hashC);
                            }
                        } else {
                            hashP = hashC.get(keyId);

                            hashD = new HashMap<>();
                            hashIt = new HashMap<>();

                            hashIt.put("SBU_CODE", rs.getString("SBU_CODE"));
                            hashIt.put("cusSbu", rs.getString("cusSbu"));
                            hashIt.put("LOC_CODE", rs.getString("LOC_CODE"));

                            hashIt.put("name", rs.getString("name"));
                            hashIt.put("cuscod", rs.getString("cuscod"));

                            hashIt.put("ITEM_CODE", rs.getString("ITEM_CODE"));
                            hashIt.put("QTY", rs.getString("QTY"));
                            hashIt.put("EQTY", rs.getString("EQTY"));
                            hashIt.put("EAMOUNT", rs.getString("EAMOUNT"));
                            hashIt.put("ITM_DESC", rs.getString("ITM_DESC"));
                            hashIt.put("TXN_DATE", rs.getString("TXN_DATE"));
                            hashIt.put("DOC_CODE", rs.getString("DOC_CODE"));
                            hashIt.put("DOC_NO", rs.getString("doc_no"));
                            hashIt.put("EMAIL", rs.getString("EMAIL"));
                            hashIt.put("hemail", rs.getString("hemail"));

                            hashD.put(rs.getString("SEQ_NO"), hashIt);
                            hashP.put(rs.getString("source_doc_no") + "_" + rs.getString("TXN_DATE"), hashD);

                            hashC.replace(keyId, hashP);
                            hashHead.replace(rs.getString("area"), hashC);

                        }
                    } else {
                        hashP = new HashMap<>();
                        hashD = new HashMap<>();
                        hashIt = new HashMap<>();

                        hashIt.put("SBU_CODE", rs.getString("SBU_CODE"));
                        hashIt.put("cusSbu", rs.getString("cusSbu"));
                        hashIt.put("LOC_CODE", rs.getString("LOC_CODE"));

                        hashIt.put("name", rs.getString("name"));
                        hashIt.put("cuscod", rs.getString("cuscod"));

                        hashIt.put("ITEM_CODE", rs.getString("ITEM_CODE"));
                        hashIt.put("QTY", rs.getString("QTY"));
                        hashIt.put("EQTY", rs.getString("EQTY"));
                        hashIt.put("EAMOUNT", rs.getString("EAMOUNT"));
                        hashIt.put("ITM_DESC", rs.getString("ITM_DESC"));
                        hashIt.put("TXN_DATE", rs.getString("TXN_DATE"));
                        hashIt.put("DOC_CODE", rs.getString("DOC_CODE"));
                        hashIt.put("DOC_NO", rs.getString("doc_no"));
                        hashIt.put("EMAIL", rs.getString("EMAIL"));
                        hashIt.put("hemail", rs.getString("hemail"));

                        hashD.put(rs.getString("SEQ_NO"), hashIt);
                        hashP.put(rs.getString("source_doc_no") + "_" + rs.getString("TXN_DATE"), hashD);

                        hashC.put(keyId, hashP);
                        hashHead.replace(rs.getString("area"), hashC);
                    }

                } else {

                    hashC = new HashMap<>();
                    hashP = new HashMap<>();
                    hashD = new HashMap<>();
                    hashIt = new HashMap<>();

                    hashIt.put("SBU_CODE", rs.getString("SBU_CODE"));
                    hashIt.put("cusSbu", rs.getString("cusSbu"));
                    hashIt.put("LOC_CODE", rs.getString("LOC_CODE"));

                    hashIt.put("name", rs.getString("name"));
                    hashIt.put("cuscod", rs.getString("cuscod"));

                    hashIt.put("ITEM_CODE", rs.getString("ITEM_CODE"));
                    hashIt.put("QTY", rs.getString("QTY"));
                    hashIt.put("EQTY", rs.getString("EQTY"));
                    hashIt.put("EAMOUNT", rs.getString("EAMOUNT"));
                    hashIt.put("ITM_DESC", rs.getString("ITM_DESC"));
                    hashIt.put("TXN_DATE", rs.getString("TXN_DATE"));
                    hashIt.put("DOC_CODE", rs.getString("DOC_CODE"));
                    hashIt.put("DOC_NO", rs.getString("doc_no"));
                    hashIt.put("EMAIL", rs.getString("EMAIL"));
                    hashIt.put("hemail", rs.getString("hemail"));

                    hashD.put(rs.getString("SEQ_NO"), hashIt);
                    hashP.put(rs.getString("source_doc_no") + "_" + rs.getString("TXN_DATE"), hashD);
                    hashC.put(keyId, hashP);
                    hashHead.put(HeadkeyId, hashC);
                }
                t += 1;

            }

            HashMap<String, HashMap<String, HashMap<String, HashMap<String, String>>>> hashmC = new HashMap<>();
            HashMap<String, HashMap<String, HashMap<String, String>>> hashmP = new HashMap<>();
            HashMap<String, HashMap<String, String>> hashmD = new HashMap<>();
            HashMap<String, String> hashmIt = new HashMap<>();
            Double TotalEAmount = 0.00;
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
                    m_msgbody2 = "<br><table border=\"1\" style='text-align: center;central;font-size: 13px;'>"
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

                    if (startTransactionMarksys() == 0) {
                        throw new Exception("Error in starting txn.");
                    }
                    for (Map.Entry hashPData : hashmP.entrySet()) {
                        hashmD = (HashMap<String, HashMap<String, String>>) hashPData.getValue();
                        m_msgbodyHead += "    <td align=\"left\" colspan=\"4\"><span style=\"font-weight: bold;font-size: 14px;\">order date " + hashPData.getKey().toString().split("_")[1] + " and Po Number  :- " + hashPData.getKey().toString().split("_")[0] + "</span></td>";
//                        m_msgbodyHead += "    <td align=\"left\" colspan=\"3\"><span style=\"font-weight: bold;font-size: 14px;\">Po Number :- " + hashPData.getKey().toString().split("_")[0] + "</span></td>";

                        m_msgbody2 += " <tr>"
                                + "    <td align=\"left\" colspan=\"4\"><span style=\"font-weight: bold;font-size: 14px;\">order date " + hashPData.getKey().toString().split("_")[1] + " and Po Number  :- " + hashPData.getKey().toString().split("_")[0] + "</span></td>"
                                //                                + "    <td align=\"left\" colspan=\"3\"><span style=\"font-weight: bold;font-size: 14px;\">Po Number :- " + hashPData.getKey().toString().split("_")[0] + "</span></td>"
                                + " </tr> ";

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

                            System.out.println("--" + "rms_doc_txnd");
                            System.out.println("--" + m_strqry_1);
                            System.out.println("");
                            System.out.println("");

                            stmnt_2 = conMarksys.getConn().createStatement(ResultSet.TYPE_SCROLL_SENSITIVE, ResultSet.CONCUR_READ_ONLY);
                            if (stmnt_2.executeUpdate(m_strqry_1) <= 0) {
                                throw new Exception(" Error occured in updating  in rms_doc_txnd");
                            }
                            m_msgbodyHead += " <tr>"
                                    //                                    + "     <td align=\"left\">" + hashmIt.get("TXN_DATE") + "</td>"
                                    + "     <td align=\"left\">" + hashmIt.get("ITEM_CODE") + "</td>"
                                    + "     <td align=\"left\">" + hashmIt.get("ITM_DESC") + "</td>"
                                    + "     <td align=\"left\"> " + hashmIt.get("QTY") + "</td>"
                                    + "     <td align=\"left\">" + hashmIt.get("EQTY") + "</td>"
                                    + " </tr> ";

                            m_msgbody2 += " <tr>"
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

                        System.out.println("--" + "rms_doc_txnm");
                        System.out.println("--" + m_strqry);
                        System.out.println("/////////////////////////////////////////////////////////");

                        stmnt_1 = conMarksys.getConn().createStatement(ResultSet.TYPE_SCROLL_SENSITIVE, ResultSet.CONCUR_READ_ONLY);
                        if (stmnt_1.executeUpdate(m_strqry) <= 0) {
                            throw new Exception(" Error occured in updating  in rms_doc_txnm");
                        }

                        m_msgbody2 += " <tr>"
                                + "    <td align=\"left\" colspan=\"2\"><span style=\"font-weight: bold;font-size: 14px;\">Total Amount Of Expire Orders </span></td>"
                                + "    <td  align=\"right\"  colspan=\"2\"><span style=\"font-weight: bold;font-size: 14px;\">" + setDecimalSplitor(round(TotalEAmountDIS, 8)) + " </span></td>"
                                + " </tr> ";
                        m_msgbodyHead += " </tr> ";

                        m_msgbodyHead += " <tr>"
                                + "    <td align=\"left\" colspan=\"2\"><span style=\"font-weight: bold;font-size: 14px;\">Total Amount Of Expire Orders  </span></td>"
                                + "    <td  align=\"right\" colspan=\"2\"><span style=\"font-weight: bold;font-size: 14px;\">" + setDecimalSplitor(round(TotalEAmountDIS, 8)) + " </span></td>"
                                + " </tr> ";
                        TotalEAmountASM += TotalEAmountDIS;

                    }
                    if (endTransactionMarksys() == 0) {
                        throw new Exception("Error in starting txn.");
                    }
                    stmnt_1.close();
                    stmnt_1 = null;
                    stmnt_2.close();
                    stmnt_2 = null;
                    m_msgbody2 += "</table><br>";
                    String m_msgbody = "<strong>List of Expired Purchase Orders Of " + supName + "</strong>";
                    System.out.println(m_msgbody2);
                        sendEmailPurchaseOrders("sudeepf@arpico.com,pabodhar@arpico.com", "salesorder.exp@arpico.com", "Expired Purchase Orders - Richard Pieris & Company ", m_msgbody, m_msgbody2, "<br><br><br> This is an automated message from Purchase order expiry process.");
                    if (distributerEmail != null) {
                        sendEmailPurchaseOrders(distributerEmail , "salesorder.exp@arpico.com", "Expired Purchase Orders - Richard Pieris & Company ", m_msgbody, m_msgbody2, "<br><br><br> This is an automated message from Purchase order expiry process.");
                    }

                }
                m_msgbodyHead += " <tr>"
                        + "    <td align=\"left\" colspan=\"2\"><span style=\"font-weight: bold;font-size: 14px;\">ASM " + distributerName + " Total Expire Amount  </span></td>"
                        + "    <td  align=\"right\" colspan=\"2\"><span style=\"font-weight: bold;font-size: 14px;\">" + setDecimalSplitor(round(TotalEAmountASM, 8)) + " </span></td>"
                        + " </tr> ";

                m_msgbodyHead += "</table><br>";
                System.out.println("\n ");
                System.out.println("ASM Send Email " + distributerHeadEmail);
                System.out.println(m_msgbodyHead);
                TotalEAmountDIS = 0.0;
                    sendEmailPurchaseOrders("pabodhar@arpico.com,sudeepf@arpico.com", "salesorder.exp@arpico.com", "Expired Purchase Orders (ASM) - Richard Pieris & Company ", "<strong>List of Expired Purchase Orders Of " + hashHData.getKey().toString().split("_")[1].split("-")[0] + " " + hashHData.getKey().toString().split("_")[0] + " " + hashHData.getKey().toString().split("_")[1].split("-")[1] + "</strong>", m_msgbodyHead, "<br><br><br> This is an automated message from Purchase order expiry process.");

                if (distributerHeadEmail != null) {
                    sendEmailPurchaseOrders(distributerHeadEmail , "salesorder.exp@arpico.com", "Expired Purchase Orders (ASM) - Richard Pieris & Company ", "<strong>List of Expired Purchase Orders Of " + hashHData.getKey().toString().split("_")[1].split("-")[0] + " " + hashHData.getKey().toString().split("_")[0] + " " + hashHData.getKey().toString().split("_")[1].split("-")[1] + "</strong>", m_msgbodyHead, "<br><br><br> This is an automated message from Purchase order expiry process.");

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
                sendEmailPurchaseOrders(getEmailAddressPurchaseOrders()+",sudeepf@arpico.com,pabodhar@arpico.com", "salesorder.exp@arpico.com", "Blank Email Address List for Expired Purchase Orders :- ", "Customer Details ", ListBody, "<br><br><br> This is an automated message from Purchase order expiry process Email ERR Customer List .");
            }
        } catch (Exception ex) {
            abortTransactionMarksys();
            Logger.getLogger(CRMPointsUpdate.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    public static int startTransactionMarksys() {
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

    public static int endTransactionMarksys() {
        try {
            conMarksys.getConn().commit();
            conMarksys.getConn().setAutoCommit(true);
            return 1;
        } catch (Exception e) {
            e.printStackTrace();
            return 0;
        }
    }

    public static void abortTransactionMarksys() {
        try {
            if (conMarksys.getConn().getAutoCommit() == false) {
                conMarksys.getConn().rollback();
                conMarksys.getConn().setAutoCommit(true);
            }

        } catch (Exception e) {
            e.printStackTrace();

        }
    }

    public static int startTransactionMySql() {
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

    public static int endTransactionMySql() {
        try {
            conInsurance.getConn().commit();
            conInsurance.getConn().setAutoCommit(true);
            return 1;
        } catch (Exception e) {
            e.printStackTrace();
            return 0;
        }
    }

    public static void abortTransactionMySql() {
        try {
            if (conInsurance.getConn().getAutoCommit() == false) {
                conInsurance.getConn().rollback();
                conInsurance.getConn().setAutoCommit(true);
            }

        } catch (Exception e) {
            e.printStackTrace();

        }
    }

    ////////////////////////////////////////////////////////////
    public static void sendEmailPurchaseOrders(String prm_strNextTo, String prm_strNextAppemail, String prm_strHeader, String prm_strBody1, String prm_strBody2,
            String prm_strLastAppEmail) {
        try {
            CreateMailPurchaseOrders(prm_strNextTo, prm_strNextAppemail, "", prm_strHeader, "", "", "",
                    //            CreateMailPurchaseOrders(getEmailAddressPurchaseOrders(), "salesorder.exp@arpico.com", "", prm_strHeader, "", "", "",
                    prm_strBody1, prm_strBody2, prm_strLastAppEmail, "");
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }

    public static boolean CreateMailPurchaseOrders(String strtoAddress, String prm_strFromAddress, String prm_strCCAddress, String prm_strHeader, String strFilename1, String strFilename2, String strFilename3,
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

    public static String getEmailAddressPurchaseOrders() {
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

    public static double round(double value, int places) {
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

    public static BigDecimal round(BigDecimal d, int scale, boolean roundUp) {
        int mode = (roundUp) ? BigDecimal.ROUND_UP : BigDecimal.ROUND_DOWN;
        return d.setScale(scale, mode);
    }

    public static String setDecimalSplitor(Double withoutSeperator) {
        String pattern = ",###,###.00";
        return (new DecimalFormat(pattern).format(withoutSeperator));

    }
}
