package com.savevvy.savvy;

import static android.content.ContentValues.TAG;

import android.annotation.SuppressLint;
import android.graphics.Color;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;

import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;


import com.github.mikephil.charting.charts.BarChart;
import com.github.mikephil.charting.charts.LineChart;
import com.github.mikephil.charting.data.BarData;
import com.github.mikephil.charting.data.BarDataSet;
import com.github.mikephil.charting.data.BarEntry;

import com.github.mikephil.charting.data.Entry;
import com.github.mikephil.charting.data.LineData;
import com.github.mikephil.charting.data.LineDataSet;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.savevvy.savvy.Model.Data;



import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class ChartFragment extends Fragment {



        private BarChart mBarChart;
        private BarChart mBarchatIncome;
        private LineChart lineChart;
        private DatabaseReference mExpenseDatabase;
        private DatabaseReference mIconeDatabase;

        @SuppressLint("MissingInflatedId")
        @Override
        public View onCreateView(LayoutInflater inflater, ViewGroup container,
                                 Bundle savedInstanceState) {

            View myview = inflater.inflate(R.layout.fragment_chart, container, false);
            // Expense
            mBarChart = myview.findViewById(R.id.Barchart);
            mBarChart.getDescription().setEnabled(false);
            mBarChart.setPinchZoom(false);
            mBarChart.setDrawBarShadow(false);
            mBarChart.setDrawGridBackground(false);

            // Income

            lineChart = myview.findViewById(R.id.LineIncome);
            lineChart.getDescription().setEnabled(false);
            lineChart.setPinchZoom(false);
//            lineChart.setDrawBarShadow(false);
            lineChart.setDrawGridBackground(false);



            return myview;
        }

        @Override
        public void onStart() {
            super.onStart();
            FirebaseUser currentUser = FirebaseAuth.getInstance().getCurrentUser();
            if (currentUser == null) return;

            String uid = currentUser.getUid();
            mExpenseDatabase = FirebaseDatabase.getInstance().getReference().child("ExpenseData").child(uid);
            mIconeDatabase = FirebaseDatabase.getInstance().getReference().child("ExpenseData").child(uid);
            final List<String> list = new ArrayList<>();
            final ArrayList<String> xVals = new ArrayList<>();
            final ArrayList<Entry> yVals = new ArrayList<>();

            //income
            mIconeDatabase.addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                    int i = 0;
                    for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                        String date = snapshot.getValue(Data.class).getData();
                        float amount = snapshot.getValue(Data.class).getAmount();

                        xVals.add(date);
                        yVals.add(new Entry(amount, i));
                        i++;
                    }
                    LineDataSet lineDataSet = new LineDataSet(yVals, "Income");
                    lineDataSet.setLineWidth(2f);
                    lineDataSet.setCircleRadius(4f);
                    lineDataSet.setValueTextSize(10f);

                    LineData lineData = new LineData(lineDataSet);
                    lineChart.setData(lineData);
                    lineChart.getDescription().setText("Income Line Chart");
                    lineChart.invalidate();
                }
                @Override
                public void onCancelled(@NonNull DatabaseError error) {

                }
            });
            // expense
            mExpenseDatabase.addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                    BarData barData = new BarData();
                    for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                        Integer amount = snapshot.getValue(Data.class).getAmount();
                        String dayId = snapshot.getValue(Data.class).getData();
                        String label = snapshot.getKey();

                        list.add(String.valueOf(amount));
                        list.add(dayId);

//                        barData.addDataSet(new BarDataSet(Collections.singletonList(new BarEntry(amount, barData.getEntryCount(), dayId)), label));
                        BarDataSet barDataSet = new BarDataSet(Collections.singletonList(new BarEntry(barData.getEntryCount(), amount, dayId)), label);
                        barDataSet.setBarBorderWidth(0.5f);
                        barData.setBarWidth(0.5f);
                        barDataSet.setBarBorderColor(Color.rgb(255, 123, 123));
                        barDataSet.setColor(Color.rgb(123, 255, 123));
                        barData.addDataSet(barDataSet);
                    }

                    mBarChart.setData(barData);
                    mBarChart.invalidate();
                }

                @Override
                public void onCancelled(@NonNull DatabaseError error) {

                }
            });
        }
    }


