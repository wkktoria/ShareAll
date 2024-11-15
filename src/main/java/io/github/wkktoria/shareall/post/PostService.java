package io.github.wkktoria.shareall.post;

import java.util.Date;

import org.springframework.stereotype.Service;

import io.github.wkktoria.shareall.user.User;

@Service
public class PostService {
    private final PostRepository postRepository;

    public PostService(final PostRepository postRepository) {
        this.postRepository = postRepository;
    }

    public void save(User user, Post post) {
        post.setTimestamp(new Date());
        post.setUser(user);
        postRepository.save(post);
    }
}
