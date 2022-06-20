/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package executables;

import dataaccess.OracleConnector;
import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.math.BigDecimal;
import java.net.Inet4Address;
import java.net.InetAddress;
import java.net.NetworkInterface;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.Calendar;
import java.util.Enumeration;
import java.util.Properties;
import javax.mail.Message;
import javax.mail.MessagingException;
import javax.mail.Multipart;
import javax.mail.Session;
import javax.mail.Transport;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeBodyPart;
import javax.mail.internet.MimeMessage;
import javax.mail.internet.MimeMultipart;
import org.apache.commons.net.ftp.FTP;
import org.apache.commons.net.ftp.FTPClient;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;

/**
 *
 * @author eranda.ak
 */
public class InterCompanyTxns {

    OracleConnector _dbConnection = null;
    OracleConnector _dbConnectionError = null;
    private Exception _exception = null;
    private Connection _remoteDbConnection = null;
    private String _strDBTYpe;
    private String _cronUser = "cronuser";
    private String originalDb = "Oracle";
    private String _user = "", _pass = "", _ftpserver = "";
    private int _intport = 0;
    private String _sbuCod = "", _locCod = "";
    private String _strVal01;
    private String _strVal02;
    private String _strVal03;
    private double _dblVal01;
    private double _dblVal02;
    private double _dblVal03;
    private int _intVal01;
    private int _intVal02;
    private int _intVal03;
    String _strOriginatorEmail = "";
    String _strIpAddress = "";
    private JSONArray _errObjArr = new JSONArray();
    private String _strSysDate = "";
    private String _strLocalDate = "";
    private String _strErrorList = "";
    private String _strCreaterEmail = "";
    private Statement _stmtError = null;

    public InterCompanyTxns() {
        GetIP();
    }

    public String getSbuCod() {
        return _sbuCod;
    }

    public void setSbuCod(String _sbuCod) {
        this._sbuCod = _sbuCod;
    }

    public String getLocCod() {
        return _locCod;
    }

    public void setLocCod(String _locCod) {
        this._locCod = _locCod;
    }

    public String getStrVal01() {
        return _strVal01;
    }

    public void setStrVal01(String _strVal01) {
        this._strVal01 = _strVal01;
    }

    public String getStrVal02() {
        return _strVal02;
    }

    public void setStrVal02(String _strVal02) {
        this._strVal02 = _strVal02;
    }

    public String getStrVal03() {
        return _strVal03;
    }

    public void setStrVal03(String _strVal03) {
        this._strVal03 = _strVal03;
    }

    public double getDblVal01() {
        return _dblVal01;
    }

    public void setDblVal01(double _dblVal01) {
        this._dblVal01 = _dblVal01;
    }

    public double getDblVal02() {
        return _dblVal02;
    }

    public void setDblVal02(double _dblVal02) {
        this._dblVal02 = _dblVal02;
    }

    public double getDblVal03() {
        return _dblVal03;
    }

    public void setDblVal03(double _dblVal03) {
        this._dblVal03 = _dblVal03;
    }

    public int getIntVal01() {
        return _intVal01;
    }

    public void setIntVal01(int _intVal01) {
        this._intVal01 = _intVal01;
    }

    public int getIntVal02() {
        return _intVal02;
    }

    public void setIntVal02(int _intVal02) {
        this._intVal02 = _intVal02;
    }

    public int getIntVal03() {
        return _intVal03;
    }

    public void setIntVal03(int _intVal03) {
        this._intVal03 = _intVal03;
    }

//*************************************************
    public void getSyspara(String prm_strSbuCod, String prm_strLocCod, String prm_strPara) {
        Statement m_stmt = null, m_stmtMain = null;
        try {
            m_stmtMain = _dbConnection.getConn().createStatement(ResultSet.TYPE_SCROLL_INSENSITIVE, ResultSet.CONCUR_READ_ONLY);

            //**************************************************
            String _mySql_qry = "SELECT *  FROM smsyspara  where sbucod='" + prm_strSbuCod + "' "
                    + "and loccod='" + prm_strLocCod + "' "
                    //                    + " and parcod='IMGUPDPTH'";
                    + " and parcod='" + prm_strPara + "'";
            ResultSet m_result = m_stmtMain.executeQuery(_mySql_qry);
            String m_misDoc = "";
            if (m_result.next()) {
                setStrVal01(m_result.getString("CVAL01").trim());
                setStrVal02(m_result.getString("CVAL02").trim());
                setStrVal03(m_result.getString("CVAL03").trim());
                setDblVal01(Double.parseDouble(m_result.getString("DVAL01").trim()));
                setDblVal02(Double.parseDouble(m_result.getString("DVAL02").trim()));
                setDblVal03(Double.parseDouble(m_result.getString("DVAL03").trim()));
                setIntVal01(Integer.parseInt(m_result.getString("IVAL01").trim()));
                setIntVal02(Integer.parseInt(m_result.getString("IVAL02").trim()));
                setIntVal03(Integer.parseInt(m_result.getString("IVAL03").trim()));

            } else {
                throw new Exception("Para not found..!");
            }
            //**************************************************
        } catch (Exception e) {
            e.printStackTrace();
        }

    }

