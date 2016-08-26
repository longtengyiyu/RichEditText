package com.longtengyiyu.et;

import android.content.Context;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.TextView;

import com.longtengyiyu.et.view.RichEditText;

public class MainActivity extends AppCompatActivity implements View.OnClickListener{

    private RichEditText mEditText;
    private TextView mTextView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        findViews();
    }

    private void findViews(){
        mEditText = (RichEditText) findViewById(R.id.edit_text);
        mTextView = (TextView) findViewById(R.id.text_view);
        mEditText.setText("这个是一个测试的#话题#");
        mEditText.setSelection(mEditText.getText().toString().length());
        mTextView.setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        int id = v.getId();
        switch (id){
            case R.id.text_view:
                mEditText.replaceTopicImage();
                break;
        }
    }
}
