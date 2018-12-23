package Takato;

import java.util.concurrent.TimeUnit;
import java.lang.InterruptedException;

public class Main {
    public static void main(String[] arg){
//        boolean returnValue = false;
//        try {
        Innerr in = new Innerr();
        in.start();
//            boolean returnValue = in.readBoolean2();
//            in.stop();
        try {
            TimeUnit.SECONDS.sleep(3);
        }catch (InterruptedException e){}
        System.out.println(in.gettertemp());
        System.out.println(in.getState().equals(Thread.State.TERMINATED));
        in.stop();
//        }catch (InterruptedException e) {
//            System.out.println("error:" + e);
//        }
////
    }
    
    static class Innerr extends Thread {
        boolean temp;
        public void run() {
            //Timeunit 10
            try {
                TimeUnit.SECONDS.sleep(2);
            }catch (InterruptedException e){}
            this.temp = true;
        }
        public boolean gettertemp(){
            return this.temp;
        }
    }
}
