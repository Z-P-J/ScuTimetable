package com.scu.timetable.utils;

import android.os.Handler;
import android.os.Message;
import android.util.Log;

import com.scu.timetable.events.EvaluationEvent;
import com.scu.timetable.model.EvaluationInfo;
import com.scu.timetable.utils.content.SPHelper;
import com.zpj.http.ZHttp;
import com.zpj.http.core.Connection;
import com.zpj.http.core.IHttp;
import com.zpj.http.core.ObservableTask;
import com.zpj.http.parser.html.nodes.Document;
import com.zpj.http.parser.html.nodes.Element;
import com.zpj.http.parser.html.select.Elements;

import org.greenrobot.eventbus.EventBus;
import org.json.JSONObject;

import java.io.IOException;
import java.lang.ref.WeakReference;
import java.util.HashMap;
import java.util.Map;

import io.reactivex.ObservableEmitter;

/**
 * @author Z-P-J
 * @date 2019/5/31 13:53
 */
public final class EvaluationUtil {

    private static final String[] TEACHER_SUBJECTIVE_EVALUATIONS = {
            "老师上课时备课充分，语言流畅，思路清晰，课堂上有许多生动的案例分析，课堂互动时间也很多。",
            "教师的教学效果极佳，可以使同学在领略知识魅力的同时提高自己实际技能。教师教课内容广大博深，高质量，高效率。教课内容新颖，独特，有个性。教师授课表现出来的激情和精神可以深深吸引并打动学生，希望我们的老师可以继续创新，造出更多的精品课。",
            "教师教学在书面浅显知识的基础上，进一步扩大了教学的知识的深度及广度，扩大了学生知识面，并且多方面培养学生的思考问题的能力，教师的知识渊博，因此讲授的很有深度，并且在书本知识上也有所扩展。课上教师很注意与学生的互动环节，增强了课堂气氛，使教学效果更加显著。",
            "教师课堂上的整体教学效果非常好，教师在教学方面极认真负责，教师的基本知识技能过硬，因此，课上所达到的效果是很好的，指导具有针对性，使同学更容易获得提高。",
            "教师上课认真负责，专业基础技能高深，非常注重学生的实际动手能力。",
            "课程设置合理，深浅知宜，实际操作多，教学效果好，且授课内容新颖，独到，有自己的特色，能很好的启发、带动学生的思维。立意新，大大地启发了学生的创造性思维。",
            "老师是很好的，平时课堂上讲课风趣又不失严谨，课下也对同学们的问题有求必应，帮助了我很多。",
            "课堂氛围轻松活跃，积极调动了学生的兴趣。注重与学生的互动环节，增强了课堂气氛，使教学效果更加显著。",
            "老师挺不错的，对问题分析的透彻，讲课能切中要害，很喜欢老师的讲课风格。",
            "老师讲的内容紧追时代步伐，不过时，讲课风格详实生动，大家都很喜欢。",
            "老师的讲课节奏安排的不错，最后大家对知识掌握的都比较好，复习也比较充分，考试情况不错。",
            "该课程能以旧引新，寻找新旧知识的关联和生长点，注重知识的发生发展过程，能找到教材特点及本课的疑点，并恰当处理，在课堂上设疑问难，引导点拨，是一门很有个性特点的课",
            "该课程很有艺术，教学安排清晰有序，科学规范。在教材处理上从具体到抽象，化难为易，以简驾繁突破难点。各环节有详细的练习，科学合理有效地培养我们自主，探究，创新能力的发展。",
            "课上教师很注意与学生的互动环节，增强了课堂气氛，使教学效果更加显著。"
    };

