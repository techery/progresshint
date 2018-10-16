package com.example.progresshint;

import android.os.Bundle;
import android.view.View;
import androidx.appcompat.app.AppCompatActivity;
import io.techery.progresshint.addition.ProgressHintScrollController;
import io.techery.progresshint.addition.widget.SeekBar;
import io.techery.progresshint.addition.widget.VerticalSeekBar;

public class ScrollActivity extends AppCompatActivity {

  View scrollView;
  SeekBar seekBar1;
  VerticalSeekBar seekBar2;

  @Override
  protected void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    setContentView(R.layout.activity_scroll);
    scrollView = findViewById(R.id.scroll_container);
    seekBar1 = findViewById(R.id.seekbar1);
    seekBar2 = findViewById(R.id.seekbar2);

    seekBar1.setProgress(10);
    seekBar2.setProgress(20);

    ProgressHintScrollController.register(scrollView, seekBar1);
    ProgressHintScrollController.register(scrollView, seekBar2);
  }
}
