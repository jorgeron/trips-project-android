package us.master.entregable01;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import us.master.entregable01.entity.Trip;

public class FirebaseDatabaseService {
    private static String userId;
    private static FirebaseDatabaseService service;
    private static FirebaseDatabase mDatabase;

    public static FirebaseDatabaseService getServiceInstance() {
        if (service == null || mDatabase == null) {
            service = new FirebaseDatabaseService();
            mDatabase = FirebaseDatabase.getInstance();
            mDatabase.setPersistenceEnabled(true); // Nos permitirá almacenar datos en local en caso de pérdida de conexión
        }

        if (userId == null || userId.isEmpty()) {
            userId = FirebaseAuth.getInstance().getCurrentUser().getUid() != null ? FirebaseAuth.getInstance().getCurrentUser().getUid() : "";
        }

        return service;
    }

    public DatabaseReference getTrip(String tripId) {
        return mDatabase.getReference("user/" + userId +"/trip/" + tripId).getRef();
    }

    public DatabaseReference getTrip() {
        return mDatabase.getReference("user/" + userId +"/trip/").getRef();
    }

    public void saveTrip(Trip trip, DatabaseReference.CompletionListener completionListener) {
        mDatabase.getReference("user/" + userId +"/trip/").push().setValue(trip, completionListener);
    }
}
