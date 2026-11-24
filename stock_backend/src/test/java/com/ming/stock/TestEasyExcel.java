//package com.ming.stock;
//
//import com.alibaba.excel.EasyExcel;
//import com.alibaba.excel.context.AnalysisContext;
//import com.alibaba.excel.event.AnalysisEventListener;
//import com.ming.stock.pojo.User;
//import org.junit.jupiter.api.Test;
//
//import java.util.ArrayList;
//import java.util.Date;
//import java.util.List;
//
///**
// * @Author: Ming
// * @Description TODO
// */
//public class TestEasyExcel {
//    public List<User> init() {
//        List<User> users = new ArrayList<>();
//        for (int i = 0; i < 10; i++) {
//            User user = new User();
//            user.setUserName("张三" + i);
//            user.setBirthday(new Date());
//            user.setAddress("上海" + i);
//            user.setAge(10 + i);
//            users.add(user);
//        }
//        return users;
//    }
//
//    @Test
//    public void test01() {
//        List<User> users = init();
//        EasyExcel.write("C:\\Users\\11519\\Desktop\\test.xls", User.class)
//                .sheet("用户信息").doWrite(users);
//    }
//
//    /*
//     * 读取excel数据格式必须与实体类定义一致 否则读取不到数据
//     * */
//    @Test
//    public void readExcel() {
//        ArrayList<User> users = new ArrayList<>();
//        //读取数据 最后一个参数是解析器 读取一行回调一次下面的invoke方法 都解析完成执行完成
//        EasyExcel.read("C:\\Users\\11519\\Desktop\\test.xls", User.class, new AnalysisEventListener<User>() {
//            @Override
//            public void invoke(User data, AnalysisContext context) {
//                System.out.println(data);
//                users.add(data);
//            }
//
//            @Override
//            public void doAfterAllAnalysed(AnalysisContext context) {
//                System.out.println("完成");
//            }
//        }).sheet().doRead();
//        System.out.println(users);
//    }
//
//
//}