    private static final String[] ASSISTANT_SUBJECTIVE_EVALUATIONS = {
            "助教老师认真负责，对同学们的问题有求必应，对我们帮助很大",
            "助教老师工作态度端正，管理严格，给与我们很多帮助",
            "助教上课认真负责，专业基础技能高深，对同学们的问题有求必应",
            "助教在教学实践过程中,能够刻苦钻研,虚心请教,积极施行新的教育理念,探索新的教学方法,在教学实践过程中认真钻研",
            "助教老师认真负责，讲解到位",
            "助教老师批改作业认真负责，为人和善，对我们也很好",
            "助教批改作业认真负责，对同学们的问题有求必应",
            "助教老师工作认真负责，讲解也很到位",
            "助教积极施行新的教育理念,探索新的教学方法,在教学实践过程中认真钻研"
    };

    private EvaluationCallback callback;

    private static EvaluationUtil evaluationUtil;

    public interface EvaluationCallback {
        void onGetEvaluationSubjects(String json);
        void onEvaluationError(String errMsg);
        void onGetTokenValue(String tokenValue);
        void onEvaluationFailed(String msg);
        void onEvaluationSuccess(String msg);
//        void onGetEvaluationPage(EvaluationInfo bean);
    }


    public void onGetEvaluationSubjects(String json) {
        if (callback != null) {
            callback.onGetEvaluationSubjects(json);
        }
    }

    public void onEvaluationError(String errMsg) {
        callback.onEvaluationError(errMsg);
    }

    public void onGetTokenValue(String tokenValue) {
        if (callback != null) {
            callback.onGetTokenValue(tokenValue);
        }
    }

    public void onEvaluationFailed(String msg) {
        if (callback != null) {
            callback.onEvaluationFailed(msg);
        }
    }

    public void onEvaluationSuccess(String msg) {
        if (callback != null) {
            callback.onEvaluationSuccess(msg);
        }
    }


    private EvaluationUtil(EvaluationCallback callback) {
        this.callback = callback;
    }

    public synchronized static EvaluationUtil with(EvaluationCallback callback) {
        if (evaluationUtil == null) {
            evaluationUtil = new EvaluationUtil(callback);
        }
        return evaluationUtil;
    }

