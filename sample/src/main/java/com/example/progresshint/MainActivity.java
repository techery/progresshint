package com.example.progresshint;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.SeekBar;
import butterknife.BindViews;
import butterknife.ButterKnife;
import java.util.List;

import static io.techery.progresshint.ProgressHintDelegate.POPUP_FIXED;
import static io.techery.progresshint.ProgressHintDelegate.POPUP_FOLLOW;
import static io.techery.progresshint.ProgressHintDelegate.SeekBarHintAdapter;
import static io.techery.progresshint.ProgressHintDelegate.SeekBarHintDelegateHolder;

public class MainActivity extends AppCompatActivity {

  @BindViews({
      R.id.seekbar_horizontal1, R.id.seekbar_horizontal2, R.id.seekbar_vertical1,
      R.id.seekbar_vertical2
  }) List<SeekBarHintDelegateHolder> seekBars;

  @Override protected void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    setContentView(R.layout.activity_main);
    ButterKnife.bind(this);
    //
    ((SeekBarHintDelegateHolder) findViewById(R.id.seekbar_vertical2)).getHintDelegate()
        .setHintAdapter(new SeekBarHintAdapter() {
          @Override public String getHint(android.widget.SeekBar seekBar, int progress) {
            return "V. Progress: " + String.valueOf(progress);
          }
        });

    SeekBar bar = ButterKnife.findById(this, R.id.seekbar_vertical2);
    bar.setProgress(50);
  }

  @Override public boolean onCreateOptionsMenu(Menu menu) {
    getMenuInflater().inflate(R.menu.menu_main, menu);
    return true;
  }

  @Override public boolean onOptionsItemSelected(MenuItem item) {
    int style = POPUP_FIXED;
    switch (item.getItemId()) {
      case R.id.action_fixed:
        style = POPUP_FIXED;
        break;
      case R.id.action_follow:
        style = POPUP_FOLLOW;
        break;
    }
    for (SeekBarHintDelegateHolder seekBar : seekBars) {
      seekBar.getHintDelegate().setPopupStyle(style);
    }
    return true;
  }
}
