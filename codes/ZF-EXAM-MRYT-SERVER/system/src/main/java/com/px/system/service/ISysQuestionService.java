package com.px.system.service;

import com.px.common.core.domain.AjaxResult;
import com.px.common.core.domain.entity.Question;
import com.px.common.core.domain.entity.SysAnswer;
import com.px.common.core.domain.entity.SysReport;
import com.px.common.pojo.dmo.SysQuestionExecl;
import com.px.common.pojo.response.*;
import com.px.system.domain.SysUserTestLog;

import java.util.List;

public interface ISysQuestionService {

    SysUserTestLog getUserQid(String phone);

    QuestionResponse getQuestion(String phone,Integer id);

    UserReportResponse getUserReport(String phone);

    ReportResponse getReport();

    List<AnswerResponse> getUserAnswerLog(String phone, String month);

    BaseResponse<AnsResponse> submitAnswer(String phone, Integer qid, String submitAnswer,Long userId);


    List<Question> selectQuestionList(Question question);

    void updateUserPoint(Long userId,Integer point,Integer type);

    Question queryQuestionByID(Integer id);

    List<String> queryTagList();

    AjaxResult batchAddQuestion(List<SysQuestionExecl> questionExeclList);

    AjaxResult addQuestion(Question question);

    AjaxResult updateQuestion(Question user);

    AjaxResult delQuestion(Integer id);

    AjaxResult updateUserAnswerLog(SysAnswer ans);

    AjaxResult deleteUserAnswerLog(SysAnswer ans);

    List<SysReport> getReportList(SysReport ans);


}
