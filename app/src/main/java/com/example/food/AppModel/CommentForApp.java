package com.example.food.AppModel;

import java.io.Serializable;

/**
 * Created by PC-26 on 7/12/2018.
 */

public class CommentForApp implements Serializable{
    private String userNickName;
    private String Comment;
    private String commentRecomCount;
    private String MsgCid;

    public CommentForApp(String userNickName, String comment, String commentRecomCount, String msgCid) {
        this.userNickName = userNickName;
        Comment = comment;
        this.commentRecomCount = commentRecomCount;
        MsgCid = msgCid;
    }

    public String getUserNickName() {
        return userNickName;
    }

    public void setUserNickName(String userNickName) {
        this.userNickName = userNickName;
    }

    public String getComment() {
        return Comment;
    }

    public void setComment(String comment) {
        Comment = comment;
    }

    public String getCommentRecomCount() {
        return commentRecomCount;
    }

    public void setCommentRecomCount(String commentRecomCount) {
        this.commentRecomCount = commentRecomCount;
    }

    public String getMsgCid() {
        return MsgCid;
    }

    public void setMsgCid(String msgCid) {
        MsgCid = msgCid;
    }
}
