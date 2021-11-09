package com.example.service;

import com.example.entity.TeachClass;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.example.mapper.TeachClassMapper;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;

@Service
public class TeachClassService extends ServiceImpl<TeachClassMapper, TeachClass> {

    @Resource
    private TeachClassMapper teachClassMapper;

}
