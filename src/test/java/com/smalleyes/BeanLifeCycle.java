package com.smalleyes;

import java.util.HashMap;

import org.apache.commons.io.IOUtils;
import org.apache.http.HttpResponse;
import org.apache.http.client.config.RequestConfig;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.springframework.context.ApplicationContext;
import org.springframework.context.support.ClassPathXmlApplicationContext;

import com.alibaba.fastjson.JSONObject;
import com.smalleyes.aws.util.HttpClientUtils;

public class BeanLifeCycle {

    public static void main(String[] args) {
    	
        System.out.println("现在开始初始化容器");
        
        ApplicationContext factory = new ClassPathXmlApplicationContext("beans.xml");
        System.out.println("容器初始化成功1");    
        //得到Preson，并使用
        Person person = factory.getBean("person",Person.class);
        System.out.println("容器初始化成功2");    
        Dog dog = factory.getBean("dog",Dog.class);
        System.out.println("容器初始化成功3");    
        System.out.println(person);
        
        System.out.println("现在开始关闭容器！");
        ((ClassPathXmlApplicationContext)factory).registerShutdownHook();
    }
}