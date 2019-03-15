package mode1719.student.ju.jade;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.facebook.Profile;

import org.w3c.dom.Text;

import java.sql.SQLOutput;
import java.util.Date;

public class ShowEvent extends AppCompatActivity {

    Event mEvent = new Event();

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_show_event);

        checkActivity();


    }

    public void checkActivity(){
        Button doneButton = findViewById(R.id.doneButton);
        Button deleteButton = findViewById(R.id.deleteButton);
        final ImageView eventImage = findViewById(R.id.eventImage);
        final EditText eventTitle = findViewById(R.id.eventTitle);
        final EditText eventTime = findViewById(R.id.eventTime);
        final EditText eventDescription= findViewById(R.id.eventDescription);


        Intent intent = getIntent();
        int value = intent.getIntExtra("value", 0);
        if (value == 1){
            doneButton.setVisibility(View.GONE);
            //Only show delete if creator visit?

            final Event clickedEvent = intent.getParcelableExtra("listItem");
            String imageUrl = clickedEvent.getImageUrl();
            String title = clickedEvent.getTitle();
            String time = clickedEvent.getTime();
            String description = clickedEvent.getDescription();






            Glide.with(this).asBitmap().load(imageUrl).into(eventImage);
            eventTitle.setText(title);
            eventTime.setText(time);
            eventDescription.setText(description);

            deleteButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    new AlertDialog.Builder(ShowEvent.this)
                            .setTitle("Delete Event")
                            .setMessage("Do you really want to delete it?")
                            .setPositiveButton(
                                    android.R.string.yes,
                                    new DialogInterface.OnClickListener(){
                                        public void onClick(DialogInterface dialog, int whichButton){
                                            //TODO: delete activity.
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
            });

        }

        else {
            deleteButton.setVisibility(View.GONE);
            final Date date = new Date();
            Intent mainIntent = getIntent();
            date.setTime(mainIntent.getLongExtra("date", -1));

            eventImage.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent intent = new Intent(Intent.ACTION_PICK,
                            android.provider.MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
                    startActivityForResult(intent, 0);
                    System.out.println("eventImage.onClickListener");
                }
            });

            doneButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    System.out.println(date);
                    Event event = new Event(
                            date,
                            eventTitle.getText().toString(),
                            eventDescription.getText().toString(),
                            eventTime.getText().toString(),
                            "IMAGEURL",
                            Profile.getCurrentProfile().getName(),
                            Profile.getCurrentProfile().getId());
                            System.out.println(event.getOwnerID());
                            event.addToDatabase();
                    finish();
                }
            });
        }



    }
}

