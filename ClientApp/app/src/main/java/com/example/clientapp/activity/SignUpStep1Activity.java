package com.example.clientapp.activity;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import android.annotation.SuppressLint;
import android.app.DatePickerDialog;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.example.clientapp.R;
import com.example.clientapp.model.Client;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;


import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;
import java.util.Locale;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class SignUpStep1Activity extends AppCompatActivity {
    // Constants
    private final String emailRegex = "^(?=.{1,64}@)[\\p{L}0-9_-]+(\\.[\\p{L}0-9_-]+)*@"
            + "[^-][\\p{L}0-9-]+(\\.[\\p{L}0-9-]+)*(\\.[\\p{L}]{2,})$";
    private final String TAG ="RegisterActivity";

    // Views
    private EditText emailText, usernameText, passwordText, confirmPasswordText
            , fullNameText;
    private TextView errorTxt;
    private LinearLayout nextLayoutBtn;
    private String fullName, username, email;
    private int clientSize;

    // Firebase
    private FirebaseAuth mAuth;
    private FirebaseFirestore fireStore ;
    private CollectionReference userCollection;

    // Data
    private List<Client> clientList;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sign_up_step1);

        //Init necessary components
        getViews();
        initService();
        loadClientData();
        loadSizeClient();
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
                        Log.d(TAG, "Successfully added new Client in Register Activity");
                        addClientToFireStore();
                    } else {
                        String INVALID_GMAIL = "The email format is invalid.";
                        errorTxt.setVisibility(View.VISIBLE);
                        errorTxt.setText(INVALID_GMAIL);

                        Log.w(TAG,"createClientWithEmail:failure", task.getException());
                    }
                });
    }

    // add client to db
    private void addClientToFireStore() {
        // create Client
        Client c = new Client(clientSize, fullName, username, email);

        // Client size is the id of the new client
        userCollection.document(String.valueOf(clientSize))
                .set(c.toMap())
                .addOnSuccessListener(unused -> {
                    Log.d(TAG, "Successfully added client to FireStore: " + c.toString());
                    updateUI(c);
                })
                .addOnFailureListener(e -> Log.d(TAG, "Fail to add client to FireStore: " + c.toString()));
    }

    // Load size of client collection
    private void loadSizeClient(){
        userCollection.addSnapshotListener((value, error) -> clientSize = value.size());
    }

    //update ui
    private void updateUI(Client client) {
        Intent intent = new Intent(this, SignUpStep2Activity.class);
        intent.putExtra("client", client);
        startActivity(intent);
        finish();
    }

    // Validate client's fullname
    private boolean isFullNameValid(String fullName) {
        if (fullName.isEmpty()) {
            fullNameText.setError("Full name cannot be empty");
            return false;
        }
        return true;
    }

    // Validate client's username
    private boolean isUsernameValid(String username) {
        if (username.isEmpty()) {
            usernameText.setError("Username cannot be empty");
            return false;
        }

        if (!isUsernameUnique(username)) {
            usernameText.setError("This username was already used by another account");
            return false;
        }

        return true;
    }

    // Check if username is unique
    private boolean isUsernameUnique(String username){
        for (Client client: clientList) {
            if (username.equals(client.getUsername())) return false;
        }
        return true;
    }

    // Check if username is unique
    private boolean isEmailValid(String email) {
        if (email.isEmpty()) {
            emailText.setError("Email cannot be empty. Please try again!");
            return false;
        }
        if (!Pattern.compile(emailRegex)
                .matcher(email)
                .matches()) {
            emailText.setError("Invalid email format (e.g abc@gmail.com)");
            return false;
        }
        if (!isEmailUnique(email)) {
            emailText.setError("This email was already used by another account");
            return false;
        }

        return true;
    }

    // Check if username is unique
    private boolean isEmailUnique(String email){
        for (Client client: clientList) {
            if (email.equals(client.getEmail())) return false;
        }
        return true;
    }

    // validate password
    private boolean isPasswordValid(String password, String confirmPassword) {
        if (password.isEmpty()) {
            passwordText.setError("Password cannot be empty");
            confirmPasswordText.setError("Password cannot be empty");
            return false;
        }

        // check length
        if (password.length() < 6){
            passwordText.setError("Password cannot have less than 6 characters");
            confirmPasswordText.setError("Password cannot have less than 6 characters");
            return false;
        }

        // check validation
        if (!password.equals(confirmPassword)){
            passwordText.setError("Confirm password does not match");
            confirmPasswordText.setError("Confirm password does not match");
            return false;
        }
        return true;
    }

    // Validate input
    private boolean validateInput(String fullName,
                                  String username,
                                  String email,
                                  String password,
                                  String confirmPassword) {

        return isFullNameValid(fullName)
                && isUsernameValid(username)
                && isEmailValid(email)
                && isPasswordValid(password, confirmPassword);
    }

    // Init service
    public void initService(){
        // Init firestore
        mAuth = FirebaseAuth.getInstance();
        fireStore = FirebaseFirestore.getInstance();
        String CLIENT_COLLECTION = "clients";
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

        fullNameText = findViewById(R.id.editFullName);
        emailText = findViewById(R.id.editEmail);
        usernameText = findViewById(R.id.editUserName);
        passwordText = findViewById(R.id.editPassword);
        confirmPasswordText = findViewById(R.id.editConfirmPassword);
        nextLayoutBtn = findViewById(R.id.nextLinearLayoutBtn);
    }

    public void onNextBtnClick(View view) {
        username = usernameText.getText().toString().trim();
        fullName = fullNameText.getText().toString().trim();
        email = emailText.getText().toString().trim();
        String password = passwordText.getText().toString().trim();
        String confirmPassword = confirmPasswordText.getText().toString().trim();

        username = "c2";
        fullName = "c2 name";
        email = "c2@gmail.com";
        password = "111111";
        confirmPassword = "111111";

        if (validateInput(fullName, username, email, password, confirmPassword)) {
            // Add client to authentication & firebase collection & go to next sign up step
            addClientToAuthentication(email, password);
        }
    }
}