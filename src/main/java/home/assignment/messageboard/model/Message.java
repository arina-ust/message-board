package home.assignment.messageboard.model;

import java.time.OffsetDateTime;

public class Message {

    private Integer id;
    private Integer userId;
    private String title;
    private String text;
    private OffsetDateTime createdAt;
    private OffsetDateTime updatedAt;

    public Message(String title, String text, Integer userId) {
        this.title = title;
        this.text = text;
        this.userId = userId;
    }

    public Message(Integer id, Integer userId, String title, String text, OffsetDateTime createdAt, OffsetDateTime updatedAt) {
        this.id = id;
        this.userId = userId;
        this.title = title;
        this.text = text;
        this.createdAt = createdAt;
        this.updatedAt = updatedAt;
    }

    public Integer getId() {
        return id;
    }

    public String getTitle() {
        return title;
    }

    public String getText() {
        return text;
    }

    public Integer getUserId() {
        return userId;
    }

    public OffsetDateTime getCreatedAt() {
        return createdAt;
    }

    public OffsetDateTime getUpdatedAt() {
        return updatedAt;
    }
}
