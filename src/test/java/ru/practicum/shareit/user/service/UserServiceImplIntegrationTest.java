package ru.practicum.shareit.user.service;

import lombok.RequiredArgsConstructor;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.context.junit.jupiter.SpringJUnitConfig;
import ru.practicum.shareit.ShareItApp;
import ru.practicum.shareit.user.dto.UserRequestDto;
import ru.practicum.shareit.user.model.User;

import javax.persistence.EntityManager;
import javax.persistence.TypedQuery;
import javax.transaction.Transactional;
import java.util.List;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.notNullValue;
import static org.junit.jupiter.api.Assertions.assertEquals;

@RequiredArgsConstructor(onConstructor_ = @Autowired)
@SpringJUnitConfig({ShareItApp.class, UserServiceImpl.class})
@TestPropertySource(properties = {"db.name=test"})
@Transactional
class UserServiceImplIntegrationTest {

    private final EntityManager em;
    private final UserService userService;

    private UserRequestDto userRequestDto;
    private UserRequestDto userRequestDto2;

    @BeforeEach
    void setUp() {
        userRequestDto = new UserRequestDto();
        userRequestDto.setName("Arthur");
        userRequestDto.setEmail("arthur@gmail.com");

        userRequestDto2 = new UserRequestDto();
        userRequestDto2.setName("Jonsi");
        userRequestDto2.setEmail("jonsi@gmail.com");
    }

    @Test
    void createUserTest() {
        userService.createUser(userRequestDto);
        TypedQuery<User> query = em.createQuery("Select u from User u where u.email = :email", User.class);
        User queryUser = query
                .setParameter("email", userRequestDto.getEmail()) // Выбираем пользователя по email, так как он должен быть уникальным
                .getSingleResult();

        assertThat(queryUser.getId(), notNullValue());
        assertEquals(userRequestDto.getName(), queryUser.getName());
        assertEquals(userRequestDto.getEmail(), queryUser.getEmail());
    }

    @Test
    void getUsersTest() {
        userService.createUser(userRequestDto);
        userService.createUser(userRequestDto2);
        List<User> allUsers = em.createQuery("SELECT u FROM User u", User.class)
                .getResultList();

        assertEquals(2, allUsers.size());
        assertEquals(allUsers.get(0).getName(), userRequestDto.getName());
        assertEquals(allUsers.get(0).getEmail(), userRequestDto.getEmail());
        assertEquals(allUsers.get(1).getName(), userRequestDto2.getName());
        assertEquals(allUsers.get(1).getEmail(), userRequestDto2.getEmail());
    }
}