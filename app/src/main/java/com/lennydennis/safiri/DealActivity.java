package com.lennydennis.safiri;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.content.res.Resources;
import android.net.Uri;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import com.google.android.gms.tasks.Continuation;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.lennydennis.safiri.Util.FirebaseUtil;
import com.squareup.picasso.Picasso;

import java.io.File;
import java.util.Objects;

import butterknife.BindView;
import butterknife.ButterKnife;

public class DealActivity extends AppCompatActivity {

    private FirebaseDatabase mDatabase;
    private DatabaseReference mMyRef;
    private static final int PICTURE_RESULT=42;
    @BindView(R.id.safiri_title)
    EditText safariTitle;
    @BindView(R.id.safiri_description)
    EditText safariDescription;
    @BindView(R.id.safiri_price)
    EditText safariPrice;
    @BindView(R.id.btn_image)
    Button imageButton;
    @BindView(R.id.image)
    ImageView mImageView;
    TravelDeal travelDeal;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_deal);
        ButterKnife.bind(this);

//        FirebaseUtil.openFirebaseReference("traveldeals",this);
        mDatabase = FirebaseUtil.sFirebaseDatabase;
        mMyRef = FirebaseUtil.sDatabaseReference;

        final Intent intent = getIntent();
        TravelDeal travelDeal = (TravelDeal) intent.getSerializableExtra("Deal");
        if(travelDeal == null){
            this.travelDeal = new TravelDeal();
        }else{
            this.travelDeal = travelDeal;
            safariTitle.setText(travelDeal.getTitle());
            safariDescription.setText(travelDeal.getDescription());
            safariPrice.setText(travelDeal.getPrice());
            showImage(travelDeal.getImageUrl());
        }

        imageButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(Intent.ACTION_GET_CONTENT);
                intent.setType("image/jpeg");
                intent.putExtra(Intent.EXTRA_LOCAL_ONLY,true);
                startActivityForResult(Intent.createChooser(intent,"Insert Picture"),PICTURE_RESULT);

            }
        });

    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        switch (item.getItemId()){
            case R.id.save_menu:
                saveDeal();
                Toast.makeText(this, "Deal saved", Toast.LENGTH_SHORT).show();
                clean();
                backToList();
                return true;
            case R.id.delete_deal:
                deleteDeal();
                Toast.makeText(this, "Deal Deleted", Toast.LENGTH_SHORT).show();
                backToList();
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    private void clean() {
        safariTitle.setText("");
        safariDescription.setText("");
        safariPrice.setText("");
    }

    private void saveDeal() {
        travelDeal.setTitle(safariTitle.getText().toString());
        travelDeal.setDescription(safariPrice.getText().toString());
        travelDeal.setPrice(safariDescription.getText().toString());
        if(travelDeal.getId()==null){
            mMyRef.push().setValue(travelDeal);
        }else{
            mMyRef.child(travelDeal.getId()).setValue(travelDeal);
        }
    }
    
    private void deleteDeal(){
        if(travelDeal==null){
            Toast.makeText(this, "Can't Delete. Please save deal", Toast.LENGTH_SHORT).show();
            return;
        }
        mMyRef.child(travelDeal.getId()).removeValue();
        }

    private void backToList(){
        Intent intent = new Intent(this, ListActivity.class);
        startActivity(intent);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater menuInflater = getMenuInflater();
        menuInflater.inflate(R.menu.save_menu, menu);

        if(FirebaseUtil.isAdmin){
            menu.findItem(R.id.delete_deal).setVisible(true);
            menu.findItem(R.id.save_menu).setVisible(true);
            enableEditText(true);
        }else{
            menu.findItem(R.id.delete_deal).setVisible(false);
            menu.findItem(R.id.save_menu).setVisible(false);
            enableEditText(false);
        }
        return true;
    }

    private void enableEditText(boolean isEnabled){
        safariTitle.setEnabled(isEnabled);
        safariDescription.setEnabled(isEnabled);
        safariPrice.setEnabled(isEnabled);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if(requestCode == PICTURE_RESULT && resultCode == RESULT_OK){

            assert data != null;
            Uri imageUri = data.getData();
            final StorageReference riversRef = FirebaseUtil.mStorageRef.child(Objects.requireNonNull(imageUri.getLastPathSegment()));
            final UploadTask uploadTask = riversRef.putFile(imageUri);

            uploadTask.addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                        @Override
                        public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {

                            uploadTask.continueWithTask(new Continuation<UploadTask.TaskSnapshot, Task<Uri>>() {
                                @Override
                                public Task<Uri> then(@NonNull Task<UploadTask.TaskSnapshot> task) throws Exception {
                                    if (!task.isSuccessful()) {
                                        throw task.getException();

                                    }
                                    // Continue with the task to get the download URL
                                    return riversRef.getDownloadUrl();

                                }
                            }).addOnCompleteListener(new OnCompleteListener<Uri>() {
                                @Override
                                public void onComplete(@NonNull Task<Uri> task) {
                                    if (task.isSuccessful()) {
                                        Uri downloadUri = task.getResult();
                                        travelDeal.setImageUrl(downloadUri.toString());
                                        showImage(downloadUri.toString());
                                    }
                                }
                            });

                        }
                    })
                    .addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception exception) {
                            // Handle unsuccessful uploads
                            // ...
                        }
                    });

        }
    }

    private void showImage(String url){
        if(url != null && !url.isEmpty()){
            int width = Resources.getSystem().getDisplayMetrics().widthPixels;
            Picasso.get()
                    .load(url)
                    .resize(width,width*2/3)
                    .centerCrop()
                    .into(mImageView);
        }
    }
}