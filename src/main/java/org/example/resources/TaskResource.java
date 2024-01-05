package org.example.resources;

import io.dropwizard.hibernate.UnitOfWork;
import org.example.db.TaskDAO;
import jakarta.ws.rs.*;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;
import org.example.model.Task;
import org.hibernate.HibernateException;
import java.util.List;
import java.util.Objects;

import static java.util.Collections.list;

@Path("/tasks")
@Produces(MediaType.APPLICATION_JSON)
@Consumes(MediaType.APPLICATION_JSON)
public class TaskResource {

    private final TaskDAO taskDAO;

    public TaskResource(TaskDAO taskDAO) {
        this.taskDAO = taskDAO;
    }


    private boolean isValidStatusTransition(String currentStatus, String newStatus) {

        if ("TODO".equals(currentStatus) && "WIP".equals(newStatus)) {
            return true;
        } else if ("WIP".equals(currentStatus) && "DONE".equals(newStatus)) {
            return true;
        }
        return false;
    }

    @GET
    @UnitOfWork
    public List<Task> getAllTasks() {
        return taskDAO.findAll();
    }

    @GET
    @Path("/{taskId}")
    @UnitOfWork
    public Response getTask(@PathParam("taskId") long taskId) {
        Task task = taskDAO.findById(taskId);
        if (task != null) {
            return Response.ok().entity(task).build();
        } else {
            return Response.status(Response.Status.NOT_FOUND).build();
        }


    }

    @POST
    @UnitOfWork
    public Response createTask(Task task) throws HibernateException {
        int taskId=(int) task.getId();
        Task db_task = taskDAO.findById(taskId);
        String curr_st = task.getStatus();
        System.out.println(curr_st);
        if( !Objects.equals(curr_st, "TODO")){
//            System.out.println("1111111111111111111111111");
            return Response.status(Response.Status.NOT_ACCEPTABLE).build();
        }
        if(db_task!=null){
            return Response.status(Response.Status.CREATED).entity(task).build();
        }else{
            taskDAO.create(task);
            return Response.status(Response.Status.CREATED).entity(task).build();
        }

    }

    @PUT
    @Path("/{taskId}")
    @UnitOfWork
    public Response updateTask(@PathParam("taskId") long taskId, Task task){
        System.out.println("updating................................................|||");

        Task byId = taskDAO.findById(taskId);
        if(byId==null || byId.getId()==0){
            return Response.status(Response.Status.NOT_FOUND).build();
        }else{
            if (!isValidStatusTransition(byId.getStatus(), task.getStatus())){
                return Response.status(Response.Status.NOT_ACCEPTABLE).build();
            }

            if (task.getDescription() == null) {
                task.setDescription(byId.getDescription());
            }
            if (task.getStartDate() == null) {
                task.setStartDate(byId.getStartDate());
            }
            if (task.getTargetDate() == null) {
                task.setTargetDate(byId.getTargetDate());
            }
            task.setId(taskId);
            taskDAO.update(task);
            return Response.ok().entity(task)
                    .build();
        }

    }

    @DELETE
    @Path("/{taskId}")
    @UnitOfWork
    public Response removeTask(@PathParam("taskId") long taskId) {
        Task task = taskDAO.findById(taskId);
        System.out.println(task);
        if (task != null) {
            taskDAO.delete(task);
            return Response.ok().entity(task).build();
        } else {
            return Response.status(Response.Status.NOT_FOUND).build();
        }
    }
}