package de.felixbublitz.simra_rq.data.track;

public class TrackSegment {
    private Road road;
    private int start;
    private int end;

    public TrackSegment(Road road, int start, int end){
        this.road = road;
        this.start = start;
        this.end = end;
    }

    public Road getRoad(){
        return road;
    }

    public int getStart(){
        return start;
    }

    public int getEnd(){
        return end;
    }

    public int getLength(){
        return end-start;
    }


}

