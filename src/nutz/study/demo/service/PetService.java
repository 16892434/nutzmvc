package nutz.study.demo.service;

import java.io.File;
import java.io.IOException;

import nutz.study.demo.domain.Pet;

import org.nutz.dao.Dao;
import org.nutz.ioc.loader.annotation.IocBean;
import org.nutz.lang.Files;
import org.nutz.service.IdNameEntityService;

@IocBean(field={"dao"})
public class PetService extends IdNameEntityService<Pet> {

	public PetService() {
		super();
	}
	
	public PetService(Dao dao) {
		super(dao);
	}
	
	public void uploadPhoto(int id, File tempFile, String root) throws IOException {
		Pet pet = this.fetch(id);
		String ext = Files.getSuffixName(tempFile);
		pet.setPhotoPath("/photo/" + id + "." + ext);
		File photo = new File(root + pet.getPhotoPath());
		if(photo.exists()) {
			Files.deleteDir(photo);
		}
		Files.move(tempFile, photo);
		dao().update(pet);
	}
}
