package com.scu.timetable.ui.fragment;

import android.graphics.Color;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ScrollView;
import android.widget.TextView;

import com.deadline.statebutton.StateButton;
import com.felix.atoast.library.AToast;
import com.scu.timetable.R;
import com.scu.timetable.bean.EvaluationInfo;
import com.scu.timetable.ui.fragment.base.SkinChangeFragment;
import com.scu.timetable.ui.fragment.dialog.MoreInfoDialog;
import com.scu.timetable.ui.widget.ElasticScrollView;
import com.scu.timetable.utils.CaptchaFetcher;
import com.scu.timetable.utils.EvaluationUtil;
import com.scu.timetable.utils.LoginUtil;
import com.zpj.fragmentation.dialog.impl.AlertDialogFragment;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.LinkedList;
import java.util.Locale;
import java.util.Queue;
import java.util.concurrent.atomic.AtomicInteger;

public class EvaluationFragment extends SkinChangeFragment
        implements View.OnClickListener,
        LoginUtil.LoginCallback,
        EvaluationUtil.EvaluationCallback {

    private static final String TAG = "EvaluationFragment";

    private FrameLayout background;

    private float currentAlpha = 0.0f;

    private ElasticScrollView scrollView;
    private LinearLayout consoleView;
    private LinearLayout captchaLayout;
    private EditText captchaEdit;
    private StateButton evaluationButton;

    private AtomicInteger evaluatedCount = new AtomicInteger(0);
    private AtomicInteger notEvaluatedCount = new AtomicInteger(0);

    private Queue<EvaluationInfo> evaluationInfoQueue = new LinkedList<>();

    private TextView countDownView;

//    private static final String COUNT_DOWN_TEXT = "%ds后自动进行下一次评教，\n将应用置于后台休息一下吧(๑‾ ꇴ ‾๑)";
    private static final String COUNT_DOWN_TEXT = "%ds后自动开始评教，\n将应用置于后台休息一下吧(๑‾ ꇴ ‾๑)";

    private boolean isEvaluating;

    private EvaluationUtil.EvaluationEvent currentEvent;

    private final CountDownTimer timer = new CountDownTimer(10 * 1000, 1000) {
        @Override
        public void onTick(long millisUntilFinished) {
            countDownView.setText(String.format(Locale.getDefault(), COUNT_DOWN_TEXT, millisUntilFinished / 1000));
        }

        @Override
        public void onFinish() {
            countDownView.setText("");
//            startEvaluation();
            if (currentEvent != null) {
                EvaluationUtil.with(EvaluationFragment.this).evaluation(currentEvent.getEvaluationBean(), currentEvent.getConnection());
            }
        }
    };

    private final Runnable scrollToBottomRunnable = new Runnable() {
        @Override
        public void run() {
            scrollView.fullScroll(ScrollView.FOCUS_DOWN);
        }
    };

    @Override
    protected int getLayoutId() {
        return R.layout.fragment_evaluation;
    }

    @Override
    public int getToolbarTitleId() {
        return R.string.text_title_evaluate;
    }

    @Override
    protected boolean supportSwipeBack() {
        return true;
    }

    @Override
    protected void initView(View view, @Nullable Bundle savedInstanceState) {
        scrollView = view.findViewById(R.id.scroll_view);

        consoleView = view.findViewById(R.id.console_view);
        consoleLog("请输入验证码，并点击“一键评教”开始评教");

        captchaLayout = view.findViewById(R.id.layout_captcha);
        ImageView imgCatpcha = view.findViewById(R.id.img_captcha);
        CaptchaFetcher.fetchCaptcha(imgCatpcha);
        TextView changeCatpcha = view.findViewById(R.id.change_captcha);
        changeCatpcha.setOnClickListener(v -> CaptchaFetcher.fetchCaptcha(imgCatpcha));
        captchaEdit = view.findViewById(R.id.captcha);

        evaluationButton = view.findViewById(R.id.btn_evaluation);
        evaluationButton.setOnClickListener(this);
    }

    @Override
    public void toolbarRightImageButton(@NonNull ImageButton imageButton) {
        imageButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showInfoPopupView(
                        imageButton,
                        "关于一键评教",
                        "1.一键评教将自动对未评教的教师或助教进行评教。\n" +
                                "2.由于教务系统服务器的限制，每成功评教一次将等待两分钟。\n" +
                                "3.教师或助教的主观评价将从默认的十几条评价中随机选择。"
                );
            }
        });
    }

    @Override
    public boolean onBackPressedSupport() {
        if (isEvaluating) {
            new AlertDialogFragment()
                    .setTitle("确认返回！")
                    .setContent("返回后将终止评教，确认返回？")
                    .setPositiveButton(popup -> {
                        timer.cancel();
                        setSwipeBackEnable(true);
                        pop();
                    })
                    .show(context);
            return true;
        }
        pop();
        return true;
    }

    private void showInfoPopupView(View view, final String title, final String content) {
        new MoreInfoDialog()
                .setTitle(title)
                .setContent(content)
                .show(context);
    }

    @Override
    public void onClick(View v) {
        int id = v.getId();
        if (id == R.id.btn_evaluation) {
            String captcha = captchaEdit.getText().toString();
            if (TextUtils.isEmpty(captcha)) {
                AToast.normal("验证码为空！");
                return;
            }
            isEvaluating = true;
            setSwipeBackEnable(false);
            evaluationButton.setText("评教中...");
            evaluationButton.setClickable(false);
            consoleLog("\n获取评教教师和助教...");
            LoginUtil.with()
                    .setLoginCallback(this)
                    .checkCaptcha(captchaEdit.getText().toString());
            captchaLayout.setVisibility(View.GONE);
            captchaEdit.setText("");
        }
    }

    @Override
    public void onGetCookie(String cookie) {
        Log.d(TAG, "onGetCookie cookie=" + cookie);
    }

    @Override
    public void onLoginSuccess() {
        Log.d(TAG, "onLoginSuccess");
        EvaluationUtil.with(this)
//                .setEvaluationCallback(this)
                .getEvaluationSubjects();
    }

    @Override
    public void onLoginFailed() {
        Log.d(TAG, "onLoginFailed");
        consoleLog("出错了( ˃ ˄ ˂̥̥ )", Color.RED);
        notEvaluatedCount.incrementAndGet();
        timer.cancel();
//        checkFinished();
    }

    @Override
    public void onLoginError(String errorMsg) {
        Log.d(TAG, "onLoginError errorMsg=" + errorMsg);
        consoleLog("出错了( ˃ ˄ ˂̥̥ ) " + errorMsg, Color.RED);
        notEvaluatedCount.incrementAndGet();
        timer.cancel();
//        checkFinished();
    }

    @Override
    public void onGetTimetable(JSONObject jsonObject) { }

    @Override
    public void onGetTimetableFinished() { }

    @Override
    public void onGetSemesters(String json) { }

    @Override
    public void onGetEvaluationSubjects(String json) {
//        consoleLog(json);
        Log.d(TAG, "onGetEvaluationSubjects json=" + json);
        consoleLog("获取评教教师和助教成功\n");
        try {
            JSONObject jsonObject = new JSONObject(json);
            int notFinishedNum = jsonObject.getInt("notFinishedNum");
            JSONArray jsonArray = jsonObject.getJSONArray("data");
            int totalNum = jsonArray.length();
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
                EvaluationInfo bean = new EvaluationInfo();
                bean.setEvaluatedPeople(evaluatedPeople);
                bean.setEvaluatedPeopleNum(evaluatedPeopleNum);
                bean.setEvaluationContent(evaluationContent);
                bean.setEvaluationContentNumber(evaluationContentNumber);
                bean.setQuestionnaireCoding(questionnaireCoding);
                bean.setQuestionnaireName(questionnaireName);

//                evaluationBeanList.add(bean);
                evaluationInfoQueue.add(bean);



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
        Log.d(TAG, "onEvaluationError errMsg=" + errMsg);
        consoleLog(errMsg, Color.RED);
        notEvaluatedCount.incrementAndGet();
//        checkFinished();
        newEvaluation();
    }

    @Override
    public void onGetTokenValue(String tokenValue) { }

    @Override
    public void onEvaluationFailed(String msg) {
        Log.d(TAG, "onEvaluationFailed msg=" + msg);
        consoleLog(msg, Color.RED);
        notEvaluatedCount.incrementAndGet();
//        checkFinished();
        newEvaluation();
    }

    @Override
    public void onEvaluationSuccess(String msg) {
        Log.d(TAG, "onEvaluationSuccess msg=" + msg);
        consoleLog(msg, Color.GREEN);
        evaluatedCount.incrementAndGet();
//        checkFinished();
        newEvaluation();
    }

    private void newEvaluation() {
        if (hasEvaluation()) {
            startEvaluation();
            post(scrollToBottomRunnable);
        }
    }

    private void startEvaluation() {
        if (hasEvaluation()) {
            EvaluationInfo bean = evaluationInfoQueue.remove();
            consoleLog(bean.getQuestionnaireName() + " " + bean.getEvaluatedPeople() + " " + bean.getEvaluationContent() + " 评教中...", Color.BLACK);
//            EvaluationUtil.with(this)
//                    .getEvaluationPage(bean.getEvaluatedPeople(), bean.getEvaluatedPeopleNum(), bean.getQuestionnaireCoding(),
//                            bean.getQuestionnaireName(), bean.getEvaluationContentNumber(), bean.getEvaluationContent());
            EvaluationUtil.with(this)
                    .getEvaluationPage(bean, event -> {
                        currentEvent = event;
                        if (event.getConnection() != null) {
                            countDownView = new TextView(_mActivity);
                            countDownView.setText(String.format(Locale.getDefault(), COUNT_DOWN_TEXT, 10));
                            consoleView.addView(countDownView);
                            timer.start();
                            post(scrollToBottomRunnable);
                        }
                    });
        }
    }

    private boolean hasEvaluation() {
        if (evaluationInfoQueue.isEmpty()) {
            consoleLog("一键评教完成！ 已评教" + evaluatedCount.get() + "人，评教失败" + notEvaluatedCount.get() + "人");
            evaluationButton.setText("评教完成");
            isEvaluating = false;
//            setSwipeable(true);
//            setCancelable(true);
//            setCanceledOnTouchOutside(true);
            setSwipeBackEnable(true);
            return false;
        }
        return true;
    }

    private void consoleLog(String msg) {
        TextView textView = new TextView(_mActivity);
        textView.setText(msg);
        consoleView.addView(textView);
        post(scrollToBottomRunnable);
    }

    private void consoleLog(String msg, int color) {
        TextView textView = new TextView(_mActivity);
        textView.setText(msg);
        textView.setTextColor(color);
        consoleView.addView(textView);
        post(scrollToBottomRunnable);
    }

//    @Subscribe(threadMode = ThreadMode.MAIN)
//    public void onEvaluationEvent(EvaluationEvent event) {
//        currentEvent = event;
//        if (event.getConnection() != null) {
//            countDownView = new TextView(_mActivity);
//            countDownView.setText(String.format(Locale.getDefault(), COUNT_DOWN_TEXT, 10));
//            consoleView.addView(countDownView);
//            timer.start();
//            EvaluationUtil.with(this).post(scrollToBottomRunnable);
//        }
//    }

}
