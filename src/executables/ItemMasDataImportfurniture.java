/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package executables;

import dataaccess.OracleConnector;
import java.io.*;
import javax.sql.RowSet;

import java.sql.Statement;
import java.sql.ResultSet;
import org.apache.commons.net.ftp.FTP;
import org.apache.commons.net.ftp.FTPClient;

/**
 *
 * @author maheshanip
 */
public class ItemMasDataImportfurniture {

//    OracleConnector conMarksys = new OracleConnector("192.168.1.27", "rpd2", "MARKSYS",new jText.TextUti().getText("oracle"));
    OracleConnector conMarksys = null;
    Exception _exception = null;
    private RowSet _rset;
    private Statement stmnt, stmnt1, stmnt2, stmnt3;
    private ResultSet rs, rs1, rs2, rs3;
    private FileWriter aWriter, aWriter1, aWriter2, aWriter3;

    public void itemascreatefile() {
        try {
            OracleConnector conMarksys = new OracleConnector("192.168.1.27", "rpd2", "MARKSYS", new jText.TextUti().getText("oracle"));
            String m_strSQL = "";
            closeStatement();
            String path = "";
            path = "./" + "Items_original.csv";
            File file = new File(path);
            file.delete();
            aWriter = new FileWriter(path);

            m_strSQL = " select a.item_code,a.plu_code as sku_PLU,"
                    + " 'Simple Product' as type, '' as category, a.itm_desc as name,"
                    + " round((a.unit_price+(a.unit_price*r.tax_rate/100))-((a.unit_price+(a.unit_price*r.tax_rate/100))*nvl(p.Cust_disc,0)/100),1) as special_price, "
                    + " round(a.unit_price+(r.tax_rate*a.unit_price/100),1) as price, "
                    + " p.Cust_disc, a.itm_desc as description, a.itm_desc as short_description , '0' as weight,  '/'|| a.plu_code ||'.jpg' as media_gallery,"
                    + " '1' as status, '1' as is_in_stock,'4' as visibility  "
                    + " from rms_itmmaster a inner join rms_tax_rates r "
                    + " on r.sbu_code='830' and r.tax_code=a.tax_code   "
                    + " left outer join ("
                    + " SELECT b.item_code,max(b.sup_disc) as sup_disc,max(b.cus_disc) as Cust_disc  "
                    + " FROM rmis.pom_promohd a inner join rmis.pom_promodt b on a.orderno=b.orderno"
                    + " where a.camp_stdt <= to_date(sysdate) and a.camp_endt >= to_date(sysdate) "
                    + " and b.line_status='APP'"
                    + " group by b.item_code )p"
                    + " on a.item_code= p.item_code  "
                    + "  inner join rms_itemgroup x  "
                    + "  on a.sbu_code = x.sbu_code and a.itm_group = x.itm_group "
                    + " where a.sbu_code='830' "
                    + " and (a.plu_code is not null or  a.plu_code  <>'')  "
                    + " and (x.grpct1 like 'C%' or x.grpct1 like 'M%' or x.grpct1 like 'N%') ";
            //  + " and a.catcd4='ECOM' "
//                    + " and a.itm_group not like '%ZZ%'";modified 2021-01-12

            stmnt = conMarksys.getConn().createStatement(ResultSet.TYPE_SCROLL_SENSITIVE, ResultSet.CONCUR_READ_ONLY);
            rs = stmnt.executeQuery(m_strSQL);
            if (rs.next()) {
                rs.beforeFirst();
                int i = 0;
                while (rs.next()) {

                    String m_sku = rs.getString("sku_PLU"),
                            m_type = rs.getString("type"),
                            //                            m_category = rs.getString("category"),
                            m_category = "",
                            m_name = rs.getString("name"),
                            m_special_price = rs.getString("special_price"),
                            m_price = rs.getString("price"),
                            m_description = rs.getString("description"),
                            m_short_description = rs.getString("short_description"),
                            m_weight = rs.getString("weight"),
                            m_media_gallery = rs.getString("media_gallery"),
                            m_status = rs.getString("status"),
                            m_is_in_stock = rs.getString("is_in_stock"),
                            m_visibility = rs.getString("visibility");

                    aWriter.write(m_sku + ","
                            + m_type + ","
                            + m_category + ","
                            + m_name + ","
                            + m_special_price + ","
                            + m_price + ","
                            + m_description + ","
                            + m_short_description + ","
                            + m_weight + ","
                            + m_media_gallery + ","
                            + m_status + ","
                            + m_is_in_stock + ","
                            + m_visibility + "," + System.getProperty("line.separator"));

//                    System.out.println(i);
                    i++;
                }
                aWriter.flush();
                aWriter.close();
                System.out.println("Successfully Completed." + i + " Items_original.csv");
//                System.out.println("Successfully  Saved " + prm_strDocCode + " With Doc Number " + _strDocNo);
            }

            String m_strSQL2 = "";
            closeStatement2();
            String path2 = "";
            path2 = "./" + "Items_stocks.csv";
            File file2 = new File(path2);
            file2.delete();
            aWriter = new FileWriter(path2);

//            m_strSQL2 = " select lcat04 as store_id,a.plu_code as sku_PLU,"
//                    //                    + " sum(b.on_hand_q) as QTY "
//                    + " case when sum(b.on_hand_q)<=0 then 0 else round(sum(b.on_hand_q)* 0.2,0) end as QTY"
//                    + " from rms_itmmaster a "
//                    + " inner join rms_stockloc_inv b "
//                    + " on a.sbu_code = b.sbu_code and a.item_code = b.item_code "
//                    + " inner join rms_locations c "
//                    + " on a.sbu_code=c.sbu_code and b.loc_code=c.loc_code "
//                    + " where a.sbu_code='830'   "
//                    //                    + " and b.loc_code in (" + m_strLocs + ")"
//                    + " and b.stock_loc='00'  and (a.plu_code is not null or  a.plu_code  <>'') "
//                    //                    + " and b.on_hand_q > 0  "
//                    + " and a.catcd4='ECOM' "
//                    + " and (lcat04 is not null or lcat04<>'') "
//                    //                    + " having round(sum(b.on_hand_q)* 0.2,0) > 0"
//                    + " group by lcat04,a.plu_code "
//                    + " order by lcat04,a.plu_code  ";
            m_strSQL2 = "  select lcat04 as store_id,a.plu_code as sku_PLU,  "
                    //                    + " case when sum(b.on_hand_q)<=0 then 0 else round(sum(b.on_hand_q)* 0.2,0) end as QTY "
                    //                    + " case when sum(b.on_hand_q)<=0 then 0 else round(sum(b.on_hand_q),0) end as QTY " //2021-10-28 requested by furniture if loc 40 stock is less than or equal to 2 consider as 0
                    + "  sum(case when (b.loc_code='40' and (b.on_hand_q)<=2) then 0 "
                    + "  when (b.loc_code<>'40' and (b.on_hand_q)<=0) then 0  "
                    + "  else round((b.on_hand_q),0) end) as QTY "
                    + " from rms_itmmaster a  "
                    + " inner join rms_stockloc_inv b  "
                    + " on a.sbu_code = b.sbu_code and a.item_code = b.item_code  "
                    + " inner join rms_locations c  "
                    + " on a.sbu_code=c.sbu_code and b.loc_code=c.loc_code  "
                    + " inner join rms_itemgroup x "
                    + " on a.sbu_code = x.sbu_code and a.itm_group =x.itm_group "
                    + " where a.sbu_code='830' "
                    + " and b.stock_loc='00'  and (a.plu_code is not null or  a.plu_code  <>'')  "
                    //                    + " and b.loc_code not in ('98',  '22',  '04' , '23','109' ,  '24' , '113' , '05' ,  '02' ,'79' ,  '27',  '115','14' , '112'  , '108'  ,  '111'  , '110' ,  '105'  , '10'  ,  '26', '106' ,  '76'  ,'116' , '03','25', '114') "
                    //                    + " and b.loc_code  in ('40',  '140',  '240') "
                    + " and b.loc_code  in ('40',  '140',  '240','340') "
                    // + " and x.grpct1 in ('C%','M%','N%') "
                    + " and (x.grpct1 like 'C%' or x.grpct1 like 'M%' or x.grpct1 like 'N%') "
                    //                    + " and a.itm_group not like '%ZZ%' "  modified 2021-01-12
                    //and a.catcd4='ECOM'  
                    //  + " and (lcat04 is not null or lcat04<>'')         "
                    + " group by lcat04,a.plu_code  "
                    + " order by lcat04,a.plu_code   ";

            //   and x.mod_date > '01-JUL-2013'  and x.mod_date between sysdate - 1 and sysdate
            stmnt2 = conMarksys.getConn().createStatement(ResultSet.TYPE_SCROLL_SENSITIVE, ResultSet.CONCUR_READ_ONLY);
            rs2 = stmnt2.executeQuery(m_strSQL2);
            if (!rs2.next()) {
                System.out.println("Data not found in rms_stockloc_inv.");
            } else {
                rs2.beforeFirst();
                int i = 0;
                while (rs2.next()) {
                    String m_sku_PLU = rs2.getString("sku_PLU"),
                            m_store_id = rs2.getString("store_id"),
                            m_QTY = rs2.getString("QTY");

//                    aWriter.write(m_sku_PLU + ","
//                            + m_store_id + ","
//                            + m_QTY + "," + System.getProperty("line.separator"));
                    aWriter.write(m_sku_PLU + ","
                            + m_QTY + "," + System.getProperty("line.separator"));

                    i++;
                }
                aWriter.flush();
                aWriter.close();
                System.out.println("Successfully Completed." + i + " Items_stocks.csv");
            }
            if (rs2 != null) {
                rs2.close();
                rs2 = null;
            }
            if (stmnt2 != null) {
                stmnt2.close();
                stmnt2 = null;
            }
            if (rs != null) {
                rs.close();
                rs = null;
            }
            if (stmnt != null) {
                stmnt.close();
                stmnt = null;
            }
            if (conMarksys.getConn() != null) {
                conMarksys.getConn().close();
                conMarksys = null;
            }

            FileTransfer ftpfiles = new FileTransfer();
//            ftpfiles.connectFTP("ftp.arpicosupercentre.com", "csvfileupload@arpicosupercentre.com", "4]HJKkif}3Zw");
//            ftpfiles.connectFTP("ftp.arpicofurniture.com", "stock", "5@2sp23dh4");
            ftpfiles.connectFTP("128.199.99.22", "stock@arpicofurniture.com", "5@2sp23dh4");
            ftpfiles.uploadFileToServer(path);
//            ftpfiles.uploadFileToServer(path1);
            ftpfiles.uploadFileToServer(path2);
            ftpfiles.disconnectFTP();

        } catch (Exception e) {
            e.printStackTrace();
        }

    }

