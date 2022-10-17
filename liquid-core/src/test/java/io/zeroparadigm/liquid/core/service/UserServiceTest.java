package io.zeroparadigm.liquid.core.service;

import static org.assertj.core.api.Assertions.assertThat;

import io.zeroparadigm.liquid.common.api.media.MinioService;
import io.zeroparadigm.liquid.core.service.impl.UserServiceImpl;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.context.testng.AbstractTestNGSpringContextTests;

//@SpringBootTest(classes = UserService.class)
//@TestPropertySource(properties = {
//    "dubbo.consumer.scope = local",
//    "dubbo.consumer.check = false",
//    "dubbo.registry.address=N/A",
//})
//@AutoConfigureMockMvc
//class UserServiceTest {
//
//    @InjectMocks
//    @Resource
//    UserServiceImpl userService;
//
//    @MockBean
//    MinioService minioService;
//
//    @Test
//    void testA() {
//        Mockito.when(minioService.upload(null, null, "a"))
//            .thenReturn("ok");
//
//        assertThat(userService.getMinioService().upload(null, null, "a")).isEqualTo("ok");
//    }
//}

//@SpringBootTest(classes = UserService.class)
//@TestPropertySource(properties = {
//    "dubbo.consumer.scope = local",
//    "dubbo.consumer.check = false",
//    "dubbo.registry.address=N/A",
//})
@AutoConfigureMockMvc
class UserServiceTest extends AbstractTestNGSpringContextTests {

    @InjectMocks
    @Autowired
    UserServiceImpl userService;

    @MockBean
    MinioService minioService;

    @BeforeEach
    void initMocks() {
        MockitoAnnotations.initMocks(this);
    }

        @Test
    void testA() {
        Mockito.when(minioService.upload(null, null, "a"))
            .thenReturn("ok");

        assertThat(userService.getMinioService().upload(null, null, "a")).isEqualTo("ok");
    }

}