import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashMap;

public class Message implements Serializable {
    static final long serialVersionUID = 42L;
    public String username;
    public String recipient;

    public String whattodo;
    public boolean isUnique;
    public ArrayList<String> ListOUsers = new ArrayList<>();

    public ArrayList<String> ListOPUsers = new ArrayList<>();
    public ArrayList<String> ListOGUsers = new ArrayList<>();
    public ArrayList<String> ListOGMUsers = new ArrayList<>();

    public ArrayList<String> AllGroups = new ArrayList<>();
    public String message = "";
    public String GroupName;
    public HashMap<String,ArrayList<String>> GroupMembers = new HashMap<>();

    public String LeaveTheServer;



}



