/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package model.entity;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;

/**
 *
 * @author Amanda
 */
public class Group {
    private ArrayList<Student> sGroup;
    private HashMap<Integer,TimeIntervalsList> locList;
    
    /**
     *default constructor for Group Object
     * A group object is a list of students and theirs location timings 
     */
    public Group(){
        sGroup = new ArrayList<>();
        locList = new HashMap<>();
    }
    
    /**
     * Adds a new student into the group
     * @param s student to be added in 
     */
    public void addStudent(Student s){
        if (!sGroup.contains(s)){
            sGroup.add(s);
        }
    }
    
    /**
     * Sets a list of students in the group
     * @param sList list of students 
     */
    public void setStudents(ArrayList<Student> sList){
        sGroup = sList;
    }
    
    /**
     *  Sets a new record list to the group details
     * @param locaList list of location records entered to replace the existing location records 
     */
    public void setLocaList(HashMap<Integer,TimeIntervalsList> locaList){
        locList = locaList;
    }
    
    /**
     * Adds a new location with time interval lists into the group details
     * @param locationID locationID of the semantic place
     * @param list records of the time intervals 
     */
    public void addLocation(int locationID, TimeIntervalsList list){
        locList.put(locationID, list);
    }
    
    /**
     * Returns the list of students in the group
     * @return an arraylist of students 
     */
    public ArrayList<Student> getGroup(){
        return sGroup;
    }
    
    /**
     * Returns the location records of the group
     * @return a HashMap of location records where key is locationID and value is the list of time intervals
     */
    public HashMap<Integer,TimeIntervalsList> getRecord(){
        return locList;
    }
    
    /**
     * Checks whether group contains minimally one member from another group
     * @param g Another group list
     * @return true if at least one member is presented in both groups, false if otherwise
     */
    public boolean contains(Group g){
        for(Student s : sGroup){
            for(Student s1 : g.sGroup){
                if(s.equals(s1)){
                    return true;
                }
            }
        }
        return false;
    }
    
    /**
     * Adds students of another group into the current group. Group size doesnt increase if group is a subset of current group
     * @param g group of students 
     */
    public void addGroup(Group g){
        for(Student s : g.sGroup){
            addStudent(s);
        }
    }
    
    /**
     * Returns the total duration of a given group
     * @return the total duration (seconds) in double format
     */
    public double getTotalDuration(){
        Iterator<Integer> iter = locList.keySet().iterator();
        double duration =0;
        while(iter.hasNext()){
            duration += locList.get(iter.next()).getDuration();
        }
        return duration;
    }
    
    /**
     *Gets a list of other student in a group excluding the input student 
     * @param s Student object of a particular student
     * @return the rest of the group members
     */
    public List<Student> getOtherStudentsInGroup(Student s){
        List<Student> toReturn = new ArrayList<>();
        for(Student stu : sGroup){
            if(!stu.equals(s)){
                toReturn.add(stu);
            }
        }
        return toReturn;
    }
    
    /**
     * Gets a list of other student in a group excluding the input student 
     * @param macAddress Unique macaddress of a particular student you want to exclude
     * @return the rest of the group members
     */
    public List<Student> getOtherStudentsInGroup(String macAddress){
        List<Student> toReturn = new ArrayList<>();
        for(Student stu : sGroup){
            if(!stu.getMacAddress().equals(macAddress)){
                toReturn.add(stu);
            }
        }
        return toReturn;
    }
}
