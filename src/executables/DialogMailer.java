/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package executables;

import dataaccess.OracleConnector;
import java.sql.ResultSet;
import java.sql.Statement;
import java.util.Hashtable;
import net.sf.jasperreports.engine.JRExporter;
import net.sf.jasperreports.engine.JRExporterParameter;
import net.sf.jasperreports.engine.JasperFillManager;
import net.sf.jasperreports.engine.JasperPrint;
import net.sf.jasperreports.engine.JasperReport;
import net.sf.jasperreports.engine.export.JRCsvExporter;
import net.sf.jasperreports.engine.util.JRLoader;

/**
 *
 * @author hayaz
 */
public class DialogMailer {

    private String _strSbuCode;

    public DialogMailer() {

        this._strSbuCode = "830";

    }

    public void dayEndMail(String prm_strTxnDate) {

        try {

            OracleConnector con = new OracleConnector("192.168.1.27", "rpd2", "bview", "bview#123");

            String m_strcomcod = "DIABILL";
            Statement stmnt = con.getConn().createStatement();
            ResultSet rs = null;
            String m_strTxnDate = prm_strTxnDate;

            String m_strRepPath = "/All Un-success Transactions.jasper";

            Hashtable<String, String> htbpara = new Hashtable<String, String>();

            JasperReport jasperReport = null;
            jasperReport = (JasperReport) JRLoader.loadObject(getClass().getResource(m_strRepPath));

            htbpara.put("comcod", m_strcomcod);
            htbpara.put("coltyp", "GSM");
            htbpara.put("sdate", m_strTxnDate);

            JasperPrint jasperPrint = JasperFillManager.fillReport(jasperReport, htbpara, con.getConn());
            JRExporter exporter;
            exporter = new JRCsvExporter();
            exporter.setParameter(JRExporterParameter.JASPER_PRINT, jasperPrint);
            exporter.setParameter(JRExporterParameter.OUTPUT_FILE_NAME, "DialogErr.csv");
            exporter.exportReport();

            //Post Paid
            m_strRepPath = "/Dialog Sucses Transactions.jasper";
            htbpara = new Hashtable<String, String>();

            jasperReport = null;
            jasperReport = (JasperReport) JRLoader.loadObject(getClass().getResource(m_strRepPath));

            htbpara.put("comcod", m_strcomcod);
            htbpara.put("coltyp", "GSM");
            htbpara.put("coltyp2", "'POS'");
            htbpara.put("sdate", m_strTxnDate);

            jasperPrint = JasperFillManager.fillReport(jasperReport, htbpara, con.getConn());
            exporter = new JRCsvExporter();
            exporter.setParameter(JRExporterParameter.JASPER_PRINT, jasperPrint);
            exporter.setParameter(JRExporterParameter.OUTPUT_FILE_NAME, "DialogPay.csv");
            exporter.exportReport();

            //Pre Success
            jasperReport = (JasperReport) JRLoader.loadObject(getClass().getResource(m_strRepPath));
            htbpara = new Hashtable<String, String>();

            htbpara.put("comcod", m_strcomcod);
            htbpara.put("coltyp", "GSM");
            htbpara.put("coltyp2", "'PRE'");
            htbpara.put("sdate", m_strTxnDate);

            jasperPrint = JasperFillManager.fillReport(jasperReport, htbpara, con.getConn());
            exporter = new JRCsvExporter();
            exporter.setParameter(JRExporterParameter.JASPER_PRINT, jasperPrint);
            exporter.setParameter(JRExporterParameter.OUTPUT_FILE_NAME, "DialogPre.csv");
            exporter.exportReport();

            //All Txn
            m_strRepPath = "/Dialog All Transactions.jasper.jasper";
            jasperReport = (JasperReport) JRLoader.loadObject(getClass().getResource(m_strRepPath));

            htbpara = new Hashtable<String, String>();

            htbpara.put("comcod", m_strcomcod);
            htbpara.put("coltyp", "GSM");
            htbpara.put("coltyp2", "'POS','ERR','NO'");
            htbpara.put("sdate", m_strTxnDate);

            jasperPrint = JasperFillManager.fillReport(jasperReport, htbpara, con.getConn());
            exporter = new JRCsvExporter();
            exporter.setParameter(JRExporterParameter.JASPER_PRINT, jasperPrint);
            exporter.setParameter(JRExporterParameter.OUTPUT_FILE_NAME, "DialogTxn.csv");
            exporter.exportReport();

            //Summary
            m_strRepPath = "/Summary of Post Paid Success.jasper";
            jasperReport = (JasperReport) JRLoader.loadObject(getClass().getResource(m_strRepPath));

            htbpara = new Hashtable<String, String>();

            htbpara.put("comcod", m_strcomcod);
            htbpara.put("coltyp", "GSM");
            htbpara.put("coltyp2", "'POS'");
            htbpara.put("sdate", m_strTxnDate);

            jasperPrint = JasperFillManager.fillReport(jasperReport, htbpara, con.getConn());
            exporter = new JRCsvExporter();
            exporter.setParameter(JRExporterParameter.JASPER_PRINT, jasperPrint);
            exporter.setParameter(JRExporterParameter.OUTPUT_FILE_NAME, "DialogPaySum.csv");
            exporter.exportReport();

            //DTV Success
            m_strRepPath = "/Dialog Sucses Transactions.jasper";

            htbpara = new Hashtable<String, String>();

            jasperReport = null;
            jasperReport = (JasperReport) JRLoader.loadObject(getClass().getResource(m_strRepPath));

            htbpara.put("comcod", m_strcomcod);
            htbpara.put("coltyp", "DTV");
            htbpara.put("coltyp2", "'POS'");
            htbpara.put("sdate", m_strTxnDate);

            jasperPrint = JasperFillManager.fillReport(jasperReport, htbpara, con.getConn());
            exporter = new JRCsvExporter();
            exporter.setParameter(JRExporterParameter.JASPER_PRINT, jasperPrint);
            exporter.setParameter(JRExporterParameter.OUTPUT_FILE_NAME, "DialogDTVPay.csv");
            exporter.exportReport();

            //DTV Un Success
            m_strRepPath = "/All Un-success Transactions.jasper";

            htbpara = new Hashtable<String, String>();

            jasperReport = null;
            jasperReport = (JasperReport) JRLoader.loadObject(getClass().getResource(m_strRepPath));

            htbpara.put("comcod", m_strcomcod);
            htbpara.put("coltyp", "DTV");
            htbpara.put("sdate", m_strTxnDate);

            jasperPrint = JasperFillManager.fillReport(jasperReport, htbpara, con.getConn());
            exporter = new JRCsvExporter();
            exporter.setParameter(JRExporterParameter.JASPER_PRINT, jasperPrint);
            exporter.setParameter(JRExporterParameter.OUTPUT_FILE_NAME, "DialogDTVErr.csv");
            exporter.exportReport();

            //DTV ALL
            m_strRepPath = "/Dialog All Transactions.jasper.jasper";
            jasperReport = (JasperReport) JRLoader.loadObject(getClass().getResource(m_strRepPath));

            htbpara = new Hashtable<String, String>();

            htbpara.put("comcod", m_strcomcod);
            htbpara.put("coltyp", "DTV");
            htbpara.put("coltyp2", "'POS','ERR','NO','PRE'");
            htbpara.put("sdate", m_strTxnDate);

            jasperPrint = JasperFillManager.fillReport(jasperReport, htbpara, con.getConn());
            exporter = new JRCsvExporter();
            exporter.setParameter(JRExporterParameter.JASPER_PRINT, jasperPrint);
            exporter.setParameter(JRExporterParameter.OUTPUT_FILE_NAME, "DialogDTVAll.csv");
            exporter.exportReport();

            //ISP
            m_strRepPath = "/Dialog All Transactions.jasper.jasper";
            jasperReport = (JasperReport) JRLoader.loadObject(getClass().getResource(m_strRepPath));

            htbpara = new Hashtable<String, String>();

            htbpara.put("comcod", m_strcomcod);
            htbpara.put("coltyp", "ISP");
            htbpara.put("coltyp2", "'NOT'");
            htbpara.put("sdate", m_strTxnDate);

            jasperPrint = JasperFillManager.fillReport(jasperReport, htbpara, con.getConn());
            exporter = new JRCsvExporter();
            exporter.setParameter(JRExporterParameter.JASPER_PRINT, jasperPrint);
            exporter.setParameter(JRExporterParameter.OUTPUT_FILE_NAME, "DiaISPTxn.csv");
            exporter.exportReport();

            //CDMA
            //CDMA-POST PAID Success
            m_strRepPath = "/Dialog Sucses Transactions.jasper";

            htbpara = new Hashtable<String, String>();

            jasperReport = null;
            jasperReport = (JasperReport) JRLoader.loadObject(getClass().getResource(m_strRepPath));

            htbpara.put("comcod", m_strcomcod);
            htbpara.put("coltyp", "CDMA");
            htbpara.put("coltyp2", "'POS'");
            htbpara.put("sdate", m_strTxnDate);

            jasperPrint = JasperFillManager.fillReport(jasperReport, htbpara, con.getConn());
            exporter = new JRCsvExporter();
            exporter.setParameter(JRExporterParameter.JASPER_PRINT, jasperPrint);
            exporter.setParameter(JRExporterParameter.OUTPUT_FILE_NAME, "DialogCDMAPay.csv");
            exporter.exportReport();

            //CDMA-PRE Success
            m_strRepPath = "/Dialog Sucses Transactions.jasper";
            htbpara = new Hashtable<String, String>();
            jasperReport = null;
            jasperReport = (JasperReport) JRLoader.loadObject(getClass().getResource(m_strRepPath));
            htbpara.put("comcod", m_strcomcod);
            htbpara.put("coltyp", "CDMA");
            htbpara.put("coltyp2", "'PRE'");
            htbpara.put("sdate", m_strTxnDate);
            jasperPrint = JasperFillManager.fillReport(jasperReport, htbpara, con.getConn());
            exporter = new JRCsvExporter();
            exporter.setParameter(JRExporterParameter.JASPER_PRINT, jasperPrint);
            exporter.setParameter(JRExporterParameter.OUTPUT_FILE_NAME, "DialogCDMAPre.csv");
            exporter.exportReport();

            //CDMA Un Success
            m_strRepPath = "/All Un-success Transactions.jasper";

            htbpara = new Hashtable<String, String>();

            jasperReport = null;
            jasperReport = (JasperReport) JRLoader.loadObject(getClass().getResource(m_strRepPath));

            htbpara.put("comcod", m_strcomcod);
            htbpara.put("coltyp", "CDMA");
            htbpara.put("sdate", m_strTxnDate);

            jasperPrint = JasperFillManager.fillReport(jasperReport, htbpara, con.getConn());
            exporter = new JRCsvExporter();
            exporter.setParameter(JRExporterParameter.JASPER_PRINT, jasperPrint);
            exporter.setParameter(JRExporterParameter.OUTPUT_FILE_NAME, "DialogCDMAErr.csv");
            exporter.exportReport();

            //CDMA ALL
            m_strRepPath = "/Dialog All Transactions.jasper.jasper";
            jasperReport = (JasperReport) JRLoader.loadObject(getClass().getResource(m_strRepPath));

            htbpara = new Hashtable<String, String>();

            htbpara.put("comcod", m_strcomcod);
            htbpara.put("coltyp", "CDMA");
            htbpara.put("coltyp2", "'POS','ERR','NO','PRE'");
            htbpara.put("sdate", m_strTxnDate);

            jasperPrint = JasperFillManager.fillReport(jasperReport, htbpara, con.getConn());
            exporter = new JRCsvExporter();
            exporter.setParameter(JRExporterParameter.JASPER_PRINT, jasperPrint);
            exporter.setParameter(JRExporterParameter.OUTPUT_FILE_NAME, "DialogCDMAAll.csv");
            exporter.exportReport();

        } catch (Exception e) {
            e.printStackTrace();

        }

    }

