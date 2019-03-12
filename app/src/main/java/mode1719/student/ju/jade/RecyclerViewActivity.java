package mode1719.student.ju.jade;

import android.app.Dialog;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.ArrayList;
import java.util.Date;

public class RecyclerViewActivity extends AppCompatActivity {

    private Date _date = new Date();

    private static final String TAG = "RecyclerViewActivity";
    private ArrayList<Event> mEvents = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_show_event_list);
        Log.d(TAG, "onCreate: ");
        getMainIntent();
        initEventList();

        final Button addButton = findViewById(R.id.addEventButton);
        addButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent addIntent = new Intent(RecyclerViewActivity.this, ShowEvent.class);
                addIntent.putExtra("value", 0);
                addIntent.putExtra("date", _date.getTime());
                startActivity(addIntent);
            }});


    }

    private void getMainIntent(){
        //get Data
        Intent mainIntent = getIntent();
        _date.setTime(mainIntent.getLongExtra("date", -1));
    }




    private void initRecyclerView() {
        Log.d(TAG, "initRecyclerView: ");
        RecyclerView recyclerView = findViewById(R.id.recyclerView);
        RecyclerViewAdapter adapter = new RecyclerViewAdapter(mEvents, this);
        recyclerView.setAdapter(adapter);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));

    }

    private void initEventList() {

        mEvents.add(new Event("Havasu Falls", "https://c1.staticflickr.com/5/4636/25316407448_de5fbf183d_o.jpg"));
        mEvents.add(new Event("Trondheim", "https://i.redd.it/tpsnoz5bzo501.jpg"));
        mEvents.add(new Event("Portugal", "https://i.redd.it/qn7f9oqu7o501.jpg"));
        mEvents.add(new Event("Rocky Mountain National Park", "https://i.redd.it/j6myfqglup501.jpg"));
        mEvents.add(new Event("Mahahual", "https://i.redd.it/0h2gm1ix6p501.jpg"));
        mEvents.add(new Event("Frozen Lake", "https://i.redd.it/k98uzl68eh501.jpg"));
        //mEvents.add(new Event("White Sands Dessert", "https://i.redd.it/glin0nwndo501.jpg"));
        addToDatabase();
        initRecyclerView();
    }

    private void addToDatabase() {
        DatabaseReference myRef = FirebaseDatabase.getInstance("https://jade-8a2a9.firebaseio.com/").getReference();
        myRef.child("Events").setValue(mEvents);
    }

}