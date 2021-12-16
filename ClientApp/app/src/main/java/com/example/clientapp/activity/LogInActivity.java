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
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthCredential;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.GoogleAuthProvider;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

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
import java.util.HashMap;
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
//    private FirebaseDatabase firebaseDatabase;
//    private DatabaseReference databaseReference;
    private FirebaseFirestore fireStore ;
    private CollectionReference userCollection;
    private static final String CLIENT_COLLECTION = "clients";

    private FirebaseAuth.AuthStateListener authStateListener;
    private GoogleApiClient googleApiClient;
    private List<Client> clientList;


    String idToken;

    private SignInButton signInGoogleButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_log_in);

        // init services
        attachComponents();
        initService();

        // sign in gg btn
        signInGoogleButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Log.d(TAG, "Before going into google");
                Intent intent = Auth.GoogleSignInApi.getSignInIntent(googleApiClient);
                startActivityForResult(intent,GOOGLE_SUCCESSFULLY_SIGN_IN);
            }
        });

    }

    // init services
    public void initService(){

        // init firebase services
        firebaseAuth = FirebaseAuth.getInstance();

        // init fireStore db
        fireStore = FirebaseFirestore.getInstance();
        userCollection = fireStore.collection(CLIENT_COLLECTION);

//        firebaseDatabase = FirebaseDatabase.getInstance("https://a2-android-56cbb-default-rtdb.asia-southeast1.firebasedatabase.app/");
//        databaseReference = firebaseDatabase.getReference();

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
        userCollection.addSnapshotListener(new EventListener<QuerySnapshot>() {
            @Override
            public void onEvent(@Nullable QuerySnapshot value, @Nullable FirebaseFirestoreException error) {

                for (QueryDocumentSnapshot doc : value) {

                    Log.d(TAG, "onEvent: " + doc.get("name"));
                }
            }
        });
//        databaseReference.child("users").addValueEventListener(new ValueEventListener() {
//            @Override
//            public void onDataChange(@NonNull DataSnapshot snapshot) {
//                // This method is called once with the initial value and again
//                // whenever data at this location is updated.
//
//                GenericTypeIndicator<HashMap<String, User>> genericTypeIndicator =new GenericTypeIndicator<HashMap<String, User>>(){};
//
//                HashMap<String,User> users= snapshot.getValue(genericTypeIndicator);
//
//                try {
//                    for (User u : users.values() ){
//                        Log.d(TAG, "Value is: " + u.getEmail());
//                        userList.add(u);
//                    }
//                } catch (Exception e){
//                    Log.d(TAG, "Cannot load the users");
//                }
//
//            }
//
//            @Override
//            public void onCancelled(@NonNull DatabaseError error) {
//
//            }
//        });


        GoogleSignInOptions gso =  new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestIdToken(getString(R.string.web_client_id))//you can also use R.string.default_web_client_id
                .requestEmail()
                .build();

        googleApiClient=new GoogleApiClient.Builder(this)
                .enableAutoManage(this, this)
                .addApi(Auth.GOOGLE_SIGN_IN_API,gso)
                .build();

    }

    // attach components with xml
    public void attachComponents(){
        passwordText = findViewById(R.id.passwordTxt);
        emailText = findViewById(R.id.editEmailLogInTxt);
        errorLoginTxt = findViewById(R.id.errorLoginTxt);
        errorLoginTxt.setVisibility(View.INVISIBLE);
        signInGoogleButton = findViewById(R.id.signInWithGoogle);

        TextView textView = (TextView) signInGoogleButton.getChildAt(0);
        textView.setText("Sign in with Google");
    }

    public void normalLogIn(View view) {

        //TODO: Take firestore from c Phuc to validate ne


//        // validate in case it cannot sign in with authentication
//        try {
//            //TODO: remember to change back to normal way
//            firebaseAuth.signInWithEmailAndPassword(emailText.getText().toString(), passwordText.getText().toString())
////            firebaseAuth.signInWithEmailAndPassword("2@gmail.com" , "123456")
//                    .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
//                        @Override
//                        public void onComplete(@NonNull Task<AuthResult> task) {
//                            if (task.isSuccessful()){
//
//                                // Sign in success, update UI with signed-in user's information
//                                Log.d(TAG, "signInWithEmail:success");
////                                Toast.makeText(LogInActivity.this, "Authentication success", Toast.LENGTH_SHORT).show();
//
//                                FirebaseUser userFirebase = firebaseAuth.getCurrentUser();
//
//                                User user;
//                                Log.d(TAG, userFirebase.getEmail() + " mail1");
//
//                                try {
//                                    // get the user from realtime db
//                                    user = searchUser(userFirebase.getEmail());
//                                    Log.d(TAG, user.getEmail().toString());
//
//                                    // update UI (send intent)
//                                    updateUI(user);
//
//
//                                } catch (Exception e){
//                                    Log.d(TAG, "Cannot validate the user in firestone");
//
//                                }
//
//                            }else {
//
//                                // if sign in fails, display a message to the user
//                                Log.w(TAG, "signInWithEmail:failure", task.getException());
////                                Toast.makeText(LogInActivity.this, "Authentication failed", Toast.LENGTH_SHORT).show();
//                            }
//                        }
//                    });
//
//        } catch (Exception e){
//            errorLoginTxt.setVisibility(View.VISIBLE);
//            errorLoginTxt.setText("Please enter your mail and password.");
//            return;
//        }
    }

    // handle sign in with google
    private void handleSignInResult(GoogleSignInResult result){

        // Check if the result is successful
        if(result.isSuccess()){
            // get the account
            GoogleSignInAccount account = result.getSignInAccount();
            idToken = account.getIdToken();


            // you can store user data to SharedPreference
            AuthCredential credential = GoogleAuthProvider.getCredential(idToken, null);
            firebaseAuthWithGoogle(credential);
        }else{
            // Google Sign In failed, update UI appropriately
            Log.e(TAG, "Login Unsuccessful. "+result);
//            Toast.makeText(this, "Login Unsuccessful", Toast.LENGTH_SHORT).show();
        }
    }

    // firebaseAuth with GG
    private void firebaseAuthWithGoogle(AuthCredential credential){

        // sign in with gg
        firebaseAuth.signInWithCredential(credential)
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        Log.d(TAG, "signInWithCredential:onComplete:" + task.isSuccessful());
                        if(task.isSuccessful()){
//                            Toast.makeText(LogInActivity.this, "Login successful", Toast.LENGTH_SHORT).show();

                            // get the current logged in user
                            FirebaseUser userFirebase = firebaseAuth.getCurrentUser();
                            Log.d(TAG,userFirebase.getDisplayName() + " name" );
                            Log.d(TAG, Objects.requireNonNull(userFirebase).getEmail() + " email");



                            // create the user
                            Client client = new Client(userFirebase.getDisplayName(), userFirebase.getEmail(), userFirebase.getPhoneNumber());

                            //TODO: Switch to fireStore
//                            databaseReference.child("users").child(user.getName()).setValue(user.toMap());

                            // update the UI
                            updateUI(client);
                        }else{
                            Log.w(TAG, "signInWithCredential" + task.getException().getMessage());
                            task.getException().printStackTrace();
                            errorLoginTxt.setVisibility(View.VISIBLE);
                            errorLoginTxt.setText("Cannot found the account in the system.\nPlease check again the password and mail");
//                            Toast.makeText(LogInActivity.this, "Authentication failed.",
//                                    Toast.LENGTH_SHORT).show();
                        }

                    }
                });
    }

    // update UI
    private void updateUI(Client client) {


//        Intent intent = new Intent(LogInActivity.this, MapsActivity.class);

//        intent.putExtra("user",user );
//        setResult(RESULT_OK, intent);
//        finish();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        // check if from google
        if (requestCode == GOOGLE_SUCCESSFULLY_SIGN_IN){

            // The Task returned from this call is always completed, no need to attach
            // a listener.
            GoogleSignInResult result = Auth.GoogleSignInApi.getSignInResultFromIntent(data);
            handleSignInResult(result);

            return;
        }
    }

    public void signUpActivity(View view) {

        Intent intent = new Intent(LogInActivity.this, RegisterActivity.class);
        startActivityForResult(intent, REGISTER_CODE);
    }

    @Override
    public void onConnectionFailed(@NonNull ConnectionResult connectionResult) {

    }
}