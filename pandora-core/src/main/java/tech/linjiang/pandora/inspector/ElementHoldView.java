package tech.linjiang.pandora.inspector;

import android.app.Activity;
import android.content.Context;
import android.os.Build;
import android.util.Log;
import android.view.ContextThemeWrapper;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import tech.linjiang.pandora.inspector.model.Element;

/**
 * Created by linjiang on 09/06/2018.
 *
 * https://github.com/eleme/UETool/
 */

public class ElementHoldView extends HintView {
    private static final String TAG = "ElementHoldView";

    public ElementHoldView(Context context) {
        super(context);
    }

    private List<Element> elements = new ArrayList<>();


    private void traverse(View view) {
        if (view.getAlpha() == 0 || view.getVisibility() != View.VISIBLE) return;
        elements.add(new Element(view));
        if (view instanceof ViewGroup) {
            ViewGroup parent = (ViewGroup) view;
            for (int i = 0; i < parent.getChildCount(); i++) {
                traverse(parent.getChildAt(i));
            }
        }
    }

    protected final Element getTargetElement(float x, float y) {
        Element target = null;
        for (int i = elements.size() - 1; i >= 0; i--) {
            final Element element = elements.get(i);
            if (element.getRect().contains((int) x, (int) y)) {
                if (isParentNotVisible(element.getParentElement())) {
                    continue;
                }
                target = element;
                break;
            }
        }
        if (target == null) {
            Log.w(TAG, "getTargetElement: not find");
        }
        return target;
    }

    protected final Element getTargetElement(View v) {
        Element target = null;
        for (int i = elements.size() - 1; i >= 0; i--) {
            final Element element = elements.get(i);
            if (element.getView() == v) {
                target = element;
                break;
            }
        }
        if (target == null) {
            Log.w(TAG, "getTargetElement: not find");
        }
        return target;
    }


    protected final void resetAll() {
        for (Element e : elements) {
            if (e != null) {
                e.reset();
            }
        }
    }

    private boolean isParentNotVisible(Element parent) {
        if (parent == null) {
            return false;
        }
        if (parent.getRect().left >= getMeasuredWidth()
                || parent.getRect().top >= getMeasuredHeight()) {
            return true;
        } else {
            return isParentNotVisible(parent.getParentElement());
        }
    }

    @Override
    protected void onDetachedFromWindow() {
        super.onDetachedFromWindow();
        elements.clear();
    }

    public void tryGetFrontView(Activity targetActivity) {
        try {
            WindowManager windowManager = targetActivity.getWindowManager();
            Field mGlobalField = Class.forName("android.view.WindowManagerImpl").getDeclaredField("mGlobal");
            mGlobalField.setAccessible(true);

            if (Build.VERSION.SDK_INT <= Build.VERSION_CODES.M) {
                Field mViewsField = Class.forName("android.view.WindowManagerGlobal").getDeclaredField("mViews");
                mViewsField.setAccessible(true);
                List<View> views = (List<View>) mViewsField.get(mGlobalField.get(windowManager));
                for (int i = views.size() - 1; i >= 0; i--) {
                    View targetView = getTargetDecorView(targetActivity, views.get(i));
                    if (targetView != null) {
                        traverse(targetView);
                        break;
                    }
                }
            } else {
                Field mRootsField = Class.forName("android.view.WindowManagerGlobal").getDeclaredField("mRoots");
                mRootsField.setAccessible(true);
                List viewRootImpls;
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
                    viewRootImpls = (List) mRootsField.get(mGlobalField.get(windowManager));
                } else {
                    viewRootImpls = Arrays.asList((Object[]) mRootsField.get(mGlobalField.get(windowManager)));
                }
                for (int i = viewRootImpls.size() - 1; i >= 0; i--) {
                    Class clazz = Class.forName("android.view.ViewRootImpl");
                    Object object = viewRootImpls.get(i);
                    Field mWindowAttributesField = clazz.getDeclaredField("mWindowAttributes");
                    mWindowAttributesField.setAccessible(true);
                    Field mViewField = clazz.getDeclaredField("mView");
                    mViewField.setAccessible(true);
                    View decorView = (View) mViewField.get(object);
                    WindowManager.LayoutParams layoutParams = (WindowManager.LayoutParams) mWindowAttributesField.get(object);
                    if (layoutParams.getTitle().toString().contains(targetActivity.getClass().getName())
                            || getTargetDecorView(targetActivity, decorView) != null) {
                        traverse(decorView);
                        break;
                    }
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
            try {
                traverse(targetActivity.getWindow().peekDecorView());
            } catch (Exception ignore){}
        }
    }

    private View getTargetDecorView(Activity targetActivity, View decorView) {
        View targetView = null;
        Context context = decorView.getContext();
        if (context == targetActivity) {
            targetView = decorView;
        } else {
            while (context instanceof ContextThemeWrapper) {
                Context baseContext = ((ContextThemeWrapper) context).getBaseContext();
                if (baseContext == targetActivity) {
                    targetView = decorView;
                    break;
                }
                context = baseContext;
            }
        }
        return targetView;
    }
}
