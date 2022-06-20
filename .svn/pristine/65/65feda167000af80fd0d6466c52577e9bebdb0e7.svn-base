/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package executables;

import dataaccess.OracleConnector;
import java.net.Inet4Address;
import java.net.InetAddress;
import java.net.NetworkInterface;
import java.sql.ResultSet;
import java.sql.Statement;
import java.util.Enumeration;
import java.util.Properties;
import javax.activation.DataHandler;
import javax.activation.DataSource;
import javax.activation.FileDataSource;
import javax.mail.Message;
import javax.mail.Session;
import javax.mail.Transport;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeBodyPart;
import javax.mail.internet.MimeMessage;
import javax.mail.internet.MimeMultipart;

/**
 *
 * @author pabodhar
 */
public class CRMBdayMail {

    private String _strIpAddress;
//    OracleConnector con = null;
//    OracleConnector con = new OracleConnector("192.168.1.27", "rpd2", "CRM_TEST", "CRM_TEST");
    OracleConnector conmarksys = null;
    String _strRepPath = "";
    Exception _exception = null;

    public void SendBdayMail() {

        try {
            conmarksys = new OracleConnector("192.168.1.27", "rpd2", "MARKSYS", new jText.TextUti().getText("oracle"));
//            con = new OracleConnector("192.168.1.27", "rpd2", "CRM", new jText.TextUti().getText("crm"));
            String m_strcs_name = "";
            String m_strCusEmail = "";
            String m_strCustTitle = "";
            String m_strtoday = "";
            String m_strFilePath = getImagePath();//"\\\\192.168.1.3\\MarkSysUpdates\\Happy Birthday to you.jpg";
            if (m_strFilePath == null || m_strFilePath.trim().equals("")) {
                throw new Exception(_exception.getLocalizedMessage());
            }

            if (validatePostedDate() == 0) {
                throw new Exception(_exception.getLocalizedMessage());
            }

            Statement stmnt = conmarksys.getConn().createStatement(ResultSet.TYPE_SCROLL_SENSITIVE, ResultSet.CONCUR_READ_ONLY);
            String m_strSql = "select cust_id,case when cust_title='Other' then title1 else cust_title end as cust_title,initcap(cust_app_name) cust_app_name,cust_dob,cust_email1,to_char(sysdate,'DD/MM/YYYY') as today from crm.crm_cust_mast "
                    + " where sbu_code='830'  and to_char(cust_dob,'DD-Mon') = to_char(sysdate,'DD-Mon')"
                    + " and (trim(cust_email1)<>'' or trim(cust_email1) is not null) and cust_stat='VLD'";

            ResultSet rs = stmnt.executeQuery(m_strSql);
            if (rs.next()) {
                rs.beforeFirst();
                GetIP();
                while (rs.next()) {
                    m_strcs_name = "";
                    m_strCusEmail = "";
                    m_strCustTitle = "";
                    m_strcs_name = (rs.getString("cust_app_name") == null ? "" : rs.getString("cust_app_name"));
                    m_strCusEmail = (rs.getString("cust_email1") == null ? "" : rs.getString("cust_email1"));
                    m_strCustTitle = (rs.getString("cust_title") == null ? "" : rs.getString("cust_title"));
                    m_strtoday = rs.getString("today");
                    if (!m_strCusEmail.trim().equals("") && !m_strcs_name.trim().equals("")) {
//                        if (CreateMail(m_strCusEmail, "privilege@arpico.com", m_strFilePath, "privilege@arpico.com", m_strcs_name, m_strCustTitle) == null) {
                        if (CreateMail(m_strCusEmail, "privilege@arpico.com", m_strFilePath, "privilege@arpico.com", m_strcs_name, m_strCustTitle) == null) {
                            continue;
                        }
                    }
                }
            }
            if (startTransaction() == 0) {
                throw new Exception(_exception.getLocalizedMessage());
            }
            if (INSERT_CRM_CUSTTRAN_LOG(m_strtoday) == 0) {
                throw new Exception(_exception.getLocalizedMessage());
            }
            if (endTransaction() == 0) {
                throw new Exception(_exception.getLocalizedMessage());
            }

            stmnt.close();
            stmnt = null;
            rs.close();
            rs = null;
//            con.getConn().close();
            conmarksys.getConn().close();
        } catch (Exception e) {
            abortTransaction();
            e.printStackTrace();
        }

    }

