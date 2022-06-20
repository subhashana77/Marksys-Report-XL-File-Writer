/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package executables;

import com.itextpdf.text.BaseColor;
import com.itextpdf.text.Document;
import com.itextpdf.text.DocumentException;
import com.itextpdf.text.Element;
import com.itextpdf.text.Font;
import com.itextpdf.text.PageSize;
import com.itextpdf.text.Paragraph;
import com.itextpdf.text.Phrase;
import com.itextpdf.text.pdf.PdfPCell;
import com.itextpdf.text.pdf.PdfPTable;
import com.itextpdf.text.pdf.PdfWriter;
import dataaccess.OracleConnector;
import java.io.File;
import java.io.FileOutputStream;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.text.DateFormat;
import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.Hashtable;
import java.util.List;
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
import org.apache.poi.hssf.usermodel.HSSFCellStyle;
import org.apache.poi.hssf.usermodel.HSSFDataFormat;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;

import org.apache.poi.hssf.util.CellRangeAddress;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.CellStyle;
import org.apache.poi.ss.usermodel.CreationHelper;
import org.apache.poi.ss.usermodel.IndexedColors;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.json.simple.JSONObject;

import org.apache.poi.hssf.usermodel.HSSFRow;
import org.apache.poi.hssf.usermodel.HSSFSheet;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;

/**
 *
 * @author menaka.p
 */
public class PanelReport {

    private static Connection _OracleCon = null;
    static OracleConnector _conOracleCon = null;

