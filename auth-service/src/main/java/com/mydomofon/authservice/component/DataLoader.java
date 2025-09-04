package com.mydomofon.authservice.component;

import com.mydomofon.authservice.model.ERole;
import com.mydomofon.authservice.model.User;
import com.mydomofon.authservice.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

@Component
public class DataLoader implements CommandLineRunner {
    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    @Autowired
    public DataLoader(UserRepository userRepository, PasswordEncoder passwordEncoder) {
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
    }

    @Override
    public void run(String... args) throws Exception {
        // Проверяем, пуста ли база, чтобы не создавать дубликаты при каждом перезапуске
        if (userRepository.count() == 0) {
            // Создаем обычного пользователя
            User user = new User();
            user.setUsername("user_flat_101");
            // ШИФРУЕМ ПАРОЛЬ ПРЯМО ЗДЕСЬ!
            user.setPassword(passwordEncoder.encode("password123"));
            user.setRole(ERole.ROLE_USER);
            userRepository.save(user);

            // Создаем администратора
            User admin = new User();
            admin.setUsername("admin_user");
            admin.setPassword(passwordEncoder.encode("password123"));
            admin.setRole(ERole.ROLE_ADMIN);
            userRepository.save(admin);

            System.out.println("!!! TEST USERS CREATED VIA DataLoader !!!");
        }
    }
}
