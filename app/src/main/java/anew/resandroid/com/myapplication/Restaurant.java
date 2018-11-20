package anew.resandroid.com.myapplication;

import android.location.Location;
import android.os.Parcel;
import android.os.Parcelable;
import android.util.Log;

import com.google.android.gms.location.places.Place;

public class Restaurant implements ILocation, Parcelable {

    private String restaurantName;
    private String phoneNumber;
    private String pricePoint;
    private String address;
    private float rating;
    Boolean liked = null;

    Location location = null;

    public Restaurant(Place p)
    {
        restaurantName = p.getName().toString();
        phoneNumber = p.getPhoneNumber().toString();
        address = p.getAddress().toString();
        rating = p.getRating();
        makePricePointString(p.getPriceLevel());
    }

    protected Restaurant(Parcel in) {
        restaurantName = in.readString();
        phoneNumber = in.readString();
        pricePoint = in.readString();
        address = in.readString();
        rating = in.readFloat();
        byte tmpLiked = in.readByte();
        liked = tmpLiked == 0 ? null : tmpLiked == 1;
        location = in.readParcelable(Location.class.getClassLoader());
    }

    public static final Creator<Restaurant> CREATOR = new Creator<Restaurant>() {
        @Override
        public Restaurant createFromParcel(Parcel in) {
            return new Restaurant(in);
        }

        @Override
        public Restaurant[] newArray(int size) {
            return new Restaurant[size];
        }
    };

    @Override
    public void findLocation() {

    }

    @Override
    public void setLocation(Location l) {
        this.location = l;

    }

    @Override
    public Location getLocation() {
        return this.location;
    }

    private void makePricePointString(int price)
    {
        StringBuilder priceString = new StringBuilder();
        for(int i = 0; i < price; i++){
            priceString.append("$");
        }

        this.pricePoint = priceString.toString();
    }

    public String getAddress() {
        return address;
    }

    public float getRating() {
        return rating;
    }

    public String getRestaurantName() {
        return restaurantName;
    }

    public String getPhoneNumber() { return phoneNumber; }

    public String getPricePoint() {
        return pricePoint;
    }

    public void doGreenButton(){
        Log.v("Button", this.getRestaurantName() + "GREEN");
    }

    public void doRedButton(){
        Log.v("Button", this.getRestaurantName() + "RED");
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(restaurantName);
        dest.writeString(phoneNumber);
        dest.writeString(pricePoint);
        dest.writeString(address);
        dest.writeFloat(rating);
        dest.writeByte((byte) (liked == null ? 0 : liked ? 1 : 2));
        dest.writeParcelable(location, flags);
    }
}
