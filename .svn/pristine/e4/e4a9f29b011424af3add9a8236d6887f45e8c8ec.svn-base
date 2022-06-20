/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package cronMail;

import dataaccess.MysqlConnector;
import dataaccess.OracleConnector;
import dblogin.DBLogin;
import executables.CEBMailer;
import java.sql.ResultSet;
import java.util.Properties;
import javax.activation.DataHandler;
import javax.activation.FileDataSource;
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
 * @author indika.kuruppu
 */
public class CreateMail {

    private String strSBU;
    private String strLoc;
    private String strProgId;
    private ResultSet _rsetautomail;
    private OracleConnector _oradb;
    private MysqlConnector _mySqldb;
    private String __strDatabase;
    private String __strIP;
    private String __strDBName;
    private String __strUserName;
    private String __strPwd;
    private String __srtDate = "";

    public CreateMail() {
        readFile();
        if (__strDatabase.equalsIgnoreCase("oracle")) {
            _oradb = new OracleConnector(__strIP, __strDBName, __strUserName, __strPwd);
        } else {
            _mySqldb = new MysqlConnector(__strIP, __strDBName, __strUserName, __strPwd);
        }
    }

    public CreateMail(String prm_strDate) {
        readFile();
        if (__strDatabase.equalsIgnoreCase("oracle")) {
            _oradb = new OracleConnector(__strIP, __strDBName, __strUserName, __strPwd);
        } else {
            _mySqldb = new MysqlConnector(__strIP, __strDBName, __strUserName, __strPwd);
        }
        __srtDate = prm_strDate;
    }

    public void readFile() {
        try {
            java.io.RandomAccessFile file = new java.io.RandomAccessFile(System.getProperty("user.dir") + "/rms.cfg", "r");
            __strDatabase = file.readLine();
            __strIP = file.readLine();
            __strDBName = file.readLine();
            __strUserName = file.readLine();
            __strPwd = new jText.TextUti().getText("bview");//file.readLine().trim();

            //the reading of password from text file is ignored and a password given from application property file is taken
            if (__strDatabase.equalsIgnoreCase("mysql")) {
//                __strPwd = new DBLogin().getDbPassword();
            }
            file.close();
        } catch (Exception e) {
            javax.swing.JOptionPane.showMessageDialog(null, "Could Not Find rms.cfg file to connect to the database");
            e.printStackTrace();
            System.exit(0);
        }
    }