    private static Hashtable<String, Hashtable<String, Hashtable<Integer, JSONObject>>> HashBatch;
    static Exception _exception = null;
    static String _strIpAddress = "";
    static String _strToAddress = "";
    static String _strCcAddress = "";
    static String _strBccAddress = "";

//    public static void main(String[] args) throws SQLException, Exception {
    public void createReport() {
        try {

            getEmailAddressPurchaseOrders();

            ////////////////////////////////////
//                        Class.forName("oracle.jdbc.driver.OracleDriver").newInstance();
//            String url = "jdbc:oracle:thin:@192.168.1.27:1521:RPD2";
//            ;
            ////////////////////////////////////
            _conOracleCon = new OracleConnector("192.168.1.27", "rpd2", "LMD", new jText.TextUti().getText("oracle"));

            HashBatch = new Hashtable<String, Hashtable<String, Hashtable<Integer, JSONObject>>>();
//            Statement statement = conMarksys.getConn().createStatement(ResultSet.TYPE_SCROLL_SENSITIVE, ResultSet.CONCUR_READ_ONLY);
//            Statement statementWC = conMarksys.getConn().createStatement(ResultSet.TYPE_SCROLL_SENSITIVE, ResultSet.CONCUR_READ_ONLY);

            Statement statement = _conOracleCon.getConn().createStatement();
            Statement statementWC = _conOracleCon.getConn().createStatement();

//            Statement statement =_OracleCon.createStatement();
//            Statement statementWC =_OracleCon.createStatement();
            String dataCollectQuery = "select  h.SBUCOD,h.LOCCOD,h.JOB_NO,h.ITMCOD,h.SEQNUM,h.DOCCOD,h.DOC_NO,h.WKCENT,h.TXNDAT,h.ITMQTY,h.STATUS ,\n"
                    + " w.wkname as TRANWC,NVL( h.WKCENT, 'NOT') as WKCENTID ,h.CREABY,h.CREADT,h.LNSTAT,\n"
                    + " h.HOLDQT, NVL(h.REMARK, 'n/a') as REMARK ,h.FGSTA,s.name as CUST_SUP ,i.itm_desc,m.CRE_DATE as STARDT \n"
                    + " ,m.JOB_NO as PERJOB ,NVL(m.REF4, 'NO') as PERJOBTYPE , NVL(m.REF3, '-') as PONUM ,m.inv_amt as INV_AMT ,m.cre_date as SODATE ,"
                    + " NVL( TO_CHAR( j.txn_date, 'YYYY-MM-DD' ), 'IN Stock') as INV_DATE \n"
                    + " from LMD.workordtrack h  inner join rms_doc_txnd d  ON h.sbucod=d.sbu_code and h.loccod=d.loc_code \n"
                    + " and h.doccod=d.doc_code and h.doc_no=d.doc_no \n"
                    + " INNER JOIN rms_itmmaster i  ON h.sbucod=i.sbu_code and i.loc_code='100' and d.item_code=i.item_code  \n"
                    + " INNER JOIN LMD.rms_doc_txnm m  ON d.sbu_code=m.sbu_code and d.loc_code=m.loc_code and d.doc_code=m.doc_code and d.doc_no=m.doc_no   \n"
                    + " INNER JOIN LMD.workcenter w\n"
                    + " ON w.sbucod=m.sbu_code and w.loccod=m.loc_code and w.wkcode=h.WKCENT  \n"
                    + " INNER JOIN rms_cust_sup s\n"
                    + " ON s.sbu_code= h.SBUCOD and s.cust_sup='C' and s.cs_code=m.cust_sup_code \n"
                    + " left outer join rms_doc_txnm j\n"
                    + " ON j.sbu_code=m.sbu_code and j.loc_code='140' \n"
                    + " and  j.doc_code in ('SVAT','VINV','NVAT')and m.job_no=j.job_no  "
                    + " WHERE  d.sbu_code='880' and d.loc_code='120' and d.doc_code='SAOR' and \n"
                    + " m.cre_date > '20-DEC-19'  and (h.tranwc is null or h.status='INPRO') and h.job_no not in ('N100-1050','N100-1019') "
                    //                    + " and h.job_no in ('O025-1049') \n"
                    + " order by  h.Job_no desc ";
            // or h.TRANWC='99' //and d.doc_no=2994 //and h.job_no='N100-1014'
//            System.out.println(dataCollectQuery);
            ResultSet resultSet = statement.executeQuery(dataCollectQuery);
            ResultSet workcenter = statementWC.executeQuery(" select * from LMD.workcenter where sbucod='880' and loccod ='120' ");
            List<String> list = new ArrayList<String>();
            while (workcenter.next()) {
                list.add(workcenter.getString("WKCODE"));
            }

//            ResultSet resultSet = s.executeQuery("select * from LMD.workordtrack WHERE doc_no in (2994)  order by seqnum,doc_no");
            while (resultSet.next()) {
//                System.out.println(resultSet.getString("JOB_NO"));
//                System.out.println(resultSet.getString("DOC_NO"));
//                System.out.println(resultSet.getString("seqnum"));

                if (HashBatch.containsKey(resultSet.getString("JOB_NO"))) {
//                    System.out.println("already in Batch");
                    if (HashBatch.get(resultSet.getString("JOB_NO")).containsKey(resultSet.getString("DOC_NO"))) {
                        if (HashBatch.get(resultSet.getString("JOB_NO")).get(resultSet.getString("DOC_NO")).containsKey(resultSet.getString("SEQNUM"))) {
//                            System.out.println("Already in seq number in details");
                        } else {

                            Hashtable<Integer, JSONObject> hashSeq = HashBatch.get(resultSet.getString("JOB_NO")).get(resultSet.getString("DOC_NO"));

                            JSONObject jobdetails = new JSONObject();

                            jobdetails.put("SBUCOD", resultSet.getString("SBUCOD"));
                            jobdetails.put("LOCCOD", resultSet.getString("LOCCOD"));
                            jobdetails.put("JOB_NO", resultSet.getString("JOB_NO"));
                            jobdetails.put("ITMCOD", resultSet.getString("ITMCOD"));
                            jobdetails.put("SEQNUM", resultSet.getString("SEQNUM"));
                            jobdetails.put("DOCCOD", resultSet.getString("DOCCOD"));
                            jobdetails.put("DOC_NO", resultSet.getString("DOC_NO"));
                            jobdetails.put("WKCENT", resultSet.getString("WKCENT"));
                            jobdetails.put("TXNDAT", resultSet.getString("TXNDAT"));
                            jobdetails.put("ITMQTY", resultSet.getString("ITMQTY"));
                            jobdetails.put("TRANWC", resultSet.getString("TRANWC"));
                            jobdetails.put("WKCENTID", resultSet.getString("WKCENTID"));
                            jobdetails.put("STATUS", resultSet.getString("STATUS"));
                            jobdetails.put("CREABY", resultSet.getString("CREABY"));
                            jobdetails.put("CREADT", resultSet.getString("CREADT"));
                            jobdetails.put("LNSTAT", resultSet.getString("LNSTAT"));
                            jobdetails.put("HOLDQT", resultSet.getString("HOLDQT"));
                            jobdetails.put("REMARK", resultSet.getString("REMARK"));
                            jobdetails.put("FGSTA", resultSet.getString("FGSTA"));
                            jobdetails.put("ITMDESC", resultSet.getString("itm_desc"));
                            jobdetails.put("STARDT", resultSet.getString("STARDT"));
                            jobdetails.put("SODATE", resultSet.getString("SODATE"));
                            jobdetails.put("PERJOB", resultSet.getString("PERJOB"));
                            jobdetails.put("PERJOBTYPE", resultSet.getString("PERJOBTYPE"));
                            jobdetails.put("PONUM", resultSet.getString("PONUM"));
                            jobdetails.put("CUST_SUP", resultSet.getString("CUST_SUP"));
                            jobdetails.put("INV_AMT", resultSet.getString("INV_AMT"));
                            jobdetails.put("INV_DATE", resultSet.getString("INV_DATE"));
                            jobdetails.put("list", list);
                            if (resultSet.getString("REMARK").contains("Direct Insert From Initial Sales Order->")) {
                                jobdetails.put("LINK", "N");
                            } else {
                                jobdetails.put("LINK", "Y");
                            }

                            hashSeq.put(resultSet.getInt("seqnum"), jobdetails);
                            Hashtable<String, Hashtable<Integer, JSONObject>> hashdocNo = HashBatch.get(resultSet.getString("JOB_NO"));

                            hashdocNo.put(resultSet.getString("DOC_NO"), hashSeq);
                            HashBatch.put(resultSet.getString("JOB_NO"), hashdocNo);
                        }

                    } else {

                        JSONObject jobdetails = new JSONObject();
                        Hashtable<Integer, JSONObject> hashSeq = HashBatch.get(resultSet.getString("JOB_NO")).get(resultSet.getString("DOC_NO"));
                        if (hashSeq == null) {
                            hashSeq = new Hashtable<Integer, JSONObject>();
                        }
                        jobdetails.put("SBUCOD", resultSet.getString("SBUCOD"));
                        jobdetails.put("LOCCOD", resultSet.getString("LOCCOD"));
                        jobdetails.put("JOB_NO", resultSet.getString("JOB_NO"));
                        jobdetails.put("ITMCOD", resultSet.getString("ITMCOD"));
                        jobdetails.put("SEQNUM", resultSet.getString("SEQNUM"));
                        jobdetails.put("DOCCOD", resultSet.getString("DOCCOD"));
                        jobdetails.put("DOC_NO", resultSet.getString("DOC_NO"));
                        jobdetails.put("WKCENT", resultSet.getString("WKCENT"));
                        jobdetails.put("TXNDAT", resultSet.getString("TXNDAT"));
                        jobdetails.put("ITMQTY", resultSet.getString("ITMQTY"));
                        jobdetails.put("TRANWC", resultSet.getString("TRANWC"));
                        jobdetails.put("WKCENTID", resultSet.getString("WKCENTID"));
                        jobdetails.put("STATUS", resultSet.getString("STATUS"));
                        jobdetails.put("CREABY", resultSet.getString("CREABY"));
                        jobdetails.put("CREADT", resultSet.getString("CREADT"));
                        jobdetails.put("LNSTAT", resultSet.getString("LNSTAT"));
                        jobdetails.put("HOLDQT", resultSet.getString("HOLDQT"));
                        jobdetails.put("REMARK", resultSet.getString("REMARK"));
                        jobdetails.put("FGSTA", resultSet.getString("FGSTA"));
                        jobdetails.put("ITMDESC", resultSet.getString("itm_desc"));
                        jobdetails.put("STARDT", resultSet.getString("STARDT"));
                        jobdetails.put("SODATE", resultSet.getString("SODATE"));
                        jobdetails.put("PERJOB", resultSet.getString("PERJOB"));
                        jobdetails.put("PERJOBTYPE", resultSet.getString("PERJOBTYPE"));
                        jobdetails.put("PONUM", resultSet.getString("PONUM"));
                        jobdetails.put("CUST_SUP", resultSet.getString("CUST_SUP"));
                        jobdetails.put("INV_AMT", resultSet.getString("INV_AMT"));
                        jobdetails.put("INV_DATE", resultSet.getString("INV_DATE"));
                        jobdetails.put("list", list);
                        if (resultSet.getString("REMARK").contains("Direct Insert From Initial Sales Order->")) {
                            jobdetails.put("LINK", "N");
                        } else {
                            jobdetails.put("LINK", "Y");
                        }

                        hashSeq.put(resultSet.getInt("seqnum"), jobdetails);
                        Hashtable<String, Hashtable<Integer, JSONObject>> hashdocNo = HashBatch.get(resultSet.getString("JOB_NO"));
                        hashdocNo.put(resultSet.getString("DOC_NO"), hashSeq);
                        HashBatch.put(resultSet.getString("JOB_NO"), hashdocNo);
                    }

                } else {
                    Hashtable<String, Hashtable<Integer, JSONObject>> hashdocNo = new Hashtable<String, Hashtable<Integer, JSONObject>>();
                    Hashtable<Integer, JSONObject> hashSeq = new Hashtable<Integer, JSONObject>();

                    JSONObject jobdetails = new JSONObject();

                    jobdetails.put("SBUCOD", resultSet.getString("SBUCOD"));
                    jobdetails.put("LOCCOD", resultSet.getString("LOCCOD"));
                    jobdetails.put("JOB_NO", resultSet.getString("JOB_NO"));
                    jobdetails.put("ITMCOD", resultSet.getString("ITMCOD"));
                    jobdetails.put("SEQNUM", resultSet.getString("SEQNUM"));
                    jobdetails.put("DOCCOD", resultSet.getString("DOCCOD"));
                    jobdetails.put("DOC_NO", resultSet.getString("DOC_NO"));
                    jobdetails.put("WKCENT", resultSet.getString("WKCENT"));
                    jobdetails.put("TXNDAT", resultSet.getString("TXNDAT"));
                    jobdetails.put("ITMQTY", resultSet.getString("ITMQTY"));
                    jobdetails.put("TRANWC", resultSet.getString("TRANWC"));
                    jobdetails.put("WKCENTID", resultSet.getString("WKCENTID"));
                    jobdetails.put("STATUS", resultSet.getString("STATUS"));
                    jobdetails.put("CREABY", resultSet.getString("CREABY"));
                    jobdetails.put("CREADT", resultSet.getString("CREADT"));
                    jobdetails.put("LNSTAT", resultSet.getString("LNSTAT"));
                    jobdetails.put("HOLDQT", resultSet.getString("HOLDQT"));
                    jobdetails.put("REMARK", resultSet.getString("REMARK"));
                    jobdetails.put("FGSTA", resultSet.getString("FGSTA"));
                    jobdetails.put("ITMDESC", resultSet.getString("itm_desc"));
                    jobdetails.put("STARDT", resultSet.getString("STARDT"));
                    jobdetails.put("SODATE", resultSet.getString("SODATE"));
                    jobdetails.put("PERJOB", resultSet.getString("PERJOB"));
                    jobdetails.put("PERJOBTYPE", resultSet.getString("PERJOBTYPE"));
                    jobdetails.put("PONUM", resultSet.getString("PONUM"));
                    jobdetails.put("CUST_SUP", resultSet.getString("CUST_SUP"));
                    jobdetails.put("INV_AMT", resultSet.getString("INV_AMT"));
                    jobdetails.put("INV_DATE", resultSet.getString("INV_DATE"));
                    jobdetails.put("list", list);

                    if (resultSet.getString("REMARK").contains("Direct Insert From Initial Sales Order->")) {
                        jobdetails.put("LINK", "N");
                    } else {
                        jobdetails.put("LINK", "Y");
                    }

                    hashSeq.put(resultSet.getInt("seqnum"), jobdetails);
                    hashdocNo.put(resultSet.getString("DOC_NO"), hashSeq);
                    HashBatch.put(resultSet.getString("JOB_NO"), hashdocNo);

                }

            }

//            System.out.println(HashBatch.toString());
            TreeMap<String, Hashtable<String, Hashtable<Integer, JSONObject>>> sorted = new TreeMap<String, Hashtable<String, Hashtable<Integer, JSONObject>>>();

            sorted.putAll(HashBatch);

//            System.out.println(sorted.toString());
//            _OracleCon.close();
            createPDF("PanelReportBreakDown", HashBatch);
            createPDFStatus("PanelReportStatus", HashBatch);

        } catch (Exception ex) {
            ex.printStackTrace();
            Logger.getLogger(PanelReport.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    void createPDF(String pdfFilename, Hashtable<String, Hashtable<String, Hashtable<Integer, JSONObject>>> HashBatch) {

        Document doc = new Document();
        PdfWriter docWriter = null;
        ///////////////////////////////
        Workbook workbook = (Workbook) new HSSFWorkbook(); // new HSSFWorkbook() for generating `.xls` file
        String[] columns = {"PO number", "SO number", "SO creation date", "JOb Started date", "Job number", "Job type",
            "Linked", "Customer name", "Item code", "Item description", "O Qty", " Order value", "Pending Day", "Days", "WS 1", "Days", "WS 2", "Days", "WS 3", "Days", "WS 4", "Days", "WS 5", "Days", "WS 6", "Days", "Invoiced", "Days"};
        CreationHelper createHelper = workbook.getCreationHelper();
        Sheet sheet = workbook.createSheet("Panel Batch");

        int rowNum = 1;

        ///////////////////////////////
        // Create a Font for styling header cells
        org.apache.poi.ss.usermodel.Font headerFont = workbook.createFont();
        // headerFont.setBold(true);
        headerFont.setFontHeightInPoints((short) 12);
        headerFont.setColor(IndexedColors.BLACK.getIndex());

        // Create a CellStyle with the font
        CellStyle headerCellStyle = workbook.createCellStyle();
        headerCellStyle.setFont(headerFont);
        headerCellStyle.setBorderBottom(HSSFCellStyle.BORDER_THIN);
        headerCellStyle.setBorderLeft(HSSFCellStyle.BORDER_THIN);
        headerCellStyle.setBorderRight(HSSFCellStyle.BORDER_THIN);
        headerCellStyle.setBorderTop(HSSFCellStyle.BORDER_THIN);

        // Create Cell Style for formatting Date
        CellStyle dateCellStyle = workbook.createCellStyle();
//        dateCellStyle.setDataFormat(createHelper.createDataFormat().getFormat("dd-MM-yyyy"));
//        dateCellStyle.setBorderBottom(HSSFCellStyle.BORDER_THIN);
//        dateCellStyle.setBorderLeft(HSSFCellStyle.BORDER_THIN);
//        dateCellStyle.setBorderRight(HSSFCellStyle.BORDER_THIN);
//        dateCellStyle.setBorderTop(HSSFCellStyle.BORDER_THIN);

        // Create Cell Style for formatting Date
        CellStyle boarderCellStyle = workbook.createCellStyle();
        boarderCellStyle.setBorderBottom(HSSFCellStyle.BORDER_THIN);
        boarderCellStyle.setBorderLeft(HSSFCellStyle.BORDER_THIN);
        boarderCellStyle.setBorderRight(HSSFCellStyle.BORDER_THIN);
        boarderCellStyle.setBorderTop(HSSFCellStyle.BORDER_THIN);

        // Create Cell Style for formatting Date
        CellStyle boarderCellStyleThousandSeparator = workbook.createCellStyle();
        boarderCellStyle.setBorderBottom(HSSFCellStyle.BORDER_THIN);
        boarderCellStyle.setBorderLeft(HSSFCellStyle.BORDER_THIN);
        boarderCellStyle.setBorderRight(HSSFCellStyle.BORDER_THIN);
        boarderCellStyle.setBorderTop(HSSFCellStyle.BORDER_THIN);
        boarderCellStyle.setDataFormat(HSSFDataFormat.getBuiltinFormat("#,##0"));

        // Create Cell Style for formatting Date
        CellStyle boarderCellStyleCenter = workbook.createCellStyle();
        boarderCellStyleCenter.setFont(headerFont);
        boarderCellStyleCenter.setBorderBottom(HSSFCellStyle.BORDER_THIN);
        boarderCellStyleCenter.setBorderLeft(HSSFCellStyle.BORDER_THIN);
        boarderCellStyleCenter.setBorderRight(HSSFCellStyle.BORDER_THIN);
        boarderCellStyleCenter.setBorderTop(HSSFCellStyle.BORDER_THIN);
        boarderCellStyleCenter.setBorderTop(CellStyle.ALIGN_CENTER);

        // Create a Row
        Row headerRow = sheet.createRow(1);

        // Create cells
        for (int i = 0; i < columns.length; i++) {
            Cell cell = headerRow.createCell(i);
            cell.setCellValue(columns[i]);
            cell.setCellStyle(headerCellStyle);
        }

        /////////////////////////////////// POI
        Date dateE = null;
        Date dateS = null;
        Date jobStart = null;
        long difference;
        float daysBetween;

        DecimalFormat df = new DecimalFormat("0.00");

        try {

            //special font sizes
            Font bfBold12 = new Font(Font.FontFamily.TIMES_ROMAN, 12, Font.BOLD, new BaseColor(0, 0, 0));
            Font bf12 = new Font(Font.FontFamily.TIMES_ROMAN, 12);

            //file path
            String path = System.getProperty("user.dir") + "\\" + pdfFilename + ".pdf";
            docWriter = PdfWriter.getInstance(doc, new FileOutputStream(path));

            //document header attributes
            doc.addAuthor("betterThanZero");
            doc.addCreationDate();
            doc.addProducer();
            doc.addCreator("MySampleCode.com");
            doc.addTitle("Report with Column Headings");
            doc.setPageSize(PageSize.B0);

            //open document
            doc.open();

            //create a paragraph
            Paragraph paragraph = new Paragraph("");

            //specify column widths
            float[] columnWidths = {
                1f, 1f, 2f, 1f, 1f, 1f, 3f, 2f, 3f, 0.5f,
                1f, 1f, 0.5f, 1f, 0.5f, 1f, 0.5f, 1f, 0.5f, 1f,
                0.5f, 1f, 0.5f, 1f, 0.5f, 1.2f, 0.5f
            };
            //create PDF table with the given widths
            PdfPTable table = new PdfPTable(columnWidths);
            // set table width a percentage of the page width
            table.setWidthPercentage(90f);

            //insert an empty row
            insertCell(table, "Date:", Element.ALIGN_LEFT, 29, bfBold12);

            //insert column headings
            insertCell(table, "PO number", Element.ALIGN_LEFT, 1, bfBold12);            //01
            insertCell(table, "SO number", Element.ALIGN_LEFT, 1, bfBold12);            //02
            insertCell(table, "SO creation date", Element.ALIGN_LEFT, 1, bfBold12);     //03
            insertCell(table, "Job number", Element.ALIGN_LEFT, 1, bfBold12);           //04
            insertCell(table, "Job type", Element.ALIGN_LEFT, 1, bfBold12);             //05
            insertCell(table, "Linked", Element.ALIGN_LEFT, 1, bfBold12);               //06
            insertCell(table, "Customer name", Element.ALIGN_LEFT, 1, bfBold12);        //07
            insertCell(table, "Item code", Element.ALIGN_LEFT, 1, bfBold12);            //08
            insertCell(table, "Item description", Element.ALIGN_LEFT, 1, bfBold12);     //09
            insertCell(table, "O Qty", Element.ALIGN_LEFT, 1, bfBold12);                  //10
            insertCell(table, "Order value", Element.ALIGN_RIGHT, 1, bfBold12);          //11
            insertCell(table, "Pending Day", Element.ALIGN_LEFT, 1, bfBold12);              //12
            insertCell(table, "Days", Element.ALIGN_LEFT, 1, bfBold12);                 //13
            insertCell(table, "WS 1", Element.ALIGN_LEFT, 1, bfBold12);                 //14
            insertCell(table, "Days", Element.ALIGN_LEFT, 1, bfBold12);                 //15
            insertCell(table, "WS 2", Element.ALIGN_LEFT, 1, bfBold12);                 //16
            insertCell(table, "Days", Element.ALIGN_LEFT, 1, bfBold12);                 //17
            insertCell(table, "WS 3", Element.ALIGN_LEFT, 1, bfBold12);                 //18
            insertCell(table, "Days", Element.ALIGN_LEFT, 1, bfBold12);                 //19
            insertCell(table, "WS 4", Element.ALIGN_LEFT, 1, bfBold12);                 //20
            insertCell(table, "Days", Element.ALIGN_LEFT, 1, bfBold12);                 //21
            insertCell(table, "WS 5", Element.ALIGN_LEFT, 1, bfBold12);                 //22
            insertCell(table, "Days", Element.ALIGN_LEFT, 1, bfBold12);                 //23
            insertCell(table, "WS 6", Element.ALIGN_LEFT, 1, bfBold12);                 //24
            insertCell(table, "Days", Element.ALIGN_LEFT, 1, bfBold12);                 //25
//            insertCell(table, "Completed", Element.ALIGN_LEFT, 1, bfBold12);            //26
//            insertCell(table, "Days", Element.ALIGN_LEFT, 1, bfBold12);                 //27
            insertCell(table, "Invoiced", Element.ALIGN_LEFT, 1, bfBold12);             //28
            insertCell(table, "Days", Element.ALIGN_LEFT, 1, bfBold12);                 //29

            table.setHeaderRows(1);

            //insert an empty row
            insertCell(table, " ", Element.ALIGN_LEFT, 29, bfBold12);
            double orderTotal, total = 0;

            //////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
            TreeMap<String, Hashtable<String, Hashtable<Integer, JSONObject>>> sorted = new TreeMap<String, Hashtable<String, Hashtable<Integer, JSONObject>>>();

            // Copy all data from hashMap into TreeMap
            sorted.putAll(HashBatch);

//            System.out.println(sorted.toString());
            for (Map.Entry<String, Hashtable<String, Hashtable<Integer, JSONObject>>> entry : sorted.entrySet()) {
                String key = entry.getKey();
                insertCell(table, "Batch Number  :- " + key, Element.ALIGN_LEFT, 29, bfBold12);

                Row row = sheet.createRow(++rowNum);
                row.createCell(0).setCellValue("Batch Number  :- " + key);
                CellRangeAddress address = new CellRangeAddress(row.getRowNum(), row.getRowNum(), 0, columns.length - 1);
                sheet.addMergedRegion(address);

                Hashtable<String, Hashtable<Integer, JSONObject>> value = entry.getValue();

                for (Map.Entry<String, Hashtable<Integer, JSONObject>> entryy : value.entrySet()) {
                    String keyy = entryy.getKey();
                    Hashtable<Integer, JSONObject> valuee = entryy.getValue();

                    ArrayList<Integer> keyList = new ArrayList<Integer>(valuee.keySet());
//                    System.out.println(keyList);
                    Collections.sort(keyList);
//                    System.out.println(keyList);

                    JSONObject valueee = valuee.get(keyList.get(keyList.size() - 1));
                    JSONObject valuemin = valuee.get(keyList.get(0));
//                    System.out.println(valuemin.get("STARDT").toString());

                    DateFormat inputFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss.S");
                    DateFormat inputFormatOnlydate = new SimpleDateFormat("yyyy-MM-dd");
//                    if (valuemin.get("STARDT").toString().contains("/")) {
//                        inputFormat = new SimpleDateFormat("MM/dd/yyyy HH:mm:ss");
//                        inputFormatOnlydate = new SimpleDateFormat("MM/dd/yyyy");
//                    }
                    DateFormat outFormat = new SimpleDateFormat("yyyy-MM-dd");

                    dateS = inputFormat.parse(valuemin.get("STARDT").toString());
                    dateE = inputFormat.parse(valueee.get("TXNDAT").toString());

                    String outputDateS = outFormat.format(dateS);
                    String outputDateE = outFormat.format(dateE);

//                    System.out.println(outputDateS);
//                    System.out.println(outputDateE);
                    dateS = inputFormatOnlydate.parse(outputDateS);
                    dateE = inputFormatOnlydate.parse(outputDateE);
//                    System.out.println(dateS);
//                    System.out.println(dateE);

                    difference = dateE.getTime() - dateS.getTime();
                    daysBetween = (difference / (1000 * 60 * 60 * 24));

//                    System.out.println(daysBetween);
                    insertCell(table, valueee.get("PONUM").toString(), Element.ALIGN_LEFT, 1, bf12);
                    insertCell(table, valueee.get("DOC_NO").toString(), Element.ALIGN_LEFT, 1, bf12);
                    insertCell(table, valueee.get("STARDT").toString(), Element.ALIGN_LEFT, 1, bf12);
                    insertCell(table, valueee.get("PERJOB").toString(), Element.ALIGN_LEFT, 1, bf12);
                    insertCell(table, valueee.get("PERJOBTYPE").toString(), Element.ALIGN_LEFT, 1, bf12);
                    insertCell(table, valueee.get("LINK").toString(), Element.ALIGN_LEFT, 1, bf12);
                    insertCell(table, valueee.get("CUST_SUP").toString(), Element.ALIGN_LEFT, 1, bf12);
                    insertCell(table, valueee.get("ITMCOD").toString(), Element.ALIGN_LEFT, 1, bf12);
                    insertCell(table, valueee.get("ITMDESC").toString(), Element.ALIGN_LEFT, 1, bf12);
                    insertCell(table, valuemin.get("ITMQTY").toString(), Element.ALIGN_LEFT, 1, bf12);
                    insertCell(table, df.format(Double.parseDouble(valueee.get("INV_AMT").toString())), Element.ALIGN_RIGHT, 1, bf12);

                    insertCell(table, "Total Pending Day ", Element.ALIGN_LEFT, 1, bf12);

                    ////////////////////////////////////////////////////////////////////////////////////////////////
                    row = sheet.createRow(++rowNum);
                    int collCount = 0;
//                    row.createCell(collCount).setCellValue(valueee.get("").toString());
//                    row.getCell(collCount).setCellStyle(boarderCellStyle);
                    row.createCell(collCount).setCellValue(valueee.get("PONUM").toString());
                    row.getCell(collCount).setCellStyle(boarderCellStyle);
                    row.createCell(++collCount).setCellValue(valueee.get("DOC_NO").toString());
                    row.getCell(collCount).setCellStyle(boarderCellStyle);
                    row.createCell(++collCount).setCellValue(outputDateS);
                    row.getCell(collCount).setCellStyle(boarderCellStyle);
                    row.createCell(++collCount).setCellValue(outputDateE);
                    row.getCell(collCount).setCellStyle(boarderCellStyle);
                    row.createCell(++collCount).setCellValue(valueee.get("PERJOB").toString());
                    row.getCell(collCount).setCellStyle(boarderCellStyle);
                    row.createCell(++collCount).setCellValue(valueee.get("PERJOBTYPE").toString());
                    row.getCell(collCount).setCellStyle(boarderCellStyle);
                    row.createCell(++collCount).setCellValue(valueee.get("LINK").toString());
                    row.getCell(collCount).setCellStyle(boarderCellStyle);
                    row.createCell(++collCount).setCellValue(valueee.get("CUST_SUP").toString());
                    row.getCell(collCount).setCellStyle(boarderCellStyle);
                    row.createCell(++collCount).setCellValue(valueee.get("ITMCOD").toString());
                    row.getCell(collCount).setCellStyle(boarderCellStyle);
                    row.createCell(++collCount).setCellValue(valueee.get("ITMDESC").toString());
                    row.getCell(collCount).setCellStyle(boarderCellStyle);
                    row.createCell(++collCount).setCellValue(valuemin.get("ITMQTY").toString());
                    row.getCell(collCount).setCellStyle(boarderCellStyle);
                    row.createCell(++collCount).setCellValue(df.format(Double.parseDouble(valueee.get("INV_AMT").toString())));
                    row.getCell(collCount).setCellStyle(boarderCellStyle);
                    row.createCell(++collCount).setCellValue("Total Pending Day ");
                    row.getCell(collCount).setCellStyle(boarderCellStyle);

                    ////////////////////////////////////////////////////////////////////////////////////////////////
                    dateS = inputFormat.parse(valuemin.get("STARDT").toString());
//                    System.out.println(dateS);
                    jobStart = dateS;

                    int loopCount = 7;
                    for (int i = 0; i < keyList.size(); i++) {
                        String keyyy = keyList.get(i) + "";

                        valueee = valuee.get(keyList.get(i));
//                        System.out.println("seq id" + keyList.get(i));
                        dateE = inputFormat.parse(valueee.get("TXNDAT").toString());
//                        System.out.println(dateE);
                        difference = dateE.getTime() - dateS.getTime();
                        daysBetween = (difference / (1000 * 60 * 60 * 24));
//                        System.out.println(daysBetween);

                        List<String> list = (List<String>) valueee.get("list");
                        Collections.reverse(list);
//                        System.out.println(valueee.get("WKCENTID").toString());
//                        System.out.println(list);
                        boolean isWrite = false;

                        --loopCount;
//                        System.out.println(loopCount);

//                                            row.createCell(++collCount).setCellValue("Total Pending Day ");
//                    row.getCell(collCount).setCellStyle(boarderCellStyle);
//                    row.createCell(++collCount).setCellValue(valueee.get("TRANWC").toString());
//                    row.getCell(collCount).setCellStyle(boarderCellStyle);
//                    row.createCell(++collCount).setCellValue(valueee.get("ITMQTY").toString().replace("-", ""));
//                    row.getCell(collCount).setCellStyle(boarderCellStyle);
//                    row.createCell(++collCount).setCellValue(daysBetween);
//                    row.getCell(collCount).setCellStyle(boarderCellStyle);
                        if (valueee.get("WKCENTID").toString().contains("NA")) {
                            isWrite = true;
                        }
                        if (valueee.get("WKCENTID").toString().contains("01")) {
                            insertCell(table, daysBetween + "", Element.ALIGN_LEFT, 1, bf12);
                            insertCell(table, valueee.get("TRANWC").toString(), Element.ALIGN_LEFT, 1, bf12);

                            row.createCell(++collCount).setCellValue(daysBetween + "");
                            row.getCell(collCount).setCellStyle(boarderCellStyle);
                            row.createCell(++collCount).setCellValue(valueee.get("TRANWC").toString());
                            row.getCell(collCount).setCellStyle(boarderCellStyle);

                            isWrite = true;
                        }
                        if (valueee.get("WKCENTID").toString().contains("02")) {
                            insertCell(table, daysBetween + "", Element.ALIGN_LEFT, 1, bf12);
                            insertCell(table, valueee.get("TRANWC").toString(), Element.ALIGN_LEFT, 1, bf12);

                            row.createCell(++collCount).setCellValue(daysBetween + "");
                            row.getCell(collCount).setCellStyle(boarderCellStyle);
                            row.createCell(++collCount).setCellValue(valueee.get("TRANWC").toString());
                            row.getCell(collCount).setCellStyle(boarderCellStyle);

                            isWrite = true;
                        }
                        if (valueee.get("WKCENTID").toString().contains("03")) {
                            insertCell(table, daysBetween + "", Element.ALIGN_LEFT, 1, bf12);
                            insertCell(table, valueee.get("TRANWC").toString(), Element.ALIGN_LEFT, 1, bf12);
                            row.createCell(++collCount).setCellValue(daysBetween + "");
                            row.getCell(collCount).setCellStyle(boarderCellStyle);
                            row.createCell(++collCount).setCellValue(valueee.get("TRANWC").toString());
                            row.getCell(collCount).setCellStyle(boarderCellStyle);
                            isWrite = true;
                        }
                        if (valueee.get("WKCENTID").toString().contains("04")) {
                            insertCell(table, daysBetween + "", Element.ALIGN_LEFT, 1, bf12);
                            insertCell(table, valueee.get("TRANWC").toString(), Element.ALIGN_LEFT, 1, bf12);
                            row.createCell(++collCount).setCellValue(daysBetween + "");
                            row.getCell(collCount).setCellStyle(boarderCellStyle);
                            row.createCell(++collCount).setCellValue(valueee.get("TRANWC").toString());
                            row.getCell(collCount).setCellStyle(boarderCellStyle);
                            isWrite = true;
                        }
                        if (valueee.get("WKCENTID").toString().contains("05")) {
                            insertCell(table, daysBetween + "", Element.ALIGN_LEFT, 1, bf12);
                            insertCell(table, valueee.get("TRANWC").toString(), Element.ALIGN_LEFT, 1, bf12);
                            row.createCell(++collCount).setCellValue(daysBetween + "");
                            row.getCell(collCount).setCellStyle(boarderCellStyle);
                            row.createCell(++collCount).setCellValue(valueee.get("TRANWC").toString());
                            row.getCell(collCount).setCellStyle(boarderCellStyle);
                            isWrite = true;
                        }
                        if (valueee.get("WKCENTID").toString().contains("06")) {
                            insertCell(table, daysBetween + "", Element.ALIGN_LEFT, 1, bf12);
                            insertCell(table, valueee.get("TRANWC").toString(), Element.ALIGN_LEFT, 1, bf12);
                            row.createCell(++collCount).setCellValue(daysBetween + "");
                            row.getCell(collCount).setCellStyle(boarderCellStyle);
                            row.createCell(++collCount).setCellValue(valueee.get("TRANWC").toString());
                            row.getCell(collCount).setCellStyle(boarderCellStyle);
                            isWrite = true;
                        }
                        if (valueee.get("WKCENTID").toString().contains("99")) {
                            insertCell(table, daysBetween + "", Element.ALIGN_LEFT, 1, bf12);
                            insertCell(table, valueee.get("TRANWC").toString(), Element.ALIGN_LEFT, 1, bf12);

                            row.createCell(++collCount).setCellValue(daysBetween + "");
                            row.getCell(collCount).setCellStyle(boarderCellStyle);
                            row.createCell(++collCount).setCellValue(valueee.get("TRANWC").toString());
                            row.getCell(collCount).setCellStyle(boarderCellStyle);
                            isWrite = true;
                        }

                        if (list.contains(valueee.get("WKCENTID").toString()) && isWrite) {
//                            System.out.println("DATA");
                            dateS = inputFormat.parse(valueee.get("TXNDAT").toString());
                        } else {
//                            System.out.println("EMPTY");

                            insertCell(table, "", Element.ALIGN_LEFT, 1, bf12);
                            insertCell(table, "", Element.ALIGN_LEFT, 1, bf12);

                            row.createCell(++collCount).setCellValue("");
                            row.getCell(collCount).setCellStyle(boarderCellStyle);
                            row.createCell(++collCount).setCellValue("");
                            row.getCell(collCount).setCellStyle(boarderCellStyle);
                        }

                    }
                    for (int i = 0; i < loopCount; i++) {
                        insertCell(table, "", Element.ALIGN_LEFT, 1, bf12);
                        insertCell(table, "", Element.ALIGN_LEFT, 1, bf12);
                    }
                    difference = dateE.getTime() - jobStart.getTime();
                    daysBetween = (difference / (1000 * 60 * 60 * 24));
//                    System.out.println(daysBetween);
                    insertCell(table, daysBetween + "", Element.ALIGN_LEFT, 1, bf12);
//                    insertCell(table, "DONE", Element.ALIGN_LEFT, 1, bf12);
//                    insertCell(table, "", Element.ALIGN_LEFT, 1, bf12);
//                    insertCell(table, "", Element.ALIGN_LEFT, 1, bf12);
                    if (valueee.containsKey("INV_DATE")) {
                        Date d = new Date();
                        difference = d.getTime() - jobStart.getTime();
                        float daysBetweenInv = (difference / (1000 * 60 * 60 * 24));
//                        System.out.println(daysBetween);
                        insertCell(table, ((valueee.get("INV_DATE").toString().contains("IN Stock")) ? "Stock" : "Invoiced"), Element.ALIGN_LEFT, 1, bf12);
                        insertCell(table, daysBetweenInv + "", Element.ALIGN_LEFT, 1, bf12);

                        row.createCell(++collCount).setCellValue(daysBetween + "");
                        row.getCell(collCount).setCellStyle(boarderCellStyle);
                        row.createCell(26).setCellValue(((valueee.get("INV_DATE").toString().contains("IN Stock")) ? "Stock" : "Invoiced"));
                        row.getCell(26).setCellStyle(boarderCellStyle);
                        row.createCell(27).setCellValue(daysBetweenInv);
                        row.getCell(27).setCellStyle(boarderCellStyle);
                    } else {
                        insertCell(table, "", Element.ALIGN_LEFT, 1, bf12);
                        insertCell(table, "", Element.ALIGN_LEFT, 1, bf12);

                        row.createCell(++collCount).setCellValue(daysBetween + "");
                        row.getCell(collCount).setCellStyle(boarderCellStyle);
                        row.createCell(++collCount).setCellValue("");
                        row.getCell(collCount).setCellStyle(boarderCellStyle);
                    }

                }

            }

            //add the PDF table to the paragraph
            paragraph.add(table);
            // add the paragraph to the document
            doc.add(paragraph);

            // Resize all columns to fit the content size
            for (int i = 0; i < columns.length; i++) {
                sheet.autoSizeColumn(i);
            }

            // Write the output to a file
            String pathv = System.getProperty("user.dir") + "\\" + pdfFilename + ".xls";
            FileOutputStream fileOut = new FileOutputStream(pathv);
            workbook.write(fileOut);
            fileOut.close();

            // Closing the workbook
            workbook.close();

        } catch (DocumentException dex) {
            dex.printStackTrace();
        } catch (Exception ex) {
            ex.printStackTrace();
        } finally {
            if (doc != null) {
                //close the document
                doc.close();
            }
            if (docWriter != null) {
                //close the writer
                docWriter.close();
            }

//            if (true) {
//                try {
//                    CreateMailPurchaseOrders("menaka.p@arpico.com", "salesorder.exp@arpico.com", "", "SUNJECT", pdfFilename, pdfFilename, pdfFilename, pdfFilename, "BODY 1", "BODY 2", "BODY 3", "BODY 4");
////                     CreateMailPurchaseOrders(_strToAddress, "salesorder.exp@arpico.com", _strCcAddress, "SUNJECT", pdfFilename, pdfFilename, "", "BODY 1", "BODY 2", "BODY 3", "BODY 4");
////                CreateMailPurchaseOrders("menaka.p@arpico.com", "salesorder.exp@arpico.com", "Expired Purchase Orders - Richard Pieris & Company ", "One ", "Two ", "<br><br><br> This is an automated message from Purchase order expiry process.");
//                } catch (Exception ex) {
//                    Logger.getLogger(PanelReport.class.getName()).log(Level.SEVERE, null, ex);
//                }
//            }
        }
    }

    void createPDFStatus(String pdfFilename, Hashtable<String, Hashtable<String, Hashtable<Integer, JSONObject>>> HashBatch) {

        ///////////////////////////////////
        Document doc = new Document();
        PdfWriter docWriter = null;
        ///////////////////////////////////
//        HSSFWorkbook workbook = new HSSFWorkbook();
        Workbook workbook = (Workbook) new HSSFWorkbook(); // new HSSFWorkbook() for generating `.xls` file
        String[] columns = {"PO number", "SO number", "SO creation date", "JOb Started date", "Job number", "Job type",
            "Linked", "Customer name", "Item code", "Item description", "O Qty", " Order value", "Status", "Status-QTY", "Since"};
        CreationHelper createHelper = workbook.getCreationHelper();
        Sheet sheet = workbook.createSheet("Panel Batch");

        int rowNum = 1;

        // Create a Font for styling header cells
        org.apache.poi.ss.usermodel.Font headerFont = workbook.createFont();
        // headerFont.setBold(true);
        headerFont.setFontHeightInPoints((short) 12);
        headerFont.setColor(IndexedColors.BLACK.getIndex());

        // Create a CellStyle with the font
        CellStyle headerCellStyle = workbook.createCellStyle();
        headerCellStyle.setFont(headerFont);
        headerCellStyle.setBorderBottom(HSSFCellStyle.BORDER_THIN);
        headerCellStyle.setBorderLeft(HSSFCellStyle.BORDER_THIN);
        headerCellStyle.setBorderRight(HSSFCellStyle.BORDER_THIN);
        headerCellStyle.setBorderTop(HSSFCellStyle.BORDER_THIN);

        // Create Cell Style for formatting Date
        CellStyle dateCellStyle = workbook.createCellStyle();
//        dateCellStyle.setDataFormat(createHelper.createDataFormat().getFormat("dd-MM-yyyy"));
//        dateCellStyle.setBorderBottom(HSSFCellStyle.BORDER_THIN);
//        dateCellStyle.setBorderLeft(HSSFCellStyle.BORDER_THIN);
//        dateCellStyle.setBorderRight(HSSFCellStyle.BORDER_THIN);
//        dateCellStyle.setBorderTop(HSSFCellStyle.BORDER_THIN);

        // Create Cell Style for formatting Date
        CellStyle boarderCellStyle = workbook.createCellStyle();
        boarderCellStyle.setBorderBottom(HSSFCellStyle.BORDER_THIN);
        boarderCellStyle.setBorderLeft(HSSFCellStyle.BORDER_THIN);
        boarderCellStyle.setBorderRight(HSSFCellStyle.BORDER_THIN);
        boarderCellStyle.setBorderTop(HSSFCellStyle.BORDER_THIN);

        // Create Cell Style for formatting Date
        CellStyle boarderCellStyleThousandSeparator = workbook.createCellStyle();
        boarderCellStyle.setBorderBottom(HSSFCellStyle.BORDER_THIN);
        boarderCellStyle.setBorderLeft(HSSFCellStyle.BORDER_THIN);
        boarderCellStyle.setBorderRight(HSSFCellStyle.BORDER_THIN);
        boarderCellStyle.setBorderTop(HSSFCellStyle.BORDER_THIN);
        boarderCellStyle.setDataFormat(HSSFDataFormat.getBuiltinFormat("#,##0"));

        // Create Cell Style for formatting Date
        CellStyle boarderCellStyleCenter = workbook.createCellStyle();
        boarderCellStyleCenter.setFont(headerFont);
        boarderCellStyleCenter.setBorderBottom(HSSFCellStyle.BORDER_THIN);
        boarderCellStyleCenter.setBorderLeft(HSSFCellStyle.BORDER_THIN);
        boarderCellStyleCenter.setBorderRight(HSSFCellStyle.BORDER_THIN);
        boarderCellStyleCenter.setBorderTop(HSSFCellStyle.BORDER_THIN);
        boarderCellStyleCenter.setBorderTop(CellStyle.ALIGN_CENTER);

        // Create a Row
        Row headerRow = sheet.createRow(1);

        // Create cells
        for (int i = 0; i < columns.length; i++) {
            Cell cell = headerRow.createCell(i);
            cell.setCellValue(columns[i]);
            cell.setCellStyle(headerCellStyle);
        }

        /////////////////////////////////// POI
        DecimalFormat df = new DecimalFormat("0.00");

        try {

            //special font sizes
            Font bfBold12 = new Font(Font.FontFamily.TIMES_ROMAN, 12, Font.BOLD, new BaseColor(0, 0, 0));
            Font bf12 = new Font(Font.FontFamily.TIMES_ROMAN, 12);

            //file path
            String path = System.getProperty("user.dir") + "\\" + pdfFilename + ".pdf";
            docWriter = PdfWriter.getInstance(doc, new FileOutputStream(path));

            //document header attributes
            doc.addAuthor("betterThanZero");
            doc.addCreationDate();
            doc.addProducer();
            doc.addCreator("MySampleCode.com");
            doc.addTitle("Report with Column Headings");
            doc.setPageSize(PageSize.B2);

            //open document
            doc.open();

            //create a paragraph
            Paragraph paragraph = new Paragraph("");

            //specify column widths
            float[] columnWidths = {
                1f, 0.5f, 1f, 1f, 1f, 0.5f, 0.5f, 3f, 2f, 3f, 0.5f,
                1f, 1f, 0.5f, 0.5f};
            //create PDF table with the given widths
            PdfPTable table = new PdfPTable(columnWidths);
            // set table width a percentage of the page width
            table.setWidthPercentage(90f);

            //insert an empty row
            insertCell(table, "Date:", Element.ALIGN_LEFT, 15, bfBold12);

            //insert column headings
            insertCell(table, "PO number", Element.ALIGN_LEFT, 1, bfBold12);            //01
            insertCell(table, "SO number", Element.ALIGN_LEFT, 1, bfBold12);            //02
            insertCell(table, "SO creation date", Element.ALIGN_LEFT, 1, bfBold12);     //03
            insertCell(table, "JOb Started date", Element.ALIGN_LEFT, 1, bfBold12);     //03
            insertCell(table, "Job number", Element.ALIGN_LEFT, 1, bfBold12);           //04
            insertCell(table, "Job type", Element.ALIGN_LEFT, 1, bfBold12);             //05
            insertCell(table, "Linked", Element.ALIGN_LEFT, 1, bfBold12);               //06
            insertCell(table, "Customer name", Element.ALIGN_LEFT, 1, bfBold12);        //07
            insertCell(table, "Item code", Element.ALIGN_LEFT, 1, bfBold12);            //08
            insertCell(table, "Item description", Element.ALIGN_LEFT, 1, bfBold12);     //09
            insertCell(table, "O Qty", Element.ALIGN_LEFT, 1, bfBold12);                  //10
            insertCell(table, "Order value", Element.ALIGN_RIGHT, 1, bfBold12);          //11
            insertCell(table, "Status ", Element.ALIGN_LEFT, 1, bfBold12);              //12
            insertCell(table, "Status Qty", Element.ALIGN_LEFT, 1, bfBold12);              //13
            insertCell(table, "Since", Element.ALIGN_LEFT, 1, bfBold12);                 //14

            table.setHeaderRows(1);

            insertCell(table, " ", Element.ALIGN_LEFT, 15, bfBold12);
            double orderTotal, total = 0;

            //////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
            TreeMap<String, Hashtable<String, Hashtable<Integer, JSONObject>>> sorted = new TreeMap<String, Hashtable<String, Hashtable<Integer, JSONObject>>>();

            sorted.putAll(HashBatch);

//            System.out.println(sorted.toString());
            ArrayList<String> keyMainList = new ArrayList<String>(sorted.keySet());
//            System.out.println(keyMainList);
            Collections.sort(keyMainList);
//            System.out.println(keyMainList);
            for (int i = 0; i < keyMainList.size(); i++) {
                String key = keyMainList.get(i) + "";
//                System.out.println(key);
                Hashtable<String, Hashtable<Integer, JSONObject>> value = sorted.get(key);

                insertCell(table, "Batch Number  :- " + key, Element.ALIGN_LEFT, 15, bfBold12);
//                System.out.println(rowNum);
                Row row = sheet.createRow(++rowNum);
                row.createCell(0).setCellValue("Batch Number  :- " + key);
                CellRangeAddress address = new CellRangeAddress(row.getRowNum(), row.getRowNum(), 0, columns.length - 1);
                sheet.addMergedRegion(address);

                for (Map.Entry<String, Hashtable<Integer, JSONObject>> entryy : value.entrySet()) {
                    String keyy = entryy.getKey();
                    Hashtable<Integer, JSONObject> valuee = entryy.getValue();

                    ArrayList<Integer> keyList = new ArrayList<Integer>(valuee.keySet());
//                    System.out.println(keyList);
                    Collections.sort(keyList);
//                    System.out.println(keyList);

                    String keyyy = keyList.get(keyList.size() - 1) + "";

                    JSONObject valueee = valuee.get(keyList.get(keyList.size() - 1));
                    JSONObject valuemin = valuee.get(keyList.get(0));

                    long difference;
                    float daysBetween;
                    DateFormat inputFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss.S");
                    DateFormat inputFormatOnlydate = new SimpleDateFormat("yyyy-MM-dd");
                    DateFormat outFormat = new SimpleDateFormat("yyyy-MM-dd");

                    Date dateS = inputFormat.parse(valuemin.get("STARDT").toString());
                    Date dateE = inputFormat.parse(valueee.get("TXNDAT").toString());

                    String outputDateS = outFormat.format(dateS);
                    String outputDateE = outFormat.format(dateE);

//                    System.out.println(outputDateS);
//                    System.out.println(outputDateE);
                    dateS = inputFormatOnlydate.parse(outputDateS);
                    dateE = inputFormatOnlydate.parse(outputDateE);
//                    System.out.println(dateS);
//                    System.out.println(dateE);

                    difference = dateE.getTime() - dateS.getTime();
                    daysBetween = (difference / (1000 * 60 * 60 * 24));

//                    System.out.println(daysBetween);
                    insertCell(table, valueee.get("PONUM").toString(), Element.ALIGN_LEFT, 1, bf12);
                    insertCell(table, valueee.get("DOC_NO").toString(), Element.ALIGN_LEFT, 1, bf12);
                    insertCell(table, outputDateS, Element.ALIGN_LEFT, 1, bf12);
                    insertCell(table, outputDateE, Element.ALIGN_LEFT, 1, bf12);
                    insertCell(table, valueee.get("PERJOB").toString(), Element.ALIGN_LEFT, 1, bf12);
                    insertCell(table, valueee.get("PERJOBTYPE").toString(), Element.ALIGN_LEFT, 1, bf12);
                    insertCell(table, valueee.get("LINK").toString(), Element.ALIGN_LEFT, 1, bf12);
                    insertCell(table, valueee.get("CUST_SUP").toString(), Element.ALIGN_LEFT, 1, bf12);
                    insertCell(table, valueee.get("ITMCOD").toString(), Element.ALIGN_LEFT, 1, bf12);
                    insertCell(table, valueee.get("ITMDESC").toString(), Element.ALIGN_LEFT, 1, bf12);
                    insertCell(table, valuemin.get("ITMQTY").toString(), Element.ALIGN_LEFT, 1, bf12);
                    insertCell(table, df.format(Double.parseDouble(valueee.get("INV_AMT").toString())), Element.ALIGN_RIGHT, 1, bf12);
                    insertCell(table, valueee.get("TRANWC").toString(), Element.ALIGN_LEFT, 1, bf12);
                    insertCell(table, valueee.get("ITMQTY").toString().replace("-", ""), Element.ALIGN_LEFT, 1, bf12);

                    insertCell(table, daysBetween + "", Element.ALIGN_LEFT, 1, bf12);

                    /////////////////////////////////////////////////////////////////////
                    row = sheet.createRow(++rowNum);
                    int collCount = 0;
                    row.createCell(collCount).setCellValue(valueee.get("PONUM").toString());
                    row.getCell(collCount).setCellStyle(boarderCellStyle);
                    row.createCell(++collCount).setCellValue(valueee.get("DOC_NO").toString());
                    row.getCell(collCount).setCellStyle(boarderCellStyle);
                    row.createCell(++collCount).setCellValue(outputDateS);
                    row.getCell(collCount).setCellStyle(boarderCellStyle);
                    row.createCell(++collCount).setCellValue(outputDateE);
                    row.getCell(collCount).setCellStyle(boarderCellStyle);
                    row.createCell(++collCount).setCellValue(valueee.get("PERJOB").toString());
                    row.getCell(collCount).setCellStyle(boarderCellStyle);
                    row.createCell(++collCount).setCellValue(valueee.get("PERJOBTYPE").toString());
                    row.getCell(collCount).setCellStyle(boarderCellStyle);
                    row.createCell(++collCount).setCellValue(valueee.get("LINK").toString());
                    row.getCell(collCount).setCellStyle(boarderCellStyle);
                    row.createCell(++collCount).setCellValue(valueee.get("CUST_SUP").toString());
                    row.getCell(collCount).setCellStyle(boarderCellStyle);
                    row.createCell(++collCount).setCellValue(valueee.get("ITMCOD").toString());
                    row.getCell(collCount).setCellStyle(boarderCellStyle);
                    row.createCell(++collCount).setCellValue(valueee.get("ITMDESC").toString());
                    row.getCell(collCount).setCellStyle(boarderCellStyle);
                    row.createCell(++collCount).setCellValue(valuemin.get("ITMQTY").toString());
                    row.getCell(collCount).setCellStyle(boarderCellStyle);
                    row.createCell(++collCount).setCellValue(df.format(Double.parseDouble(valueee.get("INV_AMT").toString())));
                    row.getCell(collCount).setCellStyle(boarderCellStyle);
                    row.createCell(++collCount).setCellValue(valueee.get("TRANWC").toString());
                    row.getCell(collCount).setCellStyle(boarderCellStyle);
                    row.createCell(++collCount).setCellValue(valueee.get("ITMQTY").toString().replace("-", ""));
                    row.getCell(collCount).setCellStyle(boarderCellStyle);
                    row.createCell(++collCount).setCellValue(daysBetween);
                    row.getCell(collCount).setCellStyle(boarderCellStyle);

                }

            }

            paragraph.add(table);
            doc.add(paragraph);

            // Resize all columns to fit the content size
            for (int i = 0; i < columns.length; i++) {
                sheet.autoSizeColumn(i);
            }

            // Write the output to a file
            String pathv = System.getProperty("user.dir") + "\\" + pdfFilename + ".xls";
            FileOutputStream fileOut = new FileOutputStream(pathv);
            workbook.write(fileOut);
            fileOut.close();

            // Closing the workbook
            workbook.close();

        } catch (DocumentException dex) {
            dex.printStackTrace();
        } catch (Exception ex) {
            ex.printStackTrace();
        } finally {
            if (doc != null) {
                doc.close();
            }
            if (docWriter != null) {
                docWriter.close();
            }

            File f1 = new File(System.getProperty("user.dir") + "\\PanelReportBreakDown.pdf");
            File f2 = new File(System.getProperty("user.dir") + "\\PanelReportBreakDown.xls");
            File f3 = new File(System.getProperty("user.dir") + "\\PanelReportStatus.pdf");
            File f4 = new File(System.getProperty("user.dir") + "\\PanelReportStatus.xls");
            if (f1.exists() && f2.exists() && f3.exists() && f4.exists()) {
                try {
                    CreateMailPurchaseOrders(_strToAddress, "e_marksys@arpico.com", _strCcAddress, _strBccAddress, "WIP Monitoring report", "PanelReportBreakDown.pdf", "PanelReportBreakDown.xls", "PanelReportStatus.pdf", "PanelReportStatus.xls", "This is a System Generated email.For any queries please contact Group IT - Application Support Team.\n"
                            + " \n"
                            + "Regards,", "", "", "");
                    System.out.println("email report successful \n to-"+_strToAddress +"\n cc-"+_strCcAddress+"\n bcc-"+_strBccAddress);
                } catch (Exception ex) {
                    Logger.getLogger(PanelReport.class.getName()).log(Level.SEVERE, null, ex);
                }
            } else {
                try {
                    CreateMailPurchaseOrders("pabodhar@arpico.com", "e_marksys@arpico.com", "", "menaka.p@arpico.com", "ERR In WIP Monitoring report", (f1.exists()) ? f1.getName() : "", (f2.exists()) ? f2.getName() : "", (f3.exists()) ? f3.getName() : "", (f4.exists()) ? f4.getName() : "", "<br><br>  This is a System Generated Err email. Please Check PanelReprt.java <br><br><br>"
                            + f1.getName() + " --- " + f1.exists() + "<br>" + f2.getName() + " --- " + f2.exists() + "<br>" + f3.getName() + " --- " + f3.exists() + "<br>" + f4.getName() + " --- " + f4.exists()
                            + "<br>", "", "", "");
                } catch (Exception ex) {
                    Logger.getLogger(PanelReport.class.getName()).log(Level.SEVERE, null, ex);
                }
            }
        }
    }

    private static void insertCell(PdfPTable table, String text, int align, int colspan, Font font) {

        //create a new cell with the specified Text and Font
        PdfPCell cell = new PdfPCell(new Phrase(text.trim(), font));
        //set the cell alignment
        cell.setHorizontalAlignment(align);
        //set the cell column span in case you want to merge two or more cells
        cell.setColspan(colspan);
        //in case there is no text and you wan to create an empty row
        if (text.trim().equalsIgnoreCase("")) {
            cell.setMinimumHeight(10f);
        }
        //add the call to the table
        table.addCell(cell);

    }

    boolean CreateMailPurchaseOrders(String strtoAddress, String prm_strFromAddress, String prm_strCCAddress, String prm_strBCCAddress, String prm_strHeader, String strFilename1, String strFilename2, String strFilename3, String strFilename4,
            String prm_strboodyText1, String prm_strboodyText2, String prm_strbodytext3, String prm_strbodytext4) throws Exception {
        boolean result = false;

        strFilename1 = (strFilename1.isEmpty()) ? "" : System.getProperty("user.dir") + "\\" + strFilename1;
        strFilename2 = (strFilename2.isEmpty()) ? "" : System.getProperty("user.dir") + "\\" + strFilename2;
        strFilename3 = (strFilename3.isEmpty()) ? "" : System.getProperty("user.dir") + "\\" + strFilename3;
        strFilename4 = (strFilename4.isEmpty()) ? "" : System.getProperty("user.dir") + "\\" + strFilename4;

        try {
            String from = prm_strFromAddress;
            String[] to = strtoAddress.split(",");
            String[] cc = prm_strCCAddress.split(",");
            String[] bcc = prm_strBCCAddress.split(",");
            String fileAttachment1 = strFilename1;
            String fileAttachment2 = strFilename2;
            String fileAttachment3 = strFilename3;
            String fileAttachment4 = strFilename4;
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
            for (int i = 0; i < bcc.length; i++) {
                if (!bcc[i].equals("")) {
                    message.addRecipient(Message.RecipientType.BCC, new InternetAddress(bcc[i]));
                }
            }
            message.setSubject(prm_strHeader);
            MimeBodyPart messageBodyPart1 = null;
            MimeBodyPart messageBodyPart2 = null;
            MimeBodyPart messageBodyPart3 = null;
            MimeBodyPart messageBodyPart4 = null;
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
            if (!fileAttachment4.trim().equals("")) {
                messageBodyPart4 = new MimeBodyPart();
                messageBodyPart4.attachFile(fileAttachment4);
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
            if (!fileAttachment4.trim().equals("")) {
                multipart.addBodyPart(messageBodyPart4);
            }
            message.setContent(multipart);
            Transport.send(message);
            result = true;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return result;
    }


    String getEmailAddressPurchaseOrders() {
        String m_strQty = "";
        String condition = "";
        String m_sql = "";
        try {
            _conOracleCon = new OracleConnector("192.168.1.27", "rpd2", "LMD", new jText.TextUti().getText("oracle"));

            Statement m_stmnt = _conOracleCon.getConn().createStatement(ResultSet.TYPE_SCROLL_SENSITIVE, ResultSet.CONCUR_READ_ONLY);

            m_sql = "select cval01,cval02,cval03 from smsyspara where sbucod = 'XXX' and loccod = 'XXX' and parcod = 'PANEL'";

            ResultSet rs = m_stmnt.executeQuery(m_sql);
            if (rs.next()) {
                m_strQty = rs.getString("cval01");

//                _strToAddress = rs.getString("cval01");
//                _strCcAddress = rs.getString("cval02");
//                _strBccAddress= rs.getString("cval03");
                _strToAddress = (rs.getString("cval01") == null) ? "" : rs.getString("cval01");
                _strCcAddress = (rs.getString("cval02") == null) ? "" : rs.getString("cval02");
                _strBccAddress = (rs.getString("cval03") == null) ? "" : rs.getString("cval03");
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
}
