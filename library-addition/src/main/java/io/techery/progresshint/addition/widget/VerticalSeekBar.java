package io.techery.progresshint.addition.widget;

import android.content.Context;
import android.util.AttributeSet;
import io.techery.progresshint.ProgressHintDelegate;
import io.techery.progresshint.ProgressHintDelegate.SeekBarHintDelegateHolder;
import io.techery.progresshint.addition.VerticalProgressHintDelegate;

public class VerticalSeekBar extends com.h6ah4i.android.widget.verticalseekbar.VerticalSeekBar
    implements SeekBarHintDelegateHolder {

  private ProgressHintDelegate hintDelegate;

  public VerticalSeekBar(Context context) {
    super(context);
    init(null, 0);
  }

  public VerticalSeekBar(Context context, AttributeSet attrs) {
    super(context, attrs);
    init(attrs, 0);
  }

  public VerticalSeekBar(Context context, AttributeSet attrs, int defStyle) {
    super(context, attrs, defStyle);
    init(attrs, defStyle);
  }

  private void init(AttributeSet attrs, int defStyle) {
    if (!isInEditMode()) {
      hintDelegate = new VerticalProgressHintDelegate(this, attrs, defStyle);
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
