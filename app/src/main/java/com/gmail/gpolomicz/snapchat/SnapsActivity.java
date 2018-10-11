package com.gmail.gpolomicz.snapchat;

import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.FirebaseDatabase;

import java.util.ArrayList;

public class SnapsActivity extends AppCompatActivity {

    FirebaseAuth mAuth;
    ListView snapsListView;
    ArrayList<String> emailsArrayList = new ArrayList<>();
    ArrayList<DataSnapshot> snaps = new ArrayList<>();
    ArrayAdapter<String> adapter;

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.snap_menu, menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        if(item.getItemId() == R.id.createSnap) {
            Intent intent = new Intent(this, CreateSnap.class);
            startActivity(intent);
        } else if(item.getItemId() == R.id.logOut) {
            mAuth.signOut();
            finish();
        }

        return super.onOptionsItemSelected(item);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_snaps);

        snapsListView = findViewById(R.id.snapsListView);
        mAuth = FirebaseAuth.getInstance();
        adapter = new ArrayAdapter<>(this, android.R.layout.simple_list_item_1, emailsArrayList);
        snapsListView.setAdapter(adapter);

        FirebaseDatabase.getInstance().getReference().child("users").child(mAuth.getCurrentUser().getUid()).child("snaps").addChildEventListener(new ChildEventListener() {
            @Override
            public void onChildAdded(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {
                emailsArrayList.add(dataSnapshot.child("from").getValue().toString());
                snaps.add(dataSnapshot);
                adapter.notifyDataSetChanged();
            }

            @Override
            public void onChildChanged(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {

            }
            @Override
            public void onChildRemoved(@NonNull DataSnapshot dataSnapshot) {

                int index = 0;
                for (DataSnapshot snap : snaps) {

                    Log.d("GPDEB", snap.getKey() + " == " + dataSnapshot.getKey());

                    if(snap.getKey().equals(dataSnapshot.getKey())) {
                        snaps.remove(index);
                        emailsArrayList.remove(index);
                    }
                    index++;
                    Log.d("GPDEB", String.valueOf(index));
                }
                adapter.notifyDataSetChanged();
            }
            @Override
            public void onChildMoved(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {

            }
            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });

        snapsListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                DataSnapshot dataSnapshot = snaps.get(i);

                Intent intent = new Intent(SnapsActivity.this, ViewSnapActivity.class);
                intent.putExtra("imageName", dataSnapshot.child("imageName").getValue().toString());
                intent.putExtra("imageURL", dataSnapshot.child("imageURL").getValue().toString());
                intent.putExtra("message", dataSnapshot.child("message").getValue().toString());
                intent.putExtra("snapKey", dataSnapshot.getKey());
                startActivity(intent);
            }
        });

    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        mAuth.signOut();
    }
}
