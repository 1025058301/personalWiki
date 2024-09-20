package per.cy.personalwiki.mapper;

import java.util.List;
import org.apache.ibatis.annotations.Param;
import per.cy.personalwiki.pojo.Doc;
import per.cy.personalwiki.pojo.DocExample;

public interface DocMapper {
    long countByExample(DocExample example);

    int deleteByExample(DocExample example);

    int deleteByPrimaryKey(Long id);

    int insert(Doc record);

    int insertSelective(Doc record);

    List<Doc> selectByExample(DocExample example);

    Doc selectByPrimaryKey(Long id);

    int updateByExampleSelective(@Param("record") Doc record, @Param("example") DocExample example);

    int updateByExample(@Param("record") Doc record, @Param("example") DocExample example);

    int updateByPrimaryKeySelective(Doc record);

    int updateByPrimaryKey(Doc record);

    void increaseViewCount(@Param("id")long id);
}