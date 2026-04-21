package com.sg.assignment.controller;

import com.sg.assignment.model.CommentEntity;
import com.sg.assignment.service.CommentService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/posts")
public class CommentController {

    @Autowired
    private CommentService commentService;

    @PostMapping("/{postId}/comments")
    public CommentEntity addComment(@PathVariable Long postId,
                              @RequestBody CommentEntity comment) {

        comment.setPostId(postId);
        return commentService.addComment(comment);
    }


}