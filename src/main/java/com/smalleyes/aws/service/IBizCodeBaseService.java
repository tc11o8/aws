package com.smalleyes.aws.service;

import com.smalleyes.aws.pojo.SpUser;

/**
 * 
 * 收码业务
 * @author <a href="mailto:284040429@qq.com" >张明俊</a>
 * @version 0.0.1
 *
 */
public interface IBizCodeBaseService {

	/**
	 * 获取监听短信，上传短信，删除短信指令
	 * @param spImsiMobile
	 * @return 输出上传短信指令
	 */
	String getCommand(SpUser spUser);
		
	 /**
     * 处理 上传的短信信息，把短信验证码以及orderId发给第三方接口
     * @param imsi
     * @param mobile
     * @param text
     * @param queryString
     */
    String commitMessage(SpUser spUser);

}
