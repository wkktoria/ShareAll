package io.github.wkktoria.shareall.post;

import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import io.github.wkktoria.shareall.shared.annotation.CurrentUser;
import io.github.wkktoria.shareall.user.User;
import jakarta.validation.Valid;

@RestController
@RequestMapping("/api/1.0")
class PostController {
	private final PostService postService;

	PostController(final PostService postService) {
		this.postService = postService;
	}

	@PostMapping("/posts")
	void createPost(@Valid @RequestBody Post post, @CurrentUser User user) {
		postService.save(user, post);
	}

}
