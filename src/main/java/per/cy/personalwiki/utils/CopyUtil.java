package per.cy.personalwiki.utils;

import org.springframework.beans.BeanUtils;

import java.util.ArrayList;
import java.util.List;

public class CopyUtil {
    public static <T> T copyInstance(Object source,Class<T> clazz){
        if(source==null)return null;
        try {
            T reObject=clazz.newInstance();
            BeanUtils.copyProperties(source,reObject);
            return reObject;
        } catch (InstantiationException | IllegalAccessException e) {
            throw new RuntimeException(e);
        }
    }
    public static <T> List<T> copyList(List source,Class<T> clazz){
        if(source==null) return null;
        List<T> list=new ArrayList<>();
        for(Object object:source){
            list.add(copyInstance(object,clazz));
        }
        return list;
    }
}
