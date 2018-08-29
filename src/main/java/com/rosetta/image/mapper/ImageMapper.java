package com.rosetta.image.mapper;

import com.rosetta.image.entity.Image;

public interface ImageMapper {
    int deleteByPrimaryKey(Integer id);

    int insert(Image record);

    int insertSelective(Image record);

    Image selectByPrimaryKey(Integer id);

    int updateByPrimaryKeySelective(Image record);

    int updateByPrimaryKeyWithBLOBs(Image record);

    int updateByPrimaryKey(Image record);

    Integer selectCountAll();


}