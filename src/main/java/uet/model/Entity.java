package uet.model;
import java.time.LocalDateTime;
public abstract class Entity {
    private long id;
    private LocalDateTime createdAt;     //thời gian tạo
    private LocalDateTime updatedAt;     // thời gian cập nhật
    private static long totalEntity;    // tổng số thực thế
    public Entity(){
        this.createdAt = LocalDateTime.now();
        this.updatedAt = LocalDateTime.now();
        Entity.totalEntity++;
    }
    public LocalDateTime getUpdatedAt() {
        return updatedAt;
    }
    public LocalDateTime getCreatedAt() {
        return createdAt;
    }
    public long getId() {
        return id;
    }
    public void setUpdatedAt(LocalDateTime updatedAt) {
        this.updatedAt = updatedAt;
    }
    public abstract String getType();
}