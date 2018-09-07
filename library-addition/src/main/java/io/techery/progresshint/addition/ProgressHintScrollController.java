package io.techery.progresshint.addition;

import android.view.View;
import android.view.ViewTreeObserver;
import io.techery.progresshint.ProgressHintDelegate;
import io.techery.progresshint.ProgressHintDelegate.SeekBarHintDelegateHolder;

public class ProgressHintScrollController implements ViewTreeObserver.OnScrollChangedListener {

  private final View container;
  private final ProgressHintDelegate delegate;

  public static ProgressHintScrollController register(View container, SeekBarHintDelegateHolder holder) {
    ProgressHintScrollController controller = new ProgressHintScrollController(container, holder);
    container.getViewTreeObserver().addOnScrollChangedListener(controller);
    return controller;
  }

  ProgressHintScrollController(View container, SeekBarHintDelegateHolder holder) {
    this.container = container;
    this.delegate = holder.getHintDelegate();
  }

  @Override public void onScrollChanged() {
    if (delegate.isPopupVisible()) {
      if (!delegate.isWidgetFullyVisible(container)) delegate.hidePopup();
    } else if (delegate.isPopupAlwaysShown() && delegate.isWidgetFullyVisible(container)) {
      delegate.showPopup();
    }
  }

  public void dispose() {
    container.getViewTreeObserver().removeOnScrollChangedListener(this);
  }
}
