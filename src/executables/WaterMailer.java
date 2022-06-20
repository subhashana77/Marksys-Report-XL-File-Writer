/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package executables;





import dataaccess.OracleConnector;
import java.sql.ResultSet;
import java.sql.Statement;
import java.util.Hashtable;
import net.sf.jasperreports.engine.*;
import net.sf.jasperreports.engine.export.JRCsvExporter;
import net.sf.jasperreports.engine.util.JRLoader;


/**
 *
 * @author Hayaz
 */
public class WaterMailer {
    private String strPreviousDay;


    public void dayEndMail() {

        try {

            OracleConnector con = new OracleConnector("192.168.1.27","rpd2","bview", new jText.TextUti().getText("bview"));

            String m_strcomcod = "WATBILL";
            Statement stmnt = con.getConn().createStatement();
            ResultSet rs = null;
            String m_strTxnDate = "";

            String m_strSql = "select to_char(sysdate,'DD-MON-YYYY') txndat from dual";
            rs = stmnt.executeQuery(m_strSql);
            if(rs.next()){            
                    m_strTxnDate = rs.getString("txndat");
            }
            rs.close();
            stmnt.close();
            rs = null;
            stmnt=null;



            String m_strRepPath = "";

            Hashtable <String,String> htbpara = new Hashtable<String, String>();

            JasperReport jasperReport = null;
            JasperPrint jasperPrint = null;
            JRExporter exporter;          
            m_strRepPath = ".//WaterBoard Success Transactions.jasper";
            htbpara = new Hashtable<String, String>();


//            jasperReport = null;
//            jasperReport = (JasperReport) JRLoader.loadObject(getClass().getResource(m_strRepPath));

            htbpara.put("comcod",m_strcomcod);
            htbpara.put("coltyp","BILL");
            htbpara.put("sdate",m_strTxnDate);

//            jasperPrint = JasperFillManager.fillReport(jasperReport, htbpara, con.getConn());
//            exporter = new JRCsvExporter();
//            exporter.setParameter(JRExporterParameter.JASPER_PRINT, jasperPrint);
//            exporter.setParameter(JRExporterParameter.OUTPUT_FILE_NAME, "WaterBill.csv");
//            exporter.exportReport();

             jasperPrint = JasperFillManager.fillReport(m_strRepPath, htbpara, con.getConn());
            if (jasperPrint == null) {
                throw new Exception("Unable to Load File");
            }
            JasperExportManager.exportReportToPdfFile(jasperPrint, "WaterBill.pdf");
        
        } catch (Exception e) {
            e.printStackTrace();

        }




    }

    /**
     * @return the strPreviousDay
     */
    public String getStrPreviousDay() {
        return strPreviousDay;
    }

    /**
     * @param strPreviousDay the strPreviousDay to set
     */
    public void setStrPreviousDay(String strPreviousDay) {
        this.strPreviousDay = strPreviousDay;
    }



}
