package com.example.joanna.housepharmacy;

import android.content.Intent;
import android.database.Cursor;
import android.os.Bundle;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.Button;

import java.util.ArrayList;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

public class MedDisplayActivity extends Toolbar {

    @BindView(R.id.recView)
    RecyclerView recView;

    MedAdapter medAdapter;
    ArrayList<Meds> meds;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_med_display);
        init(this, R.string.instruction_display, "Lista leków");
        ButterKnife.bind(this);

        RecyclerView.LayoutManager layoutManager = new LinearLayoutManager(getApplicationContext());
        recView.setLayoutManager(layoutManager);
        recView.setLayoutManager(new LinearLayoutManager(this));
        recView.setItemAnimator(new DefaultItemAnimator());

        Bundle bundle = getIntent().getExtras();
        if (bundle != null) {

            meds = (ArrayList<Meds>) bundle.getSerializable("MedsList");
            if (!(meds.size() < 1)) {
                recView.setAdapter(medAdapter);
            }
        } else {
            meds = new ArrayList<>();
            retrieveMed();
        }
        inicialize();
    }

    private void retrieveMed() {
        meds.clear();

        DatabaseMedAdapter dbMed = new DatabaseMedAdapter(this);
        DatabasePlaceAdapter dbPlace = new DatabasePlaceAdapter(this);
        DatabaseFormAdapter dbForm = new DatabaseFormAdapter(this);


        dbMed.openDB();
        Cursor c = dbMed.getAllMeds();
        while (c.moveToNext()) {
            int id = c.getInt(0);
            String name = c.getString(1);
            String dose = c.getString(2);
            int formInt = c.getInt(3);
            double amount = c.getDouble(4);
            String purpose = c.getString(5);
            int placeInt = c.getInt(6);
            dbMed.closeDB();

            dbForm.openDB();
            String form = dbForm.getFormName(formInt);
            dbForm.closeDB();
            dbPlace.openDB();
            String place = dbPlace.getPlaceName(placeInt);
            dbPlace.closeDB();

            Meds m = new Meds(id, name, dose, form, amount, purpose, place);
            meds.add(m);
        }

        if (!(meds.size() < 1)) {
            recView.setAdapter(medAdapter);
        }

    }

    public void inicialize(){
        RecyclerView.LayoutManager layoutManager = new LinearLayoutManager(this);
        recView.setLayoutManager(layoutManager);
        RecyclerViewClickListener listener = (view, position, id, bNumber) -> {
            if (bNumber == 1){
                goToUpdateMed(id);
            }else if(bNumber==2){
                deleteMedFromList(id);
            }
        };
        medAdapter = new MedAdapter(meds, listener);
        recView.setAdapter(medAdapter);
    }
    public void goToUpdateMed(String id) {


            Intent intent = new Intent(MedDisplayActivity.this, MedUpdateActivity.class);
            Bundle bundle = new Bundle();
            bundle.putInt("Id", Integer.parseInt(id));
            intent.putExtras(bundle);
            startActivity(intent);

    }

    public void deleteMedFromList(String id) {

        AlertDialog.Builder mBuilder = new AlertDialog.Builder(activity);
        View mView = getLayoutInflater().inflate(R.layout.dialog_delete, null);
        mBuilder.setView(mView);
        final AlertDialog dialog = mBuilder.create();
        Button bNo = (Button) mView.findViewById(R.id.bDoNotDelete);
        bNo.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                dialog.dismiss();

            }
        });

        Button bYes = (Button) mView.findViewById(R.id.bDeleteContent);
        bYes.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                DatabaseMedAdapter dbMed = new DatabaseMedAdapter(getBaseContext());
                dbMed.openDB();
                dbMed.deleteMed(String.valueOf(id));
                dbMed.closeDB();
                dialog.dismiss();
                retrieveMed();

            }
        });
        dialog.show();

    }

}