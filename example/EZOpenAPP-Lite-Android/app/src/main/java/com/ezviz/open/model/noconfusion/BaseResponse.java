package com.ezviz.open.model.noconfusion;

import com.ezviz.open.common.EZOpenConstant;

/**
 * Description:
 * Created by dingwei3
 *
 * @date : 2017/1/12
 */
public class BaseResponse implements DonotConfusion{
    public String code;
    public String msg;


    public boolean parseCode(){
        int coderesp = Integer.parseInt(code);
        if (coderesp == EZOpenConstant.HTTP_RESUILT_OK) {
            return true;
        }
        return false;
    }


    
}


