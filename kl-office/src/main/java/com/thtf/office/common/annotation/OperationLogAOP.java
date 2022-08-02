package com.thtf.office.common.annotation;

import com.thtf.office.common.dto.adminserver.UserInfo;
import com.thtf.office.common.util.HttpUtil;
import com.thtf.office.feign.AdminAPI;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.Signature;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Pointcut;
import org.aspectj.lang.reflect.MethodSignature;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.transaction.annotation.Transactional;
import javax.servlet.http.HttpServletRequest;
import java.lang.reflect.Method;

/**
 * @Auther: liwencai
 * @Date: 2022/8/2 11:05
 * @Description:
 */
@Aspect
@Slf4j
public class OperationLogAOP {

    @Autowired
    private AdminAPI adminAPI;



    @Pointcut(value = "@annotation(com.thtf.office.common.annotation.OperationLog)")
    public void cutService(){
    }

    @Around("cutService()")
    @Transactional
    public Object recordSysLog(ProceedingJoinPoint point) throws Throwable {
        //先执行业务
        Object result = point.proceed();

        try {
            handle(point);
        }catch (Exception e){
            log.info("日志记录出错");
        }
        return result;
    }

    private void handle(ProceedingJoinPoint point) throws Exception {
        //获取拦截的方法名
        Signature sig = point.getSignature();

        // 先验证是否是方法
        MethodSignature msig = null;
        if (!(sig instanceof MethodSignature)) {
            throw new IllegalArgumentException("该注解只能用于方法");
        }

        msig = (MethodSignature) sig;
        Object target = point.getTarget();
        Method currentMethod = target.getClass().getMethod(msig.getName(), msig.getParameterTypes());


        // 方法名
        String methodName = currentMethod.getName();
        log.info("访问的方法：{}",methodName);

        // 获取访问类名
        String className = point.getTarget().getClass().getName();
        log.info("访问的类名：{}",className);

        // 获取拦截方法的参数
        Object[] params = point.getArgs();
        StringBuilder sb = new StringBuilder();
        for (Object param : params) {
            sb.append(param);
            sb.append(" & ");
        }
        log.info("访问访问参数：{}",sb);

        /* 获取注解参数名称 */
        OperationLog annotation = currentMethod.getAnnotation(OperationLog.class);
        // 事件操作记录
        RequestWay requestWay = annotation.requestWay();
        String requestWayName = requestWay.getDesc();
        log.info("事件操作记录：{}",requestWayName);
        // 访问控制事件
        String event = annotation.event();
        log.info("访问控制事件：{}",event);
        // 操作页面
        String visitPage = annotation.visitPage();
        log.info("操作页面：{}",visitPage);

        /* 获取IP */
        HttpServletRequest request = HttpUtil.getRequest();
        String remoteAddr = request.getRemoteAddr();
        log.info("请 求IP："+remoteAddr);

        /* 获取用户名 */
//        log.info("用户的真实姓名：{}", HttpUtil.getUserInfo().getRealname());

    }

}
