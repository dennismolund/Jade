package mode1719.student.ju.jade;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.Toast;

import com.facebook.Profile;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.Date;

public class RecyclerViewActivity extends AppCompatActivity {

    private Date date = new Date();
    public String city;
    public String facebookID = Profile.getCurrentProfile().getId();
    private static final String TAG = "RecyclerViewActivity";
    private ArrayList<Event> mEvents = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_show_event_list);
        getMainIntent();
        retrieveFromDatabase();

        final FloatingActionButton addButton = findViewById(R.id.floatingActionButton);
        addButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent addIntent = new Intent(RecyclerViewActivity.this, DetailEventViewActivity.class);
                addIntent.putExtra("value", 0);
                addIntent.putExtra("date", date.getTime());
                addIntent.putExtra("city", city);
                startActivity(addIntent);
            }});
    }

    //Get data from MainActivity
    private void getMainIntent(){
        Intent mainIntent = getIntent();
        date.setTime(mainIntent.getLongExtra("date", -1));
        city = mainIntent.getStringExtra("city");
        System.out.println("RecyclerView/getMainIntent: " + city);
    }

    private void initRecyclerView() {
        RecyclerView recyclerView = findViewById(R.id.recyclerView);
        RecyclerViewAdapter adapter = new RecyclerViewAdapter(mEvents, this);
        recyclerView.setAdapter(adapter);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));

    }

    private void retrieveFromDatabase(){
        DatabaseReference myRef = FirebaseDatabase.getInstance().getReference();
        System.out.println("RetrieveFromDatabase / city: " + city);
        myRef.child("Events").child(date.toString()).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                for (DataSnapshot eventSnap: dataSnapshot.getChildren()){
                    if(eventSnap.getValue(Event.class).getDate().toString().equals(date.toString())) {
                        System.out.println("In first if: " + city);
                        Event tempEvent = eventSnap.getValue(Event.class);
                        tempEvent.setKey(eventSnap.getKey());
                        if(tempEvent.getCity().equals(city) /*|| facebookID.equals(tempEvent.getOwnerID())*/){
                            mEvents.add(tempEvent);
                            System.out.println("In second if: " + mEvents.size());
                        }
                    }
                }
                initRecyclerView();
            }
            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                Toast toast = Toast.makeText(RecyclerViewActivity.this, getString(R.string.something_wrong) + databaseError.toString(), Toast.LENGTH_LONG);
                toast.show();
            }
        });
    }



}
