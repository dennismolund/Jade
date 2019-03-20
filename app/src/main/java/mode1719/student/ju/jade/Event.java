package mode1719.student.ju.jade;

import android.os.Parcel;
import android.os.Parcelable;
import android.widget.ImageView;

import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.io.File;
import java.security.Key;
import java.util.ArrayList;
import java.util.Date;

public class Event implements Parcelable {
    private Date date;
    private String title;
    private String description;
    private String time;
    private String imageUrl;
    private String creator;
    private String ownerID;
    private String key;

    public Event(){}

    public Event(Date date, String title, String description, String time, String imageUrl, String creator, String ownerID){
        this.date = date;
        this.title = title;
        this.description = description;
        this.time = time;
        this.imageUrl = imageUrl;
        this.creator = creator;
        this.ownerID = ownerID;
    }

    protected Event(Parcel in) {
        title = in.readString();
        description = in.readString();
        time = in.readString();
        imageUrl = in.readString();
        creator = in.readString();
        ownerID = in.readString();
        key = in.readString();
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(title);
        dest.writeString(description);
        dest.writeString(time);
        dest.writeString(imageUrl);
        dest.writeString(creator);
        dest.writeString(ownerID);
        dest.writeString(key);
    }

    @Override
    public int describeContents() {
        return 0;
    }

    public static final Creator<Event> CREATOR = new Creator<Event>() {
        @Override
        public Event createFromParcel(Parcel in) {
            return new Event(in);
        }

        @Override
        public Event[] newArray(int size) {
            return new Event[size];
        }
    };

    public Date getDate() { return date; }

    public void setDate(Date date) { this.date = date; }

    public String getTitle() { return title; }

    public void setTitle(String title) { this.title = title; }

    public String getDescription() { return description; }

    public void setDescription(String description) { this.description = description; }

    public String getTime() { return time; }

    public void setTime(String time) { this.time = time; }

    public String getImageUrl() { return imageUrl; }

    public void setImageUrl(String imageUrl) { this.imageUrl = imageUrl; }

    public String getCreator() { return creator; }

    public void setCreator(String creator) { this.creator = creator; }

    public String getOwnerID(){ return  ownerID; }

    public void setOwnerID(String ownerID) { this.ownerID = ownerID; }

    public String getKey(){ return key; }

    public void setKey(String key){ this.key = key; }

    public void addToDatabase(){
        System.out.println("Event / addToDatabase - " + this.imageUrl);
        DatabaseReference myRef = FirebaseDatabase.getInstance().getReference();
        myRef.child("Events").child(this.getDate().toString()).push().setValue(this);
    }
    public void removeFromDatabase(){
        DatabaseReference myRef = FirebaseDatabase.getInstance().getReference();
        myRef.child("Events").child(this.getDate().toString()).removeValue();
    }

}
