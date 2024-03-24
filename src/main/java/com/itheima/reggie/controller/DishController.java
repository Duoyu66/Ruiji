package com.itheima.reggie.controller;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.itheima.reggie.common.R;
import com.itheima.reggie.dto.DishDto;
import com.itheima.reggie.entity.Category;
import com.itheima.reggie.entity.Dish;
import com.itheima.reggie.entity.DishFlavor;
import com.itheima.reggie.entity.Employee;
import com.itheima.reggie.service.impl.CategoryService;
import com.itheima.reggie.service.impl.DishFlavorService;
import com.itheima.reggie.service.impl.DishService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.stream.Collectors;

//菜品管理
@Slf4j
@RestController
@RequestMapping("/dish")
public class DishController {
    @Autowired
    private DishService dishService;
    @Autowired
    private DishFlavorService dishFlavorService;
    @Autowired
    private CategoryService categoryService;

    //菜品展示
    @GetMapping("/page")
    public R<Page> page(int page, int pageSize, String name) {
        log.info("菜品获取到的size：{}，pageSize是：{}", page, pageSize);
        //构造分页构造器
        Page<Dish> pageInfo = new Page<>(page, pageSize);
        Page<DishDto> dishDtoPage = new Page<>();
        //构造条件构造器
        LambdaQueryWrapper<Dish> queryWrapper = new LambdaQueryWrapper<>();
        //添加过滤条件
        queryWrapper.like(name != null, Dish::getName, name);
        //添加排序条件
        queryWrapper.orderByDesc(Dish::getUpdateTime);
        dishService.page(pageInfo, queryWrapper);
        //对象拷贝
        BeanUtils.copyProperties(pageInfo, dishDtoPage, "records");
        List<Dish> records = pageInfo.getRecords();
        List<DishDto> list = records.stream().map((item) -> {

            DishDto dishDto = new DishDto();
            //把item属性拷贝到dishDto中
            BeanUtils.copyProperties(item, dishDto);
            //获得categoryid
            Long categoryId = item.getCategoryId();
            //通过categoryId获取category对象
            Category category = categoryService.getById(categoryId);
            if (category != null) {
                //从category对象中获取categoryName
                String categoryName = category.getName();
                //将categoryName放进dishDto中
                dishDto.setCategoryName(categoryName);
            }

            return dishDto;
        }).collect(Collectors.toList());

        dishDtoPage.setRecords(list);
        return R.success(dishDtoPage);
    }

    //新增菜品
    @PostMapping
    public R<String> addDish(@RequestBody DishDto dishDto) {
//        dishService.save(DishDto);
        dishService.saveWithFlavor(dishDto);
        return R.success("添加菜品成功了哦~");
    }
    @GetMapping("/{id}")
    public R<DishDto> showMessage(@PathVariable  Long id){
        log.info("回显的数据是:{}",id);
      DishDto dishDto =   dishService.getByIdWithFlavor(id);
        return R.success(dishDto);
    }

    //更新菜品
    @PutMapping
    public R<String> update(@RequestBody DishDto dishDto) {
//        dishService.save(DishDto);
        dishService.updateWithFlavor(dishDto);
        return R.success("更新菜品成功了哦~");
    }
}
