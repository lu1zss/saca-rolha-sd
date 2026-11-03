package sacarolha;

import java.net.DatagramPacket;
import java.net.InetAddress;
import java.net.UnknownHostException;
import java.nio.charset.StandardCharsets;

import sacarolha.model.MasterInfo;

public class App 
{
    public static void main( String[] args ) throws UnknownHostException
    {
 
        // MasterAnnouncer announcer = new MasterAnnouncer(5000, 5001, 2000);
        // announcer.start();


        String message = "SACAROLHA_MASTER:" + 5000;
        byte[] messageBytes = message.getBytes(StandardCharsets.UTF_8);

        InetAddress address = InetAddress.getByName("192.168.1.10");

        DatagramPacket packet = new DatagramPacket(
            messageBytes,
            messageBytes.length,
            address,
            5001
        );

        MasterInfo mi = new MasterInfo(packet);
        mi.getData();
    
    }
}