    public void dayEndMail() {

        try {

//            OracleConnector con = new OracleConnector("192.168.1.27", "rpd2", "RMIS", "RETAIL#RMIS");
            OracleConnector con = new OracleConnector("192.168.1.27", "RPD2", "bview", new jText.TextUti().getText("bview"));

            String m_strcomcod = "DIABILL";
            Statement stmnt = con.getConn().createStatement();
            ResultSet rs = null;
            String m_strTxnDate = "";

//            String m_strSql = "select to_char(sysdate-2,'DD-MON-YYYY') txndat from dual";
            String m_strSql = "select to_char(sysdate,'DD-MON-YYYY') as txndat from dual";
            rs = stmnt.executeQuery(m_strSql);
            if (rs.next()) {
                m_strTxnDate = rs.getString("txndat");

            }
            rs.close();
            stmnt.close();
            rs = null;
            stmnt = null;
            
//            m_strTxnDate="30-Nov-2020";

            String m_strRepPath = "/All Un-success Transactions.jasper";

            Hashtable<String, String> htbpara = new Hashtable<String, String>();

            JasperReport jasperReport = null;
            jasperReport = (JasperReport) JRLoader.loadObject(getClass().getResource(m_strRepPath));

            htbpara.put("comcod", m_strcomcod);
            htbpara.put("coltyp", "GSM");
            htbpara.put("sdate", m_strTxnDate);

            JasperPrint jasperPrint = JasperFillManager.fillReport(jasperReport, htbpara, con.getConn());
            JRExporter exporter;
            exporter = new JRCsvExporter();
            exporter.setParameter(JRExporterParameter.JASPER_PRINT, jasperPrint);
            exporter.setParameter(JRExporterParameter.OUTPUT_FILE_NAME, "DialogErr.csv");
            exporter.exportReport();

            //Post Paid
            m_strRepPath = "/Dialog Sucses Transactions.jasper";
            htbpara = new Hashtable<String, String>();

            jasperReport = null;
            jasperReport = (JasperReport) JRLoader.loadObject(getClass().getResource(m_strRepPath));

            htbpara.put("comcod", m_strcomcod);
            htbpara.put("coltyp", "GSM");
            htbpara.put("coltyp2", "'POS'");
            htbpara.put("sdate", m_strTxnDate);

            jasperPrint = JasperFillManager.fillReport(jasperReport, htbpara, con.getConn());
            exporter = new JRCsvExporter();
            exporter.setParameter(JRExporterParameter.JASPER_PRINT, jasperPrint);
            exporter.setParameter(JRExporterParameter.OUTPUT_FILE_NAME, "DialogPay.csv");
            exporter.exportReport();

            //Pre Success
            jasperReport = (JasperReport) JRLoader.loadObject(getClass().getResource(m_strRepPath));
            htbpara = new Hashtable<String, String>();

            htbpara.put("comcod", m_strcomcod);
            htbpara.put("coltyp", "GSM");
            htbpara.put("coltyp2", "'PRE'");
            htbpara.put("sdate", m_strTxnDate);

            jasperPrint = JasperFillManager.fillReport(jasperReport, htbpara, con.getConn());
            exporter = new JRCsvExporter();
            exporter.setParameter(JRExporterParameter.JASPER_PRINT, jasperPrint);
            exporter.setParameter(JRExporterParameter.OUTPUT_FILE_NAME, "DialogPre.csv");
            exporter.exportReport();

            //All Txn
            m_strRepPath = "/Dialog All Transactions.jasper.jasper";
            jasperReport = (JasperReport) JRLoader.loadObject(getClass().getResource(m_strRepPath));

            htbpara = new Hashtable<String, String>();

            htbpara.put("comcod", m_strcomcod);
            htbpara.put("coltyp", "GSM");
            htbpara.put("coltyp2", "'POS','ERR','NO'");
            htbpara.put("sdate", m_strTxnDate);

            jasperPrint = JasperFillManager.fillReport(jasperReport, htbpara, con.getConn());
            exporter = new JRCsvExporter();
            exporter.setParameter(JRExporterParameter.JASPER_PRINT, jasperPrint);
            exporter.setParameter(JRExporterParameter.OUTPUT_FILE_NAME, "DialogTxn.csv");
            exporter.exportReport();

            //Summary
            m_strRepPath = "/Summary of Post Paid Success.jasper";
            jasperReport = (JasperReport) JRLoader.loadObject(getClass().getResource(m_strRepPath));

            htbpara = new Hashtable<String, String>();

            htbpara.put("comcod", m_strcomcod);
            htbpara.put("coltyp", "GSM");
            htbpara.put("coltyp2", "'POS'");
            htbpara.put("sdate", m_strTxnDate);

            jasperPrint = JasperFillManager.fillReport(jasperReport, htbpara, con.getConn());
            exporter = new JRCsvExporter();
            exporter.setParameter(JRExporterParameter.JASPER_PRINT, jasperPrint);
            exporter.setParameter(JRExporterParameter.OUTPUT_FILE_NAME, "DialogPaySum.csv");
            exporter.exportReport();

            //DTV Success
            m_strRepPath = "/Dialog Sucses Transactions.jasper";

            htbpara = new Hashtable<String, String>();

            jasperReport = null;
            jasperReport = (JasperReport) JRLoader.loadObject(getClass().getResource(m_strRepPath));

            htbpara.put("comcod", m_strcomcod);
            htbpara.put("coltyp", "DTV");
            htbpara.put("coltyp2", "'POS'");
            htbpara.put("sdate", m_strTxnDate);

            jasperPrint = JasperFillManager.fillReport(jasperReport, htbpara, con.getConn());
            exporter = new JRCsvExporter();
            exporter.setParameter(JRExporterParameter.JASPER_PRINT, jasperPrint);
            exporter.setParameter(JRExporterParameter.OUTPUT_FILE_NAME, "DialogDTVPay.csv");
            exporter.exportReport();

            //DTV Un Success
            m_strRepPath = "/All Un-success Transactions.jasper";

            htbpara = new Hashtable<String, String>();

            jasperReport = null;
            jasperReport = (JasperReport) JRLoader.loadObject(getClass().getResource(m_strRepPath));

            htbpara.put("comcod", m_strcomcod);
            htbpara.put("coltyp", "DTV");
            htbpara.put("sdate", m_strTxnDate);

            jasperPrint = JasperFillManager.fillReport(jasperReport, htbpara, con.getConn());
            exporter = new JRCsvExporter();
            exporter.setParameter(JRExporterParameter.JASPER_PRINT, jasperPrint);
            exporter.setParameter(JRExporterParameter.OUTPUT_FILE_NAME, "DialogDTVErr.csv");
            exporter.exportReport();

            //DTV ALL
            m_strRepPath = "/Dialog All Transactions.jasper.jasper";
            jasperReport = (JasperReport) JRLoader.loadObject(getClass().getResource(m_strRepPath));

            htbpara = new Hashtable<String, String>();

            htbpara.put("comcod", m_strcomcod);
            htbpara.put("coltyp", "DTV");
            htbpara.put("coltyp2", "'POS','ERR','NO','PRE'");
            htbpara.put("sdate", m_strTxnDate);

            jasperPrint = JasperFillManager.fillReport(jasperReport, htbpara, con.getConn());
            exporter = new JRCsvExporter();
            exporter.setParameter(JRExporterParameter.JASPER_PRINT, jasperPrint);
            exporter.setParameter(JRExporterParameter.OUTPUT_FILE_NAME, "DialogDTVAll.csv");
            exporter.exportReport();

            //ISP
            m_strRepPath = "/Dialog All Transactions.jasper.jasper";
            jasperReport = (JasperReport) JRLoader.loadObject(getClass().getResource(m_strRepPath));

            htbpara = new Hashtable<String, String>();

            htbpara.put("comcod", m_strcomcod);
            htbpara.put("coltyp", "ISP");
            htbpara.put("coltyp2", "'NOT'");
            htbpara.put("sdate", m_strTxnDate);

            jasperPrint = JasperFillManager.fillReport(jasperReport, htbpara, con.getConn());
            exporter = new JRCsvExporter();
            exporter.setParameter(JRExporterParameter.JASPER_PRINT, jasperPrint);
            exporter.setParameter(JRExporterParameter.OUTPUT_FILE_NAME, "DiaISPTxn.csv");
            exporter.exportReport();

            //CDMA
            //CDMA-POST PAID Success
            m_strRepPath = "/Dialog Sucses Transactions.jasper";

            htbpara = new Hashtable<String, String>();

            jasperReport = null;
            jasperReport = (JasperReport) JRLoader.loadObject(getClass().getResource(m_strRepPath));

            htbpara.put("comcod", m_strcomcod);
            htbpara.put("coltyp", "CDMA");
            htbpara.put("coltyp2", "'POS'");
            htbpara.put("sdate", m_strTxnDate);

            jasperPrint = JasperFillManager.fillReport(jasperReport, htbpara, con.getConn());
            exporter = new JRCsvExporter();
            exporter.setParameter(JRExporterParameter.JASPER_PRINT, jasperPrint);
            exporter.setParameter(JRExporterParameter.OUTPUT_FILE_NAME, "DialogCDMAPay.csv");
            exporter.exportReport();

            //CDMA-PRE Success
            m_strRepPath = "/Dialog Sucses Transactions.jasper";
            htbpara = new Hashtable<String, String>();
            jasperReport = null;
            jasperReport = (JasperReport) JRLoader.loadObject(getClass().getResource(m_strRepPath));
            htbpara.put("comcod", m_strcomcod);
            htbpara.put("coltyp", "CDMA");
            htbpara.put("coltyp2", "'PRE'");
            htbpara.put("sdate", m_strTxnDate);
            jasperPrint = JasperFillManager.fillReport(jasperReport, htbpara, con.getConn());
            exporter = new JRCsvExporter();
            exporter.setParameter(JRExporterParameter.JASPER_PRINT, jasperPrint);
            exporter.setParameter(JRExporterParameter.OUTPUT_FILE_NAME, "DialogCDMAPre.csv");
            exporter.exportReport();

            //CDMA Un Success
            m_strRepPath = "/All Un-success Transactions.jasper";

            htbpara = new Hashtable<String, String>();

            jasperReport = null;
            jasperReport = (JasperReport) JRLoader.loadObject(getClass().getResource(m_strRepPath));

            htbpara.put("comcod", m_strcomcod);
            htbpara.put("coltyp", "CDMA");
            htbpara.put("sdate", m_strTxnDate);

            jasperPrint = JasperFillManager.fillReport(jasperReport, htbpara, con.getConn());
            exporter = new JRCsvExporter();
            exporter.setParameter(JRExporterParameter.JASPER_PRINT, jasperPrint);
            exporter.setParameter(JRExporterParameter.OUTPUT_FILE_NAME, "DialogCDMAErr.csv");
            exporter.exportReport();

            //CDMA ALL
            m_strRepPath = "/Dialog All Transactions.jasper.jasper";
            jasperReport = (JasperReport) JRLoader.loadObject(getClass().getResource(m_strRepPath));

            htbpara = new Hashtable<String, String>();

            htbpara.put("comcod", m_strcomcod);
            htbpara.put("coltyp", "CDMA");
            htbpara.put("coltyp2", "'POS','ERR','NO','PRE'");
            htbpara.put("sdate", m_strTxnDate);

            jasperPrint = JasperFillManager.fillReport(jasperReport, htbpara, con.getConn());
            exporter = new JRCsvExporter();
            exporter.setParameter(JRExporterParameter.JASPER_PRINT, jasperPrint);
            exporter.setParameter(JRExporterParameter.OUTPUT_FILE_NAME, "DialogCDMAAll.csv");
            exporter.exportReport();
            
            
            /************************DBN**************************************/
            
            //DBN
            //DBN-POST PAID Success
            m_strRepPath = "/Dialog Sucses Transactions.jasper";

            htbpara = new Hashtable<String, String>();

            jasperReport = null;
            jasperReport = (JasperReport) JRLoader.loadObject(getClass().getResource(m_strRepPath));

            htbpara.put("comcod", m_strcomcod);
            htbpara.put("coltyp", "DBN");
            htbpara.put("coltyp2", "'POS'");
            htbpara.put("sdate", m_strTxnDate);

            jasperPrint = JasperFillManager.fillReport(jasperReport, htbpara, con.getConn());
            exporter = new JRCsvExporter();
            exporter.setParameter(JRExporterParameter.JASPER_PRINT, jasperPrint);
            exporter.setParameter(JRExporterParameter.OUTPUT_FILE_NAME, "DialogDBNPay.csv");
            exporter.exportReport();

            //DBN-PRE Success
            m_strRepPath = "/Dialog Sucses Transactions.jasper";
            htbpara = new Hashtable<String, String>();
            jasperReport = null;
            jasperReport = (JasperReport) JRLoader.loadObject(getClass().getResource(m_strRepPath));
            htbpara.put("comcod", m_strcomcod);
            htbpara.put("coltyp", "DBN");
            htbpara.put("coltyp2", "'PRE'");
            htbpara.put("sdate", m_strTxnDate);
            jasperPrint = JasperFillManager.fillReport(jasperReport, htbpara, con.getConn());
            exporter = new JRCsvExporter();
            exporter.setParameter(JRExporterParameter.JASPER_PRINT, jasperPrint);
            exporter.setParameter(JRExporterParameter.OUTPUT_FILE_NAME, "DialogDBNPre.csv");
            exporter.exportReport();

            //DBN Un Success
            m_strRepPath = "/All Un-success Transactions.jasper";

            htbpara = new Hashtable<String, String>();

            jasperReport = null;
            jasperReport = (JasperReport) JRLoader.loadObject(getClass().getResource(m_strRepPath));

            htbpara.put("comcod", m_strcomcod);
            htbpara.put("coltyp", "DBN");
            htbpara.put("sdate", m_strTxnDate);

            jasperPrint = JasperFillManager.fillReport(jasperReport, htbpara, con.getConn());
            exporter = new JRCsvExporter();
            exporter.setParameter(JRExporterParameter.JASPER_PRINT, jasperPrint);
            exporter.setParameter(JRExporterParameter.OUTPUT_FILE_NAME, "DialogDBNErr.csv");
            exporter.exportReport();

            //DBN ALL
            m_strRepPath = "/Dialog All Transactions.jasper.jasper";
            jasperReport = (JasperReport) JRLoader.loadObject(getClass().getResource(m_strRepPath));

            htbpara = new Hashtable<String, String>();

            htbpara.put("comcod", m_strcomcod);
            htbpara.put("coltyp", "DBN");
            htbpara.put("coltyp2", "'POS','ERR','NO','PRE'");
            htbpara.put("sdate", m_strTxnDate);

            jasperPrint = JasperFillManager.fillReport(jasperReport, htbpara, con.getConn());
            exporter = new JRCsvExporter();
            exporter.setParameter(JRExporterParameter.JASPER_PRINT, jasperPrint);
            exporter.setParameter(JRExporterParameter.OUTPUT_FILE_NAME, "DialogDBNAll.csv");
            exporter.exportReport();
            
            /*********************DBN End*****************************************/

        } catch (Exception e) {
            e.printStackTrace();

        }

    }

}
