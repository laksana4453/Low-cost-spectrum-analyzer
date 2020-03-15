package th.ac.kku.udomboonyaluck.disra.lowcostspectrumanalyzerapplication.util;

import java.util.List;

public class colletionArraylist {

    private List<Double> Yaxis;
    private  static  final  colletionArraylist instance = new colletionArraylist();


    public colletionArraylist(){

    }
    public  static  colletionArraylist getInstance(){
        return instance;

    }
    public void saveArraylist(List<Double> Yaxis){
        this.Yaxis = Yaxis;
    }
    public List<Double> getYaxisArraylist(){
        return Yaxis;
    }

}