    public void getEvaluationSubjects() {
        ZHttp.get("http://zhjw.scu.edu.cn/student/teachingEvaluation/teachingEvaluation/search")
                .onRedirect(redirectUrl -> false)
                .header("Cookie", SPHelper.getString("cookie", ""))
                .header("Referer", "http://zhjw.scu.edu.cn/student/teachingEvaluation/evaluation/index")
                .userAgent(TimetableHelper.UA)
                .ignoreHttpErrors(true)
                .ignoreContentType(true)
                .toStr()
                .onSuccess(this::onGetEvaluationSubjects)
                .onError(throwable -> onEvaluationError(throwable.getMessage()))
                .subscribe();
//        ExecutorHelper.submit(() -> {
//            try {
//                String doc = ZHttp.get("http://zhjw.scu.edu.cn/student/teachingEvaluation/teachingEvaluation/search")
//                        .onRedirect(redirectUrl -> false)
//                        .header("Cookie", SPHelper.getString("cookie", ""))
//                        .header("Referer", "http://zhjw.scu.edu.cn/student/teachingEvaluation/evaluation/index")
//                        .userAgent(TimetableHelper.UA)
//                        .ignoreHttpErrors(true)
//                        .ignoreContentType(true)
//                        .toStr();
//                sendMessage(1, doc);
//                Log.d("getEvaluationSubjects", "body=" + doc);
//            } catch (IOException e) {
//                e.printStackTrace();
//                sendMessage(-1, e.getMessage());
//            }
//        });
    }

//    public void getEvaluationPage(final String evaluatedPeople, final String evaluatedPeopleNumber,
//                                  final String questionnaireCode, final String questionnaireName,
//                                  final String evaluationContentNumber, final String evaluationContent) {
//        ExecutorHelper.submit(() -> {
//            try {
//                Document doc = Jsoup.connect("http://zhjw.scu.edu.cn/student/teachingEvaluation/teachingEvaluation/evaluationPage")
//                        .followRedirects(true)
//                        .header("cookie", SPHelper.getString("cookie", ""))
//                        .header("Referer", "http://zhjw.scu.edu.cn/student/teachingEvaluation/evaluation/index")
//                        .userAgent(TimetableHelper.UA)
//                        .data("evaluatedPeople", evaluatedPeople)
//                        .data("evaluatedPeopleNumber", evaluatedPeopleNumber)
//                        .data("questionnaireCode", questionnaireCode)
//                        .data("questionnaireName", questionnaireName)
//                        .data("evaluationContentNumber", evaluationContentNumber)
//                        .data("evaluationContentContent", "")
//                        .post();
//                Element element = doc.getElementById("tokenValue");
//                final String tokenValue = element.val();
//                Log.d("tokenValue", "tokenValue=" + tokenValue);
//                final Element table = doc.getElementsByClass("table-box").get(0);
//                Log.d("table", "table=" + table);
//                Elements inputs = table.select("input.ace");
//                Log.d("inputs", "inputs=" + inputs.toString());
//
//                Thread.sleep(10000);
//
//                if (!tokenValue.isEmpty()) {
//                    Connection connection = Jsoup.connect("http://zhjw.scu.edu.cn/student/teachingEvaluation/teachingEvaluation/evaluation")
//                            .method(Connection.Method.POST)
//                            .followRedirects(false)
//                            .header("cookie", SPHelper.getString("cookie", ""))
//                            .header("Referer", "http://zhjw.scu.edu.cn/student/teachingEvaluation/teachingEvaluation/evaluationPage")
//                            .data("tokenValue", tokenValue)
//                            .data("questionnaireCode", questionnaireCode)
//                            .data("evaluationContentNumber", evaluationContentNumber)
//                            .data("evaluatedPeopleNumber", evaluatedPeopleNumber)
//                            .data("count", "0")
//                            .data("zgpj", getRandomEvaluation(questionnaireName))
//                            .ignoreHttpErrors(true)
//                            .ignoreContentType(true);
//                    Map<String, String> map = new HashMap<>();
//
//                    int randomNum1 = (int) (Math.random() * inputs.size()) / 5;
//                    int randomNum2 = (int) (Math.random() * inputs.size()) / 5;
//                    int count = 0;
//                    for (Element input : inputs) {
//                        String name = input.attr("name");
//                        Log.d("name", name);
//                        if (map.containsKey(name)) {
//                            continue;
//                        }
////                        if (count == randomNum1 || count == randomNum2) {
////                            connection.data(name, "10_0.8");
////                        } else {
////                            connection.data(name, "10_1");
////                        }
//                        connection.data(name, "10_1");
//                        map.put(name, "");
//                        count++;
//                    }
//                    Log.d("map", "map.size=" + map.size());
//
//                    Connection.Response response = connection.execute();
//                    Log.d("getEvaluationPage", "result1=" + response.body());
//                    JSONObject jsonObject = new JSONObject(response.body());
//                    String result = jsonObject.getString("result");
//                    Log.d("getEvaluationPage", "resultMsg=" + result);
//                    if ("error".equals(result)) {
////                        connection.data("tokenValue", jsonObject.getString("token"));
////                        response = connection.execute();
//                        sendMessage(3, questionnaireName + " " + evaluatedPeople + " " + evaluationContent + " 评教失败！");
//                        return;
//                    } else if ("/logout".equals(result)) {
//                        // 重新登录
////                        response = Jsoup.connect("http://zhjw.scu.edu.cn/logout")
////                                .followRedirects(true)
////                                .userAgent(TimetableHelper.UA)
////                                .ignoreContentType(true)
////                                .execute();
//                        Log.d("getEvaluationPage", "/logout");
//                        sendMessage(3, questionnaireName + " " + evaluatedPeople + " " + evaluationContent + " 评教失败！请重新登录");
//                        return;
//                    } else {
////                        Log.d("getEvaluationPage", "result2=" + response.body());
//                        sendMessage(4, questionnaireName + " " + evaluatedPeople + " " + evaluationContent + " 评教成功！");
//                        return;
//                    }
//                }
//                sendMessage(3, questionnaireName + " " + evaluatedPeople + " " + evaluationContent + " 评教失败！");
//            } catch (Exception e) {
//                e.printStackTrace();
//                sendMessage(-1, questionnaireName + " " + evaluatedPeople + " " + evaluationContent + " 评教出错了！ 错误信息：" + e.getMessage());
//            }
//        });
//    }

