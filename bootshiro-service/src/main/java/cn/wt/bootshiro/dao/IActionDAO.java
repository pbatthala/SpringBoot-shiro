package cn.wt.bootshiro.dao;

import java.util.Set;

import org.apache.ibatis.annotations.Mapper;

@Mapper
public interface IActionDAO {
	public Set<String> findAllActionByMember(String mid);
}
