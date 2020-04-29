package us.master.entregable01.database;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.ListenerRegistration;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.Map;

import us.master.entregable01.entity.Trip;

public class FirestoreService {

    private static final String USERS_COLLECTION_NAME = "users";
    private static final String TRIPS_COLLECTION_NAME = "trips";

    private static String userId;
    private static FirebaseFirestore mDatabase;
    private static FirestoreService service;

    public static FirestoreService getServiceInstance() {
        if (service == null || mDatabase == null) {
            mDatabase = FirebaseFirestore.getInstance();
            service = new FirestoreService();
        }

        if (userId == null || userId.isEmpty()) {
            userId = FirebaseAuth.getInstance().getCurrentUser().getUid() != null ? FirebaseAuth.getInstance().getCurrentUser().getUid() : "";
        }

        return service;
    }

    public void saveTrip(Trip trip, OnCompleteListener<DocumentReference> listener) {
        mDatabase.collection(USERS_COLLECTION_NAME).document(userId).collection(TRIPS_COLLECTION_NAME).add(trip).addOnCompleteListener(listener);
    }

    public void getTrips(OnCompleteListener<QuerySnapshot> querySnapshotOnCompleteListener) {
        mDatabase.collection(USERS_COLLECTION_NAME).document(userId).collection(TRIPS_COLLECTION_NAME).get().addOnCompleteListener(querySnapshotOnCompleteListener);
    }

    //Mirar cómo filtrar por varios parámetros. ¿HAcer varias consultas?
    public void getFilteredTrips(Long fechaSalida, OnCompleteListener<QuerySnapshot> querySnapshotOnCompleteListener) {
        mDatabase.collection(USERS_COLLECTION_NAME).document(userId).collection(TRIPS_COLLECTION_NAME).whereGreaterThanOrEqualTo("fechaSalida", fechaSalida).get().addOnCompleteListener(querySnapshotOnCompleteListener);
    }

    public ListenerRegistration getSelectedTrips(EventListener<QuerySnapshot> querySnapshotEventListener) {
        return mDatabase.collection(USERS_COLLECTION_NAME).document(userId).collection(TRIPS_COLLECTION_NAME).whereEqualTo("seleccionado", true).addSnapshotListener(querySnapshotEventListener);
    }

    public void getTrip(String idTrip, EventListener<DocumentSnapshot> snapshotListener) {
        mDatabase.collection(USERS_COLLECTION_NAME).document(userId).collection(TRIPS_COLLECTION_NAME).document(idTrip).addSnapshotListener(snapshotListener);
    }

    public ListenerRegistration getTrips(EventListener<QuerySnapshot> querySnapshotEventListener) {
        return mDatabase.collection(USERS_COLLECTION_NAME).document(userId).collection(TRIPS_COLLECTION_NAME).addSnapshotListener(querySnapshotEventListener);
    }

    public void updateTrip(String idTrip, Map<String, Object> data) {
        mDatabase.collection(USERS_COLLECTION_NAME).document(userId).collection(TRIPS_COLLECTION_NAME).document(idTrip).update(data);
    }

    public void deleteTrip(String idTrip) {
        mDatabase.collection(USERS_COLLECTION_NAME).document(userId).collection(TRIPS_COLLECTION_NAME).document(idTrip).delete();
    }
}
