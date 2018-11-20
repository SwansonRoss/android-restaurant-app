package anew.resandroid.com.myapplication;

import android.location.Location;

public interface ILocation {

    //Find location from gps data
    void findLocation();

    //set location
    void setLocation(Location location);

    //returns location
    Location getLocation();

}
