package com.example.onlinequeue;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

public class CreateQueueActivity extends AppCompatActivity {
    private DatabaseReference myDataBase;
    private Queue myQueue;
    EditText etNameQueue;
    private String nameQueue;
    private FirebaseUser cUser;
    private FirebaseAuth myAuth;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.create_queue_layout);
        init();
    }

    private void init()
    {
        myDataBase = FirebaseDatabase.getInstance().getReference();
        myAuth = FirebaseAuth.getInstance();

        cUser = myAuth.getCurrentUser();

        etNameQueue = findViewById(R.id.etNameQueue);
    }

    public void onClickConfirmCreate(View view)
    {
        nameQueue = etNameQueue.getText().toString();
        myQueue = new Queue(nameQueue);
        if(cUser != null)
        {
            myQueue.setAdmin(cUser.getEmail().replace("."," "));
            myDataBase.child("All queues").child(myQueue.nameQueue).child("MODERATORS").setValue(myQueue.admins);
            myDataBase.child("All queues").child(myQueue.nameQueue).child("Number of people").setValue(myQueue.numOfPeople);
        }
        Intent i = new Intent(CreateQueueActivity.this, ShowActivity.class);
        i.putExtra("nameQueue", myQueue.nameQueue);
        startActivity(i);
    } 
}