    //---------------------------------------------------//
    // *** ~ DESTINATION GL ENTRY WRITING PROCCESS ~ *** //
    //---------------------------------------------------//
    public JSONObject savetoDestinationGl() {
        Statement m_stmt = null, m_stmtMain = null;
        int seq = 1;
        String m_strResult = "", m_strSourceNo = "";
        JSONObject objResult = new JSONObject();
        String prm_strSbuCod = "920";
        String prm_strLocCod = "100";
        setSbuCod("920");
        setLocCod("100");
        _strSysDate = "";
        String m_DesSbu = "", m_DesLoc = "", m_DesAcc = "", m_DesDrCrAcc = "";
        try {
            _dbConnection = new OracleConnector("192.168.1.27", "RPD2", "LMD", new jText.TextUti().getText("oracle"));
            _dbConnectionError = new OracleConnector("192.168.1.27", "RPD2", "LMD", new jText.TextUti().getText("oracle"), true);

//            _dbConnection = new OracleConnector("192.168.1.27", "RPD2", "TLMD", "GIT12345");
//            _dbConnectionError = new OracleConnector("192.168.1.27", "RPD2", "TLMD", "GIT12345", true);
            String m_jentDesDoc = "", m_oriDocNum = "", m_strBatchNo = "";
            String qry = "";
            //  InetAddress localhost = InetAddress.getLocalHost();
            m_stmtMain = _dbConnection.getConn().createStatement(ResultSet.TYPE_SCROLL_INSENSITIVE, ResultSet.CONCUR_READ_ONLY);
            m_stmt = _dbConnection.getConn().createStatement(ResultSet.TYPE_SCROLL_INSENSITIVE, ResultSet.CONCUR_READ_ONLY);

            // ERROR CONNECTION
            _stmtError = _dbConnectionError.getConn().createStatement(ResultSet.TYPE_SCROLL_INSENSITIVE, ResultSet.CONCUR_READ_ONLY);

            getSyspara(prm_strSbuCod, prm_strLocCod, "INTEML");
            _strOriginatorEmail = getStrVal01();
            getSyspara(prm_strSbuCod, prm_strLocCod, "IMGUPDPTH");
            _ftpserver = getStrVal01();
            _user = getStrVal02();
            _pass = getStrVal03();
            _intport = getIntVal01();

            // SELECT ALL INTER-COMPANY TRANSACTIONS FOR GL
            qry = "select a.sbucod,a.loccod,a.LINNUM,a.SEQNUM,b.ALACID,a.INTERID,c.DESSBU,c.DESLOC,c.DESACC,c.DRCRAC,a.BATTYP,a.BATCNO,"
                    + " a.DOCCOD,a.DOCNUM,a.AMOUNT,a.TRANDT,u.email"
                    + " from gltrantemp a inner join glcharofaccs b"
                    + " on  a.sbucod=b.sbucod and a.LOCCOD=b.LOCCOD and a.interid=b.interid"
                    + " LEFT join glintcomtrans c"
                    + " on a.sbucod=c.ORISBU AND b.ALACID=c.ORIACC and a.dimm04=c.DESSBU and a.dimm05=c.DESLOC"
                    + " left join rms_users u on a.sbucod=u.sbu_code and a.loccod=u.loc_code and trim(a.creaby)=u.user_id"
                    + " where "
                    + " a.sbucod='" + prm_strSbuCod + "' and"
                    + " a.loccod='" + prm_strLocCod + "' and"
                    + " a.dimm01='INTCOM' "
                    + " and c.ORISBU is not null"
                    + " ORDER BY SBUCOD,LOCCOD,DOCCOD,DOCNUM,LINNUM,SEQNUM";
            ResultSet rs = m_stmtMain.executeQuery(qry);

            /*
             * create new batch no and insert to acpbatchmas
             */
            String m_strException = "Connection fail :\n";
            String prm_strOriDocNum = "", prm_strOriAccNum = "", prm_strOriAccID = "";
            boolean prm_remoteCon = false;

            String prm_preJent = "";
            String prm_strbattyp = "";
            String prm_strBatNum = "";
            String prm_strDocCod = "";
            String prm_strUserEmail = "";
            // MAIN LOOP FOR SELECT
            rs.beforeFirst();
            while (rs.next()) {
                //2016-11-30 GET BATCH TYPE AND DOC TYPE FROM INNER QUERY
                //---------------------------
                prm_strbattyp = rs.getString("BATTYP");
                prm_strBatNum = rs.getString("BATCNO");
                prm_strDocCod = rs.getString("DOCCOD");
                prm_strUserEmail = rs.getString("email");
                //---------------------------

                String prm_strDesSbu = rs.getString("DESSBU");
                String prm_strDesLoc = rs.getString("DESLOC");
                prm_strOriDocNum = rs.getString("DOCNUM");
                prm_strOriAccNum = rs.getString("ALACID");
                prm_strOriAccID = rs.getString("interid");

                String prm_strPreDesLoc = "";

                try {
                    // CHECK DESTINATION SBU/LOC WITH PREVIOUS ONE
                    if (!prm_strPreDesLoc.equalsIgnoreCase(prm_strDesSbu + prm_strDesLoc)) {
                        prm_remoteCon = false;
                        prm_strPreDesLoc = prm_strDesSbu + prm_strDesLoc;
                    }

                    // IF REMOTE CONNECTION FALSE, CREATE NEW REMOTE CONNECTION WITH DESTINATION SBU/LOC
                    if (!prm_remoteCon) {

                        if (dbChange(prm_strDesSbu, prm_strDesLoc)) {
                            prm_remoteCon = true;
                        } else {
                            prm_remoteCon = false;
                            m_strException += "|SBU:" + prm_strDesSbu + "|LOCATION:" + rs.getString("DESLOC") + "\n";
                        }
                    }

                    // IF REMOTE CONNCTION SUCCESFULLY CREATED
                    if (prm_remoteCon) {
                        startTransactionRemote();
                        // REMOTE STATEMENT CREATE
                        Statement m_RemotStmt = _remoteDbConnection.createStatement(ResultSet.TYPE_SCROLL_INSENSITIVE, ResultSet.CONCUR_READ_ONLY);
                        Statement m_RemotStmtInsert = _remoteDbConnection.createStatement();
                        // SOURCE REMARK
                        m_strSourceNo = "|" + rs.getString("SBUCOD") + "-" + rs.getString("loccod") + "-" + rs.getString("BATTYP") + "-"
                                + rs.getString("BATCNO") + "-" + rs.getString("DOCCOD") + "-" + rs.getString("DOCNUM");

                        // TRANSACTION DATE FORMAT
                        int m_intTranYear = 0, m_intTranMonth = 0, m_intTranDay = 0;
                        String m_strBatchDate = "";
                        if (getStrDBTYpe().toString().equalsIgnoreCase("Oracle")) {
                            Calendar cal = Calendar.getInstance();
                            cal.setTime(rs.getDate("TRANDT"));
                            m_intTranYear = cal.get(Calendar.YEAR);
                            m_intTranMonth = cal.get(Calendar.MONTH) + 1;
                            m_intTranDay = cal.get(Calendar.DAY_OF_MONTH);
                            String m_strMonth = formatMonth(m_intTranMonth);
                            m_strBatchDate = m_intTranDay + "-" + m_strMonth + "-" + m_intTranYear;
                            _strLocalDate = m_strBatchDate;
                        } else {
                            Calendar cal = Calendar.getInstance();
                            cal.setTime(rs.getDate("TRANDT"));
                            m_intTranYear = cal.get(Calendar.YEAR);
                            m_intTranMonth = cal.get(Calendar.MONTH) + 1;
                            m_intTranDay = cal.get(Calendar.DAY_OF_MONTH);
                            m_strBatchDate = m_intTranYear + "-" + m_intTranMonth + "-" + m_intTranDay;
                            _strLocalDate = m_strBatchDate;
                        }
                        // END - TRANSACTION DATE FORMAT

                        // CHECK GL FINANCIAL PERIODS
                        String m_strFinPeriod[] = getFinPeriod(m_strBatchDate, false, true, true, false, _remoteDbConnection, prm_strDesSbu);
                        if (!m_strFinPeriod[0].equals("OK")) {
                            throw new Exception(m_strFinPeriod[1]);
                        }
                        // GET BATCH NUMBER
                        String m_strDesSysDate = getStrDBTYpe().toString().equalsIgnoreCase("Oracle") ? "sysdate" : "curdate()";
                        String m_strSql = "select seqobj,(curv+1) as current_value," + m_strDesSysDate + " as systemdate from smbatchtypes a"
                                + " inner join smsequence b"
                                + " on a.sbucod=b.sbucod and a.loccod=b.loccod and a.seqobj=b.seqid"
                                + " where a.sbucod='" + prm_strDesSbu + "'"
                                + " and b.loccod='" + prm_strDesLoc + "'"
                                + " and  a.battyp='" + prm_strbattyp + "'";
                        ResultSet m_rs = m_RemotStmt.executeQuery(m_strSql);
                        if (m_rs.next()) {
                            String m_strSeqObj = m_rs.getString("seqobj");
                            m_strBatchNo = m_rs.getString("current_value"); //BATCH NUMBER
                            String m_strQry = " insert into  acpbatchmas "
                                    + "(SBUCOD, LOCCOD, BATTYP, BATCNO, BATDAT, FINAYR, FINAMN, BATSTA, BATREF, BAGPNO,"
                                    + " LOCFLG, LOCUSR, CREABY, CREADT, MODIBY, MODIDT)"
                                    + " values "
                                    + " ('" + prm_strDesSbu + "','" + prm_strDesLoc + "','" + prm_strbattyp + "',"
                                    + " '" + m_rs.getString("current_value") + "',"
                                    + " '" + m_strBatchDate + "',"
                                    + " '" + m_strFinPeriod[1] + "','" + m_strFinPeriod[2] + "',"
                                    + " 'VAL','','','N','','" + _cronUser + "',"
                                    + "" + m_strDesSysDate + ",'','')";
                            if (m_RemotStmtInsert.executeUpdate(m_strQry) <= 0) {
                                throw new Exception("Error occured in creating batch in destination sbu.(SBU:" + prm_strDesSbu + "/LOC:" + prm_strDesLoc + ")");
                            }
                            // UPDATE BATCH NUMBER
                            String qrySeq = "update smsequence set curv=(curv+1)"
                                    + " where sbucod='" + prm_strDesSbu + "' and loccod='" + prm_strDesLoc + "' and"
                                    + " seqid='" + m_strSeqObj + "'";
                            if (m_RemotStmt.executeUpdate(qrySeq) <= 0) {
                                throw new Exception("Error occured in JENT document sequence update..");
                            }
                        } else {
                            throw new Exception("Error occured in creating batch in destination sbu.(SBU:" + prm_strDesSbu + "/LOC:" + prm_strDesLoc + ")");
                        }

                        //***********-----------------------------------------------------
                        m_DesSbu = rs.getString("DESSBU");
                        m_DesLoc = rs.getString("DESLOC");
                        m_DesAcc = rs.getString("DESACC");
                        m_DesDrCrAcc = rs.getString("DRCRAC");

                        // GET ORIGIN LOCAITON TRANTEMPAPP
                        String qrySelect = "select SBUCOD,LOCCOD,BATTYP,BATCNO,DOCCOD,DOCNUM,LINNUM,SEQNUM,TRANDT,INTERID,DIMM01,DIMM02,DIMM03,DIMM04,DIMM05,AMOUNT,DRCRTY,"
                                + " CURCOD,EXCRAT,AMTFCU,REMARK,MODTYP,CREABY,CREADT,BNKREC,UNOFMS,UNTQTY,JCAT01,JCAT02,JCAT03,JCAT04,JCAT05,REACOD,"
                                + " REFTXT,LKRRAT,POSTDT,'" + _cronUser + "',to_char(sysdate,'DD-Mon-YYYY') current_date,period,filept from gltrantempapp  where "
                                + " sbucod='" + prm_strSbuCod + "' and "
                                + " loccod='" + prm_strLocCod + "' and "
                                + " battyp='" + prm_strbattyp + "' and "
                                + " batcno='" + rs.getString("BATCNO") + "' and "
                                + " doccod='" + rs.getString("DOCCOD") + "' and "
                                + " docnum='" + rs.getString("DOCNUM") + "' and "
                                + " linnum='" + rs.getString("LINNUM") + "' and "
                                + " seqnum='" + rs.getString("SEQNUM") + "' and"
                                + " interid='" + rs.getString("interid") + "' ";
                        ResultSet rsSelect = m_stmt.executeQuery(qrySelect);

                        // DATA STORE IN JSON OBJECT
                        JSONObject m_objSelect = null;
                        int m_TranYear = 0, m_TranMonth = 0, m_TranDay = 0;
                        int m_currYear = 0, m_currMonth = 0, m_currDay = 0;
                        while (rsSelect.next()) {
                            m_objSelect = new JSONObject();
                            for (int i = 1; i <= rsSelect.getMetaData().getColumnCount(); i++) {

                                String key = rsSelect.getMetaData().getColumnName(i);
                                String val = rsSelect.getString(rsSelect.getMetaData().getColumnName(i));
                                val = (val == null || val.equalsIgnoreCase("null")) ? "" : val;
                                if (key.equalsIgnoreCase("TRANDT")) {
                                    if (originalDb.equalsIgnoreCase("Oracle")) {
                                        Calendar cal = Calendar.getInstance();
                                        cal.setTime(rsSelect.getDate(rsSelect.getMetaData().getColumnName(i)));
                                        m_TranYear = cal.get(Calendar.YEAR);
                                        m_TranMonth = cal.get(Calendar.MONTH) + 1;
                                        m_TranDay = cal.get(Calendar.DAY_OF_MONTH);
                                    } else {
                                        Calendar cal = Calendar.getInstance();
                                        cal.setTime(rsSelect.getDate(rsSelect.getMetaData().getColumnName(i)));
                                        m_TranYear = cal.get(Calendar.YEAR);
                                        m_TranMonth = cal.get(Calendar.MONTH) + 1;
                                        m_TranDay = cal.get(Calendar.DAY_OF_MONTH);
                                    }
                                }
                                m_objSelect.put(key, val);
                            }
                        }
                        // END - DATA STORE IN JSON OBJECT

                        //----------------------------------------------------------
                        // DB CONNECTION CHANGE AREA
                        // REMOTE LOCAITON ACCOUNTS INTER IDS
                        if (m_DesAcc != null && m_DesDrCrAcc != null) {
                            String qryRemoteSelect = "select current_date,"
                                    + " MAX(case when ALACID in ('" + m_DesAcc + "') then INTERID else NULL end) AS DESID,"
                                    + " MAX(case when ALACID in ('" + m_DesDrCrAcc + "') then INTERID else NULL end) AS DRCRID"
                                    + " from glcharofaccs"
                                    + " where SBUCOD='" + m_DesSbu + "' AND"
                                    + " LOCCOD='" + m_DesLoc + "'  AND"
                                    + " ALACID IN ('" + m_DesAcc + "','" + m_DesDrCrAcc + "')";
                            ResultSet rsRemoteSelect = m_RemotStmt.executeQuery(qryRemoteSelect);

                            String m_DesAccID = "", m_DesDrCrAccID = "";
                            if (rsRemoteSelect.next()) {
                                m_DesAccID = rsRemoteSelect.getString("DESID") == null ? "" : rsRemoteSelect.getString("DESID");
                                m_DesDrCrAccID = rsRemoteSelect.getString("DRCRID") == null ? "" : rsRemoteSelect.getString("DRCRID");

                                if (getStrDBTYpe().toString().equalsIgnoreCase("Oracle")) {
                                    Calendar cal = Calendar.getInstance();
                                    cal.setTime(rsRemoteSelect.getDate("current_date"));
                                    m_currYear = cal.get(Calendar.YEAR);
                                    m_currMonth = cal.get(Calendar.MONTH) + 1;
                                    m_currDay = cal.get(Calendar.DAY_OF_MONTH);
                                    String m_strMonth = formatMonth(m_currMonth);
                                    _strSysDate = m_currDay + "-" + m_strMonth + "-" + m_currYear;
                                } else {

                                    Calendar cal = Calendar.getInstance();
                                    cal.setTime(rsRemoteSelect.getDate("current_date"));
                                    m_currYear = cal.get(Calendar.YEAR);
                                    m_currMonth = cal.get(Calendar.MONTH) + 1;
                                    m_currDay = cal.get(Calendar.DAY_OF_MONTH);
                                    _strSysDate = m_currYear + "-" + m_currMonth + "-" + m_currDay;
                                }

                            }
                            // FIRST (i==0) : DESTINATION ACCOUNT ENTRY(DR||CR) | SECOND (i==1) : RELATIVE NEXT ACCOUNT OF DESTINATION LOCATION (DR||CR)
                            for (int i = 0; i < 2; i++) {
                                String m_interId = "", m_Amount = "", m_type = "";
                                String m_dbtyp = getStrDBTYpe();
                                if (i == 0) {
                                    m_interId = m_DesAccID;
                                    m_Amount = new BigDecimal(m_objSelect.get("AMOUNT").toString()).multiply(new BigDecimal("-1")).toString();
                                    m_type = m_objSelect.get("DRCRTY").toString().equalsIgnoreCase("Dr") ? "CR" : "DR";
                                    if (m_dbtyp.equalsIgnoreCase("Oracle")) {
                                        String m_strMonth = formatMonth(m_TranMonth);
                                        m_objSelect.put("TRANDT", m_TranDay + "-" + m_strMonth + "-" + m_TranYear);
                                    } else {
                                        m_objSelect.put("TRANDT", m_TranYear + "-" + m_TranMonth + "-" + m_TranDay);
                                    }
                                } else {
                                    m_interId = m_DesDrCrAccID;
                                    m_Amount = m_objSelect.get("AMOUNT").toString();
                                    m_type = m_objSelect.get("DRCRTY").toString();
                                }

                                // JENT DOCNUM UPDATE //
                                if (!m_oriDocNum.equalsIgnoreCase(m_objSelect.get("DOCNUM").toString()) || i == 0) {
                                    String qrySeq = "select seqobj,(curv+1) as current_value from smpaymentparameters a"
                                            + " inner join smsequence b on a.sbucod=b.sbucod and a.seqobj=b.seqid"
                                            + " where a.sbucod='" + m_DesSbu + "'"
                                            + " and b.loccod='" + m_DesLoc + "'"
                                            + " and  a.doccod='" + prm_strDocCod + "' for update";
                                    ResultSet rsDocnum = m_RemotStmt.executeQuery(qrySeq);

                                    if (rsDocnum.next()) {
                                        m_jentDesDoc = rsDocnum.getString("current_value");
                                    }
                                    qrySeq = "update smsequence set curv=(curv+1)"
                                            + " where sbucod='" + m_DesSbu + "' and loccod='" + m_DesLoc + "' and"
                                            + " seqid='" + rsDocnum.getString("seqobj") + "'";

                                    if (m_RemotStmt.executeUpdate(qrySeq) <= 0) {
                                        throw new Exception("Error occured in JENT document sequence update..");
                                    }
                                    m_oriDocNum = m_objSelect.get("DOCNUM").toString();
                                } else {
                                    m_oriDocNum = m_objSelect.get("DOCNUM").toString();
                                }
                                // END  - JENT DOCNUM UPDATE //

                                // REMOTE LOCATION JENT INSERT
                                String qryRemoteInsert = " insert into gltrantempapp ("
                                        + " SBUCOD,LOCCOD,BATTYP,BATCNO,DOCCOD,DOCNUM,LINNUM,SEQNUM,TRANDT,INTERID,DIMM01,DIMM02,DIMM03,DIMM04,DIMM05,AMOUNT,DRCRTY,"//17
                                        + " CURCOD,EXCRAT,AMTFCU,REMARK,MODTYP,CREABY,CREADT,BNKREC,UNOFMS,UNTQTY,JCAT01,JCAT02,JCAT03,JCAT04,JCAT05,REACOD,"//16
                                        + " REFTXT,LKRRAT,POSTDT,modiby,modidt,period) values (" //06
                                        + "'" + m_DesSbu + "',"
                                        + "'" + m_DesLoc + "',"
                                        + "'" + m_objSelect.get("BATTYP").toString() + "',"
                                        + "'" + m_strBatchNo + "',"
                                        + "'" + m_objSelect.get("DOCCOD").toString() + "',"
                                        + "'" + m_jentDesDoc + "',"//m_objSelect.get("DOCNUM").toString()
                                        + "'" + m_objSelect.get("LINNUM").toString() + "',"//m_objSelect.get("LINNUM").toString()
                                        + "'" + (seq++) + "',"
                                        + "'" + m_objSelect.get("TRANDT").toString() + "',"//m_objSelect.get("TRANDT").toString()
                                        + "'" + m_interId + "',"//m_objSelect.get("INTERID").toString()
                                        + "'INTTRA'," //" + m_objSelect.get("DIMM01").toString() + "
                                        + "'" + m_objSelect.get("DIMM02").toString() + "',"
                                        + "'" + m_objSelect.get("DIMM03").toString() + "',"
                                        + "'" + m_objSelect.get("DIMM04").toString() + "',"
                                        + "'" + m_objSelect.get("DIMM05").toString() + "',"
                                        + "'" + m_Amount + "',"
                                        + "'" + m_type + "',"//m_objSelect.get("DRCRTY").toString()
                                        + "'" + m_objSelect.get("CURCOD").toString() + "',"
                                        + "'" + m_objSelect.get("EXCRAT").toString() + "',"
                                        + "'" + m_Amount + "',"//m_objSelect.get("AMTFCU").toString()
                                        + " concat('" + m_objSelect.get("REMARK").toString() + "','" + m_strSourceNo + "'),"
                                        + "'" + m_objSelect.get("MODTYP").toString() + "',"
                                        + "'" + _cronUser + "',"
                                        + "'" + _strSysDate + "',"
                                        + "'" + m_objSelect.get("BNKREC").toString() + "',"
                                        + "'" + m_objSelect.get("UNOFMS").toString() + "',"
                                        + "'" + m_objSelect.get("UNTQTY").toString() + "',"
                                        + "'" + m_objSelect.get("JCAT01").toString() + "',"
                                        + "'" + m_objSelect.get("JCAT02").toString() + "',"
                                        + "'" + m_objSelect.get("JCAT03").toString() + "',"
                                        + "'" + m_objSelect.get("JCAT04").toString() + "',"
                                        + "'" + m_objSelect.get("JCAT05").toString() + "',"
                                        + "'" + m_objSelect.get("REACOD").toString() + "',"
                                        + "'" + m_objSelect.get("REFTXT").toString() + "',"
                                        + "'" + m_objSelect.get("LKRRAT").toString() + "',"
                                        + "'" + m_objSelect.get("POSTDT").toString() + "',"
                                        + "'',"
                                        + "'',"
                                        + "'" + m_objSelect.get("PERIOD").toString() + "'"
                                        + ")";

                                if (m_RemotStmt.executeUpdate(qryRemoteInsert) <= 0) {
                                    throw new Exception("Error occured in approval ");
                                }

                            }
                        }
                        m_strResult = "|" + prm_strDesSbu + "-" + prm_strDesLoc + "-" + prm_strbattyp + "-" + m_strBatchNo + "-" + prm_strDocCod + "-" + m_jentDesDoc;
                        //******____________________________________---------------

                        JSONObject objEml = new JSONObject();
                        if (!m_strBatchNo.trim().isEmpty()) {
                            objEml.put("orisbu", prm_strSbuCod);
                            objEml.put("oriloc", prm_strLocCod);
                            objEml.put("oridoccod", prm_strDocCod);
                            objEml.put("oridocnum", prm_strOriDocNum);//prm_strOriAccNum
                            objEml.put("oriAccNum", prm_strOriAccNum);
                            objEml.put("dessbu", prm_strDesSbu);
                            objEml.put("desloc", prm_strDesLoc);
                            objEml.put("desdocnum", m_jentDesDoc);

                            if (m_DesAcc != null && m_DesDrCrAcc != null && m_objSelect.get("FILEPT") != null
                                    && !m_objSelect.get("FILEPT").toString().trim().equalsIgnoreCase("")) {
                                if (!downloadFileFromFTPServer(m_objSelect.get("FILEPT").toString())) {
                                    _strErrorList += ",Ftp file download fail.";
//                                    throw new Exception("Error..!");
                                };
                                objEml.put("filename", ".//" + m_objSelect.get("FILEPT").toString());
                            } else {
                                objEml.put("filename", "");
                            }
                            setObjEml(objEml);
                        }
                        boolean prm_oriEmlSend = false;
                        if (rs.getRow() == 1) {
                            prm_oriEmlSend = true;
                        } else {
                            prm_oriEmlSend = false;
                        }

                        // **** EMAIL SEND OPTION *******************
                        if (!emailSend(_objEml, prm_oriEmlSend)) {
                            //throw new Exception("Error occured in email send..! ");
                        }
                        // **** END - EMAIL SEND OPTION *************

                        // END - REMOTE TRANSACTION
                        endTransactionRemote();

                        qry = "update gltrantemp "
                                + " set DIMM01='INTUPD',remark=concat(remark,'" + m_strResult + "') where "
                                + " sbucod='" + prm_strSbuCod + "' and"
                                + " loccod='" + prm_strLocCod + "' and"
                                + " doccod='" + prm_strDocCod + "' and"
                                + " docnum='" + rs.getString("DOCNUM") + "' and "
                                + " dimm01='INTCOM' and"
                                + " dimm04='" + prm_strDesSbu + "' and "
                                + " dimm05='" + prm_strDesLoc + "' and "
                                + " interid='" + prm_strOriAccID + "'";
                        if (_stmtError.executeUpdate(qry) <= 0) {
                            _strErrorList += ",GL status (Dimm01) not updated for Originating.";
                        }

                        // IF ERROR LIST NOT BLANK 
                        String m_error = "";
                        if (_strErrorList.trim().length() > 0) {
                            String m_errorArr[] = _strErrorList.trim().split(",");
                            for (int i = 0; i < m_errorArr.length; i++) {
                                m_error = m_errorArr[i];
                                if (m_error.trim().length() > 0) {
                                    JSONObject m_errObj = new JSONObject();
                                    m_errObj.put("sbucod", _sbuCod);
                                    m_errObj.put("loccod", _locCod);
                                    m_errObj.put("PROGRAM_ID", "JENTERR");
                                    m_errObj.put("DOCCOD", prm_strDocCod);
                                    m_errObj.put("DOCNUM", prm_strOriDocNum);
                                    m_errObj.put("BATCOD", prm_strbattyp);
                                    m_errObj.put("BATNUM", prm_strBatNum);
                                    m_errObj.put("ACCNUM", prm_strOriAccNum);
                                    m_errObj.put("DESSBU", prm_strDesSbu);
                                    m_errObj.put("DESLOC", prm_strDesLoc);
                                    m_errObj.put("DESACC", m_DesAcc);
                                    m_errObj.put("USERID", _cronUser);
                                    m_errObj.put("LOGDAT", _strSysDate);
                                    m_errObj.put("ERRLOG", m_error);
                                    m_errObj.put("DESDOC", prm_strDocCod + "-" + m_jentDesDoc);
                                    m_errObj.put("DESBAT", prm_strbattyp + "-" + m_strBatchNo);
                                    _errObjArr.add(m_errObj);
                                }

                            }
                            if (writeErrorLog(prm_strUserEmail)) {

                            }
                        }
                        // endTransactionRemote();
                    }
                } catch (Exception e) { // Main loop will continue  
                    abortTransactionRemote();

                    System.out.println("Error[Destination GL] : " + prm_strDesSbu + prm_strDesLoc + "_" + prm_strOriDocNum + "_" + prm_strOriAccNum);
                    e.printStackTrace();

                    // THIS FOR ROLLBACK TRANSACTIONS
                    JSONObject m_errObj = new JSONObject();
                    m_errObj.put("sbucod", _sbuCod);
                    m_errObj.put("loccod", _locCod);
                    m_errObj.put("PROGRAM_ID", "JENTERR");
                    m_errObj.put("DOCCOD", prm_strDocCod);
                    m_errObj.put("DOCNUM", prm_strOriDocNum);
                    m_errObj.put("BATCOD", prm_strbattyp);
                    m_errObj.put("BATNUM", prm_strBatNum);
                    m_errObj.put("ACCNUM", prm_strOriAccNum);
                    m_errObj.put("DESSBU", prm_strDesSbu);
                    m_errObj.put("DESLOC", prm_strDesLoc);
                    m_errObj.put("DESACC", m_DesAcc);
                    m_errObj.put("USERID", _cronUser);
                    m_errObj.put("LOGDAT", _strLocalDate);
                    m_errObj.put("ERRLOG", e);
                    m_errObj.put("DESDOC", prm_strDocCod + "-" + m_jentDesDoc);
                    m_errObj.put("DESBAT", prm_strbattyp + "-" + m_strBatchNo);
                    _errObjArr.add(m_errObj);
                    if (writeErrorLog(prm_strUserEmail)) {

                    }
                }
            }
            // END - MAIN LOOP FOR SELECT --------------------------------------

        } catch (Exception e) {
            e.printStackTrace();
            objResult.put("exception", e);
            _exception = e;
            abortTransactionRemote();
            m_strResult = null;
        } finally {
            if (m_stmt != null) {
                try {
                    m_stmt.close();
                    m_stmt = null;
                } catch (SQLException ex) {
                    ex.printStackTrace();
                }
            }
        }
        objResult.put("result", m_strResult);
        System.out.println("Process Completed...");
        return objResult;
    }

