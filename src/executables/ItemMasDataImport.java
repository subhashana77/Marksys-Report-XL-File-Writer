/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package executables;

import com.jcraft.jsch.Channel;
import com.jcraft.jsch.ChannelSftp;
import com.jcraft.jsch.JSch;
import com.jcraft.jsch.Session;
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
public class ItemMasDataImport {

//    OracleConnector conMarksys = new OracleConnector("192.168.1.27", "rpd2", "MARKSYS",new jText.TextUti().getText("oracle"));
    OracleConnector conMarksys = null;
    Exception _exception = null;
    private RowSet _rset;
    private Statement stmnt, stmnt1, stmnt2, stmnt3;
    private ResultSet rs, rs1, rs2, rs3;
    private FileWriter aWriter, aWriter1, aWriter2, aWriter3;

    private String _sshserver = "";
    private int _port = 0;
    private String _user = "";
    private String _pass = "";

    private ChannelSftp _channelSftp = null;
    private Session _session = null;
    private Channel _channel = null;

    public void itemascreatefile() {
        try {
            OracleConnector conMarksys = new OracleConnector("192.168.1.27", "rpd2", "MARKSYS", new jText.TextUti().getText("oracle"));
            String m_strSQL = "";
            closeStatement();
            String path = "";
            path = "./" + "items_original.csv";
            File file = new File(path);
            file.delete();
            aWriter = new FileWriter(path);
//            m_strSQL = "  select 'Default' as attribute_set,'Simple Product' as type, "
//                    + " 'Simple' as product_type_id, "
//                    + " 'container1' as options_container, "
//                    + " '0'as has_options, "
//                    + " '1' as status, "
//                    + " '4' as visibility, "
//                    + " a.on_hand_q as qty, "
//                    + " '1' as is_in_stock, "
//                    + " b.loc_code as store_id, "
//                    + " '0' as weight, "
//                    + " round(a.unit_price,2) as price, "
//                    + " a.plu_code as sku, "
//                    + " '/'|| a.plu_code ||'.jpg' as image, "
//                    + " '/'|| a.plu_code ||'.jpg' as small_image, "
//                    + " '/'|| a.plu_code ||'.jpg' as thumbnail,"
//                    + " a.itm_desc as name,"
//                    + " a.itm_desc as image_label,"
//                    + " a.itm_desc as small_image_label,"
//                    + " a.itm_desc as thumbnail_label, "
//                    + " a.itm_desc as meta_title,"
//                    + " a.itm_desc as meta_keyword,"
//                    + " a.itm_desc as meta_description,"
//                    + " a.itm_desc as description,"
//                    + " '3,44' as category_ids,"
//                    + " '' as us_skus,"
//                    + " '' as cs_skus"
//                    + " from rms_itmmaster a "
//                    + " inner join rms_stockloc_inv b "
//                    + " on a.sbu_code=b.sbu_code "
//                    + " and a.item_code=b.item_code "
//                    + " where a.sbu_code='830' and b.stock_loc='00' "
//                    + " and a.plu_code is not null and b.on_hand_q > 0 and catcd4='ECOM'";
////                    + " cre_date=to_char(sysdate,'DD-MON-YYYY'"

//            m_strSQL = "  select a.plu_code as sku_PLU,"
//                    + " 'Simple Product' as type,"
//                    + " '' as category,"
//                    + " a.itm_desc as name,"
//                    + " round(a.unit_price,2) as special_price, "
//                    + " round(a.unit_price+r.tax_rate*a.unit_price/100),1) as price, "
//                    + " a.itm_desc as description,"
//                    + " a.itm_desc as short_description ,"
//                    + " '0' as weight, "
//                    + " '/'|| a.plu_code ||'.jpg' as media_gallery,"
//                    + " '1' as status,"
//                    + " '1' as is_in_stock,'4' as visibility "
//                    + " from rms_itmmaster a   "
//                    + "  inner join rms_tax_rates r on r.sbu_code='830' and r.tax_code=a.tax_code   "
//                    + "  where a.sbu_code='830' "
//                    + "  and (a.plu_code is not null or  a.plu_code  <>'')  "
//                    + " and a.catcd4='ECOM'";
//             m_strSQL = "select a.item_code,a.plu_code as sku_PLU,"
//                    + " 'Simple Product' as type, '' as category, a.itm_desc as name,"
//                    + " round((a.unit_price+(a.unit_price*r.tax_rate/100))-((a.unit_price+(a.unit_price*r.tax_rate/100))*nvl(p.Cust_disc,0)/100),1) as special_price, "
////                    + " round(a.unit_price+(r.tax_rate*a.unit_price/100),1) as special_price, "
//                    + " round(a.unit_price+(r.tax_rate*a.unit_price/100),1) as price, "
//                    + " p.Cust_disc, a.itm_desc as description, a.itm_desc as short_description , '0' as weight,  '/'|| a.plu_code ||'.jpg' as media_gallery,"
//                    + " '1' as status, '1' as is_in_stock,'4' as visibility  "
//                    + " from rms_itmmaster a inner join rms_tax_rates r "
//                    + " on r.sbu_code='830' and r.tax_code=a.tax_code   "
//                    + " left outer join ("
//                    + " SELECT b.item_code,max(b.sup_disc) as sup_disc,max(b.cus_disc) as Cust_disc  "
//                    + " FROM rmis.pom_promohd a inner join rmis.pom_promodt b on a.orderno=b.orderno"
//                    + " where a.camp_stdt <= to_date(sysdate) and a.camp_endt >= to_date(sysdate) "
//                    + " and b.line_status='APP'"
//                    + " group by b.item_code )p"
//                    + " on a.item_code= p.item_code  "
//                    + " where a.sbu_code='830' "
//                    + " and (a.plu_code is not null or  a.plu_code  <>'')"
//                    //                    + " and a.catcd4='ECOM' "
//                    + " and a.catcd4='ECOMT' ";
//                    + " and a.itm_group not like '%ZZ%'"; modified 20200723
            //modified 2021-04-21
            m_strSQL = "select a.item_code,a.plu_code as sku_PLU, "
                    + " 'Simple Product' as type, '' as category, a.itm_desc as name, "
                    + " round((a.unit_price+(a.unit_price*r.tax_rate/100))-((a.unit_price+(a.unit_price*r.tax_rate/100))*nvl(p.Cust_disc,0)/100),1)as special_price, "
                    + " round(a.unit_price+(r.tax_rate*a.unit_price/100),1) as price, "
                    + " p.Cust_disc, a.itm_desc as description, a.itm_desc as "
                    + " short_description , '0' as weight,  '/'|| a.plu_code ||'.jpg' as "
                    + " media_gallery,'1' as status, '1' as is_in_stock,'4' as visibility "
                    + " from rms_itmmaster a inner join rms_tax_rates r on r.sbu_code='830' and r.tax_code=a.tax_code left outer join ( "
                    + " select d.item_code,max(d.sup_disc) as sup_disc,max(d.cus_disc)as Cust_disc from "
                    + " (SELECT c.promid, c.itmcod,count(distinct loccod)FROM  rmis.pom_promo_loc c  "
                    + " inner join rms_locations l on c.sbucod=l.sbu_code and c.loccod=l.loc_code "
                    + " where c.sbucod='830' and l.lstatus='A' "
                    + " group by c.promid, c.itmcod "
                    + " having count(distinct loccod)>=30)x "
                    + " inner join rmis.pom_promohd h on x.promid=h.ORDERNO "
                    + " inner join rmis.pom_promodt d on x.promid=d.ORDERNO "
                    + " where h.camp_stdt <= to_date(sysdate) and h.camp_endt >= "
                    + " to_date(sysdate)"
//                    + " and d.line_status='APP'" 2021-05-28
                    + " and h.ORDER_STAT in ('UPD','APP')"
                    + " and x.itmcod=d.item_code "
                    + " group by d.item_code "
                    + " )p "
                    + " on a.item_code= p.item_code "
                    + " where a.sbu_code='830'   "
                    + " and (a.plu_code is not null or  a.plu_code <>'') "
                    + " and a.catcd4='ECOMT'";

            stmnt = conMarksys.getConn().createStatement(ResultSet.TYPE_SCROLL_SENSITIVE, ResultSet.CONCUR_READ_ONLY);
            rs = stmnt.executeQuery(m_strSQL);
            if (rs.next()) {
                rs.beforeFirst();
                int i = 0;
                while (rs.next()) {
//                    String m_attribute_set = rs.getString("attribute_set"),
//                            m_type = rs.getString("type"),
//                            m_product_type_id = rs.getString("product_type_id"),
//                            m_options_container = rs.getString("options_container"),
//                            m_has_options = rs.getString("has_options"),
//                            m_status = rs.getString("status"),
//                            m_visibility = rs.getString("visibility"),
//                            m_qty = rs.getString("qty"),
//                            m_is_in_stock = rs.getString("is_in_stock"),
//                            m_store_id = rs.getString("store_id"),
//                            m_weight = rs.getString("weight"),
//                            m_price = rs.getString("price"),
//                            m_sku = rs.getString("sku"),
//                            m_image = rs.getString("image"),
//                            m_small_image = rs.getString("small_image"),
//                            m_thumbnail = rs.getString("thumbnail"),
//                            m_name = rs.getString("name"),
//                            m_image_label = rs.getString("image_label"),
//                            m_small_image_label = rs.getString("small_image_label"),
//                            m_thumbnail_label = rs.getString("thumbnail_label"),
//                            m_meta_title = rs.getString("meta_title"),
//                            m_meta_keyword = rs.getString("meta_keyword"),
//                            m_meta_description = rs.getString("meta_description"),
//                            m_description = rs.getString("description"),
//                            m_category_ids = rs.getString("category_ids"),
//                            m_us_skus = rs.getString("us_skus"),
//                            m_cs_skus = rs.getString("us_skus");

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

//                    aWriter.write(m_attribute_set + ","
//                            + m_type + ","
//                            + m_product_type_id + ","
//                            + m_options_container + ","
//                            + m_has_options + ","
//                            + m_status + ","
//                            + m_visibility + ","
//                            + m_qty + ","
//                            + m_is_in_stock + ","
//                            + m_store_id + ","
//                            + m_weight + ","
//                            + m_price + ","
//                            + m_status + ","
//                            + m_sku + ","
//                            + m_image + ","
//                            + m_small_image + ","
//                            + m_thumbnail + ","
//                            + m_name + ","
//                            + m_image_label + ","
//                            + m_small_image_label + ","
//                            + m_thumbnail_label + ","
//                            + m_meta_title + ","
//                            + m_meta_keyword + ","
//                            + m_meta_description + ","
//                            + m_description + ","
//                            + m_category_ids + ","
//                            + m_us_skus + ","
//                            + m_cs_skus + "," + System.getProperty("line.separator"));
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
                System.out.println("Successfully Completed." + i + " items_original.csv");
//                System.out.println("Successfully  Saved " + prm_strDocCode + " With Doc Number " + _strDocNo);
            }

//            String m_strSQL1 = "";
//            closeStatement1();
//            String path1 = "";
//            path1 = "./" + "core_store.csv";
//            File file1 = new File(path1);
//            file1.delete();
//            aWriter = new FileWriter(path1);
//            m_strSQL1 = "select loc_code as loc_code,loc_name as loc_name from rms_locations where sbu_code='830' and  lcat02='SC'";
//            stmnt1 = conMarksys.getConn().createStatement(ResultSet.TYPE_SCROLL_SENSITIVE, ResultSet.CONCUR_READ_ONLY);
//            rs1 = stmnt1.executeQuery(m_strSQL1);
//            if (rs1.next()) {
//                rs1.beforeFirst();
//                int i = 0;
//                while (rs1.next()) {
//                    String m_Store_id = rs1.getString("loc_code"),
//                            m_Store_code = rs1.getString("loc_name");
//                    aWriter.write(m_Store_id + ","
//                            + m_Store_code + "," + System.getProperty("line.separator"));
////                    System.out.println(i);
//                    i++;
//                }
//                aWriter.flush();
//                aWriter.close();
//                System.out.println("Successfully Completed.");
//            }
            String m_strSQL2 = "";
            closeStatement2();
            String path2 = "";
            path2 = "./" + "items_stocks.csv";
            File file2 = new File(path2);
            file2.delete();
            aWriter = new FileWriter(path2);
//            m_strSQL2 = "select a.*,"
//                    + " case when (d.item_code is null or d.item_code='') then '-' else cast(unit_price as varchar2(100)) end price  from "
//                    + "(select a.loc_code as store_id ,a.item_code,b.plu_code as sku ,a.on_hand_q as qty,round(b.unit_price,2) unit_price,"
//                    + " '1' as stauts"
//                    + " from rms_stockloc_inv a"
//                    + " inner join rms_itmmaster b"
//                    + " on a.sbu_code=b.sbu_code and a.item_code=b.item_code "
//                    + " where a.sbu_code='830' and a.stock_loc='00' "
//                    + "  and (b.plu_code is not null or  b.plu_code  <>'') and a.on_hand_q > 0 and catcd4='ECOM'"
//                    + "  )a "
//                    + "  left outer join "
//                    + "  (select y.item_code from rmis.pom_poh x"
//                    + "  inner join rmis.pom_pod y "
//                    + "  on  x.orderno = y.orderno where x.rev_type='R' and x.order_stat in('UPD','APP') "
//                    + "  and y.line_status in('UPD','APP')  and x.mod_date between sysdate - 1 and sysdate ) d "
//                    + " on a.item_code=d.item_code";

//            String m_sql = "select cval01 from smsyspara where sbucod='830' and loccod='100' and parcod='ECOMLOC'";
//            Statement stmnt3 = conMarksys.getConn().createStatement(ResultSet.TYPE_SCROLL_SENSITIVE, ResultSet.CONCUR_READ_ONLY);
//            ResultSet rs = stmnt3.executeQuery(m_sql);
//            String m_strLocs = "";
//            if (rs.next()) {
//                m_strLocs = getParaEnclosed(rs.getString("cval01"));
//            }
//            if (rs != null) {
//                rs.close();
//                rs = null;
//            }
//            if (stmnt3 != null) {
//                stmnt3.close();
//                stmnt3 = null;
//            }
            m_strSQL2 = " select lcat04 as store_id,a.plu_code as sku_PLU,"
                    //                    + " sum(b.on_hand_q) as QTY "
                    //                    + " case when sum(b.on_hand_q)<=0 then 0 else round(sum(b.on_hand_q)* 0.2,0) end as QTY" //modified to 10% of the stock requested by shehan 2020-03-18
//                    + " case when sum(b.on_hand_q)<=8 then 0 else round(sum(b.on_hand_q)* 0.3,0) end as QTY" // modified 2021-05-28 req by rpd mgt
//                    + " case when sum(b.on_hand_q)<=5 then 0 else round(sum(b.on_hand_q)* 0.7,0) end as QTY" // modified 2021-07-28
//                    + " case when sum(b.on_hand_q)<=8 then 0 else round(sum(b.on_hand_q)* 0.3,0) end as QTY" //modified 2021-08-30 req by rpd
                    + " case when sum(b.on_hand_q)<=7 then 0 else round(sum(b.on_hand_q)* 0.5,0) end as QTY"
                    + " from rms_itmmaster a "
                    + " inner join rms_stockloc_inv b "
                    + " on a.sbu_code = b.sbu_code and a.item_code = b.item_code "
                    + " inner join rms_locations c "
                    + " on a.sbu_code=c.sbu_code and b.loc_code=c.loc_code "
                    + " where a.sbu_code='830'   "
                    //                    + " and b.loc_code in (" + m_strLocs + ")"
                    + " and b.stock_loc='00'  and (a.plu_code is not null or  a.plu_code  <>'') "
                    //                    + " and b.on_hand_q > 0  "
                    + " and a.catcd4='ECOMT' "
                    //                    + " and b.loc_code in ('10','03','27','79','105') "
                    //                    + " and b.loc_code ='10' "
                    + " and (lcat04 is not null or lcat04<>'') "
                    //                    + " having round(sum(b.on_hand_q)* 0.2,0) > 0"
                    + " group by lcat04,a.plu_code "
                    + " order by lcat04,a.plu_code  ";

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

                    aWriter.write(m_sku_PLU + ","
                            + m_store_id + ","
                            + m_QTY + "," + System.getProperty("line.separator"));
                    i++;
                }
                aWriter.flush();
                aWriter.close();
                System.out.println("Successfully Completed." + i + " items_stocks.csv");
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
//String  path = "./" + "itemmaster.csv";
//String  path1 = "./" + "core_store.csv";
//String  path2 = "./" + "products_inventory.csv";

            /**
             * ********old file transfer***********
             */
//            FileTransfer ftpfiles = new FileTransfer();
////            ftpfiles.connectFTP("ftp.arpicosupercentre.com", "csvfileupload@arpicosupercentre.com", "4]HJKkif}3Zw");
//            ftpfiles.connectFTP("52.200.183.63", "ftpuser", "nx6g07o");
//            ftpfiles.uploadFileToServer(path);
////            ftpfiles.uploadFileToServer(path1);
//            ftpfiles.uploadFileToServer(path2);
//            ftpfiles.disconnectFTP();
            /**
             * ***********end old file transfer****************
             */
//            if (!SSHConnection("/home/rpcadmin/AWS/lightcs.ppk")) {
//                throw new Exception(_exception);
//            }
//            fileTransfer(null);
        } catch (Exception e) {
            e.printStackTrace();
        }

    }

    private void fileTransfertoAWS() {
        try {

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public boolean SSHConnection() {
        boolean success = false;

        try {

            String prm_privateKeyPath = "D://AWS//arpico-app-server2.ppk";

            _sshserver = "18.140.132.70";
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

            fileTransfer(null);
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

    private boolean fileTransfer(String prm_date) {
        boolean m_result = false;
        int SFTPPORT = 22; // SFTP Port Number
        String SFTPWORKINGDIR = "";
        //DateFormat dateFormat = new SimpleDateFormat("MMMM dd, yyyy");

        try {

//            SFTPWORKINGDIR = "/home/rpcadmin/items_original.csv"; // Source Directory on SFTP server
            SFTPWORKINGDIR = "D:\\AWS\\items_original.csv"; // Source Directory on SFTP server

            System.out.println("Stock Transfering...");
            _channelSftp.put(new FileInputStream(SFTPWORKINGDIR), "/var/www/html/stkUp/test/items_original.csv", ChannelSftp.OVERWRITE);
            System.out.println("Stock Transfer file completed.!");

        } catch (Exception e) {
            System.out.println("Transfer failed.!");
            e.printStackTrace();
        } finally {
            SSHConClose();
        }
        return m_result;

    }
}
