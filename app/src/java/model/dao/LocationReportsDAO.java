/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package model.dao;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Time;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.TreeMap;
import java.util.logging.Level;
import java.util.logging.Logger;
import model.entity.Group;
import model.entity.Location;
import model.entity.Student;
import model.utility.BreakdownUtility;
import model.utility.DBConnection;
import model.utility.GroupComparator;
import model.utility.LocationComparator;

/**
 *
 * @author shrey
 */
public class LocationReportsDAO {

    static Timestamp startDateTime;
    static Timestamp endDateTime;
    static Timestamp startDateTimeTwo;
    static Timestamp endDateTimeTwo;
    private BreakdownUtility bu;

    public LocationReportsDAO(Timestamp startDateTime, Timestamp endDateTime) {
        this.startDateTime = startDateTime;
        this.endDateTime = endDateTime;
        bu = new BreakdownUtility();
    }

    public LocationReportsDAO(Timestamp startDateTime, Timestamp endDateTime, Timestamp startDateTimeTwo, Timestamp endDateTimeTwo) {
        this.startDateTime = startDateTime;
        this.endDateTime = endDateTime;
        this.startDateTimeTwo = startDateTimeTwo;
        this.endDateTimeTwo = endDateTimeTwo;
        bu = new BreakdownUtility();
    }

    /**
     * Incomplete
     *
     * @param option1 
     * @param option2
     * @param option3
     */
    public void breakdownByYearAndGender(String option1, String option2, String option3) {
        Connection conn = null;
        PreparedStatement stmt = null;
        ResultSet rs = null;
        TreeMap<String, Student> studentMap = new TreeMap<>();

        //Getting Hashtable of all students in the SIS building during processing window
        try {
            conn = DBConnection.createConnection();
            stmt = conn.prepareStatement("select DISTINCT d.macaddress, name, password, email, gender "
                    + "from demograph d, location l, locationlookup llu "
                    + "where d.macaddress = l.macaddress and time >= ? and time < ? "
                    + "and l.locationid = llu.locationid");
            stmt.setTimestamp(1, startDateTime);
            stmt.setTimestamp(2, endDateTime);

            rs = stmt.executeQuery();

            while (rs.next()) {
                Student student = new Student(rs.getString("macAddress"), rs.getString("name"), rs.getString("email"), rs.getString("gender").charAt(0));
                studentMap.put(rs.getString("macAddress"), student);
            }

        } catch (SQLException ex) {
            Logger.getLogger(LocationReportsDAO.class.getName()).log(Level.SEVERE, "Unable to perform request", ex);
        } catch (ClassNotFoundException ex) {
            Logger.getLogger(LocationReportsDAO.class.getName()).log(Level.SEVERE, null, ex);
        } finally {
            DBConnection.close(conn, stmt, rs);
        }

        //Check which function to call
        if (option2.equals("none2") && option3.equals("none3")) {
            //calls one value function if only the first option is filled
            bu.percentageOneOption(option1, studentMap);
        } else if ((!option2.equals("none2") && option3.equals("none3"))) {
            //calls 2 option function
            bu.percentageTwoOptions(option1, option2, studentMap);
        } else if ((option2.equals("none2") && !option3.equals("none3"))) {
            bu.percentageTwoOptions(option1, option3, studentMap);
        } else {
            //calls 3 function option
            bu.percentageAllOptions(option1, option2, option3, studentMap);
        }
    }

    /**
     *
     * Returns the top-k most popular places within a processing window. The
     * popularity of a location is derived from the number of people located
     * there in the specified processing time window.
     *
     * @return topKList which contains top-k popular places in the selected
     * processing window as a LinkedHashMap. The key denotes the sematic place
     * and the value represents the number of people in the place within the
     * query window.
     */
    public LinkedHashMap<String, Integer> topkPopularPlaces() {
        Connection conn = null;
        PreparedStatement stmt = null;
        ResultSet rs = null;
        LinkedHashMap<String, Integer> topKList = new LinkedHashMap<>();
        try {
            conn = DBConnection.createConnection();
            stmt = conn.prepareStatement("select llu.semanticplace as 'semanticplace', count(t1.macadd) as 'noOfMacAdd' from \n"
                    + "(select macaddress as 'macadd', time as 'ts', locationid as 'locationid' from location \n"
                    + "where time >=? and time < ?) as t1\n"
                    + "inner join \n"
                    + "(select macaddress  as 'macadd2', max(time) as 'maxts' from location \n"
                    + "where time >= ? and time < ? \n"
                    + "group by macadd2) as t2\n"
                    + "on t1.macAdd = t2.macadd2 and t1.ts = t2.maxts right outer join locationlookup llu\n"
                    + "on t1.locationid = llu.locationid group by llu.semanticplace\n"
                    + "order by noOfMacAdd DESC;");
            //setting parameters
            stmt.setTimestamp(1, startDateTime);
            stmt.setTimestamp(2, endDateTime);
            stmt.setTimestamp(3, startDateTime);
            stmt.setTimestamp(4, endDateTime);
            rs = stmt.executeQuery();
            //generating result set
            while (rs.next()) {
                topKList.put(rs.getString("semanticplace"), rs.getInt("noOfMacAdd"));
            }

        } catch (SQLException ex) {
            Logger.getLogger(LocationReportsDAO.class.getName()).log(Level.SEVERE, "Unable to perform request", ex);
        } catch (ClassNotFoundException ex) {
            Logger.getLogger(LocationReportsDAO.class.getName()).log(Level.SEVERE, null, ex);
        } finally {
            DBConnection.close(conn, stmt, rs);
        }
        //returns LinkedHashMap
        return topKList;
    }

