package org.kootox.episodesmanager;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import org.debux.webmotion.server.call.Call;
import org.debux.webmotion.server.call.HttpContext;
import org.debux.webmotion.server.mapping.Mapping;
import org.debux.webmotion.server.render.Render;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.PrintWriter;


public class MyRenderJson extends Render {

    protected Object model;

    public MyRenderJson(Object model) {
        this.model = model;
    }

    public Object getModel() {
        return model;
    }

    @Override
    public void create(Mapping mapping, Call call) throws IOException, ServletException {
        HttpContext context = call.getContext();
        HttpServletResponse response = context.getResponse();

        response.setContentType("application/json");

        Gson gson = new GsonBuilder().setExclusionStrategies()
                .setExclusionStrategies(new EpisodeExclusionStrategy())
                .create();
        String json = gson.toJson(model);
        PrintWriter out = context.getOut();
        out.print(json);
    }

}
