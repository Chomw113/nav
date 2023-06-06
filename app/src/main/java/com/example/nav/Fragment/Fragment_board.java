package com.example.nav.Fragment;

import android.content.Intent;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.example.nav.R;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;

import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;


import javax.net.ssl.HttpsURLConnection;

public class Fragment_board extends Fragment {

    private DatabaseReference databaseRef;

    private ListView listView;
    private Button reg_button;
    private ArrayList<String> titleList = new ArrayList<>();
    private ArrayList<String> seqList = new ArrayList<>();
    private ArrayAdapter<String> arrayAdapter;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.frag_board, container, false);

        // Firebase Realtime Database의 "board" 레퍼런스를 가져옴
        databaseRef = FirebaseDatabase.getInstance().getReference("board");
        listView = view.findViewById(R.id.listView);
        reg_button = view.findViewById(R.id.reg_button);

        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                Toast.makeText(getActivity(), adapterView.getItemAtPosition(i) + " 클릭", Toast.LENGTH_SHORT).show();

                Intent intent = new Intent(getActivity(), DetailActivity.class);
                intent.putExtra("board_seq", seqList.get(i));
                startActivity(intent);
            }
        });

        reg_button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(getActivity(), RegisterActivity.class);
                startActivity(intent);
            }
        });

        return view;
    }

    @Override

    public void onResume() {
        super.onResume();

        // 이벤트 리스너 등록
        databaseRef.addChildEventListener(childEventListener);
    }
    public void onPause() {
        super.onPause();

        // 이벤트 리스너 해제
        databaseRef.removeEventListener(childEventListener);
    }

    private ChildEventListener childEventListener = new ChildEventListener() {
        @Override
        public void onChildAdded(@NonNull DataSnapshot snapshot, @Nullable String previousChildName) {
            // 데이터가 추가될 때 호출되는 콜백 메서드
            String seq = snapshot.getKey();
            String title = snapshot.child("title").getValue(String.class);

            // 리스트에 추가
            seqList.add(seq);
            titleList.add(title);

            // 어댑터 갱신
            if (arrayAdapter == null) {
                arrayAdapter = new ArrayAdapter<>(requireActivity(), android.R.layout.simple_list_item_1, titleList);
                listView.setAdapter(arrayAdapter);
            } else {
                arrayAdapter.notifyDataSetChanged();
            }
        }

        @Override
        public void onChildChanged(@NonNull DataSnapshot snapshot, @Nullable String previousChildName) {
            // 데이터가 변경될 때 호출되는 콜백 메서드
            String seq = snapshot.getKey();
            String title = snapshot.child("title").getValue(String.class);
            int index = seqList.indexOf(seq);

            // 변경된 데이터로 업데이트
            if (index >= 0) {
                titleList.set(index, title);
                arrayAdapter.notifyDataSetChanged();
            }
        }

        @Override
        public void onChildRemoved(@NonNull DataSnapshot snapshot) {
            // 데이터가 삭제될 때 호출되는 콜백 메서드
            String seq = snapshot.getKey();
            int index = seqList.indexOf(seq);

            // 삭제된 데이터 제거
            if (index >= 0) {
                seqList.remove(index);
                titleList.remove(index);
                arrayAdapter.notifyDataSetChanged();
            }
        }

        @Override
        public void onChildMoved(@NonNull DataSnapshot snapshot, @Nullable String previousChildName) {
            // 데이터의 순서가 변경될 때 호출되는 콜백 메서드
            // 여기서는 사용하지 않음
        }

        @Override
        public void onCancelled(@NonNull DatabaseError error) {
            // 데이터 로드가 취소될 때 호출되는 콜백 메서드
            // 여기서는 사용하지 않음
        }
    };
    private class GetBoard extends AsyncTask<String, Void, String> {
        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            Log.d("프래그먼트", "onPreExecute");
        }

        @Override
        protected void onPostExecute(String result) {
            super.onPostExecute(result);
            Log.d("프래그먼트", "onPostExecute, " + result);

            titleList.clear();
            seqList.clear();

            if (result != null && !result.isEmpty()) {
                try {
                    JSONArray jsonArray = new JSONArray(result);

                    for (int i = 0; i < jsonArray.length(); i++) {
                        JSONObject jsonObject = jsonArray.getJSONObject(i);
                        String title = jsonObject.optString("title");
                        String seq = jsonObject.optString("seq");
                        titleList.add(title);
                        seqList.add(seq);
                    }

                    if (arrayAdapter == null) {
                        arrayAdapter = new ArrayAdapter<>(getActivity(), android.R.layout.simple_list_item_1, titleList);
                        listView.setAdapter(arrayAdapter);
                    } else {
                        arrayAdapter.notifyDataSetChanged();
                    }

                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        }

        @Override
        protected String doInBackground(String... params) {
            String server_url = "http://15.164.252.136/load_board.php";
            URL url;
            String response = "";
            try {
                url = new URL(server_url);

                HttpURLConnection conn = (HttpURLConnection) url.openConnection();
                conn.setReadTimeout(15000);
                conn.setConnectTimeout(15000);
                conn.setRequestMethod("POST");
                conn.setDoInput(true);
                conn.setDoOutput(true);

                OutputStream os = conn.getOutputStream();
                BufferedWriter writer = new BufferedWriter(new OutputStreamWriter(os, "UTF-8"));
                writer.flush();
                writer.close();
                os.close();

                conn.connect();
                int responseCode = conn.getResponseCode();

                if (responseCode == HttpsURLConnection.HTTP_OK) {
                    String line;
                    BufferedReader br = new BufferedReader(new InputStreamReader(conn.getInputStream()));
                    while ((line = br.readLine()) != null) {
                        response += line;
                    }
                } else {
                    response = "";
                }
            } catch (Exception e) {
                e.printStackTrace();
            }

            return response;
        }
    }
}
