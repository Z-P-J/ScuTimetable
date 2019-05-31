package com.scu.timetable.ui.fragment;

import android.animation.ValueAnimator;
import android.graphics.Color;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.PopupWindow;
import android.widget.ScrollView;
import android.widget.TextView;
import android.widget.Toast;

import com.deadline.statebutton.StateButton;
import com.scu.timetable.R;
import com.scu.timetable.model.EvaluationBean;
import com.scu.timetable.ui.fragment.base.FullscreenDialogFragment;
import com.scu.timetable.ui.view.ElasticScrollView;
import com.scu.timetable.utils.CaptchaFetcher;
import com.scu.timetable.utils.EvaluationUtil;
import com.scu.timetable.utils.LoginUtil;
import com.zpj.popupmenuview.CustomPopupMenuView;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.LinkedList;
import java.util.Locale;
import java.util.Queue;
import java.util.concurrent.atomic.AtomicInteger;

public class EvaluationDialogFragment extends FullscreenDialogFragment implements View.OnClickListener, LoginUtil.Callback, EvaluationUtil.EvaluationCallback {

    private FrameLayout background;

    private float currentAlpha = 0.0f;

    private ElasticScrollView scrollView;
    private LinearLayout consoleView;
    private LinearLayout captchaLayout;
    private EditText captchaEdit;
    private StateButton evaluationButton;

    private AtomicInteger evaluatedCount = new AtomicInteger(0);
    private AtomicInteger notEvaluatedCount = new AtomicInteger(0);

    private int totalNum;

//    private List<EvaluationBean> evaluationBeanList = new ArrayList<>();
    private Queue<EvaluationBean> evaluationBeanQueue = new LinkedList<>();

    private TextView countDownView;

    private static final String countDownText = "%ds后自动进行下一次评教，将应用置于后台休息一下吧(๑‾ ꇴ ‾๑)";

    private final CountDownTimer timer = new CountDownTimer(60 * 1000 * 2, 1000) {
        @Override
        public void onTick(long millisUntilFinished) {
            countDownView.setText(String.format(Locale.getDefault(), countDownText, millisUntilFinished / 1000));
        }

        @Override
        public void onFinish() {
            countDownView.setText("");
            startEvaluation();
        }
    };

