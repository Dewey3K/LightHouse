package cse340.finalproject;

import android.app.AlertDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.text.InputType;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.google.android.material.floatingactionbutton.FloatingActionButton;

import java.util.LinkedHashSet;
import java.util.Set;

public class PresetActivity extends AppCompatActivity {

    /** A PresetList containing all presets in the application */
    private PresetList presList;

    /** SharedPreferences File which stores the presets */
    protected SharedPreferences mSharedPreferences;

    /** The key for preset button text for the intent */
    private final String PRESETS_KEY = "preset_text";

    /**
     * Callback that is called when the activity is first created.
     * @param savedInstanceState contains the activity's previously saved state.
     */
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.preset_activity);

        FloatingActionButton addPresetRow = findViewById(R.id.addPresetRow);

        addPresetRow.setOnClickListener(v -> startNewRequest());

        presList = findViewById(R.id.presetList);

        // creates an intent to MainActivity with the text of the preset button
        // when it gets clicked
        presList.setPresetClickListener(v -> {
            Intent intent = new Intent(PresetActivity.this, MainActivity.class);
            intent.putExtra(PRESETS_KEY, ((Button)v).getText().toString());
            startActivity(intent);
        });
        presList.setItemRemovedListener(name -> syncPresets());

        loadPresets();

        Button homeButton = findViewById(R.id.goMain);

        homeButton.setOnClickListener(v -> {
            Intent intent = new Intent(PresetActivity.this, MainActivity.class);
            startActivity(intent);
        });


    }

    /**
     * This method opens when the FAB is clicked, and pops up a user input dialogue
     * where they can input text that goes into creating a new preset
     * CODE INSPIRED FROM AS3 EditRequestsActivity.java
     */
    private void startNewRequest() {
        EditText presetInput = new EditText(this);
        presetInput.setHint(getResources().getString(R.string.preset_hint));
        presetInput.setHintTextColor(getResources().getColor(R.color.black));
        presetInput.setTextSize(25);
        presetInput.setInputType(InputType.TYPE_CLASS_TEXT | InputType.TYPE_TEXT_VARIATION_NORMAL);

        // Builds and displays input dialog for creating a new request
        new AlertDialog.Builder(this)
                .setTitle(R.string.add_request)
                .setView(presetInput)
                .setNegativeButton(R.string.cancel, (dialog, whichButton) -> dialog.cancel())
                .setPositiveButton(R.string.set_request, (dialog, whichButton) -> {
                    if (!presetInput.getText().toString().equals("")) {
                        dialog.dismiss();
                        String presetContent = presetInput.getText().toString();
                        presList.createPreset(presetContent);
                        syncPresets();
                    } else {
                        Toast.makeText(getApplicationContext(), "Preset Input was Blank",
                                Toast.LENGTH_SHORT).show();
                    }
                }).show();
    }

    /**
     * This method changes the SharedPreferences file with the new presets
     * that are present. It's called when something has been deleted from the existing
     * presets
     * CODE INSPIRED FROM AS3 EditRequestsActivity.java
     */
    private void syncPresets() {
        Set<String> presets = new LinkedHashSet<>();

        for (int i = 0; i < presList.numPresets(); i++) {
            // Prepending 'i' allows us to preserve sorted order while using shared preferences
            presets.add(i + presList.getPresetText(i));
        }

        try { // Store these values in our shared preferences
            SharedPreferences.Editor editor = mSharedPreferences.edit();
            editor.putStringSet(PRESETS_KEY, presets);
            editor.apply();
        } catch (Exception e) { // Failed to edit shared preferences file
            Toast.makeText(getApplicationContext(), "Failed to edit shared preferences",
                    Toast.LENGTH_SHORT).show();
        }
    }

    /**
     * This method loads the presets from SharedPreferences
     * CODE INSPIRED FROM AS3 EditRequestsActivity.java
     */
    private void loadPresets() {
        Set<String> presets = getPrefs().getStringSet(PRESETS_KEY, new LinkedHashSet<>());

        if (presets != null) {
            presets.stream().sorted().forEach(s -> presList.addPreset(s.substring(1)));
        }
    }

    /**
     * This method returns SharedPreferences but if there isn't one created,
     * makes a new one
     * @return SharedPreferences file for the application
     * CODE INSPIRED FROM AS3 EditRequestsActivity.java
     */
    protected SharedPreferences getPrefs() {
        if (mSharedPreferences == null) {
            try {
                Context context = getApplicationContext();
                mSharedPreferences = context.getSharedPreferences(
                        context.getPackageName() + ".PREFERENCES",
                        Context.MODE_PRIVATE
                );
            } catch (Exception e) { // Failed to edit shared preferences file
                Toast.makeText(getApplicationContext(), "Failed to edit shared preferences",
                        Toast.LENGTH_SHORT).show();
            }
        }
        return mSharedPreferences;
    }


}
