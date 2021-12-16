package com.example.clientapp.activity;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import com.example.clientapp.R;
import com.example.clientapp.model.Client;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;


import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class RegisterActivity extends AppCompatActivity {

    private EditText emailText, clientNameText, passwordText, confirmPasswordText;
    private Button signUpBtn;
    private FirebaseAuth mAuth;
//    private FirebaseDatabase firebaseDatabase;
//    private DatabaseReference databaseReference;

    private List<Client> clientList;

    private TextView errorTxt;

    public static final String Client_COLLECTION = "clients";

    public static final String TAG ="RegisterActivity";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);

        //Init necessary components
        attachComponents();
        initService();
        loadClientData();

        signUpBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Log.d(TAG, "signUpBtn");

                // validate name
                if (!validateClientName(clientNameText.getText().toString())){
                    clientNameText.setError("This Clientname is already existed");
                    Log.d(TAG, "Clientname already exists");
                    return;
                }

                // validate mail
                if (!validateMail(emailText.getText().toString())){
                    emailText.setError("This email is already existed");
                    Log.d(TAG, "email already exists");
                    return;
                }

                // validate the password
                if (!validatePassword()){
                    Log.d(TAG, "Password does not match or less than 6 characters ");
                    return;
                }

                addClientToAuthentication(emailText.getText().toString(),  confirmPasswordText.getText().toString());

            }
        });

    }

    // load the Client data
    public void loadClientData(){

//        // load Clients
//        databaseReference.child("Clients").addValueEventListener(new ValueEventListener() {
//            @Override
//            public void onDataChange(@NonNull DataSnapshot snapshot) {
//                // This method is called once with the initial value and again
//                // whenever data at this location is updated.
//
//
//                GenericTypeIndicator<HashMap<String, Client>> genericTypeIndicator = new GenericTypeIndicator<HashMap<String, Client>>() {
//                };
//
//                HashMap<String, Client> Clients = snapshot.getValue(genericTypeIndicator);
//
//
//                try {
//                    for (Client u : Clients.values()) {
////                        Log.d(TAG, "Value is: " + u.getEmail());
//                        ClientList.add(u);
//                    }
//
//
//                } catch (Exception e) {
//                    Log.d(TAG, "Cannot load the Clients");
//                }
//
//            }
//
//            @Override
//            public void onCancelled(@NonNull DatabaseError error) {
//
//            }
//        });
    }

    // add to authentication
    public void addClientToAuthentication(String mail, String password){

        mAuth.createUserWithEmailAndPassword(mail, password)
                .addOnCompleteListener(this,new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {

                        if (task.isSuccessful()){
                            // Sign in success, update UI
                            Log.d(TAG,"createClientWithEmail:success");
                            FirebaseUser user = mAuth.getCurrentUser();
//                            Toast.makeText(RegisterActivity.this, "Successfully created", Toast.LENGTH_SHORT).show();


                            // create Client
                            Client client1 = new Client(clientNameText.getText().toString(), emailText.getText().toString(), "false");

//                            databaseReference.child("Clients").child(client1.getName()).setValue(client1.toMap());

                            Log.d(TAG, "Successfully added new Client in Register Activity");

                            updateUI(client1);
                        } else {

                            errorTxt.setVisibility(View.VISIBLE);
                            errorTxt.setText("The gmail format is incorrect.");

                            Log.w(TAG,"createClientWithEmail:failure", task.getException());
//                            Toast.makeText(RegisterActivity.this, "Create account fail", Toast.LENGTH_SHORT).show();


                        }
                    }
                });
    }

    //update ui
    private void updateUI(Client client) {
        Intent intent = new Intent(RegisterActivity.this , LogInActivity.class);
        intent.putExtra("email" , client.getEmail());
        setResult(RESULT_OK , intent);
        finish();
    }

    // validate Client name
    private boolean validateClientName(String clientName){

        for (Client client: clientList
        ) {
            if (clientName.equals(client.getName())){
                return false;
            }
        }
        return true;
    }

    // validate Client email
    private boolean validateMail(String mail){

        for (Client client: clientList
        ) {
            if (mail.equals(client.getEmail())){
                return false;
            }
        }
        return true;
    }


    // validate password
    private boolean validatePassword() {

        String password = passwordText.getText().toString();
        String confirmPassword = confirmPasswordText.getText().toString();

        // check validation
        if (!password.equals(confirmPassword)){
            passwordText.setError("The password does not match");
            confirmPasswordText.setError("The password does not match");
            return false;
        }

        // check length
        if (confirmPassword.length() < 6){
            passwordText.setError("The password cannot have less than 6 characters");
            confirmPasswordText.setError("The password cannot have less than 6 characters");
            return false;
        }

        return true;
    }


    // init service
    public void initService(){
        // Init firestone
        mAuth = FirebaseAuth.getInstance();

        // init realtime db
//        firebaseDatabase = FirebaseDatabase.getInstance("https://a2-android-56cbb-default-rtdb.asia-southeast1.firebasedatabase.app/");
//        databaseReference = firebaseDatabase.getReference();
        clientList = new ArrayList<>();


    }

    // attach components
    public void attachComponents(){
        errorTxt = findViewById(R.id.errorTxt);
        errorTxt.setVisibility(View.INVISIBLE);

        emailText = findViewById(R.id.editEmail);
        clientNameText = findViewById(R.id.editUserName);
        passwordText = findViewById(R.id.editPassword);
        confirmPasswordText = findViewById(R.id.editConfirmPassword);
        signUpBtn = findViewById(R.id.signUpBtn);
    }
}