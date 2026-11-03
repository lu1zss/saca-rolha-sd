package sacarolha.model;

import java.net.DatagramPacket;
import java.net.InetAddress;
import java.nio.charset.StandardCharsets;

public class MasterInfo {
    private final InetAddress address; 
    private final int port;
    private final String data;


    public MasterInfo(DatagramPacket packet){
        this.address = packet.getAddress();

        String dataString = new String(packet.getData(),0 , packet.getLength(),StandardCharsets.UTF_8);
        this.data = dataString;

        if(dataString.startsWith("SACAROLHA_MASTER:")){
            this.port = Integer.parseInt(dataString.substring(dataString.indexOf(':') + 1, dataString.length()));
        } else {
            throw new IllegalArgumentException("A mensagem enviada não é a esperada!");
        }
    }


    public void getData() {
        System.out.printf("\nMessage: %s", this.data);
        System.out.printf("\nAddress: %s", this.address.getHostAddress());
        System.out.printf("\nPort: %d\n", this.port);
    }

}
