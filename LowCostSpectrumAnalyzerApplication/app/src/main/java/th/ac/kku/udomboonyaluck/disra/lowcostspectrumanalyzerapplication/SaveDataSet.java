package th.ac.kku.udomboonyaluck.disra.lowcostspectrumanalyzerapplication;

import java.util.List;

public class SaveDataSet {
    private List<String> dataSet;
    private  static  final  SaveDataSet instance = new SaveDataSet();


    public SaveDataSet(){

    }
    public  static  SaveDataSet getInstance(){
        return instance;

    }
    public void saveArraylist(List<String> DataSet){
        this.dataSet = DataSet;
    }
    public List<String> get_mDataSet(){
        return dataSet;
    }
}
