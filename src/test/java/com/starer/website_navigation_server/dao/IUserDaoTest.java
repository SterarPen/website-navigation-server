package com.starer.website_navigation_server.dao;

import com.starer.website_navigation_server.pojo.Role;
import com.starer.website_navigation_server.pojo.User;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.util.Arrays;


@SpringBootTest
public class IUserDaoTest {

    @Autowired
    private IUserDao IUserDao;

    @Test
    public void test() {
        Role vip = new Role(
                "111111111110",
                "VIP",
                Byte.valueOf("4")
        );
//        int insert = userDao.insert(new User(
//                "222222222226",
//                "用户D",
//                "123",
//                vip,
//                "13251412141"
//        ));
//        System.out.println("THE RESULT FOR INSERTING A NEW USER TO DATABASE: " + insert);

        User[] users = IUserDao.selectAll();
        System.out.println(Arrays.toString(users));

        User user = IUserDao.selectById("222222222222");
        System.out.println("THE RESULT OF QUERYING A USER BY ID: " + user);

        User user1 = new User();
//        user1.setRole(vip);
        user1.setExpire('0');
        User[] select = IUserDao.select(user1);
        System.out.println(Arrays.toString(select));

        User user2 = new User("222222222222");
        user2.setEmail("5645615@139.com");
        int update = IUserDao.update(user2);
        System.out.println("THE RESULT FOR UPDATING USER: " + update);

    }
}
