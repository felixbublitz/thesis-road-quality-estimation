package de.felixbublitz.simra_rq.track;

import de.felixbublitz.simra_rq.DebugHelper;
import de.felixbublitz.simra_rq.etc.GPSOperation;
import de.felixbublitz.simra_rq.simra.GPSData;
import org.json.JSONArray;

import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.util.ArrayList;
import java.util.Collections;
import java.util.concurrent.TimeUnit;

public class RoadPath {

    private ArrayList<RoadNode> nodes;
    private static final int MAX_NODE_DIST = 20; //m


    public RoadPath(ArrayList nodes){
        if(nodes.get(0).getClass() == RoadNode.class)
            this.nodes = nodes;

        if(nodes.get(0).getClass() == GPSData.class)
            this.nodes = getRoadNodes(nodes);

    }




    private ArrayList<RoadNode> getRoadNodes(ArrayList<GPSData> nodes){
        ArrayList<RoadNode> out = new ArrayList<>();

        double position = 0;
        for(int i=0; i<nodes.size(); i++){
            out.add(new RoadNode(nodes.get(i), (int)position));
            if(i<nodes.size()-1)
            position = position + nodes.get(i).getDistanceTo(nodes.get(i+1));
        }

        return out;
    }

    public ArrayList<RoadNode> getNodes(){
        return nodes;
    }

    public RoadPath getIntersection(Integer start, Integer end){
        ArrayList<RoadNode> outNodes = new ArrayList<>();

        if(start == null)
            start = 0;

        if(end == null)
            end = getLength();

        for(RoadNode n : nodes){
            if(n.getPosition() >= start && n.getPosition() <= end){
                outNodes.add(n);
            }
        }

        if(outNodes.size() == 0){
            outNodes.add(0,new RoadNode(getGPSPoint(start), start));
            outNodes.add(new RoadNode(getGPSPoint(end), end));
            return new RoadPath(outNodes);

        }

        if(outNodes.get(0).getPosition() != start){
            outNodes.add(0,new RoadNode(getGPSPoint(start), start));
        }

        if(outNodes.get(outNodes.size()-1).getPosition() != end){
            outNodes.add(new RoadNode(getGPSPoint(end), end));
        }


        return new RoadPath(outNodes);
    }

    public int getLength(){
        return nodes.get(nodes.size()-1).getPosition();
    }

    private RoadNode getEnclosingNode(RoadNode node, GPSData px){
        final int MAX_ANGLE = 90;
        double minDist = Integer.MAX_VALUE;
        int enclosingNodeId = 0;

        for(int i=0;i<nodes.size(); i++){
            RoadNode currNode = nodes.get(i);
            if (px.getDistanceTo(currNode.getGPSData()) < minDist && GPSOperation.getAngle(currNode.getGPSData(), px, node.getGPSData()) < MAX_ANGLE && currNode != node) {
                minDist = px.getDistanceTo(nodes.get(i).getGPSData());
                enclosingNodeId = i;
            }
        }
        return nodes.get(enclosingNodeId);
    }

    private RoadNode getNearestNode(GPSData gps){
        double minDist = Double.MAX_VALUE;
        RoadNode nearest = null;
        for(RoadNode n: nodes){
            double currDist = n.getGPSData().getDistanceTo(gps);
            if(currDist < minDist){
                minDist = currDist;
                nearest = n;
            }
        }
        return nearest;
    }


    private RoadNode getNearestNode(int position){
        double minDist = Double.MAX_VALUE;
        RoadNode nearest = null;
        for(RoadNode n: nodes){
            double currDist = Math.abs(position-n.getPosition());
            if(currDist < minDist){
                minDist = currDist;
                nearest = n;
            }
        }
        return nearest;
    }


    public GPSData getGPSPoint(int position){

        RoadNode nearestNode = getNearestNode(position);
        RoadNode nextNode = null;
        int nearestNodeId = nodes.indexOf(nearestNode);

        if( nearestNode.getPosition() < position){
            nextNode = nodes.get(Math.min(nodes.size()-1,nearestNodeId+1));
        }else{
            nextNode = nodes.get( Math.max(0,nearestNodeId-1));
        }

        int nearestNodeDist = Math.abs(position-nearestNode.getPosition());
        int nextNodeDist = Math.abs(position-nextNode.getPosition());

        double dist = nearestNodeDist + nextNodeDist;
        double latDist =  nextNode.getGPSData().getLatitude() - nearestNode.getGPSData().getLatitude();
        double lonDist = nextNode.getGPSData().getLongitude() - nearestNode.getGPSData().getLongitude();

        double progress = nearestNodeDist/dist;

        return new GPSData(nearestNode.getGPSData().getLatitude() + progress*latDist, nearestNode.getGPSData().getLongitude() + progress * lonDist);

    }



    public Integer getPosition(GPSData px) {
        RoadNode nearestNode = getNearestNode(px);
        RoadNode nextNode = getEnclosingNode(nearestNode, px);

        double nearestNodeDist = nearestNode.getGPSData().getDistanceTo(px);
        double nextNodeDist = nextNode.getGPSData().getDistanceTo(px);

        double dist = nearestNodeDist + nextNodeDist;
        double progress = nearestNodeDist/dist;

        if(nextNode.getPosition() < nearestNode.getPosition()){
            progress*=-1;
        }

        Integer out = (int)(nearestNode.getPosition() + progress*dist);

        //DebugHelper.showOnMap(this, px,getGPSPoint(out), nearestNode.getGPSData(), nextNode.getGPSData());

      //  int pos = getPosition(g1);

        if(getGPSPoint(out).getDistanceTo(px) >= MAX_NODE_DIST){
           // DebugHelper.showOnMap(this, getGPSPoint(out), px);
            return null;
        }


        return out;
    }




    /*
    public ArrayList<GPSData> getNodes(int start, int end){
        double pos = 0;
        ArrayList<GPSData> out = new ArrayList<GPSData>();

        if(pos >= start && pos <= end){
            out.add(nodes.get(0));
        }
        for(int i=0;i<nodes.size()-1;i++){
            pos += nodes.get(i).getDistanceTo(nodes.get(i+1));
            if(pos >= start && pos <= end){
                out.add(nodes.get(i));
            }
        }

        return nodes;
    }

    private int getLengt(){
        int len = 0;
        for(ArrayList<GPSData> n : nodes) {
            for (int i = 0; i < n.size() - 1; i++) {
                len += n.get(i).getDistanceTo(n.get(i + 1));
            }
        }

        return (int)Math.round(len*0.1)*10;
    };








 */


}
