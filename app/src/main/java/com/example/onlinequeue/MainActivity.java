package com.example.onlinequeue;
import androidx.appcompat.app.AppCompatActivity;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.EditText;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

public class MainActivity extends AppCompatActivity {
    private EditText etUserName;
    private User newUser;
    private FirebaseAuth myAuth;
    private FirebaseUser cUser;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.main_layout);
        init();
    }
    private void init()
    {
        myAuth = FirebaseAuth.getInstance();
        cUser = myAuth.getCurrentUser();
        newUser = new User(cUser.getUid() , cUser.getEmail());
        etUserName = findViewById(R.id.etNameUser);
    }

    public void onClickCreate(View view)
    {
        Intent i = new Intent(MainActivity.this, CreateQueueActivity.class);
        startActivity(i);
    }
    
    public void onClickJoin(View view)
    {
        if(!TextUtils.isEmpty(etUserName.getText())){newUser.setName(etUserName.getText().toString().trim());}
        Intent i = new Intent(MainActivity.this, JoinQueueActivity.class);
        i.putExtra("user", newUser);
        startActivity(i);
    }
}