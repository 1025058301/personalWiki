package per.cy.personalwiki.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import per.cy.personalwiki.mapper.TestMapper;
import per.cy.personalwiki.pojo.Test;

import javax.xml.ws.soap.Addressing;
import java.util.List;

@Service
public class TestService {
    @Autowired
    TestMapper testMapper;

    public List<Test> getList(){
        return testMapper.getList();
    }

}
