package com.example.clientapp.activity;

import com.example.clientapp.R;
import com.example.clientapp.model.Client;
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

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public class LogInActivity extends AppCompatActivity implements GoogleApiClient.OnConnectionFailedListener{

    public final static int REGISTER_CODE = 101;

    private EditText emailText;
    private EditText passwordText;
    private TextView errorLoginTxt;

    private static final int GOOGLE_SUCCESSFULLY_SIGN_IN = 1;

    private static final String TAG = "LogInActivity";

    private FirebaseAuth firebaseAuth;
    private FirebaseFirestore fireStore ;
    private CollectionReference userCollection;
    private static final String CLIENT_COLLECTION = "clients";

    private FirebaseAuth.AuthStateListener authStateListener;
    private GoogleApiClient googleApiClient;
    private List<Client> clientList;
    private Client client;

    String idToken;

    private SignInButton signInGoogleButton;

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

        GoogleSignInOptions gso =  new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestIdToken(getString(R.string.web_client_id))//you can also use R.string.default_web_client_id
                .requestEmail()
                .build();

        googleApiClient=new GoogleApiClient.Builder(this)
                .enableAutoManage(this, this)
                .addApi(Auth.GOOGLE_SIGN_IN_API,gso)
                .build();

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
        emailText.setText("c2@gmail.com");
        passwordText.setText("111111");
//        signInGoogleButton = findViewById(R.id.signInWithGoogle);
//
//        TextView textView = (TextView) signInGoogleButton.getChildAt(0);
//        textView.setText("Sign in with Google");
    }

    public void normalLogIn(View view) {

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
//                                Toast.makeText(LogInActivity.this, "Authentication failed", Toast.LENGTH_SHORT).show();
                        }
                    });

        } catch (Exception e){
            errorLoginTxt.setVisibility(View.VISIBLE);
            errorLoginTxt.setText("Please enter your mail and password.");
            return;
        }
    }

    // handle sign in with google
    private void handleSignInResult(GoogleSignInResult result){

        // Check if the result is successful
        if(result.isSuccess()){
            // get the account
            GoogleSignInAccount account = result.getSignInAccount();
            idToken = account != null ? account.getIdToken() : null;


            // you can store user data to SharedPreference
            AuthCredential credential = GoogleAuthProvider.getCredential(idToken, null);
            firebaseAuthWithGoogle(credential);
        }else{
            // Google Sign In failed, update UI appropriately
            Log.e(TAG, "Login Unsuccessful. "+result.getStatus());
//            Toast.makeText(this, "Login Unsuccessful", Toast.LENGTH_SHORT).show();
        }
    }

    // firebaseAuth with GG
    private void firebaseAuthWithGoogle(AuthCredential credential){

        // sign in with gg
        firebaseAuth.signInWithCredential(credential)
                .addOnCompleteListener(this, task -> {
                    Log.d(TAG, "signInWithCredential:onComplete:" + task.isSuccessful());
                    if(task.isSuccessful()){
//                            Toast.makeText(LogInActivity.this, "Login successful", Toast.LENGTH_SHORT).show();

                        // get the current logged in user
                        FirebaseUser userFirebase = firebaseAuth.getCurrentUser();
                        Log.d(TAG,userFirebase.getDisplayName() + " name" );
                        Log.d(TAG, Objects.requireNonNull(userFirebase).getEmail() + " email");


                        // Create the user
//                            Client client = new Client(userFirebase.getDisplayName(), userFirebase.getEmail(), userFirebase.getPhoneNumber());

                        //TODO: Choose which one to set the document id
                        addClientToFireStore(userFirebase.getDisplayName() );

                        // update the UI
                            updateUI();
                    }else{
                        Log.w(TAG, "signInWithCredential" + task.getException().getMessage());
                        task.getException().printStackTrace();
                        errorLoginTxt.setVisibility(View.VISIBLE);
                        errorLoginTxt.setText("Cannot found the account in the system.\nPlease check again the password and mail");
//                            Toast.makeText(LogInActivity.this, "Authentication failed.",
//                                    Toast.LENGTH_SHORT).show();
                    }

                });
    }

    private void getFirebaseClientByEmail(String email) {
        fireStore.collection("clients")
                .whereEqualTo("email", email)
                .addSnapshotListener((value, e) -> {
                    if (e != null) {
                        Log.w(TAG, "Listen failed.", e);
                        return;
                    }

                    try {
                        if (value != null) {
                            DocumentSnapshot doc = value.getDocuments().get(0);
                            if (doc != null) {
                                client = doc.toObject(Client.class);
                                Log.d(TAG, "Query Client by email="+client.toString());

                                updateUI();
                            }
                        }
                    } catch (Exception exception) {
                        exception.printStackTrace();
                    }
                });
    }

    private void addClientToFireStore(String displayedName) {
        // create Client
        String fullName = displayedName;
        Client c = new Client();
        c.setEmail(emailText.getText().toString().trim());
        c.setFullName(fullName);

        userCollection.document(emailText.getText().toString().trim())
                .set(c.toMap())
                .addOnSuccessListener(unused -> {
                    Log.d(TAG, "Successfully added client to FireStore: " + c.toString());
                    updateUI();
                })
                .addOnFailureListener(e -> Log.d(TAG, "Fail to add client to FireStore: " + c.toString()));
    }

    // update UI
    private void updateUI() {
        Intent intent = new Intent(LogInActivity.this, MainActivity.class);
        intent.putExtra("client", client);
        Log.d(TAG, "updateUI, client=" + client.toString());
        startActivity(intent);
        finish();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        // check if from google
        if (requestCode == GOOGLE_SUCCESSFULLY_SIGN_IN){

            // The Task returned from this call is always completed, no need to attach
            // a listener.
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
        Intent intent = new Intent(LogInActivity.this, SignUpStep1Activity.class);
        startActivityForResult(intent, REGISTER_CODE);
    }

    @Override
    public void onConnectionFailed(@NonNull ConnectionResult connectionResult) {

    }

    public void signInWithGoogleBtnClick(View view) {
        Log.d(TAG, "Before going into google");
        Intent intent = Auth.GoogleSignInApi.getSignInIntent(googleApiClient);
        startActivityForResult(intent,GOOGLE_SUCCESSFULLY_SIGN_IN);
    }
}