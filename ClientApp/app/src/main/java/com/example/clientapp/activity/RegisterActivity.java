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
import com.google.android.gms.tasks.OnFailureListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;


import java.util.ArrayList;
import java.util.List;

public class RegisterActivity extends AppCompatActivity {
    // Constants
    private final String CLIENT_COLLECTION = "clients";
    private final String TAG ="RegisterActivity";

    // Views
    private EditText emailText, usernameText, passwordText, confirmPasswordText;
    private TextView errorTxt;
    private Button signUpBtn;

    // Firebase
    private FirebaseAuth mAuth;
    private FirebaseFirestore fireStore ;
    private CollectionReference userCollection;

    // Data
    private List<Client> clientList;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);

        //Init necessary components
        getViews();
        initService();
        loadClientData();

        signUpBtn.setOnClickListener(v -> {
            Log.d(TAG, "signUpBtn");

            // validate name
            if (!usernameExists(usernameText.getText().toString())){
                usernameText.setError("Username already existed");
                Log.d(TAG, "Username already exists");
                return;
            }

            // validate mail
            if (!emailExists(emailText.getText().toString())){
                emailText.setError("Email already existed");
                Log.d(TAG, "Email already exists");
                return;
            }

            // validate the password
            if (!validatePassword()){
                Log.d(TAG, "Password does not match or less than 6 characters ");
                return;
            }

            addClientToAuthentication(emailText.getText().toString(),  confirmPasswordText.getText().toString());

        });

    }

    // load the Client data
    private void loadClientData() {
        userCollection.addSnapshotListener((value, error) -> {
            if (error != null) {
                Log.w(TAG, "Listen failed.", error);
                return;
            }
            Client c;
            if (value != null) {
                for (QueryDocumentSnapshot doc : value) {
                    if (doc != null) {
                        c = doc.toObject(Client.class);
                        clientList.add(c);
                        Log.d(TAG, "Client data loaded successfully: " + c.toString());
                    }
                }
            }
        });
    }

    // add to authentication
    private void addClientToAuthentication(String mail, String password){
        mAuth.createUserWithEmailAndPassword(mail, password)
                .addOnCompleteListener(this, task -> {

                    if (task.isSuccessful()){
                        // Sign in success, update UI
//                        FirebaseUser user = mAuth.getCurrentUser();
                        Log.d(TAG, "Successfully added new Client in Register Activity");
                        addClientToFireStore();
                    } else {

                        errorTxt.setVisibility(View.VISIBLE);
                        errorTxt.setText("The gmail format is incorrect.");

                        Log.w(TAG,"createClientWithEmail:failure", task.getException());
//                            Toast.makeText(RegisterActivity.this, "Create account fail", Toast.LENGTH_SHORT).show();
                    }
                });
    }

    private void addClientToFireStore() {
        // create Client
        String username = usernameText.getText().toString().trim();
        Client c = new Client();
        c.setEmail(emailText.getText().toString().trim());
        c.setUsername(usernameText.getText().toString().trim());

        userCollection.document(username)
                .set(c.toMap())
                .addOnSuccessListener(unused -> Log.d(TAG, "Successfully added client to FireStore: " + c.toString()))
                .addOnFailureListener(e -> Log.d(TAG, "Fail to add client to FireStore: " + c.toString()));
    }

    //update ui
    private void updateUI(Client client) {
        Intent intent = new Intent(RegisterActivity.this , LogInActivity.class);
        intent.putExtra("email" , client.getEmail());
        setResult(RESULT_OK , intent);
        finish();
    }

    // validate Client name
    private boolean usernameExists(String username){
        for (Client client: clientList) {
            if (username.equals(client.getUsername())){
                return false;
            }
        }
        return true;
    }

    // validate Client email
    private boolean emailExists(String email){
        for (Client client: clientList) {
            if (email.equals(client.getEmail())){
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
            passwordText.setError("Password does not match");
            confirmPasswordText.setError("Password does not match");
            return false;
        }

        // check length
        if (confirmPassword.length() < 6){
            passwordText.setError("Password cannot have less than 6 characters");
            confirmPasswordText.setError("Password cannot have less than 6 characters");
            return false;
        }

        return true;
    }

    // init service
    public void initService(){
        // Init firestore
        mAuth = FirebaseAuth.getInstance();
        fireStore = FirebaseFirestore.getInstance();
        userCollection = fireStore.collection(CLIENT_COLLECTION);
        // init realtime db
//        firebaseDatabase = FirebaseDatabase.getInstance("https://a2-android-56cbb-default-rtdb.asia-southeast1.firebasedatabase.app/");
//        databaseReference = firebaseDatabase.getReference();
        clientList = new ArrayList<>();


    }

    // attach components
    public void getViews() {
        errorTxt = findViewById(R.id.errorTxt);
        errorTxt.setVisibility(View.INVISIBLE);

        emailText = findViewById(R.id.editEmail);
        usernameText = findViewById(R.id.editUserName);
        passwordText = findViewById(R.id.editPassword);
        confirmPasswordText = findViewById(R.id.editConfirmPassword);
        signUpBtn = findViewById(R.id.signUpBtn);
    }
}

//TODO: after logging in => (send email by intent to other activity ? check firebaseUser in every activity)