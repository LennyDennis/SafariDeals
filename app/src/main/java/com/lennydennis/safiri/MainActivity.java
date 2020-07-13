package com.lennydennis.safiri;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

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

public class MainActivity extends AppCompatActivity {

    private FirebaseDatabase mDatabase;
    private DatabaseReference mMyRef;
    @BindView(R.id.safiri_title)
    EditText safariTitle;
    @BindView(R.id.safiri_description)
    EditText safariDescription;
    @BindView(R.id.safiri_price)
    EditText safariPrice;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        ButterKnife.bind(this);

        FirebaseUtil.openFirebaseReference("traveldeals");
        mDatabase = FirebaseUtil.sFirebaseDatabase;
        mMyRef = FirebaseUtil.sDatabaseReference;


    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        switch (item.getItemId()){
            case R.id.save_menu:
                saveMenu();
                Toast.makeText(this, "Deal saved", Toast.LENGTH_SHORT).show();
                clean();
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

    private void saveMenu() {
        String title = safariTitle.getText().toString();
        String price = safariPrice.getText().toString();
        String description = safariDescription.getText().toString();
        TravelDeal travelDeal = new TravelDeal(title,description,price," ");
        mMyRef.push().setValue(travelDeal);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater menuInflater = getMenuInflater();
        menuInflater.inflate(R.menu.save_menu, menu);
        return true;
    }
}