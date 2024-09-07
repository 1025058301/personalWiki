package per.cy.personalwiki.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import per.cy.personalwiki.mapper.DemoMapper;
import per.cy.personalwiki.pojo.Demo;
import per.cy.personalwiki.pojo.DemoExample;

import java.util.List;

@Service
public class DemoService {
    @Autowired
    DemoMapper demoMapper;

    public List<Demo> selectByExample(DemoExample demoExample){
        return demoMapper.selectByExample(demoExample);
    }

}