    public boolean writeErrorLog(String prm_userEmail) {
        boolean result = false;
        try {

            //Statement m_stmt = _dbConnection.getConn().createStatement();
            // WRITE TO LOG FILE
            JSONObject m_objError = new JSONObject();
            String qry = "", m_seqnum = "1";
            qry = "select max(seqnum) as seqnum from RMS_ERROR_LOG where"
                    + " sbucod='" + _sbuCod + "' and"
                    + " loccod='" + _locCod + "' and"
                    + " PROGRAM_ID='JENTERR' and "
                    + " logdat=TO_CHAR (SYSDATE, 'DD-MON-YY')";
            ResultSet rs = _stmtError.executeQuery(qry);
            if (rs.next()) {
                if (rs.getString("seqnum") != null) {
                    m_seqnum = String.valueOf(Integer.parseInt(rs.getString("seqnum")) + 1);
                }
            }

            for (int i = 0; i < _errObjArr.size(); i++) {
                m_objError = (JSONObject) _errObjArr.get(i);
                qry = "insert into RMS_ERROR_LOG (sbucod,loccod,PROGRAM_ID,seqnum,DOCCOD,DOCNUM,BATCOD,BATNUM,ACCNUM,DESSBU,DESLOC,"
                        + "DESACC,USERID,LOGDAT,ERRLOG,LOCKIN,DESDOC,DESBAT) "
                        + "values ('"
                        + _sbuCod + "','"
                        + _locCod + "','JENTERR','"
                        + m_seqnum + "','"
                        + m_objError.get("DOCCOD").toString() + "','"
                        + m_objError.get("DOCNUM").toString() + "','"
                        + m_objError.get("BATCOD").toString() + "','"
                        + m_objError.get("BATNUM").toString() + "','"
                        + (m_objError.get("ACCNUM") == null ? "" : m_objError.get("ACCNUM").toString()) + "','"
                        + (m_objError.get("DESSBU") == null ? "" : m_objError.get("DESSBU").toString()) + "','"
                        + (m_objError.get("DESLOC") == null ? "" : m_objError.get("DESLOC").toString()) + "','"
                        + (m_objError.get("DESACC") == null ? "" : m_objError.get("DESACC").toString()) + "','"
                        + _cronUser + "',TO_CHAR (SYSDATE, 'DD-MON-YY'),'"
                        + m_objError.get("ERRLOG").toString().trim().replace("'", "") + "',current_timestamp,'"
                        + (m_objError.get("DESDOC") == null ? "" : m_objError.get("DESDOC").toString()) + "','"
                        + (m_objError.get("DESBAT") == null ? "" : m_objError.get("DESBAT").toString()) + "')";
                if (_stmtError.executeUpdate(qry) <= 0) {
                    throw new Exception(m_objError.get("DOCCOD").toString() + "-" + m_objError.get("DOCNUM").toString() + "-" + (m_objError.get("ACCNUM") == null ? "" : m_objError.get("ACCNUM").toString()) + ":Log file error");
                }

            }
            if (!errorEmailSend(_errObjArr, prm_userEmail)) {
                System.out.println("Error email not send.Please refer log file reports.");
            }
            _strErrorList = "";
            _errObjArr.clear();
            result = true;
            // END - WRITE TO LOG FILE
        } catch (Exception e) {
            e.printStackTrace();

        }
        return result;
    }

