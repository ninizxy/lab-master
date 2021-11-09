package com.example.service;

import com.example.entity.StudentList;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.example.mapper.StudentListMapper;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.ArrayList;
import java.util.List;

@Service
public class StudentListService extends ServiceImpl<StudentListMapper, StudentList> {

    @Resource
    private StudentListMapper studentListMapper;

    @Resource
    private StudentListService studentListService;

    public List<StudentList> getEveryClass(int classId) {
        List<StudentList> all = studentListService.list();
        List<StudentList> everyClassStudents = new ArrayList<>();
        for (StudentList stu : all) {
            if (stu.getClassId() == classId) {
                everyClassStudents.add(stu);
            }
        }
        return everyClassStudents;
    }
}



