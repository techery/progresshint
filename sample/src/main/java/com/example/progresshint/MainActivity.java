package com.example.progresshint;

import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.SeekBar;
import androidx.appcompat.app.AppCompatActivity;
import java.util.Arrays;
import java.util.List;

import static io.techery.progresshint.ProgressHintDelegate.POPUP_FIXED;
import static io.techery.progresshint.ProgressHintDelegate.POPUP_FOLLOW;
import static io.techery.progresshint.ProgressHintDelegate.SeekBarHintAdapter;
import static io.techery.progresshint.ProgressHintDelegate.SeekBarHintDelegateHolder;

public class MainActivity extends AppCompatActivity {

  List<SeekBarHintDelegateHolder> seekBars;

  @Override protected void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    setContentView(R.layout.activity_main);
    seekBars = Arrays.asList(
        ((SeekBarHintDelegateHolder) findViewById(R.id.seekbar_horizontal1)),
        ((SeekBarHintDelegateHolder) findViewById(R.id.seekbar_horizontal2)),
        ((SeekBarHintDelegateHolder) findViewById(R.id.seekbar_vertical1)),
        ((SeekBarHintDelegateHolder) findViewById(R.id.seekbar_vertical2))
    );
    //®
    ((SeekBarHintDelegateHolder) findViewById(R.id.seekbar_vertical2)).getHintDelegate()
        .setHintAdapter(new SeekBarHintAdapter() {
          @Override public String getHint(android.widget.SeekBar seekBar, int progress) {
            return "V. Progress: " + String.valueOf(progress);
          }
        });

    SeekBar bar = findViewById(R.id.seekbar_vertical2);
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
      case R.id.action_open_scroll:
        startActivity(new Intent(this, ScrollActivity.class));
        return true;
    }
    for (SeekBarHintDelegateHolder seekBar : seekBars) {
      seekBar.getHintDelegate().setPopupStyle(style);
    }
    return true;
  }
}
