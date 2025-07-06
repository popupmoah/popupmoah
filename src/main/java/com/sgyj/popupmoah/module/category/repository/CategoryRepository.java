package com.sgyj.popupmoah.module.category.repository;

import com.sgyj.popupmoah.module.category.entity.Category;
import org.springframework.data.jpa.repository.JpaRepository;

public interface CategoryRepository extends JpaRepository<Category, Long> {
}
