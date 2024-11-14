package io.github.wkktoria.shareall.post;

import org.springframework.stereotype.Service;

import java.util.Date;

@Service
public class PostService {
    private final PostRepository postRepository;

    public PostService(final PostRepository postRepository) {
        this.postRepository = postRepository;
    }

    public void save(final Post post) {
        post.setTimestamp(new Date());
        postRepository.save(post);
    }
}
