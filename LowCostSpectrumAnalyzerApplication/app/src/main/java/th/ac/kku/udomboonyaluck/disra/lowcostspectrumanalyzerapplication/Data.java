package th.ac.kku.udomboonyaluck.disra.lowcostspectrumanalyzerapplication;

public class Data {
    private int id;
    private String name;
    private String position;
    private int height;
    private int n;
    private int x1,y1,x2,y2;
    public Data() {

    }

    public Data(int id, String name, String position, int height, int n, int x1, int y1,int x2, int y2) {
        this.id = id;
        this.name = name;
        this.position = position;
        this.height = height;
        this.n = n;
        this.x1 = x1;
        this.y1 = y1;
        this.x2 = x2;
        this.y2 = y2;
    }

    public int getN() {
        return n;
    }

    public void setX1(int x1){
        this.x1 = x1;
    }
    public void setY1(int y1){
        this.y1 = y1;
    }
    public void setX2(int x2){
        this.x2 = x2;
    }
    public void setY2(int y2){
        this.y2 = y2;
    }
    public int getX1(){
        return x1;
    }
    public int getY1(){
        return y1;
    }
    public int getX2(){
        return x2;
    }
    public int getY2(){
        return y2;
    }

    public void setN(int n) {
        this.n = n;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getPosition() {
        return position;
    }

    public void setPosition(String position) {
        this.position = position;
    }

    public int getHeight() {
        return height;
    }

    public void setHeight(int height) {
        this.height = height;
    }

    @Override
    public String toString() {
        return name + " - peak " + height + "- n " + n + "\n imageString " + position;
    }
}
