package com.zpj.fragmentation.dialog.imagetrans.listener;

/**
 * Created by liuting on 17/6/15.
 */

public interface OnTransformListener {

    void transformStart();

    void transformEnd();

    void onTransform(float ratio);
}
