package com.example.nav.Fragment;

import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.example.nav.R;
import com.google.firebase.database.DatabaseException;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

public class RegisterActivity extends AppCompatActivity {

    // 사용할 컴포넌트 선언
    EditText title_et, content_et;
    Button reg_button;

    // 게시글 수정 모드 여부
    boolean isEdit = false;

    // 게시글의 고유 번호
    String board_seq = "";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);

        // ListActivity에서 넘긴 변수들을 받아줌
        isEdit = getIntent().getBooleanExtra("isEdit", false);
        board_seq = getIntent().getStringExtra("board_seq");

        // 컴포넌트 초기화
        title_et = findViewById(R.id.title_et);
        content_et = findViewById(R.id.content_et);
        reg_button = findViewById(R.id.reg_button);

        // 게시글 수정 모드인 경우 데이터 불러오기
        if (isEdit) {
            String title = getIntent().getStringExtra("title");
            String content = getIntent().getStringExtra("content");

            title_et.setText(title);
            content_et.setText(content);
        }

        // 등록/수정 버튼 클릭 이벤트 설정
        reg_button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (isEdit) {
                    // 게시글 수정 모드인 경우
                    updateBoard();
                } else {
                    // 게시글 등록 모드인 경우
                    createBoard();
                }
            }
        });
    }

    private void createBoard() {
        // 제목과 내용 가져오기
        String title = title_et.getText().toString();
        String content = content_et.getText().toString();

        // Firebase 데이터베이스에 게시글 등록
        DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference("board");
        String newKey = databaseReference.push().getKey(); // 새로운 고유 키 생성
        databaseReference.child(newKey).child("title").setValue(title);
        databaseReference.child(newKey).child("content").setValue(content);

        Toast.makeText(this, "게시물이 등록되었습니다.", Toast.LENGTH_SHORT).show();

        // 등록 후 DetailActivity로 이동
        Intent intent = new Intent(RegisterActivity.this, DetailActivity.class);
        intent.putExtra("board_seq", newKey);
        startActivity(intent);

        finish(); // 현재 액티비티 종료
    }

    private void updateBoard() {
        // 제목과 내용 가져오기
        String title = title_et.getText().toString();
        String content = content_et.getText().toString();

        // Firebase 데이터베이스에서 게시글 수정
        DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference("board").child(board_seq);
        databaseReference.child("title").setValue(title);
        databaseReference.child("content").setValue(content);

        Toast.makeText(this, "게시물이 수정되었습니다.", Toast.LENGTH_SHORT).show();

        // 수정 후 DetailActivity로 이동
        Intent intent = new Intent(RegisterActivity.this, DetailActivity.class);
        intent.putExtra("board_seq", board_seq);
        startActivity(intent);

        finish(); // 현재 액티비티 종료
    }
}
