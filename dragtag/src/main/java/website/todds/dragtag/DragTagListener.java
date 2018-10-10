package website.todds.dragtag;

import android.view.DragEvent;
import android.view.View;
import android.view.ViewGroup;

public class DragTagListener implements View.OnDragListener {

    @Override
    public boolean onDrag(View v, DragEvent event) {
        switch (event.getAction()) {
            case DragEvent.ACTION_DRAG_STARTED: {
                DragTag item = (DragTag) event.getLocalState();
                item.setVisibility(View.INVISIBLE);
                return true;
            }
            case DragEvent.ACTION_DRAG_ENTERED: {
                return true;
            }
            case DragEvent.ACTION_DRAG_LOCATION: {
                return true;
            }
            case DragEvent.ACTION_DRAG_EXITED: {
                return true;
            }
            case DragEvent.ACTION_DROP: {
                DragTag item = (DragTag) event.getLocalState();
                item.moveTo((ViewGroup) v);
                return true;
            }
            case DragEvent.ACTION_DRAG_ENDED: {
                ((View) event.getLocalState()).setVisibility(View.VISIBLE);
            }
        }
        return false;
    }
}
