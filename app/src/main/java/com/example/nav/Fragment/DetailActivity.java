package com.example.nav.Fragment;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.example.nav.R;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

public class DetailActivity extends AppCompatActivity {

    // 로그에 사용할 TAG
    final private String TAG = getClass().getSimpleName();

    // 사용할 컴포넌트 선언
    TextView title_tv, content_tv;
    Button delete_btn, edit_btn;

    // 선택한 게시물의 번호
    String board_seq = "";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_detail);

        // ListActivity에서 넘긴 변수들을 받아줌
        board_seq = getIntent().getStringExtra("board_seq");

        // 컴포넌트 초기화
        title_tv = findViewById(R.id.title_tv);
        content_tv = findViewById(R.id.content_tv);
        delete_btn = findViewById(R.id.delete_btn);
        edit_btn = findViewById(R.id.button3);

        // 해당 게시물의 데이터 불러오기
        loadDataFromFirebase();

        // 삭제 버튼 클릭 이벤트 설정
        delete_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                deleteBoard();
            }
        });

        // 수정 버튼 클릭 이벤트 설정
        edit_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                navigateToEditActivity();
            }
        });
    }

    private void loadDataFromFirebase() {
        // 파이어베이스에서 해당 게시물의 데이터를 가져오는 리스너 설정
        FirebaseDatabase.getInstance().getReference("board").child(board_seq)
                .addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot dataSnapshot) {
                        if (dataSnapshot.exists()) {
                            // 데이터 스냅샷에서 제목과 내용 가져오기
                            String title = dataSnapshot.child("title").getValue(String.class);
                            String content = dataSnapshot.child("content").getValue(String.class);

                            // 제목과 내용 설정
                            title_tv.setText(title);
                            content_tv.setText(content);
                        } else {
                            Log.d(TAG, "Data not found");
                        }
                    }

                    @Override
                    public void onCancelled(DatabaseError databaseError) {
                        Log.e(TAG, "Failed to read data from Firebase", databaseError.toException());
                    }
                });
    }

    private void deleteBoard() {
        // 파이어베이스에서 해당 게시물 삭제
        FirebaseDatabase.getInstance().getReference("board").child(board_seq)
                .removeValue(new DatabaseReference.CompletionListener() {
                    @Override
                    public void onComplete(DatabaseError databaseError, DatabaseReference databaseReference) {
                        if (databaseError == null) {
                            // 삭제 성공
                            Toast.makeText(DetailActivity.this, "게시물이 삭제되었습니다.", Toast.LENGTH_SHORT).show();
                            finish(); // 현재 액티비티 종료
                        } else {
                            // 삭제 실패
                            Toast.makeText(DetailActivity.this, "게시물 삭제에 실패했습니다.", Toast.LENGTH_SHORT).show();
                        }
                    }
                });
    }

    private void navigateToEditActivity() {
        // RegisterActivity로 이동하는 Intent 생성
        Intent intent = new Intent(DetailActivity.this, RegisterActivity.class);
        intent.putExtra("board_seq", board_seq);
        intent.putExtra("isEdit", true);
        intent.putExtra("title", title_tv.getText().toString());
        intent.putExtra("content", content_tv.getText().toString());
        startActivity(intent);
    }
}
