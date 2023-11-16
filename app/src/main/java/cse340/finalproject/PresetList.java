package cse340.finalproject;

import android.widget.TableLayout;
import android.content.Context;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.TableRow;

public class PresetList extends TableLayout{
    private String presetText;

    // THIS FILE INSPIRED BY AS3 EditableList.java
    public interface presetRemovedListener {
        void presetRemoved(String name);
    }
    private View.OnClickListener presetClickListener;
    private presetRemovedListener presetRemovedListener;

    public PresetList(Context context) {
        this(context, null);
    }

    public PresetList(Context context, AttributeSet attrs) {
        super(context, attrs);
        presetText = "";
    }


    public void setPresetClickListener(View.OnClickListener listener) {
        presetClickListener = listener;
    }

    public void setItemRemovedListener(presetRemovedListener listener) {
        presetRemovedListener = listener;
    }

    /**
     * This method returns the number of presets in the layout
     * @return number of presets
     * CODE INSPIRED FROM AS3 EditRequestsActivity.java
     */

    public int numPresets() {
        return getChildCount();
    }

    /**
     * This method gets the preset text which is inside the button that has been clicked
     * @param index the index (row) of the button that has been clicked
     * CODE INSPIRED FROM AS3 EditRequestsActivity.java
     */
    public String getPresetText(int index) {
        TableRow row = (TableRow) getChildAt(index);
        Button presetButton = row.findViewById(R.id.pair_name);
        return presetButton.getText().toString();
    }

    /**
     * This method adds a new preset
     * CODE INSPIRED FROM AS3 EditRequestsActivity.java
     */

    public void createPreset(String name) {
        addPreset(name);
    }

    /**
     * This method adds a new preset pair to the layout which is inflated from the
     * preset_activity.xml
     * CODE INSPIRED FROM AS3 EditableList.java
     */
    public void addPreset(String name) {
        TableRow row = (TableRow) LayoutInflater.from(getContext())
                .inflate(R.layout.preset_pair, this, false);
        addView(row);

        // Get references to both of the views in our item pair
        Button presetButton = row.findViewById(R.id.pair_name);
        ImageButton deleteButton = row.findViewById(R.id.pair_delete);

        presetButton.setText(name);

        // Setup the callbacks for clicking the delete button in the item pair
        deleteButton.setOnClickListener(button -> {
            removeView((View) button.getParent());
            if (presetRemovedListener != null) {
                presetRemovedListener.presetRemoved(name);
            }
        });

        if (presetClickListener != null) {
            presetButton.setOnClickListener(presetClickListener);
        }

    }


}


