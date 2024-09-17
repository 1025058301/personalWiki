package per.cy.personalwiki.service;

import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.ObjectUtils;
import per.cy.personalwiki.mapper.CategoryMapper;
import per.cy.personalwiki.pojo.Category;
import per.cy.personalwiki.pojo.CategoryExample;
import per.cy.personalwiki.req.CategoryQueryRequest;
import per.cy.personalwiki.req.CategorySaveRequest;
import per.cy.personalwiki.resp.CategoryQueryResp;
import per.cy.personalwiki.resp.PageResp;
import per.cy.personalwiki.utils.CopyUtil;
import per.cy.personalwiki.utils.SnowFlake;

import java.util.List;

@Service
public class CategoryService {
    public static Logger logger= LoggerFactory.getLogger(CategoryService.class);
    @Autowired
    CategoryMapper categoryMapper;

    @Autowired
    SnowFlake snowFlake;
    public PageResp<CategoryQueryResp> selectByExample(CategoryQueryRequest categoryQueryRequest) {
        CategoryExample example = new CategoryExample();
        CategoryExample.Criteria criteria = example.createCriteria();
        PageHelper.startPage(categoryQueryRequest.getPage(), categoryQueryRequest.getSize());
        List<Category> categorys = categoryMapper.selectByExample(example);
        PageInfo<Category> pageInfo = new PageInfo<>(categorys);
        logger.info("总行数：{}", pageInfo.getTotal());
        logger.info("总页数：{}", pageInfo.getPages());
        List<CategoryQueryResp> resList=CopyUtil.copyList(categorys, CategoryQueryResp.class);
        PageResp<CategoryQueryResp> resp=new PageResp<>();
        resp.setList(resList);
        resp.setTotalsPages((int)pageInfo.getTotal());
        return resp;
    }
    public void saveCategory(CategorySaveRequest categorySaveRequest){
        Category category=CopyUtil.copyInstance(categorySaveRequest,Category.class);
        if(ObjectUtils.isEmpty(category.getId())){
            category.setId(snowFlake.nextId());
            categoryMapper.insert(category);
        }else {
            categoryMapper.updateByPrimaryKey(category);
        }
    }
    public void deleteCategory(long id){
        categoryMapper.deleteByPrimaryKey(id);
    }
}
