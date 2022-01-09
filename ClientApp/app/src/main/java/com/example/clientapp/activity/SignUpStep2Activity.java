package com.example.clientapp.activity;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.annotation.SuppressLint;
import android.app.DatePickerDialog;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.example.clientapp.R;
import com.example.clientapp.model.Client;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class SignUpStep2Activity extends AppCompatActivity {
    // Constant
    final String TAG = this.getClass().getSimpleName();

    // Client
    private Client client;
    private int clientID;
    private String dob, phone, doorNo, street, ward, district, city, address, weightStr, heightStr;
    private double weight, height;

    // Views
    private final Calendar calendar = Calendar.getInstance();
    private EditText editDob, editDoorNo, editStreet, editWard, editDistrict, editCity, editPhone,
            editWeight, editHeight;

    // Firebase
    private FirebaseFirestore fireStore;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sign_up_step2);

        // Init db
        fireStore = FirebaseFirestore.getInstance();
        // Get Views
        getViews();
        // Get intent data
        getIntentData();
    }

    // get views
    private void getViews() {
        editDob = findViewById(R.id.editDob);
        editPhone = findViewById(R.id.editPhone);
        editWeight = findViewById(R.id.editWeight);
        editHeight = findViewById(R.id.editHeight);

        editDoorNo = findViewById(R.id.editDoorNo);
        editStreet = findViewById(R.id.editStreet);
        editWard = findViewById(R.id.editWard);
        editDistrict = findViewById(R.id.editDistrict);
        editCity = findViewById(R.id.editCity);

        initDatePicker();
    }

    @SuppressLint("ClickableViewAccessibility")
    private void initDatePicker() {
        // date picker dialog
        DatePickerDialog.OnDateSetListener date = (view, year, monthOfYear, dayOfMonth) -> {
            calendar.set(Calendar.YEAR, year);
            calendar.set(Calendar.MONTH, monthOfYear);
            calendar.set(Calendar.DAY_OF_MONTH, dayOfMonth);
            updateDobLabel();
        };

        //edit dob
        editDob.setOnTouchListener((v, event) -> {
            if (event.getAction() == MotionEvent.ACTION_UP) {
                v.performClick();
            }
            return true;
        });

        editDob.setOnClickListener(v -> new DatePickerDialog(this, date, calendar
                .get(Calendar.YEAR), calendar.get(Calendar.MONTH),
                calendar.get(Calendar.DAY_OF_MONTH)).show());
    }

    private void updateDobLabel() {
        String myFormat = "MM/dd/yyyy"; //In which you need put here
        SimpleDateFormat sdf = new SimpleDateFormat(myFormat, Locale.US);

        editDob.setText(sdf.format(calendar.getTime()));
    }

    // Validate client's phone
    private boolean isPhoneValid(String phone) {
        if (phone.isEmpty()) {
            String EMPTY_PHONE = "Phone cannot be empty. Please try again!";
            editPhone.setError(EMPTY_PHONE);
            return false;
        } else if (countDigits(phone) < 9) {
            String INVALID_PHONE = "Invalid phone number. Please enter the last 9 digits" +
                    "of your phone number!";
            editPhone.setError(INVALID_PHONE);
            return false;
        }

        return true;
    }

    // Validate client's address
    private boolean isAddressValid(String address) {
        if (address.isEmpty()) {
            editDoorNo.setError("Address cannot be empty. Please try again!");
            return false;
        }

        return true;
    }

    // count digits
    private int countDigits(String stringToSearch) {
        Pattern digitRegex = Pattern.compile("\\d");
        Matcher countEmailMatcher = digitRegex.matcher(stringToSearch);

        int count = 0;
        while (countEmailMatcher.find()) {
            count++;
        }

        return count;
    }

    // Validate client's dob
    private boolean isDobValid(String dob) {
        if (dob.isEmpty()) {
            editDob.setError("Date of birth cannot be empty. Please try again!");
            return false;
        }

        if (2021 - Integer.parseInt(dob.substring(dob.length() - 4)) < 13) {
            editDob.setError("You should be at least 13 to use this app");
            return false;
        }

        return true;
    }

    // Validate client's weight
    private boolean isWeightValid(String weightInput) {
        if (weightInput.isEmpty()) {
//            editWeight.setError("Weight cannot be empty. Please try again!");
//            return false;
            weightStr = "0.0";
        } else {
            double weight = Double.parseDouble(weightStr);
            if (weight == 0.0) {
                editWeight.setError("Weight cannot be 0. Please try again!");
                return false;
            }
        }

        return true;
    }

    // Validate client's height
    private boolean isHeightValid(String heightInput) {
        if (heightInput.isEmpty()) {
//            editHeight.setError("Weight cannot be empty. Please try again!");
//            return false;
            heightStr = "0.0";
        } else {
            double height = Double.parseDouble(heightStr);
            if (height == 0.0) {
                editHeight.setError("Height cannot be 0. Please try again!");
                return false;
            }
        }
        return true;
    }

    // validate input
    private boolean validateInput(String phone,
                                  String address,
                                  String dob,
                                  String weightStr,
                                  String heightStr) {

        return isPhoneValid(phone)
                && isAddressValid(address)
                && isDobValid(dob)
                && isWeightValid(weightStr)
                && isHeightValid(heightStr);
    }

    // get intent
    private void getIntentData() {
        Intent intent = getIntent();
        client = intent.getParcelableExtra("client");
        if (client != null) {
            Toast.makeText(this, "client=" + client.toString(), Toast.LENGTH_SHORT).show();
        }
    }

    // update fire store client
    private void updateFirestoreClient(String phone, String address, String dob, String weightStr, String heightStr) {

        // set up client
        client.setPhone(phone);
        client.setDob(dob);
        client.setAddress(address);
        client.setWeight(Double.parseDouble(weightStr));
        client.setHeight(Double.parseDouble(heightStr));

        fireStore.collection("clients").document(String.valueOf(client.getId()))
                .set(client.toMap())
                .addOnSuccessListener(aVoid -> {
                    Log.d(TAG, "DocumentSnapshot successfully written!");
                    updateUI();
                })
                .addOnFailureListener(e -> Log.w(TAG, "Error writing document", e));
    }

    // sign up button
    public void onSignUpBtnClick(View view) {
        phone = editPhone.getText().toString().trim();
        dob = editDob.getText().toString().trim();
        weightStr = editWeight.getText().toString().trim();
        heightStr = editHeight.getText().toString().trim();

        doorNo = editDoorNo.getText().toString().trim();
        street = editStreet.getText().toString().trim();
        ward = editWard.getText().toString().trim();
        district = editDistrict.getText().toString().trim();
        city = editCity.getText().toString().trim();
        address = doorNo + ", " + street + ", " + ward + ", " + district + ", " + city;

        if (validateInput(phone, address, dob, weightStr, heightStr)) {
            updateFirestoreClient(phone, address, dob, weightStr, heightStr);
        }
    }

    public void onSkipBtnClick(View view) {
        updateUI();
    }

    private void updateUI() {
        Intent intent = new Intent(this , LogInActivity.class);
        intent.putExtra("client" , client);
        setResult(RESULT_OK , intent);
        finish();
    }
}