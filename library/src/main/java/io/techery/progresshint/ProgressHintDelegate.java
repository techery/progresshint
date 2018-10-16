package io.techery.progresshint;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Point;
import android.graphics.PointF;
import android.os.Build;
import android.os.Handler;
import android.util.AttributeSet;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnAttachStateChangeListener;
import android.view.View.OnTouchListener;
import android.view.ViewTreeObserver.OnGlobalLayoutListener;
import android.widget.PopupWindow;
import android.widget.SeekBar;
import android.widget.SeekBar.OnSeekBarChangeListener;
import android.widget.TextView;
import androidx.annotation.IntDef;
import androidx.annotation.LayoutRes;
import androidx.annotation.NonNull;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;

import static android.view.View.MeasureSpec.UNSPECIFIED;
import static android.view.View.MeasureSpec.makeMeasureSpec;
import static android.view.ViewGroup.LayoutParams.WRAP_CONTENT;

@SuppressWarnings({ "WeakerAccess", "unused" })
public abstract class ProgressHintDelegate implements OnSeekBarChangeListener {

  protected SeekBar mSeekBar;

  protected PopupWindow mPopup;
  protected View mPopupView;
  protected TextView mPopupTextView;

  private int mPopupLayout;
  protected int mPopupOffset;
  private boolean mPopupAlwaysShown;
  private boolean mPopupDraggable;
  protected int mPopupStyle;
  private int mPopupAnimStyle;

  public static final int POPUP_FIXED = 1;
  public static final int POPUP_FOLLOW = 0;

  @Retention(RetentionPolicy.SOURCE) @IntDef({ POPUP_FIXED, POPUP_FOLLOW }) public @interface PopupStyle {}

  private SeekBarHintAdapter mHintAdapter;
  private SeekBarHintAttacher mHintAttacher;
  private ProxyChangeListener listener = new ProxyChangeListener();

  private Handler handler = new Handler();
  private boolean isTracking;

  public ProgressHintDelegate(SeekBar seekBar, int mPopupLayout, int mPopupOffset, boolean mPopupAlwaysShown, boolean mPopupDraggable,
      int mPopupStyle, int mPopupAnimStyle) {
    initDelegate(seekBar, mPopupLayout, mPopupOffset, mPopupAlwaysShown, mPopupDraggable, mPopupStyle, mPopupAnimStyle);
  }

  public ProgressHintDelegate(SeekBar seekBar, AttributeSet attrs, int defStyleAttr) {
    TypedArray a = seekBar.getContext().obtainStyledAttributes(attrs, R.styleable.ProgressHint, defStyleAttr, R.style.Widget_ProgressHint);
    //
    int mPopupLayout = a.getResourceId(R.styleable.ProgressHint_popupLayout, R.layout.progress_hint_popup);
    int mPopupOffset = (int) a.getDimension(R.styleable.ProgressHint_popupOffset, 0);
    int mPopupStyle = a.getInt(R.styleable.ProgressHint_popupStyle, ProgressHintDelegate.POPUP_FOLLOW);
    int mPopupAnimStyle = a.getResourceId(R.styleable.ProgressHint_popupAnimationStyle, R.style.ProgressHintPopupAnimation);
    boolean mPopupAlwaysShown = a.getBoolean(R.styleable.ProgressHint_popupAlwaysShown, false);
    boolean mPopupDraggable = a.getBoolean(R.styleable.ProgressHint_popupDraggable, false);
    a.recycle();
    //
    initDelegate(seekBar, mPopupLayout, mPopupOffset, mPopupAlwaysShown, mPopupDraggable, mPopupStyle, mPopupAnimStyle);
  }

  private void initDelegate(SeekBar seekBar, int mPopupLayout, int mPopupOffset, boolean mPopupAlwaysShown, boolean mPopupDraggable,
      int mPopupStyle, int mPopupAnimStyle) {
    this.mSeekBar = seekBar;
    this.mPopupLayout = mPopupLayout;
    this.mPopupOffset = mPopupOffset;
    this.mPopupAlwaysShown = mPopupAlwaysShown;
    this.mPopupDraggable = mPopupDraggable;
    this.mPopupStyle = mPopupStyle;
    this.mPopupAnimStyle = mPopupAnimStyle;
    this.mHintAdapter = ProgressHintDelegate.DEFAULT_HINT_ADAPTER;

    initHintPopup();
    attachSeekBar();
  }

