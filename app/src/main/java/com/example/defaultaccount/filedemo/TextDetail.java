package com.example.defaultaccount.filedemo;

import android.databinding.DataBindingUtil;
import android.os.Build;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.Window;
import android.widget.EditText;

import com.example.defaultaccount.filedemo.databinding.ActivityTextDetailBinding;
import com.trello.rxlifecycle2.components.support.RxAppCompatActivity;

public class TextDetail extends RxAppCompatActivity {
    TextDetailViewModel viewModel;
    ActivityTextDetailBinding detailBinding;
    String content;
    String title;
    String path;
    EditText editText;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_text_detail);
        Window window = this.getWindow();
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            window.setStatusBarColor(getResources().getColor(R.color.colorPrimary));
        }
        content = getIntent().getStringExtra("content");
        title = getIntent().getStringExtra("title");
        path = getIntent().getStringExtra("path");
        viewModel = new TextDetailViewModel(this, content, title);
        detailBinding = DataBindingUtil.setContentView(this, R.layout.activity_text_detail);
        detailBinding.setViewModel(viewModel);
        setSupportActionBar(detailBinding.toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

    }

    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_detail, menu);
        return true;
    }

    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == android.R.id.home) {
            finish();
        } else if (item.getItemId() == R.id.text_save) {
            editText=detailBinding.etContent;
            viewModel.saveFile(path,editText.getText().toString());

        }
        return super.onOptionsItemSelected(item);
    }


}
