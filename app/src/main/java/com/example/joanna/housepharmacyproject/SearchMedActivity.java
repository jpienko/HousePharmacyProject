package com.example.joanna.housepharmacyproject;

import android.content.Intent;
import android.database.Cursor;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.EditText;

import java.util.ArrayList;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

public class SearchMedActivity extends Toolbar {


    MedAdapter medAdapter;
    ArrayList<Meds> meds = new ArrayList<>();


    @BindView(R.id.etSearchByName)
    EditText etSearch;

    @BindView(R.id.recViewSearch)
    RecyclerView recyclerView;


    @OnClick(R.id.bSearchMed)
    void ClickSearch(View view) {
        showData();
    }


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_search_med);
        ButterKnife.bind(this);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        recyclerView.setItemAnimator(new DefaultItemAnimator());
        initToolBar("Szukaj",R.string.instruction_search,MainActivity.class);
    }

    public void showData() {
        meds.clear();

        DatabaseMedAdapter db = new DatabaseMedAdapter(this);
        db.openDB();

        Cursor c = db.searchDB(etSearch.getText().toString());
        while (c.moveToNext()) {
            int id = c.getInt(0);
            String name = c.getString(1);
            int amount = c.getInt(2);
            String dose = c.getString(3);
            String place = c.getString(4);
            Meds p = new Meds(id, name, dose, amount, place);
            meds.add(p);
        }
        if (!(meds.size() < 1)) {
            recyclerView.setAdapter(medAdapter);
        }
        db.closeDB();
    }
    public void goToUpdate() {
        RecyclerView.LayoutManager layoutManager = new LinearLayoutManager(this);
        recyclerView.setLayoutManager(layoutManager);

        RecyclerViewClickListener listener = (view, position, id) -> {
            Intent intent = new Intent(SearchMedActivity.this, UpdateMedActivity.class);
            Bundle bundle = new Bundle();
            bundle.putInt("Id", Integer.parseInt(id));
            getActivityNameToBudle(bundle);
            intent.putExtras(bundle);
            startActivity(intent);
        };
        medAdapter = new MedAdapter(meds, listener);
        recyclerView.setAdapter(medAdapter);
    }

}
