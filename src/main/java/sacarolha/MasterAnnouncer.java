package sacarolha;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.SocketException;
import java.nio.charset.StandardCharsets;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.ScheduledFuture;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

public class MasterAnnouncer {
	private final int masterPort;
    private final int announcementPort;
    private final long interval;
    private DatagramSocket socket;
    private ScheduledExecutorService scheduler;
    private ScheduledFuture<?> announcementTask;

    public MasterAnnouncer(int masterPort, int announcementPort, long interval){

        if (masterPort >=1 && masterPort <=65535){ 
            this.masterPort = masterPort;
        } else {
            throw new IllegalArgumentException("Range de Portas Invalido");
        }

        if (announcementPort >=1 && announcementPort <=65535){ 
            this.announcementPort = announcementPort;
        } else {
            throw new IllegalArgumentException("Range de Portas Invalido");
        }

        if (interval > 0){
            this.interval = interval;
        } else {
            throw new IllegalArgumentException("Intervalo de ms Invalido");
        }
    }

    public void start() {

        if (socket != null && !socket.isClosed()) {
            throw new IllegalStateException("O socket UDP já está iniciado.");
        }

        try {
            this.socket = new DatagramSocket();
            this.socket.setBroadcast(true);

            this.scheduler = Executors.newSingleThreadScheduledExecutor();

            this.announcementTask = this.scheduler.scheduleAtFixedRate(() -> {
                try {
                    sendAnnouncement();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }, 0, interval, TimeUnit.MILLISECONDS);

            System.out.println("MasterAnnoucer: Started");
        } catch (SocketException e) {
            if (socket != null) {
                socket.close();
            }
            throw new RuntimeException("Erro ao iniciar o socket UDP", e);
        }
}

    public void stop() {
        System.out.println("\nSolicitação de Stop:");
        if (announcementTask != null) {
            // False: Impede novas execuções da Tarefa
            announcementTask.cancel(false);
            announcementTask = null;
            System.out.println("Task Cancelada - Novas Execuções Canceladas");
        }

        // Verifica se o executor já recebeu ordem de encerramento. 
        if (scheduler != null && !scheduler.isShutdown()) {
            scheduler.shutdown(); 
            scheduler = null;
            System.out.println("Executor em Modo Shutdown");
        }

        if (socket != null && !socket.isClosed()) {
            socket.close(); // Fecha o Socket UDP
            System.out.println("Socket Fechado");
        }
    }


    private void sendAnnouncement() throws IOException {

        if (socket == null || socket.isClosed()) {
            throw new IllegalStateException("Socket UDP não inicializado ou já fechado.");
        }

        String message = "SACAROLHA_MASTER:" + this.masterPort;
        byte[] messageBytes = message.getBytes(StandardCharsets.UTF_8);

        InetAddress address = InetAddress.getByName("255.255.255.255");

        
        DatagramPacket packet = new DatagramPacket(
            messageBytes,
            messageBytes.length,
            address,
            this.announcementPort
        );

        System.out.printf("\n[UDP] Anuncio Enviado pela porta %d", this.masterPort);

        socket.send(packet);
    }

}
