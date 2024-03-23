package com.itheima.reggie.controller;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.itheima.reggie.common.R;
import com.itheima.reggie.entity.Category;
import com.itheima.reggie.service.impl.CategoryService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

@Slf4j
@RestController
@RequestMapping("/category")
public class CategoryController {
    @Autowired
    private CategoryService categoryService;

    @PostMapping
    public R<String>save(@RequestBody Category category){
        categoryService.save(category);
        return R.success("新增分类成功");
    }


    @GetMapping("/page")
    public R<Page> page(int page,int pageSize){
        log.info("分类的page:{},pageSize:{}",page,pageSize);
        //构造分页构造器
        Page pageInfo = new Page<>(page,pageSize);
        //构造条件构造器
        LambdaQueryWrapper<Category> queryWrapper = new LambdaQueryWrapper<>();
        //添加排序条件
        queryWrapper.orderByDesc(Category::getSort);
        //执行查询
        categoryService.page(pageInfo,queryWrapper);
        return  R.success(pageInfo);
    }

    //删除某个分类
    @DeleteMapping
    public R<String> delete(Long ids){
        log.info("该删除分类的id是：{}",ids);
        log.info("test github");
        categoryService.remove(ids);
        return R.success("删除分类成功");
    }
}
