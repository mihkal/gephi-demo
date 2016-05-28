package edu.buaa.act.gephi.plugin.task;

import org.act.neo4j.temporal.demo.utils.TransactionWrapper;
import org.gephi.graph.api.Edge;
import org.gephi.graph.api.Graph;
import org.gephi.utils.longtask.spi.LongTask;
import org.gephi.utils.progress.ProgressTicket;
import org.neo4j.graphdb.GraphDatabaseService;
import org.neo4j.graphdb.Node;
import org.neo4j.graphdb.Relationship;
import org.neo4j.register.Register;
import org.neo4j.tooling.GlobalGraphOperations;

import java.awt.*;
import java.util.HashMap;
import java.util.Map;

/**
 * Created by song on 16-5-12.
 */
public class UpdateGraphStatusAsyncTask extends TransactionWrapper<Integer> implements LongTask, Runnable{

    private int time;
    private final Graph graph;
    private GraphDatabaseService db;
    private ProgressTicket progress;
    private boolean shouldGo = true;

    public UpdateGraphStatusAsyncTask(GraphDatabaseService db, Graph graph, int time) {
        this.db=db;
        this.time = time;
        this.graph = graph;
    }

    @Override
    public void runInTransaction() {
        boolean updateFlag=false;
        for (Edge edge: graph.getEdges()){
            if(!shouldGo) return;
            progress.progress();
            updateFlag=true;
            Object tgraphId = edge.getAttribute("tgraph_id");
            if(tgraphId!=null){
                Relationship r = db.getRelationshipById((Long) tgraphId);
                if(r!=null){
                    Object fullStatus = r.getDynPropertyPointValue("full-status", time);
                    if(fullStatus!=null){
//                        System.out.println("has status.");
                        int status = (Integer) fullStatus;
                        if(status==1){
                            edge.setColor(Color.GREEN);
                        }else if(status==2){
                            edge.setColor(Color.ORANGE);
                        }else if(status==3){ //status==3
                            edge.setColor(Color.RED);
                        }else{
                            edge.setColor(new Color(0xb0b0b0));
                        }
//                        float speed = ((Integer) travelTime)*1f/length;
                    }else{
                        System.out.println("null status");
                    }
                }else{
                    throw new RuntimeException("Relationship not found in TGraph!");
                }
            }else{
                System.out.println("no tgraph_id attribut in gephi.");
            }
        }
        if(!updateFlag) System.out.println("no edge");
    }

    @Override
    public void run() {
        Thread.currentThread().setName("TGraph.task.UpdateGraph");
        progress.start(124436);
        this.start(db);
        progress.finish();
    }

    @Override
    public boolean cancel() {
        shouldGo=false;
        return true;
    }

    @Override
    public void setProgressTicket(ProgressTicket progressTicket) {
        this.progress = progressTicket;
    }

}
