package cn.wt.bootshiro.service.impl;
import java.util.List;
import javax.annotation.Resource;
import org.springframework.stereotype.Service;

import cn.wt.bootshiro.dao.IDeptDAO;
import cn.wt.bootshiro.service.IDeptService;
import cn.wt.bootshiro.vo.Dept;
@Service
public class DeptServiceImpl implements IDeptService {
	@Resource
	private IDeptDAO deptDAO ;
	@Override
	public List<Dept> list() {
		return this.deptDAO.findAll(); 
	}

}
