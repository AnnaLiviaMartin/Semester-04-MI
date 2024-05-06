package client_server;

import java.io.*;
import java.net.ServerSocket;
import java.net.Socket;

public class Server<ClientSocket>
{
	private boolean connected;
	private Socket socket;
	private BufferedReader reader;
	private BufferedWriter writer;
	private ServerSocket serverSocket;

	public Server()
	{
		try {
			this.serverSocket = new ServerSocket(27999);
			System.out.println("Warte auf Client...");
		}
		catch (IOException e)
		{
			e.printStackTrace();
		}
	}

	public void run_forever()  {
		while(true){
			try {
				this.socket = this.serverSocket.accept();
				this.connected = true;
				System.out.printf("Client hat sich verbunden: %s%n", socket.getRemoteSocketAddress());
				reader = new BufferedReader(new InputStreamReader(socket.getInputStream()));
				writer = new BufferedWriter(new OutputStreamWriter(socket.getOutputStream()));
			}
			catch (IOException e) {
				e.printStackTrace();
			}

			handleRequests(socket, reader, writer);
		}
	}

	private void handleRequests(final Socket socket, final BufferedReader reader, final BufferedWriter writer)
	{
		while (this.connected)
		{

			try
			{
				String line = reader.readLine();
				if (line != null) {
					System.out.printf("Vom Client (%s) empfangen: %s%n", socket.getRemoteSocketAddress(), line);
					final StringBuffer reverse = new StringBuffer(line).reverse();
					System.out.printf("Sende an Client (%s): %s%n", socket.getRemoteSocketAddress(), reverse);
					writer.write(reverse + "\n");
					writer.flush();
				}
				else{
					connected = false;
				}
			}
			catch (IOException e)
			{
				System.out.println(e.getMessage());
				return;
			}
		}
	}

	public static void main(String[] args)
	{
		final Server s = new Server();
		s.run_forever();

	}
}
