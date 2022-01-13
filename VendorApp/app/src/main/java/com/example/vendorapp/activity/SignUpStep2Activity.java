package com.example.vendorapp.activity;

import android.annotation.SuppressLint;
import android.app.DatePickerDialog;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.example.vendorapp.R;
import com.example.vendorapp.model.Vendor;
import com.google.firebase.firestore.FirebaseFirestore;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Locale;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class SignUpStep2Activity extends AppCompatActivity {
    // Constant
    final String TAG = this.getClass().getSimpleName();

    // Vendor
    private Vendor vendor;
    private int vendorID;
    private String phone, doorNo, street, ward, district, city, address;
    private double weight, height;

    // Views
    private final Calendar calendar = Calendar.getInstance();
    private EditText editDoorNo, editStreet, editWard, editDistrict, editCity, editPhone;

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
        editPhone = findViewById(R.id.editPhone);
        editDoorNo = findViewById(R.id.editDoorNo);
        editStreet = findViewById(R.id.editStreet);
        editWard = findViewById(R.id.editWard);
        editDistrict = findViewById(R.id.editDistrict);
        editCity = findViewById(R.id.editCity);

    }



    // Validate vendor's phone
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

    // Validate vendor's address
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
    // validate input
    private boolean validateInput(String phone,
                                  String address) {

        return isPhoneValid(phone)
                && isAddressValid(address);
    }

    // get intent
    private void getIntentData() {
        Intent intent = getIntent();
        vendor = intent.getParcelableExtra("vendor");
        if (vendor != null) {
//            Toast.makeText(this, "vendor=" + vendor.toString(), Toast.LENGTH_SHORT).show();
        }
    }

    // update fire store vendor
    private void updateFirestoreVendor(String phone, String address ) {

        // set up vendor
        vendor.setPhone(phone);
        vendor.setAddress(address);

        fireStore.collection("vendors").document(String.valueOf(vendor.getId()))
                .set(vendor.toMap())
                .addOnSuccessListener(aVoid -> {
                    Log.d(TAG, "DocumentSnapshot successfully written!");
                    updateUI();
                })
                .addOnFailureListener(e -> Log.w(TAG, "Error writing document", e));
    }

    // sign up button
    public void onSignUpBtnClick(View view) {
        phone = editPhone.getText().toString().trim();
        doorNo = editDoorNo.getText().toString().trim();
        street = editStreet.getText().toString().trim();
        ward = editWard.getText().toString().trim();
        district = editDistrict.getText().toString().trim();
        city = editCity.getText().toString().trim();
        address = doorNo + ", " + street + ", " + ward + ", " + district + ", " + city;

        if (validateInput(phone, address )) {
            updateFirestoreVendor(phone, address );
        }
    }

    public void onSkipBtnClick(View view) {
        updateUI();
    }

    private void updateUI() {
        Intent intent = new Intent(this, LogInActivity.class);
        intent.putExtra("vendor", vendor);
        setResult(RESULT_OK, intent);
        finish();
    }
}