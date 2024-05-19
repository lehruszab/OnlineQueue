package com.example.onlinequeue;
import android.content.Intent;
import android.os.Bundle;
import android.os.Message;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.messaging.FirebaseMessaging;
import com.google.firebase.messaging.RemoteMessage;

public class JoinQueueActivity extends AppCompatActivity {
    private EditText etNameForJoin;
    private DatabaseReference myDataBase;
    User newUser = new User();

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.join_queue_layout);
        init();
        getFMCToken();
    }

    private void init ()
    {
        etNameForJoin = findViewById(R.id.etNameToJoin);
        myDataBase = FirebaseDatabase.getInstance().getReference();

        Intent i =getIntent();
        newUser = (User) i.getSerializableExtra("user");
    }

    public void onClickJoinQueue(View view)
    {
    String nameForJoin = etNameForJoin.getText().toString().trim();

        if (!nameForJoin.isEmpty())
        {
            myDataBase.child("All queues").child(nameForJoin).addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(DataSnapshot dataSnapshot) {
                    if (dataSnapshot.exists()) {
                        int numberOfPeople = dataSnapshot.child("Number of people").getValue(Integer.class);
                        numberOfPeople ++;
                        newUser.setQuePos(numberOfPeople);
                        myDataBase.child("All queues").child(nameForJoin).child("Number of people").setValue(numberOfPeople);

                        myDataBase.child("All queues").child(nameForJoin).child("USERS").child(newUser.id).setValue(newUser);
                        Toast.makeText(JoinQueueActivity.this, "Користувач доданий до черги", Toast.LENGTH_SHORT).show();

                        Intent i = new Intent(JoinQueueActivity.this, ShowActivity.class);
                        i.putExtra("nameQueue", nameForJoin);
                        startActivity(i);
                    } else {
                        Toast.makeText(JoinQueueActivity.this, "Черги не існує", Toast.LENGTH_SHORT).show();
                    }
                }
                @Override
                public void onCancelled(@NonNull DatabaseError error) {
                }
            });
        }
    }

    private void getFMCToken()
    {
        FirebaseMessaging.getInstance().getToken().addOnCompleteListener(new OnCompleteListener<String>() {
            @Override
            public void onComplete(@NonNull Task<String> task) {
                if(task.isSuccessful()){
                    String token = task.getResult();
                    newUser.setToken(token);
                }
            }
        });
    }

    public void sendNotification(String token)
    {
        RemoteMessage.Builder builder = new RemoteMessage.Builder(token)
            .addData("title", "TestTitle")
            .addData("body", "TestTitle");
    }
}
