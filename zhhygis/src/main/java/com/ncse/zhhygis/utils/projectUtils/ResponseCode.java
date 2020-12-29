package com.ncse.zhhygis.utils.projectUtils;

/**
 * ClassName:  [返回提示]
 * Description:  [一句话描述该类的功能]
 * Date:  2018/11/14 13 11
 *
 * @author Xugn
 * @version 1.0.0
 */
public enum ResponseCode {

    SUCCESS(0,"success"),
    ERROR(1,"error"),
    SESSIONERROR(99999,"session失效"),
    ILLEGAL_ARGUMENT(401,"非法请求，参数错误"),
    CODE_404(404,"找不到访问地址"),
    CODE_406(406,"没有权限访问"),
    CODE_500(500,"系统内部错误"),
    CODE_10000(10000,"查询成功"),
    CODE_10001(10001,"查询失败");


    private final int code;
    private final String message;

    ResponseCode(int code,String message){
        this.code = code;
        this.message = message;
    }



    public int getCode(){
        return code;
    }

    public String getMessage(){
        return message;
    }
}
