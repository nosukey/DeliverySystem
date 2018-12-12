package entity.inPC;

import entity.common.Parcel;
import entity.inEV3.RelayStation;

import java.util.List;

public class TestMain {
    public static void main(String[] args){
        Reception rep = new Reception();
        List<Parcel> nimotu = rep.getnimotu();
        List<Parcel> nimotuRe = rep.getRedelNimotu();

        RelayStation rel = new RelayStation();
        sinnyuuTest2(rel);
//        rel.receiveParcels(nimotu);
//        rel.sendParcels();
//        rel.sendParcels();
//        rel.sendParcels();
//        rel.sendParcels();

    }
    public static void sinnnyuuTest1(RelayStation rel){
        System.out.println("first:" + rel.checkCanEntry());
        System.out.println("secound:" + rel.checkCanEntry());
    }

    public static void sinnyuuTest2(RelayStation rel){
        System.out.println("first:" + rel.checkCanEntry());
        System.out.println("secound:" + rel.checkCanEntry());
//        private -> publi　の擬似テスト
//        rel.enableEntry();
//        System.out.println("first:" + rel.checkCanEntry());
    }

    public static void hassouTest1(Reception rep, List<Parcel> nimotu, List<Parcel> nimotuRe){
        rep.receiveRequest();
        rep.setRedely(nimotu);
        System.out.println("size:"+nimotu.size());
        System.out.println("sizeRe:"+nimotuRe.size());
        rep.receiveRequest();
        rep.receiveRequest();
        rep.receiveRequest();
        rep.receiveRequest();
        rep.receiveRequest();

        rep.receiveRequest();
        rep.receiveRequest();
        rep.receiveRequest();
        rep.receiveRequest();
        rep.receiveRequest();
        System.out.println("size:"+nimotu.size());
        System.out.println("sizeRe:"+nimotuRe.size());
        rep.promptToTransport();
        System.out.println("size:"+nimotu.size());
        System.out.println("sizeRe:"+nimotuRe.size());
    }

    public static void hassouTest2(Reception rep, List<Parcel> nimotu, List<Parcel> nimotuRe){
        rep.receiveRequest();
        rep.receiveRequest();
        rep.setRedely(nimotu);
        System.out.println("size:"+nimotu.size());
        System.out.println("sizeRe:"+nimotuRe.size());
        rep.receiveRequest();

        System.out.println("size:"+nimotu.size());
        rep.promptToTransport();
        System.out.println("size:"+nimotu.size());
        System.out.println("sizeRe:"+nimotuRe.size());
    }

    public static void hassouhoukokuTest1(Reception rep, List<Parcel> nimotu, List<Parcel> nimotuRe){
        rep.receiveRequest();
        rep.setRedely(nimotu);
        System.out.println("size:"+nimotu.size());
        System.out.println("sizeRe:"+nimotuRe.size());
        rep.receiveRequest();
        rep.receiveRequest();

        System.out.println("size:"+nimotu.size());
        System.out.println("sizeRe:"+nimotuRe.size());
        rep.promptToTransport();
    }
}
