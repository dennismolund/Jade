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
import java.text.DateFormat;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;

public class MainActivity extends AppCompatActivity {

    public ArrayList<Date> dateForEvents = new ArrayList<>();
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        CalendarPickerView datePicker = getDatePicker();
        highlightDatesForEvents(datePicker);

        datePicker.setOnDateSelectedListener(new CalendarPickerView.OnDateSelectedListener() {
            @Override
            public void onDateSelected(Date date) {
                Intent intent = new Intent(MainActivity.this, RecyclerViewActivity.class);
                intent.putExtra("date", date.getTime());
                startActivity(intent);
            }
            public void onDateUnselected(Date date) { }
        });
    }

    @Override
    protected void onResume() {
        dateForEvents.clear();
        CalendarPickerView datePicker = getDatePicker();
        datePicker.clearHighlightedDates();
        System.out.println("onResume");
        highlightDatesForEvents(datePicker);

        super.onResume();
    }

    private void highlightDatesForEvents (final CalendarPickerView datePicker){
        final DatabaseReference myRef = FirebaseDatabase.getInstance().getReference();

        myRef.child("Events").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                for (DataSnapshot eventSnap: dataSnapshot.getChildren()){
                    getDateForEvents(eventSnap, myRef);
                }
                datePicker.highlightDates(dateForEvents);
            }
            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                Toast toast = Toast.makeText(MainActivity.this,
                        "Ops something went wrong:" + databaseError.toString(), Toast.LENGTH_LONG);
                toast.show();
            }
        });
    }

    private CalendarPickerView getDatePicker(){
        Date currentDay = new Date();
        Calendar nextYear = Calendar.getInstance();
        nextYear.add(Calendar.YEAR, 3);
        //Display Calendar
        CalendarPickerView datePicker = findViewById(R.id.calendarView);
        datePicker.init(currentDay, nextYear.getTime()).withSelectedDate(currentDay);
        return datePicker;
    }

    private ArrayList<Date> getDateForEvents(DataSnapshot eventSnap, DatabaseReference myRef){
        final Date yesterday = iterateDateFromToday(-1);
        Date tempDate = new Date(eventSnap.getKey());

        if(tempDate.before(yesterday)){
            System.out.println("Removing child " + tempDate.toString());
            myRef.child("Events").child(eventSnap.getKey()).removeValue();
        }
        else {
            dateForEvents.add(new Date(eventSnap.getKey()));
            System.out.println(dateForEvents.size());
        }
        return dateForEvents;
    }

    private Date iterateDateFromToday(int i){
        Date date = new Date();
        Calendar c = Calendar.getInstance();
        c.add(Calendar.DATE, i);
        date = c.getTime();
        date.setHours(0);
        date.setMinutes(0);
        date.setSeconds(0);
        return date;
    }
}