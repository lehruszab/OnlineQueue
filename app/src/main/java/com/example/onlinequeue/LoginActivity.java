package com.example.onlinequeue;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

public class LoginActivity extends AppCompatActivity {
    private EditText etEmail, etPassword;
    private TextView etUserEmail;
    private Button btnStart, btnSignUp, btnSignIn, btnExit;
    private FirebaseAuth myAuth;
    private FirebaseUser cUser;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.login_layout);
        init();
    }

    @Override
    protected void onStart() {
        super.onStart();

        if (cUser != null)
        {
            showSigned();
        }
        else
        {
            notShowSigned();
            Toast.makeText(this, "Користувач відсутній", Toast.LENGTH_SHORT).show();
        }
    }

    public void init()
    {
        etEmail =findViewById(R.id.etEmail);
        etPassword =findViewById(R.id.etPassword);
        myAuth = FirebaseAuth.getInstance();
        cUser = myAuth.getCurrentUser();
        etUserEmail = findViewById(R.id.etUserEmail);
        btnStart = findViewById(R.id.btnStart);
        btnSignIn = findViewById(R.id.btnSignIn);
        btnSignUp = findViewById(R.id.btnSignUp);
        btnExit = findViewById(R.id.btnExit);
    }

    public void onClickSignIn(View view)
    {
        if(!TextUtils.isEmpty(etEmail.getText().toString()) && !TextUtils.isEmpty(etPassword.getText().toString()))
        {
            myAuth.signInWithEmailAndPassword(etEmail.getText().toString(),etPassword.getText().toString()).addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                @Override
                public void onComplete(@NonNull Task<AuthResult> task) {
                    if(task.isSuccessful())
                    {
                        Toast.makeText(LoginActivity.this, "Вхід успішний", Toast.LENGTH_SHORT).show();
                        showSigned();
                    }
                    else
                    {
                        Toast.makeText(LoginActivity.this, "Увійти не вдалося", Toast.LENGTH_SHORT).show();
                    }
                }
            });
        }
    }

    public void onClickSignUp(View view)
    {
        if(!TextUtils.isEmpty(etEmail.getText().toString()) && !TextUtils.isEmpty(etPassword.getText().toString()))
        {
            myAuth.createUserWithEmailAndPassword(etEmail.getText().toString().trim(), etPassword.getText().toString().trim()).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                @Override
                public void onComplete(@NonNull Task<AuthResult> task) {
                    if(task.isSuccessful())
                    {
                        showSigned();
                        Toast.makeText(LoginActivity.this, "Реєстрація пройшла успішно", Toast.LENGTH_SHORT).show();
                    }
                    else
                    {
                        notShowSigned();
                        Toast.makeText(LoginActivity.this, "Зареєструватися не вдалося", Toast.LENGTH_SHORT).show();
                    }
                }
            });
        }
    }

    public void onClickLogOut(View view)
    {
        FirebaseAuth.getInstance().signOut();
        notShowSigned();
        Toast.makeText(this, "Користувач відсутній", Toast.LENGTH_SHORT).show();
    }

    private void showSigned()
    {
        btnStart.setVisibility(View.VISIBLE);
        btnExit.setVisibility(View.VISIBLE);
        etUserEmail.setVisibility(View.VISIBLE);
        btnSignUp.setVisibility(View.GONE);
        btnSignIn.setVisibility(View.GONE);
        etEmail.setVisibility(View.GONE);
        etPassword.setVisibility(View.GONE);

        cUser = myAuth.getCurrentUser();
        String userName = "Ви здійснили вхід " + cUser.getEmail();
        etUserEmail.setText(userName);
    }

    private void notShowSigned()
    {
        btnStart.setVisibility(View.GONE);
        btnExit.setVisibility(View.GONE);
        etUserEmail.setVisibility(View.GONE);
        btnSignUp.setVisibility(View.VISIBLE);
        btnSignIn.setVisibility(View.VISIBLE);
        etEmail.setVisibility(View.VISIBLE);
        etPassword.setVisibility(View.VISIBLE);
    }

    public void onClickStart(View view)
    {
        String userId = cUser.getUid();
        DatabaseReference allQueuesRef = FirebaseDatabase.getInstance().getReference().child("All queues");
        allQueuesRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                for (DataSnapshot queueSnapshot : dataSnapshot.getChildren()) {
                    String queueName = queueSnapshot.getKey();
                    for (DataSnapshot folderSnapshot : queueSnapshot.getChildren()) {
                        for (DataSnapshot userSnapshot : folderSnapshot.getChildren()) {
                            String id = userSnapshot.getKey();

                            if (id != null && id.equals(userId)) {
                                Intent i = new Intent(LoginActivity.this, ShowActivity.class);
                                i.putExtra("nameQueue", queueName);
                                startActivity(i);
                                return;
                            }
                        }
                    }
                }
                Intent i = new Intent(LoginActivity.this, MainActivity.class);
                startActivity(i);
            }
            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
            }
        });
    }
}
