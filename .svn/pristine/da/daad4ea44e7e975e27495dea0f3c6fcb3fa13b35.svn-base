/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package executables;

import dataaccess.MysqlConnector;
import dataaccess.OracleConnector;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.Statement;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import org.json.simple.JSONObject;

/**
 *
 * @author eranda.ak
 */
public class EmployeeDownload {

    // ** VARIABLE ** //
    private OracleConnector _oraConnector;
    private MysqlConnector _mysqlConnector;
    private Exception _exception;
    private Statement _oraStat, _mysqlStat;
    private Connection _oraCon, _mysqlCon;
    private ResultSet _rsOra, _rsMysql;
    //------------------------------------

    public JSONObject downloadEmployeeFromPayroll() {
        JSONObject result = new JSONObject();
        try {

            _oraConnector = new OracleConnector("192.168.1.27", "RPD2", "MARKSYS", new jText.TextUti().getText("oracle"));
            // _oraConnector = new OracleConnector("192.168.1.27", "RPD2", "TMARKSYS", "TMARKSYS"); // Oracle connector
            _mysqlConnector = new MysqlConnector("192.168.1.52", "payroll_export", "payroll_user", new jText.TextUti().getText("mysql")); // Mysql Connector
            _oraCon = _oraConnector.getConn(); // Oracle Connection
            _mysqlCon = _mysqlConnector.getConn(); // Mysql Connection
            _oraStat = _oraCon.createStatement(ResultSet.TYPE_SCROLL_INSENSITIVE, ResultSet.CONCUR_READ_ONLY); // Oracle Statement
            _mysqlStat = _mysqlCon.createStatement(ResultSet.TYPE_SCROLL_INSENSITIVE, ResultSet.CONCUR_READ_ONLY); // Mysql Statement

            DateFormat oraFormat = new SimpleDateFormat("dd-MMM-yyyy");
            DateFormat mysqlFormat = new SimpleDateFormat("yyyy-MM-dd");
            // SELECT ACTIVE EMPLOYEES FROM PAYROLL DB
            String m_PayrollEmpList = "select"
                    + " SBUCOD, LOCCOD, DEPCOD, EMP_NO, ETITLE, INITAL, FIRSTN,"
                    + " MIDDLN, LAST_N, ADDRS1, ADDRS2, ADDRS3, EM_DOB, NIC_NO,"
                    + " GENDER, DOJOIN, SBU_NAME from pyemp_master a left join rms_sbu b"
                    + " on a.sbucod=b.SBU_CODE"
                    + " where STATUS in ('1','2')";

            _rsMysql = _mysqlStat.executeQuery(m_PayrollEmpList);

            String m_oraSql = "";
            // DELETE EXISTING EMPLOYEE LIST OF ORACLE
//            m_oraSql = "delete from rms_pyemp_master ";
//            _oraStat.executeUpdate(m_oraSql);

//            if (_oraStat.executeUpdate(m_oraSql) == 0) {
//                throw new Exception("Error : Status Update of employee master.");
//            }
            // INSET NEW LIST TO ORACLE
            System.out.println("Downloading...");
            while (_rsMysql.next()) {
                try {
                    // INSERT NEW LINES TO ORACLE
                    m_oraSql = "insert into rms_pyemp_master ("
                            + " SBUCOD, LOCCOD, DEPCOD, EMP_NO, ETITLE, INITAL, FIRSTN,"
                            + " MIDDLN, LAST_N, ADDRS1, ADDRS2, ADDRS3, EM_DOB, NIC_NO,"
                            + " GENDER, SBUNAM, DOJOIN) values ('"
                            + _rsMysql.getString("SBUCOD") + "','"
                            + _rsMysql.getString("LOCCOD") + "','"
                            + _rsMysql.getString("DEPCOD") + "','"
                            + _rsMysql.getString("EMP_NO") + "','"
                            + _rsMysql.getString("ETITLE") + "','"
                            + _rsMysql.getString("INITAL") + "','"
                            + _rsMysql.getString("FIRSTN") + "','"
                            + _rsMysql.getString("MIDDLN") + "','"
                            + _rsMysql.getString("LAST_N") + "','"
                            + _rsMysql.getString("ADDRS1") + "','"
                            + _rsMysql.getString("ADDRS2") + "','"
                            + _rsMysql.getString("ADDRS3") + "','"
                            + oraFormat.format(mysqlFormat.parse(_rsMysql.getString("EM_DOB"))) + "','"
                            + _rsMysql.getString("NIC_NO") + "','"
                            + _rsMysql.getString("GENDER") + "','"
                            + _rsMysql.getString("SBU_NAME") + "','"
                            + oraFormat.format(mysqlFormat.parse(_rsMysql.getString("DOJOIN"))) + "')";

                    if (_oraStat.executeUpdate(m_oraSql) == 0) {
                        throw new Exception("Error : Insert of employee master.\n"
                                + " Emp No : " + _rsMysql.getString("EMP_NO"));
                    }

                } catch (Exception e) {
                    e.printStackTrace();
                }
            }

            if (endOracleTransaction() == 0) {
                throw new Exception("Oracle transaction ending error.");
            }
            System.out.println("Download completed.");
            result.put("result", "success");
            result.put("info", "Employee download to oracle is completed.");

        } catch (Exception e) {
            abortOracleTransaction();
            result.put("resule", "failed");
            result.put("error", e);
        } finally {
            System.out.println("Job done");
        }
        return result;
    }

    //--------------------------------------------------------------------------
    // **** CONNECTION HANDLING **** //
    //--------------------------------------------------------------------------
    public int startOracleTransaction() {
        try {

            if (_oraCon.getAutoCommit() == false) {
                _oraCon.rollback();
                _oraCon.setAutoCommit(true);
            }
            _oraCon.setAutoCommit(false);
            return 1;
        } catch (Exception e) {
            e.printStackTrace();
            return 0;
        }
    }

    public int endOracleTransaction() {
        try {
            _oraCon.commit();
            _oraCon.setAutoCommit(true);
            return 1;
        } catch (Exception e) {
            e.printStackTrace();
            return 0;
        }
    }

    public void abortOracleTransaction() {
        try {
            if (_oraCon.getAutoCommit() == false) {
                _oraCon.rollback();
                _oraCon.setAutoCommit(true);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
    //--------------------------------------------------------------------------
    // **** END OF CONNECTION HANDLING **** //
    //--------------------------------------------------------------------------

}
