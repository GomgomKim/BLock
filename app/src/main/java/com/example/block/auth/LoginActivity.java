package com.example.block.auth;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.util.Patterns;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

import com.example.block.R;
import com.example.block.database.MemberPost;
import com.example.block.main.MainActivity;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.iid.FirebaseInstanceId;

import java.util.HashMap;
import java.util.Map;
import java.util.regex.Pattern;

public class LoginActivity extends AppCompatActivity {
    // 비밀번호 정규식
    private static final Pattern PASSWORD_PATTERN = Pattern.compile("^[a-zA-Z0-9!@.#$%^&*?_~]{4,16}$");

    // 파이어베이스 인증 객체 생성
    private FirebaseAuth firebaseAuth;
    //리스너 생성
    private FirebaseAuth.AuthStateListener mAuthListener;
    //데이터베이스
    DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference();

    // 이메일과 비밀번호
    private EditText editTextEmail;
    private EditText editTextPassword;
    private EditText editTextPhone;
    private EditText editTextName;


    private String email = "";
    private String password = "";
    private String phone="";
    private String name="";

    String u_id;
    String token;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        // 파이어베이스 인증 객체 선언
        firebaseAuth = FirebaseAuth.getInstance();

        editTextEmail = findViewById(R.id.et_eamil);
        editTextPassword = findViewById(R.id.et_password);
        editTextPhone = findViewById(R.id.et_phone);
        editTextName = findViewById(R.id.et_name);


        mAuthListener = firebaseAuth -> {
            FirebaseUser user = firebaseAuth.getCurrentUser();
            if (user != null) {
                // User is signed in
                u_id = user.getUid();
                Log.d("solKim", "onAuthStateChanged:signed_in:" + user.getUid());
                startActivity(new Intent(getApplicationContext(), MainActivity.class));
                finish();

            } else { // User is signed out
                Log.d("solKim", "onAuthStateChanged:signed_out");
            }
        };

    }

    @Override
    protected void onStart() {
        super.onStart();
        firebaseAuth.addAuthStateListener(mAuthListener);

    }

    @Override
    protected void onStop() {
        super.onStop();
        if (mAuthListener != null) {
            firebaseAuth.removeAuthStateListener(mAuthListener);
        }

    }

    public void singUp(View view) {//가입
        email = editTextEmail.getText().toString();
        password = editTextPassword.getText().toString();
        phone = editTextPhone.getText().toString();
        name = editTextName.getText().toString();

        if(isValidEmail() && isValidPasswd()) {
            if(isValidPhone() &&isValidName()){
                createUser(email, password);
            }
        }
    }

    public void signIn(View view) {
        email = editTextEmail.getText().toString();
        password = editTextPassword.getText().toString();

        if(isValidEmail() && isValidPasswd()) {
            loginUser(email, password);
        }
    }

    // 이메일 유효성 검사
    private boolean isValidEmail() {
        if (email.isEmpty()) {
            // 이메일 공백
            return false;
        } else if (!Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
            // 이메일 형식 불일치
            return false;
        } else {
            return true;
        }
    }

    // 비밀번호 유효성 검사
    private boolean isValidPasswd() {
        if (password.isEmpty()) {
            // 비밀번호 공백
            return false;
        } else if (!PASSWORD_PATTERN.matcher(password).matches()) {
            // 비밀번호 형식 불일치
            return false;
        } else {
            return true;
        }
    }
    // 전화번호 유효성 검사
    private boolean isValidPhone() {
        if (phone.isEmpty()) {
            // phone 공백
            return false;
        } else if (!PASSWORD_PATTERN.matcher(phone).matches()) {
            // 비밀번호 형식 불일치
            return false;
        } else {
            return true;
        }
    }

    // 이름 유효성 검사
    private boolean isValidName() {
        if (phone.isEmpty()) {
            // phone 공백
            return false;
        } else if (!PASSWORD_PATTERN.matcher(name).matches()) {
            // 비밀번호 형식 불일치
            return false;
        } else {
            return true;
        }
    }

    // 회원가입
    private void createUser(String email, String password) {
        firebaseAuth.createUserWithEmailAndPassword(email, password)
                .addOnCompleteListener(task -> {
                    if(task.isSuccessful()){
//                FirebaseUser user = task.getResult().getUser();
                        token = FirebaseInstanceId.getInstance().getToken();
                        Log.d("SolKim", "Token:" + token);
//                User userModel =new User(token);
//                databaseReference.child("user").child(user.getUid()).setValue(userModel);
                        Map<String, Object> set_value = new HashMap<>();
                        Map<String, Object> getMember = null;
                        MemberPost memberPost = new MemberPost(name, "", token, u_id);
                        getMember = memberPost.toMap();
                        set_value.put("/member/"+phone, getMember);
                        databaseReference.updateChildren(set_value);
                    }

                }).addOnFailureListener(e->{

        });
    }

    // 로그인
    private void loginUser(String email, String password)
    {
        firebaseAuth.signInWithEmailAndPassword(email, password)
                .addOnCompleteListener(this, task -> {
                    if (task.isSuccessful()) {
                        // 로그인 성공
                        Toast.makeText(LoginActivity.this, R.string.success_login, Toast.LENGTH_SHORT).show();

                    } else {
                        // 로그인 실패
                        Toast.makeText(LoginActivity.this, R.string.failed_login, Toast.LENGTH_SHORT).show();
                    }
                });
    }
}