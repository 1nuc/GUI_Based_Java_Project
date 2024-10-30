package Adminstrator;

import javax.swing.table.DefaultTableModel;

abstract class Adminstrator{
    private String Name;
    private String Email;
    private String ID;
    private String Role;
    private String Password;
//applying encabsulation
    public Adminstrator(String Name, String Email, String ID, String Role,String Password){
        this.Name=Name;
        this.Email=Email;
        this.ID=ID;
        this.Role=Role;
        this.Password=Password;
    }

    public void set_ID(String ID){
        this.ID=ID;
    }
    
    public String get_ID(){
        return ID;
    }
    //------------------------

    public void set_Name(String Name){
        this.Name=Name;
    }
    public String get_Name(){
        return Name;
    }
    //------------------------
    public void set_Email(String Email){
        this.Email=Email;
    }
    public String get_Email(){
        return Email;
    }
    //------------------------
    public void set_Role(String Role){
        this.Role=Role;
    }
    public String get_Role(){
        return Role;
    }
    //------------------------
    public void set_Password(String Password){
        this.Password=Password;
    }

    public String get_Password(){
        return Password;
    }
    //------------------------
//creating protected varuables to store the information of each role efficiently

    abstract public void Set_info(String Name, String Email, String ID, String Password);
 
    abstract protected void SaveToFile();
    
    abstract protected void Role_checking();//A function that checks the ID to identify the role


    abstract protected boolean ID_checking(String ID);//A function that checks the Syntax of the ID 
    
    
    abstract protected Boolean email_checking(String Email);//A function that checks the syntax of the Email entered

    abstract void  view_User_Info(String FileName, DefaultTableModel model, String Role);
    abstract public void update_info(String EnteredID, String Selected_Opt, String ID, String Name, String Email, String Password);
    abstract public void delete_info(String FileName, String ID_Delete);
    abstract public void filter_info(DefaultTableModel model, String Search);
    abstract public void Select_Specific_User(String FileName, DefaultTableModel model,String find);
    abstract public void Manage_Block_User(String ID_Blocked,boolean isBloackAction);

}

//Applying inheretience for further imporvments in the code
