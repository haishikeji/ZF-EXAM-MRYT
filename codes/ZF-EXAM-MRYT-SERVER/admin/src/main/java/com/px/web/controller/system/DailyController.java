package com.px.web.controller.system;

import com.px.common.annotation.RequestLog;
import com.px.common.core.controller.BaseController;
import com.px.common.core.domain.entity.SysDept;
import com.px.common.core.domain.entity.SysUser;
import com.px.common.core.domain.model.LoginUser;
import com.px.common.core.redis.RedisCache;
import com.px.common.pojo.request.AnswerLogRequest;
import com.px.common.pojo.request.AnswerRequest;
import com.px.common.pojo.request.QuestionRequest;
import com.px.common.pojo.response.*;
import com.px.common.utils.DateUtils;
import com.px.common.utils.SecurityUtils;
import com.px.system.service.ISysDeptService;
import com.px.system.service.ISysQuestionService;
import com.px.system.service.ISysUserService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.util.CollectionUtils;
import org.springframework.web.bind.annotation.*;

import java.util.*;
import java.util.concurrent.TimeUnit;

@Api("每日一题考试系统")
@RestController
@Slf4j
public class DailyController extends BaseController {

    @Autowired
    private ISysDeptService deptService;
    @Autowired
    private ISysQuestionService questionService;

    @Autowired
    private RedisCache redisCache;

    @Autowired
    private ISysUserService userService;

    @ApiOperation("单位列表")
    @PostMapping("/deList")
    @RequestLog
    @ResponseBody
    public BaseResponse<List<DeptResponse>> deptList() {
        List<DeptResponse> deptResponseList = new ArrayList<>();
        List<SysDept> depts = deptService.selectDeptList();
        if (!CollectionUtils.isEmpty(depts)){
            for (SysDept sysDept:depts){
                DeptResponse deptResponse = DeptResponse.builder().build();
                deptResponse.setId(sysDept.getDeptId());
                deptResponse.setName(sysDept.getDeptName());
                deptResponseList.add(deptResponse);
            }
        }
        return BaseResponse.success(deptResponseList);


    }


    @ApiOperation("获取考试题目")
    @PostMapping("/getQuestion")
    @RequestLog
    @ResponseBody
    public BaseResponse<QuestionResponse> getQuestion(@RequestBody QuestionRequest request){
        LoginUser currUser = getLoginUser();
        if (null == currUser || null ==currUser.getUser()){
            return BaseResponse.error(202,"您还没有登录");
        }
        String phone = currUser.getUser().getPhonenumber();
        Integer logId = request.getId();
       return BaseResponse.success(questionService.getQuestion(phone,logId));
    }


    @ApiOperation("用户积分统计")
    @PostMapping("/getUserReport")
    @RequestLog
    @ResponseBody
    public BaseResponse<UserReportResponse> getUserReport(){
        LoginUser currUser = getLoginUser();
        if (null == currUser || null ==currUser.getUser()){
            return BaseResponse.error(202,"您还没有登录");
        }
        String phone = currUser.getUser().getPhonenumber();
        return BaseResponse.success(questionService.getUserReport(phone));
    }


    @ApiOperation("验证密码更新时间，超过三个月就自动更新密码")
    @PostMapping("/checkPwd")
    @RequestLog
    @ResponseBody
    public BaseResponse<UserReportResponse> checkPwd(){
        LoginUser currUser = getLoginUser();
        if (null == currUser || null ==currUser.getUser()){
            return BaseResponse.error(202,"您还没有登录");
        }
        String phone = currUser.getUser().getPhonenumber();

        Date now = new Date();
        Integer diffDay = DateUtils.differentDaysByMillisecond(currUser.getUser().getPwdUpdateTime(),now);
        if (diffDay>90){
            //生成随机密码
            String pwd = generatePassword(8);
            SysUser user = currUser.getUser();
            userService.checkUserAllowed(user);
            userService.checkUserDataScope(user.getUserId());
            user.setPassword(SecurityUtils.encryptPassword(pwd));
            user.setUpdateBy(getUsername());
            userService.resetPwd(user);
            return BaseResponse.success(pwd);
        }else{
            return BaseResponse.success("无需更新");
        }
    }

