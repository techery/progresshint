package io.techery.progresshint.addition;

import android.graphics.Point;
import android.graphics.PointF;
import android.support.annotation.IntDef;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.widget.SeekBar;
import io.techery.progresshint.ProgressHintDelegate;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;

public class VerticalProgressHintDelegate extends ProgressHintDelegate {

  public VerticalProgressHintDelegate(SeekBar seekBar, AttributeSet attrs, int defStyleAttr) {
    super(seekBar, attrs, defStyleAttr);
  }

  final int[] tmpCoords = new int[2];

  @Override protected PointF getHintDragCoordinates(MotionEvent event) {
    mSeekBar.getLocationOnScreen(tmpCoords);
    float x = (event.getRawY() - tmpCoords[1]) * getOrientation();
    float y = mSeekBar.getY();
    return new PointF(x, y);
  }

  @Override protected Point getFixedHintOffset() {
    int xOffset = getHorizontalOffset();
    int yOffset = getVerticalOffset(mSeekBar.getMax() / 2);
    return new Point(xOffset, yOffset);
  }

  @Override protected Point getFollowHintOffset() {
    int xOffset = getHorizontalOffset();
    int yOffset = getVerticalOffset(mSeekBar.getProgress());
    return new Point(xOffset, yOffset);
  }

  private int getHorizontalOffset() {
    switch (getOrientation()) {
      case CW:
        return mPopupOffset;
      case CCW:
        return mSeekBar.getHeight() + mPopupOffset;
      default:
        throw new IllegalStateException("This widget orientation is not supported");
    }
  }

  private int getVerticalOffset(int progress) {
    int followPosition = getFollowPosition(progress);
    int yOddOffset;
    switch (getOrientation()) {
      case CW:
        yOddOffset = mPopupView.getMeasuredHeight() / 2 + mSeekBar.getHeight() / 2 - 15;
        return followPosition - yOddOffset;
      case CCW:
        yOddOffset = mPopupView.getMeasuredHeight() / 2 + mSeekBar.getHeight() * 2;
        return -followPosition - yOddOffset;
      default:
        throw new IllegalStateException("This widget orientation is not supported");
    }
  }

  @VerticalOrientation private int getOrientation() {
    return (int) (mSeekBar.getRotation() / 90f) == 1 ? CW : CCW;
  }

  @Retention(RetentionPolicy.SOURCE) @IntDef({ CW, CCW }) public @interface VerticalOrientation {}

  private static final int CW = 1;
  private static final int CCW = 0;
}
