package com.smalleyes.aws.service;

import com.smalleyes.aws.pojo.SpUser;

/**
 * @author ：<a href="mailto:284040429@qq.com" >张明俊</a>
 * @create 19-5-16 下午5:01
 * @description: 具体收码业务对接第三方接口
 */
public interface IBizCodeAction {

	/**
	 * 检查该用户是否可做
	 * @param spImsiMobile
	 * @return  true可做任务    false不能做任务
	 */
	boolean isCanDo(SpUser spUser);
	
	/**
	 * 判断该用户是否使用过
	 * @param bizId
	 * @param imsi
	 * @return  true使用过     false没有
	 */
	boolean isUserUsed(String imsi);
	
	/**
	 * 提交手机号到第三方平台进行注册
	 * @param bizId  收码业务id  自定义
	 * @param spImsiMobile  
	 * @return
	 */
	boolean submitMobile(SpUser spUser);
	
	/**
	 * 记录该业务用户已做
	 * @param imsi
	 * @param mobile
	 */
	void saveUser(String imsi,String mobile);
	
	/**
	 * 获取上传短信地址
	 * @return
	 */
	String getUploadUrl();
	
	/**
	 * 提交短信验证码给第三方
	 * @param bizCodeContext
	 * @return
	 */
	SubmitResult submitMessageCode(SpUser spUser);
	
	
	class SubmitResult{
		
		/*
		 * true提交成功   false提交失败  
		 */
		Boolean result;
		
		//提交到第三方接口，返回的字符串
		String returnString;
		
		public SubmitResult(Boolean result){
			this(result,null);
		}
		
		public SubmitResult(Boolean result,String returnString){
			this.result = result;
			this.returnString = returnString;
		}

		public Boolean getResult() {
			return result;
		}

		public void setResult(Boolean result) {
			this.result = result;
		}

		public String getReturnString() {
			return returnString;
		}

		public void setReturnString(String returnString) {
			this.returnString = returnString;
		}
		
	}
}
