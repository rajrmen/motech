package org.motechproject.admin.domain;

import org.motechproject.mds.annotations.Cascade;
import org.motechproject.mds.annotations.Entity;
import org.motechproject.mds.annotations.Field;

import java.util.ArrayList;
import java.util.List;

@Entity
public class Car {

    @Field
    @Cascade(persist = false, update = false, delete = false)
    private List<Wheel> wheels;

    @Field
    private String name;

    public List<Wheel> getWheels() {
        if (wheels == null) {
            wheels = new ArrayList<>();
        }
        return wheels;
    }

    public void setWheels(List<Wheel> wheels) {
        this.wheels = wheels;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }
}
