package Scheduler;

public class Hall{
    
    private String Hall_ID; 
    private String Hall_Type; 
    private String Hall_location;
    private double cost_per_hour;   
    private int Hall_Capacity;
    
   public Hall(String Hall_ID, String Hall_Type, String Hall_location, double cost_per_hour, int Hall_Capacity){
        this.Hall_ID=Hall_ID;
        this.Hall_Type=Hall_Type;
        this.Hall_location=Hall_location;
        this.cost_per_hour=cost_per_hour;
        this.Hall_Capacity=Hall_Capacity;
    }
    
    public String getHall_ID(){
        return Hall_ID;
    }
    public String getHall_Type(){
        return Hall_Type;
    }
    public String getHall_location(){
        return Hall_location;
    }
    public int getHall_Capacity(){
        return Hall_Capacity;
    }
    public double getcost_per_hour(){
        return cost_per_hour;
    }

    public void setHall_ID(String Hall_ID){
        this.Hall_ID=Hall_ID;
    }

    public void setHall_Type(String Hall_Type){
        this.Hall_Type=Hall_Type;
    }
    public void setHall_location(String Hall_location){
        this.Hall_location=Hall_location;
    }
    public void setHall_Capacity(int Hall_Capacity){
        this.Hall_Capacity=Hall_Capacity;
    }
    public void setcost_per_hour(double cost_per_hour){
        this.cost_per_hour=cost_per_hour;
    }

}