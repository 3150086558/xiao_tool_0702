package com.xiao.sys.controller;

import com.xiao.sys.common.Result;
import com.xiao.sys.dto.PageResult;
import com.xiao.sys.entity.SysDictData;
import com.xiao.sys.entity.SysDictType;
import com.xiao.sys.service.SysDictService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/sys/dict")
public class SysDictController {

    @Autowired
    private SysDictService dictService;

    @GetMapping("/type/page")
    public Result<Map<String, Object>> listDictTypes(
            @RequestParam(defaultValue = "1") Integer page,
            @RequestParam(defaultValue = "10") Integer size,
            @RequestParam(required = false) String name) {
        PageResult<SysDictType> result = dictService.listDictTypes(name, page, size);
        return Result.success(Map.of("records", result.getRecords(), "total", result.getTotal()));
    }

    @GetMapping("/type/all")
    public Result<List<SysDictType>> listAllDictTypes() {
        return Result.success(dictService.listAllDictTypes());
    }

    @GetMapping("/type/{id}")
    public Result<SysDictType> getDictType(@PathVariable Integer id) {
        return Result.success(dictService.getDictTypeById(id));
    }

    @PostMapping("/type")
    public Result<SysDictType> createDictType(@RequestBody SysDictType dictType) {
        return Result.success(dictService.createDictType(dictType));
    }

    @PutMapping("/type/{id}")
    public Result<SysDictType> updateDictType(@PathVariable Integer id, @RequestBody SysDictType dictType) {
        return Result.success(dictService.updateDictType(id, dictType));
    }

    @DeleteMapping("/type/{id}")
    public Result<Map<String, Boolean>> deleteDictType(@PathVariable Integer id) {
        boolean deleted = dictService.deleteDictType(id);
        return Result.success(Map.of("ok", deleted));
    }

    @GetMapping("/data/page")
    public Result<Map<String, Object>> listDictData(
            @RequestParam(defaultValue = "1") Integer page,
            @RequestParam(defaultValue = "10") Integer size,
            @RequestParam(required = false) String dictCode) {
        PageResult<SysDictData> result = dictService.listDictData(dictCode, page, size);
        return Result.success(Map.of("records", result.getRecords(), "total", result.getTotal()));
    }

    @GetMapping("/data/code/{dictCode}")
    public Result<List<SysDictData>> listDictDataByCode(@PathVariable String dictCode) {
        return Result.success(dictService.listDictDataByCode(dictCode));
    }

    @GetMapping("/data/{id}")
    public Result<SysDictData> getDictData(@PathVariable Integer id) {
        return Result.success(dictService.getDictDataById(id));
    }

    @PostMapping("/data")
    public Result<SysDictData> createDictData(@RequestBody SysDictData dictData) {
        return Result.success(dictService.createDictData(dictData));
    }

    @PutMapping("/data/{id}")
    public Result<SysDictData> updateDictData(@PathVariable Integer id, @RequestBody SysDictData dictData) {
        return Result.success(dictService.updateDictData(id, dictData));
    }

    @DeleteMapping("/data/{id}")
    public Result<Map<String, Boolean>> deleteDictData(@PathVariable Integer id) {
        boolean deleted = dictService.deleteDictData(id);
        return Result.success(Map.of("ok", deleted));
    }
}
