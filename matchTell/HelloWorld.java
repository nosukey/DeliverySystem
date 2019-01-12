
public class HelloWorld{
    public static void main(String[] args){

        String number = "080(1222)0157";
        System.out.println(number.replaceAll("[-()]","").equals("08012220157"));

        // 固定電話、携帯電話、IP電話の-/()有無に対応
        System.out.println(number.matches("^0(\\d[-(]\\d{4}|\\d{2}[-(]\\d{3}|\\d{3}[-(]\\d{2}|\\d{4}[-(]\\d{1})[-)]\\d{4}$|^0\\d{9}$|^(0[5789]0)[-(]\\d{4}[-)]\\d{4}$|^(0[5789]0)\\d{8}$"));
    }
}