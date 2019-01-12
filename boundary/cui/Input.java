package boundary.cui;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.IOException;

class Input {
    private final String ERROR_INPUT_INT = "Input value is not int value.\n";
    private final String ERROR_INPUT_STR = "Input value is not string value.\n";

    public int inputInt(String msg) {
        System.out.print(msg);
        try {
            return Integer.parseInt(input());
        } catch (Exception e) {
            System.out.println(ERROR_INPUT_INT);
            return inputInt(msg);
        }
    }

    public String inputString(String msg) {
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
