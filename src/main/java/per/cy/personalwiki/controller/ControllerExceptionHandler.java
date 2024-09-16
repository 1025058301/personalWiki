package per.cy.personalwiki.controller;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.validation.BindException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.ResponseStatus;
import per.cy.personalwiki.resp.CommonResp;

/**
 * 统一异常处理、数据预处理等
 */
@ControllerAdvice
public class ControllerExceptionHandler {

    private static final Logger LOG = LoggerFactory.getLogger(ControllerExceptionHandler.class);

    /**
     * 校验异常统一处理
     * @param e
     * @return
     */
    @ExceptionHandler(value = BindException.class)
    @ResponseBody
    public CommonResp validExceptionHandler(BindException e) {
        CommonResp commonResp = new CommonResp();
        LOG.warn("参数校验失败：{}", e.getBindingResult().getAllErrors().get(0).getDefaultMessage());
        commonResp.setSuccess(false);
        commonResp.setMessage(e.getBindingResult().getAllErrors().get(0).getDefaultMessage());
        return commonResp;
    }

    @ExceptionHandler(value = HttpMessageNotReadableException.class)
    @ResponseBody
    public CommonResp handleHttpMessageNotReadableException(HttpMessageNotReadableException ex) {
        // 自定义错误信息，可以根据异常对象 ex 提取详细信息
        CommonResp commonResp = new CommonResp();
        String errorMessage = "书目类别应该是数字！ " + ex.getMessage();
        commonResp.setSuccess(false);
        commonResp.setMessage(errorMessage);
        return commonResp;
    }
}
