package com.example.controller;

import cn.hutool.core.collection.CollUtil;
import cn.hutool.core.io.FileUtil;
import cn.hutool.core.io.IoUtil;
import cn.hutool.poi.excel.ExcelUtil;
import cn.hutool.poi.excel.ExcelWriter;
import com.example.common.Result;
import com.example.entity.StudentList;
import com.example.service.StudentListService;
import com.example.entity.User;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import org.springframework.web.bind.annotation.*;
import com.example.exception.CustomException;
import cn.hutool.core.util.StrUtil;

import javax.annotation.Resource;
import javax.servlet.ServletOutputStream;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.net.URLEncoder;
import java.util.*;


@RestController
@RequestMapping("/api/studentList")
public class StudentListController {
    @Resource
    private StudentListService studentListService;
    @Resource
    private HttpServletRequest request;

    public User getUser() {
        User user = (User) request.getSession().getAttribute("user");
        if (user == null) {
            throw new CustomException("-1", "请登录");
        }
        return user;
    }

    @PostMapping
    public Result<?> save(@RequestBody StudentList studentList) {
        return Result.success(studentListService.save(studentList));
    }

    @PutMapping
    public Result<?> update(@RequestBody StudentList studentList) {
        return Result.success(studentListService.updateById(studentList));
    }

    @DeleteMapping("/{id}")
    public Result<?> delete(@PathVariable Long id) {
        studentListService.removeById(id);
        return Result.success();
    }

    @GetMapping("/{id}")
    public Result<?> findById(@PathVariable Long id) {
        return Result.success(studentListService.getById(id));
    }

    @GetMapping
    public Result<?> findAll() {
        return Result.success(studentListService.list());
    }

//    @GetMapping("/{id}")
//    public Result<?> findByClass(@PathVariable int id) {
//        return  Result.success(studentListService.getEveryClass(id));
//    }

    @GetMapping("/page")
    public Result<?> findPage(@RequestParam(required = false, defaultValue = "") String name,
                                                @RequestParam(required = false, defaultValue = "1") Integer pageNum,
                                                @RequestParam(required = false, defaultValue = "10") Integer pageSize) {
        LambdaQueryWrapper<StudentList> query = Wrappers.<StudentList>lambdaQuery().orderByDesc(StudentList::getId);
        if (StrUtil.isNotBlank(name)) {
            query.like(StudentList::getStudentName, name);
        }
        return Result.success(studentListService.page(new Page<>(pageNum, pageSize), query));
    }

    @GetMapping("/export")
    public void export(HttpServletResponse response) throws IOException {

        List<Map<String, Object>> list = CollUtil.newArrayList();
        List<StudentList> all = studentListService.list();
        for (StudentList obj : all) {
            Map<String, Object> row = new LinkedHashMap<>();
            row.put("班级班号", obj.getClassId());
            row.put("学生学号", obj.getStudentId());
            row.put("学生姓名", obj.getStudentName());
            list.add(row);
        }
        // 2. 写excel
        ExcelWriter writer = ExcelUtil.getWriter(true);
        writer.write(list, true);
        response.setContentType("application/vnd.openmosix-officiated.spreadsheet.sheet;charset=utf-8");
        String fileName = URLEncoder.encode("学生名单信息", "UTF-8");
        response.setHeader("Content-Disposition", "attachment;filename=" + fileName + ".xlsx");
        String[] headers={"班级班号","学生学号","学生姓名"};
        ServletOutputStream out = response.getOutputStream();
        writer.flush(out, true);
        writer.close();
        IoUtil.close(System.out);

    }



    @GetMapping("/upload/{fileId}")
    public Result<?> upload(@PathVariable String fileId) {
        String basePath = System.getProperty("user.dir") + "/src/main/resources/static/file/";
        List<String> fileNames = FileUtil.listFileNames(basePath);
        String filename = fileNames.stream().filter(name -> name.contains(fileId)).findAny().orElse("");
        List<List<Object>> lists = ExcelUtil.getReader(basePath + filename).read(1);
        List<StudentList> saveList = new ArrayList<>();
        for (List<Object> row : lists) {
            StudentList obj = new StudentList();
            obj.setClassId(Integer.valueOf(((String) row.get(1))));
            obj.setStudentId(Integer.valueOf((String) row.get(2)));
            obj.setStudentName((String) row.get(3));
            saveList.add(obj);
        }
        studentListService.saveBatch(saveList);
        return Result.success();
    }
}
