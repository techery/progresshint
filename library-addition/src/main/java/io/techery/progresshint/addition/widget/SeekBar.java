package io.techery.progresshint.addition.widget;

import android.content.Context;
import android.util.AttributeSet;
import io.techery.progresshint.ProgressHintDelegate;
import io.techery.progresshint.addition.HorizontalProgressHintDelegate;

public class SeekBar extends android.support.v7.widget.AppCompatSeekBar implements ProgressHintDelegate.SeekBarHintDelegateHolder {

  private ProgressHintDelegate hintDelegate;

  public SeekBar(Context context) {
    super(context);
    init(null, 0);
  }

  public SeekBar(Context context, AttributeSet attrs) {
    super(context, attrs);
    init(attrs, 0);
  }

  public SeekBar(Context context, AttributeSet attrs, int defStyleAttr) {
    super(context, attrs, defStyleAttr);
    init(attrs, defStyleAttr);
  }

  private void init(AttributeSet attrs, int defStyle) {
    if (!isInEditMode()) {
      hintDelegate = new HorizontalProgressHintDelegate(this, attrs, defStyle);
    }
  }

  @Override public void setOnSeekBarChangeListener(OnSeekBarChangeListener l) {
    super.setOnSeekBarChangeListener(hintDelegate.setOnSeekBarChangeListener(l));
  }

  @Override
  public ProgressHintDelegate getHintDelegate() {
    return hintDelegate;
  }
}
