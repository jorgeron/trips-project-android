package us.master.entregable01;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.transition.AutoTransition;
import android.transition.Transition;
import android.transition.TransitionManager;
import android.transition.TransitionSet;
import android.util.Log;
import android.view.View;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInClient;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.common.api.ApiException;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.snackbar.Snackbar;
import com.google.android.material.textfield.TextInputLayout;
import com.google.firebase.auth.AuthCredential;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.GoogleAuthProvider;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;
import java.util.List;

import us.master.entregable01.database.FirebaseDatabaseService;
import us.master.entregable01.database.FirestoreService;
import us.master.entregable01.entity.Trip;

public class LoginActivity extends AppCompatActivity {

    private static final int RC_SIGN_IN = 0x152;
    private FirebaseAuth mAuth;
    private Button signInButtonGoogle;
    private Button signInButtonMail;
    private Button signUpButton;
    private ProgressBar progressBar;
    private TextInputLayout loginEmailParent;
    private TextInputLayout loginPassParent;
    private AutoCompleteTextView loginEmail;
    private AutoCompleteTextView loginPass;

    private ValueEventListener valueEventListener;
    private FirebaseDatabaseService firebaseDatabaseService;
    private FirestoreService firestoreService;

    @RequiresApi(api = Build.VERSION_CODES.O)
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        mAuth = FirebaseAuth.getInstance();

        progressBar = findViewById(R.id.login_progress);
        loginEmail = findViewById(R.id.login_email_et);
        loginPass = findViewById(R.id.login_pass_et);
        loginEmailParent = findViewById(R.id.login_email);
        loginPassParent = findViewById(R.id.login_pass);
        signInButtonGoogle = findViewById(R.id.login_button_google);
        signInButtonMail = findViewById(R.id.login_button_mail);
        signUpButton = findViewById(R.id.login_button_register);

        hideLoginButton(false);

        GoogleSignInOptions googleSignInOptions = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestIdToken(getString(R.string.default_client_id))
                .requestEmail()
                .build();

        signInButtonGoogle.setOnClickListener(l -> attemptLoginGoogle(googleSignInOptions));

        signInButtonMail.setOnClickListener(l -> attemptLoginEmail());