    private final Runnable scrollToBottomRunnable = new Runnable() {
        @Override
        public void run() {
            scrollView.fullScroll(ScrollView.FOCUS_DOWN);
        }
    };

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        FrameLayout frameLayout = new FrameLayout(getContext());
        View view = inflater.inflate(R.layout.dialog_fragment_evaluation, null, false);
        frameLayout.addView(view);
        background = new FrameLayout(getContext());
        background.setBackgroundColor(Color.BLACK);
        background.setAlpha(currentAlpha);
        frameLayout.addView(background);
        initView(view);
        return frameLayout;
    }

    private void initView(View view) {

        TextView headerTitle = view.findViewById(R.id.header_title);
        headerTitle.setText("一键评教");

        scrollView = view.findViewById(R.id.scroll_view);

        consoleView = view.findViewById(R.id.console_view);
        consoleLog("请输入验证码，并点击“一键评教”开始评教!");

        captchaLayout = view.findViewById(R.id.layout_captcha);
        ImageView imgCatpcha = view.findViewById(R.id.img_captcha);
        CaptchaFetcher.fetchcaptcha(imgCatpcha);
        TextView changeCatpcha = view.findViewById(R.id.change_captcha);
        changeCatpcha.setOnClickListener(v -> CaptchaFetcher.fetchcaptcha(imgCatpcha));
        captchaEdit = view.findViewById(R.id.captcha);

        evaluationButton = view.findViewById(R.id.btn_evaluation);
        evaluationButton.setOnClickListener(this);
    }

    private void initBackground(View view) {
//        LinearLayout linearLayout = view.findViewById(R.id.container);
//        Bitmap mBitmap = BitmapFactory.decodeResource(getResources(), R.drawable.background_login);
//
//        Bitmap scaledBitmap = Bitmap.createScaledBitmap(mBitmap,
//                mBitmap.getWidth() / 4,
//                mBitmap.getHeight() / 4,
//                false);
//        Bitmap blurBitmap = FastBlur.doBlur(scaledBitmap, 20, true);
//
//        linearLayout.setBackground(new BitmapDrawable(null, blurBitmap));
    }

    public void setBackgroudAlpha(float alpha) {
        ValueAnimator animator = ValueAnimator.ofFloat(currentAlpha, alpha);
        currentAlpha = alpha;
//        if (alpha > 0.0f) {
//            animator = ValueAnimator.ofFloat(0.0f, alpha);;
//        } else {
//            ValueAnimator.ofFloat(0.0f, alpha);
//        }
        animator.setDuration(300);
        animator.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
            @Override
            public void onAnimationUpdate(ValueAnimator animation) {
                float value = (Float) animation.getAnimatedValue();
                background.setAlpha(value);
            }
        });
        animator.start();
    }

    private void showInfoPopupView(View view, final String title, final String content) {
        CustomPopupMenuView.with(getContext(), R.layout.layout_text)
                .setOrientation(LinearLayout.VERTICAL)
//                .setBackgroundAlpha(getActivity(), 0.9f)
                .setPopupViewBackgroundColor(Color.parseColor("#eeffffff"))
//                        .setAnimationTranslationShow(EasyDialog.DIRECTION_X, 350, 100, 0)
//                        .setAnimationTranslationShow(EasyDialog.DIRECTION_Y, 350, -100, 0)
                .setAnimationAlphaShow(350, 0.0f, 1.0f)
                .setAnimationAlphaDismiss(350, 1.0f, 0.0f)
                .initViews(
                        1,
                        (popupMenuView, itemView, position) -> {
                            TextView titleView = itemView.findViewById(R.id.title);
                            titleView.setText(title);
                            TextView contentView = itemView.findViewById(R.id.content);

                            StringBuilder content2 = new StringBuilder(content);
                            if (title.length() >= content2.length()) {
                                for (int i = 0; i < title.length() * 4; i++) {
                                    content2.append(" ");
                                }
                            }
                            contentView.setText(content2.toString());
                            ImageView btnClose = itemView.findViewById(R.id.btn_close);
                            btnClose.setOnClickListener(new View.OnClickListener() {
                                @Override
                                public void onClick(View v) {
                                    popupMenuView.dismiss();

                                }
                            });
                        })
                .setOnPopupWindowDismissListener(new PopupWindow.OnDismissListener() {
                    @Override
                    public void onDismiss() {
                        setBackgroudAlpha(0.0f);
                    }
                })
                .show(view);
        setBackgroudAlpha(0.1f);
    }

    @Override
    public void onClick(View v) {
        int id = v.getId();
        if (id == R.id.btn_back) {
            dismiss();
        } else if (id == R.id.btn_evaluation) {
            String captcha = captchaEdit.getText().toString();
            if (TextUtils.isEmpty(captcha)) {
                Toast.makeText(getContext(), "验证码为空！", Toast.LENGTH_SHORT).show();
                return;
            }
//            LoginUtil.with()
//                    .setCallback(this)
//                    .getCookie();
            evaluationButton.setText("评教中...");
            evaluationButton.setClickable(false);
            consoleLog("\n获取评教教师和助教...");
            LoginUtil.with()
                    .setCallback(this)
                    .login(captchaEdit.getText().toString());
            captchaLayout.setVisibility(View.GONE);
            captchaEdit.setText("");
        }
    }

    @Override
    public void onGetCookie(String cookie) {

    }

    @Override
    public void onLoginSuccess() {
        EvaluationUtil.with(this)
//                .setEvaluationCallback(this)
                .getEvaluationSubjects();
    }

    @Override
    public void onLoginFailed() {
        consoleLog("出错了!", Color.RED);
        notEvaluatedCount.incrementAndGet();
//        checkFinished();
    }

    @Override
    public void onLoginError(String errorMsg) {
        consoleLog("出错了：" + errorMsg, Color.RED);
        notEvaluatedCount.incrementAndGet();
//        checkFinished();
    }

    @Override
    public void onGetTimetable(String json) {
    }

    @Override
    public void onGetEvaluationSubjects(String json) {
//        consoleLog(json);
        consoleLog("获取评教教师和助教成功\n");
        try {
            JSONObject jsonObject = new JSONObject(json);
            int notFinishedNum = jsonObject.getInt("notFinishedNum");
            JSONArray jsonArray = jsonObject.getJSONArray("data");
            totalNum = jsonArray.length();
            consoleLog("共" + totalNum + "人，已评教" + (totalNum - notFinishedNum) + "人，未评教" + notFinishedNum + "人", Color.BLACK);
            for (int i = 0; i < totalNum; i++) {
                JSONObject evaluationObj = jsonArray.getJSONObject(i);
                String evaluatedPeople = evaluationObj.getString("evaluatedPeople");
                String evaluationContent = evaluationObj.getString("evaluationContent");
                JSONObject questionnaire = evaluationObj.getJSONObject("questionnaire");
                String questionnaireName = questionnaire.getString("questionnaireName");
                if ("是".equals(evaluationObj.getString("isEvaluated"))) {
                    consoleLog(questionnaireName + " " + evaluatedPeople + " " + evaluationContent + " 已评教!", Color.GREEN);
                    evaluatedCount.incrementAndGet();
                    continue;
                }
//                String questionnaireNumber = questionnaire.getString("questionnaireNumber");

//                totalNum = 1;
                JSONObject id = evaluationObj.getJSONObject("id");
                String evaluatedPeopleNum = id.getString("evaluatedPeople");
                String evaluationContentNumber = id.getString("evaluationContentNumber");
                String questionnaireCoding = id.getString("questionnaireCoding");
                Log.d("evaluatedPeople", evaluatedPeople);
                Log.d("evaluatedPeopleNum", evaluatedPeopleNum);
                Log.d("questionnaireCoding", questionnaireCoding);
                Log.d("questionnaireName", questionnaireName);
                Log.d("evaluationContentNumber", evaluationContentNumber);
                EvaluationBean bean = new EvaluationBean();
                bean.setEvaluatedPeople(evaluatedPeople);
                bean.setEvaluatedPeopleNum(evaluatedPeopleNum);
                bean.setEvaluationContent(evaluationContent);
                bean.setEvaluationContentNumber(evaluationContentNumber);
                bean.setQuestionnaireCoding(questionnaireCoding);
                bean.setQuestionnaireName(questionnaireName);

//                evaluationBeanList.add(bean);
                evaluationBeanQueue.add(bean);



//                consoleLog(questionnaireName + " " + evaluatedPeople + " " + evaluationContent + " 评教中...", Color.BLACK);
//                EvaluationUtil.with(this)
////                        .setEvaluationCallback(this)
//                        .getEvaluationPage(evaluatedPeople, evaluatedPeopleNum, questionnaireCoding,
//                                questionnaireName, evaluationContentNumber, evaluationContent);
//                break;
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
        startEvaluation();
    }

    @Override
    public void onEvaluationError(String errMsg) {
        consoleLog(errMsg, Color.RED);
        notEvaluatedCount.incrementAndGet();
//        checkFinished();
        newEvaluation();
    }

    @Override
    public void onGetTokenValue(String tokenValue) {

    }

    @Override
    public void onEvaluationFailed(String msg) {
        consoleLog(msg, Color.RED);
        notEvaluatedCount.incrementAndGet();
//        checkFinished();
        newEvaluation();
    }

    @Override
    public void onEvaluationSuccess(String msg) {
        consoleLog(msg, Color.GREEN);
        evaluatedCount.incrementAndGet();
//        checkFinished();
        newEvaluation();
    }

    private void newEvaluation() {
        if (hasEvaluation()) {
            countDownView = new TextView(getContext());
            countDownView.setText(String.format(Locale.getDefault(), countDownText, 120));
            consoleView.addView(countDownView);
            timer.start();
            EvaluationUtil.with(this).post(scrollToBottomRunnable);
        }
    }

    private void startEvaluation() {
        if (hasEvaluation()) {
            EvaluationBean bean = evaluationBeanQueue.remove();
            consoleLog(bean.getQuestionnaireName() + " " + bean.getEvaluatedPeople() + " " + bean.getEvaluationContent() + " 评教中...", Color.BLACK);
            EvaluationUtil.with(this)
                    .getEvaluationPage(bean.getEvaluatedPeople(), bean.getEvaluatedPeopleNum(), bean.getQuestionnaireCoding(),
                            bean.getQuestionnaireName(), bean.getEvaluationContentNumber(), bean.getEvaluationContent());
        }
    }

    private boolean hasEvaluation() {
        if (evaluationBeanQueue.isEmpty()) {
            consoleLog("一键评教完成！ 已评教" + evaluatedCount.get() + "人，评教失败" + notEvaluatedCount.get() + "人");
            evaluationButton.setText("评教完成");
            return false;
        }
        return true;
    }

    private void consoleLog(String msg) {
        TextView textView = new TextView(getContext() == null ? getActivity() : getContext());
        textView.setText(msg);
        consoleView.addView(textView);
        EvaluationUtil.with(this).post(scrollToBottomRunnable);
//        consoleView.setText(consoleView.getText().toString() + "\n" + msg);
    }

    private void consoleLog(String msg, int color) {
        TextView textView = new TextView(getContext() == null ? getActivity() : getContext());
        textView.setText(msg);
        textView.setTextColor(color);
        consoleView.addView(textView);
        EvaluationUtil.with(this).post(scrollToBottomRunnable);
//        String newStr= consoleView.getText().toString() + "\n" + msg;
//        SpannableStringBuilder spannable = new SpannableStringBuilder(newStr);
//        spannable.setSpan(new ForegroundColorSpan(color), newStr.length() - msg.length(), newStr.length(), Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
//        consoleView.setText(spannable);
    }

//    private void checkFinished() {
//        int num1 = evaluatedCount.get();
//        int mu2 = notEvaluatedCount.get();
//        Log.d("checkFinished", "num1=" + num1);
//        Log.d("checkFinished", "mu2=" + mu2);
//        Log.d("checkFinished", "totalNum=" + totalNum);
//        if (num1 + mu2 == totalNum) {
//            consoleLog("一键评教完成！ 已评教" + evaluatedCount.get() + "人，评教失败" + notEvaluatedCount.get() + "人");
//            evaluationButton.setText("评教完成");
//        }
//    }

}
