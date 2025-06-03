import java.io.File;
public class file {
    public static void main(String[] args) {
        File file = new File("D:\\voting system\\nie.png");
        if (file.exists()) {
            System.out.println("File found!");
        } else {
            System.out.println("File not found.");
        }
    }
}
