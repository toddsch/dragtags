package website.todds.demo.dragtag;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.LinearLayout;

import website.todds.dragtag.DragTag;
import website.todds.dragtag.DragTagListener;

public class MainActivity extends AppCompatActivity implements DragTag.OnPrimaryClickListener, DragTag.OnSecondaryClickListener, DragTag.OnDropListener {

    private LinearLayout container1;
    private LinearLayout container2;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        container1 = findViewById(R.id.container_1);
        container2 = findViewById(R.id.container_2);

        DragTagListener dragListener = new DragTagListener();
        container1.setOnDragListener(dragListener);
        container2.setOnDragListener(dragListener);

        setupDragTag();
    }

    private void setupDragTag() {
        String[] terms = {"Artist","Album","Title","Track"};
        for (int i = 0; i < terms.length; i++) {
            DragTag tag = (DragTag) LayoutInflater.from(getApplicationContext()).inflate(R.layout.dragtag, null);
            tag.setOnPrimaryClick(this);
            tag.setOnSecondaryClick(this);
            tag.setOnDropListener(this);
            tag.setText(terms[i]);
            container1.addView(tag);
        }
    }

//    private void setupDragTag() {
//        final DragTag item = findViewById(R.id.dragtag);
//        final DragTag item2 = findViewById(R.id.dragtag2);
//        final DragTag item3 = findViewById(R.id.dragtag3);
//        final DragTag item4 = findViewById(R.id.dragtag4);
//        final DragTag item5 = findViewById(R.id.dragtag5);
//
//        item.setText("Duration");
//        item2.setText("Artist");
//        item3.setText("Album");
//        item4.setText("Title");
//        item5.setText("Track");
//
//        item.setOnPrimaryClick(this);
//        item.setOnSecondaryClick(this);
//        item.setOnDropListener(this);
//
//        item2.setOnPrimaryClick(this);
//        item2.setOnSecondaryClick(this);
//        item2.setOnDropListener(this);
//
//        item3.setOnPrimaryClick(this);
//        item3.setOnSecondaryClick(this);
//        item3.setOnDropListener(this);
//
//        item4.setOnPrimaryClick(this);
//        item4.setOnSecondaryClick(this);
//        item4.setOnDropListener(this);
//
//        item5.setOnPrimaryClick(this);
//        item5.setOnSecondaryClick(this);
//        item5.setOnDropListener(this);
//    }

    @Override
    public void onPrimaryClick(DragTag dragTag, ImageButton primaryButton) {

    }

    @Override
    public void onSecondaryClick(DragTag dragTag, ImageButton secondaryButton) {
        // Delete clicked, move back to main container
        dragTag.moveTo(container1);
    }

    @Override
    public void onDrop(DragTag dragTag, ViewGroup previous, ViewGroup newGroup) {
        // Hide buttons if in container1, show otherwise
        int vis = newGroup.getId() == R.id.container_1 ? View.GONE : View.VISIBLE;
        dragTag.setButtonsVisibility(vis, vis);
    }
}
