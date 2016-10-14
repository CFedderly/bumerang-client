package com.seng480b.bumerang;

//import android.os.Parcelable;
//import android.os.Parcel;

//import java.io.Serializable;

/**
 * Created by mavis on 2016-10-07.
 */

//could also use Parcelable - if the object is big (or has 1000s of uses) it'll be faster/uses less resources...
// but Serializable a lot simpler

public class DummyRequest {  //implements Serializable{
    //private static final long serialVersionUID = ???
    private String name_of_borrower = "unknown";
    private String title_of_request = "unknown";
    private int number_of_views = 0;
    private int number_of_pending_accepts = 0;

    public DummyRequest(String name,String title){
        this.name_of_borrower = name;
        this.title_of_request = title;
    }

    public void setNumberViews(int views){
        this.number_of_views = views;
    }

    public void setNumberPendingRequests(int num_pending){
        this.number_of_pending_accepts = num_pending;
    }

    public String getNameOfBorrower(){
        return this.name_of_borrower;
    }

    public String getTitleOfRequest() { return this.title_of_request; }

}