    public void closeStatement() {
        try {
            if (stmnt != null) {
                stmnt.close();
            }

            if (rs != null) {
                rs.close();
            }

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void closeStatement1() {
        try {
            if (stmnt1 != null) {
                stmnt1.close();
            }

            if (rs1 != null) {
                rs1.close();
            }

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void closeStatement2() {
        try {
            if (stmnt2 != null) {
                stmnt2.close();
            }

            if (rs2 != null) {
                rs2.close();
            }

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public int startTransactionMarksys() {
        try {
            if (conMarksys.getConn().getAutoCommit() == false) {
                conMarksys.rollback();
                conMarksys.getConn().setAutoCommit(true);
            }
            conMarksys.getConn().setAutoCommit(false);
            return 1;
        } catch (Exception e) {
            e.printStackTrace();
            return 0;
        }
    }

    public int endTransactionMarksys() {
        try {
            conMarksys.getConn().commit();
            conMarksys.getConn().setAutoCommit(true);
            return 1;
        } catch (Exception e) {
            e.printStackTrace();
            return 0;
        }
    }

    public void abortTransactionMarksys() {
        try {
            if (conMarksys.getConn().getAutoCommit() == false) {
                conMarksys.getConn().rollback();
                conMarksys.getConn().setAutoCommit(true);
            }

        } catch (Exception e) {
            e.printStackTrace();

        }
    }

    public String getParaEnclosed(String prm_strStr) {
        String m_strList[] = prm_strStr.split(",");
        String m_List = "";
        for (int i = 0; i < m_strList.length; i++) {
            m_List = m_List + "'" + m_strList[i] + "'" + ",";
        }
        return m_List.substring(0, m_List.length() - 1);
    }

    public String uploadFileToServer(String prm_strPara) {
        FTPClient ftpClient = new FTPClient();
        String _strERR = "";
        try {
            try {
                ftpClient.connect("52.200.183.63", 21);
                ftpClient.login("ftpuser", "nx6g07o");
                ftpClient.enterLocalPassiveMode();
                ftpClient.setFileType(FTP.BINARY_FILE_TYPE);
                File firstLocalFile = new File(".//Deutsche_Bank_" + prm_strPara + "_upload.txt");
                String firstRemoteFile = "/opt/webapp/trunk/media/import/Deutsche_Bank_" + prm_strPara + "_upload.txt";
                InputStream inputStreamnew = new FileInputStream(firstLocalFile);
                System.out.println("Start uploading file");
                boolean donenew = ftpClient.storeFile(firstRemoteFile, inputStreamnew);
                inputStreamnew.close();
                if (donenew) {
                    System.out.println("The file is uploaded successfully.");
                }

                File firstLocalFile1 = new File(".//Deutsche_Bank_" + prm_strPara + "_upload.chk");
                String firstRemoteFile1 = "/home/pump/pub/Deutsche_Bank_" + prm_strPara + "_upload.chk";
                InputStream inputStreamnew1 = new FileInputStream(firstLocalFile1);
                System.out.println("Start uploading file2");
                boolean donenew1 = ftpClient.storeFile(firstRemoteFile1, inputStreamnew1);
                inputStreamnew1.close();
                if (donenew1) {
                    System.out.println("The file is uploaded successfully.");
                }

            } catch (IOException ex) {
                System.out.println("Error: " + ex.getMessage());
                ex.printStackTrace();
                _strERR = "Error: " + ex.getMessage();
            } finally {
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
        } catch (Exception e) {
            e.printStackTrace();
            _strERR = _strERR + " " + e.getMessage();
        }
        return _strERR;
    }
}