    /**
     * Returns the top-k most companions within a processing window.The student
     * list is derived from the number of people co-located with a specific user
     * in the specified processing time window.
     *
     * @param k the number of companions
     * @param studentMac student's unique macaddress
     * @return stList List of student objects based on the number specified
     */
    public HashMap<Integer, Group> topkCompanions(int k, String studentMac) {
        HashMap<Integer, Group> stList = new HashMap<>();
        StudentDAO sDAO = new StudentDAO();
        TreeMap<String, Student> sMap = sDAO.getAllStudentsWithinProcessingWindow(startDateTime, endDateTime);
        Student s = sMap.get(studentMac);
        if (s != null) {
            try {
                sDAO.importDataFromDatabase(sMap, startDateTime, endDateTime);
                //gets groups of two
                ArrayList<Group> studentGroup = sDAO.getStudentGroups(s);
                //sort groups by their duration (Highest to lowest)
                Collections.sort(studentGroup, new GroupComparator());

                //merge the groups if they have common total duration (regardless of location)
                studentGroup = sDAO.mergeGroups(studentGroup);
                int i = 1;
                for (Group g : studentGroup) {
                    if (i <= k) {
                        stList.put(i, g);
                        i++;
                    } else {
                        continue;
                    }
                }

            } catch (SQLException ex) {
                Logger.getLogger(LocationReportsDAO.class.getName()).log(Level.SEVERE, null, ex);
            } catch (ClassNotFoundException ex) {
                Logger.getLogger(LocationReportsDAO.class.getName()).log(Level.SEVERE, null, ex);
            }
        }

        return stList;
    }

    /**
     *
     * Returns the top-k next places within a processing window. The popularity
     * of a location is derived from the number of people likely to visit there
     * in the specified processing time window.
     *
     * @param milliSeconds to get the sql Timestamp
     * @return topKPlacesList a list of the top-k popular places in the selected
     * processing window
     */
    public static ArrayList<ArrayList<String>> topkNextPlaces() {
        Connection conn = null;
        PreparedStatement stmt = null;
        ResultSet rs = null;
        ArrayList<ArrayList<String>> peopleInNextTimeWindow = new ArrayList<>();
        String test = "";
        try {
            conn = DBConnection.createConnection();
            //selects macaddress from location within time interval and time window, and semantic place filtering by semanticplace
            stmt = conn.prepareStatement("select macaddress, semanticplace from \n"
                    + "(select time, macaddress,location.locationid, llu.semanticplace from location left outer join locationlookup llu on location.locationid = llu.locationid) as t\n"
                    + "where time >= ? and time < ?\n"
                    + "group by macaddress, semanticplace\n"
                    + "having min(time) <= date_sub(max(time), interval 5 minute)\n"
                    + "order by macaddress;");
            //set params
            stmt.setTimestamp(1, startDateTimeTwo);
            stmt.setTimestamp(2, endDateTimeTwo);
            //execution of query
            rs = stmt.executeQuery();

            while (rs.next()) {
                //gets semanticplace and macaddress from result set
                String querySemanticPlace = rs.getString("semanticplace");
                String macAddress = rs.getString("macaddress");
                ArrayList<String> stringArray = new ArrayList<>();
                stringArray.add(querySemanticPlace);
                stringArray.add(macAddress);
                peopleInNextTimeWindow.add(stringArray);
            }

        } catch (SQLException ex) {
            Logger.getLogger(LocationReportsDAO.class.getName()).log(Level.SEVERE, "Unable to perform request", ex);
        } catch (ClassNotFoundException ex) {
            Logger.getLogger(LocationReportsDAO.class.getName()).log(Level.SEVERE, null, ex);
        } finally {
            DBConnection.close(conn, stmt, rs);
        }
        return peopleInNextTimeWindow;
        //return peopleInNextTimeWindow;
    }

    /**
     * Returns the macaddresses within the time window that are within the
     * specified semantic place. The macaddresses are obtained from the data
     * within location.csv that matches with the specified processing time
     * window.
     *
     * @param semanticPlace A String value of the semantic place of choice
     * @return addressList as an ArrayList of macaddresss that appear in the
     * location within the specified processing window.
     */
    public static ArrayList<String> peopleInSemanticPlace(String semanticPlace) {
        Connection conn = null;
        PreparedStatement stmt = null;
        ResultSet rs = null;
        ArrayList<String> addressList = new ArrayList<>();
        try {
            conn = DBConnection.createConnection();
            stmt = conn.prepareStatement("select distinct macaddress from location,"
                    + " locationlookup where time >= ? and time < ? "
                    + " and location.locationid = locationlookup.locationid"
                    + " and semanticplace like ?;");
            stmt.setTimestamp(1, startDateTime);
            stmt.setTimestamp(2, endDateTime);
            stmt.setString(3, semanticPlace);
            rs = stmt.executeQuery();
            while (rs.next()) {
                addressList.add(rs.getString("macaddress"));
            }

        } catch (SQLException ex) {
            Logger.getLogger(LocationReportsDAO.class.getName()).log(Level.SEVERE, "Unable to perform request", ex);
        } catch (ClassNotFoundException ex) {
            Logger.getLogger(LocationReportsDAO.class.getName()).log(Level.SEVERE, null, ex);
        } finally {
            DBConnection.close(conn, stmt, rs);
        }
        return addressList;
    }

}