        signUpButton.setOnClickListener(l -> redirectRegisterActivity());
    }

    private void redirectRegisterActivity() {
        Intent intent = new Intent(this, RegisterActivity.class);
        intent.putExtra(RegisterActivity.EMAIL_PARAM, loginEmail.getText().toString());
        startActivity(intent);
    }

    private void attemptLoginGoogle(GoogleSignInOptions googleSignInOptions) {
        GoogleSignInClient googleSignInClient = GoogleSignIn.getClient(this, googleSignInOptions);
        Intent signInIntent = googleSignInClient.getSignInIntent();
        startActivityForResult(signInIntent, RC_SIGN_IN);
    }


    @RequiresApi(api = Build.VERSION_CODES.O)
    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == RC_SIGN_IN) {
            Task<GoogleSignInAccount> result = GoogleSignIn.getSignedInAccountFromIntent(data);
            try {
                GoogleSignInAccount account = result.getResult(ApiException.class);
                assert account != null;
                AuthCredential credential = GoogleAuthProvider.getCredential(account.getIdToken(), null);
                if (mAuth == null) {
                    mAuth = FirebaseAuth.getInstance();
                }
                if (mAuth != null) {
                    mAuth.signInWithCredential(credential).addOnCompleteListener(this, task -> {
                        if (task.isSuccessful()) {
                            FirebaseUser user = task.getResult().getUser();
                            checkUserDatabaseLogin(user);
                        } else {
                            showErrorDialogMail();
                        }
                    });
                } else {
                    showGooglePlayServicesError();
                }
            } catch (ApiException e) {

            }
        }
    }

    @RequiresApi(api = Build.VERSION_CODES.O)
    private void attemptLoginEmail() {
        // Comprobamos si se cumplen restricciones
        loginEmailParent.setError(null);
        loginPassParent.setError(null);

        if(loginEmail.getText().length() == 0 ) {
            loginEmailParent.setErrorEnabled(true);
            loginEmailParent.setError(getString(R.string.login_mail_error_1));
        } else if (loginPass.getText().length() == 0) {
            loginPassParent.setErrorEnabled(true);
            loginPassParent.setError(getString(R.string.login_mail_error_2));
        } else {
            signInEmail();
        }
    }

    @RequiresApi(api = Build.VERSION_CODES.O)
    private void signInEmail() {
        if(mAuth == null) {
            mAuth = FirebaseAuth.getInstance();
        }

        if (mAuth != null) {
            mAuth.signInWithEmailAndPassword(loginEmail.getText().toString(), loginPass.getText().toString()).addOnCompleteListener(this, task -> {
                if (!task.isSuccessful() || task.getResult().getUser() == null) {
                    showErrorDialogMail();
                } else if (!task.getResult().getUser().isEmailVerified()) {
                    showErrorMailVerified(task.getResult().getUser());
                } else {
                    FirebaseUser user = task.getResult().getUser();
                    checkUserDatabaseLogin(user);
                }
            });
        } else {
            showGooglePlayServicesError();
        }
    }

    private void showGooglePlayServicesError() {
        Snackbar.make(signUpButton, R.string.login_google_play_services_error, Snackbar.LENGTH_LONG).setAction(R.string.login_download_gps, view -> {
            try {
                startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse(getString(R.string.gps_download_url))));
            } catch (Exception ex) {
                startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse(getString(R.string.market_dowload_url))));
            }
        });
    }

    @RequiresApi(api = Build.VERSION_CODES.O)
    private void checkUserDatabaseLogin(FirebaseUser user) {
        //TODO
        Toast.makeText(this, String.format(getString(R.string.login_completed), user.getEmail()), Toast. LENGTH_LONG).show();

        firestoreService = FirestoreService.getServiceInstance();
        firestoreService.getTrips(new OnCompleteListener<QuerySnapshot>() {
            @Override
            public void onComplete(@NonNull Task<QuerySnapshot> task) {
                List<Trip> lista = new ArrayList<>();
                for (DocumentSnapshot documentSnapshot : task.getResult().getDocuments()) {
                    Trip t = documentSnapshot.toObject(Trip.class);
                    lista.add(t);
                }
                Log.i("Tripstaa", "lista de trips: " + lista);
            }
        });

        Intent intent = new Intent(LoginActivity.this, EnlacesActivity.class);
        intent.putExtra("currentUser", user);

        startActivity(intent);
        finish();

        /*
        firebaseDatabaseService = FirebaseDatabaseService.getServiceInstance();

        // Trip trip = Trip.generaTrips(1).get(0);
        Trip trip = new Trip(1, "Titulo", "lugarSalida", "urlimg", 1657364153, 1659743877, 1000, false, false);

        firebaseDatabaseService.saveTrip(trip, new DatabaseReference.CompletionListener() {
            @Override
            public void onComplete(@Nullable DatabaseError databaseError, @NonNull DatabaseReference databaseReference) {
                if (databaseError == null) {
                    Log.i("TripsApp", "Viaje insertado");
                } else {
                    Log.i("TripsApp", "Error al insertar viaje " + databaseError.getMessage());
                }
            }
        });


        //Este método nos devuelve el snapshot de ese elemento en ese instante concreto. El get de toda la vida
        firebaseDatabaseService.getTrip("-M5kmOYu0fc7zSVU1mrb").addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if (dataSnapshot.exists() && dataSnapshot.getValue() != null) {
                    Trip trip = dataSnapshot.getValue(Trip.class);

                    Toast.makeText(LoginActivity.this, trip.toString(), Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });


        //Con este método nos subscribimos para estar al tanto de modificaciones del elemento
        valueEventListener = firebaseDatabaseService.getTrip("-M5kmOYu0fc7zSVU1mrb").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if (dataSnapshot.exists() && dataSnapshot.getValue() != null) {
                    Trip trip = dataSnapshot.getValue(Trip.class);
                    Log.i("TripsApp", "Elemento modificado individualmente: " + trip.toString());
                    Toast.makeText(LoginActivity.this, trip.toString(), Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });


        ChildEventListener childEventListener = firebaseDatabaseService.getTrip().addChildEventListener(new ChildEventListener() {
            @Override
            public void onChildAdded(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {
                if (dataSnapshot.exists() && dataSnapshot.getValue() != null) {
                    Trip trip = dataSnapshot.getValue(Trip.class);

                    Log.i("TripsApp", "Nuevo viaje añadido: " + trip.toString());
                }
            }

            @Override
            public void onChildChanged(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {
                if (dataSnapshot.exists() && dataSnapshot.getValue() != null) {
                    Trip trip = dataSnapshot.getValue(Trip.class);

                    Log.i("TripsApp", "Viaje modificado: " + trip.toString());
                }
            }

            @Override
            public void onChildRemoved(@NonNull DataSnapshot dataSnapshot) {
                if (dataSnapshot.exists() && dataSnapshot.getValue() != null) {
                    Trip trip = dataSnapshot.getValue(Trip.class);

                    Log.i("TripsApp", "Viaje eliminado: " + trip.toString());
                }
            }

            @Override
            public void onChildMoved(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {
                if (dataSnapshot.exists() && dataSnapshot.getValue() != null) {
                    Trip trip = dataSnapshot.getValue(Trip.class);

                    Log.i("TripsApp", "Viaje movido: " + trip.toString());
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
         */
    }

    @Override
    protected void onPause() {
        super.onPause();
        if (firebaseDatabaseService != null && valueEventListener != null) {
            //Para el unsubscribe
            firebaseDatabaseService.getTrip("-M5kmOYu0fc7zSVU1mrb").removeEventListener(valueEventListener);
        }
    }

    private void showErrorMailVerified(FirebaseUser user) {
        hideLoginButton(false);
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setMessage(R.string.login_verified_mail_error)
                .setPositiveButton(R.string.login_verified_mail_ok, ((dialog, which) -> {
                    user.sendEmailVerification().addOnCompleteListener(task1 -> {
                        if(task1.isSuccessful()) {
                            Snackbar.make(loginEmail, R.string.login_verified_mail_error_sent, Snackbar.LENGTH_SHORT).show();
                        } else {
                            Snackbar.make(loginEmail, R.string.login_verified_mail_no_error_sent, Snackbar.LENGTH_SHORT).show();
                        }
                    });
                })).setNegativeButton(R.string.login_verified_mail_error_cancel, (dialog, which) -> {
        }).show();
    }

    private void hideLoginButton(boolean hide) {
        TransitionSet transitionSet = new TransitionSet();
        Transition layoutFade = new AutoTransition();
        layoutFade.setDuration(1000);
        transitionSet.addTransition(layoutFade);

        if (hide) {
            TransitionManager.beginDelayedTransition(findViewById(R.id.login_main_layout), transitionSet);
            progressBar.setVisibility(View.VISIBLE);
            signInButtonMail.setVisibility(View.GONE);
            signInButtonGoogle.setVisibility(View.GONE);
            signUpButton.setVisibility(View.GONE);
            loginEmailParent.setEnabled(false);
            loginPassParent.setEnabled(false);
        } else {
            TransitionManager.beginDelayedTransition(findViewById(R.id.login_main_layout), transitionSet);
            progressBar.setVisibility(View.GONE);
            signInButtonMail.setVisibility(View.VISIBLE);
            signInButtonGoogle.setVisibility(View.VISIBLE);
            signUpButton.setVisibility(View.VISIBLE);
            loginEmailParent.setEnabled(true);
            loginPassParent.setEnabled(true);
        }
    }

    private void showErrorDialogMail() {
        hideLoginButton(false);
        Snackbar.make(signInButtonMail, getString(R.string.login_mail_access_error), Snackbar.LENGTH_SHORT).show();
    }
}
