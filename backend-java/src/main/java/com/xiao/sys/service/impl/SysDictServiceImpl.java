package com.xiao.sys.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.xiao.sys.dto.PageResult;
import com.xiao.sys.entity.SysDictData;
import com.xiao.sys.entity.SysDictType;
import com.xiao.sys.mapper.SysDictDataMapper;
import com.xiao.sys.mapper.SysDictTypeMapper;
import com.xiao.sys.service.SysDictService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;

@Service
public class SysDictServiceImpl implements SysDictService {

    @Autowired
    private SysDictTypeMapper dictTypeMapper;

    @Autowired
    private SysDictDataMapper dictDataMapper;

    private static final DateTimeFormatter FORMATTER = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");

    @Override
    public PageResult<SysDictType> listDictTypes(String name, Integer page, Integer size) {
        QueryWrapper<SysDictType> wrapper = new QueryWrapper<>();
        if (name != null && !name.trim().isEmpty()) {
            wrapper.like("dict_name", name.trim()).or().like("dict_code", name.trim());
        }
        wrapper.orderByDesc("id");
        Page<SysDictType> pageParam = new Page<>(page, size);
        IPage<SysDictType> result = dictTypeMapper.selectPage(pageParam, wrapper);
        return PageResult.of(result.getRecords(), result.getTotal(), result.getCurrent(), result.getSize());
    }

    @Override
    public List<SysDictType> listAllDictTypes() {
        QueryWrapper<SysDictType> wrapper = new QueryWrapper<>();
        wrapper.eq("status", 1).orderByAsc("id");
        return dictTypeMapper.selectList(wrapper);
    }

    @Override
    public SysDictType getDictTypeById(Integer id) {
        return dictTypeMapper.selectById(id);
    }

    @Override
    public SysDictType createDictType(SysDictType dictType) {
        String now = LocalDateTime.now().format(FORMATTER);
        dictType.setCreatedAt(now);
        dictType.setUpdatedAt(now);
        if (dictType.getStatus() == null) dictType.setStatus(1);
        dictTypeMapper.insert(dictType);
        return dictType;
    }

    @Override
    public SysDictType updateDictType(Integer id, SysDictType dictType) {
        dictType.setId(id);
        dictType.setUpdatedAt(LocalDateTime.now().format(FORMATTER));
        dictTypeMapper.updateById(dictType);
        return dictTypeMapper.selectById(id);
    }

    @Override
    public boolean deleteDictType(Integer id) {
        dictDataMapper.delete(new QueryWrapper<SysDictData>().eq("dict_code",
                dictTypeMapper.selectById(id).getDictCode()));
        return dictTypeMapper.deleteById(id) > 0;
    }

    @Override
    public PageResult<SysDictData> listDictData(String dictCode, Integer page, Integer size) {
        QueryWrapper<SysDictData> wrapper = new QueryWrapper<>();
        if (dictCode != null && !dictCode.trim().isEmpty()) {
            wrapper.eq("dict_code", dictCode.trim());
        }
        wrapper.orderByAsc("sort_order", "id");
        Page<SysDictData> pageParam = new Page<>(page, size);
        IPage<SysDictData> result = dictDataMapper.selectPage(pageParam, wrapper);
        return PageResult.of(result.getRecords(), result.getTotal(), result.getCurrent(), result.getSize());
    }

    @Override
    public List<SysDictData> listDictDataByCode(String dictCode) {
        QueryWrapper<SysDictData> wrapper = new QueryWrapper<>();
        wrapper.eq("dict_code", dictCode).eq("status", 1).orderByAsc("sort_order", "id");
        return dictDataMapper.selectList(wrapper);
    }

    @Override
    public SysDictData getDictDataById(Integer id) {
        return dictDataMapper.selectById(id);
    }

    @Override
    public SysDictData createDictData(SysDictData dictData) {
        String now = LocalDateTime.now().format(FORMATTER);
        dictData.setCreatedAt(now);
        dictData.setUpdatedAt(now);
        if (dictData.getStatus() == null) dictData.setStatus(1);
        if (dictData.getSortOrder() == null) dictData.setSortOrder(0);
        dictDataMapper.insert(dictData);
        return dictData;
    }

    @Override
    public SysDictData updateDictData(Integer id, SysDictData dictData) {
        dictData.setId(id);
        dictData.setUpdatedAt(LocalDateTime.now().format(FORMATTER));
        dictDataMapper.updateById(dictData);
        return dictDataMapper.selectById(id);
    }

    @Override
    public boolean deleteDictData(Integer id) {
        return dictDataMapper.deleteById(id) > 0;
    }
}
