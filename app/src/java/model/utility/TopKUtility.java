/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package model.utility;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
import static model.utility.DBConnection.createConnection;

/**
 *
 * @author Ming Xuan
 */
public class TopKUtility {
    
    public static List<String[]> getStudentMacAddress(){
        List<String[]> studentMA = new ArrayList<>();
        try {
            Connection conn = DBConnection.createConnection();
            PreparedStatement stmt = conn.prepareStatement("select distinct macaddress,name from demograph limit 10000;");
            ResultSet rs = stmt.executeQuery();
            while(rs.next()){
                String[] row = new String[]{rs.getString(1),rs.getString(2)};
                studentMA.add(row);
            }
            DBConnection.close(conn, stmt, rs);
        } catch (SQLException ex) {
            Logger.getLogger(DBConnection.class.getName()).log(Level.SEVERE, null, ex);
        } catch (ClassNotFoundException ex) {
            Logger.getLogger(DBConnection.class.getName()).log(Level.SEVERE, null, ex);
        }
        return studentMA;
    }
    
     public static List<String> getSemanticPlaces(){
        List<String> semanticPlaces = new ArrayList<>();
        try {
            Connection conn = DBConnection.createConnection();
            PreparedStatement stmt = conn.prepareStatement("select distinct semanticplace from locationlookup limit 10000;");
            ResultSet rs = stmt.executeQuery();
            while(rs.next()){
                semanticPlaces.add(rs.getString(1));
            }
            DBConnection.close(conn, stmt, rs);
        } catch (SQLException ex) {
            Logger.getLogger(DBConnection.class.getName()).log(Level.SEVERE, null, ex);
        } catch (ClassNotFoundException ex) {
            Logger.getLogger(DBConnection.class.getName()).log(Level.SEVERE, null, ex);
        }
        return semanticPlaces;
    }
    
}