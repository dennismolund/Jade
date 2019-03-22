package mode1719.student.ju.jade;

import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.facebook.Profile;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.io.IOException;
import java.util.Date;
import java.util.UUID;

public class DetailEventViewActivity extends AppCompatActivity {
    private static final int PICK_IMAGE = 1;
    public Uri filePath;
    public String eventImageUrl;
    public EditText eventDescription;
    public EditText eventTime;
    public ImageView eventImage;
    public EditText eventTitle;

    StorageReference storageReference;


    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_show_event);
        initLayoutObjects();
        storageReference = FirebaseStorage.getInstance().getReference();
        if(getValIntent() == 1){
            showEvent();
        }
        else if(getValIntent() == 0){
            createNewEvent();
        }

    }
    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == PICK_IMAGE && resultCode == RESULT_OK){
            filePath = data.getData();
            try {
                Bitmap bitmap = MediaStore.Images.Media.getBitmap(getContentResolver(), filePath);
                eventImage.setImageBitmap(bitmap);
            }catch (IOException e){
                e.printStackTrace();
            }
        }
    }


    private void uploadEvent(final Event event) {

        if (filePath != null){
            final String imageId = UUID.randomUUID().toString();
            final StorageReference ref = storageReference.child(imageId);
            ref.putFile(filePath)
                    .addOnCompleteListener(new OnCompleteListener<UploadTask.TaskSnapshot>() {
                        @Override
                        public void onComplete(@NonNull Task<UploadTask.TaskSnapshot> task) {

                            ref.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                                @Override
                                public void onSuccess(Uri uri) {
                                    System.out.println(uri);
                                    System.out.println(uri.toString());
                                    eventImageUrl = uri.toString();
                                    event.setImageUrl(eventImageUrl);
                                    event.addToDatabase();
                                }
                            });
                        }
                    }).addOnFailureListener(new OnFailureListener() {
                @Override
                public void onFailure(@NonNull Exception e) {
                    System.out.println("Error: " + e);
                }
            });
        }
        else{
            event.addToDatabase();
        }
    }

    public void createNewEvent(){
        Button doneBtn = findViewById(R.id.doneButton);
        doneBtn.setVisibility(View.VISIBLE);
        final Date date = getDateIntent();

        eventImage = findViewById(R.id.eventImage);

        eventImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent gallery = new Intent();
                gallery.setType("image/*");
                gallery.setAction(Intent.ACTION_GET_CONTENT);

                startActivityForResult(Intent.createChooser(gallery, "Select image"), PICK_IMAGE);
            }
        });
        doneBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(isValidEvent()){
                    Event event = new Event(
                            date,
                            eventTitle.getText().toString(),
                            eventDescription.getText().toString(),
                            eventTime.getText().toString(),
                            eventImageUrl,
                            Profile.getCurrentProfile().getName(),
                            Profile.getCurrentProfile().getId(),
                            getCityIntent());
                        uploadEvent(event);
                        finish();
                }
                else{
                    Toast toast = Toast.makeText(DetailEventViewActivity.this, "You need to enter a title", Toast.LENGTH_LONG);
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
        eventDescription = findViewById(R.id.eventDescription);
        eventTime = findViewById(R.id.eventTime);
        eventImage = findViewById(R.id.eventImage);
        eventTitle = findViewById(R.id.eventTitle);
    }

    private void showEvent(){

        setUpViewObjects();

        final Event clickedEvent = getEventIntent();

        String imageUrl = clickedEvent.getImageUrl();
        String title = clickedEvent.getTitle();
        String time = clickedEvent.getTime();
        String description = clickedEvent.getDescription();

        System.out.println("Show event imageURL: " + imageUrl);

        if(imageUrl != null) {
            Glide.with(this).asBitmap().load(imageUrl).into(eventImage);
            eventTitle.setText(title);
            eventTime.setText(time);
            eventDescription.setText(description);
        }
        else {
            Glide.with(this).asBitmap().load(R.mipmap.ic_launcher).into(eventImage);
            eventTitle.setText(title);
            eventTime.setText(time);
            eventDescription.setText(description);
        }

        checkForEventOwner(clickedEvent);

    }

    private void checkForEventOwner(Event clickedEvent){
        Button deleteButton = findViewById(R.id.deleteButton);

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

    private void setUpViewObjects(){
        eventTitle.setFocusable(false);
        eventTitle.setClickable(false);
        eventTime.setFocusable(false);
        eventTime.setClickable(false);
        eventDescription.setFocusable(false);
        eventDescription.setClickable(false);
    }

    private void deleteEvent(){
        final Event clickedEvent = getEventIntent();
        final Date clickedDate = getDateIntent();
        new AlertDialog.Builder(DetailEventViewActivity.this)
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

    private String getCityIntent(){
        Intent intent = getIntent();
        return intent.getStringExtra("city");
    }

    private int getValIntent(){
        Intent intent = getIntent();
        return intent.getIntExtra("value", -1);
    }

    private Event getEventIntent(){
        Intent intent = getIntent();
        return intent.getParcelableExtra("listItem");
    }

    private Date getDateIntent(){
        Date date = new Date();
        Intent intent = getIntent();
        date.setTime(intent.getLongExtra("date", -1));
        return date;
    }
}
