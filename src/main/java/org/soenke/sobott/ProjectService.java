package org.soenke.sobott;

import io.quarkus.mongodb.panache.PanacheMongoRepository;
import org.soenke.sobott.entity.Project;

import javax.enterprise.context.ApplicationScoped;
import java.util.List;

@ApplicationScoped
public class ProjectService implements PanacheMongoRepository<Project> {

    public List<Project> getAllProjects() {
        return listAll();
    }
}
