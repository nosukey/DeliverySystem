package entity.inPC;

import entity.common.Parcel;
import entity.inEV3.RelayStation;

import java.util.List;

public class TestMain {
    public static void main(String[] args){
        Reception rep = new Reception();
        List<Parcel> nimotu = rep.getnimotu();
        List<Parcel> nimotuRe = rep.getRedelNimotu();
//        rep.receiveRequest();
//        hassouTest1(rep, nimotu, nimotuRe);
//        hassouTest2(rep, nimotu, nimotuRe);
        hassouhoukokuTest1(rep, nimotu, nimotuRe);

//        System.out.println("size:"+nimotu.size());
//
//        rep.promptToTransport();
////        rep.promptToTransport();
//        System.out.println("size:"+nimotu.size());
//
//        RelayStation rel = new RelayStation();
//        rel.receiveParcels(nimotu);
//        rel.sendParcels();
//        rel.sendParcels();
//        rel.sendParcels();
//        rel.sendParcels();

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
