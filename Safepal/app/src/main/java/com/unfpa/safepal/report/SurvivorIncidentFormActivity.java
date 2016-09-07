package com.unfpa.safepal.report;

import android.content.ContentValues;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;

import android.support.design.widget.Snackbar;
import android.support.v4.app.DialogFragment;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.Spinner;
import android.widget.Toast;


import com.unfpa.safepal.R;
import com.unfpa.safepal.datepicker.DatePickerFragment;
import com.unfpa.safepal.home.HomeActivity;
import com.unfpa.safepal.store.ReportIncidentContentProvider;
import com.unfpa.safepal.store.ReportIncidentTable;


public class SurvivorIncidentFormActivity extends AppCompatActivity {

    /**
     * sif - Stands for "Survivor Incident Form"
     * */
    //Global Variables
    Toolbar sifToolbar;

    FloatingActionButton sifAbortAppFab, sifBackFab, sifSubmitFab;

    private Button sifDateOfBirthButton;

    private RadioGroup sifGenderRG;
    private RadioButton sifGenderRB;
    private Spinner sifIncidentTypeSpinner;
    private EditText sifIncidentLocationEt, sifIncidentDetailsEt;
    //content provider
    private Uri reportIncidentUri;
    private Snackbar sifFeedbackSnackbar;



    @Override
    protected void onCreate(Bundle bundle) {
        super.onCreate(bundle);
        setContentView(R.layout.activity_survivor_incident_form);

        sifToolbar = (Toolbar) findViewById(R.id.sif_toolbar);

        //Abort fab of  sif activity
        sifAbortAppFab = (FloatingActionButton) findViewById(R.id.sif_abort_app_fab);
        //Back fab of  sif activity
        sifBackFab = (FloatingActionButton) findViewById(R.id.sif_back_fab);
        sifSubmitFab = (FloatingActionButton)findViewById(R.id.sif_submit_fab);

        sifDateOfBirthButton = (Button)findViewById(R.id.sif_date_of_birth_button);

        sifGenderRG=(RadioGroup)findViewById(R.id.sif_gender_rg);
        sifIncidentTypeSpinner = (Spinner) findViewById(R.id.sif_incident_type_spinner);
        sifIncidentLocationEt = (EditText)findViewById(R.id.sif_incident_location_et);
        sifIncidentDetailsEt = (EditText)findViewById(R.id.sif_incident_details_rt);

        //Toolbar
        setSupportActionBar(sifToolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);


        //content provider

       // Bundle extras = getIntent().getExtras();
        // check from the saved Instance
        reportIncidentUri = (bundle == null) ? null : (Uri) bundle
                .getParcelable(ReportIncidentContentProvider.CONTENT_ITEM_TYPE);

        /*// Or passed from the other activity
        if (extras != null) {
            reportIncidentUri = extras
                    .getParcelable(ReportIncidentContentProvider.CONTENT_ITEM_TYPE);

            fillData(reportIncidentUri);
        }*/
        //end content provider

        sifAbortAppFab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                moveTaskToBack(true);
                android.os.Process.killProcess(android.os.Process.myPid());
                System.exit(2);
            }
        });
        sifBackFab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(getApplicationContext(), WhoSGettingHelpActivity.class));
            }
        });

        // Create an ArrayAdapter using the string array and a default spinner layout
        ArrayAdapter<CharSequence> sifIncidentTypeAdapter = ArrayAdapter.createFromResource(this,
                R.array.sif_incident_type, android.R.layout.simple_spinner_item);
        // Specify the layout to use when the list of choices appears
        sifIncidentTypeAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        // Apply the sifIncidentTypeAdapter to the spinner
        sifIncidentTypeSpinner.setAdapter(sifIncidentTypeAdapter);


    }

    public void showDatePickerDialog(View v) {
        DialogFragment newFragment = new DatePickerFragment();
        newFragment.show(getSupportFragmentManager(), "datePicker");
    }

    public void onClickAddIncidentSurvivor(View view) {

        int genderRBId = sifGenderRG.getCheckedRadioButtonId();

        //checks if gender radio group isn't selected;
        if(genderRBId==-1){
            //feedback to developer
            sifFeedbackSnackbar = Snackbar.make(view,"Tell us your gender please!!!",Snackbar.LENGTH_LONG);
            sifFeedbackSnackbar.show();
            return;
        }
        View genderRBView = sifGenderRG.findViewById(genderRBId);
        int idx = sifGenderRG.indexOfChild(genderRBView);
        sifGenderRB = (RadioButton) sifGenderRG.getChildAt(idx);
        //end check of radio group



        String reportedBy = "Survivor";
        String survivorDateOfBirth = sifDateOfBirthButton.getText().toString();;
        String survivorGender = (String)sifGenderRB.getText();
        String incidentType =(String)sifIncidentTypeSpinner.getSelectedItem();
        String incidentLocation = sifIncidentLocationEt.getText().toString();
        String incidentStory = sifIncidentDetailsEt.getText().toString();;
        String uniqueIdentifier = "test_uid";;



        if(sifDateOfBirthButton.getText().toString()== getResources().getText(R.string.sif_survivor_pick_age)){
            sifFeedbackSnackbar = Snackbar.make(view,"Pick a date of birth",Snackbar.LENGTH_LONG);
            sifFeedbackSnackbar.show();
            return;
        }


        if (sifIncidentTypeSpinner.getSelectedItemPosition() <= 0) {
            sifFeedbackSnackbar = Snackbar.make(view,"Choose what happened to you.",Snackbar.LENGTH_LONG);
            sifFeedbackSnackbar.show();
            return;
        }


        // only save if either location or story
        // is available

        if (sifIncidentLocationEt.length() == 0 && sifIncidentDetailsEt.length() == 0) {
            //feedback to developer
            sifFeedbackSnackbar = Snackbar.make(view, "Story or location is not filled", Snackbar.LENGTH_LONG);
            sifFeedbackSnackbar.show();
            return;
        }



        ContentValues values = new ContentValues();
        values.put(ReportIncidentTable.COLUMN_REPORTED_BY, reportedBy);
        values.put(ReportIncidentTable.COLUMN_SURVIVOR_DATE_OF_BIRTH, survivorDateOfBirth);
        values.put(ReportIncidentTable.COLUMN_SURVIVOR_GENDER, survivorGender);

        values.put(ReportIncidentTable.COLUMN_INCIDENT_TYPE, incidentType);
        values.put(ReportIncidentTable.COLUMN_INCIDENT_LOCATION, incidentLocation);
        values.put(ReportIncidentTable.COLUMN_INCIDENT_STORY, incidentStory);
        values.put(ReportIncidentTable.COLUMN_UNIQUE_IDENTIFIER, uniqueIdentifier);


        if (reportIncidentUri == null) {
            // New reported incident
            reportIncidentUri = getContentResolver().insert(
                    ReportIncidentContentProvider.CONTENT_URI, values);
            startActivity(new Intent(getApplicationContext(),HomeActivity.class));
            //feedback to developer
            Toast.makeText(getBaseContext(),
                    reportIncidentUri.toString() + " success\n"+survivorGender, Toast.LENGTH_LONG).show();


        } else {
            // Update reported incident
            getContentResolver().update(reportIncidentUri, values, null, null);
        }
    }
}