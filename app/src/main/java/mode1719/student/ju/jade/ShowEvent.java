package mode1719.student.ju.jade;

import android.app.ProgressDialog;
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
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Date;
import java.util.UUID;

public class ShowEvent extends AppCompatActivity {
    private String attendee = Profile.getCurrentProfile().getName()+Profile.getCurrentProfile().getId();
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

        if(getIntentVal() == 1){
            showEvent();
        }
        else if(getIntentVal() == 0){
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
                            new ArrayList<>());
                        event.addAttendees(Profile.getCurrentProfile().getName()+Profile.getCurrentProfile().getId());
                        uploadEvent(event);
                    System.out.println("Debug5");
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
        Button attendBtn = findViewById(R.id.attendButton);
        Button unattendBtn = findViewById(R.id.unattendButton);

        eventTitle.setFocusable(false);
        eventTitle.setClickable(false);

        eventTime.setFocusable(false);
        eventTime.setClickable(false);

        eventDescription.setFocusable(false);
        eventDescription.setClickable(false);

        final Event clickedEvent = getEventInent();

        String imageUrl = clickedEvent.getImageUrl();
        String title = clickedEvent.getTitle();
        String time = clickedEvent.getTime();
        String description = clickedEvent.getDescription();

        System.out.println("Show event imageURL: " + imageUrl);

        Glide.with(this).asBitmap().load(imageUrl).into(eventImage);
        eventTitle.setText(title);
        eventTime.setText(time);
        eventDescription.setText(description);
        if(Profile.getCurrentProfile().getId().equals(clickedEvent.getOwnerID())){
            deleteButton.setVisibility(View.VISIBLE);
        }
        else if (isAttending(clickedEvent)){
            unattendBtn.setVisibility(View.VISIBLE);
        }
        else{
            attendBtn.setVisibility(View.VISIBLE);
        }


        deleteButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                deleteEvent();
            }
        });
        unattendBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                removeAttendee(clickedEvent);
            }
        });
        attendBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                addAttendee(clickedEvent);
            }
        });
    }
    public void addAttendee(Event event){
        DatabaseReference mRef = FirebaseDatabase.getInstance().getReference();
        mRef.child("Events").child(event.getDate().toString()).child(event.getKey()).child("attendees").push().setValue(attendee);
    }

    public void removeAttendee(Event event){
        DatabaseReference mRef = FirebaseDatabase.getInstance().getReference();
        mRef.child("Events").child(event.getDate().toString()).child(event.getKey()).child("attendees").child(attendee).removeValue();
        event.getAttendees().remove(attendee);
    }

    public boolean isAttending(Event event){
        for (int i = 0; i < event.getAttendees().size(); i++){
            if ((Profile.getCurrentProfile().getName()+Profile.getCurrentProfile().getId()).equals(event.getAttendees().get(i))){
                return true;
            }
        }
        return false;
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