    public String[] getFinPeriod(String prm_strTxnDate, boolean prm_chkAcrStatus, boolean prm_chkApyStatus,
            boolean prm_chkGlpStatus, boolean prm_chkInvStatus, Connection prm_strCon, String prm_strSBU) {
        String m_strSql = "";
//        _strTxnDate = prm_strTxnDate;
        String m_strFinPeriod[] = null;

        try {
            m_strFinPeriod = new String[3];
            m_strSql = "select prdyer,period,acrsta,apysta,glpsta,invsta from glfinperiods "
                    + " where sbucod='" + prm_strSBU + "' "
                    + " and stdate <='" + prm_strTxnDate + "' and endate >='" + prm_strTxnDate + "'";

            Statement stmnt = prm_strCon.createStatement();
            ResultSet rs = stmnt.executeQuery(m_strSql);
            if (rs.next()) {
                if (prm_chkAcrStatus) {
                    if (!rs.getString("acrsta").equalsIgnoreCase("OPN")) {
                        throw new Exception("Sorry the fiscal period for Accounts Receivable is not open.(" + prm_strTxnDate + ")");
                    }
                }

                if (prm_chkApyStatus) {
                    if (!rs.getString("apysta").equalsIgnoreCase("OPN")) {
                        throw new Exception("Sorry the fiscal period for Accounts Payable is not open.(" + prm_strTxnDate + ")");
                    }
                }

                if (prm_chkGlpStatus) {
                    if (!rs.getString("glpsta").equalsIgnoreCase("OPN")) {
                        throw new Exception("Sorry the fiscal period for General Ledger is not open.(" + prm_strTxnDate + ")");
                    }
                }
                if (prm_chkInvStatus) {
                    if (!rs.getString("invsta").equalsIgnoreCase("OPN")) {
                        throw new Exception("Sorry the fiscal period for Inventory is not open.(" + prm_strTxnDate + ")");
                    }
                }

                m_strFinPeriod[0] = "OK";
                m_strFinPeriod[1] = rs.getString("prdyer");
                m_strFinPeriod[2] = rs.getString("period");

            } else {
                m_strFinPeriod[0] = "NO";
                m_strFinPeriod[1] = "Could Not Find Fiscal Period. Please check your GL period settings.(" + prm_strTxnDate + ")";
            }

            rs.close();
            rs = null;
            stmnt.close();
            stmnt = null;
            return m_strFinPeriod;

        } catch (Exception e) {
            e.printStackTrace();
            m_strFinPeriod[0] = "NO";
            m_strFinPeriod[1] = e.getMessage();
            return m_strFinPeriod;
        }

    }

