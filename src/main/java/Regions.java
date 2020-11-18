import org.hibernate.annotations.GenericGenerator;

import javax.persistence.*;

@Entity
public class Regions {
    private Integer region_id;
    private String name;
    public Regions(String name) {
        this.name = name;
    }
    public Regions() {
    }

    @Id
    @GeneratedValue(generator = "incrementator-inator")
    @GenericGenerator(name = "incrementator-inator", strategy = "increment")
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
