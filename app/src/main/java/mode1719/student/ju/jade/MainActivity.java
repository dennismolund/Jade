package mode1719.student.ju.jade;

import android.Manifest;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.text.InputType;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.squareup.timessquare.CalendarPickerView;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Locale;

public class MainActivity extends AppCompatActivity {

    public ArrayList<Date> dateForEvents = new ArrayList<>();
    public String city;
    public TextView cityName;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        checkGPSPermission();
        CalendarPickerView datePicker = getDatePicker();
        fetchDatesWithEvents(datePicker);
        pickDate(datePicker);

    }

    @Override
    protected void onResume() {
        dateForEvents.clear();
        CalendarPickerView datePicker = getDatePicker();
        datePicker.clearHighlightedDates();
        fetchDatesWithEvents(datePicker);

        super.onResume();
    }

    //Checks if the user have given permission to the app to access location.
    private void checkGPSPermission(){
        if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.M &&
                checkSelfPermission(Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED &&
                checkSelfPermission(Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED){
            requestPermissions(new String[] {Manifest.permission.ACCESS_COARSE_LOCATION, Manifest.permission.ACCESS_FINE_LOCATION}, 1000);
        }
        else{
            getLocationCoordinates();
        }
    }

    // If the app gets null from the GPS_PROVIDER user can enter wich city they are using the app in.
    private void enterCityManually(){
        if(city == null) {
            AlertDialog.Builder builder = new AlertDialog.Builder(this);
            builder.setTitle(R.string.enter_location_message);
            final EditText input = new EditText(this);
            input.setInputType(InputType.TYPE_TEXT_FLAG_CAP_WORDS);
            builder.setView(input);
            builder.setPositiveButton(R.string.Done, new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    city = input.getText().toString();
                    cityName.setText(city);
                }
            }).show();
        }
    }

    // Looks for location updates and last known location.
    private void getLocationCoordinates(){
        cityName = findViewById(R.id.cityName);
        try{
            LocationManager locationManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
            locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 100, 0, new LocationListener() {
                @Override
                public void onLocationChanged(Location location) {
                    try{
                        city = getLocation(location.getLatitude(), location.getLongitude());
                        cityName.setText(city);
                    }
                    catch (NullPointerException e){
                        e.printStackTrace();
                        enterCityManually();
                    }
                    catch (SecurityException e){
                        e.printStackTrace();
                        enterCityManually();
                    }
                }

                @Override
                public void onStatusChanged(String provider, int status, Bundle extras) {
                }
                @Override
                public void onProviderEnabled(String provider) {
                }
                @Override
                public void onProviderDisabled(String provider) {
                    enterCityManually();
                }
            });
            Location location = locationManager.getLastKnownLocation(LocationManager.GPS_PROVIDER);
            city = getLocation(location.getLatitude(), location.getLongitude());
        }
        catch (SecurityException e){
            e.printStackTrace();
            enterCityManually();
        }
        catch (NullPointerException e){
            e.printStackTrace();
            enterCityManually();
        }
    }

    // Catches result from user after asking for location permission.
    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        switch (requestCode){
            case 1000:
                getLocationCoordinates();
        }
    }

    // Returns the users location as a string.
    private String getLocation(double lat, double lon){
        String location = new String();
        Geocoder geocoder = new Geocoder(this, Locale.getDefault());
        List<Address> addresses;
        try{
            addresses = geocoder.getFromLocation(lat, lon, 10);
            if (addresses.size() > 0){
                for (Address adr: addresses){
                    if(adr.getLocality() != null && adr.getLocality().length() > 0){
                        location = adr.getLocality();
                        break;
                    }
                }
            }
        }
        catch(IOException e){
            e.printStackTrace();
        }
        return location;
    }

    // Calls the database for the dates that has events.
    private void fetchDatesWithEvents(final CalendarPickerView datePicker){
        final DatabaseReference myRef = FirebaseDatabase.getInstance().getReference();
        myRef.child("Events").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                for (DataSnapshot eventSnap: dataSnapshot.getChildren()){
                    setUpDatesForEvents(eventSnap, myRef);
                }
                datePicker.highlightDates(dateForEvents);
                dateForEvents.clear();
            }
            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                Toast toast = Toast.makeText(MainActivity.this,
                        R.string.something_wrong + databaseError.toString(), Toast.LENGTH_LONG);
                toast.show();
            }
        });
    }

    // Removes past dates and their children from the database and lists the remaining dates.
    private void setUpDatesForEvents(DataSnapshot eventSnap, DatabaseReference myRef){
        final Date yesterday = iterateDateFromToday(-1);
        Date tempDate = new Date(eventSnap.getKey());

        if(tempDate.before(yesterday)){
            myRef.child(eventSnap.getKey()).removeValue();

        }
        else {
            dateForEvents.add(new Date(eventSnap.getKey()));
        }
    }

    // Sets up and returns the calendar picker view.
    private CalendarPickerView getDatePicker(){
        Date currentDay = new Date();
        Calendar nextYear = Calendar.getInstance();
        nextYear.add(Calendar.YEAR, 3);
        //Display Calendar
        CalendarPickerView datePicker = findViewById(R.id.calendarView);
        datePicker.init(currentDay, nextYear.getTime()).withSelectedDate(currentDay);
        return datePicker;
    }

    // Makes calendar dates clickable.
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

    // Returns the date i days from today.
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