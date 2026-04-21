package com.sg.assignment.service;

import com.sg.assignment.model.PostEntity;
import com.sg.assignment.repository.PostRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;

@Service
public class PostService {

    @Autowired
    private PostRepository postRepository;

    @Autowired
    private RedisTemplate<String, Object> redisTemplate;

    public PostEntity createPost(PostEntity post) {
        post.setCreatedAt(LocalDateTime.now());
        return postRepository.save(post);
    }

    public void likePost(Long postId) {
        String key = "post:" + postId + ":virality_score";
        redisTemplate.opsForValue().increment(key, 20);
    }

}
