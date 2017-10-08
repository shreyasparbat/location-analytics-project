package model.dao;

import model.entity.Location;
import java.util.*;

/**
 *  Allows the application to access the database and execute CRUD Functions
 * involved in the use of Location Objects
 * @author Joel Tay
 */
public class LocationDAO{
    //attributes
    private ArrayList<Location> locationList;
    
    //constructors

    /**
     * Initializes a newly created LocationDAO object
     */
    public LocationDAO(){
        locationList = new ArrayList<Location>();
         // hard copy user input
        locationList.add(new Location("test1", 123 ));
        locationList.add(new Location("test1", 1256 ));
        locationList.add(new Location("test2", 1223 ));
    }
    
    //methods
    //returns a specific semanticLocation

    /**
     * Returns the name of the semantic location based on the location I.D.
     * @param id int of the location id 
     * @return String of the name of the semantic location if found. Else return null
     */
    public String getSpecificSemanticLocation(int id){
        
        for(Location l: locationList){
            if(l.getLocationId() == id){
                return l.getSemanticPlace();
            }
        }

        return null;
    }
    
    

    /**
     * Returns the entire list of available locations
     * @return ArrayList of Location
     */
    public ArrayList<Location> getAllLocation(){
        return locationList;
    }
    
    //returns 

    /**
     * Returns all location objects of a semanticLocation
     * @param semanticLocation the string of the semantic location 
     * @return ArrayList of Location 
     */
    public ArrayList<Location> getLocation(String semanticLocation){
        ArrayList<Location> semanticList = new ArrayList<>();
        for(Location l : locationList){
            if(l.getSemanticPlace().equals(semanticLocation)){
                semanticList.add(l);
            }
        }
        return semanticList;
    }
    
}