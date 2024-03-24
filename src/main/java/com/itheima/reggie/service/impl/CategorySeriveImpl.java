package com.itheima.reggie.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.itheima.reggie.common.CustomException;
import com.itheima.reggie.entity.Category;
import com.itheima.reggie.entity.Dish;
import com.itheima.reggie.entity.Setmeal;
import com.itheima.reggie.mapper.CategoryMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.bind.annotation.PutMapping;

@Service
public class CategorySeriveImpl extends ServiceImpl<CategoryMapper, Category> implements CategoryService {
    @Autowired
    private  DishService dishService;
    @Autowired
    private SetmealService setmealService;

    //根据id删除分类
    @Override
    public void remove(Long category_id) {
        LambdaQueryWrapper<Dish> dishLambdaQueryWrapper = new LambdaQueryWrapper<Dish>();
        //添加查询条件，根据分类id进行查询
        dishLambdaQueryWrapper.eq(Dish::getCategoryId,category_id);
        int count = dishService.count(dishLambdaQueryWrapper);

        //查询当前分类是否关联了菜品，如果已经关联，直接抛出业务异常
        if (count>0){//关联了,需要抛异常
            throw new CustomException("当前分类下关联了菜品不能删除~");
        }
        //查询当前分类是否关联了套餐，如果已经关联，直接抛出业务异常
        LambdaQueryWrapper<Setmeal> setmealLambdaQueryWrapper = new LambdaQueryWrapper<Setmeal>();
        setmealLambdaQueryWrapper.eq(Setmeal::getCategoryId,category_id);
        int count2= setmealService.count(setmealLambdaQueryWrapper);
        if (count2>0){//已经关联，直接抛出业务异常
            throw new CustomException("当前分类下关联了套餐不能删除~aggga");
        }
        //正常删除
        super.removeById(category_id);
    }

}
