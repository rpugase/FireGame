package com.andriigridin.firegame;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.widget.FrameLayout;

import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.HashMap;
import java.util.Map;

public class MainActivity extends AppCompatActivity implements ChildEventListener, CircleButton.OnMoveEventListener {

    private FrameLayout frameLayout;

    private FirebaseDatabase db = FirebaseDatabase.getInstance();
    private DatabaseReference circleReference = db.getReference(Circle.class.getSimpleName());
    private String key;
    private Map<String, CircleButton> circleMap = new HashMap<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        frameLayout = new FrameLayout(this);
        setContentView(frameLayout);
    }

    @Override
    protected void onStart() {
        super.onStart();
        final DatabaseReference circle = circleReference.push();
        circle.setValue(new Circle());
        key = circle.getKey();

        circleReference.addChildEventListener(this);
    }

    @Override
    protected void onStop() {
        for (Map.Entry<String, CircleButton> entry : circleMap.entrySet()) {
            frameLayout.removeView(entry.getValue());
        }
        circleMap.clear();

        circleReference.child(key).removeValue();
        circleReference.removeEventListener(this);
        super.onStop();
    }

    @Override
    public void onChildAdded(DataSnapshot dataSnapshot, String s) {
        final CircleButton circleButton = new CircleButton(this);

        if (dataSnapshot.getKey().equals(key)) {
            circleButton.identifyButton();
            circleButton.setOnMoveEventListener(this);
        }

        frameLayout.addView(circleButton);
        circleMap.put(dataSnapshot.getKey(), circleButton);
    }

    @Override
    public void onChildChanged(DataSnapshot dataSnapshot, String s) {
        if (!dataSnapshot.getKey().equals(key)) {
            final Circle circle = dataSnapshot.getValue(Circle.class);

            if (circle != null) {
                final FrameLayout.LayoutParams lParams = (FrameLayout.LayoutParams) circleMap.get(dataSnapshot.getKey()).getLayoutParams();
                lParams.leftMargin = circle.x;
                lParams.topMargin = circle.y;
                lParams.bottomMargin = getResources().getDimensionPixelSize(R.dimen.buttonSize);
                lParams.rightMargin = getResources().getDimensionPixelSize(R.dimen.buttonSize);
                circleMap.get(dataSnapshot.getKey()).setLayoutParams(lParams);
            }
        }
    }

    @Override
    public void onChildRemoved(DataSnapshot dataSnapshot) {
        frameLayout.removeView(circleMap.get(dataSnapshot.getKey()));
        circleMap.remove(dataSnapshot.getKey());
    }

    @Override
    public void onChildMoved(DataSnapshot dataSnapshot, String s) {

    }

    @Override
    public void onCancelled(DatabaseError databaseError) {

    }

    @Override
    public void onMoveEvent(int x, int y) {
        circleReference.child(key).setValue(new Circle(x, y));
    }
}
