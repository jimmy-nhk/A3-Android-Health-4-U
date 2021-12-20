package com.example.vendorapp.activity;

import android.Manifest;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;

import com.example.vendorapp.R;
import com.example.vendorapp.model.Vendor;
import com.google.android.gms.auth.api.Auth;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.auth.api.signin.GoogleSignInResult;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.SignInButton;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.firebase.auth.AuthCredential;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.GoogleAuthProvider;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public class LogInActivity extends AppCompatActivity implements GoogleApiClient.OnConnectionFailedListener {
    public final static int REGISTER_CODE = 101;
    private static final int GOOGLE_SUCCESSFULLY_SIGN_IN = 1;
    private static final String TAG = "LogInActivity";
    private static final String VENDOR_COLLECTION = "vendors";

    private EditText emailText;
    private EditText passwordText;
    private TextView errorLoginTxt;

    private FirebaseAuth firebaseAuth;
    private FirebaseFirestore fireStore;
    private CollectionReference userCollection;

    private GoogleApiClient googleApiClient;
    private FirebaseAuth.AuthStateListener authStateListener;
    private List<Vendor> vendorList;
    private Vendor vendor;
    private String email, password, username;

    // Google sign-in
    String idToken;
    private SignInButton signInGoogleButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        Log.d(TAG, "on Login Activity Create");
        // init services
        attachComponents();
        initService();
        requestPermission();
        FirebaseUser firebaseUser = firebaseAuth.getCurrentUser();
        if (firebaseUser != null) {
            firebaseUser.getEmail();
        }
    }

    // init services
    public void initService() {

        // init firebase services
        firebaseAuth = FirebaseAuth.getInstance();

        // init fireStore db
        fireStore = FirebaseFirestore.getInstance();
        userCollection = fireStore.collection(VENDOR_COLLECTION);
        FirebaseAuth.getInstance().signOut();

        //this is where we start the Auth state Listener to listen for whether the user is signed in or not
        authStateListener = firebaseAuth -> {
            // Get signedIn user
            FirebaseUser user = firebaseAuth.getCurrentUser();

            //if user is signed in, we call a helper method to save the user details to Firebase
            if (user != null) {
                // User is signed in
                // you could place other firebase code
                //logic to save the user details to Firebase
                Log.d(TAG, "onAuthStateChanged:signed_in:" + user.getUid());
            } else {
                // User is signed out
                Log.d(TAG, "onAuthStateChanged:signed_out");
            }
        };

        vendorList = new ArrayList<>();

        // load users
        userCollection.addSnapshotListener((value, error) -> {
            if (value != null) {
                for (QueryDocumentSnapshot doc : value) {
                    Log.d(TAG, "onEvent: " + doc.get("name"));
                }
            }
        });

        GoogleSignInOptions gso = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestIdToken(getString(R.string.web_client_id))//you can also use R.string.default_web_vendor_id
                .requestEmail()
                .build();

        googleApiClient = new GoogleApiClient.Builder(this)
                .enableAutoManage(this, this)
                .addApi(Auth.GOOGLE_SIGN_IN_API, gso)
                .build();
        // sign in gg btn
        signInGoogleButton.setOnClickListener(view -> {
            Log.d(TAG, "Before going into google");
            Intent intent = Auth.GoogleSignInApi.getSignInIntent(googleApiClient);
            startActivityForResult(intent, GOOGLE_SUCCESSFULLY_SIGN_IN);
        });

    }

    // attach components with xml
    public void attachComponents() {
        passwordText = findViewById(R.id.passwordTxt);
        emailText = findViewById(R.id.editEmailLogInTxt);
        errorLoginTxt = findViewById(R.id.errorLoginTxt);
        errorLoginTxt.setVisibility(View.INVISIBLE);
        signInGoogleButton = findViewById(R.id.signInWithGoogle);

        TextView textView = (TextView) signInGoogleButton.getChildAt(0);
        textView.setText("Sign in with Google");
    }

    public void onLogInBtnClick(View view) {
//        email = emailText.getText().toString().trim();
//        password = passwordText.getText().toString().trim();
//        email = "afc.luan2508@gmail.com";
//        password = "222222";
//        username = "";

        email = "c1@gmail.com";
        password = "111111";
        username = "";

        if (!email.contains("@")) {
            username = email;
            logInWithUsername(username);
        } else {
            logInWithEmail(email, password);
        }
    }

    private void logInWithEmail(String email, String password) {
        // validate in case it cannot sign in with authentication
        try {
            Log.d(TAG, "signInWithEmail: email: " + email);
            Log.d(TAG, "signInWithEmail: password: " + password);
            firebaseAuth.signInWithEmailAndPassword(email, password)
                    .addOnCompleteListener(this, task -> {
                        if (task.isSuccessful()) {
                            // Sign in success, update UI with signed-in user's information
                            Log.d(TAG, "signInWithEmail:success");
//                                Toast.makeText(LogInActivity.this, "Authentication success", Toast.LENGTH_SHORT).show();

                            try {
                                FirebaseUser userFirebase = firebaseAuth.getCurrentUser();
                                // If log in by email, get Vendor from Firebase first then update UI
                                // after complete
                                if (userFirebase != null && username.isEmpty()) {
                                    getFirebaseVendorByEmail(userFirebase.getEmail());
                                }

                                updateUI();

                            } catch (Exception e) {
                                e.printStackTrace();
                                Log.d(TAG, "" + e.getMessage());
                                Log.d(TAG, "Cannot validate the user in fireStore");
                            }
                        } else {
                            // if sign in fails, display a message to the user
                            Log.w(TAG, "signInWithEmail:failure", task.getException());
//                                Toast.makeText(LogInActivity.this, "Authentication failed", Toast.LENGTH_SHORT).show();
                        }
                    });

        } catch (Exception e) {
            errorLoginTxt.setVisibility(View.VISIBLE);
            errorLoginTxt.setText("Please enter your mail and password.");
            return;
        }
    }

    private void logInWithUsername(String username) {
        fireStore.collection("vendors")
                .document(username)
                .addSnapshotListener((value, e) -> {
                    if (e != null) {
                        Log.w(TAG, "Listen failed.", e);
                        return;
                    }

                    if (value != null) {
                        vendor = value.toObject(Vendor.class);
                        if (vendor != null) {
                            email = vendor.getEmail();
                            logInWithEmail(email, password);
                        }
//                        Log.d(TAG, "vendor by username="+vendor.toString());
//                        Log.d(TAG, "getFirebaseVendorByUsername email="+email);
                    }
                });
    }

    private void getFirebaseVendorByEmail(String email) {

        Log.d(TAG, "getFirebaseVendorByEmail: " );
        fireStore.collection("vendors")
                .whereEqualTo("email", email)
                .addSnapshotListener((value, e) -> {
                    if (e != null) {
                        Log.w(TAG, "Listen failed.", e);
                        return;
                    }

                    if (value != null) {
                        DocumentSnapshot doc = value.getDocuments().get(0);
                        if (doc != null) {
                            vendor = doc.toObject(Vendor.class);
                            Log.d(TAG, "Query Vendor by email="+vendor.toString());

                            updateUI();
                        }
                    }
                });
    }

    // handle sign in with google
    private void handleSignInResult(GoogleSignInResult result) {

        // Check if the result is successful
        if (result.isSuccess()) {
            // get the account
            GoogleSignInAccount account = result.getSignInAccount();
            idToken = account != null ? account.getIdToken() : null;


            // you can store user data to SharedPreference
            AuthCredential credential = GoogleAuthProvider.getCredential(idToken, null);
            firebaseAuthWithGoogle(credential);
        } else {
            // Google Sign In failed, update UI appropriately
            Log.e(TAG, "Login Unsuccessful. " + result.getStatus());
//            Toast.makeText(this, "Login Unsuccessful", Toast.LENGTH_SHORT).show();
        }
    }

    // firebaseAuth with GG
    private void firebaseAuthWithGoogle(AuthCredential credential) {

        // sign in with gg
        firebaseAuth.signInWithCredential(credential)
                .addOnCompleteListener(this, task -> {
                    Log.d(TAG, "signInWithCredential:onComplete:" + task.isSuccessful());
                    if (task.isSuccessful()) {
//                            Toast.makeText(LogInActivity.this, "Login successful", Toast.LENGTH_SHORT).show();

                        // get the current logged in user
                        FirebaseUser currentUser = firebaseAuth.getCurrentUser();
//                        Log.d(TAG, userFirebase.getDisplayName() + " name");
//                        Log.d(TAG, Objects.requireNonNull(userFirebase).getEmail() + " email");

                        // Create the user
//                        Vendor vendor = new Vendor(userFirebase.getDisplayName(), userFirebase.getEmail(), userFirebase.getPhoneNumber());

                        if (currentUser != null) {
                            email = currentUser.getEmail();
                            //TODO: Choose which one to set the document id
                            addVendorToFireStore(currentUser.getDisplayName(), email);

                            // update the UI
                            updateUI();
                        }
                    } else {
                        Log.w(TAG, "signInWithCredential" + task.getException().getMessage());
                        task.getException().printStackTrace();
                        errorLoginTxt.setVisibility(View.VISIBLE);
                        errorLoginTxt.setText("Cannot found the account in the system.\nPlease check again the password and mail");
//                            Toast.makeText(LogInActivity.this, "Authentication failed.",
//                                    Toast.LENGTH_SHORT).show();
                    }

                });
    }

    private void addVendorToFireStore(String email, String displayedName) {
        // create Vendor
        Vendor c = new Vendor();
        c.setEmail(email);
        c.setFullName(displayedName);

        userCollection.document(email)
                .set(c.toMap())
                .addOnSuccessListener(unused -> {
                    Log.d(TAG, "Successfully added vendor to FireStore: " + c.toString());
                    updateUI();
                })
                .addOnFailureListener(e -> Log.d(TAG, "Fail to add vendor to FireStore: " + c.toString()));
    }

    // update UI
    private void updateUI() {
        Intent intent = new Intent(LogInActivity.this, MainActivity.class);
        intent.putExtra("vendor", vendor);
        Log.d(TAG, "LoginActivity, send parcel to MainActivity: updateUI=" + vendor.toString());
        startActivity(intent);
    }

    private void requestPermission() {
        //Request for permission if needed
        ActivityCompat.requestPermissions(LogInActivity.this, new String[]{
                        Manifest.permission.INTERNET},

                99);

    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        // check if from google
        if (requestCode == GOOGLE_SUCCESSFULLY_SIGN_IN) {

//            // The Task returned from this call is always completed, no need to attach
//            // a listener.
//            GoogleSignInResult result = null;
//            if (data != null) {
//                result = Auth.GoogleSignInApi.getSignInResultFromIntent(data);
//            }
//            if (result != null) {
//                handleSignInResult(result);
//            }

            // The Task returned from this call is always completed, no need to attach
            // a listener.
            Log.d(TAG, "signInWithGoogle: " + data.getType());
            Log.d(TAG, "signInWithGoogle: " + data.toString());
            GoogleSignInResult result = Auth.GoogleSignInApi.getSignInResultFromIntent(data);
            handleSignInResult(result);

        }
    }

    public void signUpActivity(View view) {
        try {
            Intent intent = new Intent(LogInActivity.this, SignUpActivity.class);
            startActivityForResult(intent, REGISTER_CODE);
        } catch (Exception e){
            Log.d(TAG, "Cannot change to SignUp Activity");
        }
    }

    @Override
    public void onConnectionFailed(@NonNull ConnectionResult connectionResult) {

    }
}