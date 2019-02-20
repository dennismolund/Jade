package mode1719.student.ju.jade;

import java.util.ArrayList;
import java.util.Date;

public class Data {
    public static ArrayList<Events> eventItem = new ArrayList<>();

    static{
        Date testDate = new Date();
        eventItem.add(new Events(testDate, "Title", "hej", "hej"));
    }

    public static class Events{
        public Date date;
        public String title;
        public String description;
        public String time;


        public Events(Date date, String title, String time , String description){
            this.date = date;
            this.title = title;
            this.description = description;
            this.time = time;
        }
        @Override
        public String toString(){ return "Title: " + title + "\n" + "Time: " + time + "\n" + "Description: " + description;}

    }
}
