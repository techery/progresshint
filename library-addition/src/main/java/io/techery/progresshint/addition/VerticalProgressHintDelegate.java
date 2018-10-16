package io.techery.progresshint.addition;

import android.graphics.Point;
import android.graphics.PointF;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.View;
import android.widget.SeekBar;
import androidx.annotation.IntDef;
import io.techery.progresshint.ProgressHintDelegate;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;

public class VerticalProgressHintDelegate extends ProgressHintDelegate {

  public VerticalProgressHintDelegate(SeekBar seekBar, AttributeSet attrs, int defStyleAttr) {
    super(seekBar, attrs, defStyleAttr);
  }

  final int[] tmpCoords = new int[2];

  @Override
  protected PointF getHintDragCoordinates(MotionEvent event) {
    mSeekBar.getLocationOnScreen(tmpCoords);
    float x;
    float y = mSeekBar.getY();
    switch (getOrientation()) {
      case CW:
        x = event.getRawY() - tmpCoords[1];
        break;
      case CCW:
        x = tmpCoords[1] - event.getRawY();
        break;
      default:
        throw new IllegalStateException("This widget orientation is not supported");
    }
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

  ///////////////////////////////////////////////////////////////////////////
  // Visibility Helper
  ///////////////////////////////////////////////////////////////////////////

  @Override public boolean isWidgetFullyVisible(View container) {
    int relativeTop = ViewUtil.getRelativeTop(mSeekBar, container);
    int followPosition = getFollowPosition(getPopupStyle() == POPUP_FOLLOW ? mSeekBar.getProgress() : mSeekBar.getMax() / 2);
    //
    boolean fitsTop;
    boolean fitsBottom;
    switch (getOrientation()) {
      case CW:
        fitsTop =
            relativeTop + followPosition + mSeekBar.getPaddingLeft() - mPopupView.getHeight() / 2 > container.getScrollY();
        fitsBottom = container.getHeight() + container.getScrollY() >
            relativeTop + followPosition + mSeekBar.getPaddingRight() + mPopupView.getHeight() / 2;
        break;
      case CCW:
        fitsTop = relativeTop + (mSeekBar.getWidth() - followPosition) - mPopupView.getHeight() > container.getScrollY();
        fitsBottom = container.getHeight() + container.getScrollY() + (followPosition + mSeekBar.getPaddingLeft()) >
            relativeTop + mSeekBar.getWidth() + mPopupView.getHeight() / 2;
        break;
      default:
        throw new IllegalStateException("This widget orientation is not supported");
    }
    //
    return fitsTop && fitsBottom;
  }
}
