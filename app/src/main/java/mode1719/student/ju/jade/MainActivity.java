package mode1719.student.ju.jade;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.widget.Toast;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.squareup.timessquare.CalendarPickerView;

import java.sql.SQLOutput;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        ArrayList<Date> dateList = retrieveFromDatabase();
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        Date currentDay = new Date();
        Calendar nextYear = Calendar.getInstance();
        nextYear.add(Calendar.YEAR, 3);
        //Display Calendar
        CalendarPickerView datePicker = findViewById(R.id.calendarView);
        datePicker.init(currentDay, nextYear.getTime()).withSelectedDate(currentDay);


        datePicker.setOnDateSelectedListener(new CalendarPickerView.OnDateSelectedListener() {
            @Override
            public void onDateSelected(Date date) {
                Intent intent = new Intent(MainActivity.this, RecyclerViewActivity.class);
                intent.putExtra("date", date.getTime());
                //date = Fri Feb 08 00:00:00 GMT+01:00 2019
                startActivity(intent);
            }


            public void onDateUnselected(Date date) {

            }
        });



        System.out.println(dateList.size() + "DEBUG3");
        datePicker.highlightDates(dateList);
    }

    private ArrayList<Date> retrieveFromDatabase(){
        DatabaseReference myRef = FirebaseDatabase.getInstance().getReference();
        //Array that consist of dates that have an Event.
        final ArrayList<Date> dateForEvents = new ArrayList<>();
        //Highlight dates that have events.

        myRef.child("Events").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                for (DataSnapshot eventSnap: dataSnapshot.getChildren()){
                    System.out.println(dateForEvents.size() + "DebugA");
                    dateForEvents.add(eventSnap.getValue(Event.class).getDate());
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                Toast toast = Toast.makeText(MainActivity.this, "Ops something went wrong:" + databaseError.toString(), Toast.LENGTH_LONG);
                toast.show();
            }
        });
        System.out.println(dateForEvents.size() + "DebugB");
        return dateForEvents;


    }



}