/**
 * @(#)MiguMusicLogServiceImpl.java 2018年5月9日
 */
package com.smalleyes.aws.service.impl;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.smalleyes.aws.common.IBatchExecutor;
import com.smalleyes.aws.mapper.UploadResultMapper;
import com.smalleyes.aws.pojo.UploadResult;


@Service("uploadResultService")
public class UploadResultServiceImpl implements IBatchExecutor<UploadResult> {

	public static final Logger LOGGER = LoggerFactory.getLogger(UploadResultServiceImpl.class);

	@Autowired
	private UploadResultMapper uploadResultMapper;

	@Override
	public void execute(List<UploadResult> records) {
		Map<String, Object> dataMap = new HashMap<String, Object>();
		dataMap.put("currentDate", LocalDate.now().format(DateTimeFormatter.BASIC_ISO_DATE));
		dataMap.put("addList", records);
		uploadResultMapper.insert(dataMap);
	}
	
}
