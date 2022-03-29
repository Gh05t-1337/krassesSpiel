package com.autismprime.krassesSpiel;

import android.Manifest;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import androidx.activity.result.ActivityResult;
import androidx.activity.result.ActivityResultCallback;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.app.NavUtils;
import androidx.preference.Preference;
import androidx.preference.PreferenceFragmentCompat;
import androidx.preference.PreferenceManager;
import java.net.URISyntaxException;
import yuku.ambilwarna.AmbilWarnaDialog;

public class SettingsActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.settings_activity);
        if (savedInstanceState == null) {
            getSupportFragmentManager()
                    .beginTransaction()
                    .replace(R.id.settings, new SettingsFragment())
                    .commit();
        }
        ActionBar actionBar = getSupportActionBar();
        if (actionBar != null) {
            actionBar.setDisplayHomeAsUpEnabled(true);
            actionBar.setTitle("Settings");
        }

    }

    public static class SettingsFragment extends PreferenceFragmentCompat {
        public static Uri ur;
        ActivityResultLauncher<Intent> launcher=registerForActivityResult(new ActivityResultContracts.StartActivityForResult(),
                new ActivityResultCallback<ActivityResult>() {
                    @Override
                    public void onActivityResult(ActivityResult result) {
                        if(result.getData()!=null) {
                            Intent data = result.getData();
                            ur = data.getData();

                            String uriString=null;
                            try {
                                uriString=PathUtil.getPath(getContext(), SettingsFragment.ur);
                            } catch (URISyntaxException e) {
                                e.printStackTrace();
                            }

                            PreferenceManager.getDefaultSharedPreferences(getContext()).edit().putString("uri", uriString).commit();
                        }
                    }
                }
                );
        @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
        @Override
        public void onCreatePreferences(Bundle savedInstanceState, String rootKey) {
            setPreferencesFromResource(R.xml.root_preferences, rootKey);

            final Preference prefList = findPreference("music");

            //OPEN MUSIC FILE PICKER
            prefList.setOnPreferenceChangeListener(new Preference.OnPreferenceChangeListener() {
                public boolean onPreferenceChange(Preference preference, Object value) {
                    System.out.println("Selected: " + value);
                    if(value.equals("-1")) {
                        //PERMISSION>>
                        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.M) { // Level 23, Android 6.0 M
                                // If don't have permission prompt the user.
                                ActivityCompat.requestPermissions(getActivity(),
                                        new String[]{Manifest.permission.READ_EXTERNAL_STORAGE},
                                        1000
                                );
                        }
                        //<<
                        Intent intent;
                        intent = new Intent();
                        intent.setAction(Intent.ACTION_GET_CONTENT);
                        intent.setType("audio/*");

                        launcher.launch(Intent.createChooser(intent, ""));
                    }
                    return true;
                }
            });


            //OPEN COLOR PICKER LISTENER
            Preference.OnPreferenceClickListener click=new Preference.OnPreferenceClickListener() {
                @Override
                public boolean onPreferenceClick(Preference preference) {
                    Log.i("",preference.getKey());
                    String defaul=(preference.getKey().equals("qcolor")||preference.getKey().equals("bbcolor"))?"#000000":"#ffffff";
                    int col= Color.parseColor(PreferenceManager.getDefaultSharedPreferences(getContext()).getString(preference.getKey(), defaul));
                    AmbilWarnaDialog dialog = new AmbilWarnaDialog(getContext(), col, new AmbilWarnaDialog.OnAmbilWarnaListener() {
                        @Override
                        public void onOk(AmbilWarnaDialog dialog, int color) {
                            // color is the color selected by the user.
                            preference.getIcon().setTint(color);
                            SharedPreferences shad= PreferenceManager.getDefaultSharedPreferences(getContext());
                            shad.edit().putString(preference.getKey(),String.format("#%06X", (0xFFFFFF & color))).commit();
                        }

                        @Override
                        public void onCancel(AmbilWarnaDialog dialog) {
                            // cancel was selected by the user
                        }
                    });
                    dialog.show();
                    return false;
                }
            };
            //ADD OPEN COLOR PICKER LISTENER
            findPreference("scolor").setOnPreferenceClickListener(click);
            findPreference("bcolor").setOnPreferenceClickListener(click);
            findPreference("qcolor").setOnPreferenceClickListener(click);
            findPreference("bbcolor").setOnPreferenceClickListener(click);
            findPreference("bbocolor").setOnPreferenceClickListener(click);
            findPreference("excolor").setOnPreferenceClickListener(click);
            findPreference("uicolor").setOnPreferenceClickListener(click);
            for(int i=1;i<=5;i++)
                findPreference("color"+String.valueOf(i)).setOnPreferenceClickListener(click);

            //COLORS
            SharedPreferences shed=PreferenceManager.getDefaultSharedPreferences(getContext());
            findPreference("scolor").getIcon().setTint(Color.parseColor(shed.getString("scolor","#ffffff")));
            findPreference("bcolor").getIcon().setTint(Color.parseColor(shed.getString("bcolor","#ffffff")));
            findPreference("bbocolor").getIcon().setTint(Color.parseColor(shed.getString("bbocolor","#ffffff")));
            findPreference("excolor").getIcon().setTint(Color.parseColor(shed.getString("excolor","#ffffff")));
            findPreference("uicolor").getIcon().setTint(Color.parseColor(shed.getString("uicolor","#ffffff")));
            findPreference("qcolor").getIcon().setTint(Color.parseColor(shed.getString("qcolor","#000000")));
            findPreference("bbcolor").getIcon().setTint(Color.parseColor(shed.getString("bbcolor","#000000")));
            for(int i=1;i<=5;i++)
                findPreference("color"+String.valueOf(i)).getIcon().setTint(Color.parseColor(shed.getString("color"+String.valueOf(i),"#a0a0a0")));


            //GRADIENT PREFERENCES>
            for(int i=Integer.parseInt(shed.getString("color_count","2"))+1;i<=5;i++){
                findPreference("color"+String.valueOf(i)).setVisible(false);
            }
            //<
            findPreference("color_count").setOnPreferenceChangeListener(new Preference.OnPreferenceChangeListener() {
                @Override
                public boolean onPreferenceChange(Preference preference, Object newValue) {
                    for(int i=1;i<=Integer.parseInt(newValue.toString());i++){
                        findPreference("color"+String.valueOf(i)).setVisible(true);
                    }
                    for(int i=Integer.parseInt(newValue.toString())+1;i<=5;i++){
                        findPreference("color"+String.valueOf(i)).setVisible(false);
                    }
                    return true;
                }
            });

            //RESET SETTINGS TO DEFAULT
            final Preference def=findPreference("default");
            def.setOnPreferenceClickListener(new Preference.OnPreferenceClickListener() {
                @Override
                public boolean onPreferenceClick(Preference preference) {
                    SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(getContext());
                    SharedPreferences.Editor editor = preferences.edit();
                    editor.clear();
                    editor.commit();
                    getActivity().finish();
                    startActivity(getActivity().getIntent());
                    return false;
                }
            });

        }

    }

    @Override
    public void onBackPressed() {
        NavUtils.navigateUpFromSameTask(this);
    }
}
