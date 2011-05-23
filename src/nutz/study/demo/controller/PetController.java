package nutz.study.demo.controller;

import java.io.File;
import java.io.IOException;
import java.util.List;

import javax.servlet.ServletContext;
import javax.servlet.http.HttpSession;

import nutz.study.demo.domain.Master;
import nutz.study.demo.domain.Pet;
import nutz.study.demo.service.MasterService;
import nutz.study.demo.service.PetService;

import org.nutz.dao.Cnd;
import org.nutz.dao.FieldFilter;
import org.nutz.ioc.annotation.InjectName;
import org.nutz.ioc.loader.annotation.Inject;
import org.nutz.ioc.loader.annotation.IocBean;
import org.nutz.mvc.annotation.AdaptBy;
import org.nutz.mvc.annotation.At;
import org.nutz.mvc.annotation.By;
import org.nutz.mvc.annotation.Fail;
import org.nutz.mvc.annotation.Filters;
import org.nutz.mvc.annotation.Ok;
import org.nutz.mvc.annotation.Param;
import org.nutz.mvc.filter.CheckSession;
import org.nutz.mvc.upload.UploadAdaptor;
import org.nutz.trans.Atom;

@IocBean
@InjectName
@At("/pet")
@Fail("json")
@Filters({@By(type=CheckSession.class, args={"master", "index.jsp"})})
public class PetController {

	@Inject
	private PetService petService;
	
	@Inject
	private MasterService masterService;
	
	@At
	@Filters
	@Ok("redirect:/pet/all")
	@Fail("redirect:/wrong_master.jsp")
	public void login(@Param("name") String name, @Param("pwd") String password, HttpSession session) {
		Master m = masterService.fetch(Cnd.where("name", "=", name).and("password", "=", password));
		if(null == m)
			throw new RuntimeException("Error username or password");
		session.setAttribute("master", m);
	}
	
	@At
	@Ok("redirect:/index.jsp")
	public void logout(HttpSession session) {
		session.removeAttribute("master");
	}
	
	@At
	@Ok("redirect:/pet/all")
	public Pet add(@Param("nm") String name, HttpSession session) {
		Master m = (Master)session.getAttribute("master");
		Pet pet = new Pet();
		pet.setName(name);
		pet.setMasterId(m.getId());
		return petService.dao().insert(pet);
	}
	
	@At
	@Ok("redirect:/pet/all")
	public List<Pet> remove(@Param("id") int id, HttpSession session) {
		petService.delete(id);
		return all(session);
	}
	
	@At
	@Ok("redirect:/pet/detail?id=${id}")
	public int update(@Param("..") final Pet pet) {
		FieldFilter.create(Pet.class, null, "photoPath", true).run(new Atom() {
			public void run() {
				petService.dao().update(pet);
			}
		});
		return pet.getId();
	}
	
	@At
	@Ok("jsp:jsp.pet.list")
	public List<Pet> all(HttpSession session) {
		Master m = (Master)session.getAttribute("master");
		return petService.query(Cnd.where("masterId", "=", m.getId()).asc("name"), null);
	}
	
	@At
	@Ok("jsp:jsp.pet.detail")
	public Pet detail(@Param("id") int id) {
		return petService.fetch(id);
	}
	
	@At
	@AdaptBy(type=UploadAdaptor.class, args={"~/nutz/demo/hellomvc/petm/tmp", "8192", "UTF-8", "10" })
	@Ok("jsp:jsp.upload.done")
	@Fail("jsp:jsp.upload.fail")
	public void uploadPhoto(@Param("id") int id, @Param("photo") File f, ServletContext context) throws IOException {
		petService.uploadPhoto(id, f, context.getRealPath("/"));
	}
	
	@At
	@Ok("json")
	public Pet[] insertTwo(@Param("::A.") Pet a, @Param("::B.") Pet b, HttpSession session) {
		Master m = (Master) session.getAttribute("master");
		a.setMasterId(m.getId());
		b.setMasterId(m.getId());
		Pet[] list = new Pet[2];
		list[0] = a;
		list[1] = b;
		petService.dao().insert(list);
		return list;
	}
	
	@At
	@Ok("json")
	public Pet[] getPets(@Param("id") int[] ids) {
		Pet[] list = new Pet[ids.length];
		for(int i = 0; i < ids.length; i++) {
			list[i] = petService.fetch(ids[i]);
		}
		return list;
	}
}
