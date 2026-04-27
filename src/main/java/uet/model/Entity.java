package uet.model;
import java.time.LocalDateTime;
public abstract class Entity {
    private long id;
    private LocalDateTime createdAt;     //thời gian tạo
    private LocalDateTime updatedAt;     // thời gian cập nhật
    public Entity(){
        this.createdAt = LocalDateTime.now();
        this.updatedAt = LocalDateTime.now();
    }
    // getter
    public LocalDateTime getUpdatedAt() {
        return updatedAt;
    }
    public LocalDateTime getCreatedAt() {
        return createdAt;
    }
    public long getId() {
        return id;
    }
    // setter
    public void setUpdatedAt(LocalDateTime updatedAt) {
        this.updatedAt = updatedAt;
    }

    public void setId(long id) {
        this.id = id;
    }
    public void setCreatedAt(LocalDateTime createdAt) {
        this.createdAt = createdAt;
    }

    public abstract String getType();
}