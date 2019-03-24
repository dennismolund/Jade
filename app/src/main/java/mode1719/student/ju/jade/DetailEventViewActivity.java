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

    // Connects the event and it's image and uploads them to database/firestorage.
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
                                    eventImageUrl = uri.toString();
                                    event.setImageUrl(eventImageUrl);
                                    event.setImageID(imageId);
                                    event.addToDatabase();
                                }
                            });
                        }
                    }).addOnFailureListener(new OnFailureListener() {
                @Override
                public void onFailure(@NonNull Exception e) {
                    e.printStackTrace();
                }
            });
        }
        else{
            event.addToDatabase();
        }
    }

    // Allows the user to create a new event.
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

                startActivityForResult(Intent.createChooser(gallery, getString(R.string.select_image)), PICK_IMAGE);
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
                    Toast toast = Toast.makeText(DetailEventViewActivity.this, getString(R.string.invalid_title), Toast.LENGTH_LONG);
                    toast.show();
                }
            }
        });
    }

    // Returns true if the event has a title.
    public boolean isValidEvent(){
        return (eventTitle.getText().toString().length() > 0);
    }

    // Initializes the view objects.
    public void initLayoutObjects(){
        eventDescription = findViewById(R.id.eventDescription);
        eventTime = findViewById(R.id.eventTime);
        eventImage = findViewById(R.id.eventImage);
        eventTitle = findViewById(R.id.eventTitle);
    }

    // Sets up the event view and sets a default image if no image was added by the creator.
    private void showEvent(){

        prepareViewObjects();

        final Event clickedEvent = getEventIntent();

        String imageUrl = clickedEvent.getImageUrl();
        String title = clickedEvent.getTitle();
        String time = clickedEvent.getTime();
        String description = clickedEvent.getDescription();

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

    // If the creator of the owner looks at the event, there is a choice to delete it.
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

    // Prepares the layout.
    private void prepareViewObjects(){
        eventTitle.setFocusable(false);
        eventTitle.setClickable(false);
        eventTime.setFocusable(false);
        eventTime.setClickable(false);
        eventDescription.setFocusable(false);
        eventDescription.setClickable(false);
    }

    // Removes the event and it's image from the database / firestorage.
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
                                if(clickedEvent.getImageID() != null){
                                    FirebaseStorage.getInstance().getReference().child(clickedEvent.getImageID()).delete();
                                }
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

    // Returns the city user is in.
    private String getCityIntent(){
        Intent intent = getIntent();
        return intent.getStringExtra("city");
    }

    // Returns 0 if the user clicked create button and 1 if user picked an event to look at.
    private int getValIntent(){
        Intent intent = getIntent();
        return intent.getIntExtra("value", -1);
    }

    // Returns the clicked event.
    private Event getEventIntent(){
        Intent intent = getIntent();
        return intent.getParcelableExtra("listItem");
    }

    // Returns the date user clicked.
    private Date getDateIntent(){
        Date date = new Date();
        Intent intent = getIntent();
        date.setTime(intent.getLongExtra("date", -1));
        return date;
    }
}
