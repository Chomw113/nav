package com.example.nav.Fragment;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.example.nav.R;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.ArrayList;

public class ListActivity extends AppCompatActivity {

    private ListView listView;
    private Button reg_button;
    private ArrayList<String> titleList = new ArrayList<>();
    private ArrayList<String> seqList = new ArrayList<>();
    private ArrayAdapter<String> arrayAdapter;
    private DatabaseReference databaseRef;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_list);

        listView = findViewById(R.id.listView);
        reg_button = findViewById(R.id.reg_button);

        databaseRef = FirebaseDatabase.getInstance().getReference("board");

        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                Toast.makeText(ListActivity.this, adapterView.getItemAtPosition(i) + " 클릭", Toast.LENGTH_SHORT).show();

                Intent intent = new Intent(ListActivity.this, DetailActivity.class);
                intent.putExtra("board_seq", seqList.get(i));
                startActivity(intent);
            }
        });

        reg_button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(ListActivity.this, RegisterActivity.class);
                startActivity(intent);
            }
        });
    }

    @Override
    protected void onResume() {
        super.onResume();
        databaseRef.addChildEventListener(childEventListener);
    }

    @Override
    protected void onPause() {
        super.onPause();
        databaseRef.removeEventListener(childEventListener);
    }

    private ChildEventListener childEventListener = new ChildEventListener() {
        @Override
        public void onChildAdded(DataSnapshot snapshot, String previousChildName) {
            String seq = snapshot.getKey();
            String title = snapshot.child("title").getValue(String.class);
            seqList.add(seq);
            titleList.add(title);

            if (arrayAdapter == null) {
                arrayAdapter = new ArrayAdapter<>(ListActivity.this, android.R.layout.simple_list_item_1, titleList);
                listView.setAdapter(arrayAdapter);
            } else {
                arrayAdapter.notifyDataSetChanged();
            }
        }

        @Override
        public void onChildChanged(DataSnapshot snapshot, String previousChildName) {
            String seq = snapshot.getKey();
            String title = snapshot.child("title").getValue(String.class);
            int index = seqList.indexOf(seq);

            if (index >= 0) {
                titleList.set(index, title);
                arrayAdapter.notifyDataSetChanged();
            }
        }

        @Override
        public void onChildRemoved(DataSnapshot snapshot) {
            String seq = snapshot.getKey();
            int index = seqList.indexOf(seq);

            if (index >= 0) {
                seqList.remove(index);
                titleList.remove(index);
                arrayAdapter.notifyDataSetChanged();
            }
        }

        @Override
        public void onChildMoved(DataSnapshot snapshot, String previousChildName) {
            // 이동된 데이터 처리 (사용하지 않음)
        }

        @Override
        public void onCancelled(DatabaseError error) {
            Log.e("Firebase", "Database error: " + error.getMessage());
        }
    };
}