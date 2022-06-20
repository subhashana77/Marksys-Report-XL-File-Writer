/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package executables;

import com.jcraft.jsch.Channel;
import com.jcraft.jsch.ChannelSftp;
import com.jcraft.jsch.JSch;
import com.jcraft.jsch.Session;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;

/**
 *
 * @author eranda.ak
 */
public class CloudBackupTransfer {

    private String _sshserver = "";
    private int _port = 0;
    private String _user = "";
    private String _pass = "";

    private ChannelSftp _channelSftp = null;
    private Session _session = null;
    private Channel _channel = null;
    private Exception _exception = null;

    public boolean CloudTransfer(String prm_date) {
        try {
            if (!SSHConnection("/home/rpcadmin/SLT/lightcs.ppk")) {
                //if (!SSHConnection("C:\\Users\\eranda.ak\\Documents\\SLTkey\\lightcs.ppk")) {
                throw new Exception(_exception);
            }
            SLTtoOracle(prm_date);

//            if (prm_procTyep.trim().equalsIgnoreCase("down")) {
//                if (!SLTtoOracle(prm_date)) {
//                    throw new Exception("");
//                }
//            } else {
//                if (!OracletoSLT()) {
//                    throw new Exception("");
//                }
//            }
            return true;
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }

    public boolean CurrentDateTransfer() {
        try {
//            if (!SSHConnection("/home/rpcadmin/SLT/lightcs.ppk")) {
            // System.getProperty("user.dir")
            if (!SSHConnection(System.getProperty("user.dir") + "/lightcs.ppk")) {
//            if (!SSHConnection("D://SLTSetup//lightcs.ppk")) {                
                //if (!SSHConnection("C:\\Users\\eranda.ak\\Documents\\SLTkey\\lightcs.ppk")) {
                throw new Exception(_exception);
            }
            SLTtoOracle(null);

            return true;
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }

    private boolean SLTtoOracle(String prm_date) {
        boolean m_result = false;
        int SFTPPORT = 22; // SFTP Port Number
        String SFTPWORKINGDIR = "";
        //DateFormat dateFormat = new SimpleDateFormat("MMMM dd, yyyy");

        try {
            if (prm_date == null) {
                DateFormat dateFormat = new SimpleDateFormat("dd-MM-yy");
                Date currentDate = new Date();
                String currentDateString = dateFormat.format(currentDate);
                SFTPWORKINGDIR = "/var/backup/sql_sqm_" + currentDateString + ".tar.gz"; // Source Directory on SFTP server
            } else if (prm_date.trim().length() > 0) {
                Date date = new SimpleDateFormat("dd-MMM-yyyy").parse(prm_date);
                String currentDateString = new SimpleDateFormat("dd-MM-yy").format(date);
                SFTPWORKINGDIR = "/var/backup/sql_sqm_" + currentDateString + ".tar.gz"; // Source Directory on SFTP server
            }

//            if ((!prm_date.equalsIgnoreCase("00")) && prm_date.trim().length() > 0) {
//                Date date = new SimpleDateFormat("dd-MMM-yyyy").parse(prm_date);
//                String currentDateString = new SimpleDateFormat("dd-MM-yy").format(date);
//                SFTPWORKINGDIR = "/var/backup/sql_sqm_" + currentDateString + ".tar.gz"; // Source Directory on SFTP server
//            } else {
//                DateFormat dateFormat = new SimpleDateFormat("dd-MM-yy");
//                Date currentDate = new Date();
//                String currentDateString = dateFormat.format(currentDate);
//                SFTPWORKINGDIR = "/var/backup/sql_sqm_" + currentDateString + ".tar.gz"; // Source Directory on SFTP server
//            }
            System.out.println("Transfering...");
//            _channelSftp.get(SFTPWORKINGDIR, "/home/rpcadmin/SLT/SLTbackup/");
//            _channelSftp.get(SFTPWORKINGDIR, "/home/SLT/");
            _channelSftp.get(SFTPWORKINGDIR, "/SLTbackups/SLT/");
            // _channelSftp.get(SFTPWORKINGDIR, "C:\\Users\\eranda.ak\\Documents\\SLTkey");
            System.out.println("Transfer completed.!");

        } catch (Exception e) {
            System.out.println("Transfer failed.!");
            e.printStackTrace();
        } finally {
            SSHConClose();
        }
        return m_result;

    }

    private boolean OracletoSLT(String prm) {

        boolean m_result = false;
        try {

        } catch (Exception e) {
        }
        return m_result;
    }

    private boolean SSHConnection(String prm_privateKeyPath) {
        boolean success = false;

        try {

            _sshserver = "222.165.180.104";
            _user = "rpcadmin";
            _pass = "LightCS DB@cce$$ #2143o";
            _port = 52241;

            JSch jsch = new JSch();

            jsch.addIdentity(prm_privateKeyPath, _pass);
            _session = jsch.getSession(_user, _sshserver, _port);
            _session.setPassword(_pass);
            //_session.setConfig("PreferredAuthentications", "publickey,keyboard-interactive,password");
            java.util.Properties config = new java.util.Properties();
            config.put("StrictHostKeyChecking", "no");
            _session.setConfig(config);
            _session.connect(); // Create SFTP Session
            _channel = _session.openChannel("sftp"); // Open SFTP Channel
            _channel.connect();
            _channelSftp = (ChannelSftp) _channel;
            success = true;
        } catch (Exception ex) {
            ex.printStackTrace();
            _exception = ex;
        }
        return success;
    }

    private boolean SSHConClose() {
        try {
            if (_channelSftp != null) {
                _channelSftp.disconnect();
            }
            if (_channel != null) {
                _channel.disconnect();
            }
            if (_session != null) {
                _session.disconnect();
            }
            return true;
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }

}
