package com.scu.timetable;

import android.animation.Animator;
import android.animation.Animator.AnimatorListener;
import android.animation.AnimatorSet;
import android.animation.ObjectAnimator;
import android.animation.PropertyValuesHolder;
import android.app.Activity;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.Window;
import android.view.animation.AccelerateDecelerateInterpolator;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.scu.timetable.utils.JellyInterpolator;

public class MainActivity2 extends Activity implements OnClickListener {

	private TextView mBtnLogin;
	
	private View progress;
	
	private View mInputLayout;

	private LinearLayout mName, mPsw;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		setContentView(R.layout.activity_main);

		initView();
	}

	private void initView() {
		mBtnLogin = findViewById(R.id.main_btn_login);
		progress = findViewById(R.id.layout_progress);
		mInputLayout = findViewById(R.id.input_layout);
		mName = findViewById(R.id.input_user_name);
		mPsw = findViewById(R.id.input_password);

		mBtnLogin.setOnClickListener(this);
	}

	@Override
	public void onClick(View v) {

		float mWidth = mBtnLogin.getMeasuredWidth();
		float mHeight = mBtnLogin.getMeasuredHeight();

		mName.setVisibility(View.INVISIBLE);
		mPsw.setVisibility(View.INVISIBLE);

		inputAnimator(mInputLayout, mWidth, mHeight);

	}

	private void inputAnimator(final View view, float w, float h) {

		AnimatorSet set = new AnimatorSet();

//		ValueAnimator animator = ValueAnimator.ofFloat(0, w);
//		animator.addUpdateListener(new AnimatorUpdateListener() {
//
//			@Override
//			public void onAnimationUpdate(ValueAnimator animation) {
//				float value = (Float) animation.getAnimatedValue();
//				MarginLayoutParams params = (MarginLayoutParams) view
//						.getLayoutParams();
//				params.leftMargin = (int) value;
//				params.rightMargin = (int) value;
//				view.setLayoutParams(params);
//			}
//		});

//		ValueAnimator animator4 = ValueAnimator.ofFloat(h, 0);
//		animator.addUpdateListener(new AnimatorUpdateListener() {
//
//			@Override
//			public void onAnimationUpdate(ValueAnimator animation) {
//				float value = (Float) animation.getAnimatedValue();
//				MarginLayoutParams params = (MarginLayoutParams) view
//						.getLayoutParams();
//				params.topMargin = (int) value;
//				params.bottomMargin = (int) value;
//				view.setLayoutParams(params);
//			}
//		});

		ObjectAnimator animator2 = ObjectAnimator.ofFloat(mInputLayout,
				"scaleX", 1f, 0.0f);
		ObjectAnimator animator3 = ObjectAnimator.ofFloat(mInputLayout,
				"scaleY", 1f, 0.0f);
		set.setDuration(500);
		set.setInterpolator(new AccelerateDecelerateInterpolator());
		set.playTogether(animator2, animator3);
		set.start();
		set.addListener(new AnimatorListener() {

			@Override
			public void onAnimationStart(Animator animation) {

			}

			@Override
			public void onAnimationRepeat(Animator animation) {
				// TODO Auto-generated method stub

			}

			@Override
			public void onAnimationEnd(Animator animation) {

				progress.setVisibility(View.VISIBLE);
				progressAnimator(progress);
				mInputLayout.setVisibility(View.INVISIBLE);

			}

			@Override
			public void onAnimationCancel(Animator animation) {
				// TODO Auto-generated method stub

			}
		});

	}

	private void progressAnimator(final View view) {
		PropertyValuesHolder animator = PropertyValuesHolder.ofFloat("scaleX",
				0.5f, 1f);
		PropertyValuesHolder animator2 = PropertyValuesHolder.ofFloat("scaleY",
				0.5f, 1f);
		ObjectAnimator animator3 = ObjectAnimator.ofPropertyValuesHolder(view,
				animator, animator2);
		animator3.setDuration(1000);
		animator3.setInterpolator(new JellyInterpolator());
		animator3.start();

	}
}
