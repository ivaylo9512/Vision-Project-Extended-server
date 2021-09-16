package com.vision.project.services;

import com.vision.project.models.Menu;
import com.vision.project.repositories.base.MenuRepository;
import com.vision.project.services.base.MenuService;
import org.springframework.stereotype.Service;

import javax.persistence.EntityNotFoundException;
import java.util.List;

@Service
public class MenuServiceImpl implements MenuService {
    private final MenuRepository menuRepository;

    public MenuServiceImpl(MenuRepository menuRepository) {
        this.menuRepository = menuRepository;
    }

    @Override
    public void delete(int id) {
       menuRepository.deleteById(id);
    }

//    @Override void update(MenuSpec menuSpec) {
//        menuRepository.findById(id).orElseThrow(() ->
//                new EntityNotFoundException("Menu not found."));
//
//        return menuRepository.save()
//    }

    @Override
    public Menu create(Menu menu) {
        return menuRepository.save(menu);
    }
}
