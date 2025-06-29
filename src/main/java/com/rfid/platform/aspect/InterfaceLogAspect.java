package com.rfid.platform.aspect;

import com.alibaba.fastjson2.JSON;
import com.rfid.platform.annotation.InterfaceLog;
import com.rfid.platform.common.ExecNoContext;
import com.rfid.platform.entity.TagInterfaceLogBean;
import com.rfid.platform.service.TagInterfaceLogService;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.reflect.MethodSignature;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.web.multipart.MultipartFile;

import java.lang.reflect.Method;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

@Aspect
@Component
public class InterfaceLogAspect {
    
    @Autowired
    private TagInterfaceLogService tagInterfaceLogService;
    
    @Around("@annotation(com.rfid.platform.annotation.InterfaceLog)")
    public Object around(ProceedingJoinPoint joinPoint) throws Throwable {
        // 生成执行编号
        String execNo = UUID.randomUUID().toString().replace("-", "");
        
        // 将execNo设置到ThreadLocal中
        ExecNoContext.setExecNo(execNo);
        
        // 获取注解信息
        MethodSignature signature = (MethodSignature) joinPoint.getSignature();
        Method method = signature.getMethod();
        InterfaceLog interfaceLog = method.getAnnotation(InterfaceLog.class);
        
        // 创建日志对象
        TagInterfaceLogBean logBean = new TagInterfaceLogBean();
        logBean.setType(interfaceLog.type());
        logBean.setExecNo(execNo);
        
        // 记录请求参数
        Map<String, Object> reqParams = buildRequestParams(joinPoint, execNo);
        logBean.setReqContent(JSON.toJSONString(reqParams));
        
        // 保存接口日志
        tagInterfaceLogService.saveTagInterfaceLog(logBean);
        
        Object result = null;
        try {
            // 执行目标方法
            result = joinPoint.proceed();
            
            // 记录成功响应
            Map<String, Object> respData = new HashMap<>();
            respData.put("success", true);
            respData.put("result", result);
            respData.put("execNo", execNo);
            logBean.setRespContent(JSON.toJSONString(respData));
            
        } catch (Exception e) {
            // 记录异常响应
            Map<String, Object> respData = new HashMap<>();
            respData.put("success", false);
            respData.put("error", e.getMessage());
            respData.put("execNo", execNo);
            logBean.setRespContent(JSON.toJSONString(respData));
            
            throw e; // 重新抛出异常
        } finally {
            // 更新日志
            tagInterfaceLogService.updateTagInterfaceLogByPk(logBean);
            // 清除ThreadLocal
            ExecNoContext.clear();
        }
        
        return result;
    }
    
    /**
     * 构建请求参数
     */
    private Map<String, Object> buildRequestParams(ProceedingJoinPoint joinPoint, String execNo) {
        Map<String, Object> reqParams = new HashMap<>();

        // 获取方法参数
        Object[] args = joinPoint.getArgs();
        String[] paramNames = ((MethodSignature) joinPoint.getSignature()).getParameterNames();
        
        for (int i = 0; i < args.length; i++) {
            Object arg = args[i];
            String paramName = paramNames[i];
            
            if (arg instanceof MultipartFile) {
                MultipartFile file = (MultipartFile) arg;
                Map<String, Object> fileInfo = new HashMap<>();
                fileInfo.put("fileName", file.getOriginalFilename());
                fileInfo.put("fileSize", file.getSize());
//                fileInfo.put("contentType", file.getContentType());
                reqParams.put(paramName, fileInfo);
            } else {
                reqParams.put(paramName, arg);
            }
        }
        
        reqParams.put("execNo", execNo);

        return reqParams;
    }
}