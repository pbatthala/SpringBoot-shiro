package cn.wt.bootshiro.dao;
import java.util.List;

import org.apache.ibatis.annotations.Mapper;

import cn.wt.bootshiro.vo.Dept;
@Mapper
public interface IDeptDAO {
	public List<Dept> findAll() ;	
}
