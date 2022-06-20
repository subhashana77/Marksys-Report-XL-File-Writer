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
import java.util.HashMap;

/**
 *
 * @author Thilina
 */
public class AWSEcomAppDbBackup {

    private String _sshserver;
    private int _port;
    private String _user;
    private String _pass;

    private ChannelSftp _channelSftp;
    private Session _session;
    private Channel _channel;
    private Exception _exception;

    HashMap _awsToOraclePara;

    private boolean AWStoOracle(HashMap awsToOraclePara, String prm_date) {
        boolean m_result = false;
        DateFormat dateFormat = new SimpleDateFormat("dd-MM-yyyy");;
        Date currentDate = new Date();
        String SFTPWORKINGDIR = (String) awsToOraclePara.get("working_dir");
        String currentDateString = dateFormat.format(currentDate);
        try {
            if (prm_date == null) {
                SFTPWORKINGDIR += awsToOraclePara.get("file_prefix").toString() + currentDateString + awsToOraclePara.get("file_ext").toString(); // Source Directory on SFTP server
            } else if (prm_date.trim().length() > 0) {
                SFTPWORKINGDIR += awsToOraclePara.get("file_prefix").toString() + currentDateString + awsToOraclePara.get("file_ext").toString(); // Source Directory on SFTP server
            }
            System.out.println("Transfering...");
            _channelSftp.get(SFTPWORKINGDIR, (String) awsToOraclePara.get("backup_dir"));
            System.out.println("Transfer completed.!");
        } catch (Exception e) {
            System.out.println("Transfer failed.!");
            e.printStackTrace();
        } finally {
            SSHConClose();
        }
        return m_result;
    }

    private boolean SSHConnection(String prm_privateKeyPath) {
        boolean success = false;

        try {
            _user = "ec2-user";
            _pass = "";
            _port = 22;
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

    public boolean CurrentDateTransferApp() {
        try {
            _sshserver = "18.140.132.70";
            if (!SSHConnection(System.getProperty("user.dir") + "/arpico-app-server2.ppk")) {
                throw new Exception(_exception);
            } else {
                _awsToOraclePara = new HashMap();
                _awsToOraclePara.put("working_dir", "/home/ec2-user/Arpicosupercentre_AppBackup/backups/");
                _awsToOraclePara.put("file_prefix", "aws_app_");
                _awsToOraclePara.put("file_ext", ".tar.gz");
                _awsToOraclePara.put("backup_dir", "E:\\Thilina\\NetbeansProjects\\CronJobApplication\\autobackups\\app");
                AWStoOracle(_awsToOraclePara, null);
            }
            return true;
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }

    public boolean CurrentDateTransferDB() {
        try {
            _sshserver = "18.140.149.83";
            if (!SSHConnection(System.getProperty("user.dir") + "/arpico-db-server2.ppk")) {
                throw new Exception(_exception);
            } else {
                _awsToOraclePara = new HashMap();
                _awsToOraclePara.put("working_dir", "/home/ec2-user/Arpicosupercentre_DBBackup/backups/");
                _awsToOraclePara.put("file_prefix", "ecomsite_");
                _awsToOraclePara.put("file_ext", ".sql.gz");
                _awsToOraclePara.put("backup_dir", "E:\\Thilina\\NetbeansProjects\\CronJobApplication\\autobackups\\db");
            }
            AWStoOracle(_awsToOraclePara, null);
            return true;
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }

    public static void main(String[] args) {
        AWSEcomAppDbBackup awspb = new AWSEcomAppDbBackup();
        System.out.println("starting..." + System.getProperty("user.dir"));
//        awspb.CurrentDateTransferDB();
        awspb.CurrentDateTransferApp();
//        awspb.SSHConnection("E:/Thilina/NetbeansProjects/CronJobApplication/arpico-app-server2.ppk");

    }

}