    public void readAutoMail() {
        try {
            System.out.println("date3:" + __srtDate);
            _rsetautomail = null;
            String m_strDate = __srtDate.equals("") ? " to_char(sysdate,'MMDD')" : " to_char(to_date('" + __srtDate + "'),'MMDD')";
            String m_strQry = "select a.*,"
                    + " " + m_strDate + " as filedate"
                    + " from smautomail a where a.sbucod='" + getStrSBU() + "' "
                    + " and a.loccod='" + getStrLoc() + "' and a.progid='" + getStrProgId() + "'";
            if (__strDatabase.equalsIgnoreCase("oracle")) {
                _rsetautomail = _oradb.executeQuery(m_strQry);
            } else {
                _rsetautomail = _mySqldb.executeQuery(m_strQry);
            }
            if (_rsetautomail.next()) {
                sendMail();
            } else {
                throw new Exception("Cannot find email config for programe id " + getStrProgId());
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void sendMail() throws Exception {

        String host = _rsetautomail.getString("mailhs") == null ? "" : _rsetautomail.getString("mailhs");
        String from = _rsetautomail.getString("frmadd") == null ? "" : _rsetautomail.getString("frmadd");
        String tempto = _rsetautomail.getString("toaddr") == null ? "" : _rsetautomail.getString("toaddr");
        String tempCC = _rsetautomail.getString("carcpy") == null ? "" : _rsetautomail.getString("carcpy");
        String tempBCC = _rsetautomail.getString("bcccpy") == null ? "" : _rsetautomail.getString("bcccpy");
        String m_strSubject = _rsetautomail.getString("subjet") == null ? "" : _rsetautomail.getString("subjet");
        String m_strBody = _rsetautomail.getClob("mssage").getSubString(1, (int) _rsetautomail.getClob("mssage").length()) == null ? "" : _rsetautomail.getClob("mssage").getSubString(1, (int) _rsetautomail.getClob("mssage").length());
        String temp_Attachments = _rsetautomail.getString("attach") == null ? "" : _rsetautomail.getString("attach");
        String m_strProgrammID = _rsetautomail.getString("progid") == null ? "" : _rsetautomail.getString("progid");
        String m_strFileDate = _rsetautomail.getString("filedate") == null ? "" : _rsetautomail.getString("filedate");

        String[] to = tempto.split(";");
        String[] cc = tempCC.split(";");
        String[] bcc = tempBCC.split(";");

        String fileAttachment[] = temp_Attachments.split(";");
        if (m_strProgrammID.equalsIgnoreCase("CEBMailer")) {
            for (int i = 0; i < fileAttachment.length; i++) {
                fileAttachment[i] = fileAttachment[i].concat(m_strFileDate).concat(".SDF");
            }
            m_strFileDate = "";
        }

        // Get system properties
        Properties props = System.getProperties();

        // Setup mail server
        props.put("mail.smtp.host", host);
        System.out.println("Mail Host : " + host);

        // Get session
        Session session = Session.getInstance(props, null);

        // Define message
        MimeMessage message = new MimeMessage(session);
        message.setFrom(new InternetAddress(from));
        System.out.println("Masge From : " + from);

        for (int i = 0; i < to.length; i++) {
            if (!to[i].trim().equalsIgnoreCase("")) {
                message.addRecipient(Message.RecipientType.TO, new InternetAddress(to[i]));
                System.out.println("Masge sent to : " + to[i]);
            }
        }

        for (int i = 0; i < cc.length; i++) {
            if (!cc[i].trim().equalsIgnoreCase("")) {
                message.addRecipient(Message.RecipientType.CC, new InternetAddress(cc[i]));
                System.out.println("Masge sent to CC : " + cc[i]);
            }
        }

        for (int i = 0; i < bcc.length; i++) {
            if (!bcc[i].trim().equalsIgnoreCase("")) {
                message.addRecipient(Message.RecipientType.BCC, new InternetAddress(bcc[i]));
                System.out.println("Masge sent to BCC : " + bcc[i]);
            }
        }

        message.setSubject(m_strSubject);
        System.out.println("Masge Sublect : " + m_strSubject);

        // create the message part
        MimeBodyPart messageBodyPart = new MimeBodyPart();

        //fill message
        messageBodyPart.setText(m_strBody);
        System.out.println("Body : " + m_strBody);

        Multipart multipart = new MimeMultipart();
        multipart.addBodyPart(messageBodyPart);

        // Part two is attachment
        for (int i = 0; i < fileAttachment.length; i++) {
            if (!fileAttachment[i].trim().equalsIgnoreCase("")) {
                messageBodyPart = new MimeBodyPart();
                FileDataSource fds = new FileDataSource(fileAttachment[i]);
                messageBodyPart.setDataHandler(new DataHandler(fds));
                messageBodyPart.setFileName(fds.getName());
                multipart.addBodyPart(messageBodyPart);
                System.out.println("Attachments : " + fileAttachment[i]);
            }
        }

        // Put parts in message
        message.setContent(multipart);

        // Send the message
        Transport.send(message);
    }

    public void sendMail(String prm_strHost, String prm_strFrom, String prm_strTempto, String prm_strTempCC, String prm_strTempBCC, String prm_strSubject, String prm_StrBody, String prm_strAttachments) throws Exception {

        String host = prm_strHost;
        String from = prm_strFrom;
        String tempto = prm_strTempto;
        String tempCC = prm_strTempCC;
        String tempBCC = prm_strTempBCC;
        String m_strSubject = prm_strSubject;
        String m_strBody = prm_StrBody;
        String temp_Attachments = prm_strAttachments;

        String[] to = tempto.split(";");
        String[] cc = tempCC.split(";");
        String[] bcc = tempBCC.split(";");

        String fileAttachment[] = temp_Attachments.split(";");

        // Get system properties
        Properties props = System.getProperties();

        // Setup mail server
        props.put("mail.smtp.host", host);
        System.out.println("Mail Host : " + host);

        // Get session
        Session session = Session.getInstance(props, null);

        // Define message
        MimeMessage message = new MimeMessage(session);
        message.setFrom(new InternetAddress(from));
        System.out.println("Masge From : " + from);

        for (int i = 0; i < to.length; i++) {
            if (!to[i].trim().equalsIgnoreCase("")) {
                message.addRecipient(Message.RecipientType.TO, new InternetAddress(to[i]));
                System.out.println("Masge sent to : " + to[i]);
            }
        }

        for (int i = 0; i < cc.length; i++) {
            if (!cc[i].trim().equalsIgnoreCase("")) {
                message.addRecipient(Message.RecipientType.CC, new InternetAddress(cc[i]));
                System.out.println("Masge sent to CC : " + cc[i]);
            }
        }

        for (int i = 0; i < bcc.length; i++) {
            if (!bcc[i].trim().equalsIgnoreCase("")) {
                message.addRecipient(Message.RecipientType.BCC, new InternetAddress(bcc[i]));
                System.out.println("Masge sent to BCC : " + bcc[i]);
            }
        }

        message.setSubject(m_strSubject);
        System.out.println("Masge Sublect : " + m_strSubject);

        // create the message part
        MimeBodyPart messageBodyPart = new MimeBodyPart();

        //fill message
        messageBodyPart.setText(m_strBody);
        System.out.println("Body : " + m_strBody);

        Multipart multipart = new MimeMultipart();
        multipart.addBodyPart(messageBodyPart);

        // Part two is attachment
        for (int i = 0; i < fileAttachment.length; i++) {
            if (!fileAttachment[i].trim().equalsIgnoreCase("")) {
                messageBodyPart = new MimeBodyPart();
                FileDataSource fds = new FileDataSource(fileAttachment[i]);
                messageBodyPart.setDataHandler(new DataHandler(fds));
                messageBodyPart.setFileName(fds.getName());
                multipart.addBodyPart(messageBodyPart);
                System.out.println("Attachments : " + fileAttachment[i]);
            }
        }

        // Put parts in message
        message.setContent(multipart);

        // Send the message
        Transport.send(message);
    }

    /**
     * @return the strSBU
     */
    public String getStrSBU() {
        return strSBU;
    }

    /**
     * @param strSBU the strSBU to set
     */
    public void setStrSBU(String strSBU) {
        this.strSBU = strSBU;
    }

    /**
     * @return the strLoc
     */
    public String getStrLoc() {
        return strLoc;
    }

    /**
     * @param strLoc the strLoc to set
     */
    public void setStrLoc(String strLoc) {
        this.strLoc = strLoc;
    }

    /**
     * @return the strProgId
     */
    public String getStrProgId() {
        return strProgId;
    }

    /**
     * @param strProgId the strProgId to set
     */
    public void setStrProgId(String strProgId) {
        this.strProgId = strProgId;
    }

    /**
     * @return the __srtDate
     */
    public String getSrtDate() {
        return __srtDate;
    }

    /**
     * @param __srtDate the __srtDate to set
     */
    public void setSrtDate(String __srtDate) {
        this.__srtDate = __srtDate;
    }

    private void sendMailCEB() {
        try {
            String host = _rsetautomail.getString("mailhs") == null ? "" : _rsetautomail.getString("mailhs");
            String from = _rsetautomail.getString("frmadd") == null ? "" : _rsetautomail.getString("frmadd");
            String tempto = _rsetautomail.getString("toaddr") == null ? "" : _rsetautomail.getString("toaddr");
            String tempCC = _rsetautomail.getString("carcpy") == null ? "" : _rsetautomail.getString("carcpy");
            String tempBCC = _rsetautomail.getString("bcccpy") == null ? "" : _rsetautomail.getString("bcccpy");
            String m_strSubject = _rsetautomail.getString("subjet") == null ? "" : _rsetautomail.getString("subjet");
            String m_strBody = _rsetautomail.getClob("mssage").getSubString(1, (int) _rsetautomail.getClob("mssage").length()) == null ? "" : _rsetautomail.getClob("mssage").getSubString(1, (int) _rsetautomail.getClob("mssage").length());
            String temp_Attachments = _rsetautomail.getString("attach") == null ? "" : _rsetautomail.getString("attach");
            String m_strProgrammID = _rsetautomail.getString("progid") == null ? "" : _rsetautomail.getString("progid");
            String m_strFileDate = _rsetautomail.getString("filedate") == null ? "" : _rsetautomail.getString("filedate");

            String[] to = tempto.split(";");
            String[] cc = tempCC.split(";");
            String[] bcc = tempBCC.split(";");

            String fileAttachment[] = temp_Attachments.split(";");
            if (m_strProgrammID.equalsIgnoreCase("CEBMailer")) {
                for (int i = 0; i < fileAttachment.length; i++) {
                    fileAttachment[i] = fileAttachment[i].concat(m_strFileDate).concat(".SDF");
                }
                m_strFileDate = "";
            }

            // Get system properties
            Properties props = System.getProperties();

            // Setup mail server
            props.put("mail.smtp.host", host);
            System.out.println("Mail Host : " + host);

            // Get session
            Session session = Session.getInstance(props, null);

            // Define message
            MimeMessage message = new MimeMessage(session);
            message.setFrom(new InternetAddress(from));
            System.out.println("Masge From : " + from);

            for (int i = 0; i < to.length; i++) {
                if (!to[i].trim().equalsIgnoreCase("")) {
                    message.addRecipient(Message.RecipientType.TO, new InternetAddress(to[i]));
                    System.out.println("Masge sent to : " + to[i]);
                }
            }

            for (int i = 0; i < cc.length; i++) {
                if (!cc[i].trim().equalsIgnoreCase("")) {
                    message.addRecipient(Message.RecipientType.CC, new InternetAddress(cc[i]));
                    System.out.println("Masge sent to CC : " + cc[i]);
                }
            }

            for (int i = 0; i < bcc.length; i++) {
                if (!bcc[i].trim().equalsIgnoreCase("")) {
                    message.addRecipient(Message.RecipientType.BCC, new InternetAddress(bcc[i]));
                    System.out.println("Masge sent to BCC : " + bcc[i]);
                }
            }

            message.setSubject(m_strSubject);
            System.out.println("Masge Sublect : " + m_strSubject);

            // create the message part
            MimeBodyPart messageBodyPart = new MimeBodyPart();

            //fill message
            messageBodyPart.setText(m_strBody);
            System.out.println("Body : " + m_strBody);

            Multipart multipart = new MimeMultipart();
            multipart.addBodyPart(messageBodyPart);

            // Part two is attachment
            for (int i = 0; i < fileAttachment.length; i++) {
                if (!fileAttachment[i].trim().equalsIgnoreCase("")) {
                    messageBodyPart = new MimeBodyPart();
                    FileDataSource fds = new FileDataSource(fileAttachment[i]);
                    messageBodyPart.setDataHandler(new DataHandler(fds));
                    messageBodyPart.setFileName(fds.getName());
                    multipart.addBodyPart(messageBodyPart);
                    System.out.println("Attachments : " + fileAttachment[i]);
                }
            }

            // Put parts in message
            message.setContent(multipart);

            // Send the message
            Transport.send(message);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
