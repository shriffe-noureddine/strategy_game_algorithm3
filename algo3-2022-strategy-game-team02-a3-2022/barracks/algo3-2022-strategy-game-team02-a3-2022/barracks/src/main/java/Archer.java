import java.util.Objects;
import java.util.concurrent.atomic.AtomicInteger;

public class Archer {

    private int id;
    private boolean available;
    private boolean dead;
    private boolean upgraded;

    private static AtomicInteger lastId;
    static {
        lastId = new AtomicInteger(0);
    }
    public Archer() {
        this.available = true;
        this.id = Integer.parseInt(String.valueOf(lastId.incrementAndGet()));
        this.dead = false;
        if(app.archersUpgraded){
            this.upgraded = true;
        }else {
            this.upgraded = false;
        }

    }

    public int getId() {
        return id;
    }


    public boolean isAvailable() {
        return available;
    }

    public void setAvailable(boolean available) {
        this.available = available;
    }

    public boolean isDead() {
        return dead;
    }

    public void setDead(boolean dead) {
        this.dead = dead;
    }

    public boolean isUpgraded(){
        return upgraded;
    }
    public void setUpgraded(boolean upgraded){
        this.upgraded = upgraded;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Archer archer = (Archer) o;
        return Objects.equals(id, archer.id);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id);
    }

    @Override
    public String toString() {
        return "{" +
                "id='" + id + '\'' +
                ", available='" + available + '\'' +
                '}';
    }
}