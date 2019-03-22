package mode1719.student.ju.jade;

import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.location.LocationManager;
import android.os.Build;
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

import java.io.IOException;
import java.sql.SQLOutput;
import java.text.DateFormat;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Locale;

import javax.net.ssl.ManagerFactoryParameters;

public class MainActivity extends AppCompatActivity {

    public ArrayList<Date> dateForEvents = new ArrayList<>();
    public String city;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        setCity();

        CalendarPickerView datePicker = getDatePicker();
        highlightDatesForEvents(datePicker);
        pickDate(datePicker);

    }
    private void setCity(){
        if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.M &&
                checkSelfPermission(Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED){
            requestPermissions(new String[] {Manifest.permission.ACCESS_COARSE_LOCATION}, 1000);
        }
        else{
            System.out.println("Else");
            LocationManager locationManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
            Location location = locationManager.getLastKnownLocation(LocationManager.NETWORK_PROVIDER);
            city = myLocation(location.getLatitude(), location.getLongitude());
            System.out.println(city);
        }
    }

    /*
    *** Try statements has same code as in setCity, but couldn't really get around it without making new permission
    *** requests which seemed worse. Making it a function made it ask for permissions again.
     */
    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        switch (requestCode){
            case 1000:
                try{
                    LocationManager locationManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
                    Location location = locationManager.getLastKnownLocation(LocationManager.NETWORK_PROVIDER);
                    city = myLocation(location.getLatitude(), location.getLongitude());
                    System.out.println("onRequestPermission city: " + city);
                }
                catch (SecurityException e){
                    e.printStackTrace();
                }
        }
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

    private String myLocation(double lat, double lon){
        String city = "";
        Geocoder gecoder = new Geocoder(this, Locale.getDefault());
        List<Address> addresses;
        try{
            addresses = gecoder.getFromLocation(lat, lon, 10);
            if (addresses.size() > 0){
                for (Address adr: addresses){
                    if(adr.getLocality() != null && adr.getLocality().length() > 0){
                        city = adr.getLocality();
                        break;
                    }
                }
            }
        }
        catch(IOException e){
            e.printStackTrace();
        }
        System.out.println("myLocation: " + city);
        return city;
    }

    private void pickDate(CalendarPickerView datePicker){
        datePicker.setOnDateSelectedListener(new CalendarPickerView.OnDateSelectedListener() {
            @Override
            public void onDateSelected(Date date) {
                Intent intent = new Intent(MainActivity.this, RecyclerViewActivity.class);
                intent.putExtra("date", date.getTime());
                intent.putExtra("city", city);
                startActivity(intent);
            }
            public void onDateUnselected(Date date) { }
        });
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