    public void getEvaluationPage(EvaluationInfo bean, IHttp.OnSuccessListener<EvaluationEvent> onSuccessListener) {

        ZHttp.post("http://zhjw.scu.edu.cn/student/teachingEvaluation/teachingEvaluation/evaluationPage")
                .method(Connection.Method.POST)
                .onRedirect(redirectUrl -> true)
                .header("cookie", SPHelper.getString("cookie", ""))
                .header("Referer", "http://zhjw.scu.edu.cn/student/teachingEvaluation/evaluation/index")
                .userAgent(TimetableHelper.UA)
                .data("evaluatedPeople", bean.getEvaluatedPeople())
                .data("evaluatedPeopleNumber", bean.getEvaluatedPeopleNum())
                .data("questionnaireCode", bean.getQuestionnaireCoding())
                .data("questionnaireName", bean.getQuestionnaireName())
                .data("evaluationContentNumber", bean.getEvaluationContentNumber())
                .data("evaluationContentContent", "")
                .toHtml()
                .flatMap((ObservableTask.OnFlatMapListener<Document, EvaluationEvent>) (doc, emitter) -> {
                    Log.d("getEvaluationPage", "body=" + doc.body());
                    Element element = doc.getElementById("tokenValue");
                    final String tokenValue = element.val();
                    Log.d("tokenValue", "tokenValue=" + tokenValue);
                    final Element table = doc.getElementsByClass("table-box").get(0);
                    Log.d("table", "table=" + table);
                    Elements inputs = table.select("input.ace");
                    Log.d("inputs", "inputs=" + inputs.toString());

                    if (!tokenValue.isEmpty()) {
                        Connection connection = ZHttp.get("http://zhjw.scu.edu.cn/student/teachingEvaluation/teachingEvaluation/evaluation")
                                .method(Connection.Method.POST)
                                .onRedirect(redirectUrl -> false)
                                .cookie(SPHelper.getString("cookie", ""))
                                .referer("http://zhjw.scu.edu.cn/student/teachingEvaluation/teachingEvaluation/evaluationPage")
                                .data("tokenValue", tokenValue)
                                .data("evaluatedPeopleNumber", bean.getEvaluatedPeopleNum())
                                .data("questionnaireCode", bean.getQuestionnaireCoding())
                                .data("evaluationContentNumber", bean.getEvaluationContentNumber())
                                .data("count", "0")
                                .data("zgpj", getRandomEvaluation(bean.getQuestionnaireName()))
                                .ignoreHttpErrors(true)
                                .ignoreContentType(true);
                        Map<String, String> map = new HashMap<>();

                        int randomNum1 = (int) (Math.random() * inputs.size()) / 5;
                        int randomNum2 = (int) (Math.random() * inputs.size()) / 5;
                        int count = 0;
                        for (Element input : inputs) {
                            String name = input.attr("name");
                            Log.d("name", name);
                            if (map.containsKey(name)) {
                                continue;
                            }
                            connection.data(name, "10_1");
                            map.put(name, "");
                            count++;
                        }
                        Log.d("map", "map.size=" + map.size());
//                            EventBus.getDefault().post(
//                                    EvaluationEvent.create()
//                                            .setConnection(connection)
//                                            .setEvaluationBean(bean)
//                            );
                        emitter.onNext(EvaluationEvent.create()
                                .setConnection(connection)
                                .setEvaluationBean(bean));
                    }
                })
                .onSuccess(onSuccessListener)
                .onError(e -> onEvaluationError(bean.getQuestionnaireName() + " "
                        + bean.getEvaluatedPeople() + " " + bean.getEvaluationContent()
                        + " 评教出错了！ 错误信息：" + e.getMessage()))
                .subscribe();

//        ExecutorHelper.submit(() -> {
//            try {
//                Document doc = ZHttp.post("http://zhjw.scu.edu.cn/student/teachingEvaluation/teachingEvaluation/evaluationPage")
//                        .method(Connection.Method.POST)
//                        .onRedirect(redirectUrl -> true)
//                        .header("cookie", SPHelper.getString("cookie", ""))
//                        .header("Referer", "http://zhjw.scu.edu.cn/student/teachingEvaluation/evaluation/index")
//                        .userAgent(TimetableHelper.UA)
//                        .data("evaluatedPeople", bean.getEvaluatedPeople())
//                        .data("evaluatedPeopleNumber", bean.getEvaluatedPeopleNum())
//                        .data("questionnaireCode", bean.getQuestionnaireCoding())
//                        .data("questionnaireName", bean.getQuestionnaireName())
//                        .data("evaluationContentNumber", bean.getEvaluationContentNumber())
//                        .data("evaluationContentContent", "")
//                        .toHtml();
//                Log.d("getEvaluationPage", "body=" + doc.body());
////                Document doc = Jsoup.parse(response.body());
////                Document doc = Jsoup.connect("http://zhjw.scu.edu.cn/student/teachingEvaluation/teachingEvaluation/evaluationPage")
////                        .followRedirects(true)
////                        .header("cookie", SPHelper.getString("cookie", ""))
////                        .header("Referer", "http://zhjw.scu.edu.cn/student/teachingEvaluation/evaluation/index")
////                        .userAgent(TimetableHelper.UA)
////                        .data("evaluatedPeople", bean.getEvaluatedPeople())
////                        .data("evaluatedPeopleNumber", bean.getEvaluatedPeopleNum())
////                        .data("questionnaireCode", bean.getQuestionnaireCoding())
////                        .data("questionnaireName", bean.getQuestionnaireName())
////                        .data("evaluationContentNumber", bean.getEvaluationContentNumber())
////                        .data("evaluationContentContent", "")
////                        .post();
//                Element element = doc.getElementById("tokenValue");
//                final String tokenValue = element.val();
//                Log.d("tokenValue", "tokenValue=" + tokenValue);
//                final Element table = doc.getElementsByClass("table-box").get(0);
//                Log.d("table", "table=" + table);
//                Elements inputs = table.select("input.ace");
//                Log.d("inputs", "inputs=" + inputs.toString());
//
//                if (!tokenValue.isEmpty()) {
//                    Connection connection = ZHttp.get("http://zhjw.scu.edu.cn/student/teachingEvaluation/teachingEvaluation/evaluation")
//                            .method(Connection.Method.POST)
//                            .onRedirect(redirectUrl -> false)
//                            .header("cookie", SPHelper.getString("cookie", ""))
//                            .header("Referer", "http://zhjw.scu.edu.cn/student/teachingEvaluation/teachingEvaluation/evaluationPage")
//                            .data("tokenValue", tokenValue)
////                            .data("questionnaireCode", questionnaireCode)
////                            .data("evaluationContentNumber", evaluationContentNumber)
////                            .data("evaluatedPeopleNumber", evaluatedPeopleNumber)
//                            .data("evaluatedPeopleNumber", bean.getEvaluatedPeopleNum())
//                            .data("questionnaireCode", bean.getQuestionnaireCoding())
//                            .data("evaluationContentNumber", bean.getEvaluationContentNumber())
//                            .data("count", "0")
//                            .data("zgpj", getRandomEvaluation(bean.getQuestionnaireName()))
//                            .ignoreHttpErrors(true)
//                            .ignoreContentType(true);
//                    Map<String, String> map = new HashMap<>();
//
//                    int randomNum1 = (int) (Math.random() * inputs.size()) / 5;
//                    int randomNum2 = (int) (Math.random() * inputs.size()) / 5;
//                    int count = 0;
//                    for (Element input : inputs) {
//                        String name = input.attr("name");
//                        Log.d("name", name);
//                        if (map.containsKey(name)) {
//                            continue;
//                        }
////                        if (count == randomNum1 || count == randomNum2) {
////                            connection.data(name, "10_0.8");
////                        } else {
////                            connection.data(name, "10_1");
////                        }
//                        connection.data(name, "10_1");
//                        map.put(name, "");
//                        count++;
//                    }
//                    Log.d("map", "map.size=" + map.size());
//                    EventBus.getDefault().post(
//                            EvaluationEvent.create()
//                                    .setConnection(connection)
//                                    .setEvaluationBean(bean)
//                    );
//
////                    Connection.Response response = connection.execute();
////                    Log.d("getEvaluationPage", "result1=" + response.body());
////                    JSONObject jsonObject = new JSONObject(response.body());
////                    String result = jsonObject.getString("result");
////                    Log.d("getEvaluationPage", "resultMsg=" + result);
////                    if ("error".equals(result)) {
//////                        connection.data("tokenValue", jsonObject.getString("token"));
//////                        response = connection.execute();
////                        sendMessage(3, questionnaireName + " " + evaluatedPeople + " " + evaluationContent + " 评教失败！");
////                        return;
////                    } else if ("/logout".equals(result)) {
////                        // 重新登录
//////                        response = Jsoup.connect("http://zhjw.scu.edu.cn/logout")
//////                                .followRedirects(true)
//////                                .userAgent(TimetableHelper.UA)
//////                                .ignoreContentType(true)
//////                                .execute();
////                        Log.d("getEvaluationPage", "/logout");
////                        sendMessage(3, questionnaireName + " " + evaluatedPeople + " " + evaluationContent + " 评教失败！请重新登录");
////                        return;
////                    } else {
//////                        Log.d("getEvaluationPage", "result2=" + response.body());
////                        sendMessage(4, questionnaireName + " " + evaluatedPeople + " " + evaluationContent + " 评教成功！");
////                        return;
////                    }
//                }
////                sendMessage(3, questionnaireName + " " + evaluatedPeople + " " + evaluationContent + " 评教失败！");
//            } catch (Exception e) {
//                e.printStackTrace();
//                sendMessage(-1, bean.getQuestionnaireName() + " " + bean.getEvaluatedPeople() + " " + bean.getEvaluationContent() + " 评教出错了！ 错误信息：" + e.getMessage());
//            }
//        });
    }

