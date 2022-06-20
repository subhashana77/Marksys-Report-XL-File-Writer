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
 * @author Hayaz
 */
public class MobitelMailer {

    public void dayEndMail(String prm_strTxnDat) {

        try {

            OracleConnector con = new OracleConnector("192.168.1.27", "rpd2", "bview", "bview#123");

            String m_strcomcod = "MOBBILL";
            Statement stmnt = con.getConn().createStatement();
            ResultSet rs = null;
            String m_strTxnDate = "";

            m_strTxnDate = prm_strTxnDat;

            String m_strRepPath = "";

            Hashtable<String, String> htbpara = new Hashtable<String, String>();

            JasperReport jasperReport = null;
            JasperPrint jasperPrint = null;
            JRExporter exporter;

            //Post Paid
            m_strRepPath = "/Mobitell Success Transactions.jasper";
            htbpara = new Hashtable<String, String>();

            jasperReport = null;
            jasperReport = (JasperReport) JRLoader.loadObject(getClass().getResource(m_strRepPath));

            htbpara.put("comcod", m_strcomcod);
            htbpara.put("coltyp", "MGSM");
            htbpara.put("coltyp2", "'POS'");
            htbpara.put("sdate", m_strTxnDate);

            jasperPrint = JasperFillManager.fillReport(jasperReport, htbpara, con.getConn());
            exporter = new JRCsvExporter();
            exporter.setParameter(JRExporterParameter.JASPER_PRINT, jasperPrint);
            exporter.setParameter(JRExporterParameter.OUTPUT_FILE_NAME, "MobitelPos.csv");
            exporter.exportReport();

            //Pre Success
            jasperReport = (JasperReport) JRLoader.loadObject(getClass().getResource(m_strRepPath));
            htbpara = new Hashtable<String, String>();

            htbpara.put("comcod", m_strcomcod);
            htbpara.put("coltyp", "MGSM");
            htbpara.put("coltyp2", "'PRE'");
            htbpara.put("sdate", m_strTxnDate);

            jasperPrint = JasperFillManager.fillReport(jasperReport, htbpara, con.getConn());
            exporter = new JRCsvExporter();
            exporter.setParameter(JRExporterParameter.JASPER_PRINT, jasperPrint);
            exporter.setParameter(JRExporterParameter.OUTPUT_FILE_NAME, "MobitelPre.csv");
            exporter.exportReport();

            //Err
            m_strRepPath = "/Mobitelll Un-success Transactions.jasper";
            jasperReport = (JasperReport) JRLoader.loadObject(getClass().getResource(m_strRepPath));
            htbpara = new Hashtable<String, String>();

            htbpara.put("comcod", m_strcomcod);
            htbpara.put("coltyp", "MGSM");
            htbpara.put("sdate", m_strTxnDate);

            jasperPrint = JasperFillManager.fillReport(jasperReport, htbpara, con.getConn());
            exporter = new JRCsvExporter();
            exporter.setParameter(JRExporterParameter.JASPER_PRINT, jasperPrint);
            exporter.setParameter(JRExporterParameter.OUTPUT_FILE_NAME, "MobitelErr.csv");
            exporter.exportReport();

        } catch (Exception e) {
            e.printStackTrace();

        }

    }

    public void dayEndMail() {

        try {

            OracleConnector con = new OracleConnector("192.168.1.27", "rpd2", "bview", "bview#123");

            String m_strcomcod = "MOBBILL";
            Statement stmnt = con.getConn().createStatement();
            ResultSet rs = null;
            String m_strTxnDate = "";

            String m_strSql = "select to_char(sysdate,'DD-MON-YYYY') txndat from dual";
            rs = stmnt.executeQuery(m_strSql);
            if (rs.next()) {
                m_strTxnDate = rs.getString("txndat");

            }
            rs.close();
            stmnt.close();
            rs = null;
            stmnt = null;

            String m_strRepPath = "";

            Hashtable<String, String> htbpara = new Hashtable<String, String>();

            JasperReport jasperReport = null;
            JasperPrint jasperPrint = null;
            JRExporter exporter;

            //Post Paid
            m_strRepPath = "/Mobitell Success Transactions.jasper";
            htbpara = new Hashtable<String, String>();

            jasperReport = null;
            jasperReport = (JasperReport) JRLoader.loadObject(getClass().getResource(m_strRepPath));

            htbpara.put("comcod", m_strcomcod);
            htbpara.put("coltyp", "MGSM");
            htbpara.put("coltyp2", "'POS'");
            htbpara.put("sdate", m_strTxnDate);

            jasperPrint = JasperFillManager.fillReport(jasperReport, htbpara, con.getConn());
            exporter = new JRCsvExporter();
            exporter.setParameter(JRExporterParameter.JASPER_PRINT, jasperPrint);
            exporter.setParameter(JRExporterParameter.OUTPUT_FILE_NAME, "MobitelPos.csv");
            exporter.exportReport();

            //Pre Success
            jasperReport = (JasperReport) JRLoader.loadObject(getClass().getResource(m_strRepPath));
            htbpara = new Hashtable<String, String>();

            htbpara.put("comcod", m_strcomcod);
            htbpara.put("coltyp", "MGSM");
            htbpara.put("coltyp2", "'PRE'");
            htbpara.put("sdate", m_strTxnDate);

            jasperPrint = JasperFillManager.fillReport(jasperReport, htbpara, con.getConn());
            exporter = new JRCsvExporter();
            exporter.setParameter(JRExporterParameter.JASPER_PRINT, jasperPrint);
            exporter.setParameter(JRExporterParameter.OUTPUT_FILE_NAME, "MobitelPre.csv");
            exporter.exportReport();

            //Err
            m_strRepPath = "/Mobitelll Un-success Transactions.jasper";
            jasperReport = (JasperReport) JRLoader.loadObject(getClass().getResource(m_strRepPath));
            htbpara = new Hashtable<String, String>();

            htbpara.put("comcod", m_strcomcod);
            htbpara.put("coltyp", "MGSM");
            htbpara.put("sdate", m_strTxnDate);

            jasperPrint = JasperFillManager.fillReport(jasperReport, htbpara, con.getConn());
            exporter = new JRCsvExporter();
            exporter.setParameter(JRExporterParameter.JASPER_PRINT, jasperPrint);
            exporter.setParameter(JRExporterParameter.OUTPUT_FILE_NAME, "MobitelErr.csv");
            exporter.exportReport();

        } catch (Exception e) {
            e.printStackTrace();

        }

    }

}
