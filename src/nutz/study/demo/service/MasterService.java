package nutz.study.demo.service;

import nutz.study.demo.domain.Master;

import org.nutz.dao.Dao;
import org.nutz.ioc.loader.annotation.IocBean;
import org.nutz.service.IdNameEntityService;

@IocBean(field={"dao"})
public class MasterService extends IdNameEntityService<Master> {

	public MasterService() {
		super();
	}
	
	public MasterService(Dao dao) {
		super(dao);
	}
}
