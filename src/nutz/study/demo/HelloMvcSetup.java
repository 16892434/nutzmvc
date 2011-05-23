package nutz.study.demo;

import java.sql.Timestamp;

import nutz.study.demo.domain.Master;

import org.nutz.dao.Dao;
import org.nutz.dao.tools.Tables;
import org.nutz.ioc.Ioc;
import org.nutz.mvc.Setup;
import org.nutz.mvc.init.NutConfig;

public class HelloMvcSetup implements Setup {

	/**
	 * 当服务启动的时候，自动检查数据库，如果必要的数据表不存在，创建它们 并创建一个默认的 master 记录
	 */
	public void init(NutConfig config) {
		Ioc ioc = config.getIoc();
		Dao dao = ioc.get(Dao.class, "dao");
		if (!dao.exists("t_pet")) {
			// Create tables
			Tables.define(dao, Tables.loadFrom("tables.dod"));

			// Create master account
			Master m = new Master();
			m.setName("admin");
			m.setPassword("admin");
			m.setBirthday(new Timestamp(System.currentTimeMillis()));
			dao.insert(m);
		}
	}

	public void destroy(NutConfig config) {}

}
