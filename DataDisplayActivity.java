package com.zybooks.project2cs_360;

import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.database.Cursor;
import android.os.Bundle;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.GridView;
import android.widget.LinearLayout;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;
import java.util.ArrayList;

public class DataDisplayActivity extends AppCompatActivity {
    private GridView gridViewData;
    private Button buttonAddData, buttonDelete;
    private DatabaseHelper databaseHelper;
    private ArrayList<String> weightDataList;
    private ArrayList<Integer> weightDataIds; // List to store IDs of weight data
    private int currentUserId;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_data_display);

        gridViewData = findViewById(R.id.gridViewData);
        buttonAddData = findViewById(R.id.buttonAddData);
        buttonDelete = findViewById(R.id.buttonDelete);
        databaseHelper = new DatabaseHelper(this);
        weightDataList = new ArrayList<>();
        weightDataIds = new ArrayList<>(); // Initialize the IDs list

        currentUserId = getIntent().getIntExtra("USER_ID", -1);

        buttonAddData.setOnClickListener(v -> addData());
        buttonDelete.setOnClickListener(v -> deleteData());

        loadDataIntoGrid();
    }

    private void addData() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Add Weight Data");

        LinearLayout layout = new LinearLayout(this);
        layout.setOrientation(LinearLayout.VERTICAL);
        final EditText inputWeight = new EditText(this);
        inputWeight.setHint("Weight (e.g., 70 kg)");
        final EditText inputDate = new EditText(this);
        inputDate.setHint("Date (e.g., 2024-10-17)");
        layout.addView(inputWeight);
        layout.addView(inputDate);
        builder.setView(layout);

        builder.setPositiveButton("Add", (dialog, which) -> {
            String weight = inputWeight.getText().toString();
            String date = inputDate.getText().toString();
            if (!weight.isEmpty() && !date.isEmpty()) {
                databaseHelper.addWeightData(weight, date, currentUserId);
                loadDataIntoGrid();
            } else {
                Toast.makeText(this, "Please enter both weight and date", Toast.LENGTH_SHORT).show();
            }
        });
        builder.setNegativeButton("Cancel", (dialog, which) -> dialog.cancel());
        builder.show();
    }

    private void deleteData() {
        if (!weightDataIds.isEmpty()) {
            // Prompt the user to select which item to delete
            AlertDialog.Builder builder = new AlertDialog.Builder(this);
            builder.setTitle("Delete Weight Data");

            String[] dataItems = weightDataList.toArray(new String[0]);
            builder.setItems(dataItems, (dialog, which) -> {
                int idToDelete = weightDataIds.get(which); // Get ID of selected item
                databaseHelper.deleteWeightData(idToDelete); // Call delete method
                loadDataIntoGrid(); // Refresh the grid after deletion
                Toast.makeText(this, "Data deleted", Toast.LENGTH_SHORT).show();
            });

            builder.setNegativeButton("Cancel", (dialog, which) -> dialog.cancel());
            builder.show();
        } else {
            Toast.makeText(this, "No data to delete", Toast.LENGTH_SHORT).show();
        }
    }

    private void loadDataIntoGrid() {
        weightDataList.clear();
        weightDataIds.clear(); // Clear the IDs list
        Cursor cursor = databaseHelper.getWeightData(currentUserId);

        if (cursor != null && cursor.moveToFirst()) {
            do {
                @SuppressLint("Range") String weight = cursor.getString(cursor.getColumnIndex(DatabaseHelper.COLUMN_WEIGHT_VALUE));
                @SuppressLint("Range") String date = cursor.getString(cursor.getColumnIndex(DatabaseHelper.COLUMN_DATE));
                @SuppressLint("Range") int id = cursor.getInt(cursor.getColumnIndex(DatabaseHelper.COLUMN_ID)); // Get ID

                weightDataList.add(weight + " (" + date + ")");
                weightDataIds.add(id); // Store the ID
            } while (cursor.moveToNext());
            cursor.close();
        }

        gridViewData.setAdapter(new ArrayAdapter<>(this, android.R.layout.simple_list_item_1, weightDataList));
    }
}