    public Exception getException() {
        return _exception;
    }

    public boolean dbChange(String prm_Sbu, String prm_Loc) {
        try {

            String m_strdbtype = "", m_strIP = "", m_strDbSchema = "", m_struserID = "", m_strpassword = "";
            ResultSet m_site = checkLocationinrmssites(prm_Sbu, prm_Loc);
            if (m_site == null) {
                throw new Exception(getException().getLocalizedMessage());
            }
            // SET DATA TO LOCAL PARAMETERS
            m_site.beforeFirst();
            if (m_site.next()) {
                m_strdbtype = m_site.getString("SITE_ID");
                m_strIP = m_site.getString("IP");
                m_strDbSchema = m_site.getString("DB");
                m_struserID = m_site.getString("USERID") == null ? "" : m_site.getString("USERID");
                m_strpassword = m_site.getString("PASSWD") == null ? "" : m_site.getString("PASSWD");
                if (m_strpassword.trim().equals("")) {
                    if (m_strdbtype.trim().equalsIgnoreCase("M")) {
                        m_strpassword = new jText.TextUti().getText("mysql");
                    } else {
                        m_strpassword = new jText.TextUti().getText("oracle");
                    }
                }
            }
            String m_dbtyp = m_strdbtype.equals("O") ? "Oracle" : "MySql";
            if (m_dbtyp.equals("MySql")) {
                if (!getMysqlConnect(m_strIP, m_strDbSchema, m_struserID, m_strpassword)) {
                    throw new Exception("Error Occured in connecting to destination db");
                }
            } else {
                if (!OracleConnector(m_strIP, m_strDbSchema, m_struserID, m_strpassword)) {
                    throw new Exception("Error Occured in connecting to destination db");
                }
            }
            return true;
        } catch (Exception e) {
            e.printStackTrace();
            _exception = e;
            return false;
        }
        //-----------
    }

