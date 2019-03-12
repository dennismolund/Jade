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

public class ShowEvent extends AppCompatActivity {

    ImageView eventImage = findViewById(R.id.eventImage);
    TextView eventTitle = findViewById(R.id.eventTitle);
    TextView eventTime = findViewById(R.id.eventTime);
    TextView eventDescription= findViewById(R.id.eventDescription);
    Button doneButton = findViewById(R.id.doneButton);

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_show_event);

        checkParentActivity();

        doneButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

            }
        });
    }

    public void checkParentActivity(){
        Intent intent = getIntent();
        int value = intent.getIntExtra("value", 0);
        if (value == 1){
            doneButton.setVisibility(View.GONE);
            getIntents();
        }
    }

    private void getIntents(){

        String imageUrl = getIntent().getStringExtra("image_url");
        String title = getIntent().getStringExtra("title");
        String time = getIntent().getStringExtra("time");
        String description = getIntent().getStringExtra("description");

        setEvent(imageUrl,title,time,description);
    }

    private void setEvent(String imageUrl, String title, String time, String description ){

        Glide.with(this).asBitmap().load(imageUrl).into(eventImage);
        eventTitle.setText(title);
        eventTime.setText(time);
        eventDescription.setText(description);
    }
}