  private void initHintPopup() {
    String popupText = null;
    if (mHintAdapter != null) {
      popupText = mHintAdapter.getHint(mSeekBar, mSeekBar.getProgress());
    }

    // init views
    LayoutInflater inflater = (LayoutInflater) mSeekBar.getContext().getSystemService(Context.LAYOUT_INFLATER_SERVICE);
    mPopupView = inflater.inflate(mPopupLayout, null);
    mPopupView.measure(makeMeasureSpec(0, UNSPECIFIED), makeMeasureSpec(0, UNSPECIFIED));
    mPopupTextView = mPopupView.findViewById(android.R.id.text1);
    mPopupTextView.setText(popupText != null ? popupText : String.valueOf(mSeekBar.getProgress()));
    // init popup
    mPopup = new PopupWindow(mPopupView, WRAP_CONTENT, WRAP_CONTENT, false);
    mPopup.setAnimationStyle(mPopupAnimStyle);
  }

  private void attachSeekBar() {
    final OnGlobalLayoutListener layoutListener = new OnGlobalLayoutListener() {
      @Override public void onGlobalLayout() {
        if (mSeekBar.getVisibility() != View.VISIBLE) hidePopup();
        else checkInitialState();
      }
    };
    mSeekBar.addOnAttachStateChangeListener(new OnAttachStateChangeListener() {
      @Override public void onViewAttachedToWindow(View v) {
        mSeekBar.setOnSeekBarChangeListener(listener);
        mSeekBar.getViewTreeObserver().addOnGlobalLayoutListener(layoutListener);
        if (mHintAttacher != null) mHintAttacher.onAttached();
      }

      @Override public void onViewDetachedFromWindow(View v) {
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.JELLY_BEAN) {
          mSeekBar.getViewTreeObserver().removeGlobalOnLayoutListener(layoutListener);
        } else {
          mSeekBar.getViewTreeObserver().removeOnGlobalLayoutListener(layoutListener);
        }
        hidePopup();
      }
    });
    this.listener.setInternalListener(this);
  }

  private void checkInitialState() {
    setPopupAlwaysShown(mPopupAlwaysShown);
    setPopupDraggable(mPopupDraggable);
  }

  private OnTouchListener popupTouchProxy = new OnTouchListener() {
    @SuppressLint("ClickableViewAccessibility")
    @Override public boolean onTouch(View v, MotionEvent event) {
      PointF coordinates = getHintDragCoordinates(event);
      MotionEvent modifiedEvent = MotionEvent.obtain(event.getDownTime(), event.getEventTime(), event.getAction(),
          coordinates.x, coordinates.y, event.getMetaState()
      );
      boolean dispatched = mSeekBar.dispatchTouchEvent(modifiedEvent);
      modifiedEvent.recycle();
      return dispatched;
    }
  };

  protected abstract PointF getHintDragCoordinates(MotionEvent event);

  public void showPopup() {
    handler.removeCallbacksAndMessages(null);
    handler.post(new Runnable() {
      @Override public void run() {
        showPopupInternally();
      }
    });
  }

  private void showPopupInternally() {
    final Point offsetPoint;
    switch (mPopupStyle) {
      case POPUP_FOLLOW:
        offsetPoint = getFollowHintOffset();
        break;
      case POPUP_FIXED:
        offsetPoint = getFixedHintOffset();
        break;
      default:
        throw new IllegalArgumentException("Popup style is unknown");
    }
    mPopup.showAtLocation(mSeekBar, Gravity.NO_GRAVITY, 0, 0);
    mPopup.update(mSeekBar, offsetPoint.x, offsetPoint.y, -1, -1);
  }

  public void hidePopup() {
    handler.removeCallbacksAndMessages(null);
    if (mPopup.isShowing()) mPopup.dismiss();
  }

  public boolean isPopupVisible() {
    return mPopup.isShowing();
  }

  public abstract boolean isWidgetFullyVisible(View container);

  ///////////////////////////////////////////////////////////////////////////
  // Public api
  ///////////////////////////////////////////////////////////////////////////

  public SeekBar getSeekBar() {
    return mSeekBar;
  }

  @LayoutRes public int getPopupLayout() {
    return mPopupLayout;
  }

  public void setPopupLayout(@LayoutRes int layout) {
    this.mPopupLayout = layout;
    if (mPopup != null) mPopup.dismiss();
    initHintPopup();
    checkInitialState();
  }

  @PopupStyle public int getPopupStyle() {
    return mPopupStyle;
  }

  public void setPopupStyle(@PopupStyle int style) {
    mPopupStyle = style;
    if (mPopupAlwaysShown) showPopup();
  }

  public boolean isPopupAlwaysShown() {
    return mPopupAlwaysShown;
  }

  public void setPopupAlwaysShown(boolean alwaysShown) {
    this.mPopupAlwaysShown = alwaysShown;
    if (alwaysShown) {
      showPopup();
      listener.onProgressChanged(mSeekBar, mSeekBar.getProgress(), false);
    } else if (!isTracking) {
      hidePopup();
    }
  }

  public boolean isPopupDraggable() {
    return mPopupDraggable;
  }

  public void setPopupDraggable(boolean draggable) {
    this.mPopupDraggable = draggable;
    if (mPopupView != null) mPopupView.setOnTouchListener(draggable ? popupTouchProxy : null);
  }

  public void setHintAdapter(@NonNull SeekBarHintAdapter adapter) {
    mHintAdapter = adapter;
    if (mPopupTextView != null) {
      mPopupTextView.setText(mHintAdapter.getHint(mSeekBar, mSeekBar.getProgress()));
    }
  }

  public void setHintAttacher(@NonNull SeekBarHintAttacher listener) {
    this.mHintAttacher = listener;
  }

  ///////////////////////////////////////////////////////////////////////////
  // Progress tracking
  ///////////////////////////////////////////////////////////////////////////

  private static class ProxyChangeListener implements OnSeekBarChangeListener {

    private OnSeekBarChangeListener mInternalListener;
    private OnSeekBarChangeListener mExternalListener;

    void setInternalListener(OnSeekBarChangeListener l) {
      mInternalListener = l;
    }

    void setExternalListener(OnSeekBarChangeListener l) {
      mExternalListener = l;
    }

    @Override public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
      if (mInternalListener != null) {
        mInternalListener.onProgressChanged(seekBar, progress, fromUser);
      }
      if (mExternalListener != null) {
        mExternalListener.onProgressChanged(seekBar, progress, fromUser);
      }
    }

    @Override public void onStartTrackingTouch(SeekBar seekBar) {
      if (mInternalListener != null) mInternalListener.onStartTrackingTouch(seekBar);
      if (mExternalListener != null) mExternalListener.onStartTrackingTouch(seekBar);
    }

    @Override public void onStopTrackingTouch(SeekBar seekBar) {
      if (mInternalListener != null) mInternalListener.onStopTrackingTouch(seekBar);
      if (mExternalListener != null) mExternalListener.onStopTrackingTouch(seekBar);
    }
  }

  public OnSeekBarChangeListener setOnSeekBarChangeListener(@NonNull OnSeekBarChangeListener l) {
    if (l instanceof ProxyChangeListener) {
      listener = (ProxyChangeListener) l;
    } else {
      listener.setExternalListener(l);
    }
    return listener;
  }

  @Override public void onStartTrackingTouch(SeekBar seekBar) {
    isTracking = true;
    showPopupInternally();
  }

  @Override public void onStopTrackingTouch(SeekBar seekBar) {
    isTracking = false;
    if (!mPopupAlwaysShown) hidePopup();
  }

  @Override public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
    String popupText = mHintAdapter.getHint(mSeekBar, progress);
    mPopupTextView.setText(popupText);

    if (mPopupStyle == POPUP_FOLLOW) {
      Point offsetPoint = getFollowHintOffset();
      mPopup.update(mSeekBar, offsetPoint.x, offsetPoint.y, -1, -1);
    }
  }

  protected abstract Point getFixedHintOffset();

  protected abstract Point getFollowHintOffset();

  protected int getFollowPosition() {
    return getFollowPosition(mSeekBar.getProgress());
  }

  protected int getFollowPosition(int progress) {
    return (int) (progress * (mSeekBar.getWidth() - mSeekBar.getPaddingLeft() - mSeekBar.getPaddingRight()) / (float) mSeekBar.getMax());
  }

  ///////////////////////////////////////////////////////////////////////////
  // Hint interfaces
  ///////////////////////////////////////////////////////////////////////////

  public interface SeekBarHintDelegateHolder {
    ProgressHintDelegate getHintDelegate();
  }

  public interface SeekBarHintAttacher {
    void onAttached();
  }

  public interface SeekBarHintAdapter {
    @NonNull String getHint(SeekBar seekBar, int progress);
  }

  public static final SeekBarHintAdapter DEFAULT_HINT_ADAPTER = new SeekBarHintAdapter() {
    @NonNull @Override public String getHint(SeekBar seekBar, int progress) {
      return String.valueOf(progress);
    }
  };
}
