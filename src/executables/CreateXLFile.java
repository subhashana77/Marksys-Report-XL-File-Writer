package executables;

import dataaccess.OracleConnector;
import org.apache.commons.net.ftp.FTPClient;
//import org.apache.commons.net.ftp.FTP;
//import org.apache.commons.net.ftp.FTPClient;
//import org.apache.poi.hssf.usermodel.HSSFRow;
//import org.apache.poi.hssf.usermodel.HSSFSheet;
//import org.apache.poi.hssf.usermodel.HSSFWorkbook;

import javax.sql.RowSet;
import java.io.*;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.HashMap;

/**
 * @author dilshan.r
 * @created 08/03/2022 - 10:21 AM
 * @project CronJobApplication
 * @ide IntelliJ IDEA
 */

public class CreateXLFile {

    new FTPClient

    String xlFilePath = "";
    String renamedFile = "";

    //Create oracle database connection
    OracleConnector con = new OracleConnector("192.168.1.27", "rpd2", "TMARKSYS", "TESTMARKSYS");
    Exception _exception = null;

    public void posTxnFileUpload() {

        // call to method, who is write the data to XL file
        txnFileWriter();

        System.exit(0);
    }

    //    get ftp and path parameters on cloud
    public HashMap<String, String> getSysPara() {

        HashMap<String, String> m_para = new HashMap<>();

        try {
            //get oracle db connection from global variable
            Statement selectStatement = con.getConn().createStatement();
            ResultSet resultSet = selectStatement.executeQuery(
                    "SELECT IVAL01, CVAL01, CVAL02, CVAL03, CCOMM, CREMA " +
                            "FROM SMSYSPARA " +
                            "WHERE PARCOD ='RPFLFTP'");

            //Get all data to variables from oracle database
            while (resultSet.next()) {

                m_para.put("_ftp_port_number", resultSet.getString("IVAL01"));
                m_para.put("_ftp_server_ip", resultSet.getString("CVAL01"));
                m_para.put("_ftp_user_name", resultSet.getString("CVAL02"));
                m_para.put("_ftp_password", resultSet.getString("CVAL03"));
                m_para.put("_remote_file_path",  resultSet.getString("CCOMM"));
                m_para.put("_local_file_path", resultSet.getString("CREMA"));
            }

        } catch (Exception e ) {
            e.printStackTrace();
        }
        return m_para;
    }