    private int INSERT_CRM_CUSTTRAN_LOG(String prm_strDate) {
        try {
            Statement m_stmnt = conmarksys.getConn().createStatement(ResultSet.TYPE_SCROLL_SENSITIVE, ResultSet.CONCUR_READ_ONLY);
            String m_strSQL = "INSERT INTO crm.CRM_CUSTTRAN_LOG "
                    + "(sbu_code,old_card_no,new_card_no,cre_by,cre_date,LOG_REM,doc_code) "
                    + "VALUES('830','0000000000','0000000000','system',sysdate,'Last Posted Date:" + prm_strDate + "','POST')";

            if (m_stmnt.executeUpdate(m_strSQL) <= 0) {
                throw new Exception(" Error occured in Inserting to CRM_CUSTTRAN_LOG");
            }
            m_stmnt.close();
            m_stmnt = null;

            return 1;
        } catch (Exception e) {
            e.printStackTrace();
            _exception = e;
            return 0;
        }
    }

    private int validatePostedDate() {
        try {

            Statement m_stmnt = conmarksys.getConn().createStatement(ResultSet.TYPE_SCROLL_SENSITIVE, ResultSet.CONCUR_READ_ONLY);
            ResultSet rs = null;
            String m_strQry = "select substr(log_rem,18,30) log_rem from crm.crm_custtran_log "
                    + " where sbu_code='830' and substr(log_rem,18,30)=to_char(sysdate,'DD/MM/YYYY') and doc_code='POST'";
            rs = m_stmnt.executeQuery(m_strQry);
            if (rs.next()) {
                throw new Exception("E-mails are already sent for current date");
            }
            rs = null;
            m_stmnt.close();
            return 1;
        } catch (Exception e) {
            e.printStackTrace();
            _exception = e;
            return 0;
        }
    }

    private String getImagePath() {
        try {
            ResultSet _rsrep = null;
            String m_strSQL = "select cval01 from smsyspara where sbucod='830' and loccod='100' and parcod='CRMPOST'";
            Statement m_stmnt = conmarksys.getConn().createStatement(ResultSet.TYPE_SCROLL_SENSITIVE, ResultSet.CONCUR_READ_ONLY);
            _rsrep = m_stmnt.executeQuery(m_strSQL);
            if (_rsrep.next()) {
                _strRepPath = _rsrep.getString("cval01");
            } else {
                throw new Exception("Parameter is not set for Image.(CRMPOST)");
            }
            _rsrep.close();
            m_stmnt.close();
            m_stmnt = null;
            return _strRepPath;
        } catch (Exception e) {
            e.printStackTrace();
            _exception = e;
            return null;
        }
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

    public String CreateMail(String strtoAddress, String prm_strFromAddress, String strFilename, String prm_strCCAddress, String prm_strHeader, String prm_strCustTitle) {
        try {
            String from = prm_strFromAddress;
            String[] to = strtoAddress.split(",");
            String fileAttachment = strFilename;
            prm_strHeader = prm_strCustTitle.concat(" ").concat(prm_strHeader).concat(",");
            String bodytext1 = "Dear " + prm_strHeader + "<br><br>";
            String bodytext2 = "<img src=\"" + fileAttachment + "\">";
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
                message.setSubject("Birthday Wishes");
            }
            MimeMultipart multipart = new MimeMultipart("related");
            MimeBodyPart messageBodyPart = new MimeBodyPart();
            String htmlText1 = "Please use the email link below for any inquiries<br>Email us on privilege@arpico.com<br>If you do not wish to receive our updates, please forward the email to privilege@arpico.com with subject line: \"Unsubscribe\"";
            String htmlText = "Dear " + prm_strHeader + "<br><br><img src=\"cid:image\"><br><br>" + htmlText1;
            messageBodyPart.setContent(htmlText, "text/html");
            multipart.addBodyPart(messageBodyPart);
            messageBodyPart = new MimeBodyPart();
            DataSource fds = new FileDataSource(fileAttachment);
            messageBodyPart.setDataHandler(new DataHandler(fds));
            messageBodyPart.setHeader("Content-ID", "<image>");
            multipart.addBodyPart(messageBodyPart);
            message.setContent(multipart);
            Transport.send(message);
            return "success";
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }


    }

    public int startTransaction() {
        try {
            if (conmarksys.getConn().getAutoCommit() == false) {
                conmarksys.rollback();
                conmarksys.getConn().setAutoCommit(true);
            }
            conmarksys.getConn().setAutoCommit(false);
            return 1;
        } catch (Exception e) {
            e.printStackTrace();
//            _exception = e;
            return 0;
        }
    }

    public int endTransaction() {
        try {
            conmarksys.getConn().commit();
            conmarksys.getConn().setAutoCommit(true);
            return 1;
        } catch (Exception e) {
            e.printStackTrace();
//            _exception = e;
            return 0;
        }
    }

    public void abortTransaction() {
        try {
            if (conmarksys.getConn().getAutoCommit() == false) {
                conmarksys.getConn().rollback();
                conmarksys.getConn().setAutoCommit(true);
            }

        } catch (Exception e) {
            e.printStackTrace();
//            _exception = e;

        }
    }
}