    public ResultSet checkLocationinrmssites(String prm_strSbusbuCode, String prm_strLocCode) {
        Statement _stmt = null;
        ResultSet rs = null;
        try {

            String m_strSQL = "";
            // closeStatement();

            m_strSQL = " SELECT SBU_CODE, LOC_CODE, SITE_ID, SITE_NAME, IP, DB, PASSWD, USERID, ALTSBU,ITMLOC "
                    + " FROM RMS_SITES where ALTSBU ='" + prm_strSbusbuCode + "' and ITMLOC='" + prm_strLocCode + "'";
            _stmt = _dbConnection.getConn().createStatement(ResultSet.TYPE_SCROLL_INSENSITIVE, ResultSet.CONCUR_READ_ONLY);

            rs = _stmt.executeQuery(m_strSQL);
            if (!rs.next()) {
                throw new Exception("Unable to Find Sbu Code in RMS_SITES");
            }

            return rs;
        } catch (Exception e) {
            e.printStackTrace();
            _exception = e;
            return null;
        }
    }

    public boolean getMysqlConnect(String p_ip_address, String p_db_name,
            String p_db_user, String p_db_pwd) {
        try {
            Class.forName("com.mysql.jdbc.Driver").newInstance();
            String url = "jdbc:mysql://" + p_ip_address
                    + ":3306/" + p_db_name
                    + "?autoReconnect=true&user=" + p_db_user
                    + "&password=" + p_db_pwd;
            _remoteDbConnection = java.sql.DriverManager.getConnection(url);
            if (_remoteDbConnection == null) {
                throw new Exception("mysql connection failed..." + p_ip_address);
            }
            setStrDBTYpe("MySql");
            return true;
        } catch (Exception e) {
            e.printStackTrace();
            _exception = e;
            return false;
        }
    }

    public boolean OracleConnector(String p_ip_address, String p_db_name,
            String p_db_user, String p_db_pwd) {
        // Load Oracle driver
        try {
            DriverManager.registerDriver(new oracle.jdbc.driver.OracleDriver());
            Class.forName("oracle.jdbc.driver.OracleDriver");
            String url = "jdbc:oracle:thin:@" + p_ip_address
                    + ":1521:" + p_db_name;
            _remoteDbConnection = java.sql.DriverManager.getConnection(url, p_db_user, p_db_pwd);
            setStrDBTYpe("Oracle");
            return true;
        } catch (Exception e) {
            System.out.println("Could Not Connect To Oracle" + e.getMessage());
            e.printStackTrace();
            return false;
        }
    }

    public int startTransactionRemote() {
        try {
            if (_remoteDbConnection.getAutoCommit() == false) {
                _remoteDbConnection.rollback();
                _remoteDbConnection.setAutoCommit(true);
            }
            _remoteDbConnection.setAutoCommit(false);
            return 1;
        } catch (Exception e) {
            e.printStackTrace();
            return 0;
        }
    }

    public int endTransactionRemote() {
        try {
            _remoteDbConnection.commit();
            _remoteDbConnection.setAutoCommit(true);
            return 1;
        } catch (Exception e) {
            e.printStackTrace();
            return 0;
        }
    }

    public void abortTransactionRemote() {
        try {
            if (_remoteDbConnection.getAutoCommit() == false) {
                _remoteDbConnection.rollback();
                _remoteDbConnection.setAutoCommit(true);
            }

        } catch (Exception e) {
            e.printStackTrace();

        }
    }

    /**
     * @return the _strDBTYpe
     */
    public String getStrDBTYpe() {
        return _strDBTYpe;
    }

    /**
     * @param _strDBTYpe the _strDBTYpe to set
     */
    public void setStrDBTYpe(String _strDBTYpe) {
        this._strDBTYpe = _strDBTYpe;
    }

    public static String formatMonth(int prm_intMonth) {
        String m_strMonth = "";
        switch (prm_intMonth) {
            case 1:
                m_strMonth = "Jan";
                break;
            case 2:
                m_strMonth = "Feb";
                break;
            case 3:
                m_strMonth = "Mar";
                break;
            case 4:
                m_strMonth = "Apr";
                break;
            case 5:
                m_strMonth = "May";
                break;
            case 6:
                m_strMonth = "Jun";
                break;
            case 7:
                m_strMonth = "Jul";
                break;
            case 8:
                m_strMonth = "Aug";
                break;
            case 9:
                m_strMonth = "Sep";
                break;
            case 10:
                m_strMonth = "Oct";
                break;
            case 11:
                m_strMonth = "Nov";
                break;
            case 12:
                m_strMonth = "Dec";
                break;
            default:
                m_strMonth = "";
        }
        return m_strMonth;
    }

    public boolean downloadFileFromFTPServer(String prm_strFileName) {
        String server = _ftpserver;
        int port = _intport;
        String user = _user;
        String pass = _pass;
        boolean success = false;
        FTPClient ftpClient = new FTPClient();
        try {
            ftpClient.connect(server, port);
            ftpClient.login(user, pass);
            ftpClient.enterLocalPassiveMode();
            ftpClient.setFileType(FTP.BINARY_FILE_TYPE);
            String remoteFile2 = "/var/ftp/pub/marksys/InterComTxns/" + prm_strFileName;
            File downloadFile2 = new File(".//" + prm_strFileName);
            OutputStream outputStream2 = new BufferedOutputStream(new FileOutputStream(downloadFile2));
            InputStream inputStream = ftpClient.retrieveFileStream(remoteFile2);
            byte[] bytesArray = new byte[4096];
            int bytesRead = -1;
            while ((bytesRead = inputStream.read(bytesArray)) != -1) {
                outputStream2.write(bytesArray, 0, bytesRead);
            }
            success = ftpClient.completePendingCommand();
            outputStream2.close();
            inputStream.close();
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            try {
                if (ftpClient.isConnected()) {
                    ftpClient.logout();
                    ftpClient.disconnect();
                }
            } catch (IOException ex) {
                ex.printStackTrace();
            }
        }
        return success;
    }

