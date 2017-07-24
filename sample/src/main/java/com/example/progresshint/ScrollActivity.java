package com.example.progresshint;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import butterknife.BindView;
import butterknife.ButterKnife;
import io.techery.progresshint.addition.ProgressHintScrollController;
import io.techery.progresshint.addition.widget.SeekBar;
import io.techery.progresshint.addition.widget.VerticalSeekBar;

public class ScrollActivity extends AppCompatActivity {

  @BindView(R.id.scroll_container) View scrollView;
  @BindView(R.id.seekbar1) SeekBar seekBar1;
  @BindView(R.id.seekbar2) VerticalSeekBar seekBar2;

  @Override
  protected void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    setContentView(R.layout.activity_scroll);
    ButterKnife.bind(this);

    seekBar1.setProgress(10);
    seekBar2.setProgress(20);

    ProgressHintScrollController.register(scrollView, seekBar1);
    ProgressHintScrollController.register(scrollView, seekBar2);
  }
}
