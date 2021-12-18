package com.example.clientapp.activity;

import androidx.appcompat.app.AppCompatActivity;

import android.annotation.SuppressLint;
import android.app.DatePickerDialog;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import com.example.clientapp.R;
import com.example.clientapp.model.Client;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;


import java.text.SimpleDateFormat;
import java.time.Year;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;
import java.util.Locale;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class SignUpActivity extends AppCompatActivity {
    // Constants
    private final String emailRegex = "^(?=.{1,64}@)[\\p{L}0-9_-]+(\\.[\\p{L}0-9_-]+)*@"
            + "[^-][\\p{L}0-9-]+(\\.[\\p{L}0-9-]+)*(\\.[\\p{L}]{2,})$";
    private final String TAG ="RegisterActivity";

    // Views
    private EditText emailText, usernameText, passwordText, confirmPasswordText
            , fullNameText, phoneText, dobText, weightText, heightText;
    private TextView errorTxt;
    private Button signUpBtn;
    private final Calendar calendar = Calendar.getInstance();
    private String fullName, username, phone, email, dob, weightStr, heightStr;
    private double weight, height;

    // Firebase
    private FirebaseAuth mAuth;
    private FirebaseFirestore fireStore ;
    private CollectionReference userCollection;

    // Data
    private List<Client> clientList;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sign_up);

        //Init necessary components
        getViews();
        initService();
        loadClientData();

        signUpBtn.setOnClickListener(v -> {
            username = usernameText.getText().toString().trim();
            fullName = fullNameText.getText().toString().trim();
            email = emailText.getText().toString().trim();
            phone = phoneText.getText().toString().trim();
            dob = dobText.getText().toString().trim();
            weightStr = weightText.getText().toString().trim();
            heightStr = heightText.getText().toString().trim();
            String password = passwordText.getText().toString().trim();
            String confirmPassword = confirmPasswordText.getText().toString().trim();
            Log.d(TAG, "weight=" + weightStr);
            Log.d(TAG, "height=" + heightStr);
            if (validateInput(username, email, password, confirmPassword, fullName, phone, dob, weightStr, heightStr)) {
                addClientToAuthentication(email, password);
            }
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
                        String INVALID_GMAIL = "The gmail format is invalid.";
                        errorTxt.setVisibility(View.VISIBLE);
                        errorTxt.setText(INVALID_GMAIL);

                        Log.w(TAG,"createClientWithEmail:failure", task.getException());
//                            Toast.makeText(RegisterActivity.this, "Create account fail", Toast.LENGTH_SHORT).show();
                    }
                });
    }

    private void addClientToFireStore() {
        // create Client
        Client c = new Client();
        c.setEmail(email);
        c.setUsername(username);
        c.setFullName(fullName);
        c.setDob(dob);
        c.setPhone(phone);
        c.setHeight(height);
        c.setWeight(weight);

        userCollection.document(username)
                .set(c.toMap())
                .addOnSuccessListener(unused -> {
                    Log.d(TAG, "Successfully added client to FireStore: " + c.toString());
                    updateUI(c);
                })
                .addOnFailureListener(e -> Log.d(TAG, "Fail to add client to FireStore: " + c.toString()));
    }

    //update ui
    private void updateUI(Client client) {
        Intent intent = new Intent(SignUpActivity.this , LogInActivity.class);
        intent.putExtra("email" , client.getEmail());
        setResult(RESULT_OK , intent);
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

    // Validate client's phone
    private boolean isPhoneValid(String phone) {
        if (phone.isEmpty()) {
            String EMPTY_PHONE = "Phone cannot be empty. Please try again!";
            phoneText.setError(EMPTY_PHONE);
            return false;
        } else if (countDigits(phone) < 9) {
            String INVALID_PHONE = "Invalid phone number. Please enter the last 9 digits" +
                    "of your phone number!";
            phoneText.setError(INVALID_PHONE);
            return false;
        }

        return true;
    }

    private int countDigits(String stringToSearch) {
        Pattern digitRegex = Pattern.compile("\\d");
        Matcher countEmailMatcher = digitRegex.matcher(stringToSearch);

        int count = 0;
        while (countEmailMatcher.find()) {
            count++;
        }

        return count;
    }

    // Validate client's weight
    private boolean isWeightValid(String weightStr) {
//        if (weightStr.isEmpty()) {
//            phoneText.setError("Weight cannot be empty. Please try again!");
//            return false;
//        }

        if (!weightStr.isEmpty()) {
            double weight = Double.parseDouble(weightStr);
            if (weight == 0.0) {
                weightText.setError("Weight cannot be 0. Please try again!");
                return false;
            }
        }

        return true;
    }

    // Validate client's height
    private boolean isHeightValid(String heightStr) {
//        if (heightStr.isEmpty()) {
//            heightText.setError("Weight cannot be empty. Please try again!");
//            return false;
//        }

        if (!heightStr.isEmpty()) {
            double height = Double.parseDouble(heightStr);
            if (weight == 0.0) {
            }
                heightText.setError("Weight cannot be 0. Please try again!");
                return false;
            }
        return true;
    }

    // Validate client's dob
    private boolean isDobValid(String dob) {
        if (dob.isEmpty()) {
            dobText.setError("Date of birth cannot be empty. Please try again!");
            return false;
        }

        if (2021 - Integer.parseInt(dob.substring(dob.length() - 4)) < 13) {
            dobText.setError("You should be at least 13 to use this app");
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
//        String password = passwordText.getText().toString();
//        String confirmPassword = confirmPasswordText.getText().toString();

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

    private boolean validateInput(String username,
                                  String email,
                                  String password,
                                  String confirmPassword,
                                  String fullName,
                                  String phone,
                                  String dob,
                                  String weightStr,
                                  String heightStr) {
//        boolean isValid = true;

        Log.d(TAG, "isWeightValid=" + isWeightValid(weightStr));
        Log.d(TAG, "isHeightValid=" + isHeightValid(heightStr));
        Log.d(TAG, "isPasswordValid=" + isPasswordValid(password, confirmPassword));

        return isFullNameValid(fullName)
                && isUsernameValid(username)
                && isEmailValid(email)
                && isPhoneValid(phone)
                && isDobValid(dob)
                && isWeightValid(weightStr)
                && isHeightValid(heightStr)
                && isPasswordValid(password, confirmPassword);
//        if (phone.length() < 4 && isValid) {
//            warningMsg = EMPTY_PHONE;
//            isValid = false;
//        }
//        // Phone number format
//        if (phone.length() != 12 && isValid) {
//            warningMsg = INVALID_PHONE;
//            isValid = false;
//        }


//        if (dob.isEmpty() && isValid) {
//            warningMsg = EMPTY_DOB;
//            isValid = false;
//        }
//        try {
//            int year = Integer.parseInt(dob.substring(6, 10));
//            if ((2021 - year < 18) && isValid) {
//                warningMsg = INVALID_DOB;
//                isValid = false;
//            }
//        } catch (Exception e) {
//            e.printStackTrace();
//        }

//        if (email.isEmpty() && isValid) {
//            warningMsg = EMPTY_EMAIL;
//            isValid = false;
//        }
//        // Email format
//        if (!isPatternValid(email, EMAIL_REGEX) && isValid) {
//            warningMsg = INVALID_EMAIL;
//            isValid = false;
//        }
//
//
//        if (password.isEmpty() && isValid) {
//            warningMsg = EMPTY_PASSWORD;
//            isValid = false;
//        }
//        if (password.length() < 6 && isValid) {
//            warningMsg = INVALID_PASSWORD;
//            isValid = false;
//        }
//        if (confirmPassword.isEmpty() && isValid) {
//            warningMsg = EMPTY_CONFIRM_PASSWORD;
//            isValid = false;
//        }
//        if (!confirmPassword.equals(password) && isValid) {
//            warningMsg = INVALID_CONFIRM_PASSWORD;
//            isValid = false;
//        }
//
//        if (!isValid) {
//            warningTextView.setText(warningMsg);
//            warningTextView.setVisibility(View.VISIBLE);
//            return false;
//        }
    }

    // init service
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
        dobText = findViewById(R.id.editDob);
        weightText = findViewById(R.id.editWeight);
        heightText = findViewById(R.id.editHeight);
        phoneText = findViewById(R.id.editPhone);
        emailText = findViewById(R.id.editEmail);
        usernameText = findViewById(R.id.editUserName);
        passwordText = findViewById(R.id.editPassword);
        confirmPasswordText = findViewById(R.id.editConfirmPassword);
        signUpBtn = findViewById(R.id.signUpBtn);

        initDatePicker();
    }

    @SuppressLint("ClickableViewAccessibility")
    private void initDatePicker() {
        DatePickerDialog.OnDateSetListener date = (view, year, monthOfYear, dayOfMonth) -> {
            calendar.set(Calendar.YEAR, year);
            calendar.set(Calendar.MONTH, monthOfYear);
            calendar.set(Calendar.DAY_OF_MONTH, dayOfMonth);
            updateDobLabel();
        };

//        dobText.setOnTouchListener((v, event) -> {
//            new DatePickerDialog(this, date, calendar
//                    .get(Calendar.YEAR), calendar.get(Calendar.MONTH),
//                    calendar.get(Calendar.DAY_OF_MONTH)).show();
//            return true;
//        });

        dobText.setOnClickListener(v -> new DatePickerDialog(SignUpActivity.this, date, calendar
                .get(Calendar.YEAR), calendar.get(Calendar.MONTH),
                calendar.get(Calendar.DAY_OF_MONTH)).show());
    }

    private void updateDobLabel() {
        String myFormat = "MM/dd/yyyy"; //In which you need put here
        SimpleDateFormat sdf = new SimpleDateFormat(myFormat, Locale.US);

        dobText.setText(sdf.format(calendar.getTime()));
    }
}