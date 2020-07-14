package com.lennydennis.safiri;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.widget.EditText;
import android.widget.Toast;

import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.lennydennis.safiri.Util.FirebaseUtil;

import butterknife.BindView;
import butterknife.ButterKnife;

public class DealActivity extends AppCompatActivity {

    private FirebaseDatabase mDatabase;
    private DatabaseReference mMyRef;
    @BindView(R.id.safiri_title)
    EditText safariTitle;
    @BindView(R.id.safiri_description)
    EditText safariDescription;
    @BindView(R.id.safiri_price)
    EditText safariPrice;
    TravelDeal travelDeal;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        ButterKnife.bind(this);

        FirebaseUtil.openFirebaseReference("traveldeals",this);
        mDatabase = FirebaseUtil.sFirebaseDatabase;
        mMyRef = FirebaseUtil.sDatabaseReference;

        Intent intent = getIntent();
        TravelDeal travelDeal = (TravelDeal) intent.getSerializableExtra("Deal");
        if(travelDeal == null){
            this.travelDeal = new TravelDeal();
        }else{
            this.travelDeal = travelDeal;
            safariTitle.setText(travelDeal.getTitle());
            safariDescription.setText(travelDeal.getDescription());
            safariPrice.setText(travelDeal.getPrice());
        }

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
        return true;
    }
}