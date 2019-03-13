package mode1719.student.ju.jade;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;

import org.w3c.dom.Text;

import java.util.Date;

public class ShowEvent extends AppCompatActivity {



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
        final TextView eventTitle = findViewById(R.id.eventTitle);
        final TextView eventTime = findViewById(R.id.eventTime);
        final TextView eventDescription= findViewById(R.id.eventDescription);


        Intent intent = getIntent();
        int value = intent.getIntExtra("value", 0);
        if (value == 1){
            doneButton.setVisibility(View.GONE);
            //Only show delete if creator visit?

            String imageUrl = getIntent().getStringExtra("image_url");
            String title = getIntent().getStringExtra("title");
            String time = getIntent().getStringExtra("time");
            String description = getIntent().getStringExtra("description");
            int position = getIntent().getIntExtra("position", -1);
            System.out.println(position);

            Glide.with(this).asBitmap().load(imageUrl).into(eventImage);
            eventTitle.setText(title);
            eventTime.setText(time);
            eventDescription.setText(description);

            deleteButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {

                    //delete Event.
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
                    //add image when creating event.
                    System.out.println("hej");
                }
            });

            doneButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    System.out.println(date);
                    Event event = new Event(
                            date,
                            eventTitle.toString(),
                            eventDescription.toString(),
                            eventTime.toString(),
                            eventImage.toString(),
                            "creatror");
                    finish();
                }
            });
        }

    }
}

