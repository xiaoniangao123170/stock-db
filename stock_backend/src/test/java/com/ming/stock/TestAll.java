//package com.ming.stock;
//
//import org.junit.jupiter.api.Test;
//import org.springframework.beans.factory.annotation.Autowired;
//import org.springframework.boot.test.context.SpringBootTest;
//import org.springframework.security.crypto.password.PasswordEncoder;
//
///**
// * @Author: Ming
// * @Description TODO
// */
//@SpringBootTest
//public class TestAll {
//    @Autowired
//    private PasswordEncoder passwordEncoder;
//    /**
//     * 测试密码加密
//     */
//    @Test
//    public void testPwd(){
//        String pwd = "123456";
//        //加密$2a$10$hMvLeQ9RDDwzknmyCDwIJuXN4qzU3p6NeOEd0JRSaHGHkzE4R8pBa
//        String encode = passwordEncoder.encode(pwd);
//        System.out.println(encode);
//        /*
//        * matches()匹配明文密码和加密后的密码是否匹配 如果匹配返回true 否则false
//        * */
//        boolean flag = passwordEncoder.matches(pwd,"$2a$10$hMvLeQ9RDDwzknmyCDwIJuXN4qzU3p6NeOEd0JRSaHGHkzE4R8pBa");
//        System.out.println(flag);
//    }
//}
