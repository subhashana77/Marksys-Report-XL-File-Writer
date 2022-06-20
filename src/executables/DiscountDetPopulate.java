/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package executables;

import dataaccess.OracleConnector;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.Statement;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import org.json.simple.JSONObject;

/**
 *
 * @author eranda.ak
 */
public class DiscountDetPopulate {

    private OracleConnector _oraDbConnection = null;
    private Connection _remoteDbConnection = null;
    private Exception _exception = null;
    private String _strDBTYpe;
    //--------------------------------------------------------------------------
    // **** GETTERS/SETTERS **** //
    //--------------------------------------------------------------------------

    public Exception getException() {
        return _exception;
    }

    public void setException(Exception _exception) {
        this._exception = _exception;
    }

    public String getStrDBTYpe() {
        return _strDBTYpe;
    }

    public void setStrDBTYpe(String _strDBTYpe) {
        this._strDBTYpe = _strDBTYpe;
    }

    //--------------------------------------------------------------------------
    // **** END OF GETTERS/SETTERS **** //
    //--------------------------------------------------------------------------
    //--------------------------------------------------------------------------
    // **** BUSINESS LOGIC **** // *********************************************
    //--------------------------------------------------------------------------
    public void ImportDiscountsCall() {
        ImportDiscountsFromLocation(null);
    }

    public void ImportDiscountsCall(String prm_strBackDate) {
        ImportDiscountsFromLocation(prm_strBackDate);
    }

