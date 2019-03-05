package mode1719.student.ju.jade;

import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.Date;

public class Event {
    private Date date;
    private String title;
    private String description;
    private String time;
    private String imageUrl;
    private String creator;

    public Event(){}

    public Event(String title, String imageUrl){
        this.title = title;
        this.imageUrl = imageUrl;
    }

    public Event(Date date, String title, String description, String time, String imageUrl, String creator){
        this.date = date;
        this.title = title;
        this.description = description;
        this.time = time;
        this.imageUrl = imageUrl;
        this.creator = creator;
    }

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

    public void addToDatabase(Event event){
        DatabaseReference myRef = FirebaseDatabase.getInstance().getReference();
        myRef.push().setValue(event);
    }

    @Override
    public String toString(){ return "Title: " + title + "\n" + "Time: " + time + "\n" + "Description: " + description;}

}
