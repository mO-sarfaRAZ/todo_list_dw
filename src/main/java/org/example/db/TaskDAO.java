package org.example.db;

import io.dropwizard.hibernate.AbstractDAO;
import org.example.model.Task;
import org.hibernate.SessionFactory;
import java.util.List;

public class TaskDAO extends AbstractDAO<Task> {

    public TaskDAO(SessionFactory factory) {
        super(factory);
    }

    public Task findById(long id) {
        return get(id);
    }

    public List<Task> findAll() {
        return list(namedTypedQuery("org.example.model.Task.findALL"));
    }

    public Task create(Task task) {
        return persist(task);
    }

    public Task update(Task task) {
        return currentSession().merge(task);
    }

    public void delete(Task task) {
        currentSession().delete(task);
    }
}
