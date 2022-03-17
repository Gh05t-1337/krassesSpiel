package com.autismprime.krassesSpiel;

import android.Manifest;
//import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
//import android.media.MediaPlayer;
import android.graphics.Color;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
//import android.os.Environment;
//import android.os.FileUtils;
import android.text.InputType;
import android.util.Log;

import androidx.activity.result.ActivityResult;
import androidx.activity.result.ActivityResultCallback;
import androidx.activity.result.ActivityResultLauncher;
//import androidx.activity.result.contract.ActivityResultContract;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
//import androidx.fragment.app.Fragment;
import androidx.core.app.NavUtils;
import androidx.preference.EditTextPreference;
import androidx.preference.Preference;
import androidx.preference.PreferenceFragmentCompat;
import androidx.preference.PreferenceManager;

import yuku.ambilwarna.AmbilWarnaDialog;

//import java.io.File;
//import java.io.IOException;

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
        public static final int REQ_CODE_PICK_SOUNDFILE = 1;
        public static Uri ur;
        ActivityResultLauncher<Intent> launcher=registerForActivityResult(new ActivityResultContracts.StartActivityForResult(),
                new ActivityResultCallback<ActivityResult>() {
                    @Override
                    public void onActivityResult(ActivityResult result) {
                        if(result.getData()!=null) {
                            Intent data = result.getData();
                            ur = data.getData();
                        }
                        //Log.i("", ur.toString());//file.exists()+data.getData().getPath());
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
                        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.M) { // Level 23

                            // Check if we have Call permission
                            int permisson = ActivityCompat.checkSelfPermission(getContext(),
                                    Manifest.permission.READ_EXTERNAL_STORAGE);

                            if (permisson != PackageManager.PERMISSION_GRANTED) {
                                // If don't have permission so prompt the user.
                                ActivityCompat.requestPermissions(getActivity(),
                                        new String[]{Manifest.permission.READ_EXTERNAL_STORAGE},
                                        1000
                                );

                            }
                        }
                        //<<
                        Intent intent;
                        intent = new Intent();
                        intent.setAction(Intent.ACTION_GET_CONTENT);
                        intent.setType("audio/*");
                       // startActivityForResult(Intent.createChooser(intent, ""), REQ_CODE_PICK_SOUNDFILE);
                        launcher.launch(Intent.createChooser(intent, ""));
                    }
                    return true;
                }
            });

            //final Preference pref=findPreference("color");

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
                            //shad.edit().commit();
                            //Log.i("",shad.getString("color","leer"));
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
            //pref.setOnPreferenceClickListener(click);
            findPreference("scolor").setOnPreferenceClickListener(click);
            findPreference("bcolor").setOnPreferenceClickListener(click);
            findPreference("qcolor").setOnPreferenceClickListener(click);
            findPreference("bbcolor").setOnPreferenceClickListener(click);
            findPreference("bbocolor").setOnPreferenceClickListener(click);
            findPreference("excolor").setOnPreferenceClickListener(click);
            findPreference("uicolor").setOnPreferenceClickListener(click);
            for(int i=1;i<=5;i++)
                findPreference("color"+String.valueOf(i)).setOnPreferenceClickListener(click);
            //findPreference("color2").setOnPreferenceClickListener(click);

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
            //findPreference("color2").getIcon().setTint(Color.parseColor(shed.getString("color2","#a0a0a0")));
            //pref.getIcon().setTint(Color.parseColor(shed.getString("color","#000000")));


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
            //EditTextPreference radius=(EditTextPreference) findPreference("color_radius");
            //radius.getIcon().setTint(Color.RED);
            //radius.setInputType(InputType.TYPE_CLASS_NUMBER);

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