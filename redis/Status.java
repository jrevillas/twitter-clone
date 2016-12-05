package redis;

import java.io.Serializable;

/**
 * Created by jruiz on 12/1/16.
 */
public class Status implements Serializable {


    private Long postId;
    private String user;
    private String body;

    public Status() {}

    public String getUser() {
        return user;
    }

    public Status setUser(String user) {
        this.user = user;
        return this;
    }

    public String getBody() {
        return body;
    }

    public Status setBody(String body) {
        this.body = body;
        return this;
    }

    public Long getPostId() {
        return postId;
    }

    public Status setPostId(Long postId) {
        this.postId = postId;
        return this;
    }

}
