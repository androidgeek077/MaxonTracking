package app.mustansar.maxontracking.Models;

public class getLocationModel {

    public double getmLatitude() {
        return mLatitude;
    }

    public void setmLatitude(double mLatitude) {
        this.mLatitude = mLatitude;
    }

    public double getmLongitude() {
        return mLongitude;
    }

    public void setmLongitude(double mLongitude) {
        this.mLongitude = mLongitude;
    }



    double mLatitude;

    public getLocationModel() {
    }

    double mLongitude;

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }


    String name;
    String uID;

    public String getuID() {
        return uID;
    }

    public void setuID(String uID) {
        this.uID = uID;
    }

    public getLocationModel(double mLatitude, double mLongitude, String name) {
        this.mLatitude = mLatitude;
        this.mLongitude = mLongitude;
        this.name = name;
        this.uID = uID;
    }
}
