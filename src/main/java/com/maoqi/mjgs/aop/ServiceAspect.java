package com.maoqi.mjgs.aop;

import com.alibaba.fastjson.JSON;
import com.maoqi.mjgs.dao.impl.TaleDaoImpl;
import com.maoqi.mjgs.pojo.dbbean.VisitLog;
import com.maoqi.mjgs.pojo.vo.TaleVO;
import com.maoqi.mjgs.util.IpUtil;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.Signature;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.reflect.MethodSignature;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.InputStreamSource;
import org.springframework.stereotype.Component;
import org.springframework.web.multipart.MultipartFile;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Component
@Aspect
public class ServiceAspect {
    private final HttpServletRequest request;
    private final TaleDaoImpl taleDao;

    public ServiceAspect(HttpServletRequest request, TaleDaoImpl taleDao) {
        this.request = request;
        this.taleDao = taleDao;
    }

    @Around("execution(* com.maoqi.mjgs.controller.TaleController.getTaleById(..))")
    public Object serviceBefore(ProceedingJoinPoint pjp) throws Throwable {
        Map<String, Object> logMap1 = new HashMap<>();
        Signature signature = pjp.getSignature();
        MethodSignature methodSignature = (MethodSignature) signature;
        //2.最关键的一步:通过这获取到方法的所有参数名称的字符串数组
        String[] parameterNames = methodSignature.getParameterNames();
        int i = 0;
        for (Object arg : pjp.getArgs()) {
            String argValue="";
            if(!(arg instanceof HttpServletRequest ||arg instanceof HttpServletResponse
                    ||arg instanceof InputStreamSource ||arg instanceof MultipartFile)){
                try {
                    if(arg instanceof List){
                        List b=(List) arg;
                        if(!b.isEmpty()){
                            if(b.get(0) instanceof MultipartFile){
                                continue;
                            }
                        }
                    }
                    argValue= JSON.toJSONString(arg);
                }catch (Exception e){
                    e.printStackTrace();
                }
            }
            logMap1.put(parameterNames[i++], argValue);
        }

        TaleVO taleVO = (TaleVO)pjp.proceed();

        VisitLog visitLog = new VisitLog();
        visitLog.setMethod(signature.getName());
        visitLog.setRequest(logMap1.toString());
        visitLog.setResponse(taleVO.getTitle());
        visitLog.setIp(IpUtil.getIpAddr(request));
        visitLog.setUseragent(IpUtil.getUserAgent(request));
        taleDao.saveVisitLog(visitLog);

        return taleVO;
    }
}
