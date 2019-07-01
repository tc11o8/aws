package com.smalleyes.aws.mapper;

import java.util.Map;

import org.apache.ibatis.annotations.Mapper;
import com.smalleyes.aws.annotation.DB;

@Mapper
@DB(name="awsLog")
public interface UploadResultMapper {

    int insert(Map<String,Object> dataMap);
    
}