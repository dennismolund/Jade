package mode1719.student.ju.jade;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.View;
import android.widget.Toast;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.Date;

public class RecyclerViewActivity extends AppCompatActivity {

    private Date _date = new Date();
    private String city = "";

    private static final String TAG = "RecyclerViewActivity";
    private ArrayList<Event> mEvents = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_show_event_list);
        Log.d(TAG, "onCreate: ");
        getMainIntent();
        retrieveFromDatabase();

        final FloatingActionButton addButton = findViewById(R.id.floatingActionButton);
        addButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent addIntent = new Intent(RecyclerViewActivity.this, DetailEventViewActivity.class);
                addIntent.putExtra("value", 0);
                addIntent.putExtra("date", _date.getTime());
                addIntent.putExtra("city", city);
                startActivity(addIntent);
            }});
    }

    //Get data from MainActivity
    private void getMainIntent(){
        Intent mainIntent = getIntent();
        _date.setTime(mainIntent.getLongExtra("date", -1));
        city = mainIntent.getStringExtra("city");
    }

    private void initRecyclerView() {
        Log.d(TAG, "initRecyclerView: " + city);
        RecyclerView recyclerView = findViewById(R.id.recyclerView);
        RecyclerViewAdapter adapter = new RecyclerViewAdapter(mEvents, this, this.city);
        recyclerView.setAdapter(adapter);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));

    }
    private void retrieveFromDatabase(){
        DatabaseReference myRef = FirebaseDatabase.getInstance().getReference();
        myRef.child("Events").child(_date.toString()).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                for (DataSnapshot eventSnap: dataSnapshot.getChildren()){
                    if(eventSnap.getValue(Event.class).getDate().getTime() == _date.getTime()) {
                        Event tempEvent = eventSnap.getValue(Event.class);
                        tempEvent.setKey(eventSnap.getKey());
                        mEvents.add(tempEvent);
                    }
                }
                initRecyclerView();
                mEvents = new ArrayList<Event>();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                Toast toast = Toast.makeText(RecyclerViewActivity.this, "Ops something went wrong:" + databaseError.toString(), Toast.LENGTH_LONG);
                toast.show();
            }
        });
    }



}
