package cn.wt.bootshiro.service;

import java.util.Map;
import java.util.Set;

import cn.wt.bootshiro.vo.Member;

public interface IMemberService {
	public Member get(String mid) ;
	public Map<String,Set<String>> getRoleAndActionByMember(String mid) ;
}