    public static String generatePassword(int length) {
        String result = null;
        result = getRandomPassword(length);
        if (result.matches(".*[a-z]{1,}.*") && result.matches(".*[A-Z]{1,}.*") && result.matches(".*[0-9]{1,}.*") && result.matches(".*[~!@#$%^&*\\.?]{1,}.*")) {
            return result;
        }
        return generatePassword(length);
    }

    public static String getRandomPassword(int length){
        char charr[] = "abcdefghijklmnopqrstuvwxyzABCDEFGHIJKLMNOPQRSTUVWXYZ1234567890~!@#$%^&*.?".toCharArray();
        char charrFirst[] = "abcdefghijklmnopqrstuvwxyzABCDEFGHIJKLMNOPQRSTUVWXYZ1234567890".toCharArray();
        StringBuilder sb = new StringBuilder();
        Random r = new Random();
        sb.append(charrFirst[r.nextInt(charrFirst.length)]);
        for (int x = 1; x < length; ++x) {
            sb.append(charr[r.nextInt(charr.length)]);
        }
        return sb.toString();
    }





    @ApiOperation("获取排行榜")
    @PostMapping("/rank")
    @RequestLog
    @ResponseBody
    public BaseResponse<ReportResponse> getReport(){
        LoginUser currUser = getLoginUser();
        if (null == currUser || null ==currUser.getUser()){
            return BaseResponse.error(202,"您还没有登录");
        }
        String rediskey = "DAILY_REPORT";
        ReportResponse result =  redisCache.getCacheObject(rediskey);
        if (result==null){
            result = questionService.getReport();
            redisCache.setCacheObject(rediskey,result,5, TimeUnit.MINUTES);
        }
        return BaseResponse.success(result);
    }

    @ApiOperation("获取答题记录")
    @PostMapping("/res")
    @RequestLog
    @ResponseBody
    public BaseResponse<List<AnswerResponse>> getAnswerLog(@RequestBody AnswerLogRequest request){
        if (StringUtils.isBlank(request.getMonth())){
            return BaseResponse.error(203,"月份不能为空");
        }
        LoginUser currUser = getLoginUser();
        if (null == currUser || null ==currUser.getUser()){
            return BaseResponse.error(202,"您还没有登录");
        }
        String phone = currUser.getUser().getPhonenumber();
        Date month = null;
        try {
             month = DateUtils.dateTime(DateUtils.YYYY_MM,request.getMonth());
        }catch (Exception e){
            log.error("日期格式错误：",e);
            return BaseResponse.error(202,"日期格式错误");
        }
        if (month == null){
            return BaseResponse.error(202,"日期格式错误");
        }
        return BaseResponse.success(questionService.getUserAnswerLog(phone,request.getMonth()));
    }



    @ApiOperation("答题")
    @PostMapping("/postAns")
    @RequestLog
    @ResponseBody
    public BaseResponse<AnsResponse> postAns(@RequestBody AnswerRequest request){
        if (null == request.getId()){
            return BaseResponse.error(203,"答题id不能为空");
        }
        LoginUser currUser = getLoginUser();
        if (null == currUser || null ==currUser.getUser()){
            return BaseResponse.error(202,"您还没有登录");
        }
        String rediskey = "DAILY_POST_ANS_"+currUser.getUserId();
        String result =  redisCache.getCacheObject(rediskey);
        if (StringUtils.isNotBlank(result)){
            return BaseResponse.error(202,"请勿频繁提交");
        }
        redisCache.setCacheObject(rediskey,request.getId().toString(),2, TimeUnit.MINUTES);
        String phone = currUser.getUser().getPhonenumber();
        String submitAnswer = "";
        List<String> optionList = request.getOptions();
        if (!CollectionUtils.isEmpty(optionList)){
            Collections.sort(optionList);
            for (String option:optionList){
                submitAnswer += option.trim()+";";
            }
            if (submitAnswer.length()>1){
                submitAnswer = submitAnswer.substring(0,submitAnswer.length()-1);
            }
        }
        BaseResponse<AnsResponse> response = questionService.submitAnswer(phone,request.getId(),submitAnswer,currUser.getUser().getUserId());
        redisCache.deleteObject(rediskey);
        return response;
    }




}
