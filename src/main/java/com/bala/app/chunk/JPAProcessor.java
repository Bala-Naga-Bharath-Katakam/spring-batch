package com.bala.app.chunk;

import com.bala.app.entity.postgres.Student;
import lombok.extern.slf4j.Slf4j;
import org.springframework.batch.item.ItemProcessor;
import org.springframework.stereotype.Component;
import org.springframework.stereotype.Service;

@Slf4j
@Component
public class JPAProcessor implements ItemProcessor<Student, com.bala.app.entity.sql.Student> {
    @Override
    public com.bala.app.entity.sql.Student process(Student item) throws Exception {

        log.info("Processing id : {} ",item.getId());

        com.bala.app.entity.sql.Student student=new com.bala.app.entity.sql.Student();
        student.setDeptId(item.getDeptId());
        student.setEmail(item.getEmail());
        student.setFirstName(item.getFirstName());
        student.setId(item.getId());
        student.setIsActive(Boolean.valueOf(item.getIsActive()));
        student.setLastName(item.getLastName());

        return student;
    }
}