    public void evaluation(EvaluationInfo bean, Connection connection) {
        connection.toJsonObject()
                .onSuccess(jsonObject -> {
                    Log.d("getEvaluationPage", "result1=" + jsonObject.toString());
                    String result = jsonObject.getString("result");
                    Log.d("getEvaluationPage", "resultMsg=" + result);
                    if ("error".equals(result)) {
                        onEvaluationFailed(bean.getQuestionnaireName() + " " + bean.getEvaluatedPeople() + " " + bean.getEvaluationContent() + " 评教失败！");
                    } else if ("/logout".equals(result)) {
                        Log.d("getEvaluationPage", "/logout");
                        onEvaluationFailed(bean.getQuestionnaireName() + " " + bean.getEvaluatedPeople() + " " + bean.getEvaluationContent() + " 评教失败！请重新登录");
                    } else {
                        onEvaluationSuccess(bean.getQuestionnaireName() + " " + bean.getEvaluatedPeople() + " " + bean.getEvaluationContent() + " 评教成功！");
                    }
                })
                .subscribe();
    }

    private String getRandomEvaluation(String questionnaireName) {
        boolean isAssistantEvaluation = "研究生助教评价".equals(questionnaireName);
        int size = isAssistantEvaluation ? ASSISTANT_SUBJECTIVE_EVALUATIONS.length : TEACHER_SUBJECTIVE_EVALUATIONS.length;
        int random = (int) (Math.random() * size);
        return isAssistantEvaluation ? ASSISTANT_SUBJECTIVE_EVALUATIONS[random] : TEACHER_SUBJECTIVE_EVALUATIONS[random];
    }

}
