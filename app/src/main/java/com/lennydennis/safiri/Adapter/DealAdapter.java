package com.lennydennis.safiri.Adapter;

import android.content.Context;
import android.content.Intent;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.recyclerview.widget.RecyclerView;

import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.lennydennis.safiri.DealActivity;
import com.lennydennis.safiri.R;
import com.lennydennis.safiri.TravelDeal;
import com.lennydennis.safiri.Util.FirebaseUtil;

import java.util.ArrayList;

import butterknife.BindView;
import butterknife.ButterKnife;

public class DealAdapter extends RecyclerView.Adapter<DealAdapter.ViewHolder> {

    private static final String TAG = "Deal Adapter" ;
    ArrayList<TravelDeal> mTravelDeals;
    private FirebaseDatabase mFirebaseDatabase;
    private DatabaseReference mDatabaseReference;
    private ChildEventListener mChildEventListener;


    public DealAdapter() {
        FirebaseUtil.openFirebaseReference("traveldeals");
        mFirebaseDatabase = FirebaseUtil.sFirebaseDatabase;
        mDatabaseReference = FirebaseUtil.sDatabaseReference;
        mTravelDeals = FirebaseUtil.sTravelDeals;
        mChildEventListener = new ChildEventListener() {
            @Override
            public void onChildAdded(@NonNull DataSnapshot snapshot, @Nullable String previousChildName) {
                TravelDeal travelDeal = snapshot.getValue(TravelDeal.class);
                Log.d(TAG, "onChildAdded:  "+travelDeal.getTitle());
                travelDeal.setId(snapshot.getKey());
                mTravelDeals.add(travelDeal);
                notifyItemInserted(mTravelDeals.size()-1);
            }

            @Override
            public void onChildChanged(@NonNull DataSnapshot snapshot, @Nullable String previousChildName) {

            }

            @Override
            public void onChildRemoved(@NonNull DataSnapshot snapshot) {

            }

            @Override
            public void onChildMoved(@NonNull DataSnapshot snapshot, @Nullable String previousChildName) {

            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        };
        mDatabaseReference.addChildEventListener(mChildEventListener);

    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        Context context = parent.getContext();
        View view = LayoutInflater.from(context).inflate(R.layout.deal_item,parent,false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        TravelDeal travelDeal = mTravelDeals.get(position);
        holder.bind(travelDeal);
    }

    @Override
    public int getItemCount() {
        return mTravelDeals.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {

        TextView dealTitle;
        TextView dealDescription;
        TextView dealPrice;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            dealTitle = itemView.findViewById(R.id.travel_title);
            dealDescription = itemView.findViewById(R.id.travel_description);
            dealPrice = itemView.findViewById(R.id.travel_price);
            itemView.setOnClickListener(this);
        }

        public void bind(TravelDeal travelDeal){
            dealTitle.setText(travelDeal.getTitle());
            dealDescription.setText(travelDeal.getDescription());
            dealPrice.setText(travelDeal.getPrice());
        }

        @Override
        public void onClick(View v) {
            int position = getAdapterPosition();
            TravelDeal travelDealSelected = mTravelDeals.get(position);
            Intent intent = new Intent(itemView.getContext(), DealActivity.class);
            intent.putExtra("Deal", travelDealSelected);
            itemView.getContext().startActivity(intent);
        }
    }

}
