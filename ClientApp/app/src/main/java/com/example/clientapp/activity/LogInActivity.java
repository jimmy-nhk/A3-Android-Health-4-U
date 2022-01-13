package com.example.clientapp.activity;

import com.example.clientapp.R;
import com.example.clientapp.model.Client;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;

public class LogInActivity extends AppCompatActivity implements GoogleApiClient.OnConnectionFailedListener{

    public final static int REGISTER_CODE = 101;

    private EditText emailText;
    private EditText passwordText;
    private TextView errorLoginTxt;

    private static final String TAG = "LogInActivity";

    private FirebaseAuth firebaseAuth;
    private FirebaseFirestore fireStore ;
    private CollectionReference userCollection;
    private static final String CLIENT_COLLECTION = "clients";

    private FirebaseAuth.AuthStateListener authStateListener;
    private List<Client> clientList;
    private Client client;

    private String email = "c0@gmail.com";
    private String password = "111111";

    String idToken;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_log_in);
        // init services
        attachComponents();
        initService();

        FirebaseUser firebaseUser = firebaseAuth.getCurrentUser();
        if (firebaseUser != null) {
            firebaseUser.getEmail();
        }
    }

    // init services
    public void initService(){

        // init firebase services
        firebaseAuth = FirebaseAuth.getInstance();

        // init fireStore db
        fireStore = FirebaseFirestore.getInstance();
        userCollection = fireStore.collection(CLIENT_COLLECTION);
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

        clientList = new ArrayList<>();

        // load users
        userCollection.addSnapshotListener((value, error) -> {

            for (QueryDocumentSnapshot doc : value) {

                Log.d(TAG, "onEvent: " + doc.get("name"));
            }
        });

        // sign in gg btn
//        signInGoogleButton.setOnClickListener(view -> {
//
//        });

    }

    // attach components with xml
    public void attachComponents(){
        passwordText = findViewById(R.id.passwordTxt);
        emailText = findViewById(R.id.editEmailLogInTxt);
        errorLoginTxt = findViewById(R.id.errorLoginTxt);
        errorLoginTxt.setVisibility(View.INVISIBLE);

        //FIXME: TURN THIS OFF
        emailText.setText(email);
        passwordText.setText(password);
//        signInGoogleButton = findViewById(R.id.signInWithGoogle);
//
//        TextView textView = (TextView) signInGoogleButton.getChildAt(0);
//        textView.setText("Sign in with Google");
    }

    // normal log in
    public void normalLogIn(View view) {
        errorLoginTxt.setText("");
        errorLoginTxt.setVisibility(View.INVISIBLE);
        // validate in case it cannot sign in with authentication
        try {

            firebaseAuth.signInWithEmailAndPassword(emailText.getText().toString(), passwordText.getText().toString())
                    .addOnCompleteListener(this, task -> {
                        if (task.isSuccessful()) {
                            // Sign in success, update UI with signed-in user's information
                            Log.d(TAG, "signInWithEmail:success");
//                                Toast.makeText(LogInActivity.this, "Authentication success", Toast.LENGTH_SHORT).show();
                            FirebaseUser userFirebase = firebaseAuth.getCurrentUser();

                            Log.d(TAG, userFirebase.getEmail() + " mail1");

                            try {
                                getFirebaseClientByEmail(userFirebase.getEmail());
                            } catch (Exception e){
                                Log.d(TAG, "Cannot validate the user in firestore");
                            }
                        } else {
                            // if sign in fails, display a message to the user
                            Log.w(TAG, "signInWithEmail:failure", task.getException());
                            errorLoginTxt.setText(("Sign in failed. Please try again!"));
                            errorLoginTxt.setVisibility(View.VISIBLE);
//                                Toast.makeText(LogInActivity.this, "Authentication failed", Toast.LENGTH_SHORT).show();
                        }
                    });

        } catch (Exception e){
            errorLoginTxt.setVisibility(View.VISIBLE);
            errorLoginTxt.setText(("Please enter your mail and password"));
            return;
        }
    }

    // get firebase client by email
    private void getFirebaseClientByEmail(String email) {
//        fireStore.collection("clients")
//                .whereEqualTo("email", email)
//                .addSnapshotListener((value, e) -> {
//                    if (e != null) {
//                        Log.w(TAG, "Listen failed.", e);
//                        return;
//                    }
//
//                    try {
//                        if (value != null) {
//                            DocumentSnapshot doc = value.getDocuments().get(0);
//                            if (doc != null) {
//                                client = doc.toObject(Client.class);
//                                Log.d(TAG, "Query Client by email="+client.toString());
//
//                                updateUI();
//                            }
//                        }
//                    } catch (Exception exception) {
//                        exception.printStackTrace();
//                    }
//                });

        fireStore.collection("clients")
                .whereEqualTo("email", email)
                .get()
                .addOnSuccessListener(queryDocumentSnapshots -> {
                    client = queryDocumentSnapshots.getDocuments().get(0).toObject(Client.class);
                    if (client != null) {
                        Log.d(TAG, "Query Client by email="+client.toString());

                        updateUI();
                    }
                });
    }

    // update UI
    private void updateUI() {
        Intent intent = new Intent(LogInActivity.this, MainActivity.class);
        intent.putExtra("client", client);
        Log.d(TAG, "updateUI, client=" + client.toString());
        startActivity(intent);

    }

    // sign up
    public void signUpActivity(View view) {
        try {
            Intent intent = new Intent(LogInActivity.this, SignUpStep1Activity.class);
            startActivityForResult(intent, REGISTER_CODE);
        } catch (Exception e){
            Log.d(TAG, "Cannot change to SignUp Activity");
        }
    }

    @Override
    public void onConnectionFailed(@NonNull ConnectionResult connectionResult) {

    }
}