    public JSONObject ImportDiscountsFromLocation(String prm_strBackDate) {
        JSONObject m_objResult = new JSONObject();
        JSONObject m_objLocMeta = new JSONObject();
        Statement m_oraSiteStmt = null;
        Statement m_oraInsertStmt = null;
        Statement m_remoteStmt = null;
        try {
            _oraDbConnection = new OracleConnector("192.168.1.27", "RPD2", "MARKSYS", new jText.TextUti().getText("oracle"));
            // _oraDbConnection = new OracleConnector("192.168.1.27", "RPD2", "TMARKSYS", "TMARKSYS");
            m_oraSiteStmt = _oraDbConnection.getConn().createStatement(ResultSet.TYPE_SCROLL_INSENSITIVE, ResultSet.CONCUR_READ_ONLY);
            m_oraInsertStmt = _oraDbConnection.getConn().createStatement(ResultSet.TYPE_SCROLL_INSENSITIVE, ResultSet.CONCUR_READ_ONLY);
            String m_mysqlQry = "", m_oraQy = "";

            String m_oraLocList = "select * from RMS_SITES where sbu_code in ('830') ";
            ResultSet rs = m_oraSiteStmt.executeQuery(m_oraLocList);
            while (rs.next()) {
                try {
                    m_objLocMeta.put("SBU_CODE", rs.getString("SBU_CODE"));
                    m_objLocMeta.put("LOC_CODE", rs.getString("LOC_CODE"));
                    m_objLocMeta.put("SITE_ID", rs.getString("SITE_ID"));
                    m_objLocMeta.put("SITE_NAME", rs.getString("SITE_NAME"));
                    m_objLocMeta.put("IP", rs.getString("IP"));
                    m_objLocMeta.put("DB", rs.getString("DB"));
                    m_objLocMeta.put("USERID", rs.getString("USERID"));
                    m_objLocMeta.put("PASSWD", rs.getString("PASSWD"));

                    // CHANGE DB CONNECTION
                    if (dbChange(m_objLocMeta)) {
                        //------------------------------------------------------
                        String m_disBackDate = " date_format(curdate()-2,'%Y-%m-%d') ";
                        if (prm_strBackDate != null && prm_strBackDate.trim().length() > 0) {
                            m_disBackDate = prm_strBackDate.trim();
                            DateFormat oraFormat = new SimpleDateFormat("dd-MMM-yyyy");
                            DateFormat mySqlFormat = new SimpleDateFormat("yyyy-MM-dd");
                            Date date = null;
                            String formatedDate = null;
                            date = oraFormat.parse(m_disBackDate);
                            formatedDate = mySqlFormat.format(date);
                            m_disBackDate = "'" + formatedDate + "'";
                        }

                        m_remoteStmt = _remoteDbConnection.createStatement(ResultSet.TYPE_SCROLL_INSENSITIVE, ResultSet.CONCUR_READ_ONLY);
                        ResultSet m_mysqlRs = null;

                        m_mysqlQry = "select"
                                + " sbu_code,loc_code,sale_type,txn_date,itm_group,plu_code,item_code,sales_qty,"
                                + " disc_amt,before_disc_value,dis_pert,tax_amt"
                                + " from"
                                + " (select"
                                + " a.sbu_code,a.loc_code,a.sale_type,a.txn_date,c.itm_group,b.plu_code,c.item_code,"
                                + " sum(b.qty) sales_qty,sum(b.disc_amt*100/(100+t.tax_rate)) disc_amt,"
                                + " sum(b.qty*b.price)"
                                + " before_disc_value,"
                                + " case when sum(qty*-1*b.price*100/(100+t.tax_rate))*100= 0 then 0 else"
                                + " round(sum(b.disc_per*qty*-1*price/(100+t.tax_rate))/round(sum(qty*-1*b.price*100/(100+t.tax_rate))*100,2),2) end as dis_pert,"
                                //                                + " round(sum(b.disc_amt*100/(100+t.tax_rate))/sum(qty*b.price"
                                //                                + " *100/(100+t.tax_rate))*100,2) as dis_pert,"
                                + " sum((b.qty*b.price - disc_amt)*t.tax_rate/(100+t.tax_rate)) as tax_amt"
                                + " from rms_pos_txn_mas a"
                                + " inner join rms_pos_txn_det b on a.sbu_code=b.sbu_code and"
                                + " a.loc_code=b.loc_code"
                                + " and a.mach_code=b.mach_code and a.txn_date=b.txn_date and"
                                + " a.user_id=b.user_id and a.receiptno=b.receiptno"
                                + " inner join rms_itmmaster c on b.sbu_code=c.sbu_code and"
                                + " b.item_code=c.item_code"
                                + " inner join rms_tax_rates t on t.sbu_code='830' and t.tax_code=c.tax_code"
                                + " where a.sbu_code='830' and a.loc_code=" + rs.getString("LOC_CODE") + " and"
                                + "  a.txn_date = " + m_disBackDate + " and"
                                + " a.inv_status='VALID' and b.inv_status='VALID' and b.disc_amt <> 0 and b.ref_remark<>'FRPROMOITEM'  "
                                + " group by a.sbu_code,a.loc_code,a.sale_type,a.txn_date,c.itm_group,b.plu_code,c.item_code"
                                + " union all "
                                + " select a.sbu_code,a.loc_code,'RE' as"
                                + " sale_type,a.txn_date,c.itm_group,b.plu_code,c.item_code,sum(b.qty*-1)"
                                + " sales_qty,sum(b.qty*b.price*b.disc_amt*-1/(100+t.tax_rate)) disc_amt,"
                                + " sum(qty*-1*b.price*100/(100+t.tax_rate)) before_disc_value,"
                                //                                + " sum(qty*-1*b.price*100/(100+t.tax_rate))"
                                //                                + " before_disc_value,round(sum(b.disc_amt*qty*b.price*-1/(100+t.tax_rate))/sum("
                                //                                + " qty*-1*b.price*100/(100+t.tax_rate))*100,2) as dis_pert, "                              
                                + " case when sum(qty*-1*b.price*100/(100+t.tax_rate))*100= 0 then 0 else"
                                + " round(sum(b.disc_amt*qty*b.price*-1/(100+t.tax_rate))/sum("
                                + " qty*-1*b.price*100/(100+t.tax_rate))*100,2) end as dis_pert,"
                                + " sum((qty*-1*(b.price - disc_amt*.01*b.price)*t.tax_rate)/(100+t.tax_rate)) as tax_amt"
                                + " from rms_pos_ref_mas a"
                                + " inner join rms_pos_ref_det b on a.sbu_code=b.sbu_code and"
                                + " a.loc_code=b.loc_code"
                                + " and a.mach_code=b.mach_code and a.txn_date=b.txn_date and"
                                + " a.user_id=b.user_id and a.receiptno=b.refundno"
                                + " inner join rms_itmmaster c on b.sbu_code=c.sbu_code and"
                                + " b.item_code=c.item_code"
                                + " inner join rms_tax_rates t on t.sbu_code='830' and t.tax_code=c.tax_code"
                                + " where a.sbu_code='830' and a.loc_code=" + rs.getString("LOC_CODE") + " and"
                                + " a.txn_date =" + m_disBackDate + " and"
                                + "   b.ref_status='VALID' and a.inv_status='VALID' "//2017-11-24
                                + " and b.disc_amt <> 0 "
                                + " group by a.sbu_code,a.loc_code,a.txn_date,c.itm_group,b.plu_code,c.item_code"
                                + " union all"
                                + " select a.sbu_code,a.loc_code,max('CREDIT') as"
                                + " sale_type,a.txn_date,c.itm_group,b.plu_code,c.item_code,sum(b.qty*-1)"
                                + " sales_qty,sum(b.disrate*b.qty*-1*price/(100+t.tax_rate)) disc_amt,"
                                + " sum(qty*-1*b.price*100/(100+t.tax_rate))"
                                + " before_disc_value,"
                                //                                + " round(sum(b.disrate*qty*-1*price/(100+t.tax_rate))/sum(qty"
                                //                                + " *-1*b.price*100/(100+t.tax_rate))*100,2) as"
                                //                                + " dis_pert,"
                                + " case when sum(qty*-1*b.price*100/(100+t.tax_rate))*100=0 then 0 else"
                                + " round(sum(b.disrate*qty*-1*price/(100+t.tax_rate))/sum(qty*-1*b.price*100/(100+t.tax_rate))*100,2) end as dis_pert,"
                                + " sum((b.price-b.price*disrate*.01) / (100+t.tax_rate) * t.tax_rate *"
                                + " if(a.doc_code='CRDN',qty ,qty*-1)) as tax_amt"
                                + " from marksys.rms_doc_txnm a inner join rms_doc_txnd b on"
                                + " a.sbu_Code=b.sbu_Code and a.loc_code=b.loc_Code and a.doc_code=b.doc_code"
                                + " and a.doc_no=b.doc_no"
                                + " inner join marksys.rms_itmmaster c on c.sbu_code=b.sbu_code and"
                                + " c.item_code=b.item_code"
                                + " left outer join rms_tax_rates t on t.sbu_code='830' and"
                                + " t.tax_code=b.tax_code"
                                + " where a.sbu_code='830' and b.loc_code=" + rs.getString("LOC_CODE") + " and"
                                + " a.doc_code in"
                                + " ('NVAT','AINV','INVI','CRDN','INVS','INVR') and"
                                + " a.txn_date = " + m_disBackDate + " and b.disrate<>0"
                                + " group by a.sbu_code,a.loc_code,a.txn_date,c.itm_group,b.plu_code,c.item_code"
                                + " ) x";
                        m_mysqlRs = m_remoteStmt.executeQuery(m_mysqlQry);

                        while (m_mysqlRs.next()) {
                            m_oraQy = "insert into  rms_loc_disc_txns ("
                                    + "sbu_code,loc_code,sale_type,txn_date,itm_group,plu_code,item_code,"
                                    + "sales_qty,disc_amt,before_disc_value,dis_pert,tax_amt)"
                                    + " values ('"
                                    + m_mysqlRs.getString("sbu_code") + "','"
                                    + m_mysqlRs.getString("loc_code") + "','"
                                    + m_mysqlRs.getString("sale_type") + "',to_date('"
                                    + m_mysqlRs.getString("txn_date") + "','yyyy-mm-dd'),'"
                                    + m_mysqlRs.getString("itm_group") + "','"
                                    + m_mysqlRs.getString("plu_code") + "','"
                                    + m_mysqlRs.getString("item_code") + "','"
                                    + m_mysqlRs.getString("sales_qty") + "','"
                                    + m_mysqlRs.getString("disc_amt") + "','"
                                    + (m_mysqlRs.getString("before_disc_value") == null ? "0" : m_mysqlRs.getString("before_disc_value")) + "','"
                                    + (m_mysqlRs.getString("dis_pert") == null ? "0" : m_mysqlRs.getString("dis_pert")) + "','"
                                    + (m_mysqlRs.getString("tax_amt") == null ? "0" : m_mysqlRs.getString("tax_amt")) + "')";

                            try {

                                m_oraInsertStmt.executeUpdate(m_oraQy);
                                _oraDbConnection.getConn().commit();
                            } catch (Exception e) {
                                e.printStackTrace();
                                System.out.println(m_oraQy);
                            }
                        }
                        //----------------------------------------------------------
                    } else {
                        System.out.println("Connection error. IP : " + rs.getString("IP"));
                    }
                    System.out.println(rs.getString("IP") + ": Completed.");
                } catch (Exception e) {
                    e.printStackTrace();
                    _exception = e;
                }
            }

            m_objResult.put("result", "success");
            m_objResult.put("info", "Process Completed...!");
        } catch (Exception e) {
            e.printStackTrace();
            m_objResult.put("result", "failed");
            m_objResult.put("info", "Process not completed." + e);
            _exception = e;

        } finally {
            System.out.println("Process completed.");
            try {
                if (_oraDbConnection.getConn() != null) {
                    _oraDbConnection.getConn().close();
                }
                if (_remoteDbConnection != null) {
                    _remoteDbConnection.close();
                }
                if (m_oraInsertStmt != null) {
                    m_oraInsertStmt.close();
                    m_oraInsertStmt = null;
                }
                if (m_oraSiteStmt != null) {
                    m_oraSiteStmt.close();
                    m_oraSiteStmt = null;
                }
                if (m_remoteStmt != null) {
                    m_remoteStmt.close();
                    m_remoteStmt = null;
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        return m_objResult;
    }

    //--------------------------------------------------------------------------
    // **** END OF BUSINESS LOGIC **** // **************************************
    //-------------------------------------------------------------------------- 
    //--------------------------------------------------------------------------
    // **** DB CONNCTION CHANGE FROM LOCATION TO LOCATION **** //
    //--------------------------------------------------------------------------
    public boolean dbChange(JSONObject prm_objLocMeta) {
        boolean m_result = false;
        String m_strIP = "";
        try {

            String m_strdbtype = "", m_strDbSchema = "", m_struserID = "", m_strpassword = "";
            JSONObject m_site = prm_objLocMeta;
            if (m_site == null) {
                throw new Exception(getException().getLocalizedMessage());
            }

            // SET DATA TO LOCAL PARAMETERS      
            m_strIP = m_site.get("IP").toString();
            m_strdbtype = m_site.get("SITE_ID").toString();
            m_strDbSchema = m_site.get("DB").toString();
            m_struserID = m_site.get("USERID") == null ? "" : m_site.get("USERID").toString();
            m_strpassword = m_site.get("PASSWD") == null ? "" : m_site.get("PASSWD").toString();
            //if (m_strpassword.trim().equals("")) {
            if (m_strdbtype.trim().equalsIgnoreCase("O")) {
                m_strpassword = new jText.TextUti().getText("oracle");
            } else {
                m_strpassword = new jText.TextUti().getText("mysql");
            }
            // }

            String m_dbtyp = m_strdbtype.equals("O") ? "Oracle" : "MySql";
            if (m_dbtyp.equals("MySql")) {
                if (!getMysqlConnect(m_strIP, m_strDbSchema, m_struserID, m_strpassword)) {
                    throw new Exception("Error Occured in connecting to destination db. IP : " + m_strIP);
                } else {
                    m_result = true;
                }
            } else {
                if (!OracleConnector(m_strIP, m_strDbSchema, m_struserID, m_strpassword)) {
                    throw new Exception("Error Occured in connecting to destination db. IP : " + m_strIP);
                } else {
                    m_result = true;
                }
            }
        } catch (Exception e) {
            m_result = false;
            System.out.println("Error IP:" + m_strIP);
            e.printStackTrace();
            _exception = e;
        }
        //-----------
        return m_result;
    }

    public ResultSet getLocationConectivityData(String prm_strSbusbuCode, String prm_strLocCode) {
        Statement _stmt = null;
        ResultSet rs = null;
        try {
            String m_strSQL = "";

            m_strSQL = " SELECT SBU_CODE, LOC_CODE, SITE_ID, SITE_NAME, IP, DB, PASSWD, USERID, ALTSBU,ITMLOC "
                    + " FROM RMS_SITES where ALTSBU ='" + prm_strSbusbuCode + "' and ITMLOC='" + prm_strLocCode + "'";
            _stmt = _oraDbConnection.getConn().createStatement(ResultSet.TYPE_SCROLL_INSENSITIVE, ResultSet.CONCUR_READ_ONLY);

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
    //--------------------------------------------------------------------------
    // **** END OF DB CONNCTION CHANGE **** //
    //--------------------------------------------------------------------------

    //--------------------------------------------------------------------------
    // **** CONNECTION HANDLING **** //
    //--------------------------------------------------------------------------
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
    //--------------------------------------------------------------------------
    // **** END OF CONNECTION HANDLING **** //
    //--------------------------------------------------------------------------

}
