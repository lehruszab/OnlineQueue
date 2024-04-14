package com.example.onlinequeue;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

public class ShowActivity extends AppCompatActivity {

    private ListView listView;
    private ArrayAdapter<String> adapter;
    private List<String> listData;
    private List<User> listTemp;
    private String myString;

    private TextView etNameOfQueue, etInfName, etInfEmail, editTextNumber,
            staticWelcome, staticWelcome2, staticText;

    private DatabaseReference myDataBase;
    private String tempName;
    private FirebaseAuth myAuth;
    private FirebaseUser cUser;
    private Button btnSkip;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.read_layout);
        init();
        moderVeref();
    }

    private void moderVeref() {
        myDataBase.child("MODERATORS").get().addOnCompleteListener(new OnCompleteListener<DataSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DataSnapshot> task) {
                if(task.isSuccessful())
                {
                    for(DataSnapshot moderatorsSnapshot : task.getResult().getChildren())
                    {
                        String email = moderatorsSnapshot.getKey().replace(" ", ".");
                        if(email != null && email.equals(cUser.getEmail()))
                        {
                            showModerator();
                            updateQuePos();
                            myDataBase.child("USERS").child(cUser.getUid()).removeValue();

                        }
                        else showUser();
                    }

                }
            }
        });
    }

    private void init()
    {
        listView = findViewById(R.id.listView);
        listData = new ArrayList<>();
        listTemp = new ArrayList<>();
        adapter = new ArrayAdapter<>(this, android.R.layout.simple_list_item_1,listData);
        listView.setAdapter(adapter);
        etNameOfQueue = findViewById(R.id.etNameOfQueue);
        etInfEmail = findViewById(R.id.etInfEmail);
        etInfName = findViewById(R.id.etInfName);
        editTextNumber = findViewById(R.id.editTextNumber);
        staticWelcome = findViewById(R.id.staticWelcome);
        staticWelcome2 = findViewById(R.id.staticWelcome2);
        staticText = findViewById(R.id.staticText);

        btnSkip = findViewById(R.id.button);

        myAuth = FirebaseAuth.getInstance();
        cUser = myAuth.getCurrentUser();

        Intent i =getIntent();
        tempName = i.getStringExtra("nameQueue");
        etNameOfQueue.setText(tempName);

        myDataBase = FirebaseDatabase.getInstance().getReference().child("All queues").child(tempName);
    }

    private void getDataFromDB()
    {

        myDataBase.child("USERS").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if(listData.size()>0)listData.clear();
                if(listTemp.size()>0)listTemp.clear();
                for(DataSnapshot ds : snapshot.getChildren())
                {
                    User user =ds.getValue(User.class);
                    listTemp.add(user);
                }
                Collections.sort(listTemp, new Comparator<User>() {
                    @Override
                    public int compare(User user, User t1) {
                        return user.quePos - t1.quePos;
                    }
                });
                for (User user : listTemp)
                {
                    listData.add(user.name);
                }
                adapter.notifyDataSetChanged();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }

    @Override
    public void onBackPressed() {
        showConfirmationDialog();
    }

    private void showConfirmationDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Вихід");
        builder.setMessage("Ви впевнені, що хочете покинути цю чергу?");
        builder.setPositiveButton("Так", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                if (tempName != null) {
                    myDataBase.addListenerForSingleValueEvent(new ValueEventListener() {
                        @Override
                        public void onDataChange(@NonNull DataSnapshot snapshot) {
                            if (snapshot.exists()) {
                                int x = snapshot.child("Number of people").getValue(Integer.class);
                                if(x != 0 ){myDataBase.child("Number of people").setValue(x-1);}
                                updateQuePos();
                                myDataBase.child("USERS").child(cUser.getUid()).removeValue();
                            }
                            finish();
                        }

                        @Override
                        public void onCancelled(@NonNull DatabaseError error) {
                            finish();
                        }
                    });
                }
            }
        });
        builder.setNegativeButton("Ні", null);
        builder.show();
    }


    public void onClickNext(View view) {
        for (User user : listTemp) {
            if (user.quePos == 1) {
                etInfName.setText("Name: " + user.name);
                etInfEmail.setText("Email: " + user.email);
                myDataBase.child("USERS").child(user.id).removeValue();
                updateQuePos();
                updatePos();
                return;
            }
        }
    }

    private void updateQuePos() {
        int newNumOfPeople = 0;
        for (User user : listTemp) {
            newNumOfPeople++;
            int newQuePos = user.quePos - 1;
            myDataBase.child("USERS").child(user.id).child("quePos").setValue(newQuePos);
        }
        myDataBase.child("Number of people").setValue(newNumOfPeople);
    }

    private void showModerator(){
        staticWelcome.setVisibility(View.GONE);
        staticWelcome2.setVisibility(View.GONE);
        editTextNumber.setVisibility(View.GONE);
        listView.setVisibility(View.VISIBLE);
        etInfEmail.setVisibility(View.VISIBLE);
        etInfName.setVisibility(View.VISIBLE);
        staticText.setVisibility(View.VISIBLE);
        btnSkip.setVisibility(View.VISIBLE);

        getDataFromDB();
    }

    private void showUser(){
        staticWelcome.setVisibility(View.VISIBLE);
        staticWelcome2.setVisibility(View.VISIBLE);
        editTextNumber.setVisibility(View.VISIBLE);
        listView.setVisibility(View.GONE);
        etInfEmail.setVisibility(View.GONE);
        etInfName.setVisibility(View.GONE);
        staticText.setVisibility(View.GONE);
        btnSkip.setVisibility(View.GONE);

        staticWelcome.setText(cUser.getEmail() + ", ви знаходитесь в черзі під номером");
        updatePos();
    }


    private void updatePos()
    {
        ValueEventListener valueEventListener = new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                if (dataSnapshot.exists()) {
                    int x = dataSnapshot.getValue(Integer.class);
                    myString = String.valueOf(x);
                    editTextNumber.setText(myString);
                    if(x < 1)
                    {
                        editTextNumber.setVisibility(View.GONE);
                        staticWelcome.setText("Чудово, " +cUser.getEmail() + ", ваша чергра настала!");
                        staticWelcome2.setText("Підійдіть до потрібного пункту.");
                    }
                }
            }
            @Override
            public void onCancelled(DatabaseError databaseError) {    }
        };
        myDataBase.child("USERS").child(cUser.getUid()).child("quePos").addValueEventListener(valueEventListener);

    }
}
