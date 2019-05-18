package com.zpj.popupmenuview;

import android.content.Context;
import android.graphics.Color;
import android.graphics.Point;
import android.graphics.Rect;
import android.graphics.drawable.ColorDrawable;
import android.util.Log;
import android.view.View;
import android.view.View.MeasureSpec;
import android.view.WindowManager.LayoutParams;
import android.widget.PopupWindow;

/**
 * Created by felix on 16/11/20.
 */
public class PopupView extends PopupWindow {
    private static final String TAG = "PopupView";

    private int mSites = 0x9C;

    private Context mViewContext;

    public static final int SITE_TOP = 0;

    public static final int SITE_LEFT = 1;

    public static final int SITE_RIGHT = 2;

    public static final int SITE_BOTTOM = 3;

    public PopupView(Context context) {
        super(context);
        mViewContext = context;
        setWidth(LayoutParams.WRAP_CONTENT);
        setHeight(LayoutParams.WRAP_CONTENT);
        setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        setFocusable(true);
        setOutsideTouchable(true);
    }

    public void measureContentView() {
        if (getContentView() != null) {
            getContentView().measure(MeasureSpec.UNSPECIFIED, MeasureSpec.UNSPECIFIED);
        }
    }

    public void setSites(int sites) {
        mSites = sites;
    }

    public void setSites(int... sites) {
        if (sites != null) {
            mSites = 0;
            for (int i = 0; i < sites.length; i++) {
                mSites |= sites[i] << (i << 1);
            }
        }
    }

    public int getContentWidth() {
        return getContentView().getMeasuredWidth();
    }

    public int getContentHeight() {
        return getContentView().getMeasuredHeight();
    }

    public int[] reviseFrameAndOrigin(View anchor, Rect frame, Point origin) {
        int[] location = new int[2];
        anchor.getLocationInWindow(location);

        int l1 = location[0];
        int l2 = location[1];
        if (l1 == 0 || l2 == 0) {
            Rect rect = new Rect();
            anchor.getGlobalVisibleRect(rect);
            location[0] = rect.left;
            location[1] = rect.top;
        }

        if (origin.x < 0 || origin.y < 0) {
            origin.set(anchor.getWidth() >> 1, anchor.getHeight() >> 1);
        }

        if (frame.isEmpty() || !frame.contains(origin.x + location[0], origin.y + location[1])) {
            anchor.getWindowVisibleDisplayFrame(frame);
        }

        return location;
    }

    public void show(View anchor) {
        show(anchor, null, null);
    }

    public void show(View anchor, int animRes) {
        show(anchor);
    }

    public void show(View anchor, Point origin) {
        show(anchor, null, origin);
    }

    public void show(View anchor, Rect frame) {
        show(anchor, frame, null);
    }

//    public void show(View anchor, int x, int y){
//        if (x > DisplayUtil.getWidth() / 2) {
//            x = DisplayUtil.getWidth() - x + getContentWidth();
////            if (y > )
//            showAsDropDown(anchor, x, y, ((y < DisplayUtil.getHeight() / 2) ? Gravity.TOP : Gravity.BOTTOM) | Gravity.END);
//        } else {
//            showAsDropDown(anchor, x, y, ((y < DisplayUtil.getHeight() / 2) ? Gravity.TOP : Gravity.BOTTOM) | Gravity.START);
//        }
//
//    }

    public void show(View anchor, Rect frame, Point origin) {

        if (origin == null) {
            origin = new Point(-1, -1);
        }
        if (frame == null) {
            frame = new Rect();
        }


        int[] location = reviseFrameAndOrigin(anchor, frame, origin);

        int x = location[0], y = location[1];
        int width = anchor.getWidth(), height = anchor.getHeight();
        int contentWidth = getContentWidth(), contentHeight = getContentHeight();
        Log.d("whwhwhwhwh", "width=" + width + "   height=" + height);

        Point offset = getOffset(frame, new Rect(x, y + height, contentWidth + x,
                contentHeight + y + height), x + origin.x, y + origin.y);

        int sites = mSites;
        do {
            int site = sites & 0x03;
            Log.d("popupview", "x=" + x + " y=" + y + " contentHeight=" + contentHeight + " contentWidth=" + contentWidth
                    + " frame.left" + frame.left + " frame.right" + frame.right + " frame.top" + frame.top + " frame.bottom" + frame.bottom);
            Log.d("sites", "sites=" + sites);
            switch (site) {
                case SITE_TOP:
                    if (y - contentHeight > frame.top) {
                        showAtTop(anchor, origin, offset.x, -height - contentHeight);
                        return;
                    }
                    break;
                case SITE_LEFT:
                    if (x - contentWidth > frame.left) {
                        showAtLeft(anchor, origin, -contentWidth, offset.y);
                        return;
                    }
                    break;
                case SITE_RIGHT:
                    if (x + width + contentWidth < frame.right) {
                        showAtRight(anchor, origin, width, offset.y);
                        return;
                    }
                    break;
                case SITE_BOTTOM:
                    if (y + height + contentHeight < frame.bottom) {
                        showAtBottom(anchor, origin, offset.x, 0);
                        return;
                    }
                    break;
            }
            if (sites <= 0) {
                showAtTop(anchor, origin, offset.x, -height - contentHeight);
                break;
            }
            sites >>= 2;
        } while (sites >= 0);
    }

    private Point getOffset(Rect frame, Rect rect, int x, int y) {
        Rect rt = new Rect(rect);
        rt.offset(x - rt.centerX(), y - rt.centerY());
        Log.d("getOffset", "frame=" + frame.toString());
        Log.d("getOffset", "rt=" + rt.toString());
        if (!frame.contains(rt)) {
            int offsetX = 0, offsetY = 0;
            if (rt.bottom > frame.bottom) {
                offsetY = frame.bottom - rt.bottom;
            } else if (rt.top < frame.top) {
                offsetY = frame.top - rt.top;
            }
//            offsetX = Math.max(frame.bottom - rt.bottom, frame.top - rt.top);
            Log.d("getOffset", "offsetY111111111=" + (frame.bottom - rt.bottom));
            Log.d("getOffset", "offsetY2222222222=" + (frame.top - rt.top));

            if (rt.right > frame.right) {
                offsetX = frame.right - rt.right;
            } else if (rt.left < frame.left) {
                offsetX = frame.left - rt.left;
            }

            Log.d("getOffset", "offsetX1111111111=" + (frame.right - rt.right));
            Log.d("getOffset", "offsetX2222222222=" + (frame.left - rt.left));

            Log.d("getOffset", "offsetX=" + offsetX);
            Log.d("getOffset", "offsetY=" + offsetY);
            rt.offset(offsetX, offsetY);
        }
        return new Point(rt.left - rect.left, rt.top - rect.top);
    }

    public void showAtTop(View anchor, Point origin, int xOff, int yOff) {
        showAsDropDown(anchor, xOff, yOff);
    }

    public void showAtLeft(View anchor, Point origin, int xOff, int yOff) {
        showAsDropDown(anchor, xOff, yOff);
    }

    public void showAtRight(View anchor, Point origin, int xOff, int yOff) {
        showAsDropDown(anchor, xOff, yOff);
    }

    public void showAtBottom(View anchor, Point origin, int xOff, int yOff) {
        showAsDropDown(anchor, xOff, yOff);
    }

    protected Context getContext() {
        return mViewContext;
    }
}
