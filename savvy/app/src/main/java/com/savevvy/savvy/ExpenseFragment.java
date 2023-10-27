package com.savevvy.savvy;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.firebase.ui.database.FirebaseRecyclerOptions;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.savevvy.savvy.Model.Data;

import java.text.DateFormat;
import java.util.Date;

public class ExpenseFragment extends Fragment {

    private FirebaseAuth mAuth;
    private DatabaseReference mExpenseDatabase;
    private RecyclerView recyclerView;
    private TextView expenseTotalSum;
    private String type, note, post_key;
    private int amount;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View myview = inflater.inflate(R.layout.fragment_expense, container, false);
        mAuth = FirebaseAuth.getInstance();
        FirebaseUser mUser = mAuth.getCurrentUser();
        String uid = mUser.getUid();
        mExpenseDatabase = FirebaseDatabase.getInstance().getReference().child("ExpenseData").child(uid);
        expenseTotalSum = myview.findViewById(R.id.expense_txt_result);
        recyclerView = myview.findViewById(R.id.recycler_id_expense);
        recyclerView.setLayoutManager(new LinearLayoutManager(getActivity(), RecyclerView.VERTICAL, true));
        return myview;
    }

    @Override
    public void onStart() {
        super.onStart();
        FirebaseRecyclerAdapter<Data, MyViewHolder> adapter = new FirebaseRecyclerAdapter<Data, MyViewHolder>(
                new FirebaseRecyclerOptions.Builder<Data>().setQuery(mExpenseDatabase, Data.class).build()
        ) {
            @NonNull
            @Override
            public MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
                return new MyViewHolder(LayoutInflater.from(parent.getContext()).inflate(R.layout.income_recycler_data, parent, false));
            }

            @Override
            protected void onBindViewHolder(@NonNull MyViewHolder holder, int position, @NonNull Data model) {
                holder.setData(model);
                holder.itemView.setOnClickListener(view -> {
                    type = model.getType();
                    note = model.getNote();
                    amount = model.getAmount();
                    post_key = getRef(position).getKey();
                    updateDataItem();
                });
            }
        };
        recyclerView.setAdapter(adapter);
        adapter.startListening();
        updateIncomeTotal();
    }

    private void updateIncomeTotal() {
        mExpenseDatabase.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                int expenseTotal = 0;
                for (DataSnapshot ds : snapshot.getChildren()) {
                    expenseTotal += ds.getValue(Data.class).getAmount();
                }
                expenseTotalSum.setText(String.valueOf(expenseTotal));
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {}
        });
    }

    private void updateDataItem() {
        AlertDialog editDialog = new AlertDialog.Builder(getContext()).create();
        View dialogView = LayoutInflater.from(getContext()).inflate(R.layout.update_data_item, null);
        EditText edtAmmount = dialogView.findViewById(R.id.amount_edt);
        EditText edtType = dialogView.findViewById(R.id.type_edt);
        EditText edtNote = dialogView.findViewById(R.id.note_edt);
        edtType.setText(type);
        edtType.setSelection(type.length());
        edtNote.setText(note);
        edtNote.setSelection(note.length());
        edtAmmount.setText(String.valueOf(amount));
        edtAmmount.setSelection(String.valueOf(amount).length());

        dialogView.findViewById(R.id.btn_upd_Update).setOnClickListener(view -> {
            type = edtType.getText().toString().trim();
            note = edtNote.getText().toString().trim();
            amount = Integer.parseInt(edtAmmount.getText().toString().trim());
            String currentDate = DateFormat.getDateInstance().format(new Date());
            Data data = new Data(amount, type, note, post_key, currentDate);
            mExpenseDatabase.child(post_key).setValue(data);
            editDialog.dismiss();
        });

        dialogView.findViewById(R.id.btnPD_Delete).setOnClickListener(view ->
                editDialog.dismiss());
        mExpenseDatabase.child(post_key).removeValue();
        editDialog.setView(dialogView);
        editDialog.show();
    }

    public static class MyViewHolder extends RecyclerView.ViewHolder {
        public MyViewHolder(View itemView) {
            super(itemView);
        }

        public void setData(Data data) {
            ((TextView) itemView.findViewById(R.id.amount_txt_income)).setText(String.valueOf(data.getAmount()));
            ((TextView) itemView.findViewById(R.id.type_txt_income)).setText(data.getType());
            ((TextView) itemView.findViewById(R.id.note_txt_income)).setText(data.getNote());
            ((TextView) itemView.findViewById(R.id.date_txt_income)).setText(data.getData());
        }
    }
}
