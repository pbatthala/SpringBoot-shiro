package cn.wt.bootshiro.dao;

import org.apache.ibatis.annotations.Mapper;

import cn.wt.bootshiro.vo.Member;

@Mapper
public interface IMemberDAO {
	public Member findById(String mid) ;
}
