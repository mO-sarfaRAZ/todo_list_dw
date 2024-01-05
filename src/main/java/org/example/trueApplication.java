package org.example;

import io.dropwizard.core.Application;
import io.dropwizard.core.setup.Bootstrap;
import io.dropwizard.core.setup.Environment;
import io.dropwizard.db.DataSourceFactory;
import io.dropwizard.hibernate.HibernateBundle;
import org.example.db.TaskDAO;
import org.example.model.Task;
import org.example.resources.TaskResource;

public class trueApplication extends Application<trueConfiguration> {

    public static void main(final String[] args) throws Exception {
        new trueApplication().run(args);
    }

    @Override
    public String getName() {
        return "true";
    }

    private final HibernateBundle<trueConfiguration> hibernate = new HibernateBundle<trueConfiguration>(Task.class) {
        @Override
        public DataSourceFactory getDataSourceFactory(trueConfiguration configuration) {
            return configuration.getDataSourceFactory();
        }
    };
    
    @Override
    public void initialize(final Bootstrap<trueConfiguration> bootstrap) {
        bootstrap.addBundle(hibernate);
    }

    @Override
    public void run(final trueConfiguration configuration,
                    final Environment environment) {
        final TaskDAO taskDAO = new TaskDAO(hibernate.getSessionFactory());
        environment.jersey().register(new TaskResource(taskDAO));
    }

}
