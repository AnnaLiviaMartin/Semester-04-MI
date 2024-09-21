package de.hsrm.mi.web.springhtmx;

public class TabellenEintrag {
    private long id; 
    private String name; 
    private Boolean checked;

    public TabellenEintrag() {
    }
    
    public TabellenEintrag(long id, String name, Boolean checked) {
        this.id = id;
        this.name = name;
        this.checked = checked;
    }
    public long getId() {
        return id;
    }
    public void setId(long id) {
        this.id = id;
    }
    public String getName() {
        return name;
    }
    public void setName(String name) {
        this.name = name;
    }
    public Boolean getChecked() {
        return checked;
    }
    public void setChecked(Boolean checked) {
        this.checked = checked;
    }
    @Override
    public int hashCode() {
        final int prime = 31;
        int result = 1;
        result = prime * result + (int) (id ^ (id >>> 32));
        return result;
    }
    @Override
    public boolean equals(Object obj) {
        if (this == obj)
            return true;
        if (obj == null)
            return false;
        if (getClass() != obj.getClass())
            return false;
        TabellenEintrag other = (TabellenEintrag) obj;
        if (id != other.id)
            return false;
        return true;
    }
    @Override
    public String toString() {
        return "TabellenEintrag [id=" + id + ", name=" + name + ", checked=" + checked + "]";
    }

    
    
}
