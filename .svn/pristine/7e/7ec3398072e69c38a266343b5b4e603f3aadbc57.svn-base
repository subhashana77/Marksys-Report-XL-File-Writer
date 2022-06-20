/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package executables;

import dataaccess.MysqlConnector;
import dataaccess.OracleConnector;
import java.sql.ResultSet;
import java.sql.Statement;

/**
 *
 * @author indika.kuruppu
 */
public class RDStock {

    OracleConnector con;// = new OracleConnector("192.168.1.27", "rpd2", "CRM", "RETAIL#123%");
    MysqlConnector mysql;// = new MysqlConnector("192.168.1.7", "redist", "tab", "tab123");
    Statement _orastmnt;
    Statement _mySqlstmnt;
    ResultSet _oraRs;
    String _stroraqry = "";
    String _strmysqlqry = "";

    public void updateRD_stocks() {
        try {
            closeStatement();
            con = new OracleConnector("192.168.1.27", "rpd2", "MARKSYS", "best#for#my#home#123");
            mysql = new MysqlConnector("192.168.1.7", "redist", "tab", "tab123");
            _stroraqry = "select z.sbu_code,l.loc_code,l.loc_name,to_char(sysdate,'YYYY-MM-DD HH24:mm:SS') rep_date,x.itm_group,x.grup_name,"
                    + " i.item_code,i.itm_desc,sum(qty) as qty ,sum(alloc_qty) alcqty,"
                    + " max(i.unit_price) unipri,max(i.wholesale_price) whslpr from "
                    + " (select a.sbu_code,a.loc_code,a.item_code,a.on_hand_q as qty from rms_stockloc_inv a  "
                    + " inner join rms_itmmaster b on b.sbu_code='830' and b.item_code=a.item_code "
                    + " where a.sbu_code='830' and a.loc_code = '60' and  substr(b.itm_group,3,2) in ('58') "
                    + " ) z "
                    + " inner join rms_locations L on L.sbu_code='830' and z.loc_code=L.loc_code  "
                    + " inner join rms_itmmaster i on i.sbu_code='830' and i.item_code=z.item_code "
                    + " inner join rms_itemgroup x on x.sbu_code='830' and x.itm_group=i.itm_group "
                    + " group by z.sbu_code,l.loc_code,l.loc_name,x.itm_group,x.grup_name,i.item_code,i.itm_desc";
            _orastmnt = con.getConn().createStatement(ResultSet.TYPE_SCROLL_INSENSITIVE, ResultSet.CONCUR_READ_ONLY);
            _mySqlstmnt = mysql.getConn().createStatement(ResultSet.TYPE_SCROLL_INSENSITIVE, ResultSet.CONCUR_READ_ONLY);
            _oraRs = _orastmnt.executeQuery(_stroraqry);
            if (!_oraRs.next()) {
                throw new Exception("Empty Rd Stocks");
            }
            _mySqlstmnt.executeUpdate("delete from rditemstocks");
            _oraRs.beforeFirst();
            while (_oraRs.next()) {
                _strmysqlqry = "insert into rditemstocks(sbucod, loccod, locnam, repdat, itmgrp, grpnam, itmcod, itmdes, itmqty,alcqty,unipri,whslpr) "
                        + " values("
                        + "'" + _oraRs.getString("sbu_code") + "',"
                        + "'" + _oraRs.getString("loc_code") + "',"
                        + "'" + _oraRs.getString("loc_name") + "',"
                        + "'" + _oraRs.getString("rep_date") + "',"
                        + "'" + _oraRs.getString("itm_group") + "',"
                        + "'" + _oraRs.getString("grup_name") + "',"
                        + "'" + _oraRs.getString("item_code") + "',"
                        + "'" + _oraRs.getString("itm_desc") + "',"
                        + "'" + _oraRs.getString("qty") + "',"
                        + "'" + _oraRs.getString("alcqty") + "',"
                        + "'" + _oraRs.getString("unipri") + "',"
                        + "'" + _oraRs.getString("whslpr") + "'"
                        + ")";
                if (_mySqlstmnt.executeUpdate(_strmysqlqry) <= 0) {
                    throw new Exception("Error occured in insert into rditemstocks");
                }
            }
            closeStatement();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void updateRD_Sales() {
        try {
            closeStatement();
            con = new OracleConnector("192.168.1.27", "rpd2", "MARKSYS", "best#for#my#home#123");
            mysql = new MysqlConnector("192.168.1.7", "redist", "tab", "tab123");
            _stroraqry = "select to_char(sysdate,'YYYY-MM-DD HH24:mm:SS') rep_date,cs.rep_id,cs_code,cs.name,y.catdes as Category,i.itm_group,i.grup_name,sum(z.sal_qty) as Sales_qty,sum(z.sal_value) as Sales_value,"
                    + " case when sum(z.bud_val) <> 0 then round(sum(z.sal_value)/sum(z.bud_val)*100,2) else 0 end as achivement"
                    + " from ( "
                    + " select cs_code,itm_group,itm_code as item_code,sum(qty) as bud_qty,sum(value) as bud_val,sum(0) as sal_qty,sum(0) as sal_value "
                    + " from rmis.rpd_rd "
                    + " where sbu_code='830' and loc_code='60' and rep_date between to_date('01-'||to_char(sysdate,'MON')||'-'||to_char(sysdate,'YYYY')) and last_day(sysdate) and type='P' "
                    + " group by cs_code,itm_group,itm_code "
                    + " union  "
                    + " select a.cust_sup_code as cs_code,b.itmgrp as itm_group,b.item_code,sum(0) as bud_qty,sum(0) as bud_value,"
                    + " sum(case when a.doc_code in ('RDIN','DISR') then qty else -1*qty end) as sal_qty,"
                    + " sum((b.qty* (case when a.doc_code in ('RDIN','DISR') then 1 else -1 end)*(b.price-(b.price*b.disrate/100))) + nvl(b.tax_amt1,0) + nvl(b.tax_amt2,0) + nvl(b.tax_amt3,0) + nvl(tax_amt4,0))  Sal_value "
                    + " from marksys.rms_doc_txnm a,marksys.rms_doc_txnd b,marksys.rms_itmmaster c "
                    + " where a.sbu_code='830' and a.loc_code in ('60','61','62') and  "
                    + " a.doc_code in ('DISC','NVAT','CRDN','INRV','PINV','PSRN','KINV','KSRN','CINV','BINV','BRET','DISR','DINV') and "
                    + " a.txn_date between to_date('01-'||to_char(sysdate,'MON')||'-'||to_char(sysdate,'YYYY')) and last_day(sysdate) and "
                    + " a.sbu_code=b.sbu_code and a.loc_code=b.loc_code and a.doc_code=b.doc_code and a.doc_no=b.doc_no and  "
                    + " b.sbu_code=c.sbu_code  and b.item_code=c.item_code "
                    + " group by a.cust_sup_code,b.itmgrp,b.item_code"
                    + " ) z "
                    + " inner join rms_itemgroup i on i.sbu_code='830' and i.itm_group=z.itm_group  "
                    + " INNER join marksys.rms_cust_sup cs on cs.sbu_code='830' and cs.cust_sup='C' and cs.cs_code=z.cs_Code "
                    + " left outer join smcategories y on y.sbucod='830' and y.cattyp='BUD' and catpos='011' and y.catcod=i.gcat07 "
                    + " group by sysdate,cs.rep_id,cs.cs_code,cs.name,y.catdes,i.itm_group,i.grup_name";

            _orastmnt = con.getConn().createStatement(ResultSet.TYPE_SCROLL_INSENSITIVE, ResultSet.CONCUR_READ_ONLY);
            _mySqlstmnt = mysql.getConn().createStatement(ResultSet.TYPE_SCROLL_INSENSITIVE, ResultSet.CONCUR_READ_ONLY);
            _oraRs = _orastmnt.executeQuery(_stroraqry);
            if (!_oraRs.next()) {
                throw new Exception("Empty Rd Sales");
            }
            _mySqlstmnt.executeUpdate("delete from rdcusdaysale");
            _oraRs.beforeFirst();
            while (_oraRs.next()) {
                _strmysqlqry = "insert into rdcusdaysale(sbucod, repdat, reprid, cscode, csname, catcod, itmgrp, grpnam, salqty, salval, achive) "
                        + " values("
                        + "'830',"
                        + "'" + _oraRs.getString("rep_date") + "',"
                        + "'" + _oraRs.getString("rep_id") + "',"
                        + "'" + _oraRs.getString("cs_code") + "',"
                        + "'" + _oraRs.getString("name") + "',"
                        + "'" + _oraRs.getString("Category") + "',"
                        + "'" + _oraRs.getString("itm_group") + "',"
                        + "'" + _oraRs.getString("grup_name") + "',"
                        + "'" + _oraRs.getString("Sales_qty") + "',"
                        + "'" + _oraRs.getString("Sales_value") + "',"
                        + "'" + _oraRs.getString("achivement") + "'"
                        + ")";
                if (_mySqlstmnt.executeUpdate(_strmysqlqry) <= 0) {
                    throw new Exception("Error occured in insert into rdcusdaysale");
                }
            }
            closeStatement();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void updateRD_CUST_BAL() {
        try {
            closeStatement();
            con = new OracleConnector("192.168.1.27", "rpd2", "MARKSYS", "best#for#my#home#123");
            mysql = new MysqlConnector("192.168.1.7", "redist", "tab", "tab123");
//            _stroraqry = "SELECT a.sbu_code sbu_code, a.cs_code cs_code,  b.name name,b.rep_id rep_id,max(f.user_name) repnam, credit_lim as crdlim, "
//                    + " to_char(sysdate,'YYYY-MM-DD HH24:mm:SS') as rep_date,blk_status,"
//                    + "  ROUND(SUM(CASE  WHEN a.pd_flag<>'Y' THEN a.amount ELSE 0  END),2) AS balance, "
//                    + "  ROUND(SUM(CASE  WHEN ((a.txn_date >sysdate-60)  AND a.txn_date <= sysdate)  AND a.pd_flag <>'Y' THEN a.amount ELSE 0  END),2) AS L60day_Balance, "
//                    + "  ROUND(SUM(CASE  WHEN (a.txn_date <=sysdate-60)   AND a.pd_flag <>'Y'    THEN a.amount    ELSE 0  END),2) AS Over60_OutStanding, "
//                    + "  ROUND(SUM(CASE  WHEN a.pd_flag='Y'  THEN a.amount ELSE 0  END),2) AS pd_balance, "
//                    + "  max(b.add1) add1,max(b.add2) add2,max(b.add3) add3,max(b.city) city,max(b.street) street,"
//                    + " max(h.catdes) district,max(g.dsca) arecod,max(b.cusdis) cusdis "
//                    + " FROM  "
//                    + " (select a.sbu_code, a.loc_code, a.cs_code,a.doc_code, a.doc_no,sum(a.amount) as amount, "
//                    + " min(a.txn_date) as txn_date,min(a.pd_flag) as pd_flag "
//                    + " from rms_acc_receivable a "
//                    + " inner join rms_locations b on a.sbu_code=b.sbu_code and a.loc_code=b.loc_code "
//                    + " where a.sbu_code='830' and a.loc_code  in( '60','61','62') and a.cust_sup='C' and lpad(cs_code,6,0) between '300000' and '359999' "
//                    + " group by a.sbu_code, a.loc_code, a.cs_code,a.doc_code, a.doc_no "
//                    + " having sum(a.amount)<>0) a "
//                    + " INNER JOIN rms_cust_sup b  ON a.sbu_code=b.sbu_code AND b.cust_sup='C' and a.cs_code=b.cs_code "
//                    + " INNER JOIN rms_sbu c ON a.sbu_code =c.sbu_code "
//                    + " inner join rms_locations e on a.sbu_code=e.sbu_code and a.loc_code=e.loc_code "
//                    + " inner join cust_area g on  b.area_code=g.area "
//                    + " left outer join smcategories h on a.sbu_code=h.sbucod and h.cattyp='DIS' and h.catpos='022' and  b.cbrn=h.catcod "
//                    + " left outer join rms_reps_manages f on a.sbu_code=f.sbu_code and a.loc_code=f.loc_code and rep_man_flg='N' and  b.rep_id=f.rep_id "
//                    + " GROUP BY a.sbu_code ,a.cs_code ,b.rep_id ,b.name ,credit_lim, sysdate,blk_status ";
            _stroraqry = "select sbu_code sbu_code ,xx.cs_code cs_code,name ,rep_id rep_id,repnam repnam,credit_limit crdlim,report_date rep_date,"
                    + " blk_status blk_status,balance balance,L60day_Balance L60day_Balance,Over60_OutStanding Over60_OutStanding,pd_balance pd_balance"
                    + " ,nvl(order_balance,0) order_balance,add1,add2,add3,city,street,district,arecod,cusdis"
                    + " from "
                    + " ("
                    + " SELECT a.sbu_code , a.cs_code ,  b.name ,b.rep_id, credit_lim as Credit_limit, to_char(sysdate,'YYYY-MM-DD HH24:mm:SS') report_date,blk_status,"
                    + " ROUND(SUM(CASE  WHEN a.pd_flag<>'Y' THEN a.amount ELSE 0  END),2) AS balance,"
                    + " ROUND(SUM(CASE  WHEN ((a.txn_date >sysdate-60)  AND a.txn_date <= sysdate)  AND a.pd_flag <>'Y' THEN a.amount ELSE 0  END),2) AS L60day_Balance,"
                    + " ROUND(SUM(CASE  WHEN (a.txn_date <=sysdate-60)   AND a.pd_flag <>'Y'    THEN a.amount    ELSE 0  END),2) AS Over60_OutStanding,"
                    + " ROUND(SUM(CASE  WHEN a.pd_flag='Y'  THEN a.amount ELSE 0  END),2) AS pd_balance,"
                    + " max(b.add1) add1,max(b.add2) add2,max(b.add3) add3,max(b.city) city,max(b.street) street,max(h.catdes) district,"
                    + " max(g.dsca) arecod,max(b.cusdis) cusdis ,max(f.user_name) repnam"
                    + " FROM "
                    + " (select a.sbu_code, a.loc_code, a.cs_code,a.doc_code, a.doc_no,sum(a.amount) as amount,"
                    + " min(a.txn_date) as txn_date,min(a.pd_flag) as pd_flag "
                    + " from rms_acc_receivable a "
                    + " inner join rms_locations b on a.sbu_code=b.sbu_code and a.loc_code=b.loc_code "
                    + " where a.sbu_code='830' and a.loc_code  in( '60','61','62') and to_date(a.txn_date)<= sysdate  "
                    + " group by a.sbu_code, a.loc_code, a.cs_code,a.doc_code, a.doc_no "
                    + " having sum(a.amount)<>0) a "
                    + " INNER JOIN rms_cust_sup b  ON a.sbu_code=b.sbu_code AND a.cs_code=b.cs_code"
                    + " INNER JOIN rms_sbu c ON a.sbu_code =c.sbu_code  "
                    + " inner join rms_locations e on a.sbu_code=e.sbu_code and a.loc_code=e.loc_code "
                    + " inner join cust_area g on  b.area_code=g.area  "
                    + " left outer join smcategories h on a.sbu_code=h.sbucod and h.cattyp='DIS' and h.catpos='022' and  b.cbrn=h.catcod  "
                    + " left outer join rms_reps_manages f on a.sbu_code=f.sbu_code and a.loc_code=f.loc_code and rep_man_flg='N' and  b.rep_id=f.rep_id  "
                    + " WHERE a.sbu_code ='830' AND e.lcat01  ='RD' AND b.cust_sup ='C'  "
                    + " GROUP BY a.sbu_code ,a.cs_code ,b.rep_id ,b.name ,credit_lim, sysdate,blk_status  ) xx "
                    + " left outer join "
                    + " ( select d.cs_code,sum((qty-issued_qty)*price) as Order_balance from rms_doc_txnm a inner join rms_doc_txnd b on a.sbu_code=b.sbu_code and a.loc_code=b.loc_code and a.doc_code=b.doc_code and a.doc_no=b.doc_no"
                    + " inner join rms_itmmaster c on c.sbu_code=b.sbu_code and c.item_code=b.item_code  "
                    + " inner join rms_cust_sup d on a.sbu_code=d.sbu_code and d.cust_sup='C' and a.cust_sup_code=d.cs_code  "
                    + " where a.sbu_code='830' and a.loc_code='60' and a.doc_code='SORD' and a.txn_date between sysdate - 21 and sysdate  and qty - issued_qty <> 0 and a.mstat='VAL' and b.dstat='VAL'"
                    + " group by d.cs_code ) yy on yy.cs_Code=xx.cs_Code";

            _orastmnt = con.getConn().createStatement(ResultSet.TYPE_SCROLL_INSENSITIVE, ResultSet.CONCUR_READ_ONLY);
            _mySqlstmnt = mysql.getConn().createStatement(ResultSet.TYPE_SCROLL_INSENSITIVE, ResultSet.CONCUR_READ_ONLY);
            _oraRs = _orastmnt.executeQuery(_stroraqry);
            if (!_oraRs.next()) {
                throw new Exception("Empty customer balances");
            }
            _mySqlstmnt.executeUpdate("delete from rdcusbalance");
            _oraRs.beforeFirst();
            while (_oraRs.next()) {

                _strmysqlqry = "insert into rdcusbalance(sbucod, cscode, csname, reprid, crdlim, repdat, blksta, balamt, 60dbal, ov60bl, repnam,"
                        + " add1, add2, add3, city, street, district, arecod, cusdis,pdbal,alcamt) "
                        + " values("
                        + "'" + _oraRs.getString("sbu_code") + "',"
                        + "'" + _oraRs.getString("cs_code") + "',"
                        + "'" + _oraRs.getString("name") + "',"
                        + "'" + _oraRs.getString("rep_id") + "',"
                        + "'" + _oraRs.getString("crdlim") + "',"
                        + "'" + _oraRs.getString("rep_date") + "',"
                        + "'" + _oraRs.getString("blk_status") + "',"
                        + "'" + _oraRs.getString("balance") + "',"
                        + "'" + _oraRs.getString("L60day_Balance") + "',"
                        + "'" + _oraRs.getString("Over60_OutStanding") + "',"
                        + "'" + _oraRs.getString("repnam") + "',"
                        + "'" + _oraRs.getString("add1") + "',"
                        + "'" + _oraRs.getString("add2") + "',"
                        + "'" + _oraRs.getString("add3") + "',"
                        + "'" + _oraRs.getString("city") + "',"
                        + "'" + _oraRs.getString("street") + "',"
                        + "'" + _oraRs.getString("district") + "',"
                        + "'" + _oraRs.getString("arecod") + "',"
                        + "'" + _oraRs.getString("cusdis") + "',"
                        + "'" + _oraRs.getString("pd_balance") + "',"
                        + "'" + _oraRs.getString("order_balance") + "'"
                        + ")";
                if (_mySqlstmnt.executeUpdate(_strmysqlqry) <= 0) {
                    throw new Exception("Error occured in insert into rdcusbalance");
                }
            }
            closeStatement();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void updatependingOrders() {
        try {
            closeStatement();
            con = new OracleConnector("192.168.1.27", "rpd2", "MARKSYS", "best#for#my#home#123");
            mysql = new MysqlConnector("192.168.1.7", "redist", "tab", "tab123");
            _stroraqry = "select a.sbu_code sbucod,a.loc_code loccod,d.cs_code cscode,d.name csname,a.doc_code doccod, "
                    + " a.doc_no docnum,to_char(a.txn_date,'YYYY-MM-DD') txndat,c.item_code itmcod ,c.itm_desc itmdes,qty-issued_qty as penqty from "
                    + " rms_doc_txnm a inner join rms_doc_txnd b on a.sbu_code=b.sbu_code and a.loc_code=b.loc_code and "
                    + " a.doc_code=b.doc_code and a.doc_no=b.doc_no "
                    + " inner join rms_itmmaster c on c.sbu_code=b.sbu_code and c.item_code=b.item_code  "
                    + " inner join rms_cust_sup d on a.sbu_code=d.sbu_code and d.cust_sup='C' and a.cust_sup_code=d.cs_code  "
                    + " where a.sbu_code='830' and a.loc_code='60' and a.doc_code='SORD' and "
                    + " a.txn_date between sysdate - 21 and sysdate  and qty - issued_qty <> 0";


            _orastmnt = con.getConn().createStatement(ResultSet.TYPE_SCROLL_INSENSITIVE, ResultSet.CONCUR_READ_ONLY);
            _mySqlstmnt = mysql.getConn().createStatement(ResultSet.TYPE_SCROLL_INSENSITIVE, ResultSet.CONCUR_READ_ONLY);
            _oraRs = _orastmnt.executeQuery(_stroraqry);
            if (!_oraRs.next()) {
                throw new Exception("Empty customer Pending Quantity");
            }
            _mySqlstmnt.executeUpdate("delete from rdcustpenqty");
            _oraRs.beforeFirst();
            while (_oraRs.next()) {

                _strmysqlqry = "insert into rdcustpenqty(sbucod, loccod, cscode, doccod, docnum, txndat, itmcod, itmdes, penqty,csname) "
                        + " values("
                        + "'" + _oraRs.getString("sbucod") + "',"
                        + "'" + _oraRs.getString("loccod") + "',"
                        + "'" + _oraRs.getString("cscode") + "',"
                        + "'" + _oraRs.getString("doccod") + "',"
                        + "'" + _oraRs.getString("docnum") + "',"
                        + "'" + _oraRs.getString("txndat") + "',"
                        + "'" + _oraRs.getString("itmcod") + "',"
                        + "'" + _oraRs.getString("itmdes") + "',"
                        + "'" + _oraRs.getString("penqty") + "',"
                        + "'" + _oraRs.getString("csname") + "'"
                        + ")";
                if (_mySqlstmnt.executeUpdate(_strmysqlqry) <= 0) {
                    throw new Exception("Error occured in insert into rdcustpenqty");
                }
            }
            closeStatement();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void update_cs_group_disc() {
        try {
            closeStatement();
            con = new OracleConnector("192.168.1.27", "rpd2", "MARKSYS", "best#for#my#home#123");
            mysql = new MysqlConnector("192.168.1.7", "redist", "tab", "tab123");
            _stroraqry = "select SBU_CODE, LOC_CODE, CUST_SUP, BUS_GROUP, ITEM_CODE,to_char(START_DATE,'YYYY-MM-DD') , "
                    + " to_char(END_DATE,'YYYY-MM-DD') , PRICE, DISC_PER, "
                    + " DISC_AMT, CRE_BY, to_char(CRE_DATE,'YYYY-MM-DD'), MOD_BY, to_char(MOD_DATE,'YYYY-MM-DD') from "
                    + " rms_cs_group_disc where sbu_code='830' and loc_code='60' ";


            _orastmnt = con.getConn().createStatement(ResultSet.TYPE_SCROLL_INSENSITIVE, ResultSet.CONCUR_READ_ONLY);
            _mySqlstmnt = mysql.getConn().createStatement(ResultSet.TYPE_SCROLL_INSENSITIVE, ResultSet.CONCUR_READ_ONLY);
            _oraRs = _orastmnt.executeQuery(_stroraqry);
            if (!_oraRs.next()) {
                throw new Exception("Empty Cs group Disc");
            }
            _mySqlstmnt.executeUpdate("delete from rms_cs_group_disc");
            _oraRs.beforeFirst();
            while (_oraRs.next()) {

                _strmysqlqry = "insert into rdcustpenqty(sbucod, loccod, cscode, doccod, docnum, txndat, itmcod, itmdes, penqty,csname) "
                        + " values("
                        + "'" + _oraRs.getString("sbucod") + "',"
                        + "'" + _oraRs.getString("loccod") + "',"
                        + "'" + _oraRs.getString("cscode") + "',"
                        + "'" + _oraRs.getString("doccod") + "',"
                        + "'" + _oraRs.getString("docnum") + "',"
                        + "'" + _oraRs.getString("txndat") + "',"
                        + "'" + _oraRs.getString("itmcod") + "',"
                        + "'" + _oraRs.getString("itmdes") + "',"
                        + "'" + _oraRs.getString("penqty") + "',"
                        + "'" + _oraRs.getString("csname") + "'"
                        + ")";
                if (_mySqlstmnt.executeUpdate(_strmysqlqry) <= 0) {
                    throw new Exception("Error occured in insert into rdcustpenqty");
                }
            }
            closeStatement();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void closeStatement() {
        try {
            if (_orastmnt != null) {
                _orastmnt.close();
                _orastmnt = null;
            }

            if (_mySqlstmnt != null) {
                _mySqlstmnt.close();
                _mySqlstmnt = null;
            }

            if (con.getConn() != null) {
                con.getConn().close();
                con = null;
            }
            if (mysql.getConn() != null) {
                mysql.getConn().close();
                mysql = null;
            }

        } catch (Exception e) {
        }
    }
}
