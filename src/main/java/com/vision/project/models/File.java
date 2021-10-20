package com.vision.project.models;

import com.fasterxml.jackson.annotation.JsonIdentityInfo;
import com.fasterxml.jackson.annotation.ObjectIdGenerators;
import org.hibernate.annotations.OnDelete;
import org.hibernate.annotations.OnDeleteAction;
import javax.persistence.*;

@Entity
@Table(name = "files")
@JsonIdentityInfo(generator = ObjectIdGenerators.PropertyGenerator.class, property = "id", scope = File.class)
public class File {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private long id;

    @Column(name = "resource_type")
    private String resourceType;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "owner")
    @OnDelete(action = OnDeleteAction.CASCADE)
    private UserModel owner;

    private String extension;
    private String type;
    private double size;

    public File() {
    }

    public File(String resourceType, double size, String type, String extension){
        this.resourceType = resourceType;
        this.size = size;
        this.type = type;
        this.extension = extension;
    }

    public File(String resourceType, double size, String type, String extension, UserModel owner){
        this.resourceType = resourceType;
        this.size = size;
        this.type = type;
        this.extension = extension;
        this.owner = owner;
    }


    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public double getSize() {
        return size;
    }

    public String getType() {
        return type;
    }

    public String getResourceType() {
        return resourceType;
    }

    public void setResourceType(String resourceType) {
        this.resourceType = resourceType;
    }

    public String getExtension() {
        return extension;
    }

    public void setExtension(String extension) {
        this.extension = extension;
    }

    public UserModel getOwner() {
        return owner;
    }

    public void setOwner(UserModel owner) {
        this.owner = owner;
    }
}
