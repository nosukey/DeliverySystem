package entity.inPC;

import entity.common.Parcel;
import entity.inEV3.RelayStation;

import java.util.List;

public class TestMain {
    public static void main(String[] args){
        Reception rep = new Reception();
        List<Parcel> nimotu = rep.getnimotu();
        rep.receiveRequest();
        rep.receiveRequest();

        //        rep.promptToTransport();
//        rep.promptToTransport();
        System.out.println("size:"+nimotu.size());
        RelayStation rel = new RelayStation();
        rel.receiveParcels(nimotu);
        rel.sendParcels();
        rel.sendParcels();
        rel.sendParcels();
        rel.sendParcels();

    }
}
