/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package executables;

import java.io.*;
import java.text.DateFormat;
import java.text.DateFormatSymbols;
import java.text.SimpleDateFormat;
import java.util.Date;
import org.apache.commons.net.ftp.FTP;
import org.apache.commons.net.ftp.FTPClient;

/**
 *
 * @author maheshanip
 */
/*
 * To change this template, choose Tools | Templates and open the template in
 * the editor.
 */
/**
 *
 * @author pabodhar
 */
public class FileTransfer {

    private String server = "";
    int port = 21;
    private String user = "";
    String pass = "";
    FTPClient ftpClient = new FTPClient();
    String _strERR = "";
    String _strDirName = "";

    public String uploadFileToServer(String prm_strFileName) {
        try {
            try {
                _strERR = "";
                File firstLocalFile = new File(prm_strFileName);
                String firstRemoteFile = ".//" + prm_strFileName.replace("./", "");
                InputStream inputStream = new FileInputStream(firstLocalFile);
                System.out.println("Start uploading file");
                boolean done = ftpClient.storeFile(firstRemoteFile, inputStream);
                inputStream.close();
                if (done) {
                    System.out.println("The file is uploaded successfully.");
                }
                firstLocalFile = null;
                firstRemoteFile = null;
            } catch (IOException ex) {
                System.out.println("Error: " + ex.getMessage());
                ex.printStackTrace();
                _strERR = "Error: " + ex.getMessage();
            }
        } catch (Exception e) {
            e.printStackTrace();
            _strERR = _strERR + " " + e.getMessage();
        }
        return _strERR;
    }

    public void connectFTP(String prm_strFTPServer, String prm_strUser, String FtpPwd) {
        try {
            server = prm_strFTPServer;
            user = prm_strUser;
            pass = FtpPwd;
            ftpClient.connect(getServer(), port);
            ftpClient.login(getUser(), pass);
            ftpClient.enterLocalPassiveMode();
            ftpClient.setFileType(FTP.BINARY_FILE_TYPE);
        } catch (Exception e) {
        }
    }

    public void disconnectFTP() {
        try {
            if (ftpClient.isConnected()) {
                ftpClient.logout();
                ftpClient.disconnect();
            }
        } catch (Exception ex) {
            ex.printStackTrace();
            _strERR = _strERR + " " + ex.getMessage();
        }
    }

    /**
     * @return the server
     */
    public String getServer() {
        return server;
    }

    /**
     * @param server the server to set
     */
    public void setServer(String server) {
        this.server = server;
    }

    /**
     * @return the user
     */
    public String getUser() {
        return user;
    }

    /**
     * @param user the user to set
     */
    public void setUser(String user) {
        this.user = user;
    }
//    public void createFtPdirectory(String prm_strFTPServer, String prm_strUser,
//            String FtpPwd) {
//        try {
//            server = prm_strFTPServer;
//            user = prm_strUser;
//            pass = FtpPwd;
//            ftpClient.connect(getServer(), port);
//            ftpClient.login(getUser(), pass);
//            _strDirName = "";
//            DateFormatSymbols symbols = new DateFormatSymbols();
//            DateFormat formatDate = new SimpleDateFormat("yyyyMMdd",
//                    symbols);
//            DateFormat formatTime = new SimpleDateFormat("HHmmss",
//                    symbols);
//            _strDirName = "/var/ftp/UPLOAD_RMS_ZIP_FILES/"
//                    + formatDate.format(new Date()) + formatTime.format(new Date());
//            if (!ftpClient.makeDirectory(_strDirName)) {
//                throw new IOException(
//                
//                "Unable to create remote 
//directory '" + formatDate.format(new Date()) + formatTime.format(new Date( )) + "'  .  error='" + ftpClient.getReplyString() + "'  "     
//         
//         );
//             }
//         } catch (Exception e) {
//            e.printStackTrace();
//        } finally {
//            try {
//                if (ftpClient.isConnected()) {
//                    ftpClient.logout();
//                    ftpClient.disconnect();
//                }
//            } catch (Exception ex) {
//                ex.printStackTrace();
//            }
//        }
//    }
}
