package per.cy.personalwiki.mapper;

import java.util.List;
import org.apache.ibatis.annotations.Param;
import per.cy.personalwiki.pojo.EbookSnapshot;
import per.cy.personalwiki.pojo.EbookSnapshotExample;

public interface EbookSnapshotMapper {
    long countByExample(EbookSnapshotExample example);

    int deleteByExample(EbookSnapshotExample example);

    int deleteByPrimaryKey(Long id);

    int insert(EbookSnapshot record);

    int insertSelective(EbookSnapshot record);

    List<EbookSnapshot> selectByExample(EbookSnapshotExample example);

    EbookSnapshot selectByPrimaryKey(Long id);

    int updateByExampleSelective(@Param("record") EbookSnapshot record, @Param("example") EbookSnapshotExample example);

    int updateByExample(@Param("record") EbookSnapshot record, @Param("example") EbookSnapshotExample example);

    int updateByPrimaryKeySelective(EbookSnapshot record);

    int updateByPrimaryKey(EbookSnapshot record);

    void genSnapshot();
}