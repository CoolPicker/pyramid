package com.rosetta.image.mapper;

import com.rosetta.image.entity.Hash;
import org.apache.ibatis.annotations.Param;

import java.util.List;

public interface HashMapper {
    int deleteByPrimaryKey(Integer id);

    int insert(Hash record);

    int insertSelective(Hash record);

    Hash selectByPrimaryKey(Integer id);

    int updateByPrimaryKeySelective(Hash record);

    int updateByPrimaryKeyWithBLOBs(Hash record);

    int updateByPrimaryKey(Hash record);

    List<Hash> selectAll();

    List<Hash> selectBatch(@Param("list")List<Integer> list);

    Integer insertBatch(@Param("list")List<Hash> list);

    Integer updateBatch(@Param("list")List<Hash> list);
}