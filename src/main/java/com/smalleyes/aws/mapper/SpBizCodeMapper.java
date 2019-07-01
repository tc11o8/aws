package com.smalleyes.aws.mapper;

import java.util.List;

import org.apache.ibatis.annotations.Mapper;
import com.smalleyes.aws.annotation.DB;
import com.smalleyes.aws.pojo.SpBizCode;

@Mapper
@DB(name = "aws")
public interface SpBizCodeMapper {

	List<SpBizCode> getList();
	
}