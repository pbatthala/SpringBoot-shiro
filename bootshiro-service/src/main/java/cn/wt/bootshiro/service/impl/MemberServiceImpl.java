package cn.wt.bootshiro.service.impl;

import java.util.HashMap;
import java.util.Map;
import java.util.Set;

import javax.annotation.Resource;

import org.springframework.stereotype.Service;

import cn.wt.bootshiro.dao.IActionDAO;
import cn.wt.bootshiro.dao.IMemberDAO;
import cn.wt.bootshiro.dao.IRoleDAO;
import cn.wt.bootshiro.service.IMemberService;
import cn.wt.bootshiro.vo.Member;
@Service 
public class MemberServiceImpl implements IMemberService {
	@Resource
	private IMemberDAO memberDAO ;
	@Resource
	private IRoleDAO roleDAO ;
	@Resource
	private IActionDAO actionDAO ;
	@Override
	public Member get(String mid) {
		return this.memberDAO.findById(mid);
	}

	@Override
	public Map<String, Set<String>> getRoleAndActionByMember(String mid) {
		Map<String,Set<String>> map = new HashMap<String,Set<String>>() ;
		map.put("allRoles", this.roleDAO.findAllRoleByMember(mid)) ;
		map.put("allActions", this.actionDAO.findAllActionByMember(mid)) ;
		return map;
	}

	

}
