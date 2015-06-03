package io.techery.progresshint.addition;

import android.graphics.Point;
import android.graphics.PointF;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.widget.SeekBar;
import io.techery.progresshint.ProgressHintDelegate;

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
      case 1:
        return mPopupOffset;
      case -1:
        return mSeekBar.getHeight() + mPopupOffset;
    }
    return 0;
  }

  private int getVerticalOffset(int progress) {
    int yOddOffset = 0;
    switch (getOrientation()) {
      case 1:
        //yOddOffset = offsetPadding + mPopupView.getMeasuredHeight() / 2;
        yOddOffset = mPopupView.getMeasuredHeight() / 2 + mSeekBar.getHeight() / 2 + 1;
        break;
      case -1:
        yOddOffset = mPopupView.getMeasuredHeight() / 2 - mSeekBar.getHeight() / 2 - 1;
        break;
    }
    return getFollowPosition(progress) - yOddOffset;
  }

  private int getOrientation() {
    return (int) (mSeekBar.getRotation() / 90f);
  }
}
