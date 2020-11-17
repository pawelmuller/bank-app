import javax.persistence.*;

@Entity
@Table(name = "REGIONS")
public class Regions {
    private Integer region_id;
    private String name;
    public Regions(Integer region_id, String name) {
        this.region_id = region_id;
        this.name = name;
    }
    public Regions() {
    }

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE)
    public Integer getRegion_ID() {
        return region_id;
    }
    public String getName() {
        return name;
    }
    public void setRegion_ID(int region_id) {
        this.region_id = region_id;
    }
    public void setName(String name) {
        this.name = name;
    }
}
