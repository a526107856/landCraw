package com.jindi.land;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import lombok.extern.slf4j.Slf4j;
import org.junit.Ignore;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.test.context.junit4.SpringRunner;

@ComponentScan({"com.jindi.service", "com.jindi.land"})
@MapperScan("com.jindi.land.mapper")
@RunWith(SpringRunner.class)
@SpringBootTest
@Slf4j
public class LandApplicationTest {

  //(1)创建单例实例
  static SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");

  @Test
  @Ignore
  public void testSdf() {

    //(2)创建多个线程，并启动
    for (int i = 0; i < 10; ++i) {
      Thread thread = new Thread(new Runnable() {
        public void run() {
          try {//(3)使用单例日期实例解析文本
            System.out.println(sdf.parse("2017-12-13 15:17:27"));
          } catch (ParseException e) {
            e.printStackTrace();
          }
        }
      });
      thread.start();//(4)启动线程
    }

  }
}
