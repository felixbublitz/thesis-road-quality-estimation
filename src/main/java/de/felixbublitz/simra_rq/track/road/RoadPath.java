package de.felixbublitz.simra_rq.track.road;

import de.felixbublitz.simra_rq.etc.GPSOperation;
import de.felixbublitz.simra_rq.simra.GPSData;

import java.util.ArrayList;

/**
 *
 * Consists of multiple RoadNodes
 */

public class RoadPath {

    private ArrayList<RoadNode> nodes;
    private static final int MAX_NODE_DIST = 20; //m

    /**
     * Create RoadPath by given nodes
     * @param nodes list of RoadNodes or GPSData
     */
    public RoadPath(ArrayList nodes){
        if(nodes.get(0).getClass() == RoadNode.class)
            this.nodes = nodes;

        if(nodes.get(0).getClass() == GPSData.class)
            this.nodes = getRoadNodes(nodes);

    }


    /**
     * Get road nodes from list of gps nodes
     * @param nodes list of gps nodes
     * @return list of road nodes
     */
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

    /**
     * Get Nodes of path
     * @return list of nodes
     */
    public ArrayList<RoadNode> getNodes(){
        return nodes;
    }

    /**
     * Get intersection from path by start end end road position
     * @param start road start position
     * @param end road end position
     * @return get intersection of path
     */
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

    /**
     * Get length of path
     * @return length
     */
    public int getLength(){
        return nodes.get(nodes.size()-1).getPosition();
    }

    /**
     * Get Node that encloses given gps point
     * @param node road node
     * @param gps gps point
     * @return road node
     */
    private RoadNode getEnclosingNode(RoadNode node, GPSData gps){
        final int MAX_ANGLE = 90;
        double minDist = Integer.MAX_VALUE;
        int enclosingNodeId = 0;

        for(int i=0;i<nodes.size(); i++){
            RoadNode currNode = nodes.get(i);
            if (gps.getDistanceTo(currNode.getGPSData()) < minDist && GPSOperation.getAngle(currNode.getGPSData(), gps, node.getGPSData()) < MAX_ANGLE && currNode != node) {
                minDist = gps.getDistanceTo(nodes.get(i).getGPSData());
                enclosingNodeId = i;
            }
        }
        return nodes.get(enclosingNodeId);
    }

    /**
     * Returns nearest RoadNode to given gps point
     * @param gps gps point
     * @return road node
     */
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

    /**
     * Returns nearest RoadNode to given position
     * @param position road position
     * @return road node
     */
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

    /**
     * Returns gps point from given road position
     * @param position road position
     * @return gps point
     */
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

    /**
     * Returns road position
     * @param gps gps point
     * @return road position
     */
    public Integer getPosition(GPSData gps) {
        RoadNode nearestNode = getNearestNode(gps);
        RoadNode nextNode = getEnclosingNode(nearestNode, gps);

        double nearestNodeDist = nearestNode.getGPSData().getDistanceTo(gps);
        double nextNodeDist = nextNode.getGPSData().getDistanceTo(gps);

        double dist = nearestNodeDist + nextNodeDist;
        double progress = nearestNodeDist/dist;

        if(nextNode.getPosition() < nearestNode.getPosition()){
            progress*=-1;
        }

        Integer out = (int)(nearestNode.getPosition() + progress*dist);

        if(getGPSPoint(out).getDistanceTo(gps) >= MAX_NODE_DIST){
            return null;
        }

        return out;
    }

}