    // this method will write the xl file by getting data from database
    public void txnFileWriter() {

        HashMap<String, String> sysPara = getSysPara();

        //Declare file name to be create
        xlFilePath = System.getProperty("user.dir")+"\\rpflXLFolder\\" + sysPara.get("_local_file_path");

        //Creating an instance of HSSFWorkbook class
        HSSFWorkbook workbook = new HSSFWorkbook();

        //invoking creatSheet() method and passing the name of the sheet to be created
        HSSFSheet sheet = workbook.createSheet();

        try {
            //get oracle db connection from global variable
            Statement selectStatement = con.getConn().createStatement();
            ResultSet resultSet = selectStatement.executeQuery(
                    "SELECT TXNDAT, REFTX1, REFTYP, CUSTNM, CUSTTELNO, LOCCOD, AMOUNT " +
                            "FROM RMIS_RMS_POS_BC_TXN_MAS " +
                            "WHERE SBUCOD ='830' " +
                            "AND COMCOD = 'RPFL' " +
                            "AND COLTYP = 'INST' " +
                            "AND TRANSD = 'F' " +
                            "AND to_date(GETDAT) = to_char(sysdate,'DD-Mon-YYYY')");

            //Write the data to xl sheet
            int rowNumber = 2;

            int rowCount = 0;

            //Get all data to variables from oracle database
            while (resultSet.next()) {

                rowCount = 1;

                String trans_date_time = resultSet.getString("TXNDAT");
                String ref_trans = resultSet.getString("REFTX1");
                String ref_type = resultSet.getString("REFTYP");
                String cus_name = resultSet.getString("CUSTNM");
                String cus_phone = resultSet.getString("CUSTTELNO");
                String loc_code = resultSet.getString("LOCCOD");
                double amount = resultSet.getDouble("AMOUNT");

                System.out.println(trans_date_time + " | " + ref_trans + " | " + ref_type + " | " + cus_name + " | " +cus_phone + " | " + loc_code + " | " + amount);

                //Creating the 0th row using the createRow() method
                HSSFRow rowhead = sheet.createRow((short) 0);

                //creating cell by using the createCell() method and setting the values to the cell by using the setCellValue() method
                rowhead.createCell((short) 0).setCellValue("Date");
                rowhead.createCell((short) 1).setCellValue("ID no");
                rowhead.createCell((short) 2).setCellValue("Facility Number");
                rowhead.createCell((short) 3).setCellValue("Customer Name");
                rowhead.createCell((short) 4).setCellValue("Mobile Number");
                rowhead.createCell((short) 5).setCellValue("RPD Location");
                rowhead.createCell((short) 6).setCellValue("Amount");

                HSSFRow row = sheet.createRow((short) rowNumber++);

                String[] parts = trans_date_time.split(" ");
                String trans_date = parts[0];
                row.createCell((short) 0).setCellValue(trans_date);

                if (ref_type.equalsIgnoreCase("I")) {
                    row.createCell((short) 1).setCellValue(ref_trans);
                    row.createCell((short) 2).setCellValue("null");
                } else if (ref_type.equalsIgnoreCase("F")) {
                    row.createCell((short) 2).setCellValue(ref_trans);
                    row.createCell((short) 1).setCellValue("null");
                }

                row.createCell((short) 3).setCellValue(cus_name);

                row.createCell((short) 4).setCellValue(cus_phone);

                row.createCell((short) 5).setCellValue(loc_code);

                row.createCell((short) 6).setCellValue(amount);
            }

            if (rowCount == 1) {
                try {
                    DateTimeFormatter dtf = DateTimeFormatter.ofPattern("yyyy_MM_dd-HH_mm_ss");
                    LocalDateTime now = LocalDateTime.now();
                    String formattedDate = dtf.format(now);

                    int localPathLength = xlFilePath.length();
                    String fileName = xlFilePath.substring(localPathLength-8, localPathLength-4);
                    String extensionName = xlFilePath.substring(localPathLength - 4);
                    renamedFile = fileName + "_" + formattedDate + extensionName;
                    String filePath = xlFilePath.substring(0, localPathLength - 8);
                    String newRenamedFilePath = filePath + renamedFile;

                    //Write the data to updated xl file
                    FileOutputStream updateFileOutputStream = new FileOutputStream(newRenamedFilePath);
                    workbook.write(updateFileOutputStream);

                    //Write the data to replaced xl file
                    FileOutputStream replacedFileOutputStream = new FileOutputStream(System.getProperty("user.dir")+"\\" + sysPara.get("_local_file_path"));
                    workbook.write(replacedFileOutputStream);

                    //closing the Stream
                    updateFileOutputStream.close();
                    replacedFileOutputStream.close();

                    //prints the message on the console
                    System.out.println("\n" + "Excel file has been generated successfully." + "\n");

                    // call to method, who is upload the file to server
                    txnFTPUploader();

                } catch (FileNotFoundException exception) {
                    System.out.println("ERROR : The process cannot access the SL file, because it is being used by another process");
                }
            } else {

                // no any updated data in the table
                System.out.println("Not any updated data to write the sheet");
            }

        } catch (SQLException | IOException throwable) {
            throwable.printStackTrace();
        }
    }

