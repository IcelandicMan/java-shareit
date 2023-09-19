package ru.practicum.shareit.item.repository;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import ru.practicum.shareit.item.model.Comment;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.user.model.User;
import ru.practicum.shareit.user.repository.UserRepository;

import java.time.LocalDateTime;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;

@DataJpaTest
class CommentRepositoryTest {

    @Autowired
    private CommentRepository commentRepository;

    @Autowired
    private ItemRepository itemRepository;
    @Autowired
    private UserRepository userRepository;


    private User user;
    private User user2;
    private User user3;

    private Item item;

    private Comment comment;

    @BeforeEach
    void setUp() {
        user = new User();
        user.setName("Arthur");
        user.setEmail("arthur@gmail.com");

        user2 = new User();
        user2.setName("Jonsi");
        user2.setEmail("jonsi@gmail.com");

        user3 = new User();
        user3.setName("Test");
        user3.setEmail("test@gmail.com");

        userRepository.save(user);
        userRepository.save(user2);
        userRepository.save(user3);

        item = new Item();
        item.setName("Укулеле");
        item.setDescription("струны карбоновые");
        item.setOwner(user);
        item.setAvailable(true);
        itemRepository.save(item);
    }

    @Test
    void findCommentsByItemIdTest() {
        comment = new Comment();
        comment.setItem(item);
        comment.setAuthor(user2);
        comment.setText("4 звезды");
        comment.setCreated(LocalDateTime.now().minusDays(5));
        commentRepository.save(comment);

        List<Comment> comments = commentRepository.findCommentsByItemId(1L);
        assertEquals(1, comments.size());
        assertEquals(comment, comments.get(0).getText());

        Comment comment2 = new Comment();
        comment2.setItem(item);
        comment2.setAuthor(user3);
        comment2.setText("5 звезд");
        comment2.setCreated(LocalDateTime.now().minusDays(10));
        commentRepository.save(comment2);

        comments = commentRepository.findCommentsByItemId(1L);
        assertEquals(2, comments.size());
        assertEquals(comment, comments.get(0));
        assertEquals(comment2, comments.get(1));
    }
}