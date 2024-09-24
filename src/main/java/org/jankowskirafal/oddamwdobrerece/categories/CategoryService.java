package org.jankowskirafal.oddamwdobrerece.categories;

import java.util.List;

public interface CategoryService {

    List<Category> getAllCategories();

    Category addCategory(Category category);

    Category getCategoryById(Long id);
}
