package boundary.cui;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.IOException;

/**
* 標準入力を実現するクラスです。
* @author 澤田 悠暉
* @version 1.0
*/
class Input {
    /**
    * 整数値を標準入力します。
    * @param msg 入力を促すメッセージ
    * @return 標準入力された整数値
    */
    public int inputInt(String msg) {
        final String ERROR_INPUT_INT = "Input value is not int value.\n";
        System.out.print(msg);
        try {
            return Integer.parseInt(input());
        } catch (Exception e) {
            System.out.println(ERROR_INPUT_INT);
            return inputInt(msg);
        }
    }

    /**
    * 文字列を標準入力します。
    * @param msg 入力を促すメッセージ
    * @return 標準入力された文字列
    */
    public String inputString(String msg) {
        final String ERROR_INPUT_STR = "Input value is not string value.\n";
        System.out.print(msg);
        try {
            return input();
        } catch (Exception e) {
            System.out.println(ERROR_INPUT_STR);
            return inputString(msg);
        }
    }

    private String input() throws IOException {
        BufferedReader reader = new BufferedReader(new InputStreamReader(System.in));
        return reader.readLine();
    }
}
