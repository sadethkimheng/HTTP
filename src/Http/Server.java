package Http;

import java.net.ServerSocket;
import java.net.Socket;

public class Server {

        public static void main(String[] args) throws Exception {

            ServerSocket serSock = new ServerSocket(8000);

            while (true) {
                System.out.println("Waiting for client...");
                Socket sock = serSock.accept();

                System.out.println("Task submitted");
                RequestHandler requestHandler = new RequestHandler();
                requestHandler.handleRequest(sock);

            }

        }

    }


