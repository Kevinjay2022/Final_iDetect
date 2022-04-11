package com.example.idetect;

import static android.content.ContentValues.TAG;

import android.app.ProgressDialog;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.example.idetect.Notify.APIService;
import com.example.idetect.Notify.Client;
import com.example.idetect.Notify.Constant;
import com.example.idetect.Notify.Data;
import com.example.idetect.Notify.MyResponse;
import com.example.idetect.Notify.Sender;
import com.example.idetect.Notify.Token;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.database.annotations.NotNull;
import com.squareup.picasso.Picasso;

import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.Locale;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class FragmentDriverVisitShop extends AppCompatActivity {
    TextView shopName, shopAddress, shopContact, visitDesc, visitIssue, visitMsg;
    ImageView shopImage;
    Button shopBackBTN, proceedBTN, waitingBTN, cancelBTN, completeBTN;
    String shopUID;
    boolean notify = false;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.fragment_driver_visit_shop);

        visitMsg = findViewById(R.id.visitApproveMsg);
        completeBTN = findViewById(R.id.visitComplete);
        visitIssue = findViewById(R.id.visitIssue);
        proceedBTN = findViewById(R.id.visitProceed);
        waitingBTN = findViewById(R.id.visitWaiting);
        cancelBTN = findViewById(R.id.visitCancel);
        visitDesc = findViewById(R.id.visitDescription);
        shopBackBTN = findViewById(R.id.visitBackButton);
        shopName = findViewById(R.id.visitShopName);
        shopAddress = findViewById(R.id.visitLocation);
        shopContact = findViewById(R.id.visitContact);
        shopImage = findViewById(R.id.visitImage);
        shopUID = getIntent().getStringExtra("shopID");

        FirebaseDatabase.getInstance().getReference().child("DRIVER_SERVICE_CENT_ISSUE").orderByChild("ID")
                .equalTo(FirebaseAuth.getInstance().getCurrentUser().getUid())
                .addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        for (DataSnapshot ds : snapshot.getChildren()){
                            String shopid = ds.child("shopID").getValue(String.class);
                            String feedback = ds.child("feedback").getValue(String.class);
                            if (shopid.equals(shopUID)) {
                                if(feedback.equals("pending")) {
                                    proceedBTN.setVisibility(View.GONE);
                                    waitingBTN.setVisibility(View.VISIBLE);
                                    cancelBTN.setVisibility(View.VISIBLE);
                                }
                                if (feedback.equals("accept")){
                                    proceedBTN.setVisibility(View.GONE);
                                    visitMsg.setVisibility(View.VISIBLE);
                                    completeBTN.setVisibility(View.VISIBLE);
                                }
                            }
                        }

                    }
                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {

                    }
                });

        FirebaseDatabase.getInstance().getReference("USERS").child(shopUID)
                .addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        String name = snapshot.child("name").getValue(String.class);
                        String add = snapshot.child("address").getValue(String.class);
                        String phone = snapshot.child("phonenum").getValue(String.class);
                        String image = ""+ snapshot.child("image").getValue();
                        shopName.setText(name);
                        shopAddress.setText(add);
                        shopContact.setText(phone);
                        if (image.isEmpty())
                            Picasso.get().load(R.drawable.customers_logo_nav).into(shopImage);
                        else
                            Picasso.get().load(image).into(shopImage);
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {

                    }
                });

        FirebaseDatabase.getInstance().getReference().child("SERVICE_CENT_DESC_SERVICES").child(shopUID)
                .addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        if (snapshot.exists()){
                            String desc = snapshot.child("description").getValue().toString();
                            visitDesc.setText(desc);
                        }
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {

                    }
                });

        /*DatabaseReference ref = FirebaseDatabase.getInstance().getReference();
        Query applesQuery = ref.child("DRIVER_SERVICE_CENT_ISSUE").orderByChild("ID")
                .equalTo(FirebaseAuth.getInstance().getCurrentUser().getUid());

        applesQuery.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                String feedback = "";
                for (DataSnapshot appleSnapshot: snapshot.getChildren()) {
                    if (shopUID.equals(appleSnapshot.child("shopID").getValue(String.class))){
                        feedback = appleSnapshot.child("feedback").getValue(String.class);

                        if (feedback.equals("accept")){
                            proceedBTN.setVisibility(View.GONE);
                            visitMsg.setVisibility(View.VISIBLE);
                            completeBTN.setVisibility(View.VISIBLE);
                        }
                    }
                }

            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });*/

        shopBackBTN.setOnClickListener(view -> onBackPressed());
        proceedBTN.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String ed_issue = visitIssue.getText().toString().trim();
                if (ed_issue.isEmpty()){
                    visitIssue.setError("Field should not be empty!");
                    return;
                }else{

                    notify = true;
                    ProgressDialog pd = new ProgressDialog(FragmentDriverVisitShop.this);
                    pd.setMessage("Sending request...");
                    pd.show();

                    Date d = new Date(System.currentTimeMillis());
                    Calendar c = Calendar.getInstance();
                    c.setTime(d);
                    String month = c.getDisplayName(Calendar.MONTH, Calendar.LONG, Locale.getDefault());
                    String monthReg = month.substring(0, 3);

                    String key = FirebaseDatabase.getInstance().getReference().child("DRIVER_SERVICE_CENT_ISSUE").push().getKey();
                    HashMap<String, Object> hashMap = new HashMap<>();
                    hashMap.put("issue", ed_issue);
                    hashMap.put("key", key);
                    hashMap.put("ID", FirebaseAuth.getInstance().getCurrentUser().getUid());
                    hashMap.put("feedback", "pending");
                    hashMap.put("monthReg", monthReg);
                    hashMap.put("shopID", shopUID);
                    hashMap.put("seen", "new");

                    assert key != null;
                    FirebaseDatabase.getInstance().getReference().child("DRIVER_SERVICE_CENT_ISSUE").child(key).setValue(hashMap)
                            .addOnSuccessListener(new OnSuccessListener<Void>() {
                                @Override
                                public void onSuccess(Void unused) {
                                    Toast.makeText(FragmentDriverVisitShop.this, "Request sent...\nPlease wait for shop confirmation.", Toast.LENGTH_SHORT).show();
                                    FirebaseDatabase.getInstance().getReference().child("USERS").child(FirebaseAuth.getInstance().getCurrentUser().getUid())
                                            .addValueEventListener(new ValueEventListener() {
                                                @Override
                                                public void onDataChange(@NonNull DataSnapshot snapshot) {
                                                    if (snapshot.exists()) {
                                                        if (notify) {
                                                            String name = snapshot.child("firstname").getValue().toString()+ " " +snapshot.child("lastname").getValue().toString();
                                                            sendNotification(shopUID, name, "Sent a request.", "serve_center");
                                                        }
                                                        notify = false;
                                                    }
                                                }

                                                @Override
                                                public void onCancelled(@NonNull DatabaseError error) {

                                                }
                                            });

                                    visitIssue.setText(null);
                                    proceedBTN.setVisibility(View.GONE);
                                    waitingBTN.setVisibility(View.VISIBLE);
                                    cancelBTN.setVisibility(View.VISIBLE);
                                    pd.dismiss();

                                }
                            }).addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception e) {
                            Toast.makeText(FragmentDriverVisitShop.this, "Error!", Toast.LENGTH_SHORT).show();
                            pd.dismiss();
                        }
                    });
                }
            }
        });
        cancelBTN.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                notify = true;
                DatabaseReference ref = FirebaseDatabase.getInstance().getReference();
                Query applesQuery = ref.child("DRIVER_SERVICE_CENT_ISSUE").orderByChild("ID")
                        .equalTo(FirebaseAuth.getInstance().getCurrentUser().getUid());

                applesQuery.addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot dataSnapshot) {
                        for (DataSnapshot appleSnapshot: dataSnapshot.getChildren()) {
                            String shopid = appleSnapshot.child("shopID").getValue(String.class);
                            if (shopid.equals(shopUID))
                                appleSnapshot.getRef().removeValue();
                        }
                        FirebaseDatabase.getInstance().getReference().child("USERS").child(FirebaseAuth.getInstance().getCurrentUser().getUid())
                                .addValueEventListener(new ValueEventListener() {
                                    @Override
                                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                                        if (snapshot.exists()) {
                                            if (notify) {
                                                String name = snapshot.child("firstname").getValue().toString()+ " " +snapshot.child("lastname").getValue().toString();
                                                sendNotification(shopUID, name, "Cancelled request.", "serve_center");
                                            }
                                            notify = false;
                                        }
                                    }

                                    @Override
                                    public void onCancelled(@NonNull DatabaseError error) {

                                    }
                                });
                        Toast.makeText(FragmentDriverVisitShop.this, "Request cancelled...", Toast.LENGTH_SHORT).show();
                        proceedBTN.setVisibility(View.VISIBLE);
                        waitingBTN.setVisibility(View.GONE);
                        cancelBTN.setVisibility(View.GONE);
                    }

                    @Override
                    public void onCancelled(DatabaseError databaseError) {
                        Log.e(TAG, "onCancelled", databaseError.toException());
                    }
                });
            }
        });
        completeBTN.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                DatabaseReference ref = FirebaseDatabase.getInstance().getReference();
                Query applesQuery = ref.child("DRIVER_SERVICE_CENT_ISSUE").orderByChild("ID")
                        .equalTo(FirebaseAuth.getInstance().getCurrentUser().getUid());

                applesQuery.addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        for (DataSnapshot appleSnapshot: snapshot.getChildren()) {
                            if (shopUID.equals(appleSnapshot.child("shopID").getValue(String.class))){
                                String key = appleSnapshot.child("key").getValue(String.class);
                                HashMap<String, Object> hashMap = new HashMap<>();
                                hashMap.put("feedback", "finish");

                                FirebaseDatabase.getInstance().getReference().child("DRIVER_SERVICE_CENT_ISSUE").child(key).updateChildren(hashMap)
                                        .addOnSuccessListener(new OnSuccessListener<Void>() {
                                            @Override
                                            public void onSuccess(Void unused) {
                                                Toast.makeText(getApplicationContext(), "Issue Completed", Toast.LENGTH_SHORT).show();
                                                proceedBTN.setVisibility(View.VISIBLE);
                                                waitingBTN.setVisibility(View.GONE);
                                                completeBTN.setVisibility(View.GONE);
                                            }
                                        }).addOnFailureListener(new OnFailureListener() {
                                    @Override
                                    public void onFailure(@NonNull Exception e) {
                                        Toast.makeText(getApplicationContext(), "Error!", Toast.LENGTH_SHORT).show();
                                    }
                                });
                            }
                        }
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {

                    }
                });
            }
        });
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
    }
    private void sendNotification(String receiver, String senderName, String msg, String on) {

        Query query = Constant.tokens.orderByKey().equalTo(receiver);
        query.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                for(DataSnapshot ds : snapshot.getChildren()){
                    Token token = ds.getValue(Token.class);
                    Data data = new Data(FirebaseAuth.getInstance().getCurrentUser().getUid(), R.drawable.logo, msg, senderName, receiver, on);

                    assert token != null;
                    Sender sender = new Sender(data, token.getToken());

                    Constant.apiService.sendNotification(sender)
                            .enqueue(new Callback<MyResponse>() {
                                @Override
                                public void onResponse(@NotNull Call<MyResponse> call, @NotNull Response<MyResponse> response) {
                                    if (response.code() == 200) {
                                        assert response.body() != null;
                                        if(response.body().success != 1){}
                                        //Toast.makeText(context, "Failed", Toast.).show();
                                    }
                                }
                                @Override
                                public void onFailure(@NotNull Call<MyResponse> call, @NotNull Throwable t) {
                                }
                            });
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }
}
