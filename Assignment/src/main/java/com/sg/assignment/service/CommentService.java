package com.sg.assignment.service;

import com.sg.assignment.model.CommentEntity;
import com.sg.assignment.repository.BotRepository;
import com.sg.assignment.repository.CommentRepository;
import com.sg.assignment.repository.PostRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.concurrent.TimeUnit;

@Service
public class CommentService {

    @Autowired
    private CommentRepository commentRepository;

    @Autowired
    private PostRepository postRepository;

    @Autowired
    private BotRepository botRepository;

    @Autowired
    private RedisTemplate<String, Object> redisTemplate;

    public CommentEntity addComment(CommentEntity comment){

        Long postId = comment.getPostId();

        if(comment.getDepthLevel() > 20){
            throw new RuntimeException("Depth limit exceeded");
        }

        boolean isBot = isBot(comment.getAuthorId());

        if(isBot){
            String botCountKey = "post:" + postId + ":bot_count";
            Long current = redisTemplate.opsForValue().get(botCountKey) == null
                    ? 0
                    : Long.parseLong(redisTemplate.opsForValue().get(botCountKey).toString());

            if(current >= 100){
                throw new RuntimeException("429 Too Many Bot Replies");
            }
            redisTemplate.opsForValue().decrement(botCountKey);
        }

        if(isBot){
            Long humanId = getPostOwner(postId);
            String cooldownKey = "cooldown:bot_" + comment.getAuthorId() + ":human_" + humanId;

            if (redisTemplate.hasKey(cooldownKey)) {
                throw new RuntimeException("Cooldown active");
            }

            redisTemplate.opsForValue().set(cooldownKey, "1", 10, TimeUnit.MINUTES);
        }

        String viralityKey = "post:" + postId + ":virality_score";

        if(isBot){
            redisTemplate.opsForValue().increment(viralityKey, 1);
        }else{
            redisTemplate.opsForValue().increment(viralityKey, 50);
        }

        comment.setCreatedAt(LocalDateTime.now());

        return commentRepository.save(comment);
    }

    private Long getPostOwner(Long postId){
        return postRepository.findById(postId)
                .orElseThrow()
                .getAuthorId();
    }

    private boolean isBot(Long authorId){
        return botRepository.existsById(authorId);
    }

}
