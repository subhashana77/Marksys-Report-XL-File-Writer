/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package executables;

import dataaccess.OracleConnector;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.OutputStream;
import java.net.URL;
import java.sql.ResultSet;
import java.sql.Statement;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Hashtable;
import net.sf.jasperreports.engine.JRExporter;
import net.sf.jasperreports.engine.JRParameter;
import net.sf.jasperreports.engine.JasperFillManager;
import net.sf.jasperreports.engine.JasperPrint;
import net.sf.jasperreports.engine.JasperReport;
import net.sf.jasperreports.engine.export.JExcelApiExporter;
import net.sf.jasperreports.engine.export.JRXlsExporterParameter;
import net.sf.jasperreports.engine.util.JRLoader;
import org.json.simple.JSONObject;

/**
 *
 * @author eranda.ak
 */
public class OralMailer {

    OracleConnector conMarksys = null;
    Exception _exception = null;
    Statement stmnt, stmnt2 = null;
    ResultSet rs = null;
    String excelSavePath = ".//OralExcel";

    public void dayEndMail(String date) {

        try {

            conMarksys = new OracleConnector("192.168.1.27", "rpd2", "MARKSYS", new jText.TextUti().getText("oracle"));

            String sql = "select * from smrptheaderm where sbucod='830' and loccod='100' and repid='pikme'";

            stmnt = conMarksys.getConn().createStatement(ResultSet.TYPE_SCROLL_SENSITIVE, ResultSet.CONCUR_READ_ONLY);
            rs = stmnt.executeQuery(sql);

            if (rs.next()) {
                String repPath = rs.getString("REPPTH");
                JSONObject repObj = exportToXLS(repPath);

                if (repObj.get("code").equals("200")) {
                    if (startTransactionMarksys() == 0) {
                        throw new Exception(_exception.getLocalizedMessage());
                    }

                    stmnt2 = conMarksys.getConn().createStatement();

                    String updateSql = "UPDATE rmis.smautomail SET ATTACH='" + repObj.get("path") + "' WHERE SBUCOD='830' AND LOCCOD='100' AND progid='OralMailer'";
                    if (stmnt2.executeUpdate(updateSql) <= 0) {
                        System.out.println("Failed.");
                        throw new Exception("Failed.");
                    }

                }

            }
            rs.close();
            rs = null;

            if (endTransactionMarksys() == 0) {
                throw new Exception(_exception.getLocalizedMessage());
            }

        } catch (Exception e) {
            abortTransactionMarksys();
            e.printStackTrace();
        } finally {
            try {
                if (stmnt != null) {
                    stmnt.close();
                    stmnt = null;
                }
                if (stmnt2 != null) {
                    stmnt2.close();
                    stmnt2 = null;
                }

            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    public JSONObject exportToXLS(String m_strRepPath) {
        JSONObject retObj = new JSONObject();

        try {

            SimpleDateFormat sdf = new SimpleDateFormat("yyyy_MM_dd_HHmmss");
            String date = sdf.format(new Date());
            String xlsFilePath = excelSavePath + "//Oral_Report_" + date + ".xls";

            JasperReport jasperReport = null;
            URL m_url = new URL(m_strRepPath);
            jasperReport = (JasperReport) JRLoader.loadObject(m_url);

            Hashtable<String, Object> prm_hmpRepPara = new Hashtable();

            prm_hmpRepPara.put("sbucod", "830");
            prm_hmpRepPara.put("puser", "sys");

            prm_hmpRepPara.put(JRParameter.IS_IGNORE_PAGINATION, Boolean.TRUE);

            JasperPrint jasperPrint = JasperFillManager.fillReport(jasperReport, prm_hmpRepPara, conMarksys.getConn());
            ByteArrayOutputStream output = new ByteArrayOutputStream();
            OutputStream outputfile = new FileOutputStream(new File(xlsFilePath));
            JRExporter exporterXLS = new JExcelApiExporter();

            exporterXLS.setParameter(JRXlsExporterParameter.JASPER_PRINT, jasperPrint);
            exporterXLS.setParameter(JRXlsExporterParameter.OUTPUT_STREAM, output);
            exporterXLS.setParameter(JRXlsExporterParameter.IS_ONE_PAGE_PER_SHEET, Boolean.FALSE);
            exporterXLS.setParameter(JRXlsExporterParameter.IS_DETECT_CELL_TYPE, Boolean.TRUE);
            exporterXLS.setParameter(JRXlsExporterParameter.IS_WHITE_PAGE_BACKGROUND, Boolean.FALSE);
            exporterXLS.setParameter(JRXlsExporterParameter.IS_REMOVE_EMPTY_SPACE_BETWEEN_ROWS, Boolean.TRUE);
            exporterXLS.setParameter(JRXlsExporterParameter.IS_REMOVE_EMPTY_SPACE_BETWEEN_COLUMNS, Boolean.TRUE);
            exporterXLS.setParameter(JRXlsExporterParameter.IS_COLLAPSE_ROW_SPAN, Boolean.TRUE);
            exporterXLS.exportReport();
            outputfile.write(output.toByteArray());

            retObj.put("code", "200");
            retObj.put("path", xlsFilePath);
        } catch (Exception ex) {
            retObj.put("code", "500");
            ex.printStackTrace();
            _exception = ex;

        }

        return retObj;
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
}
