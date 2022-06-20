/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package executables;

import dataaccess.OracleConnector;
import java.sql.ResultSet;
import java.sql.Statement;

/**
 *
 * @author Hayaz
 */
public class OrderInitialize {

    public void initializeOrderTable() {

        OracleConnector con = new OracleConnector("192.168.1.27", "rpd2", "MARKSYS", "best#for#my#home#123");

        try {


            Statement stmnt = con.getConn().createStatement();

            con.getConn().setAutoCommit(false);

            String m_strSql = "insert into rms_requisitions_hist select * from rms_requisitions "
                    + "where sbu_code='830' and loc_code='100' and doc_code in('SICO','MRPO','MANO','XLPO','PHPO') "
                    + "and cre_date <= sysdate - 7 and to_char(sysdate,'DY') = 'THU'";

            int m_intRowCount = stmnt.executeUpdate(m_strSql);
            System.out.println(m_intRowCount + " Rows Inserted to History.");

            if (m_intRowCount > 0) {
                m_strSql = "delete from rms_requisitions "
                        + "where sbu_code='830' and loc_code='100' and doc_code in('SICO','MRPO','MANO','XLPO','PHPO') "
                        + "and cre_date <= sysdate - 7 and to_char(sysdate,'DY') = 'THU'";

                m_intRowCount = stmnt.executeUpdate(m_strSql);
                System.out.println(m_intRowCount + " Rows Deleted.");
            }

            con.getConn().commit();
            con.getConn().setAutoCommit(true);
            stmnt.close();
            stmnt = null;

        } catch (Exception e) {
            e.printStackTrace();
            try {
                con.getConn().rollback();
                con.getConn().setAutoCommit(true);
            } catch (Exception ex) {
                ex.printStackTrace();
            }
        }

    }
}
