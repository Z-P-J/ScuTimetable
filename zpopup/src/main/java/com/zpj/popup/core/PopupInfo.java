package com.zpj.popup.core;

import android.graphics.PointF;
import android.view.View;
import android.view.ViewGroup;
import com.zpj.popup.animator.PopupAnimator;
import com.zpj.popup.enums.PopupAnimation;
import com.zpj.popup.enums.PopupPosition;
import com.zpj.popup.enums.PopupType;
import com.zpj.popup.interfaces.XPopupCallback;

/**
 * Description: Popup的属性封装
 * Create by dance, at 2018/12/8
 */
public class PopupInfo {
    public PopupType popupType = null; //窗体的类型
    public Boolean handleBackPressedEvent = true;  //按返回键是否消失
    public Boolean isDismissOnBackPressed = true;  //按返回键是否消失
    public Boolean isDismissOnTouchOutside = true; //点击外部消失
    public Boolean handleTouchOutsideEvent = true; //点击外部消失
    public Boolean autoDismiss = true; //操作完毕后是否自动关闭
    public Boolean hasShadowBg = true; // 是否有半透明的背景
    public View atView = null; // 依附于那个View显示
    public View watchView = null; // 依附于那个View显示
    // 动画执行器，如果不指定，则会根据窗体类型popupType字段生成默认合适的动画执行器
    public PopupAnimation popupAnimation = null;
    public PopupAnimator customAnimator = null;
    public PointF touchPoint = null; // 触摸的点
    public int maxWidth; // 最大宽度
    public int maxHeight; // 最大高度
    public Boolean autoOpenSoftInput = false;//是否自动打开输入法
    public XPopupCallback xPopupCallback;

    public ViewGroup decorView; //每个弹窗所属的DecorView
    public Boolean isMoveUpToKeyboard = true; //是否移动到软键盘上面，默认弹窗会移到软键盘上面
    public PopupPosition popupPosition = null; //弹窗出现在目标的什么位置
    public Boolean hasStatusBarShadow = false;
    public int offsetX, offsetY;//x，y方向的偏移量
    public Boolean enableDrag = true;//是否启用拖拽
    public boolean isCenterHorizontal = false;//是否水平居中
    public boolean isRequestFocus = true; //弹窗是否强制抢占焦点
    public boolean autoFocusEditText = true; //是否让输入框自动获取焦点
//    public boolean isClickThrough = true;//是否点击透传，默认弹背景点击是拦截的

    public View getAtView() {
        return atView;
    }

}
