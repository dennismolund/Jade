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
        ImageView eventImage = findViewById(R.id.eventImage);
        TextView eventTitle = findViewById(R.id.eventTitle);
        TextView eventTime = findViewById(R.id.eventTime);
        TextView eventDescription= findViewById(R.id.eventDescription);


        Intent intent = getIntent();
        int value = intent.getIntExtra("value", 0);
        if (value == 1){
            doneButton.setVisibility(View.GONE);


            String imageUrl = getIntent().getStringExtra("image_url");
            String title = getIntent().getStringExtra("title");
            String time = getIntent().getStringExtra("time");
            String description = getIntent().getStringExtra("description");

            Glide.with(this).asBitmap().load(imageUrl).into(eventImage);
            eventTitle.setText(title);
            eventTime.setText(time);
            eventDescription.setText(description);

        }

        else {
            final Date date = new Date();
            Intent mainIntent = getIntent();
            date.setTime(mainIntent.getLongExtra("date", -1));

            doneButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    System.out.println(date);
                    //add to database
                    finish();
                }
            });
        }

    }
}

