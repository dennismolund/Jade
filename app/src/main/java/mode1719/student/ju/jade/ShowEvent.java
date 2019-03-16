package mode1719.student.ju.jade;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.facebook.Profile;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import org.w3c.dom.Text;

import java.util.Date;

public class ShowEvent extends AppCompatActivity {

    public EditText eventDescription;
    public EditText eventTime;
    public ImageView eventImage;
    public EditText eventTitle;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_show_event);
        initLayoutObjects();

        if(getIntentVal() == 1){
            showEvent();
        }
        else if(getIntentVal() == 0){
            createNewEvent();
        }
    }

    public void createNewEvent(){
        Button doneBtn = findViewById(R.id.doneButton);
        doneBtn.setVisibility(View.VISIBLE);
        final Date date = getDateIntent();
        doneBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(isValidEvent()){
                    Event event = new Event(
                            date,
                            eventTitle.getText().toString(),
                            eventDescription.getText().toString(),
                            eventTime.getText().toString(),
                            "IMAGEURL",
                            Profile.getCurrentProfile().getName(),
                            Profile.getCurrentProfile().getId());
                        event.addToDatabase();
                        finish();
                }
                else{
                    Toast toast = Toast.makeText(ShowEvent.this, "You need to enter a title", Toast.LENGTH_LONG);
                    toast.show();
                }
            }
        });
    }

    public boolean isValidEvent(){
        if(eventTitle.getText().toString().length() > 0){
            return true;
        }
        return false;
    }

    public void initLayoutObjects(){
        eventDescription= findViewById(R.id.eventDescription);
        eventTime = findViewById(R.id.eventTime);
        eventImage = findViewById(R.id.eventImage);
        eventTitle = findViewById(R.id.eventTitle);
    }

    public void showEvent(){
        Button deleteButton = findViewById(R.id.deleteButton);
        eventTitle.setFocusable(false);
        eventTitle.setClickable(false);

        eventTime.setFocusable(false);
        eventTime.setClickable(false);

        eventDescription.setFocusable(false);
        eventDescription.setClickable(false);

        Event clickedEvent = getEventInent();

        String imageUrl = clickedEvent.getImageUrl();
        String title = clickedEvent.getTitle();
        String time = clickedEvent.getTime();
        String description = clickedEvent.getDescription();

        Glide.with(this).asBitmap().load(imageUrl).into(eventImage);
        eventTitle.setText(title);
        eventTime.setText(time);
        eventDescription.setText(description);
        if(Profile.getCurrentProfile().getId().equals(clickedEvent.getOwnerID())){
            deleteButton.setVisibility(View.VISIBLE);
        }
        deleteButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                deleteEvent();
            }
        });

    }

    public void deleteEvent(){
        final Event clickedEvent = getEventInent();
        final Date clickedDate = getDateIntent();
        new AlertDialog.Builder(ShowEvent.this)
                .setTitle("Delete Event")
                .setMessage("Do you really want to delete it?")
                .setPositiveButton(
                        android.R.string.yes,
                        new DialogInterface.OnClickListener(){
                            public void onClick(DialogInterface dialog, int whichButton){
                                FirebaseDatabase.getInstance().getReference().child("Events").child(clickedDate.
                                        toString()).child(clickedEvent.getKey()).removeValue();
                                finish();
                            }
                        }
                ).setNegativeButton(
                android.R.string.no,
                new DialogInterface.OnClickListener(){
                    public void onClick(DialogInterface dialog, int whichButton){
                        finish();
                    }
                }
        ).show();
    }

    public int getIntentVal(){
        Intent intent = getIntent();
        return intent.getIntExtra("value", -1);
    }

    public Event getEventInent(){
        Intent intent = getIntent();
        return intent.getParcelableExtra("listItem");
    }
    public Date getDateIntent(){
        Date date = new Date();
        Intent intent = getIntent();
        date.setTime(intent.getLongExtra("date", -1));
        return date;
    }
}