    public boolean errorEmailSend(JSONArray prm_objArrError, String prm_email) {
        try {
            boolean prm_desEmlsend = false;
            Statement m_stmt = null, m_RemotStmt = null;
            m_stmt = _dbConnection.getConn().createStatement(ResultSet.TYPE_SCROLL_INSENSITIVE, ResultSet.CONCUR_READ_ONLY);

            String emlBody1 = "Following errors has found in Inter-company transactions process,<br><br>";
            String emlBody2 = "";
            String emlBody3 = "<table style='text-align: center;central;font-size: 13px;'>"
                    + "<tr>"
                    + "     <td>SBU</td>"
                    + "     <td>LOCATION</td>"
                    + "     <td>DOCUMENT</td>"
                    + "     <td>DOC NO</td>"
                    + "     <td>A/C NO</td>"
                    + "     <td>ERROR</td>"
                    + " </tr>";

            for (int i = 0; i < prm_objArrError.size(); i++) {
                JSONObject m_objError = (JSONObject) prm_objArrError.get(i);

                emlBody3 += " <tr>"
                        + "     <td>" + m_objError.get("DESSBU").toString() + "</td>"
                        + "     <td>" + m_objError.get("DESLOC").toString() + "</td>"
                        + "     <td>" + m_objError.get("DOCCOD").toString() + "</td>"
                        + "     <td>" + m_objError.get("DOCNUM").toString() + "</td>"
                        + "     <td>" + m_objError.get("ACCNUM").toString() + "</td>"
                        + "     <td>" + m_objError.get("ERRLOG").toString() + "</td>"
                        + " </tr>";
            }
            emlBody3 += "</table>";
            if (!sendEmailForInterCompTxns("", "",
                    emlBody1, emlBody2, emlBody3, "", prm_email, "", "ERROR:Journal Entry - Inter Company Transaction")) {
            }

//            (!sendEmailForInterCompTxns(prm_ObjEmailPara.get("oridoccod").toString(), prm_ObjEmailPara.get("oridocnum").toString(),
//                        emlBody1, emlBody2, emlBody3, "", oriEmlList, "", ""))
            emlBody1 = "";
            emlBody2 = "";
            emlBody3 = "";
            return true;
        } catch (Exception e) {
            e.printStackTrace();
            _exception = e;
            return false;
        }
    }

    public boolean emailSend(JSONObject prm_ObjEmailPara, boolean prm_oriEmlSend) {
        try {
            boolean prm_desEmlsend = false;
            Statement m_stmt = null, m_RemotStmt = null;
            m_stmt = _dbConnection.getConn().createStatement(ResultSet.TYPE_SCROLL_INSENSITIVE, ResultSet.CONCUR_READ_ONLY);
            m_RemotStmt = _remoteDbConnection.createStatement(ResultSet.TYPE_SCROLL_INSENSITIVE, ResultSet.CONCUR_READ_ONLY);

            // ORIGINATING COMPANY
            String orginatingSql = "select  d.sbu_name,b.ALACID,b.descri,a.DOCCOD,a.DOCNUM,a.TRANDT,a.AMOUNT,a.remark,c.orieml,c.deseml,c.dessbu,"
                    + "c.desloc,u.email"
                    + " from gltrantempapp a inner join glcharofaccs b on  a.sbucod=b.sbucod and a.LOCCOD=b.LOCCOD and a.interid=b.interid "
                    + " left join glintcomtrans c on a.sbucod=c.ORISBU AND b.ALACID=c.ORIACC and a.dimm04=c.DESSBU and a.dimm05=c.DESLOC"
                    + " left join rms_sbu d  on a.sbucod=d.sbu_code"
                    + " left join rms_users u on a.sbucod=u.sbu_code and a.loccod=u.loc_code and trim(a.creaby)=u.user_id"
                    + " where  a.sbucod='" + _sbuCod + "' and"
                    + " a.loccod='" + _locCod + "' and"
                    + " a.doccod='" + prm_ObjEmailPara.get("oridoccod").toString() + "' AND"
                    + " a.DOCNUM in (" + prm_ObjEmailPara.get("oridocnum").toString() + ")";
            // + " c.ORISBU is not null";

            ResultSet originationRs = m_stmt.executeQuery(orginatingSql);
            String emlBody1 = "I have created Inter-company transactions. It's for your Approval,<br><br>";
            String emlBody2 = "";
            String emlBody3 = "<table style='text-align: center;central;font-size: 13px;'>"
                    + "<tr>"
                    + "     <td>ACC NO</td>"
                    + "     <td>DESCRIPTION</td>"
                    + "     <td>DOC CODE</td>"
                    + "     <td>DOC NO</td>"
                    + "     <td>DATE</td>"
                    + "     <td>AMOUNT</td>"
                    + "     <td>REMARK</td>"
                    + " </tr>";

            if (originationRs.next()) {
                emlBody2 = originationRs.getString("sbu_name");
                originationRs.beforeFirst();
            }
            String oriEmlList = "", remoEmlList = "";
            while (originationRs.next()) {
                emlBody3 += " <tr>"
                        + "     <td>" + originationRs.getString("ALACID") + "</td>"
                        + "     <td>" + originationRs.getString("descri") + "</td>"
                        + "     <td>" + originationRs.getString("DOCCOD") + "</td>"
                        + "     <td>" + originationRs.getString("DOCNUM") + "</td>"
                        + "     <td>" + originationRs.getString("TRANDT") + "</td>";

                if (new BigDecimal(originationRs.getString("AMOUNT")).compareTo(BigDecimal.ZERO) > 0) {
                    emlBody3 += "<td>(" + originationRs.getString("AMOUNT") + ")</td>";
                } else {
                    emlBody3 += "<td>" + originationRs.getString("AMOUNT") + "</td>";
                }
                emlBody3 += "<td>" + originationRs.getString("AMOUNT") + "</td>"
                        + "<td>" + originationRs.getString("remark") + "</td>"
                        + " </tr>";
                // ORGINATING EMAIL LIST GENARATE
                if (originationRs.getClob("orieml") != null) {
                    oriEmlList += (oriEmlList.length() != 0 ? "," : "") + originationRs.getClob("orieml").getSubString(1, (int) originationRs.getClob("orieml").length());
                }

                // REMOTE EMAIL LIST GENARATE
                String prm_desSbu = originationRs.getString("dessbu");
                String prm_desLoc = originationRs.getString("desloc");//oridocnum
                String prm_oriAcc = originationRs.getString("ALACID");

                if (prm_ObjEmailPara.get("dessbu").toString().equalsIgnoreCase(prm_desSbu)
                        && prm_ObjEmailPara.get("desloc").toString().equalsIgnoreCase(prm_desLoc)
                        && prm_ObjEmailPara.get("oriAccNum").toString().equalsIgnoreCase(prm_oriAcc)) {
                    prm_desEmlsend = true;
                }
//                if (originationRs.getClob("deseml") != null && prm_desEmlsend) {
//                    remoEmlList += (remoEmlList.length() != 0 ? "," : "") + originationRs.getClob("deseml").getSubString(1, (int) originationRs.getClob("deseml").length());
//
//                    if (originationRs.getString("email") == null) {
//                        throw new Exception("Journal entry creater email address not found..!");
//                    }
//                    remoEmlList = remoEmlList + "," + originationRs.getString("email");
//                    prm_desEmlsend = false;
//                }

                if (originationRs.getClob("deseml") != null && prm_desEmlsend) {
                    remoEmlList += (remoEmlList.length() != 0 ? "," : "") + originationRs.getClob("deseml").getSubString(1, (int) originationRs.getClob("deseml").length());

                    if (originationRs.getString("email") == null) {
                        _strErrorList += ",Error[Entry Creater Email Address]:SBU[" + _sbuCod + "]-Loc[" + _locCod + "] Email not Found for Creater[" + originationRs.getString("creaby") + "]";
                    } else {
                        remoEmlList = remoEmlList + "," + originationRs.getString("email");
                        prm_desEmlsend = false;
                    }
                }

            }
            emlBody3 += "</table>";
//            utility.SmSendMail email = new utility.SmSendMail(_audit);
            // GENARATE ORIGINATING MAIL, WHEN FIRST ROUND ONLY.
            if (prm_oriEmlSend) {
//                email.sendEmailForDocument_ModifyedMailBody(prm_ObjEmailPara.get("oridoccod").toString(), prm_ObjEmailPara.get("oridocnum").toString(), emlBody1, emlBody2, emlBody3, "", oriEmlList, "");
                if (!sendEmailForInterCompTxns(prm_ObjEmailPara.get("oridoccod").toString(), prm_ObjEmailPara.get("oridocnum").toString(),
                        emlBody1, emlBody2, emlBody3, "", oriEmlList, "", "")) {
                    //throw new Exception("Error occured in email send..! ");
                }
            }
            // REMOTE COMPANY
            String remoteSql = "select  d.sbu_name,b.alacid,b.descri,a.DOCCOD,a.DOCNUM,a.TRANDT,a.AMOUNT,a.remark"
                    + " from gltrantempapp a inner join glcharofaccs b on  a.sbucod=b.sbucod and a.LOCCOD=b.LOCCOD and a.interid=b.interid "
                    // + " left join glintcomtrans c on a.sbucod=c.ORISBU AND b.ALACID=c.ORIACC and a.dimm04=c.DESSBU and a.dimm05=c.DESLOC"
                    + " left join rms_sbu d  on a.sbucod=d.sbu_code"
                    + " where  a.sbucod='" + prm_ObjEmailPara.get("dessbu") + "' and"
                    + " a.loccod='" + prm_ObjEmailPara.get("desloc") + "' and"
                    + " a.doccod='" + prm_ObjEmailPara.get("oridoccod") + "' AND"
                    + " a.DOCNUM='" + prm_ObjEmailPara.get("desdocnum") + "'";

            ResultSet remoteRs = m_RemotStmt.executeQuery(remoteSql);
            emlBody1 = "I have created Inter-company transactions. It's for your Approval,<br>";
            emlBody2 = "";
            emlBody3 = "<table style='text-align: center;central;font-size: 13px;'>"
                    + "<tr>"
                    + "     <td style='width: 100px;'>ACC NO</td>"
                    + "     <td style='width: 100px;'>DESCRIPTION</td>"
                    + "     <td style='width: 100px;'>DOC CODE</td>"
                    + "     <td style='width: 100px;'>DOC NO</td>"
                    + "     <td style='width: 100px;'>DATE</td>"
                    + "     <td  style='width: 100px;'>AMOUNT</td>"
                    + "     <td style='width: 250px;'>REMARK</td>"
                    + " </tr>";
            //+ "                </table>";
            if (remoteRs.next()) {
                emlBody2 = "<p>" + remoteRs.getString("sbu_name") + "</p>";
                remoteRs.beforeFirst();
            }
            oriEmlList = "";
            while (remoteRs.next()) {
                emlBody3 += " <tr>"
                        + "     <td>" + remoteRs.getString("alacid") + "</td>"
                        + "     <td>" + remoteRs.getString("descri") + "</td>"
                        + "     <td>" + remoteRs.getString("DOCCOD") + "</td>"
                        + "     <td>" + remoteRs.getString("DOCNUM") + "</td>"
                        + "     <td>" + remoteRs.getString("TRANDT") + "</td>";
//                if (Integer.parseInt(remoteRs.getString("AMOUNT")) < 0) {
                if (new BigDecimal(remoteRs.getString("AMOUNT")).compareTo(BigDecimal.ZERO) > 0) {
                    emlBody3 += "<td>(" + remoteRs.getString("AMOUNT") + ")</td>";
                } else {
                    emlBody3 += "<td>" + remoteRs.getString("AMOUNT") + "</td>";
                }
                emlBody3 += "<td>(" + remoteRs.getString("AMOUNT") + ")</td>"
                        + "     <td>" + remoteRs.getString("remark") + "</td>"
                        + " </tr>";
            }
            emlBody3 += "</table>";
//            email.sendEmailForDocument_ModifyedMailBody(prm_ObjEmailPara.get("oridoccod").toString(), prm_ObjEmailPara.get("oridocnum").toString(), emlBody1, emlBody2, emlBody3, "", remoEmlList, prm_ObjEmailPara.get("filename").toString());
            if (!sendEmailForInterCompTxns(prm_ObjEmailPara.get("oridoccod").toString(), prm_ObjEmailPara.get("oridocnum").toString(),
                    emlBody1, emlBody2, emlBody3, "", remoEmlList, prm_ObjEmailPara.get("filename").toString(), "")) {
                // throw new Exception("Error occured in email send..! ");
            }

            emlBody1 = "";
            emlBody2 = "";
            emlBody3 = "";
            return true;
        } catch (Exception e) {
            e.printStackTrace();
            _exception = e;
            return false;
        }
    }