    // this method will upload the xl file to ftp path
    public void txnFTPUploader() {

        HashMap<String, String> sysPara = getSysPara();

        String server = sysPara.get("_ftp_server_ip");
        int port = Integer.parseInt(sysPara.get("_ftp_port_number"));
        String user = sysPara.get("_ftp_user_name");
        String password = sysPara.get("_ftp_password");
        String localPath = System.getProperty("user.dir")+"\\" + sysPara.get("_local_file_path");
        String remoteFile = sysPara.get("_remote_file_path");

        // get date to rename the uploaded file name

        FTPClient ftpClient = new FTPClient();

        try {
            ftpClient.connect(server, port);
            ftpClient.login(user, password);
            ftpClient.setFileType(FTP.BINARY_FILE_TYPE);

                File newFile = new File(localPath);

            try (FileInputStream fileInputStream = new FileInputStream(newFile)) {
                System.out.println("File Uploading start..." + "\n");

                // upload the XL file with ftp details
                boolean isUploaded = ftpClient.storeFile(remoteFile, fileInputStream);

                if (isUploaded) {
                    System.out.println("File upload successfully" + "\n");
                    
                    int remotePathLength = remoteFile.length();
                    String remotePath = remoteFile.substring(0, remotePathLength-8);
                    String newRenameRemoteFile = remotePath + renamedFile;
                    
                    boolean renamedRemoteFile = ftpClient.rename(remoteFile, newRenameRemoteFile);
                
                    if (renamedRemoteFile) {
                        System.out.println("Remote file renamed successfully \n");

                        // update the database
                        txnUpdateDb();
                        
                    } else {
                        System.out.println("Remote file rename fail");
                    }

                } else {
                    System.out.println("Upload Fail");
                }
            } catch (Exception e) {
                System.out.println("Something went wrong! :\n" + e.getMessage());
                e.printStackTrace();
            }

        } catch (IOException e) {
            System.out.println("Error : " + e.getMessage());
            e.printStackTrace();

        } finally {

            try {
                if (ftpClient.isConnected()) {
                    ftpClient.logout();
                    ftpClient.disconnect();
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    // this method will update the TRANSD to 'T' and TRANDT to 'current data' in the database
    public void txnUpdateDb() {

        //get oracle db connection from global variable
        try {

            Statement updateStatement = con.getConn().createStatement();

            // start transaction
            if (startTransactionTMarksys() == 0) {
                throw new Exception(_exception.getLocalizedMessage());
            }

            String updateSql = "UPDATE RMIS_RMS_POS_BC_TXN_MAS " +
                    "SET TRANSD = 'T', TRANDT = to_char(sysdate,'DD-Mon-YYYY') " +
                    "WHERE SBUCOD = '830' " +
                    "AND COMCOD = 'RPFL' " +
                    "AND COLTYP = 'INST' " +
                    "AND TRANSD = 'F' " +
                    "AND to_date(GETDAT) = to_char(sysdate,'DD-Mon-YYYY')";

            if (updateStatement.executeUpdate(updateSql) < 0) {
                System.out.println("'RMIS_RMS_POS_BC_TXN_MAS' table data update Failed! \n");
                throw new Exception();
            } else {
                System.out.println("'RMIS_RMS_POS_BC_TXN_MAS' table data update successfully! \n");
            }

            // end transaction
            if (endTransactionTMarksys() == 0) {
                throw new Exception(_exception.getLocalizedMessage());
            }

        } catch (Exception throwables) {

            // abort transaction
            abortTransactionTMarksys();

            throwables.printStackTrace();
            System.out.println("Error : " + throwables);
        }
    }

    //    Start transaction method
    public int startTransactionTMarksys() {
        try {
            if (!con.getConn().getAutoCommit()) {
                con.rollback();
                con.getConn().setAutoCommit(true);
            }
            con.getConn().setAutoCommit(false);
            return 1;
        } catch (Exception e) {
            e.printStackTrace();
            return 0;
        }
    }

    //    End Transaction method
    public int endTransactionTMarksys() {
        try {
            con.getConn().commit();
            con.getConn().setAutoCommit(true);
            return 1;
        } catch (Exception e) {
            e.printStackTrace();
            return 0;
        }
    }

    //    Abort Transaction method
    public void abortTransactionTMarksys() {
        try {
            if (!con.getConn().getAutoCommit()) {
                con.getConn().rollback();
                con.getConn().setAutoCommit(true);
            }

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

}