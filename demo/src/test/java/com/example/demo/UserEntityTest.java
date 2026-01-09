package com.example.demo;

import com.example.demo.entity.User;
import com.example.demo.repository.UserRepository;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.TestPropertySource;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest
@TestPropertySource(locations = "classpath:application.properties")
class UserEntityTest {

    @Autowired
    private UserRepository userRepository;

    @Test
    @Transactional
    void testUserDatabaseOperations() {
        // 测试数据
        String testUsername = "test_user_" + System.currentTimeMillis();
        String testPhone = "13800138000";
        
        // 1. 创建用户并保存到数据库
        User user = new User();
        user.setUsername(testUsername);
        user.setPassword("encoded_test_password"); // 实际应用中应为加密后的密码
        user.setPhone(testPhone);
        user.setCreatedAt(LocalDateTime.now());
        
        User savedUser = userRepository.save(user);
        
        // 2. 验证保存成功
        assertThat(savedUser.getId()).isNotNull(); // 新创建的用户ID应不为null
        assertThat(savedUser.getUsername()).isEqualTo(testUsername);
        assertThat(savedUser.getPhone()).isEqualTo(testPhone);
        
        // 3. 通过ID查询刚保存的用户
        Optional<User> foundUserOpt = userRepository.findById(savedUser.getId());
        assertThat(foundUserOpt).isPresent();
        
        User foundUser = foundUserOpt.get();
        assertThat(foundUser.getUsername()).isEqualTo(testUsername);
        assertThat(foundUser.getPhone()).isEqualTo(testPhone);
        assertThat(foundUser.getPassword()).isEqualTo("encoded_test_password");
        
        // 4. 通过用户名查找用户
        Optional<User> userByUsername = userRepository.findByUsername(testUsername);
        assertThat(userByUsername).isPresent();
        assertThat(userByUsername.get().getId()).isEqualTo(foundUser.getId());
        
        // 5. 更新用户信息
        String updatedPhone = "13900139000";
        foundUser.setPhone(updatedPhone);
        foundUser.setLastLoginAt(LocalDateTime.now());
        
        User updatedUser = userRepository.save(foundUser);
        assertThat(updatedUser.getPhone()).isEqualTo(updatedPhone);
        
        // 6. 验证更新后的数据
        Optional<User> updatedUserFromDb = userRepository.findById(foundUser.getId());
        assertThat(updatedUserFromDb).isPresent();
        assertThat(updatedUserFromDb.get().getPhone()).isEqualTo(updatedPhone);
        
        // 注意：由于使用了@Transactional注解，测试结束后事务会回滚，
        // 所以无需手动删除测试数据，数据库将保持原有状态
    }
    
    @Test
    @Transactional
    void testUserWithSpecialCharacters() {
        // 测试包含特殊字符的用户名
        String testUsername = "test_user_special_" + System.currentTimeMillis();
        String chineseUsername = "测试用户_" + System.currentTimeMillis();
        String phone = "13812345678";
        
        // 创建包含中文用户名的用户
        User user = new User();
        user.setUsername(chineseUsername);
        user.setPassword("encoded_test_password");
        user.setPhone(phone);
        user.setCreatedAt(LocalDateTime.now());
        
        User savedUser = userRepository.save(user);
        
        // 验证保存成功
        assertThat(savedUser.getId()).isNotNull();
        assertThat(savedUser.getUsername()).isEqualTo(chineseUsername);
        
        // 查询验证
        Optional<User> foundUserOpt = userRepository.findByUsername(chineseUsername);
        assertThat(foundUserOpt).isPresent();
        assertThat(foundUserOpt.get().getPhone()).isEqualTo(phone);
        
        // 测试英文用户名
        user.setUsername(testUsername);
        User updatedUser = userRepository.save(user);
        assertThat(updatedUser.getUsername()).isEqualTo(testUsername);
        
        // 查询验证
        Optional<User> foundUserOpt2 = userRepository.findByUsername(testUsername);
        assertThat(foundUserOpt2).isPresent();
        assertThat(foundUserOpt2.get().getPhone()).isEqualTo(phone);
    }
}