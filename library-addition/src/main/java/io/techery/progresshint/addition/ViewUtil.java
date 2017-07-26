package io.techery.progresshint.addition;

import android.view.View;

final class ViewUtil {

  static int getRelativeTop(View view, View container) {
    if (view.getParent() == container) return view.getTop();
    else return view.getTop() + getRelativeTop((View) view.getParent(), container);
  }
}