    public boolean sendEmailForInterCompTxns(String prm_strDocCode, String prm_strDocNo, String prm_embody1, String prm_embody2, String prm_embody3, String prm_embody4, String prm_emailList, String prm_filepath, String prm_subject) {
        boolean result = false;
        try {
            String m_subject = "";
            if (prm_subject.trim().length() > 0) {
                m_subject = prm_subject;
            } else {
                m_subject = "Journal Entry - Inter Company Transaction";
            }

            if (!prm_emailList.equals("")) {
                //email address to be included

                String m_emailArr[] = prm_emailList.split(",");
                for (int i = 0; i < m_emailArr.length; i++) {
                    String m_emailAdd = m_emailArr[i];
                    JSONObject m_objReturn = null;

                    if (m_emailAdd.equalsIgnoreCase(_strOriginatorEmail)) {
                        m_objReturn = CreateMailwithReturn(m_emailAdd, _strOriginatorEmail, "",
                                m_subject, prm_filepath, "", "", prm_embody1, prm_embody2, prm_embody3, prm_embody4);
                    } else {
                        m_objReturn = CreateMailwithReturn(m_emailAdd, _strOriginatorEmail, _strOriginatorEmail,
                                m_subject, prm_filepath, "", "", prm_embody1, prm_embody2, prm_embody3, prm_embody4);
                    }
                    if (!m_objReturn.get("result").toString().equalsIgnoreCase("success")) {
                        _strErrorList += "," + _exception.getCause().getLocalizedMessage();
                    }

                }
            }
            result = true;
        } catch (Exception e) {
            e.printStackTrace();
            _exception = e;
        }
        return result;
    }

    public boolean CreateMail(String strtoAddress, String prm_strFromAddress, String prm_strCCAddress, String prm_strHeader, String strFilename1, String strFilename2, String strFilename3,
            String prm_strboodyText1, String prm_strboodyText2, String prm_strbodytext3, String prm_strbodytext4) throws Exception {
        boolean result = false;
        try {
            String from = prm_strFromAddress;
            String[] to = strtoAddress.split(",");
            String[] cc = prm_strCCAddress.split(",");
            String fileAttachment1 = strFilename1;
            String fileAttachment2 = strFilename2;
            String fileAttachment3 = strFilename3;
            String bodytext1 = prm_strboodyText1;
            String bodytext2 = prm_strboodyText2;
            String bodytext3 = prm_strbodytext3;//"This is System Generated email.For any queries please contact the sender of this mail.<br><br>Regards,<br><br>";
            String bodytext4 = prm_strbodytext4;//_audit.getStrUserName();
            Properties props = System.getProperties();
            props.put("mail.transport.protocol", "smtp");
            props.put("mail.smtp.host", "192.168.1.19");
            props.put("mail.smtp.localhost", _strIpAddress);

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
            message.setSubject(prm_strHeader);
            MimeBodyPart messageBodyPart1 = null;
            MimeBodyPart messageBodyPart2 = null;
            MimeBodyPart messageBodyPart3 = null;
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
            message.setContent(multipart);
            Transport.send(message);

            result = true;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return result;
    }

    public JSONObject CreateMailwithReturn(String strtoAddress, String prm_strFromAddress, String prm_strCCAddress, String prm_strHeader, String strFilename1, String strFilename2, String strFilename3,
            String prm_strboodyText1, String prm_strboodyText2, String prm_strbodytext3, String prm_strbodytext4) throws Exception {
        JSONObject result = new JSONObject();
        try {
            String from = prm_strFromAddress;
            String[] to = strtoAddress.split(",");
            String[] cc = prm_strCCAddress.split(",");
            String fileAttachment1 = strFilename1;
            String fileAttachment2 = strFilename2;
            String fileAttachment3 = strFilename3;
            String bodytext1 = prm_strboodyText1;
            String bodytext2 = prm_strboodyText2;
            String bodytext3 = prm_strbodytext3;//"This is System Generated email.For any queries please contact the sender of this mail.<br><br>Regards,<br><br>";
            String bodytext4 = prm_strbodytext4;//_audit.getStrUserName();
            Properties props = System.getProperties();
            props.put("mail.transport.protocol", "smtp");
            props.put("mail.smtp.host", "192.168.1.19");
            props.put("mail.smtp.localhost", _strIpAddress);

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
            message.setSubject(prm_strHeader);
            MimeBodyPart messageBodyPart1 = null;
            MimeBodyPart messageBodyPart2 = null;
            MimeBodyPart messageBodyPart3 = null;
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
            message.setContent(multipart);
            Transport.send(message);
            result.put("result", "success");
        } catch (MessagingException e) {
            _exception = e;
            e.printStackTrace();
            result.put("result", "failed");
            result.put("error", e);
        }
        return result;
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
    //***********************************
    JSONObject _objEml = new JSONObject();

    public JSONObject getObjEml() {
        return _objEml;
    }

    public void setObjEml(JSONObject _objEml) {
        this._objEml = _objEml;
    }
}
