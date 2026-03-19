package com.sky.controller.admin;

import com.sky.dto.DishDTO;

import com.sky.dto.DishPageQueryDTO;
import com.sky.entity.Dish;
import com.sky.result.PageResult;
import com.sky.result.Result;
import com.sky.service.DishService;
import com.sky.vo.DishVO;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.v3.oas.annotations.Operation;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Set;

/**
 * 功能：
 * 作者：李志豪
 * 日期：2026/3/15 19:30
 */
@RestController
@RequestMapping("/admin/dish")
@Api(tags = "菜品管理")
@Slf4j
public class DishController {
    @Autowired
    private DishService dishService;
    @Autowired
    private RedisTemplate redisTemplate;

    /**
     * 新增菜品
     * @param dishDTO
     * @return
     */
    @PostMapping
    @ApiOperation("新增菜品")
    public Result save(@RequestBody DishDTO dishDTO){
      log.info("新增菜品 {}", dishDTO);
      dishService.saveWithFlavor(dishDTO);

      // 清理所有菜品的缓存数据
      String key = "dish_" + dishDTO.getCategoryId();
        cleanCache(key);
      return Result.success();
    }

    /**
     * 菜品分页查询
     * @param dishPageQueryDTO
     * @return
     */
    @GetMapping("/page")
    @ApiOperation("菜品分页查询")
    public Result<PageResult> page(DishPageQueryDTO dishPageQueryDTO){
        log.info("菜品分页查询 {}", dishPageQueryDTO);
        PageResult pageResult = dishService.pageQuery(dishPageQueryDTO);
        return Result.success(pageResult);

    }

    /**
     * 菜品批量删除菜品
     * @param ids
     * @return
     */
    @DeleteMapping
    @ApiOperation("菜品批量删除菜品")
    public Result delete(@RequestParam List<Long> ids){
        log.info("删除菜品 {}", ids);
        dishService.deleteBatch(ids);

        // 清理所有菜品的缓存数据
        cleanCache("dish_*");

        return Result.success();
    }

    /**
     * 根据id查询菜品
     * @param id
     * @return
     */
    @GetMapping("/{id}")
    @ApiOperation("根据id查询菜品")
    public  Result<DishVO> getById(@PathVariable Long  id){
        log.info("根据id查询菜品 {}", id);
        DishVO dishVO = dishService.getByIdWithFlavor(id);
        return Result.success(dishVO);
    }
    /**
     * 修改菜品
     * @param dishDTO
     * @return
     */
    @PutMapping
    @ApiOperation("修改菜品")
    public Result update(@RequestBody DishDTO dishDTO){
        log.info("修改菜品 {}", dishDTO);
        dishService.updateWithFlavor(dishDTO);

        // 清理所有菜品的缓存数据
        cleanCache("dish_*");
        return Result.success();
    }

    /**
     * 根据分类id查询菜品
     * @param categoryId
     * @return
     */
    @GetMapping("/list")
    @ApiOperation("根据分类id查询菜品")
    public Result<List<Dish>> list(Long categoryId){
        List<Dish> list = dishService.list(categoryId);
        return Result.success(list);
    }

    @PostMapping("/status/{status}")
    @ApiOperation("启用/禁用菜品")
// 【核心修复】参数顺序不能变：先绑定路径里的status，再绑定URL里的id
// 【强制】必须加@RequestParam注解，否则参数绑定失败
    public Result startOrStop(@PathVariable Integer status, @RequestParam Long id){
        // 【验证修复】日志打印顺序也要对：先id，后status
        log.info("启用/禁用菜品：菜品id={}, 状态status={}", id, status);
        dishService.startOrStop(status, id);

        // 清理所有菜品的缓存数据
        cleanCache("dish_*");
        return Result.success();
    }
    /**
     * 清理缓存数据
     * @param pattern
     */
    private void cleanCache(String pattern){
        Set keys = redisTemplate.keys(pattern);
        redisTemplate.delete(keys);